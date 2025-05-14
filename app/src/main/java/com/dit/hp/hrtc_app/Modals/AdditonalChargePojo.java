package com.dit.hp.hrtc_app.Modals;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class AdditonalChargePojo implements Serializable {

        private DepartmentPojo departmentPojo;

        private DesignationPojo designationPojo;

        private OfficeTypePojo officeTypePojo;

        private OfficePojo officePojo;

        private String chargeAssignedOn;
        private String chargeType;


    public DepartmentPojo getDepartmentPojo() {
        return departmentPojo;
    }

    public void setDepartmentPojo(DepartmentPojo departmentPojo) {
        this.departmentPojo = departmentPojo;
    }

    public DesignationPojo getDesignationPojo() {
        return designationPojo;
    }

    public void setDesignationPojo(DesignationPojo designationPojo) {
        this.designationPojo = designationPojo;
    }

    public OfficeTypePojo getOfficeTypePojo() {
        return officeTypePojo;
    }

    public void setOfficeTypePojo(OfficeTypePojo officeTypePojo) {
        this.officeTypePojo = officeTypePojo;
    }

    public OfficePojo getOfficePojo() {
        return officePojo;
    }

    public void setOfficePojo(OfficePojo officePojo) {
        this.officePojo = officePojo;
    }

    public String getChargeAssignedOn() {
        return chargeAssignedOn;
    }

    public void setChargeAssignedOn(String chargeAssignedOn) {
        this.chargeAssignedOn = chargeAssignedOn;
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    @Override
    public String toString() {
        return "AdditonalChargePojo{" +
                "departmentPojo=" + departmentPojo +
                ", designationPojo=" + designationPojo +
                ", officeTypePojo=" + officeTypePojo +
                ", officePojo=" + officePojo +
                ", chargeAssignedOn='" + chargeAssignedOn + '\'' +
                ", chargeType='" + chargeType + '\'' +
                '}';
    }
}

