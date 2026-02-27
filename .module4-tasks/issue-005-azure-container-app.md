# Issue #005: Create Azure Container App

**Status:** 🟡 Open
**Priority:** High
**Estimated Time:** 4 hours (with AI: 2 hours)
**Phase:** 2 - Terraform Deployment

---

## Description

Create Azure Container App that runs your Spring Boot Pet Clinic application. This is where your app lives in Azure.

---

## Acceptance Criteria

- [ ] Terraform file: `terraform/container-app.tf`
- [ ] Creates: azurerm_container_app_environment
- [ ] Creates: azurerm_container_app
- [ ] Pulls image: `ghcr.io/angelgarcia999/petclinic:latest`
- [ ] Connects to PostgreSQL database (from Issue #004)
- [ ] Exposes port 8080 with ingress
- [ ] Environment variables configured (DB connection)
- [ ] Health probes configured
- [ ] `terraform apply` deploys app
- [ ] App accessible via HTTPS URL
- [ ] Can see Pet Clinic homepage in browser

---

## AI Prompt

```
Generate Terraform for Azure Container App:

1. Resource: azurerm_container_app_environment
   - Name: petclinic-env
   - Location: var.location

2. Resource: azurerm_container_app
   - Name: petclinic-app
   - Image: ghcr.io/angelgarcia999/petclinic:latest
   - CPU: 0.5 cores
   - Memory: 1Gi
   - Min replicas: 1, Max replicas: 3

3. Secrets and Environment variables:
   - Create Container App secret: db-password
   - SPRING_PROFILES_ACTIVE: postgres (env var)
   - POSTGRES_URL: jdbc:postgresql://<from database output> (env var)
   - POSTGRES_USER: petclinic_admin (env var)
   - POSTGRES_PASSWORD: reference secret 'db-password' (NOT inline value)

4. Ingress:
   - External: true
   - Target port: 8080
   - Transport: auto

5. Health probes:
   - Liveness: /actuator/health/liveness
   - Readiness: /actuator/health/readiness

6. Output: application URL (FQDN)
```

---

## Related Issues

- **Depends on:** #002 (Docker image must exist)
- **Depends on:** #004 (Database must exist)
- **Blocks:** #007 (Deployment automation)

---

## Testing

```bash
cd terraform
terraform apply

# Get app URL
export APP_URL=$(terraform output -raw app_url)

# Test health endpoint
curl https://$APP_URL/actuator/health

# Test in browser
open https://$APP_URL

# Check logs if issues
az containerapp logs show \
  --name petclinic-app \
  --resource-group <your-rg>
```

---

## Notes

- **URL:** Azure provides: `*.azurecontainerapps.io`
- **SSL:** Automatic HTTPS
- **Cost:** ~$5-10/month
- **Scaling:** Auto-scales based on HTTP requests

---

## Definition of Done

- [ ] Terraform deploys Container App
- [ ] App accessible via HTTPS
- [ ] Can see Pet Clinic homepage
- [ ] Database connection working
- [ ] Health endpoints responding
- [ ] Issue marked: ✅ Closed
