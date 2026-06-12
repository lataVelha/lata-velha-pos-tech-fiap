# Infraestrutura — Lata Velha (AWS + EKS)

Infraestrutura como código para o projeto **Lata Velha** na AWS usando **Terraform >= 1.6**.

Compatível com **AWS Academy (Learner Lab)** — usa a `LabRole` pré-existente sem precisar criar IAM roles ou OIDC providers.

---

## Arquitetura

```
Internet
   │
   ▼
AWS ALB (Application Load Balancer)          ← Terraform: modules/alb
   │  HTTP 80 → NodePort 30080
   ▼
EKS Node Group (EC2 t3.small × 2)           ← nodes em subnet privada
   │
   ▼
Pods lata-velha-api (2 réplicas, HPA até 10)
   │
   ▼
RDS PostgreSQL 15 (db.t3.micro)             ← subnet privada, sem acesso público
```

### Por que ALB gerenciado pelo Terraform e não pelo AWS Load Balancer Controller?

O **ALB Controller** cria recursos AWS (ALBs, Security Groups, ENIs) fora do estado
do Terraform. No `destroy`, esses recursos bloqueiam a deleção da VPC e exigem scripts
de limpeza externos — uma solução frágil.

Com o ALB provisionado diretamente pelo Terraform via `aws_lb`, `aws_lb_target_group`
e `aws_autoscaling_attachment`, o `terraform destroy` remove tudo na ordem certa,
sem scripts auxiliares.

O tráfego chega ao ALB na porta 80 e é encaminhado para o **NodePort 30080** de
qualquer node do EKS. A associação ao ASG (`aws_autoscaling_attachment`) registra
e desregistra nodes automaticamente conforme o cluster escala.

---

## O que é provisionado

### Módulo `bootstrap` — infraestrutura base

| Recurso        | Descrição                                                             |
| -------------- | --------------------------------------------------------------------- |
| VPC            | CIDR `10.0.0.0/16`, 2 AZs, subnets públicas + privadas + NAT Gateway |
| ECR            | Repositório Docker privado (`lata-velha`)                             |
| EKS Cluster    | Control plane gerenciado, Kubernetes 1.33                             |
| EKS Node Group | EC2 `t3.small`, autoscaling min 2 / max 3                             |
| RDS PostgreSQL | `db.t3.micro`, 20 GB, sem multi-AZ, subnet privada                    |

### Módulo `deploy` — aplicação e roteamento

| Recurso AWS              | Descrição                                                         |
| ------------------------ | ----------------------------------------------------------------- |
| ALB                      | Application Load Balancer internet-facing, HTTP 80                |
| Target Group             | `target_type=instance`, NodePort 30080, health check `/actuator/health` |
| Listener                 | HTTP 80 → forward para o Target Group                             |
| Security Group (ALB)     | Permite entrada HTTP 80 da internet                               |
| Security Group Rule      | Permite tráfego ALB → nodes EKS na porta 30080                    |
| ASG Attachment           | Registra automaticamente os nodes EKS no Target Group             |

| Recurso Kubernetes       | Descrição                                                         |
| ------------------------ | ----------------------------------------------------------------- |
| Namespace                | `lata-velha`                                                      |
| Secret `aws-credentials` | Credenciais AWS para o Cluster Autoscaler (kube-system)           |
| ConfigMap                | Variáveis não-sensíveis da aplicação                              |
| Secret                   | Credenciais do banco e e-mail (base64 via Terraform)              |
| Deployment               | 2 réplicas com probes de liveness/readiness/startup               |
| Service                  | NodePort 30080 → 8080                                             |
| HPA                      | Autoscaling por CPU (70%) entre 2 e 10 réplicas                   |
| Cluster Autoscaler       | Adiciona/remove nodes EC2 conforme demanda do HPA                 |
| Metrics Server           | Coleta CPU/memória dos pods — obrigatório para o HPA              |

---

## Estrutura de arquivos

