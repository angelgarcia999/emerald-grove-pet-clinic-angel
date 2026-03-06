# Terraform Variables for AWS App Runner + RDS

# -----------------------------------------------------------------------------
# General Variables
# -----------------------------------------------------------------------------

variable "aws_region" {
  description = "AWS region for resources"
  type        = string
  default     = "us-west-2"
}

variable "environment" {
  description = "Environment name (dev, staging, prod)"
  type        = string
  default     = "dev"
}

variable "project_name" {
  description = "Project name used for resource naming"
  type        = string
  default     = "petclinic"
}

# -----------------------------------------------------------------------------
# RDS PostgreSQL Variables
# -----------------------------------------------------------------------------

variable "db_instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t3.micro"
}

variable "db_allocated_storage" {
  description = "Allocated storage in GB"
  type        = number
  default     = 20
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "petclinic"
}

variable "db_username" {
  description = "Database username"
  type        = string
  default     = "petclinic_admin"
}

variable "db_password" {
  description = "Database password"
  type        = string
  sensitive   = true
}

variable "db_backup_retention_days" {
  description = "Backup retention days (7-35)"
  type        = number
  default     = 7
}

variable "db_engine_version" {
  description = "PostgreSQL version"
  type        = string
  default     = "14.15"
}

# -----------------------------------------------------------------------------
# App Runner Variables
# -----------------------------------------------------------------------------

variable "container_image" {
  description = "Container image to deploy"
  type        = string
  default     = "ghcr.io/angelgarcia999/emerald-grove-pet-clinic-angel:latest"
}

variable "app_runner_cpu" {
  description = "CPU for App Runner (1024 = 1 vCPU, 2048 = 2 vCPU)"
  type        = string
  default     = "1024"
}

variable "app_runner_memory" {
  description = "Memory for App Runner"
  type        = string
  default     = "2048"
}

variable "app_runner_min_size" {
  description = "Minimum instances"
  type        = number
  default     = 1
}

variable "app_runner_max_size" {
  description = "Maximum instances"
  type        = number
  default     = 3
}

variable "auto_deploy_enabled" {
  description = "Enable automatic deployments when image updates"
  type        = bool
  default     = false
}
