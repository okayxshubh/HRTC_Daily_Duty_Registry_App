package com.dit.hp.hrtc_app.Modals;


import java.io.Serializable;

public class HimAccessUser implements Serializable {

//    {
//  "firstName": "Bhupender",
//  "uid": "bhupendersingh.thakur",
//  "password": "e1NTSEE1MTJ9SkVCSE5PcHBnZWZER1dpdHlQdkRRa0o3alE5dmV2cmlmczlnbk9qRzVlTWFsNXpwMlVKODU4cjdLd2JpUmtXQjRjckVNUFpMNlgrcEg4M20xMXY5MUdYMUpyVHl0R2dD",
//  "mail": "bhupendersingh.thakur@himaccess.hp.gov.in",
//  "mobile": "9857598075",
//  "dateOfRetirement": "30-09-2041",
//  "dateOfBirth": "07-08-1990",
//  "cn": "Bhupender Singh Thakur",
//  "sn": "Singh Thakur",
//  "designation": null,
//  "department": null
//}

    private String cn;
    private String sn;
    private String uid;
    private String firstName;
    private String mail;
    private String mobile;
    private String dateOfRetirement;
    private String dateOfBirth;
    private String designation;
    private String department;
    private String aadhaarNumber;


    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDateOfRetirement() {
        return dateOfRetirement;
    }

    public void setDateOfRetirement(String dateOfRetirement) {
        this.dateOfRetirement = dateOfRetirement;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "HimAccessUser{" +
                "cn='" + cn + '\'' +
                ", sn='" + sn + '\'' +
                ", uid='" + uid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", mail='" + mail + '\'' +
                ", mobile='" + mobile + '\'' +
                ", dateOfRetirement='" + dateOfRetirement + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", designation='" + designation + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}

