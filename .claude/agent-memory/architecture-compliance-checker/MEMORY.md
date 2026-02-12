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

### Validation Architecture (Verified 2026-02-12)

**Two-tier validation approach:**

1. **Entity Layer (Data Layer)** - JSR-303 Bean Validation annotations
   - `Visit.java`: `@NotNull`, `@FutureOrPresent` on date field, `@NotBlank` on description
   - `Owner.java`: `@NotBlank`, `@Pattern` on address/city/telephone
   - `Person.java`: `@NotBlank` on firstName/lastName (base class validation)
   - Entity validation is automatic when `@Valid` is used in controller

2. **Controller Layer (Presentation Layer)** - Validation trigger and error handling
   - `VisitController.java` lines 92-96: Uses `@Valid` annotation to trigger entity validation
   - `OwnerController.java`: Consistent @Valid + BindingResult pattern for Owner operations
   - `PetController.java`: Uses @Valid + BindingResult + custom PetValidator via @InitBinder
   - `BindingResult` captures validation errors from entity annotations
   - Controller checks `result.hasErrors()` and returns to form view on error
   - NO business logic in controller - just validation orchestration

**Clean separation verified:**
- Entity layer: Defines validation rules (what is valid)
- Controller layer: Triggers validation and handles results (when to validate)
- No tight coupling - controller doesn't know specific validation rules
- Follows Dependency Inversion Principle (depends on Spring's validation abstraction)

**Validation message internationalization:**
- Custom validation messages use message key references: `{visit.date.required}`, `{visit.date.future}`
- Messages defined in `messages/messages.properties` (lines 9-10)
- Supports internationalization via messages_*.properties files (de, es, fa, ko, pt, ru, tr)
- Test validation properly loads message source using `ReloadableResourceBundleMessageSource`

**Visit Date Validation Feature (Audited 2026-02-12):**
- FULLY COMPLIANT with architecture patterns
- Entity validation: `@NotNull` + `@FutureOrPresent` on Visit.date
- Controller integration: @Valid annotation triggers validation in processNewVisitForm
- Test coverage: ValidatorTests (bean validation), VisitControllerTests (web layer integration)
- No layer violations detected
- Consistent with existing Owner/Pet validation patterns

### Repository Query Patterns (Verified 2026-02-12)

**Duplicate Detection Query - Task 1.0 (AUDIT PASSED):**
- `OwnerRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone()`
- **Architecture**: Pure data access layer - NO business logic (100% compliant)
- **Spring Data Pattern**: Query derivation from method name (best practice)
- **Return Type**: `Optional<Owner>` for null-safety
- **Search Strategy**: Case-insensitive names, exact telephone match
- **Test Coverage**: 4 integration tests in `ClinicServiceTests.java` (lines 251-334)
- **Input Normalization**: Caller responsibility (controller trims whitespace)
- **Reference Quality**: Exemplifies clean repository pattern for future implementations
