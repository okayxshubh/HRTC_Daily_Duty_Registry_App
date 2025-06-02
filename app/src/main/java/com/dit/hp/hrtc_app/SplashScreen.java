package com.dit.hp.hrtc_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SplashScreen extends AppCompatActivity {

    ProgressBar progressBar;
    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        progressBar = findViewById(R.id.loadingProgress);
        progressBar.setVisibility(View.VISIBLE);

//        // Light mode by default
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);



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