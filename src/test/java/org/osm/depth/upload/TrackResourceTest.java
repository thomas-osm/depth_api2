package org.osm.depth.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.media.multipart.Boundary;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.osm.depth.upload.messages.License;
import org.osm.depth.upload.messages.Track;
import org.osm.depth.upload.messages.VesselConfiguration;

import junit.framework.TestCase;

public class TrackResourceTest extends TestCase {

	public void testX() {
		Client client = ClientBuilder.newClient();
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TestConstants.TESTUSER, TestConstants.TESTPASSWORD);
		client.register(feature);

		WebTarget basePath = client.target(TestConstants.HOST).path(TestConstants.PATH).path(TestConstants.APIPATH);
		WebTarget target = basePath.path("track");
		
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_XML);
		 
		Response response = invocationBuilder.get();
		Set<Link> links = response.getLinks();
//		URI uri = links.iterator().next().getUri();
		System.out.println(response.getStatus());
		System.out.println(response.readEntity(String.class));
//		System.out.println(uri);

	}
	
	public void testTrackUploadRoundTrip() throws FileNotFoundException {
		Client client = ClientBuilder.newClient();
		client.register(MOXyJsonProvider.class);
		client.register(MultiPartFeature.class);
		LoggingFilter loggingFilter = new LoggingFilter();
		client.register(loggingFilter);

		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TestConstants.TESTUSER, TestConstants.TESTPASSWORD);
		client.register(feature);
		WebTarget basePath = client.target(TestConstants.HOST).path(TestConstants.PATH).path(TestConstants.APIPATH);
		
		Long vesselId = createVessel(basePath);
		try {
			Long licenseId = createLicense(basePath);
			try {
				WebTarget trackPath = basePath.path("track");
				Track track3 = new Track();
				track3.vesselconfigid = vesselId;
				track3.license = licenseId;
				track3.fileName = "mytrack";
				Response response = trackPath.request(MediaType.APPLICATION_JSON).post(Entity.entity(track3, MediaType.APPLICATION_JSON));
				assertEquals(200, response.getStatus());
				
				Track track = response.readEntity(Track.class);
				
				FormDataMultiPart multipart = new FormDataMultiPart();
				
//        multipart.field("track", "text.txt");
				multipart.field("id", new Long(track.id).toString());
				
				File file = new File("res/Test.txt");
				// 
				multipart.bodyPart(new FormDataBodyPart("track", file,MediaType.MULTIPART_FORM_DATA_TYPE)).bodyPart(new StreamDataBodyPart("track", new FileInputStream(file))).type(new MediaType("multipart", "form-data",
						Collections.singletonMap(Boundary.BOUNDARY_PARAMETER, Boundary.createBoundary())));
				
//        form.bodyPart(new FileDataBodyPart("file", file, MediaType.MULTIPART_FORM_DATA_TYPE));
				response = trackPath.request(MediaType.APPLICATION_JSON).put(Entity.entity(multipart, multipart.getMediaType()));
				assertEquals(200, response.getStatus());
				
				response = trackPath.request(MediaType.APPLICATION_JSON).get();
				assertEquals(200, response.getStatus());
				List<Track> tracks = response.readEntity(new GenericType<List<Track>>(){});
				for (Track track2 : tracks) {
					if(track2.id == track.id) {
//				assertEquals(track.file_ref,"test.txt");
						assertEquals(track2.upload_state,1);
						assertEquals(track2.fileName,"mytrack");
						assertTrue(new Date(track2.uploadDate).before(Calendar.getInstance().getTime()));
						break;
					}
				}
				
				WebTarget deletePath = trackPath.path("/" + new Long(track.id).toString()); //$NON-NLS-1$
				response = deletePath.request().delete();
				assertEquals(200, response.getStatus());

			} finally {
				deleteLicense(basePath, licenseId);
			}
		} finally {
			deleteVessel(basePath, vesselId);
		}
		return;
		
	}

	private Long createLicense(WebTarget basePath) {
		WebTarget licensePath = basePath.path("license"); //$NON-NLS-1$
		License license3 = new License();
		license3.name = "FreeBSD V1";
		license3.text = "Text";
		license3.shortName = "Text";
		Response response = licensePath.request(MediaType.APPLICATION_JSON).post(Entity.entity(license3, MediaType.APPLICATION_JSON));
		assertEquals(200, response.getStatus());
		String idString =  response.readEntity(String.class);
		Long id = Long.parseLong(idString);
		return id;
	}

	private void deleteLicense(WebTarget basePath, Long id) {
		WebTarget licensePath = basePath.path("license"); //$NON-NLS-1$
		licensePath.path(id.toString()).request(MediaType.APPLICATION_JSON).delete();
	}

	private void deleteVessel(WebTarget basePath, Long vesselId) {
		WebTarget vesselPath = basePath.path("vesselconfig"); //$NON-NLS-1$
		vesselPath.path(vesselId.toString()).request(MediaType.APPLICATION_JSON).delete();
	}

	private Long createVessel(WebTarget basePath) {
		WebTarget vesselPath = basePath.path("vesselconfig"); //$NON-NLS-1$
		VesselConfiguration vesselConfiguration = new VesselConfiguration();
		vesselConfiguration.name = "Dolphin Crusher 2013";

		Response response = vesselPath.request(MediaType.APPLICATION_JSON).post(Entity.entity(vesselConfiguration, MediaType.APPLICATION_JSON));
		assertEquals(200, response.getStatus());
		String idString =  response.readEntity(String.class);
		Long id = Long.parseLong(idString);
		return id;
	}
}
