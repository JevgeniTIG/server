package com.example.beautyspot.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserDTO {
	private Long id;
	@NotEmpty
	private String firstName;
	@NotEmpty
	private String lastName;
	private String userName;
	private String bio;
	private String email;
	private String phone;
}
