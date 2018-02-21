package com.rmsi.android.mast.domain;

import com.rmsi.android.mast.util.StringUtility;

import java.io.Serializable;

/**
 * Created by Ambar.Srivastava on 12/29/2017.
 */

public abstract class ResourceAttribute implements Serializable {


    private String attribID;

    private String attribValue;

   // public static String TABLE_NAME = "RESOURCE_BASISC_ATTRIBUTES";


    public static String COL_ATTRIBVALUE = "ATTRIVALUE";
    public static String COL_ATTRIBID= "ATTRIID";



    public String getAttribID() {
        return attribID;
    }

    public void setAttribID(String attribID) {
        this.attribID = attribID;
    }

    public String getAttribValue() {
        return attribValue;
    }

    public void setAttribValue(String atrribValue) {
        this.attribValue = atrribValue;
    }

    abstract public String getTableName();


    public ResourceAttribute(){

    }
    @Override
    public String toString() {
//        if(CommonFunctions.getInstance().getLocale().equalsIgnoreCase("sw")){
//            return StringUtility.empty(getNameOtherLang());
//        }


        return StringUtility.empty(getAttribValue());
    }

}
