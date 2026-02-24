# 11-spec-conflict-detection.md

## Introduction/Overview

This specification defines a conflict detection and prevention system for the Emerald Grove Veterinary Clinic appointment booking system. The system will prevent double-booking of veterinarians and pets, enforce clinic capacity limits, and provide clear error messaging when conflicts are detected. This ensures data integrity and realistic clinic operations while maintaining a simple, effective implementation.

## Goals

- Prevent veterinarians from having overlapping appointments at the same time
- Prevent pets from being scheduled for multiple appointments at the same time
- Enforce a maximum of 5 concurrent appointments to respect clinic capacity constraints
- Provide clear, specific error messages when conflicts are detected
- Integrate seamlessly with the existing visit booking workflow established in Spec 09 and 10

## User Stories

- **As a clinic administrator**, I want to prevent double-booking of veterinarians so that each vet can focus on one patient at a time without scheduling conflicts.

- **As a pet owner**, I want the system to prevent me from booking my pet for overlapping appointments so that I don't accidentally create conflicting schedules.

- **As a clinic manager**, I want the system to enforce capacity limits so that the clinic is never overbooked and staff can provide quality care to all patients.

- **As a receptionist**, I want clear error messages when conflicts occur so that I can quickly understand the issue and suggest alternative appointment times to clients.

## Demoable Units of Work

### Unit 1: Conflict Detection Service with Vet Overlap Prevention

**Purpose:** Create a reusable service that detects veterinarian scheduling conflicts, preventing double-booking of vets and ensuring each vet can only see one patient at a time.

**Functional Requirements:**
- The system shall provide a ConflictDetectionService class with methods for checking vet, pet, and capacity conflicts
- The system shall detect when a veterinarian has an existing appointment that overlaps with a proposed appointment time
- The system shall use inclusive overlap detection: appointments conflict if any minute overlaps (e.g., 9:00-9:30 conflicts with 9:15-9:45)
- The system shall allow back-to-back appointments (e.g., 9:00-9:30 followed by 9:30-10:00 is allowed)
- The system shall calculate appointment end time as startTime + durationMinutes
- The system shall return a clear conflict result indicating the vet is already booked

**Proof Artifacts:**
- Test Report: Unit tests for ConflictDetectionService.checkVetConflict() demonstrate algorithm correctness with various overlap scenarios
- Test Report: JaCoCo coverage report shows >90% coverage for ConflictDetectionService
- Code: ConflictDetectionService.java demonstrates clean service implementation following Spring Boot patterns

### Unit 2: Pet Overlap Prevention

**Purpose:** Prevent pets from being scheduled for multiple appointments at the same time, ensuring a pet cannot be double-booked with different veterinarians.

**Functional Requirements:**
- The system shall detect when a pet has an existing appointment that overlaps with a proposed appointment time
- The system shall use the same inclusive overlap algorithm as vet conflict detection
- The system shall prevent scenarios where different vets see the same pet at overlapping times
- The system shall allow the same owner to book different pets at the same time (not a conflict)
- The system shall return a clear conflict result indicating the pet is already scheduled

**Proof Artifacts:**
- Test Report: Unit tests for ConflictDetectionService.checkPetConflict() demonstrate algorithm correctness
- Test Report: Edge case tests show same owner can book multiple pets at same time but same pet cannot be double-booked
- Screenshot: Error message in UI showing "Pet is already scheduled at this time" demonstrates user-facing validation

### Unit 3: Clinic Capacity Enforcement

**Purpose:** Enforce a maximum of 5 concurrent appointments to prevent clinic overload and ensure quality patient care.

**Functional Requirements:**
- The system shall count the number of appointments that overlap with a proposed appointment time
- The system shall reject appointments if 5 or more concurrent appointments exist at that time
- The system shall use inclusive overlap detection to determine which appointments are concurrent
- The system shall return a clear conflict result indicating the clinic is at capacity

**Proof Artifacts:**
- Test Report: Unit tests for ConflictDetectionService.checkCapacityConflict() demonstrate capacity counting
- Test Report: Integration tests show capacity limit enforced with database queries
- Screenshot: Error message in UI showing "Clinic is at capacity for this time slot" demonstrates validation

### Unit 4: Integration with Visit Booking Workflow

**Purpose:** Integrate conflict detection into the existing visit booking form submission process with clear error messaging and i18n support.

**Functional Requirements:**
- The system shall create a ConflictValidator that implements Spring's Validator interface
- The system shall register ConflictValidator with VisitController via @InitBinder
- The system shall validate visits on form submission, checking vet, pet, and capacity conflicts in sequence
- The system shall reject the form and display field errors if any conflict is detected
- The system shall provide specific error messages: "Dr. [LastName] already has an appointment at this time", "Pet is already scheduled at this time", "Clinic is at capacity for this time slot"
- The system shall support i18n for all error messages across all 8 language files

