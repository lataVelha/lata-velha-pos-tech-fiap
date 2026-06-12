output "ecr_repository_url" {
  description = "URL do repositorio ECR para push da imagem"
  value       = aws_ecr_repository.app.repository_url
}

output "ecr_push_commands" {
  description = "Comandos para autenticar e fazer push da imagem no ECR"
  value       = <<-EOT
    aws ecr get-login-password --region ${var.region} | docker login --username AWS --password-stdin ${aws_ecr_repository.app.repository_url}
    docker build -t ${aws_ecr_repository.app.repository_url}:latest .
    docker push ${aws_ecr_repository.app.repository_url}:latest
  EOT
}

output "vpc_id" {
  description = "ID da VPC"
  value       = module.vpc.vpc_id
}

output "cluster_name" {
  description = "Nome do cluster EKS"
  value       = module.eks.cluster_name
}

output "cluster_endpoint" {
  description = "Endpoint da API do cluster"
  value       = module.eks.cluster_endpoint
}

output "configure_kubectl" {
  description = "Rode isso para usar kubectl localmente"
  value       = "aws eks update-kubeconfig --region ${var.region} --name ${module.eks.cluster_name}"
}


output "app_url" {
  description = "URL publica da aplicacao (ALB leva ~2 min para ficar ativo apos o apply)"
  value       = "http://${module.alb.dns_name}"
}

output "rds_endpoint" {
  description = "Endpoint de conexao do banco de dados RDS"
  value       = module.rds.endpoint
}

output "rds_db_name" {
  description = "Nome do banco de dados criado no RDS"
  value       = module.rds.db_name
}
