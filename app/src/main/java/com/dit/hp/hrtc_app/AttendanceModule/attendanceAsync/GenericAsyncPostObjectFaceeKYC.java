package com.dit.hp.hrtc_app.AttendanceModule.attendanceAsync;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.input.contract.CaptureResponse;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.FaceAuthObjectRequest;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.FaceAuthObjectResponse;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.utils.Utils;
import com.dit.hp.hrtc_app.AttendanceModule.attendanceInterfaces.FaceEKYCInterface;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.network.HttpManager;
import com.thoughtworks.xstream.core.util.Base64Encoder;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kush.Dhawan
 * @project HPePass
 * @Time 03, 05 , 2020
 */
public class GenericAsyncPostObjectFaceeKYC extends AsyncTask<FaceAuthObjectRequest,Void , FaceAuthObjectResponse> {

    ProgressDialog dialog;
    Context context;
    FaceEKYCInterface taskListener_;
    TaskType taskType;
    private ProgressDialog mProgressDialog;

    public GenericAsyncPostObjectFaceeKYC(Context context, FaceEKYCInterface taskListener, TaskType taskType){
        this.context = context;
        this.taskListener_ = taskListener;
        this.taskType = taskType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(context, "Loading", "Connecting to Server .. Please Wait", true);
        dialog.setCancelable(false);
    }

    @Override
    protected FaceAuthObjectResponse doInBackground(FaceAuthObjectRequest... uploadObjects) {
        FaceAuthObjectRequest data = null;
        data = uploadObjects[0];
        HttpManager http_manager = null;
        FaceAuthObjectResponse Data_From_Server = null;
        boolean save = false;

        try{
            http_manager = new HttpManager();

            if(TaskType.FACE_AUTH.toString().equalsIgnoreCase(data.getTasktype().toString())){

                /**
                 * Process the Packet Here
                 */
                HashMap<String, String> authData = null;

                if(data.getSubAuaCode()!=null){
                     authData = createAuthData(data.getAadhaarNumber(),data.getSubAuaCode().trim(),data.getAuaCode().trim(), data.getAppId().trim());
                }else{
                    authData = createAuthData(data.getAadhaarNumber(),"PDPRH24063","0000540000" , "");
                }


                HashMap<String, String> usesData = createUsesData();
                HashMap<String, String> metaData = createDeviceData(data.getIntentPIDXML());
                HashMap<String, String> skeyData = createSkeyData(data.getIntentPIDXML());
                HashMap<String, String> dataAttributes = createData(data.getIntentPIDXML());
                String authRequestXml = generateAuthRequestXml(authData, usesData, metaData, skeyData, dataAttributes ,data.getIntentPIDXML() );
                data.setAuthXML(authRequestXml);
                String ekycKycRequest = getKycRequest(authRequestXml);
                data.setEkycXML(ekycKycRequest);

                Data_From_Server = http_manager.eKYCViaFaceAuth(data);

                /**
                 * Update the Object Parse
                 */

//                Data_From_Server = new FaceAuthObjectResponse();
//                Data_From_Server.setAadhaarNo(data.getAadhaarNumber());
//                Data_From_Server.seteKYCXML(data.getEkycXML());
//                Data_From_Server.setFunctionName(data.getMethordName());
//                Data_From_Server.setAuthXml(data.getAuthXML());
//                Data_From_Server.setAuthXml(data.getAuthXML());
//                Data_From_Server.setUrl(data.getUrl());
//                Data_From_Server.setCaptureResponse(data.getIntentPIDXML());

                return Data_From_Server;

            }

        }catch(Exception e){
            return Data_From_Server;
        }
        return null;
    }




    private HashMap<String, String> createAuthData(String aadhaarNumber, String sa, String ac, String appId) {
        HashMap<String, String> authData = new HashMap<>();
        authData.put("xmlns", "http://www.uidai.gov.in/authentication/uid-auth-request/2.0");
        authData.put("uid", aadhaarNumber);
        authData.put("rc", "Y");
        authData.put("tid", "registered");
        authData.put("sa", sa);
        authData.put("ac", ac);

        if(appId!=null && !appId.isEmpty()){
            authData.put("appId", appId);
        }else{
            authData.put("appId", "HP_SDPRH_PROD");
        }

        authData.put("ver", "2.5");
        authData.put("txn", "UKC:" + Utils.createTxn(Utils.getCurrentTimestamp()));
        authData.put("lk", "MJrDZPkQptyKbeUf4UwSN0UWucqzKzS6XieVezKqj5_IYZZRBsI0puo");
        return authData;
    }

