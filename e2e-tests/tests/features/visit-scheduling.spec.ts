import { test, expect } from '@fixtures/base-test';

import { VisitPage } from '@pages/visit-page';
import { getFutureDate, getPastDate, getTodayDate, getUniqueDescription } from '@utils/date-helpers';

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
    const visitDate = getFutureDate(7);
    const description = getUniqueDescription('E2E visit');
    await visitPage.fillVisitDate(visitDate);
    await visitPage.fillDescription(description);
    await visitPage.selectTime('10:00');
    await visitPage.selectVet('1');

    // Wait a moment for form to be ready
    await page.waitForTimeout(500);

    await page.screenshot({ path: testInfo.outputPath('visit-scheduling-form.png'), fullPage: true });

    await visitPage.submit();

    // Wait for navigation after form submission
    await page.waitForLoadState('networkidle');

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

    // Use future date to avoid validation error on date field
    const futureDate = getFutureDate(3);
    await visitPage.fillVisitDate(futureDate);
    await visitPage.submit();

    await expect(page.getByText(/must not be blank/i)).toBeVisible();
  });

  test('rejects visit with past date', async ({ page }, testInfo) => {
    const visitPage = new VisitPage(page);
    await page.goto('/owners/1');
    await expect(page.getByRole('heading', { name: /Owner Information/i })).toBeVisible();

    await page.getByRole('link', { name: /Add Visit/i }).first().click();

    // Use dynamic past date (30 days ago) to test validation
    const pastDate = getPastDate(30);
    await visitPage.fillVisitDate(pastDate);
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
    const today = getTodayDate();
    const description = getUniqueDescription('E2E visit today');
    await visitPage.fillVisitDate(today);
    await visitPage.fillDescription(description);
    await visitPage.selectTime('14:00');
    await visitPage.selectVet('2');

    // Wait a moment for form to be ready
    await page.waitForTimeout(500);

    await visitPage.submit();

    // Wait for navigation after form submission
    await page.waitForLoadState('networkidle');

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
    const futureDateStr = getFutureDate(7);
    const description = getUniqueDescription('E2E visit future');

    await visitPage.fillVisitDate(futureDateStr);
    await visitPage.fillDescription(description);
    await visitPage.selectTime('09:30');
    await visitPage.selectVet('3');

    // Wait a moment for form to be ready
    await page.waitForTimeout(500);

    await visitPage.submit();

    // Wait for navigation after form submission
    await page.waitForLoadState('networkidle');

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
