package com.example.kakatin.kakatinhelmet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kakatin.kakatinhelmet.models.BroadcastConstants;
import com.example.kakatin.kakatinhelmet.services.ApiConnectorService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Matti on 04/12/2015.
 */
public class OurMapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    public static final String TAG = OurMapFragment.class.getSimpleName();
    private static final int SYNC_DELAY = 1000;

    private CardView toolBar;
    private LinearLayout statsButton;
    private LinearLayout mapButton;
    private ImageView statsText;
    private ImageView mapText;
    private View slider;
    private View goneSlider;

    GoogleApiClient mGoogleApiClient;
    private MapView mapView;
    private GoogleMap googleMap;
    private Handler handler;
    private IntentFilter mDataIntentFilter = new IntentFilter(BroadcastConstants.BC_DATA_AVAILABLE);

    private Double tsoLat = 60.459031;
    private Double tsoLng = 22.267305;

    private boolean mActive;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_map, parent, false);

        toolBar = (CardView)getActivity().findViewById(R.id.toolBar);
        statsButton = (LinearLayout)toolBar.findViewById(R.id.stats_button);
        statsText = (ImageView)toolBar.findViewById(R.id.stats_text);
        mapText = (ImageView)toolBar.findViewById(R.id.map_text_button);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statsText.setImageResource(R.mipmap.stats_valk);
                mapText.setImageResource(R.mipmap.map_pun);
            }
        });
        slider = toolBar.findViewById(R.id.toolbar_slider);
        goneSlider = toolBar.findViewById(R.id.toolbar_slider_stat);
        slider.setVisibility(View.VISIBLE);
        goneSlider.setVisibility(View.GONE);
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Map clicked", Toast.LENGTH_SHORT).show();
//                slider.animate().translationX(50);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slider_move_right);
                        slider.startAnimation(animation);
                    }
                });
                fragmentTransaction();
            }
        });
        mapButton = (LinearLayout)toolBar.findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "You're here dummy.", Toast.LENGTH_SHORT).show();

            }
        });

        mapView = (MapView)v.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        try{
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e){
            Log.e(TAG, "Error thrown", e);
        }
        googleMap = mapView.getMap();
//        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        Log.e(TAG, "GoogleMap should be initialized.");
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(60.449977, 22.293327), 15.0f));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(60.449977, 22.293327))
                .zoom(15)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        handler = new Handler();
    }

    @Override
    public void onPause(){
        super.onPause();
        mapView.onPause();
        mActive = false;
        handler.removeCallbacksAndMessages(null);

    }

    private void fragmentTransaction(){
        SensorFragment fragment = SensorFragment.newInstance();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,fragment,"MAP_FRAGMENT").commit();
    }

    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
        ResponseReceiver mResponseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mResponseReceiver, mDataIntentFilter);
        mActive = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callUpdate("http://damp-spire-9142.herokuapp.com/android/deliverLocation");
                Log.e(TAG, "Calling for data");
                updateMap();
                handler.postDelayed(this, SYNC_DELAY);
            }
        }, SYNC_DELAY);
    }

    private void updateMap(){
        
    }

    public void callUpdate(String dataURI){
        Intent intent = new Intent(getActivity(), ApiConnectorService.class);
        intent.setData(Uri.parse(dataURI));
        getActivity().startService(intent);
    }

    public static OurMapFragment newInstance() {
        OurMapFragment fragment = new OurMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(60.459031, 22.267305)).zoom(18).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        mapView.invalidate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void parseJSONForLocation(String JSON){
        JSONObject data;
        try {
            data = new JSONObject(JSON);
            tsoLat = data.getDouble("Lat");
            tsoLng = data.getDouble("Long");
        } catch (JSONException e) {
            Log.e(TAG, "Error handling JSON.");
            return;
        }

    }

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
                if(intent.getStringExtra(BroadcastConstants.BC_LOCATION) != null){
                    Log.e(TAG, "Data is available. Update all views.");
                    parseJSONForLocation(intent.getStringExtra(BroadcastConstants.BC_LOCATION));
                }else if(intent.getStringExtra(BroadcastConstants.BC_IMPACT) != null){
                    Log.e(TAG, "Impact received.");
                }
            }
        }
    }
}
