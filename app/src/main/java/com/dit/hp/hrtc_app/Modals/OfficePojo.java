package com.dit.hp.hrtc_app.Modals;

import android.content.Context;
import android.util.Log;

import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;

import org.json.JSONObject;

import java.io.Serializable;

public class OfficePojo implements Serializable {

    private int officeId;
    private String officeName;
    private String officeArea;

    private OfficeLevel officeLevelPojo;

    private OfficePojo parentOffice;

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

    public OfficePojo getParentOffice() {
        return parentOffice;
    }

    public void setParentOffice(OfficePojo parentOffice) {
        this.parentOffice = parentOffice;
    }

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

    public OfficeLevel getOfficeLevelPojo() {
        return officeLevelPojo;
    }

    public void setOfficeLevelPojo(OfficeLevel officeLevelPojo) {
        this.officeLevelPojo = officeLevelPojo;
    }

    public void setLgdBlockCode(int lgdBlockCode) {
        this.lgdBlockCode = lgdBlockCode;
    }

    public void setLgdPanchayatCode(int lgdPanchayatCode) {
        this.lgdPanchayatCode = lgdPanchayatCode;
    }

    public void setLgdVillageCode(int lgdVillageCode) {
        this.lgdVillageCode = lgdVillageCode;
    }

    public void setLgdMunicipalCode(int lgdMunicipalCode) {
        this.lgdMunicipalCode = lgdMunicipalCode;
    }

    public void setLgdWardCode(int lgdWardCode) {
        this.lgdWardCode = lgdWardCode;
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


    public JSONObject getJSON(Context context) {
        Preferences.getInstance().loadPreferences(context); // pass context here

        JSONObject json = new JSONObject();
        try {
            json.put("officeName", officeName);

            json.put("officeType", officeLevelPojo.getOfficeLevelId());

            json.put("designation", designationPojo.getDesignationId());

            json.put("department", Econstants.HRTC_DEPARTMENT_ID);

            if (officeParentId != -1) {
                json.put("office", officeParentId);
            } else {
                json.put("office", JSONObject.NULL);
            }

            json.put("address", address);
            json.put("lgdDistrictCode", lgdDistrictCode);

            json.put("lgdMunicipalCode", lgdMunicipalCode != -1 ? lgdMunicipalCode : JSONObject.NULL);
            json.put("lgdWardCode", lgdWardCode != -1 ? lgdWardCode : JSONObject.NULL);

            json.put("lgdBlockCode", lgdBlockCode != -1 ? lgdBlockCode : JSONObject.NULL);
            json.put("lgdPanchayatCode", lgdPanchayatCode != -1 ? lgdPanchayatCode : JSONObject.NULL);
            json.put("lgdVillageCode", lgdVillageCode != -1 ? lgdVillageCode : JSONObject.NULL);

            json.put("pinCode", pinCode);
            json.put("sanctionedPosts", sanctionedPosts);
            json.put("otherPosts", 0);

            json.put("createdBy", Preferences.getInstance().emailID);

            Log.e("Add Office JSON", "Add Office JSON: " + json.toString());

        } catch (Exception e) {
            Log.e("Error", "Error in toJSON: " + e.getMessage());
        }
        return json;
    }





    public JSONObject getJSONToEdit(Context context) {
        Preferences.getInstance().loadPreferences(context); // pass context here

        JSONObject json = new JSONObject();
        try {
            json.put("officeName", officeName);

            json.put("officeType", officeLevelPojo.getOfficeLevelId());

            json.put("designation", designationPojo.getDesignationId());

            json.put("department", Econstants.HRTC_DEPARTMENT_ID);

            if (officeParentId != -1) {
                json.put("office", officeParentId);
            } else {
                json.put("office", JSONObject.NULL);
            }

            json.put("address", address);
            json.put("lgdDistrictCode", lgdDistrictCode);

            json.put("lgdMunicipalCode", lgdMunicipalCode != -1 ? lgdMunicipalCode : JSONObject.NULL);
            json.put("lgdWardCode", lgdWardCode != -1 ? lgdWardCode : JSONObject.NULL);
            json.put("lgdBlockCode", lgdBlockCode != -1 ? lgdBlockCode : JSONObject.NULL);
            json.put("lgdPanchayatCode", lgdPanchayatCode != -1 ? lgdPanchayatCode : JSONObject.NULL);
            json.put("lgdVillageCode", lgdVillageCode != -1 ? lgdVillageCode : JSONObject.NULL);

            json.put("pinCode", pinCode);
            json.put("sanctionedPosts", sanctionedPosts);
            json.put("otherPosts", 0);

            json.put("updatedBy", Preferences.getInstance().emailID);

            Log.e("EDIT Office JSON", "EDIT Office JSON: " + json.toString());

        } catch (Exception e) {
            Log.e("Error", "Error in toJSON: " + e.getMessage());
        }
        return json;
    }


}

