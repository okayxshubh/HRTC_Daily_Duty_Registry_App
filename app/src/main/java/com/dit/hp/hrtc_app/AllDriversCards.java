package com.dit.hp.hrtc_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dit.hp.hrtc_app.Adapters.DriversCardsAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.StaffPojo;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.OnDriverDeleteClickListener;
import com.dit.hp.hrtc_app.interfaces.OnDriverEditClickListener;
import com.dit.hp.hrtc_app.interfaces.OnDriverMoreInfoClickListener;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerGet;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerPost;
import com.dit.hp.hrtc_app.json.JsonParse;
import com.dit.hp.hrtc_app.utilities.AppStatus;
import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;

import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AllDriversCards extends AppCompatActivity implements OnDriverEditClickListener, OnDriverMoreInfoClickListener, OnDriverDeleteClickListener, ShubhAsyncTaskListenerGet, ShubhAsyncTaskListenerPost {

    AESCrypto aesCrypto = new AESCrypto();
    ImageButton addBtn;
    SearchView searchView;
    RecyclerView recyclerView;
    TextView filterDateTextView;
    DriversCardsAdapter cardsAdapter; // global bus adapter..
    CardView backCard;

    String alreadyAddedRoute; // If already existing record added

    CustomDialog CD = new CustomDialog();

    private static final int UPDATE_REQUEST_CODE = 1;
    private static final int TRANSFER_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_drivers_cards);

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        addBtn = findViewById(R.id.addBtn);
        backCard = findViewById(R.id.backCard);


        // Setting cards layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


