package com.dit.hp.hrtc_app.interfaces;

import com.dit.hp.hrtc_app.Modals.RoutePojo;

public interface OnRouteEditClickListener {
    void onEditClick(RoutePojo selectedPojo, int position);
    void onStopsClick(RoutePojo selectedPojo, int position);
    void onRemoveClick(RoutePojo selectedPojo, int position);
}
