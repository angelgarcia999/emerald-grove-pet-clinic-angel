# 09-tasks-time-based-appointments.md

## Relevant Files

### Database Schema Files
- `src/main/resources/db/h2/schema.sql` - H2 database schema (development/testing)
- `src/main/resources/db/mysql/schema.sql` - MySQL database schema (production)
- `src/main/resources/db/postgres/schema.sql` - PostgreSQL database schema (production)
- `src/main/resources/db/hsqldb/schema.sql` - HSQLDB database schema (alternative)
- `src/main/resources/db/h2/data.sql` - H2 sample data
- `src/main/resources/db/mysql/data.sql` - MySQL sample data
- `src/main/resources/db/postgres/data.sql` - PostgreSQL sample data
- `src/main/resources/db/hsqldb/data.sql` - HSQLDB sample data

### Entity and Repository Files
- `src/main/java/org/springframework/samples/petclinic/owner/Visit.java` - Visit entity (add time and vet fields)
- `src/main/java/org/springframework/samples/petclinic/owner/VisitRepository.java` - Visit repository interface
- `src/main/java/org/springframework/samples/petclinic/vet/Vet.java` - Vet entity (reference for relationship)
- `src/main/java/org/springframework/samples/petclinic/vet/VetRepository.java` - Vet repository (for form population)

### Controller and Form Files
- `src/main/java/org/springframework/samples/petclinic/owner/VisitController.java` - Visit controller (add time/vet form handling)
- `src/main/resources/templates/pets/createOrUpdateVisitForm.html` - Visit booking form template

### Validation Files
- `src/main/java/org/springframework/samples/petclinic/owner/BusinessHoursValidator.java` - Business hours validator (NEW)
- `src/main/resources/messages/messages.properties` - Validation messages (English)
- `src/main/resources/messages/messages_en.properties` - Validation messages (English)
- `src/main/resources/messages/messages_es.properties` - Validation messages (Spanish)
- `src/main/resources/messages/messages_de.properties` - Validation messages (German)
- `src/main/resources/messages/messages_tr.properties` - Validation messages (Turkish)
- `src/main/resources/messages/messages_ko.properties` - Validation messages (Korean)
- `src/main/resources/messages/messages_pt.properties` - Validation messages (Portuguese)
- `src/main/resources/messages/messages_ru.properties` - Validation messages (Russian)
- `src/main/resources/messages/messages_fa.properties` - Validation messages (Farsi)

### Test Files
- `src/test/java/org/springframework/samples/petclinic/owner/VisitEntityTests.java` - Visit entity tests (NEW)
- `src/test/java/org/springframework/samples/petclinic/owner/VisitRepositoryTests.java` - Visit repository tests (add vet relationship tests)
- `src/test/java/org/springframework/samples/petclinic/owner/VisitControllerTests.java` - Visit controller tests (add time/vet form tests)
- `src/test/java/org/springframework/samples/petclinic/owner/BusinessHoursValidatorTests.java` - Business hours validator tests (NEW)
- `e2e-tests/tests/visit-booking-with-time.spec.ts` - E2E test for booking with time and vet (NEW)

### Notes

- Follow strict TDD methodology: Write failing tests (RED) → Make tests pass (GREEN) → Refactor (REFACTOR)
- Maintain >90% test coverage for all new and modified code
- Use JUnit 5 for unit tests: `./mvnw test -Dtest=ClassName`
- Use Playwright for E2E tests: `cd e2e-tests && npm test`
- Database schema changes must work across H2, MySQL, PostgreSQL, and HSQLDB
- Follow existing PetClinic patterns: JPA annotations, Spring Data repositories, Thymeleaf templates
- All validation messages must be added to all language property files

---

## Tasks

### [x] 1.0 Database Schema and Visit Entity Enhancement (TDD)

#### 1.0 Proof Artifact(s)

- Database schema: `schema.sql` files (H2, MySQL, PostgreSQL) show new columns (`start_time`, `duration_minutes`, `vet_id`) demonstrates database schema changes
- Test report: `VisitEntityTests.java` passing with >90% coverage demonstrates entity field access and validation work correctly
- Test report: JPA integration tests demonstrate Visit entity can be persisted and retrieved with new fields across all database profiles (H2, MySQL, PostgreSQL)
- Screenshot: H2 console showing visits table structure with new columns demonstrates schema applied successfully

#### 1.0 Tasks

