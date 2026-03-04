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
├── main.tf            # Primary resources
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

### Planned Resources (Future Issues)

- **Azure Database for PostgreSQL** (Issue #004)
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

## Security

- **Never commit** `.tfstate` files or `.tfvars` files with secrets
- Use environment variables for sensitive values
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
- [Module 4 Research](../.module4-tasks/issue-003-research.md)

---

**Managed By:** Terraform
**Project:** Emerald Grove Pet Clinic
**Module:** 4 - DevOps & Platform Engineering
