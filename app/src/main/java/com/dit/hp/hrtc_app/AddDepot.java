package com.dit.hp.hrtc_app;

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

public class AddDepot extends AppCompatActivity implements ShubhAsyncTaskListenerPost, ShubhAsyncTaskListenerGet {

    AESCrypto aesCrypto = new AESCrypto();

    Button back, proceed;
    EditText depotName, depotCode;
    TextView depotNameLabel, depotLocationLabel;
    String encryptedBody;

    CustomDialog CD = new CustomDialog();

    SearchableSpinner locationSpinner;
    LocationSpinnerAdapter locationSpinnerAdapter;
    LocationsPojo selectedLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_depot);

        depotNameLabel = findViewById(R.id.depotNameLabel);
        depotNameLabel.setText(Html.fromHtml("Depot Name <font color='#FF0000'>*</font>"));

        depotLocationLabel = findViewById(R.id.depotLocationLabel);
        depotLocationLabel.setText(Html.fromHtml("Depot Location <font color='#FF0000'>*</font>"));

        depotName = findViewById(R.id.depotName);
        depotCode = findViewById(R.id.depotCode);
        locationSpinner = findViewById(R.id.locationSpinner);

        back = findViewById(R.id.backBtn);
        proceed = findViewById(R.id.proceedBtn);

//        #################################### SERVICE CALL ########################################
        // Locations
        try {
            if (AppStatus.getInstance(AddDepot.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("location"), "UTF-8"));
                object.setTasktype(TaskType.GET_LOCATION);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddDepot.this, AddDepot.this, TaskType.GET_LOCATION).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddDepot.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddDepot.this, "Something Bad happened . Please reinstall the application and try again.");
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
            AddDepot.this.finish();
        });

        // Save Btn (Proceed Button)
        proceed.setOnClickListener(v -> {
            if (AppStatus.getInstance(AddDepot.this).isOnline()) {

                if (Econstants.isNotEmpty(depotName.getText().toString())) {

                    if (selectedLocation != null) {

                        showAddConfirmationDialog("Depot");

                    } else {
                        CD.showDialog(AddDepot.this, "Please select a depot location to proceed");
                    }
                } else {
                    CD.showDialog(AddDepot.this, "Enter a depot name to proceed");
                }
            } else {
                CD.showDialog(AddDepot.this, "Internet not Available. Please Connect to the Internet and try again.");
            }
        });

    }

    private void showAddConfirmationDialog(String selectedEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add " + selectedEntity)
                .setMessage("Are you sure you want to add this depot?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (AppStatus.getInstance(AddDepot.this).isOnline()) {

                        UploadObject uploadObject = new UploadObject();
                        // We can use Enums / Econstant to store these values of url and method names
                        try {
                            uploadObject.setUrl(Econstants.base_url);
                            uploadObject.setMethordName("/master-data?masterName=");
                            uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("depot"), "UTF-8"));
                            uploadObject.setTasktype(TaskType.ADD_DEPOT);
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

                        Log.i("JSON Params", "Enc Params" + encryptedBody);
                        Log.i("Object", "Complete Object: " + uploadObject.toString());
                        Log.e("URL: ", "URL: " + uploadObject.getUrl() + uploadObject.getMethordName() + uploadObject.getMasterName() + uploadObject.getParam());

                        new ShubhAsyncPost(AddDepot.this, AddDepot.this, TaskType.ADD_DEPOT).execute(uploadObject);

                    } else {
                        CD.addCompleteEntityDialog(AddDepot.this, "Internet not Available. Please Connect to the Internet and try again.");
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

                        } else {
                            CD.showDialog(AddDepot.this, "No Locations Found");
                        }

                    } else {
                        CD.showDialog(AddDepot.this, "No Routes Found");
                    }

                } else {
                    CD.showDialog(AddDepot.this, response.getMessage());
                }
            } else {
                CD.showDialog(AddDepot.this, "Not able to connect to the server");
            }
        }

        // Add
        else if (TaskType.ADD_DEPOT == taskType) {

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
                    CD.addCompleteEntityDialog(this, "Please connect to the internet");
                }
            } else {
                Log.i("AddDriver", "Response is null");
                CD.addCompleteEntityDialog(this, "Response is null.");
            }
        }


    }
}
