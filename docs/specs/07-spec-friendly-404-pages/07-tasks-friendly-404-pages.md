# 07 Tasks - Friendly 404 Pages

## Overview

This task list implements user-friendly 404 error pages for missing owners and pets, replacing raw exception views with proper HTTP 404 responses and clear navigation options.

**Spec Reference:** `07-spec-friendly-404-pages.md`
**Issue:** #3 - Friendly 404s for missing owner/pet

## Relevant Files

### Files to Create (3 new files)

- `src/main/java/org/springframework/samples/petclinic/owner/OwnerNotFoundException.java` - Custom exception for missing owner scenarios, annotated with `@ResponseStatus(HttpStatus.NOT_FOUND)`
- `src/main/java/org/springframework/samples/petclinic/owner/PetNotFoundException.java` - Custom exception for missing pet scenarios, annotated with `@ResponseStatus(HttpStatus.NOT_FOUND)`
- `e2e-tests/tests/friendly-404.spec.ts` - Playwright E2E tests for owner and pet 404 scenarios

### Files to Modify (14 files)

#### Java Source Files
- `src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java` - Modify `findOwner()` method to throw `OwnerNotFoundException` instead of `IllegalArgumentException`
- `src/main/java/org/springframework/samples/petclinic/owner/PetController.java` - Modify `findPet()` method to throw `PetNotFoundException` instead of `IllegalArgumentException`

#### Template Files
- `src/main/resources/templates/error.html` - Add conditional "Find Owners" navigation link for 404 status codes

#### Internationalization Files (9 files)
- `src/main/resources/messages/messages.properties` - Add error.owner.notFound, error.pet.notFound, error.findOwners.link keys
- `src/main/resources/messages/messages_en.properties` - Add English translations
- `src/main/resources/messages/messages_de.properties` - Add German translations
- `src/main/resources/messages/messages_es.properties` - Add Spanish translations
- `src/main/resources/messages/messages_fa.properties` - Add Farsi translations
- `src/main/resources/messages/messages_ko.properties` - Add Korean translations
- `src/main/resources/messages/messages_pt.properties` - Add Portuguese translations
- `src/main/resources/messages/messages_ru.properties` - Add Russian translations
- `src/main/resources/messages/messages_tr.properties` - Add Turkish translations

#### Test Files
- `src/test/java/org/springframework/samples/petclinic/owner/OwnerControllerTests.java` - Add `testShowOwnerNotFound()` test method
- `src/test/java/org/springframework/samples/petclinic/owner/PetControllerTests.java` - Add `testShowPetNotFound()` and `testShowPetBelongsToDifferentOwner()` test methods

### Notes

- Follow strict TDD methodology: Write failing tests (RED) → Implement code (GREEN) → Refactor (REFACTOR)
- Use `@WebMvcTest` for controller unit tests with `@MockitoBean` for repository mocking
- Use MockMvc to simulate HTTP requests and assert 404 status codes
- Run tests with: `./mvnw test -Dtest=OwnerControllerTests` or `./mvnw test -Dtest=PetControllerTests`
- Run E2E tests with: `cd e2e-tests && npm test -- friendly-404.spec.ts`
- Generate coverage reports with: `./mvnw clean test jacoco:report`
- Use the i18n-sync-validator agent after adding message keys to ensure synchronization
- Follow existing code style and patterns in `OwnerController` and `PetController`

## Tasks

### [ ] 1.0 Implement Owner 404 Handling with TDD

Complete the TDD cycle (RED-GREEN-REFACTOR) for handling missing owner scenarios. Create a custom `OwnerNotFoundException`, modify the `OwnerController` to throw it when owners are not found, and write comprehensive unit tests to verify proper 404 behavior.

#### 1.0 Proof Artifact(s)

- **JUnit Test**: `OwnerControllerTests.testShowOwnerNotFound()` passes, asserting HTTP 404 status code, view name "error", and model contains error message with owner ID
- **Manual Test**: Navigate to `http://localhost:8080/owners/999999` and verify user-friendly error page displays with "Owner with ID 999999 was not found" message
- **Log Output**: Application logs show INFO level message: "Owner with ID 999999 not found" (not logged as ERROR or WARNING)
- **Coverage Report**: JaCoCo report shows >90% coverage for `OwnerNotFoundException` and modified `OwnerController.findOwner()` method

#### 1.0 Tasks

