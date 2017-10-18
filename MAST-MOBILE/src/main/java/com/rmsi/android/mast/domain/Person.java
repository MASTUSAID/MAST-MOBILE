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
import java.util.Date;
import java.util.List;

public class Person implements Serializable {
    private Long id;
    transient private Long featureId;
    transient private Long rightId;
    private int resident = -1;
    private int isNatural;
    transient private Long serverId;
    private int subTypeId;
    private String share;
    transient private Long disputeId;
    private int acquisitionTypeId;
    private List<Attribute> attributes = new ArrayList<>();
    private List<Media> media = new ArrayList<>();

    public static String TABLE_NAME = "PERSON";
    public static String COL_ID = "ID";
    public static String COL_RIGHT_ID = "SOCIAL_TENURE_ID";
    public static String COL_RESIDENT = "RESIDENT";
    public static String COL_IS_NATURAL = "IS_NATURAL";
    public static String COL_FEATURE_ID = "FEATURE_ID";
    public static String COL_SERVER_ID = "SERVER_PK";
    public static String COL_SUBTYPE = "PERSON_SUBTYPE";
    public static String COL_SHARE = "SHARE";
    public static String COL_ACQUISITION_TYPE_ID = "ACQUISITION_TYPE_ID";
    public static String COL_DISPUTE_ID = "DISPUTE_ID";

    public static int SUBTYPE_OWNER = 3;
    public static int SUBTYPE_ADMINISTRATOR = 4;
    public static int SUBTYPE_GUARDIAN = 5;

    public static int ATTRIBUTE_FIRST_NAME = 1;
    public static int ATTRIBUTE_LAST_NAME = 2;
    public static int ATTRIBUTE_INSTITUTION_NAME = 6;
    public static int ATTRIBUTE_AGE = 21;
    public static int ATTRIBUTE_DOB = 330;

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

    public Long getRightId() {
        return rightId;
    }

    public void setRightId(Long rightId) {
        this.rightId = rightId;
    }

    public int getResident() {
        return resident;
    }

    public void setResident(int resident) {
        this.resident = resident;
    }

    public int getIsNatural() {
        return isNatural;
    }

