package com.rmsi.android.mast.domain;

import java.io.Serializable;

public class RelationshipType extends RefData implements Serializable {
    public static String TABLE_NAME = "RELATIONSHIP_TYPE";

    public String getTableName(){
        return TABLE_NAME;
    }
    public RelationshipType(){
        super();
    }
}
