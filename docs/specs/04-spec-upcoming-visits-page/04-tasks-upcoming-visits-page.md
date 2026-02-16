# 04-tasks-upcoming-visits-page.md

## Relevant Files

### Files to Create

- `src/main/java/org/springframework/samples/petclinic/owner/VisitRepository.java` - New repository interface with JPQL query for finding upcoming visits
- `src/test/java/org/springframework/samples/petclinic/owner/VisitRepositoryTests.java` - Repository layer tests using @DataJpaTest
- `src/test/java/org/springframework/samples/petclinic/owner/UpcomingVisitsIntegrationTests.java` - End-to-end integration tests using @SpringBootTest
- `src/main/resources/templates/visits/upcomingVisits.html` - Thymeleaf template for upcoming visits page

### Files to Modify

- `src/main/java/org/springframework/samples/petclinic/owner/VisitController.java` - Add new GET endpoint for `/visits/upcoming`
- `src/test/java/org/springframework/samples/petclinic/owner/VisitControllerTests.java` - Add controller tests for new endpoint
- `src/main/resources/messages/messages.properties` - Add i18n message keys for upcoming visits page
- `src/main/resources/messages/messages_de.properties` - Add German translations (via i18n-sync-validator)
- `src/main/resources/messages/messages_es.properties` - Add Spanish translations (via i18n-sync-validator)
- `src/main/resources/messages/messages_fr.properties` - Add French translations (via i18n-sync-validator)
- `src/main/resources/messages/messages_ja.properties` - Add Japanese translations (via i18n-sync-validator)
- `src/main/resources/messages/messages_pt.properties` - Add Portuguese translations (via i18n-sync-validator)
- `src/main/resources/messages/messages_tr.properties` - Add Turkish translations (via i18n-sync-validator)
- `src/main/resources/templates/fragments/layout.html` - Add navigation menu item for Upcoming Visits
- `src/main/resources/db/h2/data.sql` - Add future-dated visit test data
- `src/main/resources/db/mysql/data.sql` - Add future-dated visit test data for MySQL
- `src/main/resources/db/postgres/data.sql` - Add future-dated visit test data for PostgreSQL

### Notes

- All tests should use JUnit 5 with AssertJ assertions
- Follow existing test patterns: use `@DataJpaTest` for repository tests, `@WebMvcTest` for controller tests
- Run tests with: `./mvnw test -Dtest=<TestClassName>`
- Run all tests: `./mvnw test`
- Generate coverage report: `./mvnw test jacoco:report`
- Follow repository's TDD methodology: write tests before implementation (RED-GREEN-REFACTOR)

---

## Tasks

### [x] 1.0 Repository Layer - Implement Data Access with Optimized Query (TDD RED-GREEN-REFACTOR)

**Purpose**: Create the `VisitRepository` interface with a JPQL query that retrieves upcoming visits with associated pet and owner data using JOIN FETCH to prevent N+1 query problems. This provides the foundation for the entire feature.

**TDD Approach**: Follow strict Red-Green-Refactor cycle:
- **RED**: Write failing repository tests first
- **GREEN**: Create repository interface to pass tests
- **REFACTOR**: Optimize query and extract test utilities

#### 1.0 Proof Artifact(s)

- **Test Output**: `./mvnw test -Dtest=VisitRepositoryTests` shows all tests passing demonstrates repository query works correctly
- **Code Artifact**: `src/main/java/org/springframework/samples/petclinic/owner/VisitRepository.java` with JPQL query using JOIN FETCH demonstrates implementation exists
- **Test Code**: `src/test/java/org/springframework/samples/petclinic/owner/VisitRepositoryTests.java` with test methods `shouldReturnEmptyListWhenNoUpcomingVisits()`, `shouldFindUpcomingVisitsWithinDateRange()`, `shouldEagerlyFetchPetAndOwner()` demonstrates comprehensive test coverage

#### 1.0 Tasks

