package com.dit.hp.hrtc_app.AACustomStuff;

import java.io.Serializable;


public class ShubhOfflineObject implements Serializable {

    private String url;
    private String requestParams;
    private String response;
    private String functionName;
    private String responseCode;

    public ShubhOfflineObject() {
    }

    public ShubhOfflineObject(String url, String requestParams, String response, String functionName, String responseCode) {
        this.url = url;
        this.requestParams = requestParams;
        this.response = response;
        this.functionName = functionName;
        this.responseCode = responseCode;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    @Override
    public String toString() {
        return "ShubhOfflineObject{" +
                "url='" + url + '\'' +
                ", requestParams='" + requestParams + '\'' +
                ", response='" + response + '\'' +
                ", functionName='" + functionName + '\'' +
                ", responseCode='" + responseCode + '\'' +
                '}';
    }
}
