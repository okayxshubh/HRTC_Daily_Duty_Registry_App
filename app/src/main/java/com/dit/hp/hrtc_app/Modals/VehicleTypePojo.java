package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

// GenderPojo
public class VehicleTypePojo implements Serializable {
    private int vehicleTypeId;
    private String vehicleTypeName;

    public int getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(int vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getVehicleTypeName() {
        return vehicleTypeName;
    }

    public void setVehicleTypeName(String vehicleTypeName) {
        this.vehicleTypeName = vehicleTypeName;
    }

    @Override
    public String toString() {
        return vehicleTypeName;
    }
}
