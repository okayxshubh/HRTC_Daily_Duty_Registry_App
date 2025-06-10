package com.dit.hp.hrtc_app.json;

import android.util.Log;

import com.dit.hp.hrtc_app.Modals.AddaPojo;
import com.dit.hp.hrtc_app.Modals.AdditonalChargePojo;
import com.dit.hp.hrtc_app.Modals.BlockPojo;
import com.dit.hp.hrtc_app.Modals.CastePojo;
import com.dit.hp.hrtc_app.Modals.DailyRegisterCardFinal;
import com.dit.hp.hrtc_app.Modals.DepartmentPojo;
import com.dit.hp.hrtc_app.Modals.DepotPojo;
import com.dit.hp.hrtc_app.Modals.DesignationPojo;
import com.dit.hp.hrtc_app.Modals.DistrictPojo;
import com.dit.hp.hrtc_app.Modals.EmployeePojo;
import com.dit.hp.hrtc_app.Modals.EmploymentTypePojo;
import com.dit.hp.hrtc_app.Modals.GenderPojo;
import com.dit.hp.hrtc_app.Modals.HimAccessUser;
import com.dit.hp.hrtc_app.Modals.HimAccessUserInfo;
import com.dit.hp.hrtc_app.Modals.LocationsPojo;
import com.dit.hp.hrtc_app.Modals.MunicipalPojo;
import com.dit.hp.hrtc_app.Modals.OfficeLevel;
import com.dit.hp.hrtc_app.Modals.OfficePojo;
import com.dit.hp.hrtc_app.Modals.OfficeSelectionPojo;
import com.dit.hp.hrtc_app.Modals.OrganisationPojo;
import com.dit.hp.hrtc_app.Modals.PanchayatPojo;
import com.dit.hp.hrtc_app.Modals.RoutePojo;
import com.dit.hp.hrtc_app.Modals.RouteTypePojo;
import com.dit.hp.hrtc_app.Modals.StaffPojo;
import com.dit.hp.hrtc_app.Modals.StaffTypePojo;
import com.dit.hp.hrtc_app.Modals.StopPojo;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.TokenInfo;
import com.dit.hp.hrtc_app.Modals.User;
import com.dit.hp.hrtc_app.Modals.VehiclePojo;
import com.dit.hp.hrtc_app.Modals.VehicleTypePojo;
import com.dit.hp.hrtc_app.Modals.VillagePojo;
import com.dit.hp.hrtc_app.Modals.WardPojo;
import com.dit.hp.hrtc_app.crypto.AESCrypto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JsonParse {

    // Parse response for every service call
    public static SuccessResponse getDecryptedSuccessResponse(String data) throws JSONException {
        AESCrypto aesCrypto = new AESCrypto();
        JSONObject responseObject = new JSONObject(data);

        SuccessResponse sr = new SuccessResponse();
        sr.setStatus(responseObject.optString("status"));
        sr.setMessage(responseObject.optString("message"));

        String encryptedData = responseObject.optString("data");
        try {
            // Decrypt and set data as JSON array string
            String decryptedData = aesCrypto.decrypt(encryptedData);
            sr.setData(decryptedData); // Set decrypted data directly
            Log.i("Decrypted data array", decryptedData);
        } catch (Exception e) {
            Log.e("Decryption Error", "Error while decrypting data: " + e.getMessage());
            e.printStackTrace();
        }

        return sr;
    }


    // For Submitting Attendance
    public static SuccessResponse getSuccessResponseFinal(String data) {
        SuccessResponse sr = new SuccessResponse();

        try {
            JSONObject responseObject = new JSONObject(data);

            if (responseObject.has("status")) {
                sr.setStatus(responseObject.optString("status"));
            }
            if (responseObject.has("message")) {
                sr.setMessage(responseObject.optString("message"));
            }
            if (responseObject.has("error")) {
                sr.setError(responseObject.optString("error"));
            }
            if (responseObject.has("data")) {
                sr.setResponse(responseObject.optString("data"));
            }
        } catch (JSONException e) {
            Log.e("Parsing Error", "Parsing Error: " + e.getMessage());
            e.printStackTrace(); // Log error but continue execution
        }

        return sr;
    }

    public static User parseDecryptedUserInfo(String response) {
        User user = null;
        try {
            // Convert response string to JSON Object
            JSONObject userData = new JSONObject(response);

            // Map data to User object
            user = new User();
            user.setEmpId(userData.optInt("empId"));
            user.setDepotName(userData.optString("depotName"));
            user.setRoleId(userData.optInt("roleId"));
            user.setRoleName(userData.optString("roleName"));
            user.setDepotId(userData.optInt("depotId"));
            user.setuserName(userData.optString("userName"));
            user.setToken(userData.optString("token"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public static HimAccessUser parseDecryptedHimAccessUserInfo(String response) {
        HimAccessUser user = null;
        try {
            JSONObject userData = new JSONObject(response);

            user = new HimAccessUser();
            user.setFirstName(userData.optString("firstName"));
            user.setUid(userData.optString("uid"));
            user.setMail(userData.optString("mail"));
            user.setMobile(userData.optString("mobile"));
            user.setDateOfRetirement(userData.optString("dateOfRetirement"));
            user.setDateOfBirth(userData.optString("dateOfBirth"));
            user.setCn(userData.optString("cn"));
            user.setSn(userData.optString("sn"));
            user.setDesignation(userData.optString("designation")); // null-safe
            user.setDepartment(userData.optString("department"));   // null-safe

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public static TokenInfo parseTokenInfo(String dataString) {
        try {
            // Clean escape characters if any
            dataString = dataString.replace("\\", "");
            if (dataString.startsWith("\"")) {
                dataString = dataString.substring(1, dataString.length() - 1);
            }

            // Directly parse JSONArray
            JSONArray dataArray = new JSONArray(dataString);
            JSONObject item = dataArray.getJSONObject(0);

            TokenInfo info = new TokenInfo();
            info.setEmail(item.optString("email"));
            info.setSuccessUrl(item.optString("successUrl"));
            info.setToken(item.optString("token"));
            info.setTokenValidTill(item.optString("tokenValidTill"));

            return info;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static HimAccessUserInfo parseUserInfoPojo(String data) {

        HimAccessUserInfo himAccessUserInfo = new HimAccessUserInfo();
        try {
            JSONArray jsonArray = new JSONArray(data);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                // Employee
                EmployeePojo employee = new EmployeePojo();
                employee.setEmpId(obj.optInt("empId"));
                employee.setEmployeeName(obj.optString("employeeName"));
                employee.setEmailId(obj.optString("emailId"));
                employee.setPmisCode(obj.optString("pmisCode"));
                employee.setSalaryCode(obj.optString("salaryCode"));
                employee.setCadre(obj.optString("cadre"));
                himAccessUserInfo.setEmployeePojo(employee);

                // Basic Fields
                himAccessUserInfo.setApplicationId(obj.optInt("applicationId"));
                himAccessUserInfo.setApplicationName(obj.optString("applicationName"));
                himAccessUserInfo.setServiceId(obj.optInt("serviceId"));
                himAccessUserInfo.setServiceName(obj.optString("serviceName"));
                himAccessUserInfo.setAppRoleId(obj.optInt("appRoleId"));
                himAccessUserInfo.setRoleId(obj.optInt("roleId"));
                himAccessUserInfo.setRoleName(obj.optString("roleName"));

                // Organisation
                OrganisationPojo organisation = new OrganisationPojo();
                organisation.setOrganisationId(obj.optInt("orgId"));
                organisation.setOrganisationName(obj.optString("orgName"));
                himAccessUserInfo.setOrganisationPojo(organisation);

                // Department
                DepartmentPojo department = new DepartmentPojo();
                department.setDepartmentId(obj.optInt("departmentId"));
                department.setDepartmentCode(obj.optString("departmentCode"));
                department.setDepartmentName(obj.optString("departmentName"));
                himAccessUserInfo.setMainDepartmentPojo(department);

                // Designation
                DesignationPojo designation = new DesignationPojo();
                designation.setDesignationId(obj.optInt("desigId"));
                designation.setDesignationName(obj.optString("desigName"));
                himAccessUserInfo.setMainDesignationPojo(designation);

                // OfficeType
                OfficeLevel officeType = new OfficeLevel();
                officeType.setOfficeLevelId(obj.optInt("officeTypeId"));
                officeType.setOfficeLevelName(obj.optString("officeTypeName"));
                himAccessUserInfo.setMainOfficeLevelPojo(officeType);

                // Office
                OfficePojo office = new OfficePojo();
                office.setOfficeId(obj.optInt("officeId"));
                office.setOfficeName(obj.optString("officeName"));
                himAccessUserInfo.setMainOffice(office);

                // Additional Charge
                JSONArray chargeArray = obj.optJSONArray("additionalChargeDetailDTO");
                List<AdditonalChargePojo> chargeList = new ArrayList<>();

                if (chargeArray != null) {
                    for (int j = 0; j < chargeArray.length(); j++) {
                        JSONObject chargeObj = chargeArray.getJSONObject(j);

                        AdditonalChargePojo charge = new AdditonalChargePojo();

                        DepartmentPojo dept = new DepartmentPojo();
                        dept.setDepartmentId(chargeObj.optInt("departmentId"));
                        dept.setDepartmentName(chargeObj.optString("departmentName"));
                        charge.setDepartmentPojo(dept);

                        DesignationPojo desig = new DesignationPojo();
                        desig.setDesignationId(chargeObj.optInt("desigId"));
                        desig.setDesignationName(chargeObj.optString("desigName"));
                        charge.setDesignationPojo(desig);

                        OfficePojo off = new OfficePojo();
                        off.setOfficeId(chargeObj.optInt("officeId"));
                        off.setOfficeName(chargeObj.optString("officeName"));
                        charge.setOfficePojo(off);

                        charge.setChargeAssignedOn(chargeObj.optString("chargeAssignedOn"));
                        charge.setChargeType(chargeObj.optString("chargeType"));

                        chargeList.add(charge);
                    }
                }
                himAccessUserInfo.setAdditionalChargeDetailDTO(chargeList);

                // Unknown part
//                himAccessUserInfo.setDdoOfficeDetails(new ArrayList<>()); // Placeholder

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return himAccessUserInfo;
    }


    public static List<VehiclePojo> parseVehicleList(String data) {
        List<VehiclePojo> vehicleList = new ArrayList<>();

        try {
            // Convert the data string to a JSONArray
            JSONArray dataArray = new JSONArray(data);

            // Iterate over each object in the JSONArray
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject vehicleObject = dataArray.getJSONObject(i);

                // Create a new VehiclePojo object
                VehiclePojo vehicle = new VehiclePojo();

                // Set the fields of VehiclePojo using data from the JSON object
                vehicle.setId(vehicleObject.optInt("id"));
                vehicle.setVehicleNumber(vehicleObject.optString("vehicleNumber"));
                vehicle.setCapacity(vehicleObject.optInt("capacity"));
                vehicle.setIotFirm(vehicleObject.optString("iotFirm"));
                vehicle.setVehicleModel(vehicleObject.optString("vehicleModel"));

                // Parse the Depot object
                JSONObject depotObject = vehicleObject.optJSONObject("depot");
                if (depotObject != null) {
                    DepotPojo depot = new DepotPojo();
                    depot.setDepotName(depotObject.optString("depotName"));
                    depot.setDepotCode(depotObject.optString("depotCode"));
                    depot.setId(depotObject.optInt("depotId"));
                    vehicle.setDepot(depot);
                }

                // Parse the VehicleType object
                JSONObject vehicleTypeObject = vehicleObject.optJSONObject("vehicleType");
                if (vehicleTypeObject != null) {
                    VehicleTypePojo vehicleType = new VehicleTypePojo();
                    vehicleType.setVehicleTypeId(vehicleTypeObject.optInt("id"));
                    vehicleType.setVehicleTypeName(vehicleTypeObject.optString("vehicleTypeName"));
                    vehicle.setVehicleType(vehicleType);
                }

                // Add the vehicle to the list
                vehicleList.add(vehicle);
            }
        } catch (Exception e) {
            e.printStackTrace();  // Handle any exceptions that occur during parsing
        }

        return vehicleList;
    }


    public static List<VehiclePojo> parseVehicleListForCards(String data) {
        List<VehiclePojo> vehicleList = new ArrayList<>();

        try {
            // Try parsing the data as a JSONObject
            JSONObject dataObject = null;
            JSONArray contentArray = null;

            try {
                // Attempt to parse as a JSONObject
                dataObject = new JSONObject(data);
                if (dataObject.has("content")) {
                    // For paginated response
                    contentArray = dataObject.getJSONArray("content");
                }
            } catch (Exception e) {
                // If not a JSONObject, treat the data as a raw JSONArray
                contentArray = new JSONArray(data);
            }

            // If contentArray is still null, it means neither JSONObject nor JSONArray
            if (contentArray == null) {
                return vehicleList; // Return empty list if the data format is unexpected
            }

            // Iterate over the JSONArray
            for (int i = 0; i < contentArray.length(); i++) {
                JSONObject vehicleObject = contentArray.getJSONObject(i);

                // Create and populate the VehiclePojo object
                VehiclePojo vehicle = new VehiclePojo();
                vehicle.setId(vehicleObject.optInt("id"));
                vehicle.setVehicleNumber(vehicleObject.optString("vehicleNumber"));
                vehicle.setCapacity(vehicleObject.optInt("capacity"));
                vehicle.setIotFirm(vehicleObject.optString("iotFirm"));
                vehicle.setVehicleModel(vehicleObject.optString("vehicleModel"));

                // Parse Depot object
                JSONObject depotObject = vehicleObject.optJSONObject("depot");
                if (depotObject != null) {
                    DepotPojo depot = new DepotPojo();
                    depot.setDepotName(depotObject.optString("depotName"));
                    depot.setDepotCode(depotObject.optString("depotCode"));
                    depot.setId(depotObject.optInt("id"));
                    vehicle.setDepot(depot);
                }

                // Parse VehicleType object
                JSONObject vehicleTypeObject = vehicleObject.optJSONObject("vehicleType");
                if (vehicleTypeObject != null) {
                    VehicleTypePojo vehicleType = new VehicleTypePojo();
                    vehicleType.setVehicleTypeId(vehicleTypeObject.optInt("id"));
                    vehicleType.setVehicleTypeName(vehicleTypeObject.optString("vehicleTypeName"));
                    vehicle.setVehicleType(vehicleType);
                }

                // Add vehicle to the list
                vehicleList.add(vehicle);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle parsing errors
        }

        return vehicleList;
    }

    public static List<VehiclePojo> parseVehicleForSpinner(String data) {
        List<VehiclePojo> vehicleList = new ArrayList<>();

        try {
            // Convert the data string to a JSONArray
            JSONArray dataArray = new JSONArray(data);

            // Iterate over each object in the JSONArray
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject vehicleObject = dataArray.getJSONObject(i);

                // Create a new VehiclePojo object
                VehiclePojo vehicle = new VehiclePojo();

                // Only set vehicle ID and vehicle number for the spinner
                vehicle.setId(vehicleObject.optInt("id"));
                vehicle.setVehicleNumber(vehicleObject.optString("vehicleNumber"));

                // Add the vehicle to the list
                vehicleList.add(vehicle);
            }
        } catch (Exception e) {
            e.printStackTrace();  // Handle any exceptions that occur during parsing
        }

        return vehicleList;
    }


    // COMBINED DRIVER CONDUCTOR + Only Parse Name and IDs
    public static void parseDecryptedStaffList(String response, List<StaffPojo> driverList, List<StaffPojo> conductorList) throws JSONException {
        // Parse the response JSON string into a JSONArray
        JSONArray staffArray = new JSONArray(response);

        // Loop through each object in the array
        for (int i = 0; i < staffArray.length(); i++) {
            JSONObject staffObject = staffArray.getJSONObject(i);

            // Get staff type
            String staffTypeName = staffObject.getJSONObject("staffType").optString("staffTypeName");

            if ("Driver".equalsIgnoreCase(staffTypeName)) {
                // Create a StaffPojo instance
                StaffPojo driver = new StaffPojo();
                driver.setId(staffObject.optInt("id"));
                driver.setName(staffObject.optString("empName"));

                // Add the driver to the driver list
                driverList.add(driver);

            } else if ("Conductor".equalsIgnoreCase(staffTypeName)) {
                // Create a StaffPojo instance
                StaffPojo conductor = new StaffPojo();
                conductor.setId(staffObject.optInt("id"));
                conductor.setName(staffObject.optString("empName"));

                // Add the conductor to the conductor list
                conductorList.add(conductor);
            }
        }
    }


    public static List<StaffPojo> parseAllStaffList(String response) throws JSONException {
        List<StaffPojo> staffList = new ArrayList<>();

        // Parse the JSON response
        JSONObject responseObject = new JSONObject(response);

        // Extract "content" array
        JSONArray contentArray = responseObject.optJSONArray("content");

        if (contentArray != null) {
            // Loop through each JSON object in "content"
            for (int i = 0; i < contentArray.length(); i++) {
                JSONObject staffObject = contentArray.getJSONObject(i);

                // Create a new StaffPojo instance
                StaffPojo staff = new StaffPojo();

                // Map fields from the JSON object to StaffPojo
                staff.setId(staffObject.optInt("id"));
                staff.setName(staffObject.optString("empName"));
                staff.setDob(staffObject.optString("dateOfBirth")); // Timestamp, format conversion might be needed
                staff.setJoiningDate(staffObject.optString("joiningDate")); // Timestamp
                staff.setEmployeeCode(staffObject.optString("empCode"));
                staff.setAddress(staffObject.optString("address"));
                staff.setRelationMemberName(staffObject.optString("relativeName"));
                staff.setLicenceNo(staffObject.optString("license"));

                // Parse and set the contact number
                try {
                    staff.setContactNo(new BigInteger(staffObject.optString("contact")));
                } catch (NumberFormatException e) {
                    staff.setContactNo(BigInteger.ZERO); // Default if parsing fails
                }

                // Parse nested objects
                staff.setStaffType(staffObject.optJSONObject("staffType") != null
                        ? staffObject.optJSONObject("staffType").optString("staffTypeName")
                        : "N/A");

                staff.setEmploymentType(staffObject.optJSONObject("employmentType") != null
                        ? staffObject.optJSONObject("employmentType").optString("employmentTypeName")
                        : "N/A");

                staff.setGender(staffObject.optJSONObject("gender") != null
                        ? staffObject.optJSONObject("gender").optString("gender")
                        : "N/A");

                staff.setCaste(staffObject.optJSONObject("socialCategory") != null
                        ? staffObject.optJSONObject("socialCategory").optString("socialCategory")
                        : "N/A");


                // Add the StaffPojo object to the list
                staffList.add(staff);
            }
        }

        return staffList;
    }

    public static List<StaffPojo> parseAllStaffListSearched(String response) throws JSONException {
        List<StaffPojo> staffList = new ArrayList<>();

        // Parse the JSON response as a JSONArray instead of JSONObject
        JSONArray contentArray = new JSONArray(response);  // Changed from JSONObject to JSONArray

        // Loop through each JSON object in the array
        for (int i = 0; i < contentArray.length(); i++) {
            JSONObject staffObject = contentArray.getJSONObject(i);

            // Create a new StaffPojo instance
            StaffPojo staff = new StaffPojo();

            // Map fields from the JSON object to StaffPojo
            staff.setId(staffObject.optInt("id"));
            staff.setName(staffObject.optString("empName"));
            staff.setDob(staffObject.optString("dateOfBirth")); // Timestamp, format conversion might be needed
            staff.setJoiningDate(staffObject.optString("joiningDate")); // Timestamp
            staff.setEmployeeCode(staffObject.optString("empCode"));
            staff.setAddress(staffObject.optString("address"));
            staff.setRelationMemberName(staffObject.optString("relativeName"));
            staff.setLicenceNo(staffObject.optString("license"));

            // Parse and set the contact number
            try {
                staff.setContactNo(new BigInteger(staffObject.optString("contact")));
            } catch (NumberFormatException e) {
                staff.setContactNo(BigInteger.ZERO); // Default if parsing fails
            }

            // Parse nested objects
            staff.setStaffType(staffObject.optJSONObject("staffType") != null
                    ? staffObject.optJSONObject("staffType").optString("staffTypeName")
                    : "N/A");

            staff.setEmploymentType(staffObject.optJSONObject("employmentType") != null
                    ? staffObject.optJSONObject("employmentType").optString("employmentTypeName")
                    : "N/A");

            staff.setGender(staffObject.optJSONObject("gender") != null
                    ? staffObject.optJSONObject("gender").optString("gender")
                    : "N/A");

            staff.setCaste(staffObject.optJSONObject("socialCategory") != null
                    ? staffObject.optJSONObject("socialCategory").optString("socialCategory")
                    : "N/A");

            // Add the StaffPojo object to the list
            staffList.add(staff);
        }

        return staffList;
    }



    public static List<StaffPojo> parseDrivers(String response) throws JSONException {
        List<StaffPojo> driverList = new ArrayList<>();

        // Parse the response string into a JSONArray
        JSONArray driversArray = new JSONArray(response);

        // Loop through each object in the array
        for (int i = 0; i < driversArray.length(); i++) {
            JSONObject driverObject = driversArray.getJSONObject(i);

            // Check if staffTypeName is "Driver"
            String staffTypeName = driverObject.getJSONObject("staffType").optString("staffTypeName");
            if ("Driver".equalsIgnoreCase(staffTypeName)) {
                // Create a StaffPojo instance
                StaffPojo driver = new StaffPojo();

                // Map fields from JSON to StaffPojo properties
                driver.setId(driverObject.optInt("id"));
                driver.setName(driverObject.optString("empName"));
                driver.setStaffType(staffTypeName);
                driver.setGender(driverObject.getJSONObject("gender").optString("gender"));
                driver.setCaste(driverObject.getJSONObject("socialCategory").optString("socialCategory"));
                driver.setRelationMemberName(driverObject.optString("relativeName"));
                driver.setEmployeeCode(driverObject.optString("empCode"));
                driver.setEmploymentType(driverObject.getJSONObject("employmentType").optString("employmentTypeName"));
                driver.setLicenceNo(driverObject.optString("license"));
                driver.setContactNo(BigInteger.valueOf(driverObject.optLong("contact")));
                driver.setAddress(driverObject.optString("address"));

                // Define the input and output date formats
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                // Parse and format the dateOfBirth (dob)
                String dobStr = driverObject.optString("dateOfBirth");
                if (dobStr != null && !dobStr.isEmpty()) {
                    try {
                        // Parse the input date and reformat it to the desired format
                        Date dob = inputDateFormat.parse(dobStr);
                        String formattedDob = outputDateFormat.format(dob);

                        // Set the formatted dob to the driver object
                        driver.setDob(formattedDob);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


                // Parse and format the joiningDate
                String joiningDateStr = driverObject.optString("joiningDate");
                if (joiningDateStr != null && !joiningDateStr.isEmpty()) {
                    try {
                        // Parse the input date and reformat it to the desired format
                        Date joiningDate = inputDateFormat.parse(joiningDateStr);
                        String formattedJoiningDate = outputDateFormat.format(joiningDate);

                        // Set the formatted joiningDate to the driver object
                        driver.setJoiningDate(formattedJoiningDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                // Add to the driver list
                driverList.add(driver);
            }
        }

        return driverList;
    }

    public static List<StaffPojo> parseStaffForSpinner(String response) throws JSONException {
        List<StaffPojo> staffList = new ArrayList<>();

        // Parse the response string into a JSONArray
        JSONArray staffArray = new JSONArray(response);

        // Loop through each object in the array
        for (int i = 0; i < staffArray.length(); i++) {
            JSONObject staffObject = staffArray.getJSONObject(i);

            // Create a StaffPojo instance
            StaffPojo staff = new StaffPojo();

            // Set staff fields
            staff.setId(staffObject.optInt("id"));
            staff.setName(staffObject.optString("empName"));
            staff.setDob(staffObject.optString("dateOfBirth"));
            staff.setJoiningDate(staffObject.optString("joiningDate"));

            // Extract staffType and employmentType
            JSONObject staffTypeObject = staffObject.optJSONObject("staffType");
            if (staffTypeObject != null) {
                staff.setStaffType(staffTypeObject.optString("staffTypeName"));
            }

            JSONObject employmentTypeObject = staffObject.optJSONObject("employmentType");
            if (employmentTypeObject != null) {
                staff.setEmploymentType(employmentTypeObject.optString("employmentTypeName"));
            }

            // Set employee code
            staff.setEmployeeCode(staffObject.optString("empCode"));

            // Add the staff object to the list
            staffList.add(staff);
        }

        return staffList;
    }

    public static List<StaffPojo> parseDriversForSpinner(String response) throws JSONException {
        List<StaffPojo> driverList = new ArrayList<>();

        // Parse the response string into a JSONArray
        JSONArray driversArray = new JSONArray(response);

        // Loop through each object in the array
        for (int i = 0; i < driversArray.length(); i++) {
            JSONObject driverObject = driversArray.getJSONObject(i);

            // Check if staffTypeName is "Driver"
            String staffTypeName = driverObject.getJSONObject("staffType").optString("staffTypeName");
            if ("Driver".equalsIgnoreCase(staffTypeName)) {
                // Create a StaffPojo instance for spinner
                StaffPojo driver = new StaffPojo();

                // Map only the id and empName fields
                driver.setId(driverObject.optInt("id"));
                driver.setName(driverObject.optString("empName"));

                // Add the driver to the list
                driverList.add(driver);
            }
        }

        return driverList;
    }

    public static List<StaffPojo> parseConductors(String response) throws JSONException {
        List<StaffPojo> conductorList = new ArrayList<>();

        // Parse the response string into a JSONArray
        JSONArray conductorsArray = new JSONArray(response);

        // Loop through each object in the array
        for (int i = 0; i < conductorsArray.length(); i++) {
            JSONObject conductorObject = conductorsArray.getJSONObject(i);

            // Check if staffTypeName is "Conductor"
            String staffTypeName = conductorObject.getJSONObject("staffType").optString("staffTypeName");
            if ("Conductor".equalsIgnoreCase(staffTypeName)) {
                // Create a StaffPojo instance
                StaffPojo conductor = new StaffPojo();

                // Map fields from JSON to StaffPojo properties
                conductor.setId(conductorObject.optInt("id"));
                conductor.setName(conductorObject.optString("empName"));
                conductor.setStaffType(staffTypeName);
                conductor.setGender(conductorObject.getJSONObject("gender").optString("gender"));
                conductor.setCaste(conductorObject.getJSONObject("socialCategory").optString("socialCategory"));
                conductor.setRelationMemberName(conductorObject.optString("relativeName"));
                conductor.setEmployeeCode(conductorObject.optString("empCode"));
                conductor.setEmploymentType(conductorObject.getJSONObject("employmentType").optString("employmentTypeName"));
                conductor.setLicenceNo(conductorObject.optString("license"));
                conductor.setContactNo(BigInteger.valueOf(conductorObject.optLong("contact")));
                conductor.setAddress(conductorObject.optString("address"));
                conductor.setDob(conductorObject.optString("dob"));
                conductor.setJoiningDate(conductorObject.optString("joiningDate"));

                // Define the input and output date formats
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                // Parse and format the dateOfBirth (dob)
                String dobStr = conductorObject.optString("dateOfBirth");
                if (dobStr != null && !dobStr.isEmpty()) {
                    try {
                        // Parse the input date and reformat it to the desired format
                        Date dob = inputDateFormat.parse(dobStr);
                        String formattedDob = outputDateFormat.format(dob);

                        // Set the formatted dob to the driver object
                        conductor.setDob(formattedDob);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                // Parse and format the joiningDate
                String joiningDateStr = conductorObject.optString("joiningDate");
                if (joiningDateStr != null && !joiningDateStr.isEmpty()) {
                    try {
                        // Parse the input date and reformat it to the desired format
                        Date joiningDate = inputDateFormat.parse(joiningDateStr);
                        String formattedJoiningDate = outputDateFormat.format(joiningDate);

                        // Set the formatted joiningDate to the driver object
                        conductor.setJoiningDate(formattedJoiningDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                // Add to the conductor list
                conductorList.add(conductor);
            }
        }

        return conductorList;
    }

    public static List<StaffPojo> parseConductorsForSpinner(String response) throws JSONException {
        List<StaffPojo> conductorList = new ArrayList<>();

        // Parse the response string into a JSONArray
        JSONArray conductorsArray = new JSONArray(response);

        // Loop through each object in the array
        for (int i = 0; i < conductorsArray.length(); i++) {
            JSONObject conductorObject = conductorsArray.getJSONObject(i);

            // Check if staffTypeName is "Conductor"
            String staffTypeName = conductorObject.getJSONObject("staffType").optString("staffTypeName");
            if ("Conductor".equalsIgnoreCase(staffTypeName)) {
                // Create a StaffPojo instance for spinner
                StaffPojo conductor = new StaffPojo();

                // Map only the id and empName fields
                conductor.setId(conductorObject.optInt("id"));
                conductor.setName(conductorObject.optString("empName"));

                // Add the conductor to the list
                conductorList.add(conductor);
            }
        }

        return conductorList;
    }

    public static List<DepotPojo> parseDecryptedDepotsInfo(String response) throws JSONException {
        List<DepotPojo> depotList = new ArrayList<>();

        try {
            // Parse the initial JSON response
            JSONArray jsonArray = new JSONArray(response);

            // Iterate through each object in the JSON array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Map each JSON object to a DepotPojo instance
                DepotPojo depot = new DepotPojo();
                depot.setId(jsonObject.optInt("id"));
                depot.setDepotName(jsonObject.optString("depotName"));
                depot.setDepotCode(jsonObject.optString("depotCode"));

                // Parse the nested location object
                JSONObject locationObject = jsonObject.optJSONObject("location");
                if (locationObject != null) {
                    LocationsPojo location = new LocationsPojo();
                    location.setId(locationObject.optInt("id"));
                    location.setName(locationObject.optString("locationName"));
                    depot.setLocation(location);
                }

                depotList.add(depot);
            }

        } catch (Exception e) {
            Log.e("Parse Error", "Exception while parsing depots: " + e.getMessage());
            throw new JSONException("Error parsing depots: " + e.getMessage());
        }

        return depotList;  // Return the list of depots
    }



    public static List<AddaPojo> parseAddaCardList(String response) throws JSONException {
        List<AddaPojo> addaList = new ArrayList<>();

        // Ensure response is not empty or null
        if (response == null || response.trim().isEmpty()) {
            System.out.println("Empty or invalid JSON response.");
            return addaList;
        }

        // Parse the JSON response
        JSONObject responseObject = new JSONObject(response);

        // Extract "data" object if present
        JSONObject dataObject = responseObject.optJSONObject("data");

        // Extract "content" array (either from root or inside "data")
        JSONArray contentArray = responseObject.optJSONArray("content");
        if (contentArray == null && dataObject != null) {
            contentArray = dataObject.optJSONArray("content");
        }

        if (contentArray == null) {
            System.out.println("Missing 'content' array in response.");
            return addaList;
        }

        // Loop through each JSON object in "content"
        for (int i = 0; i < contentArray.length(); i++) {
            JSONObject addaObject = contentArray.optJSONObject(i);
            if (addaObject == null) continue;

            // Create new AddaPojo instance
            AddaPojo adda = new AddaPojo();

            // Map basic fields
            adda.setId(addaObject.optInt("id", 0));
            adda.setAddaName(addaObject.optString("addaName", null));
            adda.setRemarks(addaObject.optString("remarks", null));

            // Parse depot
            JSONObject depotObject = addaObject.optJSONObject("depot");
            if (depotObject != null) {
                DepotPojo depot = new DepotPojo();
                depot.setId(depotObject.optInt("id", 0));
                depot.setDepotName(depotObject.optString("depotName", null));
                depot.setDepotCode(depotObject.optString("depotCode", null));
                adda.setDepot(depot);
            }

            // Parse location
            JSONObject locationObject = addaObject.optJSONObject("location");
            if (locationObject != null) {
                LocationsPojo location = new LocationsPojo();
                location.setId(locationObject.optInt("id", 0));
                location.setName(locationObject.optString("locationName", null));
                adda.setLocation(location);
            }

            // Parse staff (addaIncharge)
            JSONObject staffObject = addaObject.optJSONObject("staff");
            if (staffObject != null) {
                StaffPojo staff = new StaffPojo();
                staff.setId(staffObject.optInt("id", 0));
                staff.setName(staffObject.optString("empName", null));
                staff.setEmployeeCode(staffObject.optString("empCode", null));
                staff.setLicenceNo(staffObject.optString("license", null));
                adda.setAddaIncharge(staff);
            }

            // Add parsed object to list
            addaList.add(adda);
        }

        return addaList;
    }

    public static List<AddaPojo> parseAddaCardListSearched(String response) throws JSONException {
        List<AddaPojo> addaList = new ArrayList<>();

        // Ensure response is not empty or null
        if (response == null || response.trim().isEmpty()) {
            System.out.println("Empty or invalid JSON response.");
            return addaList;
        }

        JSONArray contentArray = null;

        // Try to parse response as JSONArray (direct case)
        if (response.trim().startsWith("[")) {
            contentArray = new JSONArray(response);
        } else {
            // Parse as JSONObject if response is an object
            JSONObject responseObject = new JSONObject(response);
            Object dataObject = responseObject.opt("data");

            if (dataObject instanceof JSONArray) {
                contentArray = (JSONArray) dataObject;
            } else if (dataObject instanceof JSONObject) {
                contentArray = ((JSONObject) dataObject).optJSONArray("content");
            }
        }

        if (contentArray == null) {
            System.out.println("Missing 'content' array in response.");
            return addaList;
        }

        // Loop through each JSON object in the array
        for (int i = 0; i < contentArray.length(); i++) {
            JSONObject addaObject = contentArray.optJSONObject(i);
            if (addaObject == null) continue;

            // Create new AddaPojo instance
            AddaPojo adda = new AddaPojo();

            // Map basic fields
            adda.setId(addaObject.optInt("id", 0));
            adda.setAddaName(addaObject.optString("addaName", null));
            adda.setRemarks(addaObject.optString("remarks", null));

            // Parse depot
            JSONObject depotObject = addaObject.optJSONObject("depot");
            if (depotObject != null) {
                DepotPojo depot = new DepotPojo();
                depot.setId(depotObject.optInt("id", 0));
                depot.setDepotName(depotObject.optString("depotName", null));
                depot.setDepotCode(depotObject.optString("depotCode", null));
                adda.setDepot(depot);
            }

            // Parse location
            JSONObject locationObject = addaObject.optJSONObject("location");
            if (locationObject != null) {
                LocationsPojo location = new LocationsPojo();
                location.setId(locationObject.optInt("id", 0));
                location.setName(locationObject.optString("locationName", null));
                adda.setLocation(location);
            }

            // Parse staff (addaIncharge)
            JSONObject staffObject = addaObject.optJSONObject("staff");
            if (staffObject != null) {
                StaffPojo staff = new StaffPojo();
                staff.setId(staffObject.optInt("id", 0));
                staff.setName(staffObject.optString("empName", null));
                staff.setEmployeeCode(staffObject.optString("empCode", null));
                staff.setLicenceNo(staffObject.optString("license", null));
                adda.setAddaIncharge(staff);
            }

            // Add parsed object to list
            addaList.add(adda);
        }

        return addaList;
    }



    public static List<LocationsPojo> parseLocation(String response) throws JSONException {
        List<LocationsPojo> itemList = new ArrayList<>();
        JSONArray dataArray = new JSONArray(response);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);

            LocationsPojo item = new LocationsPojo();
            item.setId(jsonObject.optInt("id"));
            item.setName(jsonObject.optString("locationName"));
            itemList.add(item);
        }
        return itemList;
    }

    public static List<RouteTypePojo> parseRouteType(String response) throws JSONException {
        List<RouteTypePojo> itemList = new ArrayList<>();
        // Parse the response into a JSONArray
        JSONArray dataArray = new JSONArray(response);

        // Loop through each object in the array
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);

            // Create a RouteTypePojo instance and set the properties
            RouteTypePojo item = new RouteTypePojo();
            item.setRouteTypeId(jsonObject.optInt("id"));
            item.setRouteTypeName(jsonObject.optString("routeTypeName"));

            // Add to the item list
            itemList.add(item);
        }
        return itemList;
    }

    public static List<VehicleTypePojo> parseVehicleType(String response) throws JSONException {
        // Initialize an empty list to hold the VehicleTypePojo objects
        List<VehicleTypePojo> itemList = new ArrayList<>();
        // Parse the response into a JSON array
        JSONArray dataArray = new JSONArray(response);
        // Loop through the array and extract each JSON object
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);

            // Create a new VehicleTypePojo object
            VehicleTypePojo item = new VehicleTypePojo();
            // Set the id and vehicleTypeName using the JSON values
            item.setVehicleTypeId(jsonObject.optInt("id"));
            item.setVehicleTypeName(jsonObject.optString("vehicleTypeName"));
            // Add the item to the list
            itemList.add(item);
        }

        // Return the populated list
        return itemList;
    }

    public static List<GenderPojo> parseGender(String response) throws JSONException {
        List<GenderPojo> itemList = new ArrayList<>();
        JSONArray dataArray = new JSONArray(response);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);

            GenderPojo item = new GenderPojo();
            item.setGenderId(jsonObject.optInt("id"));
            item.setGenderName(jsonObject.optString("gender"));
            itemList.add(item);
        }
        return itemList;
    }

    public static List<CastePojo> parseSocialCategory(String response) throws JSONException {
        List<CastePojo> itemList = new ArrayList<>();
        JSONArray dataArray = new JSONArray(response);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);

            CastePojo item = new CastePojo();
            item.setCasteId(jsonObject.optInt("id"));
            item.setCasteName(jsonObject.optString("socialCategory"));
            itemList.add(item);
        }
        return itemList;
    }

    public static List<EmploymentTypePojo> parseEmploymentType(String response) throws JSONException {
        List<EmploymentTypePojo> itemList = new ArrayList<>();
        JSONArray dataArray = new JSONArray(response);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);

            EmploymentTypePojo item = new EmploymentTypePojo();
            item.setEmploymentTypeId(BigInteger.valueOf(jsonObject.optInt("id")));
            item.setEmploymentTypeName(jsonObject.optString("employmentTypeName"));
            itemList.add(item);
        }
        return itemList;
    }

    public static List<StaffTypePojo> parseStaffType(String response) throws JSONException {
        List<StaffTypePojo> itemList = new ArrayList<>();
        JSONArray dataArray = new JSONArray(response);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);

            StaffTypePojo item = new StaffTypePojo();
            item.setStaffTypeId(jsonObject.optInt("id"));
            item.setStaffTypeName(jsonObject.optString("staffTypeName", null)); // Handle null values
            itemList.add(item);
        }
        return itemList;
    }

    public static List<RoutePojo> parseRoutes(String response) throws JSONException {
        List<RoutePojo> routeList = new ArrayList<>();

        // Parse the response as a JSONArray
        JSONArray dataArray = new JSONArray(response);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);

            RoutePojo route = new RoutePojo();
            route.setJourneyHours(jsonObject.optInt("journeyHours", 0));
            route.setDistance(jsonObject.optInt("distance", 0));
            route.setRouteName(jsonObject.optString("routeName", null));
            route.setRouteId(jsonObject.optInt("id", 0));
            route.setStartTime(jsonObject.optString("startTime", null));
            route.setEndTime(jsonObject.optString("endTime", null));

            // Parse startLocation
            JSONObject startLocation = jsonObject.optJSONObject("startLocation");
            if (startLocation != null) {
                LocationsPojo startLocationPojo = new LocationsPojo();
                startLocationPojo.setId(startLocation.optInt("id", 0));
                startLocationPojo.setName(startLocation.optString("locationName", null));
                route.setStartLocationPojo(startLocationPojo);
            }

            // Parse endLocation
            JSONObject endLocation = jsonObject.optJSONObject("endLocation");
            if (endLocation != null) {
                LocationsPojo endLocationPojo = new LocationsPojo();
                endLocationPojo.setId(endLocation.optInt("id", 0));
                endLocationPojo.setName(endLocation.optString("locationName", null));
                route.setEndLocationPojo(endLocationPojo);
            }

            // Parse routeType
            JSONObject routeType = jsonObject.optJSONObject("routeType");
            if (routeType != null) {
                RouteTypePojo routeTypePojo = new RouteTypePojo();
                routeTypePojo.setRouteTypeId(routeType.optInt("id", 0));
                routeTypePojo.setRouteTypeName(routeType.optString("routeTypeName", null));
                route.setRouteTypePojo(routeTypePojo);
            }

            // Parse depot (if present)
            JSONObject depot = jsonObject.optJSONObject("depot");
            if (depot != null) {
                DepotPojo depotPojo = new DepotPojo();
                depotPojo.setId(depot.optInt("depotId", 0));
                depotPojo.setDepotName(depot.optString("depotName", null));
                depotPojo.setDepotCode(depot.optString("depotCode", null));
                route.setDepotPojo(depotPojo);
            }

            routeList.add(route);
        }

        return routeList;
    }



    public static List<RoutePojo> parseRouteCards(String response) throws JSONException {
        List<RoutePojo> routeList = new ArrayList<>();

        // Ensure response is not empty or null
        if (response == null || response.trim().isEmpty() || response.trim().equals("[]")) {
            System.out.println("Empty or invalid JSON response.");
            return routeList; // Return empty list
        }

        JSONArray dataArray;

        try {
            // Try parsing as a JSON object to extract "content" array
            JSONObject jsonResponse = new JSONObject(response);
            dataArray = jsonResponse.optJSONArray("content");

            // If "content" is missing or null, try parsing response as a JSON array
            if (dataArray == null) {
                dataArray = new JSONArray(response);
            }
        } catch (JSONException e) {
            // If response is neither a valid object nor an array, return empty list
            System.out.println("Invalid JSON format.");
            return routeList;
        }

        // Process each JSON object in the array
        for (int i = 0; i < dataArray.length(); i++) {
            Object item = dataArray.get(i);

            if (item instanceof JSONObject) { // Ensure it's a valid JSON object
                JSONObject jsonObject = (JSONObject) item;
                RoutePojo route = new RoutePojo();

                route.setJourneyHours(jsonObject.optInt("journeyHours", 0));
                route.setDistance(jsonObject.optInt("distance", 0));
                route.setRouteName(jsonObject.optString("routeName", null));
                route.setRouteId(jsonObject.optInt("id", 0));
                route.setStartTime(jsonObject.optString("startTime", null));
                route.setEndTime(jsonObject.optString("endTime", null));

                // Parse startLocation
                JSONObject startLocation = jsonObject.optJSONObject("startLocation");
                if (startLocation != null) {
                    LocationsPojo startLocationPojo = new LocationsPojo();
                    startLocationPojo.setId(startLocation.optInt("id", 0));
                    startLocationPojo.setName(startLocation.optString("locationName", null));
                    route.setStartLocationPojo(startLocationPojo);
                }

                // Parse endLocation
                JSONObject endLocation = jsonObject.optJSONObject("endLocation");
                if (endLocation != null) {
                    LocationsPojo endLocationPojo = new LocationsPojo();
                    endLocationPojo.setId(endLocation.optInt("id", 0));
                    endLocationPojo.setName(endLocation.optString("locationName", null));
                    route.setEndLocationPojo(endLocationPojo);
                }

                // Parse routeType
                JSONObject routeType = jsonObject.optJSONObject("routeType");
                if (routeType != null) {
                    RouteTypePojo routeTypePojo = new RouteTypePojo();
                    routeTypePojo.setRouteTypeId(routeType.optInt("id", 0));
                    routeTypePojo.setRouteTypeName(routeType.optString("routeTypeName", null));
                    route.setRouteTypePojo(routeTypePojo);
                }

                // Parse depot (if present)
                JSONObject depot = jsonObject.optJSONObject("depot");
                if (depot != null) {
                    DepotPojo depotPojo = new DepotPojo();
                    depotPojo.setId(depot.optInt("depotId", 0));
                    depotPojo.setDepotName(depot.optString("depotName", null));
                    depotPojo.setDepotCode(depot.optString("depotCode", null));
                    route.setDepotPojo(depotPojo);
                }

                routeList.add(route);
            } else {
                System.out.println("Skipping invalid JSON element at index " + i);
            }
        }

        return routeList;
    }

    public static List<RoutePojo> parseRouteCardsSearched(String response) throws JSONException {
        List<RoutePojo> routeList = new ArrayList<>();

        // Ensure response is not empty or null
        if (response == null || response.trim().isEmpty() || response.trim().equals("[]")) {
            System.out.println("Empty or invalid JSON response.");
            return routeList; // Return empty list
        }

        JSONArray dataArray;

        try {
            // Try parsing response as a JSON array directly
            dataArray = new JSONArray(response);
        } catch (JSONException e) {
            System.out.println("Invalid JSON format: Expected an array.");
            return routeList;
        }

        // Process each JSON object in the array
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = dataArray.optJSONObject(i);
            if (jsonObject == null) {
                System.out.println("Skipping invalid JSON element at index " + i);
                continue;
            }

            RoutePojo route = new RoutePojo();

            route.setJourneyHours(jsonObject.optInt("journeyHours", 0));
            route.setDistance(jsonObject.optInt("distance", 0));
            route.setRouteName(jsonObject.optString("routeName", null));
            route.setRouteId(jsonObject.optInt("id", 0));
            route.setStartTime(jsonObject.optString("startTime", null));
            route.setEndTime(jsonObject.optString("endTime", null)); // Handles null safely

            // Parse startLocation
            JSONObject startLocation = jsonObject.optJSONObject("startLocation");
            if (startLocation != null) {
                LocationsPojo startLocationPojo = new LocationsPojo();
                startLocationPojo.setId(startLocation.optInt("id", 0));
                startLocationPojo.setName(startLocation.optString("locationName", null));
                route.setStartLocationPojo(startLocationPojo);
            }

            // Parse endLocation
            JSONObject endLocation = jsonObject.optJSONObject("endLocation");
            if (endLocation != null) {
                LocationsPojo endLocationPojo = new LocationsPojo();
                endLocationPojo.setId(endLocation.optInt("id", 0));
                endLocationPojo.setName(endLocation.optString("locationName", null));
                route.setEndLocationPojo(endLocationPojo);
            }

            // Parse routeType
            JSONObject routeType = jsonObject.optJSONObject("routeType");
            if (routeType != null) {
                RouteTypePojo routeTypePojo = new RouteTypePojo();
                routeTypePojo.setRouteTypeId(routeType.optInt("id", 0));
                routeTypePojo.setRouteTypeName(routeType.optString("routeTypeName", null));
                route.setRouteTypePojo(routeTypePojo);
            }

            // Parse depot (if present)
            JSONObject depot = jsonObject.optJSONObject("depot");
            if (depot != null) {
                DepotPojo depotPojo = new DepotPojo();
                depotPojo.setId(depot.optInt("depotId", 0));
                depotPojo.setDepotName(depot.optString("depotName", null));
                depotPojo.setDepotCode(depot.optString("depotCode", null));
                route.setDepotPojo(depotPojo);
            }

            routeList.add(route);
        }

        return routeList;
    }


    // Parse and Select Offices For Admin
    public static List<OfficePojo> parseOfficesForAdmin(String data) {
        List<OfficePojo> officeList = new ArrayList<>();

        try {
            JSONObject dataObject = new JSONObject(data);
            JSONArray contentArray = dataObject.getJSONArray("content");

            for (int i = 0; i < contentArray.length(); i++) {
                JSONObject obj = contentArray.getJSONObject(i);

                OfficePojo office = new OfficePojo();

                office.setOfficeId(obj.optInt("id"));
                office.setOfficeName(obj.optString("office_name"));
                office.setSanctionedPosts(obj.optInt("sanctioned_posts"));
                office.setOtherPosts(obj.optInt("other_posts"));

                // Handle parent office (if null, default to 0 or -1)
                String parentOffice = obj.optString("parent_office", null);
                office.setOfficeParentId(parentOffice != null && !parentOffice.equals("null") ? 1 : 0); // Optional logic

                // Create and set DepartmentPojo
                DepartmentPojo dept = new DepartmentPojo();
                dept.setDepartmentName(obj.optString("department_name"));
                office.setDepartmentPojo(dept);

                // OfficeLevel from office_level
                OfficeLevel type = new OfficeLevel();
                type.setOfficeLevelName(obj.optString("office_level"));
                type.setOfficeLevelId(obj.optInt("office_level_id"));
                office.setOfficeLevelPojo(type);

                officeList.add(office);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return officeList;
    }


    public static List<OfficePojo> parseAllOfficeCards(String data) {
        List<OfficePojo> officeList = new ArrayList<>();

        try {
            JSONArray arr = new JSONArray(data);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                OfficePojo office = new OfficePojo();

                // Basic Fields
                office.setOfficeId(obj.optInt("id", -1));
                office.setOfficeName(obj.optString("officeName", "N/A"));
                office.setAddress(obj.optString("address", "N/A"));
                office.setOfficeCategory(obj.optString("officeCategory", "N/A"));
                office.setPinCode(obj.optInt("pinCode", -1));
                office.setSanctionedPosts(obj.optInt("sanctionedPosts", 0));
                office.setOtherPosts(obj.optInt("otherPosts", 0));

                // LGD Codes
                office.setLgdDistrictCode(obj.optInt("lgdDistrictCode", -1));

                office.setLgdBlockCode(obj.optInt("lgdBlockCode", -1));
                office.setLgdPanchayatCode(obj.optInt("lgdPanchayatCode", -1));
                office.setLgdVillageCode(obj.optInt("lgdVillageCode", -1));

                office.setLgdMunicipalCode(obj.optInt("lgdMunicipalCode", -1));
                office.setLgdWardCode(obj.optInt("lgdWardCode", -1));

                office.setRevenueTehsilId(obj.optInt("revenueTehsilId", -1));
                office.setRevenuePatwarId(obj.optInt("revenuePatwarId", -1));

                // Department
                JSONObject deptObj = obj.optJSONObject("department");
                if (deptObj != null) {
                    DepartmentPojo dept = new DepartmentPojo();
                    dept.setDepartmentId(deptObj.optInt("id", -1));
                    dept.setDepartmentName(deptObj.optString("departmentName", "N/A"));
                    office.setDepartmentPojo(dept);
                }

                // Designation
                JSONObject desigObj = obj.optJSONObject("designation");
                if (desigObj != null) {
                    DesignationPojo desig = new DesignationPojo();
                    desig.setDesignationId(desigObj.optInt("id", -1));
                    desig.setDesignationName(desigObj.optString("designationName", "N/A"));
                    office.setDesignationPojo(desig);
                }

                // OfficeType
                JSONObject typeObj = obj.optJSONObject("officeType");
                if (typeObj != null) {
                    OfficeLevel type = new OfficeLevel();
                    type.setOfficeLevelId(typeObj.optInt("id", -1));
                    type.setOfficeLevelName(typeObj.optString("typeName", "N/A"));
                    office.setOfficeLevelPojo(type);
                }

                // Parent Office (Recursive Parse)
                JSONObject parentObj = obj.optJSONObject("office");
                if (parentObj != null) {
                    OfficePojo parent = new OfficePojo();

                    // Parse Location
                    parent.setOfficeLocation(parentObj.optString("location", "N/A"));

                    parent.setOfficeId(parentObj.optInt("id", -1));
                    parent.setOfficeName(parentObj.optString("officeName", "N/A"));
                    parent.setAddress(parentObj.optString("address", "N/A"));
                    parent.setOfficeCategory(parentObj.optString("officeCategory", "N/A"));
                    parent.setPinCode(parentObj.optInt("pinCode", -1));

                    parent.setLgdDistrictCode(parentObj.optInt("lgdDistrictCode", -1));
                    parent.setLgdBlockCode(parentObj.optInt("lgdBlockCode", -1));
                    parent.setLgdPanchayatCode(parentObj.optInt("lgdPanchayatCode", -1));
                    parent.setLgdVillageCode(parentObj.optInt("lgdVillageCode", -1));
                    parent.setLgdMunicipalCode(parentObj.optInt("lgdMunicipalCode", -1));
                    parent.setLgdWardCode(parentObj.optInt("lgdWardCode", -1));

                    office.setParentOffice(parent);
                    office.setOfficeParentId(parent.getOfficeId());
                }

                officeList.add(office);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return officeList;
    }


    // OLD



    public static List<StopPojo> parseStops(String response) throws JSONException {
        List<StopPojo> stopsList = new ArrayList<>();

        // Parse the response as a JSONArray
        JSONArray dataArray = new JSONArray(response);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);
            StopPojo stopPojo = new StopPojo();

            // Parse the stoppage object
            JSONObject stoppageObject = jsonObject.optJSONObject("stoppage");
            if (stoppageObject != null) {
                stopPojo.setStopName(stoppageObject.optString("locationName", "UNKNOWN").trim());
            }

            // Parse the stoppageSequence
            stopPojo.setStopSequence(jsonObject.optInt("stoppageSequence", 0));
            stopPojo.setStopId(jsonObject.optInt("id", 0)); // This is stoppage ID


            // Add the StopPojo to the list
            stopsList.add(stopPojo);
        }

        return stopsList;
    }


    // FOR ADD OFFICE #######################
    public static List<DistrictPojo> parseDistricts(String response) throws JSONException {
        List<DistrictPojo> itemList = new ArrayList<>();
        JSONArray dataArray = new JSONArray(response);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);

            DistrictPojo item = new DistrictPojo();
            item.setDistrictLgdCode(jsonObject.optInt("lgdCode"));
            item.setDistrictId(jsonObject.optInt("id"));
            item.setDistrictName(jsonObject.optString("districtNameE"));
            itemList.add(item);
        }
        return itemList;
    }

    public static List<DepartmentPojo> parseDepartments(String response) throws JSONException {
        List<DepartmentPojo> itemList = new ArrayList<>();
        JSONArray dataArray = new JSONArray(response);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);

            DepartmentPojo item = new DepartmentPojo();
            item.setDepartmentId(jsonObject.optInt("id"));
            item.setDepartmentName(jsonObject.optString("departmentName"));
            item.setDepartmentCode(jsonObject.optString("departmentCode"));
            itemList.add(item);
        }
        return itemList;
    }

    public static List<DesignationPojo> parseDesignationsList(String response) throws JSONException {
        List<DesignationPojo> itemList = new ArrayList<>();
        JSONArray dataArray = new JSONArray(response);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);
            JSONObject desigObj = obj.optJSONObject("designation");

            if (desigObj != null) {
                DesignationPojo item = new DesignationPojo();
                item.setDesignationId(desigObj.optInt("id"));
                item.setDesignationCode(desigObj.optString("designationCode", ""));
                item.setDesignationName(desigObj.optString("designationName", ""));
                itemList.add(item);
            }
        }

        return itemList;
    }

    public static List<OfficePojo> parseParentOffices(String response) throws JSONException {
        List<OfficePojo> officeList = new ArrayList<>();
        JSONArray dataArray = new JSONArray(response);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);
            OfficePojo item = new OfficePojo();

            item.setOfficeId(obj.optInt("id"));
            item.setOfficeName(obj.optString("officeName", ""));
            item.setAddress(obj.optString("address", ""));
            item.setOfficeCategory(obj.optString("officeCategory", ""));

            JSONObject officeObj = obj.optJSONObject("office");
            item.setOfficeParentId(officeObj != null ? officeObj.optInt("id") : 0);

            item.setLgdDistrictCode(obj.optInt("lgdDistrictCode", 0));
            item.setLgdBlockCode(obj.has("lgdBlockCode") && !obj.isNull("lgdBlockCode") ? obj.getInt("lgdBlockCode") : 0);
            item.setLgdPanchayatCode(obj.has("lgdPanchayatCode") && !obj.isNull("lgdPanchayatCode") ? obj.getInt("lgdPanchayatCode") : 0);
            item.setLgdVillageCode(obj.has("lgdVillageCode") && !obj.isNull("lgdVillageCode") ? obj.getInt("lgdVillageCode") : 0);
            item.setLgdMunicipalCode(obj.optInt("lgdMunicipalCode", 0));
            item.setLgdWardCode(obj.optInt("lgdWardCode", 0));
            item.setRevenueTehsilId(obj.has("revenueTehsilId") && !obj.isNull("revenueTehsilId") ? obj.getInt("revenueTehsilId") : 0);
            item.setRevenuePatwarId(obj.has("revenuePatwarId") && !obj.isNull("revenuePatwarId") ? obj.getInt("revenuePatwarId") : 0);
            item.setPinCode(obj.optInt("pinCode", 0));
            item.setSanctionedPosts(obj.optInt("sanctionedPosts", 0));
            item.setOtherPosts(obj.optInt("otherPosts", 0));

            // Designation
            JSONObject designationObj = obj.optJSONObject("designation");
            if (designationObj != null) {
                DesignationPojo desig = new DesignationPojo();
                desig.setDesignationId(designationObj.optInt("id"));
                desig.setDesignationCode(designationObj.optString("designationCode", ""));
                desig.setDesignationName(designationObj.optString("designationName", ""));
                item.setDesignationPojo(desig);
            }

            // Department
            JSONObject deptObj = obj.optJSONObject("department");
            if (deptObj != null) {
                DepartmentPojo dept = new DepartmentPojo();
                dept.setDepartmentId(deptObj.optInt("id"));
                dept.setDepartmentCode(deptObj.optString("departmentCode", ""));
                dept.setDepartmentName(deptObj.optString("departmentName", ""));
                item.setDepartmentPojo(dept);
            }

            // Office Type
            JSONObject typeObj = obj.optJSONObject("officeType");
            if (typeObj != null) {
                OfficeLevel type = new OfficeLevel();
                type.setOfficeLevelId(typeObj.optInt("id"));
                type.setOfficeLevelName(typeObj.optString("typeName", ""));
                item.setOfficeLevelPojo(type);
            }

            officeList.add(item);
        }

        return officeList;
    }

    public static List<OfficeLevel> parseOfficeLevels(String response) throws JSONException {
        List<OfficeLevel> itemList = new ArrayList<>();
        JSONArray dataArray = new JSONArray(response);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);

            OfficeLevel item = new OfficeLevel();
            item.setOfficeLevelId(jsonObject.optInt("id"));
            item.setOfficeLevelName(jsonObject.optString("typeName"));

            JSONObject dept = jsonObject.optJSONObject("department");
            if (dept != null) {
                item.setOfficeLevelDepartmentName(dept.optString("departmentName"));
            }

            itemList.add(item);
        }

        return itemList;
    }


    public static List<MunicipalPojo> parseMunicipals(String response) throws JSONException {
        List<MunicipalPojo> itemList = new ArrayList<>();
        JSONArray dataArray = new JSONArray(response);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);

            MunicipalPojo item = new MunicipalPojo();
            item.setMunicipalId(obj.optInt("id"));
            item.setMunicipalName(obj.optString("municipalName", ""));
            item.setMunicipalLgdCode(String.valueOf(obj.optInt("lgdCode"))); // convert to String if needed

            itemList.add(item);
        }

        return itemList;
    }

    public static List<WardPojo> parseWards(String response) throws JSONException {
        List<WardPojo> itemList = new ArrayList<>();
        JSONArray dataArray = new JSONArray(response);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);

            WardPojo item = new WardPojo();
            item.setWardId(obj.optInt("id"));
            item.setWardName(obj.optString("wardName", ""));
            item.setWardLgdCode(obj.optString("lgdCode", ""));

            itemList.add(item);
        }

        return itemList;
    }


    // RURAL
    public static List<BlockPojo> parseBlocks(String response) throws JSONException {
        List<BlockPojo> itemList = new ArrayList<>();
        JSONArray dataArray = new JSONArray(response);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);

            BlockPojo item = new BlockPojo();
            item.setBlockId(obj.optInt("id"));
            item.setBlockName(obj.optString("blockNameE", ""));
            item.setBlockLgdCode(String.valueOf(obj.optInt("lgdCode"))); // convert to String if needed
            itemList.add(item);
        }

        return itemList;
    }

    public static List<PanchayatPojo> parsePanchayats(String response) throws JSONException {
        List<PanchayatPojo> itemList = new ArrayList<>();
        JSONArray dataArray = new JSONArray(response);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);

            PanchayatPojo item = new PanchayatPojo();
            item.setPanchayatId(jsonObject.optInt("id"));
            item.setPanchayatName(jsonObject.optString("panchayatNameE"));
            item.setPanchayatLgdCode(String.valueOf(jsonObject.optInt("lgdCode")));

            itemList.add(item);
        }
        return itemList;
    }

    public static List<VillagePojo> parseVillages(String response) throws JSONException {
        List<VillagePojo> itemList = new ArrayList<>();
        JSONArray dataArray = new JSONArray(response);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);

            VillagePojo item = new VillagePojo();
            item.setVillageId(jsonObject.optInt("id"));
            item.setVillage(jsonObject.optString("villageNameE"));
            item.setVillageLgdCode(String.valueOf(jsonObject.optInt("lgdCode")));

            itemList.add(item);
        }
        return itemList;
    }


    // For Super Admin
    public static List<OfficeSelectionPojo> parseOfficeListForAdmin(String data) {
        List<OfficeSelectionPojo> officeList = new ArrayList<>();

        try {
            JSONArray dataArray = new JSONArray(data);

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject officeObject = dataArray.getJSONObject(i);

                OfficeSelectionPojo office = new OfficeSelectionPojo();
                office.setOfficeId(officeObject.optInt("id"));
                office.setOfficeName(officeObject.optString("office_name"));

                officeList.add(office);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return officeList;
    }








    // Basic Working Parse
    public static List<DailyRegisterCardFinal> parseDailyDutyRecords(String response) throws JSONException {
        List<DailyRegisterCardFinal> allRecordsList = new ArrayList<>();

        try {

            JSONObject responseObject = new JSONObject(response); // First, parse as JSONObject
            JSONArray jsonArray = responseObject.getJSONArray("content"); // Extract "content" array

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                DailyRegisterCardFinal dailyRegisterCard = new DailyRegisterCardFinal();

                // Parse Route
                if (jsonObject.has("route") && !jsonObject.isNull("route")) {
                    JSONObject routeObject = jsonObject.getJSONObject("route");
                    RoutePojo route = new RoutePojo();
                    route.setJourneyHours(routeObject.optInt("journeyHours", 0));
                    route.setDistance(routeObject.optInt("distance", 0));
                    route.setStartTime(routeObject.optString("startTime", ""));
                    route.setRouteId(routeObject.optInt("id", -1));
                    route.setEndTime(routeObject.optString("endTime", ""));
                    route.setRouteName(routeObject.optString("routeName", ""));
                    dailyRegisterCard.setOriginalRoute(route);
                }

                // Parse Extended or Other Route
                if (jsonObject.has("extendedRoute") && !jsonObject.isNull("extendedRoute")) {
                    JSONObject extendedRouteObject = jsonObject.getJSONObject("extendedRoute");
                    RoutePojo extendedRoute = new RoutePojo();
                    extendedRoute.setJourneyHours(extendedRouteObject.optInt("journeyHours", 0));
                    extendedRoute.setDistance(extendedRouteObject.optInt("distance", 0));
                    extendedRoute.setStartTime(extendedRouteObject.optString("startTime", ""));
                    extendedRoute.setRouteId(extendedRouteObject.optInt("id", -1));
                    extendedRoute.setEndTime(extendedRouteObject.optString("endTime", ""));
                    extendedRoute.setRouteName(extendedRouteObject.optString("routeName", ""));
                    dailyRegisterCard.setOtherRoute(extendedRoute); // Set in DailyRegisterCard
                } else {
                    dailyRegisterCard.setOtherRoute(null); // Set null if no extended route
                }


                // Parse Driver
                if (jsonObject.has("driver") && !jsonObject.isNull("driver")) {
                    JSONObject driverObject = jsonObject.getJSONObject("driver");
                    StaffPojo driver = new StaffPojo();
                    driver.setRelationMemberName(driverObject.optString("relativeName", ""));
                    driver.setLicenceNo(driverObject.optString("license", ""));
                    driver.setAddress(driverObject.optString("address", ""));
                    driver.setEmployeeCode(driverObject.optString("empCode", ""));
                    driver.setName(driverObject.optString("empName", ""));

                    long contact = driverObject.optLong("contact", 0);
                    if (contact > 0) {
                        driver.setContactNo(BigInteger.valueOf(contact));
                    }

                    driver.setJoiningDate(driverObject.optString("joiningDate", ""));
                    driver.setDob(driverObject.optString("dateOfBirth", ""));
                    driver.setId(driverObject.optInt("id", -1));
                    dailyRegisterCard.setMainDriver(driver);
                }

                // Parse Relieving Driver
                if (jsonObject.has("relievingDriver") && !jsonObject.isNull("relievingDriver")) {
                    JSONObject relievingDriverObject = jsonObject.getJSONObject("relievingDriver");
                    StaffPojo relievingDriver = new StaffPojo();
                    relievingDriver.setRelationMemberName(relievingDriverObject.optString("relativeName", ""));
                    relievingDriver.setLicenceNo(relievingDriverObject.optString("license", ""));
                    relievingDriver.setAddress(relievingDriverObject.optString("address", ""));
                    relievingDriver.setEmployeeCode(relievingDriverObject.optString("empCode", ""));
                    relievingDriver.setName(relievingDriverObject.optString("empName", ""));
                    long contact = relievingDriverObject.optLong("contact", 0);
                    if (contact > 0) {
                        relievingDriver.setContactNo(BigInteger.valueOf(contact));
                    }
                    relievingDriver.setJoiningDate(relievingDriverObject.optString("joiningDate", ""));
                    relievingDriver.setDob(relievingDriverObject.optString("dateOfBirth", ""));
                    relievingDriver.setId(relievingDriverObject.optInt("id", -1));
                    dailyRegisterCard.setRelievingDriver(relievingDriver);
                }

                // Parse Relieving Driver Stoppage
                if (jsonObject.has("relievingDriverStoppage") && !jsonObject.isNull("relievingDriverStoppage")) {
                    JSONObject relievingDriverStoppageObject = jsonObject.getJSONObject("relievingDriverStoppage");
                    StopPojo relievingDriverStop = new StopPojo();
                    relievingDriverStop.setStopId(relievingDriverStoppageObject.optInt("id", -1));
                    relievingDriverStop.setStopName(relievingDriverStoppageObject.optString("stopName", ""));
                    relievingDriverStop.setStopSequence(relievingDriverStoppageObject.optInt("stoppageSequence", -1));
                    dailyRegisterCard.setRelievingDriverStop(relievingDriverStop);
                }


                // Parse Conductor
                if (jsonObject.has("conductor") && !jsonObject.isNull("conductor")) {
                    JSONObject conductorObject = jsonObject.getJSONObject("conductor");
                    StaffPojo conductor = new StaffPojo();
                    conductor.setRelationMemberName(conductorObject.optString("relativeName", ""));
                    conductor.setLicenceNo(conductorObject.optString("license", ""));
                    conductor.setAddress(conductorObject.optString("address", ""));
                    conductor.setEmployeeCode(conductorObject.optString("empCode", ""));
                    conductor.setName(conductorObject.optString("empName", ""));
                    long contact = conductorObject.optLong("contact", 0);
                    if (contact > 0) {
                        conductor.setContactNo(BigInteger.valueOf(contact));
                    }
                    conductor.setJoiningDate(conductorObject.optString("joiningDate", ""));
                    conductor.setDob(conductorObject.optString("dateOfBirth", ""));
                    conductor.setId(conductorObject.optInt("id", -1));
                    dailyRegisterCard.setMainConductor(conductor);
                }

                // Parse Relieving Conductor
                if (jsonObject.has("relievingConductor") && !jsonObject.isNull("relievingConductor")) {
                    JSONObject relievingConductorObject = jsonObject.getJSONObject("relievingConductor");
                    StaffPojo relievingConductor = new StaffPojo();
                    relievingConductor.setRelationMemberName(relievingConductorObject.optString("relativeName", ""));
                    relievingConductor.setLicenceNo(relievingConductorObject.optString("license", ""));
                    relievingConductor.setAddress(relievingConductorObject.optString("address", ""));
                    relievingConductor.setEmployeeCode(relievingConductorObject.optString("empCode", ""));
                    relievingConductor.setName(relievingConductorObject.optString("empName", ""));
                    long contact = relievingConductorObject.optLong("contact", 0);
                    if (contact > 0) {
                        relievingConductor.setContactNo(BigInteger.valueOf(contact));
                    }
                    relievingConductor.setJoiningDate(relievingConductorObject.optString("joiningDate", ""));
                    relievingConductor.setDob(relievingConductorObject.optString("dateOfBirth", ""));
                    relievingConductor.setId(relievingConductorObject.optInt("id", -1));
                    dailyRegisterCard.setRelievingConductor(relievingConductor);
                }

                // Parse Relieving Conductor Stoppage
                if (jsonObject.has("relievingConductorStoppage") && !jsonObject.isNull("relievingConductorStoppage")) {
                    JSONObject relievingConductorStoppageObject = jsonObject.getJSONObject("relievingConductorStoppage");
                    StopPojo relievingConductorStop = new StopPojo();
                    relievingConductorStop.setStopId(relievingConductorStoppageObject.optInt("id", -1));
                    relievingConductorStop.setStopName(relievingConductorStoppageObject.optString("stopName", ""));  //
                    relievingConductorStop.setStopSequence(relievingConductorStoppageObject.optInt("stoppageSequence", -1));
                    dailyRegisterCard.setRelievingConductorStop(relievingConductorStop);
                }


                // Parse Vehicle
                if (jsonObject.has("vehicle") && !jsonObject.isNull("vehicle")) {
                    JSONObject vehicleObject = jsonObject.getJSONObject("vehicle");
                    VehiclePojo vehicle = new VehiclePojo();
                    vehicle.setIotFirm(vehicleObject.optString("iotFirm", ""));
                    vehicle.setVehicleNumber(vehicleObject.optString("vehicleNumber", ""));
                    vehicle.setVehicleModel(vehicleObject.optString("vehicleModel", ""));
                    vehicle.setId(vehicleObject.optInt("id", -1));
                    vehicle.setCapacity(vehicleObject.optInt("capacity", 0));
                    dailyRegisterCard.setAssignedVehicle(vehicle);
                }

                // Parse Curtailed Stoppage
                if (jsonObject.has("curtailedStoppage") && !jsonObject.isNull("curtailedStoppage")) {
                    JSONObject curtailedStoppageObject = jsonObject.getJSONObject("curtailedStoppage");
                    Log.d("Curtailed Stoppage", curtailedStoppageObject.toString());
                    StopPojo curtailedStoppage = new StopPojo();
                    curtailedStoppage.setStopId(curtailedStoppageObject.optInt("id", -1));
                    curtailedStoppage.setStopSequence(curtailedStoppageObject.optInt("stoppageSequence", -1));
                    curtailedStoppage.setStopName(curtailedStoppageObject.optString("stopName", "Unknown"));
                    dailyRegisterCard.setFinalStop(curtailedStoppage);
                }


                // Parse other fields
                dailyRegisterCard.setRecordId(jsonObject.optInt("id", -1));
                dailyRegisterCard.setRecordDate(jsonObject.optString("dutyDate", ""));
                dailyRegisterCard.setActualDepartureTime(jsonObject.optString("actualDepartureTime", ""));
                dailyRegisterCard.setActionTaken(jsonObject.optString("actionTaken", ""));
                dailyRegisterCard.setRemark(jsonObject.optString("remarks", ""));

                // Add to the list
                allRecordsList.add(dailyRegisterCard);
            }

        } catch (JSONException e) {
            Log.e("Parse Error", "Invalid JSON format: " + e.getMessage());
            throw new JSONException("Error parsing JSON: " + e.getMessage());
        } catch (Exception e) {
            Log.e("Parse Error", "Unexpected error: " + e.getMessage());
            throw new JSONException("Unexpected error while parsing: " + e.getMessage());
        }

        return allRecordsList;
    }

    // Filter by Date
    public static List<DailyRegisterCardFinal> parseDailyDutyRecordsFiltered(String response) throws JSONException {
        List<DailyRegisterCardFinal> allRecordsList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(response); // Directly parse as JSONArray

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                DailyRegisterCardFinal dailyRegisterCard = new DailyRegisterCardFinal();

                // Parse Route
                if (jsonObject.has("route") && !jsonObject.isNull("route")) {
                    JSONObject routeObject = jsonObject.getJSONObject("route");
                    RoutePojo route = new RoutePojo();
                    route.setJourneyHours(routeObject.optInt("journeyHours", 0));
                    route.setDistance(routeObject.optInt("distance", 0));
                    route.setStartTime(routeObject.optString("startTime", ""));
                    route.setRouteId(routeObject.optInt("id", -1));
                    route.setEndTime(routeObject.optString("endTime", ""));
                    route.setRouteName(routeObject.optString("routeName", ""));
                    dailyRegisterCard.setOriginalRoute(route);
                }

                // Parse Extended or Other Route
                if (jsonObject.has("extendedRoute") && !jsonObject.isNull("extendedRoute")) {
                    JSONObject extendedRouteObject = jsonObject.getJSONObject("extendedRoute");
                    RoutePojo extendedRoute = new RoutePojo();
                    extendedRoute.setJourneyHours(extendedRouteObject.optInt("journeyHours", 0));
                    extendedRoute.setDistance(extendedRouteObject.optInt("distance", 0));
                    extendedRoute.setStartTime(extendedRouteObject.optString("startTime", ""));
                    extendedRoute.setRouteId(extendedRouteObject.optInt("id", -1));
                    extendedRoute.setEndTime(extendedRouteObject.optString("endTime", ""));
                    extendedRoute.setRouteName(extendedRouteObject.optString("routeName", ""));
                    dailyRegisterCard.setOtherRoute(extendedRoute); // Set in DailyRegisterCard
                } else {
                    dailyRegisterCard.setOtherRoute(null); // Set null if no extended route
                }


                // Parse Driver
                if (jsonObject.has("driver") && !jsonObject.isNull("driver")) {
                    JSONObject driverObject = jsonObject.getJSONObject("driver");
                    StaffPojo driver = new StaffPojo();
                    driver.setRelationMemberName(driverObject.optString("relativeName", ""));
                    driver.setLicenceNo(driverObject.optString("license", ""));
                    driver.setAddress(driverObject.optString("address", ""));
                    driver.setEmployeeCode(driverObject.optString("empCode", ""));
                    driver.setName(driverObject.optString("empName", ""));

                    long contact = driverObject.optLong("contact", 0);
                    if (contact > 0) {
                        driver.setContactNo(BigInteger.valueOf(contact));
                    }

                    driver.setJoiningDate(driverObject.optString("joiningDate", ""));
                    driver.setDob(driverObject.optString("dateOfBirth", ""));
                    driver.setId(driverObject.optInt("id", -1));
                    dailyRegisterCard.setMainDriver(driver);
                }

                // Parse Relieving Driver
                if (jsonObject.has("relievingDriver") && !jsonObject.isNull("relievingDriver")) {
                    JSONObject relievingDriverObject = jsonObject.getJSONObject("relievingDriver");
                    StaffPojo relievingDriver = new StaffPojo();
                    relievingDriver.setRelationMemberName(relievingDriverObject.optString("relativeName", ""));
                    relievingDriver.setLicenceNo(relievingDriverObject.optString("license", ""));
                    relievingDriver.setAddress(relievingDriverObject.optString("address", ""));
                    relievingDriver.setEmployeeCode(relievingDriverObject.optString("empCode", ""));
                    relievingDriver.setName(relievingDriverObject.optString("empName", ""));
                    long contact = relievingDriverObject.optLong("contact", 0);
                    if (contact > 0) {
                        relievingDriver.setContactNo(BigInteger.valueOf(contact));
                    }
                    relievingDriver.setJoiningDate(relievingDriverObject.optString("joiningDate", ""));
                    relievingDriver.setDob(relievingDriverObject.optString("dateOfBirth", ""));
                    relievingDriver.setId(relievingDriverObject.optInt("id", -1));
                    dailyRegisterCard.setRelievingDriver(relievingDriver);
                }

                // Parse Relieving Driver Stoppage
                if (jsonObject.has("relievingDriverStoppage") && !jsonObject.isNull("relievingDriverStoppage")) {
                    JSONObject relievingDriverStoppageObject = jsonObject.getJSONObject("relievingDriverStoppage");
                    StopPojo relievingDriverStop = new StopPojo();
                    relievingDriverStop.setStopId(relievingDriverStoppageObject.optInt("id", -1));
                    relievingDriverStop.setStopName(relievingDriverStoppageObject.optString("stopName", ""));
                    relievingDriverStop.setStopSequence(relievingDriverStoppageObject.optInt("stoppageSequence", -1));
                    dailyRegisterCard.setRelievingDriverStop(relievingDriverStop);
                }


                // Parse Conductor
                if (jsonObject.has("conductor") && !jsonObject.isNull("conductor")) {
                    JSONObject conductorObject = jsonObject.getJSONObject("conductor");
                    StaffPojo conductor = new StaffPojo();
                    conductor.setRelationMemberName(conductorObject.optString("relativeName", ""));
                    conductor.setLicenceNo(conductorObject.optString("license", ""));
                    conductor.setAddress(conductorObject.optString("address", ""));
                    conductor.setEmployeeCode(conductorObject.optString("empCode", ""));
                    conductor.setName(conductorObject.optString("empName", ""));
                    long contact = conductorObject.optLong("contact", 0);
                    if (contact > 0) {
                        conductor.setContactNo(BigInteger.valueOf(contact));
                    }
                    conductor.setJoiningDate(conductorObject.optString("joiningDate", ""));
                    conductor.setDob(conductorObject.optString("dateOfBirth", ""));
                    conductor.setId(conductorObject.optInt("id", -1));
                    dailyRegisterCard.setMainConductor(conductor);
                }

                // Parse Relieving Conductor
                if (jsonObject.has("relievingConductor") && !jsonObject.isNull("relievingConductor")) {
                    JSONObject relievingConductorObject = jsonObject.getJSONObject("relievingConductor");
                    StaffPojo relievingConductor = new StaffPojo();
                    relievingConductor.setRelationMemberName(relievingConductorObject.optString("relativeName", ""));
                    relievingConductor.setLicenceNo(relievingConductorObject.optString("license", ""));
                    relievingConductor.setAddress(relievingConductorObject.optString("address", ""));
                    relievingConductor.setEmployeeCode(relievingConductorObject.optString("empCode", ""));
                    relievingConductor.setName(relievingConductorObject.optString("empName", ""));
                    long contact = relievingConductorObject.optLong("contact", 0);
                    if (contact > 0) {
                        relievingConductor.setContactNo(BigInteger.valueOf(contact));
                    }
                    relievingConductor.setJoiningDate(relievingConductorObject.optString("joiningDate", ""));
                    relievingConductor.setDob(relievingConductorObject.optString("dateOfBirth", ""));
                    relievingConductor.setId(relievingConductorObject.optInt("id", -1));
                    dailyRegisterCard.setRelievingConductor(relievingConductor);
                }

                // Parse Relieving Conductor Stoppage
                if (jsonObject.has("relievingConductorStoppage") && !jsonObject.isNull("relievingConductorStoppage")) {
                    JSONObject relievingConductorStoppageObject = jsonObject.getJSONObject("relievingConductorStoppage");
                    StopPojo relievingConductorStop = new StopPojo();
                    relievingConductorStop.setStopId(relievingConductorStoppageObject.optInt("id", -1));
                    relievingConductorStop.setStopName(relievingConductorStoppageObject.optString("stopName", ""));  //
                    relievingConductorStop.setStopSequence(relievingConductorStoppageObject.optInt("stoppageSequence", -1));
                    dailyRegisterCard.setRelievingConductorStop(relievingConductorStop);
                }


                // Parse Vehicle
                if (jsonObject.has("vehicle") && !jsonObject.isNull("vehicle")) {
                    JSONObject vehicleObject = jsonObject.getJSONObject("vehicle");
                    VehiclePojo vehicle = new VehiclePojo();
                    vehicle.setIotFirm(vehicleObject.optString("iotFirm", ""));
                    vehicle.setVehicleNumber(vehicleObject.optString("vehicleNumber", ""));
                    vehicle.setVehicleModel(vehicleObject.optString("vehicleModel", ""));
                    vehicle.setId(vehicleObject.optInt("id", -1));
                    vehicle.setCapacity(vehicleObject.optInt("capacity", 0));
                    dailyRegisterCard.setAssignedVehicle(vehicle);
                }

                // Parse Curtailed Stoppage
                if (jsonObject.has("curtailedStoppage") && !jsonObject.isNull("curtailedStoppage")) {
                    JSONObject curtailedStoppageObject = jsonObject.getJSONObject("curtailedStoppage");
                    Log.d("Curtailed Stoppage", curtailedStoppageObject.toString());
                    StopPojo curtailedStoppage = new StopPojo();
                    curtailedStoppage.setStopId(curtailedStoppageObject.optInt("id", -1));
                    curtailedStoppage.setStopSequence(curtailedStoppageObject.optInt("stoppageSequence", -1));
                    curtailedStoppage.setStopName(curtailedStoppageObject.optString("stopName", "Unknown"));
                    dailyRegisterCard.setFinalStop(curtailedStoppage);
                }


                // Parse other fields
                dailyRegisterCard.setRecordId(jsonObject.optInt("id", -1));
                dailyRegisterCard.setRecordDate(jsonObject.optString("dutyDate", ""));
                dailyRegisterCard.setActualDepartureTime(jsonObject.optString("actualDepartureTime", ""));
                dailyRegisterCard.setActionTaken(jsonObject.optString("actionTaken", ""));
                dailyRegisterCard.setRemark(jsonObject.optString("remarks", ""));

                // Add to the list
                allRecordsList.add(dailyRegisterCard);
            }

        } catch (JSONException e) {
            Log.e("Parse Error", "Invalid JSON format: " + e.getMessage());
            throw new JSONException("Error parsing JSON: " + e.getMessage());
        } catch (Exception e) {
            Log.e("Parse Error", "Unexpected error: " + e.getMessage());
            throw new JSONException("Unexpected error while parsing: " + e.getMessage());
        }

        return allRecordsList;
    }

    //[
    //  {
    //    "aadhar_no": "274791902679",
    //    "employee_name": "Bhupender Singh Thakur",
    //    "mobile_no": "9857598075",
    //    "office": "823",
    //    "official_email": "bhupendersingh.thakur@himaccess.hp.gov.in"
    //  }
    //]


    public static HimAccessUser parseAadhaarNumberForHimAccessUser(String dataString) {
        try {
            JSONArray dataArray = new JSONArray(dataString);
            JSONObject item = dataArray.getJSONObject(0);

            HimAccessUser user = new HimAccessUser();
            user.setMail(item.optString("email"));
            user.setMobile(item.optString("mobile_no"));
            user.setAadhaarNumber(item.optString("aadhar_no"));

            return user;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


//################################################### Normal Ones ################################################

    //NORMAL SUCCESS RESPONSE FOR EVERY CALL
    public static SuccessResponse getSuccessResponse(String data) throws JSONException {

        JSONObject responseObject = new JSONObject(data);
        SuccessResponse sr = new SuccessResponse();
        sr.setStatus(responseObject.optString("status"));
        sr.setMessage(responseObject.optString("message"));
        sr.setData(responseObject.optString("data"));

        return sr;
    }


}