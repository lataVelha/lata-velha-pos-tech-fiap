variable "region" {
  description = "Regiao da AWS"
  type        = string
  default     = "us-east-1"
}

variable "state_bucket" {
  description = "Bucket S3 onde este state e gravado e onde o infra-db publica o dele"
  type        = string
}

# Conexao com o EKS (provisionado pelo repo infra) — lidas via AWS CLI no
# pipeline/apply.sh e passadas como TF_VAR_, ja que provider config nao usa
# data sources para a propria inicializacao.
variable "cluster_endpoint" {
  description = "Endpoint do EKS cluster"
  type        = string
}

variable "cluster_ca_data" {
  description = "CA certificate do EKS cluster (base64)"
  type        = string
  sensitive   = true
}

variable "cluster_name" {
  description = "Nome do EKS cluster"
  type        = string
}

variable "docker_image" {
  description = "Imagem ECR da aplicacao (definida pelo pipeline apos o build/push)"
  type        = string
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
