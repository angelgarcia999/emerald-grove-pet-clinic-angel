# 08 Questions Round 1 - Scheduling System

Please answer each question below (select one or more options, or add your own notes). Feel free to add additional context under any question.

## 1. Core Functionality: What scheduling capabilities are needed?

What specific scheduling features should this system support?

- [x] (A) Book appointments at specific time slots (e.g., 9:00 AM, 2:30 PM)
- [x] (B) Assign specific veterinarians to appointments
- [x] (C) Detect and prevent double-booking conflicts
- [x] (D) View appointments in calendar format (day, week, month views)
- [ ] (E) Other (describe)

## 2. Appointment Time Slots: How should time scheduling work?

How granular should appointment scheduling be?

- [x] (A) Fixed time slots (e.g., 9:00, 9:30, 10:00) with 30-minute intervals
- [ ] (B) Fixed time slots with 15-minute intervals
- [ ] (C) Fixed time slots with 1-hour intervals
- [ ] (D) Custom time slot duration per appointment type (e.g., checkup = 30 min, surgery = 2 hours)
- [ ] (E) Other (describe)

## 3. Appointment Duration: How long are appointments?

Should appointments have variable durations or fixed durations?

- [x] (A) Fixed duration for all appointments (e.g., 30 minutes)
- [ ] (B) Variable duration based on appointment type (routine checkup, surgery, emergency)
- [ ] (C) User-selectable duration when booking
- [ ] (D) Default duration with option to override
- [ ] (E) Other (describe)

## 4. Conflict Detection Rules: What constitutes a scheduling conflict?

When should the system prevent an appointment from being booked?

- [x] (A) Same vet has overlapping appointments (vet-based conflict)
- [x] (B) Same pet has overlapping appointments (pet-based conflict)
- [x] (C) Both vet and pet conflicts should be detected
- [x] (D) Clinic capacity limit (e.g., max 5 concurrent appointments across all vets)
- [ ] (E) Other (describe)

## 5. Veterinarian Assignment: How are vets assigned to appointments?

Who decides which veterinarian handles an appointment?

- [x] (A) Owner selects vet when booking (dropdown or calendar view filtered by vet)
- [x] (B) System auto-assigns available vet based on specialty
- [x] (C) Clinic staff assigns vet after initial booking
- [x] (D) Any vet can be assigned, no specialty matching required
- [ ] (E) Other (describe)

## 6. Calendar Views: What calendar perspectives are needed?

What views should users have to visualize appointments?

- [x] (A) Day view - shows all appointments for a specific day
- [x] (B) Week view - shows appointments across 7 days
- [x] (C) Month view - shows appointments for entire month
- [x] (D) Veterinarian-specific view - filter by specific vet's schedule
- [ ] (E) Other (describe)

## 7. Booking Workflow: How should appointment booking work?

What is the user flow for creating an appointment?

- [x] (A) Owner logs in → selects pet → selects date/time → selects vet → confirms
- [x] (B) Owner logs in → selects vet → views vet's availability → selects time slot → selects pet → confirms
- [x] (C) Clinic staff books on behalf of owner (admin interface)
- [x] (D) Integration with existing "Add Visit" form, enhanced with time and vet selection
- [ ] (E) Other (describe)

## 8. Editing and Cancellation: Can appointments be modified?

Should users be able to edit or cancel appointments?

- [x] (A) Owners can cancel appointments up to 24 hours in advance
- [x] (B) Owners can reschedule appointments (check conflicts for new slot)
- [x] (C) Only clinic staff can modify/cancel appointments
- [x] (D) No editing - must cancel and rebook
- [ ] (E) Other (describe)

## 9. Appointment Types: Are there different kinds of appointments?

Should appointments have categories or types?

- [x] (A) Yes - types like "Routine Checkup", "Surgery", "Emergency", "Vaccination"
- [ ] (B) Yes - appointment type determines duration and required vet specialty
- [ ] (C) No - all appointments are generic visits
- [ ] (D) Use existing Visit "description" field for categorization
- [ ] (E) Other (describe)

## 10. Business Hours: What are the clinic's operating hours?

When can appointments be scheduled?

- [x] (A) Fixed hours (e.g., Monday-Friday 9:00 AM - 5:00 PM, Saturday 9:00 AM - 1:00 PM)
- [ ] (B) Configurable hours per day of week
- [ ] (C) No restriction - allow booking at any time for now
- [ ] (D) Block out lunch hours (e.g., 12:00 PM - 1:00 PM unavailable)
- [ ] (E) Other (describe)

## 11. Conflict Resolution: What happens when a conflict is detected?

How should the system handle scheduling conflicts?

- [x] (A) Show error message and prevent booking
- [ ] (B) Show error message with suggested alternative time slots
- [x] (C) Show visual indicator on calendar (e.g., red X for unavailable times)
- [ ] (D) Allow overbooking but show warning (soft conflict)
- [ ] (E) Other (describe)

## 12. Past Appointments: How should completed appointments be handled?

What should happen to appointments after they occur?

- [x] (A) Keep as historical record, mark as "completed"
- [ ] (B) Keep as historical record, require staff to add visit notes
- [ ] (C) Appointments remain as visits (existing Visit entity behavior)
- [ ] (D) Archive appointments older than X days
- [ ] (E) Other (describe)

## 13. Notifications: Should users receive reminders?

Should the system send appointment reminders?

- [ ] (A) Email reminders 24 hours before appointment
- [ ] (B) SMS reminders (if phone number available)
- [ ] (C) In-app notifications only (no external messaging)
- [x] (D) No reminders - out of scope for initial version
- [ ] (E) Other (describe)

## 14. Proof Artifacts: What will demonstrate this feature works?

What evidence should be provided to validate the scheduling system?

- [x] (A) Screenshots of calendar view showing appointments
- [x] (B) Screenshot of booking form with date/time/vet selection
- [x] (C) Screenshot of conflict detection error message
- [x] (D) Test report showing conflict detection algorithm works
- [ ] (E) Other (describe)

## 15. Technical Constraints: Are there any technical limitations?

Any specific technical requirements or constraints?

- [x] (A) Must work with existing H2, MySQL, and PostgreSQL databases
- [x] (B) Must maintain existing Visit entity structure for backward compatibility
- [x] (C) Calendar UI should use existing Bootstrap 5 framework
- [x] (D) Must follow strict TDD with >90% coverage
- [ ] (E) Other (describe)
