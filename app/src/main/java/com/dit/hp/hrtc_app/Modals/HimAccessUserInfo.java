package com.dit.hp.hrtc_app.Modals;


import java.io.Serializable;
import java.util.List;

public class HimAccessUserInfo implements Serializable {


    private EmployeePojo employeePojo;

    private int applicationId;
    private String applicationName;

    private int serviceId;
    private String serviceName;
    private Integer appRoleId;

    private OrganisationPojo organisationPojo;

    private DepartmentPojo mainDepartmentPojo;

    private DesignationPojo mainDesignationPojo;

    private OfficeLevel mainOfficeLevelPojo;

    private OfficePojo mainOffice;

    private Integer roleId;
    private String roleName;

    private List<AdditonalChargePojo> additionalChargeDetailDTO;

    private List<Object> ddoOfficeDetails; // unknown for noww



    public EmployeePojo getEmployeePojo() {
        return employeePojo;
    }

    public void setEmployeePojo(EmployeePojo employeePojo) {
        this.employeePojo = employeePojo;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Integer getAppRoleId() {
        return appRoleId;
    }

    public void setAppRoleId(Integer appRoleId) {
        this.appRoleId = appRoleId;
    }

    public OrganisationPojo getOrganisationPojo() {
        return organisationPojo;
    }

    public void setOrganisationPojo(OrganisationPojo organisationPojo) {
        this.organisationPojo = organisationPojo;
    }

    public DepartmentPojo getMainDepartmentPojo() {
        return mainDepartmentPojo;
    }

    public void setMainDepartmentPojo(DepartmentPojo mainDepartmentPojo) {
        this.mainDepartmentPojo = mainDepartmentPojo;
    }

    public DesignationPojo getMainDesignationPojo() {
        return mainDesignationPojo;
    }

    public void setMainDesignationPojo(DesignationPojo mainDesignationPojo) {
        this.mainDesignationPojo = mainDesignationPojo;
    }

    public OfficeLevel getMainOfficeLevelPojo() {
        return mainOfficeLevelPojo;
    }

    public void setMainOfficeLevelPojo(OfficeLevel mainOfficeLevelPojo) {
        this.mainOfficeLevelPojo = mainOfficeLevelPojo;
    }

    public OfficePojo getMainOffice() {
        return mainOffice;
    }

    public void setMainOffice(OfficePojo mainOffice) {
        this.mainOffice = mainOffice;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<AdditonalChargePojo> getAdditionalChargeDetailDTO() {
        return additionalChargeDetailDTO;
    }

    public void setAdditionalChargeDetailDTO(List<AdditonalChargePojo> additionalChargeDetailDTO) {
        this.additionalChargeDetailDTO = additionalChargeDetailDTO;
    }

    public List<Object> getDdoOfficeDetails() {
        return ddoOfficeDetails;
    }

    public void setDdoOfficeDetails(List<Object> ddoOfficeDetails) {
        this.ddoOfficeDetails = ddoOfficeDetails;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "employeePojo=" + employeePojo +
                ", applicationId=" + applicationId +
                ", applicationName='" + applicationName + '\'' +
                ", serviceId=" + serviceId +
                ", serviceName='" + serviceName + '\'' +
                ", appRoleId=" + appRoleId +
                ", organisationPojo=" + organisationPojo +
                ", mainDepartmentPojo=" + mainDepartmentPojo +
                ", mainDesignationPojo=" + mainDesignationPojo +
                ", mainOfficeTypePojo=" + mainOfficeLevelPojo +
                ", mainOffice=" + mainOffice +
                ", roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", additionalChargeDetailDTO=" + additionalChargeDetailDTO +
                ", ddoOfficeDetails=" + ddoOfficeDetails +
                '}';
    }

}

