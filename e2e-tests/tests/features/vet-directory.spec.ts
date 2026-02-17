import { test, expect } from '@fixtures/base-test';

import { VetPage } from '@pages/vet-page';

test.describe('Vet Directory', () => {
  test('can browse veterinarian list and view specialties', async ({ page }, testInfo) => {
    const vetPage = new VetPage(page);

    await vetPage.open();

    await expect(vetPage.vetsTable()).toBeVisible();

    // This test relies on Petclinic's startup seed data providing vets.
    const rows = vetPage.vetsTable().locator('tbody tr');
    const rowCount = await rows.count();
    expect(rowCount, 'Expected seeded veterinarians to be present').toBeGreaterThan(0);

    await page.screenshot({ path: testInfo.outputPath('vet-directory.png'), fullPage: true });

    // Validate each row's specialty cell contains a known specialty or "none".
    for (let i = 0; i < rowCount; i++) {
      const specialtyCell = rows.nth(i).locator('td').nth(1);
      await expect(specialtyCell).toContainText(/none|surgery|dentistry|radiology|medicine/i);
    }
  });

  test('specialty filter dropdown exists and displays all specialties', async ({ page }, testInfo) => {
    const vetPage = new VetPage(page);

    await vetPage.open();

    // Verify specialty filter is visible
    await expect(vetPage.specialtyFilter()).toBeVisible();
    await expect(vetPage.specialtyFilterLabel()).toContainText(/Filter by Specialty/i);

    // Take screenshot of filter
    await page.screenshot({ path: testInfo.outputPath('specialty-filter-dropdown.png'), fullPage: true });

    // Verify "All Specialties" option exists (check option count, not visibility)
    const specialtyOptions = vetPage.specialtyFilter().locator('option');
    const optionCount = await specialtyOptions.count();
    expect(optionCount, 'Expected specialty options to be present').toBeGreaterThan(1);

    // Verify "All Specialties" option exists by checking for empty value option
    const allSpecialtiesOption = vetPage.specialtyFilter().locator('option[value=""]');
    expect(await allSpecialtiesOption.count(), 'Expected "All Specialties" option').toBe(1);
  });

  test('can filter vets by radiology specialty', async ({ page }, testInfo) => {
    const vetPage = new VetPage(page);

    await vetPage.open();

    // Get initial row count
    const initialRows = vetPage.vetsTable().locator('tbody tr');
    const initialCount = await initialRows.count();

    // Select radiology specialty
    await vetPage.selectSpecialty('radiology');

    // Verify URL contains specialty parameter
    await expect(page).toHaveURL(/specialty=radiology/);

    // Take screenshot of filtered results
    await page.screenshot({ path: testInfo.outputPath('vet-directory-radiology-filter.png'), fullPage: true });

    // Verify filtered results
    const filteredRows = vetPage.vetsTable().locator('tbody tr');
    const filteredCount = await filteredRows.count();

    // Radiology should filter results (fewer than total)
    expect(filteredCount, 'Filtered results should be less than or equal to total').toBeLessThanOrEqual(initialCount);
    expect(filteredCount, 'Should have at least one vet with radiology specialty').toBeGreaterThan(0);

    // Verify all displayed vets have radiology specialty
    for (let i = 0; i < filteredCount; i++) {
      const specialtyCell = filteredRows.nth(i).locator('td').nth(1);
      await expect(specialtyCell).toContainText(/radiology/i);
    }

    // Verify dropdown shows selected specialty
    await expect(vetPage.specialtyFilter()).toHaveValue('radiology');
  });

  test('can filter vets by surgery specialty', async ({ page }, testInfo) => {
    const vetPage = new VetPage(page);

    await vetPage.open();

    // Select surgery specialty
    await vetPage.selectSpecialty('surgery');

    // Verify URL contains specialty parameter
    await expect(page).toHaveURL(/specialty=surgery/);

    // Take screenshot
    await page.screenshot({ path: testInfo.outputPath('vet-directory-surgery-filter.png'), fullPage: true });

    // Verify filtered results
    const filteredRows = vetPage.vetsTable().locator('tbody tr');
    const filteredCount = await filteredRows.count();
    expect(filteredCount, 'Should have at least one vet with surgery specialty').toBeGreaterThan(0);

    // Verify all displayed vets have surgery specialty
    for (let i = 0; i < filteredCount; i++) {
      const specialtyCell = filteredRows.nth(i).locator('td').nth(1);
      await expect(specialtyCell).toContainText(/surgery/i);
    }

    // Verify dropdown shows selected specialty
    await expect(vetPage.specialtyFilter()).toHaveValue('surgery');
  });

  test('specialty filter persists across pagination', async ({ page }, testInfo) => {
    const vetPage = new VetPage(page);

    // Open with radiology filter
    await vetPage.openWithSpecialty('radiology');

    // Verify filter is applied
    await expect(page).toHaveURL(/specialty=radiology/);
    await expect(vetPage.specialtyFilter()).toHaveValue('radiology');

    // Take screenshot of page 1 with filter
    await page.screenshot({ path: testInfo.outputPath('specialty-filter-page1.png'), fullPage: true });

    // Check if pagination exists (there might not be enough radiology vets for multiple pages)
    const paginationExists = await page.locator('.liatrio-pagination').isVisible();

    if (paginationExists) {
      // Click to page 2 if it exists
      const page2Link = vetPage.paginationLink(2);
      const page2Exists = await page2Link.isVisible();

      if (page2Exists) {
        await page2Link.click();
        await page.waitForLoadState('networkidle');

        // Verify specialty filter persists in URL
        await expect(page).toHaveURL(/specialty=radiology/);
        await expect(page).toHaveURL(/page=2/);

        // Verify dropdown still shows selected specialty
        await expect(vetPage.specialtyFilter()).toHaveValue('radiology');

        // Take screenshot of page 2 with filter
        await page.screenshot({ path: testInfo.outputPath('specialty-filter-page2.png'), fullPage: true });

        // Verify filtered results on page 2
        const filteredRows = vetPage.vetsTable().locator('tbody tr');
        const filteredCount = await filteredRows.count();

        if (filteredCount > 0) {
          for (let i = 0; i < filteredCount; i++) {
            const specialtyCell = filteredRows.nth(i).locator('td').nth(1);
            await expect(specialtyCell).toContainText(/radiology/i);
          }
        }
      }
    }
  });

  test('can reset filter to show all specialties', async ({ page }, testInfo) => {
    const vetPage = new VetPage(page);

    // Start with radiology filter
    await vetPage.openWithSpecialty('radiology');

    // Verify filter is applied
    await expect(vetPage.specialtyFilter()).toHaveValue('radiology');

    const filteredRows = vetPage.vetsTable().locator('tbody tr');
    const filteredCount = await filteredRows.count();

    // Select "All Specialties"
    await vetPage.selectAllSpecialties();

    // Verify URL no longer has specialty parameter (or it's empty)
    await expect(page).not.toHaveURL(/specialty=radiology/);

    // Take screenshot of all vets
    await page.screenshot({ path: testInfo.outputPath('vet-directory-all-specialties.png'), fullPage: true });

    // Verify more vets are shown (or same if all had radiology)
    const allRows = vetPage.vetsTable().locator('tbody tr');
    const allCount = await allRows.count();
    expect(allCount, 'All specialties should show equal or more vets').toBeGreaterThanOrEqual(filteredCount);

    // Verify dropdown shows "All Specialties" selected
    await expect(vetPage.specialtyFilter()).toHaveValue('');
  });

  test('direct URL navigation with specialty parameter works', async ({ page }, testInfo) => {
    const vetPage = new VetPage(page);

    // Navigate directly with specialty parameter
    await vetPage.openWithSpecialty('dentistry');

    // Verify page loads correctly
    await expect(vetPage.vetsTable()).toBeVisible();

    // Verify filter is applied from URL
    await expect(vetPage.specialtyFilter()).toHaveValue('dentistry');

    // Take screenshot
    await page.screenshot({ path: testInfo.outputPath('vet-directory-dentistry-direct.png'), fullPage: true });

    // Verify filtered results
    const filteredRows = vetPage.vetsTable().locator('tbody tr');
    const filteredCount = await filteredRows.count();

    if (filteredCount > 0) {
      // Verify all displayed vets have dentistry specialty
      for (let i = 0; i < filteredCount; i++) {
        const specialtyCell = filteredRows.nth(i).locator('td').nth(1);
        await expect(specialtyCell).toContainText(/dentistry/i);
      }
    }
  });
});
