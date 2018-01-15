package com.example.usuario.myapplication.pojo;

/**
 * Created by usuario on 15/01/18.
 */

    public class FailureEvent {
    public final String message;
    public final int status;

    public FailureEvent(String message, int status) {
        this.message = message;
        this.status = status;
    }
}

