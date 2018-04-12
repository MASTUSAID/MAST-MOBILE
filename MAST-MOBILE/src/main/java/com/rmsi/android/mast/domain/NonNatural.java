package com.rmsi.android.mast.domain;

import java.io.Serializable;

/**
 * Created by Ambar.Srivastava on 4/5/2018.
 */

public class NonNatural implements Serializable {

    private String value;
    transient private Long featureId;
    transient private String name;
    private Long id;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    private Long groupId;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static String TABLE_NAME = "NON_NATURAL";

    public static String COL_VALUE_ATTRIBUTE_ID = "ATTRIB_ID";
    public static String COL_VALUE_VALUE = "ATTRIB_VALUE";
    public static String COL_VALUE_FEATURE_ID = "FEATURE_ID";
    public static String COL_VALUE_LABEL_NAME = "LABEL_NAME";
    public static String COL_VALUE_GROUP_ID = "GROUP_ID";

}
