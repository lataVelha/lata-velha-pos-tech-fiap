#!/usr/bin/env bash
# Aplica a infra em duas fases.
#
# Fase 1: VPC + EKS + RDS + ECR — necessário porque os providers helm/kubectl
#         precisam do endpoint do cluster para ser configurados.
# Fase 2: Apply completo — ALB Controller + manifests Kubernetes.
#
# Uso:
#   ./apply.sh                         — ambas as fases com confirmação interativa
#   ./apply.sh --auto                  — ambas as fases sem confirmação
#   ./apply.sh --phase 1               — somente Fase 1 (infra base + ECR)
#   ./apply.sh --phase 2               — somente Fase 2 (ALB Controller + app)
#   ./apply.sh --pipeline              — fluxo completo igual ao CI/CD:
#                                        testes → Fase 1 → docker build/push → Fase 2 → verificar
#   ./apply.sh --pipeline --auto       — mesmo acima, sem confirmação
#   ./apply.sh --pipeline --skip-tests — pula os testes (útil se já rodou antes)
#   ./apply.sh --destroy               — destroi tudo em ordem reversa
#   ./apply.sh --bucket meu-bucket     — bucket S3 do estado remoto
#
# Variáveis do Terraform são lidas do terraform.tfvars (copie do .example):
#   cp terraform.tfvars.example terraform.tfvars
#
# O bucket S3 de estado é criado automaticamente se não existir.

set -euo pipefail

AUTO=""
DESTROY=false
PIPELINE=false
SKIP_TESTS=false
PHASE=0   # 0 = ambas, 1 = somente Fase 1, 2 = somente Fase 2
BUCKET="${TF_STATE_BUCKET:-}"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --auto)        AUTO="-auto-approve" ;;
    --destroy)     DESTROY=true ;;
    --pipeline)    PIPELINE=true ;;
    --skip-tests)  SKIP_TESTS=true ;;
    --phase)       shift; PHASE="$1" ;;
    --bucket)      shift; BUCKET="$1" ;;
  esac
  shift
done

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
REGION="${AWS_DEFAULT_REGION:-us-east-1}"

# Credenciais AWS para o kubernetes_secret gerenciado pelo Terraform.
# Lidas do ambiente (AWS Academy exporta automaticamente) ou do aws configure.
export TF_VAR_aws_access_key_id="${AWS_ACCESS_KEY_ID:-$(aws configure get aws_access_key_id 2>/dev/null || echo '')}"
export TF_VAR_aws_secret_access_key="${AWS_SECRET_ACCESS_KEY:-$(aws configure get aws_secret_access_key 2>/dev/null || echo '')}"
export TF_VAR_aws_session_token="${AWS_SESSION_TOKEN:-$(aws configure get aws_session_token 2>/dev/null || echo '')}"

# ---------------------------------------------------------------------------
# Bucket S3 de estado — criado automaticamente pelo account ID da AWS
# ---------------------------------------------------------------------------
if [[ -z "$BUCKET" ]]; then
  ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
  BUCKET="lata-velha-tfstate-${ACCOUNT_ID}"
fi

echo "==> Bucket de estado: $BUCKET"
if ! aws s3api head-bucket --bucket "$BUCKET" 2>/dev/null; then
  echo "    Bucket não encontrado — criando..."
  aws s3 mb "s3://$BUCKET" --region "$REGION"
  aws s3api put-bucket-versioning \
    --bucket "$BUCKET" \
    --versioning-configuration Status=Enabled
  echo "    Bucket criado com versionamento ativado."
fi

terraform init -reconfigure \
  -backend-config="bucket=${BUCKET}" \
  -backend-config="region=${REGION}"

# ---------------------------------------------------------------------------
# --destroy
# ---------------------------------------------------------------------------
if $DESTROY; then
  echo "==> Destruindo infraestrutura..."
  terraform destroy $AUTO

  echo "==> Removendo bucket de estado S3: $BUCKET"
  aws s3 rm "s3://$BUCKET" --recursive --region "$REGION" 2>/dev/null || true
  aws s3api delete-bucket --bucket "$BUCKET" --region "$REGION" 2>/dev/null && \
    echo "    Bucket deletado." || echo "    Bucket não encontrado ou já deletado."

  exit 0
