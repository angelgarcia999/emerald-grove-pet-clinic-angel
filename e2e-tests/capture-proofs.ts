import { chromium } from '@playwright/test';
import * as path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

async function captureProofArtifacts() {
  const browser = await chromium.launch();
  const context = await browser.newContext();
  const page = await context.newPage();

  const baseUrl = 'http://localhost:8080';
  const outputDir = path.join(__dirname, '..', 'docs', 'specs', '10-spec-visit-booking-ui', '10-proofs');

  try {
    // Screenshot 1: Desktop view - complete two-column layout
    await page.goto(`${baseUrl}/owners/1/pets/1/visits/new`);
    await page.waitForLoadState('networkidle');
    await page.screenshot({
      path: path.join(outputDir, '01-desktop-two-column-layout.png'),
      fullPage: true
    });
    console.log('✓ Captured: Desktop two-column layout');

    // Screenshot 2: Mobile view - stacked columns
    await page.setViewportSize({ width: 375, height: 667 });
    await page.screenshot({
      path: path.join(outputDir, '02-mobile-stacked-columns.png'),
      fullPage: true
    });
    console.log('✓ Captured: Mobile stacked columns');

    // Reset to desktop size
    await page.setViewportSize({ width: 1280, height: 720 });

    // Screenshot 3: Pet Summary Card closeup
    const petSummaryCard = page.locator('.card').filter({ hasText: 'Pet Summary' });
    await petSummaryCard.screenshot({
      path: path.join(outputDir, '03-pet-summary-card.png')
    });
    console.log('✓ Captured: Pet Summary Card closeup');

    // Screenshot 4: Quick Info Card closeup
    const quickInfoCard = page.locator('.card').filter({ hasText: 'Quick Info' });
    await quickInfoCard.screenshot({
      path: path.join(outputDir, '04-quick-info-card.png')
    });
    console.log('✓ Captured: Quick Info Card closeup');

    // Screenshot 5: Appointment form with all fields filled
    await page.locator('#date').fill('2026-03-15');
    await page.locator('#startTime').selectOption('10:00');
    await page.locator('#vet\\.id').selectOption('1');
    await page.locator('#description').fill('Regular checkup and vaccinations');
    await page.screenshot({
      path: path.join(outputDir, '05-form-filled.png'),
      fullPage: true
    });
    console.log('✓ Captured: Form with all fields filled');

    // Screenshot 6: Time slot dropdown expanded
    await page.locator('#startTime').click();
    await page.screenshot({
      path: path.join(outputDir, '06-time-dropdown-expanded.png'),
      fullPage: true
    });
    console.log('✓ Captured: Time slot dropdown expanded');

    // Screenshot 7: Vet selector expanded showing specialties
    await page.locator('#vet\\.id').click();
    await page.screenshot({
      path: path.join(outputDir, '07-vet-selector-expanded.png'),
      fullPage: true
    });
    console.log('✓ Captured: Vet selector expanded');

    // Screenshot 8: Form with validation errors
    await page.goto(`${baseUrl}/owners/1/pets/1/visits/new`);
    await page.waitForLoadState('networkidle');
    await page.locator('button[type="submit"]').click();
    await page.waitForTimeout(500);
    await page.screenshot({
      path: path.join(outputDir, '08-validation-errors.png'),
      fullPage: true
    });
    console.log('✓ Captured: Form with validation errors');

    // Screenshot 9: Previous visits table with data
    await page.goto(`${baseUrl}/owners/1/pets/1/visits/new`);
    await page.waitForLoadState('networkidle');
    const visitsTable = page.locator('.table-responsive').filter({ has: page.locator('table') });
    if (await visitsTable.isVisible()) {
      await visitsTable.screenshot({
        path: path.join(outputDir, '09-previous-visits-table.png')
      });
      console.log('✓ Captured: Previous visits table');
    } else {
      console.log('⚠ Previous visits table not found (may be empty state)');
    }

    // Screenshot 10: Empty state for previous visits (if we can find a pet with no visits)
    // Try pet 13 (Sly) which might not have visits
    await page.goto(`${baseUrl}/owners/10/pets/13/visits/new`);
    await page.waitForLoadState('networkidle');
    const emptyState = page.locator('text=No previous visits found');
    if (await emptyState.isVisible()) {
      await page.screenshot({
        path: path.join(outputDir, '10-empty-state-visits.png'),
        fullPage: true
      });
      console.log('✓ Captured: Empty state for previous visits');
    } else {
      console.log('⚠ Could not find empty state (pet has visits)');
    }

    // Screenshot 11: Successful form submission (capture owner details page)
    await page.goto(`${baseUrl}/owners/1/pets/1/visits/new`);
    await page.waitForLoadState('networkidle');
    await page.locator('#date').fill('2026-03-20');
    await page.locator('#startTime').selectOption('14:30');
    await page.locator('#vet\\.id').selectOption('2');
    await page.locator('#description').fill('Annual wellness exam');
    await page.locator('button[type="submit"]').click();
    await page.waitForURL('**/owners/1');
    await page.waitForLoadState('networkidle');
    await page.screenshot({
      path: path.join(outputDir, '11-successful-submission.png'),
      fullPage: true
    });
    console.log('✓ Captured: Successful form submission and redirect');

    console.log('\n✅ All proof artifacts captured successfully!');

  } catch (error) {
    console.error('Error capturing screenshots:', error);
    throw error;
  } finally {
    await browser.close();
  }
}

captureProofArtifacts();
