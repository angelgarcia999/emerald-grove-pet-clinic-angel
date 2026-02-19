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

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Validator for business hours constraints on visit appointments.
 * <p>
 * Business hours:
 * <ul>
 * <li>Monday-Friday: 9:00 AM - 5:00 PM (09:00-17:00)</li>
 * <li>Saturday: 9:00 AM - 1:00 PM (09:00-13:00)</li>
 * <li>Sunday: Closed (no appointments)</li>
 * </ul>
 *
 * @author Claude Sonnet 4.5
 */
@Component
public class BusinessHoursValidator implements Validator {

	private static final LocalTime OPENING_TIME = LocalTime.of(9, 0);

	private static final LocalTime WEEKDAY_CLOSING_TIME = LocalTime.of(17, 0);

	private static final LocalTime SATURDAY_CLOSING_TIME = LocalTime.of(13, 0);

	@Override
	public boolean supports(Class<?> clazz) {
		return Visit.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Visit visit = (Visit) target;

		// Skip validation if date or time is null (handled by other validators)
		if (visit.getDate() == null || visit.getStartTime() == null) {
			return;
		}

		LocalDate date = visit.getDate();
		LocalTime time = visit.getStartTime();
		DayOfWeek dayOfWeek = date.getDayOfWeek();

		// Check if appointment is on Sunday
		if (dayOfWeek == DayOfWeek.SUNDAY) {
			errors.rejectValue("date", "visit.businessHours.sunday", "Clinic is closed on Sundays");
			return;
		}

		// Check if appointment is before opening time (9:00 AM)
		if (time.isBefore(OPENING_TIME)) {
			errors.rejectValue("startTime", "visit.businessHours.beforeOpen", "Clinic opens at 9:00 AM");
			return;
		}

		// Check if appointment is after closing time (depends on day of week)
		if (dayOfWeek == DayOfWeek.SATURDAY) {
			if (time.isAfter(SATURDAY_CLOSING_TIME)) {
				errors.rejectValue("startTime", "visit.businessHours.afterCloseSaturday",
						"Clinic closes at 1:00 PM on Saturdays");
			}
		}
		else {
			// Monday-Friday
			if (time.isAfter(WEEKDAY_CLOSING_TIME)) {
				errors.rejectValue("startTime", "visit.businessHours.afterCloseWeekday", "Clinic closes at 5:00 PM");
			}
		}
	}

}
