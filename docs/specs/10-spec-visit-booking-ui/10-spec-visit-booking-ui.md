# 10-spec-visit-booking-ui.md

## Introduction/Overview

This specification defines the modernization of the visit scheduling interface for the Emerald Grove Veterinary Clinic. The current visit booking form uses a basic, single-column layout with minimal context and simple dropdown selectors. This update transforms it into a modern, card-based two-column interface that provides better context, improved usability, and clear visual hierarchy. The enhanced UI groups pet information and scheduling constraints in the left column while presenting a structured appointment form in the right column, with a comprehensive previous visits history displayed below.

## Goals

1. **Improve User Context**: Display pet information, owner details, and scheduling constraints prominently before appointment booking
2. **Enhance Visual Hierarchy**: Use card-based layout with Bootstrap 5 to create clear separation between informational and actionable components
3. **Streamline Appointment Creation**: Provide structured form with time slots, vet selection with specialties, and clear validation feedback
4. **Maintain Historical Context**: Show previous visits in a formatted table to provide appointment history at a glance
5. **Ensure Responsive Design**: Create mobile-friendly layout that stacks columns vertically on smaller screens while maintaining usability

## User Stories

**As a clinic staff member**, I want to see the pet's complete information (name, type, birth date, owner) before scheduling a visit so that I can confirm I'm booking for the correct patient.

**As a clinic staff member**, I want to understand the clinic's scheduling rules (hours, visit duration) upfront so that I can set appropriate expectations with pet owners during booking.

**As a clinic staff member**, I want to select from available time slots in 30-minute intervals during clinic hours (9 AM - 5 PM) so that appointments are consistently scheduled and don't conflict.

**As a clinic staff member**, I want to assign a specific veterinarian to each visit and see their specialties so that I can match the pet's needs with the appropriate vet expertise.

**As a clinic staff member**, I want to view the pet's previous visit history on the same screen so that I can reference past appointments and understand the pet's medical context.

**As a clinic staff member**, I want clear validation feedback when I miss required fields or enter invalid data so that I can correct errors quickly and complete the booking.

## Demoable Units of Work

### Unit 1: Two-Column Card Layout with Pet Context

**Purpose:** Establish the foundational UI structure that separates static pet information from the interactive appointment form, providing better visual organization and user context.

**Functional Requirements:**
- The system shall display a two-column layout using Bootstrap 5 grid system (col-md-6 for each column)
- The left column shall contain a Pet Summary Card displaying pet name, type, birth date, and owner name in read-only format
- The left column shall contain a Quick Info Card showing clinic hours (9:00 AM – 5:00 PM), visit duration (30 minutes), and scheduling constraints
- The right column shall contain the Appointment Details Card with the booking form
- The system shall stack columns vertically on mobile devices (screens < 768px) with pet context appearing above the form
- The UI shall use Bootstrap 5 card components with consistent spacing (mb-4 between cards, p-3 for card bodies)

**Proof Artifacts:**
- Screenshot: Desktop view showing two-column layout demonstrates proper card structure and spacing
- Screenshot: Mobile view (< 768px) demonstrates responsive stacking with pet context on top
- Screenshot: Pet Summary Card closeup demonstrates read-only pet information display
- Screenshot: Quick Info Card closeup demonstrates scheduling rules visibility

### Unit 2: Enhanced Appointment Form with Time and Vet Selection

**Purpose:** Upgrade the booking form with structured time slot selection and enhanced vet assignment interface, replacing basic dropdowns with improved user experience.

**Functional Requirements:**
- The system shall provide a date picker for visit date selection with validation requiring present or future dates
- The system shall display time slots as a dropdown with 30-minute intervals from 9:00 AM to 5:00 PM (17:00)
- The system shall populate vet selector with all available veterinarians showing "Dr. [LastName]" format
- The system shall display vet specialties in parentheses after the vet name (e.g., "Dr. Carter (radiology, surgery)")
- The system shall mark date, time, and vet fields as required with asterisks (*)
- The system shall provide a description textarea for optional visit notes with placeholder text
- The system shall display inline validation errors below invalid fields using Bootstrap form-feedback classes
- The system shall disable the "Add Visit" submit button until all required fields are valid

