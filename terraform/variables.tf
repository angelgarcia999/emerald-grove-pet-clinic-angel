variable "location" {
  description = "Azure region for resources"
  type        = string
  default     = "East US"
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

variable "tags" {
  description = "Common tags for all resources"
  type        = map(string)
  default = {
    Project     = "Emerald Grove Pet Clinic"
    ManagedBy   = "Terraform"
    Environment = "dev"
  }
}

# -----------------------------------------------------------------------------
# Database Variables (Issue #004)
# -----------------------------------------------------------------------------

variable "db_admin_login" {
  description = "Administrator login for PostgreSQL Flexible Server"
  type        = string
  default     = "petclinic_admin"
}

variable "db_admin_password" {
  description = "Administrator password for PostgreSQL Flexible Server. Must meet Azure complexity requirements."
  type        = string
  sensitive   = true
}

variable "db_sku_name" {
  description = "SKU name for PostgreSQL Flexible Server (e.g., B_Standard_B1ms for Burstable)"
  type        = string
  default     = "B_Standard_B1ms"
}

variable "db_storage_mb" {
  description = "Maximum storage allowed for PostgreSQL Flexible Server in megabytes"
  type        = number
  default     = 32768
}

variable "db_version" {
  description = "PostgreSQL version for the Flexible Server"
  type        = string
  default     = "14"
}

variable "db_backup_retention_days" {
  description = "Backup retention days for PostgreSQL Flexible Server (1-35). Use 1 for temporary/dev environments to minimize cost."
  type        = number
  default     = 1

  validation {
    condition     = var.db_backup_retention_days >= 1 && var.db_backup_retention_days <= 35
    error_message = "Backup retention days must be between 1 and 35."
  }
}

variable "allowed_ip_addresses" {
  description = "Map of allowed IP address ranges for PostgreSQL firewall rules. Do NOT add blanket Azure services rule (0.0.0.0 to 0.0.0.0)."
  type = map(object({
    start_ip = string
    end_ip   = string
  }))
  default = {}

  validation {
    condition = alltrue([
      for k, v in var.allowed_ip_addresses :
      !(v.start_ip == "0.0.0.0" && v.end_ip == "0.0.0.0")
    ])
    error_message = "The 0.0.0.0 to 0.0.0.0 range (blanket Azure services rule) is not allowed. Use specific IP addresses only."
  }
}
