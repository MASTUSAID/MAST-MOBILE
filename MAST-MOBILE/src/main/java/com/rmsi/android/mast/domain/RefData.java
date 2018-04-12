package com.rmsi.android.mast.domain;

import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.StringUtility;

import java.io.Serializable;

public abstract class RefData implements Serializable {
    private int code;
    private String name;
    private String nameOtherLang;
    private int active;

    public static String COL_CODE = "CODE";
    public static String COL_NAME = "NAME";
    public static String COL_NAME_OTHER_LANG = "NAME_OTHER_LANG";
    public static String COL_ACTIVE = "ACTIVE";

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    abstract public String getTableName();

    public RefData(){

    }

    @Override
    public String toString(){
        if(CommonFunctions.getInstance().getLocale().equalsIgnoreCase("sw")){
            return StringUtility.empty(getNameOtherLang());
        }
        return StringUtility.empty(getName());
    }
}
