package com.rmsi.android.mast.domain;

import java.io.Serializable;

/**
 * Created by Ambar.Srivastava on 12/29/2017.
 */

public class SubClassificationAttribute extends ResourceAttribute implements Serializable {
    public static String TABLE_NAME = "SUB_CLASSIFICATION";
    public String getTableName(){
        return TABLE_NAME;
    }

    public SubClassificationAttribute(){
        super();
    }
}
