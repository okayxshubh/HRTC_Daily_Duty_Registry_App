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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dit.hp.hrtc_app.Adapters.CasteSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.DriverSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.EmploymentTypeSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.GenderSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.RelationSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.StaffTypeSpinnerAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.CastePojo;
import com.dit.hp.hrtc_app.Modals.EmploymentTypePojo;
import com.dit.hp.hrtc_app.Modals.GenderPojo;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.StaffPojo;
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

import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class EditStaff extends AppCompatActivity implements ShubhAsyncTaskListenerPost, ShubhAsyncTaskListenerGet {
    AESCrypto aesCrypto = new AESCrypto();
    Button back, proceed, addBtn;
    LinearLayout changesLayout;

    String oldDriverNameStr, oldDriverIdStr;
    EditText newDriverId, joiningDate, dob;
    private DriverSpinnerAdapter driverSpinnerAdapter;
    TextView updatedFirstName, updatedLastName;
    String encryptedBody;
    CustomDialog CD = new CustomDialog();
    String selectedStaffTypeName;
    TextView firstNameLabel, lastNameLabel, genderLabel, casteLabel, staffTypeLabel, employmentTypeLabel, relationLabel, contactLabel, licenseNumberLabel, addStaffHeadTV, dobLabel, dateOfJoiningLabel, membersNameLabel, addressLabel, employeeCodeLabel;
    TextView topHeadEdit, employeeCode, addressField, licenseNumber, contactNo, relationName;
    String editType;

    SearchableSpinner staffTypeSpinner, casteSpinner, genderSpinner, employmentTypeSpinner;

    StaffPojo receivedDriverInfoToEdit;
    StaffPojo receivedConductorInfoToEdit;

    StaffTypeSpinnerAdapter staffTypeSpinnerAdapter;
    EmploymentTypeSpinnerAdapter employmentTypeSpinnerAdapter;
    CasteSpinnerAdapter casteSpinnerAdapter;
    GenderSpinnerAdapter genderSpinnerAdapter;
    RelationSpinnerAdapter relationSpinnerAdapter;

    CastePojo selectedSpinnerCaste;
    String selectedCasteNameStr;

    int selectedGenderId, selectedCasteId, selectedRelationId, selectedStaffTypeId;

    GenderPojo selectedSpinnerGender;
    String selectedGenderNameStr;

    String selectedRelationNameStr;

    StaffTypePojo selectedSpinnerStaffType;
    String selectedStaffTypeNameStr;

    EmploymentTypePojo selectedSpinnerEmpType;
    String selectedEmpTypeNameStr;
    BigInteger selectedEmpTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_staff);

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

        updatedFirstName = findViewById(R.id.updatedFirstName);
        updatedLastName = findViewById(R.id.updatedLastName);

        employeeCode = findViewById(R.id.employeeCode);
        licenseNumber = findViewById(R.id.licenseNumber);
        addressField = findViewById(R.id.addressField);
        staffTypeSpinner = findViewById(R.id.staffTypeDropdown);
        genderSpinner = findViewById(R.id.genderSpinner);
        casteSpinner = findViewById(R.id.casteSpinner);
        employmentTypeSpinner = findViewById(R.id.employmentTypeSpinner);
        contactNo = findViewById(R.id.contact);
        relationName = findViewById(R.id.relationNameET);

        back = findViewById(R.id.backBtn);
        proceed = findViewById(R.id.proceedBtn);
        topHeadEdit = findViewById(R.id.topHeadEdit);

        joiningDate = findViewById(R.id.date1);
        dob = findViewById(R.id.dob);

        // Retrieve the edit info + Preselect
        editType = getIntent().getStringExtra("EditType");
        Log.i("EDIT TYPE", "EDIT TYPE: " + editType);


