package com.dit.hp.hrtc_app;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.cardview.widget.CardView;

import com.dit.hp.hrtc_app.Adapters.OfficesSelectionSpinnerAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.OfficeSelectionPojo;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerPost;
import com.dit.hp.hrtc_app.json.JsonParse;
import com.dit.hp.hrtc_app.utilities.AppStatus;
import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;
import com.doi.spinnersearchable.SearchableSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Homescreen extends BaseDrawerActivity implements ShubhAsyncTaskListenerPost {

    CardView cardView1, cardView2, cardView3, cardView4, cardView5, aboutUsCard;
    ImageButton profileBtn;
    TextView welcomeTV, depotNameTV, addaTV, roleIdTV, bottomTextView;
    ImageView bottomImageView;
    CustomDialog CD = new CustomDialog();

    // Selections for SuperAdmin n Admin
    OfficeSelectionPojo popupSelectionOffice;

    AESCrypto aesCrypto = new AESCrypto();

    // FOR custom dialog
    private OfficesSelectionSpinnerAdapter officesSelectionSpinnerAdapter;
    private SearchableSpinner officeSpinner;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_homescreen; // layout for this screen
    }

    @Override
    protected int getNavMenuId() {
        return R.id.nav_home; // the nav menu item to highlight
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load saved preferences at the very beginning
        Preferences.getInstance().loadPreferences(this);

        Log.i("Homescreen", "Login As: App Role ID " + Preferences.getInstance().appRoleId);
        Log.i("Homescreen", "Login As: empId " + Preferences.getInstance().empId);
        Log.i("Homescreen", "Login As: userName " + Preferences.getInstance().userName);
        Log.i("Homescreen", "Login As: Department Id " + Preferences.getInstance().departmentId);
        Log.i("Homescreen", "Login As: Office Name & ID saved as Depot Name & Depot ID: " + Preferences.getInstance().depotName + " : " + Preferences.getInstance().depotId);
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

        loadOfficeForAdmin();


        // Apply null check
        System.out.println(Preferences.getInstance().appRoleId);
        System.out.println(Preferences.getInstance().appRoleId);
        if (Preferences.getInstance().appRoleId != -1) {
            int roleId = Preferences.getInstance().appRoleId;
            if (roleId == 1 || roleId == 2) {
                bottomTextView.setText("Choose Office");
                bottomImageView.setImageResource(R.drawable.sub_office);
                aboutUsCard.setBackgroundResource(R.drawable.customborder_dialog_green);
                bottomTextView.setTextColor(Color.WHITE);
                bottomTextView.setText("Select Regional Office");
            }
        } else {
            CD.showSessionExpiredDialog(this, "No User role found. Please Login again");
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
            if (Preferences.getInstance().appRoleId == 1 || Preferences.getInstance().appRoleId == 2) {
                // SUPER ADMIN CANNOT ADD A RECORD CUZ NO ADDA AVAILABLE FOR SUPER ADMIN
//                CD.showDialog(this,"Super Admin cannot add a daily record because no specific adda is associated with it");
                if (isRegionalOfficeSelected()) {
                    Intent intent = new Intent(Homescreen.this, AddDailyRecord.class);
                    startActivity(intent);
                } else {
                    showRegionalOfficeSelectionPopup();  // Prompt depot selection if not selected
                }
            } else {
                if (isRegionalOfficeSelected()) {
                    Intent intent = new Intent(Homescreen.this, AddDailyRecord.class);
                    startActivity(intent);  // Directly navigate for normal users
                } else {
                    CD.showDialog(this, "You do not have any office linked with your account. Contact your administrator for further assistance.");
                }
            }
        });

        // Card 2 click listener (Daily Duty Register Cards)
        cardView2.setOnClickListener(v -> {
            // If Super Admin Check if Depot and Adda Available
            if (Preferences.getInstance().appRoleId == 1 || Preferences.getInstance().appRoleId == 2) {
                if (isRegionalOfficeSelected()) {
                    Intent intent = new Intent(Homescreen.this, DailyDutyRegisterCards.class);
                    startActivity(intent);
                } else {
                    showRegionalOfficeSelectionPopup();  // Prompt depot selection if not selected
                }
            } else {
                if (AppStatus.getInstance(Homescreen.this).isOnline()) {
                    if (isRegionalOfficeSelected()) {
                        Intent intent = new Intent(Homescreen.this, DailyDutyRegisterCards.class);
                        startActivity(intent);  // Directly navigate for normal users
                    } else {
                        CD.showDialog(this, "You do not have any office linked with your account. Contact your administrator for further assistance.");
                    }
                } else {
                    CD.showDialog(Homescreen.this, Econstants.internetNotAvailable);
                }
            }
        });

        // Card 3 click listener (Download Record)
        cardView3.setOnClickListener(v -> {
            if (Preferences.getInstance().appRoleId == 1 || Preferences.getInstance().appRoleId == 2) {
                if (isRegionalOfficeSelected()) {
                    Intent intent = new Intent(Homescreen.this, DownloadRecord.class);
                    startActivity(intent);
                } else {
                    showRegionalOfficeSelectionPopup();  // Prompt depot selection if not selected
                }
            } else {
                if (isRegionalOfficeSelected()) {
                    Intent intent = new Intent(Homescreen.this, DownloadRecord.class);
                    startActivity(intent);  // Directly navigate for normal users
                } else {
                    CD.showDialog(this, "You do not have any office linked with your account. Contact your administrator for further assistance.");
                }
            }
        });

        // Card 4 click listener (Manage Entities)
        cardView4.setOnClickListener(v -> {
            if (Preferences.getInstance().appRoleId == 1 || Preferences.getInstance().appRoleId == 2) {
                if (isRegionalOfficeSelected()) {
                    Intent intent = new Intent(Homescreen.this, ManageEntities.class);
                    startActivity(intent);
                } else {
                    showRegionalOfficeSelectionPopup();  // Prompt depot selection if not selected
                }
            } else {
                CD.showDialog(this, "This privilege is restricted to the Admin. Please contact your administrator for further assistance.");
            }
        });

        // Offices
        cardView5.setOnClickListener(v -> {

            Integer appRoleId = Preferences.getInstance().appRoleId;  // App ROLE ID
            Log.e("ROLE Here: ", "ROLE Here: " + appRoleId);
            if (appRoleId != null && (appRoleId == 1 || appRoleId == 2)) {
                Intent intent = new Intent(Homescreen.this, AllOfficeCards.class);
                startActivity(intent);
            } else if (appRoleId != null && (appRoleId != 1 || appRoleId != 2)) {
                CD.showDialog(this, "This privilege is restricted to the Admin. Please contact your administrator for further assistance.");
            } else {
                CD.showDialog(this, "User role not found. Please login again.");
            }

        });

        // About Us Card click listener
        aboutUsCard.setOnClickListener(v -> {
            if (Preferences.getInstance().appRoleId == 1 || Preferences.getInstance().appRoleId == 2) {
                showRegionalOfficeSelectionPopup();
            } else {
                Intent intent = new Intent(Homescreen.this, AboutUs.class);
                startActivity(intent);  // Directly navigate for normal users
            }
        });

        // Depot Location TV click listener
        depotNameTV.setOnClickListener(v -> {
            if (Preferences.getInstance().appRoleId == 1 || Preferences.getInstance().appRoleId == 2) {
                showRegionalOfficeSelectionPopup();  // Open depot selection popup for Super Admin
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
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                        // Clear
                        Preferences.getInstance().clearPreferences(Homescreen.this);


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


    // Load Offices for Inventory
    private void loadOfficeForAdmin() {
        try {
            if (AppStatus.getInstance(Homescreen.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMasterName("");
                object.setMethordName("/api/getData?Tagname=" + URLEncoder.encode(aesCrypto.encrypt("getOffice"), "UTF-8"));

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("deptId", 106);
                jsonBody.put("empId", 0);
                jsonBody.put("ofcTypeId", Econstants.REGIONAL_OFFICE_ID);

                object.setParam(aesCrypto.encrypt(jsonBody.toString())); // Put in encypted JSON

                object.setTasktype(TaskType.GET_OFFICE_FOR_ADMIN);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncPost(Homescreen.this, Homescreen.this, TaskType.GET_OFFICE_FOR_ADMIN).execute(object);
            } else {
                // Do nothing if CD already shown once
                CD.showDialog(Homescreen.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(Homescreen.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }


    // DIALOG FOR Regional Office SELECTION
    private void showRegionalOfficeSelectionPopup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_super_admin_depot_pick, null);
        builder.setView(dialogView);

        TextView titleTextView = new TextView(this);
        titleTextView.setText("Select Office");
        titleTextView.setTextSize(20);  // You can adjust the size
        titleTextView.setTypeface(Typeface.DEFAULT_BOLD);  // Set bold font
        titleTextView.setTextColor(Color.BLACK);  // Set text color to black
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setPadding(0, 30, 0, 30);  // Optional padding for spacing
        builder.setCustomTitle(titleTextView);

        officeSpinner = dialogView.findViewById(R.id.officeSelectionSpinner);

        officeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                popupSelectionOffice = (OfficeSelectionPojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        builder.setPositiveButton("Select", (dialog, which) -> {

            if (popupSelectionOffice != null) {
                Preferences.getInstance().regionalOfficeId = popupSelectionOffice.getOfficeId();
                Preferences.getInstance().regionalOfficeName = popupSelectionOffice.getOfficeName();
                Preferences.getInstance().savePreferences(Homescreen.this);
                reloadUserDetails();
                bottomTextView.setText(Preferences.getInstance().regionalOfficeName);

            } else {
                CD.showDialog(this, "No office selected");
            }


        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();

        // Make non-dismissible
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);

        // Show the dialog
        alertDialog.show();

        // Load offices for selection
        loadOfficeForAdmin();

        // Dim the background
        WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
        lp.dimAmount = 0.7f;  // Increase for more dimming (0.0 - 1.0)
        alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    // Utility method to check if depot is selected
    private boolean isRegionalOfficeSelected() {
        String regionalOfficeName = Preferences.getInstance().regionalOfficeName;
        return regionalOfficeName != null && !regionalOfficeName.trim().isEmpty() && !regionalOfficeName.equalsIgnoreCase("null") && Preferences.getInstance().regionalOfficeId != -1;
    }

    private void reloadUserDetails() {

        // Welcome Message
        String userName = Preferences.getInstance().userName != null ? Preferences.getInstance().userName : "Guest";
        welcomeTV.setText("Welcome " + userName);

        // Depot
        String depotName = Preferences.getInstance().depotName;
        depotNameTV.setText(depotName != null && !depotName.isEmpty() ? "Depot: " + depotName : "No Info Available");

        // Role
        String roleName = Preferences.getInstance().roleName;
        roleIdTV.setText(roleName != null && !roleName.isEmpty() ? "Role: " + roleName : "No Info Available");
    }


    @Override
    public void onTaskCompleted(ResponsePojoGet result, TaskType taskType) throws JSONException {
        // Get Depots
        if (TaskType.GET_OFFICE_FOR_ADMIN == taskType) {
            SuccessResponse response = null;
            List<OfficeSelectionPojo> pojoList = new ArrayList<>();

            if (result != null) {
                Log.i("Depots: ", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        if (!(response.getData().equalsIgnoreCase("No records found"))) {
                            pojoList = JsonParse.parseOfficeListForAdmin(response.getData());
                        } else {
                            pojoList.clear();
                        }

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data", pojoList.toString());

                            officesSelectionSpinnerAdapter = new OfficesSelectionSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            if (officeSpinner != null) {
                                officeSpinner.setAdapter(officesSelectionSpinnerAdapter);
                            } else {
                                Log.e("SpinnerError", "officeSpinner is null. Check XML or findViewById.");
                            }


                            //  Preselect Depot or Regional Office If Available
                            if (Preferences.getInstance().depotName != null && officeSpinner != null) {
                                Log.d("DepotCheck", "Depot Name: " + Preferences.getInstance().depotName);
                                officeSpinner.post(() -> {
                                    if (officesSelectionSpinnerAdapter != null) {
                                        int itemPosition = officesSelectionSpinnerAdapter.getPositionForOffice(
                                                Preferences.getInstance().depotName,
                                                Preferences.getInstance().depotId
                                        );

                                        if (itemPosition != -1) {
                                            officeSpinner.setSelectedItemByIndex(itemPosition);
                                            Log.d("OfficeSelection", "Selected index: " + itemPosition);
                                        } else {
                                            Log.e("OfficeSelection", "Office not found in adapter.");
                                        }
                                    } else {
                                        Log.e("OfficeSelection", "Adapter is null.");
                                    }
                                });
                            } else {
                                Log.e("OfficeSelection", "Spinner is null or depot name missing.");
                            }


                        } else {
//                            CD.showDialog(Homescreen.this, "No offices found for selection");
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
        setHeading("Homescreen"); // Custom Method in BaseDrawerActivity
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
        reloadUserDetails(); // Reload details to update UI
    }


    @Override
    protected void onResume() {
        super.onResume();
        setHeading("Homescreen"); // Custom Method in BaseDrawerActivity
        Preferences.getInstance().loadPreferences(this); // Ensure preferences are reloaded
        reloadUserDetails(); // Reload details to update UI
    }

}