package com.dit.hp.hrtc_app;


import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dit.hp.hrtc_app.Adapters.ConductorSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.DepotSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.DriverSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.RouteSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.VehicleSpinnerAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.DepotPojo;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.RoutePojo;
import com.dit.hp.hrtc_app.Modals.StaffPojo;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Modals.VehiclePojo;
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

import javax.net.ssl.HttpsURLConnection;

public class TransferHome extends AppCompatActivity implements ShubhAsyncTaskListenerGet, ShubhAsyncTaskListenerPost {

    AESCrypto aesCrypto = new AESCrypto();
    Button back, transfer;

    SearchableSpinner oldDepotSpinner, newDepotSpinner, vehicleNumberSpinner, driverSpinner, conductorSpinner, routeSpinner;
    LinearLayout currentDepotLayout, driverLayout, conductorLayout, vehicleLayout, routeLayout, allotNewDepotLayout;

    TextView oldDepotLabel, newDepotLabel, vehicleLabel, driverLabel, conductorLabel, routeLabel, entityLabel;
    Spinner entitySpinner;

    // Global Adapters.. Set Values in them in onTaskCompleted()
    private DepotSpinnerAdapter depotSpinnerAdapter;
    private VehicleSpinnerAdapter vehicleSpinnerAdapter;
    private DriverSpinnerAdapter driverSpinnerAdapter;
    private ConductorSpinnerAdapter conductorSpinnerAdapter;
    private RouteSpinnerAdapter routeSpinnerAdapter;

    DepotPojo oldDepotSelection;
    DepotPojo newDepotSelection;
    StaffPojo driverToTransfer;
    StaffPojo conductorToTransfer;
    VehiclePojo vehicleToTransfer;
    RoutePojo routeToTransfer;

    CustomDialog CD = new CustomDialog();

