package com.rmsi.android.mast.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ambar.Srivastava on 1/22/2018.
 */

public class ResourceOwner implements Serializable {

    private long featureID;
    private List<Media> media = new ArrayList<>();
    private List<Person> naturalPersons = new ArrayList<>();
    private int groupId;

    public long getFeatureID() {
        return featureID;
    }

    public void setFeatureID(long featureID) {
        this.featureID = featureID;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    private String ownerName;

    public List<Media> getMedia() {

        return media;
    }
    public void setMedia(List<Media> media) {
        this.media = media;
    }

    public List<Person> getNaturalPersons() {
        return naturalPersons;
    }

    public void setNaturalPersons(List<Person> naturalPersons) {
        this.naturalPersons = naturalPersons;
    }
}
