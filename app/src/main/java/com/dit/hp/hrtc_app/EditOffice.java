package com.dit.hp.hrtc_app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

public class EditOffice extends AppCompatActivity implements ShubhAsyncTaskListenerPost, ShubhAsyncTaskListenerGet {

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

    String GLOBAL_LOCATION_STR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_office);


        locationBtn = findViewById(R.id.getLocationBtn);

        // EDIT MODE
        Intent getIntent = getIntent();
        receivedOfficeToEdit = (OfficePojo) getIntent.getSerializableExtra("OfficeInfo");
        isEditMode = getIntent.getBooleanExtra("EditMode", false);


        if (isEditMode) {
            System.out.println("#############################   PRESELECT  #############################");
            System.out.println("Block Code: " + (receivedOfficeToEdit.getLgdBlockCode() != -1 ? receivedOfficeToEdit.getLgdBlockCode() : "Not available"));
            System.out.println("Municipal Code: " + (receivedOfficeToEdit.getLgdMunicipalCode() != -1 ? receivedOfficeToEdit.getLgdMunicipalCode() : "Not available"));
            System.out.println("Ward Code: " + (receivedOfficeToEdit.getLgdWardCode() != -1 ? receivedOfficeToEdit.getLgdWardCode() : "Not available"));
            System.out.println("District Code: " + (receivedOfficeToEdit.getLgdDistrictCode() != -1 ? receivedOfficeToEdit.getLgdDistrictCode() : "Not available"));
            System.out.println("Panchayat Code: " + (receivedOfficeToEdit.getLgdPanchayatCode() != -1 ? receivedOfficeToEdit.getLgdPanchayatCode() : "Not available"));
            System.out.println("Village Code: " + (receivedOfficeToEdit.getLgdVillageCode() != -1 ? receivedOfficeToEdit.getLgdVillageCode() : "Not available"));
        }


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


        if (isEditMode) {
            locationBtn.setClickable(true);
        } else {
            locationBtn.setClickable(false);
        }

        locationBtn.setOnClickListener(v -> {
            // Request Permission & Get Location
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
            }
        });


        // Create an array of rural and urban options
        String[] areaOptions = {Econstants.OFFICE_Type_RURAL, Econstants.OFFICE_Type_REVENUE};

        // Static
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, areaOptions);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(areaAdapter);

        // AREA Preselect + All Service Calls for Preselection
        if (isEditMode) {
            serviceCallDistrict(); // Call 1

            if (receivedOfficeToEdit.getLgdBlockCode() != -1 || receivedOfficeToEdit.getLgdBlockCode() != 0) {
                areaSpinner.post(() -> {
                    int defaultItemPosition = areaAdapter.getPosition(Econstants.OFFICE_Type_REVENUE);
                    if (defaultItemPosition != -1) {
                        areaSpinner.setSelectedItemByIndex(defaultItemPosition);

                        System.out.println("Block Code: " + receivedOfficeToEdit.getLgdBlockCode());
                        System.out.println("Municipal Code: " + receivedOfficeToEdit.getLgdMunicipalCode());
                        System.out.println("Ward Code: " + receivedOfficeToEdit.getLgdWardCode());
                        System.out.println("District Code: " + receivedOfficeToEdit.getLgdDistrictCode());
                        System.out.println("Panchayat Code: " + receivedOfficeToEdit.getLgdPanchayatCode());
                        System.out.println("Village Code: " + receivedOfficeToEdit.getLgdVillageCode());

                        System.out.println("Calling Seervices Municipality n Ward");
                        serviceCallMunicipal(String.valueOf(receivedOfficeToEdit.getLgdDistrictCode()));
                        serviceCallWard(String.valueOf(receivedOfficeToEdit.getLgdMunicipalCode()));
                        // ############################## ALL SERVICE CALLS ########################################

                    } else {
                        Log.e("Error", "Item not found in adapter RURAL: " + receivedOfficeToEdit.getLgdMunicipalCode());
                    }
                });

            } else if (receivedOfficeToEdit.getLgdMunicipalCode() != -1 || receivedOfficeToEdit.getLgdMunicipalCode() != 0) {
                areaSpinner.post(() -> {
                    int defaultItemPosition = areaAdapter.getPosition(Econstants.OFFICE_Type_RURAL);
                    if (defaultItemPosition != -1) {
                        areaSpinner.setSelectedItemByIndex(defaultItemPosition);

                        System.out.println("Block Code: " + receivedOfficeToEdit.getLgdBlockCode());
                        System.out.println("Municipal Code: " + receivedOfficeToEdit.getLgdMunicipalCode());
                        System.out.println("Ward Code: " + receivedOfficeToEdit.getLgdWardCode());
                        System.out.println("District Code: " + receivedOfficeToEdit.getLgdDistrictCode());
                        System.out.println("Panchayat Code: " + receivedOfficeToEdit.getLgdPanchayatCode());
                        System.out.println("Village Code: " + receivedOfficeToEdit.getLgdVillageCode());

                        System.out.println("Calling Block Panchayat Village n Ward");
                        serviceCallBlock(String.valueOf(receivedOfficeToEdit.getLgdDistrictCode()));
                        serviceCallPanchayat(String.valueOf(receivedOfficeToEdit.getLgdBlockCode()));
                        serviceCallVillage(String.valueOf(receivedOfficeToEdit.getLgdPanchayatCode()));
                        // ############################## ALL SERVICE CALLS ########################################

                    } else {
                        Log.e("Error", "Item not found in adapter REVENUE: " + receivedOfficeToEdit.getLgdMunicipalCode());
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
            if (AppStatus.getInstance(EditOffice.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("office"), "UTF-8"));
                object.setParentId(aesCrypto.encrypt(String.valueOf(Econstants.HRTC_DEPARTMENT_PARENT_ID)));
                object.setTasktype(TaskType.GET_PARENT_OFFICES);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(EditOffice.this, EditOffice.this, TaskType.GET_PARENT_OFFICES).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(EditOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(EditOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }

        // Office Level
        try {
            if (AppStatus.getInstance(EditOffice.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("officeType"), "UTF-8"));
                object.setParentId(aesCrypto.encrypt(String.valueOf(Econstants.HRTC_DEPARTMENT_PARENT_ID)));
                object.setTasktype(TaskType.GET_OFFICE_LEVELS);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(EditOffice.this, EditOffice.this, TaskType.GET_OFFICE_LEVELS).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(EditOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(EditOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }

        // Designation
        try {
            if (AppStatus.getInstance(EditOffice.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("desigDeptMap"), "UTF-8"));
                object.setParentId(aesCrypto.encrypt(String.valueOf(Econstants.HRTC_DEPARTMENT_PARENT_ID)));
                object.setTasktype(TaskType.GET_HOD_DESIGNATION);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(EditOffice.this, EditOffice.this, TaskType.GET_HOD_DESIGNATION).execute(object);

            } else {
                CD.showDialog(EditOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(EditOffice.this, "Something Bad happened . Please reinstall the application and try again.");
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
                    serviceCallBlock(String.valueOf(selectedDistrict.getDistrictLgdCode()));
                }

                //
                else if (selectedArea.equalsIgnoreCase(Econstants.OFFICE_Type_REVENUE)) {
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
                Log.e("Selected Municipal: ", selectedMunicipal.getMunicipalName() + " : " + selectedMunicipal.getMunicipalLgdCode());
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
                officeValuesToAdd.setLgdBlockCode(Integer.parseInt(selectedBlock.getBlockLgdCode()));

                serviceCallPanchayat(String.valueOf(selectedBlock.getBlockLgdCode()));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        panchayatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPanchayat = (PanchayatPojo) parent.getItemAtPosition(position);
                officeValuesToAdd.setLgdPanchayatCode(Integer.parseInt(selectedPanchayat.getPanchayatLgdCode()));

                serviceCallVillage(String.valueOf(selectedPanchayat.getPanchayatLgdCode()));

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
            if (AppStatus.getInstance(EditOffice.this).isOnline()) {

                if (selectedOfficeLevel == null) {
                    CD.showDialog(EditOffice.this, "Select office level to proceed");
                    return;
                }

                if (!Econstants.isNotEmpty(officeName.getText().toString())) {
                    CD.showDialog(EditOffice.this, "Enter office name to proceed");
                    return;
                }


                if (selectedArea == null) {
                    CD.showDialog(EditOffice.this, "Select area to proceed");
                    return;
                }

                if (selectedDistrict == null) {
                    CD.showDialog(EditOffice.this, "Select district to proceed");
                    return;
                }

                if (selectedArea.equalsIgnoreCase(Econstants.OFFICE_Type_RURAL)) {
                    if (selectedBlock == null) {
                        CD.showDialog(EditOffice.this, "Select block to proceed");
                        return;
                    }

                    if (selectedPanchayat == null) {
                        CD.showDialog(EditOffice.this, "Select panchayat to proceed");
                        return;
                    }

                    if (selectedVillage == null) {
                        CD.showDialog(EditOffice.this, "Select village to proceed");
                        return;
                    }
                }

                if (selectedArea.equalsIgnoreCase(Econstants.OFFICE_Type_REVENUE)) {
                    if (selectedMunicipal == null) {
                        CD.showDialog(EditOffice.this, "Select municipal to proceed");
                        return;
                    }

                    if (selectedWard == null) {
                        CD.showDialog(EditOffice.this, "Select ward to proceed");
                        return;
                    }
                }

                if (selectedDesignation == null) {
                    CD.showDialog(EditOffice.this, "Select HOD / HOO designation to proceed");
                    return;
                }

                if (!Econstants.isNotEmpty(pincode.getText().toString())) {
                    CD.showDialog(EditOffice.this, "Enter pin-code to proceed");
                    return;
                }

                if (!Econstants.isNotEmpty(addressET.getText().toString())) {
                    CD.showDialog(EditOffice.this, "Enter address to proceed");
                    return;
                }

                if (!Econstants.isNotEmpty(sanctionedPost.getText().toString())) {
                    CD.showDialog(EditOffice.this, "Enter the number of sanctioned post to proceed");
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

                showEditConfirmationDialog("Office");

            } else {
                CD.showDialog(EditOffice.this, "Internet not Available. Please Connect to the Internet and try again.");
            }

        });

    }


    // District
    private void serviceCallDistrict() {
        try {
            if (AppStatus.getInstance(EditOffice.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("lgdDistrict"), "UTF-8"));
                object.setTasktype(TaskType.GET_DISTRICT);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(EditOffice.this, EditOffice.this, TaskType.GET_DISTRICT).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(EditOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(EditOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }


    private void serviceCallMunicipal(String selectedDistID) {
        try {
            if (AppStatus.getInstance(EditOffice.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("lgdMunicipal"), "UTF-8"));
                object.setParentId(aesCrypto.encrypt(selectedDistID));
                Log.e("Parent ID IS: selectedDistID ", selectedDistID);
                object.setTasktype(TaskType.GET_MUNICIPALITY_NP);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(EditOffice.this, EditOffice.this, TaskType.GET_MUNICIPALITY_NP).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(EditOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(EditOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }

    private void serviceCallWard(String selectedMunicipal) {
        try {
            if (AppStatus.getInstance(EditOffice.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("lgdWard"), "UTF-8"));
                object.setParentId(aesCrypto.encrypt(selectedMunicipal));
                Log.e("Parent ID IS: selectedMunicipal ", selectedMunicipal);
                object.setTasktype(TaskType.GET_WARD);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(EditOffice.this, EditOffice.this, TaskType.GET_WARD).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(EditOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(EditOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }


    // RURAL
    private void serviceCallBlock(String selectedDistID) {
        try {
            if (AppStatus.getInstance(EditOffice.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("lgdBlock"), "UTF-8"));
                object.setParentId(aesCrypto.encrypt(selectedDistID));
                Log.e("Parent ID IS: selectedDistID ", selectedDistID);
                object.setTasktype(TaskType.GET_BLOCK);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(EditOffice.this, EditOffice.this, TaskType.GET_BLOCK).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(EditOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(EditOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }

    private void serviceCallPanchayat(String selectedBlock) {
        try {
            if (AppStatus.getInstance(EditOffice.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("lgdPanchayat"), "UTF-8"));
                object.setParentId(aesCrypto.encrypt(selectedBlock));
                Log.e("Parent ID IS selectedBlock: ", selectedBlock);
                object.setTasktype(TaskType.GET_PANCHAYAT);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(EditOffice.this, EditOffice.this, TaskType.GET_PANCHAYAT).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(EditOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(EditOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }

    private void serviceCallVillage(String selectedPanchayat) {
        try {
            if (AppStatus.getInstance(EditOffice.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("lgdVillage"), "UTF-8"));
                object.setParentId(aesCrypto.encrypt(selectedPanchayat));
                Log.e("Parent ID IS: selectedPanchayat ", selectedPanchayat);
                object.setTasktype(TaskType.GET_VILLAGE);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(EditOffice.this, EditOffice.this, TaskType.GET_VILLAGE).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(EditOffice.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(EditOffice.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }


    // Exit confirmation dialog
    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to go back? Any progress made will be lost.").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditOffice.this.finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog, do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEditConfirmationDialog(String selectedEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add " + selectedEntity)
                .setMessage("Are you sure you want to edit this " + selectedEntity + " ?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (AppStatus.getInstance(EditOffice.this).isOnline()) {

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

                        Log.i("JSON Body EDIT", " " + officeValuesToAdd.getJSONToEdit(this).toString());


                        try {
                            encryptedBody = aesCrypto.encrypt(officeValuesToAdd.getJSONToEdit(this).toString());
                        } catch (Exception e) {
                            Log.e("Encryption Error", e.getMessage());
                        }

                        uploadObject.setParam(encryptedBody);
                        Log.i("JSON JSON Body EDIT:", "Enc JSON Body EDIT:" + encryptedBody);
                        Log.i("Object", "Complete Object: " + uploadObject.toString());

                        Log.e("URL: ", "URL: " + uploadObject.getUrl() + uploadObject.getMethordName() + uploadObject.getMasterName() + uploadObject.getParam());

                        new ShubhAsyncPost(EditOffice.this, EditOffice.this, TaskType.EDIT_OFFICE).execute(uploadObject);

                    } else {
                        CD.addCompleteEntityDialog(EditOffice.this, "Internet not Available. Please Connect to the Internet and try again.");
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
                            CD.showDialog(EditOffice.this, "No Data Found");
                        }

                    } else {
                        CD.showDialog(EditOffice.this, "Response Not OK");
                    }

                } else {
                    CD.showDialog(EditOffice.this, "Not able to get data");
                }
            } else {
                CD.showDialog(EditOffice.this, "Not able to connect to the server");
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

                            if (isEditMode && receivedOfficeToEdit != null &&
                                    (receivedOfficeToEdit.getOfficeParentId() != -1 && receivedOfficeToEdit.getOfficeParentId() != 0)) {
                                parentOfficeSpinner.post(() -> {
                                    int defaultItemPosition = officeSpinnerAdapter.getPositionForOffice(receivedOfficeToEdit.getOfficeParentId());
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
                        CD.showDialog(EditOffice.this, "Response Not OK");
                    }

                } else {
                    CD.showDialog(EditOffice.this, "No Response Fetched");
                }
            } else {
                CD.showDialog(EditOffice.this, "Not able to connect to the server");
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
                            CD.showDialog(EditOffice.this, "No Office Types Found");
                        }

                    } else {
                        CD.showDialog(EditOffice.this, "Response Not OK");
                    }

                } else {
                    CD.showDialog(EditOffice.this, "No Response");
                }
            } else {
                CD.showDialog(EditOffice.this, "Not able to connect to the server");
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
                            CD.showDialog(EditOffice.this, "No Data Found");
                        }

                    } else {
                        CD.showDialog(EditOffice.this, "Response Not OK");
                    }

                } else {
                    CD.showDialog(EditOffice.this, "No Response");
                }
            } else {
                CD.showDialog(EditOffice.this, "Not able to connect to the server");
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
                                        Log.e("Error", "Item not found in adapter. Block Spinner Line 1306");
                                    }
                                });
                            }

                        } else {
//                            CD.showDialog(AddOffice.this, "No Data Found");
                        }

                    } else {
                        CD.showDialog(EditOffice.this, "Response Not OK");
                    }

                } else {
                    CD.showDialog(EditOffice.this, "No Response");
                }
            } else {
                CD.showDialog(EditOffice.this, "Not able to connect to the server");
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
                                        Log.e("Error", "Item not found in adapter. Panchayat Spinner Line 1358");
                                    }
                                });
                            }

                        } else {
//                            CD.showDialog(AddOffice.this, "No Data Found");
                        }

                    } else {
                        CD.showDialog(EditOffice.this, "Response Not OK");
                    }

                } else {
                    CD.showDialog(EditOffice.this, "No Response");
                }
            } else {
                CD.showDialog(EditOffice.this, "Not able to connect to the server");
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
                                        Log.e("Error", "Item not found in adapter. Village Spinner Line 1410");
                                    }
                                });
                            }

                        } else {
//                            CD.showDialog(AddOffice.this, "No Data Found");
                        }

                    } else {
                        CD.showDialog(EditOffice.this, "Response Not OK");
                    }

                } else {
                    CD.showDialog(EditOffice.this, "No Response");
                }
            } else {
                CD.showDialog(EditOffice.this, "Not able to connect to the server");
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


                            if (isEditMode && receivedOfficeToEdit != null && receivedOfficeToEdit.getLgdMunicipalCode() != -1) {
                                // PRESELECT
                                municipalNPSPinner.post(() -> {
                                    int defaultItemPosition = municipalSpinnerAdapter.getPositionForMunicipality(String.valueOf(receivedOfficeToEdit.getLgdMunicipalCode()));
                                    // Set the spinner to the default position if valid
                                    System.out.println("HERE SHUBH");
                                    if (defaultItemPosition != -1) {
                                        municipalNPSPinner.setSelectedItemByIndex(defaultItemPosition);
                                        Log.e("OK", "ITEM SET Municipal CODE: " + receivedOfficeToEdit.getLgdMunicipalCode());
                                    } else {
                                        Log.e("Error", "Item not found in adapter. Municipal Spinner Line 1467: " + receivedOfficeToEdit.getLgdMunicipalCode());
                                    }
                                    System.out.println("DONE SHUBH");
                                });
                            }


                        } else {
                            CD.showDialog(EditOffice.this, "No Municipality / Nagar Panchayat Found");
                            municipalNPSPinner.clearSelection();
                            municipalNPSPinner.setAdapter(null);
                        }

                    } else {
                        CD.showDialog(EditOffice.this, "Response Not OK");
                    }

                } else {
                    CD.showDialog(EditOffice.this, "No Response");
                }
            } else {
                CD.showDialog(EditOffice.this, "Not able to connect to the server");
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

                            if (isEditMode && receivedOfficeToEdit != null && receivedOfficeToEdit.getLgdWardCode() != -1) {
                                // PRESELECT
                                wardSpinner.post(() -> {
                                    int defaultItemPosition = wardSpinnerAdapter.getPositionForWard(String.valueOf(receivedOfficeToEdit.getLgdWardCode()));
                                    // Set the spinner to the default position if valid
                                    if (defaultItemPosition != -1) {
                                        wardSpinner.setSelectedItemByIndex(defaultItemPosition);
                                    } else {
                                        Log.e("Error", "Item not found in adapter. Ward Spinner Line 1522: " + receivedOfficeToEdit.getLgdWardCode());
                                    }
                                });
                            }

                        } else {
//                            CD.showDialog(AddOffice.this, "No Data Found");
                        }

                    } else {
                        CD.showDialog(EditOffice.this, "Response Not OK");
                    }

                } else {
                    CD.showDialog(EditOffice.this, "No Response");
                }
            } else {
                CD.showDialog(EditOffice.this, "Not able to connect to the server");
            }
        }

        // Add
        else if (TaskType.EDIT_OFFICE == taskType) {

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
                    CD.showDismissActivityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity

                } else if (successResponse.getStatus().equalsIgnoreCase("ERROR")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.showDismissActivityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
                } else if (successResponse.getStatus().equalsIgnoreCase("NOT_FOUND")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.showDismissActivityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
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


}