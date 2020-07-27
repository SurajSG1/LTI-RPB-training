package com.user.service.application.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.user.service.application.document.UserDetails;

@Repository
public interface UserDetailsRepository extends CrudRepository<UserDetails, String> {
	Optional<UserDetails> findByUsername(String username);

}
