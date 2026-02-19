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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * Test class for {@link VisitController}
 *
 * @author Colin But
 * @author Wick Dynex
 */
@WebMvcTest(VisitController.class)
@DisabledInNativeImage
@DisabledInAotMode
class VisitControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	@MockitoBean
	private VisitRepository visits;

	@MockitoBean
	private VetRepository vets;

	@BeforeEach
	void init() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		owner.addPet(pet);
		pet.setId(TEST_PET_ID);
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(owner));
	}

	@Test
	void testInitNewVisitForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void testProcessNewVisitFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "George")
				.param("description", "Visit Description")
				.param("startTime", "10:00")
				.param("vet.id", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessNewVisitFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID).param("name",
					"George"))
			.andExpect(model().attributeHasErrors("visit"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void testProcessNewVisitFormWithPastDate() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", "2020-01-01")
				.param("description", "Past visit"))
			.andExpect(model().attributeHasErrors("visit"))
			.andExpect(model().attributeHasFieldErrors("visit", "date"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void testProcessNewVisitFormWithTodayDate() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().toString())
				.param("description", "Today's visit")
				.param("startTime", "14:00")
				.param("vet.id", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessNewVisitFormWithFutureDate() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().plusDays(7).toString())
				.param("description", "Future visit")
				.param("startTime", "09:30")
				.param("vet.id", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testShowUpcomingVisitsWithDefaultDays() throws Exception {
		given(this.visits.findUpcomingVisits(any(LocalDate.class), any(LocalDate.class)))
			.willReturn(java.util.Collections.emptyList());

		mockMvc.perform(get("/visits/upcoming"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("visits"))
			.andExpect(model().attributeExists("days"))
			.andExpect(view().name("visits/upcomingVisits"));
	}

	@Test
	void testShowUpcomingVisitsWithCustomDays() throws Exception {
		given(this.visits.findUpcomingVisits(any(LocalDate.class), any(LocalDate.class)))
			.willReturn(java.util.Collections.emptyList());

		mockMvc.perform(get("/visits/upcoming").param("days", "14"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("days", 14))
			.andExpect(view().name("visits/upcomingVisits"));
	}

	/**
	 * RED Phase: Test that GET request includes list of available vets in model
	 */
	@Test
	void testInitNewVisitFormIncludesVets() throws Exception {
		Vet vet1 = new Vet();
		vet1.setId(1);
		vet1.setFirstName("James");
		vet1.setLastName("Carter");

		Vet vet2 = new Vet();
		vet2.setId(2);
		vet2.setFirstName("Helen");
		vet2.setLastName("Leary");

		Collection<Vet> vetsList = Arrays.asList(vet1, vet2);
		given(this.vets.findAll()).willReturn(vetsList);

		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("vets"))
			.andExpect(model().attribute("vets", vetsList))
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	/**
	 * RED Phase: Test that POST request with time and vet saves Visit with both fields
	 */
	@Test
	void testProcessNewVisitFormWithTimeAndVet() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().plusDays(1).toString())
				.param("description", "Checkup")
				.param("startTime", "10:30")
				.param("vet.id", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	/**
	 * RED Phase: Test that POST request missing time returns validation error
	 */
	@Test
	void testProcessNewVisitFormMissingTime() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().plusDays(1).toString())
				.param("description", "Checkup")
				.param("vet.id", "1"))
			.andExpect(model().attributeHasErrors("visit"))
			.andExpect(model().attributeHasFieldErrors("visit", "startTime"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	/**
	 * RED Phase: Test that POST request missing vet returns validation error
	 */
	@Test
	void testProcessNewVisitFormMissingVet() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().plusDays(1).toString())
				.param("description", "Checkup")
				.param("startTime", "10:30"))
			.andExpect(model().attributeHasErrors("visit"))
			.andExpect(model().attributeHasFieldErrors("visit", "vet"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

}
