package com.dit.hp.hrtc_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.dit.hp.hrtc_app.Adapters.AddaSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.DepotSpinnerAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Modals.AddaPojo;
import com.dit.hp.hrtc_app.Modals.DepotPojo;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerGet;
import com.dit.hp.hrtc_app.json.JsonParse;
import com.dit.hp.hrtc_app.utilities.AppStatus;
import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;
import com.doi.spinnersearchable.SearchableSpinner;

import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Homescreen extends AppCompatActivity implements ShubhAsyncTaskListenerGet {

    CardView cardView1, cardView2, cardView3, cardView4, cardView5, aboutUsCard;
    ImageButton profileBtn;
    TextView welcomeTV, depotNameTV, addaTV, roleIdTV, bottomTextView;
    ImageView bottomImageView;
    CustomDialog CD = new CustomDialog();
    List<DepotPojo> pojoListDepots;

    // Selections for SuperAdmin n Admin
    DepotPojo popupSelectionDepot;
    AddaPojo popupSelectionAdda;

    AESCrypto aesCrypto = new AESCrypto();

    // FOR custom dialog
    private DepotSpinnerAdapter depotSpinnerAdapter;
    private AddaSpinnerAdapter addaSpinnerAdapter;
    private SearchableSpinner depotSpinner;
    private SearchableSpinner addaSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        // Load saved preferences at the very beginning
        Preferences.getInstance().loadPreferences(this);

        Log.i("Homescreen", "Login As: userName " + Preferences.getInstance().userName);
        Log.i("Homescreen", "Login As: empId " + Preferences.getInstance().empId);
        Log.i("Homescreen", "Login As: Department Id " + Preferences.getInstance().departmentId);
        Log.i("Homescreen", "Login As: Department Name saved as Depot Name & Code: " + Preferences.getInstance().depotName + " : " + Preferences.getInstance().depotId);
        Log.i("Homescreen", "Login As: RoleName And Role ID" + Preferences.getInstance().roleName + " : " + Preferences.getInstance().roleId);


        roleIdTV = findViewById(R.id.roleIdTV);
        welcomeTV = findViewById(R.id.headTV);
        depotNameTV = findViewById(R.id.depotLocationTV);

        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        cardView3 = findViewById(R.id.cardView3);
        cardView4 = findViewById(R.id.cardView4);
        cardView5 = findViewById(R.id.cardView5);
        aboutUsCard = findViewById(R.id.aboutUsCard);
        profileBtn = findViewById(R.id.profileB);

        bottomTextView = findViewById(R.id.moreTV);
        bottomImageView = findViewById(R.id.arrowRight);

        // Reload user details to update the UI
        reloadUserDetails();

        if (Preferences.getInstance().roleId != null) {
            if (Preferences.getInstance().roleId == 1 || Preferences.getInstance().roleId == 2) {
                bottomTextView.setText("Choose Depot");
                bottomImageView.setImageResource(R.drawable.depot);
                aboutUsCard.setBackgroundResource(R.drawable.customborder_dialog_green);
                bottomTextView.setTextColor(Color.WHITE);
                bottomTextView.setText("Depot: " + Preferences.getInstance().depotName);
            }
        }


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

        // Card 1 click listener (Add Daily Record)
        cardView1.setOnClickListener(v -> {
            if (Preferences.getInstance().roleId == 1 || Preferences.getInstance().roleId == 2) {
                // SUPER ADMIN CANNOT ADD A RECORD CUZ NO ADDA AVAILABLE FOR SUPER ADMIN
//                CD.showDialog(this,"Super Admin cannot add a daily record because no specific adda is associated with it");
                if (isDepotSelected()) {
                    Intent intent = new Intent(Homescreen.this, AddDailyRecord.class);
                    startActivity(intent);
                } else {
                    showDepotSelectionPopup();  // Prompt depot selection if not selected
                }
            } else {
                if (isDepotSelected()) {
                    Intent intent = new Intent(Homescreen.this, AddDailyRecord.class);
                    startActivity(intent);  // Directly navigate for normal users
                } else {
                    CD.showDialog(this, "You do not have any depot linked with your account. Contact your administrator for further assistance.");
                }
            }
        });

        // Card 2 click listener (Daily Duty Register Cards)
        cardView2.setOnClickListener(v -> {
            // If Super Admin Check if Depot and Adda Available
            if (Preferences.getInstance().roleId == 1 || Preferences.getInstance().roleId == 2) {
                if (isDepotSelected()) {
                    Intent intent = new Intent(Homescreen.this, DailyDutyRegisterCards.class);
                    startActivity(intent);
                } else {
                    showDepotSelectionPopup();  // Prompt depot selection if not selected
                }
            } else {
                if (AppStatus.getInstance(Homescreen.this).isOnline()) {
                    if (isDepotSelected()) {
                        Intent intent = new Intent(Homescreen.this, DailyDutyRegisterCards.class);
                        startActivity(intent);  // Directly navigate for normal users
                    } else {
                        CD.showDialog(this, "You do not have any depot linked with your account. Contact your administrator for further assistance.");
                    }
                } else {
                    CD.showDialog(Homescreen.this, Econstants.internetNotAvailable);
                }
            }
        });

        // Card 3 click listener (Download Record)
        cardView3.setOnClickListener(v -> {
            if (Preferences.getInstance().roleId == 1 || Preferences.getInstance().roleId == 2) {
                if (isDepotSelected()) {
                    Intent intent = new Intent(Homescreen.this, DownloadRecord.class);
                    startActivity(intent);
                } else {
                    showDepotSelectionPopup();  // Prompt depot selection if not selected
                }
            } else {
                if (isDepotSelected()) {
                    Intent intent = new Intent(Homescreen.this, DownloadRecord.class);
                    startActivity(intent);  // Directly navigate for normal users
                } else {
                    CD.showDialog(this, "You do not have any depot linked with your account. Contact your administrator for further assistance.");
                }
            }
        });

        // Card 4 click listener (Manage Entities)
        cardView4.setOnClickListener(v -> {
            if (Preferences.getInstance().roleId == 1 || Preferences.getInstance().roleId == 2) {
                if (isDepotSelected()) {
                    Intent intent = new Intent(Homescreen.this, ManageEntities.class);
                    startActivity(intent);
                } else {
                    showDepotSelectionPopup();  // Prompt depot selection if not selected
                }
            } else {
                CD.showDialog(this, "This privilege is restricted to the Admin. Please contact your administrator for further assistance.");
            }
        });

        // Offices
        cardView5.setOnClickListener(v -> {

//            Integer roleId = Preferences.getInstance().roleId;  // ROLE ID COMMING AS 29 FIXX IT
//            Log.e("ROLE Here: ", "ROLE Here: " + roleId);
//            if (roleId != null && (roleId == 1 || roleId == 2)) {
                if (isDepotSelected()) {
                    Intent intent = new Intent(Homescreen.this, AllOfficeCards.class);
                    startActivity(intent);
                } else {
                    showDepotSelectionPopup();
                }
//            } else if (roleId != null) {
//                CD.showDialog(this, "This privilege is restricted to the Admin. Please contact your administrator for further assistance.");
//            } else {
//                CD.showDialog(this, "User role not found. Please login again.");
//            }

        });

        // About Us Card click listener
        aboutUsCard.setOnClickListener(v -> {
            if (Preferences.getInstance().roleId == 1 || Preferences.getInstance().roleId == 2) {
                showDepotSelectionPopup();
            } else {
                Intent intent = new Intent(Homescreen.this, AboutUs.class);
                startActivity(intent);  // Directly navigate for normal users
            }
        });

        // Depot Location TV click listener
        depotNameTV.setOnClickListener(v -> {
            if (Preferences.getInstance().roleId == 1 || Preferences.getInstance().roleId == 2) {
                showDepotSelectionPopup();  // Open depot selection popup for Super Admin
            }
        });


    }


