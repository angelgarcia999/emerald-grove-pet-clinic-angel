# Issue #001: Add Unit Tests to CI Pipeline

**Status:** 🟡 Open
**Priority:** High
**Estimated Time:** 2 hours (with AI: 30 min)
**Phase:** 1 - CI/CD Pipeline
**Labels:** `ci/cd`, `testing`, `github-actions`

---

## Description

Currently, unit tests exist but don't run automatically in CI. Developers can push code without tests running, which creates risk of merging broken code.

**Goal:** Add GitHub Actions workflow that runs Maven unit tests on every push and pull request.

---

## Acceptance Criteria

- [ ] Workflow file created at `.github/workflows/maven-test.yml`
- [ ] Triggers on: `push` to all branches, `pull_request` events
- [ ] Runs: `./mvnw test`
- [ ] Uses: Java 17
- [ ] Caches: Maven dependencies (built-in with setup-java)
- [ ] Fails build if tests fail
- [ ] Completes in < 5 minutes
- [x] ~~Uploads test reports as artifacts~~ (Skipped - test results visible in logs)

---

## Module Alignment

**Deliverable:** Test output (CI screenshots)
**Exit Criteria:** Working pipeline that automatically tests

---

## Implementation Steps

1. Create workflow file
2. Test with AI-generated configuration
3. Push to feature branch
4. Verify workflow runs in GitHub Actions
5. Check that tests execute and pass
6. Merge to main when working

---

## AI Prompt

```
Generate a GitHub Actions workflow file that:
- Name: Unit Tests
- Runs on: push and pull_request events
- Job runs on: ubuntu-latest
- Timeout: 10 minutes
- Steps:
  1. Checkout code (actions/checkout@v4)
  2. Setup Java 17 Temurin with Maven caching (actions/setup-java@v4)
  3. Run ./mvnw test
- Keep it simple - no artifact uploads needed
```

---

## Related Issues

- **Blocks:** #002 (Integration tests need this foundation)
- **Blocks:** #003 (Coverage gate needs test execution)

---

## Notes

- Keep it simple - just unit tests for now
- Integration tests will be added in Issue #002
- This is the foundation for all other CI work

---

## Testing

```bash
# Test locally first:
./mvnw test

# After pushing, check:
# 1. Go to GitHub Actions tab
# 2. Verify workflow triggered
# 3. Check that tests ran
# 4. Review test results in workflow logs
```

---

## Definition of Done

- [ ] Code committed and pushed
- [ ] Workflow runs successfully in GitHub
- [ ] Tests execute and pass
- [ ] Test results visible in workflow logs (artifacts skipped)
- [ ] Documented in commit message
- [ ] This issue file updated with status: ✅ Closed
