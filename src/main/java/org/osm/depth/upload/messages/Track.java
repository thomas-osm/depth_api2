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
package org.osm.depth.upload.messages;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Track", description = "This describes tracks")
@XmlRootElement(name="track")
public class Track {
	
	@ApiModelProperty("System wide unique track id")
	public long id;

	@ApiModelProperty("The track file name")
	public String fileName;

	@ApiModelProperty("Upload state")
	public int upload_state;

	@ApiModelProperty("Detected format file type, read only")
	public String fileType;

	@ApiModelProperty("Detected compression type, read only")
	public String compression;

	public String delete;
	
	@ApiModelProperty("Vessel configuration id")
	public long vesselconfigid;

	@ApiModelProperty("In case of compressed tracks lists the parent uploaded track that contained this track, otherwise 0")
	public long containertrack;
	
	@ApiModelProperty("The license id")
	public long license;
	
//	@XmlElement(name = "uploaddate", required = true)
//	@XmlJavaTypeAdapter(TimestampAdapter.class)
	@ApiModelProperty("The date of the upload in UTC milliseconds")
	public long uploadDate;
	
	@ApiModelProperty("number of points")
	public int num_points;
	
	@ApiModelProperty("processing info")
	public String track_info;

	@ApiModelProperty("bbox left")
	public float left;

	@ApiModelProperty("bbox right")
	public float right;

	@ApiModelProperty("bbox top")
	public float top;

	@ApiModelProperty("bbox bottom")
	public float bottom;
	
	public Track() {
		
	}
	
	public Track(long trackId, String name, int upload_state, String filetype, String compression, long containertrack) {
		super();
		this.id = trackId;
		this.fileName = name;
		this.upload_state = upload_state;
		this.fileType = filetype;
		this.compression = compression;
		this.containertrack = containertrack;
	}

}
