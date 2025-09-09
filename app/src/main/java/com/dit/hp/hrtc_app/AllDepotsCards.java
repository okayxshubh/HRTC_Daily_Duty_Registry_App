package com.dit.hp.hrtc_app;

import android.content.Intent;
import android.os.Bundle;
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

import com.dit.hp.hrtc_app.Adapters.DepotsCardsAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.DepotPojo;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.OnDepotCardClickListeners;
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

import javax.net.ssl.HttpsURLConnection;

public class AllDepotsCards extends AppCompatActivity implements OnDepotCardClickListeners, ShubhAsyncTaskListenerGet, ShubhAsyncTaskListenerPost {
    AESCrypto aesCrypto = new AESCrypto();
    ImageButton addBtn;
    SearchView searchView;
    RecyclerView recyclerView;
    DepotsCardsAdapter cardsAdapter; // global bus adapter..
    CardView backCard;
    String encryptedBody;

    CustomDialog CD = new CustomDialog();

    private static final int UPDATE_REQUEST_CODE = 1;
    private static final int TRANSFER_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_depot_cards);

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        addBtn = findViewById(R.id.addBtn);
        backCard = findViewById(R.id.backCard);


        // Set LayoutManager for RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

//####################################   SERVICE CALLS   ########################################

        // Depot Service Call
        try {
            if (AppStatus.getInstance(AllDepotsCards.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("depot"), "UTF-8"));
                object.setTasktype(TaskType.GET_DEPOTS);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AllDepotsCards.this, AllDepotsCards.this, TaskType.GET_DEPOTS).execute(object);

            } else {
                CD.showDialog(AllDepotsCards.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AllDepotsCards.this, "Something Bad happened . Please reinstall the application and try again.");
        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Ensure adapter is not null before filtering
                if (cardsAdapter != null) {
                    cardsAdapter.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Ensure adapter is not null before filtering
                if (cardsAdapter != null) {
                    cardsAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });


        addBtn.setOnClickListener(v -> {
            if (Preferences.getInstance().appRoleId == 1 || Preferences.getInstance().appRoleId == 2) {
                Intent intent = new Intent(this, AddDepot.class);
                startActivity(intent);
                AllDepotsCards.this.finish();
            } else {
                // Locked
                CD.showDialog(this, "You are not authorised for this action. Please contact authorised authority for further details.");
            }

        });

        backCard.setOnClickListener(v -> AllDepotsCards.this.finish());



    }


    // Interface method for edit button
    @Override
    public void onEditClick(DepotPojo selectedPojo, int position) {

        if (Preferences.getInstance().appRoleId == 1 || Preferences.getInstance().appRoleId == 2) {
            // Allow All Unlocked
            if (selectedPojo != null) {
                Intent editIntent = new Intent(this, EditDepot.class);
                editIntent.putExtra("DepotInfo", selectedPojo);
                startActivityForResult(editIntent, UPDATE_REQUEST_CODE);
            }
        } else {
            // Locked
            CD.showDialog(this, "You are not authorised for this action. Please contact authorised authority for further details.");
        }

    }

    @Override
    public void onDeleteClick(DepotPojo selectedPojo, int position) {

        if (Preferences.getInstance().appRoleId == 1 || Preferences.getInstance().appRoleId == 2) {
            // Allow All Unlocked
            if (selectedPojo != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to remove this depot?")
                        .setPositiveButton("Yes", (dialog, which) -> {

                            if (AppStatus.getInstance(AllDepotsCards.this).isOnline()) {
                                UploadObject uploadObject = new UploadObject();

                                Log.e("Depot ID: ", "Depot ID: " + selectedPojo.getId());

                                try {
                                    uploadObject.setUrl(Econstants.base_url);
                                    uploadObject.setMethordName("/master-data?masterName=");

                                    uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("depot"), "UTF-8")
                                            + "&id=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(selectedPojo.getId()))));

                                    uploadObject.setTasktype(TaskType.REMOVE_DEPOT);
                                    uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                Log.i("Object", "Complete Object: " + uploadObject.toString());

                                new ShubhAsyncPost(AllDepotsCards.this, AllDepotsCards.this, TaskType.REMOVE_DEPOT).execute(uploadObject);

                            } else {
                                CD.addCompleteEntityDialog(AllDepotsCards.this, "Internet not Available. Please Connect to the Internet and try again.");
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
    public void onMoreInfoClick(DepotPojo selectedPojo, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.popup_more_info_depot, null);

        // Bind views
        TextView depotId = dialogView.findViewById(R.id.depotId);
        TextView depotName = dialogView.findViewById(R.id.depotName);
        TextView depotCode = dialogView.findViewById(R.id.depotCode);

        // Set data
        depotId.setText(String.valueOf(selectedPojo.getId()));
        depotCode.setText(selectedPojo.getDepotCode());
        depotName.setText(selectedPojo.getDepotName());

        builder.setView(dialogView);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();

        dialog.show();
    }


    // Scroll n Load More Data
    void loadMoreData(){
        // Depot Service Call
        try {


        } catch (Exception ex) {
            CD.showDialog(AllDepotsCards.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }



    // Start activity for Result: Editing a record
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            searchView.setQuery("", false); // Clear Search View

            // Depot Service Call
            try {
                if (AppStatus.getInstance(AllDepotsCards.this).isOnline()) {
                    UploadObject object = new UploadObject();
                    object.setUrl(Econstants.base_url);
                    object.setMethordName("/master-data?");
                    object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("depot"), "UTF-8"));
                    object.setTasktype(TaskType.GET_DEPOTS);
                    object.setAPI_NAME(Econstants.API_NAME_HRTC);

                    new ShubhAsyncGet(AllDepotsCards.this, AllDepotsCards.this, TaskType.GET_DEPOTS).execute(object);

                } else {
                    CD.showDialog(AllDepotsCards.this, Econstants.internetNotAvailable);
                }
            } catch (Exception ex) {
                CD.showDialog(AllDepotsCards.this, "Something Bad happened . Please reinstall the application and try again.");
            }

        }

        if (requestCode == TRANSFER_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.i("Driver Transferred", "Data Transferred Log Executed");
            searchView.setQuery("", false); // Clear Search View

            // FETCH All records again
            // Depot Service Call
            try {
                if (AppStatus.getInstance(AllDepotsCards.this).isOnline()) {
                    UploadObject object = new UploadObject();
                    object.setUrl(Econstants.base_url);
                    object.setMethordName("/master-data?");
                    object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("depot"), "UTF-8"));
                    object.setTasktype(TaskType.GET_DEPOTS);
                    object.setAPI_NAME(Econstants.API_NAME_HRTC);

                    new ShubhAsyncGet(AllDepotsCards.this, AllDepotsCards.this, TaskType.GET_DEPOTS).execute(object);

                } else {
                    CD.showDialog(AllDepotsCards.this, Econstants.internetNotAvailable);
                }
            } catch (Exception ex) {
                CD.showDialog(AllDepotsCards.this, "Something Bad happened . Please reinstall the application and try again.");
            }

        }
    }


    @Override
    public void onTaskCompleted(ResponsePojoGet result, TaskType taskType) throws JSONException {

        // Get STAFF (Conductors and drivers) After Selecting Depot
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
                            Log.e("Reports Data Driver", pojoListDepots.toString());

                            cardsAdapter = new DepotsCardsAdapter(pojoListDepots, this);
                            recyclerView.setAdapter(cardsAdapter);


                        } else {
                            CD.showDialog(AllDepotsCards.this, "No Data Found");
                            pojoListDepots.clear();
                        }

                    } else {
                        CD.showDialog(AllDepotsCards.this, response.getMessage());
                    }
                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(AllDepotsCards.this, "Not able to fetch data");
                }
            } else {
//                CD.showDialog(AddBusDetails.this, "Result is null");
            }
        }

        // Remove depot
        else if (TaskType.REMOVE_DEPOT == taskType) {
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
