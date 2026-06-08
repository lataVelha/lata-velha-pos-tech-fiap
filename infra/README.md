# terraform-aws-eks-nginx

Infraestrutura completa na AWS usando **Terraform**: VPC, cluster EKS com node group gerenciado e **nginx rodando como pods**, exposto publicamente via **AWS Classic Load Balancer**. Tudo provisionado e configurado pelo Terraform — incluindo os objetos Kubernetes.

Compatível com **AWS Academy (Learner Lab)** — detecta automaticamente o usuário/role atual e usa a `LabRole` pré-existente, sem precisar criar IAM roles.

---

## O que é criado

```
Internet
   │
   ▼
AWS Load Balancer       ← provisionado automaticamente pelo Service Kubernetes
   │
   ▼
EKS Node Group (EC2 t3.small)   ← nodes em subnet privada
   │
   ▼
Pods nginx (2 réplicas)
```

### Recursos AWS

| Recurso               | Descrição                                                    |
| --------------------- | ------------------------------------------------------------ |
| VPC                   | CIDR `10.0.0.0/16`, 2 AZs                                    |
| Subnets públicas      | Para o Load Balancer                                         |
| Subnets privadas      | Para os nodes EKS                                            |
| Internet Gateway      | Saída pública                                                |
| NAT Gateway           | Permite que os nodes (privados) puxem imagens da internet    |
| EKS Cluster           | Control plane gerenciado pela AWS                            |
| EKS Node Group        | EC2s que executam os pods                                    |
| EKS Access Entry      | Acesso admin concedido ao usuário/role atual automaticamente |
| Classic Load Balancer | Criado pelo Kubernetes ao aplicar o Service                  |

### Recursos Kubernetes

| Recurso              | Descrição                                 |
| -------------------- | ----------------------------------------- |
| Deployment           | 2 pods nginx:1.27                         |
| Service LoadBalancer | Expõe os pods via ELB público na porta 80 |

---

## Estrutura do projeto

```
terraform-aws-eks-nginx/
├── main.tf                    # data sources, locals e chamadas de módulos
├── variables.tf               # variáveis de entrada
├── outputs.tf                 # outputs (URL do nginx, comando kubectl)
├── providers.tf               # providers aws e kubernetes
├── versions.tf                # versões mínimas do Terraform e providers
├── backend.tf                 # estado remoto S3 (opcional, comentado)
├── terraform.tfvars.example   # exemplo de valores
└── modules/
    ├── vpc/                   # VPC, subnets, IGW, NAT Gateway, route tables
    ├── eks/                   # cluster EKS, node group, access entry
    └── nginx/                 # Deployment e Service do nginx
```

Cada módulo tem responsabilidade única:

- **vpc** — apenas rede
- **eks** — apenas cluster e nodes
- **nginx** — apenas a aplicação

---

## Compatibilidade com AWS Academy

O AWS Academy (Learner Lab) restringe várias operações IAM. Este projeto contorna essas restrições automaticamente:

| Restrição do Academy                | Como é resolvida                                                            |
| ----------------------------------- | --------------------------------------------------------------------------- |
| `iam:CreateRole` negado             | Usa a `LabRole` pré-existente para cluster e nodes                          |
| `iam:GetRole` negado                | Não usa `data "aws_iam_role"` — ARN construído via account ID               |
| `ssm:GetParameter` negado           | Não usa o community EKS module (que buscava AMI via SSM)                    |
| ARN de assumed-role no Access Entry | Convertido automaticamente para ARN de IAM role via `sts:GetCallerIdentity` |

Nenhuma configuração manual necessária — tudo é detectado automaticamente.

---

## Pré-requisitos

| Ferramenta | Versão mínima | Para que serve                                           |
| ---------- | ------------- | -------------------------------------------------------- |
| Terraform  | 1.6           | provisionar tudo                                         |
| AWS CLI v2 | qualquer      | autenticar o provider Kubernetes via `aws eks get-token` |
| kubectl    | qualquer      | opcional, para inspecionar o cluster                     |