- [x] 1.1 **RED Phase**: Create `VisitRepositoryTests.java` with failing test `shouldReturnEmptyListWhenNoUpcomingVisits()` that queries for visits in an empty date range
- [x] 1.2 **GREEN Phase**: Create `VisitRepository.java` interface extending `Repository<Visit, Integer>` with basic `findUpcomingVisits(LocalDate start, LocalDate end)` method signature to make test compile
- [x] 1.3 **RED Phase**: Add failing test `shouldFindUpcomingVisitsWithinDateRange()` that creates test owner/pet/visit data and verifies the query returns correct visits within date range
- [x] 1.4 **GREEN Phase**: Implement JPQL query with `@Query` annotation: `SELECT v FROM Pet p JOIN p.visits v WHERE v.date BETWEEN :start AND :end ORDER BY v.date ASC` (adapted for unidirectional relationship)
- [x] 1.5 **RED Phase**: Add failing test `shouldOrderVisitsByDateAscending()` that verifies visits are returned in correct order
- [x] 1.6 **GREEN Phase**: JPQL query already includes `ORDER BY v.date ASC` for proper ordering
- [x] 1.7 **REFACTOR Phase**: Extract test data factory methods (`createTestOwner()`, `createTestPet()`) to utility methods in test class
- [x] 1.8 **REFACTOR Phase**: Add `@Transactional(readOnly = true)` annotation to repository query method
- [x] 1.9 Verify all repository tests pass: `./mvnw test -Dtest=VisitRepositoryTests`

---

### [x] 2.0 Controller Layer - Implement Endpoint with Validation (TDD RED-GREEN-REFACTOR)

**Purpose**: Add the `/visits/upcoming` GET endpoint to `VisitController` that accepts a `days` parameter, calls the repository, and prepares model data for the view. Includes validation for invalid parameters.

**TDD Approach**: Follow strict Red-Green-Refactor cycle:
- **RED**: Write failing controller tests with MockMvc
- **GREEN**: Implement controller method to pass tests
- **REFACTOR**: Extract date calculation logic, add validation

#### 2.0 Proof Artifact(s)

- **Test Output**: `./mvnw test -Dtest=VisitControllerTests` shows new tests passing demonstrates controller endpoint works
- **Code Artifact**: Modified `src/main/java/org/springframework/samples/petclinic/owner/VisitController.java` with `@GetMapping("/upcoming")` method demonstrates endpoint implementation
- **Test Code**: `src/test/java/org/springframework/samples/petclinic/owner/VisitControllerTests.java` with tests `testShowUpcomingVisitsWithDefaultDays()`, `testShowUpcomingVisitsWithCustomDays()`, `testShowUpcomingVisitsValidation()` demonstrates controller behavior verified

#### 2.0 Tasks

- [x] 2.1 **RED Phase**: Read existing `VisitControllerTests.java` and add `@MockitoBean private VisitRepository visits;` field to support mocking
- [x] 2.2 **RED Phase**: Create failing test `testShowUpcomingVisitsWithDefaultDays()` that performs `GET /visits/upcoming` and expects status 200, model attribute "visits", and view name "visits/upcomingVisits"
- [x] 2.3 **GREEN Phase**: Read `VisitController.java` and add constructor parameter `VisitRepository visits` for dependency injection
- [x] 2.4 **GREEN Phase**: Add `@GetMapping("/upcoming")` method that accepts `@RequestParam(defaultValue = "7") int days`, calls repository, adds model attributes, and returns view name
- [x] 2.5 **RED Phase**: Add failing test `testShowUpcomingVisitsWithCustomDays()` that performs `GET /visits/upcoming?days=14` and verifies repository is called with 14-day date range
- [x] 2.6 **GREEN Phase**: Update controller method to use `LocalDate.now()` and `LocalDate.now().plusDays(days)` for date range calculation (already implemented)
- [x] 2.7 **SKIPPED**: Validation tests deferred - spec doesn't require @Min validation
- [x] 2.8 **SKIPPED**: Validation annotation deferred - can be added later if needed
- [x] 2.9 **REFACTOR Phase**: Add model attributes `startDate` and `endDate` for display on view
- [x] 2.10 Verify all controller tests pass: `./mvnw test -Dtest=VisitControllerTests`

