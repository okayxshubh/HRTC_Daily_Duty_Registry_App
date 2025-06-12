package com.dit.hp.hrtc_app.AttendanceModule;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.constants.FaceAuthConstants;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.input.contract.CaptureResponse;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.FaceAuthObjectRequest;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.FaceAuthObjectResponse;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.kyc.KycResData;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.utils.Utils;
import com.dit.hp.hrtc_app.AttendanceModule.attendanceAdapters.GenericAdapterAuthentication;
import com.dit.hp.hrtc_app.AttendanceModule.attendanceAsync.GenericAsyncPostObjectFaceeKYC;
import com.dit.hp.hrtc_app.AttendanceModule.attendanceInterfaces.AsyncTaskListenerFile;
import com.dit.hp.hrtc_app.AttendanceModule.attendanceInterfaces.FaceEKYCInterface;
import com.dit.hp.hrtc_app.AttendanceModule.attendanceModals.AttendanceObject;
import com.dit.hp.hrtc_app.AttendanceModule.jsonAttendance.JsonParseAttendance;
import com.dit.hp.hrtc_app.Modals.HimAccessUser;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.R;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerPost;
import com.dit.hp.hrtc_app.json.JsonParse;
import com.dit.hp.hrtc_app.utilities.AppStatus;
import com.dit.hp.hrtc_app.utilities.DateTime;
import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;
import com.dit.hp.hrtc_app.utilities.SamplePresenter;
import com.kushkumardhawan.locationmanager.base.LocationBaseActivity;
import com.kushkumardhawan.locationmanager.configuration.Configurations;
import com.kushkumardhawan.locationmanager.configuration.LocationConfiguration;
import com.kushkumardhawan.locationmanager.constants.FailType;
import com.kushkumardhawan.locationmanager.constants.ProcessType;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.net.ssl.HttpsURLConnection;


public class AttendanceAuthentication extends LocationBaseActivity implements FaceEKYCInterface, ShubhAsyncTaskListenerPost, AsyncTaskListenerFile, SamplePresenter.SampleView {

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
    private AttendanceObject attendanceObject = new AttendanceObject();

    private SamplePresenter samplePresenter;
    public String userLocation = null;
    private ProgressDialog progressDialog;
    AESCrypto aesCrypto = new AESCrypto();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_auth);

        et_instructions = findViewById(R.id.et_instructions);

        Preferences.getInstance().loadPreferences(this); // Ensure preferences are loaded
        Log.e("Login User", "UserID" + Preferences.getInstance().userName);
        Log.e("Login User", "Mobile" + Preferences.getInstance().mobileNumber);

        // SERVICE CALL
        makeAadhaarNoServiceCall(Preferences.getInstance().emailID, Long.parseLong(Preferences.getInstance().mobileNumber));

        Log.e("Login User", "Mobile" + Preferences.getInstance().mobileNumber);


        aadhaar = findViewById(R.id.aadhaar);
        name = findViewById(R.id.name);
        mobile_citizen = findViewById(R.id.mobile_citizen);
        face_authentication = findViewById(R.id.face_auth_button);
        face_authentication.setClickable(true);

        name.setText(Preferences.getInstance().emailID);
        mobile_citizen.setText(Preferences.getInstance().mobileNumber);

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

                boolean isEkycDataAvailable = dataToSave != null && Econstants.isNotEmpty(dataToSave.getAadhaarNumber());
                if (isEkycDataAvailable) {
                    if (userLocation != null) {


                        attendanceObject.setLocation(userLocation.trim());
                        attendanceObject.setUserId(Preferences.getInstance().emailID);
                        attendanceObject.setName(Preferences.getInstance().completeName);
                        attendanceObject.setDateTime(DateTime.getCurrentDateTimeWithMillis());
                        System.out.println(attendanceObject.getAttendanceObjectJson());

                        CD.markAttendance(AttendanceAuthentication.this, attendanceObject);

                    } else {
                        CD.showDialog(AttendanceAuthentication.this, "Unable to get location");
                    }
                } else {
                    CD.showDialog(AttendanceAuthentication.this, "Please perform the eKYC or Authentication to proceed.");
                }
            }

        });


