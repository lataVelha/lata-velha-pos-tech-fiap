# Estado remoto (recomendado). Terraform 1.10+ faz lock no S3 (use_lockfile),
# sem DynamoDB.
#
# 1) crie o bucket: aws s3 mb s3://SEU-BUCKET-tfstate
# 2) descomente e ajuste
# 3) terraform init -migrate-state
#
# terraform {
#   backend "s3" {
#     bucket       = "SEU-BUCKET-tfstate"
#     key          = "eks-nginx/terraform.tfstate"
#     region       = "us-east-1"
#     encrypt      = true
#     use_lockfile = true
#   }
# }
