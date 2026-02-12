# 03 Questions Round 1 - Prevent Duplicate Owner Creation

Please answer each question below (select one or more options, or add your own notes). Feel free to add additional context under any question.

## 1. Duplicate Detection Rule

What combination of fields should define a "duplicate" owner?

- [x] (A) **First Name + Last Name + Telephone** (recommended by issue notes)
  - Most specific, prevents same person with same contact info
  - Matches common real-world scenario
- [ ] (B) **First Name + Last Name only**
  - Allows same person to have multiple phones
  - Simpler but less precise
- [ ] (C) **Telephone only**
  - One phone number = one owner
  - Prevents phone number reuse but allows name changes
- [ ] (D) **Email address** (requires adding email field)
  - Would need new field added to Owner entity
  - More robust unique identifier
- [ ] (E) Other (describe)

## 2. Case Sensitivity for Name Matching

Should name matching be case-sensitive?

- [x] (A) **Case-insensitive** (recommended)
  - "John Smith" = "john smith" = "JOHN SMITH"
  - More user-friendly
- [ ] (B) **Case-sensitive**
  - "John Smith" ≠ "john smith"
  - Stricter but may create duplicates from typos
- [ ] (C) Other (describe)

## 3. Whitespace Handling

How should we handle extra spaces in names?

- [x] (A) **Trim and normalize whitespace** (recommended)
  - "John  Smith" = "John Smith"
  - Prevents duplicates from formatting differences
- [ ] (B) **Exact match including spaces**
  - Stricter matching
- [ ] (C) Other (describe)

## 4. Error Message Display

Where should the duplicate error message appear?

- [x] (A) **Field-level error** under specific fields
  - Shows error next to relevant fields (first name, last name, telephone)
  - Consistent with existing validation errors
- [ ] (B) **Form-level error** at top of form
  - Single error message explaining the conflict
  - Less specific about which fields caused the issue
- [ ] (C) **Both** field-level and form-level
  - Maximum clarity but more verbose
- [ ] (D) Other (describe)

## 5. Error Message Content

What should the error message say?

- [ ] (A) **Generic**: "An owner with this information already exists"
  - Simple, doesn't reveal duplicate detection logic
- [x] (B) **Specific**: "An owner with this name and telephone number already exists"
  - Clear about what fields triggered the duplicate detection
- [ ] (C) **Actionable**: "An owner with this name and telephone number already exists. Please search for existing owners first."
  - Guides user to next action
- [ ] (D) Other (describe your preferred message)

## 6. Duplicate Detection on Edit

Should we also prevent duplicates when editing an owner?

- [ ] (A) **Yes** - check for duplicates when editing, but allow the owner to keep their own values
  - Prevents editing Owner A to match Owner B
  - More thorough validation
- [x] (B) **No** - only check on creation
  - Simpler scope, focused on issue #6 requirements
  - Can add edit validation later if needed
- [ ] (C) Other (describe)

## 7. Existing Data Migration

What about existing duplicates in the database?

- [x] (A) **Ignore** - only prevent new duplicates going forward
  - Simplest approach
  - Don't modify existing data
- [ ] (B) **Report** - add a test or script that identifies existing duplicates
  - Awareness without enforcement
- [ ] (C) **Block** - add database constraint to prevent duplicates
  - Would require cleaning existing data first
- [ ] (D) Other (describe)

## 8. Test Coverage

What proof artifacts should we create?

- [ ] (A) **E2E Test**: Playwright test that creates owner, then attempts duplicate
  - Required by issue acceptance criteria
- [ ] (B) **Controller Test**: JUnit test for duplicate detection in controller layer
  - Required by issue acceptance criteria
- [ ] (C) **Repository Test**: Test the duplicate detection query
  - Validates the database query works correctly
- [x] (D) **All of the above** (recommended)
  - Comprehensive coverage across all layers
- [ ] (E) Other (describe)

---

**Please answer the questions above by checking the boxes or adding your own notes, then save this file and let me know when you're ready for me to continue.**
