package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class VillagePojo implements Serializable {

        private int villageId;
        private String village;

        public int getVillageId() {
                return villageId;
        }

        public void setVillageId(int villageId) {
                this.villageId = villageId;
        }

        public String getVillage() {
                return village;
        }

        public void setVillage(String village) {
                this.village = village;
        }

        @Override
        public String toString() {
                return village;
        }
}

