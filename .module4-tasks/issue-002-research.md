# Issue #002 Research: Docker Build & Push to GHCR

**Research Date:** 2026-02-27
**Researched By:** Claude Sonnet 4.5 with Context7
**Focus:** GitHub Actions Docker workflows, GHCR authentication, caching strategies

---

## Executive Summary

This research covers building and pushing Docker images to GitHub Container Registry (GHCR) using GitHub Actions with:
- **Spring Boot Cloud Native Buildpacks** for OCI-compliant image creation
- **docker/build-push-action** for efficient CI/CD image builds
- **docker/metadata-action** for automated tag generation (SHA + latest)
- **Layer caching** strategies to minimize build times
- **GITHUB_TOKEN** authentication for seamless GHCR integration

**Key Finding:** Spring Boot's `spring-boot:build-image` Maven goal integrates directly with Cloud Native Buildpacks, eliminating the need for a Dockerfile while producing production-grade OCI images.

---

## Research Sources

1. **docker/build-push-action** (High Authority, 29 code snippets)
   - GitHub Action for building and pushing images with Buildx
   - Official Docker-maintained action with BuildKit integration

2. **docker/metadata-action** (High Authority, 37 code snippets)
   - Automatic tag generation from Git references
   - Handles SHA tagging, latest tag, and semantic versioning

3. **Spring Boot** (High Authority, 971+ code snippets)
   - Cloud Native Buildpacks integration
   - Maven plugin for building OCI images without Dockerfile

---

## Implementation Approaches

### Approach 1: Spring Boot Buildpacks (RECOMMENDED)

**Why Recommended:**
- ✅ No Dockerfile required
- ✅ Production-grade base images from Paketo Buildpacks
- ✅ Automatic security patches from buildpack updates
- ✅ Optimized layer caching
- ✅ Built-in Spring Boot optimizations

**Maven Command:**
```bash
./mvnw spring-boot:build-image \
  -Dspring-boot.build-image.imageName=ghcr.io/angelgarcia999/petclinic:latest \
  -Dspring-boot.build-image.publish=false
```

**CI Integration:**
```yaml
- name: Build image with Spring Boot Buildpacks
  run: |
    ./mvnw --batch-mode spring-boot:build-image \
      -Dspring-boot.build-image.imageName=ghcr.io/${{ github.repository }}:${{ github.sha }}
```

**Key Benefits:**
- Uses `paketobuildpacks/builder-noble-java-tiny:latest` (small footprint)
- Produces layered JARs for optimal caching
- Compatible with Cloud Foundry, Kubernetes, Docker

### Approach 2: Dockerfile with docker/build-push-action

**When to Use:**
- Custom base image requirements
- Non-Spring Boot projects
- Complex multi-stage builds

**Workflow:**
```yaml
- name: Build and push
  uses: docker/build-push-action@v6
  with:
    context: .
    push: true
    tags: ${{ steps.meta.outputs.tags }}
    cache-from: type=gha
    cache-to: type=gha,mode=max
```

---

## GitHub Container Registry (GHCR) Setup

### Authentication

**Method 1: GITHUB_TOKEN (Automatic)**
```yaml
- name: Login to GHCR
  uses: docker/login-action@v3
  with:
    registry: ghcr.io
    username: ${{ github.actor }}
    password: ${{ secrets.GITHUB_TOKEN }}
```

**Requirements:**
- Workflow must have `packages: write` permission
- Add to workflow or job level:
  ```yaml
  permissions:
    contents: read
    packages: write
  ```

**Method 2: Personal Access Token (PAT)**
- Use for external CI systems or more granular control
- Create PAT with `write:packages` and `read:packages` scopes
- Store as secret: `GHCR_TOKEN`

### Image Naming Convention

**Format:** `ghcr.io/<username>/<repository>:<tag>`

**Examples:**
- `ghcr.io/angelgarcia999/petclinic:main-abc1234` (SHA-tagged)
- `ghcr.io/angelgarcia999/petclinic:latest` (latest tag)
- `ghcr.io/angelgarcia999/emerald-grove-pet-clinic-angel:v1.2.3` (semantic version)

**Note:** Repository name must be lowercase. Use `docker/metadata-action` to auto-sanitize.

---

## Tag Generation Strategy

### Using docker/metadata-action (RECOMMENDED)

