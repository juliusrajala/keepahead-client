package com.example.kakatin.kakatinhelmet;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class SensorFragment extends Fragment {
    private static final String TAG = SensorFragment.class.getSimpleName();

    private TextView speedStat;
    private TextView tempStat;
    private TextView localeStat;

    private int mTemp;
    private int mSpeed;
    private String mLocale;

    private CardView toolBar;
    private LinearLayout statsButton;
    private LinearLayout mapButton;
    private TextView statsText;
    private TextView mapText;

    public static SensorFragment newInstance() {
        SensorFragment fragment = new SensorFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        statsText = (TextView)toolBar.findViewById(R.id.stats_text);
        mapText = (TextView)toolBar.findViewById(R.id.map_text_button);
        statsText.setTextColor(getResources().getColor(R.color.TextPrimaryDark));
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Stats clicked", Toast.LENGTH_SHORT).show();
            }
        });
        mapButton = (LinearLayout)toolBar.findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Map clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

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
