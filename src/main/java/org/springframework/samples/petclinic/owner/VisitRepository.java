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

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository interface for {@link Visit} entities. Provides data access methods for
 * querying visits.
 *
 * @author Emerald Grove Dev Team
 */
public interface VisitRepository extends Repository<Visit, Integer> {

	/**
	 * Find all visits scheduled between start date (inclusive) and end date (inclusive).
	 * Queries from Pet entity to navigate the unidirectional relationship, since Visit
	 * doesn't have a direct Pet reference.
	 * @param start the beginning of the date range (inclusive)
	 * @param end the end of the date range (inclusive)
	 * @return list of visits within the specified date range, ordered by date ascending
	 */
	@Transactional(readOnly = true)
	@Query("SELECT v FROM Pet p JOIN p.visits v WHERE v.date BETWEEN :start AND :end ORDER BY v.date ASC")
	List<Visit> findUpcomingVisits(@Param("start") LocalDate start, @Param("end") LocalDate end);

	/**
	 * Find all visits for a specific vet on a given date. Used for conflict detection.
	 * @param vet the veterinarian
	 * @param date the date to check
	 * @return list of visits for the specified vet on the given date
	 */
	@Transactional(readOnly = true)
	@Query("SELECT v FROM Pet p JOIN p.visits v WHERE v.vet = :vet AND v.date = :date")
	List<Visit> findByVetAndDate(@Param("vet") org.springframework.samples.petclinic.vet.Vet vet,
			@Param("date") LocalDate date);

	/**
	 * Find all visits for a specific pet on a given date. Used for conflict detection.
	 * @param petId the pet ID
	 * @param date the date to check
	 * @return list of visits for the specified pet on the given date
	 */
	@Transactional(readOnly = true)
	@Query("SELECT v FROM Pet p JOIN p.visits v WHERE p.id = :petId AND v.date = :date")
	List<Visit> findByPetIdAndDate(@Param("petId") Integer petId, @Param("date") LocalDate date);

	/**
	 * Find all visits on a specific date. Used for clinic capacity checking.
	 * @param date the date to check
	 * @return list of all visits on the given date
	 */
	@Transactional(readOnly = true)
	@Query("SELECT v FROM Pet p JOIN p.visits v WHERE v.date = :date")
	List<Visit> findByDate(@Param("date") LocalDate date);

}
