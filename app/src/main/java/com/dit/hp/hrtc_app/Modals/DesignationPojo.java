package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class DesignationPojo implements Serializable {

        private int designationId;
        private String designationName;
        private String designationCode;

        public DesignationPojo() {
        }

        public DesignationPojo(String designationName) {
                this.designationName = designationName;
        }

        public DesignationPojo(int designationId, String designationName, String designationCode) {
                this.designationId = designationId;
                this.designationName = designationName;
                this.designationCode = designationCode;
        }

        public String getDesignationCode() {
                return designationCode;
        }

        public void setDesignationCode(String designationCode) {
                this.designationCode = designationCode;
        }

        public int getDesignationId() {
                return designationId;
        }

        public void setDesignationId(int designationId) {
                this.designationId = designationId;
        }

        public String getDesignationName() {
                return designationName;
        }

        public void setDesignationName(String designationName) {
                this.designationName = designationName;
        }

        @Override
        public String toString() {
                return designationName;
        }
}

