# 07-spec-friendly-404-pages

## Introduction/Overview

Currently, when users attempt to access a non-existent owner or pet, the application throws an `IllegalArgumentException` which results in a raw exception view being displayed. This provides a poor user experience and exposes technical implementation details. This feature will implement user-friendly 404 error pages that gracefully handle missing resources with clear messaging and navigation options, following REST best practices by returning proper HTTP 404 status codes.

## Goals

1. **Improve User Experience**: Replace raw exception views with friendly, understandable 404 error pages
2. **Maintain Security**: Avoid exposing stack traces or internal system details to end users
3. **Enable Navigation**: Provide clear paths for users to recover from errors (e.g., link to "Find Owners")
4. **Follow REST Standards**: Return proper HTTP 404 status codes for missing resources
5. **Ensure Testability**: Implement comprehensive unit and E2E tests following strict TDD methodology

## User Stories

1. **As a clinic staff member**, I want to see a friendly error message when I navigate to a non-existent owner so that I understand what went wrong without seeing technical jargon.

2. **As a clinic staff member**, I want to see which owner or pet ID was not found so that I can verify if I used the correct identifier.

3. **As a clinic staff member**, I want a link back to the "Find Owners" page when I encounter a 404 error so that I can quickly search for the correct owner.

4. **As a system administrator**, I want the application to return proper HTTP 404 status codes so that monitoring tools and search engines can correctly identify missing resources.

## Demoable Units of Work

### Unit 1: Owner 404 Handling

**Purpose:** Handle missing owner scenarios with user-friendly 404 pages for clinic staff attempting to access non-existent owners.

**Functional Requirements:**
- The system shall throw a custom `OwnerNotFoundException` (annotated with `@ResponseStatus(HttpStatus.NOT_FOUND)`) when an owner is not found in the `OwnerController.findOwner()` method
- The system shall return HTTP 404 status code when an owner is not found
- The system shall display an entity-specific error message: "Owner with ID {id} was not found"
- The system shall display a "Find Owners" navigation link on the 404 error page
- The system shall log missing owner access at INFO level (not as an error)
- The system shall support internationalization with message keys in all 8 language files

**Proof Artifacts:**
- **JUnit Test**: `OwnerControllerTests.testShowOwnerNotFound()` passes, asserting:
  - HTTP 404 status code is returned
  - View name is "error"
  - Model contains error message with owner ID
- **Playwright E2E Test**: Navigate to `/owners/999999` and verify:
  - User-friendly 404 page is displayed
  - Page contains "Owner with ID 999999 was not found" message
  - "Find Owners" link is present and functional
  - No stack traces or technical details are visible

### Unit 2: Pet 404 Handling

**Purpose:** Handle missing pet scenarios with user-friendly 404 pages for clinic staff attempting to access non-existent pets or pets that don't belong to the specified owner.

**Functional Requirements:**
- The system shall throw a custom `PetNotFoundException` (annotated with `@ResponseStatus(HttpStatus.NOT_FOUND)`) when a pet is not found in the `PetController.findPet()` method
- The system shall return HTTP 404 status code when a pet is not found
- The system shall handle the edge case where a pet exists but doesn't belong to the specified owner (when `owner.getPet(petId)` returns null)
- The system shall display an entity-specific error message: "Pet with ID {id} was not found"
- The system shall display a "Find Owners" navigation link on the 404 error page
- The system shall support internationalization with message keys in all 8 language files

**Proof Artifacts:**
- **JUnit Test**: `PetControllerTests.testShowPetNotFound()` and `PetControllerTests.testShowPetBelongsToDifferentOwner()` pass, asserting:
  - HTTP 404 status code is returned for both scenarios
  - View name is "error"
  - Model contains error message with pet ID
- **Playwright E2E Test**: Navigate to `/owners/1/pets/999999` and verify:
  - User-friendly 404 page is displayed
  - Page contains "Pet with ID 999999 was not found" message
  - "Find Owners" link is present and functional
  - No stack traces or technical details are visible

## Non-Goals (Out of Scope)

1. **Visit 404 Handling**: This feature will NOT handle missing visits (e.g., `/owners/{ownerId}/pets/{petId}/visits/{visitId}`). Only owners and pets are in scope.
2. **Other HTTP Error Codes**: Will NOT implement custom handling for 500, 403, or other error codes beyond 404.
3. **Global Exception Handling with @ControllerAdvice**: Will NOT add a global `@ControllerAdvice` class. Instead, we'll use Spring's `@ResponseStatus` annotation for simplicity.
4. **Advanced Error Recovery**: Will NOT implement features like "similar owners" suggestions or inline search functionality on the error page.
5. **Configurable Error Behavior**: Will NOT make error handling configurable per environment (e.g., different behavior in dev vs. production).
6. **Special Handling for Edge Cases**: Will NOT add special handling for negative IDs, non-numeric IDs, or very large IDs - Spring MVC handles these naturally.

## Design Considerations

