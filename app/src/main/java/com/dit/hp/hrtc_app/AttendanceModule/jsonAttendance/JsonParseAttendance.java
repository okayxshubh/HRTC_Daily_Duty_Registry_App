package com.dit.hp.hrtc_app.AttendanceModule.jsonAttendance;

import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.FaceAuthObjectResponse;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.kyc.KycResData;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.kyc.Poa;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.kyc.Poi;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.kyc.UidData;
import com.dit.hp.hrtc_app.utilities.Econstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class JsonParseAttendance {

    public static FaceAuthObjectResponse createObjectFaceResponse(String url, String AadhaarNo, String response, String Code, String functionName) {
        FaceAuthObjectResponse pojo = new FaceAuthObjectResponse();
        pojo.setUrl(url);
        pojo.setAadhaarNo(AadhaarNo);
        pojo.setResponse(response);
        pojo.setFunctionName(functionName);
        pojo.setResponseCode(Code);
        return pojo;
    }

    public static KycResData parseKYCResponse(String xmlString) {
        KycResData otpResponse = new KycResData();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));

            Element root = document.getDocumentElement();
            root.normalize();

            otpResponse.setTxn(root.getAttribute("txn"));
            otpResponse.setCode(root.getAttribute("code"));
            otpResponse.setTs(root.getAttribute("ts"));
            otpResponse.setRet(root.getAttribute("ret"));

            Element uidDataElement = (Element) root.getElementsByTagName("UidData").item(0);
            if (uidDataElement != null) {

                UidData uidData = new UidData();
                String uid = uidDataElement.getAttribute("uid");
                uidData.setUid(uid);



                Element poiElement = (Element) uidDataElement.getElementsByTagName("Poi").item(0);
                if (poiElement != null) {
                    String name = poiElement.getAttribute("name");
                    String dob = poiElement.getAttribute("dob");
                    String gender = poiElement.getAttribute("gender");

                    Poi poi = new Poi();
                    poi.setGender(gender);
                    poi.setDob(dob);
                    poi.setName(name);

                    uidData.setPoi(poi);

                }

                Element phtElement = (Element) uidDataElement.getElementsByTagName("Pht").item(0);
                if (phtElement != null) {
                    String phtValue = phtElement.getTextContent();
                    uidData.setPht(phtValue);

                }
                otpResponse.setUidData(uidData);

                Element poaElement = (Element) uidDataElement.getElementsByTagName("Poa").item(0);
                if (poaElement != null) {
                    String co = poaElement.getAttribute("co");
                    String house = poaElement.getAttribute("house");
                    String loc = poaElement.getAttribute("loc");
                    String vtc = poaElement.getAttribute("vtc");
                    String subDistrict = poaElement.getAttribute("subdist");
                    String dist = poaElement.getAttribute("dist");
                    String state = poaElement.getAttribute("state");
                    String pc = poaElement.getAttribute("pc");
                    String po = poaElement.getAttribute("po");

                    Poa poa = new Poa();


                    if(Econstants.isNotEmpty(co)){
                        poa.setCo(co);
                    }

                    if(Econstants.isNotEmpty(house)){
                        poa.setHouse(house);
                    }

                    if(Econstants.isNotEmpty(loc)){
                        poa.setLoc(loc);
                    }

                    if(Econstants.isNotEmpty(vtc)){
                        poa.setVtc(vtc);
                    }

                    if(Econstants.isNotEmpty(dist)){
                        poa.setDist(dist);
                    }

                    if(Econstants.isNotEmpty(state)){
                        poa.setState(state);
                    }

                    if(Econstants.isNotEmpty(pc)){
                        poa.setPc(pc);
                    }

                    if(Econstants.isNotEmpty(po)){
                        poa.setPo(po);
                    }

                    if(Econstants.isNotEmpty(subDistrict)){
                        poa.setSubdist(subDistrict);
                    }

                    uidData.setPoa(poa);

                    System.out.println(otpResponse.getUidData().getPoa().toString());



                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return otpResponse;
    }



}