    private HashMap<String, String> createUsesData() {
        HashMap<String, String> usesData = new HashMap<>();
        usesData.put("pi", "n");
        usesData.put("pa", "n");
        usesData.put("pfa", "n");
        usesData.put("bio", "y");
        usesData.put("bt", "FID");
        usesData.put("otp", "n");
        usesData.put("pin", "n");
        return usesData;
    }

    private HashMap<String, String> createDeviceData(CaptureResponse data) {
        HashMap<String, String> deviceData = new HashMap<>();
        deviceData.put("rdsId", data.deviceInfo.rdsId);
        deviceData.put("rdsVer", data.deviceInfo.rdsVer);
        deviceData.put("dpId", data.deviceInfo.dpId);
        deviceData.put("dc", data.deviceInfo.dc);
        deviceData.put("mi", data.deviceInfo.mi);
        deviceData.put("mc", data.deviceInfo.mc);
        return deviceData;
    }

    private HashMap<String, String> createSkeyData(CaptureResponse data) {
        HashMap<String, String> skeyData = new HashMap<>();
        skeyData.put("ci", data.skey.ci);
        return skeyData;
    }

    private HashMap<String, String> createData(CaptureResponse data) {
        HashMap<String, String> dataAttributes = new HashMap<>();
        dataAttributes.put("type", data.data.type);
        return dataAttributes;
    }

    private void appendAttributes(StringBuilder sb, HashMap<String, String> attributes) {
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=\"");
            sb.append(entry.getValue());
            sb.append("\" ");
        }
    }

    private String generateAuthRequestXml(HashMap<String, String> authData,
                                          HashMap<String, String> usesData,
                                          HashMap<String, String> metaData,
                                          HashMap<String, String> skeyData,
                                          HashMap<String, String> dataAttributes,
                                          CaptureResponse data) {
        StringBuilder sb = new StringBuilder();

        try {
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            sb.append("<Auth ");
            appendAttributes(sb, authData);
            sb.append(">");

            sb.append("<Uses ");
            appendAttributes(sb, usesData);
            sb.append("/>");

            sb.append("<Meta ");
            appendAttributes(sb, metaData);
            sb.append("/>");

            sb.append("<Skey ");
            appendAttributes(sb, skeyData);
            sb.append(">");
            sb.append(data.skey.value);
            sb.append("</Skey>");

            sb.append("<Data ");
            appendAttributes(sb, dataAttributes);
            sb.append(">");
            sb.append(data.data.value);
            sb.append("</Data>");

            sb.append("<Hmac>");
            sb.append(data.hmac);
            sb.append("</Hmac>");
            sb.append("<Demo lang=\"06\" ></Demo>");
            sb.append("</Auth>");

        } catch (Exception e) {
            Log.e("HPUtil", "Exception while generating XML: " + e.getLocalizedMessage());
        }

        return sb.toString();
    }

    private String getKycRequest(String authRequestXml) {

        StringBuilder sb = new StringBuilder();

        try {
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            sb.append("<Kyc ");
            sb.append("ver=\"2.5\" ");
            sb.append("ra=\"P\" ");
            sb.append("rc=\"Y\" ");
            sb.append("lr=\"Y\" ");
            sb.append("de=\"N\" ");
            sb.append("pfr=\"N\" ");
            sb.append("appId=\"HP_SDPRH_PROD\" ");
            sb.append(">");
            sb.append("<Rad>");
            sb.append(new Base64Encoder().encode(authRequestXml.getBytes(StandardCharsets.UTF_8)));
            sb.append("</Rad>");
            sb.append("</Kyc>");
        } catch (Exception e) {
            Log.e("HPUtil", "Exception while preparing KYC request: " + e.getLocalizedMessage());
        }

        return sb.toString();
    }


    @Override
    protected void onPostExecute(FaceAuthObjectResponse result) {
        super.onPostExecute(result);

        try {
            taskListener_.onTaskCompleted(result, taskType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.dismiss();
    }
}
