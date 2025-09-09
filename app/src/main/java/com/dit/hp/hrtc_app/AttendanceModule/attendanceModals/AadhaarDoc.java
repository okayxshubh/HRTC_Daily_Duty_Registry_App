package com.dit.hp.hrtc_app.AttendanceModule.attendanceModals;

import java.io.Serializable;

public class AadhaarDoc implements Serializable {

    private String docName;
    private String docPath;

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

    @Override
    public String toString() {
        return "AadhaarDoc{" +
                "docName='" + docName + '\'' +
                ", docPath='" + docPath + '\'' +
                '}';
    }
}
