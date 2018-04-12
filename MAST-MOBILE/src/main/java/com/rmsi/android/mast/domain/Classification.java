package com.rmsi.android.mast.domain;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.StringUtility;

import java.io.Serializable;

/**
 * Created by ambar.srivastava on 12/21/2017.
 */

public class Classification implements Serializable {

    private String id;
    private String polyType;
    private String classification;
    private String subClassi;
    private String tenureType;
    private String firstName;
    private String middleName;
    private String lastName;


    public static String TABLE_NAME = "CLASSIFICATION";

    public static String COL_CLASSIFICATION_ID = "CLASSIFICATION_ID";
    public static String COL_PLOYTYPE = "POLY_TYPE";
    public static String COL_CLASSIFICATION_NAME= "CLASSIFICATION_NAME";

    private String subclassificationId;

    public String getsubClassificationId() {
        return subclassificationId;
    }

    public void setSubClassificationId(String classificationId) {
        this.subclassificationId = classificationId;
    }






    public String getClassificationID() {
        return id;
    }

    public void setClassificationID(String id) {
        this.id = id;
    }

    public String getClassificationName() {
        return classification;
    }

    public void setClassificationName(String classification) {
        this.classification = classification;
    }

    public String getPolyName() {
        return polyType;
    }

    public void setPolyName(String polyType) {
        this.polyType = polyType;
    }


    public String getSubClassi() {
        return subClassi;
    }

    public void setSubClassi(String subClassi) {
        this.subClassi = subClassi;
    }


    public String getTenureType() {
        return tenureType;
    }

    public void setTenureType(String tenureType) {
        this.tenureType = tenureType;
    }

    public String getfirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getMiddelName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


//    public boolean validateBasicInfo(Context context, boolean showMessage) {
//        boolean result = true;
//        String errorMessage = "";
//
//        if (StringUtility.isEmpty(getClassificationName())) {
//            errorMessage = context.getResources().getString(R.string.SelectClassificationType);
//        } else if (StringUtility.isEmpty(getSubClassi())) {
//            errorMessage = context.getResources().getString(R.string.EnterClaimNumber);
//        } else if (StringUtility.isEmpty(getTenureType())) {
//            errorMessage = context.getResources().getString(R.string.SelectClaimDate);
//        }
////        else if (getHamletId() == 0L) {
////            errorMessage = context.getResources().getString(R.string.Please_select_Hamlet);
////        } else if (StringUtility.isEmpty(getAdjudicator1())) {
////            errorMessage = context.getResources().getString(R.string.Please_select_Witness_1);
////        } else if (StringUtility.isEmpty(getAdjudicator2())) {
////            errorMessage = context.getResources().getString(R.string.Please_select_Witness_2);
////        } else if (getAdjudicator1() == getAdjudicator2()) {
////            errorMessage = context.getResources().getString(R.string.Witness_1_and_Witness_2_can_not_be_same);
////        }
//
//        if (!errorMessage.equals("")) {
//            result = false;
//            if (showMessage)
//                CommonFunctions.getInstance().showToast(context, errorMessage, Toast.LENGTH_LONG, Gravity.CENTER);
//        }
//        return result;
//    }


    public Classification(){

    }

    @Override
    public String toString() {
//        if(CommonFunctions.getInstance().getLocale().equalsIgnoreCase("sw")){
//            return StringUtility.empty(getNameOtherLang());
//        }


       return StringUtility.empty(getClassificationName());
    }


}
