# 11-tasks-conflict-detection.md

## Relevant Files

- `src/main/java/org/springframework/samples/petclinic/owner/ConflictDetectionService.java` - Core service containing conflict detection business logic for vet, pet, and capacity conflicts
- `src/test/java/org/springframework/samples/petclinic/owner/ConflictDetectionServiceTests.java` - Unit tests for ConflictDetectionService
- `src/main/java/org/springframework/samples/petclinic/owner/ConflictValidator.java` - Spring Validator implementation that delegates to ConflictDetectionService
- `src/test/java/org/springframework/samples/petclinic/owner/ConflictValidatorTests.java` - Unit tests for ConflictValidator
- `src/main/java/org/springframework/samples/petclinic/owner/VisitRepository.java` - Add query methods for finding overlapping visits
- `src/main/java/org/springframework/samples/petclinic/owner/VisitController.java` - Register ConflictValidator in @InitBinder
- `src/test/java/org/springframework/samples/petclinic/owner/VisitControllerTests.java` - Add integration tests for conflict validation
- `src/main/resources/messages/messages.properties` - Add conflict error message keys (English)
- `src/main/resources/messages/messages_de.properties` - Add conflict error message keys (German)
- `src/main/resources/messages/messages_es.properties` - Add conflict error message keys (Spanish)
- `src/main/resources/messages/messages_fa.properties` - Add conflict error message keys (Farsi)
- `src/main/resources/messages/messages_ko.properties` - Add conflict error message keys (Korean)
- `src/main/resources/messages/messages_pt.properties` - Add conflict error message keys (Portuguese)
- `src/main/resources/messages/messages_ru.properties` - Add conflict error message keys (Russian)
- `src/main/resources/messages/messages_tr.properties` - Add conflict error message keys (Turkish)
- `e2e-tests/tests/features/conflict-detection.spec.ts` - E2E browser tests for conflict scenarios

### Notes

- Unit tests should be placed alongside the code files they are testing in the same directory
- Follow TDD methodology: RED (write failing test) → GREEN (make test pass) → REFACTOR (improve code)
- Use the repository's testing commands: `./mvnw test` for unit/integration tests, `npm test` for E2E tests
- Follow Spring Boot conventions: @Service for services, @Component for validators, @Repository for data access
- Maintain >90% code coverage requirement
- All error messages must be internationalized across all 8 language files

## Tasks

### [x] 1.0 Create Conflict Detection Service with Vet Overlap Prevention

#### 1.0 Proof Artifact(s)

- Test: Unit tests for `ConflictDetectionService.checkVetConflict()` pass demonstrates vet overlap algorithm correctness with various scenarios (exact overlap, partial overlap, back-to-back allowed, no conflict)
- Test: JaCoCo coverage report shows >90% line coverage for ConflictDetectionService demonstrates comprehensive testing
- Code: `ConflictDetectionService.java` shows clean service implementation following Spring Boot @Service pattern demonstrates proper architecture

#### 1.0 Tasks

- [ ] 1.1 (RED) Write failing test for vet conflict with overlapping appointments - Test that checkVetConflict returns conflict when vet has existing appointment that overlaps
- [ ] 1.2 (RED) Write failing test for no conflict with non-overlapping appointments - Test that checkVetConflict returns no conflict when appointments don't overlap
- [ ] 1.3 (RED) Write failing test for back-to-back appointments - Test that 9:00-9:30 and 9:30-10:00 appointments for same vet are allowed (not a conflict)
- [ ] 1.4 (RED) Write failing test for partial overlap - Test that 9:00-9:30 conflicts with 9:15-9:45 for same vet
- [ ] 1.5 (GREEN) Add `findByVetAndDate(Vet vet, LocalDate date)` method to VisitRepository with @Query annotation
- [ ] 1.6 (GREEN) Create ConflictDetectionService class with @Service annotation in owner package
- [ ] 1.7 (GREEN) Inject VisitRepository into ConflictDetectionService constructor
- [ ] 1.8 (GREEN) Implement `hasVetConflict(Visit newVisit)` method using inclusive overlap algorithm: (start1 < end2) AND (start2 < end1)
- [ ] 1.9 (GREEN) Calculate end time as `startTime.plusMinutes(durationMinutes)` for overlap checking
- [ ] 1.10 (GREEN) Run tests and verify all vet conflict tests pass
- [ ] 1.11 (REFACTOR) Extract overlap checking logic to private helper method `doAppointmentsOverlap()`
- [ ] 1.12 (REFACTOR) Add JavaDoc comments to public methods
- [ ] 1.13 (REFACTOR) Run JaCoCo coverage report and verify >90% line coverage

