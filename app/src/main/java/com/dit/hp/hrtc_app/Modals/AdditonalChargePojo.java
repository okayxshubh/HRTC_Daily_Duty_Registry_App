package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class AdditonalChargePojo implements Serializable {

    private int empId;

    private DepartmentPojo departmentPojo;

    private DesignationPojo designationPojo;

    private OfficeLevel officeLevel;

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

    public OfficeLevel getOfficeLevel() {
        return officeLevel;
    }

    public void setOfficeLevel(OfficeLevel officeLevel) {
        this.officeLevel = officeLevel;
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

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }


    @Override
    public String toString() {
        return "AdditonalChargePojo{" +
                "empId=" + empId +
                ", departmentPojo=" + departmentPojo +
                ", designationPojo=" + designationPojo +
                ", officeLevel=" + officeLevel +
                ", officePojo=" + officePojo +
                ", chargeAssignedOn='" + chargeAssignedOn + '\'' +
                ", chargeType='" + chargeType + '\'' +
                '}';
    }

}

