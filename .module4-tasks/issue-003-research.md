# Issue #003 Research: Setup Terraform + Azure

**Research Date:** 2026-02-27
**Researched By:** Claude Sonnet 4.5 with Context7
**Focus:** Terraform project structure, Azure CLI authentication, state management, best practices

---

## Executive Summary

This research covers setting up Terraform with Azure for infrastructure as code (IaC) deployment:
- **Terraform project structure** and file organization conventions
- **Azure CLI authentication** methods (interactive, service principal, managed identity)
- **azurerm provider** configuration for Azure resource management
- **State management** strategies (local vs remote Azure Storage backend)
- **Security best practices** for credentials and state files

**Key Finding:** Start with local backend for simplicity during development, then migrate to Azure Storage backend with state locking before production deployment. Use Azure CLI interactive login for local development and service principals for CI/CD automation.

**Project Context:** This implementation establishes the infrastructure foundation for the Emerald Grove Pet Clinic project, which uses Claude AI-powered automated code reviews for PR validation and quality checks, ensuring infrastructure changes meet best practices before deployment.

---

## Research Sources

1. **Terraform (HashiCorp)** (High Authority, 120K+ code snippets)
   - Official Terraform documentation
   - Project structure and style conventions
   - Backend configuration patterns

2. **Azure CLI Documentation** (High Authority, 3K+ code snippets)
   - Authentication methods and workflows
   - Service principal management
   - Subscription and tenant configuration

3. **Terraform Azure Provider** (High Authority)
   - azurerm provider configuration
   - Azure backend setup
   - State management with Azure Storage

---

## Terraform Fundamentals

### What is Terraform?

**Terraform** is an infrastructure as code (IaC) tool that allows you to:
- Define infrastructure in human-readable configuration files
- Version control your infrastructure
- Safely and predictably create, change, and improve infrastructure
- Use the same workflow across multiple cloud providers

### Core Concepts

**1. Configuration Files (`.tf`)**
- Written in HashiCorp Configuration Language (HCL)
- Declare what resources you want to exist
- Terraform figures out how to make it happen

**2. Providers**
- Plugins that enable Terraform to interact with cloud platforms
- For Azure: `azurerm` provider
- Each provider has its own resources and data sources

**3. State**
- Terraform keeps track of what it has created in a **state file**
- Maps real-world resources to your configuration
- Stores metadata and performance optimization data

**4. Backend**
- Where Terraform stores the state file
- **Local backend:** State stored on your machine (`terraform.tfstate`)
- **Remote backend:** State stored in cloud (e.g., Azure Storage, S3)

---

## Terraform Project Structure

### Recommended File Organization

Based on HashiCorp conventions, organize your Terraform project as follows:

```
terraform/
├── .gitignore           # Ignore sensitive files
├── terraform.tf         # Terraform and provider versions
├── providers.tf         # Provider configuration
├── backend.tf          # Backend configuration
├── main.tf             # Primary resources
├── variables.tf        # Input variables
├── outputs.tf          # Output values
├── locals.tf           # Local values (optional)
└── README.md           # Documentation
```

### File Purposes

| File | Purpose | Contents |
|------|---------|----------|
| `terraform.tf` | Version requirements | `required_version`, `required_providers` |
| `providers.tf` | Provider config | `provider "azurerm"` block |
| `backend.tf` | State backend | `backend "azurerm"` or `backend "local"` |
| `main.tf` | Resources | All `resource` and `data` blocks |
| `variables.tf` | Inputs | All `variable` blocks (alphabetical) |
| `outputs.tf` | Outputs | All `output` blocks (alphabetical) |
| `locals.tf` | Computed values | `locals` block with derived values |

**Why separate files?**
- **Clarity:** Easy to find specific configurations
- **Collaboration:** Reduces merge conflicts
- **Maintainability:** Clear separation of concerns

---

## Azure CLI Setup

### Installation

**macOS (Homebrew):**
```bash
brew install azure-cli

# Verify installation
az --version
```

**Expected Output:**
```
azure-cli                         2.50.0+
core                              2.50.0
telemetry                         1.0.8
```

**Other Platforms:**
- **Linux:** `curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash`
- **Windows:** Download MSI from https://aka.ms/installazurecliwindows

---

## Azure Authentication Methods

### Method 1: Interactive Login (Recommended for Local Development)

**Best for:** Local development, manual operations

