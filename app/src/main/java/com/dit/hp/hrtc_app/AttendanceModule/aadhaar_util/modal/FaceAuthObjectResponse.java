package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal;

import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.input.contract.CaptureResponse;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.kyc.KycResData;

import java.io.Serializable;

public class FaceAuthObjectResponse implements Serializable {

    private String aadhaarNo;
    private String response;
    private String url;
    private String functionName;
    private String responseCode;
    private KycResData kycResDate;
    private String authXml;
    private String eKYCXML;
    private CaptureResponse captureResponse;

    public CaptureResponse getCaptureResponse() {
        return captureResponse;
    }

    public void setCaptureResponse(CaptureResponse captureResponse) {
        this.captureResponse = captureResponse;
    }

    public String getAuthXml() {
        return authXml;
    }

    public void setAuthXml(String authXml) {
        this.authXml = authXml;
    }

    public String geteKYCXML() {
        return eKYCXML;
    }

    public void seteKYCXML(String eKYCXML) {
        this.eKYCXML = eKYCXML;
    }

    public String getAadhaarNo() {
        return aadhaarNo;
    }

    public void setAadhaarNo(String aadhaarNo) {
        this.aadhaarNo = aadhaarNo;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public KycResData getKycResDate() {
        return kycResDate;
    }

    public void setKycResDate(KycResData kycResDate) {
        this.kycResDate = kycResDate;
    }

    @Override
    public String toString() {
        return "FaceAuthObjectResponse{" +
                "aadhaarNo='" + aadhaarNo + '\'' +
                ", response='" + response + '\'' +
                ", url='" + url + '\'' +
                ", functionName='" + functionName + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", kycResDate=" + kycResDate +
                ", authXml='" + authXml + '\'' +
                ", eKYCXML='" + eKYCXML + '\'' +
                ", captureResponse=" + captureResponse +
                '}';
    }
}
