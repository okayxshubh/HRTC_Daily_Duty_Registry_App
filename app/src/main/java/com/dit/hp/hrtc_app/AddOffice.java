package com.dit.hp.hrtc_app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dit.hp.hrtc_app.Adapters.BlockSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.DepartmentSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.DesignationSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.DistrictSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.MunicipalSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.OfficeLevelSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.OfficeSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.PanchayatSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.VillageSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.WardSpinnerAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.DepartmentPojo;
import com.dit.hp.hrtc_app.Modals.DesignationPojo;
import com.dit.hp.hrtc_app.Modals.DistrictPojo;
import com.dit.hp.hrtc_app.Modals.LocationsPojo;
import com.dit.hp.hrtc_app.Modals.OfficeLevel;
import com.dit.hp.hrtc_app.Modals.OfficePojo;
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

public class AddOffice extends AppCompatActivity implements ShubhAsyncTaskListenerPost, ShubhAsyncTaskListenerGet {

    AESCrypto aesCrypto = new AESCrypto();

    Button back, proceed;
    EditText departmentName, depotCode;
    String encryptedBody;

    CustomDialog CD = new CustomDialog();

    EditText officeName, pincode;
    SearchableSpinner departmentSpinner, designationSpinner,  parentOfficeSpinner, officeLevelSpinner, areaSpinner, districtSpinner, municipalNPSPinner, wardSpinner, blockSpinner, panchayatSpinner, villageSpinner;

    DepartmentSpinnerAdapter departmentSpinnerAdapter;
    DesignationSpinnerAdapter designationSpinnerAdapter;
    OfficeSpinnerAdapter officeSpinnerAdapter; // Parent Office
    OfficeLevelSpinnerAdapter officeLevelSpinnerAdapter;

    DistrictSpinnerAdapter districtSpinnerAdapter;
    MunicipalSpinnerAdapter municipalSpinnerAdapter;
    WardSpinnerAdapter wardSpinnerAdapter;

    BlockSpinnerAdapter blockSpinnerAdapter;
    PanchayatSpinnerAdapter panchayatSpinnerAdapter;
    VillageSpinnerAdapter villageSpinnerAdapter;

    String selectedArea;
    LocationsPojo selectedLocation;
    LinearLayout ruralLinearLayout, urbarnLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_office);

