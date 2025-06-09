package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.input.contract;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "localFaceMatchRequest")
public class LocalFaceMatchRequest {
    @JacksonXmlProperty(isAttribute = true)
    public String requestId;

    @JacksonXmlProperty(isAttribute = true)
    public String language;

    @JacksonXmlProperty(isAttribute = true)
    public String enableAutoCapture;

    @JacksonXmlProperty(localName = "signedDocument1")
    public SignedDocument signedDocument1;

    @JacksonXmlProperty(localName = "signedDocument2")
    public SignedDocument signedDocument2;

    public String toXml() throws Exception {
        return new XmlMapper().writeValueAsString(this);
    }
}