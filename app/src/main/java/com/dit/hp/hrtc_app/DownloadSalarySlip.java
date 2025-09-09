package com.dit.hp.hrtc_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerGet;
import com.dit.hp.hrtc_app.json.JsonParse;
import com.dit.hp.hrtc_app.utilities.AppStatus;
import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;

import org.json.JSONException;

import java.net.URLEncoder;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

public class DownloadSalarySlip extends AppCompatActivity implements ShubhAsyncTaskListenerGet {

    AESCrypto aesCrypto = new AESCrypto();
    String decryptedBase64Excel;
    String decryptedBase64PDF;


    EditText date;
    Button backBtn, downloadBtn;
    TextView userNameTV, himaccessIDTV;
    String formattedDate, downloadExcelFileName, downloadPDFFileName;

    CustomDialog CD = new CustomDialog();

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 100;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_salary_slip);

        date = findViewById(R.id.date);
        backBtn = findViewById(R.id.backBtn);
        downloadBtn = findViewById(R.id.downloadBtn);
        himaccessIDTV = findViewById(R.id.himaccessIDTV);

        userNameTV = findViewById(R.id.userNameTV);


        loadPrefDetails();


        date.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    DownloadSalarySlip.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format: MM-YYYY (ignore day)
                        formattedDate = String.format("%02d-%04d", selectedMonth + 1, selectedYear);
                        date.setText(formattedDate);

                        downloadExcelFileName = "DailyDutyRegister_Excel_"
                                + Preferences.getInstance().regionalOfficeId
                                + "_" + (selectedMonth + 1) + selectedYear + ".xlsx";

                        downloadPDFFileName = "DailyDutyRegister_PDF_"
                                + Preferences.getInstance().regionalOfficeId
                                + "_" + (selectedMonth + 1) + selectedYear + ".pdf";
                    },
                    year, month, 1 // dummy day (ignored)
            );

            // Hide day spinner (only month & year)
            try {
                ((ViewGroup) ((ViewGroup) datePickerDialog.getDatePicker()
                        .getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
            } catch (Exception ignored) {
            }

            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });


        downloadBtn.setOnClickListener(v -> {
            if (Econstants.isNotEmpty(date.getText().toString())) {
                checkNotificationPermission();
                checkStoragePermission();

                if (Preferences.getInstance().employeeCode == null || Preferences.getInstance().employeeCode.equalsIgnoreCase("null")){
                    CD.showDialog(this,"Employee Code Not Found");
                    return;
                }

                downloadSalarySlip();

            } else {
                CD.showDialog(DownloadSalarySlip.this, "Please select month and year.");
            }
        });


//        downloadPDFBtn.setOnClickListener(v -> {
//            if (Econstants.isNotEmpty(date.getText().toString())) {
//
//                showPDFDownloadConfirmationDialog();
//            } else {
//                CD.showDialog(DownloadRecord.this, "Please select a date to download the record.");
//            }
//        });

        backBtn.setOnClickListener(v -> {
            DownloadSalarySlip.this.finish();
        });

    }

    private void loadPrefDetails() {
        Preferences.getInstance().loadPreferences(this);
        userNameTV.setText(Preferences.getInstance().userName);
        himaccessIDTV.setText(Preferences.getInstance().emailID);
    }


    // Android 10 and above: No need for WRITE/READ permissions


    // EXCEL
    private void downloadSalarySlip() {
        try {
            if (AppStatus.getInstance(DownloadSalarySlip.this).isOnline()) {
                UploadObject object = new UploadObject();
//                object.setUrl(Econstants.base_url);
                object.setUrl("http://localhost:8081");
                object.setMethordName("/DMS/pdf?");
                object.setParam("empCode=" + URLEncoder.encode(Preferences.getInstance().employeeCode, "UTF-8")
                        + "&date=" + URLEncoder.encode(date.getText().toString(), "UTF-8")
                );

                object.setTasktype(TaskType.DOWNLOAD_SALARY_SLIP);
                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                new ShubhAsyncGet(DownloadSalarySlip.this, DownloadSalarySlip.this, TaskType.DOWNLOAD_SALARY_SLIP).execute(object);

            } else {
                CD.showDialog(DownloadSalarySlip.this, Econstants.internetNotAvailable);
            }
        } catch (Exception e) {
            CD.showDialog(DownloadSalarySlip.this, "Something Bad happened. Please reinstall the application and try again.");
        }
    }

    @SuppressLint("MissingPermission")
    private void sendDownloadCompleteNotification(Uri fileUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "download_channel", "Download Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, "download_channel")
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle("Download Complete")
                .setContentText("Click to open the file")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat.from(this).notify(1, notification);
    }


    // Check and Request Storage Permissions (API 29 and below)
    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            // For Android 10 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Check for API 33+
            // Check if the notification permission is granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request permission if not granted
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    // Handle Permission Results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Storage Permission Granted");
            } else {
                Log.d("Permission", "Storage Permission Denied");
            }
        } else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Notification Permission Granted");
            } else {
                Log.d("Permission", "Notification Permission Denied");
            }
        }
    }

    // Handle Result for MANAGE_EXTERNAL_STORAGE (Android 11+)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Log.d("Permission", "Manage External Storage Permission Granted");
                } else {
                    Log.d("Permission", "Manage External Storage Permission Denied");
                }
            }
        }
    }

    @Override
    public void onTaskCompleted(ResponsePojoGet result, TaskType taskType) throws JSONException {

        // Download Excel
        if (TaskType.DOWNLOAD_SALARY_SLIP == taskType) {
            SuccessResponse response = null;
            if (result != null) {

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        String base64Data = response.getData();

//                        sendDownloadCompleteNotification();

                    } else if (response.getStatus().equalsIgnoreCase("EMPTY")) {
                        CD.showDownloadStartedDialog(this, response.getMessage());
                    } else {
                        CD.showDialog(DownloadSalarySlip.this, response.getMessage());
                    }


                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    Log.e("Download Response", "Download Response: " + result.getResponse());
                    CD.showDialog(DownloadSalarySlip.this, result.getResponse());
                }
            } else {
                CD.showDialog(DownloadSalarySlip.this, "Result from server is null. Check your connection");
            }

        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        loadPrefDetails();
    }

}


