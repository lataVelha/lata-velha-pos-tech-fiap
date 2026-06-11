# Infraestrutura — Lata Velha (AWS + EKS)

Infraestrutura como código para o projeto **Lata Velha** na AWS usando **Terraform >= 1.6**.

Compatível com **AWS Academy (Learner Lab)** — usa a `LabRole` pré-existente sem precisar criar IAM roles ou OIDC providers.

---

## Arquitetura

```
Internet
   │
   ▼
AWS ALB (Application Load Balancer)       ← provisionado pelo aws-load-balancer-controller
   │
   ▼
EKS Node Group (EC2 t3.small × 2)        ← nodes em subnet privada
   │
   ▼
Pods lata-velha-api (2 réplicas, HPA até 10)
   │
   ▼
RDS PostgreSQL 15 (db.t3.micro)           ← subnet privada, sem acesso público
```

---

## O que é provisionado

### Recursos AWS

| Recurso        | Descrição                                                            |
| -------------- | -------------------------------------------------------------------- |
| VPC            | CIDR `10.0.0.0/16`, 2 AZs, subnets públicas + privadas + NAT Gateway |
| ECR            | Repositório Docker privado (`lata-velha`)                            |
| EKS Cluster    | Control plane gerenciado, Kubernetes 1.33                            |
| EKS Node Group | EC2 `t3.small`, autoscaling min 2 / max 3                            |
| ALB Controller | Helm chart `aws-load-balancer-controller` no `kube-system`           |
| RDS PostgreSQL | `db.t3.micro`, 20 GB, sem multi-AZ                                   |

### Recursos Kubernetes (módulo `app`)

| Recurso                  | Descrição                                            |
| ------------------------ | ---------------------------------------------------- |
| Namespace                | `lata-velha`                                         |
| Secret `aws-credentials` | Credenciais AWS para o ALB Controller (kube-system)  |
| ConfigMap                | Variáveis não-sensíveis da aplicação                 |
| Secret                   | Credenciais do banco e e-mail (base64 via Terraform) |
| Deployment               | 2 réplicas com probes de liveness/readiness/startup  |
| Service                  | ClusterIP na porta 80 → 8080                         |
| HPA                      | Autoscaling por CPU (70%) entre 2 e 10 réplicas      |
| Ingress                  | ALB internet-facing, HTTP 80, target-type IP         |

---

## Estrutura de arquivos

```
infra/
├── k8s/                        # Manifests Kubernetes
│   ├── namespace.yaml
│   ├── configmap.yaml
│   ├── secret.yaml             # template — valores base64 injetados pelo Terraform
│   ├── deployment.yaml         # template — docker_image injetado pelo Terraform
│   ├── service.yaml
│   ├── hpa.yaml
│   └── ingress.yaml
└── terraform/
    ├── apply.sh                # Script de deploy (local e CI/CD)
    ├── main.tf                 # recursos principais e módulos
    ├── variables.tf
    ├── outputs.tf
    ├── providers.tf            # aws, helm, kubectl, kubernetes
    ├── versions.tf
    ├── backend.tf              # estado remoto S3 (partial config)
    ├── terraform.tfvars.example
    └── modules/
        ├── vpc/                # VPC, subnets, IGW, NAT, route tables
        ├── eks/                # Cluster, node group, launch template
        ├── rds/                # RDS PostgreSQL, subnet group, security group
        ├── alb-controller/     # Helm release do aws-load-balancer-controller
        └── app/                # kubectl_manifest para todos os objetos k8s
```

---

## Compatibilidade com AWS Academy

O AWS Academy (Learner Lab) tem restrições de IAM. A tabela abaixo mostra como cada uma é resolvida:

| Restrição                                 | Como é resolvida                                                            |
| ----------------------------------------- | --------------------------------------------------------------------------- |
| `iam:CreateRole` negado                   | Usa a `LabRole` pré-existente para cluster, nodes e ALB                     |
| `iam:CreateOpenIDConnectProvider` negado  | IRSA removido — credenciais injetadas via `kubernetes_secret`               |
| IMDS hop limit = 1 nos nodes              | Pods não alcançam instance profile — resolvido pelo secret acima            |
| ARN de assumed-role no Access Entry       | Convertido automaticamente para ARN de IAM role via `sts:GetCallerIdentity` |
| Recursos k8s-\* bloqueando VPC no destroy | `null_resource` apaga ALBs e SGs automaticamente antes da VPC               |

