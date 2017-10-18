package com.rmsi.android.mast.domain;

import java.io.Serializable;

public class Gender extends RefData implements Serializable {
    public static String TABLE_NAME = "GENDER";

    public String getTableName(){
        return TABLE_NAME;
    }

    public Gender(){
        super();
    }
}
