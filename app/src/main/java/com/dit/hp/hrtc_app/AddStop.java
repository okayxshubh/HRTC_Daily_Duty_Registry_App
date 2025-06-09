package com.dit.hp.hrtc_app;


import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dit.hp.hrtc_app.Adapters.LocationSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.RouteSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.StopAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.LocationsPojo;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.RoutePojo;
import com.dit.hp.hrtc_app.Modals.StopPojo;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.OnStopRemoveClickListener;
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

import javax.net.ssl.HttpsURLConnection;

public class AddStop extends AppCompatActivity implements ShubhAsyncTaskListenerGet, ShubhAsyncTaskListenerPost, OnStopRemoveClickListener {
    AESCrypto aesCrypto = new AESCrypto();
    Button back, addStop, clearBtn, updateStopBtn;
    SearchableSpinner routeSpinner, locationSpinner;
    RouteSpinnerAdapter routeSpinnerAdapter;
    LocationSpinnerAdapter locationSpinnerAdapter;
    EditText stopSequence;

    String encryptedBody;
    CustomDialog CD = new CustomDialog();
    RoutePojo receivedRouteDetailsToEdit;
    LocationsPojo selectedLocationPojo;

    StopAdapter stopAdapter;
    private List<StopPojo> completeStopList;
    RecyclerView stopListRecyclerView;
    StopPojo selectedStopToEdit;

    // Declare a new list to store newly added stops
    private List<StopPojo> newStopsList = new ArrayList<>();