**Setup:**
```yaml
- name: Docker meta
  id: meta
  uses: docker/metadata-action@v5
  with:
    images: ghcr.io/${{ github.repository }}
    tags: |
      type=sha,prefix={{branch}}-,format=short
      type=raw,value=latest,enable={{is_default_branch}}
```

**Output Tags:**
- Push to `main` branch, SHA `abc1234`:
  - `ghcr.io/angelgarcia999/petclinic:main-abc1234`
  - `ghcr.io/angelgarcia999/petclinic:latest`
- Push to `feature/branch`, SHA `def5678`:
  - `ghcr.io/angelgarcia999/petclinic:feature-branch-def5678`

**Extended Options:**
```yaml
tags: |
  # SHA tag with branch prefix
  type=sha,prefix={{branch}}-,format=short

  # Latest tag (only on main branch)
  type=raw,value=latest,enable={{is_default_branch}}

  # Branch name tag
  type=ref,event=branch

  # PR tag
  type=ref,event=pr,prefix=pr-

  # Semver tags (if pushing tags)
  type=semver,pattern={{version}}
  type=semver,pattern={{major}}.{{minor}}
```

**Flavors (Global Tag Behavior):**
```yaml
flavor: |
  latest=auto          # Auto-generate latest tag on main
  prefix=petclinic-    # Global prefix
  suffix=-snapshot     # Global suffix
```

---

## Caching Strategies

### GitHub Actions Cache (RECOMMENDED)

**Setup:**
```yaml
- name: Build and push
  uses: docker/build-push-action@v6
  with:
    context: .
    push: true
    tags: ${{ steps.meta.outputs.tags }}
    cache-from: type=gha
    cache-to: type=gha,mode=max
```

**Benefits:**
- Built into GitHub Actions (no external dependencies)
- Free for public and private repos
- Automatic cache key management
- `mode=max` exports all layers (optimal for multi-stage builds)

### Cache Backends Comparison

| Backend | Speed | Retention | Cost | Best For |
|---------|-------|-----------|------|----------|
| GitHub Actions Cache | Fast | 7 days | Free | Default choice |
| Registry Cache | Medium | Indefinite | Storage costs | Long-term retention |
| Local Cache | Fastest | Per-runner | Free | Self-hosted runners |
| S3 Cache | Medium | Configurable | Storage costs | Multi-region |

### Spring Boot Buildpacks Caching

**Automatic:**
- Buildpacks cache layers between builds
- Dependencies cached in buildpack cache layer
- Application layer rebuilt only on code changes

**Maven Dependencies Cache:**
```yaml
- name: Set up JDK
  uses: actions/setup-java@v4
  with:
    distribution: temurin
    java-version: '17'
    cache: maven  # Caches ~/.m2/repository
```

**Combined Caching (Optimal):**
1. Maven dependency cache (via setup-java)
2. Buildpack layer cache (automatic)
3. Docker layer cache (if using docker/build-push-action)

---

## Complete Workflow Examples

### Example 1: Spring Boot Buildpacks + Manual Push

```yaml
name: Build and Push Docker Image

on:
  push:
    branches:
      - main
  pull_request:

permissions:
  contents: read
  packages: write

jobs:
  build-and-push:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
          cache: maven

      - name: Run tests
        run: ./mvnw --batch-mode test

      - name: Login to GHCR
        if: github.event_name != 'pull_request'
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Generate image tags
        id: meta
        uses: docker/metadata-action@v5
        with:
          images: ghcr.io/${{ github.repository }}
          tags: |
            type=sha,prefix={{branch}}-,format=short
            type=raw,value=latest,enable={{is_default_branch}}

      - name: Build image with Buildpacks
        run: |
          ./mvnw --batch-mode spring-boot:build-image \
            -Dspring-boot.build-image.imageName=ghcr.io/${{ github.repository }}:temp

      - name: Tag and push images
        if: github.event_name != 'pull_request'
        run: |
          TAGS="${{ steps.meta.outputs.tags }}"
          for TAG in $TAGS; do
            docker tag ghcr.io/${{ github.repository }}:temp $TAG
            docker push $TAG
          done
```

### Example 2: docker/build-push-action with Dockerfile

