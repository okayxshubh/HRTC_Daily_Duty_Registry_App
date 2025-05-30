package com.dit.hp.hrtc_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.utilities.Preferences;

public class SalaryHome extends BaseDrawerActivity {

    // Show What Layout
    @Override
    protected int getLayoutId() {
        return R.layout.activity_salary_home_screen; // layout for this screen
    }

    @Override
    protected int getNavMenuId() {
        return R.id.nav_payslip; // the nav menu item to highlight
    }

    CardView cardView1, cardView2;
    ImageButton profileBtn;
    TextView welcomeTV, depotNameTV, roleIdTV;
    CustomDialog CD = new CustomDialog();
    TextView headingTV; // Super.onCreate used of BaseDrawerActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load saved preferences at the very beginning
        Preferences.getInstance().loadPreferences(this);

        roleIdTV = findViewById(R.id.roleIdTV);
        welcomeTV = findViewById(R.id.headTV);
        depotNameTV = findViewById(R.id.depotLocationTV);

        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        profileBtn = findViewById(R.id.profileB);

        // Reload user details to update the UI
        reloadUserDetails();

        // Card 1 click listener (Add Daily Record)
        cardView1.setOnClickListener(v -> {
            CD.showDialog(this, "Coming Soon..");
        });

        // Card 2 click listener (Daily Duty Register Cards)
        cardView2.setOnClickListener(v -> {
            CD.showDialog(this, "Coming Soon..");
        });

    }


//    CUSTOM METHODS ########################################################################################################################################


    private void reloadUserDetails() {

        // Welcome Message
        String userName = Preferences.getInstance().userName != null ? Preferences.getInstance().userName : "Guest";
        welcomeTV.setText("Welcome " + userName);

        // Depot
        String depotName = Preferences.getInstance().depotName;
        depotNameTV.setText(depotName != null && !depotName.isEmpty() ? "Office: " + depotName : "No Info Available");

        // Role
        String roleName = Preferences.getInstance().roleName;
        roleIdTV.setText(roleName != null && !roleName.isEmpty() ? "Role: " + roleName : "No Info Available");

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
        reloadUserDetails(); // Reload details to update UI
    }

    @Override
    protected void onResume() {
        super.onResume();
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
        reloadUserDetails(); // Reload details to update UI
    }
}