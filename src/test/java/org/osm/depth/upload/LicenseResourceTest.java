package org.osm.depth.upload;

import java.io.FileNotFoundException;
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
import org.osm.depth.upload.messages.License;

import junit.framework.TestCase;

public class LicenseResourceTest extends TestCase {
	
	public void testTrackUploadRoundTrip() throws FileNotFoundException {
		Client client = ClientBuilder.newClient();
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TestConstants.TESTUSER, TestConstants.TESTPASSWORD);
		client.register(feature);
		client.register(MOXyJsonProvider.class);
		client.register(MultiPartFeature.class);

		LoggingFilter loggingFilter = new LoggingFilter();
		client.register(loggingFilter);
		
		WebTarget basePath = client.target("http://localhost:8100").path(TestConstants.PATH).path("api2");
		WebTarget licensePath = basePath.path("license");
		
		License license3 = new License();
		license3.name = "FreeBSD V1";
		license3.publicLicense = true;
		license3.shortName = "FreeBSD";
		license3.text = "Text text";
//		license3.User = "x";
		
		Response response = licensePath.request(MediaType.APPLICATION_JSON).post(Entity.entity(license3, MediaType.APPLICATION_JSON));
		assertEquals(200, response.getStatus());
		
//		License license = response.readEntity(License.class);
		
//        BufferedReader bufferedReader = new BufferedReader(new FileInputStream("res/license.txt"));
//        byte buf[] = new byte[4096];
//        StringBuffer text = new StringBuffer();
//        while(bufferedReader.read(buf) != -1) {
//        	text.append(Charbuf.) 
//        }
        
        response = licensePath.request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, response.getStatus());
        List<License> licenses = response.readEntity(new GenericType<List<License>>(){});
        long licenseId = 0;
        for (License license2 : licenses) {
			assertEquals(license2.name,license3.name);
			assertEquals(license2.shortName,license3.shortName);
			assertEquals(license2.publicLicense,license3.publicLicense);
			assertEquals(license2.text,license3.text);
			assertEquals(license2.user,"x");
			assertNotSame(0, license2.id);
			licenseId = license2.id;
		}
        
        WebTarget deletePath = licensePath.path("/" + new Long(licenseId).toString()); //$NON-NLS-1$
        response = deletePath.request().delete();
        assertEquals(200, response.getStatus());

        response = licensePath.request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, response.getStatus());
        licenses = response.readEntity(new GenericType<List<License>>(){});
        assertTrue(licenses.isEmpty());
        
		return;
	}
	
//	public void testGetLicenseString() {
//		Client client = ClientBuilder.newClient();
//		client.register(new HttpBasicAuthFilter("x", "x"));
//		client.register(MOXyJsonProvider.class);
//		client.register(MultiPartFeature.class);
//
//		LoggingFilter loggingFilter = new LoggingFilter();
//		client.register(loggingFilter);
//		
//		WebTarget basePath = client.target("http://localhost:8100").path(TestConstants.PATH).path("api2");
//		WebTarget licensePath = basePath.path("license");
//		licensePath.queryParam("lat1", 10);
//		http://localhost:8100/org.osm.depth.upload/api2/license?lat1=10&lon1=10&lat2=20&lon2=20
//	}
}
