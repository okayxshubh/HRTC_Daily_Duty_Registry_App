package com.dit.hp.hrtc_app.Asyncs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerPost;
import com.dit.hp.hrtc_app.network.HttpManager;

import org.json.JSONException;

public class ShubhAsyncPost extends AsyncTask<UploadObject, Void, ResponsePojoGet> {

    ProgressDialog dialog;
    Context context;
    ShubhAsyncTaskListenerPost asyncTaskListenerObject;
    TaskType taskType;
    private ProgressDialog mProgressDialog;

    public ShubhAsyncPost(Context context, ShubhAsyncTaskListenerPost asyncTaskListenerObject, TaskType taskType) {
        this.context = context;
        this.asyncTaskListenerObject = asyncTaskListenerObject;
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
        HttpManager http_manager = new HttpManager();
        UploadObject uploadObject = uploadObject = uploadObjects[0];
        StringBuilder stringBuilder;
        ResponsePojoGet responseObject = null;

        // Login
        if (uploadObjects[0].getTasktype() == TaskType.LOGIN_HRTC) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.PostDataNewFixWithoutJWT(uploadObjects[0]);
            return Data_From_Server;
        }

        // DEPOT
        else if (uploadObjects[0].getTasktype() == TaskType.ADD_DEPOT) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.PostData(uploadObjects[0]);
            return Data_From_Server;
        } else if (uploadObjects[0].getTasktype() == TaskType.EDIT_DEPOT) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.PutData(uploadObjects[0]);
            return Data_From_Server;
        } else if (uploadObjects[0].getTasktype() == TaskType.REMOVE_DEPOT) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.DeleteData(uploadObjects[0]);
            return Data_From_Server;
        }


        // Vehicle
        else if (uploadObjects[0].getTasktype() == TaskType.ADD_VEHICLE) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.PostData(uploadObjects[0]);
            return Data_From_Server;
        } else if (uploadObjects[0].getTasktype() == TaskType.EDIT_VEHICLE) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.PutData(uploadObjects[0]);
            return Data_From_Server;
        } else if (uploadObjects[0].getTasktype() == TaskType.REMOVE_VEHICLE) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.DeleteData(uploadObjects[0]);
            return Data_From_Server;
        }


        // STAFF
        else if (uploadObjects[0].getTasktype() == TaskType.ADD_STAFF) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.PostData(uploadObjects[0]);
            return Data_From_Server;
        } else if (uploadObjects[0].getTasktype() == TaskType.EDIT_DRIVER) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.PutData(uploadObjects[0]);
            return Data_From_Server;
        } else if (uploadObjects[0].getTasktype() == TaskType.EDIT_CONDUCTOR) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.PutData(uploadObjects[0]);
            return Data_From_Server;
        } else if (uploadObjects[0].getTasktype() == TaskType.REMOVE_STAFF) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.DeleteData(uploadObjects[0]);
            return Data_From_Server;
        }


        // ROUTE
        else if (uploadObjects[0].getTasktype() == TaskType.ADD_ROUTE) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.PostData(uploadObjects[0]);
            return Data_From_Server;
        } else if (uploadObjects[0].getTasktype() == TaskType.EDIT_ROUTE) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());

            Data_From_Server = http_manager.PutData(uploadObjects[0]);
            return Data_From_Server;
        } else if (uploadObjects[0].getTasktype() == TaskType.REMOVE_ROUTE) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.DeleteData(uploadObjects[0]);
            return Data_From_Server;
        }


        // Add A STOPS
        else if (uploadObjects[0].getTasktype() == TaskType.ADD_STOP) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.PostData(uploadObjects[0]);
            return Data_From_Server;
        }

        // Remove Stop
        else if (uploadObjects[0].getTasktype() == TaskType.REMOVE_STOP) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.DeleteData(uploadObjects[0]);
            return Data_From_Server;
        }
        // EDIT STOP
        else if (uploadObjects[0].getTasktype() == TaskType.EDIT_STOP) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.PutData(uploadObjects[0]);
            return Data_From_Server;
        }


        // SAVE A DAILY RECORD
        else if (uploadObjects[0].getTasktype() == TaskType.SAVE_RECORD) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.PostData(uploadObjects[0]);
            return Data_From_Server;
        }

        // EDIT  A DAILY RECORD
        else if (uploadObjects[0].getTasktype() == TaskType.UPDATE_RECORD) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.PutData(uploadObjects[0]);
            return Data_From_Server;
        }


        // Add Adda
        if (uploadObjects[0].getTasktype() == TaskType.ADD_ADDA) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.PostData(uploadObjects[0]);
            return Data_From_Server;
        } else if (uploadObjects[0].getTasktype() == TaskType.EDIT_ADDA) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.PutData(uploadObjects[0]);
            return Data_From_Server;
        } else if (uploadObjects[0].getTasktype() == TaskType.REMOVE_ADDA) {
            Log.e("We Here", "Upload Object Debug: " + uploadObjects[0].getUrl() + "  " + uploadObjects[0].getMethordName() + "  " + uploadObjects[0].getMasterName() + "  " + uploadObjects[0].getParam());
            Data_From_Server = http_manager.DeleteData(uploadObjects[0]);
            return Data_From_Server;
        }










        return responseObject;
    }

    @Override
    protected void onPostExecute(ResponsePojoGet result) {
        super.onPostExecute(result);

        try {
            // this is interfaceObject.InterfaceMethod()
            asyncTaskListenerObject.onTaskCompleted(result, taskType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Dismiss the progress dialog after execution
        dialog.dismiss();
    }


}