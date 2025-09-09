package com.dit.hp.hrtc_app.network;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class NetworkUtils {

    /**
     * SSL Handshake
     * If the HTTPS Error Still exists , SSLSocketFactory class is used tof the Handshake
     */
    public static SSLSocketFactory getSSLSocketFactory() {
        SSLContext sslContext = null;
        try {
            TrustManager[] tm = {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, tm, null);
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslContext.getSocketFactory();
    }

    /**
     * @param url
     * @return HttpUrlConnection
     * Method for Setting the Header and Footer of Connections
     */
    public static HttpURLConnection getInputStreamConnection(String url) {
        HttpURLConnection conn = null;
        try {
            URL url_ = new URL(url);
            conn = (HttpURLConnection) url_.openConnection();
//            if (url_.toString().startsWith("https")) {
//                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) conn;
//                httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
//        }
        //    conn = (HttpURLConnection) url_.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setInstanceFollowRedirects(true);
            conn.connect();
        } catch (Exception e) {

        }
        return conn;
    }

    public static HttpURLConnection getSarthiInputStreamConnection(String url) {
        HttpURLConnection conn = null;
        try {
            URL url_ = new URL(url);
            conn = (HttpURLConnection) url_.openConnection();
//            if (url_.toString().startsWith("https")) {
//                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) conn;
//                httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
//        }
            //    conn = (HttpURLConnection) url_.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setInstanceFollowRedirects(true);
            conn.connect();
        } catch (Exception e) {

        }
        return conn;
    }

    /**
     * @param conn_
     * @return
     * @throws IOException
     * Methord to Get all the Errors from the Server . If status is not equal to 200
     */
    public static StringBuilder getErrorStream(HttpURLConnection conn_) throws IOException {
        StringBuilder sb = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getErrorStream(), "UTF-8"));
        String line = null;
        sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        System.out.println("Error==  "+ sb.toString());
        return sb;
    }

    /**
     * @param conn_
     * @return
     * @throws IOException
     * Method to get the Input Stream for Reading and Writing Data
     */
    public static StringBuilder getInputStream(HttpURLConnection conn_) throws IOException {
        StringBuilder sb = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(conn_.getInputStream(), "UTF-8"));
        String line = null;
        sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        return sb;
    }
}
