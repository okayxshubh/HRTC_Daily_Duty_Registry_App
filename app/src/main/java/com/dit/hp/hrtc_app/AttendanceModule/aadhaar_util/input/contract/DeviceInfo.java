package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.input.contract;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.gson.annotations.SerializedName;

public class DeviceInfo {
    @JacksonXmlProperty(isAttribute = true)
    public String dpId;
    @JacksonXmlProperty(isAttribute = true)
    public String rdsId;
    @JacksonXmlProperty(isAttribute = true)
    public String rdsVer;
    @JacksonXmlProperty(isAttribute = true)
    public String dc;
    @JacksonXmlProperty(isAttribute = true)
    public String mi;
    @JacksonXmlProperty(isAttribute = true)
    public String mc;

    @SerializedName("additionalInfo")
    @JacksonXmlProperty(localName = "Additional_Info")
    public AdditionalInfo additional_info;
}
