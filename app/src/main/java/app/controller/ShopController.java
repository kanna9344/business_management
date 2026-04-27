package app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.dto.RequestTo;
import app.dto.ShopTo;
import app.markers.Create;
import app.markers.Update;
import app.service.ShopService;
import app.utils.CustomException;

@RestController
@RequestMapping(value = "/shop")
public class ShopController {

	private ShopService service;

	@Autowired
	public ShopController(ShopService service) {
		this.service = service;
	}

	@PostMapping(value = "/create")
	public ResponseEntity<ShopTo> createShop(@Validated(value = Create.class) @RequestBody ShopTo to)
			throws CustomException {

		ShopTo response = service.saveShop(to);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PutMapping(value = "/update")
	public ResponseEntity<ShopTo> updateShop(@Validated(value = Update.class) @RequestBody ShopTo to)
			throws CustomException {

		ShopTo response = service.updateShop(to);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping(value = "/view")
	public ResponseEntity<ShopTo> viewShop(@RequestParam(value = "id") Long id) throws CustomException {
		ShopTo shopTo = service.viewShop(id);
		return ResponseEntity.status(HttpStatus.OK).body(shopTo);
	}

}
