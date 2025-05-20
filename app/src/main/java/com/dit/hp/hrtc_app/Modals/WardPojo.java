package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class WardPojo implements Serializable {

        private int wardId;
        private String wardName;

        public int getWardId() {
                return wardId;
        }

        public void setWardId(int wardId) {
                this.wardId = wardId;
        }

        public String getWardName() {
                return wardName;
        }

        public void setWardName(String wardName) {
                this.wardName = wardName;
        }

        @Override
        public String toString() {
                return wardName;
        }
}

