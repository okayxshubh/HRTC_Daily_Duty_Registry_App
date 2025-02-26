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

import com.dit.hp.hrtc_app.Adapters.AddaCardsAdapter;
import com.dit.hp.hrtc_app.Adapters.StaffCardsAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.AddaPojo;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.StaffPojo;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.OnAddaCardClickListeners;
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

public class AllAddaCards extends AppCompatActivity implements OnAddaCardClickListeners, ShubhAsyncTaskListenerGet, ShubhAsyncTaskListenerPost {
    AESCrypto aesCrypto = new AESCrypto();
    ImageButton addBtn;
    SearchView searchView;
    RecyclerView recyclerView;
    AddaCardsAdapter cardsAdapter; // global bus adapter..
    CardView backCard;
    String encryptedBody;
    List<AddaPojo> pojoList = new ArrayList<>();

    CustomDialog CD = new CustomDialog();
    private Handler handler = new Handler();
    private Runnable searchRunnable;

    private static final int UPDATE_REQUEST_CODE = 1;
    private int currentPage = 0;  // Current page index
    private final int pageSize = Econstants.PAGE_SIZE;  // Number of records per page
    private boolean isLoading = false;  // To prevent duplicate API calls during scrolling

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_adda_cards);

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        addBtn = findViewById(R.id.addBtn);
        backCard = findViewById(R.id.backCard);

        // Setting cards layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//####################################   SERVICE CALLS   ########################################
        // Adda Service Call
        fetchRecords(currentPage, pageSize);
