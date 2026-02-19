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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Wick Dynex
 */
@Controller
class OwnerController {

	private static final Logger logger = LoggerFactory.getLogger(OwnerController.class);

	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

	private static final int PAGE_SIZE = 5;

	private final OwnerRepository owners;

	public OwnerController(OwnerRepository owners) {
		this.owners = owners;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable(name = "ownerId", required = false) Integer ownerId) {
		if (ownerId == null) {
			return new Owner();
		}
		return this.owners.findById(ownerId).orElseGet(() -> {
			logger.info("Owner with ID {} not found", ownerId);
			throw new OwnerNotFoundException(ownerId);
		});
	}

	@GetMapping("/owners/new")
	public String initCreationForm() {
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/new")
	public String processCreationForm(@Valid Owner owner, BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		// Trim names for duplicate check and apply to entity
		String trimmedFirstName = owner.getFirstName().trim();
		String trimmedLastName = owner.getLastName().trim();
		owner.setFirstName(trimmedFirstName);
		owner.setLastName(trimmedLastName);

		// Check for duplicate owner
		Optional<Owner> existingOwner = this.owners.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndTelephone(
				trimmedFirstName, trimmedLastName, owner.getTelephone());

		if (existingOwner.isPresent()) {
			result.rejectValue("firstName", "owner.duplicate");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "New Owner Created");
		return "redirect:/owners/" + owner.getId();
	}

	@GetMapping("/owners/find")
	public String initFindForm() {
		return "owners/findOwners";
	}

	@GetMapping("/owners")
	public String processFindForm(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "") String city, @RequestParam(defaultValue = "") String telephone,
			Owner owner, BindingResult result, Model model) {
		// allow parameterless GET request for /owners to return all records
		String lastName = owner.getLastName();
		if (lastName == null) {
			lastName = ""; // empty string signifies broadest possible search
		}

		// find owners by multiple criteria
		Page<Owner> ownersResults = findPaginatedForMultipleCriteria(page, lastName, city, telephone);
		if (ownersResults.isEmpty()) {
			// no owners found
			result.rejectValue("lastName", "notFound", "not found");
			return "owners/findOwners";
		}

		if (ownersResults.getTotalElements() == 1) {
			// 1 owner found
			owner = ownersResults.iterator().next();
			return "redirect:/owners/" + owner.getId();
		}

		// multiple owners found
		return addPaginationModel(page, model, ownersResults, lastName, city, telephone);
	}

	private String addPaginationModel(int page, Model model, Page<Owner> paginated) {
		return addPaginationModel(page, model, paginated, "", "", "");
	}

	private String addPaginationModel(int page, Model model, Page<Owner> paginated, String lastName) {
		return addPaginationModel(page, model, paginated, lastName, "", "");
	}

	private String addPaginationModel(int page, Model model, Page<Owner> paginated, String lastName, String city,
			String telephone) {
		List<Owner> listOwners = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listOwners", listOwners);
		model.addAttribute("lastName", lastName);
		model.addAttribute("city", city);
		model.addAttribute("telephone", telephone);
		return "owners/ownersList";
	}

	private Page<Owner> findPaginatedForOwnersLastName(int page, String lastname) {
		Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
		return owners.findByLastNameStartingWith(lastname, pageable);
	}

	private Page<Owner> findPaginatedForMultipleCriteria(int page, String lastName, String city, String telephone) {
		Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
		return owners.findByLastNameStartingWithAndCityIgnoreCaseAndTelephone(lastName, city, telephone, pageable);
	}

	@GetMapping("/owners/{ownerId}/edit")
	public String initUpdateOwnerForm() {
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/{ownerId}/edit")
	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result, @PathVariable("ownerId") int ownerId,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in updating the owner.");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		if (!Objects.equals(owner.getId(), ownerId)) {
			result.rejectValue("id", "mismatch", "The owner ID in the form does not match the URL.");
			redirectAttributes.addFlashAttribute("error", "Owner ID mismatch. Please try again.");
			return "redirect:/owners/{ownerId}/edit";
		}

		owner.setId(ownerId);
		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "Owner Values Updated");
		return "redirect:/owners/{ownerId}";
	}

	/**
	 * Custom handler for displaying an owner.
	 * @param ownerId the ID of the owner to display
	 * @return a ModelMap with the model attributes for the view
	 */
	@GetMapping("/owners/{ownerId}")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		ModelAndView mav = new ModelAndView("owners/ownerDetails");
		Optional<Owner> optionalOwner = this.owners.findById(ownerId);
		Owner owner = optionalOwner.orElseGet(() -> {
			logger.info("Owner with ID {} not found", ownerId);
			throw new OwnerNotFoundException(ownerId);
		});
		mav.addObject(owner);
		return mav;
	}

	/**
	 * Export current page of owner search results to CSV format.
	 * @param page the current page number
	 * @param lastName the lastName search parameter
	 * @param city the city search parameter
	 * @param telephone the telephone search parameter
	 * @return ResponseEntity with CSV content and appropriate headers
	 */
	@GetMapping("/owners/export")
	public ResponseEntity<String> exportOwnersToCSV(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "") String lastName, @RequestParam(defaultValue = "") String city,
			@RequestParam(defaultValue = "") String telephone) {

		// Fetch owners for current page with search criteria
		Page<Owner> ownersResults = findPaginatedForMultipleCriteria(page, lastName, city, telephone);

		// Handle empty results - redirect to find page
		if (ownersResults.isEmpty()) {
			return ResponseEntity.status(302).header("Location", "/owners/find").build();
		}

		// Generate CSV content
		String csvContent = generateCSV(ownersResults.getContent());

		// Set response headers for CSV download
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");
		headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"owners.csv\"");

		return ResponseEntity.ok().headers(headers).body(csvContent);
	}

	/**
	 * Generate CSV content from a list of owners. Includes CSV injection prevention by
	 * escaping values that start with =, +, @, or -.
	 * @param owners the list of owners to export
	 * @return CSV formatted string
	 */
	private String generateCSV(List<Owner> owners) {
		StringBuilder csv = new StringBuilder();

		// CSV Header
		csv.append("ID,First Name,Last Name,Address,City,Telephone\n");

		// CSV Data Rows
		for (Owner owner : owners) {
			csv.append(owner.getId()).append(",");
			csv.append(escapeCSVValue(owner.getFirstName())).append(",");
			csv.append(escapeCSVValue(owner.getLastName())).append(",");
			csv.append(escapeCSVValue(owner.getAddress())).append(",");
			csv.append(escapeCSVValue(owner.getCity())).append(",");
			csv.append(escapeCSVValue(owner.getTelephone())).append("\n");
		}

		return csv.toString();
	}

	/**
	 * Escape CSV values to prevent CSV injection attacks. Values starting with =, +,
	 * @, or - are prefixed with a single quote.
	 * @param value the value to escape
	 * @return escaped value safe for CSV
	 */
	private String escapeCSVValue(String value) {
		if (value == null || value.isEmpty()) {
			return "";
		}

		char firstChar = value.charAt(0);
		if (firstChar == '=' || firstChar == '+' || firstChar == '@' || firstChar == '-') {
			return "'" + value;
		}

		return value;
	}

}
