# Issue #003: Setup Terraform + Azure

**Status:** 🟡 Open
**Priority:** High
**Estimated Time:** 2 hours
**Phase:** 2 - Terraform Deployment

---

## Description

Install and configure Terraform and Azure CLI, authenticate with Azure, and initialize Terraform project structure.

---

## Acceptance Criteria

- [ ] Terraform installed (v1.6+): `terraform --version`
- [ ] Azure CLI installed (v2.50+): `az --version`
- [ ] Azure account created (student credits)
- [ ] Authenticated: `az login`
- [ ] Terraform project initialized: `terraform/` directory
- [ ] Backend configured for state management
- [ ] Can run: `terraform plan` (even if empty)

---

## Implementation Steps

### 1. Install Tools

```bash
# macOS
brew install terraform azure-cli

# Verify
terraform --version
az --version
```

### 2. Azure Setup

```bash
# Sign up: https://azure.microsoft.com/en-us/free/students/
# Login
az login

# List subscriptions
az account list --output table

# Set default
az account set --subscription "YOUR_SUBSCRIPTION_ID"
```

### 3. Initialize Terraform Project

```bash
mkdir terraform
cd terraform

# Create provider.tf
# Create main.tf
# Create variables.tf
# Create outputs.tf
# Create backend.tf

terraform init
```

---

## AI Prompt

```
Create Terraform project structure:

1. provider.tf:
   - Configure azurerm provider (v3.0+)
   - Set features block

2. main.tf:
   - Resource group placeholder

3. variables.tf:
   - location (default: "East US")
   - environment (default: "dev")
   - project_name (default: "petclinic")

4. outputs.tf:
   - Empty for now

5. backend.tf:
   - Local backend initially (can migrate to Azure Storage later)

6. .gitignore:
   - Ignore .terraform/, *.tfstate, *.tfvars
```

---

## Related Issues

- **Blocks:** #004, #005, #006 (all Terraform resources)

---

## Definition of Done

- [ ] Tools installed and working
- [ ] Azure authenticated
- [ ] Terraform project initialized
- [ ] `terraform plan` runs successfully
- [ ] Issue marked: ✅ Closed
