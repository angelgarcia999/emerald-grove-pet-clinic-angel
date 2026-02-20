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

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Visit} entity to verify time-based appointment fields.
 *
 * @author Emerald Grove Dev Team
 */
class VisitEntityTests {

	@Test
	void testStartTimeGetterSetter() {
		Visit visit = new Visit();
		LocalTime startTime = LocalTime.of(9, 30);

		visit.setStartTime(startTime);

		assertThat(visit.getStartTime()).isEqualTo(startTime);
	}

	@Test
	void testDurationMinutesGetterSetter() {
		Visit visit = new Visit();
		Integer duration = 45;

		visit.setDurationMinutes(duration);

		assertThat(visit.getDurationMinutes()).isEqualTo(duration);
	}

	@Test
	void testDefaultDuration() {
		Visit visit = new Visit();

		assertThat(visit.getDurationMinutes()).isEqualTo(30);
	}

	@Test
	void testVisitWithDateAndTime() {
		Visit visit = new Visit();
		LocalDate visitDate = LocalDate.of(2026, 3, 15);
		LocalTime startTime = LocalTime.of(10, 0);

		visit.setDate(visitDate);
		visit.setStartTime(startTime);
		visit.setDescription("Routine checkup");

		assertThat(visit.getDate()).isEqualTo(visitDate);
		assertThat(visit.getStartTime()).isEqualTo(startTime);
		assertThat(visit.getDescription()).isEqualTo("Routine checkup");
	}

	@Test
	void testVisitWithCustomDuration() {
		Visit visit = new Visit();
		visit.setDurationMinutes(60);

		assertThat(visit.getDurationMinutes()).isEqualTo(60);
	}

}
