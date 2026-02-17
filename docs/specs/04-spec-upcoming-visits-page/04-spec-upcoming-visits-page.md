# 04-spec-upcoming-visits-page.md

## Introduction/Overview

The Emerald Grove Veterinary Clinic needs a centralized view to see all upcoming scheduled visits. Currently, visits are only visible on individual owner detail pages, making it difficult for clinic staff to plan their day and see the overall appointment schedule. This feature adds a dedicated "Upcoming Visits" page that displays all visits scheduled for a configurable date range, allowing staff to quickly see what appointments are coming up.

## Goals

1. **Provide centralized visibility** of upcoming appointments across all pets and owners in a single view
2. **Enable flexible time ranges** by supporting a configurable `days` parameter (default 7 days) for viewing different scheduling horizons
3. **Maintain high performance** by using optimized database queries that avoid N+1 query problems
4. **Follow strict TDD methodology** with 90%+ test coverage across all layers (repository, controller, view, integration)
5. **Ensure cross-database compatibility** by testing on H2, MySQL, and PostgreSQL backends

## User Stories

1. **As a veterinary clinic receptionist**, I want to see all upcoming visits in one place so that I can plan my day and prepare for upcoming appointments without having to check each owner's page individually.

2. **As a veterinary clinic manager**, I want to customize the date range for viewing upcoming visits (e.g., next 7 days, next 14 days) so that I can plan staffing and resources based on appointment volume.

3. **As a veterinarian**, I want to quickly see which pets I'll be seeing in the coming days so that I can review their medical history in advance.

4. **As a clinic staff member**, I want to easily navigate to an owner's details from the upcoming visits page so that I can quickly access full patient information when needed.

## Demoable Units of Work

### Unit 1: Repository Layer - Data Access Query

**Purpose:** Implement the database query that retrieves upcoming visits with associated pet and owner data, providing the foundation for the entire feature while ensuring optimal performance.

**Functional Requirements:**
- The system shall provide a repository interface `VisitRepository` that extends Spring Data's `Repository<Visit, Integer>`
- The system shall implement a query method `findUpcomingVisits(LocalDate start, LocalDate end)` that returns a list of visits within the specified date range
- The system shall use JPQL with `JOIN FETCH` to eagerly load Pet and Owner entities in a single database query
- The system shall order results by visit date in ascending order, then by pet name
- The system shall return an empty list when no visits exist in the specified date range

**Proof Artifacts:**
- **JUnit Test**: `VisitRepositoryTests.java` with passing tests demonstrates repository query works correctly
- **Test Output**: `./mvnw test -Dtest=VisitRepositoryTests` shows all tests passing
- **Code Artifact**: `VisitRepository.java` interface with JPQL query demonstrates implementation exists

---

### Unit 2: Controller Layer - Endpoint and Business Logic

**Purpose:** Implement the HTTP endpoint that handles requests for upcoming visits, processes the date range parameter, and prepares data for the view layer.

**Functional Requirements:**
- The system shall provide a GET endpoint at `/visits/upcoming` that returns an HTML page
- The system shall accept an optional `days` query parameter (integer) with a default value of 7
- The system shall show a validation error message to the user when the `days` parameter is invalid (zero, negative, or unreasonably large)
- The system shall calculate the date range as: start = today, end = today + days
- The system shall inject `VisitRepository` via constructor dependency injection
- The system shall add attributes to the model: `visits` (list), `days` (integer), `startDate`, `endDate`
- The system shall return the view name `"visits/upcomingVisits"`

**Proof Artifacts:**
- **JUnit Test**: `VisitControllerTests.java` with tests for default days, custom days, and validation
- **Test Output**: Controller tests passing with MockMvc verification
- **Code Artifact**: Modified `VisitController.java` with new endpoint method

---

### Unit 3: Presentation Layer - View Template and UI

**Purpose:** Create the user interface that displays upcoming visits in a clean, accessible table format following the existing design patterns and styling conventions.

