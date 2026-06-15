#!/usr/bin/env bash
#
# apply.sh — pipeline local e destroy:
#
#   (padrão)   [1/5] Testes → [2/5] Bootstrap → [3/5] Docker → [4/5] Deploy → [5/5] Verificar
#   --destroy  [1/3] Deploy → [2/3] Bootstrap → [3/3] Bucket S3
#
# Uso:
#   ./apply.sh              — pipeline com confirmação interativa
#   ./apply.sh --auto       — pipeline sem confirmação
#   ./apply.sh --skip-test  — pula os testes Maven
#   ./apply.sh --destroy    — destroi tudo com confirmação
#   ./apply.sh --destroy --auto — destroi tudo sem confirmação
#
# Pré-requisitos:
#   cp bootstrap/terraform.tfvars.example bootstrap/terraform.tfvars
#   cp deploy/terraform.tfvars.example    deploy/terraform.tfvars
#   # Edite os dois arquivos com suas credenciais

set -euo pipefail

AUTO=""
SKIP_TESTS=false
DESTROY=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    --auto)                   AUTO="-auto-approve" ;;
    --skip-tests|--skip-test) SKIP_TESTS=true ;;
    --destroy)                DESTROY=true ;;
    *)
      echo "Flag desconhecida: $1"
      echo "Uso: ./apply.sh [--auto] [--skip-test] [--destroy]"
      exit 1
      ;;
  esac
  shift
done

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
BOOTSTRAP_DIR="$SCRIPT_DIR/bootstrap"
DEPLOY_DIR="$SCRIPT_DIR/deploy"
REGION="${AWS_DEFAULT_REGION:-us-east-1}"

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
BUCKET="lata-velha-tfstate-${ACCOUNT_ID}"

echo "==> Bucket de estado: $BUCKET"
if ! aws s3api head-bucket --bucket "$BUCKET" 2>/dev/null; then
  echo "    Criando bucket..."
  aws s3 mb "s3://$BUCKET" --region "$REGION"
  aws s3api put-bucket-versioning \
    --bucket "$BUCKET" \
    --versioning-configuration Status=Enabled
  echo "    Bucket criado com versionamento ativado."
fi

tf_init() {
  local dir="$1"
  terraform -chdir="$dir" init -reconfigure \
    -backend-config="bucket=${BUCKET}" \
    -backend-config="region=${REGION}"
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
  tf_init "$BOOTSTRAP_DIR" > /dev/null 2>&1
  export TF_VAR_state_bucket="$BUCKET"
  export TF_VAR_cluster_endpoint=$(terraform -chdir="$BOOTSTRAP_DIR" output -raw cluster_endpoint 2>/dev/null || echo "")
  export TF_VAR_cluster_ca_data=$(terraform -chdir="$BOOTSTRAP_DIR" output -raw cluster_certificate_authority_data 2>/dev/null || echo "")
  export TF_VAR_cluster_name=$(terraform -chdir="$BOOTSTRAP_DIR" output -raw cluster_name 2>/dev/null || echo "")
  tf_init "$DEPLOY_DIR"
  terraform -chdir="$DEPLOY_DIR" destroy $AUTO

  echo ""
  echo "==> [2/3] Destruindo bootstrap (VPC + EKS + RDS + ECR)..."
  tf_init "$BOOTSTRAP_DIR"
  terraform -chdir="$BOOTSTRAP_DIR" destroy $AUTO

  echo ""
  echo "==> [3/3] Removendo bucket de estado S3: $BUCKET"
  aws s3api list-object-versions --bucket "$BUCKET" --output text \
    --query 'Versions[?VersionId!=`null`].[Key,VersionId]' 2>/dev/null | \
    while read -r key version; do
      aws s3api delete-object --bucket "$BUCKET" --key "$key" --version-id "$version" > /dev/null
    done
  aws s3api list-object-versions --bucket "$BUCKET" --output text \
    --query 'DeleteMarkers[?VersionId!=`null`].[Key,VersionId]' 2>/dev/null | \
    while read -r key version; do
      aws s3api delete-object --bucket "$BUCKET" --key "$key" --version-id "$version" > /dev/null
    done
  aws s3 rm "s3://$BUCKET" --recursive > /dev/null 2>&1 || true
  aws s3api delete-bucket --bucket "$BUCKET" --region "$REGION" 2>/dev/null && \
    echo "    Bucket removido." || echo "    Bucket não encontrado ou já removido."

  exit 0
fi

echo ""
echo "========================================"
echo "  PIPELINE LOCAL — igual ao GitHub CI/CD"
echo "========================================"

# [1/5] Testes
if ! $SKIP_TESTS; then
  echo ""
  echo "==> [1/5] Testes — subindo PostgreSQL efêmero..."
  docker rm -f lata-velha-test-db 2>/dev/null || true
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

# [2/5] Bootstrap
echo ""
echo "==> [2/5] Bootstrap — VPC + EKS + RDS + ECR"
tf_init "$BOOTSTRAP_DIR"
terraform -chdir="$BOOTSTRAP_DIR" apply $AUTO

# [3/5] Docker
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

# [4/5] Deploy
echo ""
echo "==> [4/5] Deploy — ALB + aplicação + autoscaler"
export TF_VAR_docker_image="$IMAGE"
export TF_VAR_state_bucket="$BUCKET"
export TF_VAR_cluster_endpoint=$(terraform -chdir="$BOOTSTRAP_DIR" output -raw cluster_endpoint)
export TF_VAR_cluster_ca_data=$(terraform -chdir="$BOOTSTRAP_DIR" output -raw cluster_certificate_authority_data)
export TF_VAR_cluster_name=$(terraform -chdir="$BOOTSTRAP_DIR" output -raw cluster_name)
tf_init "$DEPLOY_DIR"
terraform -chdir="$DEPLOY_DIR" apply $AUTO

# [5/5] Verificar
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
