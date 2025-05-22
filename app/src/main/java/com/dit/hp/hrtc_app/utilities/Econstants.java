package com.dit.hp.hrtc_app.utilities;

import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;


public class Econstants {

    // Is Not Empty Check Method
    public static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    public static final String internetNotAvailable = "Internet not Available. Please Connect to Internet and try again.";

    // status
    public static final Boolean STATUS_TRUE = true;
    public static final Boolean STATUS_FALSE = false;

    // Search Delay
    public static final Integer SEARCH_DELAY = 700;
    public static final Integer PAGE_SIZE = 30;


    // OTHERS: for appending correct url in HttpManager
    public static final String status = "status=";
    public static final String masterName = "&masterName=";
    public static final String parentId = "&parentId=";
    public static final String secondaryParentId = "&secondaryParentId=";

    // Add master names
    public static final String API_NAME_HRTC = "HRTC";
    public static final String OFFICE_Type_REVENUE = "Revenue";
    public static final String OFFICE_Type_RURAL = "Rural";

    public static final String HRTC_DEPARTMENT_NAME = "Himachal Road Transport Corporation";
    public static final Integer HRTC_DEPARTMENT_ID = 106;




    public static final String loginMethod = "/login/Auth?";

    public static final Integer HRTC_DEPARTMENT_PARENT_ID = 106;



//    public static final String base_url = "http://192.168.0.172:8081"; // DIT Wifi + DIT PC


    public static final String base_url = "https://himstaging1.hp.gov.in/hrtc"; // Staging
//    public static final String base_url = "https://himparivarservices.hp.gov.in/hrtc"; // PROD

    public static final String eparivar_url = "https://himparivarservices.hp.gov.in/ldap";    // For login purpose

    public static final String sarvatra_url = "https://himstaging1.hp.gov.in/sarvatra-api";   //  Staging HIM ACCESS
//    public static final String sarvatra_url = "https://himparivarservices.hp.gov.in/sarvatra-api";  //  Production HIM ACCESS


    public static final String appUniqueCode = "HRTCDDR";
    public static final String serviceId = "3";

    public static final String loginLDAP = "/login?";
    public static final String getToken = "/getToken?";
    public static final String getUserDetails = "/getUserDetails?";



//    public static final String abc = "https://himstaging2.hp.gov.in/sarvatra/GovLogin?service_id=3&app_unique=HRTCDDR&app=Himparivar"; //









    public static ResponsePojoGet createOfflineObject(String url, String requestParams, String response, String Code, String functionName) {
        ResponsePojoGet pojo = new ResponsePojoGet();
        pojo.setUrl(url);
        pojo.setRequestParams(requestParams);
        pojo.setResponse(response);
        pojo.setFunctionName(functionName);
        pojo.setResponseCode(Code);
        return pojo;
    }


}

