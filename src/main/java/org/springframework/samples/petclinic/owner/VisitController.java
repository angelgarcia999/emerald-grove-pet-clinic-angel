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
import java.util.Map;
import java.util.Optional;

import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 * @author Wick Dynex
 */
@Controller
class VisitController {

	private final OwnerRepository owners;

	private final VisitRepository visits;

	private final VetRepository vets;

	private final BusinessHoursValidator businessHoursValidator;

	private final ConflictValidator conflictValidator;

	private final ConflictDetectionService conflictDetectionService;

	public VisitController(OwnerRepository owners, VisitRepository visits, VetRepository vets,
			BusinessHoursValidator businessHoursValidator, ConflictValidator conflictValidator,
			ConflictDetectionService conflictDetectionService) {
		this.owners = owners;
		this.visits = visits;
		this.vets = vets;
		this.businessHoursValidator = businessHoursValidator;
		this.conflictValidator = conflictValidator;
		this.conflictDetectionService = conflictDetectionService;
	}

	@InitBinder("visit")
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
		dataBinder.addValidators(businessHoursValidator, conflictValidator);
	}

	/**
	 * Called before each and every @RequestMapping annotated method. 2 goals: - Make sure
	 * we always have fresh data - Since we do not use the session scope, make sure that
	 * Pet object always has an id (Even though id is not part of the form fields)
	 * @param petId
	 * @return Pet
	 */
	@ModelAttribute("visit")
	public Visit loadPetWithVisit(@PathVariable(name = "ownerId", required = false) Integer ownerId,
			@PathVariable(name = "petId", required = false) Integer petId, Map<String, Object> model) {
		// Skip this method for endpoints that don't have ownerId/petId path variables
		if (ownerId == null || petId == null) {
			return null;
		}

		Optional<Owner> optionalOwner = owners.findById(ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));

		Pet pet = owner.getPet(petId);
		if (pet == null) {
			throw new IllegalArgumentException(
					"Pet with id " + petId + " not found for owner with id " + ownerId + ".");
		}
		model.put("pet", pet);
		model.put("owner", owner);
		model.put("vets", this.vets.findAll());

		Visit visit = new Visit();
		pet.addVisit(visit);
		return visit;
	}

	// Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm is
	// called
	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String initNewVisitForm() {
		return "pets/createOrUpdateVisitForm";
	}

	// Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm is
	// called
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@ModelAttribute Owner owner, @PathVariable int petId, @Valid Visit visit,
			BindingResult result, RedirectAttributes redirectAttributes, Map<String, Object> model) {
		// Custom validation for time and vet (required for new visits)
		if (visit.getStartTime() == null) {
			result.rejectValue("startTime", "visit.time.required", "Appointment time is required");
		}
		if (visit.getVet() == null) {
			result.rejectValue("vet", "visit.vet.required", "Please select a veterinarian");
		}

		// Check for pet conflict (must be done here since we have access to petId)
		if (visit.getDate() != null && visit.getStartTime() != null
				&& conflictDetectionService.hasPetConflict(visit, petId)) {
			result.rejectValue("startTime", "visit.conflict.pet", "Pet is already scheduled at this time");
		}

		if (result.hasErrors()) {
			// Re-add vets to model for form re-display
			model.put("vets", this.vets.findAll());
			return "pets/createOrUpdateVisitForm";
		}

		// Load the full Vet entity to avoid partial object update issues
		if (visit.getVet() != null && visit.getVet().getId() != null) {
			this.vets.findById(visit.getVet().getId()).ifPresent(visit::setVet);
		}

		owner.addVisit(petId, visit);
		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "Your visit has been booked");
		return "redirect:/owners/{ownerId}";
	}

	@GetMapping("/visits/upcoming")
	public String showUpcomingVisits(@RequestParam(defaultValue = "7") int days, Map<String, Object> model) {
		LocalDate startDate = LocalDate.now();
		LocalDate endDate = startDate.plusDays(days);

		List<Visit> upcomingVisits = this.visits.findUpcomingVisits(startDate, endDate);

		model.put("visits", upcomingVisits);
		model.put("days", days);
		model.put("startDate", startDate);
		model.put("endDate", endDate);

		return "visits/upcomingVisits";
	}

}
