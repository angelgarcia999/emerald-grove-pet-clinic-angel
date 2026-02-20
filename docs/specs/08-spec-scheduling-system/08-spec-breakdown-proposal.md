# Scheduling System - Spec Breakdown Proposal

This document outlines the proposed breakdown of the comprehensive scheduling system into smaller, focused specs.

## Overview

The full scheduling system will be implemented across **5 focused specifications**, each building on the previous foundation.

---

## Spec 1: Time-Based Appointments & Vet Assignment
**Focus**: Core data model and basic scheduling infrastructure

### What's Included:
- Enhance Visit entity with time fields (appointment start time, duration)
- Add vet assignment to appointments (many-to-one relationship: Visit → Vet)
- Database schema changes (add columns: `start_time`, `duration_minutes`, `vet_id`)
- Enhanced booking form with time slot selection (30-minute intervals)
- Vet selection dropdown
- Basic business hours validation (Monday-Friday 9:00-5:00, Saturday 9:00-1:00)

### What's NOT Included:
- Conflict detection (comes in Spec 2)
- Calendar views (comes in Spec 3)
- Appointment types (comes in Spec 4)

### Dependencies:
- None (foundation spec)

### Proof Artifacts:
- Screenshot of enhanced booking form with time and vet selection
- Database schema showing new columns
- Test report showing time slot validation works

---

## Spec 2: Conflict Detection & Prevention
**Focus**: Business logic to prevent double-booking

### What's Included:
- Vet-based conflict detection (same vet can't have overlapping appointments)
- Pet-based conflict detection (same pet can't have overlapping appointments)
- Clinic capacity limit checking (max 5 concurrent appointments)
- Conflict validation service with unit tests
- Error messages when conflicts detected
- Prevention logic in booking workflow

### What's NOT Included:
- Calendar views (comes in Spec 3)
- Visual conflict indicators on calendar (comes in Spec 3)
- Appointment types (comes in Spec 4)

### Dependencies:
- **Requires Spec 1** (needs time-based appointments with vet assignment)

### Proof Artifacts:
- Screenshot of conflict detection error message
- Test report showing vet conflict detection works
- Test report showing pet conflict detection works
- Test report showing clinic capacity limit works

---

## Spec 3: Calendar Visualization System
**Focus**: Visual calendar interface for viewing appointments

### What's Included:
- Day view (show all appointments for selected day in time slot grid)
- Week view (show appointments across 7 days)
- Month view (show appointments in monthly calendar)
- Vet-specific filtering (filter calendar by selected vet)
- Navigation between views (day ↔ week ↔ month)
- Visual indicators for booked/available time slots
- Click appointment to view details
- Display conflict indicators visually (red X or blocked slot)

### What's NOT Included:
- Appointment types (comes in Spec 4)
- Edit/cancel functionality (comes in Spec 5)

### Dependencies:
- **Requires Spec 1** (needs appointments with time and vet data)
- **Optionally enhances Spec 2** (can show conflict indicators if Spec 2 is done)

### Proof Artifacts:
- Screenshot of day view calendar showing appointments
- Screenshot of week view calendar
- Screenshot of month view calendar
- Screenshot of vet-filtered view

---

## Spec 4: Appointment Types & Categorization
**Focus**: Categorize appointments by type

### What's Included:
- Create AppointmentType entity (Routine Checkup, Surgery, Emergency, Vaccination)
- Add `appointment_type_id` foreign key to Visit entity
- Type selection in booking form
- Display appointment type in calendar views (color-coded or labeled)
- Filter appointments by type

### What's NOT Included:
- Variable duration based on type (keeping fixed 30-minute duration)
- Vet specialty matching (any vet can handle any type for now)

### Dependencies:
- **Requires Spec 1** (needs basic appointment structure)
- **Optionally enhances Spec 3** (adds type display to calendar if Spec 3 is done)

### Proof Artifacts:
- Screenshot of booking form with appointment type selection
- Screenshot of calendar showing color-coded appointment types
- Database schema showing appointment_types table

---

## Spec 5: Appointment Lifecycle Management (Edit/Cancel/Reschedule)
**Focus**: Modify and cancel existing appointments

### What's Included:
- Cancel appointment functionality
- 24-hour advance cancellation rule (can't cancel if < 24 hours away)
- Reschedule appointment (change date/time, re-run conflict detection)
- Edit appointment details (description, vet, type)
- Staff override capability (clinic staff can modify any appointment)
- Mark appointments as "completed" after they occur

### What's NOT Included:
- Notifications/reminders (explicitly out of scope)

### Dependencies:
- **Requires Spec 1** (needs basic appointment structure)
- **Requires Spec 2** (needs conflict detection for reschedule)
- **Optionally enhances Spec 3** (adds edit/cancel buttons in calendar if Spec 3 is done)
- **Optionally enhances Spec 4** (can edit appointment type if Spec 4 is done)

### Proof Artifacts:
- Screenshot of cancel appointment confirmation
- Screenshot of reschedule form
- Test report showing 24-hour rule enforcement
- Test report showing conflict detection during reschedule

---

## Implementation Order

Recommended implementation sequence:

```
1. Spec 1 (Foundation) → Implement first
2. Spec 2 (Conflict Detection) → Implement second (builds on Spec 1)
3. Spec 3 (Calendar Views) → Implement third (builds on Spec 1 & 2)
4. Spec 4 (Appointment Types) → Implement fourth (enhances all previous)
5. Spec 5 (Lifecycle Management) → Implement last (requires all previous)
```

**Dependency Diagram:**

```
Spec 1 (Foundation)
   ↓
   ├──→ Spec 2 (Conflict Detection)
   ├──→ Spec 3 (Calendar Views)
   └──→ Spec 4 (Appointment Types)
         ↓
      Spec 5 (Lifecycle Management)
      (requires Specs 1, 2, optionally 3 & 4)
```

---

## Alternative: Parallel Implementation

If multiple developers are working, Specs 3 and 4 can be implemented in parallel after Specs 1 and 2 are complete:

```
Spec 1 → Spec 2
         ↓
    ┌────┴────┐
    ↓         ↓
  Spec 3    Spec 4
    └────┬────┘
         ↓
      Spec 5
```

---

## Summary

- **Total Specs**: 5 focused specifications
- **Each spec**: 2-4 demoable units (manageable size)
- **Incremental delivery**: Each spec delivers working, testable functionality
- **Clear boundaries**: Minimal overlap, well-defined dependencies
- **Logical progression**: Foundation → Business Logic → UI → Enhancements → Management

---

## Your Feedback

Does this breakdown make sense? Any adjustments you'd like to make?

- Combine any specs?
- Split any specs further?
- Change implementation order?
- Adjust what's included/excluded?
