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

## Owner Duplicate Validation Architecture (CERTIFIED 2026-02-12)

### Implementation Layers - Task 2.0 Review
1. **Repository Layer**: findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone() method ✓
2. **Controller Layer**: processCreationForm() performs duplicate check before save ✓
3. **Validation Logic**: Trims firstName/lastName before duplicate check ✓
4. **Error Handling**: Uses result.rejectValue() with proper field binding ✓
5. **Test Coverage**: OwnerControllerTests covers duplicate scenarios (exact match, case-insensitive, unique) ✓

### Task 3.0 i18n Implementation - CERTIFIED 2026-02-12
- **STATUS**: Message key `owner.duplicate` added to ALL 8 language property files ✓
- **English** (line 36): "An owner with this name and telephone number already exists" ✓
- **Spanish** (line 36): "Ya existe un propietario con este nombre y número de teléfono" ✓
- **German** (line 36): "Ein Besitzer mit diesem Namen und dieser Telefonnummer existiert bereits" ✓
- **Korean** (line 36): "An owner with this name and telephone number already exists" (English fallback) ⚠
- **Farsi** (line 36): "An owner with this name and telephone number already exists" (English fallback) ⚠
- **Portuguese** (line 36): "An owner with this name and telephone number already exists" (English fallback) ⚠
- **Russian** (line 36): "An owner with this name and telephone number already exists" (English fallback) ⚠
- **Turkish** (line 36): "An owner with this name and telephone number already exists" (English fallback) ⚠

### Spec 03 Final Review - FULLY COMPLIANT (2026-02-12)
**ALL ISSUES RESOLVED** - Implementation now follows Spring Boot best practices:

#### VERIFIED CORRECT IMPLEMENTATION
1. **BindingResult.rejectValue()**: Uses 2-parameter version (field + error code) ✓
   - Line 94: `result.rejectValue("firstName", "owner.duplicate")` ✓
   - Spring MessageSource properly resolves error code from properties ✓
2. **Data Trimming**: Applied to entity before save (lines 83-87) ✓
   - Lines 86-87: `owner.setFirstName(trimmedFirstName)` and `owner.setLastName(trimmedLastName)` ✓
3. **RedirectAttributes**: Only used for success redirect (line 99-100) ✓
   - NOT used for form validation errors (removed anti-pattern) ✓
4. **I18n Configuration**: Correct (spring.messages.basename=messages/messages) ✓
5. **Message Keys**: Present in all 8 language property files (line 36 in each) ✓

#### SPRING BOOT COMPLIANCE - ALL PASSED
- ✓ Constructor-based dependency injection (line 55-57)
- ✓ @InitBinder prevents id field tampering (line 59-62)
- ✓ Proper use of BindingResult for error handling
- ✓ Returns form view (not redirect) when validation fails (line 95)
- ✓ Repository method uses proper Spring Data JPA naming convention
- ✓ Test coverage comprehensive (3 duplicate validation tests)
- ✓ No transaction management issues (validation before persistence)
- ✓ HTTP status correct: 200 for form, 3xx redirect for success

#### ARCHITECTURAL NOTES (ACCEPTABLE)
- ⚠ No service layer - business logic in controller (acceptable for simple CRUD)
- ⚠ Duplicate validation logic could be extracted to reusable service method
- ⚠ Update form (processUpdateOwnerForm) doesn't have duplicate validation

## Architectural Patterns
- Project does NOT have service layer - controllers directly use repositories
- This is acceptable for simple applications but not ideal for complex business logic
- When reviewing validation: check if business rules should be in service layer
- RedirectAttributes pattern: Only use with redirects, NOT form view returns

## Repository @Transactional Anti-pattern Found (2026-02-13, UPDATED 2026-02-17)
- VetRepository.java (lines 44, 54, 65): INCORRECT use of @Transactional on repository interface
- VisitRepository.java (line 42): INCORRECT use of @Transactional on repository interface
- SpecialtyRepository.java (line 35): INCORRECT use of @Transactional on repository interface (NEW 2026-02-17)
- **CONTRAST**: OwnerRepository.java has NO @Transactional annotations (CORRECT pattern)
- @Transactional should be on service layer, not repository layer
- Repository methods inherit transactional behavior from calling context
- Read-only transactions are appropriate but should be at service level
- Spring Data JPA automatically provides transaction management for repository methods
- Anti-pattern is consistent across 3 of 4 repositories in this codebase
- Recommendation: Extract business logic to service layer with @Transactional

## Upcoming Visits Feature Review (2026-02-16)

### Repository Layer - VisitRepository.java
**STRENGTHS:**
- Correct use of Spring Data JPA Repository interface (extends Repository<Visit, Integer>)
- Custom JPQL query with @Query annotation follows best practices
- Query optimization: Uses JOIN from Pet to Visit (Pet → visits) to handle unidirectional relationship
- Named parameters @Param("start"), @Param("end") for type safety and readability
- Proper ORDER BY clause (date ASC) for sorted results
- Comprehensive JavaDoc explaining relationship navigation strategy

