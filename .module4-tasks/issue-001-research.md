# Research: Issue #001 - Unit Tests in CI Pipeline

**Date:** 2026-02-25
**Issue:** Add unit tests to GitHub Actions CI
**Research Method:** Context7 + existing codebase analysis

---

## Current State Analysis

### Existing Workflows
The project already has GitHub Actions workflows:

1. **`.github/workflows/e2e-tests.yml`** - Runs Playwright E2E tests
2. **`.github/workflows/claude-code.yml`** - AI code review

**Key observations:**
- Uses `actions/checkout@v4`
- Uses `actions/setup-java@v4` with Java 17 (Temurin distribution)
- Uses `actions/upload-artifact@v4` with `if: always()` (best practice)
- Triggers on: `pull_request` and `push` to `main`
- Runs on: `ubuntu-latest`

**What's missing:** No unit or integration test execution!

---

## GitHub Actions Documentation (via Context7)

### 1. Java Setup with Maven Caching

**Source:** GitHub Actions docs - Building and testing Java with Maven

**Best practice:** Use `setup-java` with built-in Maven caching:

```yaml
- name: Set up JDK 17
  uses: actions/setup-java@v4
  with:
    java-version: '17'
    distribution: 'temurin'
    cache: maven  # ✅ Built-in Maven caching
```

**How it works:**
- Caches `~/.m2/repository` automatically
- Cache key based on `pom.xml` hash
- Invalidates cache when dependencies change
- Simpler than manual `actions/cache` setup

---

### 2. Running Maven Tests

**Source:** GitHub Actions docs

**Recommended command:**
```bash
mvn --batch-mode --update-snapshots verify
```

**Why `verify` instead of `test`?**
- `verify` runs tests + integration tests + additional checks
- `--batch-mode` disables interactive output (CI-friendly)
- `--update-snapshots` ensures latest snapshot dependencies

**For our case (unit tests only):**
```bash
./mvnw --batch-mode test
```
- Uses Maven wrapper (no Maven installation needed)
- `--batch-mode` disables interactive output (CI-friendly)
- Runs only unit tests (faster)
- Integration tests will be added in a future iteration (not in current 7 issues)

---

### 3. Uploading Test Artifacts

**Source:** GitHub Actions docs

**Pattern:**
```yaml
- run: mkdir staging && cp target/*.jar staging
- uses: actions/upload-artifact@v4
  with:
    name: Package
    path: staging
```

**For test reports:**
```yaml
- name: Upload test reports
  if: always()  # ✅ Run even if tests fail
  uses: actions/upload-artifact@v4
  with:
    name: test-reports
    path: target/surefire-reports/
```

**Key points:**
- `if: always()` ensures upload even on test failure
- Surefire reports: `target/surefire-reports/`
- JaCoCo coverage reports: Not included in current scope (artifacts upload skipped per user preference)

---

### 4. Maven Dependency Caching (Alternative)

**Source:** actions/cache documentation

**Manual caching option:**
```yaml
- name: Cache Maven dependencies
  uses: actions/cache@v4
  with:
    path: ~/.m2/repository
    key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
    restore-keys: |
      ${{ runner.os }}-maven-
```

**When to use:**
- More control over cache behavior
- Custom cache paths needed
- Multiple dependency files

**Recommendation for Issue #001:**
Use `setup-java` with `cache: maven` (simpler, officially supported)

---

## Workflow Design for Issue #001

### Triggers
```yaml
on:
  pull_request:  # Test all PRs
  push:
    branches:
      - main     # Test main branch
```

**Why this pattern?**
- Matches existing `e2e-tests.yml` (consistency)
- Tests every PR before merge
- Tests main branch after merge
- Doesn't test feature branches until PR (saves CI minutes)

---

### Job Structure

```yaml
jobs:
  unit-tests:
    runs-on: ubuntu-latest
    timeout-minutes: 10  # Fail if tests hang

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

      - name: Upload test reports
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: unit-test-reports
          path: target/surefire-reports/
```

**Why 10-minute timeout?**
- Unit tests should be fast (< 5 minutes expected)
- Prevents hung tests from wasting CI time
- Can adjust if legitimate tests take longer

---

## Expected Test Output Locations

### Surefire Reports (Unit Tests)
```
target/surefire-reports/
├── TEST-*.xml          # JUnit XML format
├── *.txt               # Text summaries
└── *.html              # HTML reports (if configured)
```

### JaCoCo Coverage (Future Enhancement)
```
target/site/jacoco/
├── index.html
├── jacoco.xml
└── jacoco.csv
```
**Note:** Code coverage enforcement not included in current 7-issue scope.

