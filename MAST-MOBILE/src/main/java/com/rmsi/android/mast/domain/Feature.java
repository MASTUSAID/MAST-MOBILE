package com.rmsi.android.mast.domain;

import java.io.Serializable;

public class Feature implements Serializable {
    private Long id;
    private String coordinates;
    private String geomType;
    private String status;
    private Long serverId;
    private String polygonNumber;
    private String surveyDate;
    private int userId;

    public static String TABLE_NAME = "SPATIAL_FEATURES";
    public static String COL_ID = "FEATURE_ID";
    public static String COL_SERVER_ID = "SERVER_FEATURE_ID";
    public static String COL_COORDINATES = "COORDINATES";
    public static String COL_GEOM_TYPE = "GEOMTYPE";
    public static String COL_STATUS = "STATUS";
    public static String COL_POLYGON_NUMBER = "POLYGON_NUMBER";
    public static String COL_SURVEY_DATE = "SURVEY_DATE";

    public static String GEOM_POINT = "Point";
    public static String GEOM_LINE = "Line";
    public static String GEOM_POLYGON = "Polygon";

    public static String SERVER_STATUS_NEW = "1";
    public static String SERVER_STATUS_REFERRED = "4";
    public static String SERVER_STATUS_VALIDATED = "3";
    public static String SERVER_STATUS_APPROVED = "2";
    public static String SERVER_STATUS_REJECTED = "5";

    public static String CLIENT_STATUS_DRAFT = "draft";
    public static String CLIENT_STATUS_COMPLETE = "complete";
    public static String CLIENT_STATUS_FINAL = "final";
    public static String CLIENT_STATUS_VERIFIED = "verified";
    public static String CLIENT_STATUS_VERIFIED_AND_SYNCHED = "verified&synced";
    public static String CLIENT_STATUS_REJECTED = "rejected";
    public static String CLIENT_STATUS_DOWNLOADED = "downloaded";
    public static String CLIENT_STATUS_SYNCED = "synced";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getGeomType() {
        return geomType;
    }

    public void setGeomType(String geomType) {
        this.geomType = geomType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public String getPolygonNumber() {
        return polygonNumber;
    }

    public void setPolygonNumber(String polygonNumber) {
        this.polygonNumber = polygonNumber;
    }

    public String getSurveyDate() {
        return surveyDate;
    }

    public void setSurveyDate(String surveyDate) {
        this.surveyDate = surveyDate;
    }
}

