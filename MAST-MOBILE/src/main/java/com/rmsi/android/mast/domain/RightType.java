package com.rmsi.android.mast.domain;

import java.io.Serializable;

public class RightType extends RefData implements Serializable {
    private int forAdjudication;
    public static String TABLE_NAME = "RIGHT_TYPE";
    public static String COL_FOR_ADJUDICATION = "FOR_ADJUDICATION";

    public String getTableName(){
        return TABLE_NAME;
    }

    public int getForAdjudication() {
        return forAdjudication;
    }

    public void setForAdjudication(int forAdjudication) {
        this.forAdjudication = forAdjudication;
    }

    public RightType(){
        super();
    }
}
