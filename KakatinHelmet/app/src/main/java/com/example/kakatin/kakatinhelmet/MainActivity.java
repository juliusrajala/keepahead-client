package com.example.kakatin.kakatinhelmet;

import android.app.ActionBar;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int SENSOR_FRAGMENT = 1;
    private static final int MAP_FRAGMENT = 2;


    private int currentFragment = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }

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
