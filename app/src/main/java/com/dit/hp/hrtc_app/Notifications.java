package com.dit.hp.hrtc_app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.dit.hp.hrtc_app.Adapters.ImageSliderAdapter;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.utilities.CommonUtils;
import com.dit.hp.hrtc_app.utilities.Preferences;

import java.util.Arrays;
import java.util.List;


public class Notifications extends BaseDrawerActivity {

    // Show What Layout
    @Override
    protected int getLayoutId() {
        return R.layout.activity_notifications; // layout for this screen
    }

    @Override
    protected int getNavMenuId() {
        return R.id.nav_notifics; // the nav menu item to highlight
    }

    CustomDialog CD = new CustomDialog();
    ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load saved preferences at the very beginning
        Preferences.getInstance().loadPreferences(this);

        viewPager2 = findViewById(R.id.alerts_slider);
        List<Integer> images = Arrays.asList(
                R.drawable.slider_img1,
                R.drawable.slider_img2,
                R.drawable.slider_img3
        );

        ImageSliderAdapter adapter = new ImageSliderAdapter(images);
        viewPager2.setAdapter(adapter);

        // Auto-slide logic
        Handler sliderHandler = new Handler(Looper.getMainLooper());
        Runnable sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int nextItem = (viewPager2.getCurrentItem() + 1) % images.size();
                viewPager2.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 3000); // 3s interval
            }
        };

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });

        sliderHandler.postDelayed(sliderRunnable, 3000); // Start auto-scroll




        // Animation Effect View Pager
        viewPager2.setPageTransformer(new MarginPageTransformer(40)); // smooth slide spacing
        viewPager2.setPageTransformer((page, position) -> {
            float scale = 0.85f + (1 - Math.abs(position)) * 0.15f;
            page.setScaleY(scale);
            page.setAlpha(0.5f + (1 - Math.abs(position)));
        });
        viewPager2.setPageTransformer((page, position) -> {
            if (position <= 0) {
                page.setTranslationX(0);
                page.setScaleX(1);
                page.setScaleY(1);
                page.setAlpha(1);
            } else if (position <= 1) {
                page.setAlpha(1 - position);
                page.setTranslationX(page.getWidth() * -position);
                page.setScaleX(0.75f + (1 - position) * 0.25f);
                page.setScaleY(0.75f + (1 - position) * 0.25f);
            }
        });




    }


//    CUSTOM METHODS ########################################################################################################################################



    @Override
    protected void onRestart() {
        super.onRestart();
        setHeading("Alerts & Notifications"); // Custom Method in BaseDrawerActivity
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
    }

    @Override
    protected void onResume() {
        super.onResume();
        setHeading("Alerts & Notifications"); // Custom Method in BaseDrawerActivity
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
    }
}