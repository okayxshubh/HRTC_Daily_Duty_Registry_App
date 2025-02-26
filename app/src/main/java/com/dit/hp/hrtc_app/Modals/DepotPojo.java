package com.dit.hp.hrtc_app.Modals;

import androidx.annotation.NonNull;

import java.io.Serializable;
public class DepotPojo implements Serializable {

    Integer id;
    String depotName;
    String depotCode;
    private LocationsPojo location;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDepotName() {
        return depotName;
    }

    public void setDepotName(String depotName) {
        this.depotName = depotName;
    }

    public String getDepotCode() {
        return depotCode;
    }

    public void setDepotCode(String depotCode) {
        this.depotCode = depotCode;
    }

    public LocationsPojo getLocation() {
        return location;
    }

    public void setLocation(LocationsPojo location) {
        this.location = location;
    }

    @NonNull
    @Override
    public String toString() {
        return depotName;
    }
}

