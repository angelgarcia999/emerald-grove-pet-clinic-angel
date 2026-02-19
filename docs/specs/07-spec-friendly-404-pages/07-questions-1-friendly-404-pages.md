# 07 Questions Round 1 - Friendly 404 Pages

Please answer each question below (select one or more options, or add your own notes). Feel free to add additional context under any question.

## 1. Exception Handling Approach

How should the application detect and handle missing owners/pets?

- [x] (A) Modify the existing `@ModelAttribute` method to throw a custom exception (e.g., `OwnerNotFoundException`) that Spring will automatically map to 404
- [ ] (B) Add a `@ControllerAdvice` class with `@ExceptionHandler` methods to globally handle missing entity exceptions
- [ ] (C) Handle missing entities directly in each controller method with explicit Optional checks and return custom ModelAndView for 404
- [ ] (D) Use Spring Boot's default error handling with custom error attributes
- [ ] (E) Other (describe)

**Context:** The `OwnerController` currently throws `IllegalArgumentException` in the `@ModelAttribute` method (line 72-73) when an owner is not found. We need to convert this to proper 404 handling.

**Decision Rationale:** Option A is the simplest and most maintainable approach. The codebase already uses `@ModelAttribute` methods in both `OwnerController` (line 68-74) and `PetController` (line 66-72, 74-86). By creating custom exceptions (`OwnerNotFoundException`, `PetNotFoundException`) annotated with `@ResponseStatus(HttpStatus.NOT_FOUND)`, Spring Boot will automatically return 404 status codes and use the existing `error.html` template. This aligns with Spring Boot conventions and requires minimal code changes.

## 2. Error Page Design

What should the 404 error page include?

- [x] (A) Friendly message explaining the resource wasn't found (e.g., "Owner not found")
- [x] (B) The invalid ID or identifier that was requested
- [x] (C) Navigation link back to "Find Owners" page
- [ ] (D) Navigation link to homepage
- [ ] (E) Search box to find owners directly from the error page
- [ ] (F) Contact support information
- [ ] (G) Other (describe)

**Note:** Options A, B, and C are pre-checked based on the issue description. Please modify if needed.

## 3. Error Message Specificity

How specific should the error messages be?

- [ ] (A) Generic message for all 404s: "The requested page was not found"
- [x] (B) Entity-specific messages: "Owner not found" vs "Pet not found"
- [x] (C) Include the specific ID: "Owner with ID 999 was not found"
- [ ] (D) Include helpful context: "Owner with ID 999 was not found. The owner may have been deleted or the link may be incorrect."
- [ ] (E) Other (describe)

**Decision Rationale:** Options B and C together provide the right balance of specificity without being verbose. The current `IllegalArgumentException` messages already follow this pattern (e.g., "Owner not found with id: 1"). Entity-specific messages help users understand what went wrong, and including the ID is helpful for troubleshooting without overwhelming the user with speculation about why it's missing.

## 4. Scope Boundaries

Should this feature handle other missing entity scenarios?

- [ ] (A) Only missing owners when accessing `/owners/{ownerId}`
- [ ] (B) Only missing pets when accessing `/owners/{ownerId}/pets/{petId}`
- [x] (C) Both owners and pets (as specified in the issue)
- [ ] (D) Extend to visits as well (e.g., `/owners/{ownerId}/pets/{petId}/visits/{visitId}`)
- [ ] (E) Other (describe)

**Context:** The issue mentions "missing owner/pet" but we should clarify if this includes visits or other entities.

**Decision Rationale:** The issue explicitly states "Return user-friendly 404 pages instead of raw exception views for missing owner/pet." Both `OwnerController` and `PetController` have `@ModelAttribute` methods that throw `IllegalArgumentException` when entities are not found. Handling both aligns with the issue requirements. Visits are not mentioned and adding them would increase scope beyond what's requested.

## 5. Internationalization (i18n)

Should error messages support multiple languages?

- [x] (A) Yes, add message keys to all existing language files (messages_*.properties)
- [ ] (B) Yes, but only for English initially (messages_en.properties)
- [ ] (C) No, hardcode messages in English in the template
- [ ] (D) Other (describe)

