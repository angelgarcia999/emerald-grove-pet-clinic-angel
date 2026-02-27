# Issue #007: Automate Deployment in GitHub Actions

**Status:** 🟡 Open
**Priority:** High
**Estimated Time:** 3 hours (with AI: 1.5 hours)
**Phase:** 2 - Terraform Deployment

---

## Description

Automate Terraform deployment in GitHub Actions so that pushing to main automatically deploys to Azure.

**This completes the CI/CD pipeline!**

---

## Acceptance Criteria

- [ ] Workflow: `.github/workflows/terraform-deploy.yml`
- [ ] Triggers: On push to `main` branch
- [ ] Runs: `terraform plan` on PRs (preview changes)
- [ ] Runs: `terraform apply` on main (auto-deploy)
- [ ] Uses: Azure Service Principal for authentication
- [ ] Includes: Health checks after deployment
- [ ] Fails: If health checks fail
- [ ] Complete flow: Push code → Tests → Build → Deploy → Health check

---

## Implementation Steps

### 1. Create Azure Service Principal

```bash
# IMPORTANT: Scope to resource group, not entire subscription (least privilege)
# First, create the resource group if it doesn't exist:
az group create --name rg-petclinic-prod --location eastus

# Create service principal scoped to resource group only
az ad sp create-for-rbac \
  --name "github-actions-petclinic" \
  --role contributor \
  --scopes /subscriptions/<SUBSCRIPTION_ID>/resourceGroups/rg-petclinic-prod \
  --sdk-auth

# Save the JSON output - you'll need it for GitHub secrets
# Note: Scoped to resource group, NOT subscription-wide
```

### 2. Add GitHub Secrets

Go to GitHub repo → Settings → Secrets → Actions:
- `AZURE_CREDENTIALS` - The JSON from above
- `AZURE_SUBSCRIPTION_ID` - Your subscription ID
- `TF_VAR_db_password` - PostgreSQL admin password

### 3. Create Workflow

Use AI prompt below to generate workflow file.

---

## AI Prompt

```
Generate GitHub Actions workflow for Terraform deployment:

1. Name: terraform-deploy.yml

2. Triggers:
   - push to main branch
   - pull_request (plan only)

3. Jobs:

   Job 1: terraform-plan
   - Runs on: PRs and main
   - Steps:
     - Checkout code
     - Setup Terraform
     - Azure login (using AZURE_CREDENTIALS secret)
     - terraform init
     - terraform plan
     - Comment plan on PR (if PR)

   Job 2: terraform-apply
   - Runs on: push to main only
   - Depends on: terraform-plan
   - Steps:
     - Checkout code
     - Setup Terraform
     - Azure login
     - terraform init
     - terraform apply -auto-approve
     - Get app URL from outputs
     - Health check (curl health endpoint)
     - Fail if health check fails

4. Use:
   - hashicorp/setup-terraform@v3
   - azure/login@v1
   - Working directory: ./terraform
```

---

## Testing

### Test Plan Workflow (PR)
```bash
git checkout -b test-deployment
# Make a small change
git commit -m "test: verify terraform plan in CI"
git push origin test-deployment
# Create PR, verify plan runs and comments
```

### Test Apply Workflow (Main)
```bash
git checkout main
git merge test-deployment
git push origin main
# Verify deployment runs and succeeds
```

---

## Related Issues

- **Depends on:** #001, #002, #006 (all previous infrastructure)
- **Completes:** Core Module 4.0 requirement (automated pipeline)

---

## Notes

- **Security:** Service Principal scoped to resource group only (least privilege)
- **State:** MUST use remote backend (Azure Storage with locking) - never commit .tfstate to repo
  - Create storage account for Terraform state
  - Configure backend in terraform/backend.tf
  - Use state locking to prevent concurrent modifications
  - Add backend config to GitHub Actions workflow
- **Secrets:** Never commit credentials to git
- **Rollback:** Git revert if deployment fails

---

## Definition of Done

- [ ] Service Principal created
- [ ] GitHub secrets configured
- [ ] Workflow file created
- [ ] Plan runs on PRs
- [ ] Apply runs on main push
- [ ] Health checks verify deployment
- [ ] Full push-to-deploy working
- [ ] Issue marked: ✅ Closed

---

## Success Criteria

**You know this works when:**

```
1. Make code change
2. git push origin main
3. Watch GitHub Actions:
   - Tests run ✅
   - Docker builds ✅
   - Terraform deploys ✅
   - Health checks pass ✅
4. App updated in Azure automatically
5. Total time: ~10-15 minutes
```

**ZERO manual steps required!** 🚀
