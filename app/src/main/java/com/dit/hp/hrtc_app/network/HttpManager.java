package com.dit.hp.hrtc_app.network;


import android.util.Log;

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


public class HttpManager {

//    CryptographyAES AES = new CryptographyAES();
//
//    EncryptDecrypt ED;
//
//    public HttpManager() throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException {
//        ED = new EncryptDecrypt();
//    }

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


}
