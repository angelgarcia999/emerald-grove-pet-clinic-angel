# 10 Questions Round 1 - Visit Booking UI Enhancement

Please answer each question below (select one or more options, or add your own notes). Feel free to add additional context under any question.

## 1. Responsive Behavior

The mockup shows a two-column desktop layout. How should this behave on mobile/tablet?

- [x] (A) Stack columns vertically on mobile (pet context on top, form below)
- [ ] (B) Keep side-by-side but shrink columns (may be cramped)
- [ ] (C) Show form only, with collapsible pet context section
- [ ] (D) Different mobile layout entirely (describe)
- [ ] (E) Other (describe)

**Notes:**
This follows Bootstrap 5 best practices already used in the application. The pet context provides essential information users need before filling the form, so it should remain visible above. Use Bootstrap grid classes like `col-lg-4` for left column and `col-lg-8` for right column, which automatically stack on mobile.


## 2. Quick Info Card - Conflict Detection

The Quick Info card shows "Conflict" status. How should conflicts be determined and displayed?

- [ ] (A) Show "No conflicts" by default until date/time selected, then check for conflicts
- [ ] (B) Real-time conflict checking as user selects date/time/vet
- [ ] (C) Check for conflicts only on form submission
- [x] (D) Don't implement conflict detection yet (defer to Issue 08-02)
- [ ] (E) Other (describe)

**Notes:**
Found existing spec at docs/specs/issues/08-issue-02-conflict-detection.md which indicates this is a separate feature. For this UI enhancement, show the Quick Info card with static clinic hours and duration information only. The conflict detection logic should be implemented separately following TDD in spec 08-02.


## 3. Previous Visits Display

The mockup shows previous visits with Date, Time, Veterinarian, Description columns. Should we:

- [ ] (A) Show ALL previous visits for the pet (unlimited)
- [x] (B) Show only recent N visits (specify how many: 5)
- [ ] (C) Show visits with pagination (if more than N)
- [ ] (D) Show visits with "Load more" button
- [ ] (E) Other (describe)

**Notes:**
Showing 5 most recent visits provides sufficient context without overwhelming the UI. This aligns with the "context-first" design principle. If users need full history, they can view it on the owner details page. Sort by date descending (most recent first).


## 4. Form Validation Timing

When should validation errors be displayed?

- [ ] (A) On blur (when user leaves field)
- [ ] (B) On form submit only
- [ ] (C) Real-time as user types (immediate feedback)
- [x] (D) Combination: on blur for required fields, on submit for all
- [ ] (E) Other (describe)

**Notes:**
This provides the best UX:
- On blur: Validate required fields (date, time, vet) immediately when user leaves field
- On submit: Perform comprehensive validation including business hours check via BusinessHoursValidator
- Prevents frustration from real-time validation while typing
- Aligns with current Spring validation in VisitController.java:113-127


## 5. Time Slot Dynamic Loading

The UI Description mentions "Time slots depend on selected date". Should time slots:

- [ ] (A) Show all clinic hours (9 AM - 5 PM) regardless of availability
- [ ] (B) Show only available (non-conflicting) time slots
- [x] (C) Show all slots but disable/mark unavailable ones
- [ ] (D) Load from backend based on date selection (fetch availability)
- [ ] (E) Other (describe)

**Notes:**
Generate 30-minute slots from 9:00 AM to 5:00 PM (last slot at 4:30 PM). Initially show all slots. When conflict detection is implemented (Issue 08-02), unavailable slots will be disabled/grayed out. This provides transparency about clinic hours while preparing for future conflict detection.


## 6. Clinic Hours Configuration

The Quick Info card shows "9:00 AM – 5:00 PM" and "30 minutes" duration. Should these be:

- [ ] (A) Hardcoded in the template (simple, matches mockup)
- [x] (B) Configurable via application properties/config
- [ ] (C) Stored in database (Clinic entity)
- [ ] (D) Doesn't matter for this spec (implementation detail)
- [ ] (E) Other (describe)

**Notes:**
Use application.properties for clinic configuration:
```properties
clinic.hours.start=09:00
clinic.hours.end=17:00
clinic.visit.duration=30
```
This allows easy modification without code changes and supports different environments. The BusinessHoursValidator already validates business hours, so configuration should be centralized.


## 7. Pet Type Icons/Avatars

The mockup design mentions "Icon or avatar for pet type". Should we:

- [x] (A) Use Font Awesome icons for pet types (dog, cat, etc.)
- [ ] (B) Use Bootstrap icons
- [ ] (C) Use custom SVG icons/images
- [ ] (D) Use simple text badges (no icons for now)
- [ ] (E) Other (describe)

