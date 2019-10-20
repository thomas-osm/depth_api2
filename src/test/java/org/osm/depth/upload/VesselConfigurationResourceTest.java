package org.osm.depth.upload;


import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.osm.depth.upload.messages.DepthSensor;
import org.osm.depth.upload.messages.SBASSensor;
import org.osm.depth.upload.messages.VesselConfiguration;
import org.osm.depth.upload.messages.VesselType;

import junit.framework.TestCase;

public class VesselConfigurationResourceTest extends TestCase {

	private Client client;
	private WebTarget basePath;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		client = ClientBuilder.newClient();
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TestConstants.TESTUSER, TestConstants.TESTPASSWORD);
		client.register(feature);
		client.register(MOXyJsonProvider.class);
		client.register(MultiPartFeature.class);
		LoggingFilter loggingFilter = new LoggingFilter();
		client.register(loggingFilter);

		basePath = client.target("http://localhost:8100").path(TestConstants.PATH).path("api2");
	}
	
	public void testCreatDeleteRoundtrip() {
		WebTarget path = basePath.path("vesselconfig"); //$NON-NLS-1$
		
		VesselConfiguration vesselConfiguration = new VesselConfiguration();
		vesselConfiguration.name = "Dolphin Crusher 2013";
		vesselConfiguration.description = "hello";
		vesselConfiguration.mmsi = "1234567890";
		vesselConfiguration.manufacturer = "Karlsruhe Seaways";
		vesselConfiguration.model = "Lis Jolle";
		vesselConfiguration.loa = 5.00;
		vesselConfiguration.breadth = 1.75;
		vesselConfiguration.draft = 1.45;
		vesselConfiguration.height = 7.15;
		vesselConfiguration.displacement = 0.1;
		vesselConfiguration.maximumspeed = 8;
		vesselConfiguration.vesselType = VesselType.RUDDERBOAT;
		vesselConfiguration.sbasoffset = new SBASSensor();
		vesselConfiguration.sbasoffset.distanceFromCenter = -1.25;
		vesselConfiguration.sbasoffset.distanceFromStern = 1;
		vesselConfiguration.sbasoffset.distanceWaterline = -0.5;
		vesselConfiguration.sbasoffset.manufacturer = "QStarz";
		vesselConfiguration.sbasoffset.model = "BT1000X";
		vesselConfiguration.sbasoffset.sensorId = "RMC";
		vesselConfiguration.depthoffset = new DepthSensor();
		vesselConfiguration.depthoffset.distanceFromCenter = -1.25;
		vesselConfiguration.depthoffset.distanceFromStern = 2.99;
		vesselConfiguration.depthoffset.distanceWaterline = 0.05;
		vesselConfiguration.depthoffset.manufacturer = "Airmar";
		vesselConfiguration.depthoffset.model = "P39";
		vesselConfiguration.depthoffset.sensorId = "DBT";
		vesselConfiguration.depthoffset.frequency = 235.0;
		vesselConfiguration.depthoffset.offsetType = "transducer";

		// post
		Response response = path.request(MediaType.APPLICATION_JSON).post(Entity.entity(vesselConfiguration, MediaType.APPLICATION_JSON));
		assertEquals(200, response.getStatus());
		String idString =  response.readEntity(String.class);
		Long id = Long.parseLong(idString);
		
		try {

		// get
        response = path.request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, response.getStatus());
        List<VesselConfiguration> vesselConfigurations = response.readEntity(new GenericType<List<VesselConfiguration>>(){});

        for (VesselConfiguration vesselConfiguration2 : vesselConfigurations) {
			assertEquals(vesselConfiguration.name, vesselConfiguration2.name);
			assertEquals(vesselConfiguration.description, vesselConfiguration2.description);
			assertEquals(vesselConfiguration.mmsi, vesselConfiguration2.mmsi);
			assertEquals(vesselConfiguration.manufacturer, vesselConfiguration2.manufacturer);
			assertEquals(vesselConfiguration.model, vesselConfiguration2.model);
			assertEquals(vesselConfiguration.loa, vesselConfiguration2.loa);
			assertEquals(vesselConfiguration.breadth, vesselConfiguration2.breadth);
			assertEquals(vesselConfiguration.draft, vesselConfiguration2.draft);
			assertEquals(vesselConfiguration.height, vesselConfiguration2.height);
			assertEquals(vesselConfiguration.displacement, vesselConfiguration2.displacement);
			assertEquals(vesselConfiguration.maximumspeed, vesselConfiguration2.maximumspeed);
			assertEquals(vesselConfiguration.vesselType, vesselConfiguration2.vesselType);

			assertNotNull(vesselConfiguration2.sbasoffset);
			assertEquals(vesselConfiguration.sbasoffset.distanceFromCenter, vesselConfiguration2.sbasoffset.distanceFromCenter);
			assertEquals(vesselConfiguration.sbasoffset.distanceFromStern, vesselConfiguration2.sbasoffset.distanceFromStern);
			assertEquals(vesselConfiguration.sbasoffset.distanceWaterline, vesselConfiguration2.sbasoffset.distanceWaterline);
			assertEquals(vesselConfiguration.sbasoffset.manufacturer, vesselConfiguration2.sbasoffset.manufacturer);
			assertEquals(vesselConfiguration.sbasoffset.model, vesselConfiguration2.sbasoffset.model);
			assertEquals(vesselConfiguration.sbasoffset.sensorId, vesselConfiguration2.sbasoffset.sensorId);

			assertNotNull(vesselConfiguration2.depthoffset);
			assertEquals(vesselConfiguration.depthoffset.distanceFromCenter, vesselConfiguration2.depthoffset.distanceFromCenter);
			assertEquals(vesselConfiguration.depthoffset.distanceFromStern, vesselConfiguration2.depthoffset.distanceFromStern);
			assertEquals(vesselConfiguration.depthoffset.distanceWaterline, vesselConfiguration2.depthoffset.distanceWaterline);
			assertEquals(vesselConfiguration.depthoffset.manufacturer, vesselConfiguration2.depthoffset.manufacturer);
			assertEquals(vesselConfiguration.depthoffset.model, vesselConfiguration2.depthoffset.model);
			assertEquals(vesselConfiguration.depthoffset.sensorId, vesselConfiguration2.depthoffset.sensorId);
			assertEquals(vesselConfiguration.depthoffset.frequency, vesselConfiguration2.depthoffset.frequency);
			assertEquals(vesselConfiguration.depthoffset.offsetType, vesselConfiguration2.depthoffset.offsetType);

        }
		} finally {
			path.path(id.toString()).request(MediaType.APPLICATION_JSON).delete();
		}
 
        
	}
	
}
