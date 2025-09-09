package com.dit.hp.hrtc_app.AttendanceModule.attendanceAsync;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dit.hp.hrtc_app.AttendanceModule.attendanceInterfaces.AsyncTaskListenerFile;
import com.dit.hp.hrtc_app.AttendanceModule.attendanceModals.AadhaarDoc;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.network.HttpFileUpload;
import com.dit.hp.hrtc_app.utilities.Preferences;

import org.json.JSONException;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Generic_Async_Upload_Attendance extends AsyncTask<UploadObject, String, String> {

    private static final String TAG = "UPLOAD_ATTENDANCE";
    Context context;
    AsyncTaskListenerFile taskListener;
    TaskType taskType;
    private ProgressDialog mProgressDialog;

    public Generic_Async_Upload_Attendance(Context context, AsyncTaskListenerFile taskListener, TaskType taskType) {
        this.context = context;
        this.taskListener = taskListener;
        this.taskType = taskType;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle("Uploading");
        mProgressDialog.setMessage("Uploading Files and Images, Please Wait!");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(UploadObject... mediaFiles) {
        try {
            String token = Preferences.getInstance().token;
            System.out.println("HRTC JWT: " + token);
            if (token == null || token.trim().isEmpty()) {
                Log.e(TAG, "JWT token is null or empty.");
                return "JWT token missing";
            }

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(300, TimeUnit.SECONDS)
                    .writeTimeout(300, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();

            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            // Log JSON param
            Log.d(TAG, "JSON Added as data: " + mediaFiles[0].getParam());
            multipartBuilder.addFormDataPart("data", mediaFiles[0].getParam());

            List<AadhaarDoc> imagePathList = mediaFiles[0].getImagePaths();
            if (imagePathList != null && !imagePathList.isEmpty()) {
                for (AadhaarDoc doc : imagePathList) {
                    File file = new File(doc.getDocPath());
                    Log.d(TAG, "Adding a File as key files: " + file.getPath() +
                            " | Name: " + file.getName() +
                            " | Size: " + file.length());

                    multipartBuilder.addFormDataPart("files", file.getName().trim(),
                            RequestBody.create(MediaType.parse("application/octet-stream"), file));
                }
            } else {
                Log.w(TAG, "No image files found in list.");
            }

            String fullUrl = mediaFiles[0].getUrl() + mediaFiles[0].getMethordName();


            RequestBody body = multipartBuilder.build();
            Request request = new Request.Builder()
                    .url(fullUrl)
                    .post(body)
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            Log.d(TAG, "Request Built: " + request.toString());

            Response response = client.newCall(request).execute();
            String responseStr = response.body().string();

            Log.i(TAG, "Full URL: " + fullUrl);
            Log.i(TAG, "JWT Token: " + token);
            Log.i(TAG, "Response Code: " + response.code());
            Log.i(TAG, "Response Body: " + responseStr);

            return responseStr;

        } catch (Exception e) {
            Log.e(TAG, "Exception during upload: " + e.getMessage(), e);
            return "Exception: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            Log.e(TAG, "Upload Result: " + result);
            taskListener.onTaskCompleted(result, taskType);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException: " + e.getMessage(), e);
        }
        mProgressDialog.dismiss();
    }
}

