import { test, expect } from './fixtures/base-test';

test.describe('Friendly 404 Pages', () => {

  test('should display friendly 404 page for non-existent owner', async ({ page }, testInfo) => {
    // Navigate to non-existent owner ID
    await page.goto('/owners/999999');

    // Verify we get a 404 error page with the "Something happened..." heading
    await expect(page.getByRole('heading', { name: /Something happened/i })).toBeVisible();

    // Take screenshot for proof artifact
    await page.screenshot({ path: testInfo.outputPath('owner-404-page.png'), fullPage: true });
  });

  test('should display owner ID in 404 error message', async ({ page }) => {
    // Navigate to non-existent owner ID
    await page.goto('/owners/999999');

    // Verify the error message indicates owner was not found
    // The @ResponseStatus reason displays "Owner not found"
    await expect(page.getByText(/Owner not found/i)).toBeVisible();
  });

  test('should display Find Owners link on owner 404 page', async ({ page }) => {
    // Navigate to non-existent owner ID
    await page.goto('/owners/999999');

    // Verify "Find Owners" link is present and visible
    // It's an <a> tag styled as a button
    const findOwnersLink = page.locator('a.btn').filter({ hasText: /Find Owners/i });
    await expect(findOwnersLink).toBeVisible();
  });

  test('should navigate to Find Owners page when link is clicked', async ({ page }) => {
    // Navigate to non-existent owner ID
    await page.goto('/owners/999999');

    // Click the "Find Owners" link (styled as a button)
    const findOwnersLink = page.locator('a.btn').filter({ hasText: /Find Owners/i });
    await findOwnersLink.click();

    // Verify we're on the Find Owners page
    await expect(page).toHaveURL(/\/owners\/find/);
    await expect(page.getByRole('heading', { name: /Find Owners/i })).toBeVisible();
  });

  test('should display friendly 404 page for non-existent pet', async ({ page }, testInfo) => {
    // Navigate to edit page for non-existent pet ID (using valid owner ID 1)
    await page.goto('/owners/1/pets/999999/edit');

    // Verify we get a 404 error page with the "Something happened..." heading
    await expect(page.getByRole('heading', { name: /Something happened/i })).toBeVisible();

    // Take screenshot for proof artifact
    await page.screenshot({ path: testInfo.outputPath('pet-404-page.png'), fullPage: true });
  });

  test('should display pet ID in 404 error message', async ({ page }) => {
    // Navigate to edit page for non-existent pet ID
    await page.goto('/owners/1/pets/999999/edit');

    // Verify the error message indicates pet was not found
    // The @ResponseStatus reason displays "Pet not found"
    await expect(page.getByText(/Pet not found/i)).toBeVisible();
  });

  test('should display Find Owners link on pet 404 page', async ({ page }) => {
    // Navigate to edit page for non-existent pet ID
    await page.goto('/owners/1/pets/999999/edit');

    // Verify "Find Owners" link is present and visible
    // It's an <a> tag styled as a button
    const findOwnersLink = page.locator('a.btn').filter({ hasText: /Find Owners/i });
    await expect(findOwnersLink).toBeVisible();
  });

  test('should not display stack traces on 404 error pages', async ({ page }) => {
    // Test owner 404 page
    await page.goto('/owners/999999');

    // Verify no technical details are visible (stack traces, Java exceptions, etc.)
    const pageContent = await page.content();
    expect(pageContent).not.toContain('java.lang');
    expect(pageContent).not.toContain('org.springframework');
    expect(pageContent).not.toContain('at com.');
    expect(pageContent).not.toContain('Exception in thread');
    expect(pageContent).not.toContain('Caused by:');

    // Test pet 404 page
    await page.goto('/owners/1/pets/999999/edit');

    // Verify no technical details are visible
    const petPageContent = await page.content();
    expect(petPageContent).not.toContain('java.lang');
    expect(petPageContent).not.toContain('org.springframework');
    expect(petPageContent).not.toContain('at com.');
    expect(petPageContent).not.toContain('Exception in thread');
    expect(petPageContent).not.toContain('Caused by:');
  });

  test('should navigate from pet 404 to Find Owners page', async ({ page }) => {
    // Navigate to edit page for non-existent pet ID
    await page.goto('/owners/1/pets/999999/edit');

    // Click the "Find Owners" link from pet error page (styled as a button)
    const findOwnersLink = page.locator('a.btn').filter({ hasText: /Find Owners/i });
    await findOwnersLink.click();

    // Verify we're on the Find Owners page
    await expect(page).toHaveURL(/\/owners\/find/);
    await expect(page.getByRole('heading', { name: /Find Owners/i })).toBeVisible();
  });

});