```bash
# Opens browser for authentication
az login

# Output shows available subscriptions
[
  {
    "cloudName": "AzureCloud",
    "homeTenantId": "abcdef12-3456-7890-abcd-ef1234567890",
    "id": "12345678-1234-1234-1234-123456789abc",
    "isDefault": true,
    "name": "Azure for Students",
    "state": "Enabled",
    "tenantId": "abcdef12-3456-7890-abcd-ef1234567890",
    "user": {
      "name": "user@example.com",
      "type": "user"
    }
  }
]
```

**Features:**
- ✅ Supports multi-factor authentication (MFA)
- ✅ Works with personal and organizational accounts
- ✅ Most secure for human users
- ✅ Tokens auto-refresh

**After Login:**
```bash
# List all subscriptions
az account list --output table

# Set default subscription
az account set --subscription "Azure for Students"

# Verify current subscription
az account show --query "name"
```

---

### Method 2: Service Principal (Recommended for CI/CD)

**Best for:** GitHub Actions, automated deployments, CI/CD pipelines

#### Create Service Principal

```bash
# Create service principal with Contributor role
az ad sp create-for-rbac \
  --name "terraform-petclinic-sp" \
  --role Contributor \
  --scopes /subscriptions/{subscription-id}

# Output (SAVE THESE - shown only once!)
{
  "appId": "12345678-1234-1234-1234-123456789abc",
  "displayName": "terraform-petclinic-sp",
  "password": "super-secret-password-here",
  "tenant": "abcdef12-3456-7890-abcd-ef1234567890"
}
```

**⚠️ Security Warning:** The password is shown only once. Save it immediately in a secure location (e.g., password manager, GitHub Secrets).

#### Authenticate with Service Principal

```bash
# Method 1: Direct authentication
az login --service-principal \
  --username {appId} \
  --password {password} \
  --tenant {tenant}

# Method 2: Environment variables (better for scripts)
export ARM_CLIENT_ID="{appId}"
export ARM_CLIENT_SECRET="{password}"
export ARM_TENANT_ID="{tenant}"
export ARM_SUBSCRIPTION_ID="{subscription-id}"

az login --service-principal \
  --username $ARM_CLIENT_ID \
  --password $ARM_CLIENT_SECRET \
  --tenant $ARM_TENANT_ID
```

**Use Cases:**
- ✅ GitHub Actions workflows
- ✅ Automated Terraform deployments
- ✅ CI/CD pipelines
- ✅ Unattended operations

---

### Method 3: Managed Identity (Recommended for Azure VMs)

**Best for:** Running Terraform from Azure VMs, Container Instances, App Service

```bash
# Automatically authenticated when running on Azure resources
az login --identity

# No credentials required!
```

**Use Cases:**
- ✅ Terraform runs on Azure VM
- ✅ Terraform runs in Azure Container Instance
- ✅ Terraform runs in Azure App Service

---

## Terraform Configuration Files

### 1. terraform.tf - Version Requirements

```terraform
terraform {
  required_version = ">= 1.6.0"

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.0"
    }
  }
}
```

**Explanation:**
- `required_version`: Minimum Terraform CLI version
- `required_providers`: Specify provider sources and versions
- `~> 3.0`: Allow any 3.x version (patch and minor updates)

---

### 2. providers.tf - Azure Provider Configuration

#### Option A: CLI Authentication (Development)

```terraform
provider "azurerm" {
  features {}

  # Uses Azure CLI authentication automatically
  # No credentials needed if already logged in via `az login`
}
```

#### Option B: Service Principal (Production/CI)

```terraform
provider "azurerm" {
  features {}

  # Authentication via environment variables
  # Set these before running Terraform:
  # - ARM_CLIENT_ID
  # - ARM_CLIENT_SECRET
  # - ARM_TENANT_ID
  # - ARM_SUBSCRIPTION_ID

  # Or specify directly (NOT recommended - use env vars instead)
  # subscription_id = "00000000-0000-0000-0000-000000000000"
  # tenant_id       = "00000000-0000-0000-0000-000000000000"
  # client_id       = "00000000-0000-0000-0000-000000000000"
  # client_secret   = "secret" # NEVER commit this!
}
```

**Best Practice:** Always use environment variables for credentials, never hardcode them in `.tf` files.

---

### 3. backend.tf - State Management

#### Option A: Local Backend (Development)

