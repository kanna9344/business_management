package app.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.dto.UserTo;
import app.markers.Create;
import app.markers.Verify;
import app.repository.UserRepository;
import app.service.UserService;
import app.utils.CustomException;
import app.utils.JwtUtil;
import app.utils.Response;

@RestController
@RequestMapping(path = "/auth")
public class AuthenticationController {

	private AuthenticationManager authManager;

	private JwtUtil jwtUtil;
	private UserService service;
	private PasswordEncoder encoder;
	private UserRepository userRepo;

	public AuthenticationController(AuthenticationManager manager, JwtUtil util, UserService service,
			PasswordEncoder encoder, UserRepository userRepo) {

		this.authManager = manager;
		this.jwtUtil = util;
		this.service = service;
		this.encoder = encoder;
		this.userRepo = userRepo;

	}

	@PostMapping(value = "/register")
	public ResponseEntity<Response> createUser(@Validated(value = Create.class) @RequestBody UserTo to)
			throws CustomException {

		to.setPassword(encoder.encode(to.getPassword()));

		this.service.createUser(to);

		Response response = new Response();
		response.setStatus(HttpStatus.OK.value());
		response.setIsSuccess(true);
		response.setMessage("User created successfully ");

		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/login")
	public ResponseEntity<Map<String, String>> loginUser(@Validated(value = Verify.class) @RequestBody UserTo to) {

		Map<String, String> result = new HashMap<String, String>();

		Authentication auth = authManager
				.authenticate(new UsernamePasswordAuthenticationToken(to.getUsername(), to.getPassword()));

		if (auth.isAuthenticated()) {

			String token = jwtUtil.generateToken(to.getUsername());
			result.put("token", token);
		}

		return ResponseEntity.status(HttpStatus.OK).body(result);

	}

}
