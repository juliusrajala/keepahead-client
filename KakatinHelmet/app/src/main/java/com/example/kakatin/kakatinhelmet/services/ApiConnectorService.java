package com.example.kakatin.kakatinhelmet.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by lorenzo on 6.12.2015.
 */
public class ApiConnectorService extends IntentService {
    private final static String TAG = ApiConnectorService.class.getSimpleName();
    public static final MediaType MEDIA_TYPE_JSON
            = MediaType.parse("text; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();

    private static final int GET1 = 1;
    private static final int GET2 = 2;
    private static final int GET3 = 3;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ApiConnectorService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent workIntent){
        String dataString = workIntent.getDataString();
        if(dataString == null){
            return;
        }
        if(dataString.charAt(0)=='P'){
            //TODO: POST HTTP request
            try {
                notifyServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            //GET request
            //int CASE = (int)dataString.charAt(1);
            int CASE = 3;
            try {
                getJSON(CASE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getJSON(int CASE) throws Exception {
        Request request;
        switch (CASE){
            case GET1:
                request = new Request.Builder()
                        .url("http://damp-spire-9142.herokuapp.com/android/deliverAllData")
                        .build();
                break;
            case GET2:
                request = new Request.Builder()
                        .url("http://damp-spire-9142.herokuapp.com/android/deliverLocation")
                        .build();
                break;
            case GET3:
                request = new Request.Builder()
                        .url("http://damp-spire-9142.herokuapp.com/temperature")
                        .build();
                break;
            default:
                request = new Request.Builder()
                        .url("http://damp-spire-9142.herokuapp.com/hello")
                        .build();
                break;
        }

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    Log.e(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                Log.e(TAG, response.body().string());
            }
        });
    }

    public void notifyServer() throws Exception {
        String postBody = "";

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(MEDIA_TYPE_JSON, postBody))
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        System.out.println(response.body().string());
    }

}
