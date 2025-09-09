package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class MunicipalPojo implements Serializable {

        private int municipalId;
        private String municipalName;
        private String municipalLgdCode;

        public String getMunicipalLgdCode() {
                return municipalLgdCode;
        }

        public void setMunicipalLgdCode(String municipalLgdCode) {
                this.municipalLgdCode = municipalLgdCode;
        }

        public int getMunicipalId() {
                return municipalId;
        }

        public void setMunicipalId(int municipalId) {
                this.municipalId = municipalId;
        }

        public String getMunicipalName() {
                return municipalName;
        }

        public void setMunicipalName(String municipalName) {
                this.municipalName = municipalName;
        }

        @Override
        public String toString() {
                return municipalName;
        }
}