**Proof Artifacts:**
- Screenshot: Appointment form with all fields filled demonstrates complete form structure
- Screenshot: Form validation errors displayed demonstrates inline error feedback
- Screenshot: Time slot dropdown expanded demonstrates 30-minute interval options from 9 AM to 5 PM
- Screenshot: Vet selector expanded demonstrates vet names with specialties shown

### Unit 3: Previous Visits Table with History Display

**Purpose:** Display the pet's visit history in a formatted table below the booking form to provide medical context and appointment history.

**Functional Requirements:**
- The system shall display a "Previous Visits" section spanning the full width below both columns
- The system shall show previous visits in a Bootstrap striped table with columns: Date, Time, Veterinarian, Description
- The system shall format visit dates as "yyyy-MM-dd" and display time in "h:mm AM/PM" format if available
- The system shall display veterinarian as "Dr. [LastName]" matching the form selector format
- The system shall sort visits by date in descending order (most recent first)
- The system shall display an empty state message when no previous visits exist: "No previous visits found. New visits will appear here after scheduling."
- The system shall maintain table responsiveness on mobile devices using Bootstrap table-responsive class

**Proof Artifacts:**
- Screenshot: Previous visits table with data demonstrates complete visit history display
- Screenshot: Empty state for previous visits demonstrates empty state message
- Screenshot: Mobile view of visits table demonstrates responsive table behavior

### Unit 4: Form Validation and User Feedback

**Purpose:** Implement comprehensive form validation with clear error messages and user feedback to ensure data quality and guide users through the booking process.

**Functional Requirements:**
- The system shall validate date field is not empty and is present or future date
- The system shall validate time slot is selected (not empty)
- The system shall validate veterinarian is selected (not empty)
- The system shall display field-specific validation errors using Spring's BindingResult
- The system shall show validation errors as red text below the invalid field using Bootstrap is-invalid class
- The system shall maintain form data on validation failure so users don't lose their selections
- The system shall clear validation errors when the user corrects the invalid field
- The system shall redirect to owner details page on successful submission with visit ID in URL
- The user shall see the newly created visit appear in the previous visits table after redirection

**Proof Artifacts:**
- Screenshot: Form with missing required fields showing validation errors demonstrates validation feedback
- Screenshot: Form with date validation error demonstrates date-specific validation
- Screenshot: Successful submission redirect to owner details demonstrates success flow
- Test: E2E test `visit-scheduling.spec.ts` passing demonstrates complete booking flow works end-to-end

## Non-Goals (Out of Scope)

1. **Real-time conflict detection**: This spec does not implement live conflict checking as users select date/time/vet. Conflict detection is deferred to Issue 08-02. The Quick Info card will show static scheduling rules only.

2. **Vet availability filtering**: Time slots will show all clinic hours (9 AM - 5 PM) regardless of veterinarian availability. Dynamic availability based on vet selection is out of scope.

3. **Calendar widget integration**: This uses the browser's native date picker. A custom calendar UI widget is deferred to Issue 08-03 (Calendar Views).

4. **Visit editing or deletion**: This spec covers visit creation only. Editing or deleting existing visits is not included.

5. **Multi-pet appointment booking**: The form books one visit for one pet. Booking visits for multiple pets in a single flow is out of scope.

6. **Appointment confirmation emails**: No email notifications or confirmations are generated. Communication features are not part of this UI enhancement.

7. **Vet profile pages**: The vet selector shows name and specialties but does not link to detailed vet profile pages.

## Design Considerations

The UI follows the visual mockup provided in `UIPictures/pet_clinic2.png` and detailed in `UIPictures/UIDescription.md`. Key design elements:

**Layout Structure:**
- Two-column desktop layout using Bootstrap 5 grid (row with two col-md-6 columns)
- Mobile-first responsive design that stacks columns vertically on screens < 768px
- Full-width previous visits section below the form columns

