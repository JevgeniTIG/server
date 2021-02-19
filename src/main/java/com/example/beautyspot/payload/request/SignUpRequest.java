package com.example.beautyspot.payload.request;

import com.example.beautyspot.annotations.PasswordMatches;
import com.example.beautyspot.annotations.ValidEmail;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.*;

@Data
@PasswordMatches
public class SignUpRequest {

	@Email(message = "This field must have email format")
	@NotBlank(message = "Email is required")
	@ValidEmail
	private String email;

	@Column(nullable = true)
	@Size(max = 15)
	private String phone;

	@NotEmpty(message = "Please enter your Name")
	private String firstName;

	@NotEmpty(message = "Please enter your Lastname")
	private String lastName;

	@NotEmpty(message = "Please enter your username")
	private String userName;

	@NotEmpty(message = "Password is required")
	@Size(min = 6)
	private String password;
	private String confirmPassword;

}
