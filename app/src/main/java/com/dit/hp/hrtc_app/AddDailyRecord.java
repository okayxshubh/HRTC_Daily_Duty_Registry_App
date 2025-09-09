package com.dit.hp.hrtc_app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dit.hp.hrtc_app.Adapters.ConductorSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.DriverSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.RouteSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.StopSpinnerAdapter;
import com.dit.hp.hrtc_app.Adapters.VehicleSpinnerAdapter;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncPost;
import com.dit.hp.hrtc_app.Modals.DailyRegisterCardFinal;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.RoutePojo;
import com.dit.hp.hrtc_app.Modals.StaffPojo;
import com.dit.hp.hrtc_app.Modals.StopPojo;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Modals.VehiclePojo;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class AddDailyRecord extends AppCompatActivity implements ShubhAsyncTaskListenerGet, ShubhAsyncTaskListenerPost {
    AESCrypto aesCrypto = new AESCrypto();
    Button back, save;
    SearchableSpinner finalStopSpinner, driverSpinner, conductorSpinner, vehicleNumberSpinner, routeSpinner, otherRouteSpinner, relievingDriverSpinner, relievingConductorSpinner, relievingDriverStopSpinner, relievingConductorStopSpinner;

    private List<RoutePojo> assignedRoutes = new ArrayList<>();

    RoutePojo originalRoute, otherRoute;
    StaffPojo mainConductor, relievingConductor;
    StaffPojo mainDriver, relievingDriver;
    VehiclePojo selectedVehicle;
    StopPojo relievingDriverStop, relievingConductorStop;

    List<StopPojo> originalRouteStopsList = null;


    List<String> actions = Arrays.asList("Select Action", "extend", "curtail");
    List<StopPojo> combinedStops = new ArrayList<>();
    StopPojo selectedCurtailedStop;


    TextView dateLabel, routeLabel, otherRouteLabel, stopsLabel, timeLabel, vehicleLabel, driverLabel, conductorLabel;

    // Global Adapters.. Set Values in them in onTaskCompleted()
    private RouteSpinnerAdapter routeSpinnerAdapter;
    private DriverSpinnerAdapter driverSpinnerAdapter;
    private ConductorSpinnerAdapter conductorSpinnerAdapter;
    private VehicleSpinnerAdapter vehicleSpinnerAdapter;
    private StopSpinnerAdapter originalRouteStopsSpinnerAdapter;
    private StopSpinnerAdapter combinedRouteStopsSpinnerAdapter;

    private String departureTime = "";
    private List<RoutePojo> routeList = new ArrayList<>();


    ImageButton clearRelievingDriverSelectionBtn, clearRelievingDriverStopSelectionBtn, clearRelievingConductorSelectionBtn, clearRelievingConductorStopSelectionBtn;

    EditText journeyDate, remarks, actualDepartureTime;
    CustomDialog CD = new CustomDialog();
    Spinner routeActionSpinner;
    LinearLayout otherRouteLayout, routeActionLayout, stopsLinearLayout, relievingDriverLL, relievingConductorLL;
    StopSpinnerAdapter combinedSpinnerAdapter;
    StopSpinnerAdapter curtailedStopSpinnerAdapter;

    // Global Current Date
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    private boolean actionSet = false;

    List<StaffPojo> pojoListDriver;
    List<StaffPojo> pojoListConductor;

    List<StopPojo> originalRouteStops;
    List<StopPojo> extendedRouteStops;
    String encryptedBody;


    // ACCORDIAN
    private LinearLayout accordionHeader, accordionContent;
    private ImageView arrowIcon;
    private boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily_record);

        vehicleNumberSpinner = findViewById(R.id.vehicleNumberSpinner);
        routeActionSpinner = findViewById(R.id.routeActionSpinner);
        driverSpinner = findViewById(R.id.driverSpinner);
        finalStopSpinner = findViewById(R.id.finalStopSpinner);
        otherRouteLayout = findViewById(R.id.otherRouteLL);
        routeActionLayout = findViewById(R.id.routeActionLL);
        stopsLinearLayout = findViewById(R.id.stopsLL);
        relievingDriverLL = findViewById(R.id.relievingDriverLL);
        relievingConductorLL = findViewById(R.id.relievingConductorLL);

        relievingDriverSpinner = findViewById(R.id.relievingDriverSpinner);
        relievingDriverStopSpinner = findViewById(R.id.relievingDriverStopSpinner);
        conductorSpinner = findViewById(R.id.conductorSpinner);
        relievingConductorSpinner = findViewById(R.id.relievingConductorSpinner);
        relievingConductorStopSpinner = findViewById(R.id.relievingConductorStopSpinner);
        routeSpinner = findViewById(R.id.routeSpinner);
        otherRouteSpinner = findViewById(R.id.otherRouteSpinner);

        // Set current date by default
        journeyDate = findViewById(R.id.date);
        journeyDate.setText(String.format("%02d-%02d-%04d", day, month + 1, year));

        clearRelievingDriverSelectionBtn = findViewById(R.id.clearRelievingDriverSelection);
        clearRelievingDriverStopSelectionBtn = findViewById(R.id.clearRelievingDriverStopSelection);
        clearRelievingConductorSelectionBtn = findViewById(R.id.clearRelievingConductorSelection);
        clearRelievingConductorStopSelectionBtn = findViewById(R.id.clearRelievingConductorStopSelection);

        remarks = findViewById(R.id.remarks);

        // LABELS to make * Red
        dateLabel = findViewById(R.id.dateLabel);
        dateLabel.setText(Html.fromHtml("Record Date <font color='#FF0000'>*</font>"));

        routeLabel = findViewById(R.id.routeLabel);
        routeLabel.setText(Html.fromHtml("Select Route <font color='#FF0000'>*</font>"));

        timeLabel = findViewById(R.id.timeLabel);
        timeLabel.setText(Html.fromHtml("Actual <br>Departure Time <font color='#FF0000'>*</font>"));

        driverLabel = findViewById(R.id.driverLabel);
        driverLabel.setText(Html.fromHtml("Driver's Name <font color='#FF0000'>*</font>"));

        conductorLabel = findViewById(R.id.conductorLabel);
        conductorLabel.setText(Html.fromHtml("Conductor's Name <font color='#FF0000'>*</font>"));

        vehicleLabel = findViewById(R.id.vehicleLabel);
        vehicleLabel.setText(Html.fromHtml("Assign Vehicle <font color='#FF0000'>*</font>"));

        otherRouteLabel = findViewById(R.id.otherRouteLabel);
        otherRouteLabel.setText(Html.fromHtml("Other Route <font color='#FF0000'>*</font>"));

        stopsLabel = findViewById(R.id.stopsLabel);
        stopsLabel.setText(Html.fromHtml("Please select the final stop (when curtailed) <font color='#FF0000'>*</font>"));

        actualDepartureTime = findViewById(R.id.time);
        back = findViewById(R.id.backBtn);
        save = findViewById(R.id.proceedBtn);


        // ACCORDION
        accordionHeader = findViewById(R.id.accordionHeader);
        accordionContent = findViewById(R.id.accordionContent);
        arrowIcon = findViewById(R.id.arrowIcon);
        // Set initial state to collapsed
        accordionContent.setVisibility(View.GONE);
        arrowIcon.setRotation(0);
        accordionHeader.setOnClickListener(v -> {
            if (isExpanded) {
                accordionContent.setVisibility(View.GONE);
                arrowIcon.setRotation(0);
            } else {
                accordionContent.setVisibility(View.VISIBLE);
                arrowIcon.setRotation(180);
            }
            isExpanded = !isExpanded;
        });


