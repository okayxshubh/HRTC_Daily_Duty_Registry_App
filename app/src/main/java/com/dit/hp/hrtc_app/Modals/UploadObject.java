package com.dit.hp.hrtc_app.Modals;


import com.dit.hp.hrtc_app.enums.TaskType;

import java.io.Serializable;


public class UploadObject implements Serializable {

    private String url;
    private ScanDataPojo scanDataPojo;
    private TaskType tasktype;
    private String methordName;
    private String param;
    private String body;
    private String imagePath;

    private String encodedParams;

    private Boolean status;

    private String masterName;
    private String masterData;

    private String API_NAME;

    private String parentId;
    private String secondaryParentId;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSecondaryParentId() {
        return secondaryParentId;
    }

    public void setSecondaryParentId(String secondaryParentId) {
        this.secondaryParentId = secondaryParentId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getAPI_NAME() {
        return API_NAME;
    }

    public void setAPI_NAME(String API_NAME) {
        this.API_NAME = API_NAME;
    }

    public String getMasterData() {
        return masterData;
    }

    public void setMasterData(String masterData) {
        this.masterData = masterData;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ScanDataPojo getScanDataPojo() {
        return scanDataPojo;
    }

    public void setScanDataPojo(ScanDataPojo scanDataPojo) {
        this.scanDataPojo = scanDataPojo;
    }

    public TaskType getTasktype() {
        return tasktype;
    }

    public void setTasktype(TaskType tasktype) {
        this.tasktype = tasktype;
    }

    public String getMethordName() {
        return methordName;
    }

    public void setMethordName(String methordName) {
        this.methordName = methordName;
    }

    public String getEncodedParams() {
        return encodedParams;
    }

    public void setEncodedParams(String encodedParams) {
        this.encodedParams = encodedParams;
    }


    @Override
    public String toString() {
        return "UploadObject{" +
                "url='" + url + '\'' +
                ", scanDataPojo=" + scanDataPojo +
                ", tasktype=" + tasktype +
                ", methordName='" + methordName + '\'' +
                ", param='" + param + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", masterData='" + masterData + '\'' +
                ", status=" + status +
                ", masterName='" + masterName + '\'' +
                ", API_NAME='" + API_NAME + '\'' +
                ", parentId='" + parentId + '\'' +
                ", secondaryParentId='" + secondaryParentId + '\'' +
                '}';
    }
}