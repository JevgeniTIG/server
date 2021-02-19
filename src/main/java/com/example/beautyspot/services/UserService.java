package com.example.beautyspot.services;

import com.example.beautyspot.dto.UserDTO;
import com.example.beautyspot.entity.User;
import com.example.beautyspot.entity.enums.ERole;
import com.example.beautyspot.exceptions.UserExistsException;
import com.example.beautyspot.payload.request.SignUpRequest;
import com.example.beautyspot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UserService {

	public static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	public User createUser(SignUpRequest userIn) {

		User user = new User();
		user.setEmail(userIn.getEmail());
		user.setPhone(userIn.getPhone());
		user.setFirstName(userIn.getFirstName());
		user.setLastName(userIn.getLastName());
		user.setUserName(userIn.getUserName());
		user.setPassword(bCryptPasswordEncoder.encode(userIn.getPassword()));
		user.getRoles().add(ERole.ROLE_USER);

		try {
			LOG.info("Saving user with email " + userIn.getEmail());
			return userRepository.save(user);
		} catch (Exception ex) {
			LOG.error("Error during registration " + ex.getMessage());
			throw new UserExistsException("The user with such " + userIn.getUserName() + " already exists! " +
					"Please check details.");
		}
	}

	public User updateUser(UserDTO userDTO, Principal principal) {
		User user = getUserByPrincipal(principal);
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setBiography(userDTO.getBio());

		return userRepository.save(user);
	}

	public User getCurrentUser(Principal principal) {
		return getUserByPrincipal(principal);
	}

	private User getUserByPrincipal(Principal principal) {
		String userName = principal.getName();
		return userRepository.findUserByUserName(userName)
				.orElseThrow(() -> new UsernameNotFoundException("Username " + userName + " not found"));
	}

	public User getUserById(Long userId) {
		return userRepository.findUserById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

}