### [ ] 2.0 Implement Pet Conflict Detection

#### 2.0 Proof Artifact(s)

- Test: Unit tests for `ConflictDetectionService.checkPetConflict()` pass demonstrates pet overlap algorithm correctness
- Test: Edge case tests show same owner can book multiple pets at same time but same pet cannot be double-booked demonstrates proper business logic
- Test: Integration test with VisitRepository shows pet conflict queries work correctly demonstrates database layer integration

#### 2.0 Tasks

- [ ] 2.1 (RED) Write failing test for pet conflict with overlapping appointments - Test that checkPetConflict returns conflict when pet has existing appointment that overlaps
- [ ] 2.2 (RED) Write failing test for same owner booking multiple pets at same time - Test that owner can book Pet A and Pet B at 9:00 AM simultaneously (should be allowed)
- [ ] 2.3 (RED) Write failing test for different vets seeing same pet - Test that same pet cannot have appointments with Dr. Smith at 9:00 and Dr. Jones at 9:15 (should be prevented)
- [ ] 2.4 (GREEN) Add query method to VisitRepository to find visits by pet and date - Navigate through Pet entity since Visit doesn't have direct Pet reference
- [ ] 2.5 (GREEN) Implement `hasPetConflict(Visit newVisit, Pet pet)` method in ConflictDetectionService
- [ ] 2.6 (GREEN) Reuse `doAppointmentsOverlap()` helper method for pet conflict checking
- [ ] 2.7 (GREEN) Run tests and verify all pet conflict tests pass
- [ ] 2.8 (REFACTOR) Create integration test using @DataJpaTest to verify pet conflict queries work with actual database
- [ ] 2.9 (REFACTOR) Test edge case: verify visits for different pets don't create false conflicts

### [ ] 3.0 Implement Clinic Capacity Enforcement

#### 3.0 Proof Artifact(s)

- Test: Unit tests for `ConflictDetectionService.checkCapacityConflict()` pass demonstrates capacity counting algorithm (max 5 concurrent)
- Test: Integration tests show capacity limit enforced with database queries demonstrates concurrent appointment detection
- Test: Boundary tests show 5th appointment allowed but 6th rejected demonstrates correct limit enforcement

#### 3.0 Tasks

- [ ] 3.1 (RED) Write failing test for capacity limit with 5 concurrent appointments - Test that 5th appointment at overlapping time is allowed
- [ ] 3.2 (RED) Write failing test for capacity limit with 6 concurrent appointments - Test that 6th appointment at overlapping time is rejected
- [ ] 3.3 (RED) Write failing test for capacity counting with various overlaps - Test that only truly concurrent appointments count toward limit
- [ ] 3.4 (GREEN) Add `findByDate(LocalDate date)` method to VisitRepository to get all visits on a given date
- [ ] 3.5 (GREEN) Implement `hasCapacityConflict(Visit newVisit)` method in ConflictDetectionService
- [ ] 3.6 (GREEN) Define constant `private static final int MAX_CONCURRENT_APPOINTMENTS = 5`
- [ ] 3.7 (GREEN) Count existing appointments that overlap with new visit using `doAppointmentsOverlap()` helper
- [ ] 3.8 (GREEN) Return conflict if count >= MAX_CONCURRENT_APPOINTMENTS
- [ ] 3.9 (GREEN) Run tests and verify all capacity tests pass
- [ ] 3.10 (REFACTOR) Create integration test with @DataJpaTest simulating 5 concurrent appointments in database
- [ ] 3.11 (REFACTOR) Test boundary: verify exactly 5 concurrent passes, 6 fails

