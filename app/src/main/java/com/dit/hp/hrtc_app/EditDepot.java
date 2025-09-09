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

import com.dit.hp.hrtc_app.Adapters.LocationSpinnerAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.DepotPojo;
import com.dit.hp.hrtc_app.Modals.LocationsPojo;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
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

public class EditDepot extends AppCompatActivity implements ShubhAsyncTaskListenerPost, ShubhAsyncTaskListenerGet {

    AESCrypto aesCrypto = new AESCrypto();

    Button back, proceed;
    EditText depotName, depotCode;
    TextView depotNameLabel, depotLocationLabel;

    String encryptedBody;

    CustomDialog CD = new CustomDialog();

    SearchableSpinner locationSpinner;
    LocationSpinnerAdapter locationSpinnerAdapter;
    LocationsPojo selectedLocation;
    DepotPojo selectedDepotToEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_depot);

        depotNameLabel = findViewById(R.id.depotNameLabel);
        depotNameLabel.setText(Html.fromHtml("Depot Name <font color='#FF0000'>*</font>"));

        depotLocationLabel = findViewById(R.id.depotLocationLabel);
        depotLocationLabel.setText(Html.fromHtml("Depot Location <font color='#FF0000'>*</font>"));

        // GEt selected Depot
        Intent getIntent = getIntent();
        selectedDepotToEdit = (DepotPojo) getIntent.getSerializableExtra("DepotInfo");

        depotCode = findViewById(R.id.depotCode);
        depotName = findViewById(R.id.depotName);
        locationSpinner = findViewById(R.id.locationSpinner);

        back = findViewById(R.id.backBtn);
        proceed = findViewById(R.id.proceedBtn);

        // Preselect Items
        if (selectedDepotToEdit != null){
            depotCode.setText(selectedDepotToEdit.getDepotCode());
            depotName.setText(selectedDepotToEdit.getDepotName());
        }

//        #################################### SERVICE CALL ########################################
        // Locations + Stops Service Call
        try {
            if (AppStatus.getInstance(EditDepot.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("location"), "UTF-8"));
                object.setTasktype(TaskType.GET_LOCATION);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(EditDepot.this, EditDepot.this, TaskType.GET_LOCATION).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(EditDepot.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(EditDepot.this, "Something Bad happened . Please reinstall the application and try again.");
        }
//        ##########################################################################################


        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLocation = (LocationsPojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Back Btn
        back.setOnClickListener(v -> {
            EditDepot.this.finish();
        });

        // Save Btn (Proceed Button)
        proceed.setOnClickListener(v -> {
            if (AppStatus.getInstance(EditDepot.this).isOnline()) {

                if (Econstants.isNotEmpty(depotName.getText().toString())) {

                    if (selectedLocation != null) {

                        showAddConfirmationDialog("Depot");

                    } else {
                        CD.showDialog(EditDepot.this, "Please select a depot location to proceed");
                    }
                } else {
                    CD.showDialog(EditDepot.this, "Enter a depot name to proceed");
                }
            } else {
                CD.showDialog(EditDepot.this, "Internet not Available. Please Connect to the Internet and try again.");
            }
        });

    }

    private void showAddConfirmationDialog(String selectedEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit " + selectedEntity)
                .setMessage("Are you sure you want to edit this depot?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    if (AppStatus.getInstance(EditDepot.this).isOnline()) {
                        // We can use Enums / Econstant to store these values of url and method names
                        UploadObject uploadObject = new UploadObject();

                        try {
                            uploadObject.setUrl(Econstants.base_url);
                            uploadObject.setMethordName("/master-data?masterName=");

                            uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("depot"), "UTF-8")
                                    + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(selectedDepotToEdit.getId()))));

                            uploadObject.setTasktype(TaskType.EDIT_DEPOT);
                            uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // Creating JSON to store in param
                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("depotName", depotName.getText().toString());
                            jsonObject.put("depotCode", depotCode.getText().toString());
                            jsonObject.put("location", selectedLocation.getId());
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
                        Log.i("JSON Params", "Enc Params" + encryptedBody);
                        Log.i("Object", "Complete Object: " + uploadObject.toString());

                        new ShubhAsyncPost(EditDepot.this, EditDepot.this, TaskType.EDIT_DEPOT).execute(uploadObject);

                    } else {
                        CD.addCompleteEntityDialog(EditDepot.this, "Internet not Available. Please Connect to the Internet and try again.");
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

                            if (selectedDepotToEdit!= null){
                                // PRESELECT LOCATION
                                locationSpinner.post(() -> {
                                    int defaultItemPosition = locationSpinnerAdapter.getPositionForLocation(selectedDepotToEdit.getLocation().getId());
                                    // Set the spinner to the default position if valid
                                    if (defaultItemPosition != -1) {
                                        locationSpinner.setSelectedItemByIndex(defaultItemPosition);
                                    } else {
                                        Log.e("Error", "Location not found in adapter.");
                                    }
                                });
                            }


                        } else {
                            CD.showDialog(EditDepot.this, "No Locations Found");
                        }

                    } else {
                        CD.showDialog(EditDepot.this, "No Routes Found");
                    }

                } else {
                    CD.showDialog(EditDepot.this, response.getMessage());
                }
            } else {
                CD.showDialog(EditDepot.this, "Not able to connect to the server");
            }
        }


        else if (TaskType.EDIT_DEPOT == taskType) {
            Log.i("ASYNC TASK COMPLETED", "TASK TYPE IS Adding Entity");
            SuccessResponse successResponse = null;

            // responseObject will be null if invalid id pass
            if (responseObject != null) {
                successResponse = JsonParse.getSuccessResponse(responseObject.getResponse());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    Log.i("Add Entity Response", successResponse.getData());

                    Intent resultIntent = new Intent();
                    Log.i("From Edit Depot", "Your data is updated.");
                    setResult(RESULT_OK, resultIntent);

                    CD.showDepotEditCompleteDialog(this, successResponse.getMessage()); // Dialog that dismisses activity

                }

                // Depot has dependencies under it
                else if (successResponse.getStatus().equalsIgnoreCase("ERROR")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.showDepotEditCompleteDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
                }
                else {
                    CD.addCompleteEntityDialog(this, "Please connect to the internet");
                }
            } else {
                Log.i("AddDriver", "Response is null");
                CD.addCompleteEntityDialog(this, "Response is null.");
            }
        }




    }
}
