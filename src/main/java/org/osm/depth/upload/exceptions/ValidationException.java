package org.osm.depth.upload.exceptions;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ValidationException extends WebApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3719155758615383663L;

	public ValidationException() {
		super();

	}

	public ValidationException(int status) {
		super(status);
		
	}

	public ValidationException(Response response) {
		super(response);
		
	}

	public ValidationException(Status status) {
		super(status);
		
	}

	public ValidationException(String message, int status) {
		super(message, status);
		
	}

	public ValidationException(String message, Response response) {
		super(message, response);
		
	}

	public ValidationException(String message, Status status) {
		super(message, status);
		
	}

	public ValidationException(String message, Throwable cause, int status) {
		super(message, cause, status);
		
	}

	public ValidationException(String message, Throwable cause,
			Response response) {
		super(message, cause, response);
		
	}

	public ValidationException(String message, Throwable cause, Status status)
			throws IllegalArgumentException {
		super(message, cause, status);
		
	}

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public ValidationException(String... errors)
    {
        this(Arrays.asList(errors));
    }
	
	public ValidationException(List<String> errors) {
		super(Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
		        .entity(new GenericEntity<List<String>>(errors)
		                {}).build());
	}

	public ValidationException(Throwable cause, int status) {
		super(cause, status);
		
	}

	public ValidationException(Throwable cause, Response response) {
		super(cause, response);
		
	}

	public ValidationException(Throwable cause, Status status)
			throws IllegalArgumentException {
		super(cause, status);
		
	}

	public ValidationException(Throwable cause) {
		super(cause);
		
	}

}
