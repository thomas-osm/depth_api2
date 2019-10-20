package org.osm.depth.upload;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.osm.depth.upload.messages.Gauge;
import org.osm.depth.upload.messages.GaugeMeasurement;
import org.osm.depth.upload.messages.GaugeType;
import org.osm.depth.upload.messages.LengthUnit;

import junit.framework.TestCase;

public class GaugeMeasurementResourceTest extends TestCase {
	
	public void testCRUDRoundTrip() throws FileNotFoundException {
		Client client = ClientBuilder.newClient();
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("x", "");
		client.register(feature);
		client.register(MOXyJsonProvider.class);
		client.register(MultiPartFeature.class);

		LoggingFilter loggingFilter = new LoggingFilter();
		client.register(loggingFilter);
		
		WebTarget basePath = client.target("http://localhost:8100").path(TestConstants.PATH).path("api2");
		WebTarget gaugepath = basePath.path("gauge");
		
		Gauge gauge = new Gauge();
		gauge.name = "Maxau";
		gauge.latitude = 49.0;
		gauge.longitude = 8.0;
		gauge.gaugeType = GaugeType.RIVER;
		
		Response response = gaugepath.request(MediaType.APPLICATION_JSON).post(Entity.entity(gauge, MediaType.APPLICATION_JSON));
		assertEquals(200, response.getStatus());
		
        response = gaugepath.request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, response.getStatus());
        List<Gauge> gauges = response.readEntity(new GenericType<List<Gauge>>(){});
        Gauge gauge2 = gauges.get(0);
        WebTarget gaugeIDPath = gaugepath.path(new Long(gauge2.id).toString());
        WebTarget measurmentPath = gaugeIDPath.path("measurement");
        
        Calendar calendar = Calendar.getInstance();
        float value = 4.4f;
        for(int i = 0 ; i < 3 ;i++) {
        	GaugeMeasurement gaugeMeasurement = new GaugeMeasurement();
        	gaugeMeasurement.gaugeId = gauge2.id;
        	gaugeMeasurement.timestamp = calendar.getTime().getTime();
        	gaugeMeasurement.lengthUnit = LengthUnit.METERS;
        	gaugeMeasurement.value = value;
        	calendar.add(Calendar.HOUR, 1);
        	value += 0.1;
        	measurmentPath.request(MediaType.APPLICATION_JSON).post(Entity.entity(gaugeMeasurement, MediaType.APPLICATION_JSON));
        }
        
        Response measurementResponse = measurmentPath.request(MediaType.APPLICATION_JSON).get();
        List<GaugeMeasurement> measurements = measurementResponse.readEntity(new GenericType<List<GaugeMeasurement>>(){});
        calendar.add(Calendar.HOUR, -3);
        value = 4.4f;
        for(int i = 2 ; i >= 0 ;i--) {
        	GaugeMeasurement gaugeMeasurement = measurements.get(i);
        	assertEquals(gauge2.id, gaugeMeasurement.gaugeId);
        	assertEquals(LengthUnit.METERS, gaugeMeasurement.lengthUnit);
        	assertEquals(value, gaugeMeasurement.value, 0.0001);
        	assertTrue(calendar.getTime().getTime() == gaugeMeasurement.timestamp);
        	calendar.add(Calendar.HOUR, 1);
        	value += 0.1;
        }
        WebTarget deletePath = gaugepath.path("/" + new Long(gauge2.id).toString()); //$NON-NLS-1$
    
        // deleting measurements
        calendar.add(Calendar.HOUR, -3);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        for(int i = 0 ; i < 3 ;i++) {
        	WebTarget measurementDelete = deletePath.path("measurement/" + dateFormat.format(calendar.getTime()));
        	measurementDelete.request().delete();
        	calendar.add(Calendar.HOUR, 1);
        }
        
        // there are no more measurements for this gauge
        measurementResponse = measurmentPath.request(MediaType.APPLICATION_JSON).get();
        measurements = measurementResponse.readEntity(new GenericType<List<GaugeMeasurement>>(){});
        assertTrue("There should be any measurements left",measurements.isEmpty());
        
        response = deletePath.request().delete();
        assertEquals(200, response.getStatus());

        response = gaugepath.request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, response.getStatus());
        gauges = response.readEntity(new GenericType<List<Gauge>>(){});
        assertTrue(gauges.isEmpty());
	}
}
