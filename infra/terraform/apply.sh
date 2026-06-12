#!/usr/bin/env bash
#
# apply.sh — orquestra o deploy da infra em duas etapas independentes:
#
#   bootstrap  — VPC + EKS + RDS + ECR
#                Somente provider AWS. Roda antes do docker build.
#
#   deploy     — ALB + app + autoscaler
#                Providers kubectl/helm. Roda após o docker build.
#                Lê os outputs do bootstrap via terraform_remote_state.
#
# ──────────────────────────────────────────────────────────────────────
# Flags disponíveis:
#
#   (sem flags)          Roda bootstrap + deploy com confirmação interativa
#   --auto               Roda bootstrap + deploy sem confirmação
#   --bootstrap          Somente a etapa bootstrap (VPC + EKS + RDS + ECR)
#   --deploy             Somente a etapa deploy    (ALB + app + autoscaler)
#   --bootstrap --auto   Bootstrap sem confirmação
#   --deploy    --auto   Deploy sem confirmação
#
#   --pipeline           Fluxo completo igual ao GitHub CI/CD:
#                          testes → bootstrap → docker build/push → deploy → verificar
#   --pipeline --auto    Mesmo acima, sem confirmação
#   --pipeline --skip-test  Pula os testes Maven
#
#   --destroy            Destroi tudo em ordem reversa (deploy → bootstrap)
#   --destroy --auto     Destroy sem confirmação
#
#   --bucket <nome>      Usa um bucket S3 específico para o estado remoto
#                        (padrão: lata-velha-tfstate-<account-id>)
#
# ──────────────────────────────────────────────────────────────────────
# Pré-requisitos:
#   cp bootstrap/terraform.tfvars.example bootstrap/terraform.tfvars
#   cp deploy/terraform.tfvars.example    deploy/terraform.tfvars
#   # Edite os dois arquivos com suas credenciais
#
# ──────────────────────────────────────────────────────────────────────

set -euo pipefail

# --- Parse de flags -------------------------------------------------------
AUTO=""
DESTROY=false
PIPELINE=false
SKIP_TESTS=false
RUN_BOOTSTRAP=false
RUN_DEPLOY=false
BUCKET="${TF_STATE_BUCKET:-}"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --auto)                   AUTO="-auto-approve" ;;
    --destroy)                DESTROY=true ;;
    --pipeline)               PIPELINE=true ;;
    --skip-tests|--skip-test) SKIP_TESTS=true ;;
    --bootstrap)              RUN_BOOTSTRAP=true ;;
    --deploy)                 RUN_DEPLOY=true ;;
    --bucket)                 shift; BUCKET="$1" ;;
    *)
      echo "Flag desconhecida: $1"
      echo "Use: --bootstrap, --deploy, --pipeline, --destroy, --auto, --skip-test, --bucket"
      exit 1
      ;;
  esac
  shift
done

# Se nenhuma etapa foi escolhida explicitamente, roda as duas
if ! $RUN_BOOTSTRAP && ! $RUN_DEPLOY && ! $PIPELINE && ! $DESTROY; then
  RUN_BOOTSTRAP=true
  RUN_DEPLOY=true
fi

# --- Caminhos -------------------------------------------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
BOOTSTRAP_DIR="$SCRIPT_DIR/bootstrap"
DEPLOY_DIR="$SCRIPT_DIR/deploy"
REGION="${AWS_DEFAULT_REGION:-us-east-1}"

# --- Bucket S3 de estado --------------------------------------------------
# Criado automaticamente pelo account ID para garantir nome único global.
if [[ -z "$BUCKET" ]]; then
  ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
  BUCKET="lata-velha-tfstate-${ACCOUNT_ID}"
fi

