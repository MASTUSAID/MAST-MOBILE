package com.rmsi.android.mast.domain;

public class Bookmark {
	private String name;
	private Float zoomlevel;
	private Double latitude;
	private Double longitude;

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public String getName() {
		return name;
	}

	public Float getZoomlevel() {
		return zoomlevel;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(Double logitude) {
		this.longitude = logitude;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setZoomlevel(Float zoomlevel) {
		this.zoomlevel = zoomlevel;
	}
}