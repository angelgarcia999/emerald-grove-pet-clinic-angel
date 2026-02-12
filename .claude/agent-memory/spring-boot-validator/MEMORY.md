# Spring Boot Validator Memory

## Validation Patterns Observed

### Bean Validation Annotations
- Project uses Jakarta Bean Validation (jakarta.validation.constraints)
- Custom messages use inline strings: `@FutureOrPresent(message = "Visit date cannot be in the past")`
- Owner entity uses message keys for i18n: `@Pattern(regexp = "\\d{10}", message = "{telephone.invalid}")`
- Both patterns are valid, but message keys preferred for consistency

### Common Annotations Used
- `@NotBlank` - String fields requiring non-empty values (Person.firstName, Person.lastName, Visit.description)
- `@Pattern` - Regex validation with custom messages (Owner.telephone)
- `@FutureOrPresent` - Temporal validation for dates (Visit.date)

### Annotation Placement
- Validation annotations placed directly on entity fields
- Order: JPA annotations (@Column) → Formatting (@DateTimeFormat) → Validation (@NotBlank, @FutureOrPresent)
- Consistent pattern across Owner, Person, and Visit entities

### Entity Validation Testing
- Validation tests in ValidatorTests.java using LocalValidatorFactoryBean
- Test pattern: Arrange (create entity) → Act (validator.validate()) → Assert (constraintViolations)
- Tests verify both valid and invalid scenarios
- Multiple test cases for temporal validation (past, present, future)

## Critical Issues to Watch

### Transaction Management
- Visit entity does NOT need @Transactional (it's an entity, not a service)
- Controller uses @Valid to trigger validation before persistence
- OwnerRepository.save() handles transactional context

### Entity Relationships
- Visit has no explicit relationship to Pet in entity (unidirectional from Pet → Visit)
- Pet.visits uses @OneToMany with CascadeType.ALL and EAGER fetch
- Owner.addVisit() method adds visit to pet collection (cascade saves it)

### Testing Coverage
- ValidatorTests covers Visit date validation scenarios comprehensively
- VisitControllerTests validates controller behavior but doesn't test date validation edge cases
- Tests follow TDD pattern with clear Arrange-Act-Assert structure
