package com.rmsi.android.mast.domain;

import com.rmsi.android.mast.util.StringUtility;

import java.io.Serializable;

/**
 * Created by Ambar.Srivastava on 12/28/2017.
 */

public class SubClassification implements Serializable {

    private String subClassi;
    private String subclassificationId;

    public String getSubClassi() {
        return subClassi;
    }

    public void setSubClassi(String subClassi) {
        this.subClassi = subClassi;
    }

    public String getSubclassificationId() {
        return subclassificationId;
    }

    public void setSubclassificationId(String subclassificationId) {
        this.subclassificationId = subclassificationId;
    }

    @Override
    public String toString() {
//        if(CommonFunctions.getInstance().getLocale().equalsIgnoreCase("sw")){
//            return StringUtility.empty(getNameOtherLang());
//        }
//        if (getClassificationName() != null) {
//            return StringUtility.empty(getClassificationName());
//        }

        return StringUtility.empty(getSubClassi());
    }

}
