package com.rmsi.android.mast.domain;

import com.rmsi.android.mast.util.StringUtility;

import java.io.Serializable;

public class DeceasedPerson implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    transient private Long featureId;

    public static String TABLE_NAME = "DECEASED_PERSON";
    public static String COL_ID = "ID";
    public static String COL_FIRST_NAME = "FIRST_NAME";
    public static String COL_LAST_NAME = "LAST_NAME";
    public static String COL_MIDDLE_NAME = "MIDDLE_NAME";
    public static String COL_FEATURE_ID = "FEATURE_ID";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public DeceasedPerson(){

    }

    public String getFullName(){
        String name = "";
        if(!StringUtility.isEmpty(getLastName()))
            name = getLastName();
        if(!StringUtility.isEmpty(getFirstName())){
            if(name.equals(""))
                name = getFirstName();
            else
                name = name + " " + getFirstName();
        }
        if(!StringUtility.isEmpty(getMiddleName())){
            if(name.equals(""))
                name = getMiddleName();
            else
                name = name + " " + getMiddleName();
        }
        return name;
    }
}
