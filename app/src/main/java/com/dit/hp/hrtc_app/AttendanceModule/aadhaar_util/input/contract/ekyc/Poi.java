package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.input.contract.ekyc;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

class Poi {
    @JacksonXmlProperty(isAttribute = true)
    private String dob;
    @JacksonXmlProperty(isAttribute = true)
    private String e;
    @JacksonXmlProperty(isAttribute = true)
    private String gender;
    @JacksonXmlProperty(isAttribute = true)
    private String m;
    @JacksonXmlProperty(isAttribute = true)
    private String name;

    public String getName() {
        return name;
    }
}
