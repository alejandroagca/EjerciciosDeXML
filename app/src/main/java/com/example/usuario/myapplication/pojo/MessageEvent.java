package com.example.usuario.myapplication.pojo;

import java.io.File;

/**
 * Created by usuario on 15/01/18.
 */

public class MessageEvent {

    public final String message;
    public final File file;

    public MessageEvent(String message, File file) {
        this.message = message;
        this.file = file;
    }
}
