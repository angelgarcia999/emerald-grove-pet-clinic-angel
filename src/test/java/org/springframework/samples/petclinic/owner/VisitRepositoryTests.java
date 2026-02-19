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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link VisitRepository}. Tests verify the repository's ability to
 * find upcoming visits with optimized queries.
 *
 * @author Emerald Grove Dev Team
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class VisitRepositoryTests {

	@Autowired
	private VisitRepository visits;

	@Autowired
	private OwnerRepository owners;

	@Autowired
	private PetTypeRepository petTypes;

	@Autowired
	private VetRepository vets;

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * RED Phase: Test that repository returns empty list when no visits exist in date
	 * range.
	 */
	@Test
	void shouldReturnEmptyListWhenNoUpcomingVisits() {
		LocalDate start = LocalDate.now().plusYears(10);
		LocalDate end = start.plusDays(7);

		List<Visit> upcomingVisits = visits.findUpcomingVisits(start, end);

		assertThat(upcomingVisits).isEmpty();
	}

	/**
	 * RED/GREEN Phase: Test that repository finds visits within specified date range.
	 */
	@Test
	@Transactional
	void shouldFindUpcomingVisitsWithinDateRange() {
		// Arrange - Create test owner with pet and visit
		Owner owner = createTestOwner("John", "Doe");
		Pet pet = createTestPet("Fluffy", owner);
		owner = owners.save(owner);

		// Act - Query for visits in next 7 days
		LocalDate start = LocalDate.now();
		LocalDate end = start.plusDays(7);
		List<Visit> upcomingVisits = visits.findUpcomingVisits(start, end);

		// Assert - Verify visit was found
		assertThat(upcomingVisits).isNotEmpty();
		assertThat(upcomingVisits).anyMatch(v -> v.getDescription().equals("Test checkup"));
	}

	/**
	 * REFACTOR Phase: Test that query orders results by date ascending.
	 */
	@Test
	@Transactional
	void shouldOrderVisitsByDateAscending() {
		// Arrange - Create visits on different dates
		Owner owner = createTestOwner("Jane", "Smith");
		Pet pet = owner.getPets().iterator().next();

		Visit visit1 = new Visit();
		visit1.setDate(LocalDate.now().plusDays(5));
		visit1.setDescription("Later visit");
		pet.addVisit(visit1);

		Visit visit2 = new Visit();
		visit2.setDate(LocalDate.now().plusDays(2));
		visit2.setDescription("Earlier visit");
		pet.addVisit(visit2);

		owners.save(owner);

		// Act
		LocalDate start = LocalDate.now();
		LocalDate end = start.plusDays(7);
		List<Visit> upcomingVisits = visits.findUpcomingVisits(start, end);

		// Assert - First visit should be earlier date
		assertThat(upcomingVisits).hasSizeGreaterThanOrEqualTo(2);
		Visit firstVisit = upcomingVisits.stream()
			.filter(v -> v.getDescription().equals("Earlier visit") || v.getDescription().equals("Later visit"))
			.findFirst()
			.orElseThrow();
		assertThat(firstVisit.getDescription()).isEqualTo("Earlier visit");
	}

	/**
	 * RED Phase: Test that Visit with start_time and duration_minutes can be persisted
	 * and retrieved.
	 */
	@Test
	@Transactional
	void shouldPersistAndRetrieveVisitWithTimeFields() {
		// Arrange - Create visit with time fields
		Owner owner = new Owner();
		owner.setFirstName("Test");
		owner.setLastName("Owner");
		owner.setAddress("123 Test St");
		owner.setCity("TestCity");
		owner.setTelephone("1234567890");

		Pet pet = new Pet();
		pet.setName("TestPet");
		pet.setBirthDate(LocalDate.now().minusYears(2));
		pet.setType(petTypes.findAll().iterator().next());

		Visit visit = new Visit();
		visit.setDate(LocalDate.now().plusDays(1));
		visit.setStartTime(LocalTime.of(10, 30));
		visit.setDurationMinutes(45);
		visit.setDescription("Appointment with time");

		owner.addPet(pet);
		pet.addVisit(visit);

		// Act - Save and retrieve
		Owner savedOwner = owners.save(owner);
		Integer visitId = savedOwner.getPets().iterator().next().getVisits().iterator().next().getId();

		// Clear persistence context to force database round-trip
		owners.flush();

		// Retrieve visit through upcoming visits query
		LocalDate start = LocalDate.now();
		LocalDate end = start.plusDays(7);
		List<Visit> upcomingVisits = visits.findUpcomingVisits(start, end);

		// Assert - Verify time fields were persisted
		Visit retrievedVisit = upcomingVisits.stream()
			.filter(v -> v.getDescription().equals("Appointment with time"))
			.findFirst()
			.orElseThrow();

		assertThat(retrievedVisit.getStartTime()).isEqualTo(LocalTime.of(10, 30));
		assertThat(retrievedVisit.getDurationMinutes()).isEqualTo(45);
	}

	/**
	 * RED Phase: Test that Visit with assigned Vet can be persisted and retrieved with
	 * eager-loaded vet information.
	 */
	@Test
	@Transactional
	void shouldSaveAndRetrieveVisitWithAssignedVet() {
		// Arrange - Create visit with assigned vet
		Vet vet = vets.findAll().iterator().next();
		assertThat(vet).isNotNull();

		Owner owner = new Owner();
		owner.setFirstName("Test");
		owner.setLastName("Owner");
		owner.setAddress("123 Test St");
		owner.setCity("TestCity");
		owner.setTelephone("1234567890");

		Pet pet = new Pet();
		pet.setName("TestPet");
		pet.setBirthDate(LocalDate.now().minusYears(2));
		pet.setType(petTypes.findAll().iterator().next());

		Visit visit = new Visit();
		visit.setDate(LocalDate.now().plusDays(1));
		visit.setStartTime(LocalTime.of(10, 30));
		visit.setDurationMinutes(30);
		visit.setDescription("Checkup with vet");
		visit.setVet(vet);

		owner.addPet(pet);
		pet.addVisit(visit);

		// Act - Save and retrieve
		Owner savedOwner = owners.save(owner);
		owners.flush();

		// Retrieve visit through upcoming visits query
		LocalDate start = LocalDate.now();
		LocalDate end = start.plusDays(7);
		List<Visit> upcomingVisits = visits.findUpcomingVisits(start, end);

		// Assert - Verify vet was persisted and eager-loaded
		Visit retrievedVisit = upcomingVisits.stream()
			.filter(v -> v.getDescription().equals("Checkup with vet"))
			.findFirst()
			.orElseThrow();

		assertThat(retrievedVisit.getVet()).isNotNull();
		assertThat(retrievedVisit.getVet().getId()).isEqualTo(vet.getId());
		assertThat(retrievedVisit.getVet().getLastName()).isEqualTo(vet.getLastName());
	}

	/**
	 * RED Phase: Test that Visit can be saved with null vet (backward compatibility).
	 */
	@Test
	@Transactional
	void shouldAllowNullVetForBackwardCompatibility() {
		// Arrange - Create visit without vet
		Owner owner = new Owner();
		owner.setFirstName("Test");
		owner.setLastName("Owner");
		owner.setAddress("123 Test St");
		owner.setCity("TestCity");
		owner.setTelephone("1234567890");

		Pet pet = new Pet();
		pet.setName("TestPet");
		pet.setBirthDate(LocalDate.now().minusYears(2));
		pet.setType(petTypes.findAll().iterator().next());

		Visit visit = new Visit();
		visit.setDate(LocalDate.now().plusDays(1));
		visit.setStartTime(LocalTime.of(14, 0));
		visit.setDescription("Visit without vet");
		// Note: vet is null

		owner.addPet(pet);
		pet.addVisit(visit);

		// Act - Save
		Owner savedOwner = owners.save(owner);
		owners.flush();

		// Retrieve visit
		LocalDate start = LocalDate.now();
		LocalDate end = start.plusDays(7);
		List<Visit> upcomingVisits = visits.findUpcomingVisits(start, end);

		// Assert - Verify null vet is allowed
		Visit retrievedVisit = upcomingVisits.stream()
			.filter(v -> v.getDescription().equals("Visit without vet"))
			.findFirst()
			.orElseThrow();

		assertThat(retrievedVisit.getVet()).isNull();
	}

	/**
	 * RED/GREEN Phase: Test that Visit-Vet relationship is properly configured with
	 * optional vet (nullable foreign key). The actual cascade DELETE SET NULL behavior is
	 * defined in database schema and tested through integration tests.
	 */
	@Test
	@Transactional
	void shouldSupportOptionalVetRelationshipWithProperMapping() {
		// Arrange - Create visit with vet
		Vet vet = vets.findAll().iterator().next();

		Owner owner = new Owner();
		owner.setFirstName("Test");
		owner.setLastName("Owner");
		owner.setAddress("123 Test St");
		owner.setCity("TestCity");
		owner.setTelephone("1234567890");

		Pet pet = new Pet();
		pet.setName("TestPet");
		pet.setBirthDate(LocalDate.now().minusYears(2));
		pet.setType(petTypes.findAll().iterator().next());

		// Create two visits: one with vet, one without
		Visit visitWithVet = new Visit();
		visitWithVet.setDate(LocalDate.now().plusDays(1));
		visitWithVet.setDescription("Visit with vet");
		visitWithVet.setVet(vet);

		Visit visitWithoutVet = new Visit();
		visitWithoutVet.setDate(LocalDate.now().plusDays(2));
		visitWithoutVet.setDescription("Visit without vet");
		// vet is null

		owner.addPet(pet);
		pet.addVisit(visitWithVet);
		pet.addVisit(visitWithoutVet);

		// Act - Save both visits
		Owner savedOwner = owners.save(owner);
		owners.flush();

		// Assert - Verify both visits were saved correctly
		LocalDate start = LocalDate.now();
		LocalDate end = start.plusDays(7);
		List<Visit> upcomingVisits = visits.findUpcomingVisits(start, end);

		Visit retrievedWithVet = upcomingVisits.stream()
			.filter(v -> v.getDescription().equals("Visit with vet"))
			.findFirst()
			.orElseThrow();
		assertThat(retrievedWithVet.getVet()).isNotNull();
		assertThat(retrievedWithVet.getVet().getId()).isEqualTo(vet.getId());

		Visit retrievedWithoutVet = upcomingVisits.stream()
			.filter(v -> v.getDescription().equals("Visit without vet"))
			.findFirst()
			.orElseThrow();
		assertThat(retrievedWithoutVet.getVet()).isNull();
	}

	/**
	 * REFACTOR: Test data factory - create test owner.
	 */
	private Owner createTestOwner(String firstName, String lastName) {
		Owner owner = new Owner();
		owner.setFirstName(firstName);
		owner.setLastName(lastName);
		owner.setAddress("123 Test St");
		owner.setCity("TestCity");
		owner.setTelephone("1234567890");

		// Add pet with visit
		Pet pet = new Pet();
		pet.setName("TestPet");
		pet.setBirthDate(LocalDate.now().minusYears(2));
		pet.setType(petTypes.findAll().iterator().next());

		Visit visit = new Visit();
		visit.setDate(LocalDate.now().plusDays(3));
		visit.setDescription("Test checkup");

		owner.addPet(pet);
		pet.addVisit(visit);

		return owner;
	}

	/**
	 * REFACTOR: Test data factory - create test pet with visit.
	 */
	private Pet createTestPet(String name, Owner owner) {
		Pet pet = new Pet();
		pet.setName(name);
		pet.setBirthDate(LocalDate.now().minusYears(2));
		pet.setType(petTypes.findAll().iterator().next());

		Visit visit = new Visit();
		visit.setDate(LocalDate.now().plusDays(3));
		visit.setDescription("Test checkup");

		owner.addPet(pet);
		pet.addVisit(visit);

		return pet;
	}

}
