# 06-spec-owner-search-enhancement.md

## Introduction/Overview

Enhance the owner search functionality in the Emerald Grove Veterinary Clinic system by adding telephone and city as additional search criteria, and provide CSV export capability for search results. Currently, users can only search owners by last name, which limits their ability to find owners when they have other identifying information like phone number or city. Additionally, users cannot export search results for external use (mailing lists, reports, data analysis). This feature addresses both limitations by extending the search form with telephone and city fields and adding a CSV export button to the results page.

## Goals

- Enable staff to search for owners using telephone number and/or city in addition to last name
- Provide CSV export functionality for owner search results to support external reporting needs
- Maintain backward compatibility with existing last name search behavior
- Ensure search filters are preserved across pagination for consistent user experience
- Implement comprehensive test coverage following strict TDD methodology across all layers

## User Stories

1. **As a veterinary clinic receptionist**, I want to search for owners by telephone number so that I can quickly find an owner's record when they call and I don't remember their name.

2. **As a veterinary clinic staff member**, I want to search for owners by city so that I can find all owners in a specific geographic area for local outreach or when a name is common.

3. **As a veterinary clinic manager**, I want to export search results to CSV so that I can use the data in Excel for mailing lists, reports, or marketing campaigns.

4. **As a clinic staff member**, I want multiple search criteria to work together (AND logic) so that I can narrow down results when searching for specific owners (e.g., "Smith in Madison").

5. **As a clinic user**, I want pagination to remember my search filters so that I don't lose my search context when navigating through multiple pages of results.

## Demoable Units of Work

### Unit 1: Enhanced Search Repository Queries

**Purpose:** Implement database query methods that support searching owners by telephone and city, in addition to the existing last name search. This provides the data access foundation for multi-criteria search.

**Functional Requirements:**
- The system shall provide a repository method that queries owners by last name, city, and telephone using AND logic
- The system shall perform case-insensitive matching on city field (exact match, not starts-with)
- The system shall perform exact matching on telephone field (full 10-digit number)
- The system shall treat empty/null search parameters as "ignore this criterion" (not included in query)
- The system shall support pagination for query results with configurable page size
- The repository method shall return Page<Owner> to support pagination metadata

**Proof Artifacts:**
- JUnit Test: `OwnerRepositoryTests.shouldFindOwnersByCityAndTelephone()` demonstrates multi-criteria query works correctly
- JUnit Test: `OwnerRepositoryTests.shouldIgnoreEmptySearchCriteria()` demonstrates empty fields are ignored
- Test Output: `./mvnw test -Dtest=OwnerRepositoryTests` shows all repository tests passing
- Code: Repository method implementation in `OwnerRepository.java`

### Unit 2: Controller Search Logic Enhancement

**Purpose:** Update the OwnerController to accept city and telephone parameters, invoke the enhanced repository queries, and handle the multi-criteria search flow including edge cases.

**Functional Requirements:**
- The system shall accept `city` and `telephone` as optional query parameters in the `/owners` endpoint
- The system shall pass all non-empty search criteria to the repository query method
- The system shall handle empty search results with appropriate error message (existing "not found" behavior)
- The system shall redirect to single owner details when exactly one result is found (existing behavior)
- The system shall display paginated results when multiple owners are found
- The system shall pass search criteria to the view model for pagination link generation

**Proof Artifacts:**
- JUnit Test: `OwnerControllerTests.shouldFindOwnersByMultipleCriteria()` demonstrates controller handles multi-field search
- JUnit Test: `OwnerControllerTests.shouldReturnNotFoundForNoResults()` demonstrates empty results handling
- Test Output: `./mvnw test -Dtest=OwnerControllerTests` shows controller tests passing
- Code: Controller method updates in `OwnerController.java`

### Unit 3: Search Form UI Enhancement

**Purpose:** Update the Find Owners form to include city and telephone input fields, and update the results page pagination to preserve all search criteria in navigation links.

**Functional Requirements:**
- The user shall see telephone and city input fields added below the lastName field in the Find Owners form
- The system shall submit all three fields (lastName, city, telephone) when the Find Owner button is clicked
- The system shall preserve all search parameters (lastName, city, telephone) in pagination URLs
- The form shall use existing validation patterns (telephone format validation already exists on Owner entity)
- The pagination links shall include all non-empty search parameters in query string

**Proof Artifacts:**
- File Diff: Changes to `src/main/resources/templates/owners/findOwners.html` showing new input fields
- File Diff: Changes to `src/main/resources/templates/owners/ownersList.html` showing updated pagination links
- Playwright Test: `owner-search.spec.ts` with test case `should search owners by multiple criteria` demonstrates UI search functionality
- Test Output: `cd e2e-tests && npm test -- owner-search` shows E2E tests passing

### Unit 4: CSV Export Functionality

**Purpose:** Implement CSV export capability that allows users to download the current page of search results as a CSV file with owner contact information.

**Functional Requirements:**
- The system shall provide an "Export to CSV" button on the owners list results page
- The system shall export only the owners visible on the current page (5 records maximum)
- The system shall generate CSV with headers: "First Name", "Last Name", "Address", "City", "Telephone"
- The system shall name the downloaded file `owners.csv`
- The system shall return HTTP response with `Content-Type: text/csv` and `Content-Disposition: attachment`
- The system shall display error message if user attempts to export when no results are present

