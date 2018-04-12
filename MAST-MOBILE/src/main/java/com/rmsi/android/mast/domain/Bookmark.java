package com.rmsi.android.mast.domain;

public class Bookmark 
{
	private String name;
	private Float zoomlevel;
	private Double latitude;
	private Double longitude;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Float getZoomlevel() {
		return zoomlevel;
	}
	public void setZoomlevel(Float zoomlevel) {
		this.zoomlevel = zoomlevel;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double logitude) {
		this.longitude = logitude;
	}
}