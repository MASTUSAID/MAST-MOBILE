package com.rmsi.android.mast.domain;

import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.StringUtility;

import java.io.Serializable;

public class Village implements Serializable {
    private Integer id;
    private String name;
    private String nameEn;

    public static String TABLE_NAME = "VILLAGE";
    public static String COL_ID = "ID";
    public static String COL_NAME = "NAME";
    public static String COL_NAME_EN = "NAME_EN";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public Village(){

    }

    @Override
    public String toString(){
        if(CommonFunctions.getInstance().getLocale().equalsIgnoreCase("en")){
            return StringUtility.empty(getNameEn());
        }
        return StringUtility.empty(getName());
    }
}
