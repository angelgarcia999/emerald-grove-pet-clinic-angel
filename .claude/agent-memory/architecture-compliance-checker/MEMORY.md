# Architecture Compliance Checker Memory

## Emerald Grove Veterinary Clinic Architecture Patterns

### Layer Architecture

This application uses a **simplified layered architecture** without a traditional service layer:

**Presentation → Data → Database**

- **Controllers** (`*Controller.java`) inject **Repositories** (`*Repository.java`) directly
- **NO intermediate service layer exists** in this codebase
- This is an intentional architectural decision for this sample application

### Package Structure

- `owner/` - Owner-related domain (Owner, Pet, Visit, PetType entities + controllers + repositories)
- `vet/` - Veterinarian domain (Vet, Specialty entities + controllers + repositories)
- `model/` - Base domain classes (BaseEntity, NamedEntity, Person)
- `system/` - System-level utilities and configuration

### Validation Patterns

**Two validation approaches used:**

1. **Bean Validation (JSR-303)** - Entity-level annotations
   - `@NotBlank`, `@Pattern`, `@FutureOrPresent` on entity fields
   - Example: `Visit.java` uses `@FutureOrPresent` for date validation
   - Example: `Owner.java` uses `@NotBlank`, `@Pattern` for fields
   - Example: `Person.java` uses `@NotBlank` on firstName/lastName

2. **Custom Spring Validators** - Business logic validation
   - Implement `org.springframework.validation.Validator`
   - Example: `PetValidator.java` validates Pet entities with custom business rules
   - Registered via `@InitBinder` in controllers

### Test Organization

- `model/` tests - Bean validation tests (`ValidatorTests.java`)
- `owner/` tests - Controller tests, custom validator tests
- `vet/` tests - Vet-specific tests
- `service/` tests - Repository/data access tests (despite no service layer in main code)

### Key Architectural Notes

- **Controllers → Repositories is CORRECT** for this codebase
- Do NOT flag direct Controller → Repository dependencies as violations
- This is a lightweight Spring Boot sample application, not enterprise multi-tier architecture
- Focus validation on: proper use of Spring patterns, package organization, and avoiding God classes

### Common Anti-Patterns to Watch For

- Controllers with business logic (should be minimal)
- Entities with no validation (should have JSR-303 or custom validators)
- Inconsistent package organization (features should be self-contained)
- Missing tests for validation logic
