package com.dit.hp.hrtc_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.dit.hp.hrtc_app.Adapters.BlockSpinnerAdapter;
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
import com.dit.hp.hrtc_app.Modals.BlockPojo;
import com.dit.hp.hrtc_app.Modals.DesignationPojo;
import com.dit.hp.hrtc_app.Modals.DistrictPojo;
import com.dit.hp.hrtc_app.Modals.MunicipalPojo;
import com.dit.hp.hrtc_app.Modals.OfficeLevel;
import com.dit.hp.hrtc_app.Modals.OfficePojo;
import com.dit.hp.hrtc_app.Modals.PanchayatPojo;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Modals.VillagePojo;
import com.dit.hp.hrtc_app.Modals.WardPojo;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerGet;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerPost;
import com.dit.hp.hrtc_app.json.JsonParse;
import com.dit.hp.hrtc_app.utilities.AppStatus;
import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.SamplePresenter;
import com.doi.spinnersearchable.SearchableSpinner;
import com.kushkumardhawan.locationmanager.base.LocationBaseActivity;
import com.kushkumardhawan.locationmanager.configuration.LocationConfiguration;
import com.kushkumardhawan.locationmanager.constants.FailType;
import com.kushkumardhawan.locationmanager.constants.ProcessType;

import org.json.JSONException;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import in.balakrishnan.easycam.CameraBundleBuilder;
import in.balakrishnan.easycam.CameraControllerActivity;

public class AddOffice extends LocationBaseActivity implements SamplePresenter.SampleView, ShubhAsyncTaskListenerPost, ShubhAsyncTaskListenerGet {

    AESCrypto aesCrypto = new AESCrypto();

    Button back, proceed;
    EditText departmentName, addressET, depotCode;
    String encryptedBody;
    TextView headTV, locationTV;
    ImageButton locationBtn;

    CustomDialog CD = new CustomDialog();

    EditText officeName, pincode, sanctionedPost;
    SearchableSpinner departmentSpinner, designationSpinner, parentOfficeSpinner, officeLevelSpinner, areaSpinner, districtSpinner, municipalNPSPinner, wardSpinner, blockSpinner, panchayatSpinner, villageSpinner;

    DesignationSpinnerAdapter designationSpinnerAdapter;
    DesignationPojo selectedDesignation;
    OfficeSpinnerAdapter officeSpinnerAdapter;
    OfficePojo selectedOffice;
    OfficeLevelSpinnerAdapter officeLevelSpinnerAdapter;
    OfficeLevel selectedOfficeLevel;

    DistrictSpinnerAdapter districtSpinnerAdapter;
    DistrictPojo selectedDistrict;
    MunicipalSpinnerAdapter municipalSpinnerAdapter;
    MunicipalPojo selectedMunicipal;
    WardSpinnerAdapter wardSpinnerAdapter;
    WardPojo selectedWard;

    BlockSpinnerAdapter blockSpinnerAdapter;
    BlockPojo selectedBlock;
    PanchayatSpinnerAdapter panchayatSpinnerAdapter;
    PanchayatPojo selectedPanchayat;
    VillageSpinnerAdapter villageSpinnerAdapter;
    VillagePojo selectedVillage;

    ImageButton clearOfficeBtn;

    // For Camera Image / Image Picked
    private String[] list;
    private File actualImage;
    private File compressedImage = null;
    private File renamedFile = null;
    private String photoFilePath, photoFileName;


    // Location Stuff
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private SamplePresenter samplePresenter;
    private ProgressDialog progressDialog;



    String selectedArea;
    LinearLayout ruralLinearLayout, urbarnLinearLayout, distLL;

    OfficePojo officeValuesToAdd = new OfficePojo();
    OfficePojo receivedOfficeToEdit;
    Boolean isEditMode = false;
    ImageView mainImageView;

