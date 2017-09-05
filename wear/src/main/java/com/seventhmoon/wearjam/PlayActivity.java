package com.seventhmoon.wearjam;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.CircledImageView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.seventhmoon.wearjam.Data.DottedSeekBar;


public class PlayActivity extends WearableActivity {
    private static final String TAG = PlayActivity.class.getName();

    //private TextView textView;

    private CircledImageView btnA, btnB, btnPlayOrPause;
    private DottedSeekBar dottedSeekBar;
    private int progress_mark_a = 0, progress_mark_b = 1000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAmbientEnabled();

        Intent intent = getIntent();

        setContentView(R.layout.play_activity);

        TextView textView = findViewById(R.id.textView);
        btnA = findViewById(R.id.btn_a);
        btnB = findViewById(R.id.btn_b);
        btnPlayOrPause = findViewById(R.id.btn_play);
        dottedSeekBar = findViewById(R.id.seekBarTime);

        String title = intent.getStringExtra("TITLE");
        textView.setText(title);

        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_mark_a = dottedSeekBar.getProgress();

                dottedSeekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                dottedSeekBar.setDotsDrawable(R.drawable.dot);
                dottedSeekBar.setmLine(R.drawable.line);
            }
        });

        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_mark_b = dottedSeekBar.getProgress();

                dottedSeekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                dottedSeekBar.setDotsDrawable(R.drawable.dot);
                dottedSeekBar.setmLine(R.drawable.line);
            }
        });


    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");


        super.onPause();
        //mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        //mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        super.onDestroy();

    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        /*if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            mTextView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);
        }*/
    }
}
