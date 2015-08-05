package com.rmsi.android.mast.domain;

public class Feature {
	private Long featureid;
	private String coordinates;
	private String geomtype;
	private String status;
	private String server_featureid;

	public String getCoordinates() {
		return coordinates;
	}

	public Long getFeatureid() {
		return featureid;
	}

	public String getGeomtype() {
		return geomtype;
	}

	public String getServer_featureid() {
		return server_featureid;
	}

	public String getStatus() {
		return status;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public void setFeatureid(Long featureid) {
		this.featureid = featureid;
	}

	public void setGeomtype(String geomtype) {
		this.geomtype = geomtype;
	}

	public void setServer_featureid(String server_featureid) {
		this.server_featureid = server_featureid;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
