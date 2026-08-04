# O banco de dados (RDS) e provisionado pelo repo infra-db, que publica seu
# state neste mesmo bucket. Precisa rodar antes deste deploy.
data "terraform_remote_state" "infra_db" {
  backend = "s3"
  config = {
    bucket = var.state_bucket
    key    = "lata-velha/infra-db/terraform.tfstate"
    region = var.region
  }
}

locals {
  infra_db = data.terraform_remote_state.infra_db.outputs
}

module "app" {
  source = "./modules/app"

  docker_image  = var.docker_image
  db_url        = "jdbc:postgresql://${local.infra_db.rds_endpoint}/${local.infra_db.db_name}"
  db_username   = local.infra_db.db_username
  db_password   = local.infra_db.db_password
  mail_username = var.mail_username
  mail_password = var.mail_password
}
