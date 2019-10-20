package org.osm.depth.upload.messages;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DepthSensor extends SBASSensor {
	
	public double frequency;

	public double angleofbeam;

	public double offsetKeel;
	
	public String offsetType;
}