```
infra/
├── k8s/                          # Manifests Kubernetes (aplicados pelo módulo deploy)
│   ├── namespace.yaml
│   ├── configmap.yaml
│   ├── secret.yaml               # template — valores base64 injetados pelo Terraform
│   ├── deployment.yaml           # template — docker_image injetado pelo Terraform
│   ├── service.yaml              # NodePort 30080
│   └── hpa.yaml
└── terraform/
    ├── apply.sh                  # Orquestrador de deploy (local e CI/CD)
    ├── bootstrap/                # Etapa 1: VPC + EKS + RDS + ECR
    │   ├── main.tf
    │   ├── variables.tf
    │   ├── outputs.tf            # Expõe endpoints e IDs para o módulo deploy
    │   ├── providers.tf          # Somente provider AWS
    │   ├── versions.tf
    │   ├── backend.tf            # Estado em S3: lata-velha/bootstrap/terraform.tfstate
    │   └── terraform.tfvars.example
    ├── deploy/                   # Etapa 2: ALB + app + autoscaler
    │   ├── main.tf               # Lê infra via terraform_remote_state
    │   ├── variables.tf          # Conexão EKS via variáveis (para provider config)
    │   ├── outputs.tf            # app_url
    │   ├── providers.tf          # Providers AWS + kubectl + helm
    │   ├── versions.tf
    │   ├── backend.tf            # Estado em S3: lata-velha/deploy/terraform.tfstate
    │   └── terraform.tfvars.example
    └── modules/                  # Módulos reutilizáveis
        ├── vpc/
        ├── eks/
        ├── rds/
        ├── alb/
        ├── cluster-autoscaler/
        └── app/                  # kubectl_manifest para todos os objetos k8s
```

### Por que dois módulos Terraform separados (`bootstrap` e `deploy`)?

Os providers `kubectl` e `helm` precisam do endpoint do cluster EKS para serem
inicializados — e esse endpoint só existe após o EKS ser criado.

Em um único módulo isso cria um problema: o Terraform tenta configurar os providers
antes de provisionar o cluster, e falha. A solução padrão é dividir em dois módulos
com estados independentes:

- **`bootstrap`** provisiona o EKS (e VPC/RDS/ECR) usando apenas o provider AWS.
- **`deploy`** lê o endpoint do EKS via `terraform_remote_state` e usa esse valor
  para configurar os providers `kubectl` e `helm` — que já encontram o cluster ativo.

O `apply.sh` lê os outputs do `bootstrap` e os passa para o `deploy` via variáveis
de ambiente (`TF_VAR_`), já que blocos `provider` não aceitam `data sources`.

---

## Compatibilidade com AWS Academy

O AWS Academy (Learner Lab) tem restrições de IAM. A tabela abaixo mostra como cada uma é resolvida:

| Restrição                                 | Como é resolvida                                                            |
| ----------------------------------------- | --------------------------------------------------------------------------- |
| `iam:CreateRole` negado                   | Usa a `LabRole` pré-existente para cluster, nodes e ALB                     |
| `iam:CreateOpenIDConnectProvider` negado  | IRSA removido — credenciais injetadas via `kubectl_manifest` (Secret k8s)   |
| IMDS hop limit = 1 nos nodes              | Pods não alcançam instance profile — resolvido pelo secret acima            |
| ARN de assumed-role no Access Entry       | Convertido automaticamente para ARN de IAM role via `sts:GetCallerIdentity` |
| Recursos externos bloqueando VPC no destroy | ALB gerenciado pelo Terraform — destruído em ordem correta sem scripts    |

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

## Rodando localmente — passo a passo

### 1. Configurar o AWS CLI

Antes de qualquer coisa, o AWS CLI precisa estar autenticado. No **AWS Academy**, abra
o Learner Lab, clique em **AWS Details** e escolha uma das opções abaixo:

**Opção A — variáveis de ambiente (recomendado para sessões únicas):**

```bash
export AWS_ACCESS_KEY_ID=ASIA...
export AWS_SECRET_ACCESS_KEY=...
export AWS_SESSION_TOKEN=...
export AWS_DEFAULT_REGION=us-east-1
```

**Opção B — `aws configure` (persiste entre terminais):**

```bash
aws configure
# AWS Access Key ID:     ASIA...
# AWS Secret Access Key: ...
# Default region name:   us-east-1
# Default output format: json

# O Session Token precisa ser adicionado separadamente:
aws configure set aws_session_token ...
```

> As credenciais do Academy expiram em ~4 horas. Repita este passo sempre que a sessão expirar.

Verifique se está autenticado:

