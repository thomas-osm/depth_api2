/**
 * 
 Copyright (c) 2010-2013, Jens K�bler All rights reserved.
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
package org.osm.depth.upload;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.osm.depth.upload.resources.GaugeMeasurementResource;
import org.osm.depth.upload.resources.GaugeResource;
import org.osm.depth.upload.resources.LicenseResource;
import org.osm.depth.upload.resources.StatsResource;
import org.osm.depth.upload.resources.TideResource;
import org.osm.depth.upload.resources.TrackResource;
import org.osm.depth.upload.resources.UserResource;
import org.osm.depth.upload.resources.VesselConfigurationResource;

import io.swagger.jaxrs.config.BeanConfig;

/**
 * The core jax-rs class to register all resources and features
 */
public class OSMAPIApplication extends Application {

	public OSMAPIApplication() {
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("2.0.0");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("depth.openseamap.org:8080");
        beanConfig.setBasePath("/org.osm.depth.upload/api2");
        beanConfig.setResourcePackage(TideResource.class.getPackage().getName());
        beanConfig.setScan(true);	}
	
	@Override
    public Set<Class<?>> getClasses() {
	    HashSet<Class<?>> set = new HashSet<Class<?>>(2);
	    // apidoc
        set.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        set.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        set.add(MOXyJsonProvider.class);
	    set.add(TrackResource.class);
	    set.add(UserResource.class);
	    set.add(GaugeResource.class);
	    set.add(LicenseResource.class);
	    set.add(StatsResource.class);
	    set.add(TideResource.class);
	    set.add(GaugeMeasurementResource.class);
	    set.add(VesselConfigurationResource.class);
	    set.add(MultiPartFeature.class);
	    set.add(RolesAllowedDynamicFeature.class);
	    
    return set;
	}
}
