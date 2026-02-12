# 03-spec-prevent-duplicate-owner-creation.md

## Introduction/Overview

Prevent duplicate owner records from being created in the Emerald Grove Veterinary Clinic system. Currently, the owner creation process validates required fields and formats but does not check if an owner with the same identifying information already exists. This can lead to duplicate records, data inconsistencies, and confusion when searching for owners. This feature adds duplicate detection logic during owner creation to block duplicate entries and provide clear feedback to users.

## Goals

- Prevent creation of duplicate owner records based on first name, last name, and telephone number
- Provide clear, actionable error messages when duplicate creation is attempted
- Maintain data quality by ensuring each unique person is represented only once
- Implement validation that integrates seamlessly with existing Spring Boot validation patterns
- Ensure comprehensive test coverage across repository, controller, and end-to-end layers

## User Stories

1. **As a veterinary clinic staff member**, I want to be prevented from creating duplicate owner records so that I don't accidentally create multiple profiles for the same person.

2. **As a veterinary clinic staff member**, I want to see a clear error message when I attempt to create a duplicate owner so that I understand why the creation failed and can search for the existing record instead.

3. **As a system administrator**, I want duplicate detection to be case-insensitive and whitespace-tolerant so that minor formatting differences don't allow duplicates to slip through.

## Demoable Units of Work

### Unit 1: Repository-Level Duplicate Detection Query

**Purpose:** Implement the database query that detects duplicate owners based on first name, last name, and telephone number. This provides the foundation for duplicate detection across the application.

**Functional Requirements:**
- The system shall provide a repository method that queries for owners matching first name, last name, and telephone number
- The system shall perform case-insensitive matching on first name and last name fields
- The system shall normalize whitespace (trim) before matching first name and last name
- The system shall perform exact matching on telephone number (already validated to 10 digits)
- The repository method shall return an Optional<Owner> or boolean indicating if a duplicate exists

**Proof Artifacts:**
- JUnit Test: `OwnerRepositoryTests` with test methods demonstrating duplicate detection query works correctly
- Test Output: `./mvnw test -Dtest=OwnerRepositoryTests` shows all repository tests passing
- Code: Repository method implementation in `OwnerRepository.java`

### Unit 2: Controller-Level Duplicate Validation

**Purpose:** Integrate duplicate detection into the owner creation flow, blocking duplicate creation attempts and returning validation errors to the user interface.

**Functional Requirements:**
- The system shall check for duplicate owners before saving a new owner in `OwnerController.processCreationForm()`
- The system shall add field-level validation errors when a duplicate is detected
- The system shall use internationalized error message with key `{owner.duplicate}`
- The system shall return the user to the creation form when a duplicate is detected (not redirect)
- The system shall not create a database record when a duplicate is detected

**Proof Artifacts:**
- JUnit Test: `OwnerControllerTests` with test method `testProcessCreationFormWithDuplicateOwner()` demonstrates duplicate blocking
- Test Output: `./mvnw test -Dtest=OwnerControllerTests` shows controller tests passing including duplicate detection
- Screenshot: Manual test showing form with duplicate error message displayed

### Unit 3: Internationalization and Error Message Display

**Purpose:** Provide clear, consistent error messages in all supported languages that inform users why their owner creation attempt was blocked.

**Functional Requirements:**
- The system shall define error message in all 8 message property files (messages.properties, messages_es.properties, etc.)
- The error message shall clearly state "An owner with this name and telephone number already exists"
- The error message shall appear as a field-level error in the owner creation form
- The error message shall follow existing validation error styling and placement

**Proof Artifacts:**
- File Diff: Changes to `src/main/resources/messages/*.properties` showing new message key
- Test Output: `./mvnw test -Dtest=I18nPropertiesSyncTest` passes, confirming all language files synchronized
- Screenshot: Owner creation form showing duplicate error message

### Unit 4: End-to-End Validation and Proof of Functionality

**Purpose:** Validate the complete duplicate prevention flow in a real browser environment, demonstrating the feature works from user input through database validation and back to error display.

**Functional Requirements:**
- The system shall prevent duplicate owner creation through the complete web UI flow
- The user shall see the duplicate error message displayed in the browser
- The system shall allow creation of the first owner with given details
- The system shall block creation of the second owner with identical details
- The system shall allow creation of owners with different details after a duplicate is blocked

