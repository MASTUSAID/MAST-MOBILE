package com.rmsi.android.mast.domain;

import java.io.Serializable;

public class PersonOfInterest implements Serializable {
    private Long id;
    transient private Long featureId;
    private String name;
    private String dob;
    private int genderId;
    private int relationshipId;

    public static String TABLE_NAME = "NEXT_KIN_DETAILS";
    public static String COL_ID = "ID";
    public static String COL_NAME = "NEXT_KIN_NAME";
    public static String COL_DOB = "DOB";
    public static String COL_GENDER_ID = "GENDER_ID";
    public static String COL_FEATURE_ID = "FEATURE_ID";
    public static String COL_RELATIONSHIP_ID = "RELATIONSHIP_ID";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public int getGenderId() {
        return genderId;
    }

    public void setGenderId(int genderId) {
        this.genderId = genderId;
    }

    public int getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(int relationshipId) {
        this.relationshipId = relationshipId;
    }

    public PersonOfInterest(){

    }
}
