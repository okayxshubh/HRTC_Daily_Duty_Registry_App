package com.dit.hp.hrtc_app;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dit.hp.hrtc_app.AttendanceModule.AttendanceAuthentication;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.utilities.Preferences;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

public class AttendanceHome extends BaseDrawerActivity {

    // Show What Layout
    @Override
    protected int getLayoutId() {
        return R.layout.activity_attendance_home_screen; // layout for this screen
    }

    @Override
    protected int getNavMenuId() {
        return R.id.nav_attendance; // the nav menu item to highlight
    }


    CardView cardView1, cardView2;
    ImageButton profileBtn;
    TextView welcomeTV, depotNameTV, roleIdTV;
    CustomDialog CD = new CustomDialog();
    TextView headingTV;

    private static final int UPDATE_REQUEST_CODE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load saved preferences at the very beginning
        Preferences.getInstance().loadPreferences(this);

        roleIdTV = findViewById(R.id.roleIdTV);
        welcomeTV = findViewById(R.id.headTV);
        depotNameTV = findViewById(R.id.depotLocationTV);

//        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        profileBtn = findViewById(R.id.profileB);

        // Reload user details to update the UI
        reloadUserDetails();

//        // Card 1 See
//        cardView1.setOnClickListener(v -> {
//            CD.showDialog(this, "Coming Soon..");
//        });

        // Card 2 Mark
        cardView2.setOnClickListener(v -> {
            com.google.android.gms.location.LocationRequest locationRequest = com.google.android.gms.location.LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(5000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true); // force dialog
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(locationSettingsResponse -> {
                // Location is ON
                Intent intent = new Intent(AttendanceHome.this, AttendanceAuthentication.class);
                startActivityForResult(intent, UPDATE_REQUEST_CODE);
            });

            task.addOnFailureListener(e -> {
                if (e instanceof ResolvableApiException) {
                    try {
                        // Show Google dialog to enable location
                        ((ResolvableApiException) e).startResolutionForResult(AttendanceHome.this, 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        });

    }


//    CUSTOM METHODS ########################################################################################################################################


    // Helper function to show rationale dialog
    private void showPermissionRationaleDialog(String message, DialogInterface.OnClickListener onPositiveClickListener) {
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
            getLocation(); // Already granted
        }
    }

    private void reloadUserDetails() {

        // Welcome Message
        String userName = Preferences.getInstance().userName != null ? Preferences.getInstance().userName : "Guest";
        welcomeTV.setText("Welcome " + userName);

        // Depot
        String regionalOfficeName = Preferences.getInstance().regionalOfficeName;
        depotNameTV.setText(regionalOfficeName != null && !regionalOfficeName.isEmpty() ? "Depot: " + regionalOfficeName : "Depot: Not Available");

        // Role
        String roleName = Preferences.getInstance().roleName;
        roleIdTV.setText(roleName != null && !roleName.isEmpty() ? "Role: " + roleName : "No Info Available");

    }

//    ##########################################################################################################################


    // Handle Permission Results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
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



    @Override
    protected void onRestart() {
        super.onRestart();
        setHeading("Attendance"); // Custom Method in BaseDrawerActivity
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
        reloadUserDetails(); // Reload details to update UI
    }

    @Override
    protected void onResume() {
        super.onResume();
        setHeading("Attendance"); // Custom Method in BaseDrawerActivity
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
        reloadUserDetails(); // Reload details to update UI
    }
}