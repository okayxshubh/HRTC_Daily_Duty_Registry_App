package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal;

import java.io.Serializable;

public class AuthenticationType implements Serializable {

    private String AuthenticationType;
    private String AuthenticationId;

    public String getAuthenticationType() {
        return AuthenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        AuthenticationType = authenticationType;
    }

    public String getAuthenticationId() {
        return AuthenticationId;
    }

    public void setAuthenticationId(String authenticationId) {
        AuthenticationId = authenticationId;
    }

    @Override
    public String toString() {
        return AuthenticationType;
    }
}
