# Azure Container App for Pet Clinic
# Issue #005: Create Azure Container App
#
# Resources:
# - Log Analytics Workspace (required by Container App Environment)
# - Container App Environment
# - Container App (Spring Boot Pet Clinic)
#
# Dependencies:
# - Issue #002: Docker image (ghcr.io/angelgarcia999/petclinic:latest)
# - Issue #004: PostgreSQL Flexible Server (database.tf)

# -----------------------------------------------------------------------------
# Log Analytics Workspace (required by Container App Environment)
# -----------------------------------------------------------------------------
resource "azurerm_log_analytics_workspace" "main" {
  name                = "${local.resource_suffix}-law"
  location            = azurerm_resource_group.main.location
  resource_group_name = azurerm_resource_group.main.name
  sku                 = "PerGB2018"
  retention_in_days   = var.log_analytics_retention_days

  tags = local.common_tags
}

# -----------------------------------------------------------------------------
# Container App Environment
# -----------------------------------------------------------------------------
resource "azurerm_container_app_environment" "main" {
  name                       = "${local.resource_suffix}-env"
  location                   = azurerm_resource_group.main.location
  resource_group_name        = azurerm_resource_group.main.name
  log_analytics_workspace_id = azurerm_log_analytics_workspace.main.id

  tags = local.common_tags
}

# -----------------------------------------------------------------------------
# Container App - Spring Boot Pet Clinic
# -----------------------------------------------------------------------------
resource "azurerm_container_app" "petclinic" {
  name                         = "${local.resource_suffix}-app"
  container_app_environment_id = azurerm_container_app_environment.main.id
  resource_group_name          = azurerm_resource_group.main.name
  revision_mode                = "Single"

  tags = local.common_tags

  # --- Secrets ---
  secret {
    name  = "db-password"
    value = var.db_admin_password
  }

  # --- Template ---
  template {
    min_replicas = var.container_min_replicas
    max_replicas = var.container_max_replicas

    container {
      name   = "petclinic"
      image  = var.container_image
      cpu    = var.container_cpu
      memory = var.container_memory

      # Environment variables for Spring Boot database connection
      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "postgres"
      }

      env {
        name  = "POSTGRES_URL"
        value = "jdbc:postgresql://${azurerm_postgresql_flexible_server.main.fqdn}:5432/${azurerm_postgresql_flexible_server_database.main.name}?sslmode=require"
      }

      env {
        name  = "POSTGRES_USER"
        value = var.db_admin_login
      }

      env {
        name        = "POSTGRES_PASS"
        secret_name = "db-password"
      }

      # Liveness probe - checks if the app is alive
      liveness_probe {
        transport               = "HTTP"
        port                    = 8080
        path                    = "/actuator/health/liveness"
        initial_delay           = 30
        interval_seconds        = 10
        timeout                 = 5
        failure_count_threshold = 3
      }

      # Readiness probe - checks if the app is ready to serve traffic
      readiness_probe {
        transport               = "HTTP"
        port                    = 8080
        path                    = "/actuator/health/readiness"
        interval_seconds        = 10
        timeout                 = 5
        failure_count_threshold = 3
        success_count_threshold = 1
      }

      # Startup probe - checks if the app has started (Spring Boot can take time)
      startup_probe {
        transport               = "HTTP"
        port                    = 8080
        path                    = "/actuator/health/liveness"
        interval_seconds        = 10
        timeout                 = 5
        failure_count_threshold = 10
      }
    }
  }

  # --- Ingress ---
  ingress {
    target_port                = 8080
    external_enabled           = true
    allow_insecure_connections = false
    transport                  = "auto"

    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }
}

# -----------------------------------------------------------------------------
# Firewall Rule - Allow Container App outbound IPs to access PostgreSQL
# NOTE: Temporarily disabled due to Terraform for_each limitation with unknown values
# This will be added via Azure CLI after container app deployment
# -----------------------------------------------------------------------------
# resource "azurerm_postgresql_flexible_server_firewall_rule" "container_app" {
#   for_each = toset(azurerm_container_app.petclinic.outbound_ip_addresses)
#
#   name             = "container-app-${replace(each.value, ".", "-")}"
#   server_id        = azurerm_postgresql_flexible_server.main.id
#   start_ip_address = each.value
#   end_ip_address   = each.value
# }