**Card-Based Design:**
- All content groups use Bootstrap 5 card components with card-header and card-body
- Consistent spacing: mb-4 between cards, p-3 for card padding
- Card headers use subtle background colors to differentiate sections
- Shadow effects (shadow-sm) for depth and visual hierarchy

**Typography Hierarchy:**
- Page title: h2 heading "New Visit"
- Page subtitle: h4 "Schedule Appointment"
- Card headers: h5 or h6 for section titles
- Form labels: Bootstrap form-label class with required field indicators (*)
- Body text: Bootstrap default font stack

**Form Components:**
- Date picker: HTML5 date input with Bootstrap form-control styling
- Time dropdown: select element with form-select class showing 30-minute intervals
- Vet dropdown: select element with form-select class showing names and specialties
- Description: textarea with form-control class and placeholder text
- Submit button: btn btn-primary with "Add Visit" text

**Validation Styling:**
- Invalid fields: is-invalid class adds red border and icon
- Error messages: invalid-feedback class for red text below fields
- Required indicators: Asterisk (*) after label text for required fields

**Empty States:**
- Previous visits: Centered text with muted color when no visits exist
- Informational tone: "No previous visits found. New visits will appear here after scheduling."

**Visual Consistency:**
- Follow existing Bootstrap 5 theme used throughout the application
- Match color scheme and spacing of owner and vet list pages
- Use consistent button styles and form controls with other forms

## Repository Standards

**Spring Boot + Thymeleaf Patterns:**
- Use Thymeleaf fragments for consistent layout (layout.html)
- Follow existing template structure with th:replace for layout integration
- Use th:object for form binding to Visit entity
- Use th:field for automatic form field binding and validation
- Use th:errors for displaying Spring validation errors
- Follow internationalization pattern with #{message.key} for all labels

**Bootstrap 5 Conventions:**
- Use Bootstrap 5 grid system (container, row, col-*)
- Apply utility classes for spacing (mb-3, p-3, etc.)
- Use form-control, form-select, form-label classes for form elements
- Apply is-invalid class for validation error styling
- Use card, card-header, card-body components consistently

**Code Organization:**
- Keep template in `src/main/resources/templates/pets/createOrUpdateVisitForm.html`
- Controller logic in `src/main/java/org/springframework/samples/petclinic/owner/VisitController.java`
- Follow existing naming conventions for model attributes (visit, pet, owner, vets)

**Testing Standards:**
- Follow TDD methodology: write E2E test first, then implement UI changes
- Update E2E test page object (`e2e-tests/tests/pages/visit-page.ts`) with new selectors
- Ensure all E2E tests pass in `visit-scheduling.spec.ts` and `pet-management.spec.ts`
- Maintain test coverage > 90% for controller methods

## Technical Considerations

**Controller Requirements:**
- Pass `pet` object to template for Pet Summary Card display
- Pass `owner` object to template for owner name display
- Pass `vets` collection to template for veterinarian dropdown population
- Pass `visit.pet.visits` collection to template for previous visits table (sorted by date descending)
- Return validation errors via `BindingResult` for field-specific feedback
- Redirect to `/owners/{ownerId}` on successful visit creation

**Thymeleaf Template Structure:**
```html
<html th:replace="~{fragments/layout :: layout (~{::body},'owners')}">
<body>
  <!-- Page Header -->
  <h2>New Visit</h2>
  <h4>Schedule Appointment</h4>

  <!-- Two-Column Layout -->
  <div class="row">
    <!-- Left Column: Pet Context -->
    <div class="col-md-6">
      <!-- Pet Summary Card -->
      <!-- Quick Info Card -->
    </div>

    <!-- Right Column: Appointment Form -->
    <div class="col-md-6">
      <!-- Appointment Details Card with form -->
    </div>
  </div>

  <!-- Previous Visits Section (full width) -->
  <div class="row">
    <div class="col-12">
      <!-- Previous Visits Table -->
    </div>
  </div>
</body>
</html>
```

