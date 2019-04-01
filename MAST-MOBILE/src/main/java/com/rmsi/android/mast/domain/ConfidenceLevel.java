package com.rmsi.android.mast.domain;

import java.io.Serializable;

public class ConfidenceLevel extends RefData implements Serializable {
    public static String TABLE_NAME = "CONFIDENCE_LEVEL";

    public String getTableName(){
        return TABLE_NAME;
    }
    public ConfidenceLevel(){
        super();
    }
}
