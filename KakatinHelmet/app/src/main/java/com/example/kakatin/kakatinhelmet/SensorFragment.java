package com.example.kakatin.kakatinhelmet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.DownloadListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kakatin.kakatinhelmet.models.BroadcastConstants;
import com.example.kakatin.kakatinhelmet.services.ApiConnectorService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SensorFragment extends Fragment {
    private static final String TAG = SensorFragment.class.getSimpleName();
    private static final int SYNC_DELAY = 5000;

    private TextView speedStat;
    private TextView tempStat;
    private TextView localeStat;
    private TextView impactStat;

    private int mTemp;
    private int mSpeed = 0;
    private String mLocale;
    private Boolean mActive;
    private Double tsoLat = 60.459031;
    private Double tsoLng = 22.267305;

    private IntentFilter mDataIntentFilter = new IntentFilter(BroadcastConstants.BC_DATA_AVAILABLE);

    private CardView toolBar;
    private LinearLayout statsButton;
    private LinearLayout mapButton;
    private ImageView statsText;
    private ImageView mapText;
    private View slider;
    private View goneSlider;

    private Handler handler;

    public static SensorFragment newInstance() {
        SensorFragment fragment = new SensorFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sensor, container, false);
        speedStat = (TextView)v.findViewById(R.id.speed_stat);
        tempStat = (TextView)v.findViewById(R.id.temp_stat);
        localeStat = (TextView)v.findViewById(R.id.locale_stat);
        impactStat = (TextView)v.findViewById(R.id.last_crash_time);

        toolBar = (CardView)getActivity().findViewById(R.id.toolBar);
        statsButton = (LinearLayout)toolBar.findViewById(R.id.stats_button);
        statsText = (ImageView)toolBar.findViewById(R.id.stats_text);
        mapText = (ImageView)toolBar.findViewById(R.id.map_text_button);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapText.setImageResource(R.mipmap.map_valk);
                statsText.setImageResource(R.mipmap.stats_pun);
            }
        });
        slider = toolBar.findViewById(R.id.toolbar_slider_stat);
        goneSlider = toolBar.findViewById(R.id.toolbar_slider);
        slider.setVisibility(View.VISIBLE);
        goneSlider.setVisibility(View.GONE);

        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "You're here dummy.", Toast.LENGTH_SHORT).show();
            }
        });
        mapButton = (LinearLayout)toolBar.findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Map clicked", Toast.LENGTH_SHORT).show();
//                slider.animate().translationX(50);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slider_move_left);
                        slider.startAnimation(animation);
                    }
                });
                fragmentTransaction();

            }
        });

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        ResponseReceiver mResponseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mResponseReceiver, mDataIntentFilter);
        mActive = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callUpdate("http://damp-spire-9142.herokuapp.com/android/deliverAllData");
                Log.e(TAG, "Calling for data");
                try {
                    updateLocation();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, SYNC_DELAY);
            }
        }, SYNC_DELAY);
    }

    @Override
    public void onPause(){
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        mActive = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    private void updateView(final TextView toChange, final String data){
        if(data == null || getActivity() == null){
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toChange.setText(data);
            }
        });
    }

    public void callUpdate(String dataURI){
        Intent intent = new Intent(getActivity(), ApiConnectorService.class);
        intent.setData(Uri.parse(dataURI));
        getActivity().startService(intent);
    }

    private void fragmentTransaction(){
        OurMapFragment fragment = OurMapFragment.newInstance();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "MAP_FRAGMENT").commit();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void parseJSONForViews(String JSON){
        JSONArray dataArray;
        try {
            dataArray = new JSONArray(JSON);
            for(int i = 0; i< dataArray.length();i++){
                Log.e(TAG, "Sensordata parsed: " + dataArray.get(i));
                JSONObject jObject = new JSONObject(dataArray.get(i).toString());
                String sId = jObject.getString("name");
                if(sId.equals("TEMP_ID")){
                    Double value = jObject.getDouble("value");
                    updateView(tempStat, String.valueOf(value)+"°C");
                }else if(sId.equals("ACC_IMP_ID")){
                    //TODO: Make impactContainer class for dealing with crashes.
                    updateView(impactStat, jObject.getString("value"));
                }else if(sId.equals("SPEED_ID")){
                    if(mSpeed < jObject.getInt("value")){
                        mSpeed = (int) 3.6*jObject.getInt("value");
                    }
                    updateView(speedStat, String.valueOf(mSpeed+"km/h"));
                }else if(sId.equals("LOC_LA_ID")){
                    tsoLat = jObject.getDouble("value");
                }else if(sId.equals("LOC_LO_ID")){
                    tsoLng = jObject.getDouble("value");
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error handling JSON.");
            return;
        }

    }

    private void updateLocation() throws IOException {
        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = gcd.getFromLocation(tsoLat, tsoLng, 1);
        if (addresses.size() == 0){ return; }
        String city = (addresses.get(0).getSubAdminArea());
        Log.e(TAG, "Content: "+addresses.get(0));
        String country = (addresses.get(0).getCountryName());
        updateView(localeStat, city+", "+country);
    }

    //TODO: check jumppatikku for smarter broadcastreceiver implementation
    private class ResponseReceiver extends BroadcastReceiver
    {
        private ResponseReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action == null){
                return;
            }
            Log.e(TAG, "Currently action is: " + action);
            if(action.equals(BroadcastConstants.BC_DATA_AVAILABLE)){
                if(intent.getStringExtra(BroadcastConstants.BC_ALL) != null){
                    Log.e(TAG, "Data is available. Update all views.");
                    parseJSONForViews(intent.getStringExtra(BroadcastConstants.BC_ALL));
                }else if(intent.getStringExtra(BroadcastConstants.BC_IMPACT) != null){
                    Log.e(TAG, "Impact received.");
                }
            }
        }
    }
}
