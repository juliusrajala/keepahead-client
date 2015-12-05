package com.example.kakatin.kakatinhelmet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Matti on 04/12/2015.
 */
public class OurMapFragment extends Fragment{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_map, parent, false);

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