```terraform
terraform {
  backend "local" {
    path = "terraform.tfstate"
  }
}
```

**Pros:**
- ✅ Simple setup
- ✅ No cloud dependencies
- ✅ Fast iterations

**Cons:**
- ❌ State stored on local machine only
- ❌ No collaboration support
- ❌ No state locking
- ❌ Risk of state loss

**When to use:** Solo development, experimentation, learning

---

#### Option B: Azure Storage Backend (Production)

```terraform
terraform {
  backend "azurerm" {
    resource_group_name  = "terraform-state-rg"
    storage_account_name = "tfstatepetclinic"
    container_name       = "tfstate"
    key                  = "petclinic.terraform.tfstate"
  }
}
```

**Setup Requirements:**
1. Create resource group: `az group create -n terraform-state-rg -l eastus`
2. Create storage account: `az storage account create -n tfstatepetclinic -g terraform-state-rg -l eastus --sku Standard_LRS`
3. Create container: `az storage container create -n tfstate --account-name tfstatepetclinic`

**Pros:**
- ✅ State stored in cloud (durable, backed up)
- ✅ State locking (prevents concurrent modifications)
- ✅ Team collaboration
- ✅ Version history
- ✅ Secure access control

**Cons:**
- ❌ Requires Azure Storage account
- ❌ Additional setup steps
- ❌ Costs (minimal - pennies per month)

**When to use:** Team projects, production deployments, CI/CD

---

### 4. variables.tf - Input Variables

```terraform
variable "location" {
  description = "Azure region for resources"
  type        = string
  default     = "East US"
}

variable "environment" {
  description = "Environment name (dev, staging, prod)"
  type        = string
  default     = "dev"

  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Environment must be dev, staging, or prod."
  }
}

variable "project_name" {
  description = "Project name used for resource naming"
  type        = string
  default     = "petclinic"

  validation {
    condition     = can(regex("^[a-z0-9-]+$", var.project_name))
    error_message = "Project name must be lowercase alphanumeric with hyphens only."
  }
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
```

**Variable Types:**
- `string`: Text values
- `number`: Numeric values
- `bool`: True/false
- `list(type)`: Ordered collection
- `map(type)`: Key-value pairs
- `object({...})`: Structured data

---

### 5. main.tf - Resources

```terraform
# Example: Azure Resource Group
resource "azurerm_resource_group" "main" {
  name     = "${var.project_name}-${var.environment}-rg"
  location = var.location
  tags     = var.tags
}

# Data source: Get current subscription
data "azurerm_subscription" "current" {}

# Local values for computed names
locals {
  resource_suffix = "${var.project_name}-${var.environment}"

  common_tags = merge(
    var.tags,
    {
      CreatedBy = "Terraform"
      UpdatedAt = timestamp()
    }
  )
}
```

---

### 6. outputs.tf - Output Values

```terraform
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
  sensitive   = false # Set to true for sensitive values
}
```

---

## .gitignore Configuration

**Critical:** Never commit sensitive files to Git!

```gitignore
# Terraform state files
*.tfstate
*.tfstate.*
*.tfstate.backup

# Terraform directory
.terraform/
.terraform.lock.hcl

# Terraform plan files
*.tfplan

# Terraform variable files (may contain secrets)
*.tfvars
*.tfvars.json
*.auto.tfvars
*.auto.tfvars.json

# Override files
override.tf
override.tf.json
*_override.tf
*_override.tf.json

# CLI configuration files
.terraformrc
terraform.rc

# Crash log files
crash.log
crash.*.log

# Environment variable files
.env
.env.local

# OS files
.DS_Store
Thumbs.db
```

---

## Terraform Workflow

### Basic Commands

```bash
# 1. Initialize Terraform (download providers, setup backend)
terraform init

# 2. Format code (standardize formatting)
terraform fmt

# 3. Validate syntax (check for errors)
terraform validate

# 4. Plan changes (preview what will happen)
terraform plan

# 5. Apply changes (create/update resources)
terraform apply

# 6. Show current state
terraform show

# 7. Destroy resources (cleanup)
terraform destroy
```

### Complete Workflow Example

