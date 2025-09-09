package com.dit.hp.hrtc_app.Modals;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class AddaPojo implements Serializable {

    Integer id;
    String addaName;
    private LocationsPojo location;
    private DepotPojo depot;
    private StaffPojo addaIncharge;
    private String remarks;


    public Integer getId() {
        return id;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddaName() {
        return addaName;
    }

    public void setAddaName(String addaName) {
        this.addaName = addaName;
    }

    public LocationsPojo getLocation() {
        return location;
    }

    public void setLocation(LocationsPojo location) {
        this.location = location;
    }

    public DepotPojo getDepot() {
        return depot;
    }

    public void setDepot(DepotPojo depot) {
        this.depot = depot;
    }

    public StaffPojo getAddaIncharge() {
        return addaIncharge;
    }

    public void setAddaIncharge(StaffPojo addaIncharge) {
        this.addaIncharge = addaIncharge;
    }

    @NonNull
    @Override
    public String toString() {
        return addaName;
    }
}

