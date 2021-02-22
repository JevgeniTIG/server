package com.example.beautyspot.web;

import com.example.beautyspot.payload.request.LoginRequest;
import com.example.beautyspot.payload.request.SignUpRequest;
import com.example.beautyspot.payload.response.JWTTokenSuccessResponse;
import com.example.beautyspot.payload.response.MessageResponse;
import com.example.beautyspot.security.JWTTokenProvider;
import com.example.beautyspot.security.SecurityConstants;
import com.example.beautyspot.services.UserService;
import com.example.beautyspot.validations.ResponseErrorValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/auth")
@PreAuthorize("permitAll()")
public class AuthController {
	@Autowired
	private JWTTokenProvider jwtTokenProvider;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private ResponseErrorValidator responseErrorValidator;
	@Autowired
	private UserService userService;

	@PostMapping("/signin")
	public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
		ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
		if (!ObjectUtils.isEmpty(errors)) return errors;

		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				loginRequest.getUserName(),
				loginRequest.getPassword()
		));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);

		return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt));

	}

	@PostMapping("/signup")
	public ResponseEntity<Object> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, BindingResult bindingResult) {
		ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
		if (!ObjectUtils.isEmpty(errors)) return errors;

		userService.createUser(signUpRequest);
		return ResponseEntity.ok(new MessageResponse("User successfully registered"));
	}
}
