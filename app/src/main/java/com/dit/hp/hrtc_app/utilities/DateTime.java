package com.dit.hp.hrtc_app.utilities;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Kush.Dhawan
 * @project HPePass
 * @Time 03, 05 , 2020
 */
public class DateTime {
    /**
     * Function to get Data and Time
     * @return
     */
    public static String GetDateAndTime(){

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = df.format(c.getTime());


        String IN_TIME = formattedDate;

        return IN_TIME;
    }

    public static String getCurrentDateTimeWithMillis() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        Date now = new Date();
        return sdf.format(now);
    }

    public static String GetDateAndTimeImage(){

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String formattedDate = df.format(c.getTime());


        String IN_TIME = formattedDate;

        return IN_TIME;
    }

    //7 days earlierdate
    public static String getDateWithOffset(int offset){

        Calendar c = Calendar.getInstance();
        // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        c.setTime(c.getTime());
        c.add(Calendar.DAY_OF_MONTH, offset);
        return df.format(c.getTime());
    }


    public static String Change_Date_Format(String date_Current) throws ParseException {
        Log.e("Date",date_Current);
        Date initDate = new SimpleDateFormat("dd-MM-yyyy").parse(date_Current);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String parsedDate = formatter.format(initDate);
        return parsedDate;
    }

    public static String Change_Date_Format_second(String date_Current) throws ParseException {
        Log.e("Datexcxcxcxcxc",date_Current);
        Date initDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date_Current);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String parsedDate = formatter.format(initDate);
        Log.e("Datexcxcxcxcxc==",parsedDate);
        return parsedDate;
    }

    public static String changeTime(String date_) throws ParseException {
        String originalString = date_;
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(originalString);
        String newString = new SimpleDateFormat("HH:mm:ss").format(date); // 9:00
        return newString;
    }

    public static Date StringToDate(String dob) throws ParseException {
        //Instantiating the SimpleDateFormat class
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        //Parsing the given String to Date object
        Date date = formatter.parse(dob);
        System.out.println("Date object value: "+date);
        return date;
    }
}
