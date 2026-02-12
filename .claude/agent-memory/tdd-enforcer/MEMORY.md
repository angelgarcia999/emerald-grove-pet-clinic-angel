# TDD Enforcer Memory

## Common TDD Violations

### Violation Pattern #1: Correct RED-GREEN Sequence, Missing Commit Message Convention

**Date**: 2026-02-12
**Feature**: Visit date validation (Task 1.0)
**Violation Type**: Medium - Missing RED/GREEN/REFACTOR commit prefixes

**What Was Done Right**:
- Tests written before implementation (commit 6183194 before a77d037)
- Test confirmed to fail initially (RED phase verified)
- Implementation made tests pass (GREEN phase verified)
- All 4 tests passing with 100% line coverage on Visit.java
- Proper Arrange-Act-Assert pattern in tests
- Edge cases covered (past, today, future dates)

**What Was Missing**:
- Commit messages did not use "RED:", "GREEN:", "REFACTOR:" prefixes
- Actual commits:
  - `test: add validation tests for visit date constraints` (should be "RED: add validation tests...")
  - `feat: add past date validation to Visit entity` (should be "GREEN: add past date validation...")

**Impact**: Low - TDD cycle was followed correctly, only documentation/convention issue

**Recommendation**: Update commit message convention in project documentation to require RED/GREEN/REFACTOR prefixes for better TDD visibility in git history.

## Test Coverage Standards

**Project Requirements**:
- Minimum 90% line coverage for new code
- 100% branch coverage for critical business logic
- All edge cases explicitly tested

**Visit.java Coverage** (Task 1.0):
- Line coverage: 100% (6/6 lines covered)
- Branch coverage: N/A (no branching logic in entity)
- Edge case coverage: Complete (past, present, future dates tested)

## Successful TDD Patterns

### Bean Validation Testing Pattern

**Location**: `/Users/user/Desktop/Liatrio_Forge/emerald-grove-pet-clinic-angel/src/test/java/org/springframework/samples/petclinic/model/ValidatorTests.java`

**Pattern**:
```java
@Test
void shouldNotValidateWhenVisitDateIsInPast() {
    // Arrange
    LocaleContextHolder.setLocale(Locale.ENGLISH);
    Visit visit = new Visit();
    visit.setDate(LocalDate.of(2020, 1, 1)); // Past date
    visit.setDescription("Test visit");

    // Act
    Validator validator = createValidator();
    Set<ConstraintViolation<Visit>> constraintViolations = validator.validate(visit);

    // Assert
    assertThat(constraintViolations).hasSize(1);
    ConstraintViolation<Visit> violation = constraintViolations.iterator().next();
    assertThat(violation.getPropertyPath()).hasToString("date");
    assertThat(violation.getMessage()).isEqualTo("Visit date cannot be in the past");
}
```

**Why This Works**:
- Clear Arrange-Act-Assert structure
- Tests the validation constraint directly
- Verifies both constraint violation count and specific error message
- Uses English locale for consistent error messages
- Includes descriptive test method name

## Git History Verification Commands

**Check commit order**:
```bash
git log --oneline --all --graph --decorate -20
```

**Verify test failure at RED commit**:
```bash
git checkout <test-commit> && ./mvnw test -Dtest=<TestClass>#<testMethod>
```

**Verify test success at GREEN commit**:
```bash
git checkout <impl-commit> && ./mvnw test -Dtest=<TestClass>#<testMethod>
```

**Generate coverage report**:
```bash
./mvnw clean test jacoco:report
```

## Project-Specific Conventions

**File Structure**:
- Entity tests: `src/test/java/org/springframework/samples/petclinic/model/ValidatorTests.java`
- Entities: `src/main/java/org/springframework/samples/petclinic/owner/*.java`
- Controller tests: `src/test/java/org/springframework/samples/petclinic/owner/*ControllerTests.java`

**Testing Tools**:
- JUnit 5 for test framework
- AssertJ for fluent assertions
- Hibernate Validator for Bean Validation
- JaCoCo for coverage reporting

**Commit Message Patterns** (to verify):
- `test:` prefix indicates test code
- `feat:` prefix indicates feature implementation
- Should use `RED:`, `GREEN:`, `REFACTOR:` for explicit TDD cycle documentation
