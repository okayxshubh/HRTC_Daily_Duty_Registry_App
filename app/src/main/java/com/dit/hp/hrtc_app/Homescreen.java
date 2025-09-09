package com.dit.hp.hrtc_app;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
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
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.dit.hp.hrtc_app.Adapters.OfficesSelectionSpinnerAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.AttendanceModule.AttendanceAuthentication;
import com.dit.hp.hrtc_app.Modals.OfficeSelectionPojo;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerGet;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerPost;
import com.dit.hp.hrtc_app.json.JsonParse;
import com.dit.hp.hrtc_app.utilities.AppStatus;
import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;
import com.doi.spinnersearchable.SearchableSpinner;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Homescreen extends BaseDrawerActivity implements ShubhAsyncTaskListenerPost, ShubhAsyncTaskListenerGet {


    LinearLayout adminOptionsLL, normalUserOptionsLL;
    CardView cardView1, cardView2, cardView3, cardView4, cardView5, aboutUsCard;
    CardView profileCardView, attendanceCardView, salaryCardView;
    ImageButton profileBtn;
    TextView welcomeTV, depotNameTV, addaTV, roleIdTV, bottomTextView;
    ImageView bottomImageView;
    CustomDialog CD = new CustomDialog();

    // Selections for SuperAdmin n Admin
    OfficeSelectionPojo popupSelectionOffice;

    AESCrypto aesCrypto = new AESCrypto();
    int LOCATION_REQUEST_CODE = 1001; // Turn on location code.. onActivityResult()

    // FOR custom dialog
    private OfficesSelectionSpinnerAdapter officesSelectionSpinnerAdapter;
    private SearchableSpinner officeSpinner;
    private TextView employeeCode;

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
//        Preferences.getInstance().loadPreferences(this);
        Econstants.loadPrefsNApplyTheme(this); // Load Prefs and apply theme

        Log.i("Homescreen", "Login As: App Role ID " + Preferences.getInstance().appRoleId);
        Log.i("Homescreen", "Login As: Email ID " + Preferences.getInstance().emailID);
        Log.i("Homescreen", "Login As: empId " + Preferences.getInstance().empId);
        Log.i("Homescreen", "Login As: userName " + Preferences.getInstance().userName);
        Log.i("Homescreen", "Login As: Department Id " + Preferences.getInstance().departmentId);
        Log.i("Homescreen", "Login As: Office Name & ID saved as Depot Name & Depot ID: " + Preferences.getInstance().depotName + " : " + Preferences.getInstance().depotId);
        Log.i("Homescreen", "Login As: RoleName And Role ID" + Preferences.getInstance().roleName + " : " + Preferences.getInstance().roleId);

        // Service Call Check PMIS..
        checkEmployeeCodeServiceCall();

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

        profileCardView = findViewById(R.id.profileCardView);
        attendanceCardView = findViewById(R.id.attendanceCardView);
        salaryCardView = findViewById(R.id.salaryCardView);

        bottomTextView = findViewById(R.id.moreTV);
        bottomImageView = findViewById(R.id.arrowRight);

        adminOptionsLL = findViewById(R.id.adminOptionsLL);
        normalUserOptionsLL = findViewById(R.id.normalUserOptionsLL);

        // HIDE or SHOW Roles
        if (Preferences.getInstance().appRoleId != -1) {
            int roleId = Preferences.getInstance().appRoleId;
            if (roleId == 1 || roleId == 2) {
                adminOptionsLL.setVisibility(View.VISIBLE);
                normalUserOptionsLL.setVisibility(View.VISIBLE);
            } else {
                adminOptionsLL.setVisibility(View.GONE);
                normalUserOptionsLL.setVisibility(View.VISIBLE);
            }
        } else {
            CD.showSessionExpiredDialog(this, "No app role id found");
        }


        // Reload user details to update the UI
        reloadUserDetails();

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
            CD.showSessionExpiredDialog(this, "No app role id found. Please Login again");
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
            Log.e("ROLE Here: ", "ROLE Here for Office Click: " + appRoleId);
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


        // ############################################# NORMAL USER OPTION #################################################
        profileCardView.setOnClickListener(v -> {
            Intent intent = new Intent(Homescreen.this, ProfileScreen.class);
            startActivity(intent);
        });

        attendanceCardView.setOnClickListener(v -> {
            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10000)
                    .setFastestInterval(5000);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                    .setAlwaysShow(true); // Show dialog to enable GPS

            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(locationSettingsResponse -> {
                // GPS is already ON
                startActivity(new Intent(Homescreen.this, AttendanceAuthentication.class));
            });

            task.addOnFailureListener(e -> {
                if (e instanceof ResolvableApiException) {
                    try {
                        ((ResolvableApiException) e).startResolutionForResult(Homescreen.this, LOCATION_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        });

        salaryCardView.setOnClickListener(v -> {
            Intent intent = new Intent(Homescreen.this, DownloadSalarySlip.class);
            startActivity(intent);
        });


    }


//    CUSTOM METHODS ########################################################################################################################################

    private void showLogoutConfirmationDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout as the current user?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Logout + clear prefs
                        Intent intent = new Intent(Homescreen.this, MobileLoginHRTC.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                        // Clear Prefs
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
        String regionalOfficeName = Preferences.getInstance().regionalOfficeName;
        depotNameTV.setText(regionalOfficeName != null && !regionalOfficeName.isEmpty() ? "Depot: " + regionalOfficeName : "Depot: Not Available");

        // Role
        String roleName = Preferences.getInstance().roleName;
        roleIdTV.setText(roleName != null && !roleName.equalsIgnoreCase("null") ? "Role: " + roleName : "Role: Normal User");
    }


    private void showEnterEmployeeCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_enter_pmis, null);
        builder.setView(dialogView);

        TextView titleTextView = new TextView(this);
        titleTextView.setText("Enter Details");
        titleTextView.setTextSize(20);  // You can adjust the size
        titleTextView.setTypeface(Typeface.DEFAULT_BOLD);  // Set bold font
        titleTextView.setTextColor(Color.BLACK);  // Set text color to black
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setPadding(0, 30, 0, 30);  // Optional padding for spacing
        builder.setCustomTitle(titleTextView);

        TextView employeeCode = dialogView.findViewById(R.id.employeeCode);

        builder.setPositiveButton("Select", (dialog, which) -> {
            if (Econstants.isNotEmpty(employeeCode.getText().toString())) {
                Preferences.getInstance().employeeCode = employeeCode.getText().toString();
                Preferences.getInstance().savePreferences(this);

                sendEmployeeCodeToSave(employeeCode.getText().toString().trim());
            } else {
                CD.showDialog(this, "Please enter your employee code before proceeding");
            }
        });

        AlertDialog alertDialog = builder.create();

        // Make non-dismissible
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);

        // Show the dialog
        alertDialog.show();

        // Dim the background
        WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
        lp.dimAmount = 0.7f;  // Increase for more dimming (0.0 - 1.0)
        alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void checkEmployeeCodeServiceCall() {
        try {
            if (AppStatus.getInstance(Homescreen.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                String master = URLEncoder.encode(aesCrypto.encrypt("dmsMapping"), "UTF-8");
                String encryptedEmail = URLEncoder.encode(aesCrypto.encrypt(Preferences.getInstance().emailID), "UTF-8");

                object.setMethordName("/master-data?masterName=" + master + "&searchByName=" + encryptedEmail);
                object.setParam("");
                object.setTasktype(TaskType.CHECK_EMPLOYEE_CODE);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(Homescreen.this, Homescreen.this, TaskType.CHECK_EMPLOYEE_CODE).execute(object);
            } else {
                CD.showDialog(Homescreen.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(Homescreen.this, "Something bad happened. Please reinstall the app.");
        }
    }


    private void sendEmployeeCodeToSave(String dmsEmpCode) {
        try {
            if (AppStatus.getInstance(Homescreen.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data/addlist");

                // Encrypt masterName and set as query param
                String encryptedMasterName = URLEncoder.encode(aesCrypto.encrypt("dmsMapping"), "UTF-8");
                object.setMasterName("?masterName=" + encryptedMasterName);

                // Build body array
                JSONArray empArray = new JSONArray();
                JSONObject obj = new JSONObject();
                obj.put("himaccessId", Preferences.getInstance().emailID);
                obj.put("dmsEmpCode", dmsEmpCode);
                empArray.put(obj);

                // Encrypt body
                String encryptedBody = aesCrypto.encrypt(empArray.toString());
                object.setParam(encryptedBody);

                object.setTasktype(TaskType.SAVE_EMPLOYEE_CODE);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                // Execute POST request
                new ShubhAsyncPost(Homescreen.this, Homescreen.this, TaskType.SAVE_EMPLOYEE_CODE).execute(object);

            } else {
                CD.showDialog(Homescreen.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(Homescreen.this, "Something went wrong. Please try again.");
        }
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
                            }

                            //  Preselect Depot or Regional Office When Popup Clicked
                            if (Preferences.getInstance().regionalOfficeName != null && officeSpinner != null) {
                                officeSpinner.post(() -> {
                                    if (officesSelectionSpinnerAdapter != null) {
                                        int itemPosition = officesSelectionSpinnerAdapter.getPositionForOffice(
                                                Preferences.getInstance().regionalOfficeName,
                                                Preferences.getInstance().regionalOfficeId
                                        );

                                        if (itemPosition != -1) {
                                            officeSpinner.setSelectedItemByIndex(itemPosition);
                                        } else {
                                            Log.e("OfficeSelection", "Office not found in adapter.");
                                        }
                                    } else {
                                        Log.e("OfficeSelection", "Adapter is null.");
                                    }
                                });
                            }


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

        //
        else if (TaskType.CHECK_EMPLOYEE_CODE == taskType) {
            SuccessResponse response = null;

            if (result != null) {
                Log.i("Details", "Response Obj" + result.toString());
                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        String data = response.getData(); // e.g. "[]" or '[{...}]'

                        if (data != null) {

                            // Empty
                            if (data.trim().equals("[]")) {
                                showEnterEmployeeCodeDialog(); // No code
                            } else {
                                // Present
                                try {
                                    JSONArray arr = new JSONArray(data);
                                    if (arr.length() > 0) {
                                        JSONObject obj = arr.getJSONObject(0);
                                        String empCode = obj.optString("dmsEmpCode");
                                        Log.e("EMP_CODE", "Log dmsEmpCode Code: " + empCode);

                                        Preferences.getInstance().employeeCode = empCode;
                                        Preferences.getInstance().savePreferences(this);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("Exception Parsing Emp Code", "Error parsing employee code: " + e.getMessage());
                                }
                            }
                        }
                    } else {
                        CD.showDialog(Homescreen.this, response.getMessage());
                    }

                } else {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    CD.showDialog(Homescreen.this, response.getMessage());
                }
            } else {
                CD.showDialog(Homescreen.this, "Not able to connect to the server");
            }
        }

        //
        else if (TaskType.SAVE_EMPLOYEE_CODE == taskType) {
            SuccessResponse response = null;

            if (result != null) {
                Log.i("Details", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getSuccessResponse(result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        CD.showDialog(Homescreen.this, response.getMessage());

                    } else {
                        CD.showDialog(Homescreen.this, "Response Not OK");
                    }

                } else {
                    CD.showDialog(Homescreen.this, "No Response");
                }
            } else {
                CD.showDialog(Homescreen.this, "Not able to connect to the server");
            }
        }
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showLogoutConfirmationDialog();
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