package com.dit.hp.hrtc_app;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dit.hp.hrtc_app.Adapters.VehicleTypeSpinnerAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Modals.VehicleTypePojo;
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

public class AddVehicle extends AppCompatActivity implements ShubhAsyncTaskListenerGet, ShubhAsyncTaskListenerPost {

    AESCrypto aesCrypto = new AESCrypto();

    Button back, proceed;
    EditText newVehicleNumber, otherDetails, vehicleCapacity;
    TextView vehicleNoLabel, vehicleTypeLabel, firmLabel, makeNModelLabel, vehicleCapacityLabel;

    String[] vehicleTypeArr = new String[3];
    SearchableSpinner vehicleTypeSpinner;
    String vehicleNumberStr;
    String selectedTypeStr;
    String vehicleCapacityStr;
    EditText firmNameET, modelNameET;
    VehicleTypePojo selectedVehicleTypePojo;

    VehicleTypeSpinnerAdapter vehicleTypeSpinnerAdapter;

    String encryptedBody;
    CustomDialog CD = new CustomDialog();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        newVehicleNumber = findViewById(R.id.newVehicleNumber);

        vehicleTypeSpinner = findViewById(R.id.vehicleTypeSpinner);

        vehicleNoLabel = findViewById(R.id.vehicleNoLabel);
        vehicleNoLabel.setText(Html.fromHtml("New Vehicle Number <font color='#FF0000'>*</font>"));

        vehicleTypeLabel = findViewById(R.id.vehicleTypeLabel);
        vehicleTypeLabel.setText(Html.fromHtml("Type of Vehicle <font color='#FF0000'>*</font>"));

        firmLabel = findViewById(R.id.firmLabel);
        firmLabel.setText(Html.fromHtml("Name of firm by which the VLT device is installed in the vehicle <font color='#FF0000'>*</font>"));

        makeNModelLabel = findViewById(R.id.makeAndModelLabel);
        makeNModelLabel.setText(Html.fromHtml("Make and Model <font color='#FF0000'>*</font>"));

        vehicleCapacityLabel = findViewById(R.id.vehicleCapacityLabel);
        vehicleCapacityLabel.setText(Html.fromHtml("Vehicle Capacity <font color='#FF0000'>*</font>"));

        vehicleCapacity = findViewById(R.id.vehicleCapacity);
        vehicleCapacityStr = vehicleCapacity.getText().toString();

        firmNameET = findViewById(R.id.firmNameET);
        modelNameET = findViewById(R.id.modelNameET);

        back = findViewById(R.id.backBtn);
        proceed = findViewById(R.id.proceedBtn);

//        SERVICE CALLS ##################################################################################################################

        // Vehicle type service Call
        try {
            if (AppStatus.getInstance(AddVehicle.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("vehicleType"), "UTF-8"));
                object.setTasktype(TaskType.GET_VEHICLE_TYPE);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddVehicle.this, AddVehicle.this, TaskType.GET_VEHICLE_TYPE).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddVehicle.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddVehicle.this, "Something Bad happened . Please reinstall the application and try again.");
        }

