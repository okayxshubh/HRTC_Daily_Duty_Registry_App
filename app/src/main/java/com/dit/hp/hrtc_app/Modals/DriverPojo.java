package com.dit.hp.hrtc_app.Modals;//package com.dit.hp.hrtc_app.Modals;
//
//import java.io.Serializable;
//import java.math.BigInteger;
//import java.util.Date;
//
//public class StaffPojo implements Serializable {
//    private int driverId;
//    private String driverName;
//    private String lastUpdatedBy;
//    private String lastUpdatedOn;
//    private String createdBy;
//    private String dob;
//    private String joiningDate;
//    private String staffType;
//    private String employmentType;
//    private String employeeCode;
//
//    private int relationId;             // relation
//    private String relationMemberName;
//    private String gender;               // gender
//    private String caste;
//    private String licenceNo;
//    private String address;
//    private BigInteger contactNo;
//
//    public StaffPojo(int driverId, String driverName) {
//        this.driverId = driverId;
//        this.driverName = driverName;
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
//
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
//    public int getRelationId() {
//        return relationId;
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
//    public String getJoiningDate() {
//        return joiningDate;
//    }
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
//
//    // Getters and Setters
//    public int getId() {
//        return driverId;
//    }
//
//    public void setDriverId(int driverId) {
//        this.driverId = driverId;
//    }
//
//    public String getName() {
//        return driverName;
//    }
//
//    public void setDriverName(String driverName) {
//        this.driverName = driverName;
//    }
//
//    @Override
//    public String toString() {
//        return driverName + " : " + driverId;
//    }
//}
