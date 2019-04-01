package com.rmsi.android.mast.domain;

import java.io.Serializable;

public class FeatureType extends RefData implements Serializable {
    public static String TABLE_NAME = "FEATURE_TYPE";

    public String getTableName(){
        return TABLE_NAME;
    }
    public FeatureType(){
        super();
    }
}
