package com.dit.hp.hrtc_app.AttendanceModule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.constants.FaceAuthConstants;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.input.contract.CaptureResponse;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.FaceAuthObjectRequest;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.FaceAuthObjectResponse;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.kyc.KycResData;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.utils.Utils;
import com.dit.hp.hrtc_app.AttendanceModule.attendanceAdapters.GenericAdapterAuthentication;
import com.dit.hp.hrtc_app.AttendanceModule.attendanceAsync.GenericAsyncPostObjectFaceeKYC;
import com.dit.hp.hrtc_app.AttendanceModule.attendanceInterfaces.FaceEKYCInterface;
import com.dit.hp.hrtc_app.AttendanceModule.jsonAttendance.JsonParseAttendance;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.R;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.utilities.AppStatus;
import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;
import com.dit.hp.hrtc_app.utilities.SamplePresenter;
import com.kushkumardhawan.locationmanager.base.LocationBaseActivity;
import com.kushkumardhawan.locationmanager.configuration.Configurations;
import com.kushkumardhawan.locationmanager.configuration.LocationConfiguration;
import com.kushkumardhawan.locationmanager.constants.FailType;
import com.kushkumardhawan.locationmanager.constants.ProcessType;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;


public class AttendanceAuthentication extends LocationBaseActivity implements FaceEKYCInterface, SamplePresenter.SampleView {

    Button back, verify;
    ImageView face_authentication;
    private TextView name, aadhaar, mobile_citizen, et_instructions;
    private GenericAdapterAuthentication adapter_autnetication = null;
    private String AuthenticationTypeGlobal, AuthenticationIdGlobal = null;
    private String Global_OTP, Global_transactionId, Global_KEYPATH = null;
    private KycResData kycResData = null;
    CustomDialog CD = new CustomDialog();
    private String[] list;
    public static final Integer CAPTURE_REQ_CODE = 123;
    KycResData dataToSave;
    private BroadcastReceiver mReceiver;

    private SamplePresenter samplePresenter;
    public String userLocation = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_auth);

        Preferences.getInstance().loadPreferences(this);
        et_instructions = findViewById(R.id.et_instructions);

        Preferences.getInstance().loadPreferences(this); // Ensure preferences are loaded
        Log.e("Login User", "UserID" + Preferences.getInstance().userName);
        Log.e("Login User", "Mobile" + Preferences.getInstance().mobileNumber);


        aadhaar = findViewById(R.id.aadhaar);
        name = findViewById(R.id.name);
        mobile_citizen = findViewById(R.id.mobile_citizen);
        face_authentication = findViewById(R.id.face_auth_button);

        back = findViewById(R.id.back);
        verify = findViewById(R.id.verify);

        try {
            samplePresenter = new SamplePresenter(this);
            getLocation();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        et_instructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CD.showDialog(AttendanceAuthentication.this, Econstants.ekyc_consent);
            }
        });


//        face_authentication.setOnClickListener(v -> {
//            try {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Econstants.FaceRD_PackageName)));
//            } catch (ActivityNotFoundException e) {
//                // Play Store app not found, open in browser
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + Econstants.FaceRD_PackageName)));
//            }
//        });

        face_authentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!aadhaar.getText().toString().trim().isEmpty()) {
                    if (!name.getText().toString().trim().isEmpty()) {
                        if (!mobile_citizen.getText().toString().trim().isEmpty()) {
                            Intent intent = new Intent(FaceAuthConstants.CAPTURE_INTENT);
                            String transactionId = Utils.createTxn(Utils.getCurrentTimestamp());
                            try {
                                intent.putExtra(FaceAuthConstants.CAPTURE_INTENT_REQUEST, Utils.createPidOptionForAuth(transactionId));
                            } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                                throw new RuntimeException(e);
                            }
                            startActivityForResult(intent, CAPTURE_REQ_CODE);
                        } else {
                            CD.showDialog(AttendanceAuthentication.this, "Pleases Enter valid 10 digit mobile number");
                        }
                    } else {
                        CD.showDialog(AttendanceAuthentication.this, "Pleases Enter Your Name");
                    }
                } else {
                    CD.showDialog(AttendanceAuthentication.this, "Pleases Enter valid 12 Digit Aadhaar number");
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AttendanceAuthentication.this.finish();
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if (aadhaarNumber.length() == 12) {
                boolean isEkycDataAvailable = dataToSave != null && Econstants.isNotEmpty(dataToSave.getAadhaarNumber());
                if (isEkycDataAvailable) {

//                        Intent mainIntent = new Intent(AttendanceAuthentication.this, MapsActivity.class); //MainActivity
//                        mainIntent.putExtra("KYC_OBJ", dataToSave);
//                        AttendanceAuthentication.this.startActivity(mainIntent);

                    CD.showDialog(AttendanceAuthentication.this, "PROCEED..");

                } else {
                    CD.showDialog(AttendanceAuthentication.this, "Please perform the eKYC or Authentication of the Beneficiary.");
                }
            }

        });

    }



