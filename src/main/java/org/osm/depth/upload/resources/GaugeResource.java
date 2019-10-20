package org.osm.depth.upload.resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.osm.depth.upload.exceptions.DatabaseException;
import org.osm.depth.upload.messages.Gauge;
import org.osm.depth.upload.messages.GaugeType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/gauge")
@Api(tags = {"Gauges"})
public class GaugeResource {

	@Path("{gaugeid}/measurement")
	@ApiOperation(value = "Retrieves all measurements for a given gauge id")
	public GaugeMeasurementResource getAllGauges(@PathParam(value = "gaugeid") String gaugeId) {
		return new GaugeMeasurementResource(gaugeId);
	}
	
	@GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get a list of all gauges")
	public List<Gauge> getAllGauges() {
		Context initContext;
		try {
			initContext = new InitialContext();
			DataSource ds = (DataSource)initContext.lookup("java:/comp/env/jdbc/postgres"); //$NON-NLS-1$
			Connection conn = ds.getConnection();
//			PGConnection pgCon =(PGConnection)((DelegatingConnection)conn).getInnermostDelegate();
//			pgCon.addDataType("geometry",org.postgis.PGgeometry.class);
			try {
				Statement statement = conn.createStatement();
				try {
					ResultSet executeQuery;
					executeQuery = statement.executeQuery("SELECT id, name, gaugetype, waterlevel, ST_AsText(geom) as geom FROM gauge g"); //$NON-NLS-1$
					List<Gauge> list = new ArrayList<Gauge>();
					while(executeQuery.next()) {
						Gauge gauge = new Gauge();
						gauge.id = executeQuery.getLong("id"); //$NON-NLS-1$
						gauge.name = executeQuery.getString("name"); //$NON-NLS-1$
						// this is not the way to do it but the connection pool does not cause problems
						String geom = executeQuery.getString("geom");
						String point = geom.substring(geom.indexOf("(") + 1, geom.indexOf(")"));
						String[] coordinates = point.split(" ");
						
//						PGgeometry geom = (PGgeometry)executeQuery.getObject("geom");
//						gauge.latitude = ((Point)geom.getGeometry()).y; //$NON-NLS-1$
//						gauge.longitude = ((Point)geom.getGeometry()).x; //$NON-NLS-1$
						gauge.latitude = Double.parseDouble(coordinates[1].trim());
						gauge.longitude = Double.parseDouble(coordinates[0].trim());
						try {
							gauge.gaugeType = GaugeType.valueOf(executeQuery.getString("gaugetype")); //$NON-NLS-1$
						} catch (IllegalArgumentException e) {
							gauge.gaugeType = GaugeType.UNDEFINED; // no such gauge type
						}
						gauge.waterlevel = executeQuery.getDouble("waterlevel");
						list.add(gauge);
					}
					return list;
				} finally {
					statement.close();
				}
			} finally {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Internal SQL Error");
		} catch (NamingException e) {
			e.printStackTrace();
			throw new DatabaseException("Database unavailable");
		}
	}	
	
	@POST
	@Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	@RolesAllowed(value = {"USER", "ADMIN"})
	@ApiOperation(value = "Create a new gauge")
	public Gauge newGauge(@javax.ws.rs.core.Context SecurityContext context, Gauge gauge) {
		Context initContext;
		try {
			
			initContext = new InitialContext();
			DataSource ds = (DataSource)initContext.lookup("java:/comp/env/jdbc/postgres"); //$NON-NLS-1$
			Connection conn = ds.getConnection();
			try {
				Statement createIDStatement = conn.createStatement();
				try {
					PreparedStatement statement = conn.prepareStatement("INSERT INTO gauge (id, name, gaugetype, waterlevel, lat, lon, geom) VALUES (?,?,?,?, ST_SetSRID(ST_MakePoint(?, ?), 4326))"); //$NON-NLS-1$
					try {
						ResultSet executeQuery = createIDStatement.executeQuery("SELECT nextval('gauge_id_seq')"); //$NON-NLS-1$
						if(executeQuery.next()) {
							Long id = executeQuery.getLong(1);
							statement.setLong(1, id);
							statement.setString(2, gauge.name);
							statement.setString(3, gauge.gaugeType.toString());
							statement.setDouble(4, gauge.waterlevel);
							statement.setDouble(5, gauge.latitude);
							statement.setDouble(6, gauge.longitude);
							statement.setDouble(8, gauge.latitude);
							statement.setDouble(7, gauge.longitude);
							statement.execute();
							return gauge;
						} else {
							// failed to create id
						}
						throw new DatabaseException("Database unavailable"); //$NON-NLS-1$
					} finally {
						statement.close();
					}
				} finally {
					createIDStatement.close();
				}
				
			} finally {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Internal SQL Error"); //$NON-NLS-1$
		} catch (NamingException e) {
			e.printStackTrace();
			throw new DatabaseException("Database unavailable"); //$NON-NLS-1$
		}
	}
	
	@DELETE
	@Path("{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@RolesAllowed(value = {"ADMIN"})
	@ApiOperation(value = "Delete a gauge with the given id")
	public Response delete(@PathParam(value = "id") String id) {
		Context initContext;
		try {
			long gaugeId = Long.parseLong(id);
			initContext = new InitialContext();
			DataSource ds = (DataSource)initContext.lookup("java:/comp/env/jdbc/postgres"); //$NON-NLS-1$
			Connection conn = ds.getConnection();
			try {
				conn.setAutoCommit(false);
//			Statement gaugeUsedinTracks = conn.createStatement();
				PreparedStatement deletestatement = conn.prepareStatement("DELETE FROM gauge WHERE id = ?"); //$NON-NLS-1$
				try {
					deletestatement.setLong(1, gaugeId);
					deletestatement.execute();
					conn.commit();
				} finally {
					deletestatement.close();
				}
//			ResultSet usedInTracksResultSet = gaugeUsedinTracks.executeQuery("SELECT COUNT(id) FROM gaugetracks WHERE gaugeid ='" + gaugeId + "' ");
//			if(usedInTracksResultSet.next() && usedInTracksResultSet.getLong(1) > 0) {
//				throw new ResourceInUseException("License is still being used for recorded tracks");
//			}
				
				return Response.ok().build();
			} finally {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Internal SQL Error"); //$NON-NLS-1$
		} catch (NamingException e) {
			e.printStackTrace();
			throw new DatabaseException("Database unavailable"); //$NON-NLS-1$
		}
	}

}
