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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a pet is not found.
 *
 * This exception is automatically mapped to HTTP 404 status code by
 * Spring's @ResponseStatus annotation.
 *
 * @author Wick Dynex
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Pet not found")
public class PetNotFoundException extends RuntimeException {

	private final Integer petId;

	public PetNotFoundException(Integer petId) {
		super("Pet with ID " + petId + " was not found");
		this.petId = petId;
	}

	public Integer getPetId() {
		return petId;
	}

}
