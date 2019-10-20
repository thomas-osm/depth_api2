/**
 * 
 Copyright (c) 2010-2013, Jens Kï¿½bler All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of the <organization> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package org.osm.depth.upload.resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.osm.depth.upload.exceptions.DatabaseException;
import org.osm.depth.upload.messages.DepthSensor;
import org.osm.depth.upload.messages.SBASSensor;
import org.osm.depth.upload.messages.VesselConfiguration;
import org.osm.depth.upload.messages.VesselType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/vesselconfig")
@Api(tags = {"Vessel Configuration"})
public class VesselConfigurationResource {

	@javax.ws.rs.core.Context
	UriInfo uriInfo;

	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("{id}")
	@ApiOperation(value = "Updates a vessel configuration", notes = "Updates the details of a vessel configuration. User may only update their own configurations")
	public void updateVesselConfig(
			@javax.ws.rs.core.Context SecurityContext context,
			VesselConfiguration vesselConfiguration) {
		String username = context.getUserPrincipal().getName();
		try {
			InitialContext initContext = new InitialContext();
			DataSource ds = (DataSource) initContext
					.lookup("java:/comp/env/jdbc/postgres"); //$NON-NLS-1$
			try (Connection conn = ds.getConnection()) {
;
				try (
						PreparedStatement updateStatement = conn
						.prepareStatement(
								"UPDATE vesselconfiguration SET" +
										"(name, " +
										"description, " +
										"mmsi, " +
										"manufacturer, " +
										"model, " +
										"loa, " +
										"breadth, " +
										"draft, " +
										"height, " +
										"displacement, " +
										"maximumspeed, " +
										"type) " +
								"= (?,?,?,?,?,?,?,?,?,?,?,?) WHERE user_name = ? AND id = ?");
				PreparedStatement insertDepthOffset = conn
						.prepareStatement(
								"UPDATE depthsensor SET" +
										"(" +
										"x, " +
										"y, " +
										"z, " +
										"sensorid, " +
										"manufacturer, " +
										"model, " +
										"frequency, " +
										"offsetkeel, " +
										"offsettype, " +
										"angleofbeam ) " +
								" = (?,?,?,?,?,?,?,?,?,?) WHERE vesselconfigid = ?");
				PreparedStatement insertSbasOffset = conn
						.prepareStatement(
								"UPDATE sbassensor SET" +
										"(" +
										"x, " +
										"y, " +
										"z, " +
										"sensorid, " +
										"manufacturer, " +
										"model ) " +
								" = (?,?,?,?,?,?) WHERE vesselconfigid = ?")						
						) 
				{
					conn.setAutoCommit(false);

					updateStatement.setString(1, vesselConfiguration.name); 
					updateStatement.setString(2,  vesselConfiguration.description); 
					updateStatement.setString(3,  vesselConfiguration.mmsi );
					updateStatement.setString(4,  vesselConfiguration.manufacturer); 
					updateStatement.setString(5,  vesselConfiguration.model );
					updateStatement.setDouble(6,  vesselConfiguration.loa );
					updateStatement.setDouble(7,  vesselConfiguration.breadth );
					updateStatement.setDouble(8,  vesselConfiguration.draft );
					updateStatement.setDouble(9,  vesselConfiguration.height );
					updateStatement.setDouble(10,  vesselConfiguration.displacement );
					updateStatement.setDouble(11,  vesselConfiguration.maximumspeed );
					updateStatement.setInt(12,  vesselConfiguration.vesselType == null ? 0 : vesselConfiguration.vesselType.ordinal() );
					updateStatement.setString(13,  username );
					updateStatement.setLong(14,  vesselConfiguration.id );
					updateStatement.execute();
					if(vesselConfiguration.depthoffset != null) {
						insertDepthOffset.setDouble(1, vesselConfiguration.depthoffset.distanceFromCenter);
						insertDepthOffset.setDouble(2, vesselConfiguration.depthoffset.distanceFromStern);
						insertDepthOffset.setDouble(3, vesselConfiguration.depthoffset.distanceWaterline);
						insertDepthOffset.setString(4, vesselConfiguration.depthoffset.sensorId);
						insertDepthOffset.setString(5, vesselConfiguration.depthoffset.manufacturer);
						insertDepthOffset.setString(6, vesselConfiguration.depthoffset.model);
						insertDepthOffset.setDouble(7, vesselConfiguration.depthoffset.frequency);
						insertDepthOffset.setDouble(8, vesselConfiguration.depthoffset.offsetKeel);
						insertDepthOffset.setString(9, vesselConfiguration.depthoffset.offsetType);
						insertDepthOffset.setDouble(10, vesselConfiguration.depthoffset.angleofbeam);
						insertDepthOffset.setLong(11, vesselConfiguration.id);
						insertDepthOffset.execute();
					}

					if(vesselConfiguration.sbasoffset != null) {
						insertSbasOffset.setDouble(1, vesselConfiguration.sbasoffset.distanceFromCenter);
						insertSbasOffset.setDouble(2, vesselConfiguration.sbasoffset.distanceFromStern);
						insertSbasOffset.setDouble(3, vesselConfiguration.sbasoffset.distanceWaterline);
						insertSbasOffset.setString(4, vesselConfiguration.sbasoffset.sensorId);
						insertSbasOffset.setString(5, vesselConfiguration.sbasoffset.manufacturer);
						insertSbasOffset.setString(6, vesselConfiguration.sbasoffset.model);
						insertSbasOffset.setLong(7, vesselConfiguration.id);
						insertSbasOffset.execute();
					}
					conn.commit();
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
			throw new DatabaseException("Database unavailable"); //$NON-NLS-1$
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Database unavailable"); //$NON-NLS-1$
		}
	}

	@POST
	@Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("{id}")
	@ApiOperation(value = "Create an empty vessel configuration")
	public String createVesselConfigWithNullId(
			@javax.ws.rs.core.Context SecurityContext context,
			VesselConfiguration vesselConfiguration) {
		return createVesselConfig(context, vesselConfiguration);
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Updates a vessel configuration", notes = "Creates all the details of a vessel configuration. User may only update their own configurations")
	public String createVesselConfig(
			@javax.ws.rs.core.Context SecurityContext context,
			VesselConfiguration vesselConfiguration) {
		String username = context.getUserPrincipal().getName();
		if (!context.isUserInRole("ADMIN")
				|| vesselConfiguration.username == null) {
			vesselConfiguration.username = username;
		}
		Context initContext;
		try {
			initContext = new InitialContext();
			DataSource ds = (DataSource) initContext
					.lookup("java:/comp/env/jdbc/postgres"); //$NON-NLS-1$
			try (Connection conn = ds.getConnection()) {
				Statement createIDStatement = conn.createStatement();
				PreparedStatement insertvesselstatement = conn
						.prepareStatement(
								"INSERT INTO vesselconfiguration " +
										"(name, " +
										"description, " +
										"mmsi, " +
										"manufacturer, " +
										"model, " +
										"loa, " +
										"breadth, " +
										"draft, " +
										"height, " +
										"displacement, " +
										"maximumspeed, " +
										"type, " +
										"user_name," +
										"id)" +
								" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				PreparedStatement insertDepthOffset = conn
						.prepareStatement(
								"INSERT INTO depthsensor " +
										"(vesselconfigid, " +
										"x, " +
										"y, " +
										"z, " +
										"sensorid, " +
										"manufacturer, " +
										"model, " +
										"frequency, " +
										"offsetkeel, " +
										"offsettype, " +
										"angleofbeam)" +
								" VALUES (?,?,?,?,?,?,?,?,?,?,?)");
				PreparedStatement insertSbasOffset = conn
						.prepareStatement(
								"INSERT INTO sbassensor " +
										"(vesselconfigid, " +
										"x, " +
										"y, " +
										"z, " +
										"sensorid, " +
										"manufacturer, " +
										"model) " +
								" VALUES (?,?,?,?,?,?,?)");
				try {
					conn.setAutoCommit(false);
					ResultSet executeQuery = createIDStatement
							.executeQuery("SELECT nextval('vesselconfiguration_id_seq')"); //$NON-NLS-1$
					try {
						if (executeQuery.next()) {
							vesselConfiguration.id = executeQuery.getLong(1);
							// @formatter:off
							insertvesselstatement.setString(1, vesselConfiguration.name); 
							insertvesselstatement.setString(2,  vesselConfiguration.description); 
							insertvesselstatement.setString(3,  vesselConfiguration.mmsi );
							insertvesselstatement.setString(4,  vesselConfiguration.manufacturer); 
							insertvesselstatement.setString(5,  vesselConfiguration.model );
							insertvesselstatement.setDouble(6,  vesselConfiguration.loa );
							insertvesselstatement.setDouble(7,  vesselConfiguration.breadth );
							insertvesselstatement.setDouble(8,  vesselConfiguration.draft );
							insertvesselstatement.setDouble(9,  vesselConfiguration.height );
							insertvesselstatement.setDouble(10,  vesselConfiguration.displacement );
							insertvesselstatement.setDouble(11,  vesselConfiguration.maximumspeed );
							insertvesselstatement.setInt(12,  vesselConfiguration.vesselType == null ? 0 : vesselConfiguration.vesselType.ordinal() );
							insertvesselstatement.setString(13,  vesselConfiguration.username );
							insertvesselstatement.setLong(14,  vesselConfiguration.id);
							insertvesselstatement.execute();

							if(vesselConfiguration.depthoffset != null) {
								insertDepthOffset.setLong(1, vesselConfiguration.id);
								insertDepthOffset.setDouble(2, vesselConfiguration.depthoffset.distanceFromCenter);
								insertDepthOffset.setDouble(3, vesselConfiguration.depthoffset.distanceFromStern);
								insertDepthOffset.setDouble(4, vesselConfiguration.depthoffset.distanceWaterline);
								insertDepthOffset.setString(5, vesselConfiguration.depthoffset.sensorId);
								insertDepthOffset.setString(6, vesselConfiguration.depthoffset.manufacturer);
								insertDepthOffset.setString(7, vesselConfiguration.depthoffset.model);
								insertDepthOffset.setDouble(8, vesselConfiguration.depthoffset.frequency);
								insertDepthOffset.setDouble(9, vesselConfiguration.depthoffset.offsetKeel);
							    insertDepthOffset.setString(10, vesselConfiguration.depthoffset.offsetType);
								insertDepthOffset.setDouble(11, vesselConfiguration.depthoffset.angleofbeam);
								insertDepthOffset.execute();
							}

							if(vesselConfiguration.sbasoffset != null) {
								insertSbasOffset.setLong(1, vesselConfiguration.id);
								insertSbasOffset.setDouble(2, vesselConfiguration.sbasoffset.distanceFromCenter);
								insertSbasOffset.setDouble(3, vesselConfiguration.sbasoffset.distanceFromStern);
								insertSbasOffset.setDouble(4, vesselConfiguration.sbasoffset.distanceWaterline);
								insertSbasOffset.setString(5, vesselConfiguration.sbasoffset.sensorId);
								insertSbasOffset.setString(6, vesselConfiguration.sbasoffset.manufacturer);
								insertSbasOffset.setString(7, vesselConfiguration.sbasoffset.model);
								insertSbasOffset.execute();
							}

							conn.commit();
							return ((Long)vesselConfiguration.id).toString();
						}
					} finally {
						executeQuery.close();
					}
				} finally {
					insertDepthOffset.close();
					insertSbasOffset.close();
					insertvesselstatement.close();
					createIDStatement.close();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Database unavailable"); //$NON-NLS-1$
		} catch (NamingException e) {
			e.printStackTrace();
			throw new DatabaseException("Database unavailable"); //$NON-NLS-1$
		}
		return "-1"; //$NON-NLS-1$

	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get all vessel configurations", notes = "User may retrieve only their own vessel configurations. An admin may see all configurations")
	public Response getAll(@javax.ws.rs.core.Context SecurityContext context) {
		String username = context.getUserPrincipal().getName();
		Context initContext;
		try {
			initContext = new InitialContext();
			DataSource ds = (DataSource) initContext
					.lookup("java:/comp/env/jdbc/postgres"); //$NON-NLS-1$
			Connection conn = ds.getConnection();
			try {
				ResultSet executeQuery;
				String query = 
						"SELECT DISTINCT " + 
								"v.id, v.name, v.description, v.loa, v.breadth, v.draft, v.height, v.displacement, v.mmsi, v.manufacturer, v.model, v.maximumspeed, v.type, " +  
								"s.x, s.y, s.z, s.manufacturer, s.model, s.sensorid, " +  
								"d.x, d.y, d.z, d.manufacturer, d.model, d.sensorid, d.frequency, d.offsetkeel, d.offsettype " + 
								"FROM vesselconfiguration v LEFT JOIN depthsensor AS d ON (d.vesselconfigid = v.id) LEFT JOIN sbassensor AS s ON (s.vesselconfigid = v.id)";
				if (context.isUserInRole("ADMIN")) { //$NON-NLS-1$
					Statement statement = conn.createStatement();
					try {
						executeQuery = statement
								.executeQuery(query); //$NON-NLS-1$
						return analyzeResult(executeQuery);
					} finally {
						statement.close();
					}
				} else {
					PreparedStatement pStatement = conn
							.prepareStatement(query + " WHERE user_name= ? "); //$NON-NLS-1$
					try {
						pStatement.setString(1, username);
						executeQuery = pStatement.executeQuery();
						return analyzeResult(executeQuery);
					} finally {
						pStatement.close();
					}
				}
			} finally {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Database unavailable"); //$NON-NLS-1$
		} catch (NamingException e) {
			e.printStackTrace();
			throw new DatabaseException("Database unavailable"); //$NON-NLS-1$
		}
	}

	private Response analyzeResult(ResultSet executeQuery) throws SQLException {
		try {
			List<VesselConfiguration> list = new ArrayList<VesselConfiguration>(
					2);
			while (executeQuery.next()) {
				VesselConfiguration vc = new VesselConfiguration();
				vc.id = executeQuery.getInt("id");
				vc.name = executeQuery.getString("name");
				vc.description = executeQuery.getString("description");
				vc.loa = executeQuery.getDouble("loa");
				vc.breadth = executeQuery.getDouble("breadth");
				vc.draft = executeQuery.getDouble("draft");
				vc.height = executeQuery.getDouble("height");
				vc.displacement = executeQuery.getDouble("displacement");
				vc.mmsi = executeQuery.getString("mmsi");
				vc.manufacturer = executeQuery.getString(10);
				vc.model = executeQuery.getString(11);
				vc.maximumspeed = executeQuery.getDouble("maximumspeed");
				vc.vesselType = VesselType.values()[executeQuery.getInt("type")]; 
				vc.sbasoffset = new SBASSensor();
				vc.sbasoffset.distanceFromCenter = executeQuery.getDouble(14);
				vc.sbasoffset.distanceFromStern = executeQuery.getDouble(15);
				vc.sbasoffset.distanceWaterline = executeQuery.getDouble(16);
				vc.sbasoffset.manufacturer = executeQuery.getString(17);
				vc.sbasoffset.model = executeQuery.getString(18);
				vc.sbasoffset.sensorId = executeQuery.getString(19);

				vc.depthoffset = new DepthSensor();
				vc.depthoffset.distanceFromCenter = executeQuery.getDouble(20);
				vc.depthoffset.distanceFromStern = executeQuery.getDouble(21);
				vc.depthoffset.distanceWaterline = executeQuery.getDouble(22);
				vc.depthoffset.manufacturer = executeQuery.getString(23);
				vc.depthoffset.model = executeQuery.getString(24);
				vc.depthoffset.sensorId = executeQuery.getString(25);
				vc.depthoffset.frequency = executeQuery.getDouble(26);
				vc.depthoffset.offsetKeel = executeQuery.getDouble(27);
				vc.depthoffset.offsetType = executeQuery.getString(28);

				list.add(vc);
			}
			GenericEntity<List<VesselConfiguration>> entity = new GenericEntity<List<VesselConfiguration>>(
					list) {
			};
			Response response = Response.ok().entity(entity).build();
			return response;
		} finally {
			executeQuery.close();
		}
	}

	/**
	 * Vessel configurations may only be deleted if they are no longer used by
	 * any track (processing)
	 */
	@DELETE
	@Path("{id}")
	@ApiOperation(value = "Delete a specific vessel configuration", notes = "Users may delete only their own configuraion. Admins may delete every configuration.")
	public Response deleteVesselConfig(
			@javax.ws.rs.core.Context SecurityContext context,
			@PathParam(value = "id") String id) {
		String username = context.getUserPrincipal().getName();
		Context initContext;
		try {
			long vesselId = Long.parseLong(id);
			initContext = new InitialContext();
			DataSource ds = (DataSource) initContext
					.lookup("java:/comp/env/jdbc/postgres"); //$NON-NLS-1$
			Connection conn = ds.getConnection();
			try {
				PreparedStatement vesselconfigInUse = conn
						.prepareStatement("SELECT track_id FROM user_tracks WHERE vesselconfigid = ?"); //$NON-NLS-1$
				PreparedStatement deleteStatement = conn
						.prepareStatement("DELETE FROM vesselconfiguration WHERE id = ? AND user_name = ?"); //$NON-NLS-1$
				try {
					vesselconfigInUse.setLong(1, vesselId);
					ResultSet executeQuery = vesselconfigInUse.executeQuery();
					try {
						if (executeQuery.next()) {
							// do not delete used vesselconfigs
							return Response.serverError().build();
						}
						deleteStatement.setLong(1, vesselId);
						deleteStatement.setString(2, username);
						deleteStatement.execute();
						return Response.ok().build();
					} finally {
						executeQuery.close();
					}
				} finally {
					vesselconfigInUse.close();
					deleteStatement.close();
				}
			} finally {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Database unavailable"); //$NON-NLS-1$
		} catch (NamingException e) {
			e.printStackTrace();
			throw new DatabaseException("Database unavailable"); //$NON-NLS-1$
		} catch (NumberFormatException e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

}
