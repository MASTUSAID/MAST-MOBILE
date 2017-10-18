package com.rmsi.android.mast.domain;

import java.io.Serializable;

public class ShareType extends RefData implements Serializable {
    public static String TABLE_NAME = "SHARE_TYPE";
    public static int TYPE_MUTIPLE_OCCUPANCY_IN_COMMON = 1;
    public static int TYPE_SINGLE_OCCUPANT = 2;
    public static int TYPE_MUTIPLE_OCCUPANCY_JOINT = 3;
    public static int TYPE_TENANCY_IN_PROBATE = 4;
    public static int TYPE_GUARDIAN = 5;
    public static int TYPE_NON_NATURAL = 6;

    public String getTableName(){
        return TABLE_NAME;
    }

    public ShareType(){
        super();
    }
}
