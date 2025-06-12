package com.dit.hp.hrtc_app;

import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.dit.hp.hrtc_app.Adapters.AdditionalChargesListAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Modals.AdditonalChargePojo;
import com.dit.hp.hrtc_app.Modals.HimAccessUserInfo;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerGet;
import com.dit.hp.hrtc_app.json.JsonParse;
import com.dit.hp.hrtc_app.utilities.AppStatus;
import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;

import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class ProfileScreen extends BaseDrawerActivity implements ShubhAsyncTaskListenerGet {

    // Show What Layout
    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_screen; // layout for this screen
    }

    @Override
    protected int getNavMenuId() {
        return R.id.nav_profile; // the nav menu item to highlight
    }

    CardView cardView1, cardView2;
    ImageButton profileBtn;
    CustomDialog CD = new CustomDialog();

    ListView resultListView;
    AdditionalChargesListAdapter additionalChargesListAdapter;

    TextView username, deptName, role, designation, email, employeeId, dob, phone, allChargesHead;
    AESCrypto aesCrypto = new AESCrypto();
    HimAccessUserInfo himAccessUserInfo = new HimAccessUserInfo();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load saved preferences at the very beginning
        Preferences.getInstance().loadPreferences(this);
        getUserDetails();

        username = findViewById(R.id.user_name);
        deptName = findViewById(R.id.user_department);
        role = findViewById(R.id.user_role);
        designation = findViewById(R.id.user_designation);
        email = findViewById(R.id.user_email);
        employeeId = findViewById(R.id.user_employee_id);
        phone = findViewById(R.id.user_phone);
        dob = findViewById(R.id.dob);
        resultListView = findViewById(R.id.resultListView);
        allChargesHead = findViewById(R.id.allChargesHead);


        username.setText(Preferences.getInstance().userName != null ? Preferences.getInstance().userName : "Not Available");

        deptName.setText(Preferences.getInstance().officeTypeName != null ? "Office Type: " + Preferences.getInstance().officeTypeName : "Not Available");
        designation.setText(Preferences.getInstance().designationName != null ? "Designation: " + Preferences.getInstance().designationName : "Not Available");
        role.setText(Preferences.getInstance().roleName != null ? "Role: " + Preferences.getInstance().roleName : "Not Available");

        email.setText(Preferences.getInstance().emailID != null ? Preferences.getInstance().emailID : "Not Available");

        employeeId.setText(Preferences.getInstance().empId != null ? String.valueOf(Preferences.getInstance().empId) : "Not Available");
        phone.setText(Preferences.getInstance().mobileNumber != null ? Preferences.getInstance().mobileNumber : "Not Available");
        dob.setText(Preferences.getInstance().dateOfBirth != null ? Preferences.getInstance().dateOfBirth : "Not Available");


    }

