package com.seventhmoon.wearjam;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.CircledImageView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.seventhmoon.wearjam.Data.Constants;
import com.seventhmoon.wearjam.Data.DottedSeekBar;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static com.seventhmoon.wearjam.MainActivity.current_position;
import static com.seventhmoon.wearjam.MainActivity.mediaOperation;
import static com.seventhmoon.wearjam.MainActivity.isPlayPress;


public class PlayActivity extends WearableActivity {
    private static final String TAG = PlayActivity.class.getName();

    //private TextView textView;

    private CircledImageView btnA, btnB, btnPlayOrPause;
    public static DottedSeekBar dottedSeekBar;
    public static int progress_mark_a = 0, progress_mark_b = 1000;
    private static String title;
    public static String songPath;
    public static String songDuration;
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

        title = intent.getStringExtra("TITLE");
        songPath = intent.getStringExtra("PATH");
        songDuration = intent.getStringExtra("DURATION");
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

        btnPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaOperation.getCurrent_state() == Constants.STATE.Started) { //if playing, pause

                    //Log.d(TAG, "[imgPlayOrPause] isPlaying, songPlaying = "+songPlaying);

                    mediaOperation.setTaskStop();

                    isPlayPress = false;

                    mediaOperation.doPause();
                    current_position = mediaOperation.getCurrentPosition();
                    Log.e(TAG, "===> current_position = "+current_position);
                    btnPlayOrPause.setImageResource(R.drawable.ic_play_arrow_black_48dp);

                } else {
                    Log.d(TAG, "[imgPlayOrPause] state is paused, stopped...");

                    mediaOperation.setTaskStart();

                    //String songPath, songName;
                    isPlayPress = true;

                    //set looping true
                    if (progress_mark_a == 0 && progress_mark_b == 1000) {
                        mediaOperation.setLooping(true);
                    } else {
                        mediaOperation.setLooping(false);
                    }







                    Log.d(TAG, "play "+title+" position = "+current_position);
                    //audioOperation.setCurrentPosition(current_position_d);
                    //audioOperation.doPlay(songPath);
                    mediaOperation.setCurrentPosition(current_position);
                    mediaOperation.doPlay(songPath);

                    btnPlayOrPause.setImageResource(R.drawable.ic_pause_black_48dp);
                }
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
