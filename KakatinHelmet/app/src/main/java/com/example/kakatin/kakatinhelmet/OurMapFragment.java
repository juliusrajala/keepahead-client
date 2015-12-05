package com.example.kakatin.kakatinhelmet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Matti on 04/12/2015.
 */
public class OurMapFragment extends Fragment{
    public static final String TAG = OurMapFragment.class.getSimpleName();

    private CardView toolBar;
    private LinearLayout statsButton;
    private LinearLayout mapButton;
    private TextView statsText;
    private TextView mapText;
    private View slider;
    private View goneSlider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_map, parent, false);

        toolBar = (CardView)getActivity().findViewById(R.id.toolBar);
        statsButton = (LinearLayout)toolBar.findViewById(R.id.stats_button);
        statsText = (TextView)toolBar.findViewById(R.id.stats_text);
        mapText = (TextView)toolBar.findViewById(R.id.map_text_button);
        statsText.setTextColor(getResources().getColor(R.color.TextPrimaryDark));
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
                        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slider_move_left);
                        slider.startAnimation(animation);
                    }
                });
            }
        });
        mapButton = (LinearLayout)toolBar.findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "You're here dummy.", Toast.LENGTH_SHORT).show();

            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
    }
    public static OurMapFragment newInstance() {
        OurMapFragment fragment = new OurMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }
}