---

## Pré-requisitos locais

| Ferramenta   | Versão mínima | Para que serve                                    |
| ------------ | ------------- | ------------------------------------------------- |
| Terraform    | >= 1.6        | provisionar a infra                               |
| AWS CLI      | v2            | autenticar providers e rodar `aws eks get-token`  |
| Docker       | qualquer      | build e push da imagem para o ECR                 |
| kubectl      | qualquer      | inspecionar o cluster (opcional, mas recomendado) |
| Java + Maven | Java 21       | rodar os testes (necessário para `--pipeline`)    |

---

## Configuração local — passo a passo

### 1. Credenciais AWS

No **AWS Academy**, abra o Learner Lab, clique em **AWS Details** e copie as credenciais. Cole no terminal:

```bash
export AWS_ACCESS_KEY_ID=ASIA...
export AWS_SECRET_ACCESS_KEY=...
export AWS_SESSION_TOKEN=...
export AWS_DEFAULT_REGION=us-east-1
```

> As credenciais do Academy expiram em ~4 horas. Renove-as sempre que a sessão expirar.

### 2. Arquivo de variáveis

```bash
cd infra/terraform
cp terraform.tfvars.example terraform.tfvars
```

Abra o `terraform.tfvars` e preencha:

```hcl
db_password   = "sua_senha_do_banco"     # mínimo 8 caracteres
mail_username = "seu@gmail.com"
mail_password = "xxxx xxxx xxxx xxxx"    # Senha de App do Gmail (não a senha da conta)
```

> As demais variáveis já têm valores padrão adequados para o Academy.
> **Nunca commite o `terraform.tfvars`** — ele já está no `.gitignore`.

### 3. Deploy

```bash
./apply.sh --pipeline --auto
```

O pipeline executa automaticamente:

```
[Testes]    →  PostgreSQL Docker efêmero + mvn test
[Fase 1]    →  terraform apply  (VPC + EKS + RDS + ECR)
[Docker]    →  docker build --platform linux/amd64 + push para ECR
[Fase 2]    →  terraform apply  (ALB Controller + app + secret de credenciais)
[Verificar] →  kubectl rollout status  (timeout 5 min)
```

Tempo total: **~20–30 minutos** (na primeira execução; EKS demora ~15 min).

### 4. Verificar

```bash
# Aponta o kubectl para o cluster
aws eks update-kubeconfig --region us-east-1 --name lata-velha-eks

# Pods em execução
kubectl get pods -n lata-velha

# URL pública do ALB (aguarde ~2 min após o deploy)
kubectl get ingress lata-velha-api -n lata-velha

# Testar a API
curl http://<ADDRESS>/actuator/health
```

---

## Destruir o ambiente

```bash
./apply.sh --destroy --auto
```

O destroy é totalmente automatizado:

1. Terraform apaga os manifests Kubernetes (Ingress, Deployment, etc.)
2. Um `null_resource` usa o AWS CLI para deletar ALBs, Target Groups e Security Groups `k8s-*` criados pelo ALB Controller
3. Aguarda os ENIs serem liberados pela AWS
4. VPC é deletada sem erros

> **Destrua o ambiente quando não precisar** — o EKS control plane custa ~US$ 73/mês mesmo sem tráfego.

---

## Outras flags do apply.sh

```bash
./apply.sh                          # ambas as fases com confirmação interativa
./apply.sh --auto                   # ambas as fases sem confirmação
./apply.sh --phase 1 --auto         # somente Fase 1 (VPC + EKS + RDS + ECR)
./apply.sh --phase 2 --auto         # somente Fase 2 (ALB Controller + app)
./apply.sh --pipeline --skip-tests  # pipeline sem rodar os testes Maven
./apply.sh --destroy --auto         # destroi tudo sem confirmação
./apply.sh --bucket meu-bucket      # usar bucket S3 específico para o estado
```

---

## GitHub Actions — configuração de secrets e variáveis

O pipeline CI/CD (`.github/workflows/main.yml`) roda automaticamente em todo push para `master`. Para funcionar, configure os seguintes valores no repositório GitHub:

### Como acessar

`Settings → Secrets and variables → Actions`

### Secrets (`Settings → Secrets → Actions → New repository secret`)