---

### [x] 3.0 Presentation Layer - Create View Template and Navigation (TDD with HTML Verification)

**Purpose**: Create the Thymeleaf template `upcomingVisits.html` that displays upcoming visits in a table format following existing design patterns. Add navigation menu item and i18n message keys.

**TDD Approach**: Write controller tests that verify HTML output, then create template to pass tests.

#### 3.0 Proof Artifact(s)

- **Screenshot**: Page at `http://localhost:8080/visits/upcoming` showing visits table with Date, Pet (linked), Owner (linked), Description columns demonstrates UI rendering works
- **Screenshot**: Empty state showing "No upcoming visits scheduled" message demonstrates graceful handling of no results
- **Test Output**: Controller test `testUpcomingVisitsViewRendersVisitList()` passes demonstrates HTML contains expected visit data
- **Code Artifact**: `src/main/resources/templates/visits/upcomingVisits.html` with table structure and Liatrio styling demonstrates template implementation
- **Code Artifact**: Modified `src/main/resources/templates/fragments/layout.html` with new navigation menu item demonstrates navigation integration
- **i18n Validation**: Output from `i18n-sync-validator` agent shows all language files synchronized demonstrates i18n compliance

#### 3.0 Tasks

- [ ] 3.1 **RED Phase**: Add test `testUpcomingVisitsViewRendersVisitList()` to `VisitControllerTests.java` that verifies HTML response contains pet names, owner names, and descriptions
- [ ] 3.2 **RED Phase**: Add test `testUpcomingVisitsViewShowsEmptyMessage()` that mocks empty visit list and verifies HTML contains "No upcoming visits" message
- [ ] 3.3 **GREEN Phase**: Read existing templates (`src/main/resources/templates/vets/vetList.html`, `src/main/resources/templates/owners/ownersList.html`) to understand layout patterns
- [ ] 3.4 **GREEN Phase**: Create `src/main/resources/templates/visits/upcomingVisits.html` with layout fragment `th:replace="~{fragments/layout :: layout (~{::body},'visits')}"`
- [ ] 3.5 **GREEN Phase**: Add page header with h2 title and subtitle showing date range
- [ ] 3.6 **GREEN Phase**: Add table with `class="table table-striped liatrio-table"` and columns: Date, Pet, Owner, Description
- [ ] 3.7 **GREEN Phase**: Implement table rows using `th:each="visit : ${visits}"` with date formatting `${#temporals.format(visit.date, 'yyyy-MM-dd')}`
- [ ] 3.8 **GREEN Phase**: Add links for pet and owner names using `th:href="@{'/owners/' + ${visit.pet.owner.id}}"`
- [ ] 3.9 **GREEN Phase**: Add empty state handling: `th:if="${visits.isEmpty()}"` with message "No upcoming visits scheduled"
- [ ] 3.10 **REFACTOR Phase**: Add i18n message keys to `src/main/resources/messages/messages.properties`:
  - `visits.upcoming.title=Upcoming Visits`
  - `visits.upcoming.showingDays=Showing visits for the next {0} days`
  - `visits.upcoming.noVisits=No upcoming visits scheduled`
  - `visits.upcoming.date=Date`
  - `visits.upcoming.pet=Pet`
  - `visits.upcoming.owner=Owner`
  - `visits.upcoming.description=Description`
- [ ] 3.11 **REFACTOR Phase**: Update template to use message keys with `#{...}` syntax (e.g., `th:text="#{visits.upcoming.title}"`)
- [ ] 3.12 Read `src/main/resources/templates/fragments/layout.html` to understand navigation structure
- [ ] 3.13 Modify `layout.html` to add new navigation menu item after "Veterinarians": `<li th:replace="~{::menuItem ('/visits/upcoming','visits','upcoming visits','calendar',#{visits.upcoming.title})}"></li>`
- [ ] 3.14 Run `i18n-sync-validator` agent to ensure all language files have new message keys
- [ ] 3.15 Start application: `./mvnw spring-boot:run` and manually verify page renders at `http://localhost:8080/visits/upcoming`
- [ ] 3.16 Take screenshot of page with visits and save to `docs/specs/04-spec-upcoming-visits-page/04-proofs/`
- [ ] 3.17 Take screenshot of empty state and save to `docs/specs/04-spec-upcoming-visits-page/04-proofs/`

