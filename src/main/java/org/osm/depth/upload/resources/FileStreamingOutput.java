package org.osm.depth.upload.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

public class FileStreamingOutput implements StreamingOutput {
	
	private final File file;

	public FileStreamingOutput(File file) {
		this.file = file;
	}

	@Override
	public void write(OutputStream out) throws IOException,
			WebApplicationException {
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			byte[] buffer = new byte[131072];
			int len;
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
 		} finally {
 			if(in != null) {
 				in.close();
 			}
 		}
	}

}