    String GLOBAL_LOCATION_STR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_office);

        samplePresenter = new SamplePresenter(this);
        locationBtn = findViewById(R.id.getLocationBtn);

        // EDIT MODE
        Intent getIntent = getIntent();
        receivedOfficeToEdit = (OfficePojo) getIntent.getSerializableExtra("OfficeInfo");
        isEditMode = getIntent.getBooleanExtra("EditMode", false);

        // Setting Labels
        TextView officeLevelLabel, officeNameLabel, areaLabel, districtLabel, designationLabel, pincodeLabel, addressLabel, sanctionedPostLabel;
        officeLevelLabel = findViewById(R.id.officeLevelLabel);
        officeLevelLabel.setText(Html.fromHtml("Please Select Office Level <font color='#FF0000'>*</font>"));

        officeNameLabel = findViewById(R.id.officeNameLabel);
        officeNameLabel.setText(Html.fromHtml("Office Name <font color='#FF0000'>*</font>"));

        areaLabel = findViewById(R.id.areaLabel);
        areaLabel.setText(Html.fromHtml("Area (Urban/Rural) <font color='#FF0000'>*</font>"));

        districtLabel = findViewById(R.id.districtLabel);
        districtLabel.setText(Html.fromHtml("District <font color='#FF0000'>*</font>"));

        designationLabel = findViewById(R.id.designationLabel);
        designationLabel.setText(Html.fromHtml("HOD / HOO Designation <font color='#FF0000'>*</font>"));

        pincodeLabel = findViewById(R.id.pincodeLabel);
        pincodeLabel.setText(Html.fromHtml("Pincode <font color='#FF0000'>*</font>"));

        addressLabel = findViewById(R.id.addressLabel);
        addressLabel.setText(Html.fromHtml("Address <font color='#FF0000'>*</font>"));

        sanctionedPostLabel = findViewById(R.id.sanctionedPostLabel);
        sanctionedPostLabel.setText(Html.fromHtml("Sanctioned Post <font color='#FF0000'>*</font>"));

        headTV = findViewById(R.id.HeadTV);
        locationTV = findViewById(R.id.locationTV);


        ruralLinearLayout = findViewById(R.id.ruralLinearLayout);
        urbarnLinearLayout = findViewById(R.id.urbanLinearLayout);

        clearOfficeBtn = findViewById(R.id.clearParentOfficeSelection);
        mainImageView = findViewById(R.id.mainImageView);

        officeName = findViewById(R.id.officeName);
        pincode = findViewById(R.id.pincode);
        sanctionedPost = findViewById(R.id.sanctionedPost);
        addressET = findViewById(R.id.addressET);
        distLL = findViewById(R.id.distLL);
        distLL.setVisibility(View.GONE);

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

        getLocation(); // Automatically Fetch Location


        mainImageView.setOnClickListener(view -> {
            launchCamera();
        });

        locationBtn.setOnClickListener(v -> {
            // Request Permission & Get Location
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                // Get Location and Print Location
                getLocation();
                if (GLOBAL_LOCATION_STR != null) {
                    Log.d("Location", "Location: " + GLOBAL_LOCATION_STR);
                }
            }
        });


        // Create an array of rural and urban options
        String[] areaOptions = {Econstants.OFFICE_Type_RURAL, Econstants.OFFICE_Type_REVENUE};

        // Static
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, areaOptions);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(areaAdapter);


        if (isEditMode) {
            // Revenue
            if (receivedOfficeToEdit.getLgdMunicipalCode() != -1 && receivedOfficeToEdit.getLgdBlockCode() == -1) {
                int defaultPosition = areaAdapter.getPosition(Econstants.OFFICE_Type_REVENUE);
                areaSpinner.setSelection(defaultPosition); // Pre-Select
                serviceCallDistrict();
            } else if (receivedOfficeToEdit.getLgdBlockCode() != -1 && receivedOfficeToEdit.getLgdBlockCode() != -1) {
                int defaultPosition = areaAdapter.getPosition(Econstants.OFFICE_Type_RURAL);
                areaSpinner.setSelection(defaultPosition); // Pre-Select
                serviceCallDistrict();
            } else {
                Log.e("Selected Area", "No Selected Area Can BE Fetched");
            }
        }


        // AREA Preselect
        if (isEditMode) {
            if (receivedOfficeToEdit.getLgdBlockCode() != -1) {
                areaSpinner.post(() -> {
                    int defaultItemPosition = areaAdapter.getPosition(Econstants.OFFICE_Type_RURAL);
                    if (defaultItemPosition != -1) {
                        areaSpinner.setSelectedItemByIndex(defaultItemPosition);
                    } else {
                        Log.e("Error", "Item not found in adapter RURAL.");
                    }
                });

            } else if (receivedOfficeToEdit.getLgdMunicipalCode() != -1) {
                areaSpinner.post(() -> {
                    int defaultItemPosition = areaAdapter.getPosition(Econstants.OFFICE_Type_REVENUE);
                    if (defaultItemPosition != -1) {
                        areaSpinner.setSelectedItemByIndex(defaultItemPosition);
                    } else {
                        Log.e("Error", "Item not found in adapter REVENUE.");
                    }
                });

            } else {
                Log.e("Selected Area", "Area Cannot Be Preselected");
            }
        }

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedArea = parent.getItemAtPosition(position).toString();

                if (selectedArea != null) {
                    if (selectedArea.equalsIgnoreCase(Econstants.OFFICE_Type_RURAL)) {
                        serviceCallDistrict();
                        distLL.setVisibility(View.VISIBLE);
                        ruralLinearLayout.setVisibility(View.VISIBLE);
                        urbarnLinearLayout.setVisibility(View.GONE);
                        officeValuesToAdd.setOfficeArea(Econstants.OFFICE_Type_RURAL);
                    }

                    //
                    else if (selectedArea.equalsIgnoreCase(Econstants.OFFICE_Type_REVENUE)) {
                        serviceCallDistrict();
                        distLL.setVisibility(View.VISIBLE);
                        ruralLinearLayout.setVisibility(View.GONE);
                        urbarnLinearLayout.setVisibility(View.VISIBLE);
                        officeValuesToAdd.setOfficeArea(Econstants.OFFICE_Type_REVENUE);
                    }
                    //
                    else {
                        Log.e("Selected Area", "No Area Selected");
                    }
                } else {
                    Log.e("Selected Area", "Area is null");
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        clearOfficeBtn.setOnClickListener(v -> {
            parentOfficeSpinner.clearSelection();
            selectedOffice = null;
            officeValuesToAdd.setOfficeParentId(-1);
        });


//        ######################################### SERVICE CALL ########################################

        // Parent Office
        try {
            if (AppStatus.getInstance(AddOffice.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("office"), "UTF-8"));
                object.setParentId(aesCrypto.encrypt(String.valueOf(Econstants.HRTC_DEPARTMENT_PARENT_ID)));
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
                object.setParentId(aesCrypto.encrypt(String.valueOf(Econstants.HRTC_DEPARTMENT_PARENT_ID)));
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
                object.setParentId(aesCrypto.encrypt(String.valueOf(Econstants.HRTC_DEPARTMENT_PARENT_ID)));
                object.setTasktype(TaskType.GET_HOD_DESIGNATION);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddOffice.this, AddOffice.this, TaskType.GET_HOD_DESIGNATION).execute(object);

            } else {
                CD.showDialog(AddOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }


//        ###########################################################################################

        // Preselect Text
        if (isEditMode) {
            proceed.setText("Edit");
            headTV.setText("Edit Office");

            officeName.setText(receivedOfficeToEdit.getOfficeName());
            addressET.setText(receivedOfficeToEdit.getAddress());
            officeName.setText(receivedOfficeToEdit.getOfficeName() != null ? receivedOfficeToEdit.getOfficeName() : "");
            addressET.setText(receivedOfficeToEdit.getAddress() != null ? receivedOfficeToEdit.getAddress() : "");
            pincode.setText(receivedOfficeToEdit.getPinCode() != -1 ? String.valueOf(receivedOfficeToEdit.getPinCode()) : "");
            sanctionedPost.setText(receivedOfficeToEdit.getSanctionedPosts() != -1 ? String.valueOf(receivedOfficeToEdit.getSanctionedPosts()) : "");
        }


        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDistrict = (DistrictPojo) parent.getItemAtPosition(position);
                officeValuesToAdd.setLgdDistrictCode(selectedDistrict.getDistrictLgdCode()); // ADD
                Log.e("Selected District", selectedDistrict.toString());

                if (selectedArea.equalsIgnoreCase(Econstants.OFFICE_Type_RURAL)) {
                    // Api call of block acc to district
                    serviceCallBlock(String.valueOf(selectedDistrict.getDistrictLgdCode()));
                }

                //
                else if (selectedArea.equalsIgnoreCase(Econstants.OFFICE_Type_REVENUE)) {
                    // Api call of municipal acc to district
                    serviceCallMunicipal(String.valueOf(selectedDistrict.getDistrictLgdCode()));
                }
                //
                else {
                    Log.e("Selected Area", "No  Selected");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // FOR URBAN
        municipalNPSPinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMunicipal = (MunicipalPojo) parent.getItemAtPosition(position);
                officeValuesToAdd.setLgdMunicipalCode(Integer.parseInt(selectedMunicipal.getMunicipalLgdCode())); // Add
                serviceCallWard(String.valueOf(selectedMunicipal.getMunicipalLgdCode()));  // Ward
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        wardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedWard = (WardPojo) parent.getItemAtPosition(position);
                officeValuesToAdd.setLgdWardCode(Integer.parseInt(selectedWard.getWardLgdCode()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // FOR RURAL
        blockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBlock = (BlockPojo) parent.getItemAtPosition(position);
                serviceCallPanchayat(String.valueOf(selectedBlock.getBlockLgdCode()));
                officeValuesToAdd.setLgdBlockCode(Integer.parseInt(selectedBlock.getBlockLgdCode()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        panchayatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPanchayat = (PanchayatPojo) parent.getItemAtPosition(position);
                serviceCallVillage(String.valueOf(selectedPanchayat.getPanchayatLgdCode()));
                officeValuesToAdd.setLgdPanchayatCode(Integer.parseInt(selectedPanchayat.getPanchayatLgdCode()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVillage = (VillagePojo) parent.getItemAtPosition(position);
                officeValuesToAdd.setLgdVillageCode(Integer.parseInt(selectedVillage.getVillageLgdCode()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Others..
        parentOfficeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOffice = (OfficePojo) parent.getItemAtPosition(position);
                officeValuesToAdd.setOfficeParentId(selectedOffice.getOfficeId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                officeValuesToAdd.setOfficeParentId(-1);
            }
        });


        officeLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOfficeLevel = (OfficeLevel) parent.getItemAtPosition(position);
                officeValuesToAdd.setOfficeLevelPojo(selectedOfficeLevel);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        designationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDesignation = (DesignationPojo) parent.getItemAtPosition(position);
                officeValuesToAdd.setDesignationPojo(selectedDesignation);
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
            if (AppStatus.getInstance(AddOffice.this).isOnline()) {

                if (selectedOfficeLevel == null) {
                    CD.showDialog(AddOffice.this, "Select office level to proceed");
                    return;
                }

                if (!Econstants.isNotEmpty(officeName.getText().toString())) {
                    CD.showDialog(AddOffice.this, "Enter office name to proceed");
                    return;
                }


                if (selectedArea == null) {
                    CD.showDialog(AddOffice.this, "Select area to proceed");
                    return;
                }

                if (selectedDistrict == null) {
                    CD.showDialog(AddOffice.this, "Select district to proceed");
                    return;
                }

                if (selectedArea.equalsIgnoreCase("Rural")) {
                    if (selectedBlock == null) {
                        CD.showDialog(AddOffice.this, "Select block to proceed");
                        return;
                    }

                    if (selectedPanchayat == null) {
                        CD.showDialog(AddOffice.this, "Select panchayat to proceed");
                        return;
                    }

                    if (selectedVillage == null) {
                        CD.showDialog(AddOffice.this, "Select village to proceed");
                        return;
                    }
                }

                if (selectedArea.equalsIgnoreCase("Urban")) {
                    if (selectedMunicipal == null) {
                        CD.showDialog(AddOffice.this, "Select municipal to proceed");
                        return;
                    }

                    if (selectedWard == null) {
                        CD.showDialog(AddOffice.this, "Select ward to proceed");
                        return;
                    }
                }

                if (selectedDesignation == null) {
                    CD.showDialog(AddOffice.this, "Select HOD / HOO designation to proceed");
                    return;
                }

                if (!Econstants.isNotEmpty(pincode.getText().toString())) {
                    CD.showDialog(AddOffice.this, "Enter pin-code to proceed");
                    return;
                }

                if (!Econstants.isNotEmpty(addressET.getText().toString())) {
                    CD.showDialog(AddOffice.this, "Enter address to proceed");
                    return;
                }

                if (!Econstants.isNotEmpty(sanctionedPost.getText().toString())) {
                    CD.showDialog(AddOffice.this, "Enter the number of sanctioned post to proceed");
                    return;
                }


                officeValuesToAdd.setOfficeName(officeName.getText().toString());
                officeValuesToAdd.setAddress(addressET.getText().toString());
                officeValuesToAdd.setPinCode(Integer.parseInt(pincode.getText().toString()));

                officeValuesToAdd.setOfficeName(officeName.getText().toString());
                officeValuesToAdd.setAddress(addressET.getText().toString());
                officeValuesToAdd.setPinCode(Integer.parseInt(pincode.getText().toString()));
                officeValuesToAdd.setSanctionedPosts(Integer.parseInt(sanctionedPost.getText().toString()));

                // PARENT OFFICE
                if (selectedOffice != null) {
                    officeValuesToAdd.setOfficeParentId(selectedOffice.getOfficeId());
                }

                officeValuesToAdd.setOfficeCategory(selectedOfficeLevel.getOfficeLevelName());

                officeValuesToAdd.setDesignationPojo(selectedDesignation);
                officeValuesToAdd.setLgdDistrictCode(selectedDistrict.getDistrictLgdCode());

                if (selectedArea.equalsIgnoreCase("Rural")) {
                    officeValuesToAdd.setLgdBlockCode(Integer.parseInt(selectedBlock.getBlockLgdCode()));
                    officeValuesToAdd.setLgdPanchayatCode(Integer.parseInt(selectedPanchayat.getPanchayatLgdCode()));
                    officeValuesToAdd.setLgdVillageCode(Integer.parseInt(selectedVillage.getVillageLgdCode()));
                } else if (selectedArea.equalsIgnoreCase("Urban")) {
                    officeValuesToAdd.setLgdMunicipalCode(Integer.parseInt(selectedMunicipal.getMunicipalLgdCode()));
                    officeValuesToAdd.setLgdWardCode(Integer.parseInt(selectedWard.getWardLgdCode()));
                }

                if (isEditMode) {
                    showEditConfirmationDialog("Office");
                } else {
                    showAddConfirmationDialog("Office");
                }


            } else {
                CD.showDialog(AddOffice.this, "Internet not Available. Please Connect to the Internet and try again.");
            }

        });

    }

    private void launchCamera() {
        Intent intent = new Intent();
        intent.setClass(AddOffice.this, CameraControllerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("inputData", new CameraBundleBuilder()
                .setFullscreenMode(true)
                .setDoneButtonString("Save")
                .setSinglePhotoMode(false)
                .setMax_photo(1)
                .setManualFocus(false)
                .setBucketName(getClass().getName())
                .setPreviewEnableCount(true)
                .setPreviewIconVisiblity(true)
                .setPreviewPageRedirection(true)
                .setEnableDone(true)
                .setClearBucket(true)
                .createCameraBundle());
        startActivityForResult(intent, 1560);
    }

    // District
    private void serviceCallDistrict() {
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


    private void serviceCallMunicipal(String selectedDistID) {
        try {
            if (AppStatus.getInstance(AddOffice.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("lgdMunicipal"), "UTF-8"));
                object.setParentId(aesCrypto.encrypt(selectedDistID));
                Log.e("Parent ID IS: selectedDistID ", selectedDistID);
                object.setTasktype(TaskType.GET_MUNICIPALITY_NP);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddOffice.this, AddOffice.this, TaskType.GET_MUNICIPALITY_NP).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }

    private void serviceCallWard(String selectedMunicipal) {
        try {
            if (AppStatus.getInstance(AddOffice.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("lgdWard"), "UTF-8"));
                object.setParentId(aesCrypto.encrypt(selectedMunicipal));
                Log.e("Parent ID IS: selectedMunicipal ", selectedMunicipal);
                object.setTasktype(TaskType.GET_WARD);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddOffice.this, AddOffice.this, TaskType.GET_WARD).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }


    // RURAL
    private void serviceCallBlock(String selectedDistID) {
        try {
            if (AppStatus.getInstance(AddOffice.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("lgdBlock"), "UTF-8"));
                object.setParentId(aesCrypto.encrypt(selectedDistID));
                Log.e("Parent ID IS: selectedDistID ", selectedDistID);
                object.setTasktype(TaskType.GET_BLOCK);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddOffice.this, AddOffice.this, TaskType.GET_BLOCK).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }

    private void serviceCallPanchayat(String selectedBlock) {
        try {
            if (AppStatus.getInstance(AddOffice.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("lgdPanchayat"), "UTF-8"));
                object.setParentId(aesCrypto.encrypt(selectedBlock));
                Log.e("Parent ID IS selectedBlock: ", selectedBlock);
                object.setTasktype(TaskType.GET_PANCHAYAT);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddOffice.this, AddOffice.this, TaskType.GET_PANCHAYAT).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }

    private void serviceCallVillage(String selectedPanchayat) {
        try {
            if (AppStatus.getInstance(AddOffice.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("lgdVillage"), "UTF-8"));
                object.setParentId(aesCrypto.encrypt(selectedPanchayat));
                Log.e("Parent ID IS: selectedPanchayat ", selectedPanchayat);
                object.setTasktype(TaskType.GET_VILLAGE);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddOffice.this, AddOffice.this, TaskType.GET_VILLAGE).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }


    // Exit confirmation dialog
    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to go back? Any progress made will be lost.").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AddOffice.this.finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog, do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAddConfirmationDialog(String selectedEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add " + selectedEntity)
                .setMessage("Are you sure you want to add this " + selectedEntity + " ?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (AppStatus.getInstance(AddOffice.this).isOnline()) {

                        UploadObject uploadObject = new UploadObject();
                        // We can use Enums / Econstant to store these values of url and method names
                        try {
                            uploadObject.setUrl(Econstants.sarvatra_url);
                            uploadObject.setMethordName("/master-data?masterName=");
                            uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("office"), "UTF-8"));
                            uploadObject.setTasktype(TaskType.ADD_OFFICE);
                            uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.i("JSON Body", " " + officeValuesToAdd.getJSON(this).toString());


                        try {
                            encryptedBody = aesCrypto.encrypt(officeValuesToAdd.getJSON(this).toString());
                        } catch (Exception e) {
                            Log.e("Encryption Error", e.getMessage());
                        }

                        uploadObject.setParam(encryptedBody);
                        Log.i("JSON JSON Body:", "Enc JSON Body:" + encryptedBody);
                        Log.i("Object", "Complete Object: " + uploadObject.toString());

                        Log.e("URL: ", "URL: " + uploadObject.getUrl() + uploadObject.getMethordName() + uploadObject.getMasterName() + uploadObject.getParam());

                        new ShubhAsyncPost(AddOffice.this, AddOffice.this, TaskType.ADD_OFFICE).execute(uploadObject);

                    } else {
                        CD.addCompleteEntityDialog(AddOffice.this, "Internet not Available. Please Connect to the Internet and try again.");
                    }

                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showEditConfirmationDialog(String selectedEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add " + selectedEntity)
                .setMessage("Are you sure you want to edit this " + selectedEntity + " ?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (AppStatus.getInstance(AddOffice.this).isOnline()) {

                        UploadObject uploadObject = new UploadObject();
                        // We can use Enums / Econstant to store these values of url and method names
                        try {
                            uploadObject.setUrl(Econstants.sarvatra_url);
                            uploadObject.setMethordName("/master-data?masterName=");
                            uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("office"), "UTF-8")
                                    + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(receivedOfficeToEdit.getOfficeId()))));

                            uploadObject.setTasktype(TaskType.EDIT_OFFICE);
                            uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.i("JSON Body", " " + officeValuesToAdd.getJSONToEdit(this).toString());


                        try {
                            encryptedBody = aesCrypto.encrypt(officeValuesToAdd.getJSONToEdit(this).toString());
                        } catch (Exception e) {
                            Log.e("Encryption Error", e.getMessage());
                        }

                        uploadObject.setParam(encryptedBody);
                        Log.i("JSON JSON Body:", "Enc JSON Body:" + encryptedBody);
                        Log.i("Object", "Complete Object: " + uploadObject.toString());

                        Log.e("URL: ", "URL: " + uploadObject.getUrl() + uploadObject.getMethordName() + uploadObject.getMasterName() + uploadObject.getParam());

                        new ShubhAsyncPost(AddOffice.this, AddOffice.this, TaskType.EDIT_OFFICE).execute(uploadObject);

                    } else {
                        CD.addCompleteEntityDialog(AddOffice.this, "Internet not Available. Please Connect to the Internet and try again.");
                    }

                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {

            // Camera Click Handling
            if (resultCode == Activity.RESULT_OK && requestCode == 1560 && data != null) {
                if (data.getStringArrayExtra("resultData").length == 0) {
                    CD.showDialog(AddOffice.this, "Image not Clicked");
                } else {
                    list = data.getStringArrayExtra("resultData");
                    File imgFile = new File(list[0]);  // Directly get file

                    actualImage = new File(imgFile.getPath());
                    mainImageView.setImageBitmap(BitmapFactory.decodeFile(actualImage.getPath()));

//                    Disposable compressedImage1 = new Compressor(this)
//                            .compressToFileAsFlowable(actualImage)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(
//                                    file -> {
//                                        compressedImage = file;
//                                        if (compressedImage != null) {
//                                            Log.d("Compressed Image", compressedImage.getPath());
//
//                                            // ✅ Set the photo file path
//                                            photoFilePath = compressedImage.getPath();
//                                            photoFileName = compressedImage.getName();
//
//                                            mainImageView.setImageBitmap(BitmapFactory.decodeFile(compressedImage.getAbsolutePath()));
//                                            mainImageView.setPadding(5, 5, 5, 5);
//                                            Toast.makeText(getApplicationContext(), "One Image Clicked.", Toast.LENGTH_SHORT).show();
//                                        }
//                                    },
//                                    throwable -> Log.e("ERROR", throwable.getMessage())
//                            );
                }
            }
        }

//        if (data != null && resultCode == Activity.RESULT_OK && requestCode == 1560) {
//            String[] resultArray = data.getStringArrayExtra("resultData");
//
//            if (resultArray == null || resultArray.length == 0) {
//                CD.showDialog(AddOffice.this, "Image not Clicked");
//            } else {
//                actualImage = new File(resultArray[0]);
//                mainImageView.setImageBitmap(BitmapFactory.decodeFile(resultArray[0]));
//
//                Disposable disposable = new Compressor(this)
//                        .compressToFileAsFlowable(actualImage)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                file -> {
//                                    compressedImage = file;
//                                    photoFilePath = compressedImage.getPath();
//                                    photoFileName = compressedImage.getName();
//
//                                    mainImageView.setImageBitmap(BitmapFactory.decodeFile(photoFilePath));
//                                    mainImageView.setPadding(5, 5, 5, 5);
//                                    Toast.makeText(getApplicationContext(), "One Image Clicked.", Toast.LENGTH_SHORT).show();
//                                },
//                                throwable -> Log.e("Compressor Error", "Error compressing image", throwable)
//                        );
//            }
//        }
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

                            if (isEditMode && receivedOfficeToEdit != null) {
                                // PRESELECT LOCATION
                                districtSpinner.post(() -> {
                                    int defaultItemPosition = districtSpinnerAdapter.getPositionForDistrict(receivedOfficeToEdit.getLgdDistrictCode());
                                    // Set the spinner to the default position if valid
                                    if (defaultItemPosition != -1) {
                                        districtSpinner.setSelectedItemByIndex(defaultItemPosition);
                                    } else {
                                        Log.e("Error", "Item not found in adapter.");
                                    }
                                });
                            }

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

                            if (isEditMode && receivedOfficeToEdit != null && receivedOfficeToEdit.getOfficeParentId() != -1) {
                                // PRESELECT LOCATION
                                parentOfficeSpinner.post(() -> {
                                    int defaultItemPosition = officeSpinnerAdapter.getPositionForOffice(receivedOfficeToEdit.getOfficeParentId());
                                    // Set the spinner to the default position if valid
                                    if (defaultItemPosition != -1) {
                                        parentOfficeSpinner.setSelectedItemByIndex(defaultItemPosition);
                                    } else {
                                        Log.e("Error", "Item not found in adapter.");
                                    }
                                });
                            }

                        } else {
//                            CD.showDialog(AddOffice.this, "No Data Found");
                        }

                    } else {
                        CD.showDialog(AddOffice.this, "Response Not OK");
                    }

                } else {
                    CD.showDialog(AddOffice.this, "No Response Fetched");
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

                            if (isEditMode && receivedOfficeToEdit != null) {
                                // PRESELECT LOCATION
                                officeLevelSpinner.post(() -> {
                                    int defaultItemPosition = officeLevelSpinnerAdapter.getPositionForOfficeLevel(receivedOfficeToEdit.getOfficeLevelPojo().getOfficeLevelName(), receivedOfficeToEdit.getOfficeLevelPojo().getOfficeLevelId());
                                    // Set the spinner to the default position if valid
                                    if (defaultItemPosition != -1) {
                                        officeLevelSpinner.setSelectedItemByIndex(defaultItemPosition);
                                    } else {
                                        Log.e("Error", "Item not found in adapter.");
                                    }
                                });
                            }

                        } else {
                            CD.showDialog(AddOffice.this, "No Office Types Found");
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

                        pojoList = JsonParse.parseDesignationsList(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data: ", pojoList.toString());

                            designationSpinnerAdapter = new DesignationSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            designationSpinner.setAdapter(designationSpinnerAdapter);

                            if (isEditMode && receivedOfficeToEdit != null) {
                                // PRESELECT LOCATION
                                designationSpinner.post(() -> {
                                    int defaultItemPosition = designationSpinnerAdapter.getPositionForItem(receivedOfficeToEdit.getDesignationPojo().getDesignationName(), receivedOfficeToEdit.getDesignationPojo().getDesignationId());
                                    // Set the spinner to the default position if valid
                                    if (defaultItemPosition != -1) {
                                        designationSpinner.setSelectedItemByIndex(defaultItemPosition);
                                    } else {
                                        Log.e("Error", "Item not found in adapter.");
                                    }
                                });
                            }

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
        else if (TaskType.GET_BLOCK == taskType) {
            SuccessResponse response = null;

            List<BlockPojo> pojoList = new ArrayList<>();
            if (responseObject != null) {
                Log.i("Details", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoList = JsonParse.parseBlocks(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data: ", pojoList.toString());

                            blockSpinnerAdapter = new BlockSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            blockSpinner.setAdapter(blockSpinnerAdapter);

                            if (isEditMode && receivedOfficeToEdit != null && receivedOfficeToEdit.getLgdBlockCode() != -1) {
                                // PRESELECT
                                blockSpinner.post(() -> {
                                    int defaultItemPosition = blockSpinnerAdapter.getPositionForItem(String.valueOf(receivedOfficeToEdit.getLgdBlockCode()));
                                    // Set the spinner to the default position if valid
                                    if (defaultItemPosition != -1) {
                                        blockSpinner.setSelectedItemByIndex(defaultItemPosition);
                                    } else {
                                        Log.e("Error", "Item not found in adapter.");
                                    }
                                });
                            }

                        } else {
//                            CD.showDialog(AddOffice.this, "No Data Found");
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

        // GET Panchayat
        else if (TaskType.GET_PANCHAYAT == taskType) {
            SuccessResponse response = null;

            List<PanchayatPojo> pojoList = new ArrayList<>();
            if (responseObject != null) {
                Log.i("Details", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoList = JsonParse.parsePanchayats(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data: ", pojoList.toString());

                            panchayatSpinnerAdapter = new PanchayatSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            panchayatSpinner.setAdapter(panchayatSpinnerAdapter);

                            if (isEditMode && receivedOfficeToEdit != null && receivedOfficeToEdit.getLgdPanchayatCode() != -1) {
                                // PRESELECT
                                panchayatSpinner.post(() -> {
                                    int defaultItemPosition = panchayatSpinnerAdapter.getPositionForItem(String.valueOf(receivedOfficeToEdit.getLgdPanchayatCode()));
                                    // Set the spinner to the default position if valid
                                    if (defaultItemPosition != -1) {
                                        panchayatSpinner.setSelectedItemByIndex(defaultItemPosition);
                                    } else {
                                        Log.e("Error", "Item not found in adapter.");
                                    }
                                });
                            }

                        } else {
//                            CD.showDialog(AddOffice.this, "No Data Found");
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

        // GET_VILLAGE
        else if (TaskType.GET_VILLAGE == taskType) {
            SuccessResponse response = null;

            List<VillagePojo> pojoList = new ArrayList<>();
            if (responseObject != null) {
                Log.i("Details", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoList = JsonParse.parseVillages(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data: ", pojoList.toString());

                            villageSpinnerAdapter = new VillageSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            villageSpinner.setAdapter(villageSpinnerAdapter);

                            if (isEditMode && receivedOfficeToEdit != null && receivedOfficeToEdit.getLgdPanchayatCode() != -1) {
                                // PRESELECT
                                villageSpinner.post(() -> {
                                    int defaultItemPosition = villageSpinnerAdapter.getPositionForItem(String.valueOf(receivedOfficeToEdit.getLgdVillageCode()));
                                    // Set the spinner to the default position if valid
                                    if (defaultItemPosition != -1) {
                                        villageSpinner.setSelectedItemByIndex(defaultItemPosition);
                                    } else {
                                        Log.e("Error", "Item not found in adapter.");
                                    }
                                });
                            }


                        } else {
//                            CD.showDialog(AddOffice.this, "No Data Found");
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


        // GET_MUNICIPALITY_NP
        else if (TaskType.GET_MUNICIPALITY_NP == taskType) {
            SuccessResponse response = null;

            List<MunicipalPojo> pojoList = new ArrayList<>();
            if (responseObject != null) {
                Log.i("Details", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoList = JsonParse.parseMunicipals(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data: ", pojoList.toString());

                            municipalSpinnerAdapter = new MunicipalSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            municipalNPSPinner.setAdapter(municipalSpinnerAdapter);

                        } else {
                            CD.showDialog(AddOffice.this, "No Municipality / Nagar Panchayat Found");
                            municipalNPSPinner.clearSelection();
                            municipalNPSPinner.setAdapter(null);
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

        // GET_WARD
        else if (TaskType.GET_WARD == taskType) {
            SuccessResponse response = null;

            List<WardPojo> pojoList = new ArrayList<>();
            if (responseObject != null) {
                Log.i("Details", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoList = JsonParse.parseWards(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data: ", pojoList.toString());

                            wardSpinnerAdapter = new WardSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            wardSpinner.setAdapter(wardSpinnerAdapter);

                        } else {
//                            CD.showDialog(AddOffice.this, "No Data Found");
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
        else if (TaskType.ADD_OFFICE == taskType) {

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
                } else if (successResponse.getStatus().equalsIgnoreCase("NOT_FOUND")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.addCompleteEntityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
                } else {
                    CD.showDialog(this, "Please connect to the internet");
                }
            } else {
                Log.i("Response", "Response is null");
                CD.showDialog(this, "Response is null.");
            }
        }


    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }


    /**
     * Location Interface Methords
     *
     * @return
     */
    @Override
    public LocationConfiguration getLocationConfiguration() {
        return com.kushkumardhawan.locationmanager.configuration.Configurations.defaultConfiguration("Permission Required !", "GPS needs to be turned on.");
    }

    @Override
    public void onLocationChanged(Location location) {
        samplePresenter.onLocationChanged(location);
    }

    @Override
    public void onLocationFailed(@FailType int type) {
        samplePresenter.onLocationFailed(type);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (getLocationManager().isWaitingForLocation() && !getLocationManager().isAnyDialogShowing()) {
            displayProgress();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        dismissProgress();
    }

    private void displayProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
            progressDialog.setMessage("Getting location...");
        }

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public String getText() {
        return "";  //locationText.getText().toString()
    }

    @Override
    public void setText(String text) {
        GLOBAL_LOCATION_STR = text; // Set location
        locationTV.setText(text);
        Log.e("Location GPS", text);
    }

    @Override
    public void updateProgress(String text) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setMessage(text);
        }
    }

    @Override
    public void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onProcessTypeChanged(@ProcessType int processType) {
        samplePresenter.onProcessTypeChanged(processType);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        samplePresenter.destroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
