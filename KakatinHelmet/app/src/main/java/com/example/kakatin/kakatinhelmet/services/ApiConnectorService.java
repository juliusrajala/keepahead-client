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
        try {
            getJSON(dataString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getJSON(String URI) throws Exception {
        Request request = new Request.Builder()
                        .url(URI)
                        .build();

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
