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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dit.hp.hrtc_app.Adapters.DepotSpinnerAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.DepotPojo;
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

import java.io.Serializable;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ManageDepot extends AppCompatActivity implements ShubhAsyncTaskListenerGet, ShubhAsyncTaskListenerPost {

    AESCrypto aesCrypto = new AESCrypto();

    Button back, proceed;
    String selectedOptionStr;
    Spinner optionTypeSpinner, entityTypeSpinner;

    String depotIdStr;
    TextView optionsLabel, entityLabel;
    String encryptedBody;

    SearchableSpinner newDepotSpinner;
    private DepotSpinnerAdapter newDepotSpinnerAdapter;
    DepotPojo newDepotSelection;
    LinearLayout depotsLL;


    CustomDialog CD = new CustomDialog();

    String[] options = {"-", "Add", "Edit", "Remove"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_depot);


        optionTypeSpinner = findViewById(R.id.optionToPerformSpinner);
        newDepotSpinner = findViewById(R.id.newDepotSpinner);

        optionsLabel = findViewById(R.id.operationLabel);
        optionsLabel.setText(Html.fromHtml("Please choose an operation to perform <font color='#FF0000'>*</font>"));

        depotsLL = findViewById(R.id.depotsLayout);

        back = findViewById(R.id.backBtn);
        proceed = findViewById(R.id.proceedBtn);


// ###########################################  SERVICE CALLS  ##############################################################

        // New Depot Service Call
        try {
            if (AppStatus.getInstance(ManageDepot.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/buses/depots/");
//                object.setStatus(Econstants.STATUS_TRUE);
                object.setTasktype(TaskType.GET_DEPOTS);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(ManageDepot.this, ManageDepot.this, TaskType.GET_DEPOTS).execute(object);

            } else {
                CD.showDialog(ManageDepot.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(ManageDepot.this, "Something Bad happened . Please reinstall the application and try again.");
        }


// ########################################################################################################################

        newDepotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newDepotSelection = (DepotPojo) parent.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> optionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        optionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        optionTypeSpinner.setAdapter(optionAdapter);
        optionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (options[position]) {
                    case "Add":
                        selectedOptionStr = "Add";
                        depotsLL.setVisibility(View.GONE);
                        proceed.setText("PROCEED");
                        break;

                    case "Edit":
                        selectedOptionStr = "Edit";
                        depotsLL.setVisibility(View.VISIBLE);
                        proceed.setText("PROCEED");
                        break;

                    case "Remove":
                        selectedOptionStr = "Remove";
                        depotsLL.setVisibility(View.VISIBLE);
                        proceed.setText("Remove");
                        break;

//                    case "Transfer":
//                        selectedOptionStr = "Transfer";
//                        break;

                    default:
                        selectedOptionStr = null;
                        depotsLL.setVisibility(View.GONE);
                        proceed.setText("PROCEED");
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action
            }
        });


        // Back Btn
        back.setOnClickListener(v -> {
            ManageDepot.this.finish();
        });

        // Save Btn (Proceed Button)
        proceed.setOnClickListener(v -> {

            if (Econstants.isNotEmpty(selectedOptionStr) && !selectedOptionStr.equalsIgnoreCase("-")) {

                if (!selectedOptionStr.equalsIgnoreCase("Add")) {
                    // Check if the selected depot is null
                    if (newDepotSelection == null) {
                        CD.showDialog(this, "Please select a depot to perform the operation");
                    } else {


                        // REMOVE
                        if (selectedOptionStr.equalsIgnoreCase("Remove")) {

                            deleteConfirmationDialog("Depot");

                        }


                        // EDIT
                        else if (selectedOptionStr.equalsIgnoreCase("Edit")) {
                            Intent editIntent = new Intent(this, ManageDepot.class);
                            editIntent.putExtra("selectedDepot", (Serializable) newDepotSelection);
                            startActivity(editIntent);
                        } else {
                            Log.e("Error", "Unknown action");
                        }
                    }
                }
                // Allow adding without depot selection
                else if (selectedOptionStr.equalsIgnoreCase("Add")) {
                    Intent intent = new Intent(this, AddDepot.class);
                    startActivity(intent);
                } else {
                    CD.showDialog(this, "Invalid operation");
                }

            } else {
                CD.showDialog(this, "Please select a desired action to be performed");
            }


        });


    }

    private void deleteConfirmationDialog(String selectedEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + selectedEntity)
                .setMessage("Are you sure you want to remove this depot?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    if (AppStatus.getInstance(ManageDepot.this).isOnline()) {
                        // We can use Enums / Econstant to store these values of url and method names
                        UploadObject uploadObject = new UploadObject();
                        uploadObject.setUrl(Econstants.base_url);
                        uploadObject.setMethordName("/depots/remove");
                        uploadObject.setTasktype(TaskType.REMOVE_DEPOT);
                        uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

                        // Creating JSON to store in param
                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("depotId", newDepotSelection.getId());
                            jsonObject.put("userName", Integer.parseInt(Preferences.getInstance().userName));

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

                        new ShubhAsyncPost(ManageDepot.this, ManageDepot.this, TaskType.REMOVE_DEPOT).execute(uploadObject);

                    } else {
                        CD.addCompleteEntityDialog(ManageDepot.this, "Internet not Available. Please Connect to the Internet and try again.");
                    }

                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
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

                            newDepotSpinnerAdapter = new DepotSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            newDepotSpinner.setAdapter(newDepotSpinnerAdapter);

                        } else {
                            CD.showDialog(ManageDepot.this, "No Data Found");
                        }

                    } else {
                        CD.showDialog(ManageDepot.this, response.getMessage());
                    }
                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(ManageDepot.this, response.getMessage());
                }
            } else {
                CD.showDialog(ManageDepot.this, "Result is null");
            }
        } else if (TaskType.REMOVE_DEPOT == taskType) {
            Log.i("ASYNC TASK COMPLETED", "TASK TYPE IS Adding Entity");
            SuccessResponse successResponse = null;

            // result will be null if invalid id pass
            if (result != null) {
                successResponse = JsonParse.getSuccessResponse(result.getResponse());

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




