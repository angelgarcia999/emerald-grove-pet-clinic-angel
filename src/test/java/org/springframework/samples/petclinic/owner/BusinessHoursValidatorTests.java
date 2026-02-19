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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link BusinessHoursValidator}
 *
 * @author Claude Sonnet 4.5
 */
class BusinessHoursValidatorTests {

	private BusinessHoursValidator validator;

	@BeforeEach
	void setUp() {
		validator = new BusinessHoursValidator();
	}

	@Test
	void shouldAcceptWeekdayAppointmentAt9AM() {
		Visit visit = createVisit(DayOfWeek.MONDAY, LocalTime.of(9, 0));
		Errors errors = new BeanPropertyBindingResult(visit, "visit");

		validator.validate(visit, errors);

		assertThat(errors.hasErrors()).isFalse();
	}

	@Test
	void shouldAcceptWeekdayAppointmentAt5PM() {
		Visit visit = createVisit(DayOfWeek.WEDNESDAY, LocalTime.of(17, 0));
		Errors errors = new BeanPropertyBindingResult(visit, "visit");

		validator.validate(visit, errors);

		assertThat(errors.hasErrors()).isFalse();
	}

	@Test
	void shouldAcceptWeekdayAppointmentAtNoon() {
		Visit visit = createVisit(DayOfWeek.FRIDAY, LocalTime.of(12, 0));
		Errors errors = new BeanPropertyBindingResult(visit, "visit");

		validator.validate(visit, errors);

		assertThat(errors.hasErrors()).isFalse();
	}

	@Test
	void shouldAcceptSaturdayAppointmentAt9AM() {
		Visit visit = createVisit(DayOfWeek.SATURDAY, LocalTime.of(9, 0));
		Errors errors = new BeanPropertyBindingResult(visit, "visit");

		validator.validate(visit, errors);

		assertThat(errors.hasErrors()).isFalse();
	}

	@Test
	void shouldAcceptSaturdayAppointmentAt1PM() {
		Visit visit = createVisit(DayOfWeek.SATURDAY, LocalTime.of(13, 0));
		Errors errors = new BeanPropertyBindingResult(visit, "visit");

		validator.validate(visit, errors);

		assertThat(errors.hasErrors()).isFalse();
	}

	@Test
	void shouldRejectSundayAppointment() {
		Visit visit = createVisit(DayOfWeek.SUNDAY, LocalTime.of(10, 0));
		Errors errors = new BeanPropertyBindingResult(visit, "visit");

		validator.validate(visit, errors);

		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getFieldError("date")).isNotNull();
		assertThat(errors.getFieldError("date").getCode()).isEqualTo("visit.businessHours.sunday");
	}

	@Test
	void shouldRejectAppointmentBefore9AM() {
		Visit visit = createVisit(DayOfWeek.TUESDAY, LocalTime.of(8, 59));
		Errors errors = new BeanPropertyBindingResult(visit, "visit");

		validator.validate(visit, errors);

		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getFieldError("startTime")).isNotNull();
		assertThat(errors.getFieldError("startTime").getCode()).isEqualTo("visit.businessHours.beforeOpen");
	}

	@Test
	void shouldRejectWeekdayAppointmentAfter5PM() {
		Visit visit = createVisit(DayOfWeek.THURSDAY, LocalTime.of(17, 1));
		Errors errors = new BeanPropertyBindingResult(visit, "visit");

		validator.validate(visit, errors);

		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getFieldError("startTime")).isNotNull();
		assertThat(errors.getFieldError("startTime").getCode()).isEqualTo("visit.businessHours.afterCloseWeekday");
	}

	@Test
	void shouldRejectSaturdayAppointmentAfter1PM() {
		Visit visit = createVisit(DayOfWeek.SATURDAY, LocalTime.of(13, 1));
		Errors errors = new BeanPropertyBindingResult(visit, "visit");

		validator.validate(visit, errors);

		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getFieldError("startTime")).isNotNull();
		assertThat(errors.getFieldError("startTime").getCode()).isEqualTo("visit.businessHours.afterCloseSaturday");
	}

	@Test
	void shouldHandleNullDate() {
		Visit visit = new Visit();
		visit.setDate(null);
		visit.setStartTime(LocalTime.of(10, 0));
		Errors errors = new BeanPropertyBindingResult(visit, "visit");

		validator.validate(visit, errors);

		// Should not throw exception, validation should pass (null date handled by other
		// validators)
		assertThat(errors.hasErrors()).isFalse();
	}

	@Test
	void shouldHandleNullStartTime() {
		Visit visit = new Visit();
		visit.setDate(LocalDate.now().plusDays(1));
		visit.setStartTime(null);
		Errors errors = new BeanPropertyBindingResult(visit, "visit");

		validator.validate(visit, errors);

		// Should not throw exception, validation should pass (null time handled by other
		// validators)
		assertThat(errors.hasErrors()).isFalse();
	}

	/**
	 * Helper method to create a Visit for a specific day of week and time
	 */
	private Visit createVisit(DayOfWeek dayOfWeek, LocalTime time) {
		Visit visit = new Visit();
		LocalDate nextOccurrence = LocalDate.now().with(TemporalAdjusters.nextOrSame(dayOfWeek));
		visit.setDate(nextOccurrence);
		visit.setStartTime(time);
		visit.setDescription("Test visit");
		return visit;
	}

}
