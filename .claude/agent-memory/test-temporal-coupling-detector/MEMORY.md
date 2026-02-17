# Test Temporal Coupling Detector - Agent Memory

## Project: Emerald Grove Pet Clinic

### Scan Results (2026-02-16)

#### Database Test Data (data.sql files)
- **Status**: EXCELLENT - No temporal coupling issues
- All three database profiles (H2, MySQL, PostgreSQL) use dynamic date calculations:
  - H2: `DATEADD('DAY', 3, CURRENT_DATE())`
  - MySQL: `DATE_ADD(CURDATE(), INTERVAL 3 DAY)`
  - PostgreSQL: `CURRENT_DATE + INTERVAL '3 days'`
- Future-dated visits are relative to current date (3, 5, 6 days ahead)

#### Integration Tests
- **UpcomingVisitsIntegrationTests.java**: CLEAN
  - Uses `LocalDate.now()` for dynamic date calculations
  - Proper relative date logic: `LocalDate.now().plusDays(30)`
  - No hardcoded dates in test logic

#### Unit Tests
- **VisitControllerTests.java**: Contains intentional hardcoded past date
  - Line 103: `"2020-01-01"` used in `testProcessNewVisitFormWithPastDate()`
  - **INTENTIONAL**: This test validates that past dates are properly rejected
  - **SAFE**: This is testing validation logic, not data retrieval
  - Follows best practice of using hardcoded dates only for validation tests
  - Other tests properly use `LocalDate.now()` and relative dates

### Patterns Found

#### Good Patterns (Preserve These)
1. Database-specific date functions for future dates
2. `LocalDate.now()` for current date calculations
3. Relative date calculations: `.plusDays()`, `.minusDays()`
4. Intentional hardcoded dates in validation tests (with clear test names)

#### No Issues Found
- No temporal coupling in E2E tests
- No brittle time assertions
- No uncontrolled `new Date()` usage in date-sensitive contexts

### Recommendations
1. Continue using database-native date functions in data.sql files
2. Document intentional hardcoded dates with comments when used for validation
3. Consider adding a helper class for common date operations in tests

### Future Monitoring
- Watch for new E2E tests that might introduce hardcoded dates
- Monitor visit scheduling features for date handling
- Check any new validation tests for proper date handling patterns
