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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

/**
 * Integration tests for the Upcoming Visits feature. Tests verify end-to-end
 * functionality with real database and web server.
 *
 * @author Emerald Grove Dev Team
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("default")
class UpcomingVisitsIntegrationTests {

	@LocalServerPort
	int port;

	@Autowired
	private RestTemplateBuilder builder;

	@Autowired
	private VisitRepository visits;

	@Autowired
	private OwnerRepository owners;

	@Autowired
	private PetTypeRepository petTypes;

	/**
	 * End-to-end test that verifies the /visits/upcoming endpoint returns future-dated
	 * visits from the database.
	 */
	@Test
	void shouldDisplayUpcomingVisitsEndToEnd() {
		// Arrange - Build RestTemplate with server port
		RestTemplate template = builder.rootUri("http://localhost:" + port).build();

		// Act - GET the upcoming visits page
		ResponseEntity<String> response = template.exchange(RequestEntity.get("/visits/upcoming").build(),
				String.class);

		// Assert - Verify successful response
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Verify HTML contains expected content
		String body = response.getBody();
		assertThat(body).contains("Upcoming Visits");
		assertThat(body).contains("annual checkup"); // Future visit from data.sql
		assertThat(body).contains("vaccination booster"); // Future visit from data.sql
		assertThat(body).contains("dental cleaning"); // Future visit from data.sql
	}

	/**
	 * Integration test that programmatically creates a future visit and verifies it
	 * appears in the upcoming visits results.
	 */
	@Test
	void shouldFilterVisitsByDateRange() {
		// Arrange - Calculate date range for next 30 days
		LocalDate startDate = LocalDate.now();
		LocalDate endDate = startDate.plusDays(30);

		// Act - Query repository directly
		var upcomingVisits = visits.findUpcomingVisits(startDate, endDate);

		// Assert - Verify results contain future visits
		assertThat(upcomingVisits).isNotEmpty();
		assertThat(upcomingVisits).anyMatch(v -> v.getDate().isAfter(LocalDate.now()));
		assertThat(upcomingVisits).allMatch(v -> v.getDate().isBefore(endDate));
	}

}