//    CUSTOM METHODS ########################################################################################################################################

    // Exit confirmation
    private void showLogoutConfirmationDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout as the current user?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Logout + clear prefs
                        Intent intent = new Intent(Homescreen.this, LoginHRTC.class);
                        startActivity(intent);
                        Homescreen.this.finish();

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


    private void loadOfficeForAdmin() {
        // LOAD OFFICES FOR ADMIN
//    try {
//        if (AppStatus.getInstance(Homescreen.this).isOnline()) {
//
//            UploadObject object = new UploadObject();
//            object.setUrl(Econstants.sarvatra_url);
//            object.setMethordName("/office/getPagedOffices?");
//            object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("office"), "UTF-8")
//
//                            // HARDCODE THIS DEPARTMENT ID FOR HRTC
//                            + "&deptId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(106))) // Hardcoded ID for HRTC
//                            + "&empId=" + URLEncoder.encode(aesCrypto.encrypt("0"), "UTF-8")
//                            + "&page=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(page)))

        /// /                        + "&searchByName=" + URLEncoder.encode(aesCrypto.encrypt(""), "UTF-8")
//                            + "&size=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(size)))
//            );
//            object.setTasktype(TaskType.GET_OFFICES);
//            object.setAPI_NAME(Econstants.API_NAME_HRTC);
//
//            new ShubhAsyncGet(Homescreen.this, Homescreen.this, TaskType.GET_OFFICES).execute(object);
//        } else {
//            // Do nothing if CD already shown once
//            CD.showDialog(Homescreen.this, Econstants.internetNotAvailable);
//        }
//    } catch (Exception ex) {
//        CD.showDialog(Homescreen.this, "Something Bad happened . Please reinstall the application and try again.");
//    }
    }


    private void loadDepotsForSuperAdmin() {
        System.out.println("Loading Depots ABC");
        // Depot Service Call
        try {
            if (AppStatus.getInstance(Homescreen.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("depot"), "UTF-8"));
                object.setTasktype(TaskType.GET_DEPOTS);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(Homescreen.this, Homescreen.this, TaskType.GET_DEPOTS).execute(object);

            } else {
                CD.showDialog(Homescreen.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(Homescreen.this, "Something Bad happened . Please reinstall the application and try again.");
        }

    }


    // DIALOG FOR DEPOT SELECTION
    private void showDepotSelectionPopup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_super_admin_depot_pick, null);
        builder.setView(dialogView);

        TextView titleTextView = new TextView(this);
        titleTextView.setText("Select Depot");
        titleTextView.setTextSize(20);  // You can adjust the size
        titleTextView.setTypeface(Typeface.DEFAULT_BOLD);  // Set bold font
        titleTextView.setTextColor(Color.BLACK);  // Set text color to black
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setPadding(0, 30, 0, 30);  // Optional padding for spacing
        builder.setCustomTitle(titleTextView);

        depotSpinner = dialogView.findViewById(R.id.currentDepotSpinner);
