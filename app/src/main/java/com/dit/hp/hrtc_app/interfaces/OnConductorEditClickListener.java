package com.dit.hp.hrtc_app.interfaces;

import com.dit.hp.hrtc_app.Modals.StaffPojo;

public interface OnConductorEditClickListener {
    void onEditClick(StaffPojo selectedPojo, int position);

    void onRemoveClick(StaffPojo conductor, int position);
}
