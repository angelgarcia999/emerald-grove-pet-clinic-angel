# Module 4.0: DevOps & Platform Engineering Research

**Project:** Emerald Grove Veterinary Clinic
**Date:** 2026-02-25
**Current Branch:** `feature/11-conflict-detection`
**Repository Status:** Clean

---

## Executive Summary

The Emerald Grove Pet Clinic has **excellent development practices** (TDD, multi-database testing, E2E automation) but **lacks production-grade deployment infrastructure**. This creates a significant opportunity for AI-accelerated DevOps transformation.

**Key Findings:**
- ✅ Strong foundation: Spring Boot, multi-DB support, comprehensive testing
- ⚠️ Missing: Production CI/CD pipeline, container registry, deployment automation
- ⚠️ Security: Hardcoded credentials, no secrets management
- ⚠️ Observability: No metrics collection, centralized logging, or alerting

**Biggest Opportunity:** Compress weeks of infrastructure work into days using AI-assisted IaC and pipeline generation.

---

## 1. Current State Assessment

### 1.1 CI/CD Pipelines ❌ INCOMPLETE

**What Exists:**
```yaml
# .github/workflows/e2e-tests.yml
- Playwright E2E tests on PR/main
- Node.js 20 + Java 17 setup
- Browser automation (Chromium only)
- Test report uploads

# .github/workflows/claude-code.yml
- AI-powered code review on PRs
- Claude Sonnet 4.5 analysis
- Automated review comments
```

