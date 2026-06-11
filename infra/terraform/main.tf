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

# Credenciais AWS injetadas no ALB Controller via secret.
# AWS Academy não permite IRSA (iam:CreateOpenIDConnectProvider bloqueado) e o
# IMDS tem hop limit=1 nos nodes (pods não alcançam o instance profile).
# Valores chegam via TF_VAR_aws_access_key_id/secret/token — nunca no tfvars.
resource "kubernetes_secret" "aws_credentials" {
  metadata {
    name      = "aws-credentials"
    namespace = "kube-system"
  }

  data = {
    AWS_ACCESS_KEY_ID     = var.aws_access_key_id
    AWS_SECRET_ACCESS_KEY = var.aws_secret_access_key
    AWS_SESSION_TOKEN     = var.aws_session_token
  }

  depends_on = [module.eks, time_sleep.wait_for_eks]
}

module "cluster_autoscaler" {
  source = "./modules/cluster-autoscaler"

  cluster_name       = local.cluster_name
  region             = var.region
  kubernetes_version = var.kubernetes_version

  depends_on = [module.eks, kubernetes_secret.aws_credentials]
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

module "alb_controller" {
  source = "./modules/alb-controller"

  cluster_name = local.cluster_name
  vpc_id       = module.vpc.vpc_id
  region       = var.region

  # Ordem de apply:   vpc → null_resource → eks → aws_credentials → alb_controller → app
  # Ordem de destroy: app → alb_controller → null_resource (limpeza ALB/SGs) → vpc
  depends_on = [module.eks, null_resource.cleanup_alb_on_destroy, kubernetes_secret.aws_credentials]
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

  depends_on = [module.eks, module.rds, module.alb_controller, time_sleep.wait_for_eks]
}

# Aguarda o ALB Controller provisionar o ALB e preencher o hostname no Ingress.
# O ALB leva ~2 min após o Ingress ser criado — sem esse sleep o data source
# lê o hostname vazio e o output app_url fica em branco.
resource "time_sleep" "wait_for_alb_url" {
  depends_on      = [module.app]
  create_duration = "120s"
}

data "kubernetes_ingress_v1" "app" {
  metadata {
    name      = "lata-velha-api"
    namespace = "lata-velha"
  }
  depends_on = [time_sleep.wait_for_alb_url]
}

# Durante o destroy, apaga o ALB criado pelo Kubernetes ALB Controller antes que
# a VPC seja deletada. Sem isso, os ENIs do ALB bloqueiam a exclusão da VPC.
# O provisionador roda automaticamente após os outros recursos serem destruídos
# (app, alb_controller, eks) e antes da VPC, pois depende apenas do module.vpc.
resource "null_resource" "cleanup_alb_on_destroy" {
  triggers = {
    region = var.region
    vpc_id = module.vpc.vpc_id
  }

  provisioner "local-exec" {
    when    = destroy
    command = <<-EOT
      REGION="${self.triggers.region}"
      VPC="${self.triggers.vpc_id}"

      echo "==> [cleanup] Deletando ALBs na VPC $VPC..."
      ARNS=$(aws elbv2 describe-load-balancers --region "$REGION" \
        --query "LoadBalancers[?VpcId=='$VPC'].LoadBalancerArn" \
        --output text 2>/dev/null || true)
      for ARN in $ARNS; do
        [ -z "$ARN" ] && continue
        echo "    ALB: $ARN"
        aws elbv2 delete-load-balancer --load-balancer-arn "$ARN" --region "$REGION" 2>/dev/null || true
      done

      echo "==> [cleanup] Deletando target groups na VPC $VPC..."
      TGS=$(aws elbv2 describe-target-groups --region "$REGION" \
        --query "TargetGroups[?VpcId=='$VPC'].TargetGroupArn" \
        --output text 2>/dev/null || true)
      for TG in $TGS; do
        [ -z "$TG" ] && continue
        echo "    TG: $TG"
        aws elbv2 delete-target-group --target-group-arn "$TG" --region "$REGION" 2>/dev/null || true
      done

      echo "    Aguardando ENIs serem liberados (90s)..."
      sleep 90

      echo "==> [cleanup] Revogando regras e deletando todos os SGs não-default na VPC $VPC..."
      SGs=$(aws ec2 describe-security-groups --region "$REGION" \
        --filters "Name=vpc-id,Values=$VPC" \
        --query "SecurityGroups[?GroupName!='default'].GroupId" \
        --output text 2>/dev/null || true)

      # Primeira passagem: revogar todas as regras (resolve dependências cruzadas)
      for SG in $SGs; do
        [ -z "$SG" ] && continue
        INGRESS=$(aws ec2 describe-security-groups --group-ids "$SG" --region "$REGION" \
          --query "SecurityGroups[0].IpPermissions" --output json 2>/dev/null || echo "[]")
        EGRESS=$(aws ec2 describe-security-groups --group-ids "$SG" --region "$REGION" \
          --query "SecurityGroups[0].IpPermissionsEgress" --output json 2>/dev/null || echo "[]")
        [ "$INGRESS" != "[]" ] && aws ec2 revoke-security-group-ingress \
          --group-id "$SG" --ip-permissions "$INGRESS" --region "$REGION" 2>/dev/null || true
        [ "$EGRESS" != "[]" ] && aws ec2 revoke-security-group-egress \
          --group-id "$SG" --ip-permissions "$EGRESS" --region "$REGION" 2>/dev/null || true
      done

      # Segunda passagem: deletar os SGs
      for SG in $SGs; do
        [ -z "$SG" ] && continue
        echo "    SG: $SG"
        aws ec2 delete-security-group --group-id "$SG" --region "$REGION" 2>/dev/null || true
      done

      echo "==> [cleanup] Concluído."
    EOT
  }

  depends_on = [module.vpc]
}
