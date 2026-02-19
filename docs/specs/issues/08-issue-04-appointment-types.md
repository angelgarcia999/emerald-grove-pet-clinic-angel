# Issue 04: Spec 4 - Appointment Types & Categorization

## 🎯 Objective

Add appointment type categorization to enable better organization and visual differentiation of appointments (Routine Checkup, Surgery, Emergency, Vaccination).

## 📋 Key Features

- **AppointmentType Entity**: New entity with predefined types
- **Type Association**: Link appointments to types via foreign key
- **Type Selection**: Dropdown in booking form
- **Visual Differentiation**: Color-coded or labeled appointments in calendar
- **Type Filtering**: Filter appointments by type in calendar views

## 🔗 Dependencies

- **Requires Issue 01** - Needs basic appointment structure
- **Enhanced by Issue 03** - Adds type display to calendar if implemented

## 📦 Deliverables

- AppointmentType entity and repository
- Database migration for appointment_types table
- Updated booking form with type selection
- Calendar view enhancements for type display
- Color-coding or labeling system
- Unit and integration tests

## 📸 Proof Artifacts

- Screenshot of booking form with appointment type selection
- Screenshot of calendar showing color-coded appointment types
- Database schema showing appointment_types table and relationships

## 📄 Related Documentation

- Spec breakdown: `docs/specs/08-spec-scheduling-system/08-spec-breakdown-proposal.md`
- Detailed spec: Will be created in `docs/specs/12-spec-appointment-types/`

## 📊 Status

- [ ] Spec document created
- [ ] Tasks generated
- [ ] Implementation started
- [ ] Implementation complete
- [ ] Validated

---

**Implementation Order**: 4️⃣ (Implement Fourth - can be parallel with Issue 03 after Issue 01 and 02)
