package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;


public class ResponsePojoGet implements Serializable {

    private String url;
    private String requestParams;
    private String response;
    private String functionName;
    private String responseCode;

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
        return "ResponsePojoGet{" +
                "url='" + url + '\'' +
                ", requestParams='" + requestParams + '\'' +
                ", response='" + response + '\'' +
                ", functionName='" + functionName + '\'' +
                ", responseCode='" + responseCode + '\'' +
                '}';
    }
}
