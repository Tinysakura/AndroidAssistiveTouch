package com.example.administrator.androidassistivetouch.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.administrator.androidassistivetouch.view.AssestiveTouchDemo;
import com.example.administrator.androidassistivetouch.view.AssestiveTouchListener;
import com.example.administrator.androidassistivetouch.R;

public class AssestiveTouchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assestive_touch_layout);

        AssestiveTouchDemo assestiveTouchDemo=(AssestiveTouchDemo)findViewById(R.id.assestive_touch_button);
        assestiveTouchDemo.setAssestiveTouchListener(new AssestiveTouchListener() {
            @Override
            public void onNorthWigetClick() {
                Log.v("AssestiveTouch","north");
            }

            @Override
            public void onSourthWigetClick() {
                Log.v("AssestiveTouch","sourth");
            }

            @Override
            public void onWestWigetClick() {
                Log.v("AssestiveTouch","west");
            }

            @Override
            public void onEastWigetClick() {
                Log.v("AssestiveTouch","east");
            }
        });
    }
}