```bash
aws sts get-caller-identity
```

Deve retornar seu `Account`, `UserId` e `Arn` sem erros.

### 2. Configurar as variáveis do Terraform

```bash
cd infra/terraform
cp bootstrap/terraform.tfvars.example bootstrap/terraform.tfvars
cp deploy/terraform.tfvars.example    deploy/terraform.tfvars
```

Edite `bootstrap/terraform.tfvars`:

```hcl
db_password = "sua_senha_do_banco"   # mínimo 8 caracteres
```

Edite `deploy/terraform.tfvars`:

```hcl
mail_username = "seu@gmail.com"
mail_password = "xxxx xxxx xxxx xxxx"   # Senha de App do Gmail
```

> `db_password` e `db_username` são definidos **apenas no bootstrap** — o módulo `deploy` lê esses valores automaticamente via `terraform_remote_state`, sem duplicação.

> **Nunca commite os arquivos `terraform.tfvars`** — já estão no `.gitignore`.

### 3. Rodar o pipeline completo

```bash
./apply.sh --pipeline --auto
```

O pipeline executa automaticamente em 5 etapas:

```
[1/5] Testes     →  PostgreSQL Docker efêmero + mvn test
[2/5] Bootstrap  →  terraform apply  (VPC + EKS + RDS + ECR)   ~15 min
[3/5] Docker     →  docker build --platform linux/amd64 + push para ECR
[4/5] Deploy     →  terraform apply  (ALB + app + autoscaler)
[5/5] Verificar  →  kubectl rollout status  (timeout 5 min)
```

Tempo total: **~20–30 minutos** na primeira execução.

**Quer pular os testes Maven?**

```bash
./apply.sh --pipeline --auto --skip-test
```

**Quer rodar por etapa** (ex: EKS já existe, só quer redeployar o app)?

```bash
# Somente a infraestrutura base
./apply.sh --bootstrap --auto

# Somente o deploy da aplicação
./apply.sh --deploy --auto
```

### 4. Verificar o deploy

```bash
# Aponta o kubectl para o cluster
aws eks update-kubeconfig --region us-east-1 --name lata-velha-eks

# Verifica se os pods subiram
kubectl get pods -n lata-velha

# Pega a URL do ALB (aguarde ~2 min para ficar ativo)
terraform -chdir=deploy output app_url

# Testa a API
curl http://<DNS_DO_ALB>/actuator/health
```

### 5. Destruir o ambiente

```bash
./apply.sh --destroy --auto
```

O destroy acontece em ordem reversa:

1. **Deploy** — remove ALB, Target Group, app Kubernetes e autoscaler
2. **Bootstrap** — remove EKS, VPC, RDS, ECR
3. **Bucket S3** — remove o estado remoto do Terraform

> **Destrua o ambiente quando não precisar** — o EKS control plane custa ~US$ 73/mês mesmo sem tráfego.

---

## Flags do apply.sh

```bash
# Execução normal
./apply.sh                      # bootstrap + deploy com confirmação interativa
./apply.sh --auto               # bootstrap + deploy sem confirmação

# Por etapa
./apply.sh --bootstrap          # somente VPC + EKS + RDS + ECR
./apply.sh --bootstrap --auto   # idem, sem confirmação
./apply.sh --deploy             # somente ALB + app + autoscaler
./apply.sh --deploy --auto      # idem, sem confirmação

# Pipeline completo (igual ao CI/CD)
./apply.sh --pipeline --auto        # testes + bootstrap + docker + deploy + verificar
./apply.sh --pipeline --skip-test   # mesmo acima, pulando os testes Maven

# Outros
./apply.sh --destroy --auto     # destroi tudo sem confirmação
./apply.sh --bucket meu-bucket  # usar bucket S3 específico para o estado
```

---

## GitHub Actions — configuração de secrets e variáveis

O pipeline CI/CD (`.github/workflows/main.yml`) roda automaticamente em todo push para `master`. Para funcionar, configure os seguintes valores no repositório GitHub:

`Settings → Secrets and variables → Actions`

### Secrets

