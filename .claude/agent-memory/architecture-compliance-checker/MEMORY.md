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

### Owner Duplicate Prevention Feature (Audited 2026-02-13)

**COMPREHENSIVE AUDIT: FULLY COMPLIANT**

See detailed analysis: [audit-2026-02-13.md](./audit-2026-02-13.md)

**Key Findings:**
- ✅ Zero layer boundary violations
- ✅ Pure repository pattern (no business logic in data layer)
- ✅ Proper validation separation (entity vs. controller)
- ✅ Rich domain model (Owner has addPet(), getPet(), addVisit() methods)
- ✅ 16/16 web tests passing, 4 integration tests, 1 E2E test
- ✅ Works identically on H2, MySQL, PostgreSQL
- ✅ Internationalized messages in 8 languages
- ✅ SOLID principles adhered to
- ✅ Constructor injection everywhere (no field injection)

**Validation Pattern Confirmed:**
- Entity layer: JSR-303 annotations define rules (@NotBlank, @Pattern)
- Controller layer: @Valid triggers validation, BindingResult handles errors
- No tight coupling between layers
- Follows Dependency Inversion Principle

**Reference Implementation:**
This feature is now a reference example for:
- Spring Data JPA query derivation
- Duplicate detection patterns
- Controller validation orchestration
- Bean validation integration
- Test pyramid (unit → integration → E2E)

### Architecture Compliance Audit (2026-02-13)

**Layer Boundaries - FULLY COMPLIANT:**
- All controllers properly inject repositories directly (no service layer by design)
- `OwnerController`: Injects `OwnerRepository` (line 53)
- `PetController`: Injects `OwnerRepository` + `PetTypeRepository` (lines 52, 54)
- `VisitController`: Injects `OwnerRepository` + `VisitRepository` (lines 47, 49)
- `VetController`: Injects `VetRepository` (line 38)

**Entity Design - STRONG DOMAIN MODEL:**
- Entities contain business behavior (NOT anemic)
- `Owner.java`: Methods getPet(), addPet(), addVisit() encapsulate domain logic
- `Pet.java`: addVisit() method manages relationship
- `Vet.java`: addSpecialty(), getNrOfSpecialties() provide domain behavior
- Proper use of JPA relationships with cascade and fetch strategies

**Dependency Injection - BEST PRACTICES:**
- Constructor injection universally applied (immutable dependencies)
- No field injection (avoiding Spring anti-pattern)
- Proper use of `@Controller`, `@Repository` stereotypes

**Package Organization - FEATURE-BASED:**
- `owner/` package: Owner, Pet, Visit, PetType + controllers + repositories
- `vet/` package: Vet, Specialty + controller + repository
- `model/` package: Base classes (BaseEntity, Person, NamedEntity)
- `system/` package: Configuration (CacheConfiguration, WebConfiguration)

**Spring Boot Patterns - EXEMPLARY:**
- Spring Data JPA query derivation (OwnerRepository methods)
- `@Cacheable` on repository methods (VetRepository)
- `@Transactional(readOnly = true)` on read operations
- Proper use of `Optional<T>` for nullable returns
- Bean Validation (JSR-303) integrated properly

### Upcoming Visits Feature Audit (2026-02-16)

**Status: ✅ FULLY COMPLIANT** - See [detailed audit](./audit-upcoming-visits-2026-02-16.md)

**Key Patterns Verified:**
- `VisitRepository.findUpcomingVisits()` - Pure JPQL query with date range filtering
- `VisitController.showUpcomingVisits()` - Proper Controller → Repository delegation
- Date calculations in controller (LocalDate.now(), plusDays()) are acceptable for query parameter preparation
- JPQL navigates Pet → Visit relationship (Visit has no direct Pet reference - unidirectional)
- Integration tests cover E2E HTTP and repository layers (UpcomingVisitsIntegrationTests.java)
- View uses Thymeleaf best practices (i18n, conditional rendering, date formatting)

**Date Calculations in Controller - ACCEPTABLE PATTERN:**
- LocalDate.now() - System time retrieval (not business logic)
- plusDays(days) - Query parameter preparation (presentation layer responsibility)
- Rationale: Controller prepares input for repository, repository handles data access

**Reference Quality:**
- Date range queries with BETWEEN operator and ORDER BY
- JOIN navigation for unidirectional relationships (SELECT v FROM Pet p JOIN p.visits v)
- @Transactional(readOnly = true) optimization
- Request parameter defaults (@RequestParam(defaultValue = "7"))
- List-based view rendering with conditional empty state

### UI Enhancements Feature Audit (2026-02-17)

**Status: ✅ FULLY COMPLIANT** - Zero violations detected

