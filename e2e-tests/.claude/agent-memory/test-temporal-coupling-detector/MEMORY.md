# Test Temporal Coupling Detector - Memory

## Project: Emerald Grove Veterinary Clinic E2E Tests

### Temporal Coupling Patterns Found

#### Visit Scheduling Tests (`tests/features/visit-scheduling.spec.ts`)
- **CRITICAL**: Line 68 - Hardcoded date `'2024-03-03'` in validation test (past date as of 2026-02-24)
- **MEDIUM**: Line 82 - Hardcoded date `'2020-01-01'` for past date validation test (intentional but brittle)
- **GOOD**: Lines 31-33, 122, 172-174 - Dynamic date generation using `new Date()` with offsets

#### Other Tests
- **GOOD**: `pet-management.spec.ts` lines 38-40 - Proper dynamic future date generation
- **CLEAN**: `visit-booking-ui.spec.ts` - No temporal coupling, only tests UI elements
- **CLEAN**: `visit-booking-with-time.spec.ts` - No temporal coupling

### Recommended Pattern for This Project

**Dynamic date generation pattern:**
```typescript
// Future date (7 days from now)
const futureDate = new Date();
futureDate.setDate(futureDate.getDate() + 7);
const visitDateStr = futureDate.toISOString().split('T')[0]; // 'YYYY-MM-DD'

// Today's date
const today = new Date().toISOString().split('T')[0];

// Past date (for validation tests)
const pastDate = new Date();
pastDate.setDate(pastDate.getDate() - 30);
const pastDateStr = pastDate.toISOString().split('T')[0];
```

### Date Utilities Location
- Path alias configured: `@utils/*` → `tests/utils/*`
- Existing file: `tests/utils/test-helpers.ts` (currently only has `measureMs`)
- Created: `tests/utils/date-helpers.ts` with date utility functions

### Date Format Requirements
- Backend expects: `YYYY-MM-DD` (ISO date format)
- Conversion: `new Date().toISOString().split('T')[0]`

### Business Rules
- Visit dates must be today or future (past dates rejected)
- Validation message: "Visit date cannot be in the past"
- Clinic hours: 9:00 AM to 5:00 PM (30-minute slots)

### Files Modified
1. `tests/features/visit-scheduling.spec.ts` - Fixed hardcoded dates on lines 68 and 82
2. `tests/utils/date-helpers.ts` - Created date utility module

### CI/CD Considerations
- Tests run in GitHub Actions (timezone: UTC by default)
- No timezone-specific logic detected in current tests
- All date generation uses local system time
