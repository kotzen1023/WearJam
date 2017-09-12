package com.seventhmoon.wearjam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.CircledImageView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.seventhmoon.wearjam.Data.Constants;
import com.seventhmoon.wearjam.Data.DottedSeekBar;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static com.seventhmoon.wearjam.MainActivity.current_position;
import static com.seventhmoon.wearjam.MainActivity.current_song_duration;
import static com.seventhmoon.wearjam.MainActivity.current_volume;
import static com.seventhmoon.wearjam.MainActivity.editor;
import static com.seventhmoon.wearjam.MainActivity.mediaOperation;
import static com.seventhmoon.wearjam.MainActivity.isPlayPress;
import static com.seventhmoon.wearjam.MainActivity.pref;


public class PlayActivity extends WearableActivity {
    private static final String TAG = PlayActivity.class.getName();

    private Context context;

    private CircledImageView btnA, btnB, btnSpeed, btnPlayOrPause;
    private FrameLayout frameLayoutPlay, frameLayoutDown;
    private LinearLayout linearLayoutSpeed;
    public static DottedSeekBar timeSeekBar, seekBarSpeed, seekBarVolume;
    public static int progress_mark_a = 0, progress_mark_b = 1000;
    private static String title;
    public static String songPath;
    public static String songDuration;
    private static TextView textViewTime;
    private static TextView textViewTimeElapsed;
    private TextView textViewSpeed;

    private static float current_speed = 0;
    private int btnSpeedVolumePress = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAmbientEnabled();

        context = getBaseContext();

        Intent intent = getIntent();

        setContentView(R.layout.play_activity);

        timeSeekBar = findViewById(R.id.seekBarTime);
        TextView textTitle = findViewById(R.id.textView);

        seekBarSpeed = findViewById(R.id.seekBarSpeed);
        seekBarVolume = findViewById(R.id.seekBarVolume);

        frameLayoutPlay = findViewById(R.id.frameLayoutPlay);
        btnA = findViewById(R.id.btn_a);
        btnB = findViewById(R.id.btn_b);
        btnPlayOrPause = findViewById(R.id.btn_play);

        linearLayoutSpeed = findViewById(R.id.linearLayoutSpeed);
        frameLayoutDown = findViewById(R.id.frameLayoutDown);
        btnSpeed = findViewById(R.id.btn_speed);
        textViewTime = findViewById(R.id.textViewTime);
        textViewTimeElapsed = findViewById(R.id.textViewTimeElapsed);
        textViewSpeed = findViewById(R.id.textViewSpeed);


        title = intent.getStringExtra("TITLE");
        songPath = intent.getStringExtra("PATH");
        songDuration = intent.getStringExtra("DURATION");
        textTitle.setText(title);

        timeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaOperation.getCurrent_state() == Constants.STATE.Started) { //is playing
                    //Log.e(TAG, "song was playing, don't change");
                } else {
                    //if (mediaOperation.getCurrent_state() != Constants.STATE.Started) {
                    if (current_song_duration != 0) {

                        //NumberFormat f = new DecimalFormat("00");
                        //NumberFormat f2 = new DecimalFormat("000");

                        double per_unit = (double) current_song_duration / 1000.0;

                        double duration = seekBar.getProgress() * per_unit;

                        //Log.e(TAG, "=> onProgressChanged unit = "+String.valueOf(per_unit)+" duration = "+String.valueOf(duration));

                        setSongDuration((int) duration);
                        setSongDuration2((int) duration);
                        //setActionBarTitle((int) duration);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "onStartTrackingTouch >");

                linearLayoutSpeed.setVisibility(View.GONE);
                frameLayoutDown.setVisibility(View.VISIBLE);

                if (isPlayPress) { //play is pressed
                    if (mediaOperation.getCurrent_state() == Constants.STATE.Started) { //if playing, pause
                        mediaOperation.doPause();
                        Log.d(TAG, "songPlaying = " + title + " song_selected = " + title);

                        /*if (songPlaying == song_selected) {
                            Log.d(TAG, "seekBar: The same song from pause to play");

                            //mediaOperation.setSeekTo(current_position);
                        } else {
                            Log.d(TAG, "seekBar: The song was different from pause to play, stop!");
                            songPlaying = song_selected;
                            mediaOperation.doStop();
                            //current_position = 0;
                        }*/
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "onStopTrackingTouch <");

                linearLayoutSpeed.setVisibility(View.VISIBLE);
                frameLayoutDown.setVisibility(View.GONE);

                if (current_song_duration != 0) {

                    //NumberFormat f = new DecimalFormat("00");
                    //NumberFormat f2 = new DecimalFormat("000");

                    double per_unit = (double) current_song_duration / 1000.0;


                    double duration = seekBar.getProgress() * per_unit;

                    Log.e(TAG, "unit = " + String.valueOf(per_unit) + " duration = " + String.valueOf(duration));



                    setSongDuration((int) duration);
                    //setActionBarTitle((int)duration);

                    if (isPlayPress) { //play is pressed, state: pause -> start
                        if (mediaOperation.getCurrent_state() == Constants.STATE.Paused) {
                            //if (audioOperation.isPause()) {
                            mediaOperation.setSeekTo((int) duration);
                            mediaOperation.doPlay(songPath);

                            //audioOperation.setCurrentPosition(duration/1000.0);
                            //audioOperation.doPlay(songList.get(song_selected).getPath());
                        } else {
                            mediaOperation.doPlay(songPath);
                            //audioOperation.doPlay(songList.get(song_selected).getPath());
                        }
                    } else {
                        current_position = (int) duration;
                        //current_position_d = duration/1000.0;
                        mediaOperation.setCurrentPosition(current_position);
                        //audioOperation.setCurrentPosition(current_position_d);
                    }


                } else {
                    Log.e(TAG, "current_song_duration = 0");
                }
            }
        });

        seekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                String value;
                if (progress < 100) {
                    value = String.valueOf((int)(50.0 + 0.5 * (progress)))+"%";

                } else {
                    value = String.valueOf(progress)+"%";

                }

                //textViewSpeed.setText(value);
                textViewSpeed.setText(getResources().getString(R.string.title_speed, value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "onStartTrackingTouch Speed >");
                timeSeekBar.setVisibility(View.GONE);
                textViewSpeed.setVisibility(View.VISIBLE);

                if (mediaOperation.getCurrent_state() == Constants.STATE.Started) { //if playing, pause
                    mediaOperation.doPause();

                    current_speed = mediaOperation.getSpeed();

                    Log.d(TAG, "original speed = " + current_speed);


                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "onStopTrackingTouch Speed <");

                timeSeekBar.setVisibility(View.VISIBLE);
                textViewSpeed.setVisibility(View.GONE);

                if (seekBarSpeed.getProgress() == 0) { //min speed 0.5f (50%)
                    current_speed = 0.5f;
                } else if (seekBarSpeed.getProgress() > 0 && seekBarSpeed.getProgress() < 100) {
                    current_speed = 0.5f + ((float) seekBarSpeed.getProgress()) * 0.005f;
                } else if (seekBarSpeed.getProgress() >= 100 && seekBarSpeed.getProgress() < 200) {
                    current_speed = seekBarSpeed.getProgress() * 0.01f;
                } else { //speed = 2.0f
                    current_speed = 2.0f;
                }

                Log.d(TAG, "new speed = " + current_speed);

                mediaOperation.setSpeed(current_speed);

                //if (mediaOperation.getCurrent_state() == Constants.STATE.Paused) {
                if (isPlayPress) {
                    //if (audioOperation.isPause()) {
                    //mediaOperation.setSeekTo((int) duration);
                    if (mediaOperation.getCurrent_state() == Constants.STATE.Paused) {
                        mediaOperation.doPlay(songPath);
                    } else {
                        Log.e(TAG, "Not Pause state");
                    }

                    //audioOperation.setCurrentPosition(duration/1000.0);
                    //audioOperation.doPlay(songList.get(song_selected).getPath());
                }
            }
        });

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                String volume = String.valueOf(progress) + "%";
                textViewSpeed.setText(getResources().getString(R.string.title_volume, volume));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                timeSeekBar.setVisibility(View.GONE);
                textViewSpeed.setVisibility(View.VISIBLE);

                if (mediaOperation.getCurrent_state() == Constants.STATE.Started) { //playing, doPause
                    mediaOperation.doPause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "new volume = "+seekBar.getProgress());
                timeSeekBar.setVisibility(View.VISIBLE);
                textViewSpeed.setVisibility(View.GONE);

                current_volume = seekBar.getProgress();

                mediaOperation.setCurrent_volume(current_volume);

                if (isPlayPress) {

                    if (mediaOperation.getCurrent_state() == Constants.STATE.Paused) {
                        //if (audioOperation.isPause()) {
                        //mediaOperation.setSeekTo((int) duration);

                        mediaOperation.doPlay(songPath);

                        //audioOperation.setCurrentPosition(duration/1000.0);
                        //audioOperation.doPlay(songList.get(song_selected).getPath());
                    } else {
                        Log.e(TAG, "Not Pause state");
                    }
                }

                editor = pref.edit();
                editor.putInt("PLAY_VOLUME", current_volume);
                editor.apply();
            }
        });

        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_mark_a = timeSeekBar.getProgress();

                timeSeekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                timeSeekBar.setDotsDrawable(R.drawable.dot);
                timeSeekBar.setmLine(R.drawable.line);
            }
        });

        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_mark_b = timeSeekBar.getProgress();

                timeSeekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                timeSeekBar.setDotsDrawable(R.drawable.dot);
                timeSeekBar.setmLine(R.drawable.line);
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
                    mediaOperation.setSeekTo(current_position);
                    mediaOperation.doPlay(songPath);
                    //mediaOperation.doPlay(songPath);

                    btnPlayOrPause.setImageResource(R.drawable.ic_pause_black_48dp);
                }
            }
        });

        btnSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btnSpeedVolumePress == 0) { //speed mode
                    btnSpeedVolumePress = 1;
                    seekBarSpeed.setVisibility(View.VISIBLE);
                    seekBarVolume.setVisibility(View.GONE);
                    frameLayoutPlay.setVisibility(View.GONE);
                    btnSpeed.setImageResource(R.drawable.ic_volume_up_black_48dp);
                } else if (btnSpeedVolumePress == 1) { //volume mode
                    btnSpeedVolumePress = 2;
                    seekBarSpeed.setVisibility(View.GONE);
                    seekBarVolume.setVisibility(View.VISIBLE);
                    frameLayoutPlay.setVisibility(View.GONE);
                    btnSpeed.setImageResource(R.drawable.ic_undo_black_48dp);
                } else { // btnSpeedVolumePress = 2
                    btnSpeedVolumePress = 0;
                    seekBarSpeed.setVisibility(View.GONE);
                    seekBarVolume.setVisibility(View.GONE);
                    frameLayoutPlay.setVisibility(View.VISIBLE);
                    btnSpeed.setImageResource(R.drawable.ic_access_time_black_48dp);
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

        mediaOperation.doStop();

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

    public static void setSongDuration(int timeStamp) {

        NumberFormat f = new DecimalFormat("00");
        NumberFormat f2 = new DecimalFormat("000");


        int minutes = (timeStamp/60000);

        int seconds = (timeStamp/1000) % 60;

        int minisec = (timeStamp%1000);

        textViewTime.setText(f.format(minutes)+":"+f.format(seconds)+"."+f2.format(minisec));
        //toast(f.format(minutes)+":"+f.format(seconds)+"."+f2.format(minisec));
    }

    public static void setSongDuration2(int timeStamp) {

        NumberFormat f = new DecimalFormat("00");
        NumberFormat f2 = new DecimalFormat("000");


        int minutes = (timeStamp/60000);

        int seconds = (timeStamp/1000) % 60;

        //int minisec = (timeStamp%1000);

        textViewTimeElapsed.setText(f.format(minutes)+":"+f.format(seconds));
        //toast(f.format(minutes)+":"+f.format(seconds)+"."+f2.format(minisec));
    }

    public void toast(String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
