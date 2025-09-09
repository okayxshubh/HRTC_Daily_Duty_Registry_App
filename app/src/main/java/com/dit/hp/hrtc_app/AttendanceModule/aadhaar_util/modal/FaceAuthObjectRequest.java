package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal;

import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.input.contract.CaptureResponse;
import com.dit.hp.hrtc_app.enums.TaskType;
import java.io.Serializable;

public class FaceAuthObjectRequest implements Serializable {

    private CaptureResponse intentPIDXML;
    private String aadhaarNumber;
    private String url;
    private String methordName;
    private String Name;
    private TaskType tasktype;
    private String authXML;
    private String ekycXML;
    private String subAuaCode;
    private String auaCode;
    private String appId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSubAuaCode() {
        return subAuaCode;
    }

    public void setSubAuaCode(String subAuaCode) {
        this.subAuaCode = subAuaCode;
    }

    public String getAuaCode() {
        return auaCode;
    }

    public void setAuaCode(String auaCode) {
        this.auaCode = auaCode;
    }

    public String getAuthXML() {
        return authXML;
    }

    public void setAuthXML(String authXML) {
        this.authXML = authXML;
    }

    public String getEkycXML() {
        return ekycXML;
    }

    public void setEkycXML(String ekycXML) {
        this.ekycXML = ekycXML;
    }

    public CaptureResponse getIntentPIDXML() {
        return intentPIDXML;
    }

    public void setIntentPIDXML(CaptureResponse intentPIDXML) {
        this.intentPIDXML = intentPIDXML;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethordName() {
        return methordName;
    }

    public void setMethordName(String methordName) {
        this.methordName = methordName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public TaskType getTasktype() {
        return tasktype;
    }

    public void setTasktype(TaskType tasktype) {
        this.tasktype = tasktype;
    }

    @Override
    public String toString() {
        return "FaceAuthObjectRequest{" +
                "intentPIDXML=" + intentPIDXML +
                ", aadhaarNumber='" + aadhaarNumber + '\'' +
                ", url='" + url + '\'' +
                ", methordName='" + methordName + '\'' +
                ", Name='" + Name + '\'' +
                ", tasktype=" + tasktype +
                ", authXML='" + authXML + '\'' +
                ", ekycXML='" + ekycXML + '\'' +
                ", subAuaCode='" + subAuaCode + '\'' +
                ", auaCode='" + auaCode + '\'' +
                ", appId='" + appId + '\'' +
                '}';
    }
}
