package com.rmsi.android.mast.domain;

public class Media 
{
//UserId INTEGER PRIMARY KEY AUTOINCREMENT, UserName TEXT, Password TEXT
	private Long featureId; 

	private String mediaPath; 

	private int mediaId;
	
	private String mediaType;
	
	
	public int getMediaId() {
		return mediaId;
	}

	public void setMediaId(int MediaId) {
		this.mediaId = MediaId;
	}
	public Long getFeatureId() {
		return featureId;
	}

	public void setFeatureId(Long FeatureId) {
		this.featureId = FeatureId;
	}

	public String getMediaPath() {
		return mediaPath;
	}

	public void setMediaPath(String MediaPath) {
		this.mediaPath = MediaPath;
	}

	

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String MediaType) {
		this.mediaType = MediaType;
	}

	
}
