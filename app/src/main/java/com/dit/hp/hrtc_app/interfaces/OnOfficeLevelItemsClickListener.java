package com.dit.hp.hrtc_app.interfaces;

import com.dit.hp.hrtc_app.Modals.OfficeLevel;

public interface OnOfficeLevelItemsClickListener {
    void onOfficeLevelRemoveClick(OfficeLevel selectedPojo, int position);
    void onOfficeLevelEditClick(OfficeLevel selectedPojo, int position);
}
