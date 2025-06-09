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

import com.dit.hp.hrtc_app.Adapters.RouteCardsAdapter;
import com.dit.hp.hrtc_app.Adapters.VehicleCardsAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.RoutePojo;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Modals.VehiclePojo;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.OnRouteEditClickListener;
import com.dit.hp.hrtc_app.interfaces.OnRouteMoreInfoClickListener;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerGet;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerPost;
import com.dit.hp.hrtc_app.json.JsonParse;
import com.dit.hp.hrtc_app.utilities.AppStatus;
import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;

import org.json.JSONException;

import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class AllRouteCards extends AppCompatActivity implements OnRouteEditClickListener, OnRouteMoreInfoClickListener, ShubhAsyncTaskListenerGet, ShubhAsyncTaskListenerPost {
    AESCrypto aesCrypto = new AESCrypto();

    ImageButton addBtn;
    SearchView searchView;
    RecyclerView recyclerView;
    RouteCardsAdapter cardsAdapter;
    CardView backCard;

    CustomDialog CD = new CustomDialog();
    private Handler handler = new Handler();
    private Runnable searchRunnable;
    private boolean isLoading = false;  // To prevent duplicate API calls during scrolling
    private boolean isLastPage = false;
    private int currentPage = 0;  // Current page index
    private final int pageSize = Econstants.PAGE_SIZE;  // Number of records per page


    List<RoutePojo> pojoList = null;

    private static final int UPDATE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_routes_cards);

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        addBtn = findViewById(R.id.addBtn);
        backCard = findViewById(R.id.backCard);

        // Setting cards layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//#####################################################   SERVICE CALLS   ##############################################################
        // Route Service Call
        fetchRecords(currentPage, pageSize);
