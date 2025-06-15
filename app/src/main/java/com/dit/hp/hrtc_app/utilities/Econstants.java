package com.dit.hp.hrtc_app.utilities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.FaceAuthObjectResponse;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


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

    public static final String FaceRD_PackageName = "in.gov.uidai.facerd";
    public static final Integer HRTC_DEPARTMENT_PARENT_ID = 106;


    // MAIN HRTC URL
    public static final String base_url = "https://himstaging1.hp.gov.in/hrtc"; // Staging
//    public static final String base_url = "https://himparivarservices.hp.gov.in/hrtc"; // PROD

    public static final String eparivar_url = "https://himparivarservices.hp.gov.in/ldap";    // For login purpose


    public static final String sarvatra_url = "https://himstaging1.hp.gov.in/sarvatra-api"; public static final Integer REGIONAL_OFFICE_ID = 369;  //Staging HIM ACCESS + Staging Regional Office ID
//    public static final String sarvatra_url = "https://himparivarservices.hp.gov.in/sarvatra-api"; public static final Integer REGIONAL_OFFICE_ID = 267; //  Production HIM ACCESS + Production Regional Office ID

    // Regional Office just Like Depot with all the items


    // Live 267

    public static final String appUniqueCode = "HRTCDDR";
    public static final String serviceId = "3";

    public static final String loginLDAP = "/login?";
    public static final String getToken = "/getToken?";
    public static final String getUserDetails = "/getUserDetails?";


    // ATTENDANCE
    public static final String attendanceBaseURL = "https://himstaging1.hp.gov.in/attendance-api?";
    public static final String methodSaveAttendance = "/attendance/save";





    public static String ekyc_consent ="1. I understand that my Aadhaar number, photograph and demographic information, as understood under the Aadhaar (Targeted Delivery of Financial and Other Subsidies, Benefitsand services) Act, 2016 (18 of 2016) and regulations framed there under, is being collected by the Government of India for the following Purposes: \n \n \t \t i. Authenticating my identity by way of the Aadhaar authentication system: \n \n \t \t ii. Registering on the Portal (Name of the Portal), after authentication, for availing subsidies, benefits & services under Section 7 of the Aadhaar (Targeted Delivery ofFinancial and Other Subsidies, Benefits and Services) Act, 2016 (18 of 2016) \n \n \t \t iii. Sharing of my Aadhaar number and demographic information and photograph, for verifying my identity for the purpose of determining my eligibility across Governmentwelfare programmes, which are in existence and for future programmes, run by the Central Government and State Governments under Section 7 of the Aadhaar (Targeted Delivery of Financial and Other Subsidies, Benefits and Services) Act, 2016 (18 of 2016) \n \n 2. I understand that the Government of India shall create an Aadhaar-seeded database containing my Aadhaar number, photograph and demographic information for all or any of thepurposes enlisted in paragraphs 1(i)-(iii) of this form, and that the Government of India shall ensure that requisite mechanisms have been put in place to ensure safety, security and privacy of such information in accordance with applicable laws, rules and regulations. \n \n 3. I have no objection to provide my Aadhaar Number, photograph and demographic information for Aadhaar based authentication for the purposes enlisted in paragraphs 1(i)-(iii) of this form and further for creation of an Aadhaar-seeded database as described Paragraph 2 of this form, \n \n 4. I also understand that my ‘no-objection’ accorded in this form is revocable and I have the right to withdraw the same at any time in future, through a communication of opting out. \n \n \n 1. मैं समझता हूँ कि मेरे द्वारा दी गयी जानकारी जैसे मेरा आधार नंबर, फोटोग्राफ, जनसांख्यिकीय जानकारी जिसमें मेरा (नाम, पता, जन्म तिथि, लिंग) आदि आते हैं। इन्हें वित्तीय और अन्य सब्सिडी का लक्षित वितरण, लाभ और सेवाएं अधिनियम, 2016 और उसके तहत बनाए गए नियमों के अंतर्गत एकत्र किया गया है। इनका प्रयोग भारत सरकार द्वारा निम्नलिखित उद्देश्य के लिए किया जा रहा है: \n \n \t \t i. आधार प्रमाणीकरण प्रणाली के माध्यम से मेरी पहचान को प्रमाणित करना। \n \n \t \t ii. प्रमाणित होने के बाद पोर्टल (पोर्टल का नाम) पर पंजीकरण और लाभ उठाने के लिए आधार की धारा 7 के तहत सब्सिडी, लाभ और सेवाएं लक्षित डिलीवरी वित्तीय और अन्य सब्सिडी, लाभ और सेवाएं अधिनियम, 2016 के तहत प्रयोग के लिए। \n \n \t \t iii. मैं अपना आधार और उससे जुड़ी जानकारियां आधार की धारा 7 के तहत सरकारी कल्याण कार्यक्रम, जो अस्तित्व में हैं और भविष्य के कार्यक्रमों के लिए हैं और केंद्र सरकार और राज्य की वित्तीय और अन्य सब्सिडी, लाभ और सेवाओं के वितरण जोकि अधिनियम, 2016 के अंतर्गत आते हैं उनमें अपनी पात्रता जाँचने के लिए साझा कर रहा हूँ। \n \n 2. मैं समझता हूँ कि प्रदेश सरकार आधार युक्त डेटाबेस तैयार करेगी जिसमें मेरा आधार नंबर, फोटोग्राफ और सभी या इनमें से किसी के लिए जिसमें जनसांख्यिकीय जानकारी शामिल है जो इस प्रपत्र के पैराग्राफ  1(i)-(iii) में सूचीबद्ध है साथ ही इस उद्देश्य से मेरे द्वारा दी गयी जानकारी की  सुरक्षा और गोपनीयता सुनिश्चित करने के लिए आवश्यक तंत्र स्थापित किए गए हैं और भारत सरकार लागू कानूनों, नियमों और विनियमों के अनुसार ये सुनिश्चित करें। \n \n 3. पैराग्राफ  1(i)-(iii) में सूचीबद्ध उद्देश्यों के लिए, आधार आधारित प्रमाणीकरण के लिए और इस फॉर्म के पैराग्राफ 2 में वर्णित आधार युक्त डेटाबेस के निर्माण के लिए मुझे अपना आधार नंबर, फोटोग्राफ और जनसांख्यिकीय जानकारी प्रदान करने में कोई आपत्ति नहीं है। \n \n 4. मैं ये समझता हूँ कि मेरे द्वारा दी गयी अनापत्ति से मैं भविष्य में पत्राचार के माध्यम से बाहर निकलने और इसे वापस लेने का अधिकार रखता हूँ।";


    // For Service Calls
    public static ResponsePojoGet createOfflineObject(String url, String requestParams, String response, String Code, String functionName) {
        ResponsePojoGet pojo = new ResponsePojoGet();
        pojo.setUrl(url);
        pojo.setRequestParams(requestParams);
        pojo.setResponse(response);
        pojo.setFunctionName(functionName);
        pojo.setResponseCode(Code);
        return pojo;
    }


    // For Face Auth
    public static FaceAuthObjectResponse createObjectFaceResponse(String url, String AadhaarNo, String response, String Code, String functionName) {
        FaceAuthObjectResponse pojo = new FaceAuthObjectResponse();
        pojo.setUrl(url);
        pojo.setAadhaarNo(AadhaarNo);
        pojo.setResponse(response);
        pojo.setFunctionName(functionName);
        pojo.setResponseCode(Code);
        return pojo;
    }


    // For Displaying Auth Photo
    public static String saveBase64ImageToFile(String base64Image, String directoryPath, String fileName , Activity activity) {
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);

        // Create a Bitmap from the byte array
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

        // Get the directory path
        File directory;
        if (isExternalStorageWritable()) {
            directory = new File(activity.getApplicationContext().getExternalCacheDir(), directoryPath);
        } else {
            directory = new File(activity.getApplicationContext().getCacheDir(), directoryPath);
        }

        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
        }

        String filePath = directory.getAbsolutePath() + File.separator + fileName+".jpg";
        File imageFile = new File(filePath);

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // Compress and save the bitmap as a JPEG file
            fos.close();

            return imageFile.getAbsolutePath(); // Return the absolute path of the saved image
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // Return null if image saving failed
    }
    // Helper Method
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }



}