```yaml
name: Build and Push Docker Image

on:
  push:
    branches:
      - main
  pull_request:

permissions:
  contents: read
  packages: write

jobs:
  build-and-push:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
          cache: maven

      - name: Run tests
        run: ./mvnw --batch-mode test

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Login to GHCR
        if: github.event_name != 'pull_request'
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Generate image tags
        id: meta
        uses: docker/metadata-action@v5
        with:
          images: ghcr.io/${{ github.repository }}
          tags: |
            type=sha,prefix={{branch}}-,format=short
            type=raw,value=latest,enable={{is_default_branch}}

      - name: Build and push
        uses: docker/build-push-action@v6
        with:
          context: .
          push: ${{ github.event_name != 'pull_request' }}
          tags: ${{ steps.meta.outputs.tags }}
          labels: ${{ steps.meta.outputs.labels }}
          cache-from: type=gha
          cache-to: type=gha,mode=max
```

### Example 3: Hybrid Approach (Current Project)

**Extends existing maven-test.yml:**
```yaml
name: CI Pipeline

on:
  push:
    branches:
      - main
  pull_request:

permissions:
  contents: read
  packages: write

jobs:
  test:
    runs-on: ubuntu-latest
    timeout-minutes: 10

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
          cache: maven

      - name: Run unit tests
        run: ./mvnw --batch-mode test

  build-image:
    needs: test
    runs-on: ubuntu-latest
    if: github.event_name != 'pull_request'

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
          cache: maven

      - name: Login to GHCR
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Generate tags
        id: meta
        uses: docker/metadata-action@v5
        with:
          images: ghcr.io/${{ github.repository }}
          tags: |
            type=sha,prefix=main-
            type=raw,value=latest

      - name: Build and push with Buildpacks
        run: |
          # Build image with first tag
          FIRST_TAG=$(echo "${{ steps.meta.outputs.tags }}" | head -n1)
          ./mvnw --batch-mode spring-boot:build-image \
            -Dspring-boot.build-image.imageName=$FIRST_TAG \
            -Dspring-boot.build-image.publish=true

          # Tag with additional tags
          for TAG in ${{ steps.meta.outputs.tags }}; do
            if [ "$TAG" != "$FIRST_TAG" ]; then
              docker tag $FIRST_TAG $TAG
              docker push $TAG
            fi
          done
```

---

## Security Best Practices

### 1. Pin Action Versions to SHA

**Before:**
```yaml
uses: docker/login-action@v3
```

**After:**
```yaml
uses: docker/login-action@e92390f5fb421da1463c202d546fed0ec5c39f20  # v3.1.0
```

### 2. Scope Permissions Minimally

```yaml
permissions:
  contents: read       # Read code only
  packages: write      # Write to GHCR only
  # Do NOT use:
  # contents: write    # Not needed for building images
  # actions: write     # Not needed
```

### 3. Conditional Push (Don't Push on PRs)

```yaml
- name: Build and push
  if: github.event_name != 'pull_request'
  # ... push logic
```

### 4. Scan Images for Vulnerabilities

```yaml
- name: Scan image
  uses: aquasecurity/trivy-action@master
  with:
    image-ref: ghcr.io/${{ github.repository }}:${{ github.sha }}
    format: sarif
    output: trivy-results.sarif
```

### 5. Package Visibility

**After first push:**
1. Go to GitHub Package page
2. Navigate to package settings
3. Change visibility to Public (for public repos)
4. Link to repository for automatic cleanup

---

## Performance Optimization

### Build Time Targets

