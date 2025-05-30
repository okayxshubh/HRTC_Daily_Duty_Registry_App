package com.dit.hp.hrtc_app;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

    TextView username, deptName, role, designation, email, employeeId, joinDate, phone;

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
        joinDate = findViewById(R.id.user_join_date);
        phone = findViewById(R.id.user_phone);

        username.setText(Preferences.getInstance().userName != null ? Preferences.getInstance().userName : "Not Available");
        deptName.setText(Preferences.getInstance().depotName != null ? Preferences.getInstance().depotName : "Not Available");
        role.setText(Preferences.getInstance().roleName != null ? Preferences.getInstance().roleName : "Not Available");
        email.setText(Preferences.getInstance().emailID != null ? Preferences.getInstance().emailID : "Not Available");


    }


//    CUSTOM METHODS ########################################################################################################################################


    @Override
    protected void onRestart() {
        super.onRestart();
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
    }

    @Override
    protected void onResume() {
        super.onResume();
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
    }
}