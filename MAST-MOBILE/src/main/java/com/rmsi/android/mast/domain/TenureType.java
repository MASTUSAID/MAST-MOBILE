package com.rmsi.android.mast.domain;

import java.io.Serializable;

/**
 * Created by Ambar.Srivastava on 1/12/2018.
 */

public class TenureType  extends ResourceAttribute implements Serializable {
    public static String TABLE_NAME = "TENURE_TYPE";
    public String getTableName(){
        return TABLE_NAME;
    }

    public TenureType(){
        super();
    }
}