# Issue #002: Build & Push Docker Images in CI

**Status:** 🟡 Open
**Priority:** High
**Estimated Time:** 3 hours (with AI: 1 hour)
**Phase:** 1 - CI/CD Pipeline

---

## Description

After tests pass in CI, automatically build a Docker image and push it to GitHub Container Registry. This creates a deployable artifact for every successful build.

**Current state:** Images built locally with `./mvnw spring-boot:build-image`
**Target state:** Images built in CI and pushed to registry automatically

---

## Acceptance Criteria

- [ ] Enhances existing GitHub Actions workflow (from Issue #001)
- [ ] Builds Docker image after tests pass
- [ ] Tags image with git SHA: `ghcr.io/angelgarcia999/petclinic:main-abc123`
- [ ] Also tags with `latest` if on main branch
- [ ] Pushes to GitHub Container Registry (ghcr.io)
- [ ] Image build takes < 5 minutes
- [ ] Can pull image: `docker pull ghcr.io/angelgarcia999/petclinic:latest`

---

## AI Prompt

```
Add Docker build and push steps to GitHub Actions workflow:

1. Add workflow permissions (at workflow or job level):
   permissions:
     contents: read
     packages: write

2. After tests pass, build image:
   - Use docker/build-push-action@v5
   - Build from Dockerfile (or use spring-boot:build-image)
   - Tag with: ghcr.io/angelgarcia999/petclinic:${{ github.sha }}
   - Tag with: ghcr.io/angelgarcia999/petclinic:latest (if main branch)

3. Login to GitHub Container Registry:
   - Use docker/login-action@v3
   - Registry: ghcr.io
   - Username: ${{ github.actor }}
   - Password: ${{ secrets.GITHUB_TOKEN }}

4. Push image to registry

Include caching for faster builds.

IMPORTANT: Workflow must have 'packages: write' permission for GHCR push to succeed.
```

---

## Related Issues

- **Depends on:** #001 (tests must pass first)
- **Blocks:** #005 (Container App needs this image)
- **Blocks:** #006 (deployment needs deployable image)

---

## Testing

```bash
# After workflow runs, pull the image
docker pull ghcr.io/angelgarcia999/petclinic:latest

# Run it locally
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=h2 \
  ghcr.io/angelgarcia999/petclinic:latest

# Test in browser
open http://localhost:8080
```

---

## Notes

- **Registry:** GitHub Container Registry is free for public repos
- **Authentication:** GITHUB_TOKEN is automatically available in Actions
- **Visibility:** Make package public in GitHub settings after first push
- **Caching:** Use Docker layer caching to speed up builds

---

## Definition of Done

- [ ] Workflow builds and pushes image
- [ ] Image visible in GitHub Packages
- [ ] Can pull and run image locally
- [ ] Image tagged with both SHA and latest
- [ ] Issue marked: ✅ Closed
