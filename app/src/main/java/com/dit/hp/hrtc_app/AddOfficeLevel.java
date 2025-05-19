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
import androidx.recyclerview.widget.RecyclerView;

import com.dit.hp.hrtc_app.Adapters.LocationSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.OfficeLevelAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.LocationsPojo;
import com.dit.hp.hrtc_app.Modals.OfficeLevel;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.StopPojo;
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

public class AddOfficeLevel extends AppCompatActivity implements ShubhAsyncTaskListenerPost, ShubhAsyncTaskListenerGet {

    AESCrypto aesCrypto = new AESCrypto();

    Button back, proceed;
    EditText officeLevelName;
    TextView depotNameLabel, depotLocationLabel;
    String encryptedBody;
    private List<OfficeLevel> completePojoList;
    RecyclerView officeLevelRecyclerView;


    CustomDialog CD = new CustomDialog();

    SearchableSpinner departmentSpinner;
    OfficeLevelAdapter officeLevelAdapter;
    LocationsPojo selectedLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_office_level);

        Preferences.getInstance().loadPreferences(this);

//        depotNameLabel = findViewById(R.id.depotNameLabel);
//        depotNameLabel.setText(Html.fromHtml("Depot Name <font color='#FF0000'>*</font>"));
//        depotLocationLabel = findViewById(R.id.depotLocationLabel);
//        depotLocationLabel.setText(Html.fromHtml("Depot Location <font color='#FF0000'>*</font>"));


        officeLevelName = findViewById(R.id.officeLevelET);
        departmentSpinner = findViewById(R.id.departmentNameSpinner);

        officeLevelRecyclerView = findViewById(R.id.officeLevelRecyclerView);

        List <OfficeLevel> dummyOfficeLevels = new ArrayList<>();
        dummyOfficeLevels.add(new OfficeLevel(1, "Office Level 1", "Department 1"));
        dummyOfficeLevels.add(new OfficeLevel(2, "Office Level 2", "Department 1"));
        dummyOfficeLevels.add(new OfficeLevel(3, "Office Level 3", "Department 1"));
        populateItemList(dummyOfficeLevels);// DUMMY ITEMS


        back = findViewById(R.id.backBtn);
        proceed = findViewById(R.id.proceedBtn);

//        #################################### SERVICE CALL ########################################
//        // Locations
//        try {
//            if (AppStatus.getInstance(AddOfficeLevel.this).isOnline()) {
//
//                UploadObject object = new UploadObject();
//                object.setUrl(Econstants.base_url);
//                object.setMethordName("/master-data?");
//                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("location"), "UTF-8"));
//                object.setTasktype(TaskType.GET_LOCATION);
//                object.setAPI_NAME(Econstants.API_NAME_HRTC);
//
//                new ShubhAsyncGet(AddOfficeLevel.this, AddOfficeLevel.this, TaskType.GET_LOCATION).execute(object);
//
//            } else {
//                // Do nothing if CD already shown once
//                CD.showDialog(AddOfficeLevel.this, Econstants.internetNotAvailable);
//            }
//        } catch (Exception ex) {
//            CD.showDialog(AddOfficeLevel.this, "Something Bad happened . Please reinstall the application and try again.");
//        }
//        ##########################################################################################

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
            AddOfficeLevel.this.finish();
        });

        // Save Btn (Proceed Button)
        proceed.setOnClickListener(v -> {
            if (AppStatus.getInstance(AddOfficeLevel.this).isOnline()) {

                if (Econstants.isNotEmpty(officeLevelName.getText().toString())) {

//                    if (selectedLocation != null) {

                        showAddConfirmationDialog("Depot");

//                    } else {
//                        CD.showDialog(AddOfficeLevel.this, "Please select a depot location to proceed");
//                    }
                } else {
                    CD.showDialog(AddOfficeLevel.this, "Enter a depot name to proceed");
                }
            } else {
                CD.showDialog(AddOfficeLevel.this, "Internet not Available. Please Connect to the Internet and try again.");
            }
        });
    }

    private void showAddConfirmationDialog(String selectedEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add " + selectedEntity)
                .setMessage("Are you sure you want to add this office level?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (AppStatus.getInstance(AddOfficeLevel.this).isOnline()) {

                        UploadObject uploadObject = new UploadObject();
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
                            jsonObject.put("officeLevel", officeLevelName.getText().toString());
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

                        new ShubhAsyncPost(AddOfficeLevel.this, AddOfficeLevel.this, TaskType.ADD_DEPOT).execute(uploadObject);

                    } else {
                        CD.addCompleteEntityDialog(AddOfficeLevel.this, "Internet not Available. Please Connect to the Internet and try again.");
                    }

                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Preselect the route stops in the stop list
    private void populateItemList(List<OfficeLevel> itemList) {
        // Clear existing stops if needed
        completePojoList.clear();
        // Add fetched stops to the completePojoList
        for (int i = 0; i < itemList.size(); i++) {
            OfficeLevel item = itemList.get(i);
            completePojoList.add(item);
        }
        // Notify the adapter about data changes
        officeLevelAdapter.notifyDataSetChanged();
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


                        } else {
                            CD.showDialog(AddOfficeLevel.this, "No Locations Found");
                        }

                    } else {
                        CD.showDialog(AddOfficeLevel.this, "No Routes Found");
                    }

                } else {
                    CD.showDialog(AddOfficeLevel.this, response.getMessage());
                }
            } else {
                CD.showDialog(AddOfficeLevel.this, "Not able to connect to the server");
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


    @Override
    protected void onRestart() {
        super.onRestart();
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
    }

    @Override
    protected void onResume() {
        super.onResume();
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
    }
}
