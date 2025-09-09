package com.dit.hp.hrtc_app.interfaces;

import com.dit.hp.hrtc_app.Modals.StopPojo;

public interface OnStopRemoveClickListener {
    void onStopRemoveClick(StopPojo selectedPojo, int position);
    void onStopEditClick(StopPojo selectedPojo, int position);
}
