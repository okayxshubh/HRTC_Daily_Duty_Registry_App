package com.dit.hp.hrtc_app.AttendanceModule.attendanceInterfaces;

import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.FaceAuthObjectResponse;
import com.dit.hp.hrtc_app.enums.TaskType;

public interface FaceAuthenticationInterface {

    void onTaskCompleted(FaceAuthObjectResponse object, TaskType taskType) throws Exception;
}
