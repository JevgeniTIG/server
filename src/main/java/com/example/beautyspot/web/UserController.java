package com.example.beautyspot.web;

import com.example.beautyspot.dto.UserDTO;
import com.example.beautyspot.entity.User;
import com.example.beautyspot.facade.UserFacade;
import com.example.beautyspot.services.UserService;
import com.example.beautyspot.validations.ResponseErrorValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private UserFacade userFacade;
	@Autowired
	private ResponseErrorValidator responseErrorValidator;

	@GetMapping("/")
	public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
		User user = userService.getCurrentUser(principal);
		UserDTO userDTO = userFacade.userToUserDTO(user);

		return new ResponseEntity<>(userDTO, HttpStatus.OK);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<UserDTO> getUserProfile(@PathVariable("userId") String userId) {
		User user = userService.getUserById(Long.parseLong(userId));
		UserDTO userDTO = userFacade.userToUserDTO(user);

		return new ResponseEntity<>(userDTO, HttpStatus.OK);
	}

	@PostMapping("/update")
	public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult, Principal principal) {
		ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
		if (!ObjectUtils.isEmpty(errors)) return errors;

		User user = userService.updateUser(userDTO, principal);
		UserDTO userUpdated = userFacade.userToUserDTO(user);

		return new ResponseEntity<>(userUpdated, HttpStatus.OK);
	}
}
