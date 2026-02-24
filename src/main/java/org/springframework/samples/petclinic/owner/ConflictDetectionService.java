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

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * Service for detecting scheduling conflicts in visit appointments.
 * <p>
 * Implements three types of conflict detection:
 * <ul>
 * <li>Vet conflicts - prevents same vet from having overlapping appointments</li>
 * <li>Pet conflicts - prevents same pet from having overlapping appointments</li>
 * <li>Capacity conflicts - enforces maximum concurrent appointments limit</li>
 * </ul>
 *
 * @author Claude Sonnet 4.5
 */
@Service
public class ConflictDetectionService {

	private static final int MAX_CONCURRENT_APPOINTMENTS = 5;

	private final VisitRepository visitRepository;

	public ConflictDetectionService(VisitRepository visitRepository) {
		this.visitRepository = visitRepository;
	}

	/**
	 * Check if a new visit conflicts with any existing vet appointments.
	 * @param newVisit the visit to check
	 * @return true if vet has conflicting appointment
	 */
	public boolean hasVetConflict(Visit newVisit) {
		if (newVisit.getVet() == null || newVisit.getDate() == null || newVisit.getStartTime() == null) {
			return false;
		}

		List<Visit> existingVisits = visitRepository.findByVetAndDate(newVisit.getVet(), newVisit.getDate());

		for (Visit existing : existingVisits) {
			if (doAppointmentsOverlap(newVisit, existing)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Check if a new visit conflicts with any existing pet appointments.
	 * @param newVisit the visit to check
	 * @param petId the pet ID
	 * @return true if pet has conflicting appointment
	 */
	public boolean hasPetConflict(Visit newVisit, Integer petId) {
		if (petId == null || newVisit.getDate() == null || newVisit.getStartTime() == null) {
			return false;
		}

		List<Visit> existingVisits = visitRepository.findByPetIdAndDate(petId, newVisit.getDate());

		for (Visit existing : existingVisits) {
			if (doAppointmentsOverlap(newVisit, existing)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Check if a new visit would exceed clinic capacity limit.
	 * @param newVisit the visit to check
	 * @return true if clinic is at capacity
	 */
	public boolean hasCapacityConflict(Visit newVisit) {
		if (newVisit.getDate() == null || newVisit.getStartTime() == null) {
			return false;
		}

		List<Visit> allVisitsOnDate = visitRepository.findByDate(newVisit.getDate());

		int concurrentCount = 0;
		for (Visit existing : allVisitsOnDate) {
			if (doAppointmentsOverlap(newVisit, existing)) {
				concurrentCount++;
			}
		}

		return concurrentCount >= MAX_CONCURRENT_APPOINTMENTS;
	}

	/**
	 * Check if two appointments overlap using inclusive overlap algorithm. Two
	 * appointments overlap if any minute of one falls within the time range of the other.
	 * Back-to-back appointments (e.g., 9:00-9:30 and 9:30-10:00) do NOT overlap.
	 * @param visit1 first visit
	 * @param visit2 second visit
	 * @return true if appointments overlap
	 */
	private boolean doAppointmentsOverlap(Visit visit1, Visit visit2) {
		if (visit1.getStartTime() == null || visit2.getStartTime() == null) {
			return false;
		}

		LocalTime end1 = visit1.getStartTime().plusMinutes(visit1.getDurationMinutes());
		LocalTime end2 = visit2.getStartTime().plusMinutes(visit2.getDurationMinutes());

		// Inclusive overlap: (start1 < end2) AND (start2 < end1)
		return visit1.getStartTime().isBefore(end2) && visit2.getStartTime().isBefore(end1);
	}

}
