# 09-spec-time-based-appointments.md

## Introduction/Overview

This specification defines the foundation for the Emerald Grove Veterinary Clinic scheduling system by enhancing the existing Visit entity to support time-based appointments with veterinarian assignment. Currently, visits only track the date and description, making it impossible to schedule specific appointment times or assign veterinarians. This enhancement transforms the simple visit tracking system into a proper appointment scheduling foundation that enables time slot booking, vet assignment, and business hours enforcement.

## Goals

- Enable appointment booking at specific times (not just dates) with 30-minute time slot granularity
- Associate veterinarians with appointments to track which vet handles each visit
- Enforce clinic business hours (Monday-Friday 9:00 AM - 5:00 PM, Saturday 9:00 AM - 1:00 PM)
- Maintain backward compatibility with existing Visit entity structure and relationships
- Provide database schema that supports all databases (H2, MySQL, PostgreSQL)

## User Stories

**As a pet owner**, I want to book appointments at specific times (not just dates) so that I can plan my day around my pet's veterinary visit.

**As a pet owner**, I want to select which veterinarian I prefer for my pet's appointment so that my pet sees a familiar vet or one with specific expertise.

**As a veterinarian**, I want appointments assigned to me so that I know my schedule and can prepare for upcoming visits.

**As a clinic receptionist**, I want the system to prevent booking appointments outside business hours so that I don't accidentally schedule visits when the clinic is closed.

**As a system administrator**, I want the appointment time fields stored in the database so that scheduling features can be built on this foundation.

## Demoable Units of Work

### Unit 1: Enhanced Visit Entity with Time Fields

**Purpose**: Extend the Visit entity to support time-based scheduling by adding start time and duration fields to the database and domain model.

**Functional Requirements:**
- The system shall add a `start_time` column to the visits table storing appointment time as TIME type
- The system shall add a `duration_minutes` column to the visits table storing appointment length as INTEGER
- The system shall set default duration to 30 minutes for all appointments
- The Visit entity shall expose `getStartTime()` and `setStartTime(LocalTime)` methods
- The Visit entity shall expose `getDuration()` and `setDuration(Integer)` methods
- The system shall maintain existing `visit_date` column and functionality for backward compatibility
- Database migrations shall work across H2, MySQL, and PostgreSQL without modification

