package com.dit.hp.hrtc_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;

public class ManageEntities extends AppCompatActivity {

    CardView cardView1, cardView2, cardView3, cardView5, cardView6, cardView7, cardView8,  aboutUsCard;
    ImageButton profileBtn;
    TextView welcomeTV, depotLocationTV, roleIdTV;
    CustomDialog CD = new CustomDialog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_entities);

        roleIdTV = findViewById(R.id.roleIdTV);
        welcomeTV = findViewById(R.id.headTV);
        depotLocationTV = findViewById(R.id.depotLocationTV);
        profileBtn = findViewById(R.id.profileB);

//        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        cardView3 = findViewById(R.id.cardView3);
        cardView6 = findViewById(R.id.cardView6);
        cardView7 = findViewById(R.id.cardView7);
        cardView8 = findViewById(R.id.cardView8);


        reloadUserDetails();

        aboutUsCard = findViewById(R.id.aboutUsCard);


        profileBtn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.logout) {
                    showLogoutConfirmationDialog();
                    return true;
                }
                return false;
            });
            popupMenu.show();

        });

//        cardView1.setOnClickListener(v -> {
//            Intent intent = new Intent(this, AllDepotsCards.class);
//            startActivity(intent);
//        });

        cardView2.setOnClickListener(v -> {
            Intent intent = new Intent(this, AllBusesCards.class);
            startActivity(intent);
        });

        cardView3.setOnClickListener(v -> {
            Intent intent = new Intent(this, AllRouteCards.class);
            startActivity(intent);
        });

        cardView6.setOnClickListener(v -> {
            // New Transfer Page
            Intent intent = new Intent(this, TransferHome.class);
            startActivity(intent);
        });

        cardView7.setOnClickListener(v -> {
            Intent intent = new Intent(this, AllAddaCards.class);
            startActivity(intent);
        });

        cardView8.setOnClickListener(v -> {
            Intent intent = new Intent(this, AllStaffCards.class);
            startActivity(intent);
        });


        aboutUsCard.setOnClickListener(v -> {
            ManageEntities.this.finish();
        });


    }

    private void reloadUserDetails() {
        welcomeTV.setText("Manage Data !");

        // Role
        String roleName = Preferences.getInstance().roleName;
        roleIdTV.setText(roleName != null && !roleName.isEmpty() ? "Role: " + roleName : "No Role Available");

        // Depot
        String regionalOfficeName = Preferences.getInstance().regionalOfficeName;
        depotLocationTV.setText(regionalOfficeName != null && !regionalOfficeName.isEmpty() ? "Depot: " + regionalOfficeName : "Depot: Not Available");
    }


    private void showLogoutConfirmationDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout as the current user?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Logout + clear prefs
                        Intent intent = new Intent(ManageEntities.this, LoginHRTC.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                        // Clear
                        SharedPreferences preferences = getSharedPreferences("com.dit.himachal.hrtc.app", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear(); // This will remove all preferences
                        editor.apply(); // or editor.commit();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing
                    }
                });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
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