output "resource_group_name" {
  description = "Name of the resource group"
  value       = azurerm_resource_group.main.name
}

output "resource_group_id" {
  description = "ID of the resource group"
  value       = azurerm_resource_group.main.id
}

output "location" {
  description = "Azure region where resources are deployed"
  value       = azurerm_resource_group.main.location
}

output "subscription_id" {
  description = "Current Azure subscription ID"
  value       = data.azurerm_subscription.current.subscription_id
  sensitive   = false
}

# -----------------------------------------------------------------------------
# Database Outputs (Issue #004)
# NOTE: Password is NOT output. Retrieve from Azure Key Vault or secret reference.
# -----------------------------------------------------------------------------

output "db_hostname" {
  description = "FQDN of the PostgreSQL Flexible Server"
  value       = azurerm_postgresql_flexible_server.main.fqdn
}

output "db_name" {
  description = "Name of the PostgreSQL database"
  value       = azurerm_postgresql_flexible_server_database.main.name
}

output "db_port" {
  description = "Port number for the PostgreSQL server"
  value       = 5432
}

output "db_admin_login" {
  description = "Administrator login name for the PostgreSQL server"
  value       = azurerm_postgresql_flexible_server.main.administrator_login
}

output "db_server_id" {
  description = "ID of the PostgreSQL Flexible Server"
  value       = azurerm_postgresql_flexible_server.main.id
}