**Form Binding:**
- Form uses `th:object="${visit}"` for model binding
- Date field: `th:field="*{date}"` with `type="date"`
- Time field: `th:field="*{startTime}"` (existing field from Issue 08-01)
- Vet field: `th:field="*{vet.id}"` (existing relationship from Issue 08-01)
- Description field: `th:field="*{description}"`

**Validation Integration:**
- Use existing `@NotNull` and `@FutureOrPresent` annotations on Visit entity
- Display errors with `th:errors="*{date}"`, `th:errors="*{startTime}"`, etc.
- Apply `is-invalid` class conditionally: `th:classappend="${#fields.hasErrors('date')} ? 'is-invalid' : ''"`

**Data Dependencies:**
- Builds on Issue 08-01 (Time-Based Appointments with Vet Assignment)
- Requires `Visit.startTime` field (LocalTime)
- Requires `Visit.vet` relationship (ManyToOne)
- Uses existing `VetRepository.findAll()` for vet selector population

**Browser Compatibility:**
- HTML5 date input with fallback text input for older browsers
- Bootstrap 5 requires modern browsers (IE 11 not supported)
- Responsive breakpoints work in all Bootstrap 5 supported browsers

## Security Considerations

**Input Validation:**
- Server-side validation required for all form inputs using Spring Validation
- Date field validated to prevent past dates (business hours validation from Issue 08-01)
- Time field validated to be within clinic hours (9:00 AM - 5:00 PM)
- Vet selection validated to ensure selected vet exists in database

**Data Privacy:**
- No sensitive medical data captured in this form (description is optional free text)
- Owner and pet information displayed is already authorized (user has navigated to pet details)
- No personally identifiable information (PII) exposed in URLs or error messages

**XSS Prevention:**
- Thymeleaf automatically escapes output (th:text) to prevent XSS attacks
- User-entered description sanitized by Thymeleaf before rendering in previous visits table
- No raw HTML insertion using th:utext

**Authorization:**
- Follow existing application authorization model (if implemented)
- Visit creation should only be allowed by authenticated clinic staff
- Owner/pet data access follows existing security patterns

**Proof Artifacts Security:**
- Screenshots should NOT contain real patient data in production
- Use development/test data for all proof artifacts
- Redact any sensitive information if production screenshots are necessary

## Success Metrics

1. **User Task Completion Time**: Reduce average time to schedule a visit from 45 seconds (current form) to 30 seconds (target) through improved layout and context
2. **Form Validation Errors**: Reduce form submission errors by 40% through upfront scheduling rules display and clear required field indicators
3. **Mobile Usability**: Achieve 90%+ mobile test pass rate with successful visit creation on devices < 768px width
4. **E2E Test Coverage**: Maintain 100% E2E test coverage for visit booking flow (all paths tested in Playwright)
5. **User Satisfaction**: Achieve "improvement" rating from 3 clinic staff members in usability testing comparing old vs new form

## Open Questions

1. **Conflict Detection Display**: The Quick Info card mentions "Conflict status indicator" but real-time conflict detection is deferred to Issue 08-02. Should we show a static placeholder message like "Conflicts will be checked after selection" or hide this field entirely until Issue 08-02?

2. **Time Slot Availability**: Should unavailable time slots be disabled in the dropdown (requires backend check) or show all slots and validate on submission? Current implementation shows all slots.

3. **Previous Visits Pagination**: If a pet has 50+ visits, should we paginate the previous visits table or implement "Load More" functionality? Current spec assumes reasonable visit counts (< 20).

4. **Pet Type Icons**: The issue mentions "Icon or avatar for pet type" in the Pet Summary Card. Should we use Font Awesome icons (fa-dog, fa-cat) or simple text labels? Current implementation uses text only.

5. **Form Submit Button State**: Should the submit button show a loading spinner during submission, or is a simple disabled state sufficient? Current spec doesn't specify animated feedback.

6. **Vet Specialties Display**: If a vet has 4+ specialties, should we truncate the display in the dropdown (e.g., "Dr. Carter (radiology, surgery, +2 more)") or show all specialties regardless of length?