**Context:** The codebase already has i18n support with multiple language files. The i18n-sync-validator agent will check that all keys exist across all language files.

**Decision Rationale:** The codebase already has comprehensive i18n support with 8 language files (en, de, es, fa, ko, pt, ru, tr) and existing error messages (error.404, error.500, error.general) are already translated across all files. The `I18nPropertiesSyncTest` validates that all keys exist in all language files. We should follow this established pattern and add new keys like `error.owner.notFound` and `error.pet.notFound` to all language files. The i18n-sync-validator agent can help generate translations.

## 6. HTTP Status Code Behavior

Should the application return actual HTTP 404 status codes?

- [x] (A) Yes, return HTTP 404 status code so browsers/tools recognize it as "Not Found"
- [ ] (B) No, return HTTP 200 with a "not found" message (user-friendly but technically incorrect)
- [ ] (C) Configurable based on environment (404 in production, 200 in development)
- [ ] (D) Other (describe)

**Context:** Proper REST semantics dictate returning 404, but some applications prefer 200 for UX reasons.

**Decision Rationale:** Option A is the correct approach. The existing `error.html` template already handles 404 status codes properly (line 13: `th:case="404"`). Using proper HTTP status codes follows REST best practices and allows browsers, search engines, and monitoring tools to correctly identify missing resources. Spring's `@ResponseStatus(HttpStatus.NOT_FOUND)` annotation makes this trivial to implement.

## 7. Logging and Monitoring

How should missing entity access be logged?

- [ ] (A) Log as WARNING with the requested ID and endpoint
- [x] (B) Log as INFO (not an error, just a missing resource)
- [ ] (C) Log as DEBUG (too noisy for production)
- [ ] (D) Don't log (user error, not system error)
- [ ] (E) Other (describe)

**Decision Rationale:** INFO level is appropriate because accessing a non-existent resource is not a system error or application failure - it's expected behavior that should be handled gracefully. INFO provides visibility for troubleshooting and analytics (e.g., detecting broken links) without cluttering logs with false alarms. This is consistent with Spring Boot's default behavior for 404s.

## 8. Testing Requirements

What level of testing is required? (Multiple selections allowed)

- [x] (A) JUnit unit tests with MockMvc asserting 404 status and view name
- [x] (B) JUnit unit tests asserting error message content
- [ ] (C) JUnit integration tests with real database
- [x] (D) Playwright E2E tests navigating to non-existent owners
- [x] (E) Playwright E2E tests navigating to non-existent pets
- [x] (F) Playwright tests verifying "Find Owners" link works
- [ ] (G) Other (describe)

**Context:** The issue mentions both JUnit and Playwright tests. Please confirm which specific tests are needed.

**Decision Rationale:** Following TDD best practices and the existing test patterns in `OwnerControllerTests.java`, we need:
- **A & B**: Unit tests using MockMvc (like `testShowOwner()` at line 228) to verify 404 status, correct view name ("error"), and error message content in the model
- **D, E, F**: E2E tests to validate the complete user experience from browser to error page, including navigation links back to "Find Owners"
- **Not C**: Integration tests with real DB are unnecessary since we're testing exception handling, not database queries. The existing `@WebMvcTest` approach with mocked repositories is sufficient and faster.

## 9. Existing Error Template

Should we reuse the existing `error.html` template or create new dedicated templates?

- [x] (A) Reuse existing `error.html` and enhance it to handle 404s better
- [ ] (B) Create a new `404.html` template specifically for not-found errors
- [ ] (C) Create entity-specific templates: `ownerNotFound.html`, `petNotFound.html`
- [ ] (D) Other (describe)

**Context:** There's already an `error.html` template at `src/main/resources/templates/error.html` that handles 404, 500, and general errors using a switch statement on the status code.

