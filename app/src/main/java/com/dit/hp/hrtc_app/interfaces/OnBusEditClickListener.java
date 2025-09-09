package com.dit.hp.hrtc_app.interfaces;

import com.dit.hp.hrtc_app.Modals.VehiclePojo;

public interface OnBusEditClickListener {
    void onEditClick(VehiclePojo vehiclePojo, int position);
    void onRemoveClick(VehiclePojo vehiclePojo, int position);
}
