data "aws_caller_identity" "current" {}

data "aws_availability_zones" "available" {
  state = "available"
}

locals {
  cluster_name    = "${var.project_name}-eks"
  caller_arn      = data.aws_caller_identity.current.arn
  is_assumed_role = can(regex(":assumed-role/", local.caller_arn))
  role_name       = local.is_assumed_role ? split("/", split(":", local.caller_arn)[5])[1] : ""
  admin_arn       = local.is_assumed_role ? "arn:aws:iam::${data.aws_caller_identity.current.account_id}:role/${local.role_name}" : local.caller_arn
  lab_role_arn    = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:role/LabRole"
}

module "vpc" {
  source = "./modules/vpc"

  name               = var.project_name
  vpc_cidr           = var.vpc_cidr
  azs                = slice(data.aws_availability_zones.available.names, 0, 2)
  enable_nat_gateway = true
  cluster_name       = local.cluster_name
}

module "eks" {
  source = "./modules/eks"

  cluster_name       = local.cluster_name
  kubernetes_version = var.kubernetes_version
  subnet_ids         = module.vpc.private_subnet_ids
  node_instance_type = var.node_instance_type
  node_min_size      = var.node_min_size
  node_max_size      = var.node_max_size
  node_desired_size  = var.node_desired_size
  iam_role_arn       = local.lab_role_arn
  cluster_admin_arns = [local.admin_arn]

  depends_on = [module.vpc]
}

# Credenciais AWS para o Cluster Autoscaler.
# AWS Academy não permite IRSA — valores chegam via TF_VAR_ e nunca ficam no tfvars.
resource "kubectl_manifest" "aws_credentials" {
  yaml_body = yamlencode({
    apiVersion = "v1"
    kind       = "Secret"
    metadata = {
      name      = "aws-credentials"
      namespace = "kube-system"
    }
    type = "Opaque"
    data = {
      AWS_ACCESS_KEY_ID     = base64encode(var.aws_access_key_id)
      AWS_SECRET_ACCESS_KEY = base64encode(var.aws_secret_access_key)
      AWS_SESSION_TOKEN     = base64encode(var.aws_session_token)
    }
  })
  sensitive_fields = ["data"]
  depends_on       = [module.eks, time_sleep.wait_for_eks]
}

module "cluster_autoscaler" {
  source = "./modules/cluster-autoscaler"

  cluster_name       = local.cluster_name
  region             = var.region
  kubernetes_version = var.kubernetes_version

  depends_on = [module.eks, kubectl_manifest.aws_credentials]
}

# Metrics Server — obrigatório para o HPA ler CPU/memória dos pods.
resource "helm_release" "metrics_server" {
  name       = "metrics-server"
  repository = "https://kubernetes-sigs.github.io/metrics-server/"
  chart      = "metrics-server"
  namespace  = "kube-system"
  version    = "3.12.1"

  set {
    name  = "args[0]"
    value = "--kubelet-use-node-status-port"
  }

  depends_on = [module.eks, time_sleep.wait_for_eks]
}

module "alb" {
  source = "./modules/alb"

  name                   = var.project_name
  vpc_id                 = module.vpc.vpc_id
  public_subnet_ids      = module.vpc.public_subnet_ids
  node_security_group_id = module.eks.cluster_security_group_id
  node_asg_name          = module.eks.node_asg_name

  depends_on = [module.eks, time_sleep.wait_for_eks]
}

module "rds" {
  source = "./modules/rds"

  identifier     = "${var.project_name}-postgres"
  vpc_id         = module.vpc.vpc_id
  vpc_cidr       = var.vpc_cidr
  subnet_ids     = module.vpc.private_subnet_ids
  db_name        = var.db_name
  db_username    = var.db_username
  db_password    = var.db_password
  instance_class = var.rds_instance_class

  depends_on = [module.vpc]
}

resource "aws_ecr_repository" "app" {
  name                 = var.project_name
  image_tag_mutability = "MUTABLE"
  force_delete         = true

  image_scanning_configuration {
    scan_on_push = true
  }
}

# Aguarda o API server do EKS estabilizar após a criação do cluster e dos nodes.
# Sem esse delay, o kubectl provider pode falhar com "context deadline exceeded"
# na primeira execução quando o cluster acabou de ser provisionado.
resource "time_sleep" "wait_for_eks" {
  depends_on      = [module.eks]
  create_duration = "60s"
}

module "app" {
  source = "./modules/app"

  docker_image  = var.docker_image
  db_url        = "jdbc:postgresql://${module.rds.endpoint}/${var.db_name}"
  db_username   = var.db_username
  db_password   = var.db_password
  mail_username = var.mail_username
  mail_password = var.mail_password

  depends_on = [module.eks, module.rds, module.alb, time_sleep.wait_for_eks]
}

