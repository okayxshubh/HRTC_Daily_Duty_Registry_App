package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class OfficeTypePojo implements Serializable {

        private int officeTypeId;
        private String officeTypeName;

        public int getOfficeTypeId() {
                return officeTypeId;
        }

        public void setOfficeTypeId(int officeTypeId) {
                this.officeTypeId = officeTypeId;
        }

        public String getOfficeTypeName() {
                return officeTypeName;
        }

        public void setOfficeTypeName(String officeTypeName) {
                this.officeTypeName = officeTypeName;
        }

        @Override
        public String toString() {
                return "OfficeTypePojo{" +
                        "officeTypeId=" + officeTypeId +
                        ", officeTypeName='" + officeTypeName + '\'' +
                        '}';
        }
}

