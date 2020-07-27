package com.user.service.application.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class UserGlobalExceptionHandler {

	@ExceptionHandler(value = { IOException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public UserApiErrorResponse badRequest(IOException e) {
		return new UserApiErrorResponse(400, e.getMessage());
	}

	@ExceptionHandler(value = { NoHandlerFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public UserApiErrorResponse noHandlerFoundException(NoHandlerFoundException e) {
		return new UserApiErrorResponse(404, e.getMessage());
	}

	@ExceptionHandler(value = { Exception.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public UserApiErrorResponse unknownException(Exception e) {
		return new UserApiErrorResponse(500, e.getMessage());
	}
}