---

## Best Practices (from Context7 Research)

### 1. Always Use Batch Mode
```bash
./mvnw --batch-mode test
```
- Disables interactive prompts
- Cleaner CI output
- Prevents hanging

### 2. Upload Artifacts Even on Failure
```yaml
if: always()
```
- Critical for debugging failed tests
- See test reports even when tests fail

### 3. Cache Maven Dependencies
```yaml
cache: maven
```
- Speeds up subsequent runs (5-10 min → 1-2 min)
- No extra configuration needed with `setup-java`

### 4. Use Timeouts
```yaml
timeout-minutes: 10
```
- Prevents runaway jobs
- Saves CI credits

### 5. Consistent Action Versions
- Use same versions as existing workflows
- Currently: `@v4` for checkout, setup-java, upload-artifact

---

## Testing Strategy

### Local Test First
```bash
# Ensure tests pass locally
./mvnw --batch-mode test

# Check what will be uploaded
ls -la target/surefire-reports/
```

### Push to Feature Branch
```bash
git checkout -b ci/add-unit-tests
# Create workflow file
git add .github/workflows/maven-test.yml
git commit -m "ci: add unit tests to GitHub Actions"
git push origin ci/add-unit-tests
```

### Verify in GitHub Actions
1. Go to GitHub → Actions tab
2. Find "Unit Tests" workflow
3. Verify:
   - Tests executed
   - All tests passed
   - Artifacts uploaded
   - Completed in < 5 minutes

### Create Pull Request
- PR triggers workflow automatically
- Verify it runs on PR
- Check that status shows in PR

---

## Maven Wrapper Note

**Why `./mvnw` instead of `mvn`?**
- Maven wrapper included in project
- Ensures consistent Maven version
- No need to install Maven in CI
- Already used in local development

**Verify wrapper exists:**
```bash
ls -la mvnw
# Should see executable file
```

---

## Integration with Existing CI

### Current CI Flow
```
Push/PR
  ↓
E2E Tests (e2e-tests.yml)
  ↓
Claude Code Review (claude-code.yml)
```

### After Issue #001
```
Push/PR
  ↓
├─ Unit Tests (maven-test.yml) ← NEW
├─ E2E Tests (e2e-tests.yml)
└─ Claude Code Review (claude-code.yml)
```

All run in parallel! ✅

---

## Acceptance Criteria Verification

From Issue #001, verify each item:

- [ ] Workflow file at `.github/workflows/maven-test.yml` ✅
- [ ] Triggers on `push` and `pull_request` ✅
- [ ] Runs `./mvnw --batch-mode test` ✅
- [ ] Uses Java 17 ✅
- [ ] Caches Maven dependencies (`cache: maven`) ✅
- [ ] Fails build if tests fail (default Maven behavior) ✅
- [ ] Completes in < 5 minutes (add 10-min timeout as safety) ✅
- [ ] Uploads test reports (`target/surefire-reports/`) ✅

---

## Next Steps (Implementation)

1. **Create workflow file:** `.github/workflows/maven-test.yml`
2. **Use AI to generate:** Copy the workflow design above to Claude
3. **Test locally:** `./mvnw --batch-mode test` (ensure it works)
4. **Push to branch:** Create feature branch
5. **Verify in GitHub:** Check Actions tab
6. **Merge when green:** PR → main

---

## Resources Referenced

### Context7 Documentation
- GitHub Actions: `/websites/github_en_actions`
- Actions Cache: `/actions/cache`

### GitHub Actions Docs (via Context7)
- [Building and testing Java with Maven](https://docs.github.com/en/actions/automating-builds-and-tests/building-and-testing-java-with-maven)
- [Caching dependencies](https://docs.github.com/en/actions/automating-builds-and-tests/building-and-testing-java-with-maven#caching-dependencies)
- [Packaging workflow data as artifacts](https://docs.github.com/en/actions/tutorials/build-and-test-code/java-with-maven)

### Actions Marketplace
- [actions/checkout](https://github.com/actions/checkout)
- [actions/setup-java](https://github.com/actions/setup-java)
- [actions/upload-artifact](https://github.com/actions/upload-artifact)
- [actions/cache](https://github.com/actions/cache)

---

## Estimated Time

- **Research:** ✅ Complete (30 minutes)
- **Implementation:** 15 minutes (with AI-generated workflow)
- **Testing:** 15 minutes (push, verify, adjust)
- **Total:** ~1 hour (vs 2 hours without research)

**AI Acceleration:** 50% time savings

---

## Ready to Implement! 🚀

All research complete. Ready to generate the workflow file using the patterns and best practices identified above.