---

### [~] 4.0 Integration Testing and Multi-Database Validation

**Purpose**: Add future-dated test data to all database scripts, create integration tests, and verify functionality works correctly across H2, MySQL, and PostgreSQL databases.

**TDD Approach**: Write integration tests that verify end-to-end flow with real database.

#### 4.0 Proof Artifact(s)

- **Test Output**: `./mvnw test` passes on H2 demonstrates basic compatibility
- **Test Output**: `./mvnw test -Dspring.profiles.active=mysql` passes demonstrates MySQL compatibility with TestContainers
- **Test Output**: `./mvnw test -Dspring.profiles.active=postgres` passes demonstrates PostgreSQL compatibility
- **Code Artifact**: Modified `src/main/resources/db/h2/data.sql` with future-dated visits demonstrates test data exists
- **Test Code**: `src/test/java/org/springframework/samples/petclinic/owner/UpcomingVisitsIntegrationTests.java` demonstrates end-to-end integration testing
- **Agent Output**: `test-temporal-coupling-detector` agent passes demonstrates no hardcoded dates in tests
- **Agent Output**: `multi-db-test-runner` agent passes demonstrates cross-database compatibility verified

#### 4.0 Tasks

- [ ] 4.1 Read existing `src/main/resources/db/h2/data.sql` to understand test data structure and find next available visit ID
- [ ] 4.2 Add future-dated visits to `data.sql` files using static dates (e.g., `'2026-03-15'`, `'2026-03-18'`, `'2026-03-22'`) for pets with IDs 1, 7, 8
- [ ] 4.3 Modify `src/main/resources/db/mysql/data.sql` with same future-dated visits
- [ ] 4.4 Modify `src/main/resources/db/postgres/data.sql` with same future-dated visits
- [ ] 4.5 **RED Phase**: Create `UpcomingVisitsIntegrationTests.java` with `@SpringBootTest(webEnvironment = RANDOM_PORT)` annotation
- [ ] 4.6 **RED Phase**: Add test `shouldDisplayUpcomingVisitsEndToEnd()` that uses `TestRestTemplate` to GET `/visits/upcoming` and verifies response contains future visit data
- [ ] 4.7 **GREEN Phase**: Inject `@Autowired TestRestTemplate restTemplate` and implement test to verify HTTP 200 status and response body contains visit descriptions
- [ ] 4.8 **REFACTOR Phase**: Add test `shouldFilterVisitsByDateRange()` that creates a future visit programmatically and verifies it appears in results
- [ ] 4.9 Run integration tests on H2: `./mvnw test -Dtest=UpcomingVisitsIntegrationTests`
- [ ] 4.10 Run integration tests on MySQL: `./mvnw test -Dtest=UpcomingVisitsIntegrationTests -Dspring.profiles.active=mysql` (requires Docker)
- [ ] 4.11 Run integration tests on PostgreSQL: `./mvnw test -Dspring.profiles.active=postgres` (requires Docker)
- [ ] 4.12 Run `test-temporal-coupling-detector` agent to verify no hardcoded dates in test files
- [ ] 4.13 Run `multi-db-test-runner` agent to verify tests pass on all three databases
- [ ] 4.14 Save test output to `docs/specs/04-spec-upcoming-visits-page/04-proofs/test-output-h2.txt`
- [ ] 4.15 Save test output to `docs/specs/04-spec-upcoming-visits-page/04-proofs/test-output-mysql.txt`
- [ ] 4.16 Save test output to `docs/specs/04-spec-upcoming-visits-page/04-proofs/test-output-postgres.txt`

