package com.dit.hp.hrtc_app.AttendanceModule.attendanceModals;


import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.kyc.KycResData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class AttendanceObject implements Serializable {

    private String name;
    private String beatName;
    private String location;
    private String dateTime;  //2025-05-23 09:15:00.000
    private String punchInOut;
    private String workDone;
    private String remarks;
    private String userId;


    private KycResData aadhaarEkyc; // Face Auth Data

    public KycResData getAadhaarEkyc() {
        return aadhaarEkyc;
    }

    public void setAadhaarEkyc(KycResData aadhaarEkyc) {
        this.aadhaarEkyc = aadhaarEkyc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeatName() {
        return beatName;
    }

    public void setBeatName(String beatName) {
        this.beatName = beatName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPunchInOut() {
        return punchInOut;
    }

    public void setPunchInOut(String punchInOut) {
        this.punchInOut = punchInOut;
    }

    public String getWorkDone() {
        return workDone;
    }

    public void setWorkDone(String workDone) {
        this.workDone = workDone;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "AttendanceObject{" +
                "name='" + name + '\'' +
                ", beatName='" + beatName + '\'' +
                ", location='" + location + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", punchInOut='" + punchInOut + '\'' +
                ", workDone='" + workDone + '\'' +
                ", remarks='" + remarks + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    public JSONObject getAttendanceObjectJson() {
        try {
            JSONObject attendanceObject = new JSONObject();
            attendanceObject.put("name", name);
            attendanceObject.put("beatName", beatName);
            attendanceObject.put("location", location);
            attendanceObject.put("dateTime", dateTime);
            attendanceObject.put("punchInOut", punchInOut);
            attendanceObject.put("workDone", workDone);
            attendanceObject.put("remarks", remarks);
            attendanceObject.put("userId", userId);

            if (aadhaarEkyc != null) {
                attendanceObject.put("aadhaarData", this.getAadhaarEkyc().jsonKYCRESObj());
            }

                return attendanceObject;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
