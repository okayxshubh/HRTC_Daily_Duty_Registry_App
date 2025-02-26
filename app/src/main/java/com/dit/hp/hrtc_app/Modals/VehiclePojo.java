package com.dit.hp.hrtc_app.Modals;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class VehiclePojo implements Serializable {

    private int id;
    private String vehicleNumber;
    private String vehicleModel;
    private int capacity;
    private String iotFirm;
    private DepotPojo depot;
    private VehicleTypePojo vehicleType;

    public VehiclePojo(int id, String vehicleNumber) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
    }

    public VehiclePojo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getIotFirm() {
        return iotFirm;
    }

    public void setIotFirm(String iotFirm) {
        this.iotFirm = iotFirm;
    }

    public DepotPojo getDepot() {
        return depot;
    }

    public void setDepot(DepotPojo depot) {
        this.depot = depot;
    }

    public VehicleTypePojo getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleTypePojo vehicleType) {
        this.vehicleType = vehicleType;
    }

    @NonNull
    @Override
    public String toString() {
        return vehicleNumber;
    }
}
