package org.osm.depth.upload.exceptions;

@SuppressWarnings("nls")
public enum ErrorCode {
	
	/** No old password supplied */
	NO_OLD_PASSWORD("100:No old password supplied"),
	/** No new password supplied */
	NO_NEW_PASSWORD("101:No new password supplied"), 
	
	OLD_PASSWORD_MISMATCH("102:Old Password mismatch"), 
	NO_SUCH_USER("103:No such user"), 
	NO_SUCH_TRACK ("104:No such track");
	
	private final String code;

	ErrorCode(String code) {
		this.code = code;
	}
	
	@Override
	public String toString() {
		return code;
	}
	
	

}
