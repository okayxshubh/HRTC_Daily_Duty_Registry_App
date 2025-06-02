

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
    private final String KEY_APP_ROLE_ID = "appRoleId";
    private final String KEY_EMP_ID = "empId";

    private final String KEY_USER_NAME = "userName";
    private final String KEY_MOBILE_NUMBER = "mobileNumber";

    private final String KEY_ROLE_ID = "roleId";
    private final String KEY_ROLE_NAME = "roleName";

    private final String KEY_DEPOT_ID = "depotId";
    private final String KEY_DEPOT_NAME = "depotName";

    private final String KEY_REGIONAL_OFFICE_ID = "regionalOfficeId";
    private final String KEY_REGIONAL_OFFICE_NAME = "regionalOfficeName";

    private final String KEY_DEPARTMENT_ID = "departmentId";
    private final String KEY_DEPARTMENT_NAME = "departmentName";

    private final String KEY_OFFICE_TYPE_ID = "officeTypeId";
    private final String KEY_OFFICE_TYPE_NAME = "officeTypeName";

    private final String KEY_DESIGNATION_ID = "designationId";
    private final String KEY_DESIGNATION_NAME = "designationName";

    private final String KEY_ORG_ID = "ordId";
    private final String KEY_ORG_NAME = "orgName";

    private final String KEY_USER_EMAIL = "emailID";
    private final String KEY_DATE_OF_BIRTH = "dateOfBirth";

    private final String KEY_TOKEN = "token";
    private final String KEY_TOKEN_HIMACCESS = "tokenHimAccess";

    private final String KEY_DARK_MODE = "darkModeKey";



//    ###############################################################################################

    // Instance Variables
    public Boolean isDarkMode = false; // Default to false

    public Integer appRoleId;
    public Integer empId;

    public String userName = null;
    public String mobileNumber = null;

    public String dateOfBirth = null;

    public Integer roleId;
    public String roleName = null;

    public Integer depotId;
    public String depotName = null;

    public Integer regionalOfficeId;
    public String regionalOfficeName = null;

    public Integer orgId;
    public String orgName = null;

    public Integer departmentId;
    public String departmentName = null;

    public Integer officeTypeId;
    public String officeTypeName = null;

    public Integer designationId;
    public String designationName = null;

    public String emailID = null;

    public String token = null;
    public String tokenHimAccess = null;

//    ###############################################################################################


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


//    ###############################################################################################


    // Load Preferences
    public void loadPreferences(Context context) {
        if (context == null) return;

        preferences = context.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE);

        isDarkMode = preferences.getBoolean(KEY_DARK_MODE, false);

        // Load values and handle `-1` as `null` for integers
        int empIdValue = preferences.getInt(KEY_EMP_ID, -1);
        empId = (empIdValue == -1) ? null : empIdValue;

        int orgIdValue = preferences.getInt(KEY_ORG_ID, -1);
        orgId = (orgIdValue == -1) ? null : orgIdValue;
        orgName = preferences.getString(KEY_ORG_NAME, null);

        dateOfBirth = preferences.getString(KEY_DATE_OF_BIRTH, null);

        int departmentIdValue = preferences.getInt(KEY_DEPARTMENT_ID, -1);
        departmentId = (departmentIdValue == -1) ? null : departmentIdValue;
        departmentName = preferences.getString(KEY_DEPARTMENT_NAME, null);

        officeTypeId = preferences.getInt(KEY_OFFICE_TYPE_ID, -1);
        officeTypeName = preferences.getString(KEY_OFFICE_TYPE_NAME, null);

        designationId = preferences.getInt(KEY_DESIGNATION_ID, -1);
        designationName = preferences.getString(KEY_DESIGNATION_NAME, null);

        int regionalOfficeIdValue = preferences.getInt(KEY_REGIONAL_OFFICE_ID, -1);
        regionalOfficeId = (regionalOfficeIdValue == -1) ? null : regionalOfficeIdValue;
        regionalOfficeName = preferences.getString(KEY_REGIONAL_OFFICE_NAME, null);

        int depotIdValue = preferences.getInt(KEY_DEPOT_ID, -1);
        depotId = (depotIdValue == -1) ? null : depotIdValue;
        depotName = preferences.getString(KEY_DEPOT_NAME, null);

        int roleIdValue = preferences.getInt(KEY_ROLE_ID, -1);
        roleId = (roleIdValue == -1) ? null : roleIdValue;
        roleName = preferences.getString(KEY_ROLE_NAME, null);

        int appRoleIdValue = preferences.getInt(KEY_APP_ROLE_ID, -1);
        appRoleId = (appRoleIdValue == -1) ? null : appRoleIdValue;

        emailID = preferences.getString(KEY_USER_EMAIL, null);

        userName = preferences.getString(KEY_USER_NAME, null);
        mobileNumber = preferences.getString(KEY_MOBILE_NUMBER, null);

        token = preferences.getString(KEY_TOKEN, null);

        tokenHimAccess = preferences.getString(KEY_TOKEN_HIMACCESS, null);
    }


    // Save Preferences
    public void savePreferences(Context context) {
        if (context == null) return;

        preferences = context.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE);
        editor = preferences.edit();

        // Dark Mode
        editor.putBoolean(KEY_DARK_MODE, isDarkMode != null && isDarkMode);

        // Save integer values and handle null
        editor.putInt(KEY_EMP_ID, empId == null ? -1 : empId);
        editor.putInt(KEY_DEPARTMENT_ID, departmentId == null ? -1 : departmentId);
        editor.putString(KEY_USER_EMAIL, emailID);
        editor.putInt(KEY_ROLE_ID, roleId == null ? -1 : roleId);
        editor.putInt(KEY_APP_ROLE_ID, appRoleId == null ? -1 : appRoleId);
        editor.putString(KEY_ROLE_NAME, roleName);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_MOBILE_NUMBER, mobileNumber);
        editor.putString(KEY_DATE_OF_BIRTH, dateOfBirth);

        editor.putInt(KEY_OFFICE_TYPE_ID, officeTypeId == null ? -1 : officeTypeId);
        editor.putString(KEY_OFFICE_TYPE_NAME, officeTypeName);

        editor.putInt(KEY_DESIGNATION_ID, designationId == null ? -1 : designationId);
        editor.putString(KEY_DESIGNATION_NAME, designationName);

        editor.putString(KEY_DEPOT_NAME, depotName);
        editor.putInt(KEY_DEPOT_ID, depotId == null ? -1 : depotId);

        editor.putString(KEY_REGIONAL_OFFICE_NAME, regionalOfficeName);
        editor.putInt(KEY_REGIONAL_OFFICE_ID, regionalOfficeId == null ? -1 : regionalOfficeId);

        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_TOKEN_HIMACCESS, tokenHimAccess);

        editor.putInt(KEY_ORG_ID, orgId == null ? -1 : orgId);
        editor.putString(KEY_ORG_NAME, orgName);
        editor.putString(KEY_DEPARTMENT_NAME, departmentName);

        editor.apply(); // Apply changes asynchronously
    }


    // CLear All Prefs
    public void clearPreferences(Context context) {
        if (context == null) return;

        preferences = context.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE);
        editor = preferences.edit();
        editor.clear(); // Clear all keys
        editor.apply(); // Apply changes
    }



//    ###############################################################################################

}
