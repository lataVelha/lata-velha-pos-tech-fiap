resource "helm_release" "alb_controller" {
  name       = "aws-load-balancer-controller"
  repository = "https://aws.github.io/eks-charts"
  chart      = "aws-load-balancer-controller"
  version    = "1.8.1"
  namespace  = "kube-system"

  set {
    name  = "clusterName"
    value = var.cluster_name
  }

  set {
    name  = "region"
    value = var.region
  }

  set {
    name  = "vpcId"
    value = var.vpc_id
  }

  set {
    name  = "serviceAccount.create"
    value = "true"
  }

  set {
    name  = "serviceAccount.name"
    value = "aws-load-balancer-controller"
  }

  # AWS Academy não permite IRSA (sem OIDC provider) nem IMDS de pods (hop limit=1).
  # O apply.sh cria o secret "aws-credentials" em kube-system com as credenciais do Lab
  # antes de rodar esta fase. O optional=true garante que o pod sobe mesmo sem o secret
  # (fallback para instance profile se o hop limit for 2).
  values = [
    yamlencode({
      extraEnvs = [
        {
          name = "AWS_ACCESS_KEY_ID"
          valueFrom = {
            secretKeyRef = {
              name     = "aws-credentials"
              key      = "AWS_ACCESS_KEY_ID"
              optional = true
            }
          }
        },
        {
          name = "AWS_SECRET_ACCESS_KEY"
          valueFrom = {
            secretKeyRef = {
              name     = "aws-credentials"
              key      = "AWS_SECRET_ACCESS_KEY"
              optional = true
            }
          }
        },
        {
          name = "AWS_SESSION_TOKEN"
          valueFrom = {
            secretKeyRef = {
              name     = "aws-credentials"
              key      = "AWS_SESSION_TOKEN"
              optional = true
            }
          }
        }
      ]
    })
  ]
}
