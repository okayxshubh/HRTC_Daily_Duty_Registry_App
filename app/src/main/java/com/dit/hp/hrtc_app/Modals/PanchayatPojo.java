package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class PanchayatPojo implements Serializable {

        private int panchayatId;
        private String panchayatName;
        private String panchayatLgdCode;

        public String getPanchayatLgdCode() {
                return panchayatLgdCode;
        }

        public void setPanchayatLgdCode(String panchayatLgdCode) {
                this.panchayatLgdCode = panchayatLgdCode;
        }

        public int getPanchayatId() {
                return panchayatId;
        }

        public void setPanchayatId(int panchayatId) {
                this.panchayatId = panchayatId;
        }

        public String getPanchayatName() {
                return panchayatName;
        }

        public void setPanchayatName(String panchayatName) {
                this.panchayatName = panchayatName;
        }

        @Override
        public String toString() {
                return panchayatName;
        }
}