```bash
# Step 1: Create project directory
mkdir terraform
cd terraform

# Step 2: Create configuration files
# (Create terraform.tf, providers.tf, backend.tf, main.tf, variables.tf, outputs.tf)

# Step 3: Initialize
terraform init

# Output:
# Initializing the backend...
# Initializing provider plugins...
# - Finding hashicorp/azurerm versions matching "~> 3.0"...
# - Installing hashicorp/azurerm v3.85.0...
# Terraform has been successfully initialized!

# Step 4: Validate configuration
terraform validate

# Output:
# Success! The configuration is valid.

# Step 5: Format code
terraform fmt

# Output:
# providers.tf
# main.tf

# Step 6: Plan deployment
terraform plan

# Output:
# Terraform will perform the following actions:
#   # azurerm_resource_group.main will be created
#   + resource "azurerm_resource_group" "main" {
#       + id       = (known after apply)
#       + location = "eastus"
#       + name     = "petclinic-dev-rg"
#       + tags     = {
#           + "Environment" = "dev"
#           + "ManagedBy"   = "Terraform"
#           + "Project"     = "Emerald Grove Pet Clinic"
#         }
#     }
# Plan: 1 to add, 0 to change, 0 to destroy.

# Step 7: Apply changes
terraform apply

# Prompt: Do you want to perform these actions? yes

# Output:
# azurerm_resource_group.main: Creating...
# azurerm_resource_group.main: Creation complete after 2s [id=/subscriptions/.../resourceGroups/petclinic-dev-rg]
# Apply complete! Resources: 1 added, 0 changed, 0 destroyed.
```

---

## State Management Best Practices

### Local State (Development)

**Use for:**
- Learning Terraform
- Solo experimentation
- Temporary testing

**Setup:**
```terraform
# backend.tf
terraform {
  backend "local" {
    path = "terraform.tfstate"
  }
}
```

**Workflow:**
```bash
terraform init
terraform plan
terraform apply

# State saved to: ./terraform.tfstate
```

---

### Remote State (Production)

**Use for:**
- Team collaboration
- Production deployments
- CI/CD automation

**Setup:**

**Step 1: Create Azure Storage resources**
```bash
# Variables
RG_NAME="terraform-state-rg"
STORAGE_NAME="tfstatepetclinic$(date +%s)" # Unique name
CONTAINER_NAME="tfstate"
LOCATION="eastus"

# Create resource group
az group create \
  --name $RG_NAME \
  --location $LOCATION

# Create storage account
az storage account create \
  --name $STORAGE_NAME \
  --resource-group $RG_NAME \
  --location $LOCATION \
  --sku Standard_LRS \
  --encryption-services blob

# Create container
az storage container create \
  --name $CONTAINER_NAME \
  --account-name $STORAGE_NAME

# Enable versioning (recommended)
az storage account blob-service-properties update \
  --account-name $STORAGE_NAME \
  --enable-versioning true
```

**Step 2: Configure Terraform backend**
```terraform
# backend.tf
terraform {
  backend "azurerm" {
    resource_group_name  = "terraform-state-rg"
    storage_account_name = "tfstatepetclinic1234567890"
    container_name       = "tfstate"
    key                  = "petclinic.terraform.tfstate"
  }
}
```

**Step 3: Initialize with backend**
```bash
terraform init

# Terraform will configure the backend and upload state
```

---

### Migrating from Local to Remote State

**Scenario:** You started with local backend, now want to move to Azure Storage

```bash
# Step 1: Create Azure Storage backend (see above)

# Step 2: Update backend.tf
# Change from "local" to "azurerm"

# Step 3: Re-initialize
terraform init -migrate-state

# Prompt: Do you want to copy existing state to the new backend? yes

# Output:
# Successfully configured the backend "azurerm"!
# Terraform has copied your state to the new backend.
```

**Verify migration:**
```bash
# Check that state is in Azure
az storage blob list \
  --account-name tfstatepetclinic1234567890 \
  --container-name tfstate \
  --output table

# Delete local state (after verification)
rm terraform.tfstate terraform.tfstate.backup
```

---

## Security Best Practices

### 1. Never Commit Secrets

**Bad:**
```terraform
provider "azurerm" {
  subscription_id = "12345678-1234-1234-1234-123456789abc"
  client_secret   = "my-secret-password" # ❌ NEVER DO THIS
}
```

**Good:**
```terraform
provider "azurerm" {
  # Uses environment variables:
  # - ARM_SUBSCRIPTION_ID
  # - ARM_CLIENT_ID
  # - ARM_CLIENT_SECRET
  # - ARM_TENANT_ID
}
```

### 2. Use .gitignore

