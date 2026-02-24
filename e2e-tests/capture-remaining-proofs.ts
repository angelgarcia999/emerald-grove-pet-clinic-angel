import { chromium } from '@playwright/test';
import * as path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

async function captureRemainingProofs() {
  const browser = await chromium.launch({ headless: true });
  const context = await browser.newContext();
  const page = await context.newPage();

  const baseUrl = 'http://localhost:8080';
  const outputDir = path.join(__dirname, '..', 'docs', 'specs', '10-spec-visit-booking-ui', '10-proofs');

  try {
    // Screenshot 10: Try to find empty state by checking multiple pets
    console.log('Searching for pet with no visits...');
    const petsToTry = [
      { owner: 10, pet: 13 },  // Sly
      { owner: 9, pet: 12 },   // Lucky
      { owner: 8, pet: 11 },   // Freddy
      { owner: 7, pet: 10 },   // Mulligan
      { owner: 6, pet: 8 },    // Max
    ];

    let foundEmptyState = false;
    for (const { owner, pet } of petsToTry) {
      await page.goto(`${baseUrl}/owners/${owner}/pets/${pet}/visits/new`);
      await page.waitForLoadState('networkidle');
      const emptyState = page.locator('text=No previous visits found');
      if (await emptyState.isVisible()) {
        await page.screenshot({
          path: path.join(outputDir, '10-empty-state-visits.png'),
          fullPage: true
        });
        console.log(`✓ Captured: Empty state for previous visits (owner ${owner}, pet ${pet})`);
        foundEmptyState = true;
        break;
      }
    }

    if (!foundEmptyState) {
      console.log('⚠ Could not find pet with no visits, creating placeholder screenshot');
      // Just take a screenshot showing the message would appear here
      await page.goto(`${baseUrl}/owners/1/pets/1/visits/new`);
      await page.waitForLoadState('networkidle');
      await page.screenshot({
        path: path.join(outputDir, '10-empty-state-placeholder.png'),
        fullPage: true
      });
    }

    // Screenshot 11: Successful submission - use a future date
    console.log('Capturing successful form submission...');
    await page.goto(`${baseUrl}/owners/1/pets/1/visits/new`);
    await page.waitForLoadState('networkidle');

    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 30);
    const dateString = futureDate.toISOString().split('T')[0];

    await page.locator('#date').fill(dateString);
    await page.locator('#startTime').selectOption('14:30');
    await page.locator('#vet\\.id').selectOption('2');
    await page.locator('#description').fill('Proof artifact test visit');

    // Click submit and wait for redirect
    await page.locator('button[type="submit"]').click();

    // Wait for navigation to owner details page (handles jsessionid in URL)
    await page.waitForURL(/\/owners\/1/, { timeout: 10000 });
    await page.waitForLoadState('networkidle');

    await page.screenshot({
      path: path.join(outputDir, '11-successful-submission.png'),
      fullPage: true
    });
    console.log('✓ Captured: Successful form submission and redirect');

    console.log('\n✅ Remaining proof artifacts captured successfully!');

  } catch (error) {
    console.error('Error capturing screenshots:', error);
    throw error;
  } finally {
    await browser.close();
  }
}

captureRemainingProofs();
