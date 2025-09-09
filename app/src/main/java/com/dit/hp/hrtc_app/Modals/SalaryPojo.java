package com.dit.hp.hrtc_app.Modals;

import android.content.Context;
import android.util.Log;

import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;

import org.json.JSONObject;

import java.io.Serializable;

public class SalaryPojo implements Serializable {

    private String month;
    private Double deductions;
    private Double netSalary;
    private String status;

    public SalaryPojo() {
    }

    public SalaryPojo(String month, Double deductions, Double netSalary, String status) {
        this.month = month;
        this.deductions = deductions;
        this.netSalary = netSalary;
        this.status = status;
    }



    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Double getDeductions() {
        return deductions;
    }

    public void setDeductions(Double deductions) {
        this.deductions = deductions;
    }

    public Double getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(Double netSalary) {
        this.netSalary = netSalary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}