### [ ] 4.0 Integrate Conflict Detection with Visit Booking Workflow

#### 4.0 Proof Artifact(s)

- Screenshot: Vet conflict error message displayed on booking form (`/owners/1/pets/1/visits/new`) demonstrates "Dr. [LastName] already has an appointment at this time" validation
- Screenshot: Pet conflict error message displayed on booking form demonstrates "Pet is already scheduled at this time" validation
- Screenshot: Capacity conflict error message displayed on booking form demonstrates "Clinic is at capacity for this time slot" validation
- Test: E2E Playwright tests pass showing conflict detection working through browser UI demonstrates end-to-end functionality
- Test: `VisitControllerTests` integration tests pass showing ConflictValidator integrated with form submission demonstrates controller integration
- Code: All 8 language files (`messages*.properties`) contain new conflict error message keys demonstrates complete i18n support

#### 4.0 Tasks

- [ ] 4.1 (RED) Write failing test in VisitControllerTests for vet conflict validation - Mock scenario where vet already has appointment, expect form rejection
- [ ] 4.2 (RED) Write failing test for pet conflict validation in VisitControllerTests
- [ ] 4.3 (RED) Write failing test for capacity conflict validation in VisitControllerTests
- [ ] 4.4 (GREEN) Create ConflictValidator class implementing Spring's Validator interface with @Component annotation
- [ ] 4.5 (GREEN) Inject ConflictDetectionService and OwnerRepository into ConflictValidator constructor
- [ ] 4.6 (GREEN) Implement `supports(Class<?> clazz)` method to return true for Visit.class
- [ ] 4.7 (GREEN) Implement `validate(Object target, Errors errors)` method - check vet conflict first, then pet, then capacity
- [ ] 4.8 (GREEN) Add error rejection: `errors.rejectValue("vet", "visit.conflict.vet", "Dr. {0} already has an appointment at this time")` with vet last name as parameter
- [ ] 4.9 (GREEN) Add error rejection: `errors.rejectValue("startTime", "visit.conflict.pet", "Pet is already scheduled at this time")`
- [ ] 4.10 (GREEN) Add error rejection: `errors.rejectValue("startTime", "visit.conflict.capacity", "Clinic is at capacity for this time slot")`
- [ ] 4.11 (GREEN) Add ConflictValidator to VisitController @InitBinder method: `dataBinder.addValidators(conflictValidator)`
- [ ] 4.12 (GREEN) Inject ConflictValidator into VisitController constructor
- [ ] 4.13 (GREEN) Add i18n message keys to all 8 language files: visit.conflict.vet, visit.conflict.pet, visit.conflict.capacity
- [ ] 4.14 (GREEN) Run VisitControllerTests and verify conflict validation tests pass
- [ ] 4.15 (GREEN) Create `conflict-detection.spec.ts` E2E test file in `e2e-tests/tests/features/`
- [ ] 4.16 (GREEN) Write E2E test: "should prevent vet double-booking" - Book visit, try to book overlapping visit for same vet, expect error
- [ ] 4.17 (GREEN) Write E2E test: "should prevent pet double-booking" - Book visit for pet, try to book overlapping visit for same pet with different vet, expect error
- [ ] 4.18 (GREEN) Write E2E test: "should enforce clinic capacity limit" - Book 5 concurrent visits, try to book 6th, expect error
- [ ] 4.19 (GREEN) Run E2E tests with `npm test` and verify all conflict tests pass
- [ ] 4.20 (GREEN) Capture screenshot of vet conflict error message on form
- [ ] 4.21 (GREEN) Capture screenshot of pet conflict error message on form
- [ ] 4.22 (GREEN) Capture screenshot of capacity conflict error message on form
- [ ] 4.23 (REFACTOR) Run full test suite (`./mvnw test` + `npm test`) and verify all tests pass
- [ ] 4.24 (REFACTOR) Generate JaCoCo coverage report and verify >90% coverage for ConflictDetectionService and ConflictValidator
- [ ] 4.25 (REFACTOR) Review all code for clarity, add comments where needed, ensure follows Spring Boot best practices
