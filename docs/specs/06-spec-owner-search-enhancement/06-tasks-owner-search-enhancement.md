# 06-tasks-owner-search-enhancement.md

## Relevant Files

### Java Source Files
- `src/main/java/org/springframework/samples/petclinic/owner/OwnerRepository.java` - Add multi-criteria search query method with optional parameters
- `src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java` - Update search handler to accept city/telephone params, add CSV export endpoint
- `src/main/java/org/springframework/samples/petclinic/owner/Owner.java` - Review entity (no changes expected, already has city/telephone fields)

### Test Files
- `src/test/java/org/springframework/samples/petclinic/service/ClinicServiceTests.java` - Add repository integration tests for multi-criteria search
- `src/test/java/org/springframework/samples/petclinic/owner/OwnerControllerTests.java` - Add controller tests for search and CSV export functionality

### Template Files
- `src/main/resources/templates/owners/findOwners.html` - Add city and telephone input fields to search form
- `src/main/resources/templates/owners/ownersList.html` - Add "Export to CSV" button, update pagination links to preserve all search params

### E2E Test Files
- `e2e-tests/tests/owner-search.spec.ts` - Create new E2E test suite for enhanced search and CSV export functionality

### Notes

- **Testing Strategy**: Follow strict TDD methodology (RED-GREEN-REFACTOR) for all tasks
- **Test Execution**:
  - Repository/Controller tests: `./mvnw test -Dtest=TestClassName`
  - All tests: `./mvnw test`
  - E2E tests: `cd e2e-tests && npm test -- owner-search`
  - Coverage: `./mvnw test jacoco:report` (report at `target/site/jacoco/index.html`)
- **Repository Pattern**: `ClinicServiceTests.java` is a `@DataJpaTest` that tests repository layer with in-memory H2 database
- **Code Organization**: Follow existing Spring Boot patterns (controller → repository, use Spring Data JPA query methods)
- **Validation Agents**: Run after implementation complete (tdd-enforcer, spring-boot-validator, architecture-compliance-checker, multi-db-test-runner)

## Tasks

### [ ] 1.0 Enhanced Search Repository Queries (TDD: Repository Layer)

**Purpose:** Implement database query methods that support searching owners by lastName, city, and telephone using AND logic with optional parameters. This provides the data access foundation for multi-criteria search.

**TDD Approach:** Write failing repository tests first (RED), implement query method to pass tests (GREEN), refactor for optimal query structure (REFACTOR).

#### 1.0 Proof Artifact(s)

- Test Output: `./mvnw test -Dtest=OwnerRepositoryTests` shows all repository tests passing (including new multi-criteria tests)
- JUnit Test: `OwnerRepositoryTests.shouldFindOwnersByLastNameCityAndTelephone()` demonstrates AND logic with all three criteria
- JUnit Test: `OwnerRepositoryTests.shouldFindOwnersByLastNameAndCity()` demonstrates two criteria search
- JUnit Test: `OwnerRepositoryTests.shouldFindOwnersByLastNameAndTelephone()` demonstrates two criteria search
- JUnit Test: `OwnerRepositoryTests.shouldIgnoreEmptySearchCriteria()` demonstrates empty fields are excluded from query
- JUnit Test: `OwnerRepositoryTests.shouldPerformCaseInsensitiveCityMatch()` demonstrates case-insensitive city matching
- Code: Repository method implementation in `src/main/java/org/springframework/samples/petclinic/owner/OwnerRepository.java`

#### 1.0 Tasks

- [ ] 1.1 **RED Phase**: Write failing repository test `shouldFindOwnersByLastNameCityAndTelephone()` that searches by all three criteria using AND logic
- [ ] 1.2 **RED Phase**: Write failing repository test `shouldFindOwnersByLastNameAndCity()` that searches by lastName and city only (telephone empty)
- [ ] 1.3 **RED Phase**: Write failing repository test `shouldFindOwnersByLastNameAndTelephone()` that searches by lastName and telephone only (city empty)
- [ ] 1.4 **RED Phase**: Write failing repository test `shouldIgnoreEmptySearchCriteria()` that verifies empty parameters are excluded from query
- [ ] 1.5 **RED Phase**: Write failing repository test `shouldPerformCaseInsensitiveCityMatch()` that verifies city matching is case-insensitive (e.g., "madison" matches "Madison")
- [ ] 1.6 **GREEN Phase**: Implement multi-criteria search method in `OwnerRepository.java` using `@Query` annotation with JPQL to handle optional parameters and make all tests pass
- [ ] 1.7 **REFACTOR Phase**: Review query implementation for optimization, ensure proper use of Spring Data JPA patterns, verify all tests still pass
- [ ] 1.8 Run `./mvnw test -Dtest=ClinicServiceTests` to verify all repository tests pass

