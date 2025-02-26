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

import com.dit.hp.hrtc_app.Adapters.ConductorsCardsAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.StaffPojo;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.OnConductorEditClickListener;
import com.dit.hp.hrtc_app.interfaces.OnConductorMoreInfoClickListener;
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

public class AllConductorsCards extends AppCompatActivity implements OnConductorEditClickListener, OnConductorMoreInfoClickListener, ShubhAsyncTaskListenerGet, ShubhAsyncTaskListenerPost {

    AESCrypto aesCrypto = new AESCrypto();

    ImageButton addBtn;
    SearchView searchView;
    RecyclerView recyclerView;
    TextView filterDateTextView;
    ConductorsCardsAdapter cardsAdapter; // global bus adapter..
    String depotIdStr;
    CardView backCard;


    String alreadyAddedRoute; // If already existing record added

    CustomDialog CD = new CustomDialog();

    private static final int UPDATE_REQUEST_CODE = 1;
    private static final int TRANSFER_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_conductors_cards);

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        addBtn = findViewById(R.id.addBtn);
        backCard = findViewById(R.id.backCard);


        // Setting cards layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//####################################   SERVICE CALLS   ########################################
        depotIdStr = String.valueOf(Preferences.getInstance().depotId);

        // Get Conductor Service Call
        try {
            if (AppStatus.getInstance(AllConductorsCards.this).isOnline()) {
                UploadObject object = new UploadObject();

                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staff"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().depotId))));
                object.setTasktype(TaskType.GET_DRIVERS);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AllConductorsCards.this, AllConductorsCards.this, TaskType.GET_DRIVERS).execute(object);

            } else {
                CD.showDialog(AllConductorsCards.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AllConductorsCards.this, "Something Bad happened . Please reinstall the application and try again.");
        }


        SearchView searchView = findViewById(R.id.searchView);  // Reference your SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Filter when user submits the query
                cardsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter dynamically as text changes
                cardsAdapter.getFilter().filter(newText);
                return false;
            }
        });


        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddStaff.class);
            intent.putExtra("AddStaffType", "Conductor");
            startActivity(intent);
            AllConductorsCards.this.finish();
        });

        backCard.setOnClickListener(v -> AllConductorsCards.this.finish());


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
        if (selectedPojo != null) {
            Log.i("onEditClick", "Conductor Pojo: " + selectedPojo.toString());
            Intent editIntent = new Intent(this, EditStaff.class);
            editIntent.putExtra("EditType", "ConductorEdit");
            editIntent.putExtra("ConductorInfo", selectedPojo); // Directly pass the object
            startActivityForResult(editIntent, UPDATE_REQUEST_CODE);
        } else {
            Log.e("onEditClick", "selected pojo is null!");
        }
    }


    @Override
    public void onRemoveClick(StaffPojo selectedPojo, int position) {
        if (selectedPojo != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to remove this conductor?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        if (AppStatus.getInstance(AllConductorsCards.this).isOnline()) {
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

                            new ShubhAsyncPost(AllConductorsCards.this, AllConductorsCards.this, TaskType.REMOVE_STAFF).execute(uploadObject);

                        } else {
                            CD.addCompleteEntityDialog(AllConductorsCards.this, "Internet not Available. Please Connect to the Internet and try again.");
                        }

                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        }
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
        TextView dateOfBirth = dialogView.findViewById(R.id.dateOfBirth);
        TextView dateOfJoining = dialogView.findViewById(R.id.dateOfJoining);
        TextView employmentType = dialogView.findViewById(R.id.employmentType);
        TextView employeeCode = dialogView.findViewById(R.id.employeeCode);
        TextView genderTv = dialogView.findViewById(R.id.genderTv);
        TextView casteTv = dialogView.findViewById(R.id.casteTv);
        TextView licenceNo = dialogView.findViewById(R.id.licenceNo);
        TextView contactNo = dialogView.findViewById(R.id.contactNo);  // New field
        TextView address = dialogView.findViewById(R.id.address);      // New field

        // Set data from EmployeeInfo object
        dialogTitle.setText("More Information");
        employeeName.setText(selectedPojo.getName());
        officialId.setText(String.valueOf(selectedPojo.getId()));
        staffType.setText(selectedPojo.getStaffType());

        dateOfBirth.setText(selectedPojo.getDob());
        dateOfJoining.setText(selectedPojo.getJoiningDate());

        employmentType.setText(selectedPojo.getEmploymentType());
        employeeCode.setText(selectedPojo.getEmployeeCode());
        genderTv.setText(selectedPojo.getGender());
        casteTv.setText(selectedPojo.getCaste());
        licenceNo.setText(selectedPojo.getLicenceNo());
        contactNo.setText(String.valueOf(selectedPojo.getContactNo()));  // Set contact
        address.setText(selectedPojo.getAddress());      // Set address

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


    // Start activity for Result: Editing a record
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            searchView.setQuery("", false); // Clear Search View

            Log.i("All Conductor Cards", "Data Updated Log Executed");
            searchView.setQuery("", false); // Clear Search View
            // Get Conductors Service Call Again to refresh
            try {
                if (AppStatus.getInstance(AllConductorsCards.this).isOnline()) {
                    UploadObject object = new UploadObject();

                    object.setUrl(Econstants.base_url);
                    object.setMethordName("/master-data?");
                    object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staff"), "UTF-8")
                            + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().depotId))));
                    object.setTasktype(TaskType.GET_DRIVERS);
                    object.setAPI_NAME(Econstants.API_NAME_HRTC);

                    new ShubhAsyncGet(AllConductorsCards.this, AllConductorsCards.this, TaskType.GET_DRIVERS).execute(object);

                } else {
                    CD.showDialog(AllConductorsCards.this, Econstants.internetNotAvailable);
                }
            } catch (Exception ex) {
                CD.showDialog(AllConductorsCards.this, "Something Bad happened . Please reinstall the application and try again.");
            }
        }

    }


    @Override
    public void onTaskCompleted(ResponsePojoGet result, TaskType taskType) throws JSONException {

        // Get STAFF (Conductors and drivers) After Selecting Depot
        if (TaskType.GET_DRIVERS == taskType) {
            SuccessResponse response = null;
            List<StaffPojo> pojoListDriver = new ArrayList<>();
            List<StaffPojo> pojoListConductor = new ArrayList<>();
            Log.i("AddBusDetails", "Task type is fetching drivers..");

            if (result != null) {
                Log.i("StaffDetails", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        pojoListConductor = JsonParse.parseConductors(response.getData());

                        if (!pojoListConductor.isEmpty()) {
                            Log.e("Reports Data Driver", pojoListDriver.toString());
                            Log.e("Reports Data Conductor", pojoListConductor.toString());

                            cardsAdapter = new ConductorsCardsAdapter(pojoListConductor, this, this);
                            recyclerView.setAdapter(cardsAdapter);

                        } else {
                            CD.showDialog(AllConductorsCards.this, "No Conductors Found");
                            pojoListDriver.clear();
                        }

                    } else {
                        CD.showDialog(AllConductorsCards.this, response.getMessage());
                    }
                } else {
                    CD.showDialog(AllConductorsCards.this, "Not able to fetch data");
                }
            } else {
//                CD.showDialog(AddBusDetails.this, "Result is null");
            }
        } else if (TaskType.REMOVE_STAFF == taskType) {
            Log.i("ASYNC TASK COMPLETED", "TASK TYPE IS Adding Entity");
            SuccessResponse successResponse = null;

            // result will be null if invalid id pass
            if (result != null) {
                successResponse = JsonParse.getSuccessResponse(result.getResponse());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.showDismissActivityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity

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


}
