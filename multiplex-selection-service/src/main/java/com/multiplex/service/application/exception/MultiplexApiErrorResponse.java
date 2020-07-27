package com.multiplex.service.application.exception;

public class MultiplexApiErrorResponse {

	private int status;
	private String message;

	public MultiplexApiErrorResponse() {
		super();
	}

	public MultiplexApiErrorResponse(int status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String toString() {
		return "ErrorResponse { " + " status = " + status + " message = " + message + "}";
	}

}
