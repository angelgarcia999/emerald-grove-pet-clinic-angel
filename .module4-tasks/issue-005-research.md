# Issue #005 Research: Azure Container App

## Requirements Analysis

### Acceptance Criteria Mapping

| Criteria | Resource/Config | Status |
|----------|----------------|--------|
| Terraform file: `terraform/container-app.tf` | New file created | Done |
| Creates: `azurerm_container_app_environment` | `azurerm_container_app_environment.main` | Done |
| Creates: `azurerm_container_app` | `azurerm_container_app.petclinic` | Done |
| Pulls image: `ghcr.io/angelgarcia999/petclinic:latest` | `var.container_image` (default) | Done |
| Connects to PostgreSQL database | JDBC URL via `azurerm_postgresql_flexible_server.main.fqdn` | Done |
| Exposes port 8080 with ingress | `ingress.target_port = 8080`, `external_enabled = true` | Done |
| Environment variables configured | 4 env vars (profile, url, user, password) | Done |
| Health probes configured | Liveness, readiness, and startup probes | Done |
| App accessible via HTTPS URL | `output "app_url"` exports FQDN | Done |

### Resource Specifications

- **CPU**: 0.5 cores (`var.container_cpu`)
- **Memory**: 1Gi (`var.container_memory`)
- **Replicas**: 1-3 (`var.container_min_replicas`, `var.container_max_replicas`)
- **Revision mode**: Single

## Architectural Decisions

### 1. Log Analytics Workspace

Azure Container App Environment requires a Log Analytics Workspace for logging.
Created `azurerm_log_analytics_workspace.main` with PerGB2018 SKU and 30-day retention
(configurable via `var.log_analytics_retention_days`).

### 2. Database Connectivity

The PostgreSQL Flexible Server uses firewall rules (not VNet integration). Container Apps
have outbound IP addresses that must be allowed through the firewall. A dynamic
`azurerm_postgresql_flexible_server_firewall_rule.container_app` resource uses `for_each`
over `azurerm_container_app.petclinic.outbound_ip_addresses` to automatically create
firewall rules for each outbound IP.

### 3. Secret Management

The database password is stored as a Container App secret (`db-password`) and referenced
by the `POSTGRES_PASSWORD` environment variable via `secret_name`. The password value
comes from `var.db_admin_password` (marked `sensitive = true`). This avoids exposing the
password in plain text in the container configuration.

### 4. Health Probes

Three probes configured for robust health checking:

- **Startup probe**: `/actuator/health/liveness` with 30 failure threshold (5 min window
  for Spring Boot startup)
- **Liveness probe**: `/actuator/health/liveness` with 30s initial delay, 10s interval
- **Readiness probe**: `/actuator/health/readiness` with 10s interval

### 5. SSL/TLS

Container Apps automatically provides HTTPS via `*.azurecontainerapps.io` domain.
`allow_insecure_connections = false` enforces HTTPS-only access.

### 6. GHCR Public Registry

The image `ghcr.io/angelgarcia999/petclinic:latest` is a public GHCR image, so no
registry authentication block is needed in the Container App configuration.

## Variables Added

| Variable | Type | Default | Description |
|----------|------|---------|-------------|
| `container_image` | string | `ghcr.io/angelgarcia999/petclinic:latest` | Container image |
| `container_cpu` | number | `0.5` | CPU cores |
| `container_memory` | string | `1Gi` | Memory allocation |
| `container_min_replicas` | number | `1` | Minimum replicas |
| `container_max_replicas` | number | `3` | Maximum replicas |
| `log_analytics_retention_days` | number | `30` | Log retention days |

## Outputs Added

| Output | Description |
|--------|-------------|
| `app_url` | FQDN of the Container App |
| `container_app_name` | Name of the Container App |
| `container_app_environment_name` | Name of the Container App Environment |
| `container_app_outbound_ips` | Outbound IP addresses |
| `log_analytics_workspace_id` | Log Analytics Workspace ID |

## TDD Validation

- **RED**: Baseline `terraform validate` passed without container app resources
- **GREEN**: `terraform validate` passed with all new resources
- **REFACTOR**: `terraform fmt` applied, `terraform fmt -check` and `terraform validate` both pass

## Testing Plan

```bash
cd terraform
terraform apply

# Get app URL
export APP_URL=$(terraform output -raw app_url)

# Test health endpoints
curl https://$APP_URL/actuator/health
curl https://$APP_URL/actuator/health/liveness
curl https://$APP_URL/actuator/health/readiness

# Test in browser
open https://$APP_URL
```
