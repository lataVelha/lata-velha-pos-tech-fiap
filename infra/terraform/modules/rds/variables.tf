variable "identifier" {
  description = "Identificador da instancia RDS"
  type        = string
}

variable "vpc_id" {
  description = "ID da VPC"
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR da VPC (usado no security group)"
  type        = string
}

variable "subnet_ids" {
  description = "IDs das subnets privadas para o DB subnet group"
  type        = list(string)
}

variable "instance_class" {
  description = "Classe da instancia RDS"
  type        = string
  default     = "db.t3.micro"
}

variable "allocated_storage" {
  description = "Armazenamento alocado em GB"
  type        = number
  default     = 20
}

variable "db_name" {
  description = "Nome do banco de dados"
  type        = string
}

variable "db_username" {
  description = "Usuario do banco de dados"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "Senha do banco de dados"
  type        = string
  sensitive   = true
}
