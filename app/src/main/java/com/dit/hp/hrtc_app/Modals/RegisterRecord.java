package com.dit.hp.hrtc_app.Modals;//package com.dit.hp.hrtc_app.Modals;
//
//import com.dit.hp.hrtc_app.utilities.Preferences;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.Serializable;
//
//public class RegisterRecord implements Serializable {
//
//    String depotId;
//    String depotNumber;
//    String routeId, route;
//    String vehicleNo;
//    String driverId, driver;
//    String conductorId, conductor;
//    String journeyDate, departureTime;
//    String remarks;
//    String userName;
//    String createdByuserName;
//    String lastUpdatedByuserName;
//
//    public String getId() {
//        return depotId;
//    }
//
//    public void setdepotId(String depotId) {
//        this.depotId = depotId;
//    }
//
//    public String getDepotNumber() {
//        return depotNumber;
//    }
//
//    public void setDepotNumber(String depotNumber) {
//        this.depotNumber = depotNumber;
//    }
//
//    public String getRouteId() {
//        return routeId;
//    }
//
//    public void setRouteId(String routeId) {
//        this.routeId = routeId;
//    }
//
//    public String getRoute() {
//        return route;
//    }
//
//    public void setRoute(String route) {
//        this.route = route;
//    }
//
//    public String getVehicleNo() {
//        return vehicleNo;
//    }
//
//    public void setVehicleNo(String vehicleNo) {
//        this.vehicleNo = vehicleNo;
//    }
//
//    public String getId() {
//        return driverId;
//    }
//
//    public void setDriverId(String driverId) {
//        this.driverId = driverId;
//    }
//
//    public String getDriver() {
//        return driver;
//    }
//
//    public void setDriver(String driver) {
//        this.driver = driver;
//    }
//
//    public String getId() {
//        return conductorId;
//    }
//
//    public void setId(String conductorId) {
//        this.conductorId = conductorId;
//    }
//
//    public String getConductor() {
//        return conductor;
//    }
//
//    public void setConductor(String conductor) {
//        this.conductor = conductor;
//    }
//
//    public String getJourneyDate() {
//        return journeyDate;
//    }
//
//    public void setJourneyDate(String journeyDate) {
//        this.journeyDate = journeyDate;
//    }
//
//    public String getDepartureTime() {
//        return departureTime;
//    }
//
//    public void setDepartureTime(String departureTime) {
//        this.departureTime = departureTime;
//    }
//
//    public String getRemarks() {
//        return remarks;
//    }
//
//    public void setRemarks(String remarks) {
//        this.remarks = remarks;
//    }
//
//    public String getCreatedByuserName() {
//        return createdByuserName;
//    }
//
//    public void setCreatedByuserName(String createdByuserName) {
//        this.createdByuserName = createdByuserName;
//    }
//
//    public String getLastUpdatedByuserName() {
//        return lastUpdatedByuserName;
//    }
//
//    public void setLastUpdatedByuserName(String lastUpdatedByuserName) {
//        this.lastUpdatedByuserName = lastUpdatedByuserName;
//    }
//
//    // Override toString() method for better readability
//    @Override
//    public String toString() {
//        return "RegisterRecord{" +
//                "depotId='" + depotId + '\'' +
//                ", depotNumber='" + depotNumber + '\'' +
//                ", routeId='" + routeId + '\'' +
//                ", route='" + route + '\'' +
//                ", vehicleNo='" + vehicleNo + '\'' +
//                ", driverId='" + driverId + '\'' +
//                ", driver='" + driver + '\'' +
//                ", conductorId='" + conductorId + '\'' +
//                ", conductor='" + conductor + '\'' +
//                ", journeyDate='" + journeyDate + '\'' +
//                ", departureTime='" + departureTime + '\'' +
//                ", remarks='" + remarks + '\'' +
//                '}';
//    }
//
//    public String jsonFamily() {
//        try {
//            JSONObject jsonObject = new JSONObject();
//
//            // Create the "id" object
//            JSONObject idObject = new JSONObject();
//            idObject.put("date", this.journeyDate);  // Assuming journeyDate is a field in the class
//            idObject.put("routeId", this.routeId);   // Assuming routeId is a field in the class
//            jsonObject.put("id", idObject);
//
//            // Create the "route" object
//            JSONObject routeObject = new JSONObject();
//            routeObject.put("routeId", this.routeId);
//            jsonObject.put("route", routeObject);
//
//            // Create the "vehicle" object
//            JSONObject vehicleObject = new JSONObject();
//            vehicleObject.put("vehicleNo", this.vehicleNo);
//            jsonObject.put("vehicle", vehicleObject);
//
//            // Create the "adda" object
//            JSONObject addaObject = new JSONObject();
//            addaObject.put("addaNo", this.depotId);
//            jsonObject.put("adda", addaObject);
//
//            // Create the "conductor" object
//            JSONObject conductorObject = new JSONObject();
//            conductorObject.put("staffId", this.conductorId);
//            jsonObject.put("conductor", conductorObject);
//
//            // Create the "driver" object
//            JSONObject driverObject = new JSONObject();
//            driverObject.put("staffId", this.driverId);
//            jsonObject.put("driver", driverObject);
//
//            // Create the "depot" object
//            JSONObject depotObject = new JSONObject();
//            depotObject.put("depotId", Preferences.getInstance().depotId);  // Assuming depotNumber is accessible via Preferences
//            jsonObject.put("depot", depotObject);
//
//            // Add "remarks"
//            jsonObject.put("remarks", this.remarks);
//
//            // Add "createdBy" object
//            JSONObject createdByObject = new JSONObject();
//            createdByObject.put("userName", this.createdByuserName);  // Assuming createdByuserName is available
//            jsonObject.put("createdBy", createdByObject);
//
//            // Add "lastUpdatedBy" object
//            JSONObject lastUpdatedByObject = new JSONObject();
//            lastUpdatedByObject.put("userName", this.lastUpdatedByuserName);  // Assuming lastUpdatedByuserName is available
//            jsonObject.put("lastUpdatedBy", lastUpdatedByObject);
//
//            // Return the final JSON string
//            return jsonObject.toString();
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return e.getMessage();  // Return error message if JSON conversion fails
//        }
//    }
//
//
//}