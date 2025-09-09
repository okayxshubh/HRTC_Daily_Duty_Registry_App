// NEW

package com.dit.hp.hrtc_app.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class AppStatus {

    private static AppStatus instance;
    private Context context; // ✅ now private
    private ConnectivityManager connectivityManager;

    private AppStatus(Context ctx) {
        this.context = ctx.getApplicationContext(); // ✅ safe usage
    }

    public static AppStatus getInstance(Context ctx) {
        if (instance == null) {
            instance = new AppStatus(ctx); // ✅ pass context on first call
        }
        return instance;
    }

    public boolean isOnline() {
        boolean connected = false;
        try {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
        } catch (Exception e) {
            Log.e("AppStatus", "CheckConnectivity Exception: " + e.getMessage(), e);
        }
        return connected;
    }
}

//OLD

//package com.dit.hp.hrtc_app.utilities;
//
//import android.content.Context;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.util.Log;
//
//public class AppStatus {
//
//    private static AppStatus instance = new AppStatus();
//
//    public Context context;
//    ConnectivityManager connectivityManager;
//    boolean connected = false;
//
//    public static AppStatus getInstance(Context ctx) {
//        context = ctx.getApplicationContext();
//        return instance;
//    }
//
//    public boolean isOnline() {
//        try {
//            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
//            return connected;
//
//        } catch (Exception e) {
//            System.out.println("CheckConnectivity Exception: " + e.getMessage());
//            Log.v("connectivity", e.toString());
//        }
//        return connected;
//    }
//}
