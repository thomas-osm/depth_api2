package org.osm.depth.upload.resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.security.RolesAllowed;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.osm.depth.upload.exceptions.DatabaseException;
import org.osm.depth.upload.messages.RoleChange;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/rolechange")
@Api(tags = {"Role Change"})
public class RoleChangeResource {

	@POST
	@Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	@RolesAllowed({ "ADMIN", "USER" })
	@ApiOperation(value = "Create a new license", notes = "Create your own or fill in an existing license for which to upload data. You may even make it available for everyone")
	public void createChangeRoleRequest(@javax.ws.rs.core.Context SecurityContext context, RoleChange roleChange) {
		String username = context.getUserPrincipal().getName();
		Context initContext;
		try {
			if(roleChange.requestedRole.toUpperCase().equals("ADMIN")) {
				return;
			}
			if(!"CONTRIBUTOR".equals(roleChange.requestedRole)) {
				return;
			}
			initContext = new InitialContext();
			DataSource ds = (DataSource)initContext.lookup("java:/comp/env/jdbc/postgres"); //$NON-NLS-1$
			try (Connection conn = ds.getConnection();
				 PreparedStatement changeRole = conn.prepareStatement("INSERT INTO rolechange (user_name, role) VALUES (?,?)")) {
				changeRole.setString(1, username);
				changeRole.setString(2, roleChange.requestedRole);
				// TODO send email to admins
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Internal SQL Error");
		} catch (NamingException e) {
			e.printStackTrace();
			throw new DatabaseException("Database unavailable");
		}		
	}

	@DELETE
	@Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	@RolesAllowed({ "ADMIN" })
	@ApiOperation(value = "Delete ", notes = "Create your own or fill in an existing license for which to upload data. You may even make it available for everyone")
	public Response approveRoleRequest(@javax.ws.rs.core.Context SecurityContext context, RoleChange roleChange) {
		Context initContext;
		try {
			if("ADMIN".equals(roleChange.requestedRole.toUpperCase())) {
				throw new WebApplicationException(Response.Status.FORBIDDEN);
			}
			initContext = new InitialContext();
			DataSource ds = (DataSource)initContext.lookup("java:/comp/env/jdbc/postgres"); //$NON-NLS-1$
			try (Connection conn = ds.getConnection();
				 PreparedStatement selectRoleChange = conn.prepareStatement("SELECT user_name, role FROM rolechange WHERE user_name = ?");
				 PreparedStatement changeRole = conn.prepareStatement("UPDATE userroles SET role = ? WHERE user_name = ?");
					PreparedStatement deleteRoleChange = conn.prepareStatement("DELETE FROM rolechange WHERE user_name = ?")) {
				selectRoleChange.setString(1, roleChange.user_name);
				try(ResultSet resultSet = selectRoleChange.executeQuery()) {
					if(resultSet.next()) {
						String userName = resultSet.getString(1);
						String role = resultSet.getString(2);
						changeRole.setString(1, role);
						changeRole.setString(2, userName);
						boolean execute = changeRole.execute();
						if(execute) {
							deleteRoleChange.setString(1, roleChange.user_name);
							boolean deletedRole = deleteRoleChange.execute();
							if(deletedRole) {
								return Response.ok().build();
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Internal SQL Error");
		} catch (NamingException e) {
			e.printStackTrace();
			throw new DatabaseException("Database unavailable");
		}
		return null;		
	}

}

