# 05-spec-ui-enhancements.md

## Introduction/Overview

This specification covers three UI enhancement features for the Emerald Grove Veterinary Clinic application that improve user experience and navigation: a language selector for internationalization support, pagination filter preservation, and veterinarian specialty filtering. These enhancements leverage existing infrastructure (i18n message files, pagination patterns, specialty entities) to provide users with better control over their browsing experience.

## Goals

- Enable users to switch the application UI language from a convenient dropdown in the header
- Preserve search filters (lastName) when users paginate through owner search results
- Allow users to filter the veterinarian directory by medical specialty
- Maintain consistent pagination behavior across all filtered views
- Provide clear user feedback when filters return no results

## User Stories

- **As a clinic staff member**, I want to switch the application language to my preferred language so that I can use the system comfortably in my native language.

- **As a clinic administrator searching for owners**, I want my search filters to persist when I navigate between pages so that I don't have to re-enter my search criteria on each page.

- **As a pet owner looking for a vet**, I want to filter veterinarians by their medical specialty so that I can quickly find a specialist for my pet's specific needs.

- **As a user navigating filtered results**, I want consistent pagination behavior so that I can efficiently browse through large result sets without losing my filter context.

## Demoable Units of Work

### Unit 1: Language Selector in Header

**Purpose:** Enable users to switch the application language from any page using a dropdown in the header navbar.

**Functional Requirements:**
- The system shall display a language selector dropdown in the top-right corner of the header navbar on all pages
- The language selector shall display all 8 supported languages in their native text (English, Español, Deutsch, فارسی, 한국어, Português, Русский, Türkçe)
- The system shall switch the UI language immediately when a user selects a language from the dropdown
- The system shall persist the selected language in the user's session so it remains active during their current visit
- The system shall use Spring's LocaleResolver and LocaleChangeInterceptor for language switching
- The selected language shall apply to all internationalized text including navigation labels, form labels, validation messages, and page content

**Proof Artifacts:**
- Screenshot: Language selector dropdown visible in header demonstrates UI component exists
- Screenshot: Application displayed in Spanish (Español selected) demonstrates language switching works
- Screenshot: Application displayed in German (Deutsch selected) demonstrates multiple languages supported
- URL: Page reloaded after language selection maintains the selected language demonstrates session persistence

### Unit 2: Preserve Search Filters Across Pagination

**Purpose:** Maintain the lastName search parameter when users navigate between pages of owner search results.

**Functional Requirements:**
- The system shall preserve the `lastName` query parameter when generating pagination links on the owner search results page
- Pagination links shall include the current search criteria in the URL (e.g., `/owners?lastName=Smith&page=2`)
- The system shall apply the preserved lastName filter when rendering each paginated page
- The user shall see consistent search results across all pages without re-entering search criteria
- The current page number and lastName filter shall both be visible in the browser URL
- The system shall maintain existing pagination behavior (5 owners per page)

**Proof Artifacts:**
- URL: `/owners?lastName=Franklin&page=1` displays first page of Franklin search demonstrates filter in URL
- URL: `/owners?lastName=Franklin&page=2` displays second page of Franklin search demonstrates filter preservation
- Screenshot: Pagination links include lastName parameter demonstrates template generates correct URLs
- Test: OwnerController integration test verifies filter preservation demonstrates backend support

### Unit 3: Filter Veterinarians by Specialty

**Purpose:** Allow users to filter the veterinarian directory by selecting a medical specialty from a dropdown.

**Functional Requirements:**
- The system shall display a specialty filter dropdown at the top of the veterinarian directory page
- The dropdown shall include an "All Specialties" option (default) and all available specialty names from the database
- The system shall filter the veterinarian list to show only vets who have the selected specialty when a specific specialty is chosen
- The system shall display all veterinarians when "All Specialties" is selected
- The system shall preserve the specialty filter parameter across pagination (e.g., `/vets.html?specialty=radiology&page=2`)
- The system shall display a "No veterinarians found with this specialty" message when the filter returns zero results
- The user shall see the specialty filter value maintained in the dropdown after pagination or page reload

**Proof Artifacts:**
- Screenshot: Specialty dropdown visible on vet directory demonstrates filter UI exists
- URL: `/vets.html?specialty=radiology&page=1` shows filtered results demonstrates specialty filtering works
- Screenshot: "No results" message displayed for specialty with no vets demonstrates edge case handling
- Test: VetController integration test with specialty parameter verifies filtering logic demonstrates backend implementation

## Non-Goals (Out of Scope)

