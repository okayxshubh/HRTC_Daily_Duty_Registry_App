package com.dit.hp.hrtc_app.interfaces;

import com.dit.hp.hrtc_app.Modals.DepotPojo;

public interface OnDepotCardClickListeners {

    void onEditClick(DepotPojo selectedPojo, int position);
    void onDeleteClick(DepotPojo selectedPojo, int position);
    void onMoreInfoClick(DepotPojo selectedPojo, int position);

}
