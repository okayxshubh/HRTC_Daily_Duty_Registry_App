package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class OrganisationPojo implements Serializable {

        private int organisationId;
        private String organisationName;

        public int getOrganisationId() {
                return organisationId;
        }

        public void setOrganisationId(int organisationId) {
                this.organisationId = organisationId;
        }

        public String getOrganisationName() {
                return organisationName;
        }

        public void setOrganisationName(String organisationName) {
                this.organisationName = organisationName;
        }

        @Override
        public String toString() {
                return "OrganisationPojo{" +
                        "organisationId=" + organisationId +
                        ", organisationName='" + organisationName + '\'' +
                        '}';
        }
}

