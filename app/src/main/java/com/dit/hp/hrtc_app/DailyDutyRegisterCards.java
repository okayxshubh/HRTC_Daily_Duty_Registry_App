package com.dit.hp.hrtc_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dit.hp.hrtc_app.Adapters.InfoCardsAdapter;
import com.dit.hp.hrtc_app.Adapters.StaffCardsAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Modals.DailyRegisterCardFinal;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.StaffPojo;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.OnEditClickListener;
import com.dit.hp.hrtc_app.interfaces.OnMoreInfoClickListener;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerGet;
import com.dit.hp.hrtc_app.json.JsonParse;
import com.dit.hp.hrtc_app.utilities.AppStatus;
import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;

import org.json.JSONException;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class DailyDutyRegisterCards extends AppCompatActivity implements OnEditClickListener, OnMoreInfoClickListener, ShubhAsyncTaskListenerGet {

    AESCrypto aesCrypto = new AESCrypto();

    SearchView searchView;
    RecyclerView recyclerView;
    TextView filterDateTextView;
    InfoCardsAdapter cardsAdapter; // global bus adapter..
    String currentDate;
    ImageButton addBtn;
    CardView backCard;
    String alreadyAddedRoute; // If already existing record added
    List<DailyRegisterCardFinal> pojoList = null;

    String dateFilterDate;

    CustomDialog CD = new CustomDialog();
    private Handler handler = new Handler();
    private Runnable searchRunnable;

    private int currentPage = 0;  // Current page index
    private final int pageSize = Econstants.PAGE_SIZE;  // Number of records per page

    private boolean isLoading = false;  // To prevent duplicate API calls during scrolling

    private static final int UPDATE_REQUEST_CODE = 1;

    // Global Calender for filter.. for setting current date
    final Calendar calendar = Calendar.getInstance();
    int[] selectedDate = {calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duty_register);

        alreadyAddedRoute = getIntent().getStringExtra("AlreadyExistingRoute");
        if (Econstants.isNotEmpty(alreadyAddedRoute)) {
            Log.i("DutyRegisterCards", "Already Added Route Name: " + alreadyAddedRoute);
        }

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        filterDateTextView = findViewById(R.id.sortTextView);
        addBtn = findViewById(R.id.addBtn);
        backCard = findViewById(R.id.backCard);

        currentDate = String.format("%02d-%02d-%04d", selectedDate[2], selectedDate[1] + 1, selectedDate[0]);
        Log.i("DutyRegisterCards", "Default Filter Date: " + currentDate);
        filterDateTextView.setText(currentDate); // Set current date by default

        // Setting cards layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//####################################   SERVICE CALLS   ########################################

        // Get All Records + Normally + Current Date + Pagination
        dateFilterDate = currentDate;
        fetchRecords(currentPage, pageSize, dateFilterDate);

        // Filter Date Service call
        filterDateTextView.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(DailyDutyRegisterCards.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {

                        selectedDate[0] = selectedYear;
                        selectedDate[1] = selectedMonth;
                        selectedDate[2] = selectedDay;

                        String formattedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear);
                        filterDateTextView.setText(formattedDate);
                        dateFilterDate = formattedDate;

                        Log.i("DutyRegisterCards", "Selected Filter Date: " + formattedDate);
                        searchView.setQuery("", true); // Clear search query

                        // Clear old data from adapter
                        if (cardsAdapter != null) {
                            cardsAdapter.clearItems();
                            cardsAdapter.notifyDataSetChanged();
                        }

                        currentPage = 0; // Reset pagination

                        // Fetch new records
                        fetchRecords(currentPage, pageSize, dateFilterDate);

                    }, selectedDate[0], selectedDate[1], selectedDate[2]);

            datePickerDialog.show();
        });



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
                        searchRecord(newText, dateFilterDate); // Trigger the search after the delay
                    }
                };

                // Schedule the task to be run after 2 seconds
                handler.postDelayed(searchRunnable, Econstants.SEARCH_DELAY);

                return false;
            }
        });


        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DailyDutyRegisterCards.this, AddDailyRecord.class);
            startActivity(intent);
            DailyDutyRegisterCards.this.finish();
        });

        backCard.setOnClickListener(v -> {
            DailyDutyRegisterCards.this.finish();
        });


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
    private void fetchRecords(int page, int size, String date) {
        try {
            if (AppStatus.getInstance(this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data/paginated?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("dailyDuty"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().regionalOfficeId)), "UTF-8")
                        + "&dutyDate=" + URLEncoder.encode(aesCrypto.encrypt(date), "UTF-8")
                        + "&page=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(page)))
                        + "&size=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(size)))
                );
                object.setTasktype(TaskType.GET_ALL_RECORDS);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                isLoading = true;  // Set loading flag
                new ShubhAsyncGet(this, this, TaskType.GET_ALL_RECORDS).execute(object);
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
        fetchRecords(currentPage, pageSize, dateFilterDate);
    }

    // Add more data to the list
    private void addMoreData(List<DailyRegisterCardFinal> newData) {
        if (cardsAdapter == null) {
            // Initialize adapter if it's the first time
            cardsAdapter = new InfoCardsAdapter(newData, this, this);
            recyclerView.setAdapter(cardsAdapter);
        } else {
            // Add data without clearing old data
            cardsAdapter.addItems(newData);
        }
        cardsAdapter.notifyDataSetChanged();  // Notify the adapter of data change
        isLoading = false;  // Reset loading flag
    }

    // Update RecyclerView with new data
    private void updateData(List<DailyRegisterCardFinal> newData) {
        if (cardsAdapter == null) {
            // Initialize adapter if it's the first time
            cardsAdapter = new InfoCardsAdapter(newData, this, this);
            recyclerView.setAdapter(cardsAdapter);
        } else {
            // Clear previous data and add new items
            cardsAdapter.clearItems();
            cardsAdapter.addItems(newData);
        }
        cardsAdapter.notifyDataSetChanged();  // Notify the adapter of data change
        isLoading = false;  // Reset loading flag
    }

    private void searchRecord(String query, String date) {
        try {
            if (AppStatus.getInstance(this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");  // Adjust based on actual endpoint

                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("dailyDuty"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().regionalOfficeId)), "UTF-8")
                        + "&dutyDate=" + URLEncoder.encode(aesCrypto.encrypt(date), "UTF-8")
                        + "&searchByName=" + URLEncoder.encode(aesCrypto.encrypt(query), "UTF-8"));
                object.setTasktype(TaskType.GET_RECORD_SEARCH);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                // Trigger async call to fetch filtered staff
                new ShubhAsyncGet(this, this, TaskType.GET_RECORD_SEARCH).execute(object);
            } else {
                CD.showDialog(this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(this, "An error occurred. Please try again.");
        }
    }

    // Interface method for edit button + Cannot edit old date records
    @Override
    public void onEditClick(DailyRegisterCardFinal busInfo, int position) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date currentDateObj = sdf.parse(currentDate);

            // Specific date to compare with
            String fetchedDateStr = busInfo.getRecordDate();
            Date fetchedDateObj = sdf.parse(fetchedDateStr);

            if (fetchedDateObj.before(currentDateObj)) {
                CD.showDialog(this, "Old records cannot be edited");
            } else {
                // Can Edit
                Intent intent = new Intent(this, EditDailyRecord.class);
                intent.putExtra("record", busInfo);
                startActivityForResult(intent, UPDATE_REQUEST_CODE);
                DailyDutyRegisterCards.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onMoreInfoClick(DailyRegisterCardFinal busInfo, int position) {
        // Create and Inflate Custom Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.popup_more_info_register, null);

        // Bind views in the dialog
        TextView date = dialogView.findViewById(R.id.date);
        TextView busNumber = dialogView.findViewById(R.id.vehicleNo);
        TextView routeName = dialogView.findViewById(R.id.routeName);
        TextView actionPerformed = dialogView.findViewById(R.id.actionPerformed);
        TextView driverName = dialogView.findViewById(R.id.driverName);
        TextView conductorName = dialogView.findViewById(R.id.conductorName);
//        TextView startLocation = dialogView.findViewById(R.id.routeStartLocation);
//        TextView endLocation = dialogView.findViewById(R.id.routeEndLocation);
        TextView remarks = dialogView.findViewById(R.id.remarks);
        TextView actualDepartureTime = dialogView.findViewById(R.id.departureTime);
        TextView otherRouteName = dialogView.findViewById(R.id.otherRouteName);
        TableRow otherRouteNameTR = dialogView.findViewById(R.id.otherRouteNameTR);
//        TableRow finalStopNameTR = dialogView.findViewById(R.id.finalStopNameTR);
//        TextView finalStopName = dialogView.findViewById(R.id.finalStopName);
        TableRow relievingDriverNameTR = dialogView.findViewById(R.id.relievingDriverNameTR);
        TableRow relievingConductorNameTR = dialogView.findViewById(R.id.relievingConductorNameTR);
//        TableRow relievingStopDriverNameTR = dialogView.findViewById(R.id.relievingDriverStopNameTR);
//        TableRow relievingStopConductorNameTR = dialogView.findViewById(R.id.relievingConductorStopNameTR);
//        TextView relievingStopDriverName = dialogView.findViewById(R.id.relievingStopDriverName);
//        TextView relievingStopConductorName = dialogView.findViewById(R.id.relievingStopConductorName);

        TextView relievingDriverName = dialogView.findViewById(R.id.relievingDriverName);
        TextView relievingConductorName = dialogView.findViewById(R.id.relievingConductorName);


        date.setText(busInfo.getRecordDate());

        if (busInfo.getActionTaken() != null || !busInfo.getActionTaken().equalsIgnoreCase("none") || !busInfo.getActionTaken().equalsIgnoreCase("null")) {
            actionPerformed.setText(busInfo.getActionTaken());
        } else {
            actionPerformed.setText("N/A");
        }

        // Extend
        if (busInfo.getActionTaken().equalsIgnoreCase("extend") && busInfo.getOtherRoute() != null) {
            otherRouteNameTR.setVisibility(View.VISIBLE);
            otherRouteName.setText(busInfo.getOtherRoute().getRouteName());
        } else {
            otherRouteNameTR.setVisibility(View.GONE);
        }

        // Curtail
        if (busInfo.getActionTaken().equalsIgnoreCase("curtail") && busInfo.getFinalStop() != null) {
            otherRouteNameTR.setVisibility(View.GONE);
            otherRouteName.setText("");
        } else {
            // Setting Curtailed Stop Name
        }


        routeName.setText(busInfo.getOriginalRoute().getRouteName());
        busNumber.setText(busInfo.getAssignedVehicle().getVehicleNumber());
        driverName.setText(busInfo.getMainDriver().getName());
        conductorName.setText(busInfo.getMainConductor().getName());


        if (busInfo.getOtherRoute() != null) {
            otherRouteName.setText(busInfo.getOtherRoute().getRouteName());
        }

        // Relieving Driver + Stop
        if (busInfo.getRelievingDriver() != null) {
            relievingDriverNameTR.setVisibility(View.VISIBLE);
//            relievingStopDriverNameTR.setVisibility(View.VISIBLE);
            relievingDriverName.setText(busInfo.getRelievingDriver().getName());
            if (busInfo.getRelievingDriverStop() != null) {
//                relievingStopDriverName.setText(busInfo.getRelievingDriverStop().getStopName());
            }
        } else {
            relievingDriverNameTR.setVisibility(View.GONE);
//            relievingStopDriverNameTR.setVisibility(View.GONE);
        }

        // Relieving Conductor + Stop
        if (busInfo.getRelievingConductor() != null) {
            relievingConductorNameTR.setVisibility(View.VISIBLE);
//            relievingStopConductorNameTR.setVisibility(View.VISIBLE);
            relievingConductorName.setText(busInfo.getRelievingConductor().getName());
            if (busInfo.getRelievingConductorStop() != null) {
//                relievingStopConductorName.setText(busInfo.getRelievingConductorStop().getStopName());
            }
        } else {
            relievingConductorNameTR.setVisibility(View.GONE);
//            relievingStopConductorNameTR.setVisibility(View.GONE);
        }

        // Remarks
        if (!Econstants.isNotEmpty(busInfo.getRemark())) {
            remarks.setText("N/A");
        } else {
            remarks.setText(busInfo.getRemark());
        }

        actualDepartureTime.setText(busInfo.getActualDepartureTime());

        // Build dialog
        builder.setView(dialogView);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();

        // Set custom height for the dialog
        int heightInDp = 400; // Desired height in dp
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
            Toast.makeText(this, "Data Updated", Toast.LENGTH_SHORT).show();


            // Get All Records Acc Again Normally
            fetchRecords(0, pageSize, dateFilterDate);

            Log.i("DutyRegisterCards", "Data Updated Log Executed");
        }
    }


    @Override
    public void onTaskCompleted(ResponsePojoGet result, TaskType taskType) throws JSONException {

        // Fetching all records
        if (TaskType.GET_ALL_RECORDS == taskType) {
            SuccessResponse response = null;
            Log.i("BusDetails", "Task type is fetching all records..");

            if (result != null) {
                Log.i("Bus Details", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoList = JsonParse.parseDailyDutyRecords(response.getData());

                        if (pojoList.size() > 0) {
                            // Load records Pagination
                            addMoreData(pojoList); // Add More Data on Scroll + Keep the old data
                        }

                        // If Already Existing Recorded added again
                        if (Econstants.isNotEmpty(alreadyAddedRoute)) {
                            Log.i("DutyRegisterCards", "Already Added Route Name: " + alreadyAddedRoute);
                            searchView.setQuery(alreadyAddedRoute, true);
                        }

                    } else {
                        CD.showDialog(DailyDutyRegisterCards.this, "No records found for this date");
                        recyclerView.setVisibility(View.GONE);
                    }

                } else {
                    CD.showDialog(DailyDutyRegisterCards.this, result.getResponse());
                }

            } else {
                CD.showDialog(DailyDutyRegisterCards.this, "Response is null");
            }


        }

        // Search Record
        else if (TaskType.GET_RECORD_SEARCH == taskType) {
            SuccessResponse response = null;

            if (result != null) {
                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());

                    if ("OK".equalsIgnoreCase(response.getStatus())) {
                        pojoList.clear();  // Clear previous results
                        pojoList = JsonParse.parseDailyDutyRecordsFiltered(response.getData());

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

    }


}
