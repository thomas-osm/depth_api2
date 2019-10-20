package org.osm.depth.upload.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ConflictException extends WebApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6110785293055959299L;

	public ConflictException() {
		super();
	}

	public ConflictException(int status) {
		super(status);
	}

	public ConflictException(Response response) {
		super(response);
	}

	public ConflictException(Status status) {
		super(status);
	}

	public ConflictException(String message, int status) {
		super(message, status);
	}

	public ConflictException(String message, Response response) {
		super(message, response);
	}

	public ConflictException(String message, Status status) {
		super(message, status);
	}

	public ConflictException(String message, Throwable cause, int status) {
		super(message, cause, status);
	}

	public ConflictException(String message, Throwable cause, Response response) {
		super(message, cause, response);
	}

	public ConflictException(String message, Throwable cause, Status status)
			throws IllegalArgumentException {
		super(message, cause, status);
	}

	public ConflictException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConflictException(String message) {
		super(message);
	}

	public ConflictException(Throwable cause, int status) {
		super(cause, status);
	}

	public ConflictException(Throwable cause, Response response) {
		super(cause, response);
	}

	public ConflictException(Throwable cause, Status status)
			throws IllegalArgumentException {
		super(cause, status);
	}

	public ConflictException(Throwable cause) {
		super(cause);
	}

}
