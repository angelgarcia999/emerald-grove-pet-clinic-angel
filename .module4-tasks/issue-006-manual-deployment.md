# Issue #006: Deploy Manually with Terraform

**Status:** ✅ Closed
**Priority:** High
**Estimated Time:** 2 hours
**Phase:** 2 - Terraform Deployment

---

## Description

Perform a complete manual deployment using Terraform to verify everything works before automating in CI/CD.

**This is your first full deployment!**

---

## Acceptance Criteria

- [x] All Terraform files completed (Issues #003, #004, #005)
- [x] Run `terraform plan` - review what will be created
- [x] Run `terraform apply` - deploy to Azure
- [x] Verify all resources created in Azure Portal
- [x] App is running and accessible
- [x] Database is populated (can add owners, pets, visits)
- [x] No Terraform errors
- [x] Document deployment process

---

## Implementation Steps

### 1. Pre-flight Check

```bash
cd terraform

# Validate syntax
terraform validate

# Format code
terraform fmt

# Review plan
terraform plan
```

### 2. Deploy

```bash
# Apply (creates all resources)
terraform apply

# Review what will be created
# Type 'yes' to confirm
```

### 3. Verify Deployment

```bash
# Get outputs
terraform output

# Test app
export APP_URL=$(terraform output -raw app_url)
curl https://$APP_URL/actuator/health

# Open in browser
open https://$APP_URL
```

### 4. Test Full Functionality

```
1. Navigate to "Find Owners"
2. Add a new owner
3. Add a pet to owner
4. Add a veterinarian
5. Schedule a visit
6. Verify data persists (refresh page)
```

### 5. Check Azure Portal

```
1. Login: portal.azure.com
2. Navigate to Resource Group
3. Verify all resources exist:
   - Container App
   - Container App Environment
   - PostgreSQL Flexible Server
   - Application Insights (if added)
```

---

## Troubleshooting

**App won't start:**
```bash
# Check logs
az containerapp logs show \
  --name petclinic-app \
  --resource-group <rg-name> \
  --follow
```

**Database connection fails:**
- Verify POSTGRES_URL format
- Check firewall rules
- Verify credentials

**Terraform errors:**
- Check Azure subscription is active
- Verify authentication: `az account show`
- Check quotas (some regions have limits)

---

## Documentation

Create `docs/DEPLOYMENT.md`:
```markdown
# Deployment Process

## Prerequisites
- Terraform v1.6+
- Azure CLI v2.50+
- Azure subscription with credits

## Deploy
\`\`\`bash
cd terraform
terraform init
terraform plan
terraform apply
\`\`\`

## Verify
- App URL: <your-url>
- Health: <your-url>/actuator/health
```

---

## Related Issues

- **Depends on:** #003, #004, #005 (all infrastructure)
- **Blocks:** #007 (need manual deploy working first)

---

## Definition of Done

- [x] `terraform apply` succeeds
- [x] All resources exist in Azure
- [x] App is accessible and functional
- [x] Can perform CRUD operations
- [x] Deployment documented
- [x] Issue marked: ✅ Closed

---

## Success Criteria

**You know this works when:**
- Push-button deployment (just `terraform apply`)
- App fully functional in Azure
- Data persists across app restarts
- No manual Azure Portal clicking needed
