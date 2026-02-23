import type { Locator, Page } from '@playwright/test';

import { BasePage } from './base-page';

export class VisitPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  heading(): Locator {
    return this.page.getByRole('heading', { name: /New Visit/i });
  }

  async fillVisitDate(date: string): Promise<void> {
    await this.page.locator('input#date').fill(date);
  }

  async fillDescription(description: string): Promise<void> {
    await this.page.locator('input#description').fill(description);
  }

  async selectTime(time: string): Promise<void> {
    await this.page.locator('select#startTime').selectOption(time);
  }

  async selectVet(vetId: string): Promise<void> {
    await this.page.locator('select#vet\\.id').selectOption(vetId);
  }

  async submit(): Promise<void> {
    await this.page.getByRole('button', { name: /Add Visit/i }).click();
  }

  // Pet Summary Card selectors
  petSummaryCard(): Locator {
    return this.page.locator('.card').filter({ hasText: 'Pet Summary' });
  }

  petName(): Locator {
    return this.petSummaryCard().locator('[data-test="pet-name"], .pet-name');
  }

  petType(): Locator {
    return this.petSummaryCard().locator('[data-test="pet-type"], .pet-type');
  }

  petBirthDate(): Locator {
    return this.petSummaryCard().locator('[data-test="pet-birth-date"], .pet-birth-date');
  }

  petOwner(): Locator {
    return this.petSummaryCard().locator('[data-test="pet-owner"], .pet-owner');
  }

  // Quick Info Card selectors
  quickInfoCard(): Locator {
    return this.page.locator('.card').filter({ hasText: 'Quick Info' });
  }

  clinicHours(): Locator {
    return this.quickInfoCard().getByText(/9:00 AM.*5:00 PM/i);
  }

  visitDuration(): Locator {
    return this.quickInfoCard().getByText(/30 minutes/i);
  }

  // Layout selectors
  twoColumnLayout(): Locator {
    return this.page.locator('.row > .col-md-6');
  }

  // Previous Visits Table selectors
  previousVisitsTable(): Locator {
    return this.page.locator('table.table-striped');
  }

  previousVisitsEmptyState(): Locator {
    return this.page.getByText(/No previous visits found/i);
  }

  // Form validation error selectors
  fieldValidationError(fieldName: string): Locator {
    return this.page.locator(`#${fieldName} ~ .invalid-feedback, [name="${fieldName}"] ~ .invalid-feedback`);
  }
}
