package com.example.kakatin.kakatinhelmet.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.kakatin.kakatinhelmet.models.BroadcastConstants;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public ApiConnectorService(){
        super("Fireeee");
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
                Log.e(TAG, "Response caught.");
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                String bodyString = response.body().string();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    Log.e(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }
//                Log.e(TAG, response.body().string());
                Log.e(TAG, "Body of response contains: " + bodyString);

                try {
                    broadcastRecogniser(bodyString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /** Takes the data caught from the response and determines type and data. */
    private void broadcastRecogniser(String body) throws JSONException {
        Log.e(TAG, "BroadcastRecogniser called with String:" + body);
        JSONObject data = new JSONObject(body);
        if(data.has("Lat")){
            sendBroadCast(BroadcastConstants.BC_LOCATION, data.toString());
        }
        if(body.length() > 200){
            JSONArray dataArray = new JSONArray(data.getString("data"));
//            for(int i = 0; i< dataArray.length();i++){
//                Log.e(TAG, "Sensordata parsed: " + dataArray.get(i));
//            }
            Log.e(TAG, "dataArray contains: " + dataArray.toString());
            sendBroadCast(BroadcastConstants.BC_ALL, dataArray.toString());
        }
    }

    //TODO: Make structure better for handling different kinds of broadcasts.
    private void sendBroadCast(String type, String data){
        Log.e(TAG, "Currently action is: " + type + " and data is " + data);
        Intent localIntent =
                new Intent(BroadcastConstants.BC_DATA_AVAILABLE)
                        .putExtra(type, data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
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