Configure as credenciais antes de rodar:

```bash
# Conta AWS normal
aws configure

# AWS Academy — cole as credenciais do Learner Lab (AWS Details > AWS CLI)
aws configure
# informe: Access Key ID, Secret Access Key, Session Token, região
```

---

## Como subir

```bash
# 1. Copie o arquivo de variáveis
cp terraform.tfvars.example terraform.tfvars

# 2. Inicialize os providers
terraform init

# 3. Veja o que será criado (opcional)
terraform plan

# 4. Aplique
terraform apply
```

O `apply` demora **15–20 minutos**. Ao final, o Terraform exibe:

```
cluster_name      = "meu-projeto-eks"
cluster_endpoint  = "https://..."
configure_kubectl = "aws eks update-kubeconfig --region us-east-1 --name meu-projeto-eks"
nginx_url         = "http://<hostname>.us-east-1.elb.amazonaws.com"
```

Abra o `nginx_url` no navegador — se aparecer **"Welcome to nginx!"**, o deploy funcionou.

> O ELB leva ~2 minutos após o `apply` para estar pronto. Se a URL retornar erro, aguarde e tente novamente.

> Se o `apply` falhar na etapa do Kubernetes por timing (cluster ainda inicializando), rode `terraform apply` de novo — ele continua de onde parou.

---

## Verificando o deploy

```bash
# Aponta o kubectl para o cluster
aws eks update-kubeconfig --region us-east-1 --name meu-projeto-eks

# Lista os pods
kubectl get pods

# Verifica o Service e o endereço do Load Balancer
kubectl get svc nginx
```

---

## Variáveis disponíveis

| Variável             | Padrão        | Descrição                    |
| -------------------- | ------------- | ---------------------------- |
| `region`             | `us-east-1`   | Região AWS                   |
| `project_name`       | `meu-projeto` | Prefixo de todos os recursos |
| `environment`        | `dev`         | Tag de ambiente              |
| `vpc_cidr`           | `10.0.0.0/16` | CIDR da VPC                  |
| `kubernetes_version` | `1.33`        | Versão do Kubernetes         |
| `node_instance_type` | `t3.small`    | Tipo de EC2 dos nodes        |
| `node_desired_size`  | `2`           | Quantidade inicial de nodes  |
| `node_min_size`      | `1`           | Mínimo de nodes              |
| `node_max_size`      | `3`           | Máximo de nodes              |
| `nginx_replicas`     | `2`           | Quantidade de pods nginx     |

---

## Custo aproximado (us-east-1)

> Cobrado por hora enquanto os recursos existirem.

| Recurso                            | Configuração | Custo/mês        |
| ---------------------------------- | ------------ | ---------------- |
| EKS control plane                  | fixo         | ~US$ 73          |
| EC2 nodes (t3.small x2, On-Demand) | 2 nodes      | ~US$ 30          |
| NAT Gateway                        | 1 AZ         | ~US$ 32          |
| Load Balancer                      | 1 ELB        | ~US$ 18          |
| **Total estimado**                 |              | **~US$ 153/mês** |

O **EKS control plane (US$73/mês)** é o maior custo e não pode ser reduzido enquanto o cluster existir.

Não é free tier. **Destrua o ambiente quando terminar de testar.**

---

## Como destruir

```bash
terraform destroy
```

> Se o destroy travar no Load Balancer, delete o Service manualmente e rode novamente:
>
> ```bash
> kubectl delete svc nginx
> terraform destroy
> ```

---

## Ajustes comuns

**Outra região:**

```hcl
# terraform.tfvars
region = "us-west-2"
```

**Menos nodes (reduzir custo):**

```hcl
node_desired_size = 1
node_min_size     = 1
```

**Mais réplicas do nginx:**

```hcl
nginx_replicas = 3
```

**Estado remoto no S3** (recomendado para times): descomente o bloco em `backend.tf` e rode `terraform init -migrate-state`.
