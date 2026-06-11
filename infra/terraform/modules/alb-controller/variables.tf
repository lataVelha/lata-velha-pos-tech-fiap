variable "cluster_name" {
  description = "Nome do cluster EKS"
  type        = string
}

variable "vpc_id" {
  description = "ID da VPC"
  type        = string
}

variable "region" {
  description = "Regiao da AWS"
  type        = string
}
