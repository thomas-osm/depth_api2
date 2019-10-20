package org.osm.depth.upload.messages;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="tide")
public class Tide {

	public float value;
	

	/** time since 1.1.1970 UTC */
	public Date timestamp;
	
	
}
