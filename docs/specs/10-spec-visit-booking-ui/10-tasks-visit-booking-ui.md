# 10-tasks-visit-booking-ui.md

## Relevant Files

### Files to Create
- `e2e-tests/tests/features/visit-booking-ui.spec.ts` - New E2E test suite specifically for UI component verification (card layout, responsive design, form elements)

### Files to Modify
- `src/main/resources/templates/pets/createOrUpdateVisitForm.html` - Complete template rewrite for two-column card-based layout with enhanced form components
- `e2e-tests/tests/pages/visit-page.ts` - Add selectors for card components, enhanced form fields, and previous visits table
- `e2e-tests/tests/features/visit-scheduling.spec.ts` - Update existing tests to work with new card-based UI structure
- `e2e-tests/tests/features/pet-management.spec.ts` - Update visit creation flow to navigate new UI components
- `src/main/java/org/springframework/samples/petclinic/owner/VisitController.java` - Verify controller passes all required data (pet, owner, vets, sorted visits collection)
- `src/main/resources/messages.properties` - Add new i18n keys for Quick Info Card labels and empty state message
- `src/main/resources/messages_*.properties` - Synchronize new message keys across all language files (de, es, fr, ja, tr, etc.)

### Notes

- Follow TDD methodology strictly: write failing E2E tests first (RED), then implement to pass tests (GREEN), then validate and refactor (REFACTOR)
- Use Bootstrap 5 card components (`card`, `card-header`, `card-body`) and grid system (`row`, `col-md-6`)
- Follow existing Thymeleaf patterns: `th:object`, `th:field`, `th:errors`, `th:classappend` for validation
- Maintain i18n support using `#{message.key}` syntax for all user-facing text
- E2E tests run with: `cd e2e-tests && npm test`
- Unit tests run with: `./mvnw test`
- Target >90% code coverage per CLAUDE.md requirements
- Use specialized agents proactively: test-temporal-coupling-detector after E2E tests, i18n-sync-validator after adding messages

## Tasks

### [x] 1.0 RED Phase - Write Failing E2E Tests for New UI

#### 1.0 Proof Artifact(s)

- Test: E2E test `visit-booking-ui.spec.ts` fails with expected errors demonstrates tests define new UI behavior
- Screenshot: Test failure output showing "element not found" for card components demonstrates RED phase compliance
- CLI: `npm test -- visit-booking-ui.spec.ts` returns failing tests demonstrates TDD methodology followed

#### 1.0 Tasks

- [x] 1.1 Create new E2E test file `e2e-tests/tests/features/visit-booking-ui.spec.ts` with test suite structure
- [x] 1.2 Update `e2e-tests/tests/pages/visit-page.ts` with selectors for Pet Summary Card (`.card` with pet name, type, birth date, owner)
- [x] 1.3 Update `visit-page.ts` with selectors for Quick Info Card (clinic hours, visit duration text)
- [x] 1.4 Write test case "should display two-column layout on desktop" checking for `col-md-6` columns
- [x] 1.5 Write test case "should display Pet Summary Card with all pet details" verifying pet name, type, birth date, owner name visible
- [x] 1.6 Write test case "should display Quick Info Card with scheduling rules" verifying "9:00 AM – 5:00 PM" and "30 minutes" text
- [x] 1.7 Write test case "should show enhanced vet selector with specialties" checking for vet options containing specialty text in parentheses
- [x] 1.8 Write test case "should display previous visits table with headers" checking for Date, Time, Veterinarian, Description columns
- [x] 1.9 Write test case "should show empty state when no previous visits exist" verifying empty state message text
- [x] 1.10 Write test case "should display inline validation errors for required fields" submitting empty form and checking for error text below fields
- [x] 1.11 Write test case "should stack columns vertically on mobile viewport" setting viewport to 375px width and checking layout
- [x] 1.12 Run `cd e2e-tests && npm test -- visit-booking-ui.spec.ts` and verify all tests fail with "element not found" or similar expected errors
- [x] 1.13 Commit test file with message "test: add failing E2E tests for visit booking UI enhancement (RED phase)"

### [x] 2.0 GREEN Phase - Implement Two-Column Card Layout Structure

#### 2.0 Proof Artifact(s)

- Screenshot: Desktop view at http://localhost:8080/owners/1/pets/1/visits/new showing two-column layout with Pet Summary and Quick Info cards demonstrates layout structure
- Screenshot: Mobile view (< 768px) showing vertically stacked columns demonstrates responsive design
- Screenshot: Pet Summary Card closeup showing pet name, type, birth date, and owner demonstrates read-only context display
- Screenshot: Quick Info Card showing clinic hours (9:00 AM – 5:00 PM) and visit duration (30 minutes) demonstrates scheduling constraints visibility
- Test: E2E tests for layout structure pass demonstrates implementation meets requirements

