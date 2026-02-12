# Spring Boot Validator Memory

## Validation Patterns Observed

### Bean Validation Annotations
- Project uses Jakarta Bean Validation (jakarta.validation.constraints)
- Visit date validation uses message keys: `@NotNull(message = "{visit.date.required}")` and `@FutureOrPresent(message = "{visit.date.future}")`
- Owner entity uses message keys for i18n: `@Pattern(regexp = "\\d{10}", message = "{telephone.invalid}")`
- Message keys pattern is consistently applied across codebase

### Common Annotations Used
- `@NotBlank` - String fields requiring non-empty values (Person.firstName, Person.lastName, Visit.description)
- `@Pattern` - Regex validation with custom messages (Owner.telephone)
- `@FutureOrPresent` - Temporal validation for dates (Visit.date)
- `@NotNull` - Non-null field validation (Visit.date)

### Annotation Placement
- Validation annotations placed directly on entity fields
- Order: JPA annotations (@Column) → Formatting (@DateTimeFormat) → Validation (@NotNull, @FutureOrPresent)
- Consistent pattern across Owner, Person, and Visit entities

### Entity Validation Testing
- Validation tests in ValidatorTests.java using LocalValidatorFactoryBean
- Test pattern: Arrange (create entity) → Act (validator.validate()) → Assert (constraintViolations)
- Tests verify both valid and invalid scenarios (past, present, future, null)
- Multiple test cases for temporal validation

## Visit Date Validation Architecture (CERTIFIED 2026-02-12)

### Implementation Layers - VERIFIED COMPLIANT
1. **Entity Layer**: Visit.java with @NotNull and @FutureOrPresent using message keys ✓
2. **Controller Layer**: VisitController with @Valid and BindingResult for error handling ✓
3. **View Layer**: Thymeleaf fragments/inputField.html renders errors inline ✓
4. **I18n Layer**: All 8 languages (en, es, de, ko, fa, pt, tr, ru) with message keys ✓
5. **Test Layers**: ValidatorTests (unit), VisitControllerTests (web), E2E Playwright tests ✓

### Architecture Strengths
- Clean separation of validation concerns (entity → controller → view)
- Message keys enable internationalization with proper i18n structure
- Constructor-based dependency injection in VisitController (preferred over @Autowired)
- @InitBinder prevents id field tampering (security best practice)
- Comprehensive test coverage across all layers (>90% coverage achieved)
- Proper cascade relationships (Owner → Pet → Visit) handle transactional context

### Exception Handling Pattern (ACCEPTABLE)
- Project does NOT use @ControllerAdvice for form validation
- Uses Spring MVC default behavior: returns form view with BindingResult errors
- This is ACCEPTABLE and RECOMMENDED for simple form validation scenarios
- Thymeleaf th:errors automatically displays field-specific validation errors
- Pattern is consistent with Spring Boot best practices for MVC form handling

### Spring Boot Compliance
- PASSED: Bean Validation correctly configured (jakarta.validation.constraints)
- PASSED: Controller annotation usage (@Controller, not @RestController for views)
- PASSED: No transaction management antipatterns (validation happens before persistence)
- PASSED: Entity relationships properly configured (CascadeType.ALL for parent-child)
- PASSED: Security consideration (@InitBinder prevents id manipulation)
- PASSED: I18n message resolution working across all locales
- PASSED: Test coverage meets TDD requirements (unit + integration + E2E)

## Critical Issues to Watch

### Transaction Management
- Visit entity does NOT need @Transactional (it's an entity, not a service)
- Controller uses @Valid to trigger validation before persistence
- OwnerRepository.save() handles transactional context via cascade

### Entity Relationships
- Visit has no explicit relationship to Pet in entity (unidirectional from Pet → Visit)
- Pet.visits uses @OneToMany with CascadeType.ALL and EAGER fetch
- Owner.addVisit() method adds visit to pet collection (cascade saves it)

### Testing Coverage
- ValidatorTests covers Visit date validation comprehensively (past, today, future, null)
- VisitControllerTests validates controller behavior with date validation tests
- E2E Playwright tests validate browser behavior for past/present/future dates
- Tests follow TDD pattern with clear Arrange-Act-Assert structure

## Configuration Notes
- spring-boot-starter-validation dependency provides Hibernate Validator
- spring.messages.basename=messages/messages enables i18n
- spring.jpa.open-in-view=false follows best practice
- No custom validation configuration needed
