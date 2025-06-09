package com.dit.hp.hrtc_app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.utilities.CommonUtils;
import com.dit.hp.hrtc_app.utilities.Preferences;


public class Settings extends BaseDrawerActivity {

    // Show What Layout
    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings; // layout for this screen
    }

    @Override
    protected int getNavMenuId() {
        return R.id.nav_settings; // the nav menu item to highlight
    }

    CustomDialog CD = new CustomDialog();
    TextView versionTV, compatibilityTV;

    Switch switchDarkMode, switchNotifications, switchAutoUpdate, switchLocation, switchBackgroundSync;
    boolean isDark; // Flag to track dark mode state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load saved preferences at the very beginning
        Preferences.getInstance().loadPreferences(this);

        // DARK MODE TOGGLE
        isDark = Preferences.getInstance().isDarkMode;
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchDarkMode.setChecked(isDark); // set switch state dark mode

        if (Preferences.getInstance().isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


        versionTV = findViewById(R.id.versionTV);
        compatibilityTV = findViewById(R.id.compatibilityTV);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchAutoUpdate = findViewById(R.id.switchAutoUpdate);
        switchLocation = findViewById(R.id.switchLocation);
        switchBackgroundSync = findViewById(R.id.switchBackgroundSync);


        versionTV.setText(CommonUtils.getVersionInfo(Settings.this)); // Helper Class With Static Methods
        compatibilityTV.setText("Android " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")");

        compatibilityTV.setText("Running: Android " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")\n" +
                CommonUtils.getMinMaxSdkInfo(Settings.this));


        // On toggle switch
//        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
//
//            Preferences.getInstance().isDarkMode = isChecked;
//            Preferences.getInstance().savePreferences(this);
////            showRestartConfimationDialog();
//        });


        switchDarkMode.setOnClickListener(v -> {
            Toast.makeText(this,"Coming Soon",Toast.LENGTH_SHORT).show();
        });
        switchNotifications.setOnClickListener(v -> {
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
        });
        switchAutoUpdate.setOnClickListener(v -> {
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
        });

        switchLocation.setOnClickListener(v -> {
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
        });
        switchBackgroundSync.setOnClickListener(v -> {
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
        });


    }


//    CUSTOM METHODS ########################################################################################################################################

    private void showRestartConfimationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Restart App");
        builder.setMessage("Are you sure you want to restart the app to apply dark mode?");
        builder.setPositiveButton("Restart", (dialog, which) -> {
            // Restart the app
            Intent intent = new Intent(Settings.this, Settings.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Not Now", (dialog, which) -> {
            // Dismiss the dialog
            dialog.dismiss();
        });
        builder.show();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        setHeading("Settings"); // Custom Method in BaseDrawerActivity
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
    }

    @Override
    protected void onResume() {
        super.onResume();
        setHeading("Settings"); // Custom Method in BaseDrawerActivity
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
    }
}