**Functional Requirements:**
- The system shall provide a Thymeleaf template at `templates/visits/upcomingVisits.html`
- The template shall use the standard layout fragment for consistent navigation and styling
- The system shall display a table with columns: Date, Pet (linked to owner details), Owner (linked to owner details), Description
- The system shall display "No upcoming visits scheduled" when the visits list is empty
- The system shall format dates using the pattern `yyyy-MM-dd` consistent with existing visit displays
- The system shall apply Liatrio styling classes: `table table-striped liatrio-table` for the table
- The template shall use internationalized message keys for all user-facing text
- The system shall add a navigation menu item labeled "Upcoming Visits" to the main navigation

**Proof Artifacts:**
- **Screenshot**: Page at `http://localhost:8080/visits/upcoming` shows visits table demonstrates UI rendering
- **Screenshot**: Empty state message demonstrates graceful handling of no results
- **HTML Test**: Controller test verifies HTML contains visit data (pet names, owner names, descriptions)
- **Code Artifact**: `upcomingVisits.html` template with table structure

---

### Unit 4: Integration and Multi-Database Testing

**Purpose:** Verify the complete end-to-end functionality works correctly across all supported database backends and that test data is properly configured.

**Functional Requirements:**
- The system shall add future-dated visit records to all database initialization scripts (H2, MySQL, PostgreSQL)
- The system shall pass integration tests that create a future visit and verify it appears on the page
- The system shall work correctly on H2 in-memory database (development)
- The system shall work correctly on MySQL with TestContainers
- The system shall work correctly on PostgreSQL with Docker Compose
- The integration test shall use `@SpringBootTest` to test the full application stack
- The system shall maintain 90%+ test coverage for all new code

**Proof Artifacts:**
- **Test Output**: `./mvnw test` passes on H2 database demonstrates basic compatibility
- **Test Output**: `./mvnw test -Dspring.profiles.active=mysql` passes demonstrates MySQL compatibility
- **Test Output**: `./mvnw test -Dspring.profiles.active=postgres` passes demonstrates PostgreSQL compatibility
- **JaCoCo Report**: Coverage report shows >90% coverage for new code demonstrates quality standard met
- **Code Artifact**: Updated `data.sql` files with future-dated visits

## Non-Goals (Out of Scope)

1. **Pagination**: The initial implementation will not include pagination. The date range naturally limits results, and pagination can be added in a future iteration if needed.

2. **Filtering and Search**: No filtering by pet type, owner name, or visit description. Users can navigate to owner details for more specific information.

3. **Editing Capabilities**: This page is read-only. Visit creation and editing remain on the owner/pet detail pages. No forms or edit buttons on this page.

4. **Sorting Options**: Results are sorted by date (ascending) only. No user-configurable sorting by owner name, pet name, or other fields.

5. **Export Functionality**: No CSV export, PDF generation, or other export formats in the initial implementation.

6. **Calendar View**: The page displays a table list view only, not a calendar/grid view of appointments.

7. **Visit Time Management**: Since the Visit entity only stores dates (not times), no time-based filtering or display is included.

## Design Considerations

**UI/UX Requirements:**

- **Layout**: Follow the existing page layout pattern used by `vets.html` and `ownersList.html`
- **Navigation**: Add menu item to main navigation bar with calendar icon and label "Upcoming Visits"
- **Table Structure**: Use Bootstrap's `table table-striped` with Liatrio's custom table classes for consistency
- **Empty State**: Display centered, muted text message when no visits exist (following pattern from owners list)
- **Links**: Pet names and owner names should be clickable links to the owner details page (`/owners/{id}`)
- **Responsive**: Table should be responsive using Bootstrap's responsive table utilities
- **Styling**: Follow Liatrio design system with classes: `liatrio-section`, `liatrio-table-card`, `liatrio-card-header`

**Visual Hierarchy:**
- Page title: "Upcoming Visits" (h2)
- Subtitle: "Showing visits for the next [N] days" (muted text)
- Table headers: Date, Pet, Owner, Description
- No action buttons (read-only view)

## Repository Standards

**Implementation must follow established repository patterns:**

