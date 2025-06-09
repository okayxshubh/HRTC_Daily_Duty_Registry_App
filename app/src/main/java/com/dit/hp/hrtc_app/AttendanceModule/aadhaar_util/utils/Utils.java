package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.constants.FaceAuthConstants;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.input.contract.CaptureResponse;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.input.contract.NameValue;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class Utils {

    public static final String ENVIRONMENT_TAG = "P";   //P;
    public static final String LANGUAGE = "en";
    public static final boolean ENABLE_AUTO_CAPTURE = true;
    public static final boolean resetImageScoreSheet = false;
    private static boolean enableImageScoreSheet = FaceAuthConstants.DEBUG;

    private Utils() {
        // Private constructor to prevent instantiation
    }

    public static String createTxn(String aua) {

        Date dNow = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyMMddHHmmssSSSS");
        String time = f.format(dNow.getTime());
        Random random = new Random();
        // String txn = time + random.nextInt(1000000000) +":"+ "BIO";
        String txn =  "HPDDTG"+time + "D";

        return txn;
    }

    public static  String getCurrentTimestamp()
    {
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        String ctime = dfm.format(date).replace(" ", "T");
        return ctime;
    }

    public static String formatCaptureResponse(CaptureResponse response) {
        long serverComputeTime = getTimeValueFromCustOption(response.custOpts.nameValues, "serverComputeTime");
        long clientComputeTime = getTimeValueFromCustOption(response.custOpts.nameValues, "clientComputeTime");
        long networkLatencyTime = getTimeValueFromCustOption(response.custOpts.nameValues, "networkLatencyTime");

        if (response.isSuccess()) {
            return "Capture of the image and face liveness was successful for transaction - " + response.getTxnID() +
                    " and an encrypted PID data has been sent for further processing.." +
                    "\n\nServer computation time " + convertToSeconds(serverComputeTime) +
                    "\nClient computation time " + convertToSeconds(clientComputeTime) +
                    "\nNetwork latency time " + convertToSeconds(networkLatencyTime) +
                    "\nTotal duration in AUA App " + convertToSeconds(TotalTime.returnTotalTime()) +
                    "\n" + response.getImageScoreSheet();
        } else {
            return "Capture failed for transaction - " + response.getTxnID() + " with error : " +
                    response.getErrCode() + " - " + response.getErrInfo() + ". \n" +
                    "\n\nServer computation time " + convertToSeconds(serverComputeTime) +
                    "\nClient computation time " + convertToSeconds(clientComputeTime) +
                    "\nNetwork latency time " + convertToSeconds(networkLatencyTime) +
                    "\nTotal duration in AUA App " + convertToSeconds(TotalTime.returnTotalTime()) +
                    "\n" + response.getImageScoreSheet();
        }
    }

    private static long getTimeValueFromCustOption(List<NameValue> nameValues, String key) {
        try {
            for (NameValue nameValue : nameValues) {
                if (nameValue.getName().equals(key)) {
                    return Long.parseLong(nameValue.getValue());
                }
            }
        } catch (Exception e) {
            return -1L;
        }
        return -1L;
    }

    private static String convertToSeconds(long timeInMillis) {
        return timeInMillis / 1000f + " secs";
    }

    public static String createPidOptionForAuth(String txnId) throws NoSuchAlgorithmException, NoSuchProviderException {
        return createPidOptions(txnId, "auth");
    }

    public static String createPidOptionForRegister(String txnId) throws NoSuchAlgorithmException, NoSuchProviderException {
        return createPidOptions(txnId, "register");
    }

    private static String createPidOptions(String txnId, String purpose) throws NoSuchAlgorithmException, NoSuchProviderException {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<PidOptions ver=\"1.0\" env=\"" + ENVIRONMENT_TAG + "\">\n" +
                "   <Opts format=\"0\" pidVer=\"2.0\" otp=\"\" wadh=\"" + FaceAuthConstants.generateWadh() + "\" />\n" +
                "   <CustOpts>\n" +
                "      <Param name=\"txnId\" value=\"" + txnId + "\"/>\n" +
                "      <Param name=\"language\" value=\"" + LANGUAGE + "\"/>\n" +
                "   </CustOpts>\n" +
                "</PidOptions>";
    }

    //wadth to be changed

    public static String getTransactionID() {
        return UUID.randomUUID().toString();
    }



    public static Bitmap convertToBitmap(String image) {
        byte[] decodedString = Base64.decode(image, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

//    public static String queryName(ContentResolver resolver, Uri uri) {
//        return queryColumnValue(resolver, uri, OpenableColumns.DISPLAY_NAME);
//    }
//
//    private static String queryColumnValue(ContentResolver resolver, Uri uri, String columnName) {
//        Cursor cursor = resolver.query(uri, null, null, null, null);
//        if (cursor != null) {
//            try {
//                if (cursor.moveToFirst()) {
//                    return cursor.getString(cursor.getColumnIndex(columnName));
//                }
//            } finally {
//                cursor.close();
//            }
//        }
//        return null;
//    }

    public static void printLog(String tag, String msg) {
        if (FaceAuthConstants.DEBUG) {
            Log.e(tag, msg);
        }
    }

    public interface TotalTime {
        // Static fields to store start and stop time
        long startTime = 0L;
        long stopTime = 0L;

        // Static method to calculate the total time
        static long returnTotalTime() {
            return stopTime - startTime;
        }
    }
}



