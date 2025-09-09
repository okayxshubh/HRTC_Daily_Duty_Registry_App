package com.dit.hp.hrtc_app.Modals;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class OfficeSelectionPojo implements Serializable {

    Integer officeId;
    String officeName;

    public Integer getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Integer officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    @NonNull
    @Override
    public String toString() {
        return officeName;
    }
}

