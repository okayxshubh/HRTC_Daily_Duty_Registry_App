package com.dit.hp.hrtc_app.Asyncs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerGet;
import com.dit.hp.hrtc_app.network.HttpManager;

import org.json.JSONException;


public class ShubhAsyncGet extends AsyncTask<UploadObject, Void, ResponsePojoGet> {


    String outputStr;
    ProgressDialog dialog;
    Context context;
    ShubhAsyncTaskListenerGet shubhAsyncTaskListenerGet;
    TaskType taskType;

    public ShubhAsyncGet(Context context, ShubhAsyncTaskListenerGet shubhAsyncTaskListenerGet, TaskType taskType) {
        this.context = context;
        this.shubhAsyncTaskListenerGet = shubhAsyncTaskListenerGet;
        this.taskType = taskType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(context, "Loading", "Connecting to Server .. Please Wait", true);
        dialog.setCancelable(false);
    }

    @Override
    protected ResponsePojoGet doInBackground(UploadObject... uploadObjects) {
        ResponsePojoGet Data_From_Server = null;
        HttpManager http_manager = null;

        String line;

        try {
            http_manager = new HttpManager();


            // Without JWT
            if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_OTP.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataWithoutJWT(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            // Get Him Access Token Without JWT
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_TOKEN.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataWithParamsWithoutJWT(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            // Get HRTC Token Without JWT
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_HRTC_JWT_TOKEN.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.PostDataWithParamsWithoutJWT(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            // Get User Details Without need of Auth Token
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_USER_DETAILS.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataWithParamsWithoutJWT(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            // Without JWT
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.VERIFY_OTP.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataWithoutJWT(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }



            // With JWT on Staging But Local should be without JWT
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.RESET_PASSWORD.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
//                Data_From_Server = http_manager.GetDataWithoutJWT(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }





            // WITH JWT
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_DEPOTS.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_ADDA.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_ADDA_SEARCH.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_VEHICLES.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_VEHICLE_SEARCH.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_VEHICLE_TYPE.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_DRIVERS.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_CONDUCTORS.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_STAFF.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_STAFF_SEARCH.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_STOPS.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_OTHER_STOPS.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_RELATION.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_GENDER.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_STAFF_TYPE.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_EMP_TYPE.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_SOCIAL_CATEGORY.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_ROUTES.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_ROUTES_SEARCH.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_LOCATION.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_LOCATION_FOR_STOPS.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }


            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_ROUTE_TYPE.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_LOCATION_N_STOPS.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }


            // Put Request..  but added with GET Requests..
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.VEHICLE_TRANSFER.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.PutData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            // Put Request..  but added with GET Requests..
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.DRIVER_TRANSFER.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.PutData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            // Put Request..  but added with GET Requests..
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.ROUTE_TRANSFER.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.PutData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.ADD_STAFF.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.PostData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_ALL_RECORDS.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_HIMACCESS_LOGIN_OFFICE.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_ALL_RECORDS_FILTERED.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_RECORD_SEARCH.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }



            // Put Request..  but added with GET Requests..
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.CONDUCTOR_TRANSFER.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.PutData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }


            // THIS IS A DELETE REQUEST WITH DeleteData method in the HTTP Manager
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.REMOVE_STAFF.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.DeleteDataWithJson(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }


            // THIS IS A DELETE REQUEST WITH DeleteData method in the HTTP Manager
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.REMOVE_VEHICLE.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.DeleteData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            // THIS IS A DELETE REQUEST WITH DeleteData method in the HTTP Manager
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.REMOVE_ROUTE.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.DeleteDataWithJson(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }




            // Put Request
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.EDIT_VEHICLE.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.PostData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            // Put Request
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.EDIT_ROUTE.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.PostData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }


            // Download Excel
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.DOWNLOAD_EXCEL.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataWithJsonBody(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            // Download PDF
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.DOWNLOAD_PDF.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetData(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }


            // JSON Body + Him Access Token
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_USER_OFFICE_INFO.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataWithJsonBody(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }



            // HIM ACCESS STUFF ########################################################################################
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_DEPARTMENT.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataHimAccess(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_OFFICES.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataHimAccess(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_OFFICES_SEARCH.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataHimAccess(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_PARENT_OFFICES.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataHimAccess(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_HOD_DESIGNATION.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataHimAccess(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_OFFICE_LEVELS.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataHimAccess(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_DISTRICT.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataHimAccess(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }

            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_BLOCK.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataHimAccess(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_PANCHAYAT.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataHimAccess(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_VILLAGE.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataHimAccess(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }


            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_MUNICIPALITY_NP.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataHimAccess(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }
            else if (uploadObjects[0].getTasktype().toString().equalsIgnoreCase(TaskType.GET_WARD.toString())) {
                Log.e("We Here", uploadObjects[0].getMethordName());
                Data_From_Server = http_manager.GetDataHimAccess(uploadObjects[0]);
                Log.e("ShubhAsyncGet", "Data from Server: " + uploadObjects[0].getMethordName());
                return Data_From_Server;
            }



        } catch (Exception e) {
            Log.e("Value Saved: ", e.getLocalizedMessage().toString());
        }
        return Data_From_Server;

    }

    @Override
    protected void onPostExecute(ResponsePojoGet result) {
        super.onPostExecute(result);
        try {
            shubhAsyncTaskListenerGet.onTaskCompleted(result, taskType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.dismiss();
    }

}