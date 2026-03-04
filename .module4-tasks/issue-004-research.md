# Issue #004 Research Report: Azure PostgreSQL Database

## Issue Overview

Create Terraform configuration for Azure PostgreSQL Flexible Server to serve as the Pet Clinic application's database. This includes the server itself, the `petclinic` database, and scoped firewall rules for network security.

## Requirements Analysis

### Acceptance Criteria (from issue)
- Terraform file: `terraform/database.tf`
- Creates: `azurerm_postgresql_flexible_server`
- Creates: `azurerm_postgresql_flexible_server_database`
- Creates: VNet integration OR specific IP firewall rules (no blanket Azure services allow)
- Database name: `petclinic`
- PostgreSQL version: 14 or higher
- Tier: Burstable B1ms (`B_Standard_B1ms`)
- Storage: 32GB (32768 MB)
- Backup retention: 1 day (minimal for temporary deployment)
- Administrator login: `petclinic_admin`
- Administrator password: via variable (marked sensitive)
- Outputs: hostname, database name, port, username (password NOT output)

### Dependencies
- **Issue #003 (Terraform setup)**: COMPLETE - provides resource group, variables, provider config

## Codebase Analysis

### Existing Terraform Structure
```
terraform/
  terraform.tf    - azurerm ~> 3.0, terraform >= 1.6.0
  providers.tf    - azurerm provider with features {}
  main.tf         - resource group, locals (resource_suffix, common_tags)
  variables.tf    - location, environment, project_name, tags
  outputs.tf      - resource_group_name, resource_group_id, location, subscription_id
  backend.tf      - local backend
  .gitignore      - ignores .tfstate, .tfvars, .terraform/
```

### Key Existing Resources to Reference
- `azurerm_resource_group.main` - resource group for all resources
- `local.resource_suffix` - `"${var.project_name}-${var.environment}"` (e.g., "petclinic-dev")
- `local.common_tags` - merged tags with CreatedBy and Environment
- `var.location` - "East US" (default)
- `var.environment` - "dev" (default)
- `var.project_name` - "petclinic" (default)

### Naming Convention
Resources use pattern: `${var.project_name}-${var.environment}-<suffix>`
For database server: `petclinic-db-${var.environment}` per issue spec

## Architecture Considerations

### Network Security Decision: Scoped Firewall Rules (for dev)
- The issue offers two options: VNet integration or scoped firewall rules
- For dev/test environment (current phase), **scoped firewall rules** are simpler and appropriate
- VNet integration adds complexity that is unnecessary at this stage
- The firewall rules approach allows specific IPs only -- NO blanket "Allow Azure services" rule
- A configurable `allowed_ip_addresses` variable enables adding specific IPs

### Resource Architecture
```
azurerm_resource_group.main (existing)
  |
  +-- azurerm_postgresql_flexible_server.main (new)
  |     - Name: petclinic-db-${var.environment}
  |     - SKU: B_Standard_B1ms
  |     - Version: 14
  |     - Storage: 32768 MB
  |     - Backup: 1 day
  |
  +-- azurerm_postgresql_flexible_server_database.main (new)
  |     - Name: petclinic
  |     - Charset: UTF8
  |     - Collation: en_US.utf8
  |
  +-- azurerm_postgresql_flexible_server_firewall_rule.allowed_ips (new)
        - Dynamic block for each allowed IP
        - NO 0.0.0.0 to 0.0.0.0 rule (blanket Azure services)
```

## Best Practices from Research

### azurerm Provider ~> 3.0 Specifics
1. **`storage_mb`** (not `storage_gb`): Use `32768` for 32GB
2. **`sku_name`**: Format is `{tier}_{name}` - use `B_Standard_B1ms` for Burstable
3. **`version`**: String value - use `"14"`
4. **`administrator_login`**: Required, use `petclinic_admin`
5. **`administrator_password`**: Required, from sensitive variable
6. **`backup_retention_days`**: Integer, use `7`
7. **`server_id`**: Used to link database and firewall rules to the server

### Security Best Practices
1. Password variable MUST be `sensitive = true`
2. Password output MUST NOT exist (do not output connection string or password)
3. Firewall rules must be scoped (specific IPs only)
4. Do NOT create rule with `0.0.0.0` to `0.0.0.0` (blanket Azure services)
5. Consider `lifecycle { prevent_destroy = true }` for database resource

### Terraform Patterns
1. Use `for_each` for dynamic firewall rules from a variable map
2. Group database resources in `database.tf` (separate from main.tf)
3. Add database variables to existing `variables.tf`
4. Add database outputs to existing `outputs.tf`

## Implementation Plan

### File Changes

#### 1. NEW: `terraform/database.tf`
Contains:
- `azurerm_postgresql_flexible_server.main`
- `azurerm_postgresql_flexible_server_database.main`
- `azurerm_postgresql_flexible_server_firewall_rule.allowed_ips`

#### 2. MODIFY: `terraform/variables.tf`
Add:
- `db_admin_login` (string, default: "petclinic_admin")
- `db_admin_password` (string, sensitive: true, no default)
- `db_sku_name` (string, default: "B_Standard_B1ms")
- `db_storage_mb` (number, default: 32768)
- `db_version` (string, default: "14")
- `db_backup_retention_days` (number, default: 7)
- `allowed_ip_addresses` (map of objects with start_ip and end_ip)

#### 3. MODIFY: `terraform/outputs.tf`
Add:
- `db_hostname` - FQDN of the server
- `db_name` - database name ("petclinic")
- `db_port` - port (5432)
- `db_admin_login` - admin username (for reference)

#### 4. MODIFY: `terraform/README.md`
Add database resources documentation

### Validation Strategy (IaC TDD Equivalent)
1. **RED**: Run `terraform validate` before writing database.tf - should show no database resources
2. **GREEN**: Write database.tf, run `terraform validate` - should pass
3. **REFACTOR**: Run `terraform fmt`, optimize variable defaults, verify outputs
4. Additional: `terraform plan` to verify resource creation plan

## Edge Cases and Considerations

1. **Server name uniqueness**: Azure requires globally unique server names for PostgreSQL
2. **Password complexity**: Azure requires passwords with mixed case, numbers, special chars
3. **Zone availability**: B1ms may not be available in all zones; do not hardcode `zone`
4. **geo_redundant_backup**: Not supported for Burstable tier; do not enable
5. **Auto-grow storage**: Consider `auto_grow_enabled` parameter
6. **SSL enforcement**: Should be enabled by default for security

## References

- [azurerm_postgresql_flexible_server docs](https://registry.terraform.io/providers/hashicorp/azurerm/latest/docs/resources/postgresql_flexible_server)
- [azurerm_postgresql_flexible_server_database docs](https://registry.terraform.io/providers/hashicorp/azurerm/latest/docs/resources/postgresql_flexible_server_database)
- [azurerm_postgresql_flexible_server_firewall_rule docs](https://registry.terraform.io/providers/hashicorp/azurerm/latest/docs/resources/postgresql_flexible_server_firewall_rule)
- Context7: hashicorp/terraform-provider-azurerm examples
- Context7: azure/terraform-azurerm-avm-res-dbforpostgresql-flexibleserver examples

## Next Steps

1. Create `terraform/database.tf` with all three resources
2. Add database variables to `terraform/variables.tf`
3. Add database outputs to `terraform/outputs.tf`
4. Update `terraform/README.md` with new resources
5. Run `terraform fmt` and `terraform validate`
6. Run `terraform plan` to verify resource creation
