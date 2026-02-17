/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link OwnerController}
 *
 * @author Colin But
 * @author Wick Dynex
 */
@WebMvcTest(OwnerController.class)
@DisabledInNativeImage
@DisabledInAotMode
class OwnerControllerTests {

	private static final int TEST_OWNER_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	private Owner george() {
		Owner george = new Owner();
		george.setId(TEST_OWNER_ID);
		george.setFirstName("George");
		george.setLastName("Franklin");
		george.setAddress("110 W. Liberty St.");
		george.setCity("Madison");
		george.setTelephone("6085551023");
		Pet max = new Pet();
		PetType dog = new PetType();
		dog.setName("dog");
		max.setType(dog);
		max.setName("Max");
		max.setBirthDate(LocalDate.now());
		george.addPet(max);
		max.setId(1);
		return george;
	}

	@BeforeEach
	void setup() {

		Owner george = george();
		given(this.owners.findByLastNameStartingWith(eq("Franklin"), any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(george)));

		// Mock new multi-criteria search method for backward compatibility
		given(this.owners.findByLastNameStartingWithAndCityIgnoreCaseAndTelephone(eq("Franklin"), eq(""), eq(""),
				any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(george)));

		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(george));
		Visit visit = new Visit();
		visit.setDate(LocalDate.now());
		george.getPet("Max").getVisits().add(visit);

	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/new"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/new").param("firstName", "Joe")
				.param("lastName", "Bloggs")
				.param("address", "123 Caramel Street")
				.param("city", "London")
				.param("telephone", "1316761638"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs").param("city", "London"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", "address"))
			.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testInitFindForm() throws Exception {
		mockMvc.perform(get("/owners/find"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(view().name("owners/findOwners"));
	}

	@Test
	void testProcessFindFormSuccess() throws Exception {
		Page<Owner> tasks = new PageImpl<>(List.of(george(), new Owner()));
		when(this.owners.findByLastNameStartingWithAndCityIgnoreCaseAndTelephone(anyString(), anyString(), anyString(),
				any(Pageable.class)))
			.thenReturn(tasks);
		mockMvc.perform(get("/owners?page=1")).andExpect(status().isOk()).andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testProcessFindFormByLastName() throws Exception {
		Page<Owner> tasks = new PageImpl<>(List.of(george()));
		when(this.owners.findByLastNameStartingWithAndCityIgnoreCaseAndTelephone(eq("Franklin"), eq(""), eq(""),
				any(Pageable.class)))
			.thenReturn(tasks);
		mockMvc.perform(get("/owners?page=1").param("lastName", "Franklin"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessFindFormNoOwnersFound() throws Exception {
		Page<Owner> tasks = new PageImpl<>(List.of());
		when(this.owners.findByLastNameStartingWithAndCityIgnoreCaseAndTelephone(eq("Unknown Surname"), eq(""), eq(""),
				any(Pageable.class)))
			.thenReturn(tasks);
		mockMvc.perform(get("/owners?page=1").param("lastName", "Unknown Surname"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("owner", "lastName"))
			.andExpect(model().attributeHasFieldErrorCode("owner", "lastName", "notFound"))
			.andExpect(view().name("owners/findOwners"));

	}

	@Test
	void testInitUpdateOwnerForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/edit", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(model().attribute("owner", hasProperty("lastName", is("Franklin"))))
			.andExpect(model().attribute("owner", hasProperty("firstName", is("George"))))
			.andExpect(model().attribute("owner", hasProperty("address", is("110 W. Liberty St."))))
			.andExpect(model().attribute("owner", hasProperty("city", is("Madison"))))
			.andExpect(model().attribute("owner", hasProperty("telephone", is("6085551023"))))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateOwnerFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "Joe")
				.param("lastName", "Bloggs")
				.param("address", "123 Caramel Street")
				.param("city", "London")
				.param("telephone", "1616291589"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessUpdateOwnerFormUnchangedSuccess() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessUpdateOwnerFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "Joe")
				.param("lastName", "Bloggs")
				.param("address", "")
				.param("telephone", ""))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", "address"))
			.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testShowOwner() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attribute("owner", hasProperty("lastName", is("Franklin"))))
			.andExpect(model().attribute("owner", hasProperty("firstName", is("George"))))
			.andExpect(model().attribute("owner", hasProperty("address", is("110 W. Liberty St."))))
			.andExpect(model().attribute("owner", hasProperty("city", is("Madison"))))
			.andExpect(model().attribute("owner", hasProperty("telephone", is("6085551023"))))
			.andExpect(model().attribute("owner", hasProperty("pets", not(empty()))))
			.andExpect(model().attribute("owner",
					hasProperty("pets", hasItem(hasProperty("visits", hasSize(greaterThan(0)))))))
			.andExpect(view().name("owners/ownerDetails"));
	}

	@Test
	public void testProcessUpdateOwnerFormWithIdMismatch() throws Exception {
		int pathOwnerId = 1;

		Owner owner = new Owner();
		owner.setId(2);
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setAddress("Center Street");
		owner.setCity("New York");
		owner.setTelephone("0123456789");

		when(owners.findById(pathOwnerId)).thenReturn(Optional.of(owner));

		mockMvc.perform(MockMvcRequestBuilders.post("/owners/{ownerId}/edit", pathOwnerId).flashAttr("owner", owner))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/" + pathOwnerId + "/edit"))
			.andExpect(flash().attributeExists("error"));
	}

	@Test
	void testProcessCreationFormWithDuplicateOwner() throws Exception {
		// Arrange: Mock repository to return an existing owner when duplicate check is
		// called
		Owner existingOwner = new Owner();
		existingOwner.setId(99);
		existingOwner.setFirstName("John");
		existingOwner.setLastName("Smith");
		existingOwner.setAddress("456 Oak St");
		existingOwner.setCity("Springfield");
		existingOwner.setTelephone("5555551234");

		given(this.owners.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone("John", "Smith", "5555551234"))
			.willReturn(Optional.of(existingOwner));

		// Act & Assert: POST to /owners/new with duplicate owner data
		mockMvc
			.perform(post("/owners/new").param("firstName", "John")
				.param("lastName", "Smith")
				.param("address", "123 Main St")
				.param("city", "Boston")
				.param("telephone", "5555551234"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("owner", "firstName"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormWithUniqueOwner() throws Exception {
		// Arrange: Mock repository to return empty (no duplicate)
		given(this.owners.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone("Jane", "Doe", "5559998888"))
			.willReturn(Optional.empty());

		// Act & Assert: POST to /owners/new with unique owner data
		mockMvc
			.perform(post("/owners/new").param("firstName", "Jane")
				.param("lastName", "Doe")
				.param("address", "789 Elm St")
				.param("city", "Seattle")
				.param("telephone", "5559998888"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/" + null));
	}

	@Test
	void testProcessCreationFormDuplicateCaseInsensitive() throws Exception {
		// Arrange: Mock repository to return existing owner even with different case
		Owner existingOwner = new Owner();
		existingOwner.setId(100);
		existingOwner.setFirstName("John");
		existingOwner.setLastName("Smith");
		existingOwner.setAddress("456 Oak St");
		existingOwner.setCity("Springfield");
		existingOwner.setTelephone("5555551234");

		// Mock should return existing owner when called with trimmed lowercase names
		given(this.owners.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone("john", "smith", "5555551234"))
			.willReturn(Optional.of(existingOwner));

		// Act & Assert: POST with lowercase names - should detect duplicate
		mockMvc
			.perform(post("/owners/new").param("firstName", "john")
				.param("lastName", "smith")
				.param("address", "999 Different St")
				.param("city", "Austin")
				.param("telephone", "5555551234"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("owner", "firstName"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessFindFormPreservesLastNameInModel() throws Exception {
		// Arrange: Create multiple owners to trigger pagination view
		Owner owner1 = new Owner();
		owner1.setId(1);
		owner1.setFirstName("George");
		owner1.setLastName("Franklin");

		Owner owner2 = new Owner();
		owner2.setId(2);
		owner2.setFirstName("Betty");
		owner2.setLastName("Franklin");

		Page<Owner> tasks = new PageImpl<>(List.of(owner1, owner2));
		when(this.owners.findByLastNameStartingWithAndCityIgnoreCaseAndTelephone(eq("Franklin"), eq(""), eq(""),
				any(Pageable.class)))
			.thenReturn(tasks);

		// Act & Assert: lastName should be preserved in model for pagination links
		mockMvc.perform(get("/owners?page=1").param("lastName", "Franklin"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("lastName", "Franklin"))
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testProcessFindFormPreservesEmptyLastNameInModel() throws Exception {
		// Arrange: Create multiple owners for empty search (list all)
		Owner owner1 = new Owner();
		owner1.setId(1);
		owner1.setFirstName("George");
		owner1.setLastName("Franklin");

		Owner owner2 = new Owner();
		owner2.setId(2);
		owner2.setFirstName("Betty");
		owner2.setLastName("Davis");

		Page<Owner> tasks = new PageImpl<>(List.of(owner1, owner2));
		when(this.owners.findByLastNameStartingWithAndCityIgnoreCaseAndTelephone(eq(""), eq(""), eq(""),
				any(Pageable.class)))
			.thenReturn(tasks);

		// Act & Assert: Empty lastName should be preserved in model
		mockMvc.perform(get("/owners?page=1"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("lastName", ""))
			.andExpect(view().name("owners/ownersList"));
	}

	// ========================
	// Multi-Criteria Search Controller Tests (Task 2.0)
	// RED Phase: Tests 2.1-2.4
	// ========================

	@Test
	void shouldFindOwnersByMultipleCriteria() throws Exception {
		// Arrange: Mock repository to return owners matching all three criteria
		Owner owner1 = new Owner();
		owner1.setId(1);
		owner1.setFirstName("George");
		owner1.setLastName("Franklin");
		owner1.setCity("Madison");
		owner1.setTelephone("6085551023");

		Owner owner2 = new Owner();
		owner2.setId(2);
		owner2.setFirstName("Betty");
		owner2.setLastName("Franklin");
		owner2.setCity("Madison");
		owner2.setTelephone("6085551749");

		Page<Owner> results = new PageImpl<>(List.of(owner1, owner2));
		when(this.owners.findByLastNameStartingWithAndCityIgnoreCaseAndTelephone(eq("Franklin"), eq("Madison"), eq(""),
				any(Pageable.class)))
			.thenReturn(results);

		// Act & Assert: Search with lastName and city parameters
		mockMvc.perform(get("/owners?page=1").param("lastName", "Franklin").param("city", "Madison"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("listOwners", hasSize(2)))
			.andExpect(model().attribute("lastName", "Franklin"))
			.andExpect(model().attribute("city", "Madison"))
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	void shouldReturnNotFoundForNoResults() throws Exception {
		// Arrange: Mock repository to return empty results
		Page<Owner> emptyResults = new PageImpl<>(List.of());
		when(this.owners.findByLastNameStartingWithAndCityIgnoreCaseAndTelephone(eq("NonExistent"), eq("UnknownCity"),
				eq(""), any(Pageable.class)))
			.thenReturn(emptyResults);

		// Act & Assert: Search with criteria that return no results
		mockMvc
			.perform(get("/owners?page=1").param("lastName", "NonExistent")
				.param("city", "UnknownCity")
				.param("telephone", ""))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("owner", "lastName"))
			.andExpect(model().attributeHasFieldErrorCode("owner", "lastName", "notFound"))
			.andExpect(view().name("owners/findOwners"));
	}

	@Test
	void shouldRedirectWhenSingleOwnerFound() throws Exception {
		// Arrange: Mock repository to return single owner
		Owner owner = new Owner();
		owner.setId(5);
		owner.setFirstName("John");
		owner.setLastName("Smith");
		owner.setCity("Boston");
		owner.setTelephone("5551234567");

		Page<Owner> singleResult = new PageImpl<>(List.of(owner));
		when(this.owners.findByLastNameStartingWithAndCityIgnoreCaseAndTelephone(eq("Smith"), eq("Boston"),
				eq("5551234567"), any(Pageable.class)))
			.thenReturn(singleResult);

		// Act & Assert: Search that returns exactly one owner should redirect to
		// details
		mockMvc
			.perform(get("/owners?page=1").param("lastName", "Smith")
				.param("city", "Boston")
				.param("telephone", "5551234567"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/" + 5));
	}

	@Test
	void shouldShowPaginatedResultsForMultipleOwners() throws Exception {
		// Arrange: Mock repository to return multiple owners
		Owner owner1 = new Owner();
		owner1.setId(1);
		owner1.setFirstName("John");
		owner1.setLastName("Davis");
		owner1.setCity("Madison");
		owner1.setTelephone("6085551111");

		Owner owner2 = new Owner();
		owner2.setId(2);
		owner2.setFirstName("Jane");
		owner2.setLastName("Davis");
		owner2.setCity("Madison");
		owner2.setTelephone("6085552222");

		Owner owner3 = new Owner();
		owner3.setId(3);
		owner3.setFirstName("Bob");
		owner3.setLastName("Davis");
		owner3.setCity("Madison");
		owner3.setTelephone("6085553333");

		Page<Owner> multipleResults = new PageImpl<>(List.of(owner1, owner2, owner3));
		when(this.owners.findByLastNameStartingWithAndCityIgnoreCaseAndTelephone(eq("Davis"), eq("Madison"), eq(""),
				any(Pageable.class)))
			.thenReturn(multipleResults);

		// Act & Assert: Search with multiple results should show paginated list
		mockMvc
			.perform(get("/owners?page=1").param("lastName", "Davis").param("city", "Madison").param("telephone", ""))
			.andExpect(status().isOk())
			.andExpect(model().attribute("listOwners", hasSize(3)))
			.andExpect(model().attribute("lastName", "Davis"))
			.andExpect(model().attribute("city", "Madison"))
			.andExpect(model().attribute("telephone", ""))
			.andExpect(view().name("owners/ownersList"));
	}

	// ========================
	// CSV Export Controller Tests (Task 4.0)
	// RED Phase: Tests 4.1-4.3
	// ========================

	@Test
	void shouldExportCurrentPageToCSV() throws Exception {
		// Arrange: Mock repository to return owners for CSV export
		Owner owner1 = new Owner();
		owner1.setId(1);
		owner1.setFirstName("George");
		owner1.setLastName("Franklin");
		owner1.setCity("Madison");
		owner1.setTelephone("6085551023");
		owner1.setAddress("110 W. Liberty St.");

		Owner owner2 = new Owner();
		owner2.setId(2);
		owner2.setFirstName("Betty");
		owner2.setLastName("Davis");
		owner2.setCity("Boston");
		owner2.setTelephone("6085551749");
		owner2.setAddress("638 Cardinal Ave.");

		Page<Owner> results = new PageImpl<>(List.of(owner1, owner2));
		when(this.owners.findByLastNameStartingWithAndCityIgnoreCaseAndTelephone(eq(""), eq(""), eq(""),
				any(Pageable.class)))
			.thenReturn(results);

		// Act & Assert: Export endpoint should return CSV with correct headers and
		// content
		mockMvc.perform(get("/owners/export").param("page", "1"))
			.andExpect(status().isOk())
			.andExpect(header().string("Content-Type", "text/csv"))
			.andExpect(header().string("Content-Disposition", "attachment; filename=\"owners.csv\""))
			.andExpect(content()
				.string(org.hamcrest.Matchers.containsString("ID,First Name,Last Name,Address,City,Telephone")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("1,George,Franklin")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("2,Betty,Davis")));
	}

	@Test
	void shouldEscapeCSVSpecialCharacters() throws Exception {
		// Arrange: Create owners with CSV injection attack vectors
		Owner owner1 = new Owner();
		owner1.setId(1);
		owner1.setFirstName("=SUM(A1:A10)"); // Formula injection
		owner1.setLastName("Normal");
		owner1.setCity("+cmd|'/c calc'!A1"); // Command injection
		owner1.setTelephone("1234567890");
		owner1.setAddress("@IMPORTXML('https://evil.example.com')"); // XML injection

		Owner owner2 = new Owner();
		owner2.setId(2);
		owner2.setFirstName("-2+3+cmd|' /C calc'!A1"); // Minus injection
		owner2.setLastName("Test");
		owner2.setCity("Boston");
		owner2.setTelephone("6085551749");
		owner2.setAddress("Normal Address");

		Page<Owner> results = new PageImpl<>(List.of(owner1, owner2));
		when(this.owners.findByLastNameStartingWithAndCityIgnoreCaseAndTelephone(eq(""), eq(""), eq(""),
				any(Pageable.class)))
			.thenReturn(results);

		// Act & Assert: CSV should escape dangerous characters with single quote prefix
		mockMvc.perform(get("/owners/export").param("page", "1"))
			.andExpect(status().isOk())
			.andExpect(content().string(org.hamcrest.Matchers.containsString("'=SUM(A1:A10)"))) // Escaped
																								// =
			.andExpect(content().string(org.hamcrest.Matchers.containsString("'+cmd|'/c calc'!A1"))) // Escaped
																										// +
			.andExpect(content().string(org.hamcrest.Matchers.containsString("'@IMPORTXML"))) // Escaped
																								// @
			.andExpect(content().string(org.hamcrest.Matchers.containsString("'-2+3+cmd"))) // Escaped
																							// -
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Normal,"))); // Normal
																							// values
																							// not
																							// escaped
	}

	@Test
	void shouldReturnErrorWhenExportingEmptyResults() throws Exception {
		// Arrange: Mock repository to return empty results
		Page<Owner> emptyResults = new PageImpl<>(List.of());
		when(this.owners.findByLastNameStartingWithAndCityIgnoreCaseAndTelephone(eq(""), eq(""), eq(""),
				any(Pageable.class)))
			.thenReturn(emptyResults);

		// Act & Assert: Export with no results should return error or redirect
		mockMvc.perform(get("/owners/export").param("page", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/find"));
	}

}
