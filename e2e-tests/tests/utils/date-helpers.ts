/**
 * Date utility functions for E2E tests
 *
 * These helpers prevent temporal coupling by generating dates dynamically
 * relative to the current execution time, ensuring tests remain stable
 * regardless of when they are executed.
 */

/**
 * Get a future date N days from now in YYYY-MM-DD format
 * @param daysFromNow Number of days in the future (default: 7)
 * @returns ISO date string (YYYY-MM-DD)
 *
 * @example
 * const visitDate = getFutureDate(7); // 7 days from now
 * await page.fill('#date', visitDate);
 */
export function getFutureDate(daysFromNow: number = 7): string {
  const date = new Date();
  date.setDate(date.getDate() + daysFromNow);
  return date.toISOString().split('T')[0];
}

/**
 * Get today's date in YYYY-MM-DD format
 * @returns ISO date string (YYYY-MM-DD)
 *
 * @example
 * const today = getTodayDate();
 * await page.fill('#date', today);
 */
export function getTodayDate(): string {
  return new Date().toISOString().split('T')[0];
}

/**
 * Get a past date N days ago in YYYY-MM-DD format
 * @param daysAgo Number of days in the past (default: 7)
 * @returns ISO date string (YYYY-MM-DD)
 *
 * @example
 * const pastDate = getPastDate(30); // 30 days ago
 * await page.fill('#date', pastDate);
 */
export function getPastDate(daysAgo: number = 7): string {
  const date = new Date();
  date.setDate(date.getDate() - daysAgo);
  return date.toISOString().split('T')[0];
}

/**
 * Get a date with a specific offset in days from today
 * @param offsetDays Number of days offset (positive for future, negative for past)
 * @returns ISO date string (YYYY-MM-DD)
 *
 * @example
 * const tomorrow = getDateWithOffset(1);
 * const yesterday = getDateWithOffset(-1);
 */
export function getDateWithOffset(offsetDays: number): string {
  const date = new Date();
  date.setDate(date.getDate() + offsetDays);
  return date.toISOString().split('T')[0];
}

/**
 * Generate a unique description string with timestamp
 * Useful for creating unique test data that can be verified later
 *
 * @param prefix Description prefix
 * @returns Description with timestamp
 *
 * @example
 * const description = getUniqueDescription('E2E visit');
 * // Returns: "E2E visit 1708790400000"
 */
export function getUniqueDescription(prefix: string): string {
  return `${prefix} ${Date.now()}`;
}
