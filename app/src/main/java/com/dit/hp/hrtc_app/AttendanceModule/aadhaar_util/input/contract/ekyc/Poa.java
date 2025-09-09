package com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.input.contract.ekyc;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Poa {
    public String getCareof() {
        return careof;
    }

    public String getCountry() {
        return country;
    }

    public String getDist() {
        return dist;
    }

    public String getHouse() {
        return house;
    }

    public String getLandmark() {
        return landmark;
    }

    public String getLoc() {
        return loc;
    }

    public String getPc() {
        return pc;
    }

    public String getPo() {
        return po;
    }

    public String getState() {
        return state;
    }

    public String getStreet() {
        return street;
    }

    public String getSubdist() {
        return subdist;
    }

    public String getVtc() {
        return vtc;
    }

    @JacksonXmlProperty(isAttribute = true)
    private String careof;
    @JacksonXmlProperty(isAttribute = true)
    private String country;
    @JacksonXmlProperty(isAttribute = true)
    private String dist;
    @JacksonXmlProperty(isAttribute = true)
    private String house;
    @JacksonXmlProperty(isAttribute = true)
    private String landmark;
    @JacksonXmlProperty(isAttribute = true)
    private String loc;
    @JacksonXmlProperty(isAttribute = true)
    private String pc;
    @JacksonXmlProperty(isAttribute = true)
    private String po;
    @JacksonXmlProperty(isAttribute = true)
    private String state;
    @JacksonXmlProperty(isAttribute = true)
    private String street;
    @JacksonXmlProperty(isAttribute = true)
    private String subdist;
    @JacksonXmlProperty(isAttribute = true)
    private String vtc;

}