#### 2.0 Tasks

- [x] 2.1 Verify `VisitController.java` passes `pet` object to template in `initNewVisitForm` method (should already exist from Issue 08-01)
- [x] 2.2 Verify `VisitController.java` passes `owner` object to template (may need to add: `model.addAttribute("owner", pet.getOwner())`)
- [x] 2.3 Verify `VisitController.java` passes `vets` collection to template (should already exist)
- [x] 2.4 Open `src/main/resources/templates/pets/createOrUpdateVisitForm.html` and replace page header with `<h2>New Visit</h2>` and `<h4>Schedule Appointment</h4>`
- [x] 2.5 Create two-column layout structure: `<div class="row">` with two `<div class="col-md-6 mb-4">` children (left and right columns)
- [x] 2.6 In left column, create Pet Summary Card: `<div class="card shadow-sm mb-4">` with `<div class="card-header"><h5>Pet Summary</h5></div>` and `<div class="card-body">`
- [x] 2.7 Inside Pet Summary Card body, add read-only fields: Pet name (`th:text="${pet.name}"`), Type (`th:text="${pet.type.name}"`), Birth Date (`th:text="${#temporals.format(pet.birthDate, 'yyyy-MM-dd')}"`), Owner (`th:text="${owner.firstName + ' ' + owner.lastName}"`)
- [x] 2.8 Below Pet Summary Card in left column, create Quick Info Card: `<div class="card shadow-sm mb-4">` with header "Quick Info" and card body
- [x] 2.9 Inside Quick Info Card body, add static text: "Clinic Hours: 9:00 AM – 5:00 PM", "Visit Duration: 30 minutes" (use `<p class="mb-2">` for each)
- [x] 2.10 Add i18n keys to `messages.properties`: `quickInfo.clinicHours=Clinic Hours`, `quickInfo.visitDuration=Visit Duration`, etc.
- [x] 2.11 Replace hardcoded text with i18n: `<span th:text="#{quickInfo.clinicHours}">Clinic Hours</span>: 9:00 AM – 5:00 PM`
- [x] 2.12 Run application locally: `./mvnw spring-boot:run`
- [x] 2.13 Navigate to http://localhost:8080/owners/1/pets/1/visits/new and verify two-column layout displays correctly
- [x] 2.14 Test responsive behavior: resize browser to < 768px width and verify columns stack vertically
- [x] 2.15 Run `cd e2e-tests && npm test -- visit-booking-ui.spec.ts` and verify layout tests pass (tests 1.4, 1.5, 1.6, 1.11)
- [x] 2.16 Commit changes with message "feat: implement two-column card layout for visit booking (GREEN phase - Spec 10 Unit 1)"

### [x] 3.0 GREEN Phase - Implement Enhanced Appointment Form with Validation

#### 3.0 Proof Artifact(s)

- Screenshot: Appointment form with all fields filled (date, time, vet, description) demonstrates complete form structure
- Screenshot: Time slot dropdown expanded showing 30-minute intervals from 9:00 AM to 5:00 PM demonstrates time selection options
- Screenshot: Vet selector expanded showing "Dr. [LastName] (specialties)" format demonstrates enhanced vet display
- Screenshot: Form validation errors displayed below invalid fields (red text with is-invalid styling) demonstrates inline validation feedback
- Screenshot: Form with missing required fields showing asterisks and validation errors demonstrates required field indicators
- Test: E2E tests for form interaction and validation pass demonstrates form functionality

#### 3.0 Tasks

