package com.dit.hp.hrtc_app.network;


import android.util.Log;

import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.FaceAuthObjectRequest;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.FaceAuthObjectResponse;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;

import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class HttpManager {

//    CryptographyAES AES = new CryptographyAES();
//
//    EncryptDecrypt ED;
//
//    public HttpManager() throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException {
//        ED = new EncryptDecrypt();
//    }

    // FOR Login + SSL + No JWT + ID Pass in Param
    // New fix that handle both http and https connection.. No SSL issue occur
    public ResponsePojoGet PostDataNewFixWithoutJWT(UploadObject data) {
        HttpURLConnection conn_ = null;
        StringBuilder sb;
        ResponsePojoGet response = null;

        try {
            // Append encrypted params to URL
            String finalURL = data.getUrl() + data.getMethordName() + data.getParam();
            Log.e("URL Formed", "URL FORMED: " + finalURL);

            URL url_ = new URL(finalURL);

            // Open connection
            if (finalURL.startsWith("https")) {
                HttpsURLConnection httpsConn = (HttpsURLConnection) url_.openConnection();
                httpsConn.setSSLSocketFactory(NetworkUtils.getSSLSocketFactory());
                conn_ = httpsConn;
            } else {
                conn_ = (HttpURLConnection) url_.openConnection();
            }

            // Setup GET/POST-style behavior
            conn_.setDoOutput(false); // Not writing any body
            conn_.setRequestMethod("POST");
            conn_.setUseCaches(false);
            conn_.setConnectTimeout(10000);
            conn_.setReadTimeout(10000);
            conn_.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // No body write (params already in URL)

            int httpResult = conn_.getResponseCode();
            InputStream inputStream = (httpResult == HttpURLConnection.HTTP_OK)
                    ? conn_.getInputStream()
                    : conn_.getErrorStream();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
                sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }

            Log.e("Data from Service", sb.toString());
            response = Econstants.createOfflineObject(finalURL, data.getParam(), sb.toString(), String.valueOf(httpResult), data.getMethordName());

        } catch (Exception e) {
            Log.e("Error", "Exception: " + e.getMessage());
            if (conn_ != null) {
                try {
                    response = Econstants.createOfflineObject(data.getUrl(), data.getParam(), conn_.getResponseMessage(), String.valueOf(conn_.getResponseCode()), data.getMethordName());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        } finally {
            if (conn_ != null) conn_.disconnect();
        }

        return response;
    }

    public ResponsePojoGet GetDataWithParamsWithoutJWT(UploadObject data) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        ResponsePojoGet response = null;

        try {
            // Build full URL with params
            StringBuilder fullURL = new StringBuilder();
            fullURL.append(data.getUrl());
            fullURL.append(data.getMethordName());

            // HRTC-specific params
            if (Econstants.API_NAME_HRTC.equalsIgnoreCase(data.getAPI_NAME())) {
                if (data.getStatus() != null) fullURL.append(Econstants.status).append(data.getStatus());
                if (data.getMasterName() != null) fullURL.append(Econstants.masterName).append(data.getMasterName());
                if (data.getParentId() != null) fullURL.append(Econstants.parentId).append(data.getParentId());
                if (data.getSecondaryParentId() != null) fullURL.append(Econstants.secondaryParentId).append(data.getSecondaryParentId());
            }

            // Additional param string (already URL encoded)
            if (data.getParam() != null) {
                fullURL.append(data.getParam());
            }

            // Log formed URL
            String finalURL = fullURL.toString();
            Log.e("URL Formed", finalURL);

            // Open connection
            URL urlObj = new URL(finalURL);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Read response
            InputStream inputStream = (connection.getResponseCode() == 200)
                    ? connection.getInputStream()
                    : connection.getErrorStream();

            reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            Log.e("Response", sb.toString());

            // Create response object
            response = Econstants.createOfflineObject(
                    finalURL,
                    data.getParam(),
                    sb.toString(),
                    String.valueOf(connection.getResponseCode()),
                    data.getMethordName()
            );

        } catch (Exception e) {
            Log.e("Exception", e.getMessage(), e);
            try {
                response = Econstants.createOfflineObject(
                        data.getUrl(),
                        data.getParam(),
                        e.getMessage(),
                        (connection != null) ? String.valueOf(connection.getResponseCode()) : "0",
                        data.getMethordName()
                );
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }

    // FOR HRTC JTWT TOKEN
    public ResponsePojoGet PostDataWithParamsWithoutJWT(UploadObject data) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        ResponsePojoGet response = null;

        try {
            // Build full URL
            StringBuilder fullURL = new StringBuilder();
            fullURL.append(data.getUrl());         // Base URL
            fullURL.append(data.getMethordName()); // Endpoint with '?'

            // Append parameters if available
            String param = data.getParam();
            if (param != null && !param.isEmpty()) {
                fullURL.append(param.startsWith("&") ? param.substring(1) : param);
            }

            // Final URL string
            String finalURL = fullURL.toString();
            Log.e("URL Formed", finalURL);

            // Open connection
            URL urlObj = new URL(finalURL);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Read response
            InputStream inputStream = (connection.getResponseCode() == 200)
                    ? connection.getInputStream()
                    : connection.getErrorStream();

            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            Log.e("Response", sb.toString());

            // Create response object
            response = Econstants.createOfflineObject(
                    finalURL,
                    data.getParam(),
                    sb.toString(),
                    String.valueOf(connection.getResponseCode()),
                    data.getMethordName()
            );

        } catch (Exception e) {
            Log.e("Exception", e.getMessage(), e);
            try {
                response = Econstants.createOfflineObject(
                        data.getUrl(),
                        data.getParam(),
                        e.getMessage(),
                        (connection != null) ? String.valueOf(connection.getResponseCode()) : "0",
                        data.getMethordName()
                );
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null) connection.disconnect();
        }

        return response;
    }


    // Without JWT Token
    public ResponsePojoGet GetDataWithoutJWT(UploadObject data) throws IOException {
        BufferedReader reader = null;
        URL urlObj = null;
        ResponsePojoGet response = null;
        HttpURLConnection connection = null;

        try {
            if (data.getAPI_NAME() != null && data.getAPI_NAME().equalsIgnoreCase(Econstants.API_NAME_HRTC)) {
                StringBuilder SB = new StringBuilder();

                SB.append(data.getUrl());
                SB.append(data.getMethordName());

                if (data.getStatus() != null) {
                    SB.append(Econstants.status);
                    SB.append(data.getStatus());
                }
                if (data.getMasterName() != null) {
                    SB.append(Econstants.masterName);
                    SB.append(data.getMasterName());
                }

                if (data.getParentId() != null) {
                    SB.append(Econstants.parentId);
                    SB.append(data.getParentId());
                }

                if (data.getSecondaryParentId() != null) {
                    SB.append(Econstants.secondaryParentId);
                    SB.append(data.getSecondaryParentId());
                }

                if (data.getParam() != null) {
                    SB.append(data.getParam());
                }

                urlObj = new URL(SB.toString());
                Log.e("URL Formed", "URL FORMED: " + urlObj.toString());

            } else {
                if (data.getParam() == null || data.getParam().isEmpty()) {
                    urlObj = new URL(data.getUrl() + data.getMethordName());
                } else {
                    urlObj = new URL(data.getUrl() + data.getMethordName() + data.getParam());
                }
            }

            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            if (connection.getResponseCode() != 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                connection.disconnect();
                Log.e("String=== ", sb.toString());
                Log.e("D Sring=== ", sb.toString());
                response = Econstants.createOfflineObject(data.getUrl(), data.getParam(), sb.toString(), Integer.toString(connection.getResponseCode()), data.getMethordName());

                return response;
            } else {

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                connection.disconnect();
                Log.e("String=== ", sb.toString());
                response = Econstants.createOfflineObject(data.getUrl(), data.getParam(), sb.toString(), Integer.toString(connection.getResponseCode()), data.getMethordName());
                return response;
            }

        } catch (Exception e) {
            e.printStackTrace();
            response = Econstants.createOfflineObject(data.getUrl(), data.getParam(), e.getLocalizedMessage(), Integer.toString(connection.getResponseCode()), data.getMethordName());

            return response;
        } finally {
            if (reader != null) {
                try {
                    reader.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    response = Econstants.createOfflineObject(data.getUrl(), data.getParam(), e.getLocalizedMessage(), Integer.toString(connection.getResponseCode()), data.getMethordName());
                    return response;
                }
            }
        }
    }

    // GET Data with JWT + Master + Param in URL
    public ResponsePojoGet GetData(UploadObject data) throws IOException {
        BufferedReader reader = null;
        URL urlObj = null;
        ResponsePojoGet response = null;
        HttpURLConnection connection = null;

        try {
            if (data.getAPI_NAME() != null && data.getAPI_NAME().equalsIgnoreCase(Econstants.API_NAME_HRTC)) {
                StringBuilder SB = new StringBuilder();

                SB.append(data.getUrl());
                SB.append(data.getMethordName());

                if (data.getStatus() != null) {
                    SB.append(Econstants.status);
                    SB.append(data.getStatus());
                }
                if (data.getMasterName() != null) {
                    SB.append(Econstants.masterName);
                    SB.append(data.getMasterName());
                }

                if (data.getParentId() != null) {
                    SB.append(Econstants.parentId);
                    SB.append(data.getParentId());
                }

                if (data.getSecondaryParentId() != null) {
                    SB.append(Econstants.secondaryParentId);
                    SB.append(data.getSecondaryParentId());
                }

                if (data.getParam() != null) {
                    SB.append(data.getParam());
                }

                urlObj = new URL(SB.toString());
                Log.e("URL Formed", "URL FORMED: " + urlObj.toString());

            } else {
                if (data.getParam() == null || data.getParam().isEmpty()) {
                    urlObj = new URL(data.getUrl() + data.getMethordName());
                } else {
                    urlObj = new URL(data.getUrl() + data.getMethordName() + data.getParam());
                }
            }

            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Authorization", "Bearer " + Preferences.getInstance().token);

            if (connection.getResponseCode() != 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                connection.disconnect();
                Log.e("String=== ", sb.toString());
                Log.e("D Sring=== ", sb.toString());
                response = Econstants.createOfflineObject(data.getUrl(), data.getParam(), sb.toString(), Integer.toString(connection.getResponseCode()), data.getMethordName());

                return response;
            } else {

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                connection.disconnect();
                Log.e("String=== ", sb.toString());
                response = Econstants.createOfflineObject(data.getUrl(), data.getParam(), sb.toString(), Integer.toString(connection.getResponseCode()), data.getMethordName());
                return response;
            }

        } catch (Exception e) {
            e.printStackTrace();
            response = Econstants.createOfflineObject(data.getUrl(), data.getParam(), e.getLocalizedMessage(), Integer.toString(connection.getResponseCode()), data.getMethordName());

            return response;
        } finally {
            if (reader != null) {
                try {
                    reader.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    response = Econstants.createOfflineObject(data.getUrl(), data.getParam(), e.getLocalizedMessage(), Integer.toString(connection.getResponseCode()), data.getMethordName());
                    return response;
                }
            }
        }
    }

    // POST / GET Data with JSON Body.. Add JSON Body in Object Param
    public ResponsePojoGet GetDataWithJsonBody(UploadObject data) throws IOException {
        BufferedReader reader = null;
        URL urlObj = null;
        ResponsePojoGet response = null;
        HttpURLConnection connection = null;

        try {
            // Construct URL
            if (data.getAPI_NAME() != null && data.getAPI_NAME().equalsIgnoreCase(Econstants.API_NAME_HRTC)) {
                urlObj = new URL(data.getUrl() + data.getMethordName());
            } else {
                urlObj = new URL(data.getUrl() + data.getMethordName());
            }
            Log.e("URL Formed", "URL FORMED: " + urlObj.toString());


            // Open Connection
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("POST"); // Changed to POST
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Authorization", "Bearer " + Preferences.getInstance().token);
            connection.setDoOutput(true); // Allow sending request body

            // Add JSON Body
            if (data.getParam() != null && !data.getParam().isEmpty()) {
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = data.getParam().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            // Handle Response
            int responseCode = connection.getResponseCode();
            InputStream inputStream = (responseCode == 200) ? connection.getInputStream() : connection.getErrorStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            response = Econstants.createOfflineObject(
                    data.getUrl(),
                    data.getParam(),
                    sb.toString(),
                    Integer.toString(responseCode),
                    data.getMethordName()
            );

        } catch (Exception e) {
            e.printStackTrace();
            response = Econstants.createOfflineObject(
                    data.getUrl(),
                    data.getParam(),
                    e.getLocalizedMessage(),
                    (connection != null) ? Integer.toString(connection.getResponseCode()) : "500",
                    data.getMethordName()
            );
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }

    public ResponsePojoGet GetDataWithJsonBodyHimAccessToken(UploadObject data) throws IOException {
        BufferedReader reader = null;
        URL urlObj = null;
        ResponsePojoGet response = null;
        HttpURLConnection connection = null;

        try {
            // Construct URL
            if (data.getAPI_NAME() != null && data.getAPI_NAME().equalsIgnoreCase(Econstants.API_NAME_HRTC)) {
                urlObj = new URL(data.getUrl() + data.getMethordName());
            } else {
                urlObj = new URL(data.getUrl() + data.getMethordName());
            }
            Log.e("URL Formed", "URL FORMED: " + urlObj.toString());


            // Open Connection
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Authorization", "Bearer " + Preferences.getInstance().tokenHimAccess);
            connection.setDoOutput(true); // Allow sending request body

            // Add JSON Body
            if (data.getParam() != null && !data.getParam().isEmpty()) {
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = data.getParam().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            // Handle Response
            int responseCode = connection.getResponseCode();
            InputStream inputStream = (responseCode == 200) ? connection.getInputStream() : connection.getErrorStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            response = Econstants.createOfflineObject(
                    data.getUrl(),
                    data.getParam(),
                    sb.toString(),
                    Integer.toString(responseCode),
                    data.getMethordName()
            );

        } catch (Exception e) {
            e.printStackTrace();
            response = Econstants.createOfflineObject(
                    data.getUrl(),
                    data.getParam(),
                    e.getLocalizedMessage(),
                    (connection != null) ? Integer.toString(connection.getResponseCode()) : "500",
                    data.getMethordName()
            );
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }


    // Delete Post mapping.. Same but with JSON Body
    public ResponsePojoGet DeleteDataWithJson(UploadObject data) {
        URL url_ = null;
        HttpURLConnection conn_ = null;
        StringBuilder sb = null;
        ResponsePojoGet response = null;

        try {
            // Construct the DELETE URL
            String URL = data.getUrl() + data.getMethordName();
            Log.e("URL Formed", "URL FORMED: " + URL.toString());

            // Initialize connection
            url_ = new URL(URL);
            conn_ = (HttpURLConnection) url_.openConnection();
            conn_.setRequestMethod("DELETE");
            conn_.setConnectTimeout(10000);
            conn_.setReadTimeout(10000);
            conn_.setRequestProperty("Content-Type", "application/json");
            conn_.setDoOutput(true); // Enable sending body data

            // Write JSON body if present
            if (data.getParam() != null && !data.getParam().isEmpty()) {
                try (OutputStream os = conn_.getOutputStream()) {
                    byte[] input = data.getParam().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            // Handle the response
            int responseCode = conn_.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                // Error response
                BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getErrorStream(), "utf-8"));
                String line;
                sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                Log.e("Error Response", sb.toString());
                response = Econstants.createOfflineObject(URL, data.getParam(), sb.toString(), String.valueOf(responseCode), data.getMethordName());
            } else {
                // Success response
                BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getInputStream(), "utf-8"));
                String line;
                sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                Log.d("Success Response", sb.toString());
                response = Econstants.createOfflineObject(URL, data.getParam(), sb.toString(), String.valueOf(responseCode), data.getMethordName());
            }
        } catch (MalformedURLException e) {
            Log.e("MalformedURLException", e.getMessage());
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        } finally {
            if (conn_ != null) {
                conn_.disconnect();
            }
        }

        return response;
    }

    // POST DATA With Master Name + JSON body
    public ResponsePojoGet PostData(UploadObject data) {

        URL url_ = null;
        HttpURLConnection conn_ = null;
        StringBuilder sb = null;
        JSONStringer userJson = null;

        String URL = null;
        ResponsePojoGet response = null;

        try {

            URL = data.getUrl() + data.getMethordName() + data.getMasterName();
            Log.e("URL Formed", "URL FORMED: " + URL.toString());
            url_ = new URL(URL);
            conn_ = (HttpURLConnection) url_.openConnection();
            conn_.setDoOutput(true);
            conn_.setRequestMethod("POST");
            conn_.setUseCaches(false);
            conn_.setConnectTimeout(10000);
            conn_.setReadTimeout(10000);
            conn_.setRequestProperty("Content-Type", "application/json");
            conn_.setRequestProperty("Authorization", "Bearer " + Preferences.getInstance().token);
            conn_.connect();


            OutputStreamWriter out = new OutputStreamWriter(conn_.getOutputStream());
            out.write(data.getParam());
            out.close();

            try {
                int HttpResult = conn_.getResponseCode();
                if (HttpResult != HttpURLConnection.HTTP_OK) {
                    Log.e("Error", conn_.getResponseMessage());
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getErrorStream(), "utf-8"));
                    String line = null;
                    sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println(sb.toString());
                    Log.e("Data from Service", sb.toString());
                    response = new ResponsePojoGet();
                    response = Econstants.createOfflineObject(URL, data.getParam(), sb.toString(), Integer.toString(conn_.getResponseCode()), data.getMethordName());
                    return response;


                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getInputStream(), "utf-8"));
                    String line = null;
                    sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println(sb.toString());
                    Log.e("Data from Service", sb.toString());
                    response = new ResponsePojoGet();
                    response = Econstants.createOfflineObject(URL, data.getParam(), sb.toString(), Integer.toString(conn_.getResponseCode()), data.getMethordName());

                }

            } catch (Exception e) {
                data.getScanDataPojo().setUploaddToServeer(false);
                response = new ResponsePojoGet();
                response = Econstants.createOfflineObject(URL, data.getParam(), conn_.getResponseMessage(), Integer.toString(conn_.getResponseCode()), data.getMethordName());
                return response;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn_ != null)
                conn_.disconnect();
        }
        return response;
    }

    // PUT DATA With Master Name + JSON body
    public ResponsePojoGet PutData(UploadObject data) {

        URL url_ = null;
        HttpURLConnection conn_ = null;
        StringBuilder sb = null;
        JSONStringer userJson = null;

        String URL = null;
        ResponsePojoGet response = null;


        try {

            URL = data.getUrl() + data.getMethordName() + data.getMasterName();

            url_ = new URL(URL);
            conn_ = (HttpURLConnection) url_.openConnection();
            conn_.setDoOutput(true);
            conn_.setRequestMethod("PUT");
            conn_.setUseCaches(false);
            conn_.setConnectTimeout(10000);
            conn_.setReadTimeout(10000);
            conn_.setRequestProperty("Content-Type", "application/json");
            conn_.setRequestProperty("Authorization", "Bearer " + Preferences.getInstance().token);
            conn_.connect();


            OutputStreamWriter out = new OutputStreamWriter(conn_.getOutputStream());
            out.write(data.getParam());
            out.close();

            try {
                int HttpResult = conn_.getResponseCode();
                if (HttpResult != HttpURLConnection.HTTP_OK) {
                    Log.e("Error", conn_.getResponseMessage());
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getErrorStream(), "utf-8"));
                    String line = null;
                    sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println(sb.toString());
                    Log.e("Data from Service", sb.toString());
                    response = new ResponsePojoGet();
                    response = Econstants.createOfflineObject(URL, data.getParam(), sb.toString(), Integer.toString(conn_.getResponseCode()), data.getMethordName());
                    return response;


                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getInputStream(), "utf-8"));
                    String line = null;
                    sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println(sb.toString());
                    Log.e("Data from Service", sb.toString());
                    response = new ResponsePojoGet();
                    response = Econstants.createOfflineObject(URL, data.getParam(), sb.toString(), Integer.toString(conn_.getResponseCode()), data.getMethordName());

                }

            } catch (Exception e) {
                data.getScanDataPojo().setUploaddToServeer(false);
                response = new ResponsePojoGet();
                response = Econstants.createOfflineObject(URL, data.getParam(), conn_.getResponseMessage(), Integer.toString(conn_.getResponseCode()), data.getMethordName());
                return response;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn_ != null)
                conn_.disconnect();
        }
        return response;
    }

    // DELETE MAPPING
    public ResponsePojoGet DeleteData(UploadObject data) {

        URL url_ = null;
        HttpURLConnection conn_ = null;
        StringBuilder sb = null;
        JSONStringer userJson = null;

        String URL = null;
        ResponsePojoGet response = null;


        try {

            URL = data.getUrl() + data.getMethordName() + data.getMasterName();

            url_ = new URL(URL);
            conn_ = (HttpURLConnection) url_.openConnection();
            conn_.setDoOutput(true);
            conn_.setRequestMethod("DELETE");
            conn_.setUseCaches(false);
            conn_.setConnectTimeout(10000);
            conn_.setReadTimeout(10000);
            conn_.setRequestProperty("Content-Type", "application/json");
            conn_.setRequestProperty("Authorization", "Bearer " + Preferences.getInstance().token);
            conn_.connect();


            if (Econstants.isNotEmpty(data.getParam())) {
                OutputStreamWriter out = new OutputStreamWriter(conn_.getOutputStream());
                out.write(data.getParam());
                out.close();
            }


            try {
                int HttpResult = conn_.getResponseCode();
                if (HttpResult != HttpURLConnection.HTTP_OK) {
                    Log.e("Error", conn_.getResponseMessage());
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getErrorStream(), "utf-8"));
                    String line = null;
                    sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println(sb.toString());
                    Log.e("Data from Service", sb.toString());
                    response = new ResponsePojoGet();
                    response = Econstants.createOfflineObject(URL, data.getParam(), sb.toString(), Integer.toString(conn_.getResponseCode()), data.getMethordName());
                    return response;


                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getInputStream(), "utf-8"));
                    String line = null;
                    sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println(sb.toString());
                    Log.e("Data from Service", sb.toString());
                    response = new ResponsePojoGet();
                    response = Econstants.createOfflineObject(URL, data.getParam(), sb.toString(), Integer.toString(conn_.getResponseCode()), data.getMethordName());

                }

            } catch (Exception e) {
                data.getScanDataPojo().setUploaddToServeer(false);
                response = new ResponsePojoGet();
                response = Econstants.createOfflineObject(URL, data.getParam(), conn_.getResponseMessage(), Integer.toString(conn_.getResponseCode()), data.getMethordName());
                return response;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn_ != null)
                conn_.disconnect();
        }
        return response;
    }


    // GET FOR HIM ACCESS.. MASTERS WITHOUT TOKEN
    public ResponsePojoGet GetDataHimAccess(UploadObject data) throws IOException {
        BufferedReader reader = null;
        URL urlObj = null;
        ResponsePojoGet response = null;
        HttpURLConnection connection = null;

        try {
            if (data.getAPI_NAME() != null && data.getAPI_NAME().equalsIgnoreCase(Econstants.API_NAME_HRTC)) {
                StringBuilder SB = new StringBuilder();

                SB.append(data.getUrl());
                SB.append(data.getMethordName());

                if (data.getStatus() != null) {
                    SB.append(Econstants.status);
                    SB.append(data.getStatus());
                }
                if (data.getMasterName() != null) {
                    SB.append(Econstants.masterName);
                    SB.append(data.getMasterName());
                }

                if (data.getParentId() != null) {
                    SB.append(Econstants.parentId);
                    SB.append(data.getParentId());
                }

                if (data.getSecondaryParentId() != null) {
                    SB.append(Econstants.secondaryParentId);
                    SB.append(data.getSecondaryParentId());
                }

                if (data.getParam() != null) {
                    SB.append(data.getParam());
                }

                urlObj = new URL(SB.toString());
                Log.e("URL Formed", "URL FORMED: " + urlObj.toString());

            } else {
                if (data.getParam() == null || data.getParam().isEmpty()) {
                    urlObj = new URL(data.getUrl() + data.getMethordName());
                } else {
                    urlObj = new URL(data.getUrl() + data.getMethordName() + data.getParam());
                }
            }

            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            if (connection.getResponseCode() != 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                connection.disconnect();
                Log.e("String=== ", sb.toString());
                Log.e("D Sring=== ", sb.toString());
                response = Econstants.createOfflineObject(data.getUrl(), data.getParam(), sb.toString(), Integer.toString(connection.getResponseCode()), data.getMethordName());

                return response;
            } else {

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                connection.disconnect();
                Log.e("String=== ", sb.toString());
                response = Econstants.createOfflineObject(data.getUrl(), data.getParam(), sb.toString(), Integer.toString(connection.getResponseCode()), data.getMethordName());
                return response;
            }

        } catch (Exception e) {
            e.printStackTrace();
            response = Econstants.createOfflineObject(data.getUrl(), data.getParam(), e.getLocalizedMessage(), Integer.toString(connection.getResponseCode()), data.getMethordName());

            return response;
        } finally {
            if (reader != null) {
                try {
                    reader.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    response = Econstants.createOfflineObject(data.getUrl(), data.getParam(), e.getLocalizedMessage(), Integer.toString(connection.getResponseCode()), data.getMethordName());
                    return response;
                }
            }
        }
    }

    // POST DATA With Master Name + JSON body + No JWT
    public ResponsePojoGet PostDataHimAccess(UploadObject data) {

        URL url_ = null;
        HttpURLConnection conn_ = null;
        StringBuilder sb = null;
        JSONStringer userJson = null;

        String URL = null;
        ResponsePojoGet response = null;

        try {

            URL = "";
            if (data.getUrl() != null) URL += data.getUrl();
            if (data.getMethordName() != null) URL += data.getMethordName();
            if (data.getMasterName() != null) URL += data.getMasterName();

            Log.e("URL Formed", "URL FORMED: " + URL.toString());
            url_ = new URL(URL);
            conn_ = (HttpURLConnection) url_.openConnection();
            conn_.setDoOutput(true);
            conn_.setRequestMethod("POST");
            conn_.setUseCaches(false);
            conn_.setConnectTimeout(10000);
            conn_.setReadTimeout(10000);
            conn_.setRequestProperty("Content-Type", "application/json");
            conn_.connect();


            OutputStreamWriter out = new OutputStreamWriter(conn_.getOutputStream());
            out.write(data.getParam());
            out.close();

            try {
                int HttpResult = conn_.getResponseCode();
                if (HttpResult != HttpURLConnection.HTTP_OK) {
                    Log.e("Error", conn_.getResponseMessage());
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getErrorStream(), "utf-8"));
                    String line = null;
                    sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println(sb.toString());
                    Log.e("Data from Service", sb.toString());
                    response = new ResponsePojoGet();
                    response = Econstants.createOfflineObject(URL, data.getParam(), sb.toString(), Integer.toString(conn_.getResponseCode()), data.getMethordName());
                    return response;


                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getInputStream(), "utf-8"));
                    String line = null;
                    sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println(sb.toString());
                    Log.e("Data from Service", sb.toString());
                    response = new ResponsePojoGet();
                    response = Econstants.createOfflineObject(URL, data.getParam(), sb.toString(), Integer.toString(conn_.getResponseCode()), data.getMethordName());

                }

            } catch (Exception e) {
                data.getScanDataPojo().setUploaddToServeer(false);
                response = new ResponsePojoGet();
                response = Econstants.createOfflineObject(URL, data.getParam(), conn_.getResponseMessage(), Integer.toString(conn_.getResponseCode()), data.getMethordName());
                return response;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn_ != null)
                conn_.disconnect();
        }
        return response;
    }

    public ResponsePojoGet DeleteDataHimAccess(UploadObject data) {

        URL url_ = null;
        HttpURLConnection conn_ = null;
        StringBuilder sb = null;
        JSONStringer userJson = null;

        String URL = null;
        ResponsePojoGet response = null;


        try {

            URL = data.getUrl() + data.getMethordName() + data.getMasterName();

            url_ = new URL(URL);
            conn_ = (HttpURLConnection) url_.openConnection();
            conn_.setDoOutput(true);
            conn_.setRequestMethod("DELETE");
            conn_.setUseCaches(false);
            conn_.setConnectTimeout(10000);
            conn_.setReadTimeout(10000);
            conn_.setRequestProperty("Content-Type", "application/json");
            conn_.connect();


            if (Econstants.isNotEmpty(data.getParam())) {
                OutputStreamWriter out = new OutputStreamWriter(conn_.getOutputStream());
                out.write(data.getParam());
                out.close();
            }


            try {
                int HttpResult = conn_.getResponseCode();
                if (HttpResult != HttpURLConnection.HTTP_OK) {
                    Log.e("Error", conn_.getResponseMessage());
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getErrorStream(), "utf-8"));
                    String line = null;
                    sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println(sb.toString());
                    Log.e("Data from Service", sb.toString());
                    response = new ResponsePojoGet();
                    response = Econstants.createOfflineObject(URL, data.getParam(), sb.toString(), Integer.toString(conn_.getResponseCode()), data.getMethordName());
                    return response;


                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getInputStream(), "utf-8"));
                    String line = null;
                    sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println(sb.toString());
                    Log.e("Data from Service", sb.toString());
                    response = new ResponsePojoGet();
                    response = Econstants.createOfflineObject(URL, data.getParam(), sb.toString(), Integer.toString(conn_.getResponseCode()), data.getMethordName());

                }

            } catch (Exception e) {
                data.getScanDataPojo().setUploaddToServeer(false);
                response = new ResponsePojoGet();
                response = Econstants.createOfflineObject(URL, data.getParam(), conn_.getResponseMessage(), Integer.toString(conn_.getResponseCode()), data.getMethordName());
                return response;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn_ != null)
                conn_.disconnect();
        }
        return response;
    }

    public ResponsePojoGet PutDataHimAccess(UploadObject data) {

        URL url_ = null;
        HttpURLConnection conn_ = null;
        StringBuilder sb = null;
        JSONStringer userJson = null;

        String URL = null;
        ResponsePojoGet response = null;


        try {

            URL = data.getUrl() + data.getMethordName() + data.getMasterName();

            url_ = new URL(URL);
            conn_ = (HttpURLConnection) url_.openConnection();
            conn_.setDoOutput(true);
            conn_.setRequestMethod("PUT");
            conn_.setUseCaches(false);
            conn_.setConnectTimeout(10000);
            conn_.setReadTimeout(10000);
            conn_.setRequestProperty("Content-Type", "application/json");
            conn_.connect();


            OutputStreamWriter out = new OutputStreamWriter(conn_.getOutputStream());
            out.write(data.getParam());
            out.close();

            try {
                int HttpResult = conn_.getResponseCode();
                if (HttpResult != HttpURLConnection.HTTP_OK) {
                    Log.e("Error", conn_.getResponseMessage());
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getErrorStream(), "utf-8"));
                    String line = null;
                    sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println(sb.toString());
                    Log.e("Data from Service", sb.toString());
                    response = new ResponsePojoGet();
                    response = Econstants.createOfflineObject(URL, data.getParam(), sb.toString(), Integer.toString(conn_.getResponseCode()), data.getMethordName());
                    return response;


                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getInputStream(), "utf-8"));
                    String line = null;
                    sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println(sb.toString());
                    Log.e("Data from Service", sb.toString());
                    response = new ResponsePojoGet();
                    response = Econstants.createOfflineObject(URL, data.getParam(), sb.toString(), Integer.toString(conn_.getResponseCode()), data.getMethordName());

                }

            } catch (Exception e) {
                data.getScanDataPojo().setUploaddToServeer(false);
                response = new ResponsePojoGet();
                response = Econstants.createOfflineObject(URL, data.getParam(), conn_.getResponseMessage(), Integer.toString(conn_.getResponseCode()), data.getMethordName());
                return response;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn_ != null)
                conn_.disconnect();
        }
        return response;
    }


    // Attendance Module / Face Auth Implemented
    public FaceAuthObjectResponse eKYCViaFaceAuth(FaceAuthObjectRequest data) {

        URL url_ = null;
        HttpURLConnection conn_ = null;
        StringBuilder sb = null;
        JSONStringer userJson = null;
        String URL = null;
        FaceAuthObjectResponse response = null;
        try {
            URL = data.getUrl() + data.getMethordName();

            url_ = new URL(URL);
            conn_ = (HttpURLConnection) url_.openConnection();

            conn_.setUseCaches(false);
            conn_.setDoInput(true);
            conn_.setDoOutput(true);
            conn_.setRequestMethod("POST");
            conn_.setRequestProperty("Content-Type", "text/plain; charset=ISO-8859-1");
            conn_.setFollowRedirects(true);
            conn_.setConnectTimeout(120000);
            conn_.setReadTimeout(120000);
            conn_.connect();


            OutputStreamWriter out = new OutputStreamWriter(conn_.getOutputStream());
            out.write(data.getEkycXML());
            out.close();

            try {
                int HttpResult = conn_.getResponseCode();
                if (HttpResult != HttpURLConnection.HTTP_OK) {
                    Log.e("Error", conn_.getResponseMessage());
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getErrorStream(), "utf-8"));
                    String line = null;
                    sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println(sb.toString());
                    Log.e("Data from Service", sb.toString());
                    response = new FaceAuthObjectResponse();
                    response = Econstants.createObjectFaceResponse(URL, data.getAadhaarNumber(), conn_.getResponseMessage(), Integer.toString(conn_.getResponseCode()), data.getMethordName());
                    return response;


                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getInputStream(), "utf-8"));
                    String line = null;
                    sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println(sb.toString());
                    Log.e("Data from Service", sb.toString());
                    response = new FaceAuthObjectResponse();
                    response = Econstants.createObjectFaceResponse(URL, data.getAadhaarNumber(), sb.toString(), Integer.toString(conn_.getResponseCode()), data.getMethordName());

                }

            } catch (Exception e) {
                response = new FaceAuthObjectResponse();
                response = Econstants.createObjectFaceResponse(URL, data.getAadhaarNumber(), "", "0", data.getMethordName());
                return response;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn_ != null)
                conn_.disconnect();
        }
        return response;
    }




}
