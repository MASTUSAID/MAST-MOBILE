package com.rmsi.android.mast.domain;

import java.io.Serializable;

/**
 * Created by Ambar.Srivastava on 1/2/2018.
 */

public class OptionAttributes  extends ResourceAttribute implements Serializable {
    public static String TABLE_NAME = "ATTRIBUTE_MASTER";
    public String getTableName(){
        return TABLE_NAME;
    }

    public OptionAttributes(){
        super();
    }
}