**Proof Artifacts:**
- Database schema diagram: Shows visits table with new `start_time` and `duration_minutes` columns demonstrates data model changes
- Migration script: Flyway migration file (V#__add_appointment_time_fields.sql) demonstrates database schema update
- Test report: VisitEntityTests passing with >90% coverage demonstrates entity changes work correctly

### Unit 2: Veterinarian Assignment to Appointments

**Purpose**: Enable assigning specific veterinarians to appointments by adding a foreign key relationship from Visit to Vet.

**Functional Requirements:**
- The system shall add a `vet_id` column to the visits table as a foreign key to vets table
- The Visit entity shall have a many-to-one relationship with Vet entity
- The Visit entity shall expose `getVet()` and `setVet(Vet)` methods
- The system shall allow null vet_id for backward compatibility with existing visits
- The system shall cascade changes appropriately (delete vet should not delete appointments)
- Repository queries shall support fetching visits with eager-loaded vet information

**Proof Artifacts:**
- Database schema diagram: Shows visits.vet_id foreign key relationship to vets table demonstrates vet assignment capability
- Test report: VisitRepositoryTests with vet association tests passing demonstrates relationship works correctly
- Integration test: Visit can be saved with vet assignment and retrieved with vet information demonstrates end-to-end functionality

### Unit 3: Enhanced Booking Form with Time and Vet Selection

**Purpose**: Update the visit booking form to allow users to select appointment time slots and choose a veterinarian.

**Functional Requirements:**
- The booking form shall display a time slot selector with 30-minute intervals (9:00 AM, 9:30 AM, 10:00 AM, etc.)
- The booking form shall display a veterinarian dropdown populated from VetRepository
- The form shall pre-populate with current date and earliest available time slot
- The form shall validate that selected time is not empty
- The form shall validate that selected vet is not empty
- The system shall save the selected time and vet when form is submitted
- The form shall display validation errors if time or vet is missing

**Proof Artifacts:**
- Screenshot: Enhanced booking form showing time slot dropdown and vet selector demonstrates UI changes
- Screenshot: Form validation error when time or vet not selected demonstrates validation works
- Test report: VisitControllerTests for new booking form passing demonstrates controller logic works
- E2E test: Playwright test for booking appointment with time and vet demonstrates complete user flow

### Unit 4: Business Hours Validation

**Purpose**: Enforce clinic operating hours to prevent appointments from being scheduled outside business hours.

**Functional Requirements:**
- The system shall validate that appointment time is within Monday-Friday 9:00 AM - 5:00 PM
- The system shall validate that appointment time is within Saturday 9:00 AM - 1:00 PM
- The system shall reject appointments on Sundays with error message "Clinic is closed on Sundays"
- The system shall reject appointments before 9:00 AM with error message "Clinic opens at 9:00 AM"
- The system shall reject appointments after 5:00 PM on weekdays with error message "Clinic closes at 5:00 PM"
- The system shall reject appointments after 1:00 PM on Saturdays with error message "Clinic closes at 1:00 PM on Saturdays"
- Validation errors shall be displayed to the user on the booking form

**Proof Artifacts:**
- Test report: BusinessHoursValidatorTests with all edge cases passing demonstrates validation logic works
- Screenshot: Error message when attempting to book outside business hours demonstrates user-facing validation
- Unit tests: Cover Monday-Friday hours, Saturday hours, Sunday rejection, before/after hours demonstrates comprehensive coverage

## Non-Goals (Out of Scope)

1. **Conflict detection** - Preventing double-booking of vets or pets is handled in Spec 2 (Issue 02)
2. **Calendar visualization** - Viewing appointments in calendar format is handled in Spec 3 (Issue 03)
3. **Appointment types** - Categorizing appointments (routine, surgery, emergency) is handled in Spec 4 (Issue 04)
4. **Edit/cancel functionality** - Modifying or canceling appointments is handled in Spec 5 (Issue 05)
5. **Variable appointment duration** - All appointments are fixed at 30 minutes; variable durations are out of scope
6. **Automatic vet assignment** - System does not auto-assign vets based on specialty or availability
7. **Appointment reminders** - Email/SMS notifications are explicitly out of scope

## Design Considerations

**Time Slot Selector UI**: Use HTML5 `<input type="time">` with datalist or custom dropdown component styled with Bootstrap 5. Time slots should be generated dynamically based on business hours (9:00 AM - 5:00 PM weekdays, 9:00 AM - 1:00 PM Saturdays) in 30-minute intervals.

**Vet Selector UI**: Standard Bootstrap dropdown showing vet name and specialties (e.g., "Dr. Smith (Dentistry, Surgery)"). Populate from VetRepository.findAll() and display in alphabetical order by last name.

**Form Layout**: Integrate time and vet selection into existing "Add Visit" form (`createOrUpdateVisitForm.html`) below the date picker. Maintain consistent styling with existing form elements.

**Responsive Design**: Ensure time and vet selectors work on mobile devices (touch-friendly dropdowns, appropriate sizing).

## Repository Standards

Follow established Spring Boot PetClinic patterns:

- **Entity Conventions**: Extend BaseEntity, use JPA annotations, follow existing naming patterns (e.g., `visit_date` → `start_time`)
- **Repository Pattern**: Use Spring Data Repository interfaces with custom queries as needed
- **Controller Pattern**: Follow existing controller structure (VisitController) with GET/POST mappings, validation, and redirect patterns
- **Testing Standards**: Maintain >90% test coverage with unit tests (@WebMvcTest, @DataJpaTest) and integration tests (@SpringBootTest)
- **Validation**: Use Bean Validation annotations (@NotNull, @FutureOrPresent) and custom validators as needed
- **Database Migrations**: Use Flyway migration scripts with sequential versioning (V#__description.sql)
- **Thymeleaf Templates**: Follow existing template structure with fragments, form binding, and error display patterns

## Technical Considerations

**Database Type Compatibility**: Use `TIME` type for `start_time` column which maps to `java.time.LocalTime` and is supported by H2, MySQL, and PostgreSQL. Avoid database-specific time functions.

**Time Zone Handling**: Store times in clinic local time (no time zone conversion). All appointment times represent the clinic's local operating hours.

**Existing Visit Data**: Existing visits in the database will have NULL for `start_time`, `duration_minutes`, and `vet_id`. This is acceptable for backward compatibility. Display logic should handle null values gracefully.

**JPA Relationships**: Use `@ManyToOne` with `FetchType.EAGER` for Visit → Vet relationship to ensure vet information is always loaded with visits. Use `CascadeType.MERGE` to avoid accidental vet deletions.

**Form Binding**: Use Spring's form binding with `@ModelAttribute` and `@Valid` for automatic form-to-entity mapping and validation.

**Migration Strategy**: Create one Flyway migration script that adds all three columns (`start_time`, `duration_minutes`, `vet_id`) in a single transaction to ensure atomic schema changes.

## Security Considerations

**Input Validation**: Validate all user input (time, vet selection) on both client and server side to prevent injection attacks and malformed data.

**SQL Injection Prevention**: Use JPA parameterized queries and Spring Data Repository methods (no raw SQL) to prevent SQL injection.

**Authorization**: Current PetClinic has no authentication system. Assume all users can book appointments. Authorization for appointment management (edit/cancel) will be addressed in Spec 5 (Issue 05).

**Data Privacy**: Visit data including appointment times and assigned vet is considered sensitive medical information. Ensure it's not exposed in logs or error messages beyond what's necessary for debugging.

**Proof Artifacts Security**: Screenshots and test data should use fictional owner/pet names. Do not include real patient information in committed artifacts.

## Success Metrics

1. **Test Coverage**: >90% line coverage for Visit entity, VisitController, and validation logic
2. **Database Compatibility**: Migration scripts execute successfully on H2, MySQL, and PostgreSQL without errors
3. **User Workflow Completion**: Users can successfully book an appointment with date, time, and vet selection in <60 seconds
4. **Validation Effectiveness**: 100% of out-of-hours appointment attempts are rejected with clear error messages
5. **Backward Compatibility**: Existing visits without time/vet data continue to display and function correctly

## Open Questions

No open questions at this time. All requirements have been clarified through the questions process.
