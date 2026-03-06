# Pet Clinic AWS Deployment

Simple AWS App Runner + RDS deployment for Pet Clinic.

---

## **What You Have**

- **App Runner Service** - Runs your Spring Boot container with auto-scaling
- **RDS PostgreSQL** - Managed database (db.t3.micro)
- **Auto HTTPS** - Automatic SSL certificate
- **VPC Connector** - Secure connection between App Runner and RDS

**Estimated Cost:** ~$20/month

---

## **Prerequisites**

1. AWS CLI configured with `liatrio-forge` profile
2. Terraform installed (>= 1.0)
3. Database password ready

---

## **Deploy**

### **1. Initialize Terraform**

```bash
cd terraform
terraform init
```

### **2. Deploy Everything**

```bash
terraform apply -var="db_password=YourSecurePassword123!"
```

**What happens:**
- Creates RDS PostgreSQL (~4 minutes)
- Creates App Runner service (~3 minutes)
- Outputs HTTPS URL

**Total time: ~7 minutes**

---

## **Access Your App**

After deployment completes:

```bash
# Get the URL
terraform output app_url

# Example output:
# https://abc123xyz.us-west-2.awsapprunner.com
```

Open that URL in your browser!

---

## **Update Container Image**

If you push a new image to GHCR:

### **Option 1: Manual update**
```bash
terraform apply -var="db_password=YourSecurePassword123!"
```

App Runner does blue/green deployment (zero downtime).

### **Option 2: Enable auto-deploy**
```hcl
# In variables.tf or via command line
auto_deploy_enabled = true
```

App Runner automatically pulls new `:latest` images!

---

## **View Logs**

```bash
# Via AWS Console
aws apprunner list-operations --service-arn $(terraform output -raw app_runner_service_arn)

# Or visit:
# https://console.aws.amazon.com/apprunner
```

---

## **Destroy Everything**

**When you're done testing:**

```bash
terraform destroy -var="db_password=YourSecurePassword123!"
```

**What happens:**
- Deletes App Runner service (~2 minutes)
- Deletes RDS database (~3 minutes)
- Everything is gone

**Total time: ~5 minutes**

**Cost stops immediately** - no residual charges.

---

## **Cost Breakdown**

| Resource | Type | Monthly Cost |
|----------|------|--------------|
| App Runner | 1 vCPU, 2GB RAM | ~$7 |
| RDS PostgreSQL | db.t3.micro | ~$12 |
| Data Transfer | Minimal | ~$1 |
| **Total** | | **~$20/month** |

**Free Tier:** RDS eligible for 12 months free (if new AWS account)

---

## **Troubleshooting**

### **App not starting?**

Check logs in AWS console or:
```bash
aws logs tail "/aws/apprunner/petclinic-dev" --follow
```

### **Database connection failed?**

Verify:
1. VPC connector is in same VPC as RDS
2. Security group allows port 5432
3. Database password is correct

### **Want to SSH / debug container?**

App Runner doesn't allow SSH. Check logs instead:
```bash
aws apprunner list-operations --service-arn $(terraform output -raw app_runner_service_arn)
```

---

## **Files**

```
terraform/
├── provider.tf          # AWS provider config
├── variables.tf         # Input variables
├── data.tf             # VPC/subnet lookups
├── app-runner.tf       # App Runner service
├── rds.tf              # PostgreSQL database
├── security-groups.tf  # Firewall rules
├── outputs.tf          # Outputs (URL, etc.)
└── README.md           # This file
```

**Total:** 8 files, ~300 lines of code

---

## **Next Steps**

After deploying:

1. ✅ Open app URL in browser
2. ✅ Test adding owners/pets
3. ✅ Verify data persists
4. ✅ When done: `terraform destroy`

---

## **Comparison to Azure**

| Feature | Azure (blocked) | AWS App Runner |
|---------|----------------|----------------|
| Setup | Can't deploy | ✅ Works! |
| Complexity | 10 files | 8 files |
| Cost | ~$10/month | ~$20/month |
| HTTPS | Auto | Auto |
| Permissions | ❌ Blocked | ✅ Have access |

**Result:** AWS App Runner gets you deployed! 🚀
