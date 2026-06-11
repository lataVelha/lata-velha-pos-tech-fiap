output "endpoint" {
  description = "Endpoint de conexao do RDS (host:porta)"
  value       = aws_db_instance.this.endpoint
}

output "db_name" {
  description = "Nome do banco de dados criado"
  value       = aws_db_instance.this.db_name
}
