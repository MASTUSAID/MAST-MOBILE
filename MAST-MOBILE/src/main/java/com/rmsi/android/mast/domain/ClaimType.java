package com.rmsi.android.mast.domain;

import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.StringUtility;

import java.io.Serializable;

public class ClaimType implements Serializable {
    private String code;
    private String name;
    private String nameOtherLang;

    public static String TABLE_NAME = "CLAIM_TYPE";
    public static String TYPE_NEW_CLAIM = "newClaim";
    public static String TYPE_EXISTING_CLAIM = "existingClaim";
    public static String TYPE_UNCLAIMED = "unclaimed";
    public static String TYPE_DISPUTE = "dispute";

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameOtherLang() {
        return nameOtherLang;
    }

    public void setNameOtherLang(String nameOtherLang) {
        this.nameOtherLang = nameOtherLang;
    }

    public ClaimType(){

    }

    @Override
    public String toString(){
        if(CommonFunctions.getInstance().getLocale().equalsIgnoreCase("sw")){
            return StringUtility.empty(getNameOtherLang());
        }
        return StringUtility.empty(getName());
    }
}