**Proof Artifacts:**
- Playwright Test: `owner-management.spec.ts` with test case `prevents duplicate owner creation` that creates owner, attempts duplicate, verifies error
- Test Output: `cd e2e-tests && npm test -- owner-management` shows E2E tests passing
- Screenshot: Playwright test artifact showing duplicate error message in browser

## Non-Goals (Out of Scope)

1. **Duplicate detection on owner edit**: This feature only prevents duplicates during creation. Editing an existing owner will not trigger duplicate checks (could be added in future enhancement).

2. **Duplicate merging or resolution**: If duplicates already exist in the database, this feature will not detect, merge, or resolve them. It only prevents new duplicates going forward.

3. **Advanced duplicate detection algorithms**: No fuzzy matching, phonetic matching, or "similar name" detection. Detection is based on exact field matching (with case-insensitivity and whitespace normalization).

4. **Database-level unique constraints**: This feature implements application-level validation only. No database schema changes or unique constraints will be added.

5. **Duplicate detection for other entities**: This feature only addresses owner duplicates. Pet, Visit, or Vet duplicate detection is out of scope.

## Design Considerations

**Form Error Display:**
- Follow existing Spring MVC validation error pattern
- Error message appears below the relevant form fields (firstName, lastName, telephone)
- Use Bootstrap alert styling consistent with other validation errors
- Error styling already handled by Thymeleaf `th:errors` directive

**User Experience:**
- User remains on the creation form (no redirect) when duplicate detected
- Form retains user input so they can modify if needed
- Clear error message guides user to search for existing owner
- Consistent with other validation error experiences (required fields, telephone format)

## Repository Standards

**Architecture:**
- Follow established layered architecture: Controller → Repository → Database
- No service layer needed (existing pattern directly uses repository in controller)
- Repository method follows Spring Data JPA naming conventions

**Validation Patterns:**
- Use Spring's `BindingResult` for error handling
- Add validation errors using `result.rejectValue(field, errorCode, message)`
- Follow existing patterns in `VisitController` for custom validation

**Testing Standards:**
- JUnit 5 for unit and integration tests
- Follow Arrange-Act-Assert pattern
- Use `@DataJpaTest` for repository tests
- Use `@WebMvcTest` with MockMvc for controller tests
- Playwright + TypeScript for E2E tests
- Maintain 90%+ code coverage

**Internationalization:**
- All error messages must have keys in all 8 language files
- Use message keys format: `{owner.duplicate}`
- Run `I18nPropertiesSyncTest` to verify synchronization

**Commit Conventions:**
- Follow conventional commits: `test:`, `feat:`, `refactor:`, `docs:`
- Follow TDD RED-GREEN-REFACTOR cycle
- Test commits before implementation commits

## Technical Considerations

**Spring Data JPA Query:**
- Use query method naming convention: `findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone()`
- Spring Data JPA will auto-generate query from method name
- `IgnoreCase` suffix provides case-insensitive matching for String fields
- Consider using `@Query` annotation if method name becomes too complex

**Whitespace Normalization:**
- Trim firstName and lastName in controller before duplicate check
- Use `String.trim()` method
- Alternative: Could be done in repository with custom `@Query` and SQL `TRIM()` function

**Performance:**
- Duplicate check adds one additional database query per owner creation
- Query should be fast (indexed fields: first_name, last_name, telephone)
- Consider adding database index if performance becomes concern

**Database Compatibility:**
- Case-insensitive matching works on H2, MySQL, PostgreSQL
- Test across all database profiles to ensure consistent behavior

## Security Considerations

**Data Privacy:**
- Duplicate detection reveals that an owner with given name/telephone exists
- This is acceptable as the creation form is only accessible to authenticated clinic staff
- No sensitive information (PII beyond name/phone) is exposed in error messages

**Proof Artifacts:**
- Error messages in screenshots should use test data only (no real PII)
- Message property files contain no sensitive information
- Test data should use fictional names and phone numbers

## Success Metrics

1. **Zero duplicate owners created**: After feature deployment, no new owner records with identical first name, last name, and telephone number shall be created
2. **Test coverage maintained**: Overall test coverage remains above 90% including new duplicate detection code
3. **Error message clarity**: Error message is clear and actionable (validated through manual testing and team review)
4. **No regression**: Existing owner creation functionality continues to work for non-duplicate cases

## Open Questions

No open questions at this time.
