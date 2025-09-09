package com.dit.hp.hrtc_app.Modals;
import java.io.Serializable;
public class StopPojo implements Serializable {

    private int stopId;
    private String stopName;
    private int stopSequence;

    public StopPojo(int stopId, String stopName, int stopSequence) {
        this.stopId = stopId;
        this.stopName = stopName;
        this.stopSequence = stopSequence;
    }

    public StopPojo(int stopId, String stopName) {
        this.stopId = stopId;
        this.stopName = stopName;
    }

    public StopPojo() {
    }

    public int getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(int stopSequence) {
        this.stopSequence = stopSequence;
    }

    public int getStopId() {
        return stopId;
    }

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    @Override
    public String toString() {
        return stopSequence + ": " + stopName;
    }

}