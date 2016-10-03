package com.whiterabbit.pisabike.screens.main;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.whiterabbit.pisabike.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportFragmentManager().findFragmentById(R.id.main_activity_frame) == null) {
            Fragment f = MainFragment.createInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.main_activity_frame, f)
                    .commit();
        }
    }
}
