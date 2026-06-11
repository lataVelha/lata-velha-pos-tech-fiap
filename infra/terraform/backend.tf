# Backend S3 com configuração parcial.
# bucket e region são passados via -backend-config (CI) ou terraform init interativo.
# O bucket é criado automaticamente pelo apply.sh (local) e pelo pipeline (CI/CD).
#
# Inicializar localmente (use ./apply.sh — ele cria o bucket e chama o init):
#   cd infra/terraform && ./apply.sh

terraform {
  backend "s3" {
    key          = "lata-velha/terraform.tfstate"
    encrypt      = true
    use_lockfile = true
  }
}