//######################################################################################################################################

        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddRoute.class);
            startActivity(intent);
            AllRouteCards.this.finish();
        });

        backCard.setOnClickListener(v -> AllRouteCards.this.finish());

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
            if (AppStatus.getInstance(AllRouteCards.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data/paginated?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("route"), "UTF-8")
                        + "&page=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(page)))
                        + "&size=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(size)))
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().regionalOfficeId)), "UTF-8"));
                object.setTasktype(TaskType.GET_ROUTES);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                isLoading = true;  // Set loading flag
                new ShubhAsyncGet(AllRouteCards.this, AllRouteCards.this, TaskType.GET_ROUTES).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AllRouteCards.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AllRouteCards.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }


    private void addMoreData(List<RoutePojo> newData) {
        if (cardsAdapter == null) {
            // Initialize adapter if it's the first time
            cardsAdapter = new RouteCardsAdapter(newData, this, this);
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
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("route"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().regionalOfficeId)), "UTF-8")
                        + "&searchByName=" + URLEncoder.encode(aesCrypto.encrypt(query), "UTF-8"));
                object.setTasktype(TaskType.GET_ROUTES_SEARCH);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                // Trigger async call to fetch filtered staff
                new ShubhAsyncGet(this, this, TaskType.GET_ROUTES_SEARCH).execute(object);
            } else {
                CD.showDialog(this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(this, "An error occurred. Please try again.");
        }
    }


    // Interface method for edit button
    @Override
    public void onEditClick(RoutePojo selectedPojo, int position) {
        Intent editIntent = new Intent(this, AddRoute.class);
        editIntent.putExtra("routeDetails", selectedPojo);
        editIntent.putExtra("isEditMode", true);
        startActivityForResult(editIntent, UPDATE_REQUEST_CODE);
    }

    @Override
    public void onStopsClick(RoutePojo selectedPojo, int position) {
        Intent stopIntent = new Intent(this, AddStop.class);
        stopIntent.putExtra("routeDetails", selectedPojo);
        startActivityForResult(stopIntent, UPDATE_REQUEST_CODE);
    }

    @Override
    public void onRemoveClick(RoutePojo selectedPojo, int position) {
        if (selectedPojo != null) {
            Log.e("ABC", "ABC : " + String.valueOf(selectedPojo.getRouteId()));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to remove this route?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        if (AppStatus.getInstance(AllRouteCards.this).isOnline()) {
                            UploadObject uploadObject = new UploadObject();

                            try {
                                uploadObject.setUrl(Econstants.base_url);
                                uploadObject.setMethordName("/master-data?masterName=");

                                uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("route"), "UTF-8")
                                        + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(selectedPojo.getRouteId()))));

                                uploadObject.setTasktype(TaskType.REMOVE_ROUTE);
                                uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Log.i("Object", "Complete Object: " + uploadObject.toString());
                            new ShubhAsyncPost(AllRouteCards.this, AllRouteCards.this, TaskType.REMOVE_ROUTE).execute(uploadObject);

                        } else {
                            CD.addCompleteEntityDialog(AllRouteCards.this, "Internet not Available. Please Connect to the Internet and try again.");
                        }

                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            Toast.makeText(this, "Invalid staff selected.", Toast.LENGTH_SHORT).show();
        }
    }
    
    public void onMoreInfoClick(RoutePojo selectedPojo, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.popup_more_info_route, null);

        // Bind views in the dialog
        TextView routeId = dialogView.findViewById(R.id.routeId);
        TextView routeName = dialogView.findViewById(R.id.routeName);
        TextView startTime = dialogView.findViewById(R.id.startTime);
        TextView journeyHours = dialogView.findViewById(R.id.journeyHours);
        TextView distance = dialogView.findViewById(R.id.distance);
        TextView startLocation = dialogView.findViewById(R.id.startLocation);
        TextView endLocation = dialogView.findViewById(R.id.endLocation);
        TextView routeType = dialogView.findViewById(R.id.routeType);

        // Set data for the dialog views
        routeId.setText(String.valueOf(selectedPojo.getRouteId())); // Assuming routeId is an integer
        routeName.setText(selectedPojo.getRouteName());
        startTime.setText(selectedPojo.getStartTime());
        journeyHours.setText(String.valueOf(selectedPojo.getJourneyHours()));
        distance.setText(String.valueOf(selectedPojo.getDistance()));

        // Set start and end location names
        startLocation.setText(selectedPojo.getStartLocationPojo().getName());
        endLocation.setText(selectedPojo.getEndLocationPojo().getName());

        // Set route type (if available)
        if (selectedPojo.getRouteTypePojo() != null) {
            routeType.setText(selectedPojo.getRouteTypePojo().getRouteTypeName());
        }

        // Build dialog
        builder.setView(dialogView);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();

        // Set custom height for the dialog
        int heightInDp = 500; // Desired height in dp
        int heightInPx = (int) (heightInDp * getResources().getDisplayMetrics().density);

        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                heightInPx
        );

        dialog.show();
    }


    // Update RecyclerView with new data
    private void updateData(List<RoutePojo> newData) {
        if (cardsAdapter == null) {
            // Initialize adapter if it's the first time
            cardsAdapter = new RouteCardsAdapter(newData, this, this);
            recyclerView.setAdapter(cardsAdapter);
        } else {
            // Clear previous data and add new items
            cardsAdapter.clearItems();
            cardsAdapter.addItems(newData);
        }
        cardsAdapter.notifyDataSetChanged();  // Notify the adapter of data change
        isLoading = false;  // Reset loading flag
    }


    // Start activity for Result: Editing a record
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            searchView.setQuery("", false); // Clear Search View

            try {
                if (AppStatus.getInstance(AllRouteCards.this).isOnline()) {
                    UploadObject object = new UploadObject();
                    object.setUrl(Econstants.base_url);
                    object.setMethordName("/master-data?");
                    object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("route"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().regionalOfficeId)), "UTF-8"));
                    object.setTasktype(TaskType.GET_ROUTES);
                    object.setAPI_NAME(Econstants.API_NAME_HRTC);

                    new ShubhAsyncGet(AllRouteCards.this, AllRouteCards.this, TaskType.GET_ROUTES).execute(object);

                } else {
                    CD.showDialog(AllRouteCards.this, Econstants.internetNotAvailable);
                }
            } catch (Exception ex) {
                CD.showDialog(AllRouteCards.this, "Something Bad happened . Please reinstall the application and try again.");
            }

        }
    }

    @Override
    public void onTaskCompleted(ResponsePojoGet result, TaskType taskType) throws JSONException {

        // Get Routes from Depot
        if (TaskType.GET_ROUTES == taskType) {
            SuccessResponse response = null;
            Log.i("AddBusDetails", "Task type is fetching routes..");

            if (result != null) {
                Log.i("AddBusDetails", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoList = JsonParse.parseRouteCards(response.getData());
                        Log.i("pojoList", pojoList.toString());


                        if (pojoList.size() > 0) {
                            Log.e("Reports Data: ", pojoList.toString());

                            // Load records Pagination
                            pojoList = JsonParse.parseRouteCards(response.getData());
                            addMoreData(pojoList); // Add More Data on Scroll + Keep the old data

                        } else {
//                            CD.showDialog(AllBusesCards.this, "No Vehicles Found");
                        }

                    } else {
                        CD.showDialog(AllRouteCards.this, response.getMessage());
                    }
                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(AllRouteCards.this, "Not able to connect to the server" + result.getResponse());
                }
            } else {
//                CD.showDialog(AddBusDetails.this, "Result is null");
            }
        }

        // SEARCH ITEM
        else if (TaskType.GET_ROUTES_SEARCH == taskType) {
            SuccessResponse response = null;

            if (result != null) {
                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());

                    if ("OK".equalsIgnoreCase(response.getStatus())) {
                        pojoList.clear();  // Clear previous results
                        pojoList = JsonParse.parseRouteCardsSearched(response.getData());

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

        // Remove Route
        else if (TaskType.REMOVE_ROUTE == taskType) {
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
                    CD.showDismissActivityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity
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