| Metric | Target | Current Baseline |
|--------|--------|------------------|
| Clean build | < 5 min | TBD |
| Cached build | < 2 min | TBD |
| Maven tests | < 1 min | ~14 sec (Issue #001) |
| Image size | < 200 MB | TBD |

### Optimization Strategies

**1. Layer Caching:**
- Use GitHub Actions cache for Docker layers
- Cache Maven dependencies with setup-java
- Buildpacks automatically cache dependency layers

**2. Parallel Jobs:**
- Run tests and image builds in parallel (if tests not required first)
- Use matrix builds for multi-platform images (if needed)

**3. Build Only on Merge:**
- Build images only on push to main (not on PRs)
- Reduces CI minutes consumption

**4. Multi-stage Builds (if using Dockerfile):**
```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline  # Cache dependencies
COPY src ./src
RUN mvn package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## Testing the Implementation

### 1. Verify Image Pushed to GHCR

```bash
# Check GitHub Packages page
open https://github.com/users/angelgarcia999/packages/container/package/emerald-grove-pet-clinic-angel

# Or via API
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
  https://ghcr.io/v2/angelgarcia999/emerald-grove-pet-clinic-angel/tags/list
```

### 2. Pull and Run Image Locally

```bash
# Pull image
docker pull ghcr.io/angelgarcia999/emerald-grove-pet-clinic-angel:latest

# Run with H2 (in-memory database)
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=h2 \
  ghcr.io/angelgarcia999/emerald-grove-pet-clinic-angel:latest

# Test in browser
open http://localhost:8080

# Check logs
docker logs -f <container-id>

# Stop container
docker stop <container-id>
```

### 3. Verify Image Metadata

```bash
# Inspect image
docker inspect ghcr.io/angelgarcia999/emerald-grove-pet-clinic-angel:latest

# Check labels
docker inspect --format='{{json .Config.Labels}}' \
  ghcr.io/angelgarcia999/emerald-grove-pet-clinic-angel:latest | jq

# Check size
docker images ghcr.io/angelgarcia999/emerald-grove-pet-clinic-angel
```

### 4. Verify Tags

```bash
# List all tags
docker pull ghcr.io/angelgarcia999/emerald-grove-pet-clinic-angel:main-abc1234
docker pull ghcr.io/angelgarcia999/emerald-grove-pet-clinic-angel:latest

# Both should reference the same image SHA
docker images --digests | grep emerald-grove-pet-clinic-angel
```

---

## Troubleshooting Guide

### Issue: "403 Forbidden" when pushing to GHCR

**Cause:** Missing `packages: write` permission

**Fix:**
```yaml
permissions:
  contents: read
  packages: write  # Add this
```

### Issue: Image build succeeds but push fails

**Cause:** Not logged in to GHCR

**Fix:**
```yaml
- name: Login to GHCR
  uses: docker/login-action@v3
  with:
    registry: ghcr.io
    username: ${{ github.actor }}
    password: ${{ secrets.GITHUB_TOKEN }}
```

### Issue: "repository name must be lowercase"

**Cause:** Repository name contains uppercase letters

**Fix:** Use `docker/metadata-action` which auto-sanitizes:
```yaml
- name: Docker meta
  id: meta
  uses: docker/metadata-action@v5
  with:
    images: ghcr.io/${{ github.repository }}  # Auto-lowercased
```

### Issue: Build is slow (> 5 minutes)

**Causes:**
- No caching enabled
- Rebuilding dependencies every time
- No layer reuse

**Fixes:**
1. Enable Maven cache in setup-java
2. Use GitHub Actions cache for Docker layers
3. Use Spring Boot Buildpacks (automatic layer optimization)

### Issue: Cannot pull image publicly

**Cause:** Package visibility is private by default

**Fix:**
1. Go to https://github.com/users/angelgarcia999/packages
2. Click on the package
3. Package settings → Change visibility to Public
4. Link to repository

---

## Recommended Implementation Plan

### Phase 1: Extend Existing Workflow (RECOMMENDED)

**File:** `.github/workflows/maven-test.yml`

1. Add `packages: write` permission
2. Add `build-image` job that depends on `unit-tests`
3. Use Spring Boot Buildpacks with `docker/metadata-action`
4. Only build on push to main (not PRs)

**Advantages:**
- Minimal changes to existing workflow
- Reuses existing test job
- Clear separation of concerns
- Easy to disable image building if needed

### Phase 2: Separate Workflow (Alternative)

**File:** `.github/workflows/docker-build.yml`

1. Create dedicated workflow for Docker builds
2. Trigger on push to main only
3. Include tests as prerequisite step

**Advantages:**
- Cleaner separation
- Can trigger independently
- Easier to customize build process

---

## Next Steps

1. **Review Issue #002 acceptance criteria** against research findings
2. **Choose implementation approach** (Spring Boot Buildpacks recommended)
3. **Decide on workflow structure** (extend existing vs. separate)
4. **Implement workflow changes** with Context7 guidance
5. **Test locally** before pushing to CI
6. **Verify GHCR push** and image accessibility
7. **Update Issue #002 status** and mark complete

---

**Research Completed:** 2026-02-27
**Ready for Implementation:** ✅ Yes
**Recommended Approach:** Spring Boot Buildpacks + docker/metadata-action + GitHub Actions Cache
