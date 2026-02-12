# Manual Testing Instructions for Task 2.0

## Overview

This document provides step-by-step instructions for manually testing the duplicate owner validation feature in a web browser.

## Prerequisites

- ✅ Application is running on http://localhost:8080
- ✅ Browser is available (Chrome, Firefox, Safari, or Edge)
- ✅ Screenshot tool is ready

## Test Procedure

### Step 1: Create a Test Owner (Baseline)

**Purpose**: Create an initial owner record that we'll later attempt to duplicate.

1. Open browser and navigate to: **http://localhost:8080/owners/new**
2. Fill in the form with the following data:
   - **First Name**: `TestDupe`
   - **Last Name**: `Owner`
   - **Address**: `123 Test St`
   - **City**: `Testville`
   - **Telephone**: `5555551234`
3. Click the **"Add Owner"** button
4. **Expected Result**:
   - Page redirects to owner details page
   - URL changes to `/owners/{id}` (note the ID number)
   - Success message displayed: "New Owner Created"
   - Owner details are visible on the page

**Verification Point**: ✅ Owner successfully created

---

### Step 2: Attempt to Create Duplicate Owner (Same Name and Telephone)

**Purpose**: Verify that duplicate detection prevents creating an owner with the same first name, last name, and telephone.

1. Navigate back to: **http://localhost:8080/owners/new**
2. Fill in the form with the SAME first name, last name, and telephone:
   - **First Name**: `TestDupe` *(same as before)*
   - **Last Name**: `Owner` *(same as before)*
   - **Address**: `456 Different St` *(different - this should NOT matter)*
   - **City**: `Otherville` *(different - this should NOT matter)*
   - **Telephone**: `5555551234` *(SAME - this is the duplicate trigger)*
3. Click the **"Add Owner"** button
4. **Expected Results**:
   - ❌ Page does NOT redirect
   - ❌ URL stays at `/owners/new`
   - ✅ Error message appears near the "First Name" field
   - ✅ Message text: **"{owner.duplicate}"** or **"An owner with this name and telephone number already exists"**
   - ✅ All form fields still contain the data you entered
   - ✅ Form is ready for you to edit and try again

**Verification Point**: ✅ Duplicate detected and error displayed

**Screenshot Required**: 📸 **TAKE SCREENSHOT NOW**
- Save as: `owner-duplicate-error-form.png`
- Location: `docs/specs/03-spec-prevent-duplicate-owner-creation/03-proofs/`
- Ensure screenshot includes:
  - The complete form with filled fields
  - The error message text
  - The URL bar showing `/owners/new`

---

### Step 3: Test Case-Insensitive Duplicate Detection

**Purpose**: Verify that duplicate detection works even when names are entered in different cases.

1. Navigate to: **http://localhost:8080/owners/new**
2. Fill in the form with lowercase names but same telephone:
   - **First Name**: `testdupe` *(lowercase version)*
   - **Last Name**: `owner` *(lowercase version)*
   - **Address**: `789 Another St`
   - **City**: `Portland`
   - **Telephone**: `5555551234` *(SAME telephone)*
3. Click the **"Add Owner"** button
4. **Expected Results**:
   - ❌ Page does NOT redirect
   - ❌ URL stays at `/owners/new`
   - ✅ Error message appears (duplicate detected despite different case)
   - ✅ Message indicates owner already exists

**Verification Point**: ✅ Case-insensitive matching confirmed

---

### Step 4: Verify Unique Owner Creation Still Works

**Purpose**: Ensure that unique owners can still be created successfully.

1. Navigate to: **http://localhost:8080/owners/new**
2. Fill in the form with DIFFERENT name and telephone:
   - **First Name**: `UniqueOwner`
   - **Last Name**: `NewPerson`
   - **Address**: `999 Unique Ave`
   - **City**: `Seattle`
   - **Telephone**: `5559998888` *(DIFFERENT telephone)*
3. Click the **"Add Owner"** button
4. **Expected Results**:
   - ✅ Page redirects to owner details page
   - ✅ URL changes to `/owners/{new-id}`
   - ✅ Success message: "New Owner Created"
   - ✅ Owner details displayed

**Verification Point**: ✅ Unique owner creation works as expected

---

## Screenshot Requirements

### Required Screenshot

**Filename**: `owner-duplicate-error-form.png`
**Location**: `docs/specs/03-spec-prevent-duplicate-owner-creation/03-proofs/`

**Must Include**:
1. ✅ The owner creation form with all fields filled
2. ✅ The error message visible near the "First Name" field
3. ✅ The URL bar showing `/owners/new` (not redirected)
4. ✅ The form data still present (not cleared)

**How to Capture**:
- **Mac**: Press `Cmd + Shift + 4`, then drag to select area
- **Windows**: Press `Windows + Shift + S`, then select area
- **Linux**: Use screenshot tool (varies by distribution)

**After Capture**:
1. Save the file with exact name: `owner-duplicate-error-form.png`
2. Move to: `docs/specs/03-spec-prevent-duplicate-owner-creation/03-proofs/`
3. Verify file is in correct location
4. Update proof document to reference screenshot

---

## Troubleshooting

### Issue: Error message shows "{owner.duplicate}" instead of readable text

**Explanation**: This is expected behavior for Task 2.0. The message key `{owner.duplicate}` will be replaced with actual text in Task 3.0 when we add internationalized messages.

**Action**: No action needed. This is correct for current task.

### Issue: Page redirects instead of showing error

**Possible Causes**:
1. Application not running latest code
2. Different owner name or telephone used
3. Database was reset

**Solution**:
1. Stop the application (Ctrl+C in terminal)
2. Run `./mvnw clean install`
3. Start again with `./mvnw spring-boot:run`
4. Try test steps again from Step 1

### Issue: Application won't start

**Check**:
1. Port 8080 is not already in use
2. Java 17+ is installed
3. Maven build was successful

**Solution**:
```bash
# Check if port 8080 is in use
lsof -i :8080

# If in use, kill the process
kill -9 <PID>

# Try starting again
./mvnw spring-boot:run
```

---

## Expected Test Results Summary

| Test Case | Input | Expected Behavior | Status |
|-----------|-------|-------------------|--------|
| Create baseline owner | TestDupe Owner, 5555551234 | ✅ Success - redirects to details | |
| Duplicate exact match | TestDupe Owner, 5555551234 | ❌ Error - stays on form | |
| Case-insensitive match | testdupe owner, 5555551234 | ❌ Error - duplicate detected | |
| Unique owner creation | UniqueOwner NewPerson, 5559998888 | ✅ Success - redirects | |

---

## Verification Checklist

After completing all test steps:

- [ ] Baseline owner created successfully
- [ ] Duplicate detection works with exact match
- [ ] Case-insensitive duplicate detection works
- [ ] Unique owner creation still works
- [ ] Screenshot captured showing error message
- [ ] Screenshot saved in correct location with correct filename
- [ ] Proof document updated with screenshot reference

---

## Next Steps

After manual testing is complete:

1. ✅ Verify screenshot is in correct location
2. ✅ Update proof document if needed
3. ✅ Commit proof document:
   ```bash
   git add docs/specs/03-spec-prevent-duplicate-owner-creation/03-proofs/
   git commit -m "docs: add Task 2.0 proof artifacts and manual testing evidence"
   ```
4. ➡️ Proceed to **Task 3.0**: Add internationalized error messages

---

**Document Version**: 1.0
**Last Updated**: 2026-02-12
**Related Task**: T2.0 - Controller-Level Duplicate Validation
**Spec**: 03-spec-prevent-duplicate-owner-creation
