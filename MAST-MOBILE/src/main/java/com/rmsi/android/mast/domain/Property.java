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
    private List<ResourcePersonOfInterest> personOfInterestsRes1 = new ArrayList<>();
    private List<ResourcePoiSync> personOfInterestsRes = new ArrayList<>();
    private List<Attribute> attributes = new ArrayList<>();

    private List<ResourceCustomAttribute> customAttributes = new ArrayList<>();
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
    public static String COL_CLASSIFICATION_ID = "CLASSIFICATION_ID";
    public static String COL_SUBCLASSIFICATION_ID = "SUBCLASSIFICATION_ID";
    public static String COL_TENURE_ID = "TENURE_ID";
    public static String COL_FLAG = "FLAG";
    public static String COL_IP_NUMBER = "IP_NUMBER";
    public static String COL_CLAIM_RIGHT = "CLAIM_RIGHT";
    public static String COL_PLOT_NO = "PLOT_NO";
    public static String COL_DOCUMENT = "DOCUMENT";
    public static String COL_DOCUMENT_TYPE = "DOCUMENT_TYPE";
    public static String COL_DOCUMENT_DATE = "DOCUMENT_DATE";
    public static String COL_DOCUMENT_REF_NO = "DOCUMENT_REF_NO";
    public static String COL_IS_NATURAL = "IS_NATURAL";



    private List<Classification> classification=new ArrayList<>();


    private List<ClassificationAttribute> classificationAttributes=new ArrayList<>();

    private List<TenureInformation> tenureInformation=new ArrayList<>();
    private String flag;



    private int ipNumber;
     private String classificationValue;
     private String subClassificationValue;
     private  String tenureTypeValue;



     private String tenureTypeID;

    private String firstName;
    private String middleName;


    private int mID;
    private String lastName;
    private int lastID;
    private String age;
    private String gender;
    private String maritalstatus;
    private String cityzenship;
    private String ethinicity;
    private String residential;
    private int residentialID;
    private String address;
    private int addressID;
    private String country;
    private int countryID;
    private String community;
    private String region;
    private String mob_no;
    private String classificationId;
     private String subClassificationId;

    private String claimRight;
    private String plotNo;
    private String document;
    private String documentType;
    private String documentDate;
    private String documentRefNo;
    private int isNatural;

    public String getClaimRight() {
        return claimRight;
    }

    public void setClaimRight(String claimRight) {
        this.claimRight = claimRight;
    }

    public String getPlotNo() {
        return plotNo;
    }

    public void setPlotNo(String plotNo) {
        this.plotNo = plotNo;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }

    public String getDocumentRefNo() {
        return documentRefNo;
    }

    public void setDocumentRefNo(String documentRefNo) {
        this.documentRefNo = documentRefNo;
    }


    public List<ResourceCustomAttribute> getAttributesres() {
        return customAttributes;
    }

    public void setAttributesres(List<ResourceCustomAttribute> attributesres) {
        this.customAttributes = attributesres;
    }

    public int getmID() {
        return mID;
    }

    public void setmID(int mID) {
        this.mID = mID;
    }

    public int getLastID() {
        return lastID;
    }

    public void setLastID(int lastID) {
        this.lastID = lastID;
    }

    public int getResidentialID() {
        return residentialID;
    }

    public void setResidentialID(int residentialID) {
        this.residentialID = residentialID;
    }

    public int getAddressID() {
        return addressID;
    }

    public void setAddressID(int addressID) {
        this.addressID = addressID;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }


    public String getTenureTypeID() {
        return tenureTypeID;
    }

    public void setTenureTypeID(String tenureTypeID) {
        this.tenureTypeID = tenureTypeID;
    }

    public String getSubClassificationId() {
        return subClassificationId;
    }

    public void setSubClassificationId(String subClassificationId) {
        this.subClassificationId = subClassificationId;
    }


    public List<ClassificationAttribute> getClassificationAttributes() {
        return classificationAttributes;
    }

    public void setClassificationAttributes(List<ClassificationAttribute> classificationAttributes) {
        this.classificationAttributes = classificationAttributes;
    }


    public String getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(String classificationId) {
        this.classificationId = classificationId;
    }



    public String getTenureTypeValue() {
        return tenureTypeValue;
    }

    public void setTenureTypeValue(String tenureTypeValue) {
        this.tenureTypeValue = tenureTypeValue;
    }


    public String getClassificationValue() {
        return classificationValue;
    }

    public void setClassificationValue(String classificationValue) {
        this.classificationValue = classificationValue;
    }

    public String getSubClassificationValue() {
        return subClassificationValue;
    }

    public void setSubClassificationValue(String subClassificationValue) {
        this.subClassificationValue = subClassificationValue;
    }




    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getIpNumber() {
        return ipNumber;
    }

    public void setIpNumber(int ipNumber) {
        this.ipNumber = ipNumber;
    }


    public List<Classification> getClassification() {
        return classification;
    }

    public void setClassification(List<Classification> classification) {
        this.classification = classification;
    }

    public List<TenureInformation> getTenureInformation() {
        return tenureInformation;
    }

    public void setTenureInformation(List<TenureInformation> tenureInformation) {
        this.tenureInformation = tenureInformation;
    }



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
    public int getIsNatural() {
        return isNatural;
    }

    public void setIsNatural(int isNatural) {
        this.isNatural = isNatural;
    }


