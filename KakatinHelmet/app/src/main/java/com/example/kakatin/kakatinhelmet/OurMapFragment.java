package com.example.kakatin.kakatinhelmet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Matti on 04/12/2015.
 */
public class OurMapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    public static final String TAG = OurMapFragment.class.getSimpleName();

    private CardView toolBar;
    private LinearLayout statsButton;
    private LinearLayout mapButton;
    private TextView statsText;
    private TextView mapText;
    private View slider;
    private View goneSlider;

    private MapView mapView;
    private GoogleMap googleMap;
    GoogleApiClient mGoogleApiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_map, parent, false);

        toolBar = (CardView)getActivity().findViewById(R.id.toolBar);
        statsButton = (LinearLayout)toolBar.findViewById(R.id.stats_button);
        statsText = (TextView)toolBar.findViewById(R.id.stats_text);
        mapText = (TextView)toolBar.findViewById(R.id.map_text_button);
        mapText.setTextColor(getResources().getColor(R.color.TextPrimaryDark));
        statsText.setTextColor(getResources().getColor(R.color.TextWhite));
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
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(60.449977, 22.293327), 12.0f));
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
    }

    @Override
    public void onPause(){
        super.onPause();
        mapView.onPause();
    }

    private void fragmentTransaction(){
        SensorFragment fragment = SensorFragment.newInstance();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,fragment,"MAP_FRAGMENT").commit();
    }

    @Override
    public void onResume(){
        super.onResume();
        mapView.onPause();
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
                .target(new LatLng(60.459031, 22.267305)).zoom(15).build();
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
}
