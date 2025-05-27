package com.dit.hp.hrtc_app.AACustomStuff;


import java.util.List;
import java.io.Serializable;
import com.dit.hp.hrtc_app.enums.TaskType;


public class ShubhUploadObject implements Serializable {

    // Add Econstant Param Builder + Body + Log URL + Body + Method Type + Others
    // Build Custom Generic Multipart Uploader & Handle Response

    private String url;
    private TaskType tasktype;
    private String methodName;

    private String param;
    private String body;
    private String singleImagePath;
    private List<String> imagePaths;  // For Multi part

    private Boolean status;  // append ?status=
    private String masterName; // append ?masterName=

    private String parentId;
    private String secondaryParentId;

    private String API_NAME;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public TaskType getTasktype() {
        return tasktype;
    }

    public void setTasktype(TaskType tasktype) {
        this.tasktype = tasktype;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSingleImagePath() {
        return singleImagePath;
    }

    public void setSingleImagePath(String singleImagePath) {
        this.singleImagePath = singleImagePath;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSecondaryParentId() {
        return secondaryParentId;
    }

    public void setSecondaryParentId(String secondaryParentId) {
        this.secondaryParentId = secondaryParentId;
    }

    public String getAPI_NAME() {
        return API_NAME;
    }

    public void setAPI_NAME(String API_NAME) {
        this.API_NAME = API_NAME;
    }


    // Log URL + Body + Method Type + Others
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ShubhUploadObject {");

        if (tasktype != null) sb.append("Task Type: ").append(tasktype).append(", ");
        if (url != null) sb.append("URL: '").append(url).append("', ");
        if (methodName != null) sb.append("Method Name: '").append(methodName).append("', ");
        if (param != null) sb.append("param: '").append(param).append("', ");
        if (body != null) sb.append("body: '").append(body).append("', ");
        if (singleImagePath != null) sb.append("singleImagePath: '").append(singleImagePath).append("', ");
        if (imagePaths != null) sb.append("imagePaths: ").append(imagePaths).append(", ");
        if (status != null) sb.append("status: ").append(status).append(", ");
        if (masterName != null) sb.append("masterName: '").append(masterName).append("', ");
        if (parentId != null) sb.append("parentId: '").append(parentId).append("', ");
        if (secondaryParentId != null) sb.append("secondaryParentId: '").append(secondaryParentId).append("', ");
        if (API_NAME != null) sb.append("API_NAME: '").append(API_NAME).append("', ");

        // Remove last comma and space if present
        if (sb.lastIndexOf(", ") == sb.length() - 2) {
            sb.setLength(sb.length() - 2);
        }

        sb.append("}");
        return sb.toString();
    }

}