package com.dit.hp.hrtc_app.AACustomStuff;

import com.dit.hp.hrtc_app.enums.TaskType;

import org.json.JSONException;


public interface ShubhAllAsyncTasksListener {
    void onTaskCompleted(ShubhOfflineObject result, TaskType taskType) throws JSONException;

}
