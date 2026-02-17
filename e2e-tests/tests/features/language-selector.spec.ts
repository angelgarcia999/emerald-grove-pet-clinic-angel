import { test, expect } from '@fixtures/base-test';

import { HomePage } from '@pages/home-page';

test.describe('Language Selector', () => {
  test('should display language selector dropdown in navbar', async ({ page }) => {
    const homePage = new HomePage(page);
    await homePage.open();

    // Verify language dropdown exists
    const languageDropdown = page.locator('#languageDropdown');
    await expect(languageDropdown).toBeVisible();

    // Verify globe icon is present
    const globeIcon = languageDropdown.locator('.fa-globe');
    await expect(globeIcon).toBeVisible();

    // Verify dropdown label
    await expect(languageDropdown).toContainText('Language');
  });

  test('should show all 8 language options in dropdown', async ({ page }) => {
    const homePage = new HomePage(page);
    await homePage.open();

    // Click language dropdown
    await page.locator('#languageDropdown').click();

    // Verify all language options are present
    const dropdownMenu = page.locator('#languageDropdown').locator('..').locator('.dropdown-menu');

    await expect(dropdownMenu.locator('a[href*="lang=en"]')).toContainText('English');
    await expect(dropdownMenu.locator('a[href*="lang=de"]')).toContainText('German');
    await expect(dropdownMenu.locator('a[href*="lang=es"]')).toContainText('Spanish');
    await expect(dropdownMenu.locator('a[href*="lang=fa"]')).toContainText('Persian');
    await expect(dropdownMenu.locator('a[href*="lang=ko"]')).toContainText('Korean');
    await expect(dropdownMenu.locator('a[href*="lang=pt"]')).toContainText('Portuguese');
    await expect(dropdownMenu.locator('a[href*="lang=ru"]')).toContainText('Russian');
    await expect(dropdownMenu.locator('a[href*="lang=tr"]')).toContainText('Turkish');
  });

  test('should change UI to Spanish when Spanish is selected', async ({ page }) => {
    const homePage = new HomePage(page);
    await homePage.open();

    // Click language dropdown and select Spanish
    await page.locator('#languageDropdown').click();
    await page.locator('a[href*="lang=es"]').click();

    // Wait for page to reload with Spanish
    await page.waitForLoadState('networkidle');

    // Verify HTML lang attribute is Spanish
    const htmlLang = await page.locator('html').getAttribute('lang');
    expect(htmlLang).toBe('es');

    // Verify UI text is in Spanish
    const homeLink = page.locator('nav.navbar').getByRole('link', { name: /Inicio/i });
    await expect(homeLink).toBeVisible();

    const findOwnersLink = page.locator('nav.navbar').getByRole('link', { name: /Buscar propietarios/i });
    await expect(findOwnersLink).toBeVisible();
  });

  test('should change UI to German when German is selected', async ({ page }) => {
    const homePage = new HomePage(page);
    await homePage.open();

    // Click language dropdown and select German
    await page.locator('#languageDropdown').click();
    await page.locator('a[href*="lang=de"]').click();

    // Wait for page to reload with German
    await page.waitForLoadState('networkidle');

    // Verify HTML lang attribute is German
    const htmlLang = await page.locator('html').getAttribute('lang');
    expect(htmlLang).toBe('de');

    // Verify UI text is in German
    const homeLink = page.locator('nav.navbar').getByRole('link', { name: /Startseite/i });
    await expect(homeLink).toBeVisible();

    const findOwnersLink = page.locator('nav.navbar').getByRole('link', { name: /Besitzer suchen/i });
    await expect(findOwnersLink).toBeVisible();
  });

  test('should persist language selection across page navigation', async ({ page }) => {
    const homePage = new HomePage(page);
    await homePage.open();

    // Select German
    await page.locator('#languageDropdown').click();
    await page.locator('a[href*="lang=de"]').click();
    await page.waitForLoadState('networkidle');

    // Verify German is active
    let htmlLang = await page.locator('html').getAttribute('lang');
    expect(htmlLang).toBe('de');

    // Navigate to Veterinarians page
    await page.locator('nav.navbar').getByRole('link', { name: /Tierärzte/i }).click();
    await page.waitForLoadState('networkidle');

    // Verify language is still German
    htmlLang = await page.locator('html').getAttribute('lang');
    expect(htmlLang).toBe('de');

    // Navigate back to home
    await page.locator('nav.navbar').getByRole('link', { name: /Startseite/i }).click();
    await page.waitForLoadState('networkidle');

    // Verify language is still German
    htmlLang = await page.locator('html').getAttribute('lang');
    expect(htmlLang).toBe('de');
  });

  test('should switch back to English from another language', async ({ page }) => {
    const homePage = new HomePage(page);
    await homePage.open();

    // Select Korean
    await page.locator('#languageDropdown').click();
    await Promise.all([
      page.waitForLoadState('networkidle'),
      page.locator('a[href*="lang=ko"]').click()
    ]);

    // Verify Korean is active
    let htmlLang = await page.locator('html').getAttribute('lang');
    expect(htmlLang).toBe('ko');

    // Switch back to English
    await page.locator('#languageDropdown').click();
    await Promise.all([
      page.waitForLoadState('networkidle'),
      page.locator('a[href*="lang=en"]').click()
    ]);

    // Wait a bit more for HTML lang attribute to update
    await page.waitForFunction(() => document.documentElement.lang === 'en');

    // Verify HTML lang attribute is English
    htmlLang = await page.locator('html').getAttribute('lang');
    expect(htmlLang).toBe('en');

    // Verify UI text is in English
    const homeLink = page.locator('nav.navbar').getByRole('link', { name: /Home/i });
    await expect(homeLink).toBeVisible();

    const findOwnersLink = page.locator('nav.navbar').getByRole('link', { name: /Find Owners/i });
    await expect(findOwnersLink).toBeVisible();
  });

  test('should maintain language after page reload', async ({ page }) => {
    const homePage = new HomePage(page);
    await homePage.open();

    // Select Spanish
    await page.locator('#languageDropdown').click();
    await page.locator('a[href*="lang=es"]').click();
    await page.waitForLoadState('networkidle');

    // Verify Spanish is active
    let htmlLang = await page.locator('html').getAttribute('lang');
    expect(htmlLang).toBe('es');

    // Reload the page
    await page.reload();
    await page.waitForLoadState('networkidle');

    // Verify language is still Spanish after reload
    htmlLang = await page.locator('html').getAttribute('lang');
    expect(htmlLang).toBe('es');

    // Verify UI text is still in Spanish
    const homeLink = page.locator('nav.navbar').getByRole('link', { name: /Inicio/i });
    await expect(homeLink).toBeVisible();
  });
});
