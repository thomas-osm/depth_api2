package org.osm.depth.upload.messages;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="tide")
public class RoleChange {

	public String user_name;

	public String requestedRole;
}
