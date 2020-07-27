package com.user.service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterDetailsDto {
	private String userName;
	private String password;
	private String confirmPassword;
	private String emailId;
	private String firstName;
	private String lastName;
	private String role;
}
