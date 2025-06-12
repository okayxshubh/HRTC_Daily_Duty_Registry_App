package com.dit.hp.hrtc_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.dit.hp.hrtc_app.utilities.Preferences;
import com.google.android.material.navigation.NavigationView;

public abstract class BaseDrawerActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    private ImageView menuBtn;
    private View header;
    private TextView headingTV;
    private TextView headerNameTV, headerRoleTV, headerOfficeTV;

    /**
     * Screen-specific layout to insert in FrameLayout
     */
    protected abstract @LayoutRes int getLayoutId();

    /**
     * Menu id that represents this screen
     */
    protected abstract @IdRes int getNavMenuId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_drawer);

        // Load prefs
        Preferences.getInstance().loadPreferences(this);

        // Inflate screen content
        View content = getLayoutInflater().inflate(getLayoutId(), null);
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        contentFrame.addView(content);

        headingTV = findViewById(R.id.headingTV);

        // Setup drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Find Nav Header after navigationView is declared
        header = navigationView.getHeaderView(0);
        headerNameTV = header.findViewById(R.id.headerNameTV);
        headerRoleTV = header.findViewById(R.id.headerRoleTV);
        headerOfficeTV = header.findViewById(R.id.headerOfficeTV);

        reloadUserDetails();

        menuBtn = findViewById(R.id.menuBtn);
        if (menuBtn != null) {
            menuBtn.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        navigationView.setCheckedItem(getNavMenuId());

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);

            Intent intent = null;

            if (id == R.id.nav_home) {
                intent = new Intent(this, Homescreen.class);
            } else if (id == R.id.nav_profile) {
                intent = new Intent(this, ProfileScreen.class);
            } else if (id == R.id.nav_attendance) {
                intent = new Intent(this, AttendanceHome.class);
            } else if (id == R.id.nav_payslip) {
                intent = new Intent(this, SalaryHome.class);
            }else if (id == R.id.nav_settings) {
                intent = new Intent(this, Settings.class);
            }else if (id == R.id.nav_notifics) {
                intent = new Intent(this, Notifications.class);
            }
            else if (id == R.id.nav_logout) {
                showLogoutConfirmationDialog();
                return true;
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
//                this.finish(); // Finish Previous Activity
            }



            return true;
        });

    }


    // Custom Method to change top heading
    protected void setHeading(String title) {
        if (headingTV != null) {
            headingTV.setText(title);
        }
    }


    private void reloadUserDetails() {
        Preferences pref = Preferences.getInstance();

        String name = pref.userName != null ? pref.userName : "Guest";
        String depot = pref.depotName != null && !pref.depotName.isEmpty() ? pref.depotName : "No Info";
        String role = pref.roleName != null && !pref.roleName.isEmpty() ? pref.roleName : "NA";

        headerNameTV.setText(name);
        headerRoleTV.setText(role);
        headerOfficeTV.setText(depot);
    }


    private void showLogoutConfirmationDialog() {
        new android.app.AlertDialog.Builder(this)
                .setMessage("Are you sure you want to logout as the current user?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    Preferences.getInstance().clearPreferences(this);

                    Intent intent = new Intent(BaseDrawerActivity.this, LoginHRTC.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                })
                .setNegativeButton("No", null)
                .create()
                .show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START); // Close nav drawer first
        } else {
            super.onBackPressed(); // Otherwise, do normal back action
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
        reloadUserDetails(); // Reload details to update UI

        // Nav Bar Selected Item
        if (navigationView != null) {
            navigationView.setCheckedItem(getNavMenuId());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
        reloadUserDetails(); // Reload details to update UI

        // Nav Bar Selected Item
        if (navigationView != null) {
            navigationView.setCheckedItem(getNavMenuId());
        }
    }


}


