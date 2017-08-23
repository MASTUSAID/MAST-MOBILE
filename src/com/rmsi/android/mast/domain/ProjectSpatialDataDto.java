package com.rmsi.android.mast.domain;

import com.google.android.gms.maps.model.TileOverlay;
import com.rmsi.android.mast.util.OfflineTileProvider;

public class ProjectSpatialDataDto {

	private Integer server_Pk;
	private String project_Name;
	private String file_Name;
	private String file_Ext;
	private String alias;
	private TileOverlay overlay;
	private OfflineTileProvider provider;
	
	public Integer getServer_Pk() {
		return server_Pk;
	}
	public void setServer_Pk(Integer server_Pk) {
		this.server_Pk = server_Pk;
	}
	public String getProject_Name() {
		return project_Name;
	}
	public void setProject_Name(String project_Name) {
		this.project_Name = project_Name;
	}
	public String getFile_Name() {
		return file_Name;
	}
	public void setFile_Name(String file_Name) {
		this.file_Name = file_Name;
	}
	public String getFile_Ext() {
		return file_Ext;
	}
	public void setFile_Ext(String file_Ext) {
		this.file_Ext = file_Ext;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public TileOverlay getOverlay() {
		return overlay;
	}
	public void setOverlay(TileOverlay overlay) {
		this.overlay = overlay;
	}
	public OfflineTileProvider getProvider() {
		return provider;
	}
	public void setProvider(OfflineTileProvider provider) {
		this.provider = provider;
	}
}