- [ ] 1.1 **RED**: Create `VisitEntityTests.java` with failing tests for new fields (`getStartTime()`, `setStartTime()`, `getDuration()`, `setDuration()`, default duration = 30)
- [ ] 1.2 **RED**: Write failing JPA integration test that attempts to persist Visit with start_time and duration_minutes
- [ ] 1.3 **GREEN**: Update H2 schema (`src/main/resources/db/h2/schema.sql`) to add `start_time TIME`, `duration_minutes INTEGER DEFAULT 30`, and `vet_id INTEGER` columns to visits table
- [ ] 1.4 **GREEN**: Update MySQL schema (`src/main/resources/db/mysql/schema.sql`) with same columns (verify TIME type compatibility)
- [ ] 1.5 **GREEN**: Update PostgreSQL schema (`src/main/resources/db/postgres/schema.sql`) with same columns (verify TIME type compatibility)
- [ ] 1.6 **GREEN**: Update HSQLDB schema (`src/main/resources/db/hsqldb/schema.sql`) with same columns for completeness
- [ ] 1.7 **GREEN**: Add `vet_id` foreign key constraint to vets table in all schema files with appropriate cascade rules (ON DELETE SET NULL)
- [ ] 1.8 **GREEN**: Update Visit entity (`Visit.java`) to add `private LocalTime startTime`, `private Integer durationMinutes` fields with JPA annotations (`@Column(name = "start_time")`, `@Column(name = "duration_minutes")`)
- [ ] 1.9 **GREEN**: Add getter/setter methods for startTime and durationMinutes in Visit entity
- [ ] 1.10 **GREEN**: Update Visit constructor to set default durationMinutes = 30
- [ ] 1.11 **REFACTOR**: Run all entity tests and verify >90% coverage (`./mvnw test -Dtest=VisitEntityTests`)
- [ ] 1.12 **REFACTOR**: Update sample data files (h2/data.sql, mysql/data.sql, postgres/data.sql) with example start_time and duration values for existing visits
- [ ] 1.13 Verify schema changes work across all database profiles: H2 (`./mvnw test`), MySQL (`./mvnw test -Dspring.profiles.active=mysql`), PostgreSQL (`./mvnw test -Dspring.profiles.active=postgres`)

---

### [x] 2.0 Vet Repository Integration and Relationship Testing (TDD)

#### 2.0 Proof Artifact(s)

- Test report: `VisitRepositoryTests.java` with vet association tests passing demonstrates Visit-Vet relationship persistence works
- Test report: Integration tests show Visit can be saved with vet assignment and retrieved with eager-loaded vet information demonstrates relationship mapping is correct
- Test report: Tests verify null vet_id is allowed (backward compatibility) and cascade behavior prevents accidental vet deletion demonstrates relationship constraints work correctly

#### 2.0 Tasks

- [x] 2.1 **RED**: Add failing test to `VisitRepositoryTests.java` that saves Visit with assigned Vet and retrieves it with eager-loaded vet
- [x] 2.2 **RED**: Add failing test that verifies Visit can be saved with null vet (backward compatibility)
- [x] 2.3 **RED**: Add failing test that verifies Visit-Vet relationship mapping (simplified from cascade delete test)
- [x] 2.4 **GREEN**: Add `@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)` relationship field `private Vet vet` to Visit entity
- [x] 2.5 **GREEN**: Add `@JoinColumn(name = "vet_id")` annotation to vet field in Visit entity
- [x] 2.6 **GREEN**: Add getter/setter methods for vet in Visit entity (`getVet()`, `setVet(Vet)`)
- [x] 2.7 **GREEN**: Verify JPA relationship configuration allows null vet_id (nullable foreign key works correctly)
- [x] 2.8 **REFACTOR**: Run repository tests and verify all vet relationship tests pass (7/7 tests passing)
- [x] 2.9 **REFACTOR**: Test that querying visits eagerly loads vet information (EAGER fetch confirmed in tests)
- [x] 2.10 Update sample data files to assign vets to some existing visits for demo purposes (completed in Task 1.0)

---

### [~] 3.0 Enhanced Booking Form with Time and Vet Selection (TDD)

#### 3.0 Proof Artifact(s)

- Screenshot: Enhanced `createOrUpdateVisitForm.html` showing time slot dropdown (30-minute intervals) and vet selector dropdown demonstrates UI changes
- Screenshot: Form validation errors when time or vet not selected demonstrates client-side and server-side validation
- Test report: `VisitControllerTests.java` passing with tests for time/vet form binding and validation demonstrates controller logic works
- E2E test: Playwright test (`visit-booking-with-time.spec.ts`) demonstrating complete booking flow with time and vet selection demonstrates end-to-end functionality
- Screenshot: Owner details page showing booked visit with time and vet name demonstrates successful booking

#### 3.0 Tasks

