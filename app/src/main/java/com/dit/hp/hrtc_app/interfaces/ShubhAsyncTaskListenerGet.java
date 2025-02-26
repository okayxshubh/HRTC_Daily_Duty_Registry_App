package com.dit.hp.hrtc_app.interfaces;

import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.enums.TaskType;

import org.json.JSONException;


public interface ShubhAsyncTaskListenerGet {
    void onTaskCompleted(ResponsePojoGet result, TaskType taskType) throws JSONException;


}
