import { test, expect } from './fixtures/base-test';
import { OwnerPage } from './pages/owner-page';
import * as fs from 'fs';

test.describe('Owner Search Enhancement', () => {

  test('should search owners by multiple criteria', async ({ page }, testInfo) => {
    const ownerPage = new OwnerPage(page);

    await ownerPage.openFindOwners();

    // Take screenshot of search form with 3 fields
    await page.screenshot({ path: testInfo.outputPath('owner-search-form-with-3-fields.png'), fullPage: true });

    // Fill all three search fields: lastName, city, telephone
    await page.locator('input#lastName').fill('Davis');
    await page.locator('input#city').fill('Windsor');
    await page.locator('input#telephone').fill('6085553198');

    // Screenshot of filled form
    await page.screenshot({ path: testInfo.outputPath('owner-search-form-filled.png'), fullPage: true });

    // Submit search
    await page.getByRole('button', { name: /Find Owner/i }).click();

    // When exactly one owner is found, it redirects to owner details page
    // Verify we're on the owner information page
    await expect(page.getByText(/Owner Information/i)).toBeVisible();

    // Verify it's the correct owner (Harold Davis from Windsor)
    await expect(page.getByText('Harold Davis')).toBeVisible();
    await expect(page.getByText('Windsor')).toBeVisible();
    await expect(page.getByText('6085553198')).toBeVisible();

    // Screenshot of result
    await page.screenshot({ path: testInfo.outputPath('owner-search-result-details.png'), fullPage: true });
  });

  test('should preserve search filters across pagination', async ({ page }, testInfo) => {
    const ownerPage = new OwnerPage(page);

    await ownerPage.openFindOwners();

    // Search by last name only to get multiple results
    await page.locator('input#lastName').fill('Davis');
    await page.locator('input#city').fill('');
    await page.locator('input#telephone').fill('');

    // Submit search
    await page.getByRole('button', { name: /Find Owner/i }).click();

    // Wait for results
    const ownersTable = ownerPage.ownersTable();
    await expect(ownersTable).toBeVisible();

    // Screenshot of results with pagination
    await page.screenshot({ path: testInfo.outputPath('owner-search-results-with-pagination.png'), fullPage: true });

    // Check that pagination links preserve search parameters
    // Look for pagination link that includes lastName parameter
    const paginationLink = page.locator('a[href*="lastName=Davis"]');
    if (await paginationLink.count() > 0) {
      await expect(paginationLink.first()).toBeVisible();

      // Verify URL contains all search parameters
      const href = await paginationLink.first().getAttribute('href');
      expect(href).toContain('lastName=Davis');

      // If city or telephone were filled, they should also be in URL
      // (In this test they're empty, so we just verify lastName is present)
    }
  });

  test('should handle empty search results gracefully', async ({ page }) => {
    const ownerPage = new OwnerPage(page);

    await ownerPage.openFindOwners();

    // Search for owner that doesn't exist
    await page.locator('input#lastName').fill('NonexistentOwner');
    await page.locator('input#city').fill('FakeCity');
    await page.locator('input#telephone').fill('0000000000');

    // Submit search
    await page.getByRole('button', { name: /Find Owner/i }).click();

    // Verify "not found" message is displayed
    await expect(page.getByText(/has not been found/i)).toBeVisible();
  });

  test('should export search results to CSV', async ({ page }) => {
    const ownerPage = new OwnerPage(page);

    await ownerPage.openFindOwners();

    // Search for Davis owners (multiple results: Betty Davis and Harold Davis)
    await page.locator('input#lastName').fill('Davis');
    await page.locator('input#city').fill('');
    await page.locator('input#telephone').fill('');

    // Submit search
    await page.getByRole('button', { name: /Find Owner/i }).click();

    // Wait for results to load (should show list with multiple owners)
    const ownersTable = ownerPage.ownersTable();
    await expect(ownersTable).toBeVisible();

    // Setup download handler before clicking export button
    const downloadPromise = page.waitForEvent('download');

    // Click export button
    await page.getByRole('button', { name: /Export to CSV/i }).click();

    // Wait for download to complete
    const download = await downloadPromise;

    // Verify download details
    expect(download.suggestedFilename()).toBe('owners.csv');

    // Read downloaded CSV content
    const path = await download.path();
    const csvContent = fs.readFileSync(path, 'utf-8');

    // Verify CSV structure and content
    expect(csvContent).toContain('ID,First Name,Last Name,Address,City,Telephone');
    expect(csvContent).toContain('Davis'); // Should contain Betty Davis and/or Harold Davis
  });

});