//####################################   SERVICE CALLS   ########################################


        // Get Driver Service Call
        try {
            if (AppStatus.getInstance(AllDriversCards.this).isOnline()) {
                UploadObject object = new UploadObject();

                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staff"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().depotId)), "UTF-8"));
                object.setTasktype(TaskType.GET_DRIVERS);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AllDriversCards.this, AllDriversCards.this, TaskType.GET_DRIVERS).execute(object);

            } else {
                CD.showDialog(AllDriversCards.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AllDriversCards.this, "Something Bad happened . Please reinstall the application and try again.");
        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // You can filter when the user submits the query
                cardsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null) {
                    // You can filter dynamically as the text changes
                    cardsAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });


        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddStaff.class);
            intent.putExtra("AddStaffType", "Driver");
            startActivity(intent);
            AllDriversCards.this.finish();
        });

        backCard.setOnClickListener(v -> AllDriversCards.this.finish());

    }

    // Custom method to encode Params.. when params are not JSON.. PUT Request to edit
    private String buildParams(Map<String, String> params) {
        StringBuilder paramBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (paramBuilder.length() > 0) {
                paramBuilder.append("&");
            }
            paramBuilder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return paramBuilder.toString();
    }

    // Interface method for edit button
    @Override
    public void onEditClick(StaffPojo selectedPojo, int position) {
        Intent editIntent = new Intent(this, EditStaff.class);
        editIntent.putExtra("EditType", "DriverEdit");
        editIntent.putExtra("DriverInfo", selectedPojo);
        startActivityForResult(editIntent, UPDATE_REQUEST_CODE);
    }

    @Override
    public void onMoreInfoClick(StaffPojo selectedPojo, int position) {

        // Create and Inflate Custom Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.popup_more_info_staff, null);

        // Bind views in the dialog
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView employeeName = dialogView.findViewById(R.id.employeeName);
        TextView officialId = dialogView.findViewById(R.id.officialId);
        TextView staffType = dialogView.findViewById(R.id.staffType);
        TextView dateOfJoining = dialogView.findViewById(R.id.dateOfJoining);
        TextView caste = dialogView.findViewById(R.id.casteTv);
        TextView gender = dialogView.findViewById(R.id.genderTv);
        TextView dateOfBirth = dialogView.findViewById(R.id.dateOfBirth);
        TextView employmentType = dialogView.findViewById(R.id.employmentType);
        TextView employeeCode = dialogView.findViewById(R.id.employeeCode);
        TextView address = dialogView.findViewById(R.id.address);
        TextView licenseNo = dialogView.findViewById(R.id.licenceNo);
        TextView contactNo = dialogView.findViewById(R.id.contactNo);

        // Set data from EmployeeInfo object
        dialogTitle.setText("More Information");
        employeeName.setText(selectedPojo.getName());
        officialId.setText(String.valueOf(selectedPojo.getId()));
        staffType.setText(selectedPojo.getStaffType());
        employmentType.setText(selectedPojo.getEmploymentType());
        employeeCode.setText(selectedPojo.getEmployeeCode());
        caste.setText(selectedPojo.getCaste());
        gender.setText(selectedPojo.getGender());
        address.setText(selectedPojo.getAddress());
        licenseNo.setText(selectedPojo.getLicenceNo());
        contactNo.setText(String.valueOf(selectedPojo.getContactNo()));

        dateOfBirth.setText(selectedPojo.getDob());
        dateOfJoining.setText(selectedPojo.getJoiningDate());

        // Build dialog
        builder.setView(dialogView);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();

        // Set custom height for the dialog
        int heightInDp = 600; // Desired height in dp
        int heightInPx = (int) (heightInDp * getResources().getDisplayMetrics().density);

        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                heightInPx
        );

        dialog.show();

    }

    @Override
    public void onDeleteClick(StaffPojo selectedPojo, int position) {
        if (selectedPojo != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to remove this driver?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        if (AppStatus.getInstance(AllDriversCards.this).isOnline()) {
                            UploadObject uploadObject = new UploadObject();

                            try {
                                uploadObject.setUrl(Econstants.base_url);
                                uploadObject.setMethordName("/master-data?masterName=");

                                uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staff"), "UTF-8")
                                        + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(selectedPojo.getId()))));

                                uploadObject.setTasktype(TaskType.REMOVE_STAFF);
                                uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Log.i("Object", "Complete Object: " + uploadObject.toString());

                            new ShubhAsyncPost(AllDriversCards.this, AllDriversCards.this, TaskType.REMOVE_STAFF).execute(uploadObject);

                        } else {
                            CD.addCompleteEntityDialog(AllDriversCards.this, "Internet not Available. Please Connect to the Internet and try again.");
                        }

                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    // Start activity for Result: Editing a record
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {

            Log.i("Drivers Updated", "Data Updated Log Executed");
            searchView.setQuery("", false); // Clear Search View

            // Get Driver Service Call
            try {
                if (AppStatus.getInstance(AllDriversCards.this).isOnline()) {
                    UploadObject object = new UploadObject();

                    object.setUrl(Econstants.base_url);
                    object.setMethordName("/master-data?");
                    object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staff"), "UTF-8")
                            + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().depotId)), "UTF-8"));
                    object.setTasktype(TaskType.GET_DRIVERS);
                    object.setAPI_NAME(Econstants.API_NAME_HRTC);

                    new ShubhAsyncGet(AllDriversCards.this, AllDriversCards.this, TaskType.GET_DRIVERS).execute(object);

                } else {
                    CD.showDialog(AllDriversCards.this, Econstants.internetNotAvailable);
                }
            } catch (Exception ex) {
                CD.showDialog(AllDriversCards.this, "Something Bad happened . Please reinstall the application and try again.");
            }
        }

    }


    @Override
    public void onTaskCompleted(ResponsePojoGet result, TaskType taskType) throws JSONException {

        // Get Drivers
        if (TaskType.GET_DRIVERS == taskType) {
            SuccessResponse response = null;
            List<StaffPojo> pojoListDriver = new ArrayList<>();
            Log.i("AddBusDetails", "Task type is fetching drivers..");

            if (result != null) {
                Log.i("StaffDetails", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        // PARSE DRIVER CARDS
                        pojoListDriver = JsonParse.parseDrivers(response.getData());


                        if (pojoListDriver.size() > 0) {
                            Log.e("Reports Data Driver", pojoListDriver.toString());

                            cardsAdapter = new DriversCardsAdapter(pojoListDriver, this, this, this);
                            recyclerView.setAdapter(cardsAdapter);

                        } else {
                            CD.showDialog(AllDriversCards.this, "No Drivers Found");
                            pojoListDriver.clear();
                        }


                    } else {
                        CD.showDialog(AllDriversCards.this, response.getMessage());
                    }
                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(AllDriversCards.this, "Not able to fetch data");
                }
            } else {
//                CD.showDialog(AddBusDetails.this, "Result is null");
            }
        }

        // Remove staff
        else if (TaskType.REMOVE_STAFF == taskType) {
            Log.i("ASYNC TASK COMPLETED", "TASK TYPE IS Adding Entity");
            SuccessResponse successResponse = null;

            // result will be null if invalid id pass
            if (result != null) {
                successResponse = JsonParse.getSuccessResponse(result.getResponse());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.showDismissActivityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity

                }
            } else if (successResponse.getStatus().equalsIgnoreCase("NOT_FOUND")) {
                CD.showDismissActivityDialog(this, "This item cannot be deleted as it has some dependencies"); // Dialog that dismisses activity
//                    CD.showDismissActivityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
            } else {
                CD.showDismissActivityDialog(this, successResponse.getMessage());
            }
        } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
            // Handle HTTP 401 Unauthorized response (session expired)
            CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
        } else {
            Log.i("AddDriver", "Response is null");
            CD.showDismissActivityDialog(this, "Response is null.");
        }
    }


}