//###############################################################################################

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

        addBtn.setOnClickListener(v -> {
            if (Preferences.getInstance().roleId != null && Preferences.getInstance().roleId == 1 || Preferences.getInstance().roleId == 2) {
                Intent intent = new Intent(this, AddAdda.class);
                startActivity(intent);
                AllAddaCards.this.finish();
            } else {
                // Locked
                CD.showDialog(this, "You are not authorised for this action. Please contact authorised authority for further details.");
            }

        });

        backCard.setOnClickListener(v -> AllAddaCards.this.finish());

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

    // Fetch records based on current page and size
    private void fetchRecords(int page, int size) {
        try {
            if (AppStatus.getInstance(this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data/paginated?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("adda"), "UTF-8")
                        + "&page=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(page)))
                        + "&size=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(size)))
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().depotId)), "UTF-8"));
                object.setTasktype(TaskType.GET_ADDA);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                isLoading = true;  // Set loading flag
                new ShubhAsyncGet(this, this, TaskType.GET_ADDA).execute(object);
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
    private void addMoreData(List<AddaPojo> newData) {
        if (cardsAdapter == null) {
            // Initialize adapter if it's the first time
            cardsAdapter = new AddaCardsAdapter(newData, this);
            recyclerView.setAdapter(cardsAdapter);
        } else {
            // Add data without clearing old data
            cardsAdapter.addItems(newData);
        }
        cardsAdapter.notifyDataSetChanged();  // Notify the adapter of data change
        isLoading = false;  // Reset loading flag
    }

    // Update RecyclerView with new data
    private void updateData(List<AddaPojo> newData) {
        if (cardsAdapter == null) {
            // Initialize adapter if it's the first time
            cardsAdapter = new AddaCardsAdapter(newData, this);
            recyclerView.setAdapter(cardsAdapter);
        } else {
            // Clear previous data and add new items
            cardsAdapter.clearItems();
            cardsAdapter.addItems(newData);
        }
        cardsAdapter.notifyDataSetChanged();  // Notify the adapter of data change
        isLoading = false;  // Reset loading flag
    }


    private void searchItem(String query) {
        try {
            if (AppStatus.getInstance(this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");  // Adjust based on actual endpoint
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("adda"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().depotId)), "UTF-8")
                        + "&searchByName=" + URLEncoder.encode(aesCrypto.encrypt(query), "UTF-8"));
                object.setTasktype(TaskType.GET_ADDA_SEARCH);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                isLoading = true;  // Set loading flag
                // Trigger async call to fetch filtered staff
                new ShubhAsyncGet(this, this, TaskType.GET_ADDA_SEARCH).execute(object);
            } else {
                CD.showDialog(this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(this, "An error occurred. Please try again.");
        }
    }


    @Override
    public void onEditClick(AddaPojo selectedPojo, int position) {
        if (Preferences.getInstance().roleId == 1 || Preferences.getInstance().roleId == 2) {
            // Allow All Unlocked
            if (selectedPojo != null) {
                Intent editIntent = new Intent(this, AddAdda.class);
                editIntent.putExtra("addaDetails", selectedPojo);
                editIntent.putExtra("isEditMode", true);
                startActivityForResult(editIntent, UPDATE_REQUEST_CODE);
            }
        } else {
            // Locked
            CD.showDialog(this, "You are not authorised for this action. Please contact authorised authority for further details.");
        }
    }

    @Override
    public void onDeleteClick(AddaPojo selectedPojo, int position) {
        if (Preferences.getInstance().roleId == 1 || Preferences.getInstance().roleId == 2) {
            // Allow All Unlocked
            if (selectedPojo != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to remove this adda?")
                        .setPositiveButton("Yes", (dialog, which) -> {

                            if (AppStatus.getInstance(AllAddaCards.this).isOnline()) {
                                UploadObject uploadObject = new UploadObject();
                                try {
                                    uploadObject.setUrl(Econstants.base_url);
                                    uploadObject.setMethordName("/master-data?masterName=");

                                    uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("adda"), "UTF-8")
                                            + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(selectedPojo.getId()))));

                                    uploadObject.setTasktype(TaskType.REMOVE_ADDA);
                                    uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Log.i("Object", "Complete Object: " + uploadObject.toString());
                                new ShubhAsyncPost(AllAddaCards.this, AllAddaCards.this, TaskType.REMOVE_ADDA).execute(uploadObject);

                            } else {
                                CD.addCompleteEntityDialog(AllAddaCards.this, "Internet not Available. Please Connect to the Internet and try again.");
                            }

                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                Toast.makeText(this, "Invalid staff selected.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Locked
            CD.showDialog(this, "You are not authorised for this action. Please contact authorised authority for further details.");
        }
    }

    @Override
    public void onMoreInfoClick(AddaPojo selectedPojo, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.popup_more_info_adda, null);

        // Bind views
        TextView addaId = dialogView.findViewById(R.id.addaId);
        TextView addaName = dialogView.findViewById(R.id.addaName);
        TextView locationName = dialogView.findViewById(R.id.location);
        TextView associatedDepot = dialogView.findViewById(R.id.associatedDepot);
        TextView addaIncharge = dialogView.findViewById(R.id.addaIncharge);

        // Set data
        addaId.setText(String.valueOf(selectedPojo.getId()));
        addaName.setText(selectedPojo.getAddaName());

        if (selectedPojo.getLocation() != null) {
            locationName.setText(selectedPojo.getLocation().getName());
        } else {
            locationName.setText("N/A");
        }

        if (selectedPojo.getDepot() != null) {
            associatedDepot.setText(selectedPojo.getDepot().getDepotName());
        } else {
            associatedDepot.setText("N/A");
        }

        if (selectedPojo.getAddaIncharge() != null) {
            addaIncharge.setText(selectedPojo.getAddaIncharge().getName());
        } else {
            addaIncharge.setText("N/A");
        }

        builder.setView(dialogView);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();

        dialog.show();
    }


    // Start activity for Result: Editing a record
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            searchView.setQuery("", false); // Clear Search View

            // FETCH All records again
            try {
                if (AppStatus.getInstance(AllAddaCards.this).isOnline()) {
                    UploadObject object = new UploadObject();
                    object.setUrl(Econstants.base_url);
                    object.setMethordName("/master-data?");
                    object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("adda"), "UTF-8")
                            + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().depotId)), "UTF-8"));
                    object.setTasktype(TaskType.GET_ADDA);
                    object.setAPI_NAME(Econstants.API_NAME_HRTC);

                    new ShubhAsyncGet(AllAddaCards.this, AllAddaCards.this, TaskType.GET_ADDA).execute(object);

                } else {
                    CD.showDialog(AllAddaCards.this, Econstants.internetNotAvailable);
                }
            } catch (Exception ex) {
                CD.showDialog(AllAddaCards.this, "Something Bad happened . Please reinstall the application and try again.");
            }


        }

    }


    @Override
    public void onTaskCompleted(ResponsePojoGet result, TaskType taskType) throws JSONException {

        if (TaskType.GET_ADDA == taskType) {
            SuccessResponse response = null;

            if (result != null) {
                Log.i("Depots: ", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        pojoList = JsonParse.parseAddaCardList(response.getData());

                        if (pojoList.size() > 0) {
                            Log.e("Reports Data Driver", pojoList.toString());

                            // Load records Pagination
                            pojoList = JsonParse.parseAddaCardList(response.getData());
                            addMoreData(pojoList); // Add More Data on Scroll + Keep the old data

                        } else {
//                            CD.showDialog(AllStaffCards.this, "No Staff Members Found");
                        }
                    } else {
                        CD.showDialog(AllAddaCards.this, response.getMessage());
                    }


                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(AllAddaCards.this, "Not able to fetch data");
                }
            } else {
//                CD.showDialog(AddBusDetails.this, "Result is null");
            }
        }

        // SEARCH STAFF
        else if (TaskType.GET_ADDA_SEARCH == taskType) {
            SuccessResponse response = null;


            if (result != null) {
                Log.i("Depots: ", "Response Obj" + result.toString());


                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());

                    if ("OK".equalsIgnoreCase(response.getStatus())) {
                        pojoList.clear();  // Clear previous results
                        pojoList = JsonParse.parseAddaCardListSearched(response.getData());

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
        
        // REMOVE
        else if (TaskType.REMOVE_ADDA == taskType) {
            Log.i("ASYNC TASK COMPLETED", "TASK TYPE IS Adding Entity");
            SuccessResponse successResponse = null;

            // result will be null if invalid id pass
            if (result != null) {
                successResponse = JsonParse.getSuccessResponse(result.getResponse());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    Log.i("Add Entity Response", successResponse.getData());
                    CD.showDismissActivityDialog(this, successResponse.getMessage()); // Dialog that dismisses activity

                }  else if (successResponse.getStatus().equalsIgnoreCase("NOT_FOUND")) {
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
