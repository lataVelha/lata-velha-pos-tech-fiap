output "app_url" {
  description = "URL publica da aplicacao (ALB leva ~2 min para ficar ativo apos o apply)"
  value       = "http://${module.alb.dns_name}"
}
