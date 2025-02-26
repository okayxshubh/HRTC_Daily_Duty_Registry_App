package com.dit.hp.hrtc_app.Modals;


import java.io.Serializable;

public class DailyRegisterCardFinal implements Serializable {

    int recordId;
    String recordDate;
    String actionTaken;
    String remark;
    String actualDepartureTime;

    RoutePojo otherRoute;
    RoutePojo originalRoute;

    StaffPojo mainDriver;
    StaffPojo mainConductor;

    StaffPojo relievingDriver;
    StaffPojo relievingConductor;

    StopPojo relievingDriverStop;
    StopPojo relievingConductorStop;

    VehiclePojo assignedVehicle;

    StopPojo finalStop;

    public String getActualDepartureTime() {
        return actualDepartureTime;
    }

    public void setActualDepartureTime(String actualDepartureTime) {
        this.actualDepartureTime = actualDepartureTime;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public StopPojo getFinalStop() {
        return finalStop;
    }

    public void setFinalStop(StopPojo finalStop) {
        this.finalStop = finalStop;
    }

    public VehiclePojo getAssignedVehicle() {
        return assignedVehicle;
    }

    public void setAssignedVehicle(VehiclePojo assignedVehicle) {
        this.assignedVehicle = assignedVehicle;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public RoutePojo getOtherRoute() {
        return otherRoute;
    }

    public void setOtherRoute(RoutePojo otherRoute) {
        this.otherRoute = otherRoute;
    }

    public RoutePojo getOriginalRoute() {
        return originalRoute;
    }

    public void setOriginalRoute(RoutePojo originalRoute) {
        this.originalRoute = originalRoute;
    }

    public StaffPojo getMainDriver() {
        return mainDriver;
    }

    public void setMainDriver(StaffPojo mainDriver) {
        this.mainDriver = mainDriver;
    }

    public StaffPojo getMainConductor() {
        return mainConductor;
    }

    public void setMainConductor(StaffPojo mainConductor) {
        this.mainConductor = mainConductor;
    }

    public StaffPojo getRelievingDriver() {
        return relievingDriver;
    }

    public void setRelievingDriver(StaffPojo relievingDriver) {
        this.relievingDriver = relievingDriver;
    }

    public StaffPojo getRelievingConductor() {
        return relievingConductor;
    }

    public void setRelievingConductor(StaffPojo relievingConductor) {
        this.relievingConductor = relievingConductor;
    }

    public StopPojo getRelievingDriverStop() {
        return relievingDriverStop;
    }

    public void setRelievingDriverStop(StopPojo relievingDriverStop) {
        this.relievingDriverStop = relievingDriverStop;
    }

    public StopPojo getRelievingConductorStop() {
        return relievingConductorStop;
    }

    public void setRelievingConductorStop(StopPojo relievingConductorStop) {
        this.relievingConductorStop = relievingConductorStop;
    }

    @Override
    public String toString() {
        return "DailyRegisterCardFinal{" +
                "recordDate='" + recordDate + '\'' +
                ", actionTaken='" + actionTaken + '\'' +
                ", remark='" + remark + '\'' +
                ", otherRoute=" + otherRoute +
                ", originalRoute=" + originalRoute +
                ", mainDriver=" + mainDriver +
                ", mainConductor=" + mainConductor +
                ", relievingDriver=" + relievingDriver +
                ", relievingConductor=" + relievingConductor +
                ", relievingDriverStop=" + relievingDriverStop +
                ", relievingConductorStop=" + relievingConductorStop +
                '}';
    }
}
