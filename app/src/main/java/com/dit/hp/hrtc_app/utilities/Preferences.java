

package com.dit.hp.hrtc_app.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    // Singleton Instance of This Class
    private static Preferences instance;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    // Just like an Econstant Name
    private final String preferenceName = "com.dit.hp.hrtc_app";

    // Key Names
    private final String KEY_USER_NAME = "userName";
    private final String KEY_ROLE_ID = "roleId";
    private final String KEY_APP_ROLE_ID = "appRoleId";
    private final String KEY_ROLE_NAME = "roleName";
    private final String KEY_DEPOT_ID = "depotId";
    private final String KEY_DEPOT_NAME = "depotName";
    private final String KEY_USER_EMAIL = "emailID";
    private final String KEY_EMP_ID = "empId";
    private final String KEY_DEPARTMENT_ID = "departmentId";
    private final String KEY_TOKEN = "token";
    private final String KEY_TOKEN_HIMACCESS = "tokenHimAccess";

    // Instance Variables
    public Integer empId;
    public Integer departmentId;
    public String depotName = null;
    public String emailID = null;
    public Integer roleId;
    public Integer appRoleId;
    public String roleName = null;
    public Integer depotId;
    public String userName = null;
    public String token = null;
    public String tokenHimAccess = null;

    // Singleton Constructor
    private Preferences() {
    }

    // Singleton Instance
    public synchronized static Preferences getInstance() {
        if (instance == null) {
            instance = new Preferences();
        }
        return instance;
    }

    // Load Preferences
    public void loadPreferences(Context context) {
        if (context == null) return;

        preferences = context.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE);

        // Load values and handle `-1` as `null` for integers
        int empIdValue = preferences.getInt(KEY_EMP_ID, -1);
        empId = (empIdValue == -1) ? null : empIdValue;

        int departmentIdValue = preferences.getInt(KEY_DEPARTMENT_ID, -1);
        departmentId = (departmentIdValue == -1) ? null : departmentIdValue;

        depotName = preferences.getString(KEY_DEPOT_NAME, null);
        emailID = preferences.getString(KEY_USER_EMAIL, null);

        int roleIdValue = preferences.getInt(KEY_ROLE_ID, -1);
        roleId = (roleIdValue == -1) ? null : roleIdValue;

        int appRoleIdValue = preferences.getInt(KEY_APP_ROLE_ID, -1);
        appRoleId = (appRoleIdValue == -1) ? null : appRoleIdValue;

        roleName = preferences.getString(KEY_ROLE_NAME, null);

        int depotIdValue = preferences.getInt(KEY_DEPOT_ID, -1);
        depotId = (depotIdValue == -1) ? null : depotIdValue;

        userName = preferences.getString(KEY_USER_NAME, null);

        token = preferences.getString(KEY_TOKEN, null);

        tokenHimAccess = preferences.getString(KEY_TOKEN_HIMACCESS, null);
    }


    // Save Preferences
    public void savePreferences(Context context) {
        if (context == null) return;

        preferences = context.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE);
        editor = preferences.edit();

        // Save integer values and handle null
        editor.putInt(KEY_EMP_ID, empId == null ? -1 : empId);
        editor.putInt(KEY_DEPARTMENT_ID, departmentId == null ? -1 : departmentId);
        editor.putString(KEY_DEPOT_NAME, depotName);
        editor.putString(KEY_USER_EMAIL, emailID);
        editor.putInt(KEY_ROLE_ID, roleId == null ? -1 : roleId);
        editor.putInt(KEY_APP_ROLE_ID, appRoleId == null ? -1 : appRoleId);
        editor.putString(KEY_ROLE_NAME, roleName);
        editor.putInt(KEY_DEPOT_ID, depotId == null ? -1 : depotId);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_TOKEN_HIMACCESS, tokenHimAccess);

        editor.apply(); // Apply changes asynchronously
    }
}