---

### [ ] 2.0 Enhanced Search Controller Integration (TDD: Controller Layer)

**Purpose:** Update OwnerController to accept city and telephone as optional query parameters, invoke the enhanced repository query method, and handle multi-criteria search results including edge cases (no results, single result, multiple results).

**TDD Approach:** Write failing controller tests first (RED), implement controller logic to pass tests (GREEN), refactor for code clarity (REFACTOR).

#### 2.0 Proof Artifact(s)

- Test Output: `./mvnw test -Dtest=OwnerControllerTests` shows all controller tests passing (including new multi-criteria search tests)
- JUnit Test: `OwnerControllerTests.shouldFindOwnersByMultipleCriteria()` demonstrates controller accepts and processes city and telephone params
- JUnit Test: `OwnerControllerTests.shouldReturnNotFoundForNoResults()` demonstrates "not found" error when no owners match
- JUnit Test: `OwnerControllerTests.shouldRedirectWhenSingleOwnerFound()` demonstrates redirect to owner details for single match
- JUnit Test: `OwnerControllerTests.shouldShowPaginatedResultsForMultipleOwners()` demonstrates pagination with multiple criteria
- Code: Controller method updates in `src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java`

#### 2.0 Tasks

- [ ] 2.1 **RED Phase**: Write failing controller test `shouldFindOwnersByMultipleCriteria()` that sends request with lastName, city, and telephone parameters and expects paginated results
- [ ] 2.2 **RED Phase**: Write failing controller test `shouldReturnNotFoundForNoResults()` that verifies "not found" error when no owners match search criteria
- [ ] 2.3 **RED Phase**: Write failing controller test `shouldRedirectWhenSingleOwnerFound()` that verifies redirect to owner details page when exactly one owner matches
- [ ] 2.4 **RED Phase**: Write failing controller test `shouldShowPaginatedResultsForMultipleOwners()` that verifies pagination display when multiple owners match
- [ ] 2.5 **GREEN Phase**: Update `OwnerController.processFindForm()` method to accept `city` and `telephone` as `@RequestParam` parameters (with `defaultValue = ""`)
- [ ] 2.6 **GREEN Phase**: Create new private method `findPaginatedForMultipleCriteria()` that calls the enhanced repository query with all three search parameters
- [ ] 2.7 **GREEN Phase**: Update `processFindForm()` to call new search method and pass city/telephone to `addPaginationModel()` for URL generation
- [ ] 2.8 **GREEN Phase**: Update `addPaginationModel()` method to accept and preserve city and telephone parameters in model attributes
- [ ] 2.9 **REFACTOR Phase**: Extract search parameter handling logic if needed, ensure clean separation of concerns, verify all tests still pass
- [ ] 2.10 Run `./mvnw test -Dtest=OwnerControllerTests` to verify all controller tests pass

---

### [ ] 3.0 Search Form UI Enhancement (TDD: View Layer + E2E)

**Purpose:** Update the Find Owners form to include city and telephone input fields, update pagination links to preserve all search parameters, and validate the complete search flow with end-to-end tests.

**TDD Approach:** Write failing E2E tests first (RED), implement UI changes to pass tests (GREEN), refactor templates for maintainability (REFACTOR).

#### 3.0 Proof Artifact(s)

