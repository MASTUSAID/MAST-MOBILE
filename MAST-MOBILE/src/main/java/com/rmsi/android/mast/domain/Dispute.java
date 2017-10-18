package com.rmsi.android.mast.domain;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.util.CommonFunctions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Dispute implements Serializable {
    private Long id;
    transient private Long featureId;
    transient private Long serverId;
    private int disputeTypeId;
    private String description;
    private String regDate;
    private List<Person> disputingPersons = new ArrayList<>();
    private List<Media> media = new ArrayList<>();

    public static String TABLE_NAME = "DISPUTE";
    public static String COL_ID = "ID";
    public static String COL_FEATURE_ID = "FEATURE_ID";
    public static String COL_SERVER_ID = "SERVER_ID";
    public static String COL_DISPUTE_TYPE_ID = "DISPUTE_TYPE";
    public static String COL_DESCRIPTION = "DESCRIPTION";
    public static String COL_REG_DATE = "REG_DATE";

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

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public int getDisputeTypeId() {
        return disputeTypeId;
    }

    public void setDisputeTypeId(int disputeTypeId) {
        this.disputeTypeId = disputeTypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public List<Person> getDisputingPersons() {
        return disputingPersons;
    }

    public void setDisputingPersons(List<Person> disputingPersons) {
        this.disputingPersons = disputingPersons;
    }

    public List<Media> getMedia() {
        return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }

    public Dispute(){

    }

    /** Validates dispute */
    public boolean validate(Context context, boolean showMessage){
        if(!validateBasicInfo(context, showMessage))
            return false;

        if(getDisputingPersons() == null || getDisputingPersons().size() < 2)
            return handleError(context, R.string.AddDisputingPersons, showMessage);

        for(Person person : getDisputingPersons()){
            if(!person.validate(context, 0, true, showMessage))
                return false;
            if(!person.hasPhoto())
                return handleError(context, R.string.warning_addPersonPhoto, showMessage);
        }

        return true;
    }

    /** Validates dispute without disputing persons list */
    public boolean validateBasicInfo(Context context, boolean showMessage){
        if(getDisputeTypeId() < 1)
            return handleError(context, R.string.SelectDisputeType, showMessage);
        return true;
    }

    private boolean handleError(Context context, int messageId, boolean showError) {
        if (showError) {
            CommonFunctions.getInstance().showToast(context, context.getResources().getString(messageId), Toast.LENGTH_LONG, Gravity.CENTER);
        }
        return false;
    }
}
