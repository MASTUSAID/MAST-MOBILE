package com.rmsi.android.mast.domain;

import java.io.Serializable;

public class ShareType extends RefData implements Serializable {

//    "Customary(Individual)"
//            "Customary(Collective)"
//            "Single Tenancy "
//            "Joint Tenency "
//            "Common Tenancy "
//            "Collective Tenancy "


    public static String TABLE_NAME = "SHARE_TYPE";
//    public static int TYPE_MUTIPLE_OCCUPANCY_IN_COMMON = 1;
//    public static int TYPE_SINGLE_OCCUPANT = 2;
//    public static int TYPE_MUTIPLE_OCCUPANCY_JOINT = 3;
//    public static int TYPE_TENANCY_IN_PROBATE = 4;
//    public static int TYPE_GUARDIAN = 5;
//    public static int TYPE_NON_NATURAL = 6;
public static int TYPE_MUTIPLE_OCCUPANCY_IN_COMMON = 10;
    public static int TYPE_SINGLE_OCCUPANT = 20;
    public static int TYPE_MUTIPLE_OCCUPANCY_JOINT = 30;
    public static int TYPE_TENANCY_IN_PROBATE = 40;
    public static int TYPE_GUARDIAN = 50;
    public static int TYPE_NON_NATURAL = 60;


    public static int TYPE_Customary_Individual= 4;
    public static int TYPE_Customary_Collective = 5;
    public static int TYPE_Single_Tenancy = 6;
    public static int TYPE_Joint_Tenency= 7;
    public static int TYPE_Common_Tenancy = 8;
    public static int TYPE_Collective_Tenancy = 9;

    public String getTableName(){
        return TABLE_NAME;
    }

    public ShareType(){
        super();
    }
}
