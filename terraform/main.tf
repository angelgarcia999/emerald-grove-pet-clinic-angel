# Placeholder resource group
# Will be populated in subsequent issues with:
# - Azure Database for PostgreSQL (Issue #004)
# - Azure Container Apps (Issue #005)

resource "azurerm_resource_group" "main" {
  name     = "${var.project_name}-${var.environment}-rg"
  location = var.location
  tags     = local.common_tags
}

# Data source: Current Azure subscription
data "azurerm_subscription" "current" {}

# Local values for computed resource names
locals {
  resource_suffix = "${var.project_name}-${var.environment}"

  common_tags = merge(
    var.tags,
    {
      CreatedBy   = "Terraform"
      Environment = var.environment
    }
  )
}
