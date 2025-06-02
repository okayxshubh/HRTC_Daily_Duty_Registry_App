package com.dit.hp.hrtc_app.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;

public class CommonUtils {


    /**
     * Convert given bitmap image to Base64 String.
     * @param bitmap: bitmap image to be converted into base64 string format
     * @return imageEncoded: Base64 conversion of given bitmap
     */
    public static String bitmapToBase64(Bitmap bitmap){
        Bitmap immagex=bitmap;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
        return imageEncoded;
    }

    /**
     * Convert a base64 string to bitmap
     * @param base64: String of type base64
     * @return decodedByte: bitmap, converted from string
     */
    public static Bitmap base64ToBitmap(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    /**
     * Converts given time in seconds to the specified String format.
     * @param seconds: seconds, this must be a long value
     * @param format: date/time format needed
     * @return converted date, from milliseconds to given format
     */
    public static String getDateTime(long seconds, String format){
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatToReturn=new SimpleDateFormat(format);
        formatToReturn.setTimeZone(TimeZone.getDefault());
        if(String.valueOf(seconds).length()>10){
            seconds=seconds/1000;
        }
        return formatToReturn.format(seconds*1000);
    }

    /**
     * Returns the actual width of screen of device in pixels
     * @param context: context of activity
     * @return width in pixels
     */
    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context context){
        if(Build.VERSION.SDK_INT >= 13){
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            return metrics.widthPixels;

        }else{
            Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).
                    getDefaultDisplay();
            return display.getWidth();
        }
    }

    /**
     * Returns the actual height of screen of device in pixels
     * @param context: context of activity
     * @return height in pixels
     */
    @SuppressWarnings("deprecation")
    public static int getScreenHeight(Context context){
        if((Build.VERSION.SDK_INT >= 13)){
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            return metrics.heightPixels;

        }else{
            Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).
                    getDefaultDisplay();
            return display.getHeight();
        }
    }

    /**
     * @param inString: string to be checked
     * @return : true if string is type of email address, else otherwise.
     */
    public static boolean isEmail(String inString) {
        CharSequence cs=inString;
        if(Patterns.EMAIL_ADDRESS.matcher(cs).matches())
            return true;
        else
            return false;
    }

    /**
     * This method returns rounded bitmap
     * @param bitmap: Image to be edited
     * @return Round bitmap
     */
    public static Bitmap getRoundCornerBitmap(Bitmap bitmap) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffff0000;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, 20, 20, paint);

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) 4);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * More refined method for getting a round cornered bitmap
     * @param context: Context of activity
     * @param input: Bitmap
     * @param pixels:
     * @param w: int, Width of required bitmap
     * @param h: int, Height of required bitmap
     * @param squareTL: boolean, if we want top left corner to be square
     * @param squareTR: boolean, if we want top right corner to be square
     * @param squareBL: boolean, if we want bottom left corner to be square
     * @param squareBR: boolean, if we want bottom right corner to be square
     * @return round cornered bitmap
     */
    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap input,
                                                int pixels , int w , int h , boolean squareTL, boolean squareTR,
                                                boolean squareBL, boolean squareBR  ) {

        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        final int color = 0xffffffff;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);

        //make sure that our rounded corner is scaled appropriately
        final float roundPx = pixels*densityMultiplier;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        //draw rectangles over the corners we want to be square
        if (squareTL ){
            canvas.drawRect(0, 0, w/2, h/2, paint);
        }
        if (squareTR ){
            canvas.drawRect(w/2, 0, w, h/2, paint);
        }
        if (squareBL ){
            canvas.drawRect(0, h/2, w/2, h, paint);
        }
        if (squareBR ){
            canvas.drawRect(w/2, h/2, w, h, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(input, 0,0, paint);

        return output;
    }

    /**
     * This method returns rounded bitmap, it even works for rectangles!
     * @param bitmap: Image to be edited
     * @param border: boolean, set this to true, if you want border around image
     * @return Round bitmap
     */
    public static Bitmap getCircleBitmap(Bitmap bitmap, boolean border) {

        int borderWidth=6;

        final Paint paint = new Paint();

        float radius = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            radius = bitmap.getHeight() / 2;
        } else {
            radius = bitmap.getWidth() / 2;
        }

        final int width = bitmap.getWidth() + borderWidth;
        final int height = bitmap.getHeight() + borderWidth;

        Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setAntiAlias(true);
        paint.setShader(shader);
        Canvas canvas = new Canvas(canvasBitmap);
        canvas.drawCircle(radius, radius, radius, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xff684321);
        paint.setStrokeWidth(borderWidth);
        if(border){
            canvas.drawCircle(radius, radius, radius - borderWidth / 2, paint);
        }
        return canvasBitmap;
    }



    /**
     * This methods returns the name of file who's path is given as parameter
     * @param filepath: path of file
     * @return name of file
     */
    public static String getFileNameFromPath(String filepath){
        //get the file name from the file path to send to server
        int idx = filepath.lastIndexOf("/");
        String upfilename = idx >= 0 ? filepath.substring(idx + 1) : filepath;
        return upfilename;
    }

    /**
     * This method converts the dps to pixels
     * @param dp
     * @param ctx
     * @return
     */
    public static int dpToPx(int dp, Context ctx)
    {
        float density = ctx.getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    // OLD Earlier Working
//    public static String getCurrentDate(){
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//        String date = sdf.format(new Date());
//        return date;
//    }

    public static String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(new Date());
        return date;
    }

     public static Integer str2Int(String str) {
        Integer result = null;
        if (null == str || 0 == str.length()) {
            return null;
        }
        try {
            result = Integer.parseInt(str);
        }
        catch (NumberFormatException e) {
            String negativeMode = "";
            if(str.indexOf('-') != -1)
                negativeMode = "-";
            str = str.replaceAll("-", "" );
            if (str.indexOf('.') != -1) {
                str = str.substring(0, str.indexOf('.'));
                if (str.length() == 0) {
                    return (Integer)0;
                }
            }
            String strNum = str.replaceAll("[^\\d]", "" );
            if (0 == strNum.length()) {
                return null;
            }
            result = Integer.parseInt(negativeMode + strNum);
        }
        return result;
    }

    public static boolean positiveNegitive(Integer number) {
        if (number > 0) {
            return true;
        } else if (number < 0) {
            return false;
        } else {
            return false;
        }
    }

    public static  String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i("IP", "***** IP="+ ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("IP", ex.toString());
        }
        return null;
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    public static String encodeStringToBase64(String input) {
        byte[] data = input.getBytes();
        byte[] encodedBytes = Base64.encode(data, Base64.DEFAULT);
        return new String(encodedBytes);
    }

    public static String encodeBytesToBase64(byte[] inputBytes) {
        byte[] encodedBytes = Base64.encode(inputBytes, Base64.DEFAULT);
        return new String(encodedBytes);
    }

    public static String getVersionInfo(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);

            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;

            return "Version Code: " + versionCode + "\nVersion Name: " + versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Version information not available";
        }
    }


    public static String getMinMaxSdkInfo(Context context) {
        try {
            ApplicationInfo appInfo = context.getApplicationInfo();
            int minSdk = Build.VERSION_CODES.BASE; // fallback
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                minSdk = appInfo.minSdkVersion;
            }

            int targetSdk = context.getApplicationContext().getApplicationInfo().targetSdkVersion;

            return "Supported SDKs: Min " + minSdk + ", Target " + targetSdk;
        } catch (Exception e) {
            return "SDK info N/A";
        }
    }

}