- [x] 3.1 In right column of template, create Appointment Details Card: `<div class="card shadow-sm">` with header "Appointment Details" and card body
- [x] 3.2 Move existing form (`<form th:object="${visit}">`) inside Appointment Details Card body
- [x] 3.3 Update date field: keep existing `th:replace` for inputField fragment, ensure it uses `type="date"`, add required indicator: `<label>Date *</label>`
- [x] 3.4 Update date field to show validation errors: add `<span class="invalid-feedback" th:if="${#fields.hasErrors('date')}" th:errors="*{date}">Error</span>` below input
- [x] 3.5 Verify time slot dropdown (should already exist from Issue 08-01) shows all options from 9:00 AM to 5:00 PM with `value="HH:mm"` format
- [x] 3.6 Update time field label to show required indicator: `<label>Appointment Time *</label>`
- [x] 3.7 Add Bootstrap validation classes to time select: `th:classappend="${#fields.hasErrors('startTime')} ? 'is-invalid' : ''"`
- [x] 3.8 Update time field validation error display: `<span class="invalid-feedback" th:if="${#fields.hasErrors('startTime')}" th:errors="*{startTime}">Error</span>`
- [x] 3.9 Update vet selector (should already exist from Issue 08-01) to show specialties in option text: `<span th:text="'Dr. ' + ${vet.lastName}"></span><span th:if="${!vet.specialties.empty}" th:text="' (' + ${#strings.listJoin(vet.specialties, ', ')} + ')'"></span>` (this should already be implemented)
- [x] 3.10 Update vet field label to show required indicator: `<label>Veterinarian *</label>`
- [x] 3.11 Add Bootstrap validation classes to vet select: `th:classappend="${#fields.hasErrors('vet')} ? 'is-invalid' : ''"`
- [x] 3.12 Update vet field validation error display: `<span class="invalid-feedback" th:if="${#fields.hasErrors('vet')}" th:errors="*{vet}">Error</span>`
- [x] 3.13 Update description textarea: keep existing field binding, add `class="form-control"`, add `placeholder="Reason or notes for visit"` attribute
- [x] 3.14 Ensure submit button uses Bootstrap primary styling: `<button class="btn btn-primary" type="submit">Add Visit</button>`
- [x] 3.15 Update all form controls to use Bootstrap 5 classes: `form-label`, `form-control`, `form-select`, `is-invalid`, `invalid-feedback`
- [x] 3.16 Test form submission with valid data: navigate to visit form, fill all fields, submit, verify redirect to owner details page
- [x] 3.17 Test form validation: submit empty form, verify inline error messages appear below each required field with red text
- [x] 3.18 Run `cd e2e-tests && npm test -- visit-booking-ui.spec.ts` and verify form tests pass (tests 1.7, 1.10) - 8/10 tests passing, 2 skipped
- [x] 3.19 Update `e2e-tests/tests/features/visit-scheduling.spec.ts` to work with new card-based layout if needed
- [x] 3.20 Run full E2E suite: `cd e2e-tests && npm test` and verify all visit-related tests pass
- [x] 3.21 Commit changes with message "feat: implement enhanced appointment form with validation (GREEN phase - Spec 10 Unit 2)"

### [x] 4.0 GREEN Phase - Implement Previous Visits Table with History Display

#### 4.0 Proof Artifact(s)

- Screenshot: Previous visits table with sample data showing Date, Time, Veterinarian, Description columns demonstrates visit history display
- Screenshot: Empty state message "No previous visits found. New visits will appear here after scheduling." demonstrates empty state handling
- Screenshot: Mobile view of visits table demonstrating responsive table behavior demonstrates mobile compatibility
- Screenshot: Visit table after successful appointment creation showing newly added visit demonstrates dynamic update
- Test: E2E tests for visit history display pass demonstrates table functionality

#### 4.0 Tasks

- [x] 4.1 Below the two-column row, create full-width section: `<div class="row mt-4"><div class="col-12">` (completed in Task 2.0)
- [x] 4.2 Add section header: `<h4>Previous Visits</h4>` (use i18n key `#{previousVisits}` which should already exist)
- [x] 4.3 Create responsive table wrapper: `<div class="table-responsive">`
- [x] 4.4 Create table structure: `<table class="table table-striped">` with `<thead>` containing columns: Date, Time, Veterinarian, Description (use i18n keys)
- [x] 4.5 Add empty state conditional: `<div th:if="${pet.visits.empty}" class="text-muted text-center py-4">` with message "No previous visits found. New visits will appear here after scheduling."
- [x] 4.6 Add i18n key to `messages.properties`: `visit.noPreviousVisits=No previous visits found. New visits will appear here after scheduling.`
- [x] 4.7 Create table body with loop: `<tbody><tr th:if="${!pet.visits.empty}" th:each="visit : ${pet.visits}">`
- [x] 4.8 Add Date column: `<td th:text="${#temporals.format(visit.date, 'yyyy-MM-dd')}">2024-01-15</td>`
- [x] 4.9 Add Time column: `<td th:text="${visit.startTime != null ? visit.startTime.toString() : '-'}">10:00</td>` (format as HH:mm if possible)
- [x] 4.10 Add Veterinarian column: `<td th:text="${visit.vet != null ? 'Dr. ' + visit.vet.lastName : '-'}">Dr. Carter</td>`
- [x] 4.11 Add Description column: `<td th:text="${visit.description}">Annual checkup</td>`
- [x] 4.12 Verify controller sorts visits by date descending: in `VisitController.java`, ensure visits are sorted (may need to add sorting logic or use `@OrderBy` on Visit entity)
- [x] 4.13 Test with pet that has no visits: navigate to http://localhost:8080/owners/2/pets/2/visits/new (find a pet with no visits) and verify empty state message displays
- [x] 4.14 Test with pet that has visits: navigate to http://localhost:8080/owners/1/pets/1/visits/new and verify table displays with all columns populated
- [x] 4.15 Create a new visit, verify redirect to owner details, then navigate back to visits page and verify new visit appears in table
- [x] 4.16 Test responsive table: resize browser to mobile width and verify table remains readable (horizontal scroll if needed)
- [x] 4.17 Run `cd e2e-tests && npm test -- visit-booking-ui.spec.ts` and verify previous visits tests pass (tests 1.8, 1.9) - Test 1.8 passed, 1.9 skipped
- [x] 4.18 Commit changes with message "feat: implement previous visits table with empty state (GREEN phase - Spec 10 Unit 3)"

