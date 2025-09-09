package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.kyc;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class KycResData implements Serializable {
    private String ret;
    private String code;
    private String txn;
    private String ts;
    private String aadhaarReferenceNumber;
    private String rarData;
    private UidData uidData;
    private String aadhaarNumber;
    private String mobileNumber;
    private String AadhaarPhotoPath;

    private String docName;
    private String docPath;

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }


    public String getAadhaarPhotoPath() {
        return AadhaarPhotoPath;
    }


    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocPath() {
        return docPath;
    }

    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }

    public void setAadhaarPhotoPath(String aadhaarPhotoPath) {
        AadhaarPhotoPath = aadhaarPhotoPath;
    }

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTxn() {
        return txn;
    }

    public void setTxn(String txn) {
        this.txn = txn;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getAadhaarReferenceNumber() {
        return aadhaarReferenceNumber;
    }

    public void setAadhaarReferenceNumber(String aadhaarReferenceNumber) {
        this.aadhaarReferenceNumber = aadhaarReferenceNumber;
    }

    public String getRarData() {
        return rarData;
    }

    public void setRarData(String rarData) {
        this.rarData = rarData;
    }

    public UidData getUidData() {
        return uidData;
    }

    public void setUidData(UidData uidData) {
        this.uidData = uidData;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    // Getters and setters


    @Override
    public String toString() {
        return "KycResData{" +
                "ret='" + ret + '\'' +
                ", code='" + code + '\'' +
                ", txn='" + txn + '\'' +
                ", ts='" + ts + '\'' +
                ", aadhaarReferenceNumber='" + aadhaarReferenceNumber + '\'' +
                ", rarData='" + rarData + '\'' +
                ", uidData=" + uidData +
                ", aadhaarNumber='" + aadhaarNumber + '\'' +
                ", AadhaarPhotoPath='" + AadhaarPhotoPath + '\'' +
                ", docName='" + docName + '\'' +
                ", docPath='" + docPath + '\'' +
                '}';
    }

    public String jsonKYCRES() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ret", ret);
            jsonObject.put("code", code);
            jsonObject.put("txn", txn);
            jsonObject.put("ts", ts);
            jsonObject.put("aadhaarReferenceNumber", aadhaarReferenceNumber);
            jsonObject.put("name", uidData.getPoi().getName());
            jsonObject.put("dob", uidData.getPoi().getDob());
            jsonObject.put("gender", uidData.getPoi().getGender());
            jsonObject.put("photo", this.AadhaarPhotoPath);

            jsonObject.put("co", uidData.getPoa().getCo());
            jsonObject.put("house", uidData.getPoa().getHouse());
            jsonObject.put("loc", uidData.getPoa().getLoc());
            jsonObject.put("vtc", uidData.getPoa().getVtc());
            jsonObject.put("subdist", uidData.getPoa().getSubdist());
            jsonObject.put("state", uidData.getPoa().getState());
            jsonObject.put("pc", uidData.getPoa().getPc());
            jsonObject.put("po", uidData.getPoa().getPo());
            jsonObject.put("po", uidData.getPoa().getPo());
            jsonObject.put("aadhaarNumber", this.aadhaarNumber);

            jsonObject.put("docName", this.docName);
            jsonObject.put("docPath", this.docPath);


            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public JSONObject jsonKYCRESObj() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ret", ret);
            jsonObject.put("code", code);
            jsonObject.put("txn", txn);
            jsonObject.put("ts", ts);
            jsonObject.put("aadhaarReferenceNumber", aadhaarReferenceNumber);
            jsonObject.put("name", uidData.getPoi().getName());
            jsonObject.put("dob", uidData.getPoi().getDob());
            jsonObject.put("gender", uidData.getPoi().getGender());
            jsonObject.put("photo", this.AadhaarPhotoPath);

            jsonObject.put("co", uidData.getPoa().getCo());
            jsonObject.put("house", uidData.getPoa().getHouse());
            jsonObject.put("loc", uidData.getPoa().getLoc());
            jsonObject.put("vtc", uidData.getPoa().getVtc());
            jsonObject.put("subdist", uidData.getPoa().getSubdist());
            jsonObject.put("state", uidData.getPoa().getState());
            jsonObject.put("pc", uidData.getPoa().getPc());
            jsonObject.put("po", uidData.getPoa().getPo());
            jsonObject.put("po", uidData.getPoa().getPo());
            jsonObject.put("aadhaarNumber", this.aadhaarNumber);

            jsonObject.put("docName", this.docName);
            jsonObject.put("docPath", this.docPath);


            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}
