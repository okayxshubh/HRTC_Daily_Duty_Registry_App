package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class DepartmentPojo implements Serializable {

        private int departmentId;
        private String departmentName;

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

        @Override
        public String toString() {
                return "DepartmentPojo{" +
                        "departmentId=" + departmentId +
                        ", departmentName='" + departmentName + '\'' +
                        '}';
        }
}

