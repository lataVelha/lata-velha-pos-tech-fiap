variable "cluster_name" {
  description = "Nome do cluster EKS"
  type        = string
}

variable "region" {
  description = "Regiao da AWS"
  type        = string
}

variable "kubernetes_version" {
  description = "Versao do Kubernetes (usada para selecionar a tag da imagem do CA)"
  type        = string
}