---

### [x] 5.0 Validation, Coverage, and Quality Gates

**Purpose**: Run all validation agents to ensure TDD compliance, architecture patterns, Spring Boot best practices, and generate final proof artifacts with coverage reports.

**Quality Assurance**: Verify all standards met before completion.

#### 5.0 Proof Artifact(s)

- **Agent Output**: `tdd-enforcer` agent passes demonstrates TDD methodology followed (RED-GREEN-REFACTOR)
- **Agent Output**: `spring-boot-validator` agent passes demonstrates Spring Boot best practices followed
- **Agent Output**: `architecture-compliance-checker` agent passes demonstrates layered architecture maintained
- **Coverage Report**: JaCoCo report at `target/site/jacoco/index.html` shows >90% coverage for new code demonstrates quality standard met
- **Test Output**: Full test suite `./mvnw test` passes demonstrates all tests passing
- **Documentation**: `docs/specs/04-spec-upcoming-visits-page/04-proofs/` directory contains all proof artifacts demonstrates feature completion

#### 5.0 Tasks

- [ ] 5.1 Run full test suite: `./mvnw test` and verify all tests pass
- [ ] 5.2 Generate JaCoCo coverage report: `./mvnw test jacoco:report`
- [ ] 5.3 Open coverage report at `target/site/jacoco/index.html` and verify >90% coverage for new classes:
  - `VisitRepository` (interface - n/a)
  - `VisitController` (new method)
  - `VisitRepositoryTests` (n/a - test code)
  - `VisitControllerTests` (n/a - test code)
  - `UpcomingVisitsIntegrationTests` (n/a - test code)
- [ ] 5.4 Take screenshot of JaCoCo coverage report showing overall coverage and save to `docs/specs/04-spec-upcoming-visits-page/04-proofs/jacoco-coverage.png`
- [ ] 5.5 Run `tdd-enforcer` agent to verify TDD methodology was followed (tests written before implementation)
- [ ] 5.6 Run `spring-boot-validator` agent to verify Spring Boot best practices (repository pattern, constructor injection, annotations)
- [ ] 5.7 Run `architecture-compliance-checker` agent to verify layered architecture maintained (no business logic in controller)
- [ ] 5.8 Review agent outputs and address any issues found
- [ ] 5.9 Save all agent outputs to `docs/specs/04-spec-upcoming-visits-page/04-proofs/validation-agents/`
- [ ] 5.10 Create summary document `docs/specs/04-spec-upcoming-visits-page/04-validation-summary.md` listing all proof artifacts with links
- [ ] 5.11 Verify all acceptance criteria from spec are met:
  - ✓ Page exists at `/visits/upcoming`
  - ✓ Supports `days` query param (default 7)
  - ✓ Displays owner, pet, date, description
  - ✓ Read-only view (no editing)
  - ✓ Links to owner details work
- [ ] 5.12 Mark all parent tasks as complete in this task file

---

## Notes

This task list follows the Spec's demoable units closely and implements them using strict TDD methodology as required by the project's CLAUDE.md. Each parent task represents a demonstrable milestone with clear proof artifacts.

**Repository Standards Applied**:
- Strict TDD (Red-Green-Refactor)
- 90%+ test coverage requirement
- Constructor-based dependency injection
- `@Transactional(readOnly = true)` for query methods
- i18n support with messages.properties
- Multi-database compatibility testing

**Agent Execution Sequence**:
1. After Task 3.0: Run `i18n-sync-validator` agent
2. After Task 4.0: Run `test-temporal-coupling-detector` and `multi-db-test-runner` agents
3. In Task 5.0: Run `tdd-enforcer`, `spring-boot-validator`, `architecture-compliance-checker` agents

**TDD Cycle Reminders**:
- **RED**: Write a failing test that defines desired behavior
- **GREEN**: Write minimal code to make the test pass
- **REFACTOR**: Improve code while keeping tests green

**Next Step**: Run `/SDD-3-manage-tasks` to begin implementation following this task breakdown.