//        addaSpinner = dialogView.findViewById(R.id.currentAddaSpinner);

        depotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                popupSelectionDepot = (DepotPojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        builder.setPositiveButton("Select", (dialog, which) -> {

            if (popupSelectionDepot != null) {
                Preferences.getInstance().depotId = popupSelectionDepot.getId();
                Preferences.getInstance().depotName = popupSelectionDepot.getDepotName();
                Preferences.getInstance().savePreferences(Homescreen.this);
                reloadUserDetails();
                bottomTextView.setText("Depot: " + Preferences.getInstance().depotName);

            } else {
                CD.showDialog(this, "No depot selected");
            }


        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();

        // Make non-dismissible
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);

        // Show the dialog
        alertDialog.show();

        // Load depots dynamically
        loadDepotsForSuperAdmin();

        // Dim the background
        WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
        lp.dimAmount = 0.7f;  // Increase for more dimming (0.0 - 1.0)
        alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    // Utility method to check if depot is selected
    private boolean isDepotSelected() {
        String depotName = Preferences.getInstance().depotName;
        // Ensure depotName and depotId are not null, empty, or "null"
        return depotName != null && !depotName.trim().isEmpty() && !depotName.equalsIgnoreCase("null") && Preferences.getInstance().depotId != -1;
    }

    private void reloadUserDetails() {

        // Welcome Message
        String userName = Preferences.getInstance().userName != null ? Preferences.getInstance().userName : "Guest";
        welcomeTV.setText("Welcome " + userName + " !");

        // Depot
        String depotName = Preferences.getInstance().depotName;
        depotNameTV.setText(depotName != null && !depotName.isEmpty() ? "Office: " + depotName : "No Info Available");

        // Role
        String roleName = Preferences.getInstance().roleName;
        roleIdTV.setText(roleName != null && !roleName.isEmpty() ? "Role: " + roleName : "No Info Available");
    }


    @Override
    public void onTaskCompleted(ResponsePojoGet result, TaskType taskType) throws JSONException {
        // Get Depots
        if (TaskType.GET_DEPOTS == taskType) {
            SuccessResponse response = null;
            List<DepotPojo> pojoListDepots = new ArrayList<>();
            Log.i("AddBusDetails", "Task type is fetching depots..");

            if (result != null) {
                Log.i("Depots: ", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        pojoListDepots = JsonParse.parseDecryptedDepotsInfo(response.getData());

                        if (pojoListDepots.size() > 0) {
                            Log.e("Reports Data", pojoListDepots.toString());

                            depotSpinnerAdapter = new DepotSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoListDepots);
                            depotSpinner.setAdapter(depotSpinnerAdapter);

                            if (Preferences.getInstance().depotName != null) {
                                // Preselect Depot If Available
                                depotSpinner.post(() -> {
                                    if (depotSpinnerAdapter != null) {
                                        int itemPosition = depotSpinnerAdapter.getPositionForDepot(Preferences.getInstance().depotName, Preferences.getInstance().depotId);
                                        if (itemPosition != -1) {
                                            depotSpinner.setSelectedItemByIndex(itemPosition);
                                        } else {
                                            Log.e("Error", "Depot not found in adapter.");
                                        }
                                    }
                                });
                            }


                        } else {
                            CD.showDialog(Homescreen.this, "No Data Found");
                            pojoListDepots.clear();
                        }

                    } else {
                        CD.showDialog(Homescreen.this, response.getMessage());
                    }
                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(Homescreen.this, "Not able to fetch data");
                }
            } else {
                CD.showDialog(Homescreen.this, "Result is null");
            }
        }
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showLogoutConfirmationDialog();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit the app? This will log you out and clear your session.")
                .setPositiveButton("Exit", (dialog, which) -> {
                    Homescreen.this.finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
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