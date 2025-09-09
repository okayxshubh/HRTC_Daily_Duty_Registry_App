package com.dit.hp.hrtc_app;

import static android.widget.Toast.LENGTH_SHORT;
import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.AdditonalChargePojo;
import com.dit.hp.hrtc_app.Modals.HimAccessUser;
import com.dit.hp.hrtc_app.Modals.HimAccessUserInfo;
import com.dit.hp.hrtc_app.Modals.OTPLoginUser;
import com.dit.hp.hrtc_app.Modals.OTPObject;
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
import com.doi.spinnersearchable.SearchableSpinner;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;


public class MobileLoginHRTC extends AppCompatActivity implements ShubhAsyncTaskListenerPost, ShubhAsyncTaskListenerGet {

//    Interface for service calls: ShubhAsyncTaskListenerGet
//    Interface for login: ShubhAsyncTaskListenerPost

    private TabLayout loginTabLayout;
    private int selectedTab = 0;

    CustomDialog CD = new CustomDialog();
    LinearLayout OTPLoginLayout, PassLoginLayout;

    Button signInBtn; // Via Password
    AutoCompleteTextView userName;
    EditText password;

    Button getOtp, validateNLoginBtn; // Via OTP
    EditText mobileNumber, otp_field;
    SearchableSpinner emails;

    Button forgotPassBtn;
    TextInputLayout enterOTPLayout;
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
        setContentView(R.layout.activity_otp_login);

        Econstants.loadPrefsNApplyTheme(this);

