package org.osm.depth.upload.messages;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "License", description = "Describes a license to be used for uploading tracks")
@XmlRootElement(name="license")
public class License {

	@ApiModelProperty("A unique license id")
	public long id;
	
	@ApiModelProperty("The full name of the license")
	public String name;
	
	@ApiModelProperty("An abbreviation of the license name")
	public String shortName;
	
	@ApiModelProperty("The license text")
	public String text;
	
	/** if null this license may be publically used and only be changed by the admin and the owner */
	@ApiModelProperty("Unused")
	public String user;
	
	/** License may be used by others */
	@ApiModelProperty("License may used by anyone")
	public boolean publicLicense;
}