1. **Language Preferences Beyond Session**: This feature will NOT persist language selection across browser sessions using cookies or user accounts. Language selection is session-only.

2. **Advanced Filter Combinations**: This feature will NOT add telephone or city search filters to the owner search. Only lastName filter preservation is included.

3. **Multiple Specialty Filtering**: This feature will NOT support selecting multiple specialties simultaneously. Users can filter by one specialty at a time.

4. **Language Selector Configuration UI**: This feature will NOT add admin controls to enable/disable specific languages. All 8 existing languages are always available.

5. **Real-time Filter Updates**: This feature will NOT implement AJAX or real-time filtering. All filters work via full page navigation with query parameters.

## Design Considerations

**Language Selector UI:**
- Dropdown should be styled consistently with existing navbar elements
- Language names should be displayed in their native script (e.g., "한국어" not "Korean")
- Current language should be indicated in the dropdown (selected state)
- Dropdown should be responsive and work on mobile viewports

**Filter UI Components:**
- Specialty dropdown should match the visual style of existing form controls
- Dropdown should be positioned prominently near the top of the veterinarian list
- "All Specialties" default option should be clearly distinguishable
- Filter state (selected specialty) should be visually apparent to users

**Pagination Links:**
- Pagination links should maintain consistent styling when including query parameters
- URL structure should remain clean and readable (e.g., `/owners?lastName=Smith&page=2`)
- Active page indicator should work correctly with filter parameters

## Repository Standards

**Coding Standards:**
- Follow existing Spring Boot and Spring MVC patterns used in OwnerController and VetController
- Use Thymeleaf template expressions (`th:href`, `th:selected`) for dynamic URL generation
- Follow existing i18n message key naming conventions in `messages.properties` files
- Maintain consistent controller method signatures (use `@RequestParam` with defaults)

**Testing Conventions:**
- Write `@WebMvcTest` unit tests for controller endpoints following OwnerControllerTests pattern
- Use MockMvc for HTTP request/response testing
- Write integration tests using `@SpringBootTest` for end-to-end validation
- Add Playwright E2E tests following existing patterns in `e2e-tests/tests/features/`

**TDD Compliance:**
- Follow RED-GREEN-REFACTOR cycle mandated by CLAUDE.md
- Write failing tests before implementation
- Maintain minimum 90% line coverage for new code

**File Organization:**
- Place controller changes in existing controller files (VetController, OwnerController)
- Add i18n message keys to all existing `messages*.properties` files (8 language files)
- Update templates in `src/main/resources/templates/` (layout.html, vets/vetList.html, owners/ownersList.html)

## Technical Considerations

**Spring Framework Integration:**
- Leverage existing `LocaleContextHolder` for language management
- Use Spring's `LocaleChangeInterceptor` to handle `?lang=xx` query parameters
- Add `LocaleResolver` bean configuration in WebConfiguration class
- Query parameters for filtering use standard Spring `@RequestParam` with default values

**Repository Layer:**
- Add `findBySpecialtiesName()` method to VetRepository for specialty filtering
- Use Spring Data JPA query methods or `@Query` annotation for custom queries
- Leverage existing `Pageable` support for pagination with filters

**Template Engine:**
- Use Thymeleaf's `#httpServletRequest.getParameter()` to access current filter values
- Generate pagination URLs with `th:href` expressions that include query parameters
- Use `th:selected` for maintaining dropdown selections across page loads

**Database Compatibility:**
- Ensure specialty filtering queries work across H2, MySQL, and PostgreSQL
- Test with existing TestContainers setup for database-specific integration tests

## Security Considerations

**Input Validation:**
- Validate language codes against the list of supported locales to prevent injection attacks
- Sanitize specialty name parameters to prevent SQL injection (use parameterized queries)
- Validate page numbers to prevent integer overflow or negative values

**No Sensitive Data in Proof Artifacts:**
- Screenshots should use test data only (existing sample owners/vets)
- No real pet owner information should be visible in proof artifacts
- URLs in documentation should use example/test database content

## Success Metrics

1. **Language Selector Adoption**: Users can successfully switch between all 8 supported languages with the UI updating immediately
2. **Filter Preservation**: 100% of pagination links maintain the lastName search parameter, eliminating need to re-enter search criteria
3. **Specialty Filter Utility**: Users can filter veterinarians by specialty and navigate paginated results without losing filter context
4. **Test Coverage**: All three features achieve >90% line coverage with passing TDD test suites
5. **Zero Regressions**: Existing pagination and search functionality continues to work unchanged

## Open Questions

No open questions at this time. All requirements have been clarified through the questions document.
