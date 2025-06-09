package com.dit.hp.hrtc_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dit.hp.hrtc_app.Adapters.StaffCardsAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.StaffPojo;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.OnStaffCardClickListeners;
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

public class AllStaffCards extends AppCompatActivity implements OnStaffCardClickListeners, ShubhAsyncTaskListenerGet, ShubhAsyncTaskListenerPost {

    AESCrypto aesCrypto = new AESCrypto();
    ImageButton addBtn;
    SearchView searchView;
    RecyclerView recyclerView;
    StaffCardsAdapter cardsAdapter; // global bus adapter..
    CardView backCard;
    List<StaffPojo> pojoListStaff; // Global staff list to show in adapter

    private Handler handler = new Handler();
    private Runnable searchRunnable;

    CustomDialog CD = new CustomDialog();

    private static final int UPDATE_REQUEST_CODE = 1;

    private int currentPage = 0;  // Current page index
    private final int pageSize = Econstants.PAGE_SIZE;  // Number of records per page

    private boolean isLoading = false;  // To prevent duplicate API calls during scrolling


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_staff_cards);

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        addBtn = findViewById(R.id.addBtn);
        backCard = findViewById(R.id.backCard);


        // Setting cards layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//####################################   SERVICE CALLS   ########################################

        // Get Staff Service Call Normally
        fetchRecords(currentPage, pageSize);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Not needed for this case
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Cancel any previous scheduled search task
                handler.removeCallbacks(searchRunnable);

                // Create a new task to perform the search after a 2-second delay
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        searchStaff(newText); // Trigger the search after the delay
                    }
                };

                // Schedule the task to be run after 2 seconds
                handler.postDelayed(searchRunnable, Econstants.SEARCH_DELAY);

                return false;
            }
        });

        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddStaff.class);
            intent.putExtra("AddStaffType", "Driver");
            startActivity(intent);
            AllStaffCards.this.finish();
        });

        backCard.setOnClickListener(v -> AllStaffCards.this.finish());

        // Add scroll listener for pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (dy > 0 && !isLoading && layoutManager != null) {
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    // Trigger load more when near the last item
                    if (lastVisibleItemPosition >= totalItemCount - 1) {
                        loadMoreData();  // Load more data
                    }
                }
            }
        });


    }


    private void searchStaff(String query) {
        try {
            if (AppStatus.getInstance(this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");  // Adjust based on actual endpoint
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staff"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().regionalOfficeId)), "UTF-8")
                        + "&searchByName=" + URLEncoder.encode(aesCrypto.encrypt(query), "UTF-8"));
                object.setTasktype(TaskType.GET_STAFF_SEARCH);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                // Trigger async call to fetch filtered staff
                new ShubhAsyncGet(this, this, TaskType.GET_STAFF_SEARCH).execute(object);
            } else {
                CD.showDialog(this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(this, "An error occurred. Please try again.");
        }
    }

    // Fetch records based on current page and size
    private void fetchRecords(int page, int size) {
        try {
            if (AppStatus.getInstance(this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data/paginated?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staff"), "UTF-8")
                        + "&page=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(page)))
                        + "&size=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(size)))
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().regionalOfficeId)), "UTF-8"));
                object.setTasktype(TaskType.GET_STAFF);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                isLoading = true;  // Set loading flag
                new ShubhAsyncGet(this, this, TaskType.GET_STAFF).execute(object);
            } else {
                CD.showDialog(this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(this, "Something Bad happened. Please reinstall the application and try again.");
        }
    }

    // Load more data when scrolling
    private void loadMoreData() {
        currentPage++;  // Increment page number
        fetchRecords(currentPage, pageSize);
    }

    // Add more data to the list
    private void addMoreData(List<StaffPojo> newData) {
        if (cardsAdapter == null) {
            // Initialize adapter if it's the first time
            cardsAdapter = new StaffCardsAdapter(newData, this);
            recyclerView.setAdapter(cardsAdapter);
        } else {
            // Add data without clearing old data
            cardsAdapter.addItems(newData);
        }
        cardsAdapter.notifyDataSetChanged();  // Notify the adapter of data change
        isLoading = false;  // Reset loading flag
    }

    // Update RecyclerView with new data
    private void updateData(List<StaffPojo> newData) {
        if (cardsAdapter == null) {
            // Initialize adapter if it's the first time
            cardsAdapter = new StaffCardsAdapter(newData, this);
            recyclerView.setAdapter(cardsAdapter);
        } else {
            // Clear previous data and add new items
            cardsAdapter.clearItems();
            cardsAdapter.addItems(newData);
        }
        cardsAdapter.notifyDataSetChanged();  // Notify the adapter of data change
        isLoading = false;  // Reset loading flag
    }

    @Override
    public void onEditClick(StaffPojo selectedPojo, int position) {
        Intent editIntent = new Intent(this, EditStaff.class);
        editIntent.putExtra("EditType", "DriverEdit");
        editIntent.putExtra("DriverInfo", selectedPojo);
//        startActivityForResult(editIntent, UPDATE_REQUEST_CODE);
        startActivity(editIntent);
        AllStaffCards.this.finish();
    }

    @Override
    public void onDeleteClick(StaffPojo selectedPojo, int position) {
        if (selectedPojo != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to remove this staff member?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        if (AppStatus.getInstance(AllStaffCards.this).isOnline()) {
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
                            new ShubhAsyncPost(AllStaffCards.this, AllStaffCards.this, TaskType.REMOVE_STAFF).execute(uploadObject);

                        } else {
                            CD.addCompleteEntityDialog(AllStaffCards.this, "Internet not Available. Please Connect to the Internet and try again.");
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

    // Start activity for Result: Editing a record
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {

            Log.i("Drivers Updated", "Data Updated Log Executed");
            searchView.setQuery("", false); // Clear Search View

            // Get Staff Service Call from Starting Page
            fetchRecords(0, pageSize);
        }

    }


    @Override
    public void onTaskCompleted(ResponsePojoGet result, TaskType taskType) throws JSONException {

        // Get Staff
        if (TaskType.GET_STAFF == taskType) {
            SuccessResponse response = null;
//            pojoListStaff = new ArrayList<>();
            Log.i("AddBusDetails", "Task type is fetching drivers..");

            if (result != null) {
                Log.i("StaffDetails", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        // PARSE ALL STAFF CARDS
                        pojoListStaff = JsonParse.parseAllStaffList(response.getData());

                        if (pojoListStaff.size() > 0) {
                            Log.e("Reports Data Driver", pojoListStaff.toString());

                            // Load records Pagination
                            pojoListStaff = JsonParse.parseAllStaffList(response.getData());
                            addMoreData(pojoListStaff); // Add More Data on Scroll + Keep the old data

                        } else {
//                            CD.showDialog(AllStaffCards.this, "No Staff Members Found");
                        }

                    } else {
                        CD.showDialog(AllStaffCards.this, response.getMessage());
                    }
                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(AllStaffCards.this, "Not able to fetch data");
                }
            } else {
//                CD.showDialog(AddBusDetails.this, "Result is null");
            }
        }

        // SEARCH STAFF
        else if (TaskType.GET_STAFF_SEARCH == taskType) {
            SuccessResponse response = null;

            if (result != null) {
                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());

                    if ("OK".equalsIgnoreCase(response.getStatus())) {
                        pojoListStaff.clear();  // Clear previous results
                        pojoListStaff = JsonParse.parseAllStaffListSearched(response.getData());

                        if (!pojoListStaff.isEmpty()) {
                            updateData(pojoListStaff);  // Update RecyclerView with search results
                        } else {
                            // Clear RecyclerView when no staff is found
                            pojoListStaff.clear();
                            updateData(pojoListStaff);
                        }
                    } else {
                        CD.showDialog(this, response.getMessage());
                    }
                } else {
                    CD.showDialog(this, "Unable to fetch data");
                }
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