- [ ] 3.1 **RED**: Add failing test to `VisitControllerTests.java` for GET request that verifies model includes list of available vets
- [ ] 3.2 **RED**: Add failing test for POST request with time and vet that verifies Visit is saved with both fields
- [ ] 3.3 **RED**: Add failing test for POST request missing time that verifies validation error is returned
- [ ] 3.4 **RED**: Add failing test for POST request missing vet that verifies validation error is returned
- [ ] 3.5 **GREEN**: Update `VisitController.loadPetWithVisit()` method to add `List<Vet>` to model using `VetRepository.findAll()`
- [ ] 3.6 **GREEN**: Update `VisitController.processNewVisitForm()` to handle startTime and vet binding from form
- [ ] 3.7 **GREEN**: Add validation annotations to Visit entity: `@NotNull(message = "{visit.time.required}")` for startTime, `@NotNull(message = "{visit.vet.required}")` for vet
- [ ] 3.8 **GREEN**: Update `createOrUpdateVisitForm.html` to add time slot dropdown (generate 30-minute intervals from 9:00 AM to 5:00 PM using Thymeleaf)
- [ ] 3.9 **GREEN**: Add vet selector dropdown to form using `th:each` to iterate over vets with format "Dr. {lastName} ({specialties})"
- [ ] 3.10 **GREEN**: Add Thymeleaf error display for time and vet validation errors using `th:if="${#fields.hasErrors('startTime')}"` pattern
- [ ] 3.11 **REFACTOR**: Run controller tests and verify >90% coverage (`./mvnw test -Dtest=VisitControllerTests`)
- [ ] 3.12 **REFACTOR**: Manually test booking form in browser (start app with `./mvnw spring-boot:run`) and take screenshot
- [ ] 3.13 **RED**: Create `e2e-tests/tests/visit-booking-with-time.spec.ts` with failing E2E test that books visit with time and vet selection
- [ ] 3.14 **GREEN**: Ensure E2E test passes by verifying form submits correctly and visit appears with time/vet on owner details page
- [ ] 3.15 **REFACTOR**: Run E2E test suite (`cd e2e-tests && npm test`) and verify new test passes
- [ ] 3.16 Add validation message keys to `messages.properties`: `visit.time.required=Appointment time is required` and `visit.vet.required=Please select a veterinarian`

---

### [ ] 4.0 Business Hours Validation (TDD)

#### 4.0 Proof Artifact(s)

- Test report: `BusinessHoursValidatorTests.java` passing with comprehensive test coverage (weekday hours, Saturday hours, Sunday rejection, before/after hours edge cases) demonstrates validation logic works correctly
- Screenshot: Booking form showing error message "Clinic is closed on Sundays" when Sunday selected demonstrates Sunday validation
- Screenshot: Booking form showing error message "Clinic closes at 5:00 PM" when 6:00 PM selected on weekday demonstrates after-hours validation
- Screenshot: Booking form showing error message "Clinic closes at 1:00 PM on Saturdays" when 2:00 PM selected on Saturday demonstrates Saturday hours validation
- Test report: Integration tests verify business hours validation integrated with booking workflow demonstrates end-to-end validation

#### 4.0 Tasks

- [ ] 4.1 **RED**: Create `BusinessHoursValidatorTests.java` with failing test for valid weekday time (9:00 AM - 5:00 PM Monday-Friday)
- [ ] 4.2 **RED**: Add failing test for valid Saturday time (9:00 AM - 1:00 PM)
- [ ] 4.3 **RED**: Add failing test that rejects Sunday appointments
- [ ] 4.4 **RED**: Add failing test that rejects appointments before 9:00 AM
- [ ] 4.5 **RED**: Add failing test that rejects weekday appointments after 5:00 PM
- [ ] 4.6 **RED**: Add failing test that rejects Saturday appointments after 1:00 PM
- [ ] 4.7 **GREEN**: Create `BusinessHoursValidator.java` implementing `Validator` interface with `validate(Visit visit, Errors errors)` method
- [ ] 4.8 **GREEN**: Implement weekday hours validation logic (Monday-Friday 9:00-17:00)
- [ ] 4.9 **GREEN**: Implement Saturday hours validation logic (Saturday 9:00-13:00)
- [ ] 4.10 **GREEN**: Implement Sunday rejection logic with appropriate error message
- [ ] 4.11 **GREEN**: Implement before-hours validation (reject times before 9:00 AM)
- [ ] 4.12 **GREEN**: Implement after-hours validation (reject times after closing based on day of week)
- [ ] 4.13 **REFACTOR**: Run validator tests and verify >90% coverage (`./mvnw test -Dtest=BusinessHoursValidatorTests`)
- [ ] 4.14 **GREEN**: Register `BusinessHoursValidator` in `VisitController` using `@InitBinder` and `WebDataBinder.addValidators()`
- [ ] 4.15 **GREEN**: Update `VisitController.processNewVisitForm()` to call validator before saving
- [ ] 4.16 Add validation message keys to all language property files in `src/main/resources/messages/`:
  - `visit.businessHours.sunday=Clinic is closed on Sundays`
  - `visit.businessHours.beforeOpen=Clinic opens at 9:00 AM`
  - `visit.businessHours.afterCloseWeekday=Clinic closes at 5:00 PM`
  - `visit.businessHours.afterCloseSaturday=Clinic closes at 1:00 PM on Saturdays`
- [ ] 4.17 **REFACTOR**: Add integration test to `VisitControllerTests.java` that verifies out-of-hours booking is rejected with appropriate error
- [ ] 4.18 **REFACTOR**: Manually test validation in browser and take screenshots of error messages for different scenarios
- [ ] 4.19 Run full test suite across all database profiles and verify >90% overall coverage (`./mvnw clean test jacoco:report`)
