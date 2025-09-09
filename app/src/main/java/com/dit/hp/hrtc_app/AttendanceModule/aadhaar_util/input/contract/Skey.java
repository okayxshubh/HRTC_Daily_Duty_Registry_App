package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.input.contract;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class Skey {
    @JacksonXmlProperty(isAttribute = true)
    public String ci;
    @JacksonXmlText
    public String value;
}
