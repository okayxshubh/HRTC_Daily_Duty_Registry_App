package com.dit.hp.hrtc_app.Modals;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.math.BigInteger;

public class StaffPojo implements Serializable {
    private int id;
    private String name;
    private String dob;
    private String joiningDate;
    private String staffType;
    private String employmentType;
    private String employeeCode;

    private int relationId;             // relation
    private String relationMemberName;
    private String gender;               // gender
    private String caste;
    private String licenceNo;
    private String address;
    private BigInteger contactNo;

    public StaffPojo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public StaffPojo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getStaffType() {
        return staffType;
    }

    public void setStaffType(String staffType) {
        this.staffType = staffType;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public int getRelationId() {
        return relationId;
    }

    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }

    public String getRelationMemberName() {
        return relationMemberName;
    }

    public void setRelationMemberName(String relationMemberName) {
        this.relationMemberName = relationMemberName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCaste() {
        return caste;
    }

    public void setCaste(String caste) {
        this.caste = caste;
    }

    public String getLicenceNo() {
        return licenceNo;
    }

    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigInteger getContactNo() {
        return contactNo;
    }

    public void setContactNo(BigInteger contactNo) {
        this.contactNo = contactNo;
    }

    @NonNull
    @Override
    public String toString() {
        return name + " : " + id;
    }
}

