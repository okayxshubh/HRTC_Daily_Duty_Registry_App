package com.dit.hp.hrtc_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.utilities.Preferences;

public class ProfileScreen extends BaseDrawerActivity {

    // Show What Layout
    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_screen; // layout for this screen
    }

    @Override
    protected int getNavMenuId() {
        return R.id.nav_profile; // the nav menu item to highlight
    }

    CardView cardView1, cardView2;
    ImageButton profileBtn;
    CustomDialog CD = new CustomDialog();

    TextView username, deptName, role, designation, email, employeeId, dob, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load saved preferences at the very beginning
        Preferences.getInstance().loadPreferences(this);

        username = findViewById(R.id.user_name);
        deptName = findViewById(R.id.user_department);
        role = findViewById(R.id.user_role);
        designation = findViewById(R.id.user_designation);
        email = findViewById(R.id.user_email);
        employeeId = findViewById(R.id.user_employee_id);
        phone = findViewById(R.id.user_phone);
        dob = findViewById(R.id.dob);

        username.setText(Preferences.getInstance().userName != null ? Preferences.getInstance().userName : "Not Available");

        deptName.setText(Preferences.getInstance().officeTypeName != null ? "Office Type: " + Preferences.getInstance().officeTypeName : "Not Available");
        designation.setText(Preferences.getInstance().designationName != null ? "Designation: " +Preferences.getInstance().designationName : "Not Available");
        role.setText(Preferences.getInstance().roleName != null ? "Role: " +Preferences.getInstance().roleName : "Not Available");

        email.setText(Preferences.getInstance().emailID != null ? Preferences.getInstance().emailID : "Not Available");

        employeeId.setText(Preferences.getInstance().empId != null ? String.valueOf(Preferences.getInstance().empId) : "Not Available");
        phone.setText(Preferences.getInstance().mobileNumber != null ? Preferences.getInstance().mobileNumber : "Not Available");
        dob.setText(Preferences.getInstance().dateOfBirth != null ? Preferences.getInstance().dateOfBirth : "Not Available");



    }

//    CUSTOM METHODS ########################################################################################################################################


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileScreen.this, Homescreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setHeading("Profile"); // Custom Method in BaseDrawerActivity
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
    }

    @Override
    protected void onResume() {
        super.onResume();
        setHeading("Profile"); // Custom Method in BaseDrawerActivity
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
    }


}