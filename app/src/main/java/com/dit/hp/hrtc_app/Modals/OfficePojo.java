package com.dit.hp.hrtc_app.Modals;

import android.util.Log;

import com.dit.hp.hrtc_app.utilities.Preferences;

import org.json.JSONObject;

import java.io.Serializable;

public class OfficePojo implements Serializable {

    private int officeId;
    private String officeName;
    private String officeArea;

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

    public String getOfficeArea() {
        return officeArea;
    }

    public void setOfficeArea(String officeArea) {
        this.officeArea = officeArea;
    }

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

    public void setLgdBlockCode(Integer lgdBlockCode) {
        this.lgdBlockCode = lgdBlockCode;
    }

    public int getLgdPanchayatCode() {
        return lgdPanchayatCode;
    }

    public void setLgdPanchayatCode(Integer lgdPanchayatCode) {
        this.lgdPanchayatCode = lgdPanchayatCode;
    }

    public int getLgdVillageCode() {
        return lgdVillageCode;
    }

    public void setLgdVillageCode(Integer lgdVillageCode) {
        this.lgdVillageCode = lgdVillageCode;
    }

    public int getLgdMunicipalCode() {
        return lgdMunicipalCode;
    }

    public void setLgdMunicipalCode(Integer lgdMunicipalCode) {
        this.lgdMunicipalCode = lgdMunicipalCode;
    }

    public int getLgdWardCode() {
        return lgdWardCode;
    }

    public void setLgdWardCode(Integer lgdWardCode) {
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


    public JSONObject getJSON() {
        JSONObject json = new JSONObject();
        try {
//            json.put("id", officeId);
            json.put("officeName", officeName);

            if(officeTypePojo != null){
                json.put("officeType", officeTypePojo.getOfficeTypeId());
            }

            if(departmentPojo != null){
                json.put("department", departmentPojo.getDepartmentId());
            }

            json.put("office", officeParentId); // Parent Office

            if(designationPojo != null){
                json.put("designation", designationPojo.getDesignationId());
            }

            json.put("address", address);

            json.put("lgdDistrictCode", lgdDistrictCode);

            // Add Area and Check
            json.put("officeCategory", officeArea);

            if (officeArea.equalsIgnoreCase("Rural")){
                json.put("lgdMunicipalCode", JSONObject.NULL);
                json.put("lgdWardCode", JSONObject.NULL);
                json.put("lgdBlockCode", lgdBlockCode);
                json.put("lgdPanchayatCode", lgdPanchayatCode);
                json.put("lgdVillageCode", lgdVillageCode);
            }
            else if (officeArea.equalsIgnoreCase("Urban")){
                json.put("lgdMunicipalCode", lgdMunicipalCode);
                json.put("lgdWardCode", lgdWardCode);
                json.put("lgdBlockCode", JSONObject.NULL);
                json.put("lgdPanchayatCode", JSONObject.NULL);
                json.put("lgdVillageCode", JSONObject.NULL);
            }


            json.put("revenueTehsilId", revenueTehsilId);
            json.put("revenuePatwarId", revenuePatwarId);
            json.put("pinCode", pinCode);
            json.put("sanctionedPosts", sanctionedPosts);
            json.put("otherPosts", otherPosts);


            if (officeTypePojo != null) {
                JSONObject type = new JSONObject();
                type.put("id", officeTypePojo.getOfficeTypeId());
                type.put("officeTypeName", officeTypePojo.getOfficeTypeName());
                json.put("officeType", type);
            }

            if (departmentPojo != null) {
                JSONObject dept = new JSONObject();
                dept.put("id", departmentPojo.getDepartmentId());
                dept.put("departmentName", departmentPojo.getDepartmentName());
                dept.put("departmentCode", departmentPojo.getDepartmentCode());
                json.put("department", dept);
            }

            if (designationPojo != null) {
                JSONObject desig = new JSONObject();
                desig.put("id", designationPojo.getDesignationId());
                desig.put("designationName", designationPojo.getDesignationName());
                desig.put("designationCode", designationPojo.getDesignationCode());
                json.put("designation", desig);
            }


//            json.put("depotCode", Preferences.getInstance().);
            json.put("createdBy", Preferences.getInstance().empId);

        } catch (Exception e) {
            Log.e("Error", "Error in toJSON: " + e.getMessage());
        }
        return json;
    }



}

