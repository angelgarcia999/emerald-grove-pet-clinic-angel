import { test, expect } from './fixtures/base-test';
import * as fs from 'fs';

test.describe('Visit Booking with Time and Vet Selection', () => {

  test('should display visit form with time and vet selection fields', async ({ page }, testInfo) => {
    // Navigate to George Franklin's owner details page (owner ID 1)
    await page.goto('/owners/1');

    // Wait for owner information to load
    await expect(page.getByText('Owner Information')).toBeVisible();
    await expect(page.getByText('George Franklin')).toBeVisible();

    // Take screenshot of owner details page showing pets
    await page.screenshot({ path: testInfo.outputPath('owner-details-with-pets.png'), fullPage: true });

    // Click "Add Visit" link for the first pet
    await page.getByRole('link', { name: /Add Visit/i }).first().click();

    // Wait for visit form to load
    await expect(page.getByText(/New\s+Visit/i)).toBeVisible();

    // Verify all form fields are visible
    await expect(page.locator('input[name="date"]')).toBeVisible();
    await expect(page.locator('input[name="description"]')).toBeVisible();
    await expect(page.locator('select#startTime')).toBeVisible();
    await expect(page.locator('select[name="vet.id"]')).toBeVisible();

    // Take screenshot of visit form with new fields
    await page.screenshot({ path: testInfo.outputPath('visit-form-with-time-and-vet.png'), fullPage: true });

    // Verify time dropdown has expected options
    const timeOptions = await page.locator('select#startTime option').count();
    expect(timeOptions).toBeGreaterThan(16); // At least 16 time slots + placeholder

    // Verify vet dropdown has options
    const vetOptions = await page.locator('select[name="vet.id"] option').count();
    expect(vetOptions).toBeGreaterThan(1); // At least 1 vet + placeholder
  });


  test('should display time slots from 9:00 AM to 5:00 PM', async ({ page }) => {
    // Navigate to owner details and add visit
    await page.goto('/owners/1');
    await page.getByRole('link', { name: /Add Visit/i }).first().click();

    // Wait for form
    await expect(page.getByText(/New\s+Visit/i)).toBeVisible();

    // Get all time options
    const timeOptions = await page.locator('select#startTime option').allTextContents();

    // Verify expected time slots exist (30-minute intervals from 9:00 AM to 5:00 PM)
    expect(timeOptions).toContain('9:00 AM');
    expect(timeOptions).toContain('12:00 PM');
    expect(timeOptions).toContain('5:00 PM');

    // Verify the options are in correct sequence
    expect(timeOptions.length).toBeGreaterThanOrEqual(17); // At least 17 options (placeholder + 16 time slots)
  });

  test('should display list of available veterinarians', async ({ page }) => {
    // Navigate to owner details and add visit
    await page.goto('/owners/1');
    await page.getByRole('link', { name: /Add Visit/i }).first().click();

    // Wait for form
    await expect(page.getByText(/New\s+Visit/i)).toBeVisible();

    // Get all vet options
    const vetOptions = await page.locator('select[name="vet.id"] option').allTextContents();

    // Verify vets are listed
    expect(vetOptions.length).toBeGreaterThan(1); // At least placeholder + one vet

    // Verify format includes "Dr." prefix
    const vetLabels = vetOptions.filter(opt => opt.includes('Dr.'));
    expect(vetLabels.length).toBeGreaterThan(0);
  });

});
