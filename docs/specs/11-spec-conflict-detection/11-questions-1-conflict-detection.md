# 11 Questions Round 1 - Conflict Detection & Prevention

Please answer each question below (select one or more options, or add your own notes). Feel free to add additional context under any question.

## 1. Conflict Detection Scope

The system needs to prevent double-booking. Which conflict types should be enforced?

- [ ] (A) Vet conflicts only - Prevent same vet from having overlapping appointments
- [ ] (B) Pet conflicts only - Prevent same pet from having overlapping appointments
- [ ] (C) Clinic capacity only - Enforce maximum concurrent appointment limit
- [x] (D) All three types (Vet + Pet + Capacity) - Comprehensive conflict prevention
- [ ] (E) Other (describe)

**Rationale**: All three are essential for data integrity and realistic clinic operations. Each is straightforward to implement independently.

## 2. Time Overlap Definition

How should the system determine if two appointments overlap?

- [ ] (A) Exact time match only - Conflicts only if same date and exact start time
- [x] (B) Inclusive overlap - Appointments conflict if any minute overlaps (e.g., 9:00-9:30 conflicts with 9:15-9:45)
- [ ] (C) Buffer time - Include buffer between appointments (e.g., 10-minute gap required)
- [ ] (D) End-to-end - 9:00-9:30 and 9:30-10:00 are considered conflicting (no back-to-back allowed)
- [ ] (E) Other (describe)

**Rationale**: Standard interval overlap algorithm. Simple logic: `(start1 < end2) AND (start2 < end1)`. Allows efficient back-to-back scheduling (9:30 appointment can follow 9:00).

## 3. Clinic Capacity Limit

What is the maximum number of concurrent appointments the clinic can handle?

- [x] (A) 5 concurrent appointments (as stated in issue)
- [ ] (B) Different limits for different time periods (e.g., more on weekdays, fewer on Saturdays)
- [ ] (C) Configurable limit (set via application property)
- [ ] (D) No limit (remove this requirement)
- [ ] (E) Other (describe)

**Rationale**: Hardcoded constant is simplest. Can be refactored to config later if needed. Fixed limit of 5 matches issue requirement.

## 4. Conflict Detection Timing

When should conflict detection occur?

- [x] (A) Real-time validation during form submission only
- [ ] (B) Proactive check before form display (show available slots)
- [ ] (C) Both validation and proactive checking
- [ ] (D) Background job that flags conflicts after booking
- [ ] (E) Other (describe)

**Rationale**: Simplest implementation - validate on form submission like existing BusinessHoursValidator. No UI changes needed. Proactive slot display can be added later in calendar views (Issue 03).

## 5. Error Messaging

What level of detail should conflict error messages provide?

- [ ] (A) Generic message - "This appointment conflicts with an existing booking"
- [x] (B) Specific conflict type - "Dr. Smith already has an appointment at this time"
- [ ] (C) Detailed information - "Dr. Smith has an appointment with Fluffy from 9:00-9:30 AM"
- [ ] (D) Suggestions - "This time is not available. Alternative times: 10:00 AM, 11:30 AM"
- [ ] (E) Other (describe)

**Rationale**: Informative without exposing other clients' private information. Shows what's wrong and who/what is involved. Simple to implement with clear i18n message keys.

## 6. Edit Appointment Handling

Should conflict detection apply when editing existing appointments?

- [ ] (A) Yes - Same rules apply when rescheduling an appointment
- [ ] (B) Exclude self - Allow moving appointment without conflicting with itself
- [ ] (C) No validation - Editing bypasses conflict checks (not recommended)
- [x] (D) Editing is out of scope for this spec (will be handled in Issue 05)
- [ ] (E) Other (describe)

**Rationale**: Keep this spec focused on create-time validation. Issue 05 (Appointment Lifecycle) will handle edit/reschedule with conflict re-validation. Simplifies scope.

## 7. Service Layer Architecture

Where should the conflict detection logic reside?

- [x] (A) New ConflictDetectionService - Standalone service with business logic
- [ ] (B) Extend existing VisitValidator - Add to validation layer
- [ ] (C) Repository layer - Use complex SQL queries for conflict detection
- [ ] (D) Controller layer - Keep logic close to HTTP handling
- [ ] (E) Other (describe)

**Rationale**: Clean separation of concerns. Service can be injected into validators, controllers, or future calendar views. Easy to test in isolation. Follows Spring Boot best practices.

## 8. Testing Priorities

Which test scenarios are most critical?

- [ ] (A) Unit tests for conflict algorithms (vet, pet, capacity)
- [ ] (B) Integration tests with database (concurrent bookings)
- [ ] (C) E2E browser tests showing error messages
- [ ] (D) Performance tests (conflict check speed with large datasets)
- [x] (E) All of the above
- [ ] (F) Other (describe)

**Rationale**: TDD requires comprehensive testing. Unit tests for service logic, integration for repository queries, E2E for user experience. All are necessary for >90% coverage target.

## 9. Proof Artifacts

What evidence will best demonstrate conflict detection works?

- [ ] (A) Screenshots of error messages in UI
- [ ] (B) Test reports showing conflict algorithms pass
- [ ] (C) Database queries showing overlapping appointments detected
- [ ] (D) Integration test results (unit + E2E)
- [x] (E) All of the above
- [ ] (F) Other (describe)

**Rationale**: Complete evidence chain from algorithm to UI. Screenshots show UX, test reports prove correctness, integration tests validate database layer.

## 10. Edge Cases

Which edge cases should be explicitly handled?

- [ ] (A) Same owner booking multiple pets at same time (should be allowed)
- [ ] (B) Different vets seeing same pet at overlapping times (should be prevented)
- [ ] (C) Appointment that spans midnight (e.g., 23:45-00:15) - probably not relevant given business hours
- [ ] (D) Cancelled appointments not counting toward conflicts
- [x] (E) All relevant edge cases above
- [ ] (F) Other (describe)

**Rationale**: (A) and (B) are important for multi-pet households and data integrity. (C) is impossible due to business hours (closes at 5PM). (D) will be relevant when appointment lifecycle is added, but for now all visits are active.

---

**Instructions:**
1. Check the boxes for your preferred options (you can select multiple)
2. Add any additional notes or clarifications under each question
3. Save this file when complete
4. Let me know when you're ready for me to continue