**Decision Rationale:** The existing `error.html` template (lines 12-16) already has a well-structured pattern using `th:switch` on status codes. It displays `error.404` message for 404s and has a `message` attribute (line 18) for additional context. We should enhance this template to:
1. Display the entity-specific error message from the exception
2. Add a navigation link back to "Find Owners" for 404 errors
3. Keep the consistent look and feel with the existing error page

This approach is simpler, more maintainable, and follows DRY principles rather than creating multiple templates.

## 10. Edge Cases

Are there any edge cases we should explicitly handle?

- [ ] (A) Negative IDs (e.g., `/owners/-1`)
- [ ] (B) Non-numeric IDs (e.g., `/owners/abc`)
- [ ] (C) Very large IDs (e.g., `/owners/999999999`)
- [ ] (D) Null or empty IDs (e.g., `/owners/`)
- [x] (E) Pet exists but belongs to different owner (e.g., `/owners/1/pets/999`)
- [ ] (F) All of the above
- [x] (G) None - let Spring handle these naturally
- [ ] (H) Other (describe)

**Decision Rationale:**
- **A, B, C**: Spring MVC's path variable binding automatically handles these. Non-numeric values return 400 Bad Request, and negative/large IDs will simply not be found in the database, triggering our 404 handler. No special handling needed.
- **D**: This matches a different route pattern (`/owners/`) which either doesn't exist (404 from Spring) or routes to the list view. No issue.
- **E**: This is a legitimate business logic edge case. The `PetController.findPet()` method (line 74-86) calls `owner.getPet(petId)` which returns null if the pet doesn't belong to that owner. This should be handled with a friendly 404 message like "Pet with ID 999 not found for this owner."
- **G**: For most cases, Spring's default behavior is sufficient. We only need explicit handling for the pet ownership scenario (E).

---

## Additional Notes

### Summary of Key Decisions

**Architecture Pattern:**
- Use custom exceptions (`OwnerNotFoundException`, `PetNotFoundException`) with `@ResponseStatus(HttpStatus.NOT_FOUND)` annotation
- Replace `IllegalArgumentException` in existing `@ModelAttribute` methods
- Leverage Spring Boot's automatic exception-to-HTTP-status mapping

**Error Page Design:**
- Enhance existing `error.html` template (don't create new templates)
- Add navigation link to "Find Owners" specifically for 404 errors
- Display entity-specific error messages (e.g., "Owner with ID 999 was not found")

**Internationalization:**
- Add new message keys: `error.owner.notFound`, `error.pet.notFound`, `error.findOwners.link`
- Update all 8 language files (en, de, es, fa, ko, pt, ru, tr)
- Use i18n-sync-validator agent to generate translations

**Testing Strategy:**
- JUnit `@WebMvcTest` tests in `OwnerControllerTests.java` and `PetControllerTests.java`
  - Test 404 status code is returned
  - Test "error" view name is returned
  - Test error message is in the model
- Playwright E2E tests
  - Navigate to non-existent owner (e.g., `/owners/999999`)
  - Navigate to non-existent pet (e.g., `/owners/1/pets/999999`)
  - Verify "Find Owners" link works and navigates correctly

**Edge Cases:**
- Pet not belonging to owner: Throw `PetNotFoundException` when `owner.getPet(petId)` returns null
- Other cases (negative IDs, non-numeric IDs): Handled naturally by Spring MVC

**Implementation Files to Modify:**
1. `src/main/java/org/springframework/samples/petclinic/owner/OwnerNotFoundException.java` (new)
2. `src/main/java/org/springframework/samples/petclinic/owner/PetNotFoundException.java` (new)
3. `src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java` (modify)
4. `src/main/java/org/springframework/samples/petclinic/owner/PetController.java` (modify)
5. `src/main/resources/templates/error.html` (enhance)
6. `src/main/resources/messages/messages*.properties` (8 files - add new keys)
7. `src/test/java/org/springframework/samples/petclinic/owner/OwnerControllerTests.java` (add tests)
8. `src/test/java/org/springframework/samples/petclinic/owner/PetControllerTests.java` (add tests)
9. `e2e-tests/tests/friendly-404.spec.ts` (new E2E tests)