**Error Page Layout:**
- Reuse and enhance the existing `error.html` template located at `src/main/resources/templates/error.html`
- The template already has a well-structured error card with pet image and uses Thymeleaf's `th:switch` to handle different status codes
- Enhancements needed:
  - Add a "Find Owners" navigation link specifically for 404 errors
  - Display the entity-specific error message from the exception in the `${message}` attribute
  - Maintain the existing visual style with the error card and pet image

**Message Display:**
- Use internationalization (i18n) message keys for all user-facing text
- Display the specific ID that was not found to help users troubleshoot
- Keep error messages concise and non-technical

**Navigation:**
- Include a prominent "Find Owners" link/button on 404 error pages
- Link should navigate to `/owners/find` (the owner search page)

## Repository Standards

**Code Organization:**
- Follow the existing package structure: `org.springframework.samples.petclinic.owner`
- Place custom exceptions (`OwnerNotFoundException`, `PetNotFoundException`) in the `owner` package alongside `Owner` and `Pet` entities
- Follow Spring Boot exception handling conventions using `@ResponseStatus` annotation

**Testing Standards:**
- Follow strict TDD methodology (RED-GREEN-REFACTOR)
- Use `@WebMvcTest` for controller unit tests with `@MockitoBean` for repositories
- Use MockMvc to simulate HTTP requests and assert responses
- Follow existing test patterns in `OwnerControllerTests.java` and `PetControllerTests.java`
- Use Playwright for E2E tests with descriptive test names
- Maintain minimum 90% line coverage for new code

**Internationalization Standards:**
- Add message keys to all 8 existing language files: `messages_en.properties`, `messages_de.properties`, `messages_es.properties`, `messages_fa.properties`, `messages_ko.properties`, `messages_pt.properties`, `messages_ru.properties`, `messages_tr.properties`
- Follow existing key naming conventions (e.g., `error.404`, `error.500`)
- Use the i18n-sync-validator agent to ensure all keys exist across all language files

**Exception Handling Standards:**
- Use Spring's `@ResponseStatus` annotation on custom exceptions
- Include the entity ID in exception messages for troubleshooting
- Replace existing `IllegalArgumentException` throws with custom exceptions
- Log at INFO level (not WARNING or ERROR) as 404s are expected behavior

## Technical Considerations

**Exception Handling Approach:**
- Create custom exception classes: `OwnerNotFoundException` and `PetNotFoundException`
- Annotate exceptions with `@ResponseStatus(HttpStatus.NOT_FOUND)` to automatically map to HTTP 404
- Modify `@ModelAttribute` methods in `OwnerController` (line 68-74) and `PetController` (line 66-86) to throw custom exceptions instead of `IllegalArgumentException`
- Spring Boot will automatically use the existing `error.html` template when these exceptions are thrown

**HTTP Status Codes:**
- Use proper HTTP 404 status codes for RESTful semantics
- Allows browsers, search engines, and monitoring tools to correctly identify missing resources
- Compatible with existing Spring Boot error handling infrastructure

**Template Enhancement:**
- The existing `error.html` template uses `th:switch="${status}"` to handle different status codes
- Add Thymeleaf conditional logic to display the "Find Owners" link only for 404 errors
- Use `${message}` attribute to display entity-specific error messages

**Edge Case: Pet Ownership Validation:**
- In `PetController.findPet()`, when `owner.getPet(petId)` returns null, throw `PetNotFoundException`
- This handles the scenario where a pet exists in the database but doesn't belong to the specified owner

**Logging:**
- Log missing entity access at INFO level: `log.info("Owner with ID {} not found", ownerId)`
- INFO level provides visibility for troubleshooting and detecting broken links without creating false alarms in error logs

## Security Considerations

**Exception Message Safety:**
- Custom exceptions should include only the entity type (Owner/Pet) and ID
- MUST NOT include:
  - Stack traces in user-facing error pages
  - Database query details
  - Internal system paths or class names
  - Sensitive owner or pet information

**Error Page Content:**
- Verify that the enhanced `error.html` template does not display `${trace}` or other debug information
- Only display user-friendly messages via `${message}` attribute
- Ensure the existing error handling configuration prevents debug information leakage in production

**Test Data Security:**
- E2E tests should use clearly non-existent IDs (e.g., 999999) that don't conflict with seed data
- Unit tests should mock repository responses and not rely on production data

## Success Metrics

1. **User Experience**: 404 error pages display friendly messages with no stack traces or technical jargon visible to end users
2. **Navigation Recovery**: 100% of 404 error pages include a functional "Find Owners" link
3. **REST Compliance**: 100% of missing entity requests return proper HTTP 404 status codes
4. **Test Coverage**: Achieve 90%+ line coverage for modified controller methods and new exception classes
5. **Internationalization**: All 8 language files contain the new error message keys, verified by `I18nPropertiesSyncTest`

## Open Questions

No open questions at this time. All clarifying questions have been answered and documented in `07-questions-1-friendly-404-pages.md`.
