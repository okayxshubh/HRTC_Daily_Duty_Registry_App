package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class DistrictPojo implements Serializable{
    private int districtLgdCode;
    private int districtId;
    private String districtName;

    public int getDistrictLgdCode() {
        return districtLgdCode;
    }

    public void setDistrictLgdCode(int districtLgdCode) {
        this.districtLgdCode = districtLgdCode;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    @Override
    public String toString() {
        return districtName;
    }
}