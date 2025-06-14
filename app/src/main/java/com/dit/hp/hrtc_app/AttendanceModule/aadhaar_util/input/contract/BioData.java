package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.input.contract;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class BioData {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "PidData")
    private List<PidData> pidData;
}