**Proof Artifacts:**
- JUnit Test: `OwnerControllerTests.shouldExportCurrentPageToCSV()` demonstrates CSV generation for current page
- JUnit Test: `OwnerControllerTests.shouldReturnErrorWhenExportingEmptyResults()` demonstrates empty results handling
- Test Output: `./mvnw test -Dtest=OwnerControllerTests` shows CSV export tests passing
- Playwright Test: `owner-search.spec.ts` with test case `should export search results to CSV` demonstrates file download
- Test Output: `cd e2e-tests && npm test -- owner-search` shows CSV export E2E test passing

## Non-Goals (Out of Scope)

1. **Advanced search operators**: No support for OR logic, fuzzy matching, or complex query operators. Search uses simple AND logic with exact/case-insensitive matching only.

2. **Telephone partial matching**: No support for searching by partial telephone numbers (e.g., last 4 digits, area code). Telephone search requires exact 10-digit match.

3. **City autocomplete or dropdown**: City field is a simple text input without autocomplete suggestions or dropdown list. Users must type the city name manually.

4. **Export all search results**: CSV export is limited to current page only (5 records). Exporting all results across all pages is not supported in this implementation.

5. **CSV format customization**: No user options to customize CSV format, column selection, or delimiter. Fixed format with predefined columns.

6. **Timestamped or custom filenames**: CSV file is always named `owners.csv`. No timestamp or search-criteria-based filename generation.

7. **Pet data in CSV export**: CSV export includes owner contact information only. Pet names and details are not included in the export.

8. **Search history or saved searches**: No capability to save search criteria or view search history. Each search is independent.

## Design Considerations

The search form UI will maintain the existing visual design language using the "liatrio-form-card" and "liatrio-form" CSS classes. The new city and telephone fields will be added below the lastName field in vertical stack layout, following the same form-group structure.

The "Export to CSV" button will be placed above the owners table on the results page, styled with Bootstrap's `btn btn-success` classes to indicate a positive action (data export). The button will be visible only when search results are present.

Pagination links will be updated to include `city` and `telephone` parameters in the query string following the existing pattern: `/owners?page=2&lastName=Smith&city=Madison&telephone=6085551023`.

No specific mockups or wireframes are provided. Implementation should follow existing UI patterns in the codebase.

## Repository Standards

Implementation should follow established repository patterns and conventions:

- **Spring Data JPA query methods**: Use Spring Data's query derivation or `@Query` annotations for repository methods
- **Controller conventions**: Follow existing OwnerController patterns for request parameter binding and model attribute handling
- **Thymeleaf templating**: Use Thymeleaf syntax consistent with existing templates (`th:field`, `th:href`, `th:text`)
- **Testing patterns**: Follow existing test structure with `@WebMvcTest`, `@DataJpaTest`, and Playwright E2E tests
- **Strict TDD methodology**: Write failing tests first (RED), implement minimal code (GREEN), refactor (REFACTOR)
- **Test coverage requirements**: Minimum 90% line coverage for new code
- **Validation annotations**: Reuse existing `@NotBlank` and `@Pattern` validation from Owner entity
- **Internationalization**: No new i18n message keys required for this feature (reusing existing validation messages)

## Technical Considerations

**Spring Data JPA Query Construction**: The repository query method will need dynamic query construction to handle optional parameters. Two implementation approaches:

1. **Query by Example (QBE)**: Use Spring Data's Example API to build queries dynamically
2. **@Query with conditional clauses**: Use JPQL `@Query` with conditional logic

Recommendation: Use `@Query` with JPQL for explicit control and readability.

**CSV Generation Strategy**: Two approaches for CSV generation:

1. **Manual StringBuilder**: Build CSV string manually with proper escaping
2. **CSV library**: Use Apache Commons CSV or OpenCSV library

Recommendation: Manual StringBuilder for simplicity (5 simple fields, no complex escaping needed). Add CSV library only if complexity increases.

**HTTP Response for CSV**: Use Spring's `ResponseEntity<String>` or `StreamingResponseBody` to return CSV content with appropriate headers:
- `Content-Type: text/csv`
- `Content-Disposition: attachment; filename="owners.csv"`

**Database Performance**: The enhanced search query should use existing indexes. The telephone field is already used for duplicate detection (indexed). City field may benefit from index if search performance is slow, but this should be measured, not assumed.

## Security Considerations

**CSV Export Authorization**: No authentication/authorization layer exists in the current application. CSV export is available to all users who can access the owner search page. If authorization is added in the future, CSV export should require the same permissions as viewing owner search results.

**CSV Injection Prevention**: Owner data fields (firstName, lastName, address, city) are user-controlled input. CSV export must escape values that start with special characters (`=`, `+`, `@`, `-`) to prevent formula injection attacks in spreadsheet applications.

**Data Privacy**: CSV export includes personally identifiable information (PII): names, addresses, phone numbers. Exported files should be handled according to organizational data privacy policies. No sensitive credential data is included in the export.

**No authentication tokens or API keys**: This feature does not introduce any new API keys, tokens, or credentials. No secrets management required.

## Success Metrics

1. **Search Precision**: Users can find owners by telephone or city with zero false positives (exact matching ensures precision)

2. **Test Coverage**: Maintain >90% line coverage across repository, controller, and service layers for new search and export functionality

3. **End-to-End Validation**: All Playwright E2E tests pass, demonstrating complete user flow from search form to CSV download

4. **Performance**: Enhanced search queries execute in under 200ms for typical database sizes (under 10,000 owners)

5. **Backward Compatibility**: Existing last-name-only searches continue to work without modification (zero regression failures)

## Open Questions

No open questions at this time. All design decisions have been clarified through the requirements gathering process.
