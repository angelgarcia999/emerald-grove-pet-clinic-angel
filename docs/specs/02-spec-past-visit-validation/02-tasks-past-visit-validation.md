# 02-tasks-past-visit-validation.md

## Relevant Files

- `src/main/java/org/springframework/samples/petclinic/owner/Visit.java` - Entity class where `@FutureOrPresent` validation annotation will be added to the date field
- `src/test/java/org/springframework/samples/petclinic/model/ValidatorTests.java` - Existing validation test class where new visit date validation tests will be added
- `src/main/java/org/springframework/samples/petclinic/owner/VisitController.java` - Controller that processes visit form submissions (already has `@Valid` annotation, may need verification)
- `src/test/java/org/springframework/samples/petclinic/owner/VisitControllerTests.java` - Controller test class where new date validation tests will be added
- `src/main/resources/templates/pets/createOrUpdateVisitForm.html` - Thymeleaf template for visit form (no changes expected, validation errors display automatically)
- `e2e-tests/tests/features/visit-scheduling.spec.ts` - Playwright E2E test file where new date validation scenarios will be added
- `e2e-tests/pages/visit-page.ts` - Page object model for visit form (may need method to verify error messages)
- `docs/specs/02-spec-past-visit-validation/02-proof-past-visit-validation.md` - Proof document to be created with all completion artifacts
- `pom.xml` - Maven configuration (no changes needed, Jakarta Bean Validation already included)
- `target/site/jacoco/index.html` - JaCoCo coverage report (generated after running tests)

### Notes

- Unit tests should be added to existing test classes following the established naming pattern (`shouldNotValidateWhen...`)
- Use Maven for running tests: `./mvnw test -Dtest=ClassName` or `./mvnw clean test` for all tests
- Use npm for E2E tests: `cd e2e-tests && npm test -- visit-scheduling`
- Follow strict TDD methodology: Write failing tests (RED), implement minimum code to pass (GREEN), then refactor
- All commits must follow conventional commit format with co-authorship footer
- Maintain 90%+ test coverage requirement
- Jakarta Bean Validation `@FutureOrPresent` annotation validates that date is today or in the future

## Tasks

### [x] 1.0 Add Entity-Level Date Validation to Visit

Implement Bean Validation constraint on the Visit entity to prevent past dates. This task follows strict TDD methodology: write failing tests first (RED), implement the validation annotation (GREEN), then verify coverage and refactor if needed.

#### 1.0 Proof Artifact(s)

- **JUnit Test Output**: `./mvnw test -Dtest=ValidatorTests` (or new `VisitValidatorTests`) shows three new test cases passing:
  - `shouldNotValidateWhenVisitDateIsInPast()` demonstrates past date rejection
  - `shouldValidateWhenVisitDateIsToday()` demonstrates today's date acceptance
  - `shouldValidateWhenVisitDateIsFuture()` demonstrates future date acceptance
- **JaCoCo Coverage Report**: `target/site/jacoco/index.html` shows 100% coverage for Visit entity validation logic
- **Code Diff**: Visit.java shows `@FutureOrPresent` annotation (or custom constraint) added to date field with appropriate error message

#### 1.0 Tasks

- [ ] 1.1 **RED**: Write failing test `shouldNotValidateWhenVisitDateIsInPast()` in `ValidatorTests.java` that creates a Visit with a past date and asserts validation fails
- [ ] 1.2 **RED**: Write test `shouldValidateWhenVisitDateIsToday()` that creates a Visit with today's date and asserts validation passes
- [ ] 1.3 **RED**: Write test `shouldValidateWhenVisitDateIsFuture()` that creates a Visit with a future date and asserts validation passes
- [ ] 1.4 **RED**: Run tests with `./mvnw test -Dtest=ValidatorTests` and verify all three new tests fail (Visit has no validation yet)
- [ ] 1.5 **RED**: Commit failing tests with message: `test: add validation tests for visit date constraints`
- [ ] 1.6 **GREEN**: Add `@FutureOrPresent(message = "Visit date cannot be in the past")` annotation to `date` field in `Visit.java`
- [ ] 1.7 **GREEN**: Run tests with `./mvnw test -Dtest=ValidatorTests` and verify all three tests now pass
- [ ] 1.8 **GREEN**: Commit implementation with message: `feat: add past date validation to Visit entity`
- [ ] 1.9 **REFACTOR**: Generate coverage report with `./mvnw test jacoco:report` and verify 100% coverage for Visit validation
- [ ] 1.10 **REFACTOR**: Review code for any improvements (ensure error message is clear, annotation placement is correct)
- [ ] 1.11 **REFACTOR**: Commit any refactoring with message: `refactor: improve visit date validation implementation` (if applicable)

