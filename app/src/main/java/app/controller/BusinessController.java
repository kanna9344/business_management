package app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.dto.BusinessTo;
import app.markers.Create;
import app.markers.Update;
import app.model.Business;
import app.service.BusinessService;
import app.utils.CustomException;

@RestController
@RequestMapping(value = "/business")
public class BusinessController {

	private BusinessService service;

	@Autowired
	public BusinessController(BusinessService service) {
		this.service = service;
	}

	@PostMapping(value = "/create")
	public ResponseEntity<BusinessTo> createBusiness(@Validated(value = Create.class) @RequestBody BusinessTo to)
			throws CustomException {

		BusinessTo business = service.createBusiness(to);
		return ResponseEntity.status(HttpStatus.OK).body(business);
	}

	@PutMapping(value = "/update")
	public ResponseEntity<BusinessTo> updateBusiness(@Validated(value = Update.class) @RequestBody BusinessTo to)
			throws CustomException {

		BusinessTo business = service.updateBusiness(to);
		return ResponseEntity.status(HttpStatus.OK).body(business);
	}

	@GetMapping(value = "/view")
	public ResponseEntity<BusinessTo> viewBusiness(@RequestParam(value = "id") Long id) throws CustomException {
		BusinessTo business = service.viewBusiness(id);
		return ResponseEntity.status(HttpStatus.OK).body(business);
	}

}
