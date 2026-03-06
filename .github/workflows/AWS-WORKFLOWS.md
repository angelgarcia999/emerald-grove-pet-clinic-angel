# AWS Terraform Workflows

## Overview

Two GitHub Actions workflows manage AWS infrastructure deployment and teardown:

1. **terraform-apply-aws.yml** - Automatically deploys when new Docker images are built
2. **terraform-destroy-aws.yml** - Manually destroys all AWS infrastructure

---

## 1. Terraform Apply AWS

**Triggers:**
- ✅ Automatically after successful "Maven Tests" workflow on `main` branch
- ✅ Manually via workflow_dispatch

**What it does:**
1. Runs `terraform plan` with the latest Docker image
2. Applies infrastructure changes automatically
3. Outputs the application URL and database endpoint

**When to use:**
- Runs automatically when you push to main and tests pass
- Can also trigger manually from GitHub Actions UI if needed

---

## 2. Terraform Destroy AWS

**Triggers:**
- Manual only (workflow_dispatch)

**What it does:**
1. Validates you typed "destroy" exactly
2. Runs `terraform destroy` to delete all AWS resources
3. Provides summary of destroyed resources

**How to use:**
1. Go to Actions → "Terraform Destroy AWS"
2. Click "Run workflow"
3. Type `destroy` in the confirmation field
4. Click "Run workflow"

**⚠️ Warning:** This will delete:
- ECS Cluster, Service, and Tasks
- Application Load Balancer
- RDS PostgreSQL Database (and all data!)
- Security Groups
- CloudWatch Logs
- IAM Roles and Policies

---

## Required GitHub Secrets

Both workflows require these secrets to be configured:

### AWS Credentials
- `AWS_ACCESS_KEY_ID` - AWS access key for Terraform
- `AWS_SECRET_ACCESS_KEY` - AWS secret key for Terraform

### Terraform Variables
- `TF_DB_PASSWORD` - PostgreSQL database password

### How to add secrets:
1. Go to your GitHub repo
2. Settings → Secrets and variables → Actions
3. Click "New repository secret"
4. Add each secret above

---

## Workflow Sequence

```
Push to main
    ↓
Maven Tests run
    ↓
Tests pass → Docker image built & pushed
    ↓
terraform-apply-aws.yml triggers
    ↓
Infrastructure updated with new image
    ↓
Application running at ALB URL
```

---

## Monitoring Deployments

**Check deployment status:**
- GitHub Actions → "Terraform Apply AWS" workflow
- View outputs for App URL and DB endpoint

**Access application:**
- URL is printed in workflow output
- Format: `http://petclinic-dev-*.elb.amazonaws.com`

---

## Troubleshooting

**Apply fails with "No value for required variable":**
- Ensure `TF_DB_PASSWORD` secret is set

**Apply fails with AWS authentication error:**
- Check `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` secrets
- Verify IAM user has necessary permissions

**Destroy workflow doesn't run:**
- Ensure you typed exactly `destroy` (lowercase, no quotes)
- Check workflow logs for validation errors

---

## Cost Management

**To save costs when not using:**
1. Run the destroy workflow to delete all resources
2. Infrastructure can be recreated by:
   - Manually triggering terraform-apply-aws.yml, or
   - Pushing code to main (triggers full pipeline)

**Estimated costs (us-west-2):**
- ECS Fargate: ~$30/month (1 vCPU, 2GB RAM, always running)
- RDS db.t3.micro: ~$15/month
- ALB: ~$16/month
- **Total: ~$61/month**
