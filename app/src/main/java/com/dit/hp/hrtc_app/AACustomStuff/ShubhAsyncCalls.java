package com.dit.hp.hrtc_app.AACustomStuff;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dit.hp.hrtc_app.enums.TaskType;

import org.json.JSONException;

public class ShubhAsyncCalls extends AsyncTask<ShubhUploadObject, Void, ShubhOfflineObject> {

    ProgressDialog dialog;
    Context context;
    ShubhAllAsyncTasksListener shubhAllAsyncTasksListener;
    TaskType taskType;

    public ShubhAsyncCalls(Context context, ShubhAllAsyncTasksListener listener, TaskType taskType) {
        this.context = context;
        this.shubhAllAsyncTasksListener = listener;
        this.taskType = taskType;
    }

    @Override
    protected ShubhOfflineObject doInBackground(ShubhUploadObject... shubhUploadObjects) {
        ShubhOfflineObject dataFromServer = null;
        ShubhHttpManager httpManager = new ShubhHttpManager();

        try {
            ShubhUploadObject uploadObject = shubhUploadObjects[0];

            // Check task type and call appropriate method in ShubhHttpManager
            if (uploadObject.getTasktype() == TaskType.IDK_TASK) {
                Log.e("Executing Task: ", uploadObject.getTasktype().name());
                dataFromServer = httpManager.ShubhGetData(uploadObject);
            }




        } catch (Exception e) {
            Log.e("Async Calls Exception", "Exception: " + e.getMessage());
        }

        return dataFromServer;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(context, "Loading", "Connecting to Server .. Please Wait", true);
        dialog.setCancelable(false);
    }

    @Override
    protected void onPostExecute(ShubhOfflineObject result) {
        super.onPostExecute(result);
        try {
            if (shubhAllAsyncTasksListener != null)
                shubhAllAsyncTasksListener.onTaskCompleted(result, taskType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

}


