# Azure PostgreSQL Flexible Server for Pet Clinic
# Issue #004: Create Azure PostgreSQL Database
#
# Resources:
# - PostgreSQL Flexible Server (Burstable B1ms)
# - PostgreSQL Database (petclinic)
# - Scoped Firewall Rules (specific IPs only - no blanket Azure services)

# -----------------------------------------------------------------------------
# PostgreSQL Flexible Server
# -----------------------------------------------------------------------------
resource "azurerm_postgresql_flexible_server" "main" {
  name                = "petclinic-db-${var.environment}"
  resource_group_name = azurerm_resource_group.main.name
  location            = azurerm_resource_group.main.location

  # PostgreSQL version
  version = var.db_version

  # Administrator credentials
  administrator_login    = var.db_admin_login
  administrator_password = var.db_admin_password

  # Compute and storage
  sku_name   = var.db_sku_name
  storage_mb = var.db_storage_mb

  # Availability zone
  zone = "3"

  # Backup configuration
  backup_retention_days = var.db_backup_retention_days

  # Burstable tier does not support geo-redundant backup
  geo_redundant_backup_enabled = false

  tags = local.common_tags
}

# -----------------------------------------------------------------------------
# PostgreSQL Database
# -----------------------------------------------------------------------------
resource "azurerm_postgresql_flexible_server_database" "main" {
  name      = "petclinic"
  server_id = azurerm_postgresql_flexible_server.main.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}

# -----------------------------------------------------------------------------
# Firewall Rules - Scoped to specific IP addresses only
# IMPORTANT: Do NOT add 0.0.0.0 to 0.0.0.0 rule (blanket Azure services)
# -----------------------------------------------------------------------------
resource "azurerm_postgresql_flexible_server_firewall_rule" "allowed_ips" {
  for_each = var.allowed_ip_addresses

  name             = each.key
  server_id        = azurerm_postgresql_flexible_server.main.id
  start_ip_address = each.value.start_ip
  end_ip_address   = each.value.end_ip
}