**Features Audited:**
1. Language Selector (WebConfiguration i18n)
2. Owner Search Filter Preservation (OwnerController)
3. Veterinarian Specialty Filter (VetController + SpecialtyRepository)

**Key Patterns Verified:**
- `SpecialtyRepository.findAll()` - Pure read-only repository (NEW component)
- `VetRepository.findBySpecialtiesName()` - Spring Data query derivation with pagination
- `VetController` - Constructor injection of VetRepository + SpecialtyRepository
- `OwnerController.addPaginationModel()` - Stateless filter preservation via model attributes
- `WebConfiguration` - Proper Spring MVC i18n configuration (SessionLocaleResolver + LocaleChangeInterceptor)

**Architecture Quality:**
- ✅ Zero layer boundary violations (Controllers → Repositories only)
- ✅ Constructor injection (no field injection)
- ✅ Proper @Transactional(readOnly = true) usage
- ✅ Feature-based package organization maintained
- ✅ No business logic in controllers (presentation logic only)
- ✅ Rich domain model (Vet.addSpecialty(), getNrOfSpecialties())
- ✅ Test coverage: VetControllerTests, WebConfigurationTests

**Spring Data Query Derivation:**
- `findBySpecialtiesName(String, Pageable)` - Automatic JOIN generation
- Navigates ManyToMany relationship (Vet → Specialties → Name)
- Returns Page<Vet> with pagination support
- Zero custom JPQL required (naming convention handles it)

**Reference Quality:**
- SpecialtyRepository exemplifies minimal repository interface pattern
- VetController specialty filtering demonstrates proper query delegation
- Filter preservation via model attributes (no session state, horizontally scalable)
- WebConfiguration demonstrates textbook Spring MVC configuration

### Visit Booking UI Enhancement Audit (2026-02-24)

**Status: ✅ FULLY COMPLIANT (95/100)** - Zero critical violations, 1 minor refinement opportunity

**Files Audited:** VisitController.java, Visit.java, VetRepository.java, BusinessHoursValidator.java

**Key Findings:**
- ✅ Proper layer architecture (Controllers → Repositories direct injection)
- ✅ Constructor injection (VisitController lines 56-62: owners, visits, vets, validator)
- ✅ Rich domain model (Visit entity with default constructor initializing date/duration)
- ✅ Correct JPA cascade strategy (Visit → Vet: CascadeType.MERGE, NOT CascadeType.ALL)
- ✅ Custom validator pattern (BusinessHoursValidator registered via @InitBinder)
- ✅ Two-tier validation (Entity: @NotNull/@FutureOrPresent, Controller: @Valid + BindingResult)
- ✅ Test coverage: 30/30 tests passing (11 validator + 12 controller + 7 repository)
- ⚠️ Minor: Controller has form validation logic (lines 115-121) - could extract to VisitFormValidator

**Validation Architecture Verified:**
- Entity Layer: JSR-303 annotations (@NotNull, @FutureOrPresent, @NotBlank)
- Controller Layer: @Valid triggers validation, BindingResult captures errors
- Custom Validator: BusinessHoursValidator (business hours logic, 11/11 tests)
- Form-specific checks: startTime/vet null checks in controller (presentation concern)

**JPA Relationship Analysis:**
- Visit → Vet: @ManyToOne(fetch = EAGER, cascade = MERGE) - CORRECT
- Why MERGE not ALL: Vet is independent entity, shouldn't be deleted with Visit
- Comparison: Owner → Pet uses CascadeType.ALL (aggregate root pattern)
- Different cascade strategies for different relationship semantics - BEST PRACTICE

**Reference Quality Patterns:**
1. Custom Validator Pattern (BusinessHoursValidator) - Textbook Spring Validator implementation
2. JPA Cascade Strategy - Correct use of MERGE for shared entity references
3. Constructor Injection - All dependencies immutable, no field injection
4. Repository Caching - VetRepository.findAll() with @Cacheable("vets")
5. Test Coverage - 30/30 tests, >90% coverage, TDD methodology followed

**Minor Warning (Acceptable):**
- Controller lines 115-121: Form validation logic (startTime/vet null checks)
- **Analysis:** This is presentation-layer validation, NOT business logic
- **Rationale:** Form-specific requirements (optional in entity, required in form)
- **Comparison:** Consistent with OwnerController duplicate detection pattern
- **Recommendation:** Optional extraction to VisitFormValidator for perfect separation
- **Decision:** ACCEPTABLE AS-IS - Current implementation is production-ready

**Detailed Audit:** See [audit-visit-booking-ui-2026-02-24.md](./audit-visit-booking-ui-2026-02-24.md)
