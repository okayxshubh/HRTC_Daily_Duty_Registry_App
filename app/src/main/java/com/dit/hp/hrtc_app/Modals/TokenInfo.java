package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class TokenInfo implements Serializable {
    private String email;
    private String successUrl;
    private String token;
    private String tokenValidTill;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSuccessUrl() { return successUrl; }
    public void setSuccessUrl(String successUrl) { this.successUrl = successUrl; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenValidTill() { return tokenValidTill; }
    public void setTokenValidTill(String tokenValidTill) { this.tokenValidTill = tokenValidTill; }

    @Override
    public String toString() {
        return "TokenInfo{" +
                "email='" + email + '\'' +
                ", successUrl='" + successUrl + '\'' +
                ", token='" + token + '\'' +
                ", tokenValidTill='" + tokenValidTill + '\'' +
                '}';
    }
}
