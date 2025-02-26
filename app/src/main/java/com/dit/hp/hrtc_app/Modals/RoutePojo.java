package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoutePojo implements Serializable {

    private int journeyHours;
    private int distance;

    private int routeId;
    private String routeName;

    private String startTime;
    private String endTime;

    private LocationsPojo startLocationPojo;
    private LocationsPojo endLocationPojo;

    private RouteTypePojo routeTypePojo;

    private DepotPojo depotPojo;

    List <StopPojo> stopsPojoList = new ArrayList<>();

    // Constructor
    public RoutePojo(int routeId, String routeName) {
        this.routeId = routeId;
        this.routeName = routeName;
    }

    public RoutePojo() {
    }

    public List<StopPojo> getStopsPojoList() {
        return stopsPojoList;
    }

    public void setStopsPojoList(List<StopPojo> stopsPojoList) {
        this.stopsPojoList = stopsPojoList;
    }

    public DepotPojo getDepotPojo() {
        return depotPojo;
    }

    public void setDepotPojo(DepotPojo depotPojo) {
        this.depotPojo = depotPojo;
    }

    public int getJourneyHours() {
        return journeyHours;
    }

    public void setJourneyHours(int journeyHours) {
        this.journeyHours = journeyHours;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public LocationsPojo getStartLocationPojo() {
        return startLocationPojo;
    }

    public void setStartLocationPojo(LocationsPojo startLocationPojo) {
        this.startLocationPojo = startLocationPojo;
    }

    public LocationsPojo getEndLocationPojo() {
        return endLocationPojo;
    }

    public void setEndLocationPojo(LocationsPojo endLocationPojo) {
        this.endLocationPojo = endLocationPojo;
    }

    public RouteTypePojo getRouteTypePojo() {
        return routeTypePojo;
    }

    public void setRouteTypePojo(RouteTypePojo routeTypePojo) {
        this.routeTypePojo = routeTypePojo;
    }

    @Override
    public String toString() {
        return routeName;
    }
}