//    CUSTOM METHODS ########################################################################################################################################

    // For HimAccess Additional Charges
    // Get User Details..
    public void getUserDetails() {
        if (AppStatus.getInstance(ProfileScreen.this).isOnline()) {
            UploadObject uploadObject = new UploadObject();
            uploadObject.setUrl(Econstants.sarvatra_url + "/application");
            uploadObject.setMethordName(Econstants.getUserDetails);
            uploadObject.setTasktype(TaskType.GET_USER_DETAILS);
            uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

            Map<String, String> params = new HashMap<>();
            try {
                // Add encrypted + encoded data to params
                params.put("serviceId", URLEncoder.encode(Econstants.serviceId, "UTF-8"));
                params.put("token", URLEncoder.encode(Preferences.getInstance().tokenHimAccess, "UTF-8"));

                // Encode Params for PUT Request
                String encParams = buildParams(params); // Method to build params to append in URL
                Log.i("Login Params: ", encParams);

                uploadObject.setParam(encParams);

            } catch (Exception e) {
                Log.e("Encryption Error", e.getMessage());
            }

            new ShubhAsyncGet(ProfileScreen.this, ProfileScreen.this, TaskType.GET_USER_DETAILS).execute(uploadObject);
            Log.i("JSON For Login: ", uploadObject.getParam());

        } else {
            CD.showDialog(ProfileScreen.this, "Internet not Available. Please Connect to the Internet and try again.");
        }
    }


    // Custom method to encode Params.. when params are not JSON.. PUT Request to edit
    private String buildParams(Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return stringBuilder.toString();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileScreen.this, Homescreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getUserDetails();
        setHeading("Profile"); // Custom Method in BaseDrawerActivity
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserDetails();
        setHeading("Profile"); // Custom Method in BaseDrawerActivity
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
    }


    @Override
    public void onTaskCompleted(ResponsePojoGet responseObject, TaskType taskType) throws JSONException {

        // Get User Info
        if (TaskType.GET_USER_DETAILS == taskType) {
            SuccessResponse response = null;

            if (responseObject != null) {
                Log.i("StaffDetails", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {

                    response = JsonParse.getSuccessResponse(responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        String decryptedResponse;
                        try {
                            decryptedResponse = aesCrypto.decrypt(response.getData());
                            // Don't call getDecryptedSuccessResponse if data is already JSONArray
                            himAccessUserInfo = JsonParse.parseUserInfoPojo(decryptedResponse);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        // All Charges List
                        List<AdditonalChargePojo> additionalChargeList = new ArrayList<>();

                        // Convert main charge to additional charge
                        AdditonalChargePojo originalCharge = new AdditonalChargePojo();
                        originalCharge.setEmpId(himAccessUserInfo.getEmployeePojo().getEmpId());
                        originalCharge.setDepartmentPojo(himAccessUserInfo.getMainDepartmentPojo());
                        originalCharge.setOfficePojo(himAccessUserInfo.getMainOffice());
                        originalCharge.setOfficeLevel(himAccessUserInfo.getMainOfficeLevelPojo());
                        originalCharge.setDesignationPojo(himAccessUserInfo.getMainDesignationPojo());

                        additionalChargeList.add(originalCharge);

                        // Parse Additional Charges + Original Charge
                        Set<Long> seenOfficeIds = new HashSet<>();
                        List<AdditonalChargePojo> fetchedAdditonalCharges = himAccessUserInfo.getAdditionalChargeDetailDTO();

                        // Check if Additional Charges Fetched
                        if (fetchedAdditonalCharges != null) {
                            for (AdditonalChargePojo additonalChargePojo : himAccessUserInfo.getAdditionalChargeDetailDTO()) {
                                long currentOfficeId = additonalChargePojo.getOfficePojo().getOfficeId();
                                long originalOfficeId = originalCharge.getOfficePojo().getOfficeId();

                                // Skip if same as original office OR already added
                                if (currentOfficeId != originalOfficeId && !seenOfficeIds.contains(currentOfficeId)) {
                                    seenOfficeIds.add(currentOfficeId);
                                    additonalChargePojo.setEmpId(himAccessUserInfo.getEmployeePojo().getEmpId());
                                    additionalChargeList.add(additonalChargePojo);
                                }
                            }

                            allChargesHead.setText("All Charges List: " + additionalChargeList.size() + " Charge(s)");

                            additionalChargesListAdapter = new AdditionalChargesListAdapter(this, additionalChargeList);
                            resultListView.setAdapter(additionalChargesListAdapter);

                        } else {
                            // Hide Addtional Charges Section
                            Toast.makeText(this,"Can't fetch charges",LENGTH_SHORT).show();
                        }

                    } else {
//                        CD.showDialog(ProfileScreen.this, response.getMessage());
                    }
                } else {
//                    CD.showDialog(ProfileScreen.this, "Not able to get user details");
                }
            } else {
//                CD.showDialog(ProfileScreen.this, "Result is null");
            }
        }


    }

}
