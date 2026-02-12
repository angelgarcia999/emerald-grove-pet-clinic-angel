# 02-spec-past-visit-validation.md

## Introduction/Overview

This specification defines the implementation of date validation for the Visit entity to prevent users from scheduling veterinary visits with past dates. Currently, the system allows users to submit visit dates in the past, which creates logical inconsistencies and poor user experience. This feature will add validation to ensure visit dates are either today or in the future.

## Goals

1. **Prevent Past Date Submission**: Block users from creating visits with dates earlier than the current date
2. **Clear User Feedback**: Display a simple, understandable error message when validation fails
3. **Maintain Existing Functionality**: Ensure visits for today and future dates continue to work without disruption
4. **Follow TDD Methodology**: Implement using strict Red-Green-Refactor cycle with comprehensive test coverage
5. **Align with Spring Boot Best Practices**: Use Bean Validation annotations consistent with existing codebase patterns

## User Stories

1. **As a veterinary clinic receptionist**, I want the system to prevent me from accidentally scheduling visits in the past so that I avoid booking errors and maintain accurate appointment records.

2. **As a pet owner using the self-service portal**, I want to receive clear feedback if I try to select a past date so that I understand the error and can correct it immediately.

3. **As a system administrator**, I want visit date validation to be enforced at the entity level so that data integrity is maintained regardless of how visits are created (web form, API, batch import, etc.).

## Demoable Units of Work

### Unit 1: Entity-Level Validation

**Purpose:** Add Bean Validation constraint to the Visit entity to enforce the "no past dates" rule at the data model level, ensuring data integrity across all entry points.

**Functional Requirements:**
- The system shall reject any Visit entity with a date field set to a date earlier than the current date
- The system shall accept Visit entities with date field set to today's date or any future date
- The system shall use Jakarta Bean Validation annotations (e.g., `@FutureOrPresent` or custom constraint) on the Visit.date field
- The validation error message shall be "Visit date cannot be in the past"

**Proof Artifacts:**
- **JUnit Test**: `ValidatorTests.java` (or new `VisitValidatorTests.java`) includes test case `shouldNotValidateWhenVisitDateIsInPast()` that demonstrates past date rejection
- **JUnit Test**: Test case `shouldValidateWhenVisitDateIsToday()` demonstrates today's date is accepted
- **JUnit Test**: Test case `shouldValidateWhenVisitDateIsFuture()` demonstrates future date is accepted

---

### Unit 2: Controller-Level Integration

**Purpose:** Ensure the VisitController properly handles validation errors from the Visit entity and displays appropriate feedback to the user through the web form.

**Functional Requirements:**
- The system shall invoke Bean Validation when processing visit form submission via `@Valid` annotation
- The controller shall check `BindingResult.hasErrors()` and return the user to the form view when validation fails
- The web form shall display the validation error message "Visit date cannot be in the past" next to the date field when validation fails
- The system shall prevent the invalid visit from being persisted to the database

**Proof Artifacts:**
- **JUnit Test**: `VisitControllerTests.java` includes test case `testProcessNewVisitFormWithPastDate()` demonstrating form returns with error when past date submitted
- **JUnit Test**: Test case `testProcessNewVisitFormWithTodayDate()` demonstrates successful submission with today's date
- **CLI Output**: Run unit tests with `./mvnw test -Dtest=VisitControllerTests` showing all tests pass

---

### Unit 3: End-to-End User Experience

**Purpose:** Validate the complete user journey from form display through submission and error feedback, ensuring the feature works correctly in a real browser environment.

**Functional Requirements:**
- The user shall see the visit form with a date input field
- When the user submits a past date, the system shall redisplay the form with the error message visible
- When the user submits today's date or a future date, the system shall successfully create the visit and redirect to the owner details page
- The visit shall appear in the "Previous Visits" table after successful submission

**Proof Artifacts:**
- **Playwright Test**: `visit-scheduling.spec.ts` includes test case `rejects visit with past date` that navigates to visit form, enters a past date, submits, and asserts validation error is displayed
- **Playwright Test**: Test case `accepts visit with today date` demonstrates successful visit creation with today's date
- **Playwright Test**: Test case `accepts visit with future date` demonstrates successful visit creation with future date
- **CLI Output**: Run E2E tests with `cd e2e-tests && npm test -- visit-scheduling` showing all tests pass

## Non-Goals (Out of Scope)