**Critical Gaps:**
- ❌ No unit test execution in CI (tests exist but don't run automatically)
- ❌ No integration tests (MySQL, PostgreSQL) in pipeline
- ❌ No code coverage reporting gate (JaCoCo configured locally only)
- ❌ No container image building
- ❌ No deployment stages (dev → staging → prod)
- ❌ No security scanning (SAST/DAST, dependency checks)
- ❌ No artifact publishing to registry

**Impact:** Developers can merge code without full test execution, coverage validation, or deployment verification.

---

### 1.2 Infrastructure as Code 🟡 PARTIAL

**What Exists:**
```yaml
# k8s/db.yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: petclinic-db
spec:
  replicas: 1
  template:
    spec:
      containers:
      - name: postgres
        image: postgres:18.1
        # PROBLEM: Hardcoded credentials in YAML
        env:
        - name: POSTGRES_USER
          value: petclinic
        - name: POSTGRES_PASSWORD
          value: petclinic
```

```yaml
# k8s/petclinic.yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: petclinic
spec:
  replicas: 1
  template:
    spec:
      containers:
      - name: petclinic
        # PROBLEM: External image, not built from source
        image: dsyer/petclinic
```

**Critical Gaps:**
- ❌ No Terraform/Pulumi for cloud infrastructure
- ❌ No Helm charts for Kubernetes
- ❌ No ConfigMaps for environment-specific config
- ❌ No Kubernetes Secrets (credentials in plain YAML)
- ❌ No persistent volumes (database data lost on pod restart)
- ❌ No Ingress configuration (no external access)
- ❌ No resource limits (CPU/memory) - risk of noisy neighbor issues
- ❌ No horizontal pod autoscaling
- ❌ No network policies or RBAC

**Impact:** Cannot deploy to production safely. Infrastructure is not reproducible or secure.

---

### 1.3 Build & Deployment ✅ LOCAL ONLY

**What Exists:**
```bash
# Local build with Spring Boot buildpacks
./mvnw spring-boot:build-image

# Local development with Tilt
tilt up  # Auto-reload, health checks, PostgreSQL

# Docker Compose for databases
docker compose up mysql postgres
```

**Build Configuration:**
- Maven wrapper with reproducible builds
- Spring Boot parent v4.0.0
- Java 17 enforcement
- Code formatting validation (spring-javaformat)
- Checkstyle + nohttp validation
- JaCoCo code coverage (local only)
- SBOM generation (CycloneDX)

**Critical Gaps:**
- ❌ Image not built in CI/CD pipeline
- ❌ No container registry (Docker Hub, ECR, GCR)
- ❌ No image versioning/tagging strategy
- ❌ No multi-stage Docker builds (buildpacks used, but not optimized)
- ❌ No deployment scripts
- ❌ No blue-green or canary deployment support
- ❌ No rollback procedures

**Impact:** Manual, error-prone deployments. No audit trail of what's running in production.

---

### 1.4 Database Management 🟡 MULTI-DB SUPPORT

**What Exists:**
```
Supported Databases:
├── H2 (default) - In-memory, development
├── MySQL 9.5 - Docker Compose + Testcontainers
└── PostgreSQL 18.1 - Docker Compose + Kubernetes

Profiles:
├── application-mysql.properties
└── application-postgres.properties

Integration Tests:
├── MySqlIntegrationTests.java (Testcontainers)
├── PostgresIntegrationTests.java (Docker Compose)
└── PostgresSequenceResetIntegrationTests.java
```

**Critical Gaps:**
- ❌ No database migration tool (Flyway/Liquibase)
  - Currently: SQL scripts run via `spring.sql.init.mode=always`
  - Problem: No versioning, rollback, or audit trail
- ❌ No connection pooling configuration (uses HikariCP defaults)
- ❌ No database backup/restore automation
- ❌ No replication or high availability setup
- ❌ No data encryption at rest
- ❌ No database performance tuning
- ❌ PostgreSQL data not persisted (no PVC in k8s/db.yml)

**Impact:** Cannot safely evolve database schema in production. No disaster recovery plan.

---

### 1.5 Testing Automation ✅ EXCELLENT

**What Exists:**
```
Unit Tests:
├── JUnit 5 + Mockito
├── Spring Boot Test
└── AssertJ fluent assertions

Integration Tests:
├── Testcontainers (MySQL, PostgreSQL)
├── Spring Boot Docker Compose support
└── Multi-database CI tests possible (not implemented)

E2E Tests:
├── Playwright v1.57.0 (TypeScript)
├── Automated in GitHub Actions
├── HTML reports + JUnit XML
└── Accessibility testing (axe-core included)

Coverage:
├── JaCoCo configured (≥90% target per CLAUDE.md)
└── Reports generated at prepare-package phase
```

**What's Missing:**
- ❌ JaCoCo not run in CI (no coverage gate)
- ❌ No performance testing (load, stress, soak)
- ❌ No chaos engineering
- ❌ No mutation testing (PIT)
- ❌ No contract testing
- ❌ No visual regression testing

**Strength:** This is the project's strongest area. TDD practices are enforced via `.claude/agents/tdd-enforcer`.

---

### 1.6 Environment Management 🟡 DEV ONLY

**What Exists:**
```properties
# application.properties (default H2)
database=h2
spring.jpa.hibernate.ddl-auto=none
spring.jpa.open-in-view=false
management.endpoints.web.exposure.include=*  # ⚠️ SECURITY RISK

# Profile-based database switching
spring.profiles.active=mysql    # or postgres
```

**Environment Variables:**
```bash
# MySQL
MYSQL_URL=jdbc:mysql://localhost/petclinic
MYSQL_USER=petclinic
MYSQL_PASS=petclinic

# PostgreSQL
POSTGRES_URL=jdbc:postgresql://localhost/petclinic
POSTGRES_USER=petclinic
POSTGRES_PASS=petclinic
```

**Critical Gaps:**
- ❌ No environment-specific configs (dev, staging, prod)
- ❌ No secrets management (Vault, AWS Secrets Manager)
- ❌ No .env file support or documentation
- ❌ No environment variable validation at startup
- ❌ All Actuator endpoints exposed (security risk in production)
- ❌ No feature flag system
- ❌ No configuration drift detection

**Impact:** Cannot safely configure production. Secrets visible in logs/environment.

---

### 1.7 Monitoring & Observability ❌ MINIMAL

**What Exists:**
```
Spring Boot Actuator:
├── /actuator/health (basic health check)
├── /actuator/metrics (not collected anywhere)
├── /actuator/info (build + git metadata)
└── 20+ endpoints exposed (⚠️ security risk)
```

**Critical Gaps:**
- ❌ No Prometheus metrics exporter
- ❌ No Grafana dashboards
- ❌ No centralized logging (ELK, Splunk, Datadog)
- ❌ No structured logging (JSON)
- ❌ No correlation IDs for request tracing
- ❌ No distributed tracing (Jaeger, Zipkin)
- ❌ No alerting system (PagerDuty, Opsgenie)
- ❌ No SLA monitoring
- ❌ No APM (Application Performance Monitoring)

**Impact:** Cannot diagnose production issues. No visibility into system health or performance.

---

### 1.8 Security & Compliance ❌ NOT PRODUCTION-READY

**What Exists:**
```
Pre-Commit Hooks:
├── Markdown linting
├── YAML/JSON/XML validation
├── Merge conflict detection
├── Shell script linting (shellcheck)
└── Commit message validation (gitlint)

Build-Time Security:
├── nohttp-checkstyle (prevents HTTP URLs)
└── SBOM generation (CycloneDX)
```

**Critical Gaps:**
- ❌ No SAST (Static Application Security Testing)
- ❌ No DAST (Dynamic Application Security Testing)
- ❌ No dependency vulnerability scanning in CI
- ❌ No container image scanning (Trivy, Snyk)
- ❌ No secrets detection (GitGuardian, TruffleHog)
- ❌ Credentials hardcoded in `k8s/db.yml`
- ❌ All Actuator endpoints exposed without authentication
- ❌ No SSL/TLS termination configured
- ❌ No HTTPS enforcement
- ❌ No CORS configuration
- ❌ No OWASP dependency check

**Impact:** High security risk. Cannot pass compliance audits.

---

## 2. Friction Points Analysis

### 🔥 Top 5 Friction Points (Ranked by Impact)

| Rank | Friction Point | Current Time Cost | Impact | AI Opportunity |
|------|----------------|-------------------|--------|----------------|
| 1 | **Manual Deployment** | ~2 hours per deploy | HIGH | Full automation with IaC |
| 2 | **No Database Migrations** | ~30 min per schema change | HIGH | Flyway generation |
| 3 | **Missing CI Test Execution** | ~15 min manual testing | MEDIUM | GitHub Actions workflow |
| 4 | **No Metrics/Logging** | Hours debugging prod issues | HIGH | Prometheus + ELK stack |
| 5 | **Hardcoded Secrets** | Security risk + config drift | HIGH | Kubernetes Secrets + Vault |

### Detailed Friction Analysis

#### 1️⃣ Manual Deployment Process

**Current State:**
```bash
# Developer must manually:
1. Build image: ./mvnw spring-boot:build-image
2. Tag image: docker tag ... angelgarcia999/petclinic:v1.2.3
3. Push to registry: docker push angelgarcia999/petclinic:v1.2.3
4. Update k8s/petclinic.yml with new tag
5. Apply: kubectl apply -f k8s/
6. Verify: kubectl get pods
7. Check logs: kubectl logs -f petclinic-xxx
```

**Time Cost:** ~2 hours (including verification, rollback if issues)

**Risk:** Human error, no audit trail, no automated verification

**AI Solution:**
```yaml
# Generate CI/CD pipeline that:
- Builds on tag push
- Runs full test suite
- Pushes to registry
- Deploys to staging
- Smoke tests staging
- Approval gate
- Deploys to production
- Health checks
- Auto-rollback on failure
```

**Estimated Time Savings:** 90% (from 2 hours to 10 minutes)

---

#### 2️⃣ Database Schema Evolution Without Migrations

**Current State:**
```sql
-- Developers manually edit SQL files:
src/main/resources/db/postgres/schema.sql
src/main/resources/db/mysql/schema.sql

-- Run via spring.sql.init.mode=always
-- ⚠️ PROBLEMS:
- No version tracking
- Cannot rollback
- No audit trail
- Idempotency issues
- Manual synchronization across DBs
```

**Time Cost:** ~30 minutes per schema change (careful manual editing, testing)

**Risk:** Data loss, production downtime, inconsistent state

**AI Solution:**
```bash
# Use AI to:
1. Generate Flyway migrations from schema diffs
2. Create rollback scripts automatically
3. Test migrations on all DB profiles
4. Version and audit all changes
```

**Implementation:**
```xml
<!-- Add to pom.xml -->
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-mysql</artifactId>
</dependency>
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

**Estimated Time Savings:** 70% (from 30 min to 10 min)

---

#### 3️⃣ Missing CI Test Execution

**Current State:**
```yaml
# .github/workflows/e2e-tests.yml runs E2E tests
# BUT: No unit tests, no integration tests, no coverage check
```

**Impact:**
- Developers can merge failing code
- No coverage enforcement (target: ≥90%)
- Multi-DB tests not verified in CI
- JaCoCo reports generated locally but ignored

**Time Cost:** ~15 minutes per PR (manual local test execution)

**Risk:** Bugs reach main branch, coverage regression

**AI Solution:**
```yaml
# Generate comprehensive CI workflow:
name: Build and Test

on: [push, pull_request]

jobs:
  unit-tests:
    runs-on: ubuntu-latest
    steps:
      - name: Run unit tests with coverage
        run: ./mvnw test jacoco:report
      - name: Enforce 90% coverage
        run: ./mvnw jacoco:check

  integration-tests:
    strategy:
      matrix:
        db: [mysql, postgres]
    runs-on: ubuntu-latest
    steps:
      - name: Run integration tests
        run: ./mvnw verify -Dspring.profiles.active=${{ matrix.db }}

  build:
    needs: [unit-tests, integration-tests]
    runs-on: ubuntu-latest
    steps:
      - name: Build container image
        run: ./mvnw spring-boot:build-image
      - name: Push to registry
        run: docker push ...
```

**Estimated Time Savings:** 100% (fully automated)

---

#### 4️⃣ Zero Observability in Production

**Current State:**
```
Debugging a production issue:
1. SSH into pod: kubectl exec -it petclinic-xxx -- bash
2. Check logs: tail -f /var/log/...
3. Guess at metrics: no data available
4. Reproduce locally: cannot replicate prod conditions
5. Deploy fix blind: hope it works
```

**Time Cost:** Hours per incident (guesswork, no data-driven debugging)

**Risk:** Extended downtime, customer impact, lost revenue

**AI Solution:**
```yaml
# Generate observability stack:
1. Prometheus metrics:
   - Request rates, latency, errors
   - JVM memory, GC, threads
   - Database connection pool stats
   - Custom business metrics

2. ELK Stack (Elasticsearch, Logstash, Kibana):
   - Centralized logging
   - Structured JSON logs
   - Correlation IDs for request tracing
   - Log aggregation across pods

3. Grafana Dashboards:
   - Application health
   - Database performance
   - Error rates and traces
   - SLA compliance

4. Alerting (Prometheus Alertmanager):
   - High error rate
   - Slow response times
   - Database connection failures
   - OOM warnings
```

**Dependencies:**
```xml
<!-- Add to pom.xml -->
<dependency>
  <groupId>io.micrometer</groupId>
  <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
<dependency>
  <groupId>net.logstash.logback</groupId>
  <artifactId>logstash-logback-encoder</artifactId>
</dependency>
```

**Estimated Time Savings:** 80% (from hours to minutes for debugging)

---

#### 5️⃣ Hardcoded Secrets & Configuration Drift

**Current State:**
```yaml
# k8s/db.yml - SECURITY VIOLATION
env:
- name: POSTGRES_USER
  value: petclinic  # ⚠️ Visible in git, logs, kubectl describe
- name: POSTGRES_PASSWORD
  value: petclinic  # ⚠️ NEVER ROTATE
```

**Impact:**
- Credentials in git history forever
- Visible in pod specs, logs, environment dumps
- No audit trail of who accessed secrets
- Cannot rotate without code changes
- Different credentials per environment = manual management

**Time Cost:** ~1 hour per secret rotation (find all references, update, redeploy)

**Risk:** Data breach, compliance violation, audit failure

**AI Solution:**
```yaml
# Generate Kubernetes Secrets:
apiVersion: v1
kind: Secret
metadata:
  name: db-credentials
type: Opaque
stringData:
  username: petclinic
  password: {{ .Values.database.password }}  # From Helm values

---
# Use in deployment:
env:
- name: POSTGRES_USER
  valueFrom:
    secretKeyRef:
      name: db-credentials
      key: username
- name: POSTGRES_PASSWORD
  valueFrom:
    secretKeyRef:
      name: db-credentials
      key: password
```

**Better: External Secrets Operator + Vault:**
```yaml
# Auto-sync from Vault:
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: db-credentials
spec:
  secretStoreRef:
    name: vault-backend
  target:
    name: db-credentials
  data:
  - secretKey: username
    remoteRef:
      key: database/petclinic
      property: username
  - secretKey: password
    remoteRef:
      key: database/petclinic
      property: password
```

**Estimated Time Savings:** 95% (from 1 hour to 3 minutes per rotation)

---

## 3. Module Objectives Mapping

### Pipeline from Scratch Challenge

**Goal:** Zero to deployed automatically

**Current Baseline:** Manual, ~2 hours per deployment

**Target State:**
```
Developer pushes code
    ↓
GitHub Actions triggers
    ↓
1. Build (Maven)
2. Unit tests + coverage check (≥90%)
3. Integration tests (H2, MySQL, PostgreSQL)
4. E2E tests (Playwright)
5. SAST/DAST security scans
6. Build container image
7. Push to registry (tag: main-sha-12345)
8. Deploy to staging (Kubernetes)
9. Smoke tests (health checks, basic API calls)
10. Manual approval gate (if main branch)
11. Deploy to production (blue-green)
12. Health verification
13. Auto-rollback if unhealthy
    ↓
Deployed in ~15 minutes
```

**AI-Assisted Tasks:**

| Task | AI Tool | Estimated Time | Manual Time |
|------|---------|----------------|-------------|
| Write CI workflow YAML | Claude Code | 10 min | 2 hours |
| Generate Dockerfile | Claude Code | 5 min | 30 min |
| Create Kubernetes manifests | Claude Code | 15 min | 3 hours |
| Write Helm chart | Claude Code | 20 min | 4 hours |
| Configure monitoring | Claude Code | 30 min | 8 hours |
| Setup secrets management | Claude Code | 20 min | 2 hours |
| **TOTAL** | | **100 min** | **19.5 hours** |

**Time Compression:** 91% reduction (19.5h → 1.7h)

---

### Friction Hunt & Automation Sprint

**Top Friction to Eliminate:**

#### Option 1: "One-Command Environment Setup"

**Current Pain:**
```bash
# New developer onboarding:
1. Install Java 17 (manually download, configure JAVA_HOME)
2. Install Maven or use wrapper
3. Install Docker Desktop
4. Install Node.js 20 for E2E tests
5. Install pre-commit hooks: ./scripts/setup-precommit.sh
6. Start databases: docker compose up
7. Run app: ./mvnw spring-boot:run
8. Run E2E tests: cd e2e-tests && npm install && npm test
9. Configure IDE settings, code formatters, etc.

Total time: ~2-3 hours (often with issues)
```

**Target: One Command to Rule Them All:**
```bash
./setup.sh  # AI-generated script
```

**AI-Generated Solution:**
```bash
#!/bin/bash
# Generated by Claude Code - Environment setup automation

set -e

echo "🚀 Setting up Emerald Grove Pet Clinic development environment..."

# 1. Verify dependencies
check_dependencies() {
  echo "Checking prerequisites..."
  command -v docker >/dev/null 2>&1 || { echo "❌ Docker required"; exit 1; }
  command -v java >/dev/null 2>&1 || { echo "❌ Java 17 required"; exit 1; }
  # ... more checks
}

# 2. Install tools
install_tools() {
  echo "Installing development tools..."
  # Pre-commit hooks
  if ! command -v pre-commit &> /dev/null; then
    brew install pre-commit || pip install pre-commit
  fi
  pre-commit install

  # Node.js for E2E tests
  if ! command -v node &> /dev/null; then
    brew install node@20 || echo "Install Node.js 20 manually"
  fi
}

# 3. Start infrastructure
start_infrastructure() {
  echo "Starting databases..."
  docker compose up -d postgres mysql

  # Wait for healthy
  until docker compose ps | grep "healthy"; do
    echo "Waiting for databases..."
    sleep 2
  done
}

# 4. Build application
build_app() {
  echo "Building application..."
  ./mvnw clean install -DskipTests

  echo "Installing E2E dependencies..."
  cd e2e-tests && npm install && cd ..
}

# 5. Verify setup
verify_setup() {
  echo "Running verification tests..."
  ./mvnw test -Dtest="*ControllerTests"
  echo "✅ Environment ready!"
}

# Main execution
check_dependencies
install_tools
start_infrastructure
build_app
verify_setup

echo ""
echo "🎉 Setup complete! You can now:"
echo "   • Run app: ./mvnw spring-boot:run"
echo "   • Run tests: ./mvnw test"
echo "   • Run E2E: cd e2e-tests && npm test"
echo "   • Use Tilt: tilt up"
```

**Time Savings:** 85% (from 2-3 hours to 20 minutes)

---

#### Option 2: "Self-Healing Test Data Generator"

**Current Pain:**
```java
// Tests manually create test data:
@BeforeEach
void setup() {
  Owner owner = new Owner();
  owner.setFirstName("George");
  owner.setLastName("Franklin");
  owner.setAddress("110 W. Liberty St.");
  owner.setCity("Madison");
  owner.setTelephone("6085551023");
  // ... 15 more lines of setup
}
```

**Problem:**
- Test data scattered across 50+ test files
- Inconsistent data between tests
- Hardcoded dates (temporal coupling risk)
- Manual maintenance
- No centralized fixtures

**AI-Generated Solution:**
```java
// Test fixture generator with Builder pattern
public class TestFixtures {

  public static OwnerBuilder owner() {
    return new OwnerBuilder()
      .withFirstName(faker.name().firstName())
      .withLastName(faker.name().lastName())
      .withAddress(faker.address().streetAddress())
      .withCity(faker.address().city())
      .withTelephone(faker.phoneNumber().phoneNumber())
      .withValidationPassing();
  }

  public static VisitBuilder visit() {
    return new VisitBuilder()
      .withDate(LocalDate.now().plusDays(1))  // Always future
      .withDescription(faker.lorem().sentence())
      .withValidVet()
      .withValidPet();
  }

  public static class OwnerBuilder {
    private Owner owner = new Owner();

    public OwnerBuilder withFirstName(String name) {
      owner.setFirstName(name);
      return this;
    }

    // ... builder methods

    public Owner build() {
      return owner;
    }
  }
}

// Usage in tests:
@Test
void shouldCreateOwner() {
  Owner owner = TestFixtures.owner().build();
  ownerRepository.save(owner);
  assertThat(owner.getId()).isNotNull();
}
```

**Time Savings:** 60% (from 10 min to 4 min per test)

---

#### Option 3: "Intelligent Log Aggregator & Analyzer"

**Current Pain:**
```bash
# Debugging production issues:
kubectl logs petclinic-abc123 | grep ERROR | less
kubectl logs petclinic-def456 | grep ERROR | less
# ... repeat for N pods

# No correlation between logs
# No structured data
# No search across time ranges
# No alerting on patterns
```

**AI-Generated Solution:**

**1. Structured Logging (Logback + JSON):**
```xml
<!-- logback-spring.xml -->
<appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
  <encoder class="net.logstash.logback.encoder.LogstashEncoder">
    <includeMdcKeyName>correlation_id</includeMdcKeyName>
    <includeMdcKeyName>user_id</includeMdcKeyName>
    <includeMdcKeyName>request_id</includeMdcKeyName>
  </encoder>
</appender>
```

**2. ELK Stack (Docker Compose):**
```yaml
# docker-compose-elk.yml
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    environment:
      - discovery.type=single-node
    ports:
      - "9200:9200"

  logstash:
    image: docker.elastic.co/logstash/logstash:8.11.0
    volumes:
      - ./logstash.conf:/usr/share/logstash/pipeline/logstash.conf
    depends_on:
      - elasticsearch

  kibana:
    image: docker.elastic.co/kibana/kibana:8.11.0
    ports:
      - "5601:5601"
    depends_on:
      - elasticsearch
```

**3. AI-Powered Log Analyzer (CLI Tool):**
```python
# log-analyzer.py - Generated by Claude
import sys
from anthropic import Anthropic

def analyze_logs(log_file):
    with open(log_file, 'r') as f:
        logs = f.read()

    client = Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

    response = client.messages.create(
        model="claude-sonnet-4-5-20250929",
        max_tokens=1024,
        messages=[{
            "role": "user",
            "content": f"""Analyze these application logs and identify:
1. Error patterns and root causes
2. Performance bottlenecks
3. Security concerns
4. Recommendations for fixes

Logs:
{logs[:10000]}  # First 10k chars
"""
        }]
    )

    print(response.content[0].text)

if __name__ == "__main__":
    analyze_logs(sys.argv[1])
```

**Usage:**
```bash
# Aggregate logs from all pods and analyze
kubectl logs -l app=petclinic --tail=1000 > /tmp/logs.txt
python log-analyzer.py /tmp/logs.txt
```

**Time Savings:** 90% (from hours to minutes for root cause analysis)

---

### Demo Your Pipeline

**5-10 Minute Demo Structure:**

**Slide 1: The Problem (30 seconds)**
```
BEFORE:
❌ Manual deployment: 2 hours
❌ No database migrations
❌ Zero observability
❌ Security risks (hardcoded secrets)
```

**Slide 2: The Solution (30 seconds)**
```
AFTER (AI-Generated):
✅ Automated CI/CD: 15 minutes, zero human intervention
✅ Flyway migrations: Safe schema evolution
✅ Prometheus + Grafana: Real-time metrics
✅ Kubernetes Secrets: Proper credential management
```

**Live Demo (6-7 minutes):**

1. **Code Push → Deployment (3 min)**
   ```bash
   # Make a simple code change
   echo "// Demo change" >> src/main/java/...

   git add .
   git commit -m "feat: demo deployment pipeline"
   git push origin main

   # Open GitHub Actions
   # Show: Build → Test → Deploy stages
   # Show: Coverage report, test results
   # Show: Container image pushed to registry
   ```

2. **Database Migration (1 min)**
   ```bash
   # Generate a migration
   ./mvnw flyway:info

   # Show: V5__add_appointment_reminder.sql
   # Show: Applied migrations, version tracking
   ```

3. **Monitoring Dashboard (2 min)**
   ```bash
   # Open Grafana: http://localhost:3000
   # Show:
   - Request rate (requests/sec)
   - Response times (p50, p95, p99)
   - Error rates
   - JVM memory usage
   - Database connection pool

   # Trigger some load:
   k6 run load-test.js

   # Watch metrics update in real-time
   ```

4. **Secrets Management (1 min)**
   ```bash
   # Show old way (k8s/db.yml):
   #   value: petclinic  ❌

   # Show new way:
   kubectl get secret db-credentials -o yaml
   # valueFrom: secretKeyRef ✅

   # Rotate secret:
   kubectl create secret generic db-credentials \
     --from-literal=password=NEW_PASSWORD \
     --dry-run=client -o yaml | kubectl apply -f -

   # Pods auto-restart with new secret
   ```

**Slide 3: Results & Metrics (1 min)**
```
TIME SAVINGS:
• Environment setup: 85% (3h → 20min)
• Deployment: 91% (2h → 10min)
• Schema changes: 70% (30min → 10min)
• Debugging: 80% (hours → minutes)

FRICTION REMOVED:
✅ One-command setup
✅ Automated testing in CI
✅ Zero-touch deployments
✅ Real-time observability

AI ACCELERATION:
• 19.5 hours of work → 1.7 hours
• Infrastructure code generated in minutes
• Monitoring dashboards auto-configured
```

**Q&A (1-2 min)**

---

## 4. Recommended Implementation Order

### Phase 1: Foundation (Week 1)
**Goal:** Unblock development team immediately

1. **CI/CD Pipeline (Priority: CRITICAL)**
   ```yaml
   Tasks:
   - [ ] Generate GitHub Actions workflow for Maven tests
   - [ ] Add JaCoCo coverage gate (≥90%)
   - [ ] Add multi-DB integration tests (MySQL, PostgreSQL)
   - [ ] Setup Checkstyle + Spring Format validation
   - [ ] Configure build artifact archiving

   AI Prompt:
   "Generate a GitHub Actions workflow that:
   1. Runs on every push and PR
   2. Executes ./mvnw test with JaCoCo
   3. Fails if coverage < 90%
   4. Runs integration tests with MySQL and PostgreSQL profiles
   5. Archives test reports and coverage data"
   ```

2. **Database Migrations (Priority: HIGH)**
   ```xml
   Tasks:
   - [ ] Add Flyway dependencies to pom.xml
   - [ ] Convert existing schema.sql to V1__initial_schema.sql
   - [ ] Generate V2, V3... for new changes
   - [ ] Add Flyway Maven plugin for CLI management
   - [ ] Test migrations on all DB profiles

   AI Prompt:
   "Convert these SQL schema files to Flyway migrations:
   - src/main/resources/db/postgres/schema.sql
   - src/main/resources/db/mysql/schema.sql
   Create versioned migration files (V1__, V2__, etc.) with proper
   idempotency and rollback scripts."
   ```

3. **Container Image Building (Priority: HIGH)**
   ```yaml
   Tasks:
   - [ ] Generate Dockerfile (multi-stage build)
   - [ ] Add image build to CI pipeline
   - [ ] Setup container registry (Docker Hub or GitHub Container Registry)
   - [ ] Configure image tagging strategy (git SHA + semantic version)
   - [ ] Add image scanning for vulnerabilities

   AI Prompt:
   "Create a multi-stage Dockerfile for this Spring Boot app that:
   1. Uses Maven to build the JAR
   2. Creates a minimal runtime image with Eclipse Temurin JRE 17
   3. Runs as non-root user
   4. Exposes port 8080
   5. Includes health check endpoint"
   ```

**Deliverables:**
- Working CI pipeline with test execution
- Database migration system operational
- Container images automatically built and pushed

**Time Estimate:** 2-3 days (with AI: ~4-6 hours)

---

### Phase 2: Production Deployment (Week 2)
**Goal:** Deploy to Kubernetes safely

4. **Kubernetes Manifests (Priority: HIGH)**
   ```yaml
   Tasks:
   - [ ] Generate proper ConfigMaps for env-specific config
   - [ ] Create Kubernetes Secrets (remove hardcoded credentials)
   - [ ] Add persistent volume claims for PostgreSQL
   - [ ] Configure Ingress for external access
   - [ ] Add resource limits and requests
   - [ ] Setup horizontal pod autoscaler
   - [ ] Add network policies and RBAC

   AI Prompt:
   "Generate production-ready Kubernetes manifests for:
   1. PostgreSQL StatefulSet with persistent storage
   2. Spring Boot Deployment with 3 replicas
   3. ConfigMap for application.properties
   4. Secrets for database credentials
   5. Service and Ingress for external access
   6. HPA for autoscaling based on CPU/memory
   Include proper health checks, resource limits, and security contexts."
   ```

5. **Helm Chart (Priority: MEDIUM)**
   ```yaml
   Tasks:
   - [ ] Create Helm chart structure
   - [ ] Parameterize Kubernetes manifests
   - [ ] Add values.yaml for dev, staging, prod
   - [ ] Generate Chart.yaml with metadata
   - [ ] Add templates for all resources
   - [ ] Test Helm install/upgrade/rollback

   AI Prompt:
   "Convert these Kubernetes manifests into a Helm chart:
   - Parameterize image tags, replicas, resources
   - Create values-dev.yaml, values-staging.yaml, values-prod.yaml
   - Add helpers for common labels and selectors
   - Include NOTES.txt with post-install instructions"
   ```

6. **Deployment Automation (Priority: HIGH)**
   ```yaml
   Tasks:
   - [ ] Add deployment stage to CI pipeline
   - [ ] Configure kubectl access in GitHub Actions
   - [ ] Implement blue-green deployment strategy
   - [ ] Add smoke tests after deployment
   - [ ] Setup auto-rollback on failure
   - [ ] Add approval gate for production

   AI Prompt:
   "Generate a GitHub Actions workflow that:
   1. Builds and pushes Docker image
   2. Deploys to staging namespace automatically
   3. Runs smoke tests (health checks, API calls)
   4. Waits for manual approval (if main branch)
   5. Deploys to production with blue-green strategy
   6. Monitors health for 5 minutes
   7. Rolls back if unhealthy"
   ```

**Deliverables:**
- Production-grade Kubernetes setup
- Helm chart for multi-environment deployment
- Automated deployment pipeline end-to-end

**Time Estimate:** 3-4 days (with AI: ~6-8 hours)

---

### Phase 3: Observability (Week 3)
**Goal:** Achieve full visibility into production

7. **Metrics Collection (Priority: HIGH)**
   ```xml
   Tasks:
   - [ ] Add Micrometer Prometheus dependency
   - [ ] Configure Prometheus server (Docker Compose or K8s)
   - [ ] Expose /actuator/prometheus endpoint
   - [ ] Add custom business metrics
   - [ ] Configure Prometheus scraping

   AI Prompt:
   "Add Prometheus metrics to this Spring Boot app:
   1. Add micrometer-registry-prometheus dependency
   2. Configure Prometheus scraping endpoint
   3. Add custom metrics for:
      - Pet registrations per hour
      - Visit bookings by vet
      - Failed login attempts
      - Database query times
   4. Generate prometheus.yml for scraping config"
   ```

8. **Grafana Dashboards (Priority: HIGH)**
   ```yaml
   Tasks:
   - [ ] Deploy Grafana (Docker Compose or K8s)
   - [ ] Configure Prometheus data source
   - [ ] Generate dashboards:
       • Application Overview (requests, errors, latency)
       • JVM Metrics (memory, GC, threads)
       • Database Performance (connections, query times)
       • Business Metrics (visits, owners, pets)
   - [ ] Export dashboards as JSON

   AI Prompt:
   "Create Grafana dashboards (JSON format) for:
   1. Application health: Request rate, error rate, p95 latency
   2. JVM metrics: Heap usage, GC pauses, thread count
   3. Database: Connection pool, slow queries, transaction times
   4. Business KPIs: New owners, visits per day, popular vets
   Use PromQL queries and proper visualization types."
   ```

9. **Centralized Logging (Priority: MEDIUM)**
   ```xml
   Tasks:
   - [ ] Add Logstash encoder dependency
   - [ ] Configure structured JSON logging
   - [ ] Deploy ELK stack (Elasticsearch, Logstash, Kibana)
   - [ ] Configure log shipping from pods
   - [ ] Create Kibana dashboards
   - [ ] Add correlation IDs for request tracing

   AI Prompt:
   "Setup ELK stack for this Spring Boot app:
   1. Add logstash-logback-encoder to pom.xml
   2. Configure logback-spring.xml for JSON logging
   3. Generate docker-compose-elk.yml with:
      - Elasticsearch 8.x
      - Logstash with input/filter/output config
      - Kibana for visualization
   4. Add correlation IDs to all log entries
   5. Create Logstash config to parse application logs"
   ```

**Deliverables:**
- Prometheus + Grafana monitoring stack
- Real-time metrics dashboards
- Centralized logging with ELK
- Correlation IDs for distributed tracing

**Time Estimate:** 2-3 days (with AI: ~4-6 hours)

---

### Phase 4: Security & Compliance (Week 4)
**Goal:** Production-ready security posture

10. **Secrets Management (Priority: CRITICAL)**
    ```yaml
    Tasks:
    - [ ] Remove hardcoded secrets from k8s/db.yml
    - [ ] Generate Kubernetes Secrets
    - [ ] Deploy HashiCorp Vault (or External Secrets Operator)
    - [ ] Configure app to read from Vault
    - [ ] Implement secret rotation policy
    - [ ] Add audit logging for secret access

    AI Prompt:
    "Implement Kubernetes Secrets + Vault integration:
    1. Convert hardcoded credentials to Kubernetes Secrets
    2. Generate External Secrets Operator manifests
    3. Configure Vault as secret backend
    4. Update deployment to use secretKeyRef
    5. Add init container for Vault token authentication
    6. Create secret rotation script (30-day cycle)"
    ```

11. **Security Scanning (Priority: HIGH)**
    ```yaml
    Tasks:
    - [ ] Add SAST scanning (SonarQube or Snyk)
    - [ ] Add dependency vulnerability checks (OWASP Dependency Check)
    - [ ] Add container image scanning (Trivy)
    - [ ] Add secrets detection (TruffleHog)
    - [ ] Configure security gates in CI

    AI Prompt:
    "Add security scanning to CI pipeline:
    1. Add Trivy container scan (fail on HIGH/CRITICAL)
    2. Add OWASP Dependency Check Maven plugin
    3. Add Snyk test for vulnerabilities
    4. Add GitGuardian or TruffleHog for secret detection
    5. Configure GitHub Security tab integration
    Generate GitHub Actions steps for each scanner."
    ```

12. **SSL/TLS & Network Security (Priority: MEDIUM)**
    ```yaml
    Tasks:
    - [ ] Configure Ingress with TLS termination
    - [ ] Generate or integrate Let's Encrypt certificates
    - [ ] Enforce HTTPS redirects
    - [ ] Add CORS configuration
    - [ ] Configure Kubernetes Network Policies
    - [ ] Restrict Actuator endpoints (authentication required)

    AI Prompt:
    "Setup production security:
    1. Generate Ingress manifest with TLS (cert-manager)
    2. Configure HTTPS-only access
    3. Add CORS filter for allowed origins
    4. Restrict /actuator/* endpoints to admin role
    5. Create NetworkPolicy to isolate database
    6. Generate security.yaml for Spring Security config"
    ```

**Deliverables:**
- Vault-backed secrets management
- Automated security scanning in CI
- TLS/HTTPS enforcement
- Network isolation and RBAC

**Time Estimate:** 3-4 days (with AI: ~6-8 hours)

---

## 5. Exit Criteria & Validation

### Module Complete Checklist

To exit Module 4.0, you must demonstrate:

- [ ] **Working Pipeline**
  - Code push triggers automated build, test, deploy
  - All tests pass (unit, integration, E2E)
  - Coverage gate enforced (≥90%)
  - Container image built and pushed to registry
  - Deployed to staging automatically
  - Production deployment with approval gate

- [ ] **Infrastructure as Code**
  - Kubernetes manifests (Deployment, Service, Ingress)
  - Helm chart for multi-environment deployment
  - ConfigMaps and Secrets properly configured
  - Persistent storage for database
  - Resource limits and autoscaling

- [ ] **Database Management**
  - Flyway migrations operational
  - Schema versioning and rollback tested
  - Migrations tested on all DB profiles (H2, MySQL, PostgreSQL)

- [ ] **Observability**
  - Prometheus metrics exported
  - Grafana dashboards created
  - Centralized logging (ELK or equivalent)
  - Alerting configured for critical issues

- [ ] **Security**
  - No hardcoded secrets (Kubernetes Secrets + Vault)
  - TLS/HTTPS enforced
  - Security scanning in CI (SAST, dependency check, image scan)
  - Network policies and RBAC configured

- [ ] **Friction Elimination**
  - Identified and fixed at least ONE major friction point
  - Measurable time savings demonstrated
  - Developer experience significantly improved

- [ ] **Documentation**
  - Deployment runbook
  - Monitoring guide
  - Incident response playbook
  - Architecture diagrams updated

### Acceptance Test: "The Demo"

**Can you demonstrate the following in under 15 minutes?**

1. **Zero-Touch Deployment:**
   - Push code to main branch
   - Show automated pipeline execution
   - Show successful deployment to staging
   - Show production deployment (with approval)

2. **Observability in Action:**
   - Open Grafana dashboards
   - Show real-time metrics updating
   - Trigger an error, show it in logs
   - Demonstrate root cause analysis

3. **Infrastructure Management:**
   - Deploy a configuration change via Helm
   - Rotate a secret (database password)
   - Scale application horizontally (HPA)
   - Rollback a deployment

4. **Developer Experience:**
   - New developer runs `./setup.sh`
   - Environment ready in < 5 minutes
   - Run tests, see coverage report
   - Deploy to personal namespace

**If you can do all of the above, Module 4.0 is COMPLETE.** ✅

---

## 6. AI Prompts Library

Quick reference prompts for each task:

### CI/CD Pipeline
```
"Generate a comprehensive GitHub Actions workflow for a Spring Boot app that:
- Runs unit tests with JaCoCo coverage
- Enforces 90% coverage threshold
- Runs integration tests with MySQL and PostgreSQL
- Builds a Docker image using spring-boot:build-image
- Pushes to GitHub Container Registry
- Deploys to Kubernetes staging namespace
- Runs smoke tests
- Requires manual approval for production
- Implements blue-green deployment
Include all necessary secrets and environment variables."
```

### Database Migrations
```
"Convert this SQL schema to Flyway migrations:
[paste schema.sql]

Requirements:
- Create V1__initial_schema.sql with all tables
- Ensure idempotency (IF NOT EXISTS, CREATE OR REPLACE)
- Add indexes for foreign keys
- Generate sample data migration (V2__seed_data.sql)
- Create rollback scripts
- Make it compatible with MySQL and PostgreSQL"
```

### Kubernetes Manifests
```
"Generate production-ready Kubernetes manifests for:
- PostgreSQL StatefulSet with 3 replicas
- PersistentVolumeClaim (10Gi)
- Spring Boot Deployment with HPA (2-10 pods)
- ConfigMap for application properties
- Secret for database credentials
- ClusterIP Service for database
- LoadBalancer Service for application
- Ingress with TLS (cert-manager)
- NetworkPolicy (deny-all, allow-specific)

Include:
- Resource limits (CPU: 500m-2, Memory: 512Mi-2Gi)
- Health checks (liveness, readiness, startup)
- Security context (non-root, read-only filesystem)
- Labels and annotations for monitoring"
```

### Monitoring Setup
```
"Setup Prometheus + Grafana monitoring:
1. Add micrometer-registry-prometheus to pom.xml
2. Configure /actuator/prometheus endpoint
3. Generate prometheus.yml scrape config
4. Create docker-compose.yml for Prometheus + Grafana
5. Generate 3 Grafana dashboards (JSON):
   - Application metrics (requests, errors, latency)
   - JVM metrics (heap, GC, threads)
   - Business metrics (visits/day, pets registered)
6. Add custom metrics for:
   - Visit booking rate
   - Conflict detection errors
   - Vet utilization percentage
Include PromQL queries and proper visualization types."
```

### Security Scanning
```
"Add comprehensive security scanning to CI:
1. Trivy container image scan (fail on HIGH/CRITICAL)
2. OWASP Dependency Check Maven plugin
3. Snyk vulnerability test
4. TruffleHog secret detection
5. Checkov infrastructure-as-code scan

Generate GitHub Actions workflow with:
- Parallel execution of all scanners
- Upload results to GitHub Security tab
- Fail pipeline on critical vulnerabilities
- Weekly scheduled scans (not just on push)"
```

---

## 7. Success Metrics

### Before Module 4.0:
```
Deployment Time:        2 hours (manual)
Environment Setup:      2-3 hours (manual, error-prone)
Schema Changes:         30 minutes (manual SQL editing)
Production Debugging:   Hours (no metrics, scattered logs)
Security Posture:       ❌ Hardcoded secrets, no scanning
Developer Onboarding:   1 day (manual setup, troubleshooting)
```

### After Module 4.0:
```
Deployment Time:        15 minutes (automated, zero-touch)
Environment Setup:      5 minutes (./setup.sh)
Schema Changes:         3 minutes (Flyway migration generation)
Production Debugging:   10 minutes (Grafana + centralized logs)
Security Posture:       ✅ Vault, automated scanning, TLS
Developer Onboarding:   30 minutes (one-command setup)
```

### Time Compression:
- **Pipeline work:** 19.5 hours → 1.7 hours (91% reduction)
- **Environment setup:** 85% faster
- **Deployment:** 88% faster
- **Debugging:** 90% faster

### Friction Removed:
- ❌ Manual database migrations → ✅ Automated Flyway
- ❌ Manual deployments → ✅ GitOps workflow
- ❌ No observability → ✅ Real-time dashboards
- ❌ Scattered logs → ✅ Centralized ELK stack
- ❌ Hardcoded secrets → ✅ Vault integration
- ❌ Manual testing → ✅ Automated CI gates

---

## 8. Next Steps

### Immediate Actions (Today):
1. ✅ Review this research document
2. 🎯 Choose Phase 1 tasks to start with
3. 🤖 Use AI prompts to generate CI/CD workflow
4. 🧪 Test generated code locally
5. 📝 Document what worked vs. what needed tweaking

### This Week:
- [ ] Complete Phase 1 (CI/CD + Migrations + Container Build)
- [ ] Run validation: `./mvnw verify` with all profiles
- [ ] Push to feature branch, verify CI executes
- [ ] Create demo recording of pipeline in action

### Next Week:
- [ ] Complete Phase 2 (Kubernetes + Helm + Deployment)
- [ ] Deploy to local Minikube or cloud K8s cluster
- [ ] Verify blue-green deployment works
- [ ] Document deployment runbook

### Capstone Preparation:
This module directly supports your final capstone by providing:
- Production deployment infrastructure
- Monitoring and observability stack
- Security and compliance foundation
- DevOps best practices demonstrated

**You're building not just an app, but a complete delivery platform.** 🚀

---

## Appendix: Tool Recommendations

### AI Code Generation:
- **Claude Code (CLI):** Primary tool for generating infrastructure code
- **GitHub Copilot:** Code completion for implementation details
- **ChatGPT Code Interpreter:** Quick prototyping of scripts

### CI/CD:
- **GitHub Actions:** Primary CI/CD (already in use)
- **ArgoCD:** GitOps-based Kubernetes deployment (advanced)
- **Flux:** Alternative GitOps tool

### Infrastructure as Code:
- **Kubernetes:** Container orchestration (already have manifests)
- **Helm:** Kubernetes package manager (next step)
- **Terraform:** Multi-cloud infrastructure (if expanding to AWS/GCP)
- **Pulumi:** Infrastructure as actual code (Python/TypeScript)

### Database Migrations:
- **Flyway:** SQL-based migrations (recommended for this project)
- **Liquibase:** XML/YAML migrations (more features, more complex)

### Monitoring:
- **Prometheus:** Metrics collection (industry standard)
- **Grafana:** Visualization (pairs with Prometheus)
- **ELK Stack:** Centralized logging (Elasticsearch, Logstash, Kibana)
- **Loki:** Lightweight log aggregation (Grafana ecosystem)

### Security:
- **Trivy:** Container image scanning (fast, accurate)
- **Snyk:** Dependency vulnerability scanning (free tier available)
- **OWASP Dependency Check:** Maven plugin for CVE detection
- **TruffleHog:** Secret detection in git history
- **HashiCorp Vault:** Enterprise secret management

### Testing:
- **k6:** Performance testing (already mentioned in docs)
- **Locust:** Python-based load testing
- **Apache JMeter:** GUI-based performance testing

### Local Development:
- **Tilt:** Local Kubernetes development (already in use)
- **Skaffold:** Continuous local development for K8s
- **Docker Compose:** Multi-container local environments (already in use)

---

**Document Version:** 1.0
**Last Updated:** 2026-02-25
**Author:** AI Research Agent (Claude Code)
**Next Review:** After Phase 1 completion