    private Boolean isStopEditMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stop);

        receivedRouteDetailsToEdit = (RoutePojo) getIntent().getSerializableExtra("routeDetails");

        back = findViewById(R.id.backBtn);
        routeSpinner = findViewById(R.id.routeSpinner);
        locationSpinner = findViewById(R.id.stopSpinner);
        stopSequence = findViewById(R.id.stopSequence);
        addStop = findViewById(R.id.addStop);
        clearBtn = findViewById(R.id.cancelBtn);
        stopListRecyclerView = findViewById(R.id.stopListRecyclerView);
        updateStopBtn = findViewById(R.id.updateStopBtn);

        // Initialize stop list
        completeStopList = new ArrayList<>();
        stopAdapter = new StopAdapter(completeStopList, this);
        stopListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stopListRecyclerView.setAdapter(stopAdapter);

        clearBtn.setOnClickListener(v -> {
            stopSequence.setText("");
            clearBtn.setVisibility(View.GONE);
            addStop.setBackground(ContextCompat.getDrawable(this, R.drawable.green_ok_bg));
            addStop.setText("Add Stop");
            stopAdapter.clearEditingPosition();
            selectedStopToEdit = null;
            selectedLocationPojo = null;
            locationSpinner.clearSelection();
        });

        // ###################################################################################################

        // Route Service Call
        try {
            if (AppStatus.getInstance(AddStop.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("route"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().regionalOfficeId)), "UTF-8"));
                object.setTasktype(TaskType.GET_ROUTES);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddStop.this, AddStop.this, TaskType.GET_ROUTES).execute(object);

            } else {
                CD.showDialog(AddStop.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddStop.this, "Something Bad happened . Please reinstall the application and try again.");
        }

        // Locations Service call
        try {
            if (AppStatus.getInstance(AddStop.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("location"), "UTF-8"));
                object.setTasktype(TaskType.GET_LOCATION);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddStop.this, AddStop.this, TaskType.GET_LOCATION).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddStop.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddStop.this, "Something Bad happened . Please reinstall the application and try again.");
        }


        // Fetch Stops of the selected route service call
        try {
            if (AppStatus.getInstance(AddStop.this).isOnline()) {

                UploadObject uploadObject = new UploadObject();
                uploadObject.setUrl(Econstants.base_url);
                uploadObject.setMethordName("/master-data?");
                uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("routeStoppage"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(receivedRouteDetailsToEdit.getRouteId())), "UTF-8"));
                uploadObject.setTasktype(TaskType.GET_STOPS);
                uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);
                Log.e("URL Log", "URL LOG: " + uploadObject.getUrl() + uploadObject.getMethordName() + uploadObject.getMasterName());

                new ShubhAsyncGet(AddStop.this, AddStop.this, TaskType.GET_STOPS).execute(uploadObject);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddStop.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddStop.this, "Something Bad happened . Please reinstall the application and try again.");
        }

        // ###################################################################################################

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLocationPojo = (LocationsPojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        addStop.setOnClickListener(v -> {

            // Stop Edit Mode
            if (isStopEditMode) {
                Log.e("ABC", "ABC: EDIT MODE");
                if (routeSpinner.getSelectedItem() != null) {
                    if (Econstants.isNotEmpty(stopSequence.getText().toString())) {
                        // Calling Add Stop API
                        JSONObject stopObject = new JSONObject();
                        try {
                            stopObject.put("route", receivedRouteDetailsToEdit.getRouteId());
                            stopObject.put("stoppage", selectedLocationPojo.getId());
                            stopObject.put("stoppageSequence", Integer.parseInt(stopSequence.getText().toString()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        UploadObject uploadObject = new UploadObject();
                        uploadObject.setUrl(Econstants.base_url);
                        uploadObject.setMethordName("/master-data?masterName=");

                        try {
                            uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("routeStoppage"), "UTF-8")
                                    + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(selectedStopToEdit.getStopId()))));

                            Log.e("STOPS", "STOP TO EDIT: " + stopObject.toString());
                            String encryptedBody = aesCrypto.encrypt(stopObject.toString());
                            uploadObject.setParam(encryptedBody);
                        } catch (Exception e) {
                            Log.e("Encryption Error", e.getMessage());
                        }

                        uploadObject.setTasktype(TaskType.EDIT_STOP);
                        uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

                        new ShubhAsyncPost(AddStop.this, AddStop.this, TaskType.EDIT_STOP).execute(uploadObject);
                    } else {
                        CD.showDialog(this, "Please enter stop sequence.");
                    }
                } else {
                    CD.showDialog(this, "Please select a stop.");
                }
            }

            // Normal Mode
            else {
                Log.e("ABC", "ABC: NORMAL MODE");
                if (routeSpinner.getSelectedItem() != null) {
                    if (selectedLocationPojo != null) {
                        if (Econstants.isNotEmpty(stopSequence.getText().toString())) {
                            // Calling Add Stop API
                            JSONObject stopObject = new JSONObject();
                            try {
                                stopObject.put("route", receivedRouteDetailsToEdit.getRouteId());
                                stopObject.put("stoppage", selectedLocationPojo.getId());
                                stopObject.put("stoppageSequence", Integer.parseInt(stopSequence.getText().toString()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            UploadObject uploadObject = new UploadObject();
                            uploadObject.setUrl(Econstants.base_url);
                            uploadObject.setMethordName("/master-data?masterName=");

                            try {
                                uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("routeStoppage"), "UTF-8"));
                                Log.e("STOPS", "New Stop to Add: " + stopObject.toString());
                                String encryptedBody = aesCrypto.encrypt(stopObject.toString());
                                uploadObject.setParam(encryptedBody);
                            } catch (Exception e) {
                                Log.e("Encryption Error", e.getMessage());
                            }

                            uploadObject.setTasktype(TaskType.ADD_STOP);
                            uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

                            new ShubhAsyncPost(AddStop.this, AddStop.this, TaskType.ADD_STOP).execute(uploadObject);
                        } else {
                            CD.showDialog(this, "Please enter stop sequence.");
                        }
                    } else {
                        CD.showDialog(this, "Please select a stop.");
                    }
                } else {
                    CD.showDialog(this, "Something went wrong. Login again to fix");
                }
            }

        });


        // Back Btn
        back.setOnClickListener(v -> {
            AddStop.this.finish();
        });


    }


    // Delete STOP
    @Override
    public void onStopRemoveClick(StopPojo selectedPojo, int position) {

        String msg;
        msg = "Are you sure you want to remove the selected stop '" + selectedPojo.getStopName() + "' from this route?";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        UploadObject uploadObject = new UploadObject();

                        try {
                            uploadObject.setUrl(Econstants.base_url);
                            uploadObject.setMethordName("/master-data?masterName=");

                            uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("routeStoppage"), "UTF-8")
                                    + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(selectedPojo.getStopId()))));

                            uploadObject.setTasktype(TaskType.REMOVE_STOP);
                            uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.i("Object", "Complete Object: " + uploadObject.toString());

                        new ShubhAsyncPost(AddStop.this, AddStop.this, TaskType.REMOVE_STOP).execute(uploadObject);


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

    @Override
    public void onStopEditClick(StopPojo selectedPojo, int position) {
        isStopEditMode = true;  // Set the mode to edit
        clearBtn.setVisibility(View.VISIBLE);
        Log.e("EditMode", "isStopEditMode set to: " + isStopEditMode);


        addStop.setText("Update Stop");
        addStop.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_bg));


        // Highlight the selected stop
        stopAdapter.setEditingPosition(position);

        selectedStopToEdit = selectedPojo;
        stopSequence.setText(String.valueOf(selectedPojo.getStopSequence()));

        if (selectedPojo != null) {
            locationSpinner.post(() -> {
                int defaultItemPosition = locationSpinnerAdapter.getPositionForLocation(selectedPojo.getStopName());
                // Set the spinner to the default position if valid
                if (defaultItemPosition != -1) {
                    locationSpinner.setSelectedItemByIndex(defaultItemPosition);
                } else {
                    Log.e("Error", "Location not found in adapter.");
                }
            });
        }

    }


    // Preselect the route stops in the stop list
    private void populateStopList(List<StopPojo> stops) {
        // Clear existing stops if needed
        completeStopList.clear();

        // Add fetched stops to the completeStopList
        for (int i = 0; i < stops.size(); i++) {
            StopPojo stop = stops.get(i);
            completeStopList.add(stop);
        }

        // Notify the adapter about data changes
        stopAdapter.notifyDataSetChanged();
    }


    @Override
    public void onTaskCompleted(ResponsePojoGet responseObject, TaskType taskType) throws JSONException {

        if (TaskType.GET_ROUTES == taskType) {
            SuccessResponse response = null;
            List<RoutePojo> pojoList = null;
            Log.i("AddBusDetails", "Task type is fetching routes..");

            if (responseObject != null) {
                Log.i("AddBusDetails", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoList = JsonParse.parseRoutes(response.getData());
                        Log.i("pojoList", pojoList.toString());

                        if (pojoList.size() > 0) {
                            Log.e("Markers Size", String.valueOf(pojoList.size()));
                            Log.e("Reports Data", pojoList.toString());

                            routeSpinnerAdapter = new RouteSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            routeSpinner.setAdapter(routeSpinnerAdapter);

                            // PRESELECT Route
                            if (receivedRouteDetailsToEdit != null) {
                                routeSpinner.post(() -> {
                                    int defaultItemPosition = routeSpinnerAdapter.getPositionForRoute(receivedRouteDetailsToEdit.getRouteName(), receivedRouteDetailsToEdit.getRouteId());
                                    // Set the spinner to the default position if valid
                                    if (defaultItemPosition != -1) {
                                        routeSpinner.setSelectedItemByIndex(defaultItemPosition);
                                        routeSpinner.setEnabled(false);
                                    } else {
                                        Log.e("Error", "Route not found in adapter.");
                                    }
                                });
                            }

                        } else {
                            CD.showDialog(AddStop.this, "No Routes Found");
                        }

                    } else {
                        CD.showDialog(AddStop.this, response.getMessage());
                    }
                } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(AddStop.this, "Not able to connect to the server");
                }
            } else {
//                CD.showDialog(AddBusDetails.this, "Result is null");
            }
        }

        // GET Location
        else if (TaskType.GET_LOCATION == taskType) {
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

                            locationSpinnerAdapter = new LocationSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoListLocation);
                            locationSpinner.setAdapter(locationSpinnerAdapter);

                        } else {
                            CD.showDialog(AddStop.this, response.getMessage());
                        }

                    } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                        // Handle HTTP 401 Unauthorized response (session expired)
                        CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                    } else {
                        CD.showDialog(AddStop.this, "No Locations Found");
                    }

                } else {
                    CD.showDialog(AddStop.this, response.getMessage());
                }

            }
        }

        // GET STOPS ACC TO ROUTE
        else if (TaskType.GET_STOPS == taskType) {
            SuccessResponse response = null;
            List<StopPojo> pojoList = null;
            Log.i("BusDetails", "Task type is fetching route stops..");

            if (responseObject != null) {
                Log.i("Bus Details", "Response Obj: " + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response Stops: ", response.toString());
                    Log.e("Response Stops: ", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        pojoList = JsonParse.parseStops(response.getData());
                        Log.i("pojoList", pojoList.toString());

                        if (pojoList != null && !pojoList.isEmpty()) {
                            Log.e("Markers Size", String.valueOf(pojoList.size()));
                            Log.e("Reports Data", pojoList.toString());

                            // Populate Stops
                            populateStopList(pojoList);
                            completeStopList.clear();
                            completeStopList.addAll(pojoList);

                        } else {
//                            CD.showDialog(AddStop.this, "No Stops Found");
                        }
                    } else {
                        CD.showDialog(AddStop.this, response.getMessage());
                    }
                } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(AddStop.this, "Not able to fetch data");
                }
            }
        }

        // Add STOPS
        if (TaskType.ADD_STOP == taskType) {
            Log.i("ASYNC TASK COMPLETED", "TASK TYPE IS Adding Entity");
            SuccessResponse successResponse = null;

            // responseObject will be null if invalid id pass
            if (responseObject != null) {
                successResponse = JsonParse.getSuccessResponse(responseObject.getResponse());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    Log.i("Add Entity Response", successResponse.getData());

                    CD.showStopAddedDialog(this, successResponse.getMessage(), receivedRouteDetailsToEdit); // Dialog that dismisses activity

                } else if (successResponse.getStatus().equalsIgnoreCase("ERROR")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.showDismissActivityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
                } else {
                    CD.showDismissActivityDialog(this, successResponse.getMessage());
                }
            } else {
                Log.i("Add Staff", "Response is null");
                CD.showDismissActivityDialog(this, "Response is null.");
            }
        }

        // Remove stop
        else if (TaskType.REMOVE_STOP == taskType) {
            Log.i("ASYNC TASK COMPLETED", "TASK TYPE IS Removing STOP");
            SuccessResponse successResponse = null;

            // responseObject will be null if invalid id pass
            if (responseObject != null) {
                successResponse = JsonParse.getSuccessResponse(responseObject.getResponse());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    Log.i("Remove Entity Response", successResponse.getData());
                    CD.showStopAddedDialog(this, successResponse.getMessage(), receivedRouteDetailsToEdit); // Dialog that dismisses activity

                    // Refresh On Deletion
                    stopAdapter.notifyDataSetChanged();

                } else if (successResponse.getStatus().equalsIgnoreCase("ERROR")) {
                    Log.i("Remove Entity Response", successResponse.getData());
                    CD.showDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
                } else {
                    CD.showDismissActivityDialog(this, "Please connect to the internet");
                }
            } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                // Handle HTTP 401 Unauthorized response (session expired)
                CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
            } else {
                Log.i("AddDriver", "Response is null");
                CD.showDismissActivityDialog(this, "Response is null.");
            }
        }

        // EDIT STOP
        else if (TaskType.EDIT_STOP == taskType) {
            SuccessResponse successResponse = null;

            // responseObject will be null if invalid id pass
            if (responseObject != null) {
                successResponse = JsonParse.getSuccessResponse(responseObject.getResponse());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    Log.i("Add Entity Response", successResponse.getData());

                    isStopEditMode = false;
                    clearBtn.setVisibility(View.GONE);
                    addStop.setBackground(ContextCompat.getDrawable(this, R.drawable.green_ok_bg));
                    stopSequence.setText("");
                    addStop.setText("Add Stop");
                    stopAdapter.clearEditingPosition();
                    selectedStopToEdit = null;
                    selectedLocationPojo = null;
                    locationSpinner.clearSelection();
                    CD.showStopAddedDialog(this, successResponse.getMessage(), receivedRouteDetailsToEdit); // Dialog that dismisses activity
                }
                // Depot has dependencies under it
                else if (successResponse.getStatus().equalsIgnoreCase("ERROR")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.showDismissActivityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
                } else {
                    CD.showDismissActivityDialog(this, successResponse.getMessage());
                }
            } else {
                Log.i("AddDriver", "Response is null");
                CD.showDismissActivityDialog(this, "Response is null.");
            }
        }

    }


}