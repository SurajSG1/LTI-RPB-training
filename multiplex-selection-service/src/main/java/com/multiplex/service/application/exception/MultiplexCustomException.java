package com.multiplex.service.application.exception;

public class MultiplexCustomException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private String errorMessage;
	
	MultiplexCustomException() {
		super();
	}
	
	public MultiplexCustomException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	

}
