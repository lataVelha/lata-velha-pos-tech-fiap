output "loadbalancer_hostname" {
  description = "Hostname do ELB provisionado pelo Service"
  value       = try(kubernetes_service_v1.nginx.status[0].load_balancer[0].ingress[0].hostname, "(provisionando)")
}

output "url" {
  description = "URL publica do nginx"
  value       = try("http://${kubernetes_service_v1.nginx.status[0].load_balancer[0].ingress[0].hostname}", "(aguarde ~2 min)")
}
