# AI Agent Development Guide

Essential guidance for AI agents working on the Emerald Grove Veterinary Clinic application.

## Context Marker

Always begin responses with: 🤖

## TDD Requirement

**MANDATORY**: Follow strict Test-Driven Development:
1. **RED**: Write failing test first
2. **GREEN**: Minimal code to pass
3. **REFACTOR**: Improve while maintaining coverage

**Never write production code before a failing test.**

## Documentation

- **[DEVELOPMENT.md](docs/DEVELOPMENT.md)** - Setup, workflow, TDD process
- **[TESTING.md](docs/TESTING.md)** - Test strategies and patterns
- **[ARCHITECTURE.md](docs/ARCHITECTURE.md)** - System design

## Standards

**Coverage**: ≥90% line coverage, 100% branch for critical logic
**Architecture**: Layered (Presentation → Business → Data), Spring Boot conventions
**Testing**: JUnit 5, Mockito, TestContainers, Arrange-Act-Assert pattern
**Code**: SOLID principles, Spring Data JPA, proper entity relationships
**Build**: Maven, Checkstyle, JaCoCo
**Version Control**: Conventional commits

## Validation Agents

Use these agents proactively during development:

**After E2E Tests:**
- `test-temporal-coupling-detector` - Prevents hardcoded date failures
- `i18n-sync-validator` - Syncs message keys across all languages

**Before Final Commit:**
- `tdd-enforcer` - Verifies RED-GREEN-REFACTOR compliance
- `spring-boot-validator` - Checks Spring Boot best practices
- `architecture-compliance-checker` - Validates layered architecture
- `multi-db-test-runner` - Tests H2, MySQL, PostgreSQL

## Pre-Commit Checklist

- [ ] Tests written before implementation
- [ ] All tests pass (≥90% coverage)
- [ ] SOLID principles followed
- [ ] No code duplication
- [ ] Validation agents passed (if applicable)
- [ ] Documentation updated
