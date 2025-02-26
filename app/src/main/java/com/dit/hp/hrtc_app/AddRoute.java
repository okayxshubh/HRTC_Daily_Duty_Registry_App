package com.dit.hp.hrtc_app;


import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dit.hp.hrtc_app.Adapters.LocationSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.RouteTypeSpinnerAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.LocationsPojo;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.RoutePojo;
import com.dit.hp.hrtc_app.Modals.RouteTypePojo;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerGet;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerPost;
import com.dit.hp.hrtc_app.json.JsonParse;
import com.dit.hp.hrtc_app.utilities.AppStatus;
import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;
import com.doi.spinnersearchable.SearchableSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class AddRoute extends AppCompatActivity implements ShubhAsyncTaskListenerGet, ShubhAsyncTaskListenerPost {
    AESCrypto aesCrypto = new AESCrypto();
    Button back, proceed;
    int depotId;
    String userNameStr;
    EditText newRouteName, distance, requiredTime;
    TextView departureTime;
    TextView departureTimeLabel, requiredTimeLabel, distanceLabel, serviceLabel, startLocationLabel, endLocationLabel, stopsLabel;
    CustomDialog CD = new CustomDialog();
    TextView headerText;
    LocationSpinnerAdapter startLocationSpinnerAdapter;
    LocationSpinnerAdapter endLocationSpinnerAdapter;
    SearchableSpinner startLocationSpinner, endLocationSpinner;
    RouteTypeSpinnerAdapter routeTypeSpinnerAdapter;
    SearchableSpinner routeTypeSpinner;
    RoutePojo receivedRouteDetailsToEdit;
    LocationsPojo selectedStartLocation, selectedEndLocation;
    RouteTypePojo selectedRouteType;
    private boolean isEditMode = false;

    String encryptedBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route);

        // EDIT MODE
        isEditMode = getIntent().getBooleanExtra("isEditMode", false);

        newRouteName = findViewById(R.id.newRouteName);

        serviceLabel = findViewById(R.id.serviceLabel);
        serviceLabel.setText(Html.fromHtml("Set Route Name <font color='#FF0000'>*</font>"));

        startLocationLabel = findViewById(R.id.startLocationLabel);
        startLocationLabel.setText(Html.fromHtml("Start Location <font color='#FF0000'>*</font>"));

        endLocationLabel = findViewById(R.id.endLocationLabel);
        endLocationLabel.setText(Html.fromHtml("End Location <font color='#FF0000'>*</font>"));

        departureTimeLabel = findViewById(R.id.departureTimeLabel);
        departureTimeLabel.setText(Html.fromHtml("Departure Time <font color='#FF0000'>*</font>"));

        requiredTimeLabel = findViewById(R.id.requiredTimeLabel);
        requiredTimeLabel.setText(Html.fromHtml("Route Time<br> (in Minutes) <font color='#FF0000'>*</font>"));

        distanceLabel = findViewById(R.id.distanceLabel);
        distanceLabel.setText(Html.fromHtml("Distance in KMs <font color='#FF0000'>*</font>"));

        distance = findViewById(R.id.serviceKms);

        requiredTime = findViewById(R.id.requiredTime);

        departureTime = findViewById(R.id.departureTime);

        startLocationSpinner = findViewById(R.id.startLocationSpinner);
        endLocationSpinner = findViewById(R.id.endLocationSpinner);
        routeTypeSpinner = findViewById(R.id.routeType);

        back = findViewById(R.id.backBtn);
        proceed = findViewById(R.id.proceedBtn);

        depotId = Preferences.getInstance().depotId;
        userNameStr = Preferences.getInstance().userName;
        distance = findViewById(R.id.serviceKms);
        requiredTime = findViewById(R.id.requiredTime);

        // Edit Mode
        if (isEditMode) {
            // PreSelecting Departure time in TV
            departureTime.setOnClickListener(v -> {
                // Parse the startTime
                String startTime = receivedRouteDetailsToEdit.getStartTime().toString();
                String[] timeParts = startTime.split(":"); // Split the time string into parts
                int hour = Integer.parseInt(timeParts[0]); // Extract the hour
                int minute = Integer.parseInt(timeParts[1]); // Extract the minute

                // Show TimePickerDialog with preselected time
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        this, (view, selectedHour, selectedMinute) -> {
                    // Format the selected time as HH:mm (keep leading zero for hour and minute)
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    // Set the formatted time to the TextView
                    departureTime.setText(formattedTime);
                },
                        hour, minute, true // Use 24-hour format
                );

                timePickerDialog.show();
            });
        }

        // Not edit mode
        else {
            departureTime.setOnClickListener(v -> {
                // Get the current time
                int hour = 0;
                int minute = 0;

                // Show TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        this, (view, selectedHour, selectedMinute) -> {
                    // Format the selected time as HH:mm
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);

                    // Set the formatted time to the TextView
                    departureTime.setText(formattedTime);
                },
                        hour, minute, true // Use 24-hour format
                );

                timePickerDialog.show();
            });
        }


        // ###################################################################################################
        // Locations
        try {
            if (AppStatus.getInstance(AddRoute.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("location"), "UTF-8"));
                object.setTasktype(TaskType.GET_LOCATION);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddRoute.this, AddRoute.this, TaskType.GET_LOCATION).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddRoute.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddRoute.this, "Something Bad happened . Please reinstall the application and try again.");
        }

        // ROUTE TYPE
        try {
            if (AppStatus.getInstance(AddRoute.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("routeType"), "UTF-8"));
                object.setTasktype(TaskType.GET_ROUTE_TYPE);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddRoute.this, AddRoute.this, TaskType.GET_ROUTE_TYPE).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddRoute.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddRoute.this, "Something Bad happened . Please reinstall the application and try again.");
        }
        // ###################################################################################################

        startLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStartLocation = (LocationsPojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        endLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEndLocation = (LocationsPojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        routeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRouteType = (RouteTypePojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Back Btn
        back.setOnClickListener(v -> {
            showExitConfirmationDialog();
        });

        // Not Edit Mode or Normal Save #####################################################################################################
        if (!isEditMode) {
            // Save Btn (Proceed Button)
            proceed.setOnClickListener(v -> {
                if (AppStatus.getInstance(AddRoute.this).isOnline()) {

                    if (Econstants.isNotEmpty(newRouteName.getText().toString())) {

                        if (selectedRouteType != null) {

                            if (selectedStartLocation != null) {

                                if (selectedEndLocation != null) {

                                    if (!selectedStartLocation.getId().equals(selectedEndLocation.getId())) {

                                        if (Econstants.isNotEmpty(departureTime.getText().toString())) {

                                            if (Econstants.isNotEmpty(distance.getText().toString().trim())) {

                                                if (Econstants.isNotEmpty(requiredTime.getText().toString().trim())) {

                                                    showAddConfirmationDialog("Route");

                                                } else {
                                                    CD.showDialog(AddRoute.this, "Please enter time required for completion of the route in minutes.");
                                                }

                                            } else {
                                                CD.showDialog(AddRoute.this, "Please enter route distance.");
                                            }

                                        } else {
                                            CD.showDialog(AddRoute.this, "Please enter departure time associated with the route.");
                                        }

                                    } else {
                                        CD.showDialog(AddRoute.this, "Please choose different start and end locations.");
                                    }

                                } else {
                                    CD.showDialog(AddRoute.this, "Please select end location.");
                                }

                            } else {
                                CD.showDialog(AddRoute.this, "Please select start location.");
                            }

                        } else {
                            CD.showDialog(AddRoute.this, "Please select the type of route.");
                        }
                    } else {
                        CD.showDialog(AddRoute.this, "Please enter route name.");
                    }

                } else {
                    CD.showDialog(AddRoute.this, "Internet not Available. Please Connect to the Internet and try again.");
                }
            });
        }

        //Edit Mode #####################################################################################################
        else if (isEditMode) {
            receivedRouteDetailsToEdit = (RoutePojo) getIntent().getSerializableExtra("routeDetails");

            // Preselect Fields
            if (receivedRouteDetailsToEdit != null) {
                Log.i("Received route information", "Heil euch mit herz und hand");
                headerText = findViewById(R.id.textView);
                headerText.setText("Update Route");
                proceed.setText("Update");

                // Preselect Fields
                newRouteName.setText(receivedRouteDetailsToEdit.getRouteName());
                distance.setText(String.valueOf(receivedRouteDetailsToEdit.getDistance()));
                requiredTime.setText(String.valueOf(receivedRouteDetailsToEdit.getJourneyHours()));
                departureTime.setText(String.valueOf(receivedRouteDetailsToEdit.getStartTime()));
            }


            // PROCEED FOR EDIT
            proceed.setOnClickListener(v -> {
                if (AppStatus.getInstance(AddRoute.this).isOnline()) {

                    if (Econstants.isNotEmpty(newRouteName.getText().toString())) {

                        if (selectedRouteType != null) {

                            if (selectedStartLocation != null) {

                                if (selectedEndLocation != null) {

                                    if (!selectedStartLocation.getId().equals(selectedEndLocation.getId())) {

                                        if (Econstants.isNotEmpty(departureTime.getText().toString())) {

                                            if (Econstants.isNotEmpty(distance.getText().toString().trim())) {

                                                if (Econstants.isNotEmpty(requiredTime.getText().toString().trim())) {

                                                    showEditConfirmationDialog("Route");

                                                } else {
                                                    CD.showDialog(AddRoute.this, "Please enter time required for completion of the route in minutes.");
                                                }

                                            } else {
                                                CD.showDialog(AddRoute.this, "Please enter route distance.");
                                            }

                                        } else {
                                            CD.showDialog(AddRoute.this, "Please enter departure time associated with the route.");
                                        }

                                    } else {
                                        CD.showDialog(AddRoute.this, "Please choose different start and end locations.");
                                    }

                                } else {
                                    CD.showDialog(AddRoute.this, "Please select end location.");
                                }

                            } else {
                                CD.showDialog(AddRoute.this, "Please select start location.");
                            }

                        } else {
                            CD.showDialog(AddRoute.this, "Please select the type of route.");
                        }
                    } else {
                        CD.showDialog(AddRoute.this, "Please enter route name.");
                    }

                } else {
                    CD.showDialog(AddRoute.this, "Internet not Available. Please Connect to the Internet and try again.");
                }
            });


        }

    }

    // Exit confirmation dialog
    private void showExitConfirmationDialog() {
        String msg;
        if (isEditMode) {
            msg = "Are you sure you want to exit? Any extra edits made will be lost.";
        } else {
            msg = "Are you sure you want to exit? All progress made will be lost.";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddRoute.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog, do nothing
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAddConfirmationDialog(String selectedEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add " + selectedEntity)
                .setMessage("Are you sure you want to add this " + selectedEntity + "?")

                .setPositiveButton("Yes", (dialog, which) -> {
                    if (AppStatus.getInstance(AddRoute.this).isOnline()) {

                        UploadObject uploadObject = new UploadObject();
                        // We can use Enums / Econstant to store these values of url and method names
                        try {
                            uploadObject.setUrl(Econstants.base_url);
                            uploadObject.setMethordName("/master-data?masterName=");
                            uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("route"), "UTF-8"));
                            uploadObject.setTasktype(TaskType.ADD_ROUTE);
                            uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        // Creating JSON to store in param
                        JSONObject jsonObject = new JSONObject();

                        try {
//
                            jsonObject.put("routeName", newRouteName.getText().toString());
                            jsonObject.put("startLocation", selectedStartLocation.getId());
                            jsonObject.put("endLocation", selectedEndLocation.getId());
                            jsonObject.put("routeType", selectedRouteType.getRouteTypeId());
                            jsonObject.put("distance", distance.getText().toString());
                            jsonObject.put("journeyHours", requiredTime.getText().toString());
                            jsonObject.put("startTime", departureTime.getText().toString());
                            jsonObject.put("depot", Preferences.getInstance().depotId);

                            jsonObject.put("createdBy", Preferences.getInstance().empId);

                        } catch (JSONException e) {
                            Log.e("JSON Exception", e.getMessage());
                        }

                        Log.i("JSON for Param", "JSON for Params: " + jsonObject.toString());

                        try {
                            encryptedBody = aesCrypto.encrypt(jsonObject.toString());
                        } catch (Exception e) {
                            Log.e("Encryption Error", e.getMessage());
                        }

                        uploadObject.setParam(encryptedBody);

                        new ShubhAsyncPost(AddRoute.this, AddRoute.this, TaskType.ADD_ROUTE).execute(uploadObject);

                    } else {
                        CD.addCompleteEntityDialog(AddRoute.this, "Internet not Available. Please Connect to the Internet and try again.");
                    }

                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showEditConfirmationDialog(String selectedEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add " + selectedEntity)
                .setMessage("Are you sure you want to edit this " + selectedEntity + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (AppStatus.getInstance(AddRoute.this).isOnline()) {

                        UploadObject uploadObject = new UploadObject();

                        try {
                            uploadObject.setUrl(Econstants.base_url);
                            uploadObject.setMethordName("/master-data?masterName=");
                            uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("route"), "UTF-8")
                                    + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(receivedRouteDetailsToEdit.getRouteId()))));
                            uploadObject.setTasktype(TaskType.EDIT_ROUTE);
                            uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        // Creating JSON to store in param
                        JSONObject jsonObject = new JSONObject();

                        try {
//
                            jsonObject.put("routeId", newRouteName.getText().toString());
                            jsonObject.put("routeName", newRouteName.getText().toString());
                            jsonObject.put("startLocation", selectedStartLocation.getId());
                            jsonObject.put("endLocation", selectedEndLocation.getId());
                            jsonObject.put("routeType", selectedRouteType.getRouteTypeId());
                            jsonObject.put("distance", distance.getText().toString());
                            jsonObject.put("journeyHours", requiredTime.getText().toString());
                            jsonObject.put("startTime", departureTime.getText().toString());
                            jsonObject.put("depot", Preferences.getInstance().depotId);
                            jsonObject.put("updatedBy", Preferences.getInstance().empId);

                        } catch (JSONException e) {
                            Log.e("JSON Exception", e.getMessage());
                        }

                        Log.i("JSON for Param", "JSON for Params: " + jsonObject.toString());

                        try {
                            encryptedBody = aesCrypto.encrypt(jsonObject.toString());
                        } catch (Exception e) {
                            Log.e("Encryption Error", e.getMessage());
                        }

                        uploadObject.setParam(encryptedBody);

                        new ShubhAsyncPost(AddRoute.this, AddRoute.this, TaskType.EDIT_ROUTE).execute(uploadObject);

                    } else {
                        CD.addCompleteEntityDialog(AddRoute.this, "Internet not Available. Please Connect to the Internet and try again.");
                    }

                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }


    @Override
    public void onTaskCompleted(ResponsePojoGet responseObject, TaskType taskType) throws JSONException {

        // GET Location
        if (TaskType.GET_LOCATION == taskType) {
            SuccessResponse response = null;
            Log.i("AddBusDetails", "Task type is fetching routes..");

            List<LocationsPojo> pojoListLocation = new ArrayList<>();

            if (responseObject != null) {
                Log.i("AddBusDetails", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoListLocation = JsonParse.parseLocation(response.getData());


                        if (pojoListLocation.size() > 0) {
                            Log.e("Reports Data Location", pojoListLocation.toString());

                            startLocationSpinnerAdapter = new LocationSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoListLocation);
                            startLocationSpinner.setAdapter(startLocationSpinnerAdapter);

                            endLocationSpinnerAdapter = new LocationSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoListLocation);
                            endLocationSpinner.setAdapter(endLocationSpinnerAdapter);

                            if (isEditMode) {

                                // START
                                if (String.valueOf(receivedRouteDetailsToEdit.getStartLocationPojo()) != null) {
                                    // Preselect Start Location
                                    startLocationSpinner.post(() -> {
                                        int defaultItemPosition = startLocationSpinnerAdapter.getPositionForLocation(receivedRouteDetailsToEdit.getStartLocationPojo().getId());
                                        // Set the spinner to the default position if valid
                                        if (defaultItemPosition != -1) {
                                            startLocationSpinner.setSelectedItemByIndex(defaultItemPosition);
                                        } else {
                                            Log.e("Error", "Location not found in adapter.");
                                        }
                                    });
                                }

                                // END
                                if (String.valueOf(receivedRouteDetailsToEdit.getEndLocationPojo()) != null) {
                                    // Preselect End Location
                                    endLocationSpinner.post(() -> {
                                        int defaultItemPosition = endLocationSpinnerAdapter.getPositionForLocation(receivedRouteDetailsToEdit.getEndLocationPojo().getId());
                                        // Set the spinner to the default position if valid
                                        if (defaultItemPosition != -1) {
                                            endLocationSpinner.setSelectedItemByIndex(defaultItemPosition);
                                        } else {
                                            Log.e("Error", "Location not found in adapter.");
                                        }
                                    });
                                }


                            }

                        }

                    } else {
                        CD.showDialog(AddRoute.this, response.getMessage());
                    }

                } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(AddRoute.this, "No Locations Found");
                }

            } else {
                CD.showDialog(AddRoute.this, response.getMessage());
            }


        }

        // GET ROUTE TYPE
        else if (TaskType.GET_ROUTE_TYPE == taskType) {
            SuccessResponse response = null;
            Log.i("AddBusDetails", "Task type is fetching routes..");

            List<RouteTypePojo> pojoList = new ArrayList<>();

            if (responseObject != null) {
                Log.i("AddBusDetails", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoList = JsonParse.parseRouteType(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data Location", pojoList.toString());
                            routeTypeSpinnerAdapter = new RouteTypeSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            routeTypeSpinner.setAdapter(routeTypeSpinnerAdapter);

                            if (isEditMode) {
                                if (receivedRouteDetailsToEdit.getRouteTypePojo().getRouteTypeId() != null) {
                                    routeTypeSpinner.post(() -> {
                                        int defaultItemPosition = routeTypeSpinnerAdapter.getPositionForType(receivedRouteDetailsToEdit.getRouteTypePojo().getRouteTypeId());
                                        // Set the spinner to the default position if valid
                                        if (defaultItemPosition != -1) {
                                            routeTypeSpinner.setSelectedItemByIndex(defaultItemPosition);
                                        } else {
                                            Log.e("Error", "Location not found in adapter.");
                                        }
                                    });
                                }
                            }
                        }

                    } else {
                        CD.showDialog(AddRoute.this, "No Route Type Found");
                    }

                } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(AddRoute.this, "No Routes Found");
                }

            } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                // Handle HTTP 401 Unauthorized response (session expired)
                CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
            } else {
                CD.showDialog(AddRoute.this, response.getMessage());
            }
        }
        // Add Route
        else if (TaskType.ADD_ROUTE == taskType) {
            Log.i("ASYNC TASK COMPLETED", "TASK TYPE IS Adding Entity");
            SuccessResponse successResponse = null;

            // responseObject will be null if invalid id pass
            if (responseObject != null) {
                successResponse = JsonParse.getSuccessResponse(responseObject.getResponse());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.addCompleteEntityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity

                } else if (successResponse.getStatus().equalsIgnoreCase("ERROR")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.showDismissActivityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
                } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(this, successResponse.getMessage());
                }
            } else {
                Log.i("AddRoute", "Response is null");
                CD.showDialog(this, "Please check your connection");
            }
        }

        // Edit Route
        else if (TaskType.EDIT_ROUTE == taskType) {
            Log.i("ASYNC TASK COMPLETED", "TASK TYPE IS Adding Entity");
            SuccessResponse successResponse = null;

            // responseObject will be null if invalid id pass
            if (responseObject != null) {
                successResponse = JsonParse.getSuccessResponse(responseObject.getResponse());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.addCompleteEntityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity

                } else if (successResponse.getStatus().equalsIgnoreCase("ERROR")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.showDismissActivityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
                } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(this, successResponse.getMessage());
                }
            } else {
                Log.i("AddRoute", "Response is null");
                CD.showDialog(this, "Please check your connection");
            }
        }


    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

}







