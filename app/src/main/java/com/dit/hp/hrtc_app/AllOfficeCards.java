package com.dit.hp.hrtc_app;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dit.hp.hrtc_app.Adapters.OfficeCardsAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.OfficePojo;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.OnOfficeCardClickListeners;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerGet;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerPost;
import com.dit.hp.hrtc_app.json.JsonParse;
import com.dit.hp.hrtc_app.utilities.AppStatus;
import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;

import org.json.JSONException;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.common.api.ResolvableApiException;


public class AllOfficeCards extends AppCompatActivity implements OnOfficeCardClickListeners, ShubhAsyncTaskListenerGet, ShubhAsyncTaskListenerPost {

    AESCrypto aesCrypto = new AESCrypto();

    SearchView searchView;
    RecyclerView recyclerView;
    OfficeCardsAdapter cardsAdapter; // global bus adapter..
    int depotId;
    CardView backCard;
    ImageButton addBtn;
    List<OfficePojo> pojoList;

    CustomDialog CD = new CustomDialog();
    private Handler handler = new Handler();
    private Runnable searchRunnable;

    private boolean isLoading = false;  // To prevent duplicate API calls during scrolling
    private boolean isLastPage = false;

    private int currentPage = 0;  // Current page index
    private final int pageSize = Econstants.PAGE_SIZE;  // Number of records per page

