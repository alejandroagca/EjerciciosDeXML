package com.example.usuario.myapplication.network;

/**
 * Created by usuario on 15/01/18.
 */

import android.content.Context;
import android.os.Environment;

import com.example.usuario.myapplication.pojo.FailureEvent;
import com.example.usuario.myapplication.pojo.MessageEvent;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import cz.msebera.android.httpclient.Header;

public class DownloadTask {

    public static void executeDownload(Context context, String canal, String temporal) {
        File miFichero = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), temporal);
        RestClient.get(canal, new FileAsyncHttpResponseHandler(context) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                EventBus.getDefault().post(new FailureEvent("Error en la descarga: " + throwable.getMessage(), statusCode));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                EventBus.getDefault().post(new MessageEvent("Todo OK", file));
            }
        });
    }
}
