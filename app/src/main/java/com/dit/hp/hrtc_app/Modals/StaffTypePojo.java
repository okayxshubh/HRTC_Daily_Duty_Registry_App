package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class StaffTypePojo implements Serializable {

    int staffTypeId;
    String staffTypeName;

    public int getStaffTypeId() {
        return staffTypeId;
    }

    public void setStaffTypeId(int staffTypeId) {
        this.staffTypeId = staffTypeId;
    }

    public String getStaffTypeName() {
        return staffTypeName;
    }

    public void setStaffTypeName(String staffTypeName) {
        this.staffTypeName = staffTypeName;
    }

    @Override
    public String toString() {
        return staffTypeName;
    }
}
