package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.constants;

import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.FaceAuthObjectResponse;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.utils.Base64New;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class FaceAuthConstants {


    public static final String FACE_RD_PACKAGE_NAME = "in.gov.uidai.rdservice.face";

    public static final String CAPTURE_INTENT= "in.gov.uidai.rdservice.face.CAPTURE";
    public static final String DEVICE_CHECK_INTENT = "in.gov.uidai.rdservice.face.CHECK_DEVICE";
    public static final String CAPTURE_INTENT_REQUEST = "request";
    public static final String CAPTURE_INTENT_RESULT = "in.gov.uidai.rdservice.face.CAPTURE_RESULT";
    public static final String CAPTURE_INTENT_RESPONSE_DATA = "response";
    public static final String DEVICE_CHECK_INTENT_RESULT = "in.gov.uidai.rdservice.face.CHECK_DEVICE_RESULT";
    public static final String WADH_KEY = "sgydIC09zzy6f8Lb3xaAqzKquKe9lFcNR9uTvYxFp+A=";


    public static final boolean DEBUG = Boolean.parseBoolean("true");
    public static final String APPLICATION_ID = "in.gov.uidai.auasample";
    public static final String BUILD_TYPE = "debug";
    public static final int VERSION_CODE = 1;
    public static final String VERSION_NAME = "1.26";


    /**
     *  wadh = SHA-256(ts+ver+ts+ra+rc+lr+de+pfr)  2.5PYYNN      version = 2.5,ra = P,rc = Y,lang = Y,de = N,pfr = N
     * @return
     * @throws NoSuchProviderException
     */

    public static String generateWadh() throws NoSuchProviderException, NoSuchAlgorithmException {

        byte[] hash = null;
        MessageDigest digest;
        digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        hash = digest.digest("2.5PYYNN".getBytes());
        String out = Base64New.encode(hash);
        System.out.println("Through Kush class--: " + out);
        return out;
    }


    public static FaceAuthObjectResponse createObjectFaceResponse(String url, String AadhaarNo, String response, String Code, String functionName) {
        FaceAuthObjectResponse pojo = new FaceAuthObjectResponse();
        pojo.setUrl(url);
        pojo.setAadhaarNo(AadhaarNo);
        pojo.setResponse(response);
        pojo.setFunctionName(functionName);
        pojo.setResponseCode(Code);
        return pojo;
    }




}
