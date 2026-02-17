import { test, expect } from '@fixtures/base-test';

import { OwnerPage } from '@pages/owner-page';

test.describe('Owner Search Filter Preservation', () => {
  test('preserves lastName filter when navigating to page 2', async ({ page }, testInfo) => {
    const ownerPage = new OwnerPage(page);

    // Navigate to owner search
    await ownerPage.openFindOwners();

    // Search for all owners (empty lastName) to trigger pagination
    await ownerPage.searchByLastName('');

    // Verify we're on the search results page
    await expect(page).toHaveURL(/\/owners/);
    await expect(page).toHaveURL(/lastName=/);
    await expect(ownerPage.ownersTable()).toBeVisible();

    // Capture page 1 screenshot
    await page.screenshot({ path: testInfo.outputPath('filter-preservation-page1.png'), fullPage: true });

    // Check if pagination exists (need >5 owners)
    const page2Link = page.locator('a[href*="page=2"]').first();
    const hasPagination = await page2Link.isVisible().catch(() => false);

    if (hasPagination) {
      await page2Link.click();

      // Verify URL contains both page=2 and lastName parameter
      await expect(page).toHaveURL(/\/owners\?.*page=2/);
      await expect(page).toHaveURL(/lastName=/);

      // Verify owners table is still visible
      await expect(ownerPage.ownersTable()).toBeVisible();

      // Capture page 2 screenshot
      await page.screenshot({ path: testInfo.outputPath('filter-preservation-page2.png'), fullPage: true });
    } else {
      // If no pagination, verify URL structure still contains filter
      const currentUrl = page.url();
      expect(currentUrl).toContain('lastName=');
      // Note: page parameter may not be present if only 1 page of results
    }
  });

  test('preserves specific lastName filter across pagination', async ({ page }, testInfo) => {
    const ownerPage = new OwnerPage(page);

    // Navigate to owner search
    await ownerPage.openFindOwners();

    // Search for owners with empty lastName to get multiple results
    await ownerPage.searchByLastName('');

    // Verify search results page
    await expect(ownerPage.ownersTable()).toBeVisible();

    // Verify initial URL contains lastName parameter
    const currentUrl = page.url();
    expect(currentUrl).toContain('lastName=');

    // Check if pagination controls exist
    const paginationDiv = page.locator('.liatrio-pagination');
    const hasPagination = await paginationDiv.isVisible().catch(() => false);

    if (hasPagination) {
      // Get all pagination links and verify they contain lastName
      const paginationLinks = page.locator('.liatrio-pagination a[href*="page="]');
      const linkCount = await paginationLinks.count();

      if (linkCount > 0) {
        // Check first pagination link
        const firstLink = paginationLinks.first();
        const href = await firstLink.getAttribute('href');
        expect(href).toContain('lastName=');

        await page.screenshot({ path: testInfo.outputPath('filter-pagination-links.png'), fullPage: true });
      }
    } else {
      // No pagination but filter is still in URL
      await page.screenshot({ path: testInfo.outputPath('filter-no-pagination.png'), fullPage: true });
    }
  });

  test('preserves filter after page reload', async ({ page }) => {
    const ownerPage = new OwnerPage(page);

    // Navigate directly to page 2 with lastName filter
    await page.goto('/owners?lastName=&page=2');

    // Verify owners table is visible
    await expect(ownerPage.ownersTable()).toBeVisible();

    // Reload the page
    await page.reload();

    // Verify URL still contains filter parameters
    await expect(page).toHaveURL(/lastName=/);
    await expect(page).toHaveURL(/page=2/);

    // Verify owners table is still visible after reload
    await expect(ownerPage.ownersTable()).toBeVisible();
  });

  test('preserves empty lastName filter in all pagination controls', async ({ page }, testInfo) => {
    const ownerPage = new OwnerPage(page);

    // Navigate to owner search
    await ownerPage.openFindOwners();

    // Search with empty lastName to list all owners
    await ownerPage.searchByLastName('');

    // Wait for results
    await expect(ownerPage.ownersTable()).toBeVisible();

    // Check pagination controls if they exist
    const paginationDiv = page.locator('.liatrio-pagination');
    const hasPagination = await paginationDiv.isVisible().catch(() => false);

    if (hasPagination) {
      // Verify all pagination links contain lastName parameter
      const allLinks = page.locator('.liatrio-pagination a[href*="owners"]');
      const count = await allLinks.count();

      for (let i = 0; i < count; i++) {
        const link = allLinks.nth(i);
        const href = await link.getAttribute('href');
        expect(href).toContain('lastName=');
      }

      await page.screenshot({ path: testInfo.outputPath('filter-all-pagination-controls.png'), fullPage: true });
    }
  });

  test('maintains search results consistency across pages', async ({ page }) => {
    const ownerPage = new OwnerPage(page);

    // Navigate to owner search
    await ownerPage.openFindOwners();

    // Search for all owners
    await ownerPage.searchByLastName('');

    // Wait for results on page 1
    await expect(ownerPage.ownersTable()).toBeVisible();

    // Get row count on page 1
    const page1Rows = await ownerPage.ownersTable().locator('tbody tr').count();

    // Navigate to page 2 if it exists
    const page2Link = page.locator('a[href*="page=2"]').first();
    const hasPage2 = await page2Link.isVisible().catch(() => false);

    if (hasPage2) {
      await page2Link.click();

      // Verify we're on page 2 with filter
      await expect(page).toHaveURL(/page=2/);
      await expect(page).toHaveURL(/lastName=/);

      // Verify results are displayed
      await expect(ownerPage.ownersTable()).toBeVisible();

      // Get row count on page 2
      const page2Rows = await ownerPage.ownersTable().locator('tbody tr').count();

      // Both pages should have data (consistency check)
      expect(page1Rows).toBeGreaterThan(0);
      expect(page2Rows).toBeGreaterThan(0);
    }
  });

  test('pagination navigation buttons preserve lastName filter', async ({ page }, testInfo) => {
    const ownerPage = new OwnerPage(page);

    // Navigate to owner search
    await ownerPage.openFindOwners();

    // Search for all owners
    await ownerPage.searchByLastName('');

    // Wait for results
    await expect(ownerPage.ownersTable()).toBeVisible();

    // Verify URL has filter parameters
    const currentUrl = page.url();
    expect(currentUrl).toContain('lastName=');

    // Check if pagination navigation buttons exist
    const paginationDiv = page.locator('.liatrio-pagination');
    const hasPagination = await paginationDiv.isVisible().catch(() => false);

    if (hasPagination) {
      const nextLink = page.locator('.liatrio-pagination a:has(.fa-step-forward)').first();
      const hasNextButton = await nextLink.isVisible().catch(() => false);

      if (hasNextButton) {
        const href = await nextLink.getAttribute('href');

        // Verify next button link includes lastName
        expect(href).toContain('lastName=');

        // Click next
        await nextLink.click();

        // Verify URL after navigation
        await expect(page).toHaveURL(/lastName=/);
        await expect(ownerPage.ownersTable()).toBeVisible();

        await page.screenshot({ path: testInfo.outputPath('filter-nav-buttons.png'), fullPage: true });
      } else {
        // No next button (single page), but filter is still in URL
        await page.screenshot({ path: testInfo.outputPath('filter-single-page.png'), fullPage: true });
      }
    } else {
      // No pagination controls but filter is preserved in URL
      await page.screenshot({ path: testInfo.outputPath('filter-no-pagination-controls.png'), fullPage: true });
    }
  });
});
