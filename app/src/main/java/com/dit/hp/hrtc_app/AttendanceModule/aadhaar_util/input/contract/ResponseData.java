package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.input.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;

public class ResponseData {
    @JsonProperty("@Skey")
    private Skey skey;

    @JsonProperty("@Hmac")
    private String hmac;

    @JsonProperty("@Data")
    private Data data;

    // Getters and Setters
    public Skey getSkey() {
        return skey;
    }

    public void setSkey(Skey skey) {
        this.skey = skey;
    }

    public String getHmac() {
        return hmac;
    }

    public void setHmac(String hmac) {
        this.hmac = hmac;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    // Companion methods for XML and JSON deserialization
    public static ResponseData fromXML(String ekycXML) throws Exception {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return xmlMapper.readValue(ekycXML, ResponseData.class);
    }

    public static ResponseData fromJson(String inputJson) {
        return new Gson().fromJson(inputJson, ResponseData.class);
    }
}