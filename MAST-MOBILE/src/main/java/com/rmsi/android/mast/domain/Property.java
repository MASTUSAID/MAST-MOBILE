package com.rmsi.android.mast.domain;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.DateUtility;
import com.rmsi.android.mast.util.GuiUtility;
import com.rmsi.android.mast.util.StringUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Property extends Feature implements Serializable {
    private String creationDate;
    private String completionDate;
    private String imei;
    private Long hamletId;
    private String adjudicator1;
    private String adjudicator2;
    private String claimTypeCode;
    private String ukaNumber;

    private Right right;
    private DeceasedPerson deceasedPerson;
    private List<PersonOfInterest> personOfInterests = new ArrayList<>();
    private List<Attribute> attributes = new ArrayList<>();
    private List<Media> media = new ArrayList<>();
    private Dispute dispute;

    public static String COL_CREATION_DATE = "CREATEDTIME";
    public static String COL_COMPLETION_DATE = "COMPLETEDTIME";
    public static String COL_IMEI = "IMEI";
    public static String COL_HAMLET_ID = "HAMLET_ID";
    public static String COL_ADJUDICATOR1 = "WITNESS_1";
    public static String COL_ADJUDICATOR2 = "WITNESS_2";
    public static String COL_CLAIM_TYPE_CODE = "CLAIM_TYPE";
    public static String COL_UKA_NUMBER = "UKA_NUMBER";

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public Long getHamletId() {
        return hamletId;
    }

    public void setHamletId(Long hamletId) {
        this.hamletId = hamletId;
    }

    public String getAdjudicator1() {
        return adjudicator1;
    }

    public void setAdjudicator1(String adjudicator1) {
        this.adjudicator1 = adjudicator1;
    }

    public String getAdjudicator2() {
        return adjudicator2;
    }

    public void setAdjudicator2(String adjudicator2) {
        this.adjudicator2 = adjudicator2;
    }

    public String getClaimTypeCode() {
        return claimTypeCode;
    }

    public void setClaimTypeCode(String claimTypeCode) {
        this.claimTypeCode = claimTypeCode;
    }

    public Right getRight() {
        return right;
    }

    public void setRight(Right right) {
        this.right = right;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<Media> getMedia() {
        return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }

    public String getUkaNumber() {
        return ukaNumber;
    }

    public void setUkaNumber(String ukaNumber) {
        this.ukaNumber = ukaNumber;
    }

    public DeceasedPerson getDeceasedPerson() {
        return deceasedPerson;
    }

    public void setDeceasedPerson(DeceasedPerson deceasedPerson) {
        this.deceasedPerson = deceasedPerson;
    }

    public List<PersonOfInterest> getPersonOfInterests() {
        return personOfInterests;
    }

    public void setPersonOfInterests(List<PersonOfInterest> personOfInterests) {
        this.personOfInterests = personOfInterests;
    }

    public Dispute getDispute() {
        return dispute;
    }

    public void setDispute(Dispute dispute) {
        this.dispute = dispute;
    }

    /**
     * Checks whether custom attributes exist for the property
     */
    public boolean hasCustomAttributes() {
        return getAttributes(Attribute.TYPE_CUSTOM).size() > 0;
    }

    /**
     * Checks whether general property attributes exist for the property
     */
    public boolean hasGeneralAttributes() {
        return getAttributes(Attribute.TYPE_GENERAL_PROPERTY).size() > 0;
    }

    /**
     * Returns attributes by type. Expected  Attribute.TYPE_CUSTOM or Attribute.TYPE_GENERAL_PROPERTY
     */
    public List<Attribute> getAttributes(String attributeType) {
        List<Attribute> attrs = new ArrayList<>();
        if (getAttributes() != null && getAttributes().size() > 0) {
            for (Attribute attribute : getAttributes()) {
                if (StringUtility.empty(attribute.getType()).equalsIgnoreCase(attributeType))
                    attrs.add(attribute);
            }
        }
        return attrs;
    }

    public Property() {
        super();
    }

    /**
     * Validates full property object
     *
     * @param context     Application context
     * @param showMessage Flag indicating whether to show error message or not
     */
    public boolean validateAll(Context context, boolean showMessage) {
        if(!validateBasicInfo(context, showMessage))
            return false;

        // Stop validation for unclaimed parcels
        if(StringUtility.empty(getClaimTypeCode()).equalsIgnoreCase(ClaimType.TYPE_UNCLAIMED))
            return true;

        // Disputes
        if(StringUtility.empty(getClaimTypeCode()).equalsIgnoreCase(ClaimType.TYPE_DISPUTE)) {
            if(getDispute() == null)
                return handleError(context, R.string.AddDisputeDetails, showMessage);

            return getDispute().validate(context, showMessage);
        }

        // For all other claims
        if(!validateGeneralProperties(context, showMessage))
            return false;

        // Validate right
        if(getRight() == null)
            return handleError(context, R.string.AddSocialTenureInfo, showMessage);

        if(!getRight().validate(context, getClaimTypeCode(), showMessage))
            return false;

        // Validate persons
        for(Person person : getRight().getNaturalPersons()){
            if(!person.validate(context, getRight().getShareTypeId(), false, showMessage))
                return false;
        }

        if(!validatePersonsList(context, showMessage))
            return false;

        if(!validateCustomProperties(context, showMessage))
            return true;

        return true;
    }

    /**
     * Validates basic property fields
     *
     * @param context     Application context
     * @param showMessage Flag indicating whether to show error message or not
     */
    public boolean validateBasicInfo(Context context, boolean showMessage) {
        boolean result = true;
        String errorMessage = "";

        if (StringUtility.isEmpty(getClaimTypeCode())) {
            errorMessage = context.getResources().getString(R.string.SelectClaimType);
        } else if (StringUtility.isEmpty(getPolygonNumber())) {
            errorMessage = context.getResources().getString(R.string.EnterClaimNumber);
        } else if (StringUtility.isEmpty(getSurveyDate())) {
            errorMessage = context.getResources().getString(R.string.SelectClaimDate);
        } else if (getHamletId() == 0L) {
            errorMessage = context.getResources().getString(R.string.Please_select_Hamlet);
        } else if (StringUtility.isEmpty(getAdjudicator1())) {
            errorMessage = context.getResources().getString(R.string.Please_select_Witness_1);
        } else if (StringUtility.isEmpty(getAdjudicator2())) {
            errorMessage = context.getResources().getString(R.string.Please_select_Witness_2);
        } else if (getAdjudicator1() == getAdjudicator2()) {
            errorMessage = context.getResources().getString(R.string.Witness_1_and_Witness_2_can_not_be_same);
        } else if (DateUtility.isDateInFuture(DateUtility.getDate(getSurveyDate()))) {
            errorMessage = context.getResources().getString(R.string.ClaimDateInFuture);
        } else if (!DbController.getInstance(context).isClaimNumberUnique(getId(), getPolygonNumber())) {
            errorMessage = context.getResources().getString(R.string.ClaimNumberUnique);
        }

        if (!errorMessage.equals("")) {
            result = false;
            if (showMessage)
                CommonFunctions.getInstance().showToast(context, errorMessage, Toast.LENGTH_LONG, Gravity.CENTER);
        }
        return result;
    }

    /**
     * Validates general property fields
     *
     * @param context     Application context
     * @param showMessage Flag indicating whether to show error message or not
     */
    public boolean validateGeneralProperties(Context context, boolean showMessage) {
        boolean result = true;
        String errorMessage = "";

        if (getAttributes(Attribute.TYPE_GENERAL_PROPERTY).size() < 1) {
            List<Attribute> attrs = DbController.getInstance(context).getAttributesByType(Attribute.TYPE_GENERAL_PROPERTY);
            if (attrs != null && attrs.size() > 0) {
                errorMessage = context.getResources().getString(R.string.FillGeneralProperties);
            }
        } else if (!GuiUtility.validateAttributes(getAttributes(Attribute.TYPE_GENERAL_PROPERTY), showMessage)) {
            errorMessage = context.getResources().getString(R.string.FillRequiredFieldsOngeneralProp);
        }

        if (!errorMessage.equals("")) {
            result = false;
            if (showMessage)
                CommonFunctions.getInstance().showToast(context, errorMessage, Toast.LENGTH_LONG, Gravity.CENTER);
        }
        return result;
    }

    /**
     * Validates custom property fields
     *
     * @param context     Application context
     * @param showMessage Flag indicating whether to show error message or not
     */
    public boolean validateCustomProperties(Context context, boolean showMessage) {
        boolean result = true;
        String errorMessage = "";

        if (getAttributes(Attribute.TYPE_CUSTOM).size() < 1) {
            List<Attribute> attrs = DbController.getInstance(context).getAttributesByType(Attribute.TYPE_CUSTOM);
            if (attrs != null && attrs.size() > 0) {
                errorMessage = context.getResources().getString(R.string.FillCustomAttributes);
            }
        } else if (!GuiUtility.validateAttributes(getAttributes(Attribute.TYPE_CUSTOM), showMessage)) {
            errorMessage = context.getResources().getString(R.string.FillCustomAttributes);
        }

        if (!errorMessage.equals("")) {
            result = false;
            if (showMessage)
                CommonFunctions.getInstance().showToast(context, errorMessage, Toast.LENGTH_LONG, Gravity.CENTER);
        }
        return result;
    }

    /**
     * Validates persons list according to the right type
     *
     * @param context     Application context
     * @param showMessage Flag indicating whether to show error message or not
     */
    public boolean validatePersonsList(Context context, boolean showMessage) {
        if (getRight() == null || getRight().getShareTypeId() < 1)
            return true;

        int shareId = getRight().getShareTypeId();

        // Check photos for all natural persons
        if (!getRight().checkPersonsHavePhoto()) {
            return handleError(context, R.string.warning_addPersonPhoto, showMessage);
        }

        // Check non-natural share type
        if (shareId == ShareType.TYPE_NON_NATURAL) {
            if (getRight().getNonNaturalPerson() == null)
                return handleError(context, R.string.AddNonNaturalPerson, showMessage);
            if(getRight().getNaturalPersons().size() < 1)
                return handleError(context, R.string.add_atlest_one_person, showMessage);
            if(getRight().getNaturalPersons().size() > 1)
                return handleError(context, R.string.can_add_only_one_person_with_non_natural_person, showMessage);
        }

        // Check single ownership
        if (shareId == ShareType.TYPE_SINGLE_OCCUPANT) {
            if(getRight().getNaturalPersons().size() != 1 || !getRight().hasPersonSubType(Person.SUBTYPE_OWNER)){
                return handleError(context, R.string.SingleShareCheck, showMessage);
            }
        }

        // Check multiple occupancy joint ownership
        if (shareId == ShareType.TYPE_MUTIPLE_OCCUPANCY_JOINT) {
            if(getRight().getOwnersCount() != 2 || getRight().getNaturalPersons().size() != getRight().getOwnersCount()){
                return handleError(context, R.string.MultiJointShareError, showMessage);
            }
        }

        // Check multiple occupancy common ownership
        if (shareId == ShareType.TYPE_MUTIPLE_OCCUPANCY_IN_COMMON) {
            if(getRight().getOwnersCount() < 2  || getRight().getOwnersCount() != getRight().getNaturalPersons().size()){
                return handleError(context, R.string.MultiCommonShareError, showMessage);
            }
        }

        // Check guardian ownership
        if (shareId == ShareType.TYPE_GUARDIAN) {
            if(getRight().hasPersonSubType(Person.SUBTYPE_ADMINISTRATOR) || !getRight().hasPersonSubType(Person.SUBTYPE_OWNER)
                    || !getRight().hasPersonSubType(Person.SUBTYPE_GUARDIAN) || getRight().getGuardianCount() > 2){
                return handleError(context, R.string.GuardianShareError, showMessage);
            }
        }

        // Check tenancy in probate ownership
        if (shareId == ShareType.TYPE_TENANCY_IN_PROBATE) {
            if(getRight().hasPersonSubType(Person.SUBTYPE_GUARDIAN) || !getRight().hasPersonSubType(Person.SUBTYPE_ADMINISTRATOR)
                    || getDeceasedPerson() == null || getRight().getAdministratorCount() > 2){
                return handleError(context, R.string.ProbateShareError, showMessage);
            }
        }

        return true;
    }

    private boolean handleError(Context context, int messageId, boolean showError) {
        if (showError) {
            CommonFunctions.getInstance().showToast(context, context.getResources().getString(messageId), Toast.LENGTH_LONG, Gravity.CENTER);
        }
        return false;
    }

}
