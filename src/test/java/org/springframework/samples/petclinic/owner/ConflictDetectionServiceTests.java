package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.vet.Vet;

/**
 * Test class for {@link ConflictDetectionService}
 */
@ExtendWith(MockitoExtension.class)
class ConflictDetectionServiceTests {

	@Mock
	private VisitRepository visitRepository;

	private ConflictDetectionService service;

	private Vet testVet;

	private LocalDate testDate;

	@BeforeEach
	void setup() {
		service = new ConflictDetectionService(visitRepository);

		testVet = new Vet();
		testVet.setId(1);
		testVet.setFirstName("James");
		testVet.setLastName("Carter");

		testDate = LocalDate.now().plusDays(1);
	}

	@Test
	void shouldDetectVetConflictWhenAppointmentsOverlap() {
		// Existing appointment: 9:00-9:30
		Visit existing = createVisit(testDate, LocalTime.of(9, 0), 30, testVet);

		// New appointment: 9:15-9:45 (overlaps)
		Visit newVisit = createVisit(testDate, LocalTime.of(9, 15), 30, testVet);

		given(visitRepository.findByVetAndDate(testVet, testDate)).willReturn(Collections.singletonList(existing));

		assertThat(service.hasVetConflict(newVisit)).isTrue();
	}

	@Test
	void shouldNotDetectVetConflictWhenAppointmentsDoNotOverlap() {
		// Existing appointment: 9:00-9:30
		Visit existing = createVisit(testDate, LocalTime.of(9, 0), 30, testVet);

		// New appointment: 10:00-10:30 (no overlap)
		Visit newVisit = createVisit(testDate, LocalTime.of(10, 0), 30, testVet);

		given(visitRepository.findByVetAndDate(testVet, testDate)).willReturn(Collections.singletonList(existing));

		assertThat(service.hasVetConflict(newVisit)).isFalse();
	}

	@Test
	void shouldAllowBackToBackAppointments() {
		// Existing appointment: 9:00-9:30
		Visit existing = createVisit(testDate, LocalTime.of(9, 0), 30, testVet);

		// New appointment: 9:30-10:00 (back-to-back, no overlap)
		Visit newVisit = createVisit(testDate, LocalTime.of(9, 30), 30, testVet);

		given(visitRepository.findByVetAndDate(testVet, testDate)).willReturn(Collections.singletonList(existing));

		assertThat(service.hasVetConflict(newVisit)).isFalse();
	}

	@Test
	void shouldDetectPetConflict() {
		Integer petId = 1;

		// Existing appointment: 9:00-9:30
		Visit existing = createVisit(testDate, LocalTime.of(9, 0), 30, testVet);

		// New appointment: 9:15-9:45 (overlaps)
		Visit newVisit = createVisit(testDate, LocalTime.of(9, 15), 30, testVet);

		given(visitRepository.findByPetIdAndDate(petId, testDate)).willReturn(Collections.singletonList(existing));

		assertThat(service.hasPetConflict(newVisit, petId)).isTrue();
	}

	@Test
	void shouldDetectCapacityConflict() {
		// 5 existing appointments all overlapping with new visit
		Visit existing1 = createVisit(testDate, LocalTime.of(9, 0), 30, testVet);
		Visit existing2 = createVisit(testDate, LocalTime.of(9, 0), 30, testVet);
		Visit existing3 = createVisit(testDate, LocalTime.of(9, 0), 30, testVet);
		Visit existing4 = createVisit(testDate, LocalTime.of(9, 0), 30, testVet);
		Visit existing5 = createVisit(testDate, LocalTime.of(9, 0), 30, testVet);

		// New appointment: 9:00-9:30 (overlaps with all 5)
		Visit newVisit = createVisit(testDate, LocalTime.of(9, 0), 30, testVet);

		given(visitRepository.findByDate(testDate))
			.willReturn(Arrays.asList(existing1, existing2, existing3, existing4, existing5));

		assertThat(service.hasCapacityConflict(newVisit)).isTrue();
	}

	@Test
	void shouldNotDetectCapacityConflictWhenUnderLimit() {
		// 4 existing appointments
		Visit existing1 = createVisit(testDate, LocalTime.of(9, 0), 30, testVet);
		Visit existing2 = createVisit(testDate, LocalTime.of(9, 0), 30, testVet);
		Visit existing3 = createVisit(testDate, LocalTime.of(9, 0), 30, testVet);
		Visit existing4 = createVisit(testDate, LocalTime.of(9, 0), 30, testVet);

		// New appointment: 9:00-9:30 (5th concurrent - should be allowed)
		Visit newVisit = createVisit(testDate, LocalTime.of(9, 0), 30, testVet);

		given(visitRepository.findByDate(testDate))
			.willReturn(Arrays.asList(existing1, existing2, existing3, existing4));

		assertThat(service.hasCapacityConflict(newVisit)).isFalse();
	}

	private Visit createVisit(LocalDate date, LocalTime startTime, int durationMinutes, Vet vet) {
		Visit visit = new Visit();
		visit.setDate(date);
		visit.setStartTime(startTime);
		visit.setDurationMinutes(durationMinutes);
		visit.setVet(vet);
		visit.setDescription("Test visit");
		return visit;
	}

}
