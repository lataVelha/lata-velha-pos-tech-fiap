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

output "nginx_loadbalancer_hostname" {
  description = "Hostname do ELB do nginx"
  value       = module.nginx.loadbalancer_hostname
}

output "nginx_url" {
  description = "URL para abrir o nginx no navegador"
  value       = module.nginx.url
}
