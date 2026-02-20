# 05-tasks-ui-enhancements.md

## Relevant Files

### Language Selector (Task 1.0)
- `src/main/java/org/springframework/samples/petclinic/system/WebConfiguration.java` - Add LocaleResolver and LocaleChangeInterceptor beans
- `src/main/resources/templates/fragments/layout.html` - Add language selector dropdown to navbar
- `src/main/resources/messages/messages.properties` - Add language selector labels (repeat for all 8 language files)
- `src/main/resources/messages/messages_de.properties` - German translations
- `src/main/resources/messages/messages_es.properties` - Spanish translations
- `src/main/resources/messages/messages_fa.properties` - Farsi translations
- `src/main/resources/messages/messages_ko.properties` - Korean translations
- `src/main/resources/messages/messages_pt.properties` - Portuguese translations
- `src/main/resources/messages/messages_ru.properties` - Russian translations
- `src/main/resources/messages/messages_tr.properties` - Turkish translations
- `src/test/java/org/springframework/samples/petclinic/system/WebConfigurationTests.java` - Unit tests for locale configuration
- `e2e-tests/tests/features/language-selector.spec.ts` - E2E tests for language switching

### Filter Preservation (Task 2.0)
- `src/main/resources/templates/owners/ownersList.html` - Update pagination links to include lastName parameter
- `src/test/java/org/springframework/samples/petclinic/owner/OwnerControllerTests.java` - Add tests for filter preservation
- `src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java` - Ensure lastName is added to model for template access