- Playwright Test: `e2e-tests/tests/owner-search.spec.ts` with test case `should search owners by multiple criteria` passes
- Playwright Test: `e2e-tests/tests/owner-search.spec.ts` with test case `should preserve search filters across pagination` passes
- Playwright Test: `e2e-tests/tests/owner-search.spec.ts` with test case `should handle empty search results gracefully` passes
- Test Output: `cd e2e-tests && npm test -- owner-search` shows all E2E search tests passing
- File Diff: Changes to `src/main/resources/templates/owners/findOwners.html` showing city and telephone fields
- File Diff: Changes to `src/main/resources/templates/owners/ownersList.html` showing updated pagination links with all params
- Screenshot: Browser showing Find Owners form with three fields (lastName, city, telephone)
- Screenshot: Browser showing search results with pagination preserving all search parameters in URL

#### 3.0 Tasks

- [ ] 3.1 **RED Phase**: Create `e2e-tests/tests/owner-search.spec.ts` with failing test `should search owners by multiple criteria` that fills lastName, city, telephone and verifies results
- [ ] 3.2 **RED Phase**: Add failing test `should preserve search filters across pagination` that verifies pagination links include all search parameters in URL
- [ ] 3.3 **RED Phase**: Add failing test `should handle empty search results gracefully` that verifies "not found" message when no results match
- [ ] 3.4 **GREEN Phase**: Update `findOwners.html` to add city input field below lastName field using same form-group structure
- [ ] 3.5 **GREEN Phase**: Update `findOwners.html` to add telephone input field below city field using same form-group structure
- [ ] 3.6 **GREEN Phase**: Ensure both new fields use `th:field="*{city}"` and `th:field="*{telephone}"` for proper model binding
- [ ] 3.7 **GREEN Phase**: Update `ownersList.html` pagination links to include `&city=${city}&telephone=${telephone}` parameters in href attributes (all pagination links: page numbers, first, previous, next, last)
- [ ] 3.8 **GREEN Phase**: Run E2E tests to verify they pass: `cd e2e-tests && npm test -- owner-search`
- [ ] 3.9 **REFACTOR Phase**: Review template code for consistency with existing patterns, ensure proper Thymeleaf attribute usage, verify all E2E tests still pass
- [ ] 3.10 Take screenshots of Find Owners form (with 3 fields) and search results page (with pagination preserving params) for proof artifacts

---

### [ ] 4.0 CSV Export Functionality (TDD: Full Stack)

**Purpose:** Implement CSV export capability that allows users to download the current page of search results as a CSV file. Add "Export to CSV" button to results page, implement CSV generation logic in controller, and validate with E2E tests.

**TDD Approach:** Write failing controller and E2E tests first (RED), implement CSV generation and download (GREEN), refactor for CSV injection prevention and code quality (REFACTOR).

#### 4.0 Proof Artifact(s)

- Test Output: `./mvnw test -Dtest=OwnerControllerTests` shows CSV export controller tests passing
- JUnit Test: `OwnerControllerTests.shouldExportCurrentPageToCSV()` demonstrates CSV generation with correct headers and data
- JUnit Test: `OwnerControllerTests.shouldEscapeCSVSpecialCharacters()` demonstrates CSV injection prevention
- JUnit Test: `OwnerControllerTests.shouldReturnErrorWhenExportingEmptyResults()` demonstrates empty results handling
- Playwright Test: `e2e-tests/tests/owner-search.spec.ts` with test case `should export search results to CSV` passes
- Test Output: `cd e2e-tests && npm test -- owner-search` shows CSV export E2E test passing
- File Diff: Changes to `src/main/resources/templates/owners/ownersList.html` showing "Export to CSV" button
- Code: CSV export endpoint implementation in `src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java`
- Sample CSV: Example `owners.csv` file showing correct format and escaped special characters

#### 4.0 Tasks

