# Deploy da aplicação

Terraform que aplica só os recursos Kubernetes da aplicação (`Namespace`, `ConfigMap`, `Secret`,
`Deployment`, `Service`, `HPA`, `PDB` — módulo `modules/app`, manifests em `../k8s`) no cluster
EKS já provisionado.

Este repo **não** provisiona VPC/EKS/ECR/ALB/API Gateway/autoscaler (repo
[`infra`](https://github.com/lataVelha/lata-velha-pos-tech-fiap-infra)), nem o banco de dados RDS
(repo [`infra-db`](https://github.com/lataVelha/lata-velha-pos-tech-fiap-infra-db)), nem a
autenticação por CPF/lambda authorizer (repo
[`lambda`](https://github.com/lataVelha/lata-velha-pos-tech-fiap-lambda)). Os três precisam ter
rodado antes deste deploy — ordem completa do pipeline: `infra` (bootstrap) → `infra-db` →
`lambda` → `infra` (addons) → **`app`** (este repo, por último). O ALB provisionado pelo `infra`
é **interno**: a URL pública de verdade é o API Gateway (`app_api_endpoint`, output do `infra`
addons), não o DNS do ALB.

## Sumário

- [Como os dados de conexão chegam aqui](#como-os-dados-de-conexão-chegam-aqui)
- [Execução local](#execução-local)
  - [Com o script (`apply.sh`)](#com-o-script-applysh)
  - [Manualmente (sem o script)](#manualmente-sem-o-script)
- [CI/CD (GitHub Actions)](#cicd-github-actions)
  - [Secrets/vars necessários no repositório](#secretsvars-necessários-no-repositório)

---

## Como os dados de conexão chegam aqui

- **Cluster EKS** (`cluster_endpoint`, `cluster_ca_data`, `cluster_name`): resolvidos via
  `aws eks describe-cluster --name lata-velha-eks` — não lemos o Terraform state do repo `infra`,
  só o nome fixo do cluster.
- **Repositório ECR** (`lata-velha`): resolvido via `aws ecr describe-repositories` no passo de
  build da imagem (antes do `terraform apply`).
- **Banco de dados** (`rds_endpoint`, `db_name`, `db_username`, `db_password`): lidos via
  `terraform_remote_state` do state do `infra-db` (`main.tf`), no mesmo bucket S3.

## Execução local

Pré-requisito, para as duas formas abaixo: `aws configure` (ou `AWS_ACCESS_KEY_ID`/`AWS_SECRET_ACCESS_KEY`/`AWS_SESSION_TOKEN` no ambiente) com acesso ao cluster EKS já criado pelo repo `infra`.

```bash
cp terraform.tfvars.example terraform.tfvars
# edite com suas credenciais de e-mail
```

### Com o script (`apply.sh`)

Forma recomendada — roda testes, build/push da imagem, `terraform apply` e verificação do rollout em sequência.

```bash
cd terraform
./apply.sh              # testes + build/push da imagem + deploy + verificação
./apply.sh --auto       # sem confirmação interativa
./apply.sh --skip-test  # pula os testes Maven
./apply.sh --destroy    # remove os recursos da aplicação no cluster
./apply.sh --destroy --auto # remove sem confirmação
```

### Manualmente (sem o script)

Útil para depurar um `plan`/`apply` específico ou quando a imagem já foi buildada/publicada.

```bash
REGION="us-east-1"
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
BUCKET="lata-velha-tfstate-${ACCOUNT_ID}"
CLUSTER_NAME="lata-velha-eks"

# build + push da imagem (repositorio ECR provisionado pelo repo infra)
ECR_URL=$(aws ecr describe-repositories --repository-names lata-velha --query 'repositories[0].repositoryUri' --output text)
IMAGE="${ECR_URL}:$(git rev-parse --short HEAD)"
aws ecr get-login-password --region "$REGION" | docker login --username AWS --password-stdin "$ECR_URL"
docker build --platform linux/amd64 -t "$IMAGE" ..
docker push "$IMAGE"

cd terraform
export TF_VAR_region="$REGION"
export TF_VAR_state_bucket="$BUCKET"
export TF_VAR_cluster_name="$CLUSTER_NAME"
export TF_VAR_cluster_endpoint=$(aws eks describe-cluster --name "$CLUSTER_NAME" --query 'cluster.endpoint' --output text)
export TF_VAR_cluster_ca_data=$(aws eks describe-cluster --name "$CLUSTER_NAME" --query 'cluster.certificateAuthority.data' --output text)
export TF_VAR_docker_image="$IMAGE"

terraform init \
  -backend-config="bucket=${BUCKET}" \
  -backend-config="region=${REGION}"
terraform plan
terraform apply
terraform destroy   # para remover os recursos da aplicação
```

## CI/CD (GitHub Actions)

`.github/workflows/main.yml`, em push para `master`: testes → build/push da imagem no ECR →
`terraform apply` (este diretório) → verificação (rollout + smoke test em `/actuator/health`,
resolvido via API Gateway, não mais o DNS do ALB).

Este repo também expõe seu `main.yml` como **workflow reusável** (`on: workflow_call`) — é assim
que o `apply.sh` da raiz do mono repo (via GitHub Actions) dispara o apply deste repo por
último no pipeline, sem duplicar a lógica de build/deploy.

### Secrets/vars necessários no repositório

| Nome | Tipo | Descrição |
| --- | --- | --- |
| `AWS_ACCESS_KEY_ID` | secret | Credencial AWS |
| `AWS_SECRET_ACCESS_KEY` | secret | Credencial AWS |
| `AWS_SESSION_TOKEN` | secret | Necessário no AWS Academy |
| `AWS_REGION` | var | Região AWS (ex: `us-east-1`) |
| `TF_MAIL_USERNAME` | secret | Conta Gmail remetente |
| `TF_MAIL_PASSWORD` | secret | [Senha de app do Gmail](https://myaccount.google.com/apppasswords) |
