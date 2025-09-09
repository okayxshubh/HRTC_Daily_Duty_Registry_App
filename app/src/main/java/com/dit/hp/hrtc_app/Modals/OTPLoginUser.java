package com.dit.hp.hrtc_app.Modals;


import java.io.Serializable;

public class OTPLoginUser implements Serializable {

    //{
    //  "empName": "Brijesh Kumar",
    //  "officialEmail": "brijesh.kumar1@himaccess.hp.gov.in",
    //  "aadhar": "764500450582",
    //  "mobileNo": "7018437924"
    //}

    private String employeeName;
    private String officialEmail;
    private String mobile;
    private String aadhaarNumber;

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getOfficialEmail() {
        return officialEmail;
    }

    public void setOfficialEmail(String officialEmail) {
        this.officialEmail = officialEmail;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    @Override
    public String toString() {
        return "OTPLoginUser{" +
                "employeeName='" + employeeName + '\'' +
                ", officialEmail='" + officialEmail + '\'' +
                ", mobile='" + mobile + '\'' +
                ", aadhaarNumber='" + aadhaarNumber + '\'' +
                '}';
    }
}