//        ################################### SERVICE CALL ##################################################
        // GET GENDER
        try {
            if (AppStatus.getInstance(EditStaff.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("gender"), "UTF-8"));
                object.setTasktype(TaskType.GET_GENDER);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(EditStaff.this, EditStaff.this, TaskType.GET_GENDER).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(EditStaff.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(EditStaff.this, "Something Bad happened . Please reinstall the application and try again.");
        }

        // GET Staff Type
        try {
            if (AppStatus.getInstance(EditStaff.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staffType"), "UTF-8"));
                object.setTasktype(TaskType.GET_STAFF_TYPE);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(EditStaff.this, EditStaff.this, TaskType.GET_STAFF_TYPE).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(EditStaff.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(EditStaff.this, "Something Bad happened . Please reinstall the application and try again.");
        }


        // GET social Category
        try {
            if (AppStatus.getInstance(EditStaff.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("socialCategory"), "UTF-8"));
                object.setTasktype(TaskType.GET_SOCIAL_CATEGORY);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(EditStaff.this, EditStaff.this, TaskType.GET_SOCIAL_CATEGORY).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(EditStaff.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(EditStaff.this, "Something Bad happened . Please reinstall the application and try again.");
        }


        // GET Employment type
        try {
            if (AppStatus.getInstance(EditStaff.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("employmentType"), "UTF-8"));
                object.setTasktype(TaskType.GET_EMP_TYPE);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);


                new ShubhAsyncGet(EditStaff.this, EditStaff.this, TaskType.GET_EMP_TYPE).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(EditStaff.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(EditStaff.this, "Something Bad happened . Please reinstall the application and try again.");
        }


        // GET RELATION
        try {
            if (AppStatus.getInstance(EditStaff.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("relation"), "UTF-8"));
                object.setTasktype(TaskType.GET_RELATION);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(EditStaff.this, EditStaff.this, TaskType.GET_RELATION).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(EditStaff.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(EditStaff.this, "Something Bad happened . Please reinstall the application and try again.");
        }


//        #####################################################################################################

        // Driver Edit
        if (editType.equalsIgnoreCase("DriverEdit")) {
            receivedDriverInfoToEdit = (StaffPojo) getIntent().getSerializableExtra("DriverInfo");
            topHeadEdit.setText("Edit Staff");

            if (receivedDriverInfoToEdit != null) {
                employeeCode.setText(receivedDriverInfoToEdit.getEmployeeCode());
                licenseNumber.setText(receivedDriverInfoToEdit.getLicenceNo());
                addressField.setText(receivedDriverInfoToEdit.getAddress());
                contactNo.setText(String.valueOf(receivedDriverInfoToEdit.getContactNo()));
                relationName.setText(receivedDriverInfoToEdit.getRelationMemberName());

                if (receivedDriverInfoToEdit.getName() != null) {
                    // Split the name into first and last name
                    String[] nameParts = receivedDriverInfoToEdit.getName().split(" ");
                    String firstName = nameParts.length > 0 ? nameParts[0] : "";
                    String lastName = nameParts.length > 1 ? nameParts[1] : "";

                    // Preselected names
                    updatedFirstName.setText(firstName);
                    updatedLastName.setText(lastName);
                    joiningDate.setText(String.valueOf(receivedDriverInfoToEdit.getJoiningDate()));
                    dob.setText(String.valueOf(receivedDriverInfoToEdit.getDob()));
                }
            }
        }

        // Conductor Edit
        else if (editType.equalsIgnoreCase("ConductorEdit")) {
            receivedConductorInfoToEdit = (StaffPojo) getIntent().getSerializableExtra("ConductorInfo");
            topHeadEdit.setText("Edit Staff");

            if (receivedConductorInfoToEdit != null) {
                employeeCode.setText(receivedConductorInfoToEdit.getEmployeeCode());
                licenseNumber.setText(receivedConductorInfoToEdit.getLicenceNo());
                addressField.setText(receivedConductorInfoToEdit.getAddress());
                contactNo.setText(String.valueOf(receivedConductorInfoToEdit.getContactNo()));
                relationName.setText(receivedConductorInfoToEdit.getRelationMemberName());

                if (receivedConductorInfoToEdit.getName() != null) {
                    // Split the name into first and last name
                    String[] nameParts = receivedConductorInfoToEdit.getName().split(" ");
                    String firstName = nameParts.length > 0 ? nameParts[0] : "";
                    String lastName = nameParts.length > 1 ? nameParts[1] : "";

                    // Preselected names
                    updatedFirstName.setText(firstName);
                    updatedLastName.setText(lastName);
                    joiningDate.setText(String.valueOf(receivedConductorInfoToEdit.getJoiningDate()));
                    dob.setText(String.valueOf(receivedConductorInfoToEdit.getDob()));
                }
            }

        } else {
            Log.i("EDIT TYPE", "Editing Staff Type IS NULL");
        }


        dob.setOnClickListener(v -> {
            // Initialize the calendar
            Calendar calendar = Calendar.getInstance();

            // Driver Edit
            if (editType.equalsIgnoreCase("DriverEdit")) {
                // Check if DOB is available
                if (receivedDriverInfoToEdit != null && receivedDriverInfoToEdit.getDob() != null) {
                    try {
                        // Parse the received DOB string (assuming it's in dd-MM-yyyy format)
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        Date dobDate = sdf.parse(receivedDriverInfoToEdit.getDob());
                        if (dobDate != null) {
                            calendar.setTime(dobDate);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Conductor Edit
            else if (editType.equalsIgnoreCase("ConductorEdit")) {
                // Check if DOB is available
                if (receivedConductorInfoToEdit != null && receivedConductorInfoToEdit.getDob() != null) {
                    try {
                        // Parse the received DOB string (assuming it's in dd-MM-yyyy format)
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        Date dobDate = sdf.parse(receivedConductorInfoToEdit.getDob());
                        if (dobDate != null) {
                            calendar.setTime(dobDate);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Show DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditStaff.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format the selected date as dd-MM-yyyy
                        String formattedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear);
                        // Set the formatted date to the TextView
                        dob.setText(formattedDate);
                    },
                    year, month, day
            );

            datePickerDialog.show();
        });

        joiningDate.setOnClickListener(v -> {
            // Initialize the calendar
            Calendar calendar = Calendar.getInstance();

            // Driver Edit
            if (editType.equalsIgnoreCase("DriverEdit")) {
                // Check if Joining Date is available
                if (receivedDriverInfoToEdit != null && receivedDriverInfoToEdit.getJoiningDate() != null) {
                    try {
                        // Parse the received Joining Date string (assuming it's in dd-MM-yyyy format)
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        Date joiningDateValue = sdf.parse(receivedDriverInfoToEdit.getJoiningDate());
                        if (joiningDateValue != null) {
                            calendar.setTime(joiningDateValue);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Conductor Edit
            else if (editType.equalsIgnoreCase("ConductorEdit")) {

                // Check if Joining Date is available
                if (receivedConductorInfoToEdit != null && receivedConductorInfoToEdit.getJoiningDate() != null) {
                    try {
                        // Parse the received Joining Date string (assuming it's in dd-MM-yyyy format)
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        Date joiningDateValue = sdf.parse(receivedConductorInfoToEdit.getJoiningDate());
                        if (joiningDateValue != null) {
                            calendar.setTime(joiningDateValue);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Show DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditStaff.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format the selected date as dd-MM-yyyy
                        String formattedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear);

                        // Set the formatted date to the TextView
                        joiningDate.setText(formattedDate);
                    },
                    year, month, day
            );

            datePickerDialog.show();
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

        casteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpinnerCaste = (CastePojo) parent.getItemAtPosition(position);
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


        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpinnerGender = (GenderPojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Back Btn
        back.setOnClickListener(v -> {
            showExitConfirmationDialog();
        });

        // Save Btn (Proceed Button)
        proceed.setOnClickListener(v -> {
            if (AppStatus.getInstance(EditStaff.this).isOnline()) {

                if (Econstants.isNotEmpty(updatedFirstName.getText().toString().trim())) {

                    if (Econstants.isNotEmpty(updatedLastName.getText().toString().trim())) {

                        if (selectedSpinnerGender != null) {
                            if (selectedSpinnerCaste != null) {
                                if (Econstants.isNotEmpty(employeeCode.getText().toString())) {
                                    if (selectedSpinnerEmpType != null) {
                                        if (Econstants.isNotEmpty(licenseNumber.getText().toString())) {
                                            if (Econstants.isNotEmpty(dob.getText().toString())) {
                                                if (Econstants.isNotEmpty(joiningDate.getText().toString())) {
                                                    if (Econstants.isNotEmpty(contactNo.getText().toString()) && contactNo.getText().toString().length() == 10) {

                                                        if (Econstants.isNotEmpty(addressField.getText().toString())) {

                                                            if (editType.equalsIgnoreCase("DriverEdit")) {
                                                                showUpdateConfirmationDialog("Driver");
                                                            }

                                                            // Conductor Edit
                                                            else if (editType.equalsIgnoreCase("ConductorEdit")) {
                                                                showUpdateConfirmationDialog("Conductor");
                                                            } else {
                                                                Log.i("EDIT TYPE", "Editing Staff Type IS NULL");
                                                                showUpdateConfirmationDialog("Member");
                                                            }


                                                        } else {
                                                            CD.showDialog(EditStaff.this, "Please enter correct address");
                                                        }

                                                    } else {
                                                        CD.showDialog(EditStaff.this, "Please enter correct contact number");
                                                    }
                                                } else {
                                                    CD.showDialog(EditStaff.this, "Please select date of joining");
                                                }
                                            } else {
                                                CD.showDialog(EditStaff.this, "Please select date of birth");
                                            }
                                        } else {
                                            CD.showDialog(EditStaff.this, "Please enter licence number");
                                        }
                                    } else {
                                        CD.showDialog(EditStaff.this, "Please select employment type");
                                    }
                                } else {
                                    CD.showDialog(EditStaff.this, "Please enter employee code");
                                }

                            } else {
                                CD.showDialog(EditStaff.this, "Please select a caste");
                            }
                        } else {
                            CD.showDialog(EditStaff.this, "Please select a gender");
                        }


                    } else {
                        CD.showDialog(EditStaff.this, "Please enter last name");
                    }

                } else {
                    CD.showDialog(EditStaff.this, "Please enter first name");
                }

            } else {
                CD.showDialog(EditStaff.this, "Internet not Available. Please Connect to the Internet and try again.");
            }

        });


    }

    private void showUpdateConfirmationDialog(String selectedEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to update the selected staff member?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Call the API to remove staff
                    if (AppStatus.getInstance(EditStaff.this).isOnline()) {

                        UploadObject uploadObject = new UploadObject();
                        try {
                            uploadObject.setUrl(Econstants.base_url);
                            uploadObject.setMethordName("/master-data?masterName=");

                            if (editType.equalsIgnoreCase("DriverEdit")) {
                                uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staff"), "UTF-8")
                                        + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(receivedDriverInfoToEdit.getId()))));
                            } else if (editType.equalsIgnoreCase("ConductorEdit")) {
                                uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staff"), "UTF-8")
                                        + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(receivedConductorInfoToEdit.getId()))));
                            }

                            uploadObject.setTasktype(TaskType.EDIT_DRIVER);
                            uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // Creating JSON to store in param
                        try {
                            // Prepare the JSON request
                            JSONObject jsonObject = new JSONObject();

                            jsonObject.put("empName", updatedFirstName.getText().toString().trim() + " " + updatedLastName.getText().toString());
                            jsonObject.put("depot", Preferences.getInstance().regionalOfficeId);

                            //Staff Type
                            if (selectedSpinnerStaffType == null) {
                                selectedSpinnerStaffType = (StaffTypePojo) staffTypeSpinner.getSelectedItem();
                            }
                            jsonObject.put("staffType", selectedSpinnerStaffType.getStaffTypeId());

                            //Emp Type
                            if (selectedSpinnerEmpType == null) {
                                selectedSpinnerEmpType = (EmploymentTypePojo) employmentTypeSpinner.getSelectedItem();
                            }
                            jsonObject.put("employmentType", selectedSpinnerEmpType.getEmploymentTypeId());


                            //Gender
                            if (selectedSpinnerGender == null) {
                                selectedSpinnerGender = (GenderPojo) genderSpinner.getSelectedItem();
                            }
                            jsonObject.put("gender", selectedSpinnerGender.getGenderId());

                            // Caste
                            if (selectedSpinnerCaste == null) {
                                selectedSpinnerCaste = (CastePojo) casteSpinner.getSelectedItem();
                            }
                            jsonObject.put("socialCategory", selectedSpinnerCaste.getCasteId());


                            jsonObject.put("dateOfBirth", dob.getText().toString());
                            jsonObject.put("joiningDate", joiningDate.getText().toString());

                            // //Initialize SimpleDateFormat with desired format
//                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); // Assuming input format is dd/MM/yyyy
//                            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy"); // Desired output format
//                            try {
//                                // Parsing and formatting joiningDate
//                                Date joiningDateParsed = dateFormat.parse(joiningDate.getText().toString());
//                                jsonObject.put("joiningDate", outputDateFormat.format(joiningDateParsed));
//
//                                // Parsing and formatting dateOfBirth
//                                Date dobParsed = dateFormat.parse(dob.getText().toString());
//                                jsonObject.put("dateOfBirth", outputDateFormat.format(dobParsed));
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }


                            jsonObject.put("empCode", employeeCode.getText().toString());
                            jsonObject.put("address", addressField.getText().toString());
                            jsonObject.put("license", licenseNumber.getText().toString());
                            jsonObject.put("contact", Long.parseLong(contactNo.getText().toString()));
                            jsonObject.put("relativeName", relationName.getText().toString());

                            jsonObject.put("updatedBy", Preferences.getInstance().empId);

                            Log.i("JSON For Edit", "JSON For Edit: " + jsonObject.toString());
                            // Encrypt the request
                            String encryptedBody = aesCrypto.encrypt(jsonObject.toString());

                            // Call the Retrofit method
                            uploadObject.setParam(encryptedBody);

                            new ShubhAsyncPost(EditStaff.this, EditStaff.this, TaskType.EDIT_DRIVER).execute(uploadObject);

                        } catch (Exception e) {
                            Log.e("EditConductor", "Error preparing request: " + e.getMessage());
                            CD.showDialog(EditStaff.this, "Failed to prepare request. Please try again.");
                        }

                    } else {
                        CD.showDialog(EditStaff.this, "Internet not Available. Please Connect to the Internet and try again.");
                    }

                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }


    // Exit confirmation dialog
    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit? Any changes made will be lost.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditStaff.this.finish();
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


        // Gender
        if (TaskType.GET_GENDER == taskType) {
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

                            genderSpinner.post(() -> {
                                if (genderSpinnerAdapter != null) {
                                    int itemPosition = 0;
                                    if (editType.equalsIgnoreCase("DriverEdit")) {
                                        itemPosition = genderSpinnerAdapter.getPositionForItem(receivedDriverInfoToEdit.getGender());
                                    } else if (editType.equalsIgnoreCase("ConductorEdit")) {
                                        itemPosition = genderSpinnerAdapter.getPositionForItem(receivedConductorInfoToEdit.getGender());
                                    }

                                    if (itemPosition != -1) {
                                        genderSpinner.setSelectedItemByIndex(itemPosition);
                                    } else {
                                        Log.e("Error", "Gender not found in adapter.");
                                    }
                                }
                            });

                        }
                    } else {
                        CD.showDialog(EditStaff.this, response.getMessage());
                    }
                } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(EditStaff.this, "Not able to fetch gender");
                }
            } else {
                CD.showDialog(EditStaff.this, "Result is null");
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

                            casteSpinner.post(() -> {
                                if (casteSpinnerAdapter != null) {
                                    int itemPosition = 0;
                                    if (editType.equalsIgnoreCase("DriverEdit")) {
                                        itemPosition = casteSpinnerAdapter.getPositionForItem(receivedDriverInfoToEdit.getCaste());
                                    } else if (editType.equalsIgnoreCase("ConductorEdit")) {
                                        itemPosition = casteSpinnerAdapter.getPositionForItem(receivedConductorInfoToEdit.getCaste());
                                    }

                                    if (itemPosition != -1) {
                                        casteSpinner.setSelectedItemByIndex(itemPosition);
                                    } else {
                                        Log.e("Error", "Caste not found in adapter.");
                                    }
                                }
                            });

                        }
                    } else {
                        CD.showDialog(EditStaff.this, response.getMessage());
                    }
                } else {
                    CD.showDialog(EditStaff.this, "Not able to fetch data");
                }
            } else {
                CD.showDialog(EditStaff.this, "Result is null");
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

                            staffTypeSpinner.post(() -> {
                                if (staffTypeSpinnerAdapter != null) {
                                    int itemPosition = 0;
                                    if (editType.equalsIgnoreCase("DriverEdit")) {
                                        itemPosition = staffTypeSpinnerAdapter.getPositionForItem(receivedDriverInfoToEdit.getStaffType());
                                    } else if (editType.equalsIgnoreCase("ConductorEdit")) {
                                        itemPosition = staffTypeSpinnerAdapter.getPositionForItem(receivedConductorInfoToEdit.getStaffType());
                                    }

                                    if (itemPosition != -1) {
                                        staffTypeSpinner.setSelectedItemByIndex(itemPosition);
                                    } else {
                                        Log.e("Error", "Caste not found in adapter.");
                                    }
                                }
                            });

                        }
                    } else {
                        CD.showDialog(EditStaff.this, response.getMessage());
                    }
                } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(EditStaff.this, "Not able to fetch data");
                }
            } else {
                CD.showDialog(EditStaff.this, "Result is null");
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

                            employmentTypeSpinner.post(() -> {
                                if (employmentTypeSpinnerAdapter != null) {
                                    int itemPosition = 0;
                                    if (editType.equalsIgnoreCase("DriverEdit")) {
                                        itemPosition = employmentTypeSpinnerAdapter.getPositionForItem(receivedDriverInfoToEdit.getEmploymentType());

                                    } else if (editType.equalsIgnoreCase("ConductorEdit")) {
                                        itemPosition = employmentTypeSpinnerAdapter.getPositionForItem(receivedConductorInfoToEdit.getEmploymentType());
                                    }
                                    if (itemPosition != -1) {
                                        employmentTypeSpinner.setSelectedItemByIndex(itemPosition);
                                    } else {
                                        Log.e("Error", "Caste not found in adapter.");
                                    }
                                }
                            });
                        }
                    } else {
                        CD.showDialog(EditStaff.this, response.getMessage());
                    }
                } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(EditStaff.this, "Not able to fetch data");
                }
            } else {
                CD.showDialog(EditStaff.this, "Result is null");
            }
        }


        // Edit DRIVER
        else if (TaskType.EDIT_DRIVER == taskType) {
            Log.i("ASYNC TASK COMPLETED", "TASK TYPE IS Adding Entity");
            SuccessResponse successResponse = null;

            // responseObject will be null if invalid id pass
            if (responseObject != null) {
                successResponse = JsonParse.getSuccessResponse(responseObject.getResponse());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    Log.i("Add Entity Response", successResponse.getData());


                    CD.showDismissActivityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);

                } else if (successResponse.getStatus().equalsIgnoreCase("ERROR")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.showDismissActivityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
                } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDismissActivityDialog(this, successResponse.getMessage());
                }
            } else {
                Log.i("AddDriver", "Response is null");
                CD.showDialog(this, "Response is null.");
            }
        }


    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }
}