**Coding Standards:**
- Use existing Spring Boot and Spring Data JPA conventions
- Follow repository pattern: `VisitRepository extends Repository<Visit, Integer>`
- Use constructor-based dependency injection (not field injection)
- Apply `@Transactional(readOnly = true)` to query methods

**Testing Conventions:**
- Follow strict TDD: write tests before implementation (RED-GREEN-REFACTOR)
- Use `@DataJpaTest` for repository tests
- Use `@WebMvcTest` for controller tests with `@MockitoBean` for dependencies
- Use `@SpringBootTest` for integration tests
- Use descriptive test method names that document behavior

**Architectural Patterns:**
- Maintain layered architecture: Data → Business → Presentation
- No business logic in controllers (controllers orchestrate, services contain logic)
- Repositories handle only data access
- Use DTOs if needed for data transfer between layers

**Documentation and Internationalization:**
- Add message keys to `messages.properties` for all user-facing text
- Run `i18n-sync-validator` agent after adding message keys
- Use Thymeleaf's `#{...}` syntax for message resolution

**Quality Assurance:**
- Maintain 90%+ line coverage
- 100% branch coverage for critical business logic
- Run all validation agents before final commit

## Technical Considerations

**Database and ORM:**
- **Challenge**: Visit entity does not have a direct reference to Pet or Owner
- **Solution**: Use JPQL with `JOIN FETCH v.pet p JOIN FETCH p.owner o` to navigate relationships
- **Performance**: Single query with eager fetching prevents N+1 query problem
- **Query Pattern**: `SELECT v FROM Visit v JOIN FETCH v.pet p JOIN FETCH p.owner WHERE v.date BETWEEN :start AND :end ORDER BY v.date ASC`

**Spring Framework:**
- Spring Boot 4.0.0
- Spring Data JPA for repository layer
- Spring MVC for controller layer
- Thymeleaf 3.x for view templates
- Bean Validation (Jakarta) for input validation

**Database Compatibility:**
- Must work on H2 (in-memory, development)
- Must work on MySQL 8.4+ (production option)
- Must work on PostgreSQL 17+ (production option)
- JPQL must be database-agnostic (avoid native SQL)

**Test Data Strategy:**
- Add static future dates to `data.sql` files
- **Note**: Static dates will require periodic updates to remain in the future
- **Alternative considered**: Dynamic dates using `DATEADD()` or `CURRENT_DATE()` were proposed but not selected

**Dependencies:**
- No new external dependencies required
- Uses existing Spring Boot starters
- TestContainers for MySQL testing
- Docker Compose for PostgreSQL testing

## Security Considerations

**No specific security considerations identified** for this read-only feature:
- No user input is persisted (read-only view)
- Date range parameter is validated to prevent abuse
- No sensitive data is exposed beyond what's already visible on owner detail pages
- Uses existing Spring Security configuration (if any)

**Proof Artifact Safety:**
- Screenshots may contain pet/owner names from test data (acceptable for internal proof)
- No real patient data should be used in test environments

## Success Metrics

1. **Feature Adoption**: Staff use the Upcoming Visits page daily to view appointments (measured by page views)
2. **Performance**: Page load time < 500ms for 100 visits (measured by application metrics)
3. **Quality**: Test coverage ≥ 90% for all new code (measured by JaCoCo report)
4. **Validation**: All 6 validation agents pass (tdd-enforcer, spring-boot-validator, architecture-compliance-checker, multi-db-test-runner, i18n-sync-validator, test-temporal-coupling-detector)
5. **User Satisfaction**: Positive feedback from clinic staff about ease of viewing upcoming appointments

## Open Questions

**No open questions at this time.** All requirements have been clarified through the questions round. The user has selected:
- Navigation: Main navigation menu
- Validation: Show error messages for invalid input
- Empty state: Simple text message
- Display format: Date, Pet (linked), Owner (linked), Description
- Out of scope: Pagination, filtering, editing, sorting, export
- Multi-DB testing: Required (H2, MySQL, PostgreSQL)
- Test data: Static future dates (requires periodic updates)
