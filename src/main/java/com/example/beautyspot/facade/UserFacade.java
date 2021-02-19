package com.example.beautyspot.facade;

import com.example.beautyspot.dto.UserDTO;
import com.example.beautyspot.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {
	public UserDTO userToUserDTO(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setId(user.getId());
		userDTO.setFirstName(user.getFirstName());
		userDTO.setLastName(user.getLastName());
		userDTO.setUserName(user.getUsername());
		userDTO.setBio(user.getBiography());
		userDTO.setEmail(user.getEmail());
		userDTO.setPhone(user.getPhone());
		return userDTO;
	}
}
