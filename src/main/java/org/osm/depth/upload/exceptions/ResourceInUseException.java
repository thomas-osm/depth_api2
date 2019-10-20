package org.osm.depth.upload.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ResourceInUseException extends WebApplicationException {

	public ResourceInUseException() {
		super(Response.serverError().build());
	}
	
	public ResourceInUseException(String message) {
		super(message);
	}

	
}
