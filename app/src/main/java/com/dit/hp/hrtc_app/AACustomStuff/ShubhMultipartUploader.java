package com.dit.hp.hrtc_app.AACustomStuff;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dit.hp.hrtc_app.enums.TaskType;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ShubhMultipartUploader extends AsyncTask<ShubhUploadObject, Void, ShubhOfflineObject> {

    private final Context context;
    private final ShubhAllAsyncTasksListener listener;
    private final TaskType taskType;
    private ProgressDialog progressDialog;

    public ShubhMultipartUploader(Context context, ShubhAllAsyncTasksListener listener, TaskType taskType) {
        this.context = context;
        this.listener = listener;
        this.taskType = taskType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait while uploading files...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    @Override
    protected ShubhOfflineObject doInBackground(ShubhUploadObject... params) {
        ShubhUploadObject uploadObject = params[0];
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        ShubhOfflineObject offlineObject = new ShubhOfflineObject();
        StringBuilder urlBuilder = new StringBuilder(uploadObject.getUrl());
        urlBuilder.append(uploadObject.getMethodName());
        boolean hasQuery = false;

        if (uploadObject.getStatus() != null) {
            urlBuilder.append(hasQuery ? "&" : "?").append("status=").append(uploadObject.getStatus());
            hasQuery = true;
        }

        if (uploadObject.getMasterName() != null) {
            urlBuilder.append(hasQuery ? "&" : "?").append("masterName=").append(uploadObject.getMasterName());
            hasQuery = true;
        }

        String fullUrl = urlBuilder.toString();
        HttpURLConnection conn = null;

        try {
            logRequest("MULTIPART (POST)", fullUrl, uploadObject.getParam(), uploadObject.getBody());

            URL url = new URL(fullUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream())) {

                if (uploadObject.getBody() != null)
                    addFormField(outputStream, "body", uploadObject.getBody(), boundary);

                if (uploadObject.getSingleImagePath() != null)
                    addFilePart(outputStream, "file", uploadObject.getSingleImagePath(), boundary);

                List<String> imagePaths = uploadObject.getImagePaths();
                if (imagePaths != null && !imagePaths.isEmpty()) {
                    for (int i = 0; i < imagePaths.size(); i++) {
                        addFilePart(outputStream, "files" + i, imagePaths.get(i), boundary);
                    }
                }

                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                outputStream.flush();
            }

            int responseCode = conn.getResponseCode();
            InputStream inputStream = (responseCode == HttpURLConnection.HTTP_OK)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder responseBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();

            String response = responseBuilder.toString();

            logResponse("POST", responseCode, response);

            offlineObject.setUrl(fullUrl);
            offlineObject.setRequestParams(uploadObject.getParam());
            offlineObject.setResponse(response);
            offlineObject.setFunctionName(uploadObject.getMethodName());
            offlineObject.setResponseCode(String.valueOf(responseCode));

        } catch (Exception e) {
            Log.e("Upload Error", e.getMessage(), e);
            offlineObject.setUrl(fullUrl);
            offlineObject.setFunctionName(uploadObject.getMethodName());
            offlineObject.setResponse("Error: " + e.getMessage());
            offlineObject.setResponseCode("500");
        } finally {
            if (conn != null) conn.disconnect();
        }

        return offlineObject;
    }

    @Override
    protected void onPostExecute(ShubhOfflineObject result) {
        super.onPostExecute(result);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        try {
            listener.onTaskCompleted(result, taskType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addFormField(DataOutputStream outputStream, String fieldName, String value, String boundary) throws IOException {
        outputStream.writeBytes("--" + boundary + "\r\n");
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"\r\n\r\n");
        outputStream.writeBytes(value + "\r\n");
    }

    private void addFilePart(DataOutputStream outputStream, String fieldName, String filePath, String boundary) throws IOException {
        File file = new File(filePath);
        String fileName = file.getName();
        String mimeType = getMimeType(filePath);

        outputStream.writeBytes("--" + boundary + "\r\n");
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"\r\n");
        outputStream.writeBytes("Content-Type: " + mimeType + "\r\n\r\n");

        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        outputStream.writeBytes("\r\n");
    }

    private String getMimeType(String filePath) {
        if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) return "image/jpeg";
        if (filePath.endsWith(".png")) return "image/png";
        if (filePath.endsWith(".pdf")) return "application/pdf";
        return "application/octet-stream";
    }



    // Reuse log helpers
    private void logRequest(String type, String url, String params, String body) {
        StringBuilder log = new StringBuilder();
        log.append("Type     : ").append(type).append("\n")
                .append("URL      : ").append(url).append("\n");

        if (body != null)
            log.append("Body     : ").append(body).append("\n");

        Log.e("HTTP REQUEST", log.toString());
    }

    private void logResponse(String type, int code, String response) {
        StringBuilder log = new StringBuilder();
        log.append("Type      : ").append(type).append("\n")
                .append("Status    : ").append(code).append("\n");

        if (response != null)
            log.append("Response  : ").append(response);

        Log.e("HTTP RESPONSE", log.toString());
    }
}