    private static final int UPDATE_REQUEST_CODE = 1;
    private static final int TRANSFER_REQUEST_CODE = 2;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_office_cards);

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        backCard = findViewById(R.id.backCard);
        addBtn = findViewById(R.id.addBtn);

        // Setting cards layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//####################################   SERVICE CALLS   ########################################
        fetchRecords(currentPage, pageSize);

        backCard.setOnClickListener(v -> AllOfficeCards.this.finish());

        addBtn.setOnClickListener(v -> {
            com.google.android.gms.location.LocationRequest locationRequest = com.google.android.gms.location.LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(5000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true); // force dialog
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(locationSettingsResponse -> {
                // Location is ON
                startActivity(new Intent(AllOfficeCards.this, AddOffice.class));
            });

            task.addOnFailureListener(e -> {
                if (e instanceof ResolvableApiException) {
                    try {
                        // Show Google dialog to enable location
                        ((ResolvableApiException) e).startResolutionForResult(AllOfficeCards.this, 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            });
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
        System.out.println("LOADING MORE DATA");
        isLoading = true;
        currentPage++;
        fetchRecords(currentPage, pageSize);
    }

    // Service Call for all records
    private void fetchRecords(int page, int size) {
        try {
            if (AppStatus.getInstance(AllOfficeCards.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("office"), "UTF-8")

                                // HARDCODE THIS DEPARTMENT ID FOR HRTC
                                + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Econstants.HRTC_DEPARTMENT_PARENT_ID))) // Hardcoded ID for HRTC
//                                + "&empId=" + URLEncoder.encode(aesCrypto.encrypt("0"), "UTF-8")
                                + "&page=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(page)))
//                        + "&searchByName=" + URLEncoder.encode(aesCrypto.encrypt(""), "UTF-8")
                                + "&size=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(size)))
                );
                object.setTasktype(TaskType.GET_OFFICES);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                isLoading = true;  // Set loading flag
                new ShubhAsyncGet(AllOfficeCards.this, AllOfficeCards.this, TaskType.GET_OFFICES).execute(object);
            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AllOfficeCards.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AllOfficeCards.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }

    private void addMoreData(List<OfficePojo> newData) {
        if (cardsAdapter == null) {
            cardsAdapter = new OfficeCardsAdapter(newData, this);
            recyclerView.setAdapter(cardsAdapter);
        } else {
            List<OfficePojo> existingList = cardsAdapter.getItems();
            for (OfficePojo newOffice : newData) {
                boolean alreadyExists = false;
                for (OfficePojo existing : existingList) {
                    if (existing.getOfficeId() == newOffice.getOfficeId()) {
                        alreadyExists = true;
                        break;
                    }
                }
                if (!alreadyExists) {
                    cardsAdapter.addItem(newOffice);
                }
            }
        }
        cardsAdapter.notifyDataSetChanged();
        isLoading = false;
    }

    // Modify your searchStaff method as follows
    private void searchItem(String query) {
        try {
            if (AppStatus.getInstance(AllOfficeCards.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.sarvatra_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("office"), "UTF-8")
                        // HARDCODE THIS DEPARTMENT ID FOR HRTC
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Econstants.HRTC_DEPARTMENT_PARENT_ID))) // Hardcoded ID for HRTC
                        + "&empId=" + URLEncoder.encode(aesCrypto.encrypt("0"), "UTF-8")
                        + "&searchByName=" + URLEncoder.encode(aesCrypto.encrypt(query), "UTF-8")
                );
                object.setTasktype(TaskType.GET_OFFICES_SEARCH);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);
                isLoading = true;  // Set loading flag
                new ShubhAsyncGet(AllOfficeCards.this, AllOfficeCards.this, TaskType.GET_OFFICES_SEARCH).execute(object);
            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AllOfficeCards.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AllOfficeCards.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }

    // Update RecyclerView with new data
    private void updateData(List<OfficePojo> newData) {
        if (cardsAdapter == null) {
            cardsAdapter = new OfficeCardsAdapter(newData, this);
            recyclerView.setAdapter(cardsAdapter);
        } else {
            cardsAdapter.clearItems();  // clear old list
            cardsAdapter.addItems(newData);  // add only found items
        }
        cardsAdapter.notifyDataSetChanged();
        isLoading = false;
    }


    // Interface method for edit button + Cannot edit old date records
    @Override
    public void onEditClick(OfficePojo selectedPojo, int position) {
        if (selectedPojo != null) {
            Intent editIntent = new Intent(this, EditOffice.class);
            editIntent.putExtra("OfficeInfo", (Serializable) selectedPojo);
            editIntent.putExtra("EditMode", true);
            startActivity(editIntent);
            AllOfficeCards.this.finish();
//            startActivityForResult(editIntent, UPDATE_REQUEST_CODE);

        } else {
            Log.e("onEditClick", "OfficePojo is null!");
        }
    }

    @Override
    public void onDeleteClick(OfficePojo selectedPojo, int position) {
        if (selectedPojo != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Office")
                    .setMessage("Are you sure you want to delete the office: " + selectedPojo.getOfficeName() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (AppStatus.getInstance(AllOfficeCards.this).isOnline()) {
                            UploadObject uploadObject = new UploadObject();

                            Log.e("POJO ID:: ", "POJO ID: " + selectedPojo.getOfficeId());

                            try {
                                uploadObject.setUrl(Econstants.sarvatra_url);
                                uploadObject.setMethordName("/master-data?masterName=");

                                uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("office"), "UTF-8")
                                        + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(selectedPojo.getOfficeId()))));

                                uploadObject.setTasktype(TaskType.DELETE_OFFICE);
                                uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            Log.i("Object", "Complete Object: " + uploadObject.toString());

                            new ShubhAsyncPost(AllOfficeCards.this, AllOfficeCards.this, TaskType.DELETE_OFFICE).execute(uploadObject);

                        } else {

                            Toast.makeText(this, "Internet not available.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            Toast.makeText(this, "No item selected for removal.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMoreInfoClick(OfficePojo selectedPojo, int position) {

        // Create the dialog and set the custom view
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.popup_more_info_offices, null);
        builder.setView(dialogView);

        // Find views in the dialog layout
        TextView officeId = dialogView.findViewById(R.id.officeId);
        TextView officeName = dialogView.findViewById(R.id.officeName);
        TextView officeType = dialogView.findViewById(R.id.officeType);
        TextView designationTV = dialogView.findViewById(R.id.designationTV);
        TextView pincodeTV = dialogView.findViewById(R.id.pincodeTV);
        TextView addressTV = dialogView.findViewById(R.id.addressTV);

        if (selectedPojo.getOfficeId() != -1) {
            officeId.setText(String.valueOf(selectedPojo.getOfficeId()));
        }
        officeName.setText(selectedPojo.getOfficeName() != null ? selectedPojo.getOfficeName() : "");
        officeType.setText(selectedPojo.getOfficeLevelPojo() != null && selectedPojo.getOfficeLevelPojo().getOfficeLevelName() != null ? selectedPojo.getOfficeLevelPojo().getOfficeLevelName() : "");
        designationTV.setText(selectedPojo.getDesignationPojo() != null && selectedPojo.getDesignationPojo().getDesignationName() != null ? selectedPojo.getDesignationPojo().getDesignationName() : "");
        pincodeTV.setText(selectedPojo.getPinCode() != -1 ? String.valueOf(selectedPojo.getPinCode()) : "");
        addressTV.setText(selectedPojo.getAddress() != null ? selectedPojo.getAddress() : "");

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

            fetchRecords(0, Econstants.PAGE_SIZE);
        }
    }

    @Override
    public void onTaskCompleted(ResponsePojoGet result, TaskType taskType) throws JSONException {
        //
        if (TaskType.GET_OFFICES == taskType) {
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

                        //  PARSE ALL CARDS
                        pojoList = JsonParse.parseAllOfficeCards(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data: ", pojoList.toString());

                            addMoreData(pojoList);

                        } else {
                            CD.showDialog(AllOfficeCards.this, "No Offices Found");
                        }

                    } else {
                        CD.showDialog(AllOfficeCards.this, response.getMessage());
                    }
                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_NO_CONTENT))) {
                    System.out.println("NO Content");
                } else {
                    CD.showDialog(AllOfficeCards.this, "Not able to fetch data");
                }
            } else {
//                CD.showDialog(AllBusesCards.this, "Result is null");
            }
        }

        else if (TaskType.GET_OFFICES_SEARCH == taskType) {
            SuccessResponse response = null;

            if (result != null && result.getResponseCode().equalsIgnoreCase(String.valueOf(HttpsURLConnection.HTTP_OK))) {
                response = JsonParse.getDecryptedSuccessResponse(result.getResponse());

                if ("OK".equalsIgnoreCase(response.getStatus())) {
                    List<OfficePojo> searchedList = JsonParse.parseAllOfficeCards(response.getData());

                    if (!searchedList.isEmpty()) {
                        updateData(searchedList);  // Show only searched results
                    } else {
                        cardsAdapter.clearItems(); // Clear RecyclerView
                        cardsAdapter.notifyDataSetChanged();

                        // Optional..Show toast
                    }

                } else {
                    cardsAdapter.clearItems();
                    cardsAdapter.notifyDataSetChanged();
                    CD.showDialog(this, response.getMessage());
                }
            } else {
                cardsAdapter.clearItems();
                cardsAdapter.notifyDataSetChanged();
                CD.showDialog(this, "Unable to fetch data");
            }
        }


        // REMOVE VEHICLE
        else if (TaskType.DELETE_OFFICE == taskType) {
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

