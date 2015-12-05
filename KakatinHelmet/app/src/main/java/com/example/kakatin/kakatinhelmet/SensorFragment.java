package com.example.kakatin.kakatinhelmet;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kakatin.kakatinhelmet.services.ApiConnectorService;


public class SensorFragment extends Fragment {
    private static final String TAG = SensorFragment.class.getSimpleName();
    private static final int SYNC_DELAY = 5000;

    private TextView speedStat;
    private TextView tempStat;
    private TextView localeStat;

    private int mTemp;
    private int mSpeed;
    private String mLocale;

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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callUpdate("http://damp-spire-9142.herokuapp.com/temperature");
                handler.postDelayed(this, SYNC_DELAY);
            }
        }, SYNC_DELAY);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    public void callUpdate(String dataURI){
        Intent intent = new Intent(getActivity(), ApiConnectorService.class);
        intent.setData(Uri.parse(dataURI));
    }

    private void fragmentTransaction(){
        OurMapFragment fragment = OurMapFragment.newInstance();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,fragment,"MAP_FRAGMENT").commit();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
