# Terraform Infrastructure

Infrastructure as Code (IaC) for Emerald Grove Pet Clinic Azure deployment.

## Prerequisites

- Terraform >= 1.6.0
- Azure CLI >= 2.50.0
- Azure account with active subscription

## Quick Start

```bash
# 1. Authenticate with Azure
az login

# 2. Initialize Terraform
terraform init

# 3. Preview changes
terraform plan

# 4. Apply changes
terraform apply

# 5. Destroy resources (cleanup)
terraform destroy
```

## Project Structure

```
terraform/
├── .gitignore          # Ignore sensitive files
├── terraform.tf        # Terraform and provider versions
├── providers.tf        # Azure provider configuration
├── backend.tf         # State backend configuration
├── main.tf            # Primary resources (resource group, locals)
├── database.tf        # PostgreSQL Flexible Server and database
├── variables.tf       # Input variables
├── outputs.tf         # Output values
└── README.md          # This file
```

## Configuration

### Variables

Default values are defined in `variables.tf`. Override them by:

**Option 1: Environment variables**
```bash
export TF_VAR_location="West US"
export TF_VAR_environment="prod"
```

**Option 2: Variable file**
```bash
# Create terraform.tfvars
location    = "West US"
environment = "prod"

# Apply with var file
terraform apply -var-file="terraform.tfvars"
```

**Option 3: Command line**
```bash
terraform apply -var="location=West US" -var="environment=prod"
```

### Backend

Currently using **local backend** for development:
- State stored in `terraform.tfstate` (git-ignored)
- Simple setup for solo development

**Migration to Azure Storage backend** (recommended before production):
1. Create Azure Storage account for state
2. Update `backend.tf` with Azure Storage configuration
3. Run `terraform init -migrate-state`

## Resources

### Current Resources

- **Resource Group** (`azurerm_resource_group.main`)
  - Name: `{project_name}-{environment}-rg`
  - Location: Configurable via `location` variable
  - Tags: Common tags for cost tracking

- **PostgreSQL Flexible Server** (`azurerm_postgresql_flexible_server.main`) - Issue #004
  - Name: `petclinic-db-{environment}`
  - SKU: Burstable B1ms (`B_Standard_B1ms`)
  - PostgreSQL version: 14
  - Storage: 32GB
  - Backup retention: 7 days

- **PostgreSQL Database** (`azurerm_postgresql_flexible_server_database.main`) - Issue #004
  - Name: `petclinic`
  - Charset: UTF8
  - Collation: en_US.utf8
  - Lifecycle: `prevent_destroy = true`

- **PostgreSQL Firewall Rules** (`azurerm_postgresql_flexible_server_firewall_rule.allowed_ips`) - Issue #004
  - Dynamic rules from `allowed_ip_addresses` variable
  - Scoped to specific IPs only (no blanket Azure services rule)

### Planned Resources (Future Issues)

- **Azure Container Apps** (Issue #005)
- **Networking** (VNet, subnets)
- **Monitoring** (Application Insights)

## Workflow

```bash
# Format code
terraform fmt

# Validate configuration
terraform validate

# Plan deployment
terraform plan -out=tfplan

# Apply plan
terraform apply tfplan

# Show current state
terraform show

# List resources
terraform state list

# Destroy all resources
terraform destroy
```

## Database Configuration

### Required Variables

The database requires an administrator password to be provided. This is a sensitive variable with no default:

```bash
# Option 1: Environment variable (recommended)
export TF_VAR_db_admin_password="YourSecurePassword123!"

# Option 2: Command line
terraform apply -var="db_admin_password=YourSecurePassword123!"
```

### Firewall Rules

By default, no firewall rules are created (the database is not accessible from any IP). Add specific IPs:

```bash
# In terraform.tfvars or via environment variable
allowed_ip_addresses = {
  "my-office" = {
    start_ip = "203.0.113.10"
    end_ip   = "203.0.113.10"
  }
  "vpn-range" = {
    start_ip = "198.51.100.0"
    end_ip   = "198.51.100.255"
  }
}
```

**IMPORTANT**: Do NOT add a rule with `0.0.0.0` to `0.0.0.0` (this is the blanket "Allow Azure services" rule and is explicitly prohibited).

### Testing Database Connection

```bash
# Get connection info
terraform output db_hostname
terraform output db_admin_login

# Connect with psql
export DB_HOST=$(terraform output -raw db_hostname)
psql -h $DB_HOST -U petclinic_admin -d petclinic
```

### Database Outputs

| Output | Description |
|--------|-------------|
| `db_hostname` | FQDN of the PostgreSQL server |
| `db_name` | Database name (`petclinic`) |
| `db_port` | Server port (`5432`) |
| `db_admin_login` | Administrator username |
| `db_server_id` | Azure resource ID of the server |

**Note**: Password is intentionally NOT included in outputs. Retrieve it from Azure Key Vault or your secret management solution.

## Security

- **Never commit** `.tfstate` files or `.tfvars` files with secrets
- Use environment variables for sensitive values
- Database password is marked as `sensitive` in Terraform
- Firewall rules are scoped to specific IPs (no blanket Azure services rule)
- Enable Azure Storage backend with encryption for production
- Scope service principals to minimum required permissions

## Troubleshooting

### "Error: Subscription Not Found"

```bash
az login
az account list --output table
az account set --subscription "YOUR_SUBSCRIPTION_NAME"
```

### "Error: Backend Initialization Required"

```bash
terraform init
```

### "Error: Provider Not Found"

```bash
terraform init -upgrade
```

## Documentation

- [Terraform Azure Provider Docs](https://registry.terraform.io/providers/hashicorp/azurerm/latest/docs)
- [Azure CLI Reference](https://docs.microsoft.com/en-us/cli/azure/)
- [Module 4 Research - Issue #003](../.module4-tasks/issue-003-research.md)
- [Module 4 Research - Issue #004](../.module4-tasks/issue-004-research.md)

---

**Managed By:** Terraform
**Project:** Emerald Grove Pet Clinic
**Module:** 4 - DevOps & Platform Engineering
