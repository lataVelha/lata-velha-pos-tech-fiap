#!/usr/bin/env bash
#
# apply.sh — pipeline local e destroy do deploy da aplicação:
#
#   (padrão)   [1/4] Testes → [2/4] Docker → [3/4] Deploy → [4/4] Verificar
#   --destroy  remove só os recursos da aplicação (Deployment/Service/etc)
#
# Pré-requisitos: repo infra (VPC+EKS+ECR+ALB) e repo infra-db (RDS) já
# aplicados — este script só faz o deploy da aplicação em cima deles.
#
# Uso:
#   ./apply.sh              — pipeline com confirmação interativa
#   ./apply.sh --auto       — pipeline sem confirmação
#   ./apply.sh --skip-test  — pula os testes Maven
#   ./apply.sh --destroy    — remove os recursos da aplicação
#   ./apply.sh --destroy --auto — remove sem confirmação
#
# Pré-requisitos:
#   cp terraform.tfvars.example terraform.tfvars
#   # Edite com suas credenciais de e-mail

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
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
REGION="${AWS_DEFAULT_REGION:-us-east-1}"
CLUSTER_NAME="lata-velha-eks"
ECR_REPO_NAME="lata-velha"

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
BUCKET="lata-velha-tfstate-${ACCOUNT_ID}"

# Dados do cluster EKS (provisionado pelo repo infra) — via AWS CLI, sem ler
# o Terraform state de outro repo.
export TF_VAR_region="$REGION"
export TF_VAR_state_bucket="$BUCKET"
export TF_VAR_cluster_name="$CLUSTER_NAME"
export TF_VAR_cluster_endpoint=$(aws eks describe-cluster --name "$CLUSTER_NAME" --region "$REGION" --query 'cluster.endpoint' --output text)
export TF_VAR_cluster_ca_data=$(aws eks describe-cluster --name "$CLUSTER_NAME" --region "$REGION" --query 'cluster.certificateAuthority.data' --output text)

tf_init() {
  terraform -chdir="$SCRIPT_DIR" init -reconfigure \
    -backend-config="bucket=${BUCKET}" \
    -backend-config="region=${REGION}"
}

if $DESTROY; then
  echo ""
  echo "==> Destruindo recursos da aplicação (Deployment/Service/ConfigMap/Secret/HPA/PDB)..."
  tf_init
  terraform -chdir="$SCRIPT_DIR" destroy $AUTO
  exit 0
fi

echo ""
echo "========================================"
echo "  PIPELINE LOCAL — igual ao GitHub CI/CD"
echo "========================================"

# [1/4] Testes
if ! $SKIP_TESTS; then
  echo ""
  echo "==> [1/4] Testes — subindo PostgreSQL efêmero..."
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
  (cd "$PROJECT_ROOT" && mvn -B clean test \
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
  echo "==> [1/4] Testes — pulando (--skip-test)"
fi

# [2/4] Docker
echo ""
echo "==> [2/4] Docker — build e push da imagem"
ECR_URL=$(aws ecr describe-repositories --repository-names "$ECR_REPO_NAME" --region "$REGION" --query 'repositories[0].repositoryUri' --output text)
GIT_SHA=$(git -C "$PROJECT_ROOT" rev-parse --short HEAD 2>/dev/null || echo "local")
IMAGE="${ECR_URL}:${GIT_SHA}"

aws ecr get-login-password --region "$REGION" \
  | docker login --username AWS --password-stdin "$ECR_URL"

docker build --platform linux/amd64 -t "$IMAGE" "$PROJECT_ROOT"
docker push "$IMAGE"
echo "    Imagem: $IMAGE"

# [3/4] Deploy
echo ""
echo "==> [3/4] Deploy — Deployment + Service + HPA + PDB"
export TF_VAR_docker_image="$IMAGE"
tf_init
terraform -chdir="$SCRIPT_DIR" apply $AUTO

# [4/4] Verificar
echo ""
echo "==> [4/4] Verificar — aguardando rollout dos pods..."
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
ALB_DNS=$(aws elbv2 describe-load-balancers --names "${ECR_REPO_NAME}-alb" --region "$REGION" --query 'LoadBalancers[0].DNSName' --output text 2>/dev/null || echo "")
[[ -n "$ALB_DNS" ]] && echo "    URL: http://${ALB_DNS}"
