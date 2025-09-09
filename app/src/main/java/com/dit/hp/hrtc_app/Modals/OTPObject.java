package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;
import java.util.List;

public class OTPObject implements Serializable {

    private List<String> emails;
    private String mobile;
    private String otp_status;


    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOtp_status() {
        return otp_status;
    }

    public void setOtp_status(String otp_status) {
        this.otp_status = otp_status;
    }

    @Override
    public String toString() {
        return "OTPObject{" +
                "emails=" + emails +
                ", mobile='" + mobile + '\'' +
                ", otp_status='" + otp_status + '\'' +
                '}';
    }
}
