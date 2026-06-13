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

variable "autoscaler_image_tag" {
  description = "Tag da imagem do cluster-autoscaler. Deve existir em registry.k8s.io/autoscaling/cluster-autoscaler"
  type        = string
  default     = "v1.35.0"
}