//    private boolean isFaceRDInstalled() {
//        try {
//            ApplicationInfo info = getPackageManager().getApplicationInfo("in.gov.uidai.facerd", 0);
//            return info.enabled;
//        } catch (PackageManager.NameNotFoundException e) {
//            return false;
//        }
//    }







    private void handleCaptureResponse(String captureResponse) throws Exception {
        CaptureResponse response = CaptureResponse.fromXML(captureResponse);

        if (response.isSuccess()) {

            if (AppStatus.getInstance(AttendanceAuthentication.this).isOnline()) {
                FaceAuthObjectRequest object = new FaceAuthObjectRequest();
                object.setUrl("http://himaua.hp.gov.in:8080/auac25");
                object.setMethordName("/auaservice/authenticateKyc");
                object.setTasktype(TaskType.FACE_AUTH);




                // INVALID AADHAAR
//                object.setAadhaarNumber(Preferences.getInstance().beat_aadhaar);  //Preferences.getInstance().beat_aadhaar
                object.setIntentPIDXML(response);
                new GenericAsyncPostObjectFaceeKYC(
                        AttendanceAuthentication.this,
                        AttendanceAuthentication.this,
                        TaskType.FACE_AUTH).
                        execute(object);
            } else {
                CD.showDialog(AttendanceAuthentication.this, Econstants.internetNotAvailable);
            }
        }
    }

    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (data != null) {
            if (requestCode == CAPTURE_REQ_CODE && resultCode == Activity.RESULT_OK) {
                try {
                    handleCaptureResponse(data.getStringExtra(FaceAuthConstants.CAPTURE_INTENT_RESPONSE_DATA));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        }


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onResume() {
        super.onResume();
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
        registerReceiver(mReceiver, new IntentFilter("surveyObjectPDS"));

        if (getLocationManager().isWaitingForLocation()
                && !getLocationManager().isAnyDialogShowing()) {
            displayProgress();
        }
    }


    /**
     * Location Details
     */
    /**
     * Location Interface Methords
     *
     * @return
     */
    @Override
    public LocationConfiguration getLocationConfiguration() {
        return Configurations.defaultConfiguration("Permission Required !", "GPS needs to be turned on.");
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
    protected void onRestart() {
        super.onRestart();
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
    }

    @Override
    protected void onPause() {
        // unregisterReceiver(mReceiver);
        super.onPause();
        Preferences.getInstance().loadPreferences(this);
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
        //locationText.setText(text);
        Log.e("Location GPS", text);
        userLocation = text;
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
    public void onTaskCompleted(FaceAuthObjectResponse object, TaskType taskType) throws Exception {

        if (TaskType.FACE_AUTH == taskType) {

            Toast.makeText(AttendanceAuthentication.this, "Parsing eKYC", Toast.LENGTH_SHORT).show();
            kycResData = JsonParseAttendance.parseKYCResponse(object.getResponse());
            Toast.makeText(AttendanceAuthentication.this, kycResData.getRet(), Toast.LENGTH_SHORT).show();
            if (kycResData.getRet().equalsIgnoreCase("Y")) {
                CD.showDialog(AttendanceAuthentication.this, "E-KYC Successfully completed");
                /**
                 * Show POP UP Data
                 */
                kycResData.setAadhaarNumber(aadhaar.getText().toString());

                System.out.println("KYCRES JSON" + kycResData.jsonKYCRES());
                System.out.println("KYCRES String" + kycResData.jsonKYCRES());

                dataToSave = kycResData;
                dataToSave.setAadhaarNumber(kycResData.getAadhaarNumber());
                CD.showDialog(this,"KYC DONE: SHOW KYC DATA HERE");
//                CD.showeKYCDataFarmer(AttendanceAuthentication.this, kycResData);

            } else {
                CD.showDialog(AttendanceAuthentication.this, "E-KYC Not Successfully completed. Please Retry");

            }
        }
    }
}
