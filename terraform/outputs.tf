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

# -----------------------------------------------------------------------------
# Container App Outputs (Issue #005)
# -----------------------------------------------------------------------------

output "app_url" {
  description = "FQDN of the Container App (use with https://)"
  value       = azurerm_container_app.petclinic.ingress[0].fqdn
}

output "container_app_name" {
  description = "Name of the Container App"
  value       = azurerm_container_app.petclinic.name
}

output "container_app_environment_name" {
  description = "Name of the Container App Environment"
  value       = azurerm_container_app_environment.main.name
}

output "container_app_outbound_ips" {
  description = "Outbound IP addresses of the Container App"
  value       = azurerm_container_app.petclinic.outbound_ip_addresses
}

output "log_analytics_workspace_id" {
  description = "ID of the Log Analytics Workspace"
  value       = azurerm_log_analytics_workspace.main.id
}
