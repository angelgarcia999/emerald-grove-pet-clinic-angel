# 02 Questions Round 1 - Past Visit Validation

Please answer each question below (select one or more options, or add your own notes). Feel free to add additional context under any question.

## 1. Date Validation Boundary

When we say "dates earlier than today", what should happen for a visit scheduled for TODAY?

- [x] (A) TODAY is **allowed** - validation rule is `date >= today`
- [ ] (B) TODAY is **NOT allowed** - validation rule is `date > today` (only future dates)
- [ ] (C) Other (describe)

**Context:** The issue says "date must be today or later", which suggests (A), but I want to confirm.

---

## 2. Validation Message

What message should be shown to users when they try to schedule a past visit?

- [x] (A) Simple: "Visit date cannot be in the past"
- [ ] (B) Helpful: "Visit date must be today or later"
- [ ] (C) Detailed: "Visit date must be today (YYYY-MM-DD) or later. Please select a valid date."
- [ ] (D) Other (describe your preferred message)

---

## 3. Where Should Validation Occur?

Where should this validation be implemented?

- [x] (A) Bean validation annotation on Visit entity (e.g., custom @FutureOrPresent annotation)
- [ ] (B) Custom validator class (like PetValidator) registered in VisitController
- [ ] (C) Both entity-level and controller-level for defense in depth
- [ ] (D) Other (describe)

**Note:** Option (C) provides the strongest guarantee but is more code to test.

---

## 4. Edge Cases and Error Handling

Are there any edge cases to consider?

- [ ] (A) What happens if a user somehow submits no date at all? (Should this be a separate validation?)
- [ ] (B) What about timezone considerations? (e.g., user in different timezone than server)
- [ ] (C) Should existing past visits in the database remain valid, or should this only apply to new visits?
- [ ] (D) All of the above
- [x] (E) None - keep it simple, date >= today is sufficient
- [ ] (F) Other (describe)

---

## 5. Proof Artifacts

The issue specifies Playwright and JUnit tests. Are these sufficient, or should we include additional proof?

- [x] (A) Playwright + JUnit tests as specified in the issue are sufficient
- [ ] (B) Add screenshot showing validation error message in UI
- [ ] (C) Add manual test case documentation
- [ ] (D) Add integration test across all database profiles (H2, MySQL, PostgreSQL)
- [ ] (E) Combination (which ones?)
- [ ] (F) Other (describe)

---

## 6. Internationalization (i18n)

Should the validation message support multiple languages?

- [ ] (A) Yes - add message to messages.properties and messages_XX.properties files
- [x] (B) No - English only for now
- [ ] (C) Other (describe)

**Context:** The codebase uses i18n (messages.properties), so this would follow existing patterns.

---

## Additional Notes

Any other requirements, concerns, or context to consider?

