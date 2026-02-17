# 04 Questions Round 1 - Upcoming Visits Page

Please answer each question below (select one or more options, or add your own notes). Feel free to add additional context under any question.

## 1. Navigation Integration

Where should users access the Upcoming Visits page?

- [x] (A) Add to main navigation menu (alongside "Find Owners" and "Veterinarians")
- [ ] (B) Link from homepage only
- [ ] (C) Accessible only via direct URL (no navigation link)
- [ ] (D) Add to a dropdown menu
- [ ] (E) Other (describe)

**Context**: Research shows existing navigation has: Home, Find Owners, Veterinarians, Error. Recommendation is to add to main nav.

---

## 2. Date Range Validation

What should happen if a user provides an invalid `days` parameter (e.g., days=0, days=-5, days=1000)?

- [ ] (A) Use default value (7 days) and proceed silently
- [x] (B) Show validation error message to user
- [ ] (C) Return HTTP 400 Bad Request
- [ ] (D) Enforce min/max limits (e.g., 1-365) with validation
- [ ] (E) Other (describe)

**Context**: Research suggests using Spring validation with `@Min(1) @Max(365)` and defaulting to 7.

---

## 3. Empty State Handling

What should the page show when no upcoming visits exist?

- [x] (A) Simple text message: "No upcoming visits scheduled"
- [ ] (B) Helpful message with action: "No upcoming visits. Schedule a visit from an owner's page."
- [ ] (C) Empty table with headers visible
- [ ] (D) Redirect to homepage
- [ ] (E) Other (describe)

**Context**: Research shows existing patterns use friendly messages for empty states.

---

## 4. Visit Display Format

What information should be displayed for each visit?

- [x] (A) Date, Pet Name (linked), Owner Name (linked), Description (basic - as researched)
- [ ] (B) Include pet type (e.g., "Fluffy (dog)")
- [ ] (C) Include owner contact info (telephone)
- [ ] (D) Include time of visit (not just date)
- [ ] (E) Other (describe)

**Context**: Research shows Visit entity only has date (LocalDate), not time. Recommendation is option A.

---

## 5. Future Enhancements (Out of Scope)

Which features should be explicitly excluded from this initial implementation?

- [x] (A) Pagination (keep simple, date-range limited)
- [x] (B) Filtering by pet type or owner
- [x] (C) Editing visits from this page (read-only)
- [x] (D) Sorting options (beyond date ASC)
- [x] (E) Export to CSV
- [ ] (F) Other (describe)

**Context**: Research suggests keeping MVP simple. Check all that should be out of scope.

---

## 6. Multi-Database Testing

Should this feature be tested across all database backends?

- [x] (A) Yes, test on H2, MySQL, and PostgreSQL (recommended based on project standards)
- [ ] (B) Test on H2 only initially
- [ ] (C) No specific multi-DB testing needed
- [ ] (D) Other (describe)

**Context**: Project uses multi-db-test-runner agent and supports H2/MySQL/PostgreSQL.

---

## 7. Test Data Strategy

How should future-dated test visits be added to the database?

- [ ] (A) Add dynamic test data using `DATEADD('DAY', N, CURRENT_DATE())` in data.sql files
- [x] (B) Add static future dates (will need updating regularly)
- [ ] (C) Only create test visits programmatically in tests
- [ ] (D) Other (describe)

**Context**: Research identified temporal coupling risk. Option A prevents tests from breaking tomorrow.

---

## Instructions

1. Answer each question by marking `[x]` for your selected option(s)
2. Add any additional notes or context below questions
3. Save this file when complete
4. Let me know you've finished answering

---

**Additional Notes or Requirements:**

[Add any additional context, requirements, or clarifications here]
