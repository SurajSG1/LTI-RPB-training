package com.user.service.application.exception;

public class UserCustomException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private String errorMessage;
	
	UserCustomException() {
		super();
	}
	
	public UserCustomException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	

}