echo "==> Bucket de estado: $BUCKET"
if ! aws s3api head-bucket --bucket "$BUCKET" 2>/dev/null; then
  echo "    Criando bucket..."
  aws s3 mb "s3://$BUCKET" --region "$REGION"
  aws s3api put-bucket-versioning \
    --bucket "$BUCKET" \
    --versioning-configuration Status=Enabled
  echo "    Bucket criado com versionamento ativado."
fi

# --- Funções auxiliares ---------------------------------------------------

tf_init() {
  local dir="$1"
  terraform -chdir="$dir" init -reconfigure \
    -backend-config="bucket=${BUCKET}" \
    -backend-config="region=${REGION}"
}

# Os providers kubectl/helm precisam dos dados de conexão do EKS como
# variáveis (data sources não funcionam em blocos provider).
# Lê os outputs do bootstrap e exporta como TF_VAR_ para o deploy.
load_bootstrap_outputs() {
  export TF_VAR_state_bucket="$BUCKET"
  export TF_VAR_cluster_endpoint=$(terraform -chdir="$BOOTSTRAP_DIR" output -raw cluster_endpoint)
  export TF_VAR_cluster_ca_data=$(terraform -chdir="$BOOTSTRAP_DIR" output -raw cluster_certificate_authority_data)
  export TF_VAR_cluster_name=$(terraform -chdir="$BOOTSTRAP_DIR" output -raw cluster_name)
}

# Credenciais AWS para o Cluster Autoscaler (AWS Academy não permite IRSA).
export TF_VAR_aws_access_key_id="${AWS_ACCESS_KEY_ID:-$(aws configure get aws_access_key_id 2>/dev/null || echo '')}"
export TF_VAR_aws_secret_access_key="${AWS_SECRET_ACCESS_KEY:-$(aws configure get aws_secret_access_key 2>/dev/null || echo '')}"
export TF_VAR_aws_session_token="${AWS_SESSION_TOKEN:-$(aws configure get aws_session_token 2>/dev/null || echo '')}"

# ==========================================================================
# --destroy
# Ordem reversa: deploy primeiro (remove ALB/app), depois bootstrap (remove VPC/EKS/RDS).
# ==========================================================================
if $DESTROY; then
  echo ""
  echo "==> [1/3] Destruindo deploy (ALB + app + autoscaler)..."
  tf_init "$DEPLOY_DIR"
  load_bootstrap_outputs
  terraform -chdir="$DEPLOY_DIR" destroy $AUTO

  echo ""
  echo "==> [2/3] Destruindo bootstrap (VPC + EKS + RDS + ECR)..."
  tf_init "$BOOTSTRAP_DIR"
  terraform -chdir="$BOOTSTRAP_DIR" destroy $AUTO

  echo ""
  echo "==> [3/3] Removendo bucket de estado S3: $BUCKET"
  aws s3 rm "s3://$BUCKET" --recursive --region "$REGION" 2>/dev/null || true
  aws s3api delete-bucket --bucket "$BUCKET" --region "$REGION" 2>/dev/null && \
    echo "    Bucket removido." || echo "    Bucket não encontrado ou já removido."

  exit 0
fi

