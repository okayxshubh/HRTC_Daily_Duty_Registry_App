package com.dit.hp.hrtc_app.AttendanceModule.attendanceInterfaces;

import com.dit.hp.hrtc_app.enums.TaskType;

import org.json.JSONException;

public interface AsyncTaskListenerFile {

    void onTaskCompleted(String object, TaskType taskType) throws JSONException;
}
