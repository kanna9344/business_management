package app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.dto.RequestTo;
import app.markers.Create;
import app.markers.Update;
import app.service.RequestService;
import app.utils.CustomException;
import app.utils.Response;

@RestController
@RequestMapping(path = "/request")
public class RequestController {

	private RequestService service;

	@Autowired
	public RequestController(RequestService service) {
		this.service = service;
	}

	@PostMapping(path = "/create")
	public ResponseEntity<RequestTo> createRequest(@Validated(Create.class) @RequestBody RequestTo to)
			throws CustomException {
		RequestTo req = service.createRequest(to);

		return ResponseEntity.ok(req);
	}

	@PutMapping(path = "/update")
	public ResponseEntity<RequestTo> updateRequest(@Validated(Update.class) @RequestBody RequestTo to)
			throws CustomException {
		RequestTo req = service.updateRequest(to);

		return ResponseEntity.ok(req);
	}

	@PatchMapping(path = "/completeRequest")
	public ResponseEntity<Response> completeRequest(@RequestParam(name = "reqId", required = true) Long id)
			throws CustomException {
		service.completeRequest(id);

		Response response = new Response();
		response.setIsSuccess(true);
		response.setStatus(HttpStatus.OK.value());
		response.setMessage("Transaction completed successfully");

		return ResponseEntity.ok(response);
	}

	@GetMapping(value = "/view")
	public ResponseEntity<RequestTo> viewRequest(@RequestParam(value = "id") Long id) throws CustomException {
		RequestTo reqTo = service.viewRequest(id);
		return ResponseEntity.status(HttpStatus.OK).body(reqTo);
	}
}