---

### [x] 2.0 Integrate Validation in Controller and Form

Update VisitController to properly handle validation errors and ensure the web form displays appropriate error messages. This task ensures the validation constraint is enforced during form submission and provides clear user feedback.

#### 2.0 Proof Artifact(s)

- **JUnit Test Output**: `./mvnw test -Dtest=VisitControllerTests` shows three new test cases passing:
  - `testProcessNewVisitFormWithPastDate()` demonstrates form returns with validation error
  - `testProcessNewVisitFormWithTodayDate()` demonstrates successful submission with today's date
  - `testProcessNewVisitFormWithFutureDate()` demonstrates successful submission with future date
- **Manual Form Test**: Screenshot of visit form showing validation error "Visit date cannot be in the past" displayed below date field when past date is submitted
- **Coverage Report**: JaCoCo report shows 90%+ coverage for modified controller code

#### 2.0 Tasks

- [ ] 2.1 **RED**: Write failing test `testProcessNewVisitFormWithPastDate()` in `VisitControllerTests.java` that submits a past date via POST and asserts status is OK (not redirect) and model has errors
- [ ] 2.2 **RED**: Write test `testProcessNewVisitFormWithTodayDate()` that submits today's date and asserts successful redirect to owner details page
- [ ] 2.3 **RED**: Write test `testProcessNewVisitFormWithFutureDate()` that submits a future date and asserts successful redirect
- [ ] 2.4 **RED**: Run tests with `./mvnw test -Dtest=VisitControllerTests` and verify new tests pass or fail appropriately
- [ ] 2.5 **RED**: Commit controller tests with message: `test: add controller tests for visit date validation`
- [ ] 2.6 **GREEN**: Verify `VisitController.processNewVisitForm()` method has `@Valid Visit visit` parameter (should already exist)
- [ ] 2.7 **GREEN**: Verify controller properly checks `result.hasErrors()` and returns form view on error (should already exist)
- [ ] 2.8 **GREEN**: Run tests with `./mvnw test -Dtest=VisitControllerTests` and verify all tests pass (validation should work automatically via `@Valid`)
- [ ] 2.9 **GREEN**: If tests fail, debug and fix controller validation handling, then commit with message: `fix: ensure visit controller handles date validation errors`
- [ ] 2.10 **REFACTOR**: Manually test the form by starting the app (`./mvnw spring-boot:run`) and navigating to a visit creation form
- [ ] 2.11 **REFACTOR**: Submit a past date and capture screenshot showing error message "Visit date cannot be in the past" displayed in the form
- [ ] 2.12 **REFACTOR**: Save screenshot to `docs/specs/02-spec-past-visit-validation/screenshots/visit-form-validation-error.png`
- [ ] 2.13 **REFACTOR**: Generate coverage report and verify 90%+ coverage for controller code
- [ ] 2.14 **REFACTOR**: Review and commit any improvements with message: `refactor: improve visit controller validation handling` (if applicable)

---

### [~] 3.0 Add End-to-End Test Coverage

Implement Playwright tests to validate the complete user journey from form display through submission and error feedback. This ensures the feature works correctly in a real browser environment.

#### 3.0 Proof Artifact(s)

- **Playwright Test Output**: `cd e2e-tests && npm test -- visit-scheduling` shows three new test cases passing:
  - `rejects visit with past date` demonstrates validation error is displayed in browser
  - `accepts visit with today date` demonstrates successful visit creation with today's date
  - `accepts visit with future date` demonstrates successful visit creation with future date
- **Screenshot**: `e2e-tests/test-results/artifacts/visit-scheduling-past-date-error.png` shows validation error message in real browser
- **Test Report**: `e2e-tests/test-results/html-report/index.html` shows all visit-scheduling tests passing

#### 3.0 Tasks