- [ ] 1.1 **[RED Phase]** Write failing unit test `testShowOwnerNotFound()` in `OwnerControllerTests.java` that mocks `OwnerRepository.findById()` to return empty Optional, performs GET request to `/owners/999999`, and asserts 404 status code and "error" view name
- [ ] 1.2 **[GREEN Phase]** Create `OwnerNotFoundException.java` class extending `RuntimeException`, annotated with `@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Owner not found")`, with constructor accepting owner ID
- [ ] 1.3 **[GREEN Phase]** Modify `OwnerController.findOwner()` method to throw `OwnerNotFoundException` instead of `IllegalArgumentException` when `owners.findById(ownerId)` returns empty Optional
- [ ] 1.4 **[GREEN Phase]** Add SLF4J logger to `OwnerController` and log at INFO level: "Owner with ID {} not found" when exception is thrown
- [ ] 1.5 **[GREEN Phase]** Run test and verify `testShowOwnerNotFound()` passes
- [ ] 1.6 **[REFACTOR Phase]** Review and refactor exception message format if needed, ensure consistency with existing error patterns
- [ ] 1.7 **[REFACTOR Phase]** Run all OwnerController tests to ensure no regressions: `./mvnw test -Dtest=OwnerControllerTests`
- [ ] 1.8 Verify test coverage with JaCoCo: `./mvnw clean test jacoco:report` and confirm >90% coverage for modified code
- [ ] 1.9 Manually test by starting app (`./mvnw spring-boot:run`) and navigating to `http://localhost:8080/owners/999999` to verify 404 page displays

---

### [ ] 2.0 Implement Pet 404 Handling with TDD

Complete the TDD cycle (RED-GREEN-REFACTOR) for handling missing pet scenarios, including the edge case where a pet exists but doesn't belong to the specified owner. Create a custom `PetNotFoundException`, modify the `PetController` to throw it appropriately, and write comprehensive unit tests.

#### 2.0 Proof Artifact(s)

- **JUnit Test**: `PetControllerTests.testShowPetNotFound()` and `PetControllerTests.testShowPetBelongsToDifferentOwner()` pass, asserting HTTP 404 status code for both scenarios
- **Manual Test**: Navigate to `http://localhost:8080/owners/1/pets/999999` and verify user-friendly error page displays with "Pet with ID 999999 was not found" message
- **Edge Case Test**: Navigate to a pet that exists but belongs to a different owner and verify 404 response
- **Log Output**: Application logs show INFO level messages for pet not found scenarios
- **Coverage Report**: JaCoCo report shows >90% coverage for `PetNotFoundException` and modified `PetController.findPet()` method

#### 2.0 Tasks

