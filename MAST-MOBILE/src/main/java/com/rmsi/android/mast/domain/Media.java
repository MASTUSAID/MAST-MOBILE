package com.rmsi.android.mast.domain;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GuiUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Media implements Serializable {
    private Long id;
    transient private Long featureId;
    transient private Long personId;
    private String path;
    private String type;
    transient private int synced = 0;
    transient private Long disputeId;
    private List<Attribute> attributes = new ArrayList<>();

    public static String TABLE_NAME = "MEDIA";
    public static String COL_ID = "MEDIA_ID";
    public static String COL_FEATURE_ID = "FEATURE_ID";
    public static String COL_PERSON_ID = "PERSON_ID";
    public static String COL_TYPE = "TYPE";
    public static String COL_PATH = "PATH";
    public static String COL_SYNCED = "SYNCED";
    public static String COL_DISPUTE_ID = "DISPUTE_ID";

    public static int TYPE_PHOTO_CODE = 1;
    public static int TYPE_VIDEO_CODE = 2;
    public static String TYPE_PHOTO = "Image";
    public static String TYPE_VIDEO = "Video";

    public static int ATTRIBUTE_NAME = 11;

    public Long getId() {
        return id;
    }

    public void setId(Long MediaId) {
        this.id = MediaId;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long FeatureId) {
        this.featureId = FeatureId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String MediaPath) {
        this.path = MediaPath;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

    public String getType() {
        return type;
    }

    public void setType(String MediaType) {
        this.type = MediaType;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Long getDisputeId() {
        return disputeId;
    }

    public void setDisputeId(Long disputeId) {
        this.disputeId = disputeId;
    }

    /**
     * Returns media name from attributes list. If attribute not found, "Media" + ID name will be used
     */
    public String getName() {
        if (getAttributes() != null && getAttributes().size() > 0) {
            for (Attribute attribute : getAttributes()) {
                if (attribute.getId() == ATTRIBUTE_NAME)
                    return attribute.getValue();
            }
        }
        return "Media " + getId().toString();
    }

    /**
     * Validates media fields
     *
     * @param context     Application context
     * @param showMessage Flag indicating whether to show error message or not
     */
    public boolean validate(Context context, boolean showMessage) {
        boolean result = true;
        String errorMessage = "";

        if (getAttributes() == null || getAttributes().size() < 1) {
            List<Attribute> attrs = DbController.getInstance(context).getAttributesByType(Attribute.TYPE_MULTIMEDIA);
            if (attrs != null && attrs.size() > 0) {
                errorMessage = context.getResources().getString(R.string.FillMediaAttributes);
            }
        } else if (!GuiUtility.validateAttributes(getAttributes(), showMessage)) {
            errorMessage = context.getResources().getString(R.string.FillMediaAttributes);
        }

        if (!errorMessage.equals("")) {
            result = false;
            if (showMessage)
                CommonFunctions.getInstance().showToast(context, errorMessage, Toast.LENGTH_LONG, Gravity.CENTER);
        }
        return result;
    }
}