| Nome                    | Valor                 | Onde obter                                                          |
| ----------------------- | --------------------- | ------------------------------------------------------------------- |
| `AWS_ACCESS_KEY_ID`     | `ASIA...`             | AWS Academy → AWS Details                                           |
| `AWS_SECRET_ACCESS_KEY` | `...`                 | AWS Academy → AWS Details                                           |
| `AWS_SESSION_TOKEN`     | `...`                 | AWS Academy → AWS Details                                           |
| `TF_DB_PASSWORD`        | senha do banco        | você define (mínimo 8 chars)                                        |
| `TF_DB_USERNAME`        | `lata_velha_user`     | padrão ou personalize                                               |
| `TF_MAIL_USERNAME`      | `seu@gmail.com`       | sua conta Gmail                                                     |
| `TF_MAIL_PASSWORD`      | `xxxx xxxx xxxx xxxx` | [Senha de App do Google](https://myaccount.google.com/apppasswords) |

### Variables

| Nome               | Valor            |
| ------------------ | ---------------- |
| `AWS_REGION`       | `us-east-1`      |
| `EKS_CLUSTER_NAME` | `lata-velha-eks` |

> **Importante:** As credenciais AWS expiram com cada sessão do Academy. Atualize-as antes de cada push para `master`.

### Pipeline do GitHub Actions

```
CI · 1 · Testes      →  mvn test com PostgreSQL efêmero
CD · 2 · Bootstrap   →  apply.sh --bootstrap  (VPC + EKS + RDS + ECR)
CD · 3 · Build       →  docker build + push para ECR
CD · 4 · Deploy      →  apply.sh --deploy     (ALB + app + autoscaler)
CD · 5 · Verificar   →  kubectl rollout status (timeout 5 min)
```

As etapas CD só rodam em push direto para `master` (não em Pull Requests).

---

## Variáveis do Terraform

### Módulo `bootstrap`

| Variável             | Padrão            | Descrição                           |
| -------------------- | ----------------- | ----------------------------------- |
| `region`             | `us-east-1`       | Região AWS                          |
| `project_name`       | `lata-velha`      | Prefixo de todos os recursos        |
| `environment`        | `dev`             | Tag de ambiente                     |
| `vpc_cidr`           | `10.0.0.0/16`     | CIDR da VPC                         |
| `kubernetes_version` | `1.33`            | Versão do Kubernetes                |
| `node_instance_type` | `t3.small`        | Tipo de EC2 dos nodes               |
| `node_desired_size`  | `2`               | Quantidade inicial de nodes         |
| `node_min_size`      | `1`               | Mínimo de nodes                     |
| `node_max_size`      | `2`               | Máximo de nodes                     |
| `db_name`            | `lata_velha`      | Nome do banco                       |
| `db_username`        | `lata_velha_user` | Usuário do banco                    |
| `db_password`        | —                 | Senha do banco (**obrigatória**)    |
| `rds_instance_class` | `db.t3.micro`     | Classe da instância RDS             |

### Módulo `deploy`

| Variável                | Padrão        | Descrição                                                        |
| ----------------------- | ------------- | ---------------------------------------------------------------- |
| `region`                | `us-east-1`   | Região AWS                                                       |
| `project_name`          | `lata-velha`  | Prefixo dos recursos                                             |
| `docker_image`          | `placeholder` | Imagem ECR (definida automaticamente pelo pipeline)              |
| `db_username`           | `lata_velha_user` | Usuário do banco                                             |
| `db_password`           | —             | Senha do banco (**obrigatória**)                                 |
| `mail_username`         | —             | Email remetente Gmail (**obrigatório**)                          |
| `mail_password`         | —             | App password do Gmail (**obrigatório**)                          |
| `state_bucket`          | —             | Injetado pelo apply.sh — não colocar no tfvars                   |
| `cluster_endpoint`      | —             | Injetado pelo apply.sh — não colocar no tfvars                   |
| `cluster_ca_data`       | —             | Injetado pelo apply.sh — não colocar no tfvars                   |
| `cluster_name`          | —             | Injetado pelo apply.sh — não colocar no tfvars                   |
| `aws_access_key_id`     | —             | Injetado pelo apply.sh — não colocar no tfvars                   |
| `aws_secret_access_key` | —             | Injetado pelo apply.sh — não colocar no tfvars                   |
| `aws_session_token`     | —             | Injetado pelo apply.sh — não colocar no tfvars                   |

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
