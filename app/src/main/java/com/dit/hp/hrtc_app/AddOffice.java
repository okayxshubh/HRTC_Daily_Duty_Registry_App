package com.dit.hp.hrtc_app;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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
import com.dit.hp.hrtc_app.Modals.BlockPojo;
import com.dit.hp.hrtc_app.Modals.DepartmentPojo;
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
    EditText departmentName, addressET, depotCode;
    String encryptedBody;

    CustomDialog CD = new CustomDialog();

    EditText officeName, pincode, sanctionedPost;
    SearchableSpinner departmentSpinner, designationSpinner, parentOfficeSpinner, officeLevelSpinner, areaSpinner, districtSpinner, municipalNPSPinner, wardSpinner, blockSpinner, panchayatSpinner, villageSpinner;

    DepartmentSpinnerAdapter departmentSpinnerAdapter;
    DepartmentPojo selectedDepartment;
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

    String selectedArea;
    LinearLayout ruralLinearLayout, urbarnLinearLayout, distLL;

    OfficePojo officeValuesToAdd = new OfficePojo();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_office);

//        TextView depotLocationLabel;
//        depotLocationLabel = findViewById(R.id.depotLocationLabel);
//        depotLocationLabel.setText(Html.fromHtml("Depot Location <font color='#FF0000'>*</font>"));

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

        // Create an array of rural and urban options
        String[] areaOptions = {Econstants.OFFICE_Type_RURAL, Econstants.OFFICE_Type_REVENUE};

        // Static
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, areaOptions);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(areaAdapter);

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
                        officeValuesToAdd.setLgdMunicipalCode(null);
                        officeValuesToAdd.setLgdWardCode(null);
                    }

                    //
                    else if (selectedArea.equalsIgnoreCase(Econstants.OFFICE_Type_REVENUE)) {
                        serviceCallDistrict();
                        distLL.setVisibility(View.VISIBLE);
                        ruralLinearLayout.setVisibility(View.GONE);
                        urbarnLinearLayout.setVisibility(View.VISIBLE);
                        officeValuesToAdd.setOfficeArea(Econstants.OFFICE_Type_RURAL);
                        officeValuesToAdd.setLgdBlockCode(null);
                        officeValuesToAdd.setLgdPanchayatCode(null);
                        officeValuesToAdd.setLgdVillageCode(null);

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

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDistrict = (DistrictPojo) parent.getItemAtPosition(position);
                officeValuesToAdd.setLgdDistrictCode(selectedDistrict.getDistrictLgdCode()); // ADD
                Log.e("Selected District", selectedDistrict.toString());

                if (selectedArea.equalsIgnoreCase("Rural")) {
                    // Api call of block acc to district
                    serviceCallBlock(String.valueOf(selectedDistrict.getDistrictLgdCode()));
                }

                //
                else if (selectedArea.equalsIgnoreCase("Urban")) {
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

            }
        });

        officeLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOfficeLevel = (OfficeLevel) parent.getItemAtPosition(position);
                officeValuesToAdd.setOfficeCategory(selectedOfficeLevel.getOfficeLevelName());
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
                    CD.showDialog(AddOffice.this, "Enter pincode to proceed");
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
                if (selectedOffice != null){
                    officeValuesToAdd.setOfficeParentId(selectedOffice.getOfficeId());
                }

                officeValuesToAdd.setOfficeCategory(selectedOfficeLevel.getOfficeLevelName());

                officeValuesToAdd.setDesignationPojo(selectedDesignation);
                officeValuesToAdd.setLgdDistrictCode(selectedDistrict.getDistrictLgdCode());

                if (selectedArea.equalsIgnoreCase("Rural")) {
                    officeValuesToAdd.setLgdBlockCode(Integer.parseInt(selectedBlock.getBlockLgdCode()));
                    officeValuesToAdd.setLgdPanchayatCode(Integer.parseInt(selectedPanchayat.getPanchayatLgdCode()));
                    officeValuesToAdd.setLgdVillageCode(Integer.parseInt(selectedVillage.getVillageLgdCode()));
                }
                else if (selectedArea.equalsIgnoreCase("Urban")) {
                    officeValuesToAdd.setLgdMunicipalCode(Integer.parseInt(selectedMunicipal.getMunicipalLgdCode()));
                    officeValuesToAdd.setLgdWardCode(Integer.parseInt(selectedWard.getWardLgdCode()));
                }

                showAddConfirmationDialog("Office");

            } else {
                CD.showDialog(AddOffice.this, "Internet not Available. Please Connect to the Internet and try again.");
            }
        });

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
                            uploadObject.setUrl(Econstants.base_url);
                            uploadObject.setMethordName("/master-data?masterName=");
                            uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("office"), "UTF-8"));
                            uploadObject.setTasktype(TaskType.ADD_OFFICE);
                            uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.i("JSON Body", " " + officeValuesToAdd.getJSON().toString() );

                        try {
                            encryptedBody = aesCrypto.encrypt(officeValuesToAdd.getJSON().toString());
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

                        pojoList = JsonParse.parseDesignationsList(response.getData());

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
        } else if (TaskType.GET_BLOCK == taskType) {
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
                } else {
                    CD.addCompleteEntityDialog(this, "Please connect to the internet");
                }
            } else {
                Log.i("AddDriver", "Response is null");
                CD.addCompleteEntityDialog(this, "Response is null.");
            }
        }


    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }
}
