package com.dit.hp.hrtc_app.interfaces;

import com.dit.hp.hrtc_app.Modals.StaffPojo;

public interface OnStaffCardClickListeners {

    void onEditClick(StaffPojo selectedPojo, int position);
    void onDeleteClick(StaffPojo selectedPojo, int position);
    void onMoreInfoClick(StaffPojo selectedPojo, int position);

}