fi

# ---------------------------------------------------------------------------
# --pipeline  →  testes → Fase 1 → docker build/push → Fase 2 → verificar
# ---------------------------------------------------------------------------
if $PIPELINE; then
  echo ""
  echo "========================================"
  echo "  PIPELINE LOCAL — igual ao GitHub CI/CD"
  echo "========================================"
  echo ""

  # ------------------------------------------------------------------
  # [Testes] — PostgreSQL efêmero via Docker, igual ao service container
  #            do GitHub Actions. Parado automaticamente ao final.
  # ------------------------------------------------------------------
  if ! $SKIP_TESTS; then
    echo "==> [Testes] Subindo PostgreSQL efêmero..."
    docker run --rm -d \
      --name lata-velha-test-db \
      -e POSTGRES_DB=lata_velha \
      -e POSTGRES_USER=admin \
      -e POSTGRES_PASSWORD=admin123 \
      -p 5432:5432 \
      postgres:15

    echo "    Aguardando PostgreSQL ficar pronto..."
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
    echo "==> [Testes] Pulando (--skip-tests)"
  fi

  # ------------------------------------------------------------------
  # [Fase 1] VPC + EKS + RDS + ECR
  # ------------------------------------------------------------------
  echo ""
  echo "==> [Fase 1] VPC + EKS + RDS + ECR"
  terraform apply \
    -target=module.vpc \
    -target=module.eks \
    -target=module.rds \
    -target=aws_ecr_repository.app \
    $AUTO

  # ------------------------------------------------------------------
  # [Docker] Build e push para ECR
  # ------------------------------------------------------------------
  echo ""
  echo "==> [Docker] Build e push da imagem"
  ECR_URL=$(terraform output -raw ecr_repository_url)
  GIT_SHA=$(git -C "$PROJECT_ROOT" rev-parse --short HEAD 2>/dev/null || echo "local")
  IMAGE="${ECR_URL}:${GIT_SHA}"

  aws ecr get-login-password --region "$REGION" \
    | docker login --username AWS --password-stdin "$ECR_URL"

  docker build --platform linux/amd64 -t "$IMAGE" "$PROJECT_ROOT"
  docker push "$IMAGE"
  echo "    Imagem: $IMAGE"

  # docker_image é passado via env var — sobrescreve o valor do terraform.tfvars
  export TF_VAR_docker_image="$IMAGE"

  # ------------------------------------------------------------------
  # [Fase 2] ALB Controller + app
  # ------------------------------------------------------------------
  echo ""
  echo "==> [Fase 2] ALB Controller + aplicacao"
  terraform apply $AUTO

  # ------------------------------------------------------------------
  # [Verificar] Aguarda rollout igual ao job verify do GitHub Actions
  # ------------------------------------------------------------------
  echo ""
  echo "==> [Verificar] Aguardando rollout dos pods..."
  CLUSTER_NAME=$(terraform output -raw cluster_name)
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
  terraform output app_url
  exit 0
fi

# ---------------------------------------------------------------------------
# --phase 1 / --phase 2 / sem flag (ambas)
# ---------------------------------------------------------------------------
if [[ $PHASE -eq 0 || $PHASE -eq 1 ]]; then
  echo "==> Fase 1: VPC + EKS + RDS + ECR"
  terraform apply \
    -target=module.vpc \
    -target=module.eks \
    -target=module.rds \
    -target=aws_ecr_repository.app \
    $AUTO
fi

if [[ $PHASE -eq 0 ]]; then
  echo ""
  echo "    Lembre-se de fazer push da imagem no ECR antes de continuar."
  echo "    Use: terraform output ecr_push_commands"
  echo ""
fi

if [[ $PHASE -eq 0 || $PHASE -eq 2 ]]; then
  echo "==> Fase 2: ALB Controller + aplicacao"
  terraform apply $AUTO
fi
