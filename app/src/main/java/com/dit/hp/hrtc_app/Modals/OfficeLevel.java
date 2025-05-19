package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class OfficeLevel implements Serializable {

    private int officeLevelId;
    private String officeLevelName;
    private String officeLevelDepartmentName;

    public OfficeLevel(int officeLevelId, String officeLevelName, String officeLevelDepartmentName) {
        this.officeLevelId = officeLevelId;
        this.officeLevelName = officeLevelName;
        this.officeLevelDepartmentName = officeLevelDepartmentName;
    }

    public OfficeLevel() {
    }

    public int getOfficeLevelId() {
        return officeLevelId;
    }

    public void setOfficeLevelId(int officeLevelId) {
        this.officeLevelId = officeLevelId;
    }

    public String getOfficeLevelName() {
        return officeLevelName;
    }

    public void setOfficeLevelName(String officeLevelName) {
        this.officeLevelName = officeLevelName;
    }

    public String getOfficeLevelDepartmentName() {
        return officeLevelDepartmentName;
    }

    public void setOfficeLevelDepartmentName(String officeLevelDepartmentName) {
        this.officeLevelDepartmentName = officeLevelDepartmentName;
    }

    @Override
    public String toString() {
        return "OfficeLevel{" +
                "officeLevelId=" + officeLevelId +
                ", officeLevelName='" + officeLevelName + '\'' +
                ", officeLevelDepartmentName='" + officeLevelDepartmentName + '\'' +
                '}';
    }
}