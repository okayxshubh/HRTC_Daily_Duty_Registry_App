package com.dit.hp.hrtc_app.Modals;


import java.io.Serializable;

public class User implements Serializable {

    private int empId;
    private String depotName;
    private int roleId;
    private String roleName;
    private int depotId;
    private String userName;
    private String token;

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getDepotName() {
        return depotName;
    }

    public void setDepotName(String depotName) {
        this.depotName = depotName;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getId() {
        return depotId;
    }

    public void setDepotId(Integer depotId) {
        this.depotId = depotId;
    }

    public String getuserName() {
        return userName;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User{" +
                "empId='" + empId + '\'' +
                ", depotName='" + depotName + '\'' +
                ", roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", depotId='" + depotId + '\'' +
                ", userName='" + userName + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}

