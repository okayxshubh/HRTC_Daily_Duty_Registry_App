package com.dit.hp.hrtc_app.interfaces;

import com.dit.hp.hrtc_app.Modals.AddaPojo;

public interface OnAddaCardClickListeners {

    void onEditClick(AddaPojo selectedPojo, int position);
    void onDeleteClick(AddaPojo selectedPojo, int position);
    void onMoreInfoClick(AddaPojo selectedPojo, int position);

}
