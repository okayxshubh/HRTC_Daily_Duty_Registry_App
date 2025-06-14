package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.input.contract;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdditionalInfo {
    @SerializedName("info")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Info")
    public List<NameValue> nameValues;
}