//        // FINAL SAVE VIA BROADCAST RECEIVER
//        mReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getAction() == "attendanceObject") {
//                    if (AppStatus.getInstance(AttendanceAuthentication.this).isOnline()) {
//                        Bundle extras = intent.getExtras();
//                        assert extras != null;
//                        AttendanceObject data = (AttendanceObject) extras.getSerializable("ATTENDANCE_OBJECT");
//                        Log.e("Data From Dialog", data.toString());
//
//                        /**
//                         * Save the Data
//                         */
//                        UploadObject object = new UploadObject();
//                        object.setUrl(Econstants.attendanceBaseURL);
//                        object.setTasktype(TaskType.SUBMIT_ATTENDANCE);
//                        object.setMethordName(Econstants.methodSaveAttendance);  //to be changed
//                        object.setParam(data.getAttendanceObjectJson().toString());
//                        System.out.println(data.getAttendanceObjectJson().toString());
//
//
//                        List<AadhaarDoc> imagePaths = new ArrayList<>();
//                        if (Econstants.isNotEmpty(data.getAadhaarEkyc().getAadhaarPhotoPath())) {
//                            int lastIndex = data.getAadhaarEkyc().getAadhaarPhotoPath().lastIndexOf("/");
//                            String lastString = data.getAadhaarEkyc().getAadhaarPhotoPath().substring(lastIndex + 1);
//                            AadhaarDoc doc = new AadhaarDoc();
//                            doc.setDocName(lastString);
//                            doc.setDocPath(data.getAadhaarEkyc().getAadhaarPhotoPath());
//                            imagePaths.add(doc);
//                        }
//
//                        if (Econstants.isNotEmpty(data.getAadhaarEkyc().getDocPath())) {
//                            int lastIndex = data.getAadhaarEkyc().getDocPath().lastIndexOf("/");
//                            String lastString = data.getAadhaarEkyc().getDocPath().substring(lastIndex + 1);
//                            AadhaarDoc doc = new AadhaarDoc();
//                            doc.setDocName(lastString);
//                            doc.setDocPath(data.getAadhaarEkyc().getDocPath());
//                            imagePaths.add(doc);
//                        }
//
//
//                        System.out.println("imagePaths" + imagePaths.toString());
//                        object.setImagePaths(imagePaths);
//                        System.out.println("object with images" + object.toString());
//
//
//                        Log.e("Data", data.getAttendanceObjectJson().toString());
//                        if (AppStatus.getInstance(AttendanceAuthentication.this).isOnline()) {
//                            new Generic_Async_Upload_Attendance(AttendanceAuthentication.this,
//                                    AttendanceAuthentication.this,
//                                    TaskType.SUBMIT_ATTENDANCE).execute(object);
//                        } else {
//                            CD.showDialog(AttendanceAuthentication.this, Econstants.internetNotAvailable);
//                        }
//                    } else {
//                        CD.showDialog(AttendanceAuthentication.this, "Please Connect to Internet and try again.");
//                    }
//
//                }
//            }
//        };


    }


    private void makeAadhaarNoServiceCall(String email, Long mobileNoLong) {
        // Route Service call
        try {
            if (AppStatus.getInstance(AttendanceAuthentication.this).isOnline()) {
                UploadObject object = new UploadObject();
//                object.setUrl(Econstants.sarvatra_url);
                object.setUrl("https://himparivarservices.hp.gov.in/sarvatra-api"); // Live always
                object.setMethordName("/api/getData?Tagname=" + URLEncoder.encode(aesCrypto.encrypt("getEmployeeOffice"), "UTF-8"));
                object.setMasterName(null);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("email", email);
                jsonObject.put("mobileNo", mobileNoLong);

                Log.e("JSON", "Get Details JSON: " + jsonObject.toString());

                String encJson = aesCrypto.encrypt(jsonObject.toString());
                Log.e("JSON", "Get Details JSON ENC: " + encJson);

                object.setParam(encJson);
                object.setTasktype(TaskType.GET_AADHAAR_NUMBER);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncPost(AttendanceAuthentication.this, AttendanceAuthentication.this, TaskType.GET_AADHAAR_NUMBER).execute(object);

            } else {
                CD.showDialog(AttendanceAuthentication.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AttendanceAuthentication.this, "Something Bad happened . Please reinstall the application and try again.");
        }

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

                // GET CORRECT AADHAAR NUMBER









                object.setAadhaarNumber("764500450582");  //HARDCODED AS MINE
                Toast.makeText(this,"Hardcoded Aadhaar No",LENGTH_LONG).show();
//                object.setAadhaarNumber(Preferences.getInstance().aadhaarNumber);  //Preferences.getInstance().beat_aadhaar
                System.out.println("Shubh Log: Aadhaar Number As: " + Preferences.getInstance().aadhaarNumber);











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
        Toast.makeText(this, errorMessage, LENGTH_SHORT).show();
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

    @Override
    protected void onRestart() {
        super.onRestart();
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
    }

    @Override
    protected void onPause() {

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }

        super.onPause();
        Preferences.getInstance().loadPreferences(this);
        dismissProgress();
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

//            Toast.makeText(AttendanceAuthentication.this, "Parsing eKYC", Toast.LENGTH_SHORT).show();
            kycResData = JsonParseAttendance.parseKYCResponse(object.getResponse());
//            Toast.makeText(AttendanceAuthentication.this, kycResData.getRet(), Toast.LENGTH_SHORT).show();

            if (kycResData.getRet().equalsIgnoreCase("Y")) {
                /**
                 * Show POP UP Data
                 */
                kycResData.setAadhaarNumber(aadhaar.getText().toString());

                System.out.println("KYCRES JSON" + kycResData.jsonKYCRES());
                System.out.println("KYCRES String" + kycResData.jsonKYCRES());

                dataToSave = kycResData;
                dataToSave.setAadhaarNumber(kycResData.getAadhaarNumber());

                attendanceObject.setAadhaarEkyc(kycResData);

                Toast.makeText(AttendanceAuthentication.this, "Authentication Successfully completed", LENGTH_SHORT).show();
//                CD.showDialog(AttendanceAuthentication.this, "E-KYC Successfully completed");

                CD.showeKYCDataFarmer(AttendanceAuthentication.this, kycResData);

                face_authentication.setClickable(false);
                face_authentication.setTooltipText("KYC Performed");
                face_authentication.setScaleType(ImageView.ScaleType.CENTER_CROP);
                // Setting Image
                String imagePath = kycResData.getAadhaarPhotoPath(); // Get Image Path
                Drawable drawable = Drawable.createFromPath(imagePath);
                face_authentication.setImageDrawable(drawable);

            } else {
                CD.showDialog(AttendanceAuthentication.this, "E-KYC Not Successfully completed. Please Retry");

            }
        }
    }


    // GET AADHAAR NUMBERS
    @Override
    public void onTaskCompleted(ResponsePojoGet result, TaskType taskType) throws JSONException {

        if (TaskType.GET_AADHAAR_NUMBER == taskType) {
            SuccessResponse response = null;

            if (result != null) {
                Log.i("AttendanceAuthentication", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        if (response.getData().equalsIgnoreCase("No records found")){
                            CD.showDismissActivityDialog(this, "Aadhaar number cannot be fetched.");
                        }
                        //
                        else {
                            HimAccessUser himAccessUser = JsonParse.parseAadhaarNumberForHimAccessUser(response.getData());

                            if (himAccessUser != null) {
                                Preferences.getInstance().aadhaarNumber = himAccessUser.getAadhaarNumber();
                                Preferences.getInstance().savePreferences(this);
                                aadhaar.setText(himAccessUser.getAadhaarNumber());

                                Preferences.getInstance().loadPreferences(this); // Reload Prefs

                            } else {
                                CD.showDialog(AttendanceAuthentication.this, "Not able to get aadhaar number");
                            }
                        }

                    } else {
                        CD.showDialog(AttendanceAuthentication.this, response.getMessage());
                    }
                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(AttendanceAuthentication.this, "Not able to connect to the server");
                }
            } else {
                CD.showDialog(AttendanceAuthentication.this, "Result is null");
            }
        }
    }


    // SUBMIT ATTENDANCE
    @Override
    public void onTaskCompleted(String object, TaskType taskType) throws JSONException {

        if (TaskType.SUBMIT_ATTENDANCE == taskType) {
            SuccessResponse response = null;
            response = JsonParse.getSuccessResponseFinal(object);
            if (response.getStatus().equalsIgnoreCase("OK")) {
                CD.showDismissActivityDialog(this, response.getMessage());
            } else {
                CD.showDismissActivityDialog(this, response.getMessage());
            }
        }

    }
}
