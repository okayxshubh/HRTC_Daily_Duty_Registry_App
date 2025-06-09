package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.kyc;

import java.io.Serializable;

public class Poa implements Serializable {

    private String co;
    private String house;
    private String loc;
    private String vtc;
    private String dist;
    private String state;
    private String pc;

    private String po;

    private String subdist;

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getSubdist() {
        return subdist;
    }

    public void setSubdist(String subdist) {
        this.subdist = subdist;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getVtc() {
        return vtc;
    }

    public void setVtc(String vtc) {
        this.vtc = vtc;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPc() {
        return pc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    @Override
    public String toString() {
        return "Poa{" +
                "co='" + co + '\'' +
                ", house='" + house + '\'' +
                ", loc='" + loc + '\'' +
                ", vtc='" + vtc + '\'' +
                ", dist='" + dist + '\'' +
                ", state='" + state + '\'' +
                ", pc='" + pc + '\'' +
                ", po='" + po + '\'' +
                ", subdist='" + subdist + '\'' +
                '}';
    }
}