//        ###############################################################################################################################

        vehicleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVehicleTypePojo = (VehicleTypePojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Capitalize Vehicle Number
        newVehicleNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();

                // Remove special characters and convert to uppercase
                String filteredInput = input.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();

                // If the filtered input changes, update the EditText and set the cursor at the end
                if (!input.equals(filteredInput)) {
                    newVehicleNumber.setText(filteredInput);
                    newVehicleNumber.setSelection(filteredInput.length()); // Maintain the cursor at the end
                }
            }
        });


        // Back Btn
        back.setOnClickListener(v -> {
            showExitConfirmationDialog();
        });

        // Save Btn (Proceed Button)
        proceed.setOnClickListener(v -> {
            if (AppStatus.getInstance(AddVehicle.this).isOnline()) {
                // Get the text from the TextView
                vehicleNumberStr = newVehicleNumber.getText().toString().trim();

                if (selectedVehicleTypePojo != null) {

                    if (Econstants.isNotEmpty(vehicleNumberStr) && vehicleNumberStr.length() > 5) {

                        if (Econstants.isNotEmpty(vehicleCapacity.getText().toString())) {

                            if (Econstants.isNotEmpty(firmNameET.getText().toString())) {

                                if (Econstants.isNotEmpty(modelNameET.getText().toString())) {

                                    // Show confirmation dialog
                                    showAddConfirmationDialog("Vehicle");

                                } else {
                                    CD.showDialog(AddVehicle.this, "Please enter model of the vehicle.");
                                }

                            } else {
                                CD.showDialog(AddVehicle.this, "Please enter the name of firm by which the VLT device is installed in the vehicle");
                            }
                        } else {
                            CD.showDialog(AddVehicle.this, "Please enter vehicle capacity.");
                        }

                    } else {
                        CD.showDialog(AddVehicle.this, "Please enter a vehicle number.");
                    }

                } else {
                    CD.showDialog(AddVehicle.this, "Please select vehicle type.");
                }
            } else {
                CD.showDialog(AddVehicle.this, "Internet not available. Please connect to the Internet and try again.");
            }
        });
    }

    private void populateSpinner(Spinner spinner, List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    private void showAddConfirmationDialog(String selectedEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add " + selectedEntity)
                .setMessage("Are you sure you want to add the selected " + selectedEntity + " to the office " + Preferences.getInstance().regionalOfficeName + "?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    if (AppStatus.getInstance(AddVehicle.this).isOnline()) {

                        UploadObject uploadObject = new UploadObject();
                        // We can use Enums / Econstant to store these values of url and method names
                        try {
                            uploadObject.setUrl(Econstants.base_url);
                            uploadObject.setMethordName("/master-data?masterName=");
                            uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("vehicle"), "UTF-8"));
                            uploadObject.setTasktype(TaskType.ADD_VEHICLE);
                            uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // Creating JSON to store in param
                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("vehicleType", selectedVehicleTypePojo.getVehicleTypeId());
                            jsonObject.put("vehicleNumber", vehicleNumberStr);

                            jsonObject.put("vehicleModel", modelNameET.getText().toString().trim());
                            jsonObject.put("iotFirm", firmNameET.getText().toString().trim());
                            jsonObject.put("capacity", Integer.parseInt(vehicleCapacity.getText().toString().trim()));

                            jsonObject.put("depot", Preferences.getInstance().regionalOfficeId);
                            jsonObject.put("createdBy", Preferences.getInstance().emailID);

                            Log.i("AddVehicle", "Vehicle to add: " + jsonObject.toString());

                        } catch (JSONException e) {
                            Log.e("JSON Exception", e.getMessage());
                        }

                        try {
                            encryptedBody = aesCrypto.encrypt(jsonObject.toString());
                        } catch (Exception e) {
                            Log.e("Encryption Error", e.getMessage());
                        }

                        uploadObject.setParam(encryptedBody);
                        Log.i("Object", "Complete Object: " + uploadObject.toString());
                        new ShubhAsyncPost(AddVehicle.this, AddVehicle.this, TaskType.ADD_VEHICLE).execute(uploadObject);

                    } else {
                        CD.showDialog(AddVehicle.this, "Internet not Available. Please Connect to the Internet and try again.");
                    }

                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Exit confirmation dialog
    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit? All progress made will be lost.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddVehicle.this.finish();
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
    public void onTaskCompleted(ResponsePojoGet responseObject, TaskType taskType) throws JSONException {

        // Add Vehicle Service call
        if (TaskType.ADD_VEHICLE == taskType) {
            Log.i("ASYNC TASK COMPLETED", "TASK TYPE IS Adding Entity");
            SuccessResponse successResponse = null;

            if (responseObject != null) {
                successResponse = JsonParse.getSuccessResponse(responseObject.getResponse());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.addCompleteEntityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
                } else if (successResponse.getStatus().equalsIgnoreCase("ERROR")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.addCompleteEntityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
                } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.addCompleteEntityDialog(this, "Please connect to the internet");
                }
            } else {
                CD.showDialog(this, "Please check your connection ");
            }
        } else if (TaskType.GET_VEHICLE_TYPE == taskType) {
            SuccessResponse response = null;
            List<VehicleTypePojo> pojoList = new ArrayList<>();

            if (responseObject != null) {
                Log.i("StaffDetails", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        pojoList = JsonParse.parseVehicleType(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data Vehicles", pojoList.toString());

                            vehicleTypeSpinnerAdapter = new VehicleTypeSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            vehicleTypeSpinner.setAdapter(vehicleTypeSpinnerAdapter);
                        }
                    } else {
                        CD.showDialog(AddVehicle.this, response.getMessage());
                    }
                } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(AddVehicle.this, "Not able to fetch gender");
                }
            } else {
                CD.showDialog(AddVehicle.this, "Result is null");
            }
        }
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }
}