        getOtp = findViewById(R.id.getOtp);
        mobileNumber = findViewById(R.id.mobileNumber);
        signInBtn = findViewById(R.id.signIn);

        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);

        emails = findViewById(R.id.emails);
        emails.setVisibility(View.GONE);

        validateNLoginBtn = findViewById(R.id.validateOTP);
        validateNLoginBtn.setVisibility(View.GONE);

        enterOTPLayout = findViewById(R.id.enterOTPLayout);
        enterOTPLayout.setVisibility(View.GONE);

        otp_field = findViewById(R.id.otpTV);
        otp_field.setVisibility(View.GONE);

        forgotPassBtn = findViewById(R.id.forgotPassBtn);

        OTPLoginLayout = findViewById(R.id.OTPLoginLayout);
        PassLoginLayout = findViewById(R.id.PassLoginLayout);

        // Location Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestAllPermissions();
        }

        loginTabLayout = findViewById(R.id.loginTabLayout);

        // Track selected tab
        loginTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTab = tab.getPosition();

                if (selectedTab == 0) {
                    // OTP LOGIN
                    OTPLoginLayout.setVisibility(View.VISIBLE);
                    PassLoginLayout.setVisibility(View.GONE);
                    otp_field.setText("");
                    mobileNumber.setText("");

                    emails.setVisibility(View.GONE);
                    emails.setAdapter(null);

                    getOtp.setVisibility(View.VISIBLE);
                    validateNLoginBtn.setVisibility(View.VISIBLE);
                    mobileNumber.setEnabled(true);
                    otp_field.setVisibility(View.GONE);
                    validateNLoginBtn.setVisibility(View.GONE);

                } else if (selectedTab == 1) {
                    // PASSWORD LOGIN
                    OTPLoginLayout.setVisibility(View.GONE);
                    PassLoginLayout.setVisibility(View.VISIBLE);
                    mobileNumber.setText("");
                    otp_field.setText("");

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // OTP LOGIN
        getOtp.setOnClickListener(v -> {
            if (mobileNumber.getText().toString().length() == 10) {
                // METHOD
                getOtp();
            } else {
                CD.showDialog(MobileLoginHRTC.this, "Please enter valid 10 digit mobile number");
            }
        });

        validateNLoginBtn.setOnClickListener(v -> {

            String enteredMobile = mobileNumber.getText().toString().trim();
            String selectedEmail = emails.getSelectedItem().toString().trim();
            String enteredOtp = otp_field.getText().toString().trim();

            if (!otp_field.getText().toString().isEmpty() && otp_field.getText().toString().length() == 6) {
                if (!"-- SELECT --".equalsIgnoreCase(selectedEmail)) {
                    // Proceed with submission
                    // For example: send selectedEmail and OTP to server
                    Log.d("Submit", "Email: " + selectedEmail + ", OTP: " + enteredOtp);

                    if (AppStatus.getInstance(MobileLoginHRTC.this).isOnline()) {
                        UploadObject object = new UploadObject();
                        String encryptedMobile = "";
                        String encryptedEmail = "";
                        String encryptedOtp = "";

                        object.setUrl(Econstants.base_url);
                        object.setMethordName(Econstants.verifyOTPMethod);
                        object.setTasktype(TaskType.VERIFY_OTP_LOGIN);

                        try {
                            encryptedMobile = aesCrypto.encrypt(enteredMobile);
                            encryptedEmail = aesCrypto.encrypt(selectedEmail);
                            encryptedOtp = aesCrypto.encrypt(enteredOtp);

                            object.setParam("?mobile=" + URLEncoder.encode(encryptedMobile, "UTF-8")
                                    + "&otp=" + URLEncoder.encode(encryptedOtp, "UTF-8")
                                    + "&email=" + URLEncoder.encode(encryptedEmail, "UTF-8")
                            );

                        } catch (Exception e) {
                            Log.e("ENC/DEC Error", "ENC/DEC Error: " + e.getMessage());
                        }


                        new ShubhAsyncGet(
                                MobileLoginHRTC.this,
                                MobileLoginHRTC.this,
                                TaskType.VERIFY_OTP_LOGIN).
                                execute(object);
                    } else {
                        CD.showDialog(MobileLoginHRTC.this, Econstants.internetNotAvailable);
                    }


                } else {
                    CD.showDialog(MobileLoginHRTC.this, "Please select a valid HimAccess ID to login");
                }
            } else {
                CD.showDialog(MobileLoginHRTC.this, "Please enter the OTP");
            }

        });


        // ID + PASS LOGIN
        // Autocomplete with @himaccess.hp.gov.in
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                if (input.contains("@") && !input.endsWith("@himaccess.hp.gov.in")) {
                    userName.setAdapter(new ArrayAdapter<>(
                            MobileLoginHRTC.this,
                            android.R.layout.simple_dropdown_item_1line,
                            new String[]{input.split("@")[0] + "@himaccess.hp.gov.in"}
                    ));
                    userName.showDropDown();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        signInBtn.setOnClickListener(v -> {

            if (!userName.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                Log.i("ID", "ID: " + userName.getText().toString().trim());
                Log.i("Pass", "Pass: " + password.getText().toString());

                if (AppStatus.getInstance(MobileLoginHRTC.this).isOnline()) {
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

                    new ShubhAsyncPost(MobileLoginHRTC.this, MobileLoginHRTC.this, TaskType.LOGIN_HRTC_HIMACCESS).execute(uploadObject);
                    Log.i("JSON For Login: ", uploadObject.getParam());

                } else {
                    CD.showDialog(MobileLoginHRTC.this, "Internet not Available. Please Connect to the Internet and try again.");
                }
            } else {
                CD.showDialog(MobileLoginHRTC.this, "Please enter valid Username and Password");
            }

        });

        // Forgot Pass
        forgotPassBtn.setOnClickListener(v -> {
            showVisitConfimationDialog();
        });

    }


//    private boolean isPackageInstalled(String packageName, Context context) {
//        try {
//            context.getPackageManager().getPackageInfo(packageName, 0);
//            return true;
//        } catch (PackageManager.NameNotFoundException e) {
//            return false;
//        }
//    }

//    private void checkFaceRDInstalled(Context context) {
//        Log.d("PackageCheck", "Looking for: " + Econstants.FaceRD_PackageName);
//        Toast.makeText(context, "Checking installed packages...", Toast.LENGTH_SHORT).show();
//
//        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
//        for (PackageInfo pkg : packages) {
//            Log.d("PackageCheck", "Found: " + pkg.packageName);
//            if (pkg.packageName.equals(Econstants.FaceRD_PackageName)) {
//                Log.d("PackageCheck", "FaceRD is installed!");
//                Toast.makeText(context, "FaceRD is installed", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//
//        Log.d("PackageCheck", "FaceRD NOT found.");
//        Toast.makeText(context, "FaceRD is NOT installed", Toast.LENGTH_SHORT).show();
//    }


    public void getOtp() {
        if (AppStatus.getInstance(MobileLoginHRTC.this).isOnline()) {
            UploadObject object = new UploadObject();
            object.setUrl(Econstants.base_url);
            object.setMethordName(Econstants.getOTPMethod);
            object.setTasktype(TaskType.GET_OTP_LOGIN);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("mobileNo", Long.valueOf(mobileNumber.getText().toString().trim()));
                object.setParam(aesCrypto.encrypt(jsonObject.toString()));
            } catch (Exception e) {
                Log.e("Exception", "Exception: " + e.getMessage());
            }
            new ShubhAsyncPost(
                    MobileLoginHRTC.this,
                    MobileLoginHRTC.this,
                    TaskType.GET_OTP_LOGIN).
                    execute(object);
        } else {
            CD.showDialog(MobileLoginHRTC.this, Econstants.internetNotAvailable);
        }
    }


    // Custom methods
    public void getHimAccessToken(String email) {
        if (AppStatus.getInstance(MobileLoginHRTC.this).isOnline()) {
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

            new ShubhAsyncGet(MobileLoginHRTC.this, MobileLoginHRTC.this, TaskType.GET_TOKEN).execute(uploadObject);
            Log.i("JSON For Login: ", uploadObject.getParam());

        } else {
            CD.showDialog(MobileLoginHRTC.this, "Internet not Available. Please Connect to the Internet and try again.");
        }
    }

    // Get HRTC Token
    public void getHRTCToken(String email) {
        if (AppStatus.getInstance(MobileLoginHRTC.this).isOnline()) {
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

            new ShubhAsyncGet(MobileLoginHRTC.this, MobileLoginHRTC.this, TaskType.GET_HRTC_JWT_TOKEN).execute(uploadObject);

        } else {
            CD.showDialog(MobileLoginHRTC.this, "Internet not Available. Please Connect to the Internet and try again.");
        }
    }

    public void showVisitConfimationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Visit HimAccess")
                .setMessage("Visit https://himaccess.hp.gov.in to register or reset password?")
                .setPositiveButton("Visit", (dialog, which) -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://himaccess.hp.gov.in"));
                    startActivity(browserIntent);
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Get User Details..
    public void getUserDetails() {
        if (AppStatus.getInstance(MobileLoginHRTC.this).isOnline()) {
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

            new ShubhAsyncGet(MobileLoginHRTC.this, MobileLoginHRTC.this, TaskType.GET_USER_DETAILS).execute(uploadObject);
            Log.i("JSON For Login: ", uploadObject.getParam());

        } else {
            CD.showDialog(MobileLoginHRTC.this, "Internet not Available. Please Connect to the Internet and try again.");
        }
    }


    // Handle Permission Results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
    private void showPermissionRationaleDialog(String message, DialogInterface.OnClickListener
            onPositiveClickListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", onPositiveClickListener)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
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
        if (TaskType.GET_OTP_LOGIN == taskType) {
            SuccessResponse response = null;
            OTPObject userData = new OTPObject();
            response = JsonParse.getSuccessResponse(responseObject.getResponse());

            if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {

                if (response.getStatus().equalsIgnoreCase("OK")) {
                    try {
                        userData = JsonParse.parseOTPObject(aesCrypto.decrypt(response.getData()));
                    } catch (Exception e) {
                        Log.e("Exception", "Decryption Exception: " + e.getMessage());
                    }

                    CD.showDialog(this, userData.getOtp_status());
                    otp_field.setVisibility(View.VISIBLE);

                    List<String> emailList = new ArrayList<>();
                    emailList.add("-- SELECT --"); // Add default option

                    if (userData.getEmails() != null) {
                        emailList.addAll(userData.getEmails());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, emailList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    emails.setAdapter(adapter);
                    emails.setVisibility(View.VISIBLE);
                    getOtp.setVisibility(View.GONE);

                    enterOTPLayout.setVisibility(View.VISIBLE);
                    validateNLoginBtn.setVisibility(View.VISIBLE);
                    mobileNumber.setEnabled(false);

                } else {
                    CD.showDialog(MobileLoginHRTC.this, response.getMessage());
                }
            } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_BAD_REQUEST))) {
                CD.showDialog(this, response.getData());
            } else {
                CD.showDialog(MobileLoginHRTC.this, responseObject.getResponse());
            }
        }

        // Verify OTP
        else if (TaskType.VERIFY_OTP_LOGIN == taskType) {
            SuccessResponse response = null;
            OTPLoginUser otpLoginUser = null;

            if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                System.out.println(responseObject.getResponse());

                response = JsonParse.getSuccessResponse(responseObject.getResponse());
                if (response.getStatus().equalsIgnoreCase("OK")) {
                    System.out.println(response.getData());

                    try {
                        String decryptedResponse = aesCrypto.decrypt(response.getData());
                        otpLoginUser = JsonParse.parseOTPLoginUser(decryptedResponse);
                    } catch (Exception e) {
                        Log.e("Decryption Error", "Decryption Error: " + e.getMessage());
                    }

                    if (otpLoginUser != null) {
                        Preferences.getInstance().dateOfBirth = "Not Available";
                        Preferences.getInstance().userName = otpLoginUser.getEmployeeName();
                        Preferences.getInstance().mobileNumber = otpLoginUser.getMobile();
                        Preferences.getInstance().aadhaarNumber = otpLoginUser.getAadhaarNumber();
                        Preferences.getInstance().emailID = otpLoginUser.getOfficialEmail();
                        Preferences.getInstance().completeName = otpLoginUser.getEmployeeName(); // Complete Name
                        Preferences.getInstance().savePreferences(this);

                        getHRTCToken(otpLoginUser.getOfficialEmail()); // Get HRTC Token

                        getHimAccessToken(otpLoginUser.getOfficialEmail()); // Get HimAccessToken + User Details

                    } else {
                        CD.showDialog(MobileLoginHRTC.this, "Cannot get user information. Please try again.");
                        emails.setAdapter(null);
                        emails.setVisibility(View.GONE);
                        getOtp.setVisibility(View.VISIBLE);

                        enterOTPLayout.setVisibility(View.GONE);
                        validateNLoginBtn.setVisibility(View.GONE);
                        mobileNumber.setEnabled(true);
                    }

                } else {
                    CD.showDialog(MobileLoginHRTC.this, response.getMessage());
                }
            } else {
                CD.showDialog(MobileLoginHRTC.this, "Invalid OTP");
            }
        }


        // Password Login Task Type
        else if (TaskType.LOGIN_HRTC_HIMACCESS == taskType) {
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


                        Preferences.getInstance().dateOfBirth = himAccessUser.getDateOfBirth();
                        Preferences.getInstance().mobileNumber = himAccessUser.getMobile();
                        Preferences.getInstance().userName = himAccessUser.getDateOfBirth();
                        Preferences.getInstance().completeName = himAccessUser.getCn(); // Complete Name
                        Preferences.getInstance().savePreferences(this);


                        getHRTCToken(himAccessUser.getMail()); // Get HRTC Token
                        getHimAccessToken(himAccessUser.getMail()); // Get HimAccessToken + User Details

                    } else if (successResponse.getStatus().equals(Integer.toString(HttpsURLConnection.HTTP_GONE))) {
                        Log.i("Login Response Invalid ID/Pass", successResponse.getData());
                        CD.showDialog(this, "Please enter correct username and password");

                    } else {
                        CD.showDialog(this, successResponse.getMessage());
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
                    CD.showDialog(this, "Something went wrong. Check your connection.");
                }
            } else {
                CD.showDialog(this, "Something went wrong. Check your connection.");
            }
        }

        // Get HimAccess Token
        else if (TaskType.GET_TOKEN == taskType) {
            SuccessResponse response = null;

            if (responseObject != null) {

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {

                    response = JsonParse.getSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        tokenInfo = JsonParse.parseTokenInfo(response.getData());
                        Preferences.getInstance().tokenHimAccess = tokenInfo.getToken();
                        Preferences.getInstance().savePreferences(this);

                        if (tokenInfo != null) {
                            getUserDetails();
                        } else {
                            CD.showDialog(MobileLoginHRTC.this, "Not able to get user details");
                        }
                    } else {
                        CD.showDialog(MobileLoginHRTC.this, response.getMessage());
                    }
                } else {
                    Log.e("Not able to get token", "Not able to get HimAccess token because application is not Mapped");
                    CD.showDialog(MobileLoginHRTC.this, "Application not mapped. Please contact the nodal officer to map this application." + responseObject.getResponse());
                }
            } else {
                CD.showDialog(MobileLoginHRTC.this, "Result is null");
            }
        }

        // Get HRTC Token
        else if (TaskType.GET_HRTC_JWT_TOKEN == taskType) {
            SuccessResponse response = null;

            if (responseObject != null) {
                Log.i("Response", "Response Obj" + responseObject.toString());

                if (responseObject.getResponseCode().equalsIgnoreCase("200")) {
                    String HRTC_JWT = responseObject.getResponse().trim();
                    Log.e("HRTC JWT", "HRTC JWT: " + HRTC_JWT);

                    Preferences.getInstance().token = HRTC_JWT;
                    Preferences.getInstance().savePreferences(this);
                } else {
                    CD.showDialog(MobileLoginHRTC.this, "Not able to get get HRTC JWT Token. Check your connection.");
//                    Toast.makeText(this, "Not able to get JWT token", LENGTH_SHORT).show();
                }
            } else {
                CD.showDialog(MobileLoginHRTC.this, "No responseObject in HRTC JWT Token");
            }
        }

        // Get User Info
        else if (TaskType.GET_USER_DETAILS == taskType) {
            SuccessResponse response = null;

            if (responseObject != null) {

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

                            Preferences.getInstance().departmentId = himAccessUserInfo.getMainDepartmentPojo().getDepartmentId();
                            Preferences.getInstance().officeTypeName = himAccessUserInfo.getMainOfficeLevelPojo().getOfficeLevelName();

                            Preferences.getInstance().roleId = himAccessUserInfo.getAppRoleId();  // Normal Role ID
                            Preferences.getInstance().roleName = himAccessUserInfo.getRoleName();

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
                        CD.showDialog(MobileLoginHRTC.this, response.getMessage());
                    }
                } else {
                    CD.showDialog(MobileLoginHRTC.this, "Not able to get user details");
                }
            } else {
                CD.showDialog(MobileLoginHRTC.this, "Result is null");
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