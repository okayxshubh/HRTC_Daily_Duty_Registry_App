package com.dit.hp.hrtc_app;

import static android.widget.Toast.LENGTH_SHORT;

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
import android.widget.Toast;

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

public class MarkAttendance extends LocationBaseActivity implements SamplePresenter.SampleView, ShubhAsyncTaskListenerPost, ShubhAsyncTaskListenerGet {

    AESCrypto aesCrypto = new AESCrypto();

    Button back, proceed;
    TextView locationTV;
    LinearLayout layoutLocationTV;
    ImageButton locationBtn;

    CustomDialog CD = new CustomDialog();

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

    ImageView mainImageView;
    String GLOBAL_LOCATION_STR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        samplePresenter = new SamplePresenter(this);
        locationBtn = findViewById(R.id.getLocationBtn);
        layoutLocationTV = findViewById(R.id.layoutLocationTV);

        back = findViewById(R.id.backBtn);
        proceed = findViewById(R.id.proceedBtn);


        mainImageView.setOnClickListener(view -> {
            // If Aadhaar Number present then perform face auth
        });

        locationBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Getting Location", LENGTH_SHORT).show();
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


        // Back Btn
        back.setOnClickListener(v -> {
            MarkAttendance.this.finish();
        });

        // Save Btn (Proceed Button)
        proceed.setOnClickListener(v -> {

        });

    }

    // Exit confirmation dialog
    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to go back? Any progress made will be lost.").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MarkAttendance.this.finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog, do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onTaskCompleted(ResponsePojoGet responseObject, TaskType taskType) throws JSONException {

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