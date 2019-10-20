package org.osm.depth.upload.messages;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Statistics")
@XmlRootElement
public class Stats {
	
	@ApiModelProperty(value= "Current users in the database")
	public int usercount;
	
	@ApiModelProperty(value= "Current tracks count in the database")
	public int trackscount;
	
	@ApiModelProperty(value= "Extracted amount of single points")
	public long contributedpoints;

}
