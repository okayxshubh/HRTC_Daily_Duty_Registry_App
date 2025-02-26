package com.dit.hp.hrtc_app;

import android.content.Intent;
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

import com.dit.hp.hrtc_app.Adapters.DepotSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.LocationSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.StaffSpinnerAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.AddaPojo;
import com.dit.hp.hrtc_app.Modals.DepotPojo;
import com.dit.hp.hrtc_app.Modals.LocationsPojo;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.StaffPojo;
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

import javax.net.ssl.HttpsURLConnection;

public class AddAdda extends AppCompatActivity implements ShubhAsyncTaskListenerPost, ShubhAsyncTaskListenerGet {

    AESCrypto aesCrypto = new AESCrypto();

    Button back, proceed;
    EditText addaName, remarks;
    String encryptedBody;
    TextView addaNameLabel, addaLocationLabel, titleTV;

    CustomDialog CD = new CustomDialog();

    SearchableSpinner locationSpinner, addaInchargeSpinner, depotSpinner;
    private DepotSpinnerAdapter depotSpinnerAdapter;
    private LocationSpinnerAdapter locationSpinnerAdapter;
    private StaffSpinnerAdapter staffSpinnerAdapter;

    LocationsPojo selectedLocation;
    DepotPojo selectedDepot;
    StaffPojo selectedAddaIncharge;

    AddaPojo receievedAddaDetailsToEdit;

    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_adda);

        // EDIT MODE
        isEditMode = getIntent().getBooleanExtra("isEditMode", false);


        addaNameLabel = findViewById(R.id.addaNameLabel);
        addaNameLabel.setText(Html.fromHtml("Adda Name <font color='#FF0000'>*</font>"));

        addaLocationLabel = findViewById(R.id.addaLocationLabel);
        addaLocationLabel.setText(Html.fromHtml("Adda Location <font color='#FF0000'>*</font>"));

        titleTV = findViewById(R.id.titleTextView);
        addaName = findViewById(R.id.addaName);
        locationSpinner = findViewById(R.id.locationSpinner);
        addaInchargeSpinner = findViewById(R.id.addaInchargeSpinner);
        depotSpinner = findViewById(R.id.depotSpinner);
        remarks = findViewById(R.id.remarks);

        back = findViewById(R.id.backBtn);
        proceed = findViewById(R.id.proceedBtn);

//        #################################### SERVICE CALL ########################################
        // Locations
        try {
            if (AppStatus.getInstance(AddAdda.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("location"), "UTF-8"));
                object.setTasktype(TaskType.GET_LOCATION);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddAdda.this, AddAdda.this, TaskType.GET_LOCATION).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddAdda.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddAdda.this, "Something Bad happened . Please reinstall the application and try again.");
        }

        // Depot Service Call
        try {
            if (AppStatus.getInstance(AddAdda.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("depot"), "UTF-8"));
                object.setTasktype(TaskType.GET_DEPOTS);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddAdda.this, AddAdda.this, TaskType.GET_DEPOTS).execute(object);

            } else {
                CD.showDialog(AddAdda.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddAdda.this, "Something Bad happened . Please reinstall the application and try again.");
        }