```gitignore
*.tfstate
*.tfstate.*
*.tfvars
.terraform/
.env
```

### 3. Sensitive Outputs

```terraform
output "database_password" {
  value     = random_password.db.result
  sensitive = true # Prevents showing in logs
}
```

### 4. State File Security

**State files may contain:**
- Resource IDs
- IP addresses
- Connection strings
- Secrets from resource attributes

**Protect state files:**
- ✅ Use remote backend with access controls
- ✅ Enable encryption at rest
- ✅ Restrict access via Azure RBAC
- ✅ Enable versioning for recovery
- ❌ Never commit state files to Git

### 5. Service Principal Scoping

```bash
# Bad: Contributor on entire subscription
az ad sp create-for-rbac \
  --name "terraform-sp" \
  --role Contributor \
  --scopes /subscriptions/{subscription-id}

# Good: Contributor only on specific resource group
az ad sp create-for-rbac \
  --name "terraform-sp" \
  --role Contributor \
  --scopes /subscriptions/{subscription-id}/resourceGroups/petclinic-rg
```

---

## Common Issues and Solutions

### Issue 1: "Error: Subscription Not Found"

**Cause:** Not logged in or wrong subscription

**Solution:**
```bash
az login
az account list --output table
az account set --subscription "Azure for Students"
```

---

### Issue 2: "Error: Backend Initialization Required"

**Cause:** Terraform directory not initialized

**Solution:**
```bash
terraform init
```

---

### Issue 3: "Error: Storage Account Not Found" (Backend)

**Cause:** Storage account doesn't exist or wrong name

**Solution:**
```bash
# Verify storage account exists
az storage account show \
  --name tfstatepetclinic1234567890 \
  --resource-group terraform-state-rg

# If missing, create it (see setup steps above)
```

---

### Issue 4: "Error: Authentication Failed"

**Cause:** Service principal credentials expired or incorrect

**Solution:**
```bash
# Reset service principal credentials
az ad sp credential reset \
  --id {appId}

# Update ARM_CLIENT_SECRET with new password
```

---

### Issue 5: "Error: State Lock"

**Cause:** Previous operation didn't complete or crashed

**Solution:**
```bash
# Force unlock (use carefully!)
terraform force-unlock {lock-id}

# Only do this if you're sure no other Terraform process is running
```

---

## Recommended Setup Path

### For Issue #003 (Initial Setup)

**Phase 1: Local Development**
1. Install Terraform and Azure CLI
2. Authenticate with `az login`
3. Create Terraform project with local backend
4. Test with simple resource group
5. Verify `terraform plan` works

**Phase 2: Remote State (Later)**
6. Create Azure Storage account for state
7. Update backend.tf to use azurerm backend
8. Migrate state with `terraform init -migrate-state`
9. Verify state in Azure Storage

**Reasoning:**
- Start simple with local backend
- Validate setup works
- Add complexity (remote state) once basics proven
- Easier to troubleshoot local issues first

---

## Next Steps (After Issue #003)

**Issue #004:** Azure Database (PostgreSQL)
- Will use Terraform to create Azure Database for PostgreSQL
- Requires Issue #003 setup to be complete

**Issue #005:** Azure Container App
- Will deploy Docker container to Azure Container Apps
- Requires Issue #003 Terraform setup

**Issue #006:** Manual Deployment
- Test Terraform deployment manually
- Verify resources created correctly

**Issue #007:** GitHub Actions Automation
- Automate Terraform with GitHub Actions
- Use service principal for authentication

---

## Testing Checklist

After completing setup, verify:

- [ ] Terraform installed: `terraform --version` shows v1.6+
- [ ] Azure CLI installed: `az --version` shows v2.50+
- [ ] Azure authenticated: `az account show` displays subscription
- [ ] Terraform project created in `terraform/` directory
- [ ] All required files present (terraform.tf, providers.tf, backend.tf, main.tf, variables.tf, outputs.tf)
- [ ] `.gitignore` configured to exclude sensitive files
- [ ] `terraform init` completes successfully
- [ ] `terraform validate` shows "Success!"
- [ ] `terraform fmt` formats code correctly
- [ ] `terraform plan` runs without errors (even if no resources)

---

**Research Completed:** 2026-02-27
**Ready for Implementation:** ✅ Yes
**Recommended Approach:** Local backend initially, migrate to Azure Storage backend after basics proven working
