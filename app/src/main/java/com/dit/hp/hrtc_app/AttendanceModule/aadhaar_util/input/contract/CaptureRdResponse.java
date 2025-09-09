package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.input.contract;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CaptureRdResponse {
    private String txnId;
    private int bestImageIndex;
    private PidData pidData;

    public String getTxnId() {
        return txnId;
    }

    public int getBestImageIndex() {
        return bestImageIndex;
    }

    public PidData getPidData() {
        return pidData;
    }

    public static CaptureRdResponse fromJson(String rdResponse) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson.fromJson(rdResponse, CaptureRdResponse.class);
    }


}

