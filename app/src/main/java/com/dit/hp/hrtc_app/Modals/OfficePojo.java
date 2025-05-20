package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class OfficePojo implements Serializable {

    private int officeId;
    private String officeName;

    private OfficeTypePojo officeTypePojo;

    private DepartmentPojo departmentPojo;

    private DesignationPojo designationPojo;

    private String address;
    private String officeCategory;

    private int officeParentId;

    private int lgdDistrictCode;

    private int lgdBlockCode;

    private int lgdPanchayatCode;

    private int lgdVillageCode;

    private int lgdMunicipalCode;
    private int lgdWardCode;
    private int revenueTehsilId;

    private int revenuePatwarId;

    private int pinCode;

    private int sanctionedPosts;

    private int otherPosts;


    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public OfficeTypePojo getOfficeTypePojo() {
        return officeTypePojo;
    }

    public void setOfficeTypePojo(OfficeTypePojo officeTypePojo) {
        this.officeTypePojo = officeTypePojo;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOfficeCategory() {
        return officeCategory;
    }

    public void setOfficeCategory(String officeCategory) {
        this.officeCategory = officeCategory;
    }

    public int getOfficeParentId() {
        return officeParentId;
    }

    public void setOfficeParentId(int officeParentId) {
        this.officeParentId = officeParentId;
    }

    public int getLgdDistrictCode() {
        return lgdDistrictCode;
    }

    public void setLgdDistrictCode(int lgdDistrictCode) {
        this.lgdDistrictCode = lgdDistrictCode;
    }

    public int getLgdBlockCode() {
        return lgdBlockCode;
    }

    public void setLgdBlockCode(int lgdBlockCode) {
        this.lgdBlockCode = lgdBlockCode;
    }

    public int getLgdPanchayatCode() {
        return lgdPanchayatCode;
    }

    public void setLgdPanchayatCode(int lgdPanchayatCode) {
        this.lgdPanchayatCode = lgdPanchayatCode;
    }

    public int getLgdVillageCode() {
        return lgdVillageCode;
    }

    public void setLgdVillageCode(int lgdVillageCode) {
        this.lgdVillageCode = lgdVillageCode;
    }

    public int getLgdMunicipalCode() {
        return lgdMunicipalCode;
    }

    public void setLgdMunicipalCode(int lgdMunicipalCode) {
        this.lgdMunicipalCode = lgdMunicipalCode;
    }

    public int getLgdWardCode() {
        return lgdWardCode;
    }

    public void setLgdWardCode(int lgdWardCode) {
        this.lgdWardCode = lgdWardCode;
    }

    public int getRevenueTehsilId() {
        return revenueTehsilId;
    }

    public void setRevenueTehsilId(int revenueTehsilId) {
        this.revenueTehsilId = revenueTehsilId;
    }

    public int getRevenuePatwarId() {
        return revenuePatwarId;
    }

    public void setRevenuePatwarId(int revenuePatwarId) {
        this.revenuePatwarId = revenuePatwarId;
    }

    public int getPinCode() {
        return pinCode;
    }

    public void setPinCode(int pinCode) {
        this.pinCode = pinCode;
    }

    public int getSanctionedPosts() {
        return sanctionedPosts;
    }

    public void setSanctionedPosts(int sanctionedPosts) {
        this.sanctionedPosts = sanctionedPosts;
    }

    public int getOtherPosts() {
        return otherPosts;
    }

    public void setOtherPosts(int otherPosts) {
        this.otherPosts = otherPosts;
    }

    @Override
    public String toString() {
        return officeName;
    }
}

