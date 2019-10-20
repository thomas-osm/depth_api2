package org.osm.depth.upload.resources;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osm.depth.upload.exceptions.DatabaseException;
import org.osm.depth.upload.messages.Stats;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/stats")
@Api(value ="Statistics")
public class StatsResource {
	
	@ApiOperation(value = "Retrieves statistics about current user and track count")
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Stats getStats() {
		Stats stats = new Stats();
		Context initContext;
		try {
			initContext = new InitialContext();
			DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/postgres"); //$NON-NLS-1$
			DataSource dsDepth = (DataSource)initContext.lookup("java:/comp/env/jdbc/depth"); //$NON-NLS-1$
			try (Connection connection = ds.getConnection();
				Connection depthConn = dsDepth.getConnection()) {
				try (Statement statement = connection.createStatement();
						 Statement statement2 = depthConn.createStatement();
						 Statement statement3 = depthConn.createStatement()) {
						try (ResultSet resultSet = statement.executeQuery("SELECT count(user_name) FROM user_profiles");
							ResultSet resultSet2 = statement2.executeQuery("SELECT count(datasetid) FROM trackpoints_raw_16");
							ResultSet resultSet3 = statement3.executeQuery("SELECT COUNT(datasetid) FROM (SELECT DISTINCT datasetid FROM trackpoints_raw_16) AS temp")) {
							while(resultSet.next()) {
								stats.usercount = resultSet.getInt(1);
							}
							while(resultSet2.next()) {
								stats.contributedpoints = resultSet2.getInt(1);
							}
							while(resultSet3.next()) {
								stats.trackscount = resultSet3.getInt(1);
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
		return stats;
	}

}