    public void setIsNatural(int isNatural) {
        this.isNatural = isNatural;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public int getSubTypeId() {
        return subTypeId;
    }

    public void setSubTypeId(int subTypeId) {
        this.subTypeId = subTypeId;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
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

    public Long getDisputeId() {
        return disputeId;
    }

    public void setDisputeId(Long disputeId) {
        this.disputeId = disputeId;
    }

    public int getAcquisitionTypeId() {
        return acquisitionTypeId;
    }

    public void setAcquisitionTypeId(int acquisitionTypeId) {
        this.acquisitionTypeId = acquisitionTypeId;
    }

    public Person() {

    }

    /**
     * Checks whether person is minor (<18). The priority is given to DOB, if empty, the Age attribute is checked.
     * If both attributes are missing or empty, false value will be returned.
     */
    public boolean isMinor() {
        try {
            if (getAttributes() != null && getAttributes().size() > 0) {
                Attribute dob = getAttribute(ATTRIBUTE_DOB);
                Attribute age = getAttribute(ATTRIBUTE_AGE);

                // Check by DOB
                if (dob != null && !StringUtility.isEmpty(dob.getValue())) {
                    if (DateUtility.getDiffYears(
                            DateUtility.getDate(dob.getValue()),
                            DateUtility.getCurrentDate()) < 18)
                        return true;
                }

                // Check by age
                if (age != null && !StringUtility.isEmpty(age.getValue())) {
                    if (Integer.parseInt(age.getValue()) < 18)
                        return true;
                }
            }
        } catch (Exception ex) {
        }
        return false;
    }

    /**
     * Checks whether person has photo or not.
     */
    public boolean hasPhoto() {
        if (getIsNatural() == 1) {
            return getMedia() != null && getMedia().size() > 0;
        }
        return true;
    }

    /**
     * Looks for the attribute by id and returns it if found, otherwise null will be returned.
     */
    public Attribute getAttribute(int attributeId) {
        if (getAttributes() != null && getAttributes().size() > 0) {
            for (Attribute attribute : getAttributes()) {
                if (attribute.getId() == attributeId) {
                    return attribute;
                }
            }
        }
        return null;
    }

    /**
     * Returns full name, including person subtype and share size
     *
     * @param context Context object to extract localized person subtype name
     */
    public String getFullName(Context context) {
        String name = "";
        if (getAttributes() != null && getAttributes().size() > 0) {
            for (Attribute attribute : getAttributes()) {
                if (attribute.getId() == Person.ATTRIBUTE_FIRST_NAME && !StringUtility.isEmpty(attribute.getValue())) {
                    name = attribute.getValue();
                }
                if (attribute.getId() == Person.ATTRIBUTE_INSTITUTION_NAME && !StringUtility.isEmpty(attribute.getValue())) {
                    name = attribute.getValue();
                }
            }
            for (Attribute attribute : getAttributes()) {
                if (attribute.getId() == Person.ATTRIBUTE_LAST_NAME && !StringUtility.isEmpty(attribute.getValue())) {
                    if (name.equals(""))
                        name = attribute.getValue();
                    else
                        name = name + " " + attribute.getValue();
                    break;
                }
            }
        }

        if (!name.equals("") && getSubTypeId() > 0) {
            if (getSubTypeId() == Person.SUBTYPE_ADMINISTRATOR) {
                name = name + " (" + context.getResources().getString(R.string.administrator) + ")";
            } else if (getSubTypeId() == Person.SUBTYPE_GUARDIAN) {
                name = name + " (" + context.getResources().getString(R.string.Guardian) + ")";
            }
        }

        if (!name.equals("") && !StringUtility.isEmpty(getShare())) {
            name = name + " (" + getShare() + ")";
        }
        return name;
    }

    /**
     * Validates right fields
     *
     * @param context     Application context
     * @param showMessage Flag indicating whether to show error message or not
     */
    public boolean validate(Context context, int shareTypeId, boolean isDispute, boolean showMessage) {
        boolean result = true;
        String errorMessage = "";

        if (getResident() < 0) {
            errorMessage = context.getResources().getString(R.string.SelectResidency);
        }

        // Attributes
        if (errorMessage.equals("")) {
            if (getAttributes() == null || getAttributes().size() < 1) {
                List<Attribute> attrs;
                if (getIsNatural() == 1)
                    attrs = DbController.getInstance(context).getAttributesByType(Attribute.TYPE_NATURAL_PERSON);
                else
                    attrs = DbController.getInstance(context).getAttributesByType(Attribute.TYPE_NON_NATURAL_PERSON);

                if (attrs != null && attrs.size() > 0) {
                    if (getIsNatural() == 1)
                        errorMessage = context.getResources().getString(R.string.FillRequiredFieldsOnPerson);
                    else
                        errorMessage = context.getResources().getString(R.string.FillRequiredFieldsOnNonPerson);
                }
            } else if (!GuiUtility.validateAttributes(getAttributes(), showMessage)) {
                if (getIsNatural() == 1)
                    errorMessage = context.getResources().getString(R.string.FillRequiredFieldsOnPerson);
                else
                    errorMessage = context.getResources().getString(R.string.FillRequiredFieldsOnNonPerson);
            } else {
                // Validate age
                Attribute dob = getAttribute(ATTRIBUTE_DOB);
                Attribute age = getAttribute(ATTRIBUTE_AGE);

                if (getIsNatural() == 1 &&
                        ((dob != null && !StringUtility.isEmpty(dob.getValue())) ||
                                (age != null && !StringUtility.isEmpty(age.getValue())))) {

                    int personAge = 0;

                    // Give priority to dob to calculate age
                    if (dob != null && !StringUtility.isEmpty(dob.getValue())) {
                        personAge = DateUtility.getDiffYears(DateUtility.getDate(dob.getValue()), DateUtility.getCurrentDate());
                    } else {
                        personAge = Integer.valueOf(age.getValue());
                    }

                    // For owners who are minors, the age must be less than 18 years, for all other cases between 18 and 110
                    if (getSubTypeId() == SUBTYPE_OWNER && shareTypeId == ShareType.TYPE_GUARDIAN) {
                        if (personAge > 17)
                            errorMessage = context.getResources().getString(R.string.AgeLessThan18);
                    } else if (personAge < 18 || personAge > 110) {
                        errorMessage = context.getResources().getString(R.string.AgeMustBe18or100);
                    }
                }

                // Validate share
                if (errorMessage.equals("")) {
                    if (getSubTypeId() == SUBTYPE_OWNER && shareTypeId == ShareType.TYPE_MUTIPLE_OCCUPANCY_IN_COMMON) {
                        if (StringUtility.isEmpty(getShare()))
                            errorMessage = context.getResources().getString(R.string.FillShareSize);
                    }
                }

                // Validate dispute
                if (errorMessage.equals("")) {
                    if(isDispute){
                        if(getAcquisitionTypeId() < 1)
                            errorMessage = context.getResources().getString(R.string.SelectAcquisitionType);
                    }
                }
            }
        }

        if (!errorMessage.equals("")) {
            result = false;
            if (showMessage)
                CommonFunctions.getInstance().showToast(context, errorMessage, Toast.LENGTH_LONG, Gravity.CENTER);
        }
        return result;
    }
}
