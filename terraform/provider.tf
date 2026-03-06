# AWS Provider Configuration

terraform {
  required_version = ">= 1.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region  = var.aws_region
  profile = "liatrio-forge"

  default_tags {
    tags = {
      Project     = "Emerald Grove Pet Clinic"
      ManagedBy   = "Terraform"
      Environment = var.environment
    }
  }
}