1. **Internationalization**: Validation messages will be in English only; multi-language support is deferred to a future iteration
2. **Timezone Handling**: The system will use server timezone for date comparison; user-specific timezone configuration is out of scope
3. **Existing Data Migration**: Historical visits with past dates in the database will remain valid; validation applies only to new visit creation
4. **Date Format Validation**: HTML5 date input already handles format validation; this feature focuses solely on past/present/future logic
5. **Bulk Import Validation**: While entity-level validation will prevent bulk imports of invalid data, specific bulk import error handling is out of scope
6. **Edit Visit Functionality**: This feature focuses on new visit creation; editing existing visits is out of scope

## Design Considerations

**UI/UX Requirements:**

- The date input field will continue using the existing Thymeleaf fragment pattern: `th:replace="~{fragments/inputField :: input ('Date', 'date', 'date')}"`
- The error message will be displayed using Thymeleaf's standard error rendering (red text below the date field)
- The form will maintain user-entered data (description field) when redisplaying after validation error
- No changes to the visual design or layout of the visit form are required

**Form Behavior:**

- The date field will default to today's date (current behavior from `Visit()` constructor)
- Users can manually select past dates from the date picker, but form submission will be rejected
- The HTML5 date input provides basic date selection UI; no custom date picker required

## Repository Standards

This implementation shall follow the established patterns and practices of the Emerald Grove Veterinary Clinic codebase:

**Testing Standards:**
- Follow strict TDD methodology (Red-Green-Refactor cycle) as mandated in CLAUDE.md
- Write failing tests before implementing validation logic
- Achieve minimum 90% line coverage for new code
- Use JUnit 5 for unit tests, Mockito for mocking, and Playwright for E2E tests
- Follow Arrange-Act-Assert pattern in test structure

**Code Organization:**
- Validation logic resides in the `owner` package alongside the Visit entity
- Test classes follow naming convention: `<ClassName>Tests.java`
- Use existing test fixtures and patterns (e.g., `TEST_OWNER_ID`, `TEST_PET_ID` constants)

**Spring Boot Patterns:**
- Use Jakarta Bean Validation annotations (`@FutureOrPresent` or custom constraint)
- Leverage `@Valid` annotation in controller methods
- Use `BindingResult` for validation error handling
- Follow existing controller patterns for form processing

**Commit Conventions:**
- Use conventional commit format: `test: add validation tests for past visit dates` (RED phase)
- Follow with: `feat: add past date validation to Visit entity` (GREEN phase)
- Complete with: `refactor: improve visit validation error messages` (REFACTOR phase)
- Include co-authorship footer as per existing workflow

## Technical Considerations

**Bean Validation Approach:**

The implementation will use Jakarta Bean Validation's `@FutureOrPresent` annotation on the `Visit.date` field. This is the simplest approach that aligns with existing patterns in the codebase.

**Alternative Considered:** Creating a custom `@PastDateNotAllowed` validator was considered but rejected for this iteration to keep the solution simple. The standard `@FutureOrPresent` annotation provides the exact behavior required.

**Dependencies:**

- No new dependencies required; Jakarta Bean Validation is already included via `spring-boot-starter-validation`
- Existing test dependencies (JUnit 5, Mockito, Playwright) are sufficient

**Database Considerations:**

- Validation occurs at the application layer (Java); no database constraints will be added
- Existing visits with past dates in the database will remain valid (no migration required)
- The `visit_date` column type (`DATE`) remains unchanged

**Performance Impact:**

- Validation adds negligible overhead (microseconds per request)
- No database queries are required for date validation
- No caching considerations necessary

## Security Considerations

**Data Validation:**

- Server-side validation is mandatory; client-side validation (HTML5 date input) is not trusted
- The `@Valid` annotation ensures validation runs before database persistence
- No sensitive data (API keys, credentials) is involved in this feature

**Input Sanitization:**

- Date input is already sanitized by Spring's `@DateTimeFormat` annotation on the Visit.date field
- The validation constraint prevents logical errors but does not introduce new security vectors

**Proof Artifact Security:**

- Test fixtures use safe, non-sensitive test data (e.g., test pet names, descriptions)
- No production data or credentials should be committed in test files
- E2E test screenshots may be generated but contain only test data

**Risk Assessment:**

No specific security risks identified. This is a low-risk feature focused on data validation.

## Success Metrics

1. **Test Coverage**: Achieve 100% line and branch coverage for Visit entity validation logic (verified via JaCoCo report)

2. **Test Passing Rate**: All unit tests (VisitControllerTests, ValidatorTests) and E2E tests (visit-scheduling.spec.ts) pass consistently

3. **Zero Regressions**: Existing visit creation tests continue to pass without modification (except for adding new test cases)

4. **User Experience**: Manual testing confirms clear error message is displayed when past date is submitted

5. **TDD Compliance**: Git history shows test commits precede implementation commits (RED → GREEN → REFACTOR)

## Open Questions

No open questions at this time. All requirements have been clarified through the questions process.
