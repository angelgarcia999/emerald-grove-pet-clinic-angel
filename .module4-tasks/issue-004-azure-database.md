# Issue #004: Create Azure PostgreSQL Database

**Status:** 🟢 Implementation Complete (pending `terraform apply`)
**Priority:** High
**Estimated Time:** 3 hours (with AI: 1.5 hours)
**Phase:** 2 - Terraform Deployment

---

## Description

Use Terraform to create Azure PostgreSQL Flexible Server for the Pet Clinic database.

---

## Acceptance Criteria

- [x] Terraform file: `terraform/database.tf`
- [x] Creates: azurerm_postgresql_flexible_server
- [x] Creates: azurerm_postgresql_flexible_server_database
- [x] Creates: VNet integration OR specific IP firewall rules (no blanket Azure services allow)
- [x] Database name: `petclinic`
- [x] PostgreSQL version: 14 or higher
- [x] Tier: Burstable B1ms (cheap for dev/test)
- [x] Outputs: hostname, database name, port (password marked sensitive)
- [ ] `terraform apply` creates database successfully
- [ ] Can connect: `psql -h <hostname> -U <user> -d petclinic`

---

## AI Prompt

```text
Generate Terraform configuration for Azure PostgreSQL:

1. Resource: azurerm_postgresql_flexible_server
   - Name: petclinic-db-${var.environment}
   - Version: 14
   - SKU: B_Standard_B1ms (burstable, cheap)
   - Storage: 32GB
   - Backup retention: 7 days
   - Administrator login: petclinic_admin
   - Administrator password: (use variable, will be secret)

2. Resource: azurerm_postgresql_flexible_server_database
   - Name: petclinic
   - Charset: UTF8
   - Collation: en_US.utf8

3. Network Security (choose one):
   Option A: VNet Integration (preferred for production)
   - Integrate database into Container App VNet
   - Use Private Endpoint for secure access

   Option B: Scoped Firewall Rules (for development)
   - Add specific IP addresses only
   - Document: Do NOT use "Allow Azure services" blanket rule
   - Example: Container App outbound IPs only

4. Outputs (non-sensitive only):
   - Hostname (e.g., petclinic-db.postgres.database.azure.com)
   - Database name (e.g., petclinic)
   - Port (5432)
   - Username (for reference)

   Note: DO NOT output connection string or password
   Password must be marked with sensitive = true
   Retrieve password from Azure Key Vault or use secret reference
```

---

## Related Issues

- **Depends on:** #003 (Terraform setup)
- **Blocks:** #005 (Container App needs database connection)

---

## Testing

```bash
cd terraform
terraform plan
terraform apply

# Get connection info
terraform output

# Test connection (requires psql)
export DB_HOST=$(terraform output -raw db_hostname)
psql -h $DB_HOST -U petclinic_admin -d petclinic
```

---

## Notes

- **Cost:** ~$10-15/month with B1ms tier
- **Security:** Use Azure Key Vault for passwords in production
- **Network:** May need VNet integration for security

---

## Definition of Done

- [x] Terraform creates database (configuration validated via `terraform validate`)
- [ ] Can connect to database (requires `terraform apply`)
- [x] Connection string available in outputs (hostname, db_name, port, admin_login)
- [ ] Issue marked: Closed (pending apply verification)
