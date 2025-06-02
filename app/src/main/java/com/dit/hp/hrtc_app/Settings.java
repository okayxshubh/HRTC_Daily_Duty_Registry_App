package com.dit.hp.hrtc_app;

import android.os.Build;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

    Switch switchDarkMode;
    boolean isDark; // Flag to track dark mode state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load saved preferences at the very beginning
        Preferences.getInstance().loadPreferences(this);
        isDark = Preferences.getInstance().isDarkMode;


        versionTV = findViewById(R.id.versionTV);
        compatibilityTV = findViewById(R.id.compatibilityTV);
        switchDarkMode = findViewById(R.id.switchDarkMode);


        versionTV.setText(CommonUtils.getVersionInfo(Settings.this)); // Helper Class With Static Methods
        compatibilityTV.setText("Android " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")");

        compatibilityTV.setText("Running: Android " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")\n" +
                CommonUtils.getMinMaxSdkInfo(Settings.this));


//        // On toggle switch
//        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
//
//            Preferences.getInstance().isDarkMode = isChecked;
//            Preferences.getInstance().savePreferences(this);
//
//            // Optional: restart to apply immediately
//            recreate();
//        });

    }


//    CUSTOM METHODS ########################################################################################################################################

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