- [x] 3.1 **RED**: Add new test case `rejects visit with past date` in `e2e-tests/tests/features/visit-scheduling.spec.ts`
- [x] 3.2 **RED**: In the test, navigate to visit form (`/owners/1`), click "Add Visit", fill form with past date (e.g., `2020-01-01`), and description
- [x] 3.3 **RED**: Assert that after form submission, the page remains on the visit form (not redirected) and error message is visible
- [x] 3.4 **RED**: Use Playwright locator to find error text containing "Visit date cannot be in the past"
- [x] 3.5 **RED**: Add test case `accepts visit with today date` that submits today's date and verifies successful creation (visit appears in table)
- [x] 3.6 **RED**: Add test case `accepts visit with future date` that submits a future date and verifies successful creation
- [x] 3.7 **RED**: Run E2E tests with `cd e2e-tests && npm test -- visit-scheduling` and verify new tests pass (they should pass if previous tasks completed)
- [x] 3.8 **GREEN**: If tests fail, debug the issue (check form error display, Playwright selectors, timing issues) and fix
- [x] 3.9 **GREEN**: Commit E2E tests with message: `test(e2e): add visit date validation E2E tests`
- [x] 3.10 **REFACTOR**: Review E2E test code for clarity, reduce duplication if possible, improve test data setup
- [x] 3.11 **REFACTOR**: Add screenshot capture in the "rejects visit with past date" test using `await page.screenshot()` for proof artifact
- [x] 3.12 **REFACTOR**: Run E2E tests again and verify screenshot is saved to test results
- [x] 3.13 **REFACTOR**: Open HTML report (`e2e-tests/test-results/html-report/index.html`) and verify all visit-scheduling tests show as passing
- [x] 3.14 **REFACTOR**: Commit any refactoring with message: `refactor(e2e): improve visit date validation test coverage` (if applicable)

---

### [ ] 4.0 Documentation and Proof Artifact Collection

Collect and organize all proof artifacts, update documentation, and verify the feature is complete and ready for validation phase.

#### 4.0 Proof Artifact(s)

- **Proof Document**: `docs/specs/02-spec-past-visit-validation/02-proof-past-visit-validation.md` documents all completed work with links to artifacts
- **Coverage Matrix**: Document showing 1:1 mapping between spec requirements and implemented tests
- **Git History**: `git log --oneline` shows TDD commit sequence (test commits before implementation commits)
- **Full Test Suite**: `./mvnw clean test` shows all tests passing with no regressions
- **E2E Test Suite**: `cd e2e-tests && npm test` shows all E2E tests passing

#### 4.0 Tasks

- [ ] 4.1 Create proof document `docs/specs/02-spec-past-visit-validation/02-proof-past-visit-validation.md` with template structure
- [ ] 4.2 Add section "Unit Test Results" with output from `./mvnw test -Dtest=ValidatorTests` showing all tests passing
- [ ] 4.3 Add section "Controller Test Results" with output from `./mvnw test -Dtest=VisitControllerTests` showing all tests passing
- [ ] 4.4 Add section "E2E Test Results" with output from `cd e2e-tests && npm test -- visit-scheduling` showing all tests passing
- [ ] 4.5 Add section "Coverage Report" with link to JaCoCo report and summary of coverage percentages
- [ ] 4.6 Add section "Manual Testing" with screenshot from Task 2.12 showing validation error in browser
- [ ] 4.7 Add section "Code Changes" with git diff showing the `@FutureOrPresent` annotation added to Visit.java
- [ ] 4.8 Create "Coverage Matrix" table mapping each spec requirement to corresponding test(s) that verify it
- [ ] 4.9 Run `git log --oneline --all | head -20` and document the TDD commit sequence (test → implementation → refactor)
- [ ] 4.10 Add section "TDD Compliance" showing RED-GREEN-REFACTOR commit sequence for each task
- [ ] 4.11 Run full test suite with `./mvnw clean test` and verify no regressions (all tests pass)
- [ ] 4.12 Add section "Full Test Suite Results" with summary output showing total tests passed
- [ ] 4.13 Run full E2E suite with `cd e2e-tests && npm test` and verify no regressions
- [ ] 4.14 Add section "Full E2E Suite Results" with summary showing all tests passed
- [ ] 4.15 Review proof document for completeness, ensure all artifacts are linked and accessible
- [ ] 4.16 Commit proof document with message: `docs: add proof artifacts for visit date validation feature`
- [ ] 4.17 Final review: Verify spec requirements, functional requirements, and acceptance criteria are all met