**Proof Artifacts:**
- Screenshot: Vet conflict error message displayed on booking form demonstrates end-to-end integration
- Screenshot: Pet conflict error message displayed on booking form demonstrates validation
- Screenshot: Capacity conflict error message displayed on booking form demonstrates capacity enforcement
- Test Report: E2E Playwright tests show conflict detection working through browser UI
- Test Report: VisitController integration tests show conflict validation integrated with form submission

## Non-Goals (Out of Scope)

1. **Edit/Reschedule conflict checking**: Conflict detection for editing existing appointments will be handled in Issue 05 (Appointment Lifecycle Management)

2. **Proactive slot availability**: Showing available time slots before form submission will be handled in Issue 03 (Calendar Visualization System)

3. **Alternative time suggestions**: Suggesting alternative appointment times when conflicts occur is not included in this spec

4. **Cancelled appointment handling**: Appointment status tracking (cancelled, completed) will be handled in Issue 05

5. **Configurable capacity limits**: The clinic capacity is hardcoded at 5 concurrent appointments (can be refactored later if needed)

6. **Buffer time between appointments**: No mandatory buffer/gap time is enforced; back-to-back appointments are allowed

## Design Considerations

No specific UI design changes are required. The conflict detection will integrate with the existing visit booking form established in Spec 10, displaying validation errors using the same Bootstrap 5 validation styling already in place.

Error messages will appear as inline validation errors on the form, consistent with the existing validation pattern used by BusinessHoursValidator.

## Repository Standards

Follow established repository patterns and conventions:

- **Service Layer**: Create ConflictDetectionService as @Service component following Spring Boot conventions
- **Validation**: Implement ConflictValidator following Spring Validator interface pattern
- **Repository Queries**: Add methods to VisitRepository for finding overlapping appointments
- **Testing**: Follow TDD methodology (RED-GREEN-REFACTOR) with >90% coverage requirement
- **Code Organization**: Place ConflictDetectionService and ConflictValidator in `owner` package alongside Visit entity
- **Naming**: Use clear, descriptive method names (e.g., `findOverlappingVisitsForVet`, `hasVetConflict`)
- **i18n**: All error messages must have keys in all 8 language files (messages.properties through messages_tr.properties)

## Technical Considerations

**Conflict Detection Algorithm:**
- Use standard interval overlap formula: `(start1 < end2) AND (start2 < end1)`
- Calculate end time as `startTime.plus(durationMinutes, ChronoUnit.MINUTES)` or `startTime.plusMinutes(durationMinutes)`
- Query existing visits from database using Spring Data JPA @Query annotations

**Repository Methods:**
```java
// Find visits for a specific vet on a given date (for conflict checking)
List<Visit> findByVetAndDate(Vet vet, LocalDate date);

// Find visits for a specific pet on a given date (for conflict checking)
List<Visit> findByPetAndDate(Pet pet, LocalDate date);

// Find all visits on a given date (for capacity checking)
List<Visit> findByDate(LocalDate date);
```

**Service Architecture:**
- ConflictDetectionService contains business logic for overlap detection
- Service methods return Optional<ConflictResult> or similar to indicate conflict type
- ConflictValidator delegates to ConflictDetectionService and translates results to validation errors

**Integration Point:**
- Add ConflictValidator to VisitController @InitBinder alongside BusinessHoursValidator
- Validation runs after JSR-303 validation but before form submission

## Security Considerations

**Privacy Protection:**
- Error messages should not expose other clients' private information
- Use format: "Dr. [LastName] already has an appointment at this time" without revealing which pet or owner

**Data Integrity:**
- Conflict detection prevents data integrity issues caused by double-booking
- Validation occurs server-side (cannot be bypassed by client manipulation)

**No Sensitive Data in Proof Artifacts:**
- Screenshots should use test data only (e.g., "George Franklin", "Betty Davis")
- Do not include real client information in proof artifacts

## Success Metrics

1. **Zero double-bookings**: No veterinarian can be scheduled for overlapping appointments
2. **Zero pet conflicts**: No pet can be scheduled for multiple appointments at the same time
3. **Capacity enforcement**: Clinic never exceeds 5 concurrent appointments
4. **Test Coverage**: >90% line coverage for ConflictDetectionService and ConflictValidator
5. **Validation Integration**: 100% of visit form submissions run through conflict detection

## Open Questions

No open questions at this time. All requirements have been clarified through the questions process.
