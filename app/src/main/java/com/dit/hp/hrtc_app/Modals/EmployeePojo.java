package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class EmployeePojo implements Serializable {

        private int empId;
        private String employeeName;
        private String pmisCode;
        private String salaryCode;
        private String cadre;
        private String emailId;


        public String getEmailId() {
                return emailId;
        }

        public void setEmailId(String emailId) {
                this.emailId = emailId;
        }

        public int getEmpId() {
                return empId;
        }

        public void setEmpId(int empId) {
                this.empId = empId;
        }

        public String getEmployeeName() {
                return employeeName;
        }

        public void setEmployeeName(String employeeName) {
                this.employeeName = employeeName;
        }

        public String getPmisCode() {
                return pmisCode;
        }

        public void setPmisCode(String pmisCode) {
                this.pmisCode = pmisCode;
        }

        public String getSalaryCode() {
                return salaryCode;
        }

        public void setSalaryCode(String salaryCode) {
                this.salaryCode = salaryCode;
        }

        public String getCadre() {
                return cadre;
        }

        public void setCadre(String cadre) {
                this.cadre = cadre;
        }

        @Override
        public String toString() {
                return "EmployeePojo{" +
                        "empId=" + empId +
                        ", employeeName='" + employeeName + '\'' +
                        ", pmisCode='" + pmisCode + '\'' +
                        ", salaryCode='" + salaryCode + '\'' +
                        ", cadre='" + cadre + '\'' +
                        ", emailId='" + emailId + '\'' +
                        '}';
        }
}

