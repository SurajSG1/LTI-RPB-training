package com.user.service.application.service;

import java.security.Principal;
import java.util.List;

import com.user.service.application.dto.RegisterDetailsDto;
import com.user.service.application.dto.UserDetailDto;

public interface UserService {
	public UserDetailDto register(RegisterDetailsDto registerDto);

	public List<UserDetailDto> getAllUsers();

	public UserDetailDto getUserDetails(String userId);

	public UserDetailDto updateUserDetails(String userId, UserDetailDto userDetailDto);

	public UserDetailDto login(Principal principal);
}
