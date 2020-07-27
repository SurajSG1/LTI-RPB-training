package com.user.service.application.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.service.application.dao.UserDetailsRepository;
import com.user.service.application.document.UserDetails;
import com.user.service.application.dto.RegisterDetailsDto;
import com.user.service.application.dto.UserDetailDto;
import com.user.service.application.exception.UserCustomException;

@Service
public class UserServiceImpl implements UserService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private UserDetailsRepository repository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public UserServiceImpl(UserDetailsRepository repository) {
		this.repository = repository;
	}

	@Override
	public UserDetailDto register(RegisterDetailsDto registerDto) throws UserCustomException {
		try {
			Optional<UserDetails> userInDb = repository.findByUsername(registerDto.getUserName());

			// validation for registering user
			if (userInDb.isPresent()) {
				throw new Exception("Username already exists. Please enter a different Username.");
			}
			if (registerDto.getUserName().isEmpty() || registerDto.getPassword().isEmpty())
				throw new Exception("Username and Password can not be empty");
			if (!registerDto.getPassword().equals(registerDto.getConfirmPassword()))
				throw new Exception("password and confirm password is not same");

			// convert registerDto to User document
			UserDetails user = new UserDetails(null, registerDto.getUserName(),
					bCryptPasswordEncoder.encode(registerDto.getPassword()), registerDto.getFirstName(),
					registerDto.getLastName(), registerDto.getEmailId(), registerDto.getRole().toUpperCase(),
					LocalDateTime.now());

			// saving user to database user_micro
			user = this.repository.save(user);

			// again convert user document to userDetailsDto
			UserDetailDto userDetailDto = new UserDetailDto(user.getUserId(), user.getUsername(), user.getEmailId(),
					user.getFirstName(), user.getLastName(), user.getRole());
			return userDetailDto;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new UserCustomException(e.getMessage());
		}
	}

	@Override
	public List<UserDetailDto> getAllUsers() throws UserCustomException {
		ArrayList<UserDetailDto> usersList;
		try {
			Iterator<UserDetails> users = this.repository.findAll().iterator();
			usersList = new ArrayList<UserDetailDto>();
			while (users.hasNext()) {
				UserDetails user = users.next();
				usersList.add(new UserDetailDto(user.getUserId(), user.getUsername(), user.getEmailId(),
						user.getFirstName(), user.getLastName(), user.getRole()));
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new UserCustomException(e.getMessage());
		}
		return usersList;
	}

	@Override
	public UserDetailDto getUserDetails(String userId) throws UserCustomException {
		UserDetailDto userDetailDto;
		try {
			Optional<UserDetails> userInDb = this.repository.findById(userId);
			if (!userInDb.isPresent())
				throw new Exception("Error while fetching user details: " + userId);
			UserDetails user = userInDb.get();
			userDetailDto = new UserDetailDto(user.getUserId(), user.getUsername(), user.getEmailId(),
					user.getFirstName(), user.getLastName(), user.getRole());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new UserCustomException(e.getMessage());
		}
		return userDetailDto;
	}

	@Override
	public UserDetailDto updateUserDetails(String userId, UserDetailDto userDetailDto) throws UserCustomException {
		try {
			// get user details of given userId
			Optional<UserDetails> userInDb = this.repository.findById(userId);
			if (!userInDb.isPresent())
				throw new UserCustomException("Error while updating user details: " + userId);
			UserDetails user = userInDb.get();

			// validation for updating user details
			// in case of username duplication in database
			Optional<UserDetails> userNameInDb = repository.findByUsername(userDetailDto.getUserName());
			if (userNameInDb.isPresent() && !userNameInDb.get().getUserId().equals(user.getUserId()))
				throw new UserCustomException("Username already exists. Please enter a different Username.");

			user.setUsername(userDetailDto.getUserName());
			user.setEmailId(userDetailDto.getEmailId());
			user.setFirstName(userDetailDto.getFirstName());
			user.setLastName(userDetailDto.getLastName());
			user.setRole(userDetailDto.getRole().toUpperCase());
			user.setModStamp(LocalDateTime.now());

			user = this.repository.save(user);

			userDetailDto = new UserDetailDto(user.getUserId(), user.getUsername(), user.getEmailId(),
					user.getFirstName(), user.getLastName(), user.getRole());

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new UserCustomException(e.getMessage());
		}
		return userDetailDto;

	}

	@Override
	public UserDetailDto login(Principal principal) throws UserCustomException {
		UserDetailDto userDetailDto;
		try {
			Optional<UserDetails> userInDb = this.repository.findByUsername(principal.getName());
			if (userInDb.isPresent()) {
				UserDetails user = userInDb.get();
				userDetailDto = new UserDetailDto(user.getUserId(), user.getUsername(), user.getEmailId(),
						user.getFirstName(), user.getLastName(), user.getRole());
				return userDetailDto;
			} else {
				throw new Exception("Please Enter Correct Username!");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new UserCustomException(e.getMessage());
		}
	}

}
