output "app_api_endpoint" {
  description = "URL publica da aplicacao — ponto de entrada unico (API Gateway -> VPC Link -> ALB interno)"
  value       = local.addons.app_api_endpoint
}