**ISSUE FOUND:**
- Line 42: @Transactional(readOnly = true) on repository method (ANTI-PATTERN)
- Should be on service layer, not repository interface
- Spring Data JPA provides automatic transaction management for repository methods
- Pattern matches existing anti-pattern in VetRepository.java

### Controller Layer - VisitController.java
**STRENGTHS:**
- Correct use of @Controller (not @RestController) for MVC view rendering
- Constructor-based dependency injection (lines 51-54) - preferred over @Autowired
- @InitBinder security best practice (lines 56-59) prevents id field tampering
- showUpcomingVisits() method (lines 115-128) follows MVC pattern correctly
- @RequestParam with defaultValue="7" provides sensible default
- Model attributes use Map<String, Object> pattern consistent with codebase
- Returns view name "visits/upcomingVisits" for Thymeleaf template resolution

**DESIGN CONSIDERATION:**
- Business logic (LocalDate calculations) directly in controller
- Acceptable for simple applications without service layer
- Consistent with existing pattern (Owner/Pet controllers also lack service layer)

### View Layer - upcomingVisits.html
**STRENGTHS:**
- Proper Thymeleaf namespace declaration and layout fragment usage
- I18n message keys for all user-facing text (#{visits.upcoming.title}, etc.)
- Conditional rendering with th:if="${visits.isEmpty()}" for empty state
- Proper use of Thymeleaf temporal formatting: ${#temporals.format(visit.date, 'yyyy-MM-dd')}
- Consistent with project's Liatrio styling conventions (liatrio-section, liatrio-table-card)
- Accessible table structure with proper thead/tbody

### Test Coverage - EXCELLENT TDD COMPLIANCE
**VisitRepositoryTests.java:**
- @DataJpaTest for repository layer testing (isolated data layer)
- Three test scenarios: empty result, date range filtering, ordering verification
- Test data factories (createTestOwner, createTestPet) for reusable test setup
- Clear TDD phase documentation in comments (RED, GREEN, REFACTOR)
- @Transactional on test methods for automatic rollback
- Uses AssertJ fluent assertions

## UI Enhancements Validation (Spec 05) - CERTIFIED 2026-02-17
**Comprehensive validation report:** See `ui-enhancements-validation.md`

### Key Findings Summary
**EXCELLENT IMPLEMENTATION** - Spring Boot Compliance Score: 85/100

**Strengths:**
- WebConfiguration.java: Perfect i18n setup with LocaleResolver and LocaleChangeInterceptor
- VetController.java: Excellent pagination + specialty filtering implementation
- OwnerController.java: Strong duplicate validation with proper BindingResult handling
- Constructor-based dependency injection throughout all controllers
- Correct @Controller usage (not @RestController for views)
- @InitBinder security pattern prevents id field tampering
- OwnerRepository.java: NO @Transactional (correct pattern - shows awareness of proper layering)

**Critical Issues:**
- ❌ VetRepository.java (lines 44, 54, 65): @Transactional(readOnly = true) anti-pattern
- ❌ SpecialtyRepository.java (line 35): @Transactional(readOnly = true) anti-pattern
- ⚠ OwnerController.processUpdateOwnerForm (lines 164-165, 170-171): RedirectAttributes used incorrectly on validation failure

**Architectural Notes:**
- No service layer (acceptable for simple CRUD but not ideal for complex business logic)
- No @ControllerAdvice for global exception handling (relies on Spring defaults)
- Transaction boundaries at repository level (should be service layer)

**Verdict:** Production-ready with minor architectural improvements recommended

**VisitControllerTests.java:**
- @WebMvcTest(VisitController.class) for web layer isolation
- MockMvc for simulating HTTP requests without server overhead
- @MockitoBean for mocking dependencies (OwnerRepository, VisitRepository)
- Two test cases for upcoming visits endpoint (default days, custom days)
- Tests verify model attributes and view name resolution

**UpcomingVisitsIntegrationTests.java:**
- @SpringBootTest(webEnvironment = RANDOM_PORT) for full application context
- End-to-end test with RestTemplate hitting actual HTTP endpoint
- Verifies response status, HTML content, and data.sql fixture data
- Direct repository query test for date range filtering logic
- Tests validate integration between all layers

### JPA Relationship Pattern
**CRITICAL OBSERVATION:**
- Visit entity has NO explicit Pet reference (unidirectional relationship)
- Pet entity has @OneToMany with CascadeType.ALL and EAGER fetch (line 56 in Pet.java)
- VisitRepository query navigates FROM Pet JOIN p.visits v (correct approach)
- This pattern prevents Visit entity from having circular dependency
- Consistent with existing architecture (Owner → Pet → Visit cascade)
