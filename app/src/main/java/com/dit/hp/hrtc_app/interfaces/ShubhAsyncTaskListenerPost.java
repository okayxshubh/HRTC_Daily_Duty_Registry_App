package com.dit.hp.hrtc_app.interfaces;

import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.enums.TaskType;

import org.json.JSONException;

public interface ShubhAsyncTaskListenerPost {
        void onTaskCompleted(ResponsePojoGet responseObject, TaskType taskType) throws JSONException;
    }

