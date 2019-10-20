package org.osm.depth.upload.resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import net.sf.seesea.tidemodel.dtu10.JDummy;

import org.osm.depth.upload.exceptions.DatabaseException;
import org.osm.depth.upload.messages.LengthUnit;
import org.osm.depth.upload.messages.Tide;
import org.osm.depth.upload.messages.TideLevel;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

//@Path("/tide")
//@Api(tags = {"Tide"})
public class TideResource {
	
	
	@ApiOperation(value = "Gets tide heights", notes = "Based on the DTU10 worldwide tide model returns the height of the tide above lowest astronomical tide (LAT)")
	@GET
	public List<Tide> getTideHeights(@QueryParam("lat") double lat, @QueryParam("lon") double lon,@QueryParam("tidelevel") TideLevel level, @QueryParam("startdate") long startDate, @QueryParam("enddate") long endDate, @QueryParam("unit") @DefaultValue("METERS") LengthUnit unit) {
		JDummy tidePredictor = new JDummy();
		List<Tide> list = new ArrayList<Tide>();
		Context initContext;
		try {
			initContext = new InitialContext();
			DataSource ds = (DataSource)initContext.lookup("java:/comp/env/jdbc/postgres"); //$NON-NLS-1$
			Connection conn = ds.getConnection();
			try {
				Statement statement = conn.createStatement();
				try {
					long timespan = new Date(endDate).getTime() - new Date(startDate).getTime();
					for(long i = 0; i < timespan ; i = i + 60000L ) {
						Date date = new Date(startDate +  i);

						
						double tideHeight = tidePredictor.getTideHeight(lat, lon, date);
						Tide tide = new Tide();
						tide.timestamp = date;
						if(TideLevel.MEANSEALEVEL.equals(level)) {
							switch (unit) {
							case FEET:
								tide.value = (float) (3.2808399 * tideHeight / 100);
								break;
							default:
								tide.value = (float) tideHeight / 100;
								break;
							}
						} else {
							double x1 = lon - Math.floor(lon);
							int x2 = (int) (x1 / 0.125);
							double a1 = x2 * 0.125 + Math.floor(lon);
							double a2 = (x2+1) * 0.125 + Math.floor(lon);
							
							double y1 = lat - Math.floor(lat) ;
							int y2 = (int) (y1 / 0.125);
							double b1 = y2 * 0.125 + Math.floor(lat);
							double b2 = (y2+1) * 0.125 + Math.floor(lat);
							
							List<Double> points = new ArrayList<Double>(4);
							PreparedStatement preparedStatement = null;
							ResultSet resultSet = null;
							try {
								preparedStatement = conn.prepareStatement("SELECT latoffset FROM msl2lat WHERE geom = ST_GeomFromText(?, 4326)"); //$NON-NLS-1$
								preparedStatement.setString(1, MessageFormat.format("Point({0} {1})", a1,
										b1));
								resultSet = preparedStatement.executeQuery();
								while(resultSet.next()) {
									points.add(resultSet.getDouble(1)); 
								}
								preparedStatement.setString(1, MessageFormat.format("Point({0} {1})", a1,
										b2));
								resultSet = preparedStatement.executeQuery();
								while(resultSet.next()) {
									points.add(resultSet.getDouble(1)); 
								}
								preparedStatement.setString(1, MessageFormat.format("Point({0} {1})", a2,
										b1));
								resultSet = preparedStatement.executeQuery();
								while(resultSet.next()) {
									points.add(resultSet.getDouble(1)); 
								}
								preparedStatement.setString(1, MessageFormat.format("Point({0} {1})", a2,
										b2));
								resultSet = preparedStatement.executeQuery();
								while(resultSet.next()) {
									points.add(resultSet.getDouble(1)); 
								}
							} finally {
								if(preparedStatement != null) {
									preparedStatement.close();
								}
								if(resultSet != null) {
									resultSet.close();
								}
							}
							double average = 0.0;
							for (Double point : points) {
								average+= point;
							}
							switch (unit) {
							case FEET:
								tide.value = (float) (3.2808399 * ( tideHeight / 100 + (average / points.size()) * -1));
								break;
							default:
								tide.value =  (float) (tideHeight / 100 + (average / points.size()) * -1);
								break;
							}
						}
						list.add(tide);
					}
					
					
//					ResultSet executeQuery = statement.executeQuery("SELECT * FROM gaugemeasurement g ORDER BY time"); //$NON-NLS-1$
//					
//					while(executeQuery.next()) {
//						Tide tide = new Tide();
//						tide.value = executeQuery.getFloat("value");
//						tide.timestamp = new Date(executeQuery.getTimestamp("time").getTime());
//						list.add(tide);
//					}
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

}
