package com.dit.hp.hrtc_app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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

import com.dit.hp.hrtc_app.Adapters.CasteSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.EmploymentTypeSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.GenderSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.RelationSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.StaffTypeSpinnerAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.CastePojo;
import com.dit.hp.hrtc_app.Modals.EmploymentTypePojo;
import com.dit.hp.hrtc_app.Modals.GenderPojo;
import com.dit.hp.hrtc_app.Modals.RelationPojo;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.StaffTypePojo;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class AddStaff extends AppCompatActivity implements ShubhAsyncTaskListenerPost, ShubhAsyncTaskListenerGet {

    AESCrypto aesCrypto = new AESCrypto();

    Button back, proceed;
    EditText staffFirstName, staffLastName, dob, employeeCode, addressField, licenseNumber, relationName, contactNo;
    TextView firstNameLabel, lastNameLabel, genderLabel, casteLabel, staffTypeLabel, employmentTypeLabel, relationLabel, contactLabel, licenseNumberLabel, addStaffHeadTV, dobLabel, dateOfJoiningLabel, membersNameLabel, addressLabel, employeeCodeLabel;
    TextView joiningDate;
    String encryptedBody, head;
    SearchableSpinner staffTypeSpinner, employmentTypeSpinner, casteSpinner, genderSpinner;
    CustomDialog CD = new CustomDialog();

    StaffTypeSpinnerAdapter staffTypeSpinnerAdapter;
    EmploymentTypeSpinnerAdapter employmentTypeSpinnerAdapter;
    CasteSpinnerAdapter casteSpinnerAdapter;
    GenderSpinnerAdapter genderSpinnerAdapter;

    CastePojo selectedSpinnerCaste;
    GenderPojo selectedSpinnerGender;
    StaffTypePojo selectedSpinnerStaffType;
    EmploymentTypePojo selectedSpinnerEmpType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        firstNameLabel = findViewById(R.id.firstNameLabel);
        firstNameLabel.setText(Html.fromHtml("First Name <font color='#FF0000'>*</font>"));

        lastNameLabel = findViewById(R.id.lastNameLabel);
        lastNameLabel.setText(Html.fromHtml("Last Name <font color='#FF0000'>*</font>"));

        genderLabel = findViewById(R.id.genderLabel);
        genderLabel.setText(Html.fromHtml("Gender <font color='#FF0000'>*</font>"));

        casteLabel = findViewById(R.id.casteLabel);
        casteLabel.setText(Html.fromHtml("Caste <font color='#FF0000'>*</font>"));

        staffTypeLabel = findViewById(R.id.staffTypeLabel);
        staffTypeLabel.setText(Html.fromHtml("Staff Type <font color='#FF0000'>*</font>"));

        employmentTypeLabel = findViewById(R.id.employmentTypeLabel);
        employmentTypeLabel.setText(Html.fromHtml("Employment Type <font color='#FF0000'>*</font>"));

        contactLabel = findViewById(R.id.contactLabel);
        contactLabel.setText(Html.fromHtml("Contact <font color='#FF0000'>*</font>"));

        licenseNumberLabel = findViewById(R.id.licenseNumberLabel);
        licenseNumberLabel.setText(Html.fromHtml("License Number <font color='#FF0000'>*</font>"));

        dobLabel = findViewById(R.id.dobLabel);
        dobLabel.setText(Html.fromHtml("Date of Birth <font color='#FF0000'>*</font>"));

        dateOfJoiningLabel = findViewById(R.id.dateOfJoiningLabel);
        dateOfJoiningLabel.setText(Html.fromHtml("Date of Joining <font color='#FF0000'>*</font>"));

        membersNameLabel = findViewById(R.id.membersNameLabel);
        membersNameLabel.setText(Html.fromHtml("Enter father's / husband's name <font color='#FF0000'>*</font>"));

        addressLabel = findViewById(R.id.addressLabel);
        addressLabel.setText(Html.fromHtml("Permanent Address <font color='#FF0000'>*</font>"));

        employeeCodeLabel = findViewById(R.id.employeeCodeLabel);
        employeeCodeLabel.setText(Html.fromHtml("Employee Code <font color='#FF0000'>*</font>"));


        // Receiving What Type of Staff is being added
        addStaffHeadTV = findViewById(R.id.addStaffHeadTV);
        staffFirstName = findViewById(R.id.newStaffFirstName);
        staffLastName = findViewById(R.id.newStaffLastName);
        dob = findViewById(R.id.dob);
        joiningDate = findViewById(R.id.date1);
        licenseNumber = findViewById(R.id.licenseNumber);
        staffTypeSpinner = findViewById(R.id.staffTypeDropdown);
        genderSpinner = findViewById(R.id.genderSpinner);
        casteSpinner = findViewById(R.id.casteSpinner);
        employmentTypeSpinner = findViewById(R.id.employmentTypeSpinner);
        relationName = findViewById(R.id.relationNameET);
        contactNo = findViewById(R.id.contact);
        contactLabel = findViewById(R.id.contactLabel);

        employeeCode = findViewById(R.id.employeeCode);
        addressField = findViewById(R.id.addressField);
        back = findViewById(R.id.backBtn);
        proceed = findViewById(R.id.proceedBtn);

//        #################################### SERVICE CALL ########################################

        // GET GENDER
        try {
            if (AppStatus.getInstance(AddStaff.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("gender"), "UTF-8"));
                object.setTasktype(TaskType.GET_GENDER);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddStaff.this, AddStaff.this, TaskType.GET_GENDER).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddStaff.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddStaff.this, "Something Bad happened . Please reinstall the application and try again.");
        }

        // GET Staff Type
        try {
            if (AppStatus.getInstance(AddStaff.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staffType"), "UTF-8"));
                object.setTasktype(TaskType.GET_STAFF_TYPE);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddStaff.this, AddStaff.this, TaskType.GET_STAFF_TYPE).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddStaff.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddStaff.this, "Something Bad happened . Please reinstall the application and try again.");
        }


        // GET social Category
        try {
            if (AppStatus.getInstance(AddStaff.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("socialCategory"), "UTF-8"));
                object.setTasktype(TaskType.GET_SOCIAL_CATEGORY);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddStaff.this, AddStaff.this, TaskType.GET_SOCIAL_CATEGORY).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddStaff.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddStaff.this, "Something Bad happened . Please reinstall the application and try again.");
        }


        // GET Employment type
        try {
            if (AppStatus.getInstance(AddStaff.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("employmentType"), "UTF-8"));
                object.setTasktype(TaskType.GET_EMP_TYPE);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);


                new ShubhAsyncGet(AddStaff.this, AddStaff.this, TaskType.GET_EMP_TYPE).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddStaff.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddStaff.this, "Something Bad happened . Please reinstall the application and try again.");
        }