- [ ] 2.1 **[RED Phase]** Write failing unit test `testShowPetNotFound()` in `PetControllerTests.java` that mocks `PetTypeRepository.findAll()` to return pet types, mocks `OwnerRepository.findById()` to return owner with no pets, performs GET request to `/owners/1/pets/999999/edit`, and asserts 404 status code
- [ ] 2.2 **[RED Phase]** Write failing unit test `testShowPetBelongsToDifferentOwner()` that tests edge case where pet exists in database but doesn't belong to the specified owner (owner.getPet(petId) returns null)
- [ ] 2.3 **[GREEN Phase]** Create `PetNotFoundException.java` class extending `RuntimeException`, annotated with `@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Pet not found")`, with constructor accepting pet ID
- [ ] 2.4 **[GREEN Phase]** Modify `PetController.findPet()` method to throw `PetNotFoundException` instead of `IllegalArgumentException` when `owners.findById(ownerId)` returns empty Optional
- [ ] 2.5 **[GREEN Phase]** Modify `PetController.findPet()` method to throw `PetNotFoundException` when `owner.getPet(petId)` returns null (handles edge case where pet doesn't belong to owner)
- [ ] 2.6 **[GREEN Phase]** Add SLF4J logger to `PetController` and log at INFO level: "Pet with ID {} not found" when exception is thrown
- [ ] 2.7 **[GREEN Phase]** Run tests and verify both `testShowPetNotFound()` and `testShowPetBelongsToDifferentOwner()` pass
- [ ] 2.8 **[REFACTOR Phase]** Review and refactor exception handling logic, ensure both scenarios (pet doesn't exist, pet belongs to different owner) have clear error messages
- [ ] 2.9 **[REFACTOR Phase]** Run all PetController tests to ensure no regressions: `./mvnw test -Dtest=PetControllerTests`
- [ ] 2.10 Verify test coverage with JaCoCo and confirm >90% coverage for modified code
- [ ] 2.11 Manually test by navigating to `http://localhost:8080/owners/1/pets/999999` to verify 404 page displays

---

### [ ] 3.0 Enhance Error Template and Internationalization

Enhance the existing `error.html` template to display a "Find Owners" navigation link for 404 errors and add internationalized message keys to all 9 language files. Verify i18n synchronization using the i18n-sync-validator agent.

#### 3.0 Proof Artifact(s)

- **Template Enhancement**: Screenshot of 404 error page showing "Find Owners" link that navigates to `/owners/find` demonstrates user recovery path
- **i18n Validation**: i18n-sync-validator agent report shows PASS status with all keys present across 9 language files
- **I18nPropertiesSyncTest**: JUnit test `I18nPropertiesSyncTest` passes, confirming all message keys exist in all language files
- **Visual Verification**: Screenshots showing error messages in different languages (English, Spanish, German) demonstrate internationalization works

#### 3.0 Tasks

- [ ] 3.1 Enhance `src/main/resources/templates/error.html` to add a conditional section that displays a "Find Owners" link/button only when `status == 404` using Thymeleaf `th:if="${status == 404}"`
- [ ] 3.2 Add the "Find Owners" link to navigate to `/owners/find` with Thymeleaf syntax: `th:href="@{/owners/find}"`
- [ ] 3.3 Style the "Find Owners" link to match existing button styles in the application (use Bootstrap classes like `btn btn-primary`)
- [ ] 3.4 Add message key `error.owner.notFound=Owner with ID {0} was not found` to `messages.properties` (base file)
- [ ] 3.5 Add message key `error.pet.notFound=Pet with ID {0} was not found` to `messages.properties` (base file)
- [ ] 3.6 Add message key `error.findOwners.link=Find Owners` to `messages.properties` (base file)
- [ ] 3.7 Add the same 3 message keys to `messages_en.properties` with English translations
- [ ] 3.8 Add the same 3 message keys to `messages_de.properties`, `messages_es.properties`, `messages_fa.properties`, `messages_ko.properties`, `messages_pt.properties`, `messages_ru.properties`, `messages_tr.properties` (use placeholder translations or invoke i18n-sync-validator for assistance)
- [ ] 3.9 Run i18n-sync-validator agent to verify all keys exist across all 9 language files and generate any missing translations
- [ ] 3.10 Fix any issues reported by i18n-sync-validator agent
- [ ] 3.11 Run `I18nPropertiesSyncTest` to verify synchronization: `./mvnw test -Dtest=I18nPropertiesSyncTest`
- [ ] 3.12 Manually test error page by navigating to `http://localhost:8080/owners/999999` and verify "Find Owners" link appears and works
- [ ] 3.13 Take screenshots of error page showing the "Find Owners" link for proof artifacts

---

### [ ] 4.0 E2E Tests and Final Validation

Write comprehensive Playwright E2E tests to validate the complete user journey for 404 error handling, including navigation links and user-friendly messages. Run all validation agents to ensure TDD compliance, Spring Boot best practices, architecture compliance, and multi-database compatibility.

#### 4.0 Proof Artifact(s)

- **Playwright Test**: E2E test file `e2e-tests/tests/friendly-404.spec.ts` passes all test cases for owner and pet 404 scenarios
- **E2E Screenshot**: Screenshot from Playwright test showing 404 error page with friendly message and "Find Owners" link demonstrates complete flow
- **Navigation Test**: Playwright test verifies clicking "Find Owners" link navigates to `/owners/find` demonstrates recovery path works
- **TDD Compliance**: tdd-enforcer agent report shows PASS status with strict TDD methodology followed (RED-GREEN-REFACTOR)
- **Spring Boot Validation**: spring-boot-validator agent report shows PASS status with proper use of `@ResponseStatus` and exception handling
- **Architecture Compliance**: architecture-compliance-checker agent report shows PASS status with layered architecture maintained
- **Multi-DB Tests**: multi-db-test-runner agent report shows PASS status across H2, MySQL, and PostgreSQL

#### 4.0 Tasks

- [ ] 4.1 Create new Playwright E2E test file: `e2e-tests/tests/friendly-404.spec.ts`
- [ ] 4.2 Write E2E test "should display friendly 404 page for non-existent owner" that navigates to `/owners/999999` and asserts error page displays with friendly message
- [ ] 4.3 Write E2E test "should display owner ID in 404 error message" that verifies the message contains "Owner with ID 999999 was not found"
- [ ] 4.4 Write E2E test "should display Find Owners link on owner 404 page" that verifies link is present and visible
- [ ] 4.5 Write E2E test "should navigate to Find Owners page when link is clicked" that clicks the link and asserts navigation to `/owners/find`
- [ ] 4.6 Write E2E test "should display friendly 404 page for non-existent pet" that navigates to `/owners/1/pets/999999` and asserts error page displays
- [ ] 4.7 Write E2E test "should display pet ID in 404 error message" that verifies the message contains "Pet with ID 999999 was not found"
- [ ] 4.8 Write E2E test "should not display stack traces on 404 error pages" that asserts technical details are not visible
- [ ] 4.9 Run all E2E tests: `cd e2e-tests && npm test -- friendly-404.spec.ts`
- [ ] 4.10 Run test-temporal-coupling-detector agent on E2E tests to detect any hardcoded dates or brittle time logic
- [ ] 4.11 Fix any issues found by test-temporal-coupling-detector agent
- [ ] 4.12 Run tdd-enforcer agent to verify strict TDD compliance (RED-GREEN-REFACTOR cycle was followed)
- [ ] 4.13 Address any issues found by tdd-enforcer agent
- [ ] 4.14 Run spring-boot-validator agent to verify Spring Boot best practices (proper use of @ResponseStatus, exception handling)
- [ ] 4.15 Address any issues found by spring-boot-validator agent
- [ ] 4.16 Run architecture-compliance-checker agent to verify layered architecture is maintained
- [ ] 4.17 Address any issues found by architecture-compliance-checker agent
- [ ] 4.18 Run multi-db-test-runner agent to test across H2, MySQL, and PostgreSQL
- [ ] 4.19 Address any issues found by multi-db-test-runner agent
- [ ] 4.20 Collect all proof artifacts: screenshots, test results, agent reports, coverage reports
- [ ] 4.21 Create proof artifacts document in `docs/specs/07-spec-friendly-404-pages/07-proof-friendly-404-pages.md` summarizing all validation results
