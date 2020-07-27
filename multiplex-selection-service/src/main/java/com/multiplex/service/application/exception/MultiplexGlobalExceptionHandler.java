package com.multiplex.service.application.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class MultiplexGlobalExceptionHandler {

	@ExceptionHandler(value = { IOException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public MultiplexApiErrorResponse badRequest(IOException e) {
		return new MultiplexApiErrorResponse(400, e.getMessage());
	}

	@ExceptionHandler(value = { NoHandlerFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public MultiplexApiErrorResponse noHandlerFoundException(NoHandlerFoundException e) {
		return new MultiplexApiErrorResponse(404, e.getMessage());
	}

	@ExceptionHandler(value = { Exception.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public MultiplexApiErrorResponse unknownException(Exception e) {
		return new MultiplexApiErrorResponse(500, e.getMessage());
	}
}
