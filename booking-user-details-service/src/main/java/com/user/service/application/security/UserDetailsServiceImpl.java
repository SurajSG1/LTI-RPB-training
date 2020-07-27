package com.user.service.application.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.user.service.application.dao.UserDetailsRepository;
import com.user.service.application.document.UserDetails;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserDetailsRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Optional<UserDetails> user = userRepository.findByUsername(username);
		if (user.isPresent()) {
			return new UserDetailsPrincipal(user.get());
		} else {
			throw new UsernameNotFoundException(username + " is not an authorized user!");
		}
	}

}
