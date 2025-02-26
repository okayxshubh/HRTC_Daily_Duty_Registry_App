package com.dit.hp.hrtc_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
import com.dit.hp.hrtc_app.Adapters.VehicleCardsAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.StaffPojo;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Modals.VehiclePojo;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.OnBusEditClickListener;
import com.dit.hp.hrtc_app.interfaces.OnBusMoreInfoClickListener;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerGet;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerPost;
import com.dit.hp.hrtc_app.json.JsonParse;
import com.dit.hp.hrtc_app.utilities.AppStatus;
import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;

import org.json.JSONException;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AllBusesCards extends AppCompatActivity implements OnBusEditClickListener, OnBusMoreInfoClickListener, ShubhAsyncTaskListenerGet, ShubhAsyncTaskListenerPost {

    AESCrypto aesCrypto = new AESCrypto();

    SearchView searchView;
    RecyclerView recyclerView;
    TextView filterDateTextView;
    VehicleCardsAdapter cardsAdapter; // global bus adapter..
    int depotId;
    CardView backCard;
    ImageButton addBtn;
    List<VehiclePojo> pojoList;

    CustomDialog CD = new CustomDialog();
    private Handler handler = new Handler();
    private Runnable searchRunnable;

    private boolean isLoading = false;  // To prevent duplicate API calls during scrolling
    private boolean isLastPage = false;
    private int currentPage = 0;  // Current page index
    private final int pageSize = Econstants.PAGE_SIZE;  // Number of records per page



    private static final int UPDATE_REQUEST_CODE = 1;
    private static final int TRANSFER_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_buses_cards);

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        backCard = findViewById(R.id.backCard);
        addBtn = findViewById(R.id.addBtn);

        // Setting cards layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//####################################   SERVICE CALLS   ########################################
       fetchRecords(currentPage, pageSize);

        backCard.setOnClickListener(v -> AllBusesCards.this.finish());

        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AllBusesCards.this, AddVehicle.class);
            startActivity(intent);
            AllBusesCards.this.finish();
        });


        // SearchView functionality to filter the RecyclerView
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
                        searchItem(newText); // Trigger the search after the delay
                    }
                };

                // Schedule the task to be run after 2 seconds
                handler.postDelayed(searchRunnable, Econstants.SEARCH_DELAY);

                return false;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && !isLastPage) {
                    if (layoutManager.findLastCompletelyVisibleItemPosition() == cardsAdapter.getItemCount() - 1) {
                        // Load next page
                        loadMoreItems();
                    }
                }
            }
        });
    }


    private void loadMoreItems() {
        isLoading = true;
        // Simulate fetching next page (e.g., via API call)
        currentPage++;
        fetchRecords(currentPage, pageSize);
    }

    // Vehicles Service Call
    private void fetchRecords(int page, int size) {
        try {
            if (AppStatus.getInstance(AllBusesCards.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data/paginated?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("vehicle"), "UTF-8")
                        + "&page=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(page)))
                        + "&size=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(size)))
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().depotId)), "UTF-8"));
                object.setTasktype(TaskType.GET_VEHICLES);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                isLoading = true;  // Set loading flag
                new ShubhAsyncGet(AllBusesCards.this, AllBusesCards.this, TaskType.GET_VEHICLES).execute(object);
            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AllBusesCards.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AllBusesCards.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }

    private void addMoreData(List<VehiclePojo> newData) {
        if (cardsAdapter == null) {
            // Initialize adapter if it's the first time
            cardsAdapter = new VehicleCardsAdapter(newData, this, this);
            recyclerView.setAdapter(cardsAdapter);
        } else {
            // Add data without clearing old data
            cardsAdapter.addItems(newData);
        }
        cardsAdapter.notifyDataSetChanged();  // Notify the adapter of data change
        isLoading = false;  // Reset loading flag
    }

    // Modify your searchStaff method as follows
    private void searchItem(String query) {
        try {
            if (AppStatus.getInstance(this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");  // Adjust based on actual endpoint
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("vehicle"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().depotId)), "UTF-8")
                        + "&searchByName=" + URLEncoder.encode(aesCrypto.encrypt(query), "UTF-8"));
                object.setTasktype(TaskType.GET_VEHICLE_SEARCH);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                // Trigger async call to fetch filtered staff
                new ShubhAsyncGet(this, this, TaskType.GET_VEHICLE_SEARCH).execute(object);
            } else {
                CD.showDialog(this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(this, "An error occurred. Please try again.");
        }
    }

    // Update RecyclerView with new data
    private void updateData(List<VehiclePojo> newData) {
        if (cardsAdapter == null) {
            // Initialize adapter if it's the first time
            cardsAdapter = new VehicleCardsAdapter(newData, this, this);
            recyclerView.setAdapter(cardsAdapter);
        } else {
            // Clear previous data and add new items
            cardsAdapter.clearItems();
            cardsAdapter.addItems(newData);
        }
        cardsAdapter.notifyDataSetChanged();  // Notify the adapter of data change
        isLoading = false;  // Reset loading flag
    }


    // Interface method for edit button + Cannot edit old date records
    @Override
    public void onEditClick(VehiclePojo vehiclePojo, int position) {
        if (vehiclePojo != null) {
            Intent editIntent = new Intent(this, EditVehicle.class);
            editIntent.putExtra("VehicleInfo", (Serializable) vehiclePojo);
            startActivityForResult(editIntent, UPDATE_REQUEST_CODE);
        } else {
            Log.e("onEditClick", "VehiclePojo is null!");
        }
    }

    // Interface method for edit button + Cannot edit old date records
    @Override
    public void onRemoveClick(VehiclePojo vehiclePojo, int position) {
        if (vehiclePojo != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Vehicle")
                    .setMessage("Are you sure you want to delete the vehicle " + vehiclePojo.getVehicleNumber() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (AppStatus.getInstance(AllBusesCards.this).isOnline()) {
                            UploadObject uploadObject = new UploadObject();

                            Log.e("Depot ID: ", "Depot ID: " + vehiclePojo.getId());

                            try {
                                uploadObject.setUrl(Econstants.base_url);
                                uploadObject.setMethordName("/master-data?masterName=");

                                uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("vehicle"), "UTF-8")
                                        + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(vehiclePojo.getId()))));

                                uploadObject.setTasktype(TaskType.REMOVE_VEHICLE);
                                uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            Log.i("Object", "Complete Object: " + uploadObject.toString());

                            new ShubhAsyncPost(AllBusesCards.this, AllBusesCards.this, TaskType.REMOVE_VEHICLE).execute(uploadObject);

                        } else {

                            Toast.makeText(this, "Internet not available.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            Toast.makeText(this, "No vehicle selected for removal.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onMoreInfoClick(VehiclePojo vehiclePojo, int position) {
        // Create the dialog and set the custom view
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.popup_more_info_buses, null);
        builder.setView(dialogView);

        // Find views in the dialog layout
        TextView busType = dialogView.findViewById(R.id.busType);
        TextView vehicleNumber = dialogView.findViewById(R.id.vehicleNumber);
        TextView iotDeviceId = dialogView.findViewById(R.id.iotDeviceId);
        TextView busModel = dialogView.findViewById(R.id.busModel);
        TextView busCapacity = dialogView.findViewById(R.id.busCapacity);
        TextView vehicleDepot = dialogView.findViewById(R.id.depotName);

        // Check and set the values into the views with null checks
        if (vehiclePojo != null) {
            if (vehiclePojo.getVehicleNumber() != null) {
                vehicleNumber.setText(vehiclePojo.getVehicleNumber());
            } else {
                vehicleNumber.setText("N/A");
            }

            if (vehiclePojo.getVehicleType() != null && vehiclePojo.getVehicleType().getVehicleTypeName() != null) {
                busType.setText(vehiclePojo.getVehicleType().getVehicleTypeName());
            } else {
                busType.setText("N/A");
            }

            if (vehiclePojo.getIotFirm() != null) {
                iotDeviceId.setText(vehiclePojo.getIotFirm());
            } else {
                iotDeviceId.setText("N/A");
            }

            busCapacity.setText(String.valueOf(vehiclePojo.getCapacity()));

            if (vehiclePojo.getVehicleModel() != null) {
                busModel.setText(vehiclePojo.getVehicleModel());
            } else {
                busModel.setText("N/A");
            }

            if (vehiclePojo.getDepot() != null) {
                vehicleDepot.setText(vehiclePojo.getDepot().getDepotName());
            } else {
                vehicleDepot.setText("N/A");
            }
        }

        // Show the dialog
        builder.setPositiveButton("Close", null);
        builder.create().show();
    }

    // Start activity for Result: Editing a record
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            searchView.setQuery("", false); // Clear Search View

            // Vehicles Service Call
            try {
                if (AppStatus.getInstance(AllBusesCards.this).isOnline()) {

                    UploadObject object = new UploadObject();
                    object.setUrl(Econstants.base_url);
                    object.setMethordName("/master-data?");
                    object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("vehicle"), "UTF-8")
                            + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().depotId))));

                    object.setTasktype(TaskType.GET_VEHICLES);
                    object.setAPI_NAME(Econstants.API_NAME_HRTC);

                    new ShubhAsyncGet(AllBusesCards.this, AllBusesCards.this, TaskType.GET_VEHICLES).execute(object);

                } else {
                    // Do nothing if CD already shown once
                    CD.showDialog(AllBusesCards.this, Econstants.internetNotAvailable);
                }
            } catch (Exception ex) {
                CD.showDialog(AllBusesCards.this, "Something Bad happened . Please reinstall the application and try again.");
            }

        }

        // Transfer
        else if (requestCode == TRANSFER_REQUEST_CODE && resultCode == RESULT_OK) {
            searchView.setQuery("", false); // Clear Search View

            // Vehicles Service Call
            try {
                if (AppStatus.getInstance(AllBusesCards.this).isOnline()) {

                    UploadObject object = new UploadObject();
                    object.setUrl(Econstants.base_url);
                    object.setMethordName("/master-data?");
                    object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("vehicle"), "UTF-8")
                            + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().depotId))));

                    object.setTasktype(TaskType.GET_VEHICLES);
                    object.setAPI_NAME(Econstants.API_NAME_HRTC);

                    new ShubhAsyncGet(AllBusesCards.this, AllBusesCards.this, TaskType.GET_VEHICLES).execute(object);

                } else {
                    // Do nothing if CD already shown once
                    CD.showDialog(AllBusesCards.this, Econstants.internetNotAvailable);
                }
            } catch (Exception ex) {
                CD.showDialog(AllBusesCards.this, "Something Bad happened . Please reinstall the application and try again.");
            }

        }

    }

    @Override
    public void onTaskCompleted(ResponsePojoGet result, TaskType taskType) throws JSONException {

        // Vehicles
        if (TaskType.GET_VEHICLES == taskType) {
            SuccessResponse response = null;
            pojoList = null;
            Log.i("BusDetails", "Task type is fetching vehicles..");

            if (result != null) {
                Log.i("Bus Details", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        // PARSE ALL CARDS
                        pojoList = JsonParse.parseVehicleListForCards(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data: ", pojoList.toString());

                            // Load records Pagination
                            pojoList = JsonParse.parseVehicleListForCards(response.getData());
                            addMoreData(pojoList); // Add More Data on Scroll + Keep the old data

                        } else {
//                            CD.showDialog(AllBusesCards.this, "No Vehicles Found");
                        }

                    } else {
                        CD.showDialog(AllBusesCards.this, response.getMessage());
                    }
                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(AllBusesCards.this, "Not able to fetch data");
                }
            } else {
//                CD.showDialog(AllBusesCards.this, "Result is null");
            }
        }

        // SEARCH ITEM
        else if (TaskType.GET_VEHICLE_SEARCH == taskType) {
            SuccessResponse response = null;

            if (result != null) {
                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());

                    if ("OK".equalsIgnoreCase(response.getStatus())) {
                        pojoList.clear();  // Clear previous results
                        pojoList = JsonParse.parseVehicleListForCards(response.getData());

                        if (!pojoList.isEmpty()) {
                            updateData(pojoList);  // Update RecyclerView with search results
                        } else {
                            // Clear RecyclerView when no staff is found
                            pojoList.clear();
                            updateData(pojoList);
                        }
                    } else {
                        CD.showDialog(this, response.getMessage());
                    }
                } else {
                    CD.showDialog(this, "Unable to fetch data");
                }
            }
        }

        // REMOVE VEHICLE
        else if (TaskType.REMOVE_VEHICLE == taskType) {
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
                    CD.showDismissActivityDialog(this, "This vehicle cannot be removed as it has some dependency"); // Dialog that dismisses activity
//                    CD.showDismissActivityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDismissActivityDialog(this, successResponse.getMessage());
                }
            } else {
                Log.i("AddDriver", "Response is null");
                CD.showDismissActivityDialog(this, "Response is null.");
            }
        }
    }



}