//        TextView depotLocationLabel;
//        depotLocationLabel = findViewById(R.id.depotLocationLabel);
//        depotLocationLabel.setText(Html.fromHtml("Depot Location <font color='#FF0000'>*</font>"));

        ruralLinearLayout = findViewById(R.id.ruralLinearLayout);
        urbarnLinearLayout = findViewById(R.id.urbanLinearLayout);

        officeName = findViewById(R.id.officeName);
        pincode = findViewById(R.id.pincode);

        designationSpinner = findViewById(R.id.designationSpinner);
        parentOfficeSpinner = findViewById(R.id.parentOfficeSpinner);
        officeLevelSpinner = findViewById(R.id.officeLevelSpinner);
        areaSpinner = findViewById(R.id.areaSpinner);
        districtSpinner = findViewById(R.id.districtSpinner);

        municipalNPSPinner = findViewById(R.id.municipalNPSpinner);
        wardSpinner = findViewById(R.id.wardSpinner);
        blockSpinner = findViewById(R.id.blockSpinner);
        panchayatSpinner = findViewById(R.id.panchayatSpinner);
        villageSpinner = findViewById(R.id.villageSpinner);

        back = findViewById(R.id.backBtn);
        proceed = findViewById(R.id.proceedBtn);

        // Create an array of rural and urban options
        String[] areaOptions = {"Rural", "Urban"};

        // Static
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, areaOptions);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(areaAdapter);

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedArea = parent.getItemAtPosition(position).toString();

                if (selectedArea.equalsIgnoreCase("Rural")) {
                    ruralLinearLayout.setVisibility(View.VISIBLE);
                    urbarnLinearLayout.setVisibility(View.GONE);

                }

                //
                else if (selectedArea.equalsIgnoreCase("Urban")) {
                    ruralLinearLayout.setVisibility(View.GONE);
                    urbarnLinearLayout.setVisibility(View.VISIBLE);

                } else {
                    Log.e("Selected Area", "No Area Selected");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


//        ######################################### SERVICE CALL ########################################
        // District
        try {
            if (AppStatus.getInstance(AddOffice.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("lgdDistrict"), "UTF-8"));
                object.setTasktype(TaskType.GET_DISTRICT);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddOffice.this, AddOffice.this, TaskType.GET_DISTRICT).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }

        // Parent Office
        try {
            if (AppStatus.getInstance(AddOffice.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("office"), "UTF-8"));
                object.setParentId(aesCrypto.encrypt(String.valueOf(Econstants.HRTC_DEPARTMENT_PARENT_ID))); // Send Dept ID
                object.setTasktype(TaskType.GET_PARENT_OFFICES);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddOffice.this, AddOffice.this, TaskType.GET_PARENT_OFFICES).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }

        // Office Level
        try {
            if (AppStatus.getInstance(AddOffice.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("officeType"), "UTF-8"));
                object.setParentId(aesCrypto.encrypt(String.valueOf(Econstants.HRTC_DEPARTMENT_PARENT_ID))); // Send Dept ID
                object.setTasktype(TaskType.GET_OFFICE_LEVELS);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddOffice.this, AddOffice.this, TaskType.GET_OFFICE_LEVELS).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }

        // Designation
        try {
            if (AppStatus.getInstance(AddOffice.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("desigDeptMap"), "UTF-8"));
                object.setParentId(aesCrypto.encrypt(String.valueOf(Econstants.HRTC_DEPARTMENT_PARENT_ID))); // Send Dept ID
                object.setTasktype(TaskType.GET_HOD_DESIGNATION);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddOffice.this, AddOffice.this, TaskType.GET_HOD_DESIGNATION).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }


//        ###########################################################################################


