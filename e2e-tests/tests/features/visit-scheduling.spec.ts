import { test, expect } from '@fixtures/base-test';

import { VisitPage } from '@pages/visit-page';

test.describe('Visit Scheduling', () => {
  test('can schedule a visit for an existing pet', async ({ page }, testInfo) => {
    const visitPage = new VisitPage(page);
    // Note: searching by last name may redirect directly to owner details when there is a single match.
    // Use a stable direct URL to avoid depending on the owners list table.
    await page.goto('/owners/1');
    await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();

    const addVisitLink = page.getByRole('link', { name: /^Add Visit$/i }).first();
    const addVisitHref = await addVisitLink.getAttribute('href');
    if (!addVisitHref) {
      throw new Error('Expected Add Visit link to have an href');
    }

    const petIdMatch = addVisitHref.match(/pets\/(\d+)\//);
    if (!petIdMatch) {
      throw new Error(`Expected Add Visit href to include pet id, got: ${addVisitHref}`);
    }

    const petId = petIdMatch[1];

    await addVisitLink.click();

    await expect(visitPage.heading()).toBeVisible();

    // Use future date to pass validation
    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 7);
    const visitDate = futureDate.toISOString().split('T')[0];
    const description = `E2E visit ${Date.now()}`;
    await visitPage.fillVisitDate(visitDate);
    await visitPage.fillDescription(description);
    await visitPage.selectTime('10:00');
    await visitPage.selectVet('1');

    await page.screenshot({ path: testInfo.outputPath('visit-scheduling-form.png'), fullPage: true });

    await visitPage.submit();

    await expect(page.getByRole('heading', { name: /Pets and Visits/i })).toBeVisible();

    const petVisitsTable = page
      .locator(`a[href*="pets/${petId}/visits/new"]`)
      .first()
      .locator('xpath=ancestor::table[1]');

    const visitRow = petVisitsTable.locator('tr').filter({ hasText: visitDate }).filter({ hasText: description });
    await expect(visitRow).toHaveCount(1);
  });

  test('validates visit description is required', async ({ page }) => {
    const visitPage = new VisitPage(page);
    await page.goto('/owners/1');
    await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();

    await page.getByRole('link', { name: /Add Visit/i }).first().click();

    await visitPage.fillVisitDate('2024-03-03');
    await visitPage.submit();

    await expect(page.getByText(/must not be blank/i)).toBeVisible();
  });

  test('rejects visit with past date', async ({ page }, testInfo) => {
    const visitPage = new VisitPage(page);
    await page.goto('/owners/1');
    await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();

    await page.getByRole('link', { name: /Add Visit/i }).first().click();

    // Fill form with past date
    await visitPage.fillVisitDate('2020-01-01');
    await visitPage.fillDescription('Past visit attempt');

    // Capture screenshot before submission
    await page.screenshot({ path: testInfo.outputPath('visit-past-date-before-submit.png'), fullPage: true });

    await visitPage.submit();

    // Verify form returns with error (not redirected)
    await expect(visitPage.heading()).toBeVisible();

    // Verify error message is displayed
    await expect(page.getByText(/Visit date cannot be in the past/i)).toBeVisible();

    // Capture screenshot showing error
    await page.screenshot({ path: testInfo.outputPath('visit-past-date-error.png'), fullPage: true });
  });

  test('accepts visit with today date', async ({ page }) => {
    const visitPage = new VisitPage(page);
    await page.goto('/owners/1');
    await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();

    const addVisitLink = page.getByRole('link', { name: /^Add Visit$/i }).first();
    const addVisitHref = await addVisitLink.getAttribute('href');
    if (!addVisitHref) {
      throw new Error('Expected Add Visit link to have an href');
    }

    const petIdMatch = addVisitHref.match(/pets\/(\d+)\//);
    if (!petIdMatch) {
      throw new Error(`Expected Add Visit href to include pet id, got: ${addVisitHref}`);
    }

    const petId = petIdMatch[1];

    await addVisitLink.click();
    await expect(visitPage.heading()).toBeVisible();

    // Use today's date
    const today = new Date().toISOString().split('T')[0];
    const description = `E2E visit today ${Date.now()}`;
    await visitPage.fillVisitDate(today);
    await visitPage.fillDescription(description);
    await visitPage.selectTime('14:00');
    await visitPage.selectVet('2');

    await visitPage.submit();

    // Verify successful redirect to owner details
    await expect(page.getByRole('heading', { name: /Pets and Visits/i })).toBeVisible();

    // Verify visit appears in the table
    const petVisitsTable = page
      .locator(`a[href*="pets/${petId}/visits/new"]`)
      .first()
      .locator('xpath=ancestor::table[1]');

    const visitRow = petVisitsTable.locator('tr').filter({ hasText: today }).filter({ hasText: description });
    await expect(visitRow).toHaveCount(1);
  });

  test('accepts visit with future date', async ({ page }) => {
    const visitPage = new VisitPage(page);
    await page.goto('/owners/1');
    await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();

    const addVisitLink = page.getByRole('link', { name: /^Add Visit$/i }).first();
    const addVisitHref = await addVisitLink.getAttribute('href');
    if (!addVisitHref) {
      throw new Error('Expected Add Visit link to have an href');
    }

    const petIdMatch = addVisitHref.match(/pets\/(\d+)\//);
    if (!petIdMatch) {
      throw new Error(`Expected Add Visit href to include pet id, got: ${addVisitHref}`);
    }

    const petId = petIdMatch[1];

    await addVisitLink.click();
    await expect(visitPage.heading()).toBeVisible();

    // Use future date (7 days from now)
    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 7);
    const futureDateStr = futureDate.toISOString().split('T')[0];
    const description = `E2E visit future ${Date.now()}`;

    await visitPage.fillVisitDate(futureDateStr);
    await visitPage.fillDescription(description);
    await visitPage.selectTime('09:30');
    await visitPage.selectVet('3');

    await visitPage.submit();

    // Verify successful redirect to owner details
    await expect(page.getByRole('heading', { name: /Pets and Visits/i })).toBeVisible();

    // Verify visit appears in the table
    const petVisitsTable = page
      .locator(`a[href*="pets/${petId}/visits/new"]`)
      .first()
      .locator('xpath=ancestor::table[1]');

    const visitRow = petVisitsTable.locator('tr').filter({ hasText: futureDateStr }).filter({ hasText: description });
    await expect(visitRow).toHaveCount(1);
  });
});