# ==========================================================================
# --pipeline
# Fluxo completo: testes → bootstrap → docker build/push → deploy → verificar
# ==========================================================================
if $PIPELINE; then
  echo ""
  echo "========================================"
  echo "  PIPELINE LOCAL — igual ao GitHub CI/CD"
  echo "========================================"

  # --- Testes ---------------------------------------------------------------
  if ! $SKIP_TESTS; then
    echo ""
    echo "==> [1/5] Testes — subindo PostgreSQL efêmero..."
    docker run --rm -d \
      --name lata-velha-test-db \
      -e POSTGRES_DB=lata_velha \
      -e POSTGRES_USER=admin \
      -e POSTGRES_PASSWORD=admin123 \
      -p 5432:5432 \
      postgres:15

    until docker exec lata-velha-test-db pg_isready -U admin 2>/dev/null; do
      sleep 1
    done

    set +e
    (cd "$PROJECT_ROOT" && mvn -B test \
      -Dspring.datasource.url=jdbc:postgresql://localhost:5432/lata_velha \
      -Dspring.datasource.username=admin \
      -Dspring.datasource.password=admin123)
    TEST_EXIT=$?
    set -e

    docker stop lata-velha-test-db

    if [[ $TEST_EXIT -ne 0 ]]; then
      echo ""
      echo "ERRO: Testes falharam. Deploy cancelado."
      exit $TEST_EXIT
    fi
    echo "    Testes concluídos com sucesso."
  else
    echo ""
    echo "==> [1/5] Testes — pulando (--skip-test)"
  fi

  # --- Bootstrap ------------------------------------------------------------
  echo ""
  echo "==> [2/5] Bootstrap — VPC + EKS + RDS + ECR"
  tf_init "$BOOTSTRAP_DIR"
  terraform -chdir="$BOOTSTRAP_DIR" apply $AUTO

  # --- Docker ---------------------------------------------------------------
  echo ""
  echo "==> [3/5] Docker — build e push da imagem"
  ECR_URL=$(terraform -chdir="$BOOTSTRAP_DIR" output -raw ecr_repository_url)
  GIT_SHA=$(git -C "$PROJECT_ROOT" rev-parse --short HEAD 2>/dev/null || echo "local")
  IMAGE="${ECR_URL}:${GIT_SHA}"

  aws ecr get-login-password --region "$REGION" \
    | docker login --username AWS --password-stdin "$ECR_URL"

  docker build --platform linux/amd64 -t "$IMAGE" "$PROJECT_ROOT"
  docker push "$IMAGE"
  echo "    Imagem: $IMAGE"
  export TF_VAR_docker_image="$IMAGE"

  # --- Deploy ---------------------------------------------------------------
  echo ""
  echo "==> [4/5] Deploy — ALB + aplicação + autoscaler"
  tf_init "$DEPLOY_DIR"
  load_bootstrap_outputs
  terraform -chdir="$DEPLOY_DIR" apply $AUTO

  # --- Verificar ------------------------------------------------------------
  echo ""
  echo "==> [5/5] Verificar — aguardando rollout dos pods..."
  CLUSTER_NAME=$(terraform -chdir="$BOOTSTRAP_DIR" output -raw cluster_name)
  aws eks update-kubeconfig --region "$REGION" --name "$CLUSTER_NAME" 2>/dev/null

  if command -v kubectl &>/dev/null; then
    kubectl rollout status deployment/lata-velha-api \
      -n lata-velha \
      --timeout=5m
    echo "    Rollout concluído com sucesso."
  else
    echo "    kubectl não encontrado — verifique manualmente:"
    echo "    kubectl rollout status deployment/lata-velha-api -n lata-velha"
  fi

  echo ""
  echo "==> Pipeline concluído."
  echo ""
  terraform -chdir="$DEPLOY_DIR" output app_url
  exit 0
fi

# ==========================================================================
# --bootstrap / --deploy / ambos (sem flags)
# ==========================================================================
if $RUN_BOOTSTRAP; then
  echo ""
  echo "==> Bootstrap — VPC + EKS + RDS + ECR"
  tf_init "$BOOTSTRAP_DIR"
  terraform -chdir="$BOOTSTRAP_DIR" apply $AUTO

  if ! $RUN_DEPLOY; then
    echo ""
    echo "    Próximo passo: faça push da imagem no ECR e rode --deploy"
    echo "    ECR: $(terraform -chdir="$BOOTSTRAP_DIR" output -raw ecr_repository_url)"
  fi
fi

if $RUN_DEPLOY; then
  echo ""
  echo "==> Deploy — ALB + aplicação + autoscaler"
  tf_init "$DEPLOY_DIR"
  load_bootstrap_outputs
  terraform -chdir="$DEPLOY_DIR" apply $AUTO
fi
