gh issue create --repo angelgarcia999/emerald-grove-pet-clinic-angel --title "Friendly 404s for missing owner/pet" --body "## Summary
Improve handling for missing owners/pets by returning a friendly 404 page (instead of a raw exception view).

## Acceptance Criteria
- [ ] Missing owner ID shows a user-friendly not-found page and returns 404.
- [ ] Missing pet for an owner is handled similarly (friendly message + 404).
- [ ] Page includes a link back to Find Owners.

## Proof / Demo
- Playwright: navigate to a non-existent owner and verify 404 + message.
- JUnit: MVC test asserts 404 status and expected view.

## Notes
- Avoid exposing stack traces or internal exception details to users.
"
gh issue create --repo angelgarcia999/emerald-grove-pet-clinic-angel --title "Export owners search results as CSV" --body "## Summary
Add a CSV export endpoint for owner search results.

## Acceptance Criteria
- [ ] A CSV endpoint exists (e.g., `/owners.csv`).
- [ ] Respects existing owner search parameters (e.g., `lastName`).
- [ ] Response has `text/csv` content type and includes a header row.

## Proof / Demo
- CLI: `curl` output snippet in proof docs.
- Playwright (optional): download CSV and verify basic content.

## Notes
- Keep columns minimal (name, address, city, telephone).
"
gh issue create --repo angelgarcia999/emerald-grove-pet-clinic-angel --title "Add an Upcoming Visits page" --body "## Summary
Add a simple read-only page to view upcoming visits for the next N days.

## Acceptance Criteria
- [ ] A new page exists at `/visits/upcoming`.
- [ ] Supports `days` query param (default 7).
- [ ] Displays owner, pet, date, and description for upcoming visits.

## Proof / Demo
- URL proof: `/visits/upcoming` renders a list.
- Playwright: create a visit within the window and verify it appears.

## Notes
- Keep initial scope read-only; no editing from this view.
"
gh issue create --repo angelgarcia999/emerald-grove-pet-clinic-angel --title "Allow deleting a pet (with confirmation)" --body "## Summary
Add the ability to delete a pet from an owner (with confirmation).

## Acceptance Criteria
- [ ] A delete action is available for a pet on the owner details page.
- [ ] Deleting requires a confirmation step.
- [ ] After deletion, the pet no longer appears on the owner details page.

## Proof / Demo
- Playwright: create a pet, delete it, verify it is removed from the UI.
- Screenshot: confirmation UI.

## Notes
- Keep safety behavior explicit (e.g., block deletion if visits exist, or require extra confirmation).
"
gh issue create --repo angelgarcia999/emerald-grove-pet-clinic-angel --title "Disallow scheduling visits in the past" --body "## Summary
Add validation to prevent scheduling visits in the past.

## Acceptance Criteria
- [ ] Visit form rejects dates earlier than today.
- [ ] A clear validation message is displayed when a past date is submitted.
- [ ] Visits for today and future dates still work.

## Proof / Demo
- Playwright: attempt a past date and assert validation error.
- JUnit: validation test for the visit date rule.

## Notes
- Keep the rule simple: date must be today or later.
"
gh issue create --repo angelgarcia999/emerald-grove-pet-clinic-angel --title "Preserve filters across pagination" --body "## Summary
When paging through Owners and/or Vets lists, preserve current search/filter parameters across pagination links.

## Acceptance Criteria
- [ ] Pagination links include the current query/filter parameters.
- [ ] Navigating to next/previous pages does not reset the current filter.
- [ ] The UI continues to reflect the active filter while paging.

## Proof / Demo
- Playwright: apply a filter, page forward/back, verify results stay filtered.
- Screenshot: pagination URLs include expected query parameters.

## Notes
- Keep scope limited to the existing list pages.
"
gh issue create --repo angelgarcia999/emerald-grove-pet-clinic-angel --title "Prevent duplicate owner creation" --body "## Summary
Prevent creating duplicate owners by adding a simple duplicate detection rule during owner creation.

## Acceptance Criteria
- [ ] Attempting to create a duplicate owner is blocked.
- [ ] The UI shows a clear, actionable error message.
- [ ] The duplicate attempt does not create a second owner record.

## Proof / Demo
- Playwright: create an owner, attempt to create the same owner again, assert error.
- JUnit: controller/service test covering duplicate detection path.

## Notes
- Define “duplicate” using a small, explicit rule (e.g., same first/last/telephone).
"
gh issue create --repo angelgarcia999/emerald-grove-pet-clinic-angel --title "Find Owners: search by telephone and city" --body "## Summary
Extend Find Owners to support optional searching/filtering by telephone and city in addition to last name.

## Acceptance Criteria
- [ ] Find Owners form includes optional inputs for telephone and city.
- [ ] Submitting the form filters results using the provided criteria.
- [ ] Invalid telephone input is rejected with a clear validation message.

## Proof / Demo
- Screenshot: updated Find Owners form.
- Playwright: create an owner, then find them using telephone and/or city.

## Notes
- Keep the default behavior (last name search) intact.
"
gh issue create --repo angelgarcia999/emerald-grove-pet-clinic-angel --title "Filter veterinarians by specialty" --body "## Summary
Add specialty filtering to the Vet Directory so users can narrow the veterinarian list by specialty.

## Acceptance Criteria
- [ ] Vet Directory includes a specialty filter control.
- [ ] Selecting a specialty shows only matching vets.
- [ ] An “All” option shows all vets; “None” is handled sensibly.

## Proof / Demo
- Screenshot: filtered vet list displayed.
- Playwright: E2E test that applies a filter and verifies results.

## Notes
- Prefer query param support so filtered URLs can be shared.
"
gh issue create --repo angelgarcia999/emerald-grove-pet-clinic-angel --title "Add language selector to header" --body "## Summary
Add a language selector in the header so users can switch the UI language using the existing locale support (via `?lang=xx`).

## Acceptance Criteria
- [ ] A language selector is visible in the global header on all pages.
- [ ] Selecting a language updates visible UI text (e.g., page headings/nav labels).
- [ ] The selected language persists across navigation in the same session.

## Proof / Demo
- Screenshot(s): same page shown in two different languages.
- Playwright: an E2E test that switches language and asserts translated text.

## Notes
- Keep initial language list small (e.g., EN/ES/DE).
"
