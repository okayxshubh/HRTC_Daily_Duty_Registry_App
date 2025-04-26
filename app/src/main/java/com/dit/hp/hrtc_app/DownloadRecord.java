package com.dit.hp.hrtc_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class DownloadRecord extends AppCompatActivity implements ShubhAsyncTaskListenerGet {

    AESCrypto aesCrypto = new AESCrypto();
    String decryptedBase64Excel;
    String decryptedBase64PDF;


    EditText date;
    Button backBtn, downloadExcelBtn, downloadPDFBtn;
    TextView depotNameTV;
    String formattedDate, downloadExcelFileName, downloadPDFFileName;

    CustomDialog CD = new CustomDialog();

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 100;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_record);

        date = findViewById(R.id.date);
        backBtn = findViewById(R.id.backBtn);
        downloadExcelBtn = findViewById(R.id.downloadExcelBtn);
//        downloadPDFBtn = findViewById(R.id.downloadPdfBtn);

        depotNameTV = findViewById(R.id.addNumTV);

        depotNameTV.setText("Depot: " + Preferences.getInstance().depotName);

        date.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            // Show DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(DownloadRecord.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format and display the selected date in the TextView
                        formattedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear);
                        date.setText(formattedDate);
                        downloadExcelFileName = "DailyDutyRegister_Excel_"
                                + Preferences.getInstance().depotId
                                + "_" + selectedDay + (selectedMonth + 1) + selectedYear + ".xlsx";
                        downloadPDFFileName = "DailyDutyRegister_PDF_"
                                + Preferences.getInstance().depotId
                                + "_" + selectedDay + (selectedMonth + 1) + selectedYear + ".pdf";
                    },
                    year,
                    month,
                    day
            );
            // Set the maximum date to the current date
            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });


        downloadExcelBtn.setOnClickListener(v -> {
            if (Econstants.isNotEmpty(date.getText().toString())) {
                checkNotificationPermission();
                checkStoragePermission();

                showExcelDownloadConfirmationDialog();
            } else {
                CD.showDialog(DownloadRecord.this, "Please select a date to download the record.");
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
            DownloadRecord.this.finish();
        });

    }


    // Android 10 and above: No need for WRITE/READ permissions

    // EXCEL
    private void showExcelDownloadConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to download the excel sheet for"
                        + (("\nDepot Name: " + Preferences.getInstance().depotName)
                        + "\nDate: " + date.getText().toString()))

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // Fetch All Records of Current Date Service Call
                        try {
                            if (AppStatus.getInstance(DownloadRecord.this).isOnline()) {

                                UploadObject object = new UploadObject();
                                object.setUrl(Econstants.base_url);
                                object.setMethordName("/api/getData?Tagname=" + URLEncoder.encode(aesCrypto.encrypt("getDutyDetails"), "UTF-8"));

                                String encryptedBody = null;
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("dutyDate", date.getText().toString());
                                jsonObject.put("depot", Preferences.getInstance().depotId);

                                Log.i("JSON Body", "JSON Body: " + jsonObject.toString());

                                // Encrypt the JSON
                                try {
                                    encryptedBody = aesCrypto.encrypt(jsonObject.toString());
                                } catch (Exception e) {
                                    Log.e("Encryption Error", e.getMessage());
                                }

                                object.setParam(encryptedBody);
                                object.setTasktype(TaskType.DOWNLOAD_EXCEL);
                                object.setAPI_NAME(Econstants.API_NAME_HRTC);

                                new ShubhAsyncGet(DownloadRecord.this, DownloadRecord.this, TaskType.DOWNLOAD_EXCEL).execute(object);

                            } else {
                                CD.showDialog(DownloadRecord.this, Econstants.internetNotAvailable);
                            }
                        } catch (Exception e) {
                            CD.showDialog(DownloadRecord.this, "Something Bad happened. Please reinstall the application and try again.");
                        }


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // New Excel Download
    public void createAndDownloadExcel(String data, String fileName) {
        try {
            // Create workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Data");

            // Define the headers
            String[] headers = {
                    "ID", "Route Name",
                    "Start Time", "Start Location",
                    "End Time", "End Location",
                    "Driver Name", "Conductor Name",
                    "Action Taken", "Extended Route", "Curtailed Stoppage Name",
                    "Relieving Driver Name", "Relieving Conductor Name",
                    "Relieving Driver Stoppage", "Relieving Conductor Stoppage"
            };


            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Convert input data to JSON array
            JSONArray jsonArray = new JSONArray(data);

            // Iterate over the data and create rows
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Row row = sheet.createRow(i + 1);

                // Use the optString method with "N/A" as the default value for missing fields
                row.createCell(0).setCellValue(jsonObject.optString("id", "N/A")); // Move ID to the first column
                row.createCell(1).setCellValue(jsonObject.optString("route_name", "N/A"));
                row.createCell(2).setCellValue(jsonObject.optString("start_time", "N/A"));

                if (jsonObject.optString("start_location") == null || (jsonObject.optString("start_location")).equalsIgnoreCase("null")) {
                    row.createCell(3).setCellValue("N/A");
                } else {
                    row.createCell(3).setCellValue(jsonObject.optString("start_location", "N/A"));
                }


                //end_time
                row.createCell(4).setCellValue(jsonObject.optString("end_time", "N/A"));

                // end_location
                if (jsonObject.optString("end_location") == null || (jsonObject.optString("end_location")).equalsIgnoreCase("null")) {
                    row.createCell(5).setCellValue("N/A");
                } else {
                    row.createCell(5).setCellValue(jsonObject.optString("end_location", "N/A"));
                }


                row.createCell(6).setCellValue(jsonObject.optString("driver_name", "N/A"));
                row.createCell(7).setCellValue(jsonObject.optString("conductor_name", "N/A"));



                // Action Taken
                if (jsonObject.optString("action_taken") == null || (jsonObject.optString("action_taken")).equalsIgnoreCase("null")) {
                    row.createCell(8).setCellValue("N/A");
                } else {
                    row.createCell(8).setCellValue(jsonObject.optString("action_taken", "N/A"));
                }

                // Extended Stop Name
                if (jsonObject.optString("extended_route") == null || (jsonObject.optString("extended_route")).equalsIgnoreCase("null")) {
                    row.createCell(9).setCellValue("N/A");
                } else {
                    row.createCell(9).setCellValue(jsonObject.optString("extended_route", "N/A"));
                }

                // Curtailed Stop Name
                if (jsonObject.optString("curtailed_stoppage_name") == null || (jsonObject.optString("curtailed_stoppage_name")).equalsIgnoreCase("null")) {
                    row.createCell(10).setCellValue("N/A");
                } else {
                    row.createCell(10).setCellValue(jsonObject.optString("curtailed_stoppage_name", "N/A"));
                }




                // Relieving Driver
                if (jsonObject.optString("relieving_driver_name") == null || (jsonObject.optString("relieving_driver_name")).equalsIgnoreCase("null")) {
                    row.createCell(11).setCellValue("N/A");
                } else {
                    row.createCell(11).setCellValue(jsonObject.optString("relieving_driver_name", "N/A"));
                }

                // Relieving Conductor
                if (jsonObject.optString("relieving_conductor_name") == null || (jsonObject.optString("relieving_conductor_name")).equalsIgnoreCase("null")) {
                    row.createCell(12).setCellValue("N/A");
                } else {
                    row.createCell(12).setCellValue(jsonObject.optString("relieving_conductor_name", "N/A"));
                }

                // Relieving Driver Stop
                if (jsonObject.optString("relieving_driver_stoppage") == null || (jsonObject.optString("relieving_driver_stoppage")).equalsIgnoreCase("null")) {
                    row.createCell(13).setCellValue("N/A");
                } else {
                    row.createCell(13).setCellValue(jsonObject.optString("relieving_driver_stoppage", "N/A"));
                }

                // Relieving Conductor Stop
                if (jsonObject.optString("relieving_conductor_stoppage") == null || (jsonObject.optString("relieving_conductor_stoppage")).equalsIgnoreCase("null")) {
                    row.createCell(14).setCellValue("N/A");
                } else {
                    row.createCell(14).setCellValue(jsonObject.optString("relieving_conductor_stoppage", "N/A"));
                }
            }


            // Write the workbook to a ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            // Convert to Base64 for download
            String base64Data = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);

            // Call download method
            downloadExcel(base64Data, fileName);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating Excel: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    public void downloadExcel(String base64Data, String fileName) {
        try {
            byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);

            // Use the appropriate content URI for Downloads folder (Scoped Storage handling)
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);  // Display name of the file
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // MIME type
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS); // Location in Downloads

            // Get the content resolver and try to insert the file into MediaStore
            ContentResolver resolver = getContentResolver();
            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            if (uri == null) {
                Toast.makeText(this, "Failed to create file URI.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Open an OutputStream to write the file data
            try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                if (outputStream != null) {
                    outputStream.write(decodedBytes);
                    Toast.makeText(this, "Excel downloaded to Downloads folder.", Toast.LENGTH_SHORT).show();
                    sendDownloadCompleteNotification(uri);
                } else {
                    Toast.makeText(this, "Failed to write file output.", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error writing file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        if (TaskType.DOWNLOAD_EXCEL == taskType) {
            SuccessResponse response = null;
            Log.i("BusDetails", "Task type is downloading excel..");

            if (result != null) {
                Log.i("Bus Details", "Response Obj" + result.toString());

                if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    Log.e("Response", result.getResponse());
                    response = JsonParse.getSuccessResponse(result.getResponse());
                    Log.e("Response", response.toString());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        // Getting base 64 data.. Response.getResponse() is whatever is inside the key: data
                        Log.i("DownloadRecord", "Fetched Base64 Data: " + response.getData());
                        String base64Data = response.getData();

                        try {
                            decryptedBase64Excel = aesCrypto.decrypt(base64Data);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        // DOWNLOAD EXCEL FROM BASE 64 DATA
                        createAndDownloadExcel(decryptedBase64Excel, downloadExcelFileName);

                    } else if (response.getStatus().equalsIgnoreCase("EMPTY")) {
                        Log.i("Records Empty", "No records to download" + response.getMessage());
                        CD.showDownloadStartedDialog(this, "No records for this date are available to download");
                    } else {
                        CD.showDialog(DownloadRecord.this, response.getMessage());
                    }


                } else if (result.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else {
                    CD.showDialog(DownloadRecord.this, "Something went wrong");
                }
            } else {
                CD.showDialog(DownloadRecord.this, "Result from server is null. Check your connection");
            }

        }


    }


}


