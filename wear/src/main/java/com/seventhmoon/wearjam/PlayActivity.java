package com.seventhmoon.wearjam;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;


public class PlayActivity extends WearableActivity {
    private static final String TAG = PlayActivity.class.getName();

    TextView textView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAmbientEnabled();

        Intent intent = getIntent();
        String title = intent.getStringExtra("TITLE");

        setContentView(R.layout.play_activity);

        textView = findViewById(R.id.textView);
        textView.setText(title);


    }
}
