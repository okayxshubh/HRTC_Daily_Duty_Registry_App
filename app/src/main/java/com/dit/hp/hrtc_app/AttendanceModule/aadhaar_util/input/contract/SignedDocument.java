package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.input.contract;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class SignedDocument {
    @JacksonXmlProperty(isAttribute = true)
    public String docType;

    @JacksonXmlProperty(isAttribute = true)
    public String auaCode;

    @JacksonXmlText
    public String value;

    public SignedDocument(String docType, String auaCode, String value) {
        this.docType = docType;
        this.auaCode = auaCode;
        this.value = value;
    }

    public SignedDocument() {
    }
}
