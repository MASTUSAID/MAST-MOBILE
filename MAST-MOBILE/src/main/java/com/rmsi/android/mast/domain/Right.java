package com.rmsi.android.mast.domain;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GuiUtility;
import com.rmsi.android.mast.util.StringUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Right implements Serializable {
    private Long id;
    transient private Long featureId;
    private int rightTypeId;
    private int shareTypeId;
    transient private Long serverId;
    private String certNumber;
    private String certDate;
    private Double juridicalArea;
    private List<Person> naturalPersons = new ArrayList<>();
    private Person nonNaturalPerson;
    private List<Attribute> attributes = new ArrayList<>();
    private int relationshipId;

    public static String TABLE_NAME = "SOCIAL_TENURE";
    public static String COL_ID = "ID";
    public static String COL_FEATURE_ID = "FEATURE_ID";
    public static String COL_SHARE_TYPE_ID = "SHARE_TYPE";
    public static String COL_SERVER_ID = "SERVER_PK";
    public static String COL_RIGHT_TYPE_ID = "RIGHT_TYPE";
    public static String COL_CERT_NUMBER = "CERT_NUMBER";
    public static String COL_CERT_DATE = "CERT_ISSUE_DATE";
    public static String COL_JURIDICAL_AREA = "JURIDICAL_AREA";
    public static String COL_RELATIONSHIP_ID = "RELATIONSHIP_ID";

    public static int RIGHT_CUSTOMARY = 2;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public int getRightTypeId() {
        return rightTypeId;
    }

    public void setRightTypeId(int rightTypeId) {
        this.rightTypeId = rightTypeId;
    }

    public int getShareTypeId() {
        return shareTypeId;
    }

    public void setShareTypeId(int shareTypeId) {
        this.shareTypeId = shareTypeId;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public String getCertNumber() {
        return certNumber;
    }

    public void setCertNumber(String certNumber) {
        this.certNumber = certNumber;
    }

    public String getCertDate() {
        return certDate;
    }

    public void setCertDate(String certDate) {
        this.certDate = certDate;
    }

    public Double getJuridicalArea() {
        return juridicalArea;
    }

    public void setJuridicalArea(Double juridicalArea) {
        this.juridicalArea = juridicalArea;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public int getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(int relationshipId) {
        this.relationshipId = relationshipId;
    }

    public List<Person> getNaturalPersons() {
        return naturalPersons;
    }

    public void setNaturalPersons(List<Person> naturalPersons) {
        this.naturalPersons = naturalPersons;
    }

    public Person getNonNaturalPerson() {
        return nonNaturalPerson;
    }

    public void setNonNaturalPerson(Person nonNaturalPerson) {
        this.nonNaturalPerson = nonNaturalPerson;
    }

    /** Returns owners count (based on person's subtype). */
    public int getOwnersCount(){
        int count = 0;
        if(getNaturalPersons() != null) {
            for (Person person : getNaturalPersons()) {
                if (person.getSubTypeId() == Person.SUBTYPE_OWNER)
                    count += 1;
            }
        }
        return count;
    }

    /** Returns administrators count (based on person's subtype). */
    public int getAdministratorCount(){
        int count = 0;
        if(getNaturalPersons() != null) {
            for (Person person : getNaturalPersons()) {
                if (person.getSubTypeId() == Person.SUBTYPE_ADMINISTRATOR)
                    count += 1;
            }
        }
        return count;
    }

    /** Returns guardian count (based on person's subtype). */
    public int getGuardianCount(){
        int count = 0;
        if(getNaturalPersons() != null) {
            for (Person person : getNaturalPersons()) {
                if (person.getSubTypeId() == Person.SUBTYPE_GUARDIAN)
                    count += 1;
            }
        }
        return count;
    }

    /** Checks for person with a given subtype. If no such person found, false is returned */
    public boolean hasPersonSubType(int subTypeId){
        if(getNaturalPersons() != null) {
            for (Person person : getNaturalPersons()) {
                if (person.getSubTypeId() == subTypeId)
                    return true;
            }
        }
        return false;
    }

    /** Checks list of natural persons to have photos */
    public boolean checkPersonsHavePhoto(){
        if(getNaturalPersons() != null) {
            for (Person person : getNaturalPersons()) {
                if (!person.hasPhoto())
                    return false;
            }
        }
        return true;
    }

    /** Returns minor count (based on person's dob and age). */
    public int getMinorCount(){
        int count = 0;
        if(getNaturalPersons() != null) {
            for (Person person : getNaturalPersons()) {
                if (person.isMinor())
                    count += 1;
            }
        }
        return count;
    }

    public Right(){

    }

    /**
     * Validates right fields
     * @param context Application context
     * @param claimTypeCode Claim type code
     * @param showMessage Flag indicating whether to show error message or not
     */
    public boolean validate(Context context, String claimTypeCode, boolean showMessage){
        boolean result = true;
        String errorMessage = "";

        if (getRightTypeId() < 1) {
            errorMessage = context.getResources().getString(R.string.SelectRightType);
        } else if (getShareTypeId() < 1) {
            errorMessage = context.getResources().getString(R.string.SelectShareType);
        } else if (claimTypeCode.equals(ClaimType.TYPE_EXISTING_CLAIM)) {
            if (StringUtility.isEmpty(getCertNumber())) {
                errorMessage = context.getResources().getString(R.string.FillCertNumber);
            } else if (StringUtility.isEmpty(getCertDate())) {
                errorMessage = context.getResources().getString(R.string.SelectCertDate);
            } else if (getJuridicalArea() == null || getJuridicalArea() == 0) {
                errorMessage = context.getResources().getString(R.string.FillJuridicalArea);
            }
        } else if(getShareTypeId() == ShareType.TYPE_MUTIPLE_OCCUPANCY_JOINT && getRelationshipId() < 1){
            errorMessage = context.getResources().getString(R.string.SelectRelationshipType);
        }

        // Attributes
        if(errorMessage.equals("")) {
            if (getAttributes() == null || getAttributes().size() < 1) {
                List<Attribute> attrs = DbController.getInstance(context).getAttributesByType(Attribute.TYPE_TENURE);
                if (attrs != null && attrs.size() > 0) {
                    errorMessage = context.getResources().getString(R.string.FillRequiredFieldsOnRight);
                }
            } else if (!GuiUtility.validateAttributes(getAttributes(), showMessage)) {
                errorMessage = context.getResources().getString(R.string.FillRequiredFieldsOnRight);
            }
        }

        if(!errorMessage.equals("")){
            result = false;
            if(showMessage)
                CommonFunctions.getInstance().showToast(context, errorMessage, Toast.LENGTH_LONG, Gravity.CENTER);
        }
        return result;
    }
}
