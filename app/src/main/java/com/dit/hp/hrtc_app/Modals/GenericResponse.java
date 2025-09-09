package com.dit.hp.hrtc_app.Modals;

public class GenericResponse<T> {
    private String status;
    private String message;
    private T data; // Can be null if there's no data

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
