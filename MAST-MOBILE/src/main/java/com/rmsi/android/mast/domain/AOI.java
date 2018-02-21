package com.rmsi.android.mast.domain;

import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.StringUtility;

import java.io.Serializable;

/**
 * Created by Ambar.Srivastava on 1/19/2018.
 */

public class AOI implements Serializable {

    public static String TABLE_NAME = "AOI";

    public static String COL_AOI_ID = "AOI";
    public static String COL_USERID = "USERID";

    public static String COL_PROJECTNAME_ID = "PROJECTNAME_ID";
    public static String COL_COORDINATES = "COORDINATES";
    public static String COL_ISACTIVE = "ISACTIVE";
    public static String COL_AOINAME = "AOINAME";


    private String aoiID;
    private int userID;
    private int projectID;
    private String coOrdinates;
    private String active;
    private String aoiName;

    public String getAoiName() {
        return aoiName;
    }

    public void setAoiName(String aoiName) {
        this.aoiName = aoiName;
    }



    public String getAoiID() {
        return aoiID;
    }

    public void setAoiID(String aoiID) {
        this.aoiID = aoiID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public String getCoOrdinates() {
        return coOrdinates;
    }

    public void setCoOrdinates(String coOrdinates) {
        this.coOrdinates = coOrdinates;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    @Override
    public String toString(){
        if(CommonFunctions.getInstance().getLocale().equalsIgnoreCase("sw")){
            return StringUtility.empty(getAoiName());
        }
        return StringUtility.empty(getAoiName());
    }



}
