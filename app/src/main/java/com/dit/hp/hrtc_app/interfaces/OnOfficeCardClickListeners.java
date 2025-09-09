package com.dit.hp.hrtc_app.interfaces;

import com.dit.hp.hrtc_app.Modals.OfficePojo;

public interface OnOfficeCardClickListeners {

    void onEditClick(OfficePojo selectedPojo, int position);
    void onDeleteClick(OfficePojo selectedPojo, int position);
    void onMoreInfoClick(OfficePojo selectedPojo, int position);

}
