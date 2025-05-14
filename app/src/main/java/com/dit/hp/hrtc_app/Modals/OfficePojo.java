package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class OfficePojo implements Serializable {

        private int officeId;
        private String officeName;

        public int getOfficeId() {
                return officeId;
        }

        public void setOfficeId(int officeId) {
                this.officeId = officeId;
        }

        public String getOfficeName() {
                return officeName;
        }

        public void setOfficeName(String officeName) {
                this.officeName = officeName;
        }

        @Override
        public String toString() {
                return "OfficePojo{" +
                        "officeId=" + officeId +
                        ", officeName='" + officeName + '\'' +
                        '}';
        }
}

