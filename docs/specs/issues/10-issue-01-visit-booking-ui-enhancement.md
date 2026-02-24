# Issue 10-01: Visit Booking UI Enhancement

## 🎯 Objective

Modernize the visit scheduling interface with a clean, card-based layout that provides better context, improved usability, and clear separation between informational and actionable UI components.

## 📋 Current State vs. Desired State

### Current Implementation
- Basic form-based visit creation
- Minimal context about the pet
- Simple dropdown for time selection
- Previous visits shown in basic table
- Limited visual hierarchy

### Desired Implementation (Per UI Guide)
- **Two-column layout**: Pet context (left) + Appointment form (right)
- **Card-based design**: Clear visual grouping of related information
- **Enhanced context**: Pet summary and scheduling constraints
- **Improved form**: Structured appointment details with better validation feedback
- **Better history view**: Previous visits table with empty state messaging

## 📐 Layout Structure

### Page Header
```
Title: "New Visit"
Subtitle: "Schedule Appointment"
```

### Left Column — Pet Context

#### 1. Pet Summary Card
**Purpose**: Confirm correct pet before scheduling

**Content**:
- Pet name (e.g., "Leo")
- Type (e.g., "Cat")
- Birth date (e.g., "2010-09-07")
- Owner name (e.g., "George Franklin")

**Design**:
- Card container with subtle shadow
- Icon or avatar for pet type
- Read-only display fields
- Clean typography hierarchy

#### 2. Quick Info Card
**Purpose**: Display scheduling rules and constraints

**Content**:
- Clinic hours: "9:00 AM – 5:00 PM"
- Visit duration: "30 minutes"
- Time slot availability status
- Conflict detection indicator

**Design**:
- Informational card with icon indicators
- Color-coded status messages
- Non-interactive (display only)

### Right Column — Appointment Form

#### 3. Appointment Details Card
**Purpose**: Collect visit scheduling information

**Form Fields**:
1. **Date picker** (required)
   - Date selection input
   - Calendar widget
   - Validation: Must be present or future date

2. **Time slot dropdown** (required)
   - Populated based on selected date
   - Shows available hours within clinic hours (9 AM - 5 PM)
   - 30-minute intervals
   - Validation: Required field

3. **Veterinarian selector** (required)
   - Dropdown of available vets
   - Shows vet name and specialties
   - Validation: Required field

4. **Description textarea** (optional)
   - Multi-line text input
   - Placeholder: "Reason or notes for visit"
   - No character limit

5. **Primary action button**
   - Text: "Add Visit"
   - Primary color (Bootstrap btn-primary)
   - Disabled until required fields valid

**Validation Feedback**:
- Inline error messages below invalid fields
- Field highlighting for errors
- Clear indication of required fields (*)

### Bottom Section

#### 4. Previous Visits Section
**Purpose**: Show visit history for context

**Content**:
- Section header: "Previous Visits"
- Table with columns:
  - Date
  - Time
  - Veterinarian
  - Description

**Empty State**:
```
"No previous visits found. New visits will appear here after scheduling."
```

**Design**:
- Full-width table below form columns
- Striped rows for readability
- Sorted by date (most recent first)
- Responsive design for mobile

## 🎨 Design Principles

1. **Context-First Scheduling**
   - Show pet information prominently
   - Display scheduling constraints upfront
   - Provide historical context via previous visits

2. **Clear Validation Expectations**
   - Mark required fields clearly
   - Provide immediate feedback on errors
   - Disable submission until form is valid

3. **Structured Data Entry**
   - Group related fields in cards
   - Use appropriate input types (date picker, dropdown, textarea)
   - Maintain consistent spacing and alignment

4. **Separation of Concerns**
   - Static info (left column) vs. editable inputs (right column)
   - Informational cards vs. action cards
   - Context display vs. form submission

## 🔗 Dependencies

- **Builds on Issue 08-01** - Requires time-based appointments with vet assignment
- **UI Reference**: `UIPictures/pet_clinic2.png` - Visual mockup of desired layout
- **UI Description**: `UIPictures/UIDescription.md` - Detailed UI specification
- **Bootstrap 5**: Leverage existing card, form, and grid components

## 📦 Deliverables

### Frontend Components
- [ ] Two-column responsive layout (Bootstrap grid)
- [ ] Pet Summary Card component
- [ ] Quick Info Card component
- [ ] Appointment Details Form with validation
- [ ] Previous Visits table with empty state
- [ ] Mobile-responsive design (stacks columns on small screens)

### Backend Enhancements
- [ ] Pass pet details to view template
- [ ] Pass clinic configuration (hours, duration) to view
- [ ] Load previous visits for the pet
- [ ] Return validation errors with field-specific messages

### Styling
- [ ] Card-based layout with consistent spacing
- [ ] Form validation states (error, success)
- [ ] Empty state styling for previous visits
- [ ] Icon integration for pet types and info indicators
- [ ] Responsive breakpoints for mobile/tablet/desktop

## 🔄 Functional Behavior

1. **Page Load**
   - Display pet information in left column
   - Show clinic hours and scheduling constraints
   - Load previous visits for the pet
   - Initialize form with empty state

2. **Date Selection**
   - Update time slot dropdown based on selected date
   - Check time slot availability
   - Update conflict indicator if applicable

3. **Form Validation**
   - Validate required fields (date, time, vet)
   - Show inline error messages
   - Enable/disable submit button based on validation

4. **Form Submission**
   - Create visit with all form data
   - Redirect to owner details page
   - Show success message
   - Update previous visits list

## 📸 Proof Artifacts

**Required Screenshots**:
1. Full page view with both columns visible
2. Pet Summary Card closeup
3. Quick Info Card closeup
4. Appointment form with all fields filled
5. Form validation errors displayed
6. Previous visits table with data
7. Empty state for previous visits
8. Mobile responsive view (stacked layout)

**Required Tests**:
- [ ] E2E test: Complete visit booking flow
- [ ] E2E test: Form validation (missing required fields)
- [ ] E2E test: Date selection updates time slots
- [ ] Visual regression test: Desktop layout
- [ ] Visual regression test: Mobile layout
- [ ] Accessibility audit: Form labels and ARIA

## 📄 Related Documentation

- **UI Design Reference**: `UIPictures/pet_clinic2.png` - Visual mockup
- **UI Description**: `UIPictures/UIDescription.md` - Detailed component breakdown
- **Implementation Spec**: To be created in `docs/specs/10-spec-visit-booking-ui/`
- **Current Implementation**: `src/main/resources/templates/pets/createOrUpdateVisitForm.html`
- **Related Issue**: Issue 08-01 (Time-Based Appointments)

## 📊 Status

- [ ] Issue created
- [ ] Spec document created
- [ ] Tasks generated
- [ ] Implementation started
- [ ] Implementation complete
- [ ] Validated with proof artifacts
- [ ] E2E tests passing
- [ ] Accessibility audit complete

---

## 🎯 Expected Outcome

A scheduling page that:
- ✅ Improves clarity and usability through card-based layout
- ✅ Supports structured appointment creation with clear validation
- ✅ Enforces scheduling rules (clinic hours, required fields)
- ✅ Integrates seamlessly with visit history
- ✅ Provides rich contextual pet information
- ✅ Offers excellent mobile experience
- ✅ Maintains consistency with existing Bootstrap 5 design system

**Implementation Priority**: 🔥 High (before calendar views)

**Implementation Order**: Should be implemented **before** Issue 08-03 (Calendar Views) to establish the foundational scheduling UX that the calendar will build upon.
