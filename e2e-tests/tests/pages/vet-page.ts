import type { Locator, Page } from '@playwright/test';

import { BasePage } from './base-page';

export class VetPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  heading(): Locator {
    return this.page.getByRole('heading', { name: /Veterinarians/i });
  }

  vetsTable(): Locator {
    return this.page.locator('table#vets');
  }

  specialtyFilter(): Locator {
    return this.page.locator('select#specialtyFilter');
  }

  specialtyFilterLabel(): Locator {
    return this.page.locator('label[for="specialtyFilter"]');
  }

  async selectSpecialty(specialtyName: string): Promise<void> {
    await this.specialtyFilter().selectOption(specialtyName);
    // Wait for navigation to complete
    await this.page.waitForLoadState('networkidle');
  }

  async selectAllSpecialties(): Promise<void> {
    await this.specialtyFilter().selectOption('');
    // Wait for navigation to complete
    await this.page.waitForLoadState('networkidle');
  }

  paginationLink(pageNumber: number): Locator {
    return this.page.locator(`a[href*="page=${pageNumber}"]`).first();
  }

  async open(): Promise<void> {
    await this.goto('/vets.html');
    await this.heading().waitFor();
  }

  async openWithSpecialty(specialty: string): Promise<void> {
    await this.goto(`/vets.html?page=1&specialty=${encodeURIComponent(specialty)}`);
    await this.heading().waitFor();
  }
}