//        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                selectedLocation = (LocationsPojo) parent.getItemAtPosition(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        // Back Btn
        back.setOnClickListener(v -> {
            AddOffice.this.finish();
        });

        // Save Btn (Proceed Button)
        proceed.setOnClickListener(v -> {
            if (AppStatus.getInstance(AddOffice.this).isOnline()) {

                if (Econstants.isNotEmpty(officeName.getText().toString())) {


                    showAddConfirmationDialog("Office");


                } else {
                    CD.showDialog(AddOffice.this, "Enter office name to proceed");
                }
            } else {
                CD.showDialog(AddOffice.this, "Internet not Available. Please Connect to the Internet and try again.");
            }
        });

    }


    private void serviceCallMunicipality(String selectedEntity) {
        try {
            if (AppStatus.getInstance(AddOffice.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("lgdDistrict"), "UTF-8"));
                object.setTasktype(TaskType.GET_DISTRICT);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddOffice.this, AddOffice.this, TaskType.GET_DISTRICT).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }

    private void showAddConfirmationDialog(String selectedEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add " + selectedEntity)
                .setMessage("Are you sure you want to add this depot?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (AppStatus.getInstance(AddOffice.this).isOnline()) {

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

                        new ShubhAsyncPost(AddOffice.this, AddOffice.this, TaskType.ADD_DEPOT).execute(uploadObject);

                    } else {
                        CD.addCompleteEntityDialog(AddOffice.this, "Internet not Available. Please Connect to the Internet and try again.");
                    }

                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onTaskCompleted(ResponsePojoGet responseObject, TaskType taskType) throws JSONException {
        // GET DIST
        if (TaskType.GET_DISTRICT == taskType) {
            SuccessResponse response = null;

            List<DistrictPojo> pojoList = new ArrayList<>();
            if (responseObject != null) {
                Log.i("Details", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoList = JsonParse.parseDistricts(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data: ", pojoList.toString());

                            districtSpinnerAdapter = new DistrictSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            districtSpinner.setAdapter(districtSpinnerAdapter);

                        } else {
                            CD.showDialog(AddOffice.this, "No Data Found");
                        }

                    } else {
                        CD.showDialog(AddOffice.this, "Response Not OK");
                    }

                } else {
                    CD.showDialog(AddOffice.this, "Not able to get data");
                }
            } else {
                CD.showDialog(AddOffice.this, "Not able to connect to the server");
            }
        }

        // Get Parent Office
        else if (TaskType.GET_PARENT_OFFICES == taskType) {
            SuccessResponse response = null;

            List<OfficePojo> pojoList = new ArrayList<>();
            if (responseObject != null) {
                Log.i("Details", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoList = JsonParse.parseParentOffices(response.getData());
                        Log.e("SIze", "SIZE 123: " + pojoList.size());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data: ", pojoList.toString());

                            officeSpinnerAdapter = new OfficeSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            parentOfficeSpinner.setAdapter(officeSpinnerAdapter);

                        } else {
                            CD.showDialog(AddOffice.this, "No Data Found");
                        }

                    } else {
                        CD.showDialog(AddOffice.this, "Response Not OK");
                    }

                } else {
                    CD.showDialog(AddOffice.this,"No Response Fetched");
                }
            } else {
                CD.showDialog(AddOffice.this, "Not able to connect to the server");
            }
        }


        // Get Office Level
        else if (TaskType.GET_OFFICE_LEVELS == taskType) {
            SuccessResponse response = null;

            List<OfficeLevel> pojoList = new ArrayList<>();
            if (responseObject != null) {
                Log.i("Details", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoList = JsonParse.parseOfficeLevels(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data: ", pojoList.toString());

                            officeLevelSpinnerAdapter = new OfficeLevelSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            officeLevelSpinner.setAdapter(officeLevelSpinnerAdapter);

                        } else {
                            CD.showDialog(AddOffice.this, "No Data Found");
                        }

                    } else {
                        CD.showDialog(AddOffice.this, "Response Not OK");
                    }

                } else {
                    CD.showDialog(AddOffice.this, "No Response");
                }
            } else {
                CD.showDialog(AddOffice.this, "Not able to connect to the server");
            }
        }

        //
        else if (TaskType.GET_HOD_DESIGNATION == taskType) {
            SuccessResponse response = null;

            List<DesignationPojo> pojoList = new ArrayList<>();
            if (responseObject != null) {
                Log.i("Details", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

//                        pojoList = JsonParse.parseDepartments(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data: ", pojoList.toString());

                            designationSpinnerAdapter = new DesignationSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            designationSpinner.setAdapter(designationSpinnerAdapter);

                        } else {
                            CD.showDialog(AddOffice.this, "No Data Found");
                        }

                    } else {
                        CD.showDialog(AddOffice.this, "Response Not OK");
                    }

                } else {
                    CD.showDialog(AddOffice.this, "No Response");
                }
            } else {
                CD.showDialog(AddOffice.this, "Not able to connect to the server");
            }
        }



        // Add
//        else if (TaskType.ADD_DEPOT == taskType) {
//
//            Log.i("ASYNC TASK COMPLETED", "TASK TYPE IS Adding Entity");
//            SuccessResponse successResponse = null;
//
//            // responseObject will be null if invalid id pass
//            if (responseObject != null) {
//                successResponse = JsonParse.getSuccessResponse(responseObject.getResponse());
//
//                // Status from response matches 200
//                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
//                    Log.i("Add Entity Response", successResponse.getData());
//                    CD.addCompleteEntityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
//
//                } else if (successResponse.getStatus().equalsIgnoreCase("ERROR")) {
//                    Log.i("Add Entity Response", successResponse.getData());
//                    CD.addCompleteEntityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
//                } else {
//                    CD.addCompleteEntityDialog(this, "Please connect to the internet");
//                }
//            } else {
//                Log.i("AddDriver", "Response is null");
//                CD.addCompleteEntityDialog(this, "Response is null.");
//            }
//        }


    }
}
