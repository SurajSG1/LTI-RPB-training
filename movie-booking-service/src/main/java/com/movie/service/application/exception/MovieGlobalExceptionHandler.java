package com.movie.service.application.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class MovieGlobalExceptionHandler {

	@ExceptionHandler(value = { IOException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public MovieApiErrorResponse badRequest(IOException e) {
		return new MovieApiErrorResponse(400, e.getMessage());
	}

	@ExceptionHandler(value = { NoHandlerFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public MovieApiErrorResponse noHandlerFoundException(NoHandlerFoundException e) {
		return new MovieApiErrorResponse(404, e.getMessage());
	}

	@ExceptionHandler(value = { Exception.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public MovieApiErrorResponse unknownException(Exception e) {
		return new MovieApiErrorResponse(500, e.getMessage());
	}
}
