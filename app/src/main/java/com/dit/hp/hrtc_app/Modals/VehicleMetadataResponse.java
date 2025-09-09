package com.dit.hp.hrtc_app.Modals;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VehicleMetadataResponse {
    @SerializedName("data")
    private Metadata data;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    public Metadata getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public class Metadata {
        @SerializedName("vehicleMakeAndModels")
        private List<String> vehicleMakeAndModels;

        @SerializedName("vehicleTypes")
        private List<String> vehicleTypes;

        @SerializedName("vehicleIots")
        private List<String> vehicleIots;

        public List<String> getVehicleMakeAndModels() {
            return vehicleMakeAndModels;
        }

        public List<String> getVehicleTypes() {
            return vehicleTypes;
        }

        public List<String> getVehicleIots() {
            return vehicleIots;
        }
    }
}
