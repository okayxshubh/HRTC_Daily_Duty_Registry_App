package com.dit.hp.hrtc_app;

import static android.widget.Toast.LENGTH_SHORT;
import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.AdditonalChargePojo;
import com.dit.hp.hrtc_app.Modals.HimAccessUser;
import com.dit.hp.hrtc_app.Modals.HimAccessUserInfo;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.TokenInfo;
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

import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;


public class LoginHRTC extends AppCompatActivity implements ShubhAsyncTaskListenerPost, ShubhAsyncTaskListenerGet {

//    Interface for service calls: ShubhAsyncTaskListenerGet
//    Interface for login: ShubhAsyncTaskListenerPost

    EditText userName, password;
    Button signInBtn;

    CustomDialog CD = new CustomDialog();

    Button forgotPassBtn;
    AESCrypto aesCrypto = new AESCrypto();
    HimAccessUser himAccessUser = new HimAccessUser();
    HimAccessUserInfo himAccessUserInfo = new HimAccessUserInfo();
    TokenInfo tokenInfo = new TokenInfo();


    private static final int STORAGE_PERMISSION_REQUEST_CODE = 100;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 102;

    private static final int ALL_PERMISSION_REQUEST_CODE = 9999;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login);

        Preferences.getInstance().loadPreferences(this);

        if (Preferences.getInstance().isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        signInBtn = findViewById(R.id.signIn);
        forgotPassBtn = findViewById(R.id.forgotPassBtn);

        userName.setText("bhupendersingh.thakur@himaccess.hp.gov.in");
        password.setText("Test@123");

//        userName.setText("ghadiyagaurang.shamjibhai@himaccess.hp.gov.in");
//        password.setText("test@123");

//        userName.setText("Admin");
//        password.setText("Admin@1234");


        // Location Permission

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestAllPermissions();
        }


        // NEW HIM ACCESS
        signInBtn.setOnClickListener(v -> {

            if (!userName.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                Log.i("ID", "ID: " + userName.getText().toString().trim());
                Log.i("Pass", "Pass: " + password.getText().toString());

                if (AppStatus.getInstance(LoginHRTC.this).isOnline()) {
                    UploadObject uploadObject = new UploadObject();
                    uploadObject.setUrl(Econstants.eparivar_url);
                    uploadObject.setMethordName(Econstants.loginLDAP);
                    uploadObject.setMasterName("");
                    uploadObject.setTasktype(TaskType.LOGIN_HRTC_HIMACCESS);
                    uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

                    Map<String, String> params = new HashMap<>();
                    try {
                        // Encrypt user credentials
                        String encrypteduserName = aesCrypto.encrypt(userName.getText().toString().trim());
                        String encryptedPassword = aesCrypto.encrypt(password.getText().toString());

                        // Add encrypted + encoded data to params
                        params.put("username", URLEncoder.encode(encrypteduserName, "UTF-8"));
                        params.put("password", URLEncoder.encode(encryptedPassword, "UTF-8"));

                        // Encode Params for PUT Request
                        String encParams = buildParams(params); // Method to build params to append in URL
                        Log.i("Login Params: ", encParams);

                        uploadObject.setParam(encParams);

                    } catch (Exception e) {
                        Log.e("Encryption Error", e.getMessage());
                    }

                    new ShubhAsyncPost(LoginHRTC.this, LoginHRTC.this, TaskType.LOGIN_HRTC_HIMACCESS).execute(uploadObject);
                    Log.i("JSON For Login: ", uploadObject.getParam());

                } else {
                    CD.showDialog(LoginHRTC.this, "Internet not Available. Please Connect to the Internet and try again.");
                }
            } else {
                CD.showDialog(LoginHRTC.this, "Please enter valid Username and Password");
            }

        });

        forgotPassBtn.setOnClickListener(v -> {
            showVisitConfimationDialog();
//            CD.showDialog(this, "Please visit https://himaccess.hp.gov.in for creating an account or resetting password.");
        });

    }


    // Custom methods

    // Get HimAccess Token
    public void getHimAccessToken(String email) {
        if (AppStatus.getInstance(LoginHRTC.this).isOnline()) {
            UploadObject uploadObject = new UploadObject();

            uploadObject.setUrl(Econstants.sarvatra_url + "/application");
            uploadObject.setMethordName(Econstants.getToken);
            uploadObject.setMasterData("");
            uploadObject.setTasktype(TaskType.GET_TOKEN);
            uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

            Map<String, String> params = new HashMap<>();
            try {

                // Add encrypted + encoded data to params
                params.put("appUniqueCode", Econstants.appUniqueCode);
                params.put("serviceId", Econstants.serviceId);
                params.put("email", email);

                // Encode Params for PUT Request
                String normalParams = buildParams(params); // Method to build params to append in URL
                Log.i("Normal Params: ", normalParams);

                uploadObject.setParam(normalParams);

            } catch (Exception e) {
                Log.e("Encryption Error", e.getMessage());
            }

            new ShubhAsyncGet(LoginHRTC.this, LoginHRTC.this, TaskType.GET_TOKEN).execute(uploadObject);
            Log.i("JSON For Login: ", uploadObject.getParam());

        } else {
            CD.showDialog(LoginHRTC.this, "Internet not Available. Please Connect to the Internet and try again.");
        }
    }

    // Get HRTC Token
    public void getHRTCToken(String email) {
        if (AppStatus.getInstance(LoginHRTC.this).isOnline()) {
            UploadObject uploadObject = new UploadObject();

            uploadObject.setUrl(Econstants.base_url);
            uploadObject.setMethordName("/application/getToken?");
            uploadObject.setMasterData("");
            uploadObject.setTasktype(TaskType.GET_HRTC_JWT_TOKEN);
            uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

            Map<String, String> params = new HashMap<>();
            try {
                // Add encrypted + encoded data to params
                params.put("username", URLEncoder.encode(aesCrypto.encrypt(email), "UTF-8"));

                // Encode Params for PUT Request
                String normalParams = buildParams(params); // Method to build params to append in URL
                Log.i("Encoded + Encrypted Params: ", normalParams);

                uploadObject.setParam(normalParams);

            } catch (Exception e) {
                Log.e("EXCEPTION LOGGED HERE.!!!!!", e.getMessage());
            }

            new ShubhAsyncGet(LoginHRTC.this, LoginHRTC.this, TaskType.GET_HRTC_JWT_TOKEN).execute(uploadObject);
            Log.i("JSON For Login: ", uploadObject.getParam());

        } else {
            CD.showDialog(LoginHRTC.this, "Internet not Available. Please Connect to the Internet and try again.");
        }
    }


    public void showVisitConfimationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Visit HimAccess?")
                .setMessage("Visit https://himaccess.hp.gov.in to reset your password or register?")
                .setPositiveButton("Visit", (dialog, which) -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://himaccess.hp.gov.in"));
                    startActivity(browserIntent);
                })
                .setNegativeButton("No", null)
                .show();
    }


    // Get User Details..
    public void getUserDetails() {
        if (AppStatus.getInstance(LoginHRTC.this).isOnline()) {
            UploadObject uploadObject = new UploadObject();
            uploadObject.setUrl(Econstants.sarvatra_url + "/application");
            uploadObject.setMethordName(Econstants.getUserDetails);
            uploadObject.setTasktype(TaskType.GET_USER_DETAILS);
            uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

            Map<String, String> params = new HashMap<>();
            try {
                // Add encrypted + encoded data to params
                params.put("serviceId", URLEncoder.encode(Econstants.serviceId, "UTF-8"));
                params.put("token", URLEncoder.encode(tokenInfo.getToken(), "UTF-8"));

                // Encode Params for PUT Request
                String encParams = buildParams(params); // Method to build params to append in URL
                Log.i("Login Params: ", encParams);

                uploadObject.setParam(encParams);

            } catch (Exception e) {
                Log.e("Encryption Error", e.getMessage());
            }

            new ShubhAsyncGet(LoginHRTC.this, LoginHRTC.this, TaskType.GET_USER_DETAILS).execute(uploadObject);
            Log.i("JSON For Login: ", uploadObject.getParam());

        } else {
            CD.showDialog(LoginHRTC.this, "Internet not Available. Please Connect to the Internet and try again.");
        }
    }

    // Check and Request Storage Permissions (API 29 and below)
    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            // For Android 10 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Check for API 33+
            // Check if the notification permission is granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request permission if not granted
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Check if rationale is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionRationaleDialog(
                        "Location access is required for map and GPS-based features.",
                        (dialog, which) -> ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION_REQUEST_CODE)
                );
            } else {
                // Directly request permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            // Do nothing permission granted
        }
    }

    private void showSettingsDialog(String permission) {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("Permission " + permission + " denied permanently. Please enable it from settings.")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    // Handle Permission Results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission", "Storage Permission Granted");
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showPermissionRationaleDialog(
                                "Storage permission is required to access and save files.",
                                (dialog, which) -> ActivityCompat.requestPermissions(this,
                                        new String[]{
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        STORAGE_PERMISSION_REQUEST_CODE)
                        );
                    }
                }
                break;

            case NOTIFICATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission", "Notification Permission Granted");
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                        showPermissionRationaleDialog(
                                "Notification permission is required to receive updates from the app.",
                                (dialog, which) -> ActivityCompat.requestPermissions(this,
                                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                        NOTIFICATION_PERMISSION_REQUEST_CODE)
                        );
                    }
                }
                break;

            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation(); // permission granted
                } else {
                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.ACCESS_FINE_LOCATION);

                    if (showRationale) {
                        new AlertDialog.Builder(this)
                                .setTitle("Permission Needed")
                                .setMessage("Location access is required for this feature to work. Please allow it.")
                                .setPositiveButton("Retry", (dialog, which) -> requestLocationPermission())
                                .setNegativeButton("Cancel", null)
                                .show();
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Permission Denied")
                                .setMessage("You have permanently denied location access. Please enable it from app settings.")
                                .setPositiveButton("Go to Settings", (dialog, which) -> {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                                    startActivity(intent);
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                }
                break;
        }
    }

    // Helper function to show rationale dialog
    private void showPermissionRationaleDialog(String message, DialogInterface.OnClickListener onPositiveClickListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", onPositiveClickListener)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }


    private void requestAllPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        // Location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // Notifications (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        // Storage (API < 30)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsToRequest.toArray(new String[0]),
                    ALL_PERMISSION_REQUEST_CODE);
        } else {
            // All Permissions Granted Do Nothing
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


    // Handle Result for MANAGE_EXTERNAL_STORAGE (Android 11+)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Log.d("Permission", "Manage External Storage Permission Granted");
                } else {
                    Log.d("Permission", "Manage External Storage Permission Denied");
                }
            }
        }
    }


    @Override
    public void onTaskCompleted(ResponsePojoGet responseObject, TaskType taskType) throws JSONException {

        // Login Task Type
        if (TaskType.LOGIN_HRTC_HIMACCESS == taskType) {
            Log.i("ASYNC TASK COMPLETED", "TASK TYPE IS HRTC LOGIN.. CHECKED");
            SuccessResponse successResponse = null;

            // responseObject will be null if invalid id pass
            if (responseObject != null) {

                successResponse = JsonParse.getSuccessResponse(responseObject.getResponse());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    Log.i("Login Response", successResponse.getData());
                    String decryptedResponse = "";

                    try {
                        decryptedResponse = aesCrypto.decrypt(successResponse.getData());
                    } catch (Exception e) {
                        Log.e("Ex", Objects.requireNonNull(e.getMessage()));
                    }

                    Log.i("Login Response", (decryptedResponse));

                    // Parse the user details
                    himAccessUser = JsonParse.parseDecryptedHimAccessUserInfo(decryptedResponse);

                    if (himAccessUser != null) {
                        Log.i("LoginActivity", "User Login As: " + himAccessUser.toString());

                        getHimAccessToken(himAccessUser.getMail()); // Get Token
                        getHRTCToken(himAccessUser.getMail()); // Get HimAccess Token

                    } else if (successResponse.getStatus().equals(Integer.toString(HttpsURLConnection.HTTP_GONE))) {
                        Log.i("Login Response Invalid ID/Pass", successResponse.getData());
                        CD.showDialog(this, "Please enter correct username and password");

                    } else {
                        CD.showDialog(this, "Please connect to the internet");
                    }

                } else if (successResponse.getStatus().equalsIgnoreCase("NOT_FOUND")) {
                    CD.showDialog(this, "Please enter correct username and password");
                }
                //
                else if (successResponse.getStatus().equalsIgnoreCase("BAD_REQUEST")) {
                    CD.showDialog(this, successResponse.getMessage());
                }
                //
                else {
                    Log.i("LoginHRTC", "Response is null");
                    CD.showDialog(this, "Something went wrong. Check your connection.");
                }
            } else {
                Log.i("LoginHRTC", "Response is null");
                CD.showDialog(this, "Something went wrong. Check your connection.");
            }
        }

        // Get HimAccess Token
        else if (TaskType.GET_TOKEN == taskType) {
            SuccessResponse response = null;

            if (responseObject != null) {
                Log.i("StaffDetails", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {

                    response = JsonParse.getSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        tokenInfo = JsonParse.parseTokenInfo(response.getData());
                        Preferences.getInstance().tokenHimAccess = tokenInfo.getToken();
                        Preferences.getInstance().savePreferences(this);

                        getUserDetails();

                    } else {
                        CD.showDialog(LoginHRTC.this, response.getMessage());
                    }
                } else {
                    Log.e("Not able to get token", "Not able to get token HimAccess");
//                    CD.showDialog(LoginHRTC.this, "Not able to get token");
                }
            } else {
                CD.showDialog(LoginHRTC.this, "Result is null");
            }
        }

        // Get HRTC Token
        else if (TaskType.GET_HRTC_JWT_TOKEN == taskType) {
            SuccessResponse response = null;

            if (responseObject != null) {
                Log.i("Response", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase("200")) {
                    String HRTC_JWT = responseObject.getResponse().toString();
                    Log.e("HRTC_JWT", HRTC_JWT);

                    Preferences.getInstance().token = HRTC_JWT;
                    Preferences.getInstance().savePreferences(this);

                } else {
//                    CD.showDialog(LoginHRTC.this, "Not able to get token");
                    Toast.makeText(this, "Not able to get JWT token", LENGTH_SHORT).show();
                    Log.e("Not able to get token", "Not able to get token HRTC JWT");
                }
            } else {
                CD.showDialog(LoginHRTC.this, "No result in HRTC JWT Token");
            }
        }

        // Get User Info
        else if (TaskType.GET_USER_DETAILS == taskType) {
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

                            // Use first object (if needed)
                            Log.e("User Role ID: ", himAccessUserInfo.getRoleId().toString());
                            Log.e("User App Role ID: ", himAccessUserInfo.getAppRoleId().toString());
                            Log.e("User Email ID: ", himAccessUserInfo.getEmployeePojo().getEmailId());
                            Log.e("User Name: ", himAccessUserInfo.getEmployeePojo().getEmployeeName());
                            Log.e("User Emp ID: ", String.valueOf(himAccessUserInfo.getEmployeePojo().getEmpId()));
                            Log.e("User Role Name: ", himAccessUserInfo.getRoleName());

                            // Add other preferences to save here
                            Preferences.getInstance().appRoleId = himAccessUserInfo.getAppRoleId();  // App Role ID

                            Preferences.getInstance().dateOfBirth = himAccessUser.getDateOfBirth();
                            Preferences.getInstance().mobileNumber = himAccessUser.getMobile();
                            Preferences.getInstance().userName = himAccessUser.getDateOfBirth();

                            Preferences.getInstance().departmentId = himAccessUserInfo.getMainDepartmentPojo().getDepartmentId();
                            Preferences.getInstance().officeTypeName = himAccessUserInfo.getMainOfficeLevelPojo().getOfficeLevelName();
                            Preferences.getInstance().designationName = himAccessUserInfo.getMainDesignationPojo().getDesignationName();

                            Preferences.getInstance().roleId = himAccessUserInfo.getAppRoleId();  // Normal Role ID
                            Preferences.getInstance().roleName = himAccessUserInfo.getRoleName();

                            Preferences.getInstance().completeName = himAccessUser.getCn(); // Complete Name

                            Preferences.getInstance().emailID = himAccessUserInfo.getEmployeePojo().getEmailId();
                            Preferences.getInstance().empId = himAccessUserInfo.getEmployeePojo().getEmpId();
                            Preferences.getInstance().userName = himAccessUserInfo.getEmployeePojo().getEmployeeName();
                            Preferences.getInstance().savePreferences(this);

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

                            // Show Dialog For Additional Chagres
                            CD.showAdditionalChargeDialog(this, additionalChargeList);

                        } else {
                            Toast.makeText(this, "No Additional Charges Fetched", LENGTH_SHORT).show();
                        }

                    } else {
                        CD.showDialog(LoginHRTC.this, response.getMessage());
                    }
                } else {
                    CD.showDialog(LoginHRTC.this, "Not able to get user details");
                }
            } else {
                CD.showDialog(LoginHRTC.this, "Result is null");
            }
        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Preferences.getInstance().loadPreferences(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Preferences.getInstance().loadPreferences(this);
    }


}