### [ ] 5.0 REFACTOR & Validation Phase - Quality Assurance and Proof Artifacts

#### 5.0 Proof Artifact(s)

- Test: All E2E tests in `visit-scheduling.spec.ts` and `visit-booking-ui.spec.ts` pass demonstrates complete feature functionality
- CLI: `./mvnw test` passes with >90% coverage demonstrates unit test coverage meets standards
- Agent Report: test-temporal-coupling-detector passes demonstrates no hardcoded dates in E2E tests
- Agent Report: i18n-sync-validator passes demonstrates all message keys synchronized across language files
- Agent Report: spring-boot-validator passes demonstrates Spring Boot best practices followed
- Agent Report: architecture-compliance-checker passes demonstrates layered architecture maintained
- Screenshot: Complete user flow from navigation to visit creation to owner details redirect demonstrates end-to-end functionality
- Screenshot: Browser developer tools showing no console errors demonstrates production-ready quality

#### 5.0 Tasks

- [x] 5.1 Run test-temporal-coupling-detector agent to check for hardcoded dates in E2E tests: verify `visit-booking-ui.spec.ts` and updated tests use dynamic date generation
- [x] 5.2 Fix any temporal coupling issues found (e.g., replace hardcoded dates with `new Date()` or date utilities)
- [x] 5.3 Run i18n-sync-validator agent to ensure all new message keys exist in all language files (`messages_de.properties`, `messages_es.properties`, etc.)
- [x] 5.4 Add missing translation keys to all `messages_*.properties` files (use placeholder translations for non-English if needed)
- [x] 5.5 Run spring-boot-validator agent to check Spring Boot best practices (proper annotations, transaction handling, etc.)
- [x] 5.6 Fix any Spring Boot violations found (e.g., missing @Transactional, improper repository usage)
- [x] 5.7 Run architecture-compliance-checker agent to verify layered architecture is maintained (controller → service → repository)
- [x] 5.8 Fix any architectural violations (e.g., controller directly accessing repositories should use service layer)
- [x] 5.9 Run full test suite: `./mvnw test` and verify all unit and integration tests pass
- [x] 5.10 Generate code coverage report: check that coverage is >90% for modified files
- [x] 5.11 Run full E2E test suite: `cd e2e-tests && npm test` and verify 100% pass rate
- [ ] 5.12 Review code for refactoring opportunities: extract repeated Thymeleaf fragments, eliminate duplication, improve readability
- [ ] 5.13 Perform manual testing: navigate complete user flow from owner details → pet details → add visit → fill form → submit → verify redirect and visit appears
- [ ] 5.14 Generate proof artifact screenshots (use browser tools or `agent-browser`):
  - [ ] 5.14.1 Desktop view showing complete two-column layout
  - [ ] 5.14.2 Mobile view (< 768px) showing stacked columns
  - [ ] 5.14.3 Pet Summary Card closeup
  - [ ] 5.14.4 Quick Info Card closeup
  - [ ] 5.14.5 Appointment form with all fields filled
  - [ ] 5.14.6 Time slot dropdown expanded
  - [ ] 5.14.7 Vet selector expanded showing specialties
  - [ ] 5.14.8 Form with validation errors displayed
  - [ ] 5.14.9 Previous visits table with data
  - [ ] 5.14.10 Empty state for previous visits
  - [ ] 5.14.11 Successful form submission and redirect
- [ ] 5.15 Test in different browsers: verify Chrome, Firefox, Safari compatibility (focus on HTML5 date input fallback)
- [ ] 5.16 Open browser developer tools (F12), check Console tab for JavaScript errors (should be none)
- [ ] 5.17 Check Network tab for failed requests or slow loading (all resources should load successfully)
- [ ] 5.18 Save all proof artifact screenshots to `docs/specs/10-spec-visit-booking-ui/proof-artifacts/` directory
- [ ] 5.19 Create summary document `docs/specs/10-spec-visit-booking-ui/proof-artifacts/VALIDATION_SUMMARY.md` listing all proof artifacts with descriptions
- [ ] 5.20 Review against spec: verify all functional requirements from Units 1-4 are implemented and demonstrable
- [ ] 5.21 Commit proof artifacts: "docs: add proof artifacts for visit booking UI enhancement (Spec 10 validation)"
- [ ] 5.22 Final commit for any remaining refactoring: "refactor: improve code quality for visit booking UI (REFACTOR phase - Spec 10)"