//        ##########################################################################################

        // Setting Top Layout heading
        Intent getIntent = new Intent();
        head = getIntent().getStringExtra("AddStaffType");

        if (head != null){
            if (head.equalsIgnoreCase("Driver")) {
                addStaffHeadTV.setText("Add Staff");
            } else if (head.equalsIgnoreCase("Conductor")) {
                addStaffHeadTV.setText("Add Staff");
            } else {
                addStaffHeadTV.setText("Add Staff");
            }
        } else {
            addStaffHeadTV.setText("Add Staff");
        }


        casteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpinnerCaste = (CastePojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpinnerGender = (GenderPojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        staffTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpinnerStaffType = (StaffTypePojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        employmentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpinnerEmpType = (EmploymentTypePojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Back Btn
        back.setOnClickListener(v -> {
            showExitConfirmationDialog();
        });

        dob.setOnClickListener(v -> {
            // Get the current date
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Show DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddStaff.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format the selected date as dd-MM-yyyy
                        String formattedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear);

                        // Set the formatted date to the TextView
                        dob.setText(formattedDate);
                    },
                    year, month, day
            );

            // Show the dialog
            datePickerDialog.show();
        });

        joiningDate.setOnClickListener(v -> {
            // Get the current date
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Show DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddStaff.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format the selected date as dd-MM-yyyy
                        String formattedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear);

                        // Set the formatted date to the TextView
                        joiningDate.setText(formattedDate);
                    },
                    year, month, day
            );

            // Show the dialog
            datePickerDialog.show();
        });


        // Save Btn (Proceed Button)
        proceed.setOnClickListener(v -> {
            if (AppStatus.getInstance(AddStaff.this).isOnline()) {
                if (Econstants.isNotEmpty(staffFirstName.getText().toString().trim())) {
                    if (Econstants.isNotEmpty(staffLastName.getText().toString().trim())) {
                        if (selectedSpinnerGender != null) {
                            if (selectedSpinnerCaste != null) {
                                if (selectedSpinnerStaffType != null) {
                                    if (Econstants.isNotEmpty(employeeCode.getText().toString())) {
                                        if (selectedSpinnerEmpType != null) {
                                            if (Econstants.isNotEmpty(licenseNumber.getText().toString())) {
                                                if (Econstants.isNotEmpty(dob.getText().toString())) {
                                                    if (Econstants.isNotEmpty(joiningDate.getText().toString())) {
                                                        if (Econstants.isNotEmpty(contactNo.getText().toString()) && contactNo.getText().toString().length() == 10) {

                                                            if (Econstants.isNotEmpty(relationName.getText().toString())) {

                                                                if (Econstants.isNotEmpty(addressField.getText().toString())) {

                                                                    if (head != null) {
                                                                        // Add for Driver
                                                                        if (head.equalsIgnoreCase("Driver")) {
                                                                            showAddConfirmationDialog("staff member");
                                                                        }

                                                                        // Add for Conductor
                                                                        else if (head.equalsIgnoreCase("Conductor")) {
                                                                            showAddConfirmationDialog("staff member");
                                                                        } else {
                                                                            showAddConfirmationDialog("staff member");
                                                                        }
                                                                    } else {
                                                                        showAddConfirmationDialog("Staff Member");
                                                                    }

                                                                } else {
                                                                    CD.showDialog(AddStaff.this, "Please enter correct address");
                                                                }

                                                            } else {
                                                                CD.showDialog(AddStaff.this, "Please enter member's name");
                                                            }

                                                        } else {
                                                            CD.showDialog(AddStaff.this, "Please enter correct contact number");
                                                        }
                                                    } else {
                                                        CD.showDialog(AddStaff.this, "Please select date of joining");
                                                    }
                                                } else {
                                                    CD.showDialog(AddStaff.this, "Please select date of birth");
                                                }
                                            } else {
                                                CD.showDialog(AddStaff.this, "Please enter licence number");
                                            }
                                        } else {
                                            CD.showDialog(AddStaff.this, "Please select employment type");
                                        }
                                    } else {
                                        CD.showDialog(AddStaff.this, "Please enter employee code");
                                    }
                                } else {
                                    CD.showDialog(AddStaff.this, "Please select a staff type");
                                }
                            } else {
                                CD.showDialog(AddStaff.this, "Please select a caste");
                            }
                        } else {
                            CD.showDialog(AddStaff.this, "Please select a gender");
                        }
                    } else {
                        CD.showDialog(AddStaff.this, "Please enter last name");
                    }
                } else {
                    CD.showDialog(AddStaff.this, "Please enter first name");
                }
            } else {
                CD.showDialog(AddStaff.this, "Internet not Available. Please Connect to the Internet and try again.");
            }
        });

    }

    private void showAddConfirmationDialog(String selectedEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add " + selectedEntity)
                .setMessage("Are you sure you want to add the selected " + selectedEntity + " to the depot " + Preferences.getInstance().depotName + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (AppStatus.getInstance(AddStaff.this).isOnline()) {
                        UploadObject uploadObject = new UploadObject();
                        // We can use Enums / Econstant to store these values of url and method names
                        try {
                            uploadObject.setUrl(Econstants.base_url);
                            uploadObject.setMethordName("/master-data?masterName=");
                            uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staff"), "UTF-8"));
                            uploadObject.setTasktype(TaskType.ADD_STAFF);
                            uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // Creating JSON to store in param
                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("empName", staffFirstName.getText().toString().trim() + " " + staffLastName.getText().toString());
                            jsonObject.put("depot", Preferences.getInstance().depotId);
                            jsonObject.put("staffType", selectedSpinnerStaffType.getStaffTypeId());

                            jsonObject.put("dateOfBirth", dob.getText().toString());
                            jsonObject.put("joiningDate", joiningDate.getText().toString());

                            jsonObject.put("license", licenseNumber.getText().toString());
                            jsonObject.put("contact", Long.parseLong(contactNo.getText().toString()));
                            jsonObject.put("address", addressField.getText().toString());

                            jsonObject.put("employmentType", selectedSpinnerEmpType.getEmploymentTypeId());
                            jsonObject.put("empCode", employeeCode.getText().toString());
                            jsonObject.put("gender", selectedSpinnerGender.getGenderId());
                            jsonObject.put("relativeName", relationName.getText().toString());
                            jsonObject.put("socialCategory", selectedSpinnerCaste.getCasteId());

                            jsonObject.put("createdBy", Preferences.getInstance().emailID);


                        } catch (Exception e) {
                            Log.e("Exception: ", e.getMessage());
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

                        new ShubhAsyncPost(AddStaff.this, AddStaff.this, TaskType.ADD_STAFF).execute(uploadObject);

                    } else {
                        CD.addCompleteEntityDialog(AddStaff.this, "Internet not Available. Please Connect to the Internet and try again.");
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
                        AddStaff.this.finish();
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
        // Add Driver
        if (TaskType.ADD_STAFF == taskType) {
            Log.i("ASYNC TASK COMPLETED", "TASK TYPE IS Adding Entity");
            SuccessResponse successResponse = null;

            // responseObject will be null if invalid id pass
            if (responseObject != null) {
                successResponse = JsonParse.getSuccessResponse(responseObject.getResponse());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    Log.i("Add Entity Response", successResponse.getData());

                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        CD.addCompleteEntityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity

                } else if (successResponse.getStatus().equalsIgnoreCase("ERROR")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.addCompleteEntityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
                }

                else {
                    CD.addCompleteEntityDialog(this, successResponse.getMessage());
                }
            } else {
                Log.i("Add Staff", "Response is null");
                CD.addCompleteEntityDialog(this, "Response is null.");
            }
        }

        // Gender
        else if (TaskType.GET_GENDER == taskType) {
            SuccessResponse response = null;
            List<GenderPojo> pojoList = new ArrayList<>();

            if (responseObject != null) {
                Log.i("StaffDetails", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        pojoList = JsonParse.parseGender(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data Driver", pojoList.toString());

                            genderSpinnerAdapter = new GenderSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            genderSpinner.setAdapter(genderSpinnerAdapter);
                        }
                    } else {
                        CD.showDialog(AddStaff.this, response.getMessage());
                    }
                } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(AddStaff.this, "Not able to fetch gender");
                }
            } else {
                CD.showDialog(AddStaff.this, "Result is null");
            }
        }

        // Category
        else if (TaskType.GET_SOCIAL_CATEGORY == taskType) {
            SuccessResponse response = null;
            List<CastePojo> pojoList = new ArrayList<>();

            if (responseObject != null) {
                Log.i("StaffDetails", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        pojoList = JsonParse.parseSocialCategory(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data Driver", pojoList.toString());

                            casteSpinnerAdapter = new CasteSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            casteSpinner.setAdapter(casteSpinnerAdapter);
                        }
                    } else {
                        CD.showDialog(AddStaff.this, response.getMessage());
                    }
                } else {
                    CD.showDialog(AddStaff.this, "Not able to fetch data");
                }
            } else {
                CD.showDialog(AddStaff.this, "Result is null");
            }
        }

        // Staff Type
        else if (TaskType.GET_STAFF_TYPE == taskType) {
            SuccessResponse response = null;
            List<StaffTypePojo> pojoList = new ArrayList<>();

            if (responseObject != null) {
                Log.i("StaffDetails", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        pojoList = JsonParse.parseStaffType(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data Driver", pojoList.toString());

                            staffTypeSpinnerAdapter = new StaffTypeSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            staffTypeSpinner.setAdapter(staffTypeSpinnerAdapter);


                                // PRESELECT ADDING STAFF TYPE
//                            staffTypeSpinner.post(() -> {
//                                if (staffTypeSpinnerAdapter != null) {
//                                    int itemPosition = 0;
//
//                                    if (head != null) {
//                                        if (head.equalsIgnoreCase("Driver")) {
//                                            itemPosition = staffTypeSpinnerAdapter.getPositionForItem("Driver");
//                                        } else if (head.equalsIgnoreCase("Conductor")) {
//                                            itemPosition = staffTypeSpinnerAdapter.getPositionForItem("Conductor");
//                                        } else {
//                                            staffTypeSpinner.setEnabled(true);
//                                        }
//                                    } else {
//                                        staffTypeSpinner.setEnabled(true);
//                                    }
//
//
//                                    if (itemPosition != -1) {
//                                        staffTypeSpinner.setSelectedItemByIndex(itemPosition);
//                                    } else {
//                                        Log.e("Error", "Staff Type not found in adapter.");
//                                    }
//                                }
//                            });


                        }
                    } else {
                        CD.showDialog(AddStaff.this, response.getMessage());
                    }
                } else {
                    CD.showDialog(AddStaff.this, "Not able to fetch data");
                }
            } else {
                CD.showDialog(AddStaff.this, "Result is null");
            }
        }

        // EMP TYpe
        else if (TaskType.GET_EMP_TYPE == taskType) {
            SuccessResponse response = null;
            List<EmploymentTypePojo> pojoList = new ArrayList<>();

            if (responseObject != null) {
                Log.i("StaffDetails", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        pojoList = JsonParse.parseEmploymentType(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data: ", pojoList.toString());

                            employmentTypeSpinnerAdapter = new EmploymentTypeSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            employmentTypeSpinner.setAdapter(employmentTypeSpinnerAdapter);
                        }

                    } else {
                        CD.showDialog(AddStaff.this, response.getMessage());
                    }
                } else {
                    CD.showDialog(AddStaff.this, "Not able to fetch data");
                }
            } else {
                CD.showDialog(AddStaff.this, "Result is null");
            }
        }


    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

}
