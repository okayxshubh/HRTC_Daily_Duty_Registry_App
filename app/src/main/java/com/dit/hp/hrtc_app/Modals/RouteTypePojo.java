package com.dit.hp.hrtc_app.Modals;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class RouteTypePojo implements Serializable {

    Integer routeTypeId;
    String routeTypeName;

    public Integer getRouteTypeId() {
        return routeTypeId;
    }

    public void setRouteTypeId(Integer routeTypeId) {
        this.routeTypeId = routeTypeId;
    }

    public String getRouteTypeName() {
        return routeTypeName;
    }

    public void setRouteTypeName(String routeTypeName) {
        this.routeTypeName = routeTypeName;
    }

    @NonNull
    @Override
    public String toString() {
        return routeTypeName;
    }
}

