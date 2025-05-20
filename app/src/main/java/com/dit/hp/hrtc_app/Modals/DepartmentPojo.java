package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class DepartmentPojo implements Serializable {

        private int departmentId;
        private String departmentName;
        private String departmentCode;

        public int getDepartmentId() {
                return departmentId;
        }

        public void setDepartmentId(int departmentId) {
                this.departmentId = departmentId;
        }

        public String getDepartmentName() {
                return departmentName;
        }

        public void setDepartmentName(String departmentName) {
                this.departmentName = departmentName;
        }

        public String getDepartmentCode() {
                return departmentCode;
        }

        public void setDepartmentCode(String departmentCode) {
                this.departmentCode = departmentCode;
        }

        @Override
        public String toString() {
                return departmentName;
        }
}