    String[] entities = {"Driver", "Conductor", "Vehicle", "Route"};
    String selectedEntityStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_home);


        // Spinners
        vehicleNumberSpinner = findViewById(R.id.vehicleNumberSpinner);
        newDepotSpinner = findViewById(R.id.newDepotSpinner);

        // Layouts
        driverLayout = findViewById(R.id.driverToTransferLayout);
        conductorLayout = findViewById(R.id.conductorToTransferLayout);
        vehicleLayout = findViewById(R.id.vehicleToTransferLayout);
        routeLayout = findViewById(R.id.routeToTransferLayout);
        currentDepotLayout = findViewById(R.id.currentDepotLayout);
        allotNewDepotLayout = findViewById(R.id.allotNewDepotLayout);

        // LABELS
        vehicleLabel = findViewById(R.id.vehicleLabel);
        vehicleLabel.setText(Html.fromHtml("Vehicle To Transfer <font color='#FF0000'>*</font>"));

        oldDepotLabel = findViewById(R.id.oldDepotLabel);
        oldDepotLabel.setText(Html.fromHtml("Current Depot <font color='#FF0000'>*</font>"));

        newDepotLabel = findViewById(R.id.newDepotLabel);
        newDepotLabel.setText(Html.fromHtml("Allot New Depot <font color='#FF0000'>*</font>"));

        driverLabel = findViewById(R.id.driverLabel);
        driverLabel.setText(Html.fromHtml("Driver To Transfer <font color='#FF0000'>*</font>"));

        conductorLabel = findViewById(R.id.conductorLabel);
        conductorLabel.setText(Html.fromHtml("Conductor To Transfer <font color='#FF0000'>*</font>"));

        vehicleLabel = findViewById(R.id.vehicleLabel);
        vehicleLabel.setText(Html.fromHtml("Vehicle To Transfer <font color='#FF0000'>*</font>"));

        routeLabel = findViewById(R.id.routeLabel);
        routeLabel.setText(Html.fromHtml("Route To Transfer <font color='#FF0000'>*</font>"));

        entityLabel = findViewById(R.id.entityLabel);
        entityLabel.setText(Html.fromHtml("Please Select Entity to Transfer <font color='#FF0000'>*</font>"));


        oldDepotSpinner = findViewById(R.id.currentDepotSpinner);
        driverSpinner = findViewById(R.id.driverSpinner);
        conductorSpinner = findViewById(R.id.conductorSpinner);
        routeSpinner = findViewById(R.id.routeSpinner);
        vehicleNumberSpinner = findViewById(R.id.vehicleNumberSpinner);

        back = findViewById(R.id.backBtn);
        transfer = findViewById(R.id.transferBtn);

        entitySpinner = findViewById(R.id.entitySpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, entities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        entitySpinner.setAdapter(adapter);


// ###########################################  SERVICE CALLS  ##############################################################

        // Depot Service Call
        try {
            if (AppStatus.getInstance(TransferHome.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("depot"), "UTF-8"));
                object.setTasktype(TaskType.GET_DEPOTS);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(TransferHome.this, TransferHome.this, TaskType.GET_DEPOTS).execute(object);

            } else {
                CD.showDialog(TransferHome.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(TransferHome.this, "Something Bad happened . Please reinstall the application and try again.");
        }

// ########################################################################################################################

        // Spinners
        entitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEntityStr = parent.getItemAtPosition(position).toString();
                Log.i("Selected Entity: ", selectedEntityStr);

                // DRIVER
                if (selectedEntityStr.equalsIgnoreCase("Driver")) {

                    // Depot Refresh
                    if (depotSpinnerAdapter != null) {

                        oldDepotSpinner.clearSelection();
                        newDepotSpinner.clearSelection();
                        driverSpinner.clearSelection();
                        conductorSpinner.clearSelection();
                        vehicleNumberSpinner.clearSelection();
                        routeSpinner.clearSelection();

                        oldDepotSelection = null;
                        newDepotSelection = null;
                        driverToTransfer = null;
                        conductorToTransfer = null;
                        vehicleToTransfer = null;
                        routeToTransfer = null;


                        // Preselect Depot Acc to Preferences
                        oldDepotSpinner.post(() -> {
                            int oldItemPosition = depotSpinnerAdapter.getPositionForDepot(Preferences.getInstance().depotName, Preferences.getInstance().depotId);
                            if (oldItemPosition != -1) {
                                oldDepotSpinner.setSelectedItemByIndex(oldItemPosition);

                                if (Preferences.getInstance().roleId == 1 || Preferences.getInstance().roleId == 2) {
                                    oldDepotSpinner.setEnabled(true);
                                } else {
                                    oldDepotSpinner.setEnabled(true);
                                }
                            } else {
                                Log.e("Error", "Item Position not found in adapter.");
                            }
                        });
                    }

                    // Service call acc to depot
                    currentDepotLayout.setVisibility(View.VISIBLE);
                    driverLayout.setVisibility(View.VISIBLE);
                    driverSpinner.setVisibility(View.VISIBLE);
                    allotNewDepotLayout.setVisibility(View.VISIBLE);
                    vehicleLayout.setVisibility(View.GONE);
                    conductorLayout.setVisibility(View.GONE);
                    routeLayout.setVisibility(View.GONE);


                } else if (selectedEntityStr.equalsIgnoreCase("Conductor")) {

                    // Depot Refresh
                    if (depotSpinnerAdapter != null) {

                        oldDepotSpinner.clearSelection();
                        newDepotSpinner.clearSelection();
                        driverSpinner.clearSelection();
                        conductorSpinner.clearSelection();
                        vehicleNumberSpinner.clearSelection();
                        routeSpinner.clearSelection();

                        oldDepotSelection = null;
                        newDepotSelection = null;
                        driverToTransfer = null;
                        conductorToTransfer = null;
                        vehicleToTransfer = null;
                        routeToTransfer = null;


                        // Preselect Depot Acc to Preferences
                        oldDepotSpinner.post(() -> {
                            int oldItemPosition = depotSpinnerAdapter.getPositionForDepot(Preferences.getInstance().depotName, Preferences.getInstance().depotId);
                            if (oldItemPosition != -1) {
                                oldDepotSpinner.setSelectedItemByIndex(oldItemPosition);

                                if (Preferences.getInstance().roleId == 1 || Preferences.getInstance().roleId == 2) {
                                    oldDepotSpinner.setEnabled(true);
                                } else {
                                    oldDepotSpinner.setEnabled(true);
                                }
                            } else {
                                Log.e("Error", "Item Position not found in adapter.");
                            }
                        });
                    }

                    // Service call acc to depot
                    currentDepotLayout.setVisibility(View.VISIBLE);
                    conductorLayout.setVisibility(View.VISIBLE);
                    conductorSpinner.setVisibility(View.VISIBLE);
                    allotNewDepotLayout.setVisibility(View.VISIBLE);
                    vehicleLayout.setVisibility(View.GONE);
                    driverLayout.setVisibility(View.GONE);
                    routeLayout.setVisibility(View.GONE);

                } else if (selectedEntityStr.equalsIgnoreCase("Vehicle")) {

                    // Depot Refresh
                    if (depotSpinnerAdapter != null) {

                        oldDepotSpinner.clearSelection();
                        newDepotSpinner.clearSelection();
                        driverSpinner.clearSelection();
                        conductorSpinner.clearSelection();
                        vehicleNumberSpinner.clearSelection();
                        routeSpinner.clearSelection();

                        oldDepotSelection = null;
                        newDepotSelection = null;
                        driverToTransfer = null;
                        conductorToTransfer = null;
                        vehicleToTransfer = null;
                        routeToTransfer = null;


                        // Preselect Depot Acc to Preferences
                        oldDepotSpinner.post(() -> {
                            int oldItemPosition = depotSpinnerAdapter.getPositionForDepot(Preferences.getInstance().depotName, Preferences.getInstance().depotId);
                            if (oldItemPosition != -1) {
                                oldDepotSpinner.setSelectedItemByIndex(oldItemPosition);

                                if (Preferences.getInstance().roleId == 1 || Preferences.getInstance().roleId == 2) {
                                    oldDepotSpinner.setEnabled(true);
                                } else {
                                    oldDepotSpinner.setEnabled(true);
                                }
                            } else {
                                Log.e("Error", "Item Position not found in adapter.");
                            }
                        });
                    }

                    // Service call acc to depot
                    currentDepotLayout.setVisibility(View.VISIBLE);
                    vehicleLayout.setVisibility(View.VISIBLE);
                    vehicleNumberSpinner.setVisibility(View.VISIBLE);
                    allotNewDepotLayout.setVisibility(View.VISIBLE);
                    conductorLayout.setVisibility(View.GONE);
                    driverLayout.setVisibility(View.GONE);
                    routeLayout.setVisibility(View.GONE);

                } else if (selectedEntityStr.equalsIgnoreCase("Route")) {

                    // Depot Refresh
                    if (depotSpinnerAdapter != null) {

                        oldDepotSpinner.clearSelection();
                        newDepotSpinner.clearSelection();
                        driverSpinner.clearSelection();
                        conductorSpinner.clearSelection();
                        vehicleNumberSpinner.clearSelection();
                        routeSpinner.clearSelection();

                        oldDepotSelection = null;
                        newDepotSelection = null;
                        driverToTransfer = null;
                        conductorToTransfer = null;
                        vehicleToTransfer = null;
                        routeToTransfer = null;


                        // Preselect Depot Acc to Preferences
                        oldDepotSpinner.post(() -> {
                            int oldItemPosition = depotSpinnerAdapter.getPositionForDepot(Preferences.getInstance().depotName, Preferences.getInstance().depotId);
                            if (oldItemPosition != -1) {
                                oldDepotSpinner.setSelectedItemByIndex(oldItemPosition);

                                if (Preferences.getInstance().roleId == 1 || Preferences.getInstance().roleId == 2) {
                                    oldDepotSpinner.setEnabled(true);
                                } else {
                                    oldDepotSpinner.setEnabled(true);
                                }
                            } else {
                                Log.e("Error", "Item Position not found in adapter.");
                            }
                        });
                    }

                    // Service call acc to depot
                    currentDepotLayout.setVisibility(View.VISIBLE);
                    routeLayout.setVisibility(View.VISIBLE);
                    routeSpinner.setVisibility(View.VISIBLE);
                    allotNewDepotLayout.setVisibility(View.VISIBLE);
                    vehicleLayout.setVisibility(View.GONE);
                    conductorLayout.setVisibility(View.GONE);
                    driverLayout.setVisibility(View.GONE);

                } else {

                    newDepotSpinner.clearSelection();
                    driverSpinner.clearSelection();
                    conductorSpinner.clearSelection();
                    vehicleNumberSpinner.clearSelection();
                    routeSpinner.clearSelection();

                    newDepotSelection = null;
                    driverToTransfer = null;
                    conductorToTransfer = null;
                    vehicleToTransfer = null;
                    routeToTransfer = null;

                    CD.showDialog(TransferHome.this, "Please select a valid entity");
                    currentDepotLayout.setVisibility(View.GONE);
                    allotNewDepotLayout.setVisibility(View.GONE);
                    vehicleLayout.setVisibility(View.GONE);
                    conductorLayout.setVisibility(View.GONE);
                    driverLayout.setVisibility(View.GONE);
                    routeLayout.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedEntityStr = null;
            }
        });

        // Old Depot Spinner
        oldDepotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                oldDepotSelection = (DepotPojo) parent.getItemAtPosition(position);

                if (selectedEntityStr.equalsIgnoreCase("Driver")) {
                    driverLayout.setVisibility(View.VISIBLE);
                    conductorLayout.setVisibility(View.GONE);
                    vehicleLayout.setVisibility(View.GONE);
                    routeLayout.setVisibility(View.GONE);
                    driverToTransfer = null;
                    conductorToTransfer = null;
                    vehicleToTransfer = null;
                    routeToTransfer = null;
                    allotNewDepotLayout.setVisibility(View.VISIBLE);
                    // Service call acc to depot
                    makeDriverServiceCall();

                    // Driver Refresh
                    if (driverSpinnerAdapter != null) {
                        driverSpinnerAdapter.notifyDataSetChanged();
                    }
                }

                // Conductor
                else if (selectedEntityStr.equalsIgnoreCase("Conductor")) {
                    driverLayout.setVisibility(View.GONE);
                    conductorLayout.setVisibility(View.VISIBLE);
                    vehicleLayout.setVisibility(View.GONE);
                    routeLayout.setVisibility(View.GONE);
                    driverToTransfer = null;
                    conductorToTransfer = null;
                    vehicleToTransfer = null;
                    routeToTransfer = null;
                    allotNewDepotLayout.setVisibility(View.VISIBLE);

                    // Service call acc to depot
                    makeConductorServiceCall();

                    // Refresh Conductor
                    if (conductorSpinnerAdapter != null) {
                        conductorSpinnerAdapter.notifyDataSetChanged();
                    }
                } else if (selectedEntityStr.equalsIgnoreCase("Vehicle")) {
                    driverLayout.setVisibility(View.GONE);
                    conductorLayout.setVisibility(View.GONE);
                    vehicleLayout.setVisibility(View.VISIBLE);
                    routeLayout.setVisibility(View.GONE);
                    driverToTransfer = null;
                    conductorToTransfer = null;
                    vehicleToTransfer = null;
                    routeToTransfer = null;
                    allotNewDepotLayout.setVisibility(View.VISIBLE);
                    // Service call acc to depot
                    makeVehicleServiceCall();

                    // Refresh Vehicle
                    if (vehicleSpinnerAdapter != null) {
                        vehicleSpinnerAdapter.notifyDataSetChanged();
                    }

                } else if (selectedEntityStr.equalsIgnoreCase("Route")) {
                    driverLayout.setVisibility(View.GONE);
                    conductorLayout.setVisibility(View.GONE);
                    vehicleLayout.setVisibility(View.GONE);
                    routeLayout.setVisibility(View.VISIBLE);
                    driverToTransfer = null;
                    conductorToTransfer = null;
                    vehicleToTransfer = null;
                    routeToTransfer = null;
                    allotNewDepotLayout.setVisibility(View.VISIBLE);
                    // Service call acc to depot
                    makeRouteServiceCall();

                    // Refresh Route
                    if (routeSpinnerAdapter != null) {
                        routeSpinnerAdapter.notifyDataSetChanged();
                    }
                } else {
                    CD.showDialog(TransferHome.this, "Please select a valid entity");
                    currentDepotLayout.setVisibility(View.GONE);
                    allotNewDepotLayout.setVisibility(View.GONE);
                    vehicleLayout.setVisibility(View.GONE);
                    conductorLayout.setVisibility(View.GONE);
                    driverLayout.setVisibility(View.GONE);
                    routeLayout.setVisibility(View.GONE);
                    driverToTransfer = null;
                    conductorToTransfer = null;
                    vehicleToTransfer = null;
                    routeToTransfer = null;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                driverToTransfer = (StaffPojo) parent.getItemAtPosition(position);
                Log.i("Selected Driver: ", driverToTransfer.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        conductorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                conductorToTransfer = (StaffPojo) parent.getItemAtPosition(position);
                Log.i("Selected Conductor: ", conductorToTransfer.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Spinners
        vehicleNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vehicleToTransfer = (VehiclePojo) parent.getItemAtPosition(position);
                Log.i("Selected Vehicle", "Vehicle: " + vehicleToTransfer.getVehicleNumber());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                routeToTransfer = (RoutePojo) parent.getItemAtPosition(position);
                Log.i("Selected Route", "Route: " + routeToTransfer.getRouteName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle if nothing is selected
            }
        });


        newDepotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newDepotSelection = (DepotPojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Back Btn
        back.setOnClickListener(v -> TransferHome.this.finish());
        
        // Transfer Btn
        transfer.setOnClickListener(v -> {
            // 1. Check if an entity is selected
            if (Econstants.isNotEmpty(selectedEntityStr)) {

                // 2. Check if the old depot is selected
                if (oldDepotSelection != null) {

                    // 3. Check if any of the items (vehicle, conductor, driver, route) is selected
                    if (vehicleToTransfer != null || driverToTransfer != null || conductorToTransfer != null || routeToTransfer != null) {

                        // 4. Check if new depot is selected and valid
                        if (newDepotSelection != null && !newDepotSelection.getDepotName().equalsIgnoreCase("-")) {

                            // 5. Ensure old and new depots are not the same
                            if (!oldDepotSelection.getDepotName().equalsIgnoreCase(newDepotSelection.getDepotName()) || oldDepotSelection.getId() != newDepotSelection.getId()) {

                                // 6. Check internet status
                                if (AppStatus.getInstance(TransferHome.this).isOnline()) {
                                    // Proceed with the transfer logic (e.g., call service or display confirmation dialog)

                                    if (oldDepotSelection != null) {
                                        Log.i("OldDepot", "Old Depot: " + oldDepotSelection.getDepotName());
                                    }

                                    if (newDepotSelection != null) {
                                        Log.i("OldDepot", "Old Depot: " + newDepotSelection.getDepotName());
                                    }

                                    // 3. Check if any of the items (vehicle, conductor, driver, route) is selected
                                    if (vehicleToTransfer != null) {
                                        Log.i("Vehicle", "Vehicle: " + vehicleToTransfer.getVehicleNumber());
                                    }

                                    if (driverToTransfer != null) {
                                        Log.i("Driver", "Driver: " + driverToTransfer.getName());
                                    }

                                    if (conductorToTransfer != null) {
                                        Log.i("Conductor", "Conductor: " + conductorToTransfer.getName());
                                    }

                                    if (routeToTransfer != null) {
                                        Log.i("Route", "Route: " + routeToTransfer.getRouteName());
                                    }
                                    transferConfirmationDialog();

                                } else {
                                    // Show error if no internet
                                    CD.showDialog(TransferHome.this, Econstants.internetNotAvailable);
                                }

                            } else {
                                // Error if old and new depots are the same
                                CD.showDialog(this, "Transfer cannot be performed between the same depots. Please choose another depot.");
                            }

                        } else {
                            // Error if new depot is not selected
                            CD.showDialog(this, "Please select a new depot.");
                        }

                    } else {
                        // Error if no item to transfer is selected
                        CD.showDialog(this, "Please select the item to transfer.");
                    }

                } else {
                    // Error if old depot is not selected
                    CD.showDialog(this, "Please select the current depot.");
                }

            } else {
                // Error if entity is not selected
                CD.showDialog(this, "Please select a valid entity.");
            }
        });


    }




    // Service Call Custom Methods
    private void makeRouteServiceCall() {
        // Route Service call
        try {
            if (AppStatus.getInstance(TransferHome.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("route"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(oldDepotSelection.getId())), "UTF-8"));
                object.setTasktype(TaskType.GET_ROUTES);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(TransferHome.this, TransferHome.this, TaskType.GET_ROUTES).execute(object);

            } else {
                CD.showDialog(TransferHome.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(TransferHome.this, "Something Bad happened . Please reinstall the application and try again.");
        }

    }

    private void makeDriverServiceCall() {
        // Get STAFF Service Call.. Acc to Depot
        try {
            if (AppStatus.getInstance(TransferHome.this).isOnline()) {
                UploadObject object = new UploadObject();

                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staff"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(oldDepotSelection.getId())), "UTF-8"));
                object.setTasktype(TaskType.GET_DRIVERS);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(TransferHome.this, TransferHome.this, TaskType.GET_DRIVERS).execute(object);

            } else {
                CD.showDialog(TransferHome.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(TransferHome.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }

    private void makeConductorServiceCall() {
        // Get STAFF Service Call.. Acc to Depot
        try {
            if (AppStatus.getInstance(TransferHome.this).isOnline()) {
                UploadObject object = new UploadObject();

                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staff"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(oldDepotSelection.getId())), "UTF-8"));
                object.setTasktype(TaskType.GET_CONDUCTORS);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(TransferHome.this, TransferHome.this, TaskType.GET_CONDUCTORS).execute(object);

            } else {
                CD.showDialog(TransferHome.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(TransferHome.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }

    private void makeVehicleServiceCall() {
        // Vehicles Service Call acc to Depot
        // Vehicles Service Call
        try {
            if (AppStatus.getInstance(TransferHome.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("vehicle"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(oldDepotSelection.getId()))));

                object.setTasktype(TaskType.GET_VEHICLES);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(TransferHome.this, TransferHome.this, TaskType.GET_VEHICLES).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(TransferHome.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(TransferHome.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }

    // Dialog for Transfer Confirmation for different entities
    private void transferConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Message for the dialog based on selected entity
        String message = "";
        if (selectedEntityStr.equalsIgnoreCase("Driver")) {
            message = "Are you sure you want to transfer this driver?";
        } else if (selectedEntityStr.equalsIgnoreCase("Conductor")) {
            message = "Are you sure you want to transfer this conductor?";
        } else if (selectedEntityStr.equalsIgnoreCase("Route")) {
            message = "Are you sure you want to transfer this route?";
        } else if (selectedEntityStr.equalsIgnoreCase("Vehicle")) {
            message = "Are you sure you want to transfer this vehicle?";
        }

        builder.setMessage(message)
                .setIcon(R.drawable.black_question)
                .setPositiveButton("Yes", (dialog, id) -> {

                    // Create the UploadObject
                    UploadObject object = new UploadObject();
                    object.setUrl(Econstants.base_url);
                    object.setAPI_NAME(Econstants.API_NAME_HRTC);
                    object.setMethordName("/master-data?masterName=");

                    // DRIVER
                    if (selectedEntityStr.equalsIgnoreCase("Driver")) {
                        object.setTasktype(TaskType.EDIT_DRIVER);
                        try {
                            object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staff"), "UTF-8")
                                    + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(driverToTransfer.getId()))));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Error", "Error: " + e.getMessage());
                        }
                        handleDriverTransfer(object);
                    }

                    // CONDUCTOR
                    else if (selectedEntityStr.equalsIgnoreCase("Conductor")) {
                        object.setTasktype(TaskType.EDIT_CONDUCTOR);
                        try {
                            object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staff"), "UTF-8")
                                    + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(conductorToTransfer.getId()))));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Error", "Error: " + e.getMessage());
                        }
                        handleConductorTransfer(object);
                    }

                    // ROUTE
                    else if (selectedEntityStr.equalsIgnoreCase("Route")) {
                        object.setTasktype(TaskType.EDIT_ROUTE);
                        try {
                            object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("route"), "UTF-8")
                                    + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(routeToTransfer.getRouteId()))));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Error", "Error: " + e.getMessage());
                        }
                        handleRouteTransfer(object);
                    }

                    // Vehicle
                    else if (selectedEntityStr.equalsIgnoreCase("Vehicle")) {

                        object.setTasktype(TaskType.EDIT_VEHICLE);
                        try {
                            object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("vehicle"), "UTF-8")
                                    + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(vehicleToTransfer.getId()))));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Error", "Error: " + e.getMessage());
                        }
                        handleVehicleTransfer(object);
                    }

                })
                .setNegativeButton("No", null); // Close dialog if No
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Handle Driver transfer
    private void handleDriverTransfer(UploadObject object) {
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("depot", newDepotSelection.getId());

            String encryptedParam = aesCrypto.encrypt(jsonParam.toString());
            object.setParam(encryptedParam);

            new ShubhAsyncPost(TransferHome.this, TransferHome.this, TaskType.EDIT_DRIVER).execute(object);

        } catch (Exception e) {
            Log.e("JSON Error", "Error while creating JSON object", e);
            Toast.makeText(TransferHome.this, "Error occurred while creating JSON data", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle Conductor transfer
    private void handleConductorTransfer(UploadObject object) {
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("depot", newDepotSelection.getId());

            String encryptedParam = aesCrypto.encrypt(jsonParam.toString());
            object.setParam(encryptedParam);

            new ShubhAsyncPost(TransferHome.this, TransferHome.this, TaskType.EDIT_CONDUCTOR).execute(object);

        } catch (Exception e) {
            Log.e("JSON Error", "Error while creating JSON object", e);
            Toast.makeText(TransferHome.this, "Error occurred while creating JSON data", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle Route transfer
    private void handleRouteTransfer(UploadObject object) {
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("depot", newDepotSelection.getId());

            String encryptedParam = aesCrypto.encrypt(jsonParam.toString());
            object.setParam(encryptedParam);

            Log.i("PARAMSSS", object.getUrl() + object.getMethordName() + object.getParam());
            new ShubhAsyncPost(TransferHome.this, TransferHome.this, TaskType.EDIT_ROUTE).execute(object);

        } catch (Exception e) {
            Log.e("JSON Error", "Error while creating JSON object", e);
            Toast.makeText(TransferHome.this, "Error occurred while creating JSON data", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle Vehicle transfer
    private void handleVehicleTransfer(UploadObject object) {
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("depot", newDepotSelection.getId());

            String encryptedParam = aesCrypto.encrypt(jsonParam.toString());
            object.setParam(encryptedParam);

            new ShubhAsyncPost(TransferHome.this, TransferHome.this, TaskType.EDIT_VEHICLE).execute(object);

        } catch (Exception e) {
            Log.e("JSON Error", "Error while creating JSON object", e);
            Toast.makeText(TransferHome.this, "Error occurred while creating JSON data", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onTaskCompleted(ResponsePojoGet result, TaskType taskType) throws JSONException {

        // Get depots
        if (TaskType.GET_DEPOTS == taskType) {
            SuccessResponse response = null;
            List<DepotPojo> pojoList = null;
            Log.i("BusDetails", "Task type is fetching vehicles..");

            if (result != null) {
                Log.i("Depots", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoList = JsonParse.parseDecryptedDepotsInfo(response.getData());
                        Log.i("pojoList", pojoList.toString());

                        if (pojoList.size() > 0) {
                            Log.e("Markers Size", String.valueOf(pojoList.size()));
                            Log.e("Reports Data", pojoList.toString());

                            depotSpinnerAdapter = new DepotSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            newDepotSpinner.setAdapter(depotSpinnerAdapter);
                            oldDepotSpinner.setAdapter(depotSpinnerAdapter);


                            // Preselect old depot if not Super ADMIN
                            Log.i("roleId: ", "roleId: " + Preferences.getInstance().roleId);

                            // Preselect + Lock Depot Spinner
                            oldDepotSpinner.post(() -> {
                                int oldItemPosition = depotSpinnerAdapter.getPositionForDepot(Preferences.getInstance().depotName, Preferences.getInstance().depotId);
                                if (oldItemPosition != -1) {
                                    oldDepotSpinner.setSelectedItemByIndex(oldItemPosition);
                                    // Unlocked for Super Admin and Admin only
                                    if(Preferences.getInstance().roleId == 1 || Preferences.getInstance().roleId == 2){
                                        oldDepotSpinner.setEnabled(true);
                                    } else{
                                        oldDepotSpinner.setEnabled(true);
                                    }
                                } else {
                                    Log.e("Error", "Item Position not found in adapter.");
                                }
                            });


                        } else {
                            CD.showDialog(TransferHome.this, "No Data Found");
                        }

                    } else {
                        CD.showDialog(TransferHome.this, response.getMessage());
                    }
                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(TransferHome.this, response.getMessage());
                }
            } else {
                CD.showDialog(TransferHome.this, "Result is null");
            }
        }

        // Get DRIVERS
        else if (TaskType.GET_DRIVERS == taskType) {
            SuccessResponse response = null;
            List<StaffPojo> pojoListDriver = new ArrayList<>();
            Log.i("AddBusDetails", "Task type is fetching drivers..");

            if (result != null) {
                Log.i("StaffDetails", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        pojoListDriver = JsonParse.parseDriversForSpinner(response.getData());

                        if (pojoListDriver.size() > 0) {
                            Log.e("Reports Data Driver", pojoListDriver.toString());

                            driverSpinnerAdapter = new DriverSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoListDriver);
                            driverSpinner.setAdapter(driverSpinnerAdapter);
                            driverSpinner.setVisibility(View.VISIBLE);

                        } else {
                            CD.showDialog(TransferHome.this, "No Drivers Found");
                            driverSpinner.setAdapter(null);
                            driverSpinner.setVisibility(View.GONE);
                        }

                    } else {
                        CD.showDialog(TransferHome.this, response.getMessage());
                    }
                } else {
                    CD.showDialog(TransferHome.this, "Not able to fetch data");
                }
            } else {
//                CD.showDialog(AddBusDetails.this, "Result is null");
            }
        }

        // Get Conductors
        else if (TaskType.GET_CONDUCTORS == taskType) {
            SuccessResponse response = null;
            List<StaffPojo> pojoListConductor = new ArrayList<>();
            Log.i("AddBusDetails", "Task type is fetching conductors..");

            if (result != null) {
                Log.i("StaffDetails", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        pojoListConductor = JsonParse.parseConductorsForSpinner(response.getData());

                        if (pojoListConductor.size() > 0) {
                            Log.e("Reports Data Conductor", pojoListConductor.toString());

                            conductorSpinnerAdapter = new ConductorSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoListConductor);
                            conductorSpinner.setAdapter(conductorSpinnerAdapter);
                            conductorSpinner.setVisibility(View.VISIBLE);

                        } else {
                            CD.showDialog(TransferHome.this, "No Conductors Found");
                            conductorSpinner.setAdapter(null);
                            conductorSpinner.setVisibility(View.GONE);
                        }

                    } else {
                        CD.showDialog(TransferHome.this, response.getMessage());
                    }
                } else {
                    CD.showDialog(TransferHome.this, "Not able to fetch data");
                }
            } else {
//                CD.showDialog(AddBusDetails.this, "Result is null");
            }
        }

        // Get Routes
        else if (TaskType.GET_ROUTES == taskType) {
            SuccessResponse response = null;
            List<RoutePojo> pojoList = null;
            Log.i("AddBusDetails", "Task type is fetching routes..");

            if (result != null) {
                Log.i("AddBusDetails", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoList = JsonParse.parseRoutes(response.getData());
                        Log.i("pojoList", pojoList.toString());

                        if (pojoList.size() > 0) {
                            Log.e("Markers Size", String.valueOf(pojoList.size()));
                            Log.e("Reports Data", pojoList.toString());

                            routeSpinnerAdapter = new RouteSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            routeSpinner.setAdapter(routeSpinnerAdapter);
                            routeSpinner.setVisibility(View.VISIBLE);
                            

                        } else {
                            CD.showDialog(TransferHome.this, "No Routes Found");
                            routeSpinner.setAdapter(null);
                            routeSpinner.setVisibility(View.GONE);
                        }

                    } else {
                        CD.showDialog(TransferHome.this, response.getMessage());
                    }
                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(TransferHome.this, "Not able to connect to the server");
                }
            } else {
//                CD.showDialog(AddBusDetails.this, "Result is null");
            }
        }

        // Get Vehicles
        else if (TaskType.GET_VEHICLES == taskType) {
            SuccessResponse response = null;
            List<VehiclePojo> pojoList = null;
            Log.i("BusDetails", "Task type is fetching vehicles..");

            if (result != null) {
                Log.i("Bus Details", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("1.Response", response.toString());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        Log.i("Data Array Response", response.getData());

                        pojoList = JsonParse.parseVehicleForSpinner(response.getData());

                        Log.i("pojoList", pojoList.toString());

                        if (pojoList.size() > 0) {
                            Log.e("Markers Size", String.valueOf(pojoList.size()));
                            Log.e("Reports Data", pojoList.toString());

                            vehicleSpinnerAdapter = new VehicleSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            vehicleNumberSpinner.setAdapter(vehicleSpinnerAdapter);
                            vehicleNumberSpinner.setVisibility(View.VISIBLE);

                        } else {
                            CD.showDialog(TransferHome.this, "No Vehicles Found");
                            vehicleNumberSpinner.setAdapter(null);
                            vehicleNumberSpinner.setVisibility(View.GONE);
                        }

                    } else {
                        CD.showDialog(TransferHome.this, response.getMessage());
                    }
                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                }
                else {
                    CD.showDialog(TransferHome.this, "Response is not OK");
                }
            } else {
                CD.showDialog(TransferHome.this, "Result is null");
            }
        }

        // Transfer Vehicle
        else if (TaskType.EDIT_VEHICLE == taskType) {
            Log.i("TransferVehicle", "Task Type is Vehicle Transfer");

            if (result != null) {
                SuccessResponse successResponse = null;
                successResponse = JsonParse.getSuccessResponse(result.getResponse());
                Log.i("TransferVehicle", successResponse.toString());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    // Transferred Successfully
                    Log.i("TransferVehicle", successResponse.getData());
                    Intent resultIntent = new Intent();
                    Log.i("From TransferVehicle", "Selected Vehicle Is Transferred.");
                    setResult(RESULT_OK, resultIntent);
                    CD.showDismissActivityDialog(this, successResponse.getMessage());
                }

                // depot or vehicle no found
                else if (successResponse.getStatus().equalsIgnoreCase("FAILED")) {
                    Log.e("TransferVehicle", successResponse.getMessage());
                    CD.showDismissActivityDialog(TransferHome.this, "Error: " + successResponse.getMessage());
                }
                else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                }

                else {
                    Log.i("TransferVehicle", "Response is null");
                    CD.showDialog(this, successResponse.getMessage());
                }

            } else {
                Log.i("TransferVehicle", "Response is null");
                CD.showDialog(this, "An error occurred. Please try again");
            }
        }

        // Transfer driver
        else if (TaskType.EDIT_DRIVER == taskType) {
            Log.i("TransferHome", "Task Type is Vehicle Transfer");

            if (result != null) {
                SuccessResponse successResponse = null;
                successResponse = JsonParse.getSuccessResponse(result.getResponse());
                Log.i("TransferHome", successResponse.toString());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    // Transferred Successfully
                    Log.i("TransferHome", successResponse.getData());
                    Intent resultIntent = new Intent();
                    Log.i("From TransferHome", "Selected Driver Is Transferred.");
                    setResult(RESULT_OK, resultIntent);
                    CD.showDismissActivityDialog(this, successResponse.getMessage());
                }

                // depot or vehicle no found
                else if (successResponse.getStatus().equalsIgnoreCase("FAILED")) {
                    Log.e("TransferHome", successResponse.getMessage());
                    CD.showDismissActivityDialog(TransferHome.this, "Error: " + successResponse.getMessage());
                }
                else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                }
                else {
                    Log.i("TransferHome", "Response is null");
                    CD.showDialog(this, successResponse.getMessage());
                }

            } else {
                Log.i("TransferHome", "Response is null");
                CD.showDialog(this, "An error occurred. Please try again");
            }
        }

        // Transfer
        else if (TaskType.EDIT_CONDUCTOR == taskType) {
            Log.i("TransferHome", "Task Type is Vehicle Transfer");

            if (result != null) {
                SuccessResponse successResponse = null;
                successResponse = JsonParse.getSuccessResponse(result.getResponse());
                Log.i("TransferHome", successResponse.toString());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    // Transferred Successfully
                    Log.i("TransferHome", successResponse.getData());
                    Intent resultIntent = new Intent();
                    Log.i("From TransferHome", "Selected Conductor Is Transferred.");
                    setResult(RESULT_OK, resultIntent);
                    CD.showDismissActivityDialog(this, successResponse.getMessage());
                }

                // depot or vehicle no found
                else if (successResponse.getStatus().equalsIgnoreCase("FAILED")) {
                    Log.e("TransferHome", successResponse.getMessage());
                    CD.showDismissActivityDialog(TransferHome.this, "Error: " + successResponse.getMessage());
                } else {
                    Log.i("TransferHome", "Response is null");
                    CD.showDialog(this, "result is null");
                }

            } else {
                Log.i("TransferHome", "Response is null");
                CD.showDialog(this, "An error occurred. Please try again");
            }
        }

        // Transfer Route
        else if (TaskType.EDIT_ROUTE == taskType) {
            Log.i("TransferHome", "Task Type is Route Transfer");

            if (result != null) {
                SuccessResponse successResponse = null;
                successResponse = JsonParse.getSuccessResponse(result.getResponse());
                Log.i("TransferHome", successResponse.toString());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    // Transferred Successfully
                    Log.i("TransferHome", successResponse.getData());
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    CD.showDismissActivityDialog(this, successResponse.getMessage());
                } else {
                    Log.i("TransferHome", "Response is null");
                    CD.showDialog(this, successResponse.getMessage());
                }

            } else {
                Log.i("TransferHome", "Response is null");
                CD.showDialog(this, "An error occurred. Please try again");
            }
        }


    }

}



