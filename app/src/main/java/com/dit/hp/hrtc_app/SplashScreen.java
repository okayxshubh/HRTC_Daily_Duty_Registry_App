package com.dit.hp.hrtc_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.dit.hp.hrtc_app.utilities.Preferences;

public class SplashScreen extends AppCompatActivity {

    ProgressBar progressBar;
    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        progressBar = findViewById(R.id.loadingProgress);
        progressBar.setVisibility(View.VISIBLE);


        Preferences.getInstance().loadPreferences(this);

        if (Preferences.getInstance().isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            Toast.makeText(this, "Dark Mode Enabled", Toast.LENGTH_SHORT).show();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//            Toast.makeText(this, "Light Mode Enabled", Toast.LENGTH_SHORT).show();
        }


        if (!isTaskRoot()) {
            // Prevent duplicate splash when activity is relaunched
            finish();
            return;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreen.this, LoginHRTC.class);
                startActivity(mainIntent);
                finish();
            }
        }, 2000);
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        finishAffinity(); // Close app instead of going back to Splash
    }

}