# 06 Questions Round 1 - Owner Search Enhancement

Please answer each question below (select one or more options, or add your own notes). Feel free to add additional context under any question.

## 1. Search Criteria Combination Logic

How should multiple search criteria (lastName, city, telephone) work together when searching for owners?

- [x] (A) **AND logic** - All provided fields must match (e.g., lastName="Smith" AND city="London" both must match)
- [ ] (B) **OR logic** - Any provided field can match (e.g., lastName="Smith" OR city="London" either matches)
- [ ] (C) **Smart combination** - lastName is required, city and telephone are optional additional filters
- [ ] (D) **Independent searches** - Allow searching by EITHER lastName OR city OR telephone individually
- [ ] (E) Other (describe)

**Your Answer:**

## 2. Search Field Behavior

What should happen when a user leaves search fields empty?

- [x] (A) **Empty lastName returns all owners** (current behavior), empty city/telephone are ignored
- [ ] (B) **All fields optional** - Empty search returns all owners, any filled field filters results
- [ ] (C) **At least one field required** - User must provide at least one search criterion
- [ ] (D) **lastName always required** - City and telephone only work as additional filters with lastName
- [ ] (E) Other (describe)

**Your Answer:**

## 3. Telephone Search Matching

How should telephone number search work?

- [x] (A) **Exact match only** - Must match the full 10-digit telephone number exactly
- [ ] (B) **Partial match** - Match if entered digits appear anywhere in the telephone (e.g., "555" matches "5551234567")
- [ ] (C) **Starts with** - Match if telephone starts with entered digits (e.g., "555" matches "5551234567" but not "1235556789")
- [ ] (D) **Ends with** - Match if telephone ends with entered digits (useful for remembering last 4 digits)
- [ ] (E) Other (describe)

**Your Answer:**

## 4. City Search Matching

How should city search work?

- [x] (A) **Exact match** - Must match city name exactly (case-insensitive)
- [ ] (B) **Starts with** - Match if city starts with entered text (similar to current lastName behavior)
- [ ] (C) **Contains** - Match if city contains entered text anywhere
- [ ] (D) **Dropdown selection** - Provide a dropdown of existing cities, user selects from list
- [ ] (E) Other (describe)

**Your Answer:**

## 5. Search Form UI Location

Where should the new search fields (city, telephone) be added?

- [x] (A) **Add to existing form** - Add city and telephone fields to the current "Find Owners" form below lastName
- [ ] (B) **Advanced search section** - Keep lastName visible, add "Advanced Search" expandable section for city/telephone
- [ ] (C) **Replace single field** - Replace lastName-only field with multi-field search form
- [ ] (D) **Side-by-side layout** - Display all three fields (lastName, city, telephone) in a horizontal row
- [ ] (E) Other (describe)

**Your Answer:**

## 6. CSV Export Trigger

How should users initiate CSV export of search results?

- [x] (A) **Export button on results page** - Add "Export to CSV" button above/below the owners table
- [ ] (B) **Export button in search form** - Add "Export Results" button next to "Find Owner" button
- [ ] (C) **Both options** - Provide export button in both search form and results page
- [ ] (D) **Right-click or dropdown menu** - Add CSV export option to a table action menu
- [ ] (E) Other (describe)

**Your Answer:**

## 7. CSV Export Scope

What data should be included in the CSV export?

- [x] (A) **Current page only** - Export only the owners visible on the current page (5 records)
- [ ] (B) **All search results** - Export all owners matching the search criteria, regardless of pagination
- [ ] (C) **User choice** - Provide option to export "Current Page" or "All Results"
- [ ] (D) **Selected rows** - Allow user to select specific owners to export via checkboxes
- [ ] (E) Other (describe)

**Your Answer:**

## 8. CSV File Content and Format

What information should be included in the CSV file?

- [x] (A) **Basic fields only** - First Name, Last Name, Address, City, Telephone (visible columns only)
- [ ] (B) **Include pet names** - Add pets as comma-separated list in one column
- [ ] (C) **Include pet details** - Add separate columns for each pet (Pet1 Name, Pet1 Type, Pet2 Name, etc.)
- [ ] (D) **Minimal export** - First Name, Last Name, Telephone only (contact info focus)
- [ ] (E) Other (describe)

**Your Answer:**

## 9. CSV Filename Convention

What should the exported CSV file be named?

- [x] (A) **Simple static name** - "owners.csv"
- [ ] (B) **Timestamped** - "owners_2026-02-17_143022.csv" (includes date and time)
- [ ] (C) **Search-aware** - "owners_search_Madison.csv" (includes search criteria in filename)
- [ ] (D) **User-prompted** - Let browser's "Save As" dialog allow user to name the file
- [ ] (E) Other (describe)

**Your Answer:**

## 10. Pagination Behavior with Enhanced Search

How should pagination work when using multiple search criteria?

- [x] (A) **Preserve all filters** - Pagination links maintain lastName, city, and telephone parameters in URL
- [ ] (B) **Same as current** - Reuse existing pagination pattern (currently preserves lastName only)
- [ ] (C) **Reset on new search** - Always go back to page 1 when search criteria change
- [ ] (D) **Session-based** - Store search criteria in session, pagination works without URL parameters
- [ ] (E) Other (describe)

**Your Answer:**

## 11. Empty Search Results for CSV Export

What should happen if user tries to export when no owners match the search?

- [x] (A) **Show error message** - Display message "No results to export"
- [ ] (B) **Export empty CSV** - Generate CSV with headers but no data rows
- [ ] (C) **Disable export button** - Hide or disable the export button when results are empty
- [ ] (D) **Export all owners** - If no search results, export all owners in database
- [ ] (E) Other (describe)

**Your Answer:**

## 12. Proof Artifacts - What Will Demonstrate Success?

What types of proof will best demonstrate this feature is working correctly?

- [ ] (A) **Screenshots** - Browser screenshots showing search form, results, CSV download
- [x] (B) **Test outputs** - JUnit and Playwright test execution results
- [ ] (C) **Sample CSV file** - An actual exported CSV file showing the output format
- [ ] (D) **Video demo** - Screen recording showing complete search → export workflow
- [ ] (E) **All of the above** - Comprehensive proof package with multiple artifact types
- [ ] (F) Other (describe)

**Your Answer:**

---

## Instructions

1. **Mark your choices** with [x] for each question
2. **Add any additional context** or specific requirements below each question if needed
3. **Save this file** when you're done
4. **Let me know** you've completed the questions so I can continue with the spec generation

