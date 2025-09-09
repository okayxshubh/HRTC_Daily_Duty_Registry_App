package com.dit.hp.hrtc_app.Modals;//package com.dit.hp.hrtc_app.Modals;
//
//import androidx.annotation.NonNull;
//
//import java.io.Serializable;
//import java.math.BigInteger;
//import java.util.Date;
//
//public class StaffPojo implements Serializable {
//
//    private int conductorId;
//    private String conductorName;
//    private String lastUpdatedBy;
//    private String createdBy;
//    private String staffName;
//    private String lastUpdatedOn;  // Add this field
//    private String joiningDate;
//    private String staffType;
//    private String dob;
//    private int relationId;             // relation
//    private String gender;               // gender
//    private String caste;
//    private String employmentType;
//    private String employeeCode;
//    private String licenceNo;
//    private String relationMemberName;
//    private String address;
//    private BigInteger contactNo;
//
//    public StaffPojo(int conductorId, String conductorName) {
//        this.conductorId = conductorId;
//        this.conductorName = conductorName;
//    }
//
//    public StaffPojo() {
//    }
//
//    // Getters and Setters
//    public BigInteger getContactNo() {
//        return contactNo;
//    }
//
//    public void setContactNo(BigInteger contactNo) {
//        this.contactNo = contactNo;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//    public String getRelationMemberName() {
//        return relationMemberName;
//    }
//
//    public void setRelationMemberName(String relationMemberName) {
//        this.relationMemberName = relationMemberName;
//    }
//
//    public String getLicenceNo() {
//        return licenceNo;
//    }
//
//    public void setLicenceNo(String licenceNo) {
//        this.licenceNo = licenceNo;
//    }
//
//    public String getEmploymentType() {
//        return employmentType;
//    }
//
//    public void setEmploymentType(String employmentType) {
//        this.employmentType = employmentType;
//    }
//
//    public String getEmployeeCode() {
//        return employeeCode;
//    }
//
//    public void setEmployeeCode(String employeeCode) {
//        this.employeeCode = employeeCode;
//    }
//
//    public int getRelationId() {
//        return relationId;
//    }
//
//    public void setRelationId(int relationId) {
//        this.relationId = relationId;
//    }
//
//    public String getGender() {
//        return gender;
//    }
//
//    public void setGender(String gender) {
//        this.gender = gender;
//    }
//
//    public String getCaste() {
//        return caste;
//    }
//
//    public void setCaste(String caste) {
//        this.caste = caste;
//    }
//
//    public int getId() {
//        return conductorId;
//    }
//
//    public void setId(int conductorId) {
//        this.conductorId = conductorId;
//    }
//
//
//    public String getLastUpdatedOn() {
//        return lastUpdatedOn;
//    }
//
//    public void setLastUpdatedOn(String lastUpdatedOn) {
//        this.lastUpdatedOn = lastUpdatedOn;
//    }
//
//    public String getDob() {
//        return dob;
//    }
//
//    public void setDob(String dob) {
//        this.dob = dob;
//    }
//
//    public String getName() {
//        return conductorName;
//    }
//
//    public void setName(String conductorName) {
//        this.conductorName = conductorName;
//    }
//
//    public String getLastUpdatedBy() {
//        return lastUpdatedBy;
//    }
//
//    public void setLastUpdatedBy(String lastUpdatedBy) {
//        this.lastUpdatedBy = lastUpdatedBy;
//    }
//
//    public String getCreatedBy() {
//        return createdBy;
//    }
//
//    public void setCreatedBy(String createdBy) {
//        this.createdBy = createdBy;
//    }
//
//    public String getStaffName() {
//        return staffName;
//    }
//
//    public void setStaffName(String staffName) {
//        this.staffName = staffName;
//    }
//
//    public String getJoiningDate() {
//        return joiningDate;
//    }
//
//    public void setJoiningDate(String joiningDate) {
//        this.joiningDate = joiningDate;
//    }
//
//    public String getStaffType() {
//        return staffType;
//    }
//
//    public void setStaffType(String staffType) {
//        this.staffType = staffType;
//    }
//
//    @NonNull
//    @Override
//    public String toString() {
//        return conductorName + " : " + conductorId;
//    }
//}
