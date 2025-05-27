package com.dit.hp.hrtc_app.AACustomStuff;

import android.util.Log;

import com.dit.hp.hrtc_app.utilities.Preferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShubhHttpManager {


    // GET
    public ShubhOfflineObject ShubhGetData(ShubhUploadObject data) throws IOException {
        BufferedReader reader = null;
        URL urlObj = null;
        ShubhOfflineObject response = null;
        HttpURLConnection connection = null;

        try {
            // Build URL
            StringBuilder SB = new StringBuilder();
            SB.append(data.getUrl());
            SB.append(data.getMethodName());

            if (data.getStatus() != null)
                SB.append(ShubhEconstants.status).append(data.getStatus());
            if (data.getMasterName() != null)
                SB.append(ShubhEconstants.masterName).append(data.getMasterName());
            if (data.getParentId() != null)
                SB.append(ShubhEconstants.parentId).append(data.getParentId());
            if (data.getSecondaryParentId() != null)
                SB.append(ShubhEconstants.secondaryParentId).append(data.getSecondaryParentId());
            if (data.getParam() != null)
                SB.append(data.getParam());

            urlObj = new URL(SB.toString());

            // Log Request
            logRequest("GET", urlObj.toString(), "", "");

            // Open connection
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
//            connection.setRequestProperty("Authorization", "Bearer " + Preferences.getInstance().token);

            int responseCode = connection.getResponseCode();
            StringBuilder sb = new StringBuilder();

            InputStream inputStream = (responseCode == 200) ?
                    connection.getInputStream() : connection.getErrorStream();

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            // Log Response
            logResponse("GET", responseCode, sb.toString());

            connection.disconnect();

            response = ShubhEconstants.createOfflineObject(
                    data.getUrl(), data.getParam(), sb.toString(), String.valueOf(responseCode), data.getMethodName()
            );

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            int code = (connection != null) ? connection.getResponseCode() : 0;
            response = ShubhEconstants.createOfflineObject(
                    data.getUrl(), data.getParam(), e.getLocalizedMessage(), String.valueOf(code), data.getMethodName()
            );
            return response;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    int code = (connection != null) ? connection.getResponseCode() : 0;
                    response = ShubhEconstants.createOfflineObject(
                            data.getUrl(), data.getParam(), e.getLocalizedMessage(), String.valueOf(code), data.getMethodName()
                    );
                }
            }
        }
    }

    // POST
    public ShubhOfflineObject ShubhPostData(ShubhUploadObject data) throws IOException {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        ShubhOfflineObject response = null;

        try {
            String urlStr = data.getUrl() + data.getMethodName();
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setRequestProperty("Authorization", "Bearer " + Preferences.getInstance().token);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if (data.getBody() != null) {
                conn.getOutputStream().write(data.getBody().getBytes());
            }

            logRequest("POST", urlStr, "", data.getBody());

            int responseCode = conn.getResponseCode();
            InputStream inputStream = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            logResponse("POST", responseCode, sb.toString());

            response = ShubhEconstants.createOfflineObject(
                    data.getUrl(), data.getParam(), sb.toString(), String.valueOf(responseCode), data.getMethodName());

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            int code = (conn != null) ? conn.getResponseCode() : 0;
            response = ShubhEconstants.createOfflineObject(
                    data.getUrl(), data.getParam(), e.getLocalizedMessage(), String.valueOf(code), data.getMethodName());
            return response;
        } finally {
            if (reader != null) reader.close();
            if (conn != null) conn.disconnect();
        }
    }

    // Put
    public ShubhOfflineObject ShubhPutData(ShubhUploadObject data) throws IOException {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        ShubhOfflineObject response = null;

        try {
            String urlStr = data.getUrl() + data.getMethodName();
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setRequestProperty("Authorization", "Bearer " + Preferences.getInstance().token);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if (data.getBody() != null) {
                conn.getOutputStream().write(data.getBody().getBytes());
            }

            logRequest("PUT", urlStr, "", data.getBody());

            int responseCode = conn.getResponseCode();
            InputStream inputStream = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            logResponse("PUT", responseCode, sb.toString());

            response = ShubhEconstants.createOfflineObject(
                    data.getUrl(), data.getParam(), sb.toString(), String.valueOf(responseCode), data.getMethodName());

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            int code = (conn != null) ? conn.getResponseCode() : 0;
            response = ShubhEconstants.createOfflineObject(
                    data.getUrl(), data.getParam(), e.getLocalizedMessage(), String.valueOf(code), data.getMethodName());
            return response;
        } finally {
            if (reader != null) reader.close();
            if (conn != null) conn.disconnect();
        }
    }

    // Delete
    public ShubhOfflineObject ShubhDeleteData(ShubhUploadObject data) throws IOException {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        ShubhOfflineObject response = null;

        try {
            String urlStr = data.getUrl() + data.getMethodName();
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Bearer " + Preferences.getInstance().token);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            logRequest("DELETE", urlStr, "", "");

            int responseCode = conn.getResponseCode();
            InputStream inputStream = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            logResponse("DELETE", responseCode, sb.toString());

            response = ShubhEconstants.createOfflineObject(
                    data.getUrl(), data.getParam(), sb.toString(), String.valueOf(responseCode), data.getMethodName());

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            int code = (conn != null) ? conn.getResponseCode() : 0;
            response = ShubhEconstants.createOfflineObject(
                    data.getUrl(), data.getParam(), e.getLocalizedMessage(), String.valueOf(code), data.getMethodName());
            return response;
        } finally {
            if (reader != null) reader.close();
            if (conn != null) conn.disconnect();
        }
    }







    // Custom Helper Methods
    private void logRequest(String type, String url, String params, String body) {
        StringBuilder sb = new StringBuilder("\n");
        if (type != null) sb.append("Type: ").append(type).append("\n");
        if (url != null) sb.append("URL: ").append(url).append("\n");
        if (params != null) sb.append("Params: ").append(params).append("\n");
        if (body != null) sb.append("Body: ").append(body);
        Log.e("HTTP REQUEST", sb.toString());
    }

    private void logResponse(String type, int code, String response) {
        StringBuilder sb = new StringBuilder("\n");
        if (type != null) sb.append("Type: ").append(type).append("\n");
        sb.append("Status: ").append(code).append("\n");
        if (response != null) sb.append("Response: ").append(response);
        Log.e("HTTP RESPONSE", sb.toString());
    }

}