- [ ] 4.1 **RED Phase**: Write failing controller test `shouldExportCurrentPageToCSV()` that verifies CSV response with correct headers (Content-Type, Content-Disposition) and CSV content
- [ ] 4.2 **RED Phase**: Write failing controller test `shouldEscapeCSVSpecialCharacters()` that verifies CSV injection prevention for values starting with `=`, `+`, `@`, `-`
- [ ] 4.3 **RED Phase**: Write failing controller test `shouldReturnErrorWhenExportingEmptyResults()` that verifies error handling when trying to export with no results
- [ ] 4.4 **RED Phase**: Add failing E2E test `should export search results to CSV` in `owner-search.spec.ts` that clicks export button and verifies file download
- [ ] 4.5 **GREEN Phase**: Create new controller method `exportOwnersToCSV()` in `OwnerController.java` with `@GetMapping("/owners/export")` annotation
- [ ] 4.6 **GREEN Phase**: Implement CSV generation logic in controller method: create CSV string with headers and data rows from `listOwners` model attribute
- [ ] 4.7 **GREEN Phase**: Implement CSV injection prevention: prefix values starting with `=`, `+`, `@`, `-` with single quote `'`
- [ ] 4.8 **GREEN Phase**: Return `ResponseEntity<String>` with CSV content, `Content-Type: text/csv`, and `Content-Disposition: attachment; filename="owners.csv"`
- [ ] 4.9 **GREEN Phase**: Update `ownersList.html` to add "Export to CSV" button above the table that links to `/owners/export` endpoint with current search parameters
- [ ] 4.10 **GREEN Phase**: Conditionally display export button only when `listOwners` is not empty using `th:if="${not #lists.isEmpty(listOwners)}"`
- [ ] 4.11 **REFACTOR Phase**: Extract CSV generation logic to private helper method `generateCSV(List<Owner> owners)` if controller method becomes too long
- [ ] 4.12 **REFACTOR Phase**: Review CSV escaping logic for completeness and edge cases, ensure proper string formatting
- [ ] 4.13 Run `./mvnw test -Dtest=OwnerControllerTests` to verify CSV export tests pass
- [ ] 4.14 Run `cd e2e-tests && npm test -- owner-search` to verify E2E CSV export test passes
- [ ] 4.15 Generate sample `owners.csv` file by manually testing export feature and save as proof artifact

---

### [ ] 5.0 Validation and Proof Artifact Collection

**Purpose:** Run all validation agents (tdd-enforcer, spring-boot-validator, architecture-compliance-checker, multi-db-test-runner), collect proof artifacts, and ensure all quality gates pass before marking feature complete.

**TDD Approach:** This is the validation phase - all TDD cycles should be complete. This task verifies quality and collects evidence.

#### 5.0 Proof Artifact(s)

- Test Output: `./mvnw test` shows all unit and integration tests passing (100% pass rate)
- Test Output: `cd e2e-tests && npm test` shows all E2E tests passing (100% pass rate)
- Coverage Report: JaCoCo report at `target/site/jacoco/index.html` shows >90% line coverage for new code
- Agent Report: `tdd-enforcer` agent validation passes (RED-GREEN-REFACTOR methodology followed)
- Agent Report: `spring-boot-validator` agent validation passes (Spring Boot best practices followed)
- Agent Report: `architecture-compliance-checker` agent validation passes (layered architecture maintained)
- Agent Report: `multi-db-test-runner` agent validation passes (H2, MySQL, PostgreSQL compatibility verified)
- Commit: Git commit with message following conventional commits format

#### 5.0 Tasks

- [ ] 5.1 Run full test suite: `./mvnw test` and verify 100% pass rate
- [ ] 5.2 Run E2E test suite: `cd e2e-tests && npm test` and verify all tests pass
- [ ] 5.3 Generate coverage report: `./mvnw test jacoco:report` and verify >90% line coverage for new code at `target/site/jacoco/index.html`
- [ ] 5.4 Run `tdd-enforcer` validation agent to verify RED-GREEN-REFACTOR methodology was followed throughout implementation
- [ ] 5.5 Run `spring-boot-validator` validation agent to verify Spring Boot best practices (proper annotations, controller patterns, repository patterns)
- [ ] 5.6 Run `architecture-compliance-checker` validation agent to verify layered architecture (no controller → repository direct calls, proper separation of concerns)
- [ ] 5.7 Run `multi-db-test-runner` validation agent to verify compatibility across H2, MySQL, and PostgreSQL databases
- [ ] 5.8 Review all proof artifacts collected from Tasks 1.0-4.0 and ensure completeness
- [ ] 5.9 Create git commit with message following conventional commits format: `feat: add multi-criteria owner search and CSV export`
- [ ] 5.10 Update any relevant documentation (CHANGELOG.md, README.md) if needed

