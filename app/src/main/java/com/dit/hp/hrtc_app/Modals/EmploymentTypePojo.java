package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;
import java.math.BigInteger;

public class EmploymentTypePojo implements Serializable {

    BigInteger employmentTypeId;
    String employmentTypeName;

    public BigInteger getEmploymentTypeId() {
        return employmentTypeId;
    }

    public void setEmploymentTypeId(BigInteger employmentTypeId) {
        this.employmentTypeId = employmentTypeId;
    }

    public String getEmploymentTypeName() {
        return employmentTypeName;
    }

    public void setEmploymentTypeName(String employmentTypeName) {
        this.employmentTypeName = employmentTypeName;
    }

    @Override
    public String toString() {
        return employmentTypeName;
    }
}
