variable "region" {
  description = "Regiao da AWS"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Prefixo dos recursos"
  type        = string
  default     = "lata-velha"
}

variable "environment" {
  description = "Ambiente (dev, staging, prod)"
  type        = string
  default     = "dev"
}

variable "vpc_cidr" {
  description = "CIDR da VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "kubernetes_version" {
  description = "Versao do Kubernetes no EKS"
  type        = string
  default     = "1.33"
}

variable "node_instance_type" {
  description = "Tipo das EC2 dos nodes"
  type        = string
  default     = "t3.small"
}

variable "node_desired_size" {
  description = "Quantidade desejada de nodes"
  type        = number
  default     = 2
}

variable "node_min_size" {
  description = "Minimo de nodes"
  type        = number
  default     = 1
}

variable "node_max_size" {
  description = "Maximo de nodes (deve suportar o maxReplicas do HPA)"
  type        = number
  default     = 2
}

variable "docker_image" {
  description = "Imagem ECR da aplicacao. Definido automaticamente pelo apply.sh --pipeline."
  type        = string
  default     = "placeholder"
}

variable "db_name" {
  description = "Nome do banco de dados"
  type        = string
  default     = "lata_velha"
}

variable "db_username" {
  description = "Usuario do banco de dados"
  type        = string
  default     = "lata_velha_user"
  sensitive   = true
}

variable "db_password" {
  description = "Senha do banco de dados"
  type        = string
  sensitive   = true
}

variable "rds_instance_class" {
  description = "Classe da instancia RDS"
  type        = string
  default     = "db.t3.micro"
}


variable "mail_username" {
  description = "Email remetente (Gmail)"
  type        = string
  sensitive   = true
}

variable "mail_password" {
  description = "Senha de app do Gmail"
  type        = string
  sensitive   = true
}

# Credenciais AWS para o ALB Controller no AWS Academy.
# O AWS Academy não permite IRSA (sem OIDC) e o IMDS tem hop limit=1 nos nodes.
# Passadas via TF_VAR_ no apply.sh e no GitHub Actions — nunca no tfvars.
variable "aws_access_key_id" {
  description = "AWS Access Key ID (ALB Controller — AWS Academy)"
  type        = string
  sensitive   = true
  default     = ""
}

variable "aws_secret_access_key" {
  description = "AWS Secret Access Key (ALB Controller — AWS Academy)"
  type        = string
  sensitive   = true
  default     = ""
}

variable "aws_session_token" {
  description = "AWS Session Token (ALB Controller — AWS Academy)"
  type        = string
  sensitive   = true
  default     = ""
}