### Specialty Filter (Task 3.0)
- `src/main/java/org/springframework/samples/petclinic/vet/VetRepository.java` - Add specialty filtering query method
- `src/main/java/org/springframework/samples/petclinic/vet/VetController.java` - Add specialty parameter and filtering logic
- `src/main/resources/templates/vets/vetList.html` - Add specialty dropdown and update pagination links
- `src/test/java/org/springframework/samples/petclinic/vet/VetControllerTests.java` - Add specialty filtering tests
- `src/test/java/org/springframework/samples/petclinic/vet/VetRepositoryTests.java` - Test specialty query (create if doesn't exist)
- `src/main/resources/messages/messages*.properties` - Add specialty filter labels (all 8 language files)

### E2E Testing (Task 4.0)
- `e2e-tests/tests/features/language-selector.spec.ts` - Language switching E2E tests
- `e2e-tests/tests/features/owner-management.spec.ts` - Update with filter preservation tests
- `e2e-tests/tests/features/vet-directory.spec.ts` - Specialty filter E2E tests (create if doesn't exist)

### Notes

- All tests must follow **TDD RED-GREEN-REFACTOR** cycle as mandated by CLAUDE.md
- Unit tests should be placed alongside the code they test
- Run tests with `./mvnw test` for unit tests and `cd e2e-tests && npm test` for E2E tests
- Follow existing Spring Boot patterns (controller structure, repository methods, Thymeleaf templates)
- Use `@WebMvcTest` for controller tests and `@DataJpaTest` for repository tests
- Maintain minimum 90% line coverage for new code
- Run validation agents after implementation: tdd-enforcer, spring-boot-validator, architecture-compliance-checker, i18n-sync-validator

## Tasks

### [ ] 1.0 Implement Language Selector in Header

**Description:** Add a language selector dropdown to the top-right corner of the header navbar that allows users to switch between all 8 supported languages (EN, DE, ES, FA, KO, PT, RU, TR) with session persistence.

#### 1.0 Proof Artifact(s)

- Screenshot: Language dropdown visible in header navbar demonstrates UI component exists
- Screenshot: Application displayed in Spanish after selecting "Español" demonstrates language switching works
- Screenshot: Application displayed in German after selecting "Deutsch" demonstrates multiple languages supported
- Test: `WebConfigurationTests` passes demonstrates locale configuration works
- URL: Application reloaded after language change maintains selected language demonstrates session persistence

#### 1.0 Tasks

- [ ] 1.1 **RED**: Write failing test in `WebConfigurationTests.java` for LocaleResolver bean existence
- [ ] 1.2 **GREEN**: Add `SessionLocaleResolver` bean to `WebConfiguration.java`
- [ ] 1.3 **RED**: Write failing test for LocaleChangeInterceptor registration
- [ ] 1.4 **GREEN**: Add `LocaleChangeInterceptor` bean and register with interceptor registry in `WebConfiguration.java`
- [ ] 1.5 **REFACTOR**: Review and optimize configuration code
- [ ] 1.6 Add i18n message keys for language selector to all 8 `messages*.properties` files:
  - `language.selector.label` (e.g., "Language", "Idioma", "Sprache")
  - `language.en` (English in each language)
  - `language.de` (German in each language)
  - `language.es` (Spanish in each language)
  - `language.fa` (Farsi in each language)
  - `language.ko` (Korean in each language)
  - `language.pt` (Portuguese in each language)
  - `language.ru` (Russian in each language)
  - `language.tr` (Turkish in each language)
- [ ] 1.7 Add language selector dropdown to `layout.html` navbar (top-right position):
  - Dropdown with language names in native text
  - Link each language to `?lang=xx` parameter
  - Style consistently with existing navbar elements
- [ ] 1.8 Test language switching manually in browser (verify session persistence)
- [ ] 1.9 Run `i18n-sync-validator` agent to verify all language files are synchronized
- [ ] 1.10 Capture proof artifacts (screenshots in multiple languages, URL showing persistence)

---

### [ ] 2.0 Preserve Search Filters Across Pagination

**Description:** Maintain the lastName search parameter when users navigate between pages of owner search results by including the filter in pagination URLs.

#### 2.0 Proof Artifact(s)

- URL: `/owners?lastName=Franklin&page=1` displays first page of filtered results demonstrates filter in URL
- URL: `/owners?lastName=Franklin&page=2` displays second page with same filter demonstrates filter preservation
- Screenshot: Pagination links include lastName parameter demonstrates template generates correct URLs
- Test: `OwnerControllerTests` with pagination and filters passes demonstrates backend implementation
- Manual test: Navigate through multiple pages without losing search context demonstrates user experience

#### 2.0 Tasks

- [ ] 2.1 **RED**: Write failing test in `OwnerControllerTests.java` for pagination with lastName filter preserved in model
- [ ] 2.2 **GREEN**: Update `OwnerController.processFindForm()` to add lastName to model attributes
- [ ] 2.3 **RED**: Write failing test verifying pagination links include lastName parameter
- [ ] 2.4 **GREEN**: Update `ownersList.html` pagination links to include `lastName` parameter:
  - Modify `th:href` expressions to append `&lastName=${owner.lastName}` (or retrieve from model)
  - Ensure all pagination links (previous, next, page numbers) include the filter
- [ ] 2.5 **REFACTOR**: Extract pagination URL generation logic if needed for clarity
- [ ] 2.6 Test manually: search for "Franklin", navigate to page 2, verify filter persists
- [ ] 2.7 Verify backward compatibility (pagination without filter still works)
- [ ] 2.8 Capture proof artifacts (URLs showing filter preservation, screenshot of pagination links)

---

### [ ] 3.0 Implement Veterinarian Specialty Filter

**Description:** Add a specialty filter dropdown to the veterinarian directory page that filters vets by their medical specialty and preserves the filter across pagination.

#### 3.0 Proof Artifact(s)

- Screenshot: Specialty dropdown visible at top of vet directory demonstrates filter UI exists
- URL: `/vets.html?specialty=radiology&page=1` shows filtered results demonstrates specialty filtering works
- Screenshot: "No veterinarians found with this specialty" message demonstrates edge case handling
- Screenshot: Specialty filter maintained after pagination demonstrates filter persistence
- Test: `VetControllerTests` with specialty filtering passes demonstrates backend implementation
- Test: `VetRepositoryTests` specialty query test passes demonstrates data layer works correctly

#### 3.0 Tasks

- [ ] 3.1 **RED**: Write failing test in `VetRepositoryTests.java` (create file if needed) for `findBySpecialtiesName()` method
- [ ] 3.2 **GREEN**: Add `findBySpecialtiesName(String specialtyName, Pageable pageable)` method to `VetRepository.java`
  - Use Spring Data JPA query method or `@Query` annotation
  - Handle specialty as a collection join (vets have multiple specialties)
- [ ] 3.3 **REFACTOR**: Verify query works across H2, MySQL, PostgreSQL
- [ ] 3.4 **RED**: Write failing test in `VetControllerTests.java` for specialty filter parameter handling
- [ ] 3.5 **GREEN**: Update `VetController.showVetList()` to accept `@RequestParam(required = false) String specialty` parameter
- [ ] 3.6 **GREEN**: Add filtering logic in `VetController`:
  - If specialty is null or empty, use `findAll(pageable)` (existing behavior)
  - If specialty is provided, use `findBySpecialtiesName(specialty, pageable)`
  - Add specialty parameter to model for template access
- [ ] 3.7 **RED**: Write test for "no results" edge case when specialty has no matching vets
- [ ] 3.8 **GREEN**: Handle empty results gracefully (existing pagination logic should work)
- [ ] 3.9 Add i18n message keys for specialty filter to all 8 `messages*.properties` files:
  - `specialty.filter.label` (e.g., "Filter by Specialty")
  - `specialty.filter.all` (e.g., "All Specialties")
  - `specialty.filter.none` (e.g., "No veterinarians found with this specialty")
- [ ] 3.10 Update `vetList.html` template:
  - Add specialty filter dropdown above vet list
  - Populate dropdown with all specialties from database
  - Set selected value based on current filter
  - Update pagination links to include `specialty` parameter
  - Display "no results" message when specialty filter returns empty
- [ ] 3.11 **REFACTOR**: Review code for clarity and consistency with owner search patterns
- [ ] 3.12 Test manually: filter by specialty, paginate, verify filter persists
- [ ] 3.13 Run `i18n-sync-validator` agent to verify message keys across all languages
- [ ] 3.14 Capture proof artifacts (screenshots of filter, URLs with specialty param, no-results message)

---

### [ ] 4.0 E2E Testing and Validation

**Description:** Create comprehensive Playwright end-to-end tests for all three features and validate against the specification requirements.

#### 4.0 Proof Artifact(s)

- Test: Playwright tests for language switching pass demonstrates E2E language selector works
- Test: Playwright tests for filter preservation pass demonstrates E2E pagination works
- Test: Playwright tests for specialty filtering pass demonstrates E2E vet filter works
- Report: All validation agents pass (tdd-enforcer, spring-boot-validator, architecture-compliance-checker) demonstrates quality gates met
- Report: Test coverage >90% for new code demonstrates TDD compliance

#### 4.0 Tasks

- [ ] 4.1 Create `e2e-tests/tests/features/language-selector.spec.ts`:
  - Test: Navigate to home page and verify language selector exists
  - Test: Select Spanish, verify UI text changes to Spanish
  - Test: Select German, verify UI text changes to German
  - Test: Reload page, verify language persists in session
  - Test: Switch back to English, verify UI returns to English
- [ ] 4.2 Update `e2e-tests/tests/features/owner-management.spec.ts` (or create filter-preservation.spec.ts):
  - Test: Search for owners by lastName
  - Test: Navigate to page 2, verify lastName filter in URL
  - Test: Verify search results remain consistent across pages
  - Test: Verify lastName visible in pagination links
- [ ] 4.3 Create `e2e-tests/tests/features/vet-directory.spec.ts`:
  - Test: Navigate to vet directory and verify specialty dropdown exists
  - Test: Select a specialty, verify filtered results display
  - Test: Verify specialty filter parameter in URL
  - Test: Navigate to page 2 with specialty filter, verify filter persists
  - Test: Select "All Specialties", verify all vets displayed
  - Test: Select specialty with no vets, verify "no results" message
- [ ] 4.4 Run all E2E tests: `cd e2e-tests && npm test`
- [ ] 4.5 Run all unit tests: `./mvnw test`
- [ ] 4.6 Generate test coverage report: `./mvnw jacoco:report`
- [ ] 4.7 Verify >90% line coverage for new code
- [ ] 4.8 Run `tdd-enforcer` agent to verify TDD compliance (RED-GREEN-REFACTOR followed)
- [ ] 4.9 Run `spring-boot-validator` agent to verify Spring Boot best practices
- [ ] 4.10 Run `architecture-compliance-checker` agent to verify layered architecture compliance
- [ ] 4.11 Run `i18n-sync-validator` agent to verify all message keys synchronized
- [ ] 4.12 Fix any issues identified by validation agents
- [ ] 4.13 Capture proof artifacts (test reports, coverage reports, agent validation summaries)
- [ ] 4.14 Document any known issues or limitations in proof artifacts