//        ##########################################################################################

        if (isEditMode) {
            titleTV.setText("Edit Adda");
            proceed.setText("Update");
            receievedAddaDetailsToEdit = (AddaPojo) getIntent().getSerializableExtra("addaDetails");
            // PRESELECT
            addaName.setText(receievedAddaDetailsToEdit.getAddaName());
            remarks.setText(receievedAddaDetailsToEdit.getRemarks());

        } else {
            titleTV.setText("Add Adda");
            proceed.setText("Proceed");
        }


        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLocation = (LocationsPojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Make depot locked for all other employees
        if(Preferences.getInstance().roleId == 1 || Preferences.getInstance().roleId == 2){
            depotSpinner.setEnabled(true);
        } else {
            depotSpinner.setEnabled(false);
        }

        depotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDepot = (DepotPojo) parent.getItemAtPosition(position);

                // CALL STAFF ACC TO SELECTED DEPOT
                makeStaffServiceCall(selectedDepot.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addaInchargeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAddaIncharge = (StaffPojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Back Btn
        back.setOnClickListener(v -> {
            AddAdda.this.finish();
        });

        // Save Btn (Proceed Button)
        proceed.setOnClickListener(v -> {
            if (AppStatus.getInstance(AddAdda.this).isOnline()) {
                if (Econstants.isNotEmpty(addaName.getText().toString())) {
                    if (selectedLocation != null) {
                        if (selectedDepot != null) {
                            if (selectedAddaIncharge != null) {

                                if (isEditMode) {
                                    showEditConfirmationDialog("Adda");
                                } else {
                                    showAddConfirmationDialog("Adda");
                                }

                            } else {
                                CD.showDialog(AddAdda.this, "Please select an adda incharge to proceed");
                            }
                        } else {
                            CD.showDialog(AddAdda.this, "Please select adda location to proceed");
                        }
                    } else {
                        CD.showDialog(AddAdda.this, "Please select adda location to proceed");
                    }
                } else {
                    CD.showDialog(AddAdda.this, "Enter a adda name to proceed");
                }
            } else {
                CD.showDialog(AddAdda.this, "Internet not Available. Please Connect to the Internet and try again.");
            }
        });


    }

    private void makeStaffServiceCall(int id) {
        // Get STAFF Service Call.. Acc to Depot
        try {
            if (AppStatus.getInstance(AddAdda.this).isOnline()) {
                UploadObject object = new UploadObject();

                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staff"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(id)), "UTF-8"));
                object.setTasktype(TaskType.GET_STAFF);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddAdda.this, AddAdda.this, TaskType.GET_STAFF).execute(object);

            } else {
                CD.showDialog(AddAdda.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddAdda.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }


    private void showAddConfirmationDialog(String selectedEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add " + selectedEntity)
                .setMessage("Are you sure you want to add this adda?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (AppStatus.getInstance(AddAdda.this).isOnline()) {

                        UploadObject uploadObject = new UploadObject();
                        // We can use Enums / Econstant to store these values of url and method names
                        try {
                            uploadObject.setUrl(Econstants.base_url);
                            uploadObject.setMethordName("/master-data?masterName=");
                            uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("adda"), "UTF-8"));
                            uploadObject.setTasktype(TaskType.ADD_ADDA);
                            uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // Creating JSON to store in param
                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("addaName", addaName.getText().toString());
                            jsonObject.put("location", selectedLocation.getId());
                            jsonObject.put("staff", selectedAddaIncharge.getId());
                            jsonObject.put("depot", selectedDepot.getId());
                            jsonObject.put("remarks", remarks.getText().toString());
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

                        Log.i("Object", "Complete Object: " + uploadObject.toString());
                        Log.e("URL: ", "URL: " + uploadObject.getUrl() + uploadObject.getMethordName() + uploadObject.getMasterName() + uploadObject.getParam());

                        new ShubhAsyncPost(AddAdda.this, AddAdda.this, TaskType.ADD_ADDA).execute(uploadObject);

                    } else {
                        CD.addCompleteEntityDialog(AddAdda.this, "Internet not Available. Please Connect to the Internet and try again.");
                    }

                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showEditConfirmationDialog(String selectedEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add " + selectedEntity)
                .setMessage("Are you sure you want to edit this adda?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (AppStatus.getInstance(AddAdda.this).isOnline()) {

                        UploadObject uploadObject = new UploadObject();
                        // We can use Enums / Econstant to store these values of url and method names
                        try {
                            uploadObject.setUrl(Econstants.base_url);
                            uploadObject.setMethordName("/master-data?masterName=");
                            uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("adda"), "UTF-8")
                                    + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(receievedAddaDetailsToEdit.getId()))));
                            uploadObject.setTasktype(TaskType.EDIT_ADDA);
                            uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // Creating JSON to store in param
                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("addaName", addaName.getText().toString());
                            jsonObject.put("location", selectedLocation.getId());
                            jsonObject.put("staff", selectedAddaIncharge.getId());
                            jsonObject.put("depot", selectedDepot.getId());
                            jsonObject.put("remarks", remarks.getText().toString());
                            jsonObject.put("updateBy", Preferences.getInstance().empId);

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

                        Log.i("Object", "Complete Object: " + uploadObject.toString());
                        Log.e("URL: ", "URL: " + uploadObject.getUrl() + uploadObject.getMethordName() + uploadObject.getMasterName() + uploadObject.getParam());

                        new ShubhAsyncPost(AddAdda.this, AddAdda.this, TaskType.EDIT_ADDA).execute(uploadObject);

                    } else {
                        CD.addCompleteEntityDialog(AddAdda.this, "Internet not Available. Please Connect to the Internet and try again.");
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

                            locationSpinnerAdapter = new LocationSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoListLocation);
                            locationSpinner.setAdapter(locationSpinnerAdapter);


                            // PRESELECT SPINNER
                            if (isEditMode && receievedAddaDetailsToEdit != null){
                                // PRESELECT LOCATION
                                locationSpinner.post(() -> {
                                    int defaultItemPosition = locationSpinnerAdapter.getPositionForLocation(receievedAddaDetailsToEdit.getLocation().getId());
                                    // Set the spinner to the default position if valid
                                    if (defaultItemPosition != -1) {
                                        locationSpinner.setSelectedItemByIndex(defaultItemPosition);
                                    } else {
                                        Log.e("Error", "Location not found in adapter.");
                                    }
                                });
                            }


                        } else {
                            CD.showDialog(AddAdda.this, "No Locations Found");
                        }

                    } else {
                        CD.showDialog(AddAdda.this, "No Routes Found");
                    }

                } else {
                    CD.showDialog(AddAdda.this, response.getMessage());
                }
            } else {
                CD.showDialog(AddAdda.this, "Not able to connect to the server");
            }
        }

        // Get depots
        else if (TaskType.GET_DEPOTS == taskType) {
            SuccessResponse response = null;
            List<DepotPojo> pojoList = null;
            Log.i("BusDetails", "Task type is fetching vehicles..");

            if (responseObject != null) {
                Log.i("Depots", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoList = JsonParse.parseDecryptedDepotsInfo(response.getData());
                        Log.i("pojoList", pojoList.toString());

                        if (pojoList.size() > 0) {
                            Log.e("Markers Size", String.valueOf(pojoList.size()));
                            Log.e("Reports Data", pojoList.toString());

                            depotSpinnerAdapter = new DepotSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            depotSpinner.setAdapter(depotSpinnerAdapter);


                            // PRESELECT SPINNER
                            if (isEditMode && receievedAddaDetailsToEdit != null){
                                // Preselect
                                depotSpinner.post(() -> {
                                    int oldItemPosition = depotSpinnerAdapter.getPositionForDepot(receievedAddaDetailsToEdit.getDepot().getDepotName(), receievedAddaDetailsToEdit.getDepot().getId());
                                    if (oldItemPosition != -1) {
                                        depotSpinner.setSelectedItemByIndex(oldItemPosition);
                                    } else {
                                        Log.e("Error", "Item Position not found in adapter.");
                                    }
                                });
                            }


                        } else {
                            CD.showDialog(AddAdda.this, "No Data Found");
                        }

                    } else {
                        CD.showDialog(AddAdda.this, response.getMessage());
                    }
                } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(AddAdda.this, response.getMessage());
                }
            } else {
                CD.showDialog(AddAdda.this, "Result is null");
            }
        }

        // Get Staff
        else if (TaskType.GET_STAFF == taskType) {
            SuccessResponse response = null;
            List<StaffPojo> pojoListStaff = new ArrayList<>();
            Log.i("AddBusDetails", "Task type is fetching drivers..");

            if (responseObject != null) {
                Log.i("StaffDetails", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        pojoListStaff = JsonParse.parseStaffForSpinner(response.getData());

                        if (pojoListStaff.size() > 0) {
                            Log.e("Reports Data Driver", pojoListStaff.toString());
                            staffSpinnerAdapter = new StaffSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoListStaff);
                            addaInchargeSpinner.setAdapter(staffSpinnerAdapter);

                            // PRESELECT SPINNER
                            if (isEditMode && receievedAddaDetailsToEdit != null){
                                // Preselect
                                if (receievedAddaDetailsToEdit.getAddaIncharge() != null){
                                    addaInchargeSpinner.post(() -> {
                                        int oldItemPosition = staffSpinnerAdapter.getPositionForStaff(receievedAddaDetailsToEdit.getAddaIncharge().getName(), receievedAddaDetailsToEdit.getAddaIncharge().getId());
                                        if (oldItemPosition != -1) {
                                            addaInchargeSpinner.setSelectedItemByIndex(oldItemPosition);
                                        } else {
                                            Log.e("Error", "Item Position not found in adapter.");
                                        }
                                    });
                                }
                            }

                        } else {
                            CD.showDialog(AddAdda.this, "No Staff Found");
                            addaInchargeSpinner.setAdapter(null);
                        }

                    } else {
                        CD.showDialog(AddAdda.this, response.getMessage());
                    }
                } else {
                    CD.showDialog(AddAdda.this, "Not able to fetch data");
                }
            } else {
//                CD.showDialog(AddBusDetails.this, "Result is null");
            }
        }

        // Add
        else if (TaskType.ADD_ADDA == taskType) {

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
                    CD.addCompleteEntityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
                } else {
                    CD.addCompleteEntityDialog(this, "Something went wrong.");
                }
            } else {
                Log.i("AddDriver", "Response is null");
                CD.addCompleteEntityDialog(this, "Response is null.");
            }
        }

        /// EDIT
        else if (TaskType.EDIT_ADDA == taskType) {
            Log.i("ASYNC TASK COMPLETED", "TASK TYPE IS Adding Entity");
            SuccessResponse successResponse = null;

            // responseObject will be null if invalid id pass
            if (responseObject != null) {
                successResponse = JsonParse.getSuccessResponse(responseObject.getResponse());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    Log.i("Add Entity Response", successResponse.getData());

                    Intent resultIntent = new Intent();
                    Log.i("From Edit Adda", "Your data is updated.");
                    setResult(RESULT_OK, resultIntent);

                    CD.showAddaEditCompleteDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
                }

                // Depot has dependencies under it
                else if (successResponse.getStatus().equalsIgnoreCase("ERROR")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.showAddaEditCompleteDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
                }
                else {
                    CD.showDismissActivityDialog(this, "Something went wrong");
                }
            } else {
                Log.i("AddDriver", "Response is null");
                CD.showDismissActivityDialog(this, "Response is null.");
            }
        }



    }
}