// #################################################################  SERVICE CALLS  ##############################################################

        // Route Service Call #######################################################
        try {
            if (AppStatus.getInstance(AddDailyRecord.this).isOnline()) {
                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("route"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().regionalOfficeId)), "UTF-8"));
                object.setTasktype(TaskType.GET_ROUTES);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddDailyRecord.this, AddDailyRecord.this, TaskType.GET_ROUTES).execute(object);

            } else {
                CD.showDialog(AddDailyRecord.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddDailyRecord.this, "Something Bad happened . Please reinstall the application and try again.");
        }

        // Vehicles Service Call Acc to Depot #######################################################
        try {
            if (AppStatus.getInstance(AddDailyRecord.this).isOnline()) {

                UploadObject object = new UploadObject();
                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("vehicle"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().regionalOfficeId))));
                object.setTasktype(TaskType.GET_VEHICLES);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddDailyRecord.this, AddDailyRecord.this, TaskType.GET_VEHICLES).execute(object);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddDailyRecord.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddDailyRecord.this, "Something Bad happened . Please reinstall the application and try again.");
        }

        // Get STAFF Service Call #######################################################
        try {
            if (AppStatus.getInstance(AddDailyRecord.this).isOnline()) {
                UploadObject object = new UploadObject();

                object.setUrl(Econstants.base_url);
                object.setMethordName("/master-data?");
                object.setMasterName(URLEncoder.encode(aesCrypto.encrypt("staff"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(Preferences.getInstance().regionalOfficeId)), "UTF-8"));
                object.setTasktype(TaskType.GET_STAFF);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(AddDailyRecord.this, AddDailyRecord.this, TaskType.GET_STAFF).execute(object);

            } else {
                CD.showDialog(AddDailyRecord.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddDailyRecord.this, "Something Bad happened . Please reinstall the application and try again.");
        }

//######################################################################################################################################

        // TIME
        actualDepartureTime.setOnClickListener(v -> {
            if (originalRoute != null && originalRoute.getStartTime() != null) {

                // Get preselected time from assignedRoutes
                String preselectedTime = originalRoute.getStartTime();
                String[] timeParts = preselectedTime.split(":");
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);

                // Show the TimePickerDialog with preselected time
                TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
                    // Compare selected time with preselected time
                    if (selectedHour > hour || (selectedHour == hour && selectedMinute >= minute)) {
                        // Format the selected time
                        departureTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                        actualDepartureTime.setText(departureTime);  // Display selected time
                    } else {
                        CD.showDialog(this, "Actual departure time must be after the allotted time of the route");
                    }
                }, hour, minute, true  // 24-hour format
                );

                timePickerDialog.show();

            } else {
                CD.showDialog(this, "Please select a route first");
            }
        });

        // MAIN ROUTE
        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                originalRoute = (RoutePojo) parent.getItemAtPosition(position);
                assignedRoutes.clear(); // Allow only one route to be selected at a time
                assignedRoutes.add(originalRoute);

                // Reset route Action everytime route is changed
                routeActionSpinner.setSelection(0);

                if (originalRoute != null) {
                    actualDepartureTime.setText(originalRoute.getStartTime());  // Display selected time
                    relievingDriverLL.setVisibility(View.VISIBLE);
                    relievingConductorLL.setVisibility(View.VISIBLE);

                    // Set adapter
                    setupRouteActions();
                    routeActionLayout.setVisibility(View.VISIBLE);
                }

                // SERVICE CALL STOPS OF ORIGINAL ROUTE
                if (originalRoute != null) {
                    makeStopServiceCall(originalRoute.getRouteId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle if nothing is selected
            }
        });

        // ACTION
        routeActionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // ADD EXTEND
                if (routeActionSpinner.getSelectedItem() != null && "extend".equalsIgnoreCase(routeActionSpinner.getSelectedItem().toString())) {
                    stopsLinearLayout.setVisibility(View.GONE);
                    otherRouteLayout.setVisibility(View.VISIBLE);

                    otherRouteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            otherRoute = (RoutePojo) parent.getItemAtPosition(position);

                            if (originalRoute.getRouteId() == otherRoute.getRouteId()) {
                                CD.showDialog(AddDailyRecord.this, "Same route cannot be selected again to extend or combine");
                                otherRouteSpinner.clearSelection();
                                otherRoute = null;
                                return;
                            }

                            makeOtherStopServiceCall(otherRoute.getRouteId());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

                // ADD CURTAIL
                else if (routeActionSpinner.getSelectedItem() != null && "curtail".equalsIgnoreCase(routeActionSpinner.getSelectedItem().toString())) {
                    stopsLinearLayout.setVisibility(View.VISIBLE);
                    otherRouteLayout.setVisibility(View.GONE);
                    otherRouteSpinner.clearSelection();
                    otherRoute = null;

                    List<StopPojo> curtailedStopsList = new ArrayList<>(originalRouteStopsList); // Start with all stops
                    // Populate stops when curtailing

                    curtailedStopSpinnerAdapter = new StopSpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, curtailedStopsList);
                    finalStopSpinner.setAdapter(originalRouteStopsSpinnerAdapter);
                    relievingDriverStopSpinner.setAdapter(curtailedStopSpinnerAdapter);
                    relievingConductorStopSpinner.setAdapter(curtailedStopSpinnerAdapter);

                    // Filter stops up to the selected curtailed stop
                    finalStopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedCurtailedStop = (StopPojo) parent.getItemAtPosition(position);

                            // Find index of selected stop in the original list
                            int finalStopIndex = originalRouteStopsList.indexOf(selectedCurtailedStop);
                            if (finalStopIndex != -1) {
                                // Update curtailedStopsList with stops up to the selected stop
                                curtailedStopsList.clear();
                                curtailedStopsList.addAll(originalRouteStopsList.subList(0, finalStopIndex + 1));
                                curtailedStopSpinnerAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            otherRoute = null;
                            otherRouteSpinner.clearSelection();

                        }
                    });

                }

                // DEFAULT Case when No Route Action is Taken
                else {
                    stopsLinearLayout.setVisibility(View.GONE);
                    otherRouteLayout.setVisibility(View.GONE);
                    otherRouteSpinner.clearSelection();

                    // Set back to original if nothing selected.
                    if (originalRouteStopsSpinnerAdapter != null) {
                        relievingDriverStopSpinner.setAdapter(originalRouteStopsSpinnerAdapter);
                        relievingConductorStopSpinner.setAdapter(originalRouteStopsSpinnerAdapter);
                    }


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                otherRouteLayout.setVisibility(View.GONE);
                stopsLinearLayout.setVisibility(View.GONE);
            }
        });

        // Main Driver
        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mainDriver = (StaffPojo) parent.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Main Conductor
        conductorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mainConductor = (StaffPojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Relieving Driver
        relievingDriverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                relievingDriver = (StaffPojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                relievingDriver = null;
            }
        });

        relievingDriverStopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                relievingDriverStop = (StopPojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                relievingDriverStop = null;
            }
        });

        // Relieving Conductor
        relievingConductorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                relievingConductor = (StaffPojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                relievingConductor = null;
            }
        });

        relievingConductorStopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                relievingConductorStop = (StopPojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                relievingConductorStop = null;
            }
        });


        // Vehicle
        vehicleNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVehicle = (VehiclePojo) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Date TextView
        journeyDate.setOnClickListener(v -> {
            // Show DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddDailyRecord.this, (view, selectedYear, selectedMonth, selectedDay) -> {
                // Format and display the selected date in the TextView
                String formattedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear);
                journeyDate.setText(formattedDate);

            }, year, month, day);

            // Get the current date
            Calendar calendar = Calendar.getInstance();
            long today = calendar.getTimeInMillis();

            // Set maximum date to tomorrow
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            long tomorrow = calendar.getTimeInMillis();

            // Set min and max dates
            datePickerDialog.getDatePicker().setMinDate(today);
            datePickerDialog.getDatePicker().setMaxDate(tomorrow);

            datePickerDialog.show();
        });

        // Delete Btn
        clearRelievingDriverSelectionBtn.setOnClickListener(v -> {
            relievingDriverSpinner.clearSelection();
            relievingDriverStopSpinner.clearSelection();
            relievingDriver = null;
            relievingDriverStop = null;
        });

        // Delete Btn
        clearRelievingDriverStopSelectionBtn.setOnClickListener(v -> {
            relievingDriverStopSpinner.clearSelection();
            relievingDriverStop = null;
        });

        // Delete Btn
        clearRelievingConductorSelectionBtn.setOnClickListener(v -> {
            relievingConductorSpinner.clearSelection();
            relievingConductorStopSpinner.clearSelection();
            relievingConductor = null;
            relievingConductorStop = null;
        });

        // Delete Btn
        clearRelievingConductorStopSelectionBtn.setOnClickListener(v -> {
            relievingConductorStopSpinner.clearSelection();
            relievingConductorStop = null;
        });


        // Back Btn
        back.setOnClickListener(v -> showExitConfirmationDialog());


        // ################################################################## SAVE BTN  ##################################################################
        save.setOnClickListener(v -> {

            // Check if at least one route is assigned
            if (originalRoute == null) {
                CD.showDialog(this, "Please select at least one route!");
                return;
            }


            // Validation
            else if (routeActionSpinner.getSelectedItem().toString().equalsIgnoreCase("extend")) {
                if (otherRouteSpinner.getSelectedItem() == null) {
                    CD.showDialog(this, "Please select another route to extend");
                    return;
                }
            }

            // Validation for Action Selected + Item selected
            if (routeActionSpinner.getSelectedItem().toString().equalsIgnoreCase("curtail")) {
                if (finalStopSpinner.getSelectedItem() == null) {
                    CD.showDialog(this, "Please select a stop to curtail the route.");
                    return;
                }
            }

            // Validation if a vehicle number is selected
            if (vehicleNumberSpinner.getSelectedItem() == null || vehicleNumberSpinner.getSelectedItem().toString().isEmpty()) {
                CD.showDialog(this, "Please select a vehicle number!");
                return;
            }

            // Check if a driver is selected
            if (driverSpinner.getSelectedItem() == null) {
                CD.showDialog(this, "Please select a driver!");
                return;
            }

            // Check if a conductor is selected
            if (conductorSpinner.getSelectedItem() == null) {
                CD.showDialog(this, "Please select a conductor!");
                return;
            }

            // Check if a relieving driver is selected but no stop is selected
            if (relievingDriverSpinner.getSelectedItem() != null && relievingDriverStopSpinner.getSelectedItem() == null) {
                CD.showDialog(this, "Please select a relieving driver's stop!");
                return;
            }

            // Check if a relieving driver stop is selected but no relieving driver is selected
            if (relievingDriverStopSpinner.getSelectedItem() != null && relievingDriverSpinner.getSelectedItem() == null) {
                CD.showDialog(this, "Please select a relieving driver!");
                return;
            }

            // Check if a relieving conductor is selected but no stop is selected
            if (relievingConductorSpinner.getSelectedItem() != null && relievingConductorStopSpinner.getSelectedItem() == null) {
                CD.showDialog(this, "Please select a relieving conductor's stop!");
                return;
            }

            // Check if a relieving conductor stop is selected but no relieving conductor is selected
            if (relievingConductorStopSpinner.getSelectedItem() != null && relievingConductorSpinner.getSelectedItem() == null) {
                CD.showDialog(this, "Please select a relieving conductor!");
                return;
            }


            // Ensure main driver and relieving driver have different IDs
            if (driverSpinner.getSelectedItem() != null && relievingDriverSpinner.getSelectedItem() != null) {
                int mainDriverId = driverSpinner.getSelectedItemPosition();
                int relievingDriverId = relievingDriverSpinner.getSelectedItemPosition();

                if (mainDriverId == relievingDriverId) {
                    CD.showDialog(this, "Main driver and relieving driver must not be same");
                    return;
                }
            }

            // Ensure main conductor and relieving conductor have different IDs
            if (conductorSpinner.getSelectedItem() != null && relievingConductorSpinner.getSelectedItem() != null) {
                int mainConductorId = conductorSpinner.getSelectedItemPosition();
                int relievingConductorId = relievingConductorSpinner.getSelectedItemPosition();

                if (mainConductorId == relievingConductorId) {
                    CD.showDialog(this, "Main conductor and relieving conductor must not be same");
                    return;
                }
            }

//            #################################################################### SENDING DATA ####################################################################################

            // Creating JSON to store in param
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("dutyDate", journeyDate.getText().toString());
                jsonObject.put("route", originalRoute.getRouteId());


                if (routeActionSpinner.getSelectedItem().toString().equalsIgnoreCase("Select Action")) {
                    jsonObject.put("actionTaken", "N/A");
                } else {
                    jsonObject.put("actionTaken", routeActionSpinner.getSelectedItem().toString());
                }

                // Curtail
                if (routeActionSpinner.getSelectedItem().toString().equalsIgnoreCase("curtail")) {
                    jsonObject.put("curtailedStoppage", selectedCurtailedStop.getStopId());
                } else {
                    jsonObject.put("curtailedStoppage", JSONObject.NULL);
                }

                // Extend
                if (routeActionSpinner.getSelectedItem().toString().equalsIgnoreCase("extend")) {
                    jsonObject.put("extendedRoute", otherRoute.getRouteId());
                } else {
                    jsonObject.put("extendedRoute", JSONObject.NULL);
                }

                // NO Route Action
                if (routeActionSpinner.getSelectedItem().toString().equalsIgnoreCase("Select Action")) {
                    jsonObject.put("curtailedStoppage", JSONObject.NULL);
                    jsonObject.put("extendedRoute", JSONObject.NULL);
                }

                jsonObject.put("actualDepartureTime", actualDepartureTime.getText().toString());
                jsonObject.put("driver", mainDriver.getId());
                jsonObject.put("conductor", mainConductor.getId());
                jsonObject.put("vehicle", selectedVehicle.getId());

                // Relieving Driver
                if (relievingDriver != null) {
                    jsonObject.put("relievingDriver", relievingDriver.getId());
                    jsonObject.put("relievingDriverStoppage", relievingDriverStop.getStopId());
                } else {
                    jsonObject.put("relievingDriver", JSONObject.NULL);
                    jsonObject.put("relievingDriverStoppage", JSONObject.NULL);
                }

                // Relieving Conductor
                if (relievingConductor != null) {
                    jsonObject.put("relievingConductor", relievingConductor.getId());
                    jsonObject.put("relievingConductorStoppage", relievingConductorStop.getStopId());
                } else {
                    jsonObject.put("relievingConductor", JSONObject.NULL);
                    jsonObject.put("relievingConductorStoppage", JSONObject.NULL);
                }

                jsonObject.put("remarks", remarks.getText().toString());
                jsonObject.put("createdBy", Preferences.getInstance().emailID);

            } catch (JSONException e) {
                Log.e("JSON Exception", e.getMessage());
            }
            Log.i("JSON for Upload", "JSON For Upload: " + jsonObject.toString());

            // Encrypt the JSON
            try {
                encryptedBody = aesCrypto.encrypt(jsonObject.toString());
            } catch (Exception e) {
                Log.e("Encryption Error", e.getMessage());
            }


            UploadObject uploadObject = new UploadObject();
            try {
                uploadObject.setUrl(Econstants.base_url);
                uploadObject.setMethordName("/master-data?masterName=");
                uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("dailyDuty"), "UTF-8"));
                uploadObject.setTasktype(TaskType.SAVE_RECORD);
                uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);
            } catch (Exception e) {
                e.printStackTrace();
            }
            uploadObject.setParam(encryptedBody);

            Log.i("JSON Params", "Enc Params" + encryptedBody);
            Log.i("Object", "Complete Object: " + uploadObject.toString());

            new ShubhAsyncPost(AddDailyRecord.this, AddDailyRecord.this, TaskType.SAVE_RECORD).execute(uploadObject);


        });

    }


    private void makeStopServiceCall(int routeId) {
        // Fetch Stops of the selected route service call
        try {
            if (AppStatus.getInstance(AddDailyRecord.this).isOnline()) {

                UploadObject uploadObject = new UploadObject();
                uploadObject.setUrl(Econstants.base_url);
                uploadObject.setMethordName("/master-data?");
                uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("routeStoppage"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(routeId)), "UTF-8"));
                uploadObject.setTasktype(TaskType.GET_STOPS);
                uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);
                Log.e("URL Log", "URL LOG: " + uploadObject.getUrl() + uploadObject.getMethordName() + uploadObject.getMasterName());

                new ShubhAsyncGet(AddDailyRecord.this, AddDailyRecord.this, TaskType.GET_STOPS).execute(uploadObject);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddDailyRecord.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddDailyRecord.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }

    private void makeOtherStopServiceCall(int routeId) {
        // Fetch Stops of the selected route service call
        try {
            if (AppStatus.getInstance(AddDailyRecord.this).isOnline()) {

                UploadObject uploadObject = new UploadObject();
                uploadObject.setUrl(Econstants.base_url);
                uploadObject.setMethordName("/master-data?");
                uploadObject.setMasterName(URLEncoder.encode(aesCrypto.encrypt("routeStoppage"), "UTF-8")
                        + "&parentId=" + URLEncoder.encode(aesCrypto.encrypt(String.valueOf(routeId)), "UTF-8"));
                uploadObject.setTasktype(TaskType.GET_OTHER_STOPS);
                uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);
                Log.e("URL Log", "URL LOG: " + uploadObject.getUrl() + uploadObject.getMethordName() + uploadObject.getMasterName());

                new ShubhAsyncGet(AddDailyRecord.this, AddDailyRecord.this, TaskType.GET_OTHER_STOPS).execute(uploadObject);

            } else {
                // Do nothing if CD already shown once
                CD.showDialog(AddDailyRecord.this, Econstants.internetNotAvailable);
            }
        } catch (Exception ex) {
            CD.showDialog(AddDailyRecord.this, "Something Bad happened . Please reinstall the application and try again.");
        }
    }

    private void setupRouteActions() {

        // Populate spinner with actions
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, actions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeActionSpinner.setAdapter(adapter);

    }

    // Exit confirmation dialog
    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to go back? Any progress made will be lost.").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AddDailyRecord.this.finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog, do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

    @Override
    public void onTaskCompleted(ResponsePojoGet result, TaskType taskType) throws JSONException {

        // Get Routes from Depot
        if (TaskType.GET_ROUTES == taskType) {
            SuccessResponse response = null;
            List<RoutePojo> pojoList = null;
            Log.i("AddBusDetails", "Task type is fetching routes..");

            if (result != null) {
                Log.i("AddBusDetails", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoList = JsonParse.parseRoutes(response.getData());
                        Log.i("pojoList", pojoList.toString());

                        if (pojoList.size() > 0) {
                            Log.e("Markers Size", String.valueOf(pojoList.size()));
                            Log.e("Reports Data", pojoList.toString());

                            routeSpinnerAdapter = new RouteSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            routeSpinner.setAdapter(routeSpinnerAdapter);
                            otherRouteSpinner.setAdapter(routeSpinnerAdapter);

                        } else {
                            CD.showDialog(AddDailyRecord.this, "No Routes Found");
                        }

                    } else {
                        CD.showDialog(AddDailyRecord.this, response.getMessage());
                    }
                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(AddDailyRecord.this, "Not able to connect to the server");
                }
            } else {
//                CD.showDialog(AddBusDetails.this, "Result is null");
            }
        }

        // GET STOPS ACC TO ROUTE
        else if (TaskType.GET_STOPS == taskType) {
            SuccessResponse response = null;
            Log.i("BusDetails", "Task type is fetching route stops..");

            if (result != null) {
                Log.i("Bus Details", "Response Obj: " + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response Stops: ", response.toString());
                    Log.e("Response Stops: ", result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        originalRouteStopsList = JsonParse.parseStops(response.getData());
                        Log.i("pojoList", originalRouteStopsList.toString());

                        if (originalRouteStopsList != null && !originalRouteStopsList.isEmpty()) {
                            Log.e("Markers Size", String.valueOf(originalRouteStopsList.size()));
                            Log.e("Reports Data", originalRouteStopsList.toString());

                            originalRouteStopsSpinnerAdapter = new StopSpinnerAdapter(this, android.R.layout.simple_spinner_item, originalRouteStopsList);
                            relievingDriverStopSpinner.setAdapter(originalRouteStopsSpinnerAdapter);
                            relievingConductorStopSpinner.setAdapter(originalRouteStopsSpinnerAdapter);


                        } else {
//                            CD.showDialog(AddDailyRecord.this, "No Stops Found");
                        }
                    } else {
                        CD.showDialog(AddDailyRecord.this, response.getMessage());
                    }
                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(AddDailyRecord.this, "Not able to fetch data");
                }
            }
        }

        // GET OTHER STOPS
        else if (TaskType.GET_OTHER_STOPS == taskType) {
            SuccessResponse response = null;
            List<StopPojo> otherRouteStopsList = null;
            List<StopPojo> combinedRoutesStopsList = new ArrayList<>();
            Log.i("BusDetails", "Task type is fetching route stops..");

            if (result != null) {
                Log.i("Bus Details", "Response Obj: " + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response Stops: ", response.toString());
                    Log.e("Response Stops: ", result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        otherRouteStopsList = JsonParse.parseStops(response.getData());
                        Log.i("pojoList", otherRouteStopsList.toString());

                        if (otherRouteStopsList != null && !otherRouteStopsList.isEmpty()) {
                            Log.e("Markers Size", String.valueOf(otherRouteStopsList.size()));
                            Log.e("Reports Data", otherRouteStopsList.toString());

                            // Add Original route stops
                            if (originalRouteStopsList != null && !originalRouteStopsList.isEmpty()) {
                                combinedRoutesStopsList.addAll(originalRouteStopsList);
                            }
                            // Add Other Route Stops
                            if (otherRouteStopsList != null && !otherRouteStopsList.isEmpty()) {
                                combinedRoutesStopsList.addAll(otherRouteStopsList);
                            }

                            // Setting Combined Route List to Relieving Staff Stops
                            if (combinedRoutesStopsList != null && !combinedRoutesStopsList.isEmpty()) {
                                combinedRouteStopsSpinnerAdapter = new StopSpinnerAdapter(this, android.R.layout.simple_spinner_item, combinedRoutesStopsList);
                                relievingDriverStopSpinner.setAdapter(combinedRouteStopsSpinnerAdapter);
                                relievingConductorStopSpinner.setAdapter(combinedRouteStopsSpinnerAdapter);
                            }


                        } else {
//                            CD.showDialog(AddDailyRecord.this, "No Stops Found");
                        }
                    } else {
                        CD.showDialog(AddDailyRecord.this, response.getMessage());
                    }
                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(AddDailyRecord.this, "Not able to fetch data");
                }
            }
        }

        // SAVE RECORD
        else if (TaskType.SAVE_RECORD == taskType) {
            Log.i("AddBusDetails", "Task type is saving record..");

            if (result != null) {
                SuccessResponse successResponse = null;
                successResponse = JsonParse.getSuccessResponse(result.getResponse());

                // Status from response matches 200
                if (successResponse.getStatus().equalsIgnoreCase("OK")) {
                    Log.i("Add Bus Response", successResponse.getData());

                    // Success
                    CD.showRecordAddedCompleteDialog(this, successResponse.getMessage());

                } else if (successResponse.getStatus().equalsIgnoreCase("FAILED")) {
                    // Record Already Exists Check
                    CD.showAlreadyExistRecordDialog(this, successResponse.getMessage(), originalRoute.getRouteName());
                }

                else {
                    Log.i("AddBusDetails", "Response is null");
                    CD.showDialog(AddDailyRecord.this, successResponse.getMessage());
                }

            } else {
                CD.showDismissActivityDialog(this, "Result is null");
            }
        }

        // Get STAFF (Conductors and drivers) After Selecting Depot
        else if (TaskType.GET_STAFF == taskType) {
            SuccessResponse response = null;
            pojoListDriver = new ArrayList<>();
            pojoListConductor = new ArrayList<>();
            Log.i("AddBusDetails", "Task type is fetching drivers..");

            if (result != null) {
                Log.i("StaffDetails", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        JsonParse.parseDecryptedStaffList(response.getData(), pojoListDriver, pojoListConductor);

                        // DRIVERS
                        if (pojoListDriver.size() > 0) {
                            Log.e("Reports Data Driver", pojoListDriver.toString());
                            Log.e("Reports Data Conductor", pojoListConductor.toString());

                            driverSpinnerAdapter = new DriverSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoListDriver);
                            driverSpinner.setAdapter(driverSpinnerAdapter);
                            relievingDriverSpinner.setAdapter(driverSpinnerAdapter);

                        } else {
//                            CD.showDialog(AddDailyRecord.this, "No Drivers Found");
                            pojoListDriver.clear();
                        }

                        // CONDUCTORS
                        if (pojoListConductor.size() > 0) {
                            Log.e("Markers Size", String.valueOf(pojoListDriver.size() + pojoListConductor.size()));
                            conductorSpinnerAdapter = new ConductorSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoListConductor);
                            conductorSpinner.setAdapter(conductorSpinnerAdapter);
                            relievingConductorSpinner.setAdapter(conductorSpinnerAdapter);
                        } else {
//                            CD.showDialog(AddDailyRecord.this, "No Conductors Found");
                        }

                    } else {
                        CD.showDialog(AddDailyRecord.this, response.getMessage());
                    }
                } else {
                    CD.showDialog(AddDailyRecord.this, "Not able to fetch data");
                }
            } else {
//                CD.showDialog(AddBusDetails.this, "Result is null");
            }
        }

        // Vehicles
        else if (TaskType.GET_VEHICLES == taskType) {
            SuccessResponse response = null;
            List<VehiclePojo> pojoList = null;
            Log.i("BusDetails", "Task type is fetching vehicles..");

            if (result != null) {
                Log.i("Bus Details", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", result.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {

                        pojoList = JsonParse.parseVehicleList(response.getData());

                        Log.i("pojoList", pojoList.toString());

                        if (pojoList.size() > 0) {
                            Log.e("Markers Size", String.valueOf(pojoList.size()));
                            Log.e("Reports Data", pojoList.toString());

                            vehicleSpinnerAdapter = new VehicleSpinnerAdapter(this, android.R.layout.simple_spinner_item, pojoList);
                            vehicleNumberSpinner.setAdapter(vehicleSpinnerAdapter);

                        } else {
//                            CD.showDialog(AddDailyRecord.this, "No Vehicles Found");
                        }
                    } else {
                        CD.showDialog(AddDailyRecord.this, response.getMessage());
                    }
                } else {
                    CD.showDialog(AddDailyRecord.this, "Not able to fetch data");
                }
            } else {
//                CD.showDialog(AddBusDetails.this, "Result is null");
            }
        }
    }
}