**Notes:**
Font Awesome is already included via WebJars according to ARCHITECTURE.md. Use icons like fa-dog, fa-cat, fa-bird, etc. This maintains consistency with existing UI patterns and requires no additional dependencies.


## 8. Empty State for Previous Visits

The empty state message is specified. Should we also show:

- [ ] (A) Just the message (as shown in mockup)
- [ ] (B) Message + illustration/icon
- [x] (C) Message + helpful tip ("Schedule your first visit above")
- [ ] (D) Hide the entire "Previous Visits" section if empty
- [ ] (E) Other (describe)

**Notes:**
Enhance the empty state with:
- Icon (e.g., fa-calendar-plus from Font Awesome)
- Message: "No previous visits found."
- Helpful tip: "New visits will appear here after scheduling."
This guides users and maintains visual balance in the card layout.


## 9. Form Submit Button State

The mockup shows "Add Visit" button. Should the button:

- [ ] (A) Be disabled until all required fields are valid (client-side)
- [ ] (B) Be always enabled, show validation errors on click
- [ ] (C) Show loading spinner during submission
- [x] (D) All of the above (A + C)
- [ ] (E) Other (describe)

**Notes:**
- Disabled state: Button disabled until required fields (date, time, vet) are filled
- Loading state: Show spinner during submission to prevent double-submission
- Use Bootstrap button states: btn-primary (enabled), disabled (incomplete), with spinner during submit


## 10. Success Feedback

After successfully adding a visit, what should happen?

- [ ] (A) Redirect to owner details page (current behavior)
- [x] (B) Redirect to owner details with success message/toast
- [ ] (C) Stay on form, clear it, show success message, update previous visits
- [ ] (D) Redirect to a confirmation page
- [ ] (E) Other (describe)

**Notes:**
This matches current behavior in VisitController.java:131-132. The controller already uses RedirectAttributes with flash message. Enhance by displaying the message as a Bootstrap alert/toast on the owner details page for better visibility.


## 11. Backend Changes Needed

To support this UI, which backend changes are required?

- [x] (A) Controller must pass pet, owner, vets list, previous visits to template
- [ ] (B) Create new DTO/model for clinic configuration (hours, duration)
- [ ] (C) Add endpoint for conflict checking (AJAX)
- [ ] (D) Update validation error responses for better field-specific feedback
- [ ] (E) No backend changes (template only)
- [ ] (F) Other (describe)

**Notes:**
Current implementation in VisitController.java:77-101 already passes pet, owner, and vets. Need to add:
- Previous visits: Query pet.getVisits() and sort by date descending, limit to 5
- Clinic config: Inject configuration properties for hours/duration
No new endpoints needed for initial implementation. Conflict checking endpoint will be added in Issue 08-02.


## 12. Accessibility Requirements

Should we include specific accessibility features?

- [ ] (A) Proper ARIA labels for form fields
- [ ] (B) Keyboard navigation support
- [ ] (C) Screen reader friendly error messages
- [ ] (D) Focus management (auto-focus first field)
- [x] (E) All of the above
- [ ] (F) Other (describe)

**Notes:**
Following WCAG 2.1 AA standards:
- ARIA labels: All form inputs with proper aria-label or aria-labelledby
- Keyboard navigation: Full tab order, Enter to submit, Escape to cancel
- Screen reader messages: Error messages with aria-live="polite" regions
- Focus management: Auto-focus on first error field after validation failure
- Color contrast: Ensure error states meet 4.5:1 contrast ratio
This aligns with enterprise application standards mentioned in ARCHITECTURE.md.


---

## Additional Questions or Clarifications

Please add any additional context, requirements, or questions below:

**Notes:**

1. **TDD Implementation Order**: Following strict TDD from AGENTS.md, the implementation should proceed:
   - Write E2E tests for UI layout and form submission
   - Write unit tests for controller changes (previous visits sorting/limiting)
   - Write integration tests for full visit creation flow
   - Implement minimal code to pass tests
   - Run test-temporal-coupling-detector and i18n-sync-validator agents

2. **Template Technology**: Confirm using Thymeleaf (current stack per ARCHITECTURE.md) with Bootstrap 5 for the card-based layout.

3. **Time Zone Handling**: Should appointment times be stored in UTC or clinic local time? Recommend storing in UTC with display in clinic timezone.

4. **Veterinarian Availability**: The vet selector shows all vets. Should this be filtered by availability once conflict detection is implemented? (Defer to Issue 08-02)
