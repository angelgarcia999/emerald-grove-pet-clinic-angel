# 05 Questions Round 1 - UI Enhancements

Please answer each question below (select one or more options, or add your own notes). Feel free to add additional context under any question.

## 1. Language Selector - Placement and Behavior

Where should the language selector be displayed and how should it work?

- [x] (A) Dropdown in the top-right corner of the header/navbar (most common pattern)
- [ ] (B) Language flags/icons in the header (visual indicators)
- [x] (C) Dropdown with language names in native text (e.g., "English", "Español", "Deutsch")
- [ ] (D) Simple text links in footer
- [ ] (E) Other (describe)

## 2. Language Selector - Language List

Which languages should be included in the initial selector?

- [x] (A) All existing languages (EN, DE, ES, FA, KO, PT, RU, TR) - 8 total
- [ ] (B) Just a subset for MVP (e.g., EN, ES, DE) - which ones?
- [ ] (C) Other (describe)

## 3. Language Selector - Persistence

How should the selected language be persisted across the session?

- [x] (A) Store in session only (language persists during current visit)
- [ ] (B) Store in cookie (language persists across visits)
- [ ] (C) Use query parameter `?lang=xx` only (no persistence, user must select each visit)
- [ ] (D) Combination of cookie + query param support
- [ ] (E) Other (describe)

## 4. Preserve Filters - Which Filters to Preserve

For the "Find Owners" page, which search parameters should be preserved across pagination?

- [x] (A) Just `lastName` (the current search parameter)
- [ ] (B) Prepare for future filters (telephone, city) even if not implemented yet
- [ ] (C) All query parameters automatically
- [ ] (D) Other (describe)

## 5. Preserve Filters - Implementation Approach

How should filters be preserved when clicking pagination links?

- [x] (A) Add query parameters to pagination URLs in the template (e.g., `?lastName=Smith&page=2`)
- [ ] (B) Store filters in session and restore on page load
- [ ] (C) Use hidden form fields and POST for pagination
- [ ] (D) Other (describe)

## 6. Filter Vets by Specialty - UI Component

How should the specialty filter be presented on the Vet Directory page?

- [x] (A) Dropdown/select box with specialty names + "All Specialties" option
- [ ] (B) Radio buttons or checkboxes for each specialty
- [ ] (C) Search box to filter by specialty name
- [ ] (D) Other (describe)

## 7. Filter Vets by Specialty - Filter Logic

How should the specialty filter work?

- [x] (A) Show vets that have the selected specialty (one vet may have multiple specialties)
- [ ] (B) Show only vets with exactly that specialty
- [ ] (C) Support selecting multiple specialties (show vets with ANY of the selected)
- [ ] (D) Other (describe)

## 8. Filter Vets by Specialty - Pagination Integration

Should the vet specialty filter work with pagination?

- [x] (A) Yes, preserve specialty filter across pagination (similar to owners)
- [ ] (B) No, show all matching vets on one page
- [ ] (C) Filter applies only to current page
- [ ] (D) Other (describe)

## 9. Proof Artifacts - What to Demonstrate

What proof artifacts would best demonstrate these features work?

- [x] (A) Screenshots of language selector in different languages
- [ ] (B) URL examples showing preserved filters (e.g., `/owners?lastName=Smith&page=2`)
- [ ] (C) Screenshot of vet directory with specialty filter applied
- [ ] (D) Playwright E2E tests passing for all three features
- [ ] (E) All of the above
- [ ] (F) Other (describe)

## 10. Edge Cases and Error Handling

Which edge cases should be explicitly handled?

- [x] (A) No vets have the selected specialty (show "no results" message)
- [ ] (B) Invalid language code in query param (fallback to default language)
- [ ] (C) Pagination beyond available pages (show last page or error)
- [ ] (D) All of the above
- [ ] (E) Other (describe)