//    public String getClaimType() {
//        return claimType;
//    }
//
//    public void setClaimType(String claimType) {
//        this.claimType = claimType;
//    }

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



    public List<ResourcePersonOfInterest> getResPersonOfInterests() {
        return personOfInterestsRes1;
    }

    public void setResPersonOfInterests(List<ResourcePersonOfInterest> personOfInterestsRes1) {
        this.personOfInterestsRes1 = personOfInterestsRes1;
    }

    public List<ResourcePoiSync> getResPOI() {
        return personOfInterestsRes;
    }

    public void setResPOI(List<ResourcePoiSync> personOfInterestsRes) {
        this.personOfInterestsRes = personOfInterestsRes;
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
        }
        else if (getIsNatural()==0) {
            errorMessage ="Select Person Type";
        }
//        else if (getHamletId() == 0L) {
//            errorMessage = context.getResources().getString(R.string.Please_select_Hamlet);
//        } else if (StringUtility.isEmpty(getAdjudicator1())) {
//            errorMessage = context.getResources().getString(R.string.Please_select_Witness_1);
//        } else if (StringUtility.isEmpty(getAdjudicator2())) {
//            errorMessage = context.getResources().getString(R.string.Please_select_Witness_2);
//        } else if (getAdjudicator1() == getAdjudicator2()) {
//            errorMessage = context.getResources().getString(R.string.Witness_1_and_Witness_2_can_not_be_same);
//        }
        else if (DateUtility.isDateInFuture(DateUtility.getDate(getSurveyDate()))) {
            errorMessage = context.getResources().getString(R.string.ClaimDateInFuture);
        } else if (!DbController.getInstance(context).isClaimNumberUnique(getId(), getPolygonNumber())) {
            errorMessage = context.getResources().getString(R.string.ClaimNumberUnique);
        }
        else if (!DbController.getInstance(context).isClaimNumberUnique(getId(), getPolygonNumber())) {
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
//        if (!getRight().checkPersonsHavePhoto()) {
//            return handleError(context, R.string.warning_addPersonPhoto, showMessage);
//        }

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
        if (shareId == ShareType.TYPE_Single_Tenancy) {
            if(getRight().getNaturalPersons().size() != 1 || !getRight().hasPersonSubType(Person.SUBTYPE_OCCUPANT)){
                return handleError(context, R.string.SingleShareCheck, showMessage);
            }
        }

        // Check multiple occupancy joint ownership
        if (shareId == ShareType.TYPE_Joint_Tenency) {
            if(getRight().getOwnersCount() != 2 || getRight().getNaturalPersons().size() != getRight().getOwnersCount()){
                return handleError(context, R.string.MultiJointShareError, showMessage);
            }
        }

        // Check multiple occupancy common ownership
        if (shareId == ShareType.TYPE_Collective_Tenancy ) {
            if(getRight().getOwnersCount() < 1  || getRight().getOwnersCount() != getRight().getNaturalPersons().size()){
                return handleError(context, R.string.SingleShareCheck, showMessage);
            }
        }

        // Check multiple occupancy common Tenancy ownership
        if ( shareId==ShareType.TYPE_Common_Tenancy) {
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

        int shareTypeId=DbController.getInstance(context).getShareIdByFeatureID(attributes.get(0).getFeatureId());

        if (shareTypeId!=6) {
            DbController db = DbController.getInstance(context);
            if (db.getPrimaryCount(attributes.get(0).getFeatureId()) == 0) {
                return handleError(context, R.string.PrimaryOwner, showMessage);

            }
        }

        return true;
    }

    //for non-natural peosn

    public boolean validateNonPersonsList(Context context, boolean showMessage,Long featureId) {
        if (getRight() == null || getRight().getShareTypeId() < 1)
            return true;

        int shareId = getRight().getShareTypeId();

        // Check photos for all natural persons
//        if (!getRight().checkPersonsHavePhoto()) {
//            return handleError(context, R.string.warning_addPersonPhoto, showMessage);
//        }

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
        if (shareId == ShareType.TYPE_Single_Tenancy) {
            if(getRight().getNaturalPersons().size() != 1 || !getRight().hasPersonSubType(Person.SUBTYPE_OCCUPANT)){
                return handleError(context, R.string.SingleShareCheck, showMessage);
            }
        }

        // Check multiple occupancy joint ownership
        if (shareId == ShareType.TYPE_Joint_Tenency) {
            if(getRight().getOwnersCount() != 2 || getRight().getNaturalPersons().size() != getRight().getOwnersCount()){
                return handleError(context, R.string.MultiJointShareError, showMessage);
            }
        }

        // Check multiple occupancy common ownership
        if (shareId == ShareType.TYPE_Collective_Tenancy ) {
//            if(getRight().getOwnersCount() < 1 ){
//                return handleError(context, R.string.SingleShareCheck, showMessage);
//            }
            DbController db = DbController.getInstance(context);
            if(db.getNONNaturalPersonsByRight(featureId).size() < 1 ){
                return handleError(context, R.string.Collective, showMessage);
            }
        }

        // Check multiple occupancy common Tenancy ownership
//        if ( shareId==ShareType.TYPE_Common_Tenancy) {
//            if(getRight().getOwnersCount() < 2  || getRight().getOwnersCount() != getRight().getNaturalPersons().size()){
//                return handleError(context, R.string.MultiCommonShareError, showMessage);
//            }
//        }


        if ( shareId==ShareType.TYPE_Common_Tenancy) {
            DbController db = DbController.getInstance(context);
            int Natural=db.getpersonTypefromFeature(featureId);
            if (Natural==1) {
                if (getRight().getOwnersCount() < 2 || getRight().getOwnersCount() != getRight().getNaturalPersons().size()) {
                    return handleError(context, R.string.MultiCommonShareError, showMessage);
                }
            }
            else if (Natural==2){
                if(db.getNONNaturalPersonsByRight(featureId).size() < 1 ){
                    return handleError(context, R.string.Collective, showMessage);
                }
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

    public String getMob_no() {
        return mob_no;
    }

    public void setMob_no(String mob_no) {
        this.mob_no = mob_no;
    }




    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }


    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }




    public String getResidential() {
        return residential;
    }

    public void setResidential(String residential) {
        this.residential = residential;
    }



    public String getEthinicity() {
        return ethinicity;
    }

    public void setEthinicity(String ethinicity) {
        this.ethinicity = ethinicity;
    }



    public String getCityzenship() {
        return cityzenship;
    }

    public void setCityzenship(String cityzenship) {
        this.cityzenship = cityzenship;
    }




    public String getMaritalstatus() {
        return maritalstatus;
    }

    public void setMaritalstatus(String maritalstatus) {
        this.maritalstatus = maritalstatus;
    }




    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }




    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }




    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }




    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }



    public String getFirstName() {
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



    public boolean validateTenureInfo(Context context, boolean b) {
        boolean result = true;
        String errorMessage = "";

        if (StringUtility.isEmpty(getFirstName())) {
            errorMessage = context.getResources().getString(R.string.SelectFirstName);
        } else if (StringUtility.isEmpty(getMiddelName())) {
            errorMessage = context.getResources().getString(R.string.SelectMiddleName);
        } else if (StringUtility.isEmpty(getLastName())) {
            errorMessage = context.getResources().getString(R.string.SelectLastName);
        }
        else if (StringUtility.isEmpty(getGender())) {
            errorMessage = context.getResources().getString(R.string.SelectGender);
        }else if (getGender().equalsIgnoreCase(context.getResources().getString(R.string.SelectOption))) {
            errorMessage = context.getResources().getString(R.string.SelectGender);
        }
        else if (StringUtility.isEmpty(getAge())) {
            errorMessage = context.getResources().getString(R.string.SelectAge);
        } else if (StringUtility.isEmpty(getMaritalstatus())) {
            errorMessage = context.getResources().getString(R.string.SelectMarital);
        }else if (getMaritalstatus().equalsIgnoreCase(context.getResources().getString(R.string.SelectOption))) {
            errorMessage = context.getResources().getString(R.string.SelectMarital);
        }
        else if (StringUtility.isEmpty(getCityzenship())) {
            errorMessage = context.getResources().getString(R.string.SelectCitizenship);
        } else if (StringUtility.isEmpty(getEthinicity())) {
            errorMessage =  context.getResources().getString(R.string.SelectEthinicity);
        }
        else if (StringUtility.isEmpty(getResidential())) {
            errorMessage = context.getResources().getString(R.string.SelectResidency);
        }else if (getResidential().equalsIgnoreCase(context.getResources().getString(R.string.SelectOption))) {
            errorMessage = context.getResources().getString(R.string.SelectResidency);
        }
        else if (StringUtility.isEmpty(getAddress())) {
            errorMessage = context.getResources().getString(R.string.SelectAddress);
        }

        else if (StringUtility.isEmpty(getCommunity())) {
            errorMessage ="Enter Community";
        }

        else if (StringUtility.isEmpty(getRegion())) {
            errorMessage = context.getResources().getString(R.string.SelectRegion);
        }

        else if (StringUtility.isEmpty(getCountry())) {
            errorMessage = context.getResources().getString(R.string.SelectCountry);
        }

        else if (StringUtility.isEmpty(getMob_no())) {
            errorMessage = context.getResources().getString(R.string.SelectMobileNumber);
        }

        if (!errorMessage.equals("")) {
            result = false;
            if (b)
                CommonFunctions.getInstance().showToast(context, errorMessage, Toast.LENGTH_LONG, Gravity.CENTER);
        }
        return result;
    }


}
