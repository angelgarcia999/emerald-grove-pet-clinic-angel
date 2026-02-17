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

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository class for <code>Owner</code> domain objects. All method names are compliant
 * with Spring Data naming conventions so this interface can easily be extended for Spring
 * Data. See:
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Wick Dynex
 */
public interface OwnerRepository extends JpaRepository<Owner, Integer> {

	/**
	 * Retrieve {@link Owner}s from the data store by last name, returning all owners
	 * whose last name <i>starts</i> with the given name.
	 * @param lastName Value to search for
	 * @return a Collection of matching {@link Owner}s (or an empty Collection if none
	 * found)
	 */
	Page<Owner> findByLastNameStartingWith(String lastName, Pageable pageable);

	/**
	 * Retrieve an {@link Owner} from the data store by id.
	 * <p>
	 * This method returns an {@link Optional} containing the {@link Owner} if found. If
	 * no {@link Owner} is found with the provided id, it will return an empty
	 * {@link Optional}.
	 * </p>
	 * @param id the id to search for
	 * @return an {@link Optional} containing the {@link Owner} if found, or an empty
	 * {@link Optional} if not found.
	 * @throws IllegalArgumentException if the id is null (assuming null is not a valid
	 * input for id)
	 */
	Optional<Owner> findById(Integer id);

	/**
	 * Find an {@link Owner} by first name, last name, and telephone number.
	 * <p>
	 * This method performs a case-insensitive search for owners matching the exact
	 * combination of first name, last name, and telephone. It is primarily used for
	 * duplicate detection when creating or updating owner records.
	 * </p>
	 * @param firstName the first name to search for (case-insensitive)
	 * @param lastName the last name to search for (case-insensitive)
	 * @param telephone the telephone number to search for (exact match)
	 * @return an {@link Optional} containing the matching {@link Owner} if found, or an
	 * empty {@link Optional} if no match exists
	 */
	Optional<Owner> findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone(String firstName, String lastName,
			String telephone);

	/**
	 * Find {@link Owner}s by last name, city, and telephone with optional parameters.
	 * <p>
	 * This method supports multi-criteria search with AND logic. Empty string parameters
	 * are treated as "ignore this criterion" and excluded from the query. Last name uses
	 * "starts with" matching, city is case-insensitive exact match, and telephone is
	 * exact match.
	 * </p>
	 * <p>
	 * Examples:
	 * <ul>
	 * <li>lastName="Smith", city="", telephone="" - finds all owners with lastName
	 * starting with "Smith"</li>
	 * <li>lastName="Smith", city="Madison", telephone="" - finds owners with lastName
	 * starting with "Smith" AND city="Madison"</li>
	 * <li>lastName="Smith", city="Madison", telephone="6085551023" - finds owners
	 * matching all three criteria</li>
	 * </ul>
	 * </p>
	 * @param lastName the last name prefix to search for (starts-with matching)
	 * @param city the city to search for (case-insensitive exact match, empty string to
	 * ignore)
	 * @param telephone the telephone number to search for (exact match, empty string to
	 * ignore)
	 * @param pageable pagination information
	 * @return a {@link Page} of matching {@link Owner}s
	 */
	@Query("""
			SELECT o FROM Owner o WHERE
				LOWER(o.lastName) LIKE LOWER(CONCAT(:lastName, '%'))
				AND (:city = '' OR LOWER(o.city) = LOWER(:city))
				AND (:telephone = '' OR o.telephone = :telephone)
			""")
	Page<Owner> findByLastNameStartingWithAndCityIgnoreCaseAndTelephone(@Param("lastName") String lastName,
			@Param("city") String city, @Param("telephone") String telephone, Pageable pageable);

}
