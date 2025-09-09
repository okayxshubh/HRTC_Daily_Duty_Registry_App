package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.kyc;

import java.io.Serializable;

public class UidData implements Serializable {

    private String uid;
    private Poi poi;
    private Poa poa;
    private String pht;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Poi getPoi() {
        return poi;
    }

    public void setPoi(Poi poi) {
        this.poi = poi;
    }

    public Poa getPoa() {
        return poa;
    }

    public void setPoa(Poa poa) {
        this.poa = poa;
    }

    public String getPht() {
        return pht;
    }

    public void setPht(String pht) {
        this.pht = pht;
    }

    @Override
    public String toString() {
        return "UidData{" +
                "uid='" + uid + '\'' +
                ", poi=" + poi +
                ", poa=" + poa +
                '}';
    }
}