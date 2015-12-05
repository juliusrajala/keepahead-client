package com.example.kakatin.kakatinhelmet;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int SENSOR_FRAGMENT = 1;
    private static final int MAP_FRAGMENT = 2;

    private int currentFragment = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(fragment == null){
            fragment = SensorFragment.newInstance();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }

    }

    private void onTouchNavi(final int position){
        //TODO: Make more fragments, add them here
        currentFragment = position;
        switch (position) {
            case SENSOR_FRAGMENT:
                openFragment(SensorFragment.newInstance());
                break;
            case MAP_FRAGMENT:
                openFragment(OurMapFragment.newInstance());
                break;
            default:
                return;
        }
    }

    public void openFragment(final Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }


}