| Nome                    | Valor                 | Onde obter                                                          |
| ----------------------- | --------------------- | ------------------------------------------------------------------- |
| `AWS_ACCESS_KEY_ID`     | `ASIA...`             | AWS Academy → AWS Details                                           |
| `AWS_SECRET_ACCESS_KEY` | `...`                 | AWS Academy → AWS Details                                           |
| `AWS_SESSION_TOKEN`     | `...`                 | AWS Academy → AWS Details                                           |
| `TF_DB_PASSWORD`        | senha do banco        | você define (mínimo 8 chars)                                        |
| `TF_DB_USERNAME`        | `lata_velha_user`     | padrão ou personalize                                               |
| `TF_MAIL_USERNAME`      | `seu@gmail.com`       | sua conta Gmail                                                     |
| `TF_MAIL_PASSWORD`      | `xxxx xxxx xxxx xxxx` | [Senha de App do Google](https://myaccount.google.com/apppasswords) |

### Variables (`Settings → Variables → Actions → New repository variable`)

| Nome               | Valor            |
| ------------------ | ---------------- |
| `AWS_REGION`       | `us-east-1`      |
| `EKS_CLUSTER_NAME` | `lata-velha-eks` |

> **Importante:** As credenciais AWS (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN`) expiram com cada sessão do Academy. Atualize-as antes de cada push para `master`.

### Pipeline do GitHub Actions

```
CI · 1 · Testes          →  compila e roda mvn test com PostgreSQL efêmero
CD · 2 · Infraestrutura  →  apply.sh --phase 1  (VPC + EKS + RDS + ECR)
CD · 3 · Build Docker    →  docker build --platform linux/amd64 + push ECR
CD · 4 · Deploy          →  apply.sh --phase 2  (ALB Controller + app)
CD · 5 · Verificar       →  kubectl rollout status
```

As etapas CD só rodam em push direto para `master` (não em Pull Requests).

---

## Variáveis do Terraform

| Variável                | Padrão            | Descrição                                                    |
| ----------------------- | ----------------- | ------------------------------------------------------------ |
| `region`                | `us-east-1`       | Região AWS                                                   |
| `project_name`          | `lata-velha`      | Prefixo de todos os recursos                                 |
| `environment`           | `dev`             | Tag de ambiente                                              |
| `vpc_cidr`              | `10.0.0.0/16`     | CIDR da VPC                                                  |
| `kubernetes_version`    | `1.33`            | Versão do Kubernetes                                         |
| `node_instance_type`    | `t3.small`        | Tipo de EC2 dos nodes                                        |
| `node_desired_size`     | `2`               | Quantidade inicial de nodes                                  |
| `node_min_size`         | `2`               | Mínimo de nodes                                              |
| `node_max_size`         | `3`               | Máximo de nodes                                              |
| `docker_image`          | `placeholder`     | Imagem ECR (definida automaticamente pelo pipeline)          |
| `db_name`               | `lata_velha`      | Nome do banco                                                |
| `db_username`           | `lata_velha_user` | Usuário do banco                                             |
| `db_password`           | —                 | Senha do banco (**obrigatória**)                             |
| `rds_instance_class`    | `db.t3.micro`     | Classe da instância RDS                                      |
| `mail_username`         | —                 | Email remetente Gmail (**obrigatório**)                      |
| `mail_password`         | —                 | App password do Gmail (**obrigatório**)                      |
| `aws_access_key_id`     | —                 | Injetado via `TF_VAR_` pelo apply.sh — não colocar no tfvars |
| `aws_secret_access_key` | —                 | Injetado via `TF_VAR_` pelo apply.sh — não colocar no tfvars |
| `aws_session_token`     | —                 | Injetado via `TF_VAR_` pelo apply.sh — não colocar no tfvars |

---

## Custo aproximado (us-east-1)

| Recurso                  | Configuração  | Custo/mês        |
| ------------------------ | ------------- | ---------------- |
| EKS control plane        | fixo          | ~US$ 73          |
| EC2 nodes (t3.small × 2) | On-Demand     | ~US$ 30          |
| NAT Gateway              | 1 AZ          | ~US$ 32          |
| ALB                      | 1 ALB         | ~US$ 18          |
| RDS db.t3.micro          | PostgreSQL 15 | ~US$ 15          |
| **Total estimado**       |               | **~US$ 168/mês** |

> No **AWS Academy** o crédito é limitado (~US$ 50). Destrua o ambiente com `./apply.sh --destroy --auto` após cada uso.
