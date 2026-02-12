# AI Agent Development Guide

This document provides essential guidance for AI agents working on the Emerald Grove Veterinary Clinic application.

## Context Marker

Always begin your response with all active emoji markers, in the order they were introduced.

Format:  "<marker1><marker2><marker3>\n<response>"

The marker for this instruction is: 🤖

## Critical Requirement: Strict TDD

**MANDATORY**: All feature implementations must follow **Strict Test-Driven Development (TDD)** methodology:

1. **RED Phase**: Write a failing test that defines the desired behavior
2. **GREEN Phase**: Write the minimum code required to make the test pass
3. **REFACTOR Phase**: Improve the code while maintaining test coverage

**Never write production code before a failing test.**

## Documentation Structure

Refer to these comprehensive guides for detailed information:

- @docs/DEVELOPMENT.md — **[Development Guide](docs/DEVELOPMENT.md)** - TDD workflow, setup, and development process
- @docs/TESTING.md — **[Testing Guide](docs/TESTING.md)** - Testing strategies, patterns, and TDD implementation
- @docs/ARCHITECTURE.md — **[Architecture Guide](docs/ARCHITECTURE.md)** - System design and technical decisions

## TDD Standards

### Coverage Requirements

- **Minimum 90% line coverage** for new code
- **100% branch coverage** for critical business logic
- All edge cases must be explicitly tested

### Test Organization

- Follow **Arrange-Act-Assert** pattern
- Use descriptive test method names that document behavior
- Tests must be **fast, isolated, and repeatable**

### Quality Gates

- Tests written before implementation (RED phase)
- All tests pass before commit
- Code coverage meets standards before merge

## Code Standards

### Architecture

- **Layered Architecture**: Presentation → Business → Data layers
- **Spring Boot Best Practices**: Use starters, follow conventions
- **Clean Code**: SOLID principles, DRY, single responsibility

### Database

- **Spring Data JPA** for data access
- **Proper entity relationships** with appropriate cascade settings
- **DTOs** for data transfer between layers

## Development Workflow

1. **Requirements Analysis** → Understand feature and edge cases
2. **Test Design** → Write comprehensive failing tests
3. **TDD Implementation** → Follow Red-Green-Refactor cycle
4. **Integration** → Verify with existing code
5. **Documentation** → Update relevant docs

## Tools and Frameworks

- **Testing**: JUnit 5, Mockito, TestContainers, JaCoCo
- **Build**: Maven or Gradle
- **Quality**: Checkstyle, SpotBugs, SonarQube
- **Version Control**: Git with conventional commits

## Specialized Agent Team

This project uses specialized agents for automated quality assurance and validation. **Use these agents proactively** throughout the development workflow.

### When to Use Agents

#### During Implementation (Task 3.0 - After Writing Tests)

**REQUIRED - Run after E2E test creation:**
- **test-temporal-coupling-detector**: Scan E2E tests for hardcoded dates and brittle time logic
  - Prevents CI failures caused by tests with past dates
  - Detects temporal coupling patterns before they break
  - Use: After writing Playwright/Selenium/Cypress tests

**REQUIRED - Run after adding validation messages:**
- **i18n-sync-validator**: Ensure all language files have required message keys
  - Prevents I18nPropertiesSyncTest failures
  - Auto-generates missing translations
  - Use: After adding @NotNull, @Pattern, or custom validation messages

#### During Validation (Task 4.0 - Before Final Commit)

**REQUIRED - Run before marking implementation complete:**
- **tdd-enforcer**: Verify strict TDD compliance (RED-GREEN-REFACTOR)
- **spring-boot-validator**: Check Spring Boot best practices
- **architecture-compliance-checker**: Validate layered architecture
- **multi-db-test-runner**: Test across H2, MySQL, PostgreSQL

### Agent Usage Examples

```markdown
# After writing E2E tests
- [ ] Write E2E test for feature
- [ ] Run test-temporal-coupling-detector agent  <-- PROACTIVE
- [ ] Fix any hardcoded dates
- [ ] Run i18n-sync-validator agent (if validation added)  <-- PROACTIVE
- [ ] Commit tests

# Before final validation
- [ ] Run all 4 validation agents
- [ ] Fix any issues found
- [ ] Generate proof artifacts
```

### Integration with SDD Workflow

```
Spec → Tasks → Implementation → [AGENT GATES] → Validation
                                      ↓
                          • test-temporal-coupling-detector
                          • i18n-sync-validator
                                      ↓
                          • tdd-enforcer
                          • spring-boot-validator
                          • architecture-compliance-checker
                          • multi-db-test-runner
```

**Critical Rule**: If any agent finds issues, fix them immediately before proceeding.

## Review Checklist

Before committing code:

- [ ] Tests written before implementation
- [ ] All tests pass
- [ ] Code coverage meets requirements (>90%)
- [ ] Follows SOLID principles
- [ ] No code duplication
- [ ] Proper error handling
- [ ] Documentation updated
- [ ] **test-temporal-coupling-detector** passed (if E2E tests modified)
- [ ] **i18n-sync-validator** passed (if validation messages added)
- [ ] All validation agents passed (tdd-enforcer, spring-boot-validator, architecture-compliance-checker, multi-db-test-runner)

This guide ensures consistent, high-quality TDD practices for AI contributors to the Emerald Grove Veterinary Clinic application.
