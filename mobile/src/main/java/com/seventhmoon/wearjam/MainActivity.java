package com.seventhmoon.wearjam;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.seventhmoon.wearjam.Data.Constants;
import com.seventhmoon.wearjam.Data.DottedSeekBar;
import com.seventhmoon.wearjam.Data.FileChooseItem;
import com.seventhmoon.wearjam.Data.MediaOperation;
import com.seventhmoon.wearjam.Data.Song;
import com.seventhmoon.wearjam.Data.SongArrayAdapter;
import com.seventhmoon.wearjam.Service.GetSongListFromRecordService;
import com.seventhmoon.wearjam.Service.SaveListToFileService;
import com.seventhmoon.wearjam.Service.SearchFileService;
import com.seventhmoon.wearjam.Service.UploadToWatchService;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.seventhmoon.wearjam.Data.FileOperation.check_file_exist;
import static com.seventhmoon.wearjam.Data.FileOperation.check_record_exist;
import static com.seventhmoon.wearjam.Data.FileOperation.init_folder_and_files;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    public static final int MODE_PLAY_ALL = 0;
    public static final int MODE_PLAY_SHUFFLE = 1;
    public static final int MODE_PLAY_REPEAT = 2;
    public static final int MODE_PLAY_AB_LOOP = 3;
    public static final int MODE_PLAY_VIDEO = 4;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    static SharedPreferences pref ;
    static SharedPreferences.Editor editor;
    private static final String FILE_NAME = "Preference";

    private Context context;
    private ListView myListview;
    public static SongArrayAdapter songArrayAdapter;


    MenuItem item_search, item_play_all, item_shuffle, item_single_repeat, item_ab_loop;

    public static MenuItem item_upload_watch;
    public static ActionBar actionBar;
    public static LinearLayout linearLayoutAB;
    public static LinearLayout layout_seekbar_time;
    public static LinearLayout linearSpeed;
    public static TextView songDuration;
    public static DottedSeekBar seekBar;
    private static DottedSeekBar speedBar;
    public static ImageView markButtonA, markButtonB;
    public static EditText textA, textB;
    TextView textSpeed;
    ImageView btnClear;

    private ImageView imgPlayOrPause;
    ImageView imgSkipPrev;
    ImageView imgSkipNext;
    ImageView imgFastRewind;
    ImageView imgFastForward;

    //private MediaPlayer mediaPlayer;
    public static ArrayList<Song> songList = new ArrayList<>();
    //for add songs to list
    public static ArrayList<String> searchList = new ArrayList<>();
    public static ArrayList<Song> addSongList = new ArrayList<>();

    //for upload to watch
    public static ArrayList<String> uploadList = new ArrayList<>();

    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;
    public static int song_selected = 0;
    public static int video_selected = 0;
    public static int current_mode = MODE_PLAY_ALL;
    public static int previos_mode = MODE_PLAY_ALL;
    public static int current_volume = 50;
    public static MediaOperation mediaOperation;
    //AudioOperation audioOperation;
    public static int current_song_duration = 0;
    public static int current_video_duration = 0;

    public static int progress_mark_a = 0;
    public static int progress_mark_b = 1000;
    //private DateFormat formatter;
    //private static boolean is_seekBarTouch = false;
    private static boolean is_editMarkA_change = false;
    private static boolean is_editMarkB_change = false;

    public static int current_position = 0;
    public static int current_video_position = 0;
    private static float current_speed = 0;

    public static ProgressDialog loadDialog = null;

    //public static int currentSongPlay = 0;



    public static int songPlaying = 0;

    private static String currentAcitonBarTitle;
    public static boolean isPlayPress = false;

    private static AlertDialog dialog = null;

    public static MenuItem item_remove, item_clear;
    public static boolean SongChooseLongClick = false;

    public static GoogleApiClient mGoogleApiClient;
    public static Long count_for_upload = 0L;

    private static Long totalUploadSize = 0L;
    private static int totalUploadFileNum = 0;

    private static boolean isSync = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getBaseContext();

        pref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        current_mode = pref.getInt("PLAY_MODE", 0);
        current_volume = pref.getInt("PLAY_VOLUME", 50);

        layout_seekbar_time = findViewById(R.id.layout_seekbar_time);
        linearLayoutAB = findViewById(R.id.layout_ab_loop);
        linearSpeed = findViewById(R.id.linearSpeed);

        songDuration = findViewById(R.id.textSongDuration);

        seekBar = findViewById(R.id.seekBarTime);
        speedBar = findViewById(R.id.seekBarSpeed);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            linearSpeed.setVisibility(View.VISIBLE);
        } else {
            linearSpeed.setVisibility(View.GONE);
        }

        textA = findViewById(R.id.textViewA);
        textB = findViewById(R.id.textViewB);
        textSpeed = findViewById(R.id.textSpeed);

        markButtonA = findViewById(R.id.btnMarkA);
        markButtonB = findViewById(R.id.btnMarkB);
        btnClear = findViewById(R.id.btnClear);

        myListview = findViewById(R.id.listViewMyFavorite);

        imgPlayOrPause = findViewById(R.id.imgPlayOrPause);
        imgSkipPrev = findViewById(R.id.imgSkipPrev);
        imgSkipNext = findViewById(R.id.imgSkipNext);
        imgFastRewind = findViewById(R.id.imgFastRewind);
        imgFastForward = findViewById(R.id.imgFastForward);

        mediaOperation = new MediaOperation(context);
        //audioOperation = new AudioOperation(context);


        mediaOperation.setCurrent_play_mode(current_mode);
        mediaOperation.setCurrent_volume(current_volume);

        if (current_mode == MODE_PLAY_REPEAT) {
            mediaOperation.setLooping(true);
        } else {
            mediaOperation.setLooping(false);
        }
        //audioOperation.setCurrent_play_mode(current_mode);

        //formatter = new SimpleDateFormat("mm:ss");

        songList.clear();



        actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayUseLogoEnabled(true);
            //actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setHomeAsUpIndicator(R.drawable.ic_all_inclusive_white_48dp);
            //actionBar.setTitle("All");

            switch (current_mode) {
                case MODE_PLAY_ALL:
                    actionBar.setHomeAsUpIndicator(R.drawable.ic_all_inclusive_white_48dp);
                    currentAcitonBarTitle = getResources().getString(R.string.play_mode_all);
                    actionBar.setTitle(currentAcitonBarTitle);
                    if (linearLayoutAB != null)
                        linearLayoutAB.setVisibility(View.GONE);
                    break;
                case MODE_PLAY_SHUFFLE:
                    actionBar.setHomeAsUpIndicator(R.drawable.ic_shuffle_white_48dp);
                    currentAcitonBarTitle = getResources().getString(R.string.play_mode_shuffle);
                    actionBar.setTitle(currentAcitonBarTitle);
                    if (linearLayoutAB != null)
                        linearLayoutAB.setVisibility(View.GONE);
                    break;

                case MODE_PLAY_REPEAT:
                    actionBar.setHomeAsUpIndicator(R.drawable.ic_repeat_white_48dp);
                    currentAcitonBarTitle = getResources().getString(R.string.play_mode_repeat);
                    actionBar.setTitle(currentAcitonBarTitle);
                    if (linearLayoutAB != null)
                        linearLayoutAB.setVisibility(View.GONE);
                    break;

                case MODE_PLAY_AB_LOOP:
                    actionBar.setHomeAsUpIndicator(R.drawable.ic_loop_white_48dp);
                    currentAcitonBarTitle = getResources().getString(R.string.play_mode_ab_loop);
                    actionBar.setTitle(currentAcitonBarTitle);
                    if (linearLayoutAB != null)
                        linearLayoutAB.setVisibility(View.VISIBLE);
                    break;

                default:
                    current_mode = MODE_PLAY_ALL;
                    actionBar.setHomeAsUpIndicator(R.drawable.ic_all_inclusive_white_48dp);
                    currentAcitonBarTitle = getResources().getString(R.string.play_mode_all);
                    actionBar.setTitle(currentAcitonBarTitle);
                    if (linearLayoutAB != null)
                        linearLayoutAB.setVisibility(View.GONE);
                    break;

            }
        }



        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            initGoogleApi();
            init_folder_and_files();
            loadFunc();

        } else {
            if(checkAndRequestPermissions()) {
                // carry on the normal flow, as the case of  permissions  granted.
                initGoogleApi();
                init_folder_and_files();
                loadFunc();
            }
        }

        IntentFilter filter;

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(Constants.ACTION.GET_SONGLIST_FROM_RECORD_FILE_COMPLETE)) {

                    if (songList.size() > 0) {

                        songArrayAdapter = new SongArrayAdapter(context, R.layout.music_list_item, songList);
                        myListview.setAdapter(songArrayAdapter);

                        if (loadDialog != null)
                            loadDialog.dismiss();
                        //set shuffle list
                        mediaOperation.shuffleReset();

                        NumberFormat f = new DecimalFormat("00");
                        NumberFormat f2 = new DecimalFormat("000");

                        switch (current_mode) {
                            case MODE_PLAY_ALL:
                                song_selected = 0;
                                current_song_duration = (int) (songList.get(song_selected).getDuration_u() / 1000);
                                break;
                            case MODE_PLAY_SHUFFLE:
                                song_selected = mediaOperation.getShufflePosition();
                                current_song_duration = (int) (songList.get(song_selected).getDuration_u() / 1000);
                                break;
                            case MODE_PLAY_REPEAT:
                                song_selected = 0;
                                current_song_duration = (int) (songList.get(song_selected).getDuration_u() / 1000);
                                break;
                            case MODE_PLAY_AB_LOOP:
                                song_selected = 0;
                                current_song_duration = (int) (songList.get(song_selected).getDuration_u() / 1000);

                                progress_mark_a = (int) ((float) songList.get(song_selected).getMark_a() / (float) current_song_duration * 1000.0);
                                progress_mark_b = (int) ((float) songList.get(song_selected).getMark_b() / (float) current_song_duration * 1000.0);

                                int minutes_a = songList.get(song_selected).getMark_a() / 60000;
                                int seconds_a = (songList.get(song_selected).getMark_a() / 1000) % 60;
                                int minisec_a = songList.get(song_selected).getMark_a() % 1000;

                                int minutes_b = songList.get(song_selected).getMark_b() / 60000;
                                int seconds_b = (songList.get(song_selected).getMark_b() / 1000) % 60;
                                int minisec_b = songList.get(song_selected).getMark_b() % 1000;

                                seekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                                seekBar.setDotsDrawable(R.drawable.dot);
                                seekBar.setmLine(R.drawable.line);

                                textA.setText(f.format(minutes_a) + ":" + f.format(seconds_a) + "." + f2.format(minisec_a));
                                textB.setText(f.format(minutes_b) + ":" + f.format(seconds_b) + "." + f2.format(minisec_b));
                                break;
                        }


                        //deselect other
                        for (int i = 0; i < songList.size(); i++) {

                            if (i == song_selected) {
                                songList.get(i).setSelected(true);

                            } else {
                                songList.get(i).setSelected(false);

                            }
                        }

                        //show item
                        if (item_remove != null) {
                            item_remove.setVisible(true);
                        }
                        if (item_clear != null) {
                            item_clear.setVisible(true);
                        }

                    } else {
                        if (loadDialog != null)
                            loadDialog.dismiss();

                        if (item_remove != null) {
                            item_remove.setVisible(false);
                        }
                        if (item_clear != null) {
                            item_clear.setVisible(false);
                        }

                        toast(getResources().getString(R.string.list_empty));
                    }



                } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.ADD_SONG_LIST_COMPLETE)) {
                    Log.d(TAG, "receive ADD_SONG_LIST_COMPLETE !");


                    for (int i=0; i<addSongList.size(); i++) {
                        songList.add(addSongList.get(i));
                        Log.d(TAG, "add "+addSongList.get(i).getName()+" to songList");
                    }

                    mediaOperation.shuffleReset();
                    mediaOperation.setShufflePosition(0);

                    if (songArrayAdapter == null) {
                        songArrayAdapter = new SongArrayAdapter(context, R.layout.music_list_item, songList);
                        myListview.setAdapter(songArrayAdapter);
                    } else {
                        Log.e(TAG, "notifyDataSetChanged");
                        songArrayAdapter.notifyDataSetChanged();
                    }



                    Intent saveintent = new Intent(context, SaveListToFileService.class);
                    saveintent.setAction(Constants.ACTION.SAVE_SONGLIST_ACTION);
                    saveintent.putExtra("FILENAME", "favorite");
                    context.startService(saveintent);

                    loadDialog = new ProgressDialog(MainActivity.this);
                    loadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    loadDialog.setTitle("Saving...");
                    loadDialog.setIndeterminate(false);
                    loadDialog.setCancelable(false);

                    loadDialog.show();

                    //clear list

                    //show item
                    if (item_remove != null) {
                        item_remove.setVisible(true);
                    }
                    if (item_clear != null) {
                        item_clear.setVisible(true);
                    }


                } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.ADD_SONG_LIST_CHANGE)) {
                    if (songArrayAdapter != null) {
                        songArrayAdapter.notifyDataSetChanged();
                    }
                } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.GET_PLAY_COMPLETE)) {
                    Log.d(TAG, "receive GET_PLAY_COMPLETE !");
                    imgPlayOrPause.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
                } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.MEDIAPLAYER_STATE_PLAYED)) {
                    Log.d(TAG, "receive MEDIAPLAYER_STATE_STARTED !("+song_selected+")");
                    //set click true
                    //imgPlayOrPause.setClickable(true);

                    imgPlayOrPause.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);

                    myListview.smoothScrollToPosition(song_selected);

                    for (int i=0; i<songList.size(); i++) {


                        if (i == song_selected) {
                            songList.get(i).setSelected(true);

                        } else {
                            songList.get(i).setSelected(false);

                        }



                    }
                    myListview.invalidateViews();


                } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.MEDIAPLAYER_STATE_PAUSED)) {
                    Log.d(TAG, "receive MEDIAPLAYER_STATE_PAUSED !");
                    //imgPlayOrPause.setClickable(true);
                    imgPlayOrPause.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
                } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.SAVE_SONGLIST_TO_FILE_COMPLETE)) {
                    if (loadDialog != null)
                        loadDialog.dismiss();
                } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.GET_UPLOAD_SONG_COMPLETE)) {
                    Log.d(TAG, "receive GET_UPLOAD_SONG_COMPLETE !");
                    //get a upload complete command, try to send next
                    if (uploadList.size() > 0) {
                        Intent uploadIntent = new Intent(MainActivity.this, UploadToWatchService.class);
                        uploadIntent.setAction(Constants.ACTION.UPLOAD_SONGS_TO_WATCH_ACTION);
                        startService(uploadIntent);
                    } else {
                        Log.d(TAG, "sendTransferComplete");

                        isSync = false;

                        if(mGoogleApiClient == null) {
                            Log.e(TAG, "mGoogleApiClient = null");
                        } else {
                            if (mGoogleApiClient.isConnected()) {
                                PutDataMapRequest putRequest = PutDataMapRequest.create("/MOBILE_COMMAND");
                                DataMap map = putRequest.getDataMap();
                                //map.putInt("color", Color.RED);
                                count_for_upload++;
                                map.putString("cmd", "TransferComplete");
                                map.putLong("count", count_for_upload);
                                Wearable.DataApi.putDataItem(mGoogleApiClient, putRequest.asPutDataRequest());
                            } else {
                                Log.e(TAG, "mGoogleApiClient is disconnected");
                            }


                        }
                    }
                } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.UPLOAD_SONGS_DIALOG_ACTION)) {
                    Log.d(TAG, "receive UPLOAD_SONGS_DIALOG_ACTION !");
                    String spaceString = intent.getStringExtra("watch_space");
                    Log.e(TAG, "get free space = "+spaceString);


                    AlertDialog.Builder confirmdialog = new AlertDialog.Builder(MainActivity.this);
                    confirmdialog.setTitle("Sync to cell phone");
                    confirmdialog.setMessage("Will send "+totalUploadFileNum+" file(s) "+totalUploadSize/1048576+" MBytes, your watch available space "+Long.valueOf(spaceString)+" MBytes.");
                    confirmdialog.setIcon(R.mipmap.ic_launcher);
                    confirmdialog.setCancelable(false);
                    confirmdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            //set sync true
                            isSync = true;

                            Intent uploadIntent = new Intent(MainActivity.this, UploadToWatchService.class);
                            uploadIntent.setAction(Constants.ACTION.UPLOAD_SONGS_TO_WATCH_ACTION);
                            startService(uploadIntent);
                        }
                    });
                    confirmdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    });

                    confirmdialog.show();
                }
            }
        };


        if (!isRegister) {
            filter = new IntentFilter();
            filter.addAction(Constants.ACTION.ADD_SONG_LIST_COMPLETE);
            filter.addAction(Constants.ACTION.GET_PLAY_COMPLETE);
            filter.addAction(Constants.ACTION.GET_SONGLIST_FROM_RECORD_FILE_COMPLETE);
            filter.addAction(Constants.ACTION.SAVE_SONGLIST_TO_FILE_COMPLETE);
            filter.addAction(Constants.ACTION.MEDIAPLAYER_STATE_PLAYED);
            filter.addAction(Constants.ACTION.MEDIAPLAYER_STATE_PAUSED);
            filter.addAction(Constants.ACTION.GET_UPLOAD_SONG_COMPLETE);
            filter.addAction(Constants.ACTION.UPLOAD_SONGS_DIALOG_ACTION);
            context.registerReceiver(mReceiver, filter);
            isRegister = true;
            Log.d(TAG, "registerReceiver mReceiver");
        }

        switch (current_mode) {
            case MODE_PLAY_ALL:
                if (linearLayoutAB != null)
                    linearLayoutAB.setVisibility(View.GONE);
                break;
            case MODE_PLAY_SHUFFLE:
                if (linearLayoutAB != null)
                    linearLayoutAB.setVisibility(View.GONE);
                break;

            case MODE_PLAY_REPEAT:
                if (linearLayoutAB != null)
                    linearLayoutAB.setVisibility(View.GONE);
                break;

            case MODE_PLAY_AB_LOOP:
                if (linearLayoutAB != null) {
                    linearLayoutAB.setVisibility(View.VISIBLE);

                    seekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                    seekBar.setDotsDrawable(R.drawable.dot);
                    seekBar.setmLine(R.drawable.line);
                }
                break;

        }
    }

    private void initGoogleApi() {
        Log.d(TAG, "initGoogleApi");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.e(TAG, "mGoogleApiClient is connected.");
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                    }
                })
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void loadFunc() {
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String value;
                if (progress < 100) {
                    value = String.valueOf((int)(50.0 + 0.5 * (progress)))+"%";

                } else {
                    value = String.valueOf(progress)+"%";

                }

                textSpeed.setText(value);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "onStartTrackingTouch Speed >");

                if (songList.size() > 0) {

                    if (mediaOperation.getCurrent_state() == Constants.STATE.Started) { //if playing, pause
                        mediaOperation.doPause();

                        current_speed = mediaOperation.getSpeed();

                        Log.d(TAG, "original speed = " + current_speed);


                    }
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "onStopTrackingTouch Speed <");
                if (songList.size() > 0) {

                    if (speedBar.getProgress() == 0) { //min speed 0.5f (50%)
                        current_speed = 0.5f;
                    } else if (speedBar.getProgress() > 0 && speedBar.getProgress() < 100) {
                        current_speed = 0.5f + ((float) speedBar.getProgress()) * 0.005f;
                    } else if (speedBar.getProgress() >= 100 && speedBar.getProgress() < 200) {
                        current_speed = speedBar.getProgress() * 0.01f;
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
                            mediaOperation.doPlay(songList.get(song_selected).getPath());
                        } else {
                            Log.e(TAG, "Not Pause state");
                        }

                        //audioOperation.setCurrentPosition(duration/1000.0);
                        //audioOperation.doPlay(songList.get(song_selected).getPath());
                    }
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Log.e(TAG, "=> onProgressChanged");

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
                        //setActionBarTitle((int) duration);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "onStartTrackingTouch >");

                if (songList.size() > 0) {

                    if (isPlayPress) { //play is pressed
                        if (mediaOperation.getCurrent_state() == Constants.STATE.Started) { //if playing, pause
                            mediaOperation.doPause();
                            Log.d(TAG, "songPlaying = " + songPlaying + " song_selected = " + song_selected);

                            if (songPlaying == song_selected) {
                                Log.d(TAG, "seekBar: The same song from pause to play");

                                //mediaOperation.setSeekTo(current_position);
                            } else {
                                Log.d(TAG, "seekBar: The song was different from pause to play, stop!");
                                songPlaying = song_selected;
                                mediaOperation.doStop();
                                //current_position = 0;
                            }
                        }
                    }

                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "onStopTrackingTouch <");

                if (songList.size() > 0) {
                    //use seekbar, set seekbar value for mark
                    //is_seekBarTouch = true;
                    is_editMarkA_change = false;
                    is_editMarkB_change = false;

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
                                mediaOperation.doPlay(songList.get(song_selected).getPath());

                                //audioOperation.setCurrentPosition(duration/1000.0);
                                //audioOperation.doPlay(songList.get(song_selected).getPath());
                            } else {
                                mediaOperation.doPlay(songList.get(song_selected).getPath());
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
            }
        });



        textA.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "text A beforeTextChanged");
                is_editMarkA_change = true;
                //is_seekBarTouch = false;
            }
        });

        markButtonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (songList.size() > 0) {

                    if (current_song_duration != 0) {

                        if (is_editMarkA_change) {
                            String time[];
                            String secs[];

                            if (textA.getText().length() > 0) {

                                time = textA.getText().toString().split(":");

                                if (time.length == 1) {
                                    Log.d(TAG, "can't find ( : )");
                                    toast(getResources().getString(R.string.invalid_input));

                                } else if (time.length > 2) {
                                    Log.d(TAG, "Invalid input!");
                                    toast(getResources().getString(R.string.invalid_input));
                                } else {

                                    secs = time[1].split("\\.");

                                    if (secs.length == 0) {
                                        Log.d(TAG, "can't find ( . )");
                                        toast(getResources().getString(R.string.invalid_input));
                                    } else {

                                        if (textA.getText().length() < 9) {
                                            Log.d(TAG, "Mark A range is invalid");
                                            toast(getResources().getString(R.string.mark_a) + " " + getResources().getString(R.string.range_invalid));
                                        } else if (time[0].length() != 2 && time[1].length() != 6) {
                                            Log.d(TAG, "Mark A range is invalid");
                                            toast(getResources().getString(R.string.mark_a) + " " + getResources().getString(R.string.range_invalid));
                                        } else if (secs[0].length() != 2 && secs[1].length() != 3) {
                                            Log.d(TAG, "Mark A range is invalid");
                                            toast(getResources().getString(R.string.mark_a) + " " + getResources().getString(R.string.range_invalid));
                                        } else if (!isNumber(time[0]) ||
                                                !isNumber(secs[0]) ||
                                                !isNumber(secs[1])) {
                                            Log.d(TAG, "Invalid input!");
                                            toast(getResources().getString(R.string.invalid_input));
                                        } else {


                                            int duration = Integer.valueOf(time[0]) * 60000;
                                            duration += Integer.valueOf(secs[0]) * 1000;
                                            duration += Integer.valueOf(secs[1]);

                                            progress_mark_a = (duration * 1000) / current_song_duration;

                                            if (progress_mark_a < 1000) {
                                                seekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                                                seekBar.setDotsDrawable(R.drawable.dot);

                                                seekBar.setmLine(R.drawable.line);
                                            } else {
                                                toast("Mark A value must less than song duration");
                                                toast(getResources().getString(R.string.mark_a) + " " + getResources().getString(R.string.must_be_less_than_song_duration));
                                            }


                                        }
                                    }
                                }
                            } else {
                                toast("Mark A range should not be empty");
                            }
                            is_editMarkA_change = false;
                        } else { //get current seekbar position
                            //NumberFormat f = new DecimalFormat("00");
                            //NumberFormat f2 = new DecimalFormat("000");

                            double per_unit = (double) current_song_duration / 1000.0;


                            double duration = seekBar.getProgress() * per_unit;

                            Log.e(TAG, "unit = " + String.valueOf(per_unit) + " duration = " + String.valueOf(duration));

                            //int minutes = ((int) duration) / 60000;

                            //int seconds = ((int) duration / 1000) % 60;

                            //int minisec = (int) duration % 1000;


                            //textA.setText(f.format(minutes) + ":" + f.format(seconds) + "." + f2.format(minisec));
                            setTextAudioA(duration);


                            progress_mark_a = seekBar.getProgress();
                            seekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                            seekBar.setDotsDrawable(R.drawable.dot);
                            seekBar.setmLine(R.drawable.line);

                            if (current_mode == MODE_PLAY_AB_LOOP) {
                                songList.get(song_selected).setMark_a((int) duration);
                            }
                        }


                        if (progress_mark_b <= progress_mark_a) {
                            toast("Mark B must greater than Mark A");

                            progress_mark_a = 0;
                            progress_mark_b = 1000;

                            songList.get(song_selected).setMark_a(0);
                            songList.get(song_selected).setMark_b(current_song_duration);

                            seekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                            seekBar.setDotsDrawable(R.drawable.dot);
                            seekBar.setmLine(R.drawable.line);

                            mediaOperation.setAb_loop_start(0);
                            mediaOperation.setAb_loop_end(current_song_duration);
                        } else {
                            Log.e(TAG, "Mark A reset");
                            //if (current_mode == MODE_PLAY_AB_LOOP) {
                            mediaOperation.setAb_loop_start(songList.get(song_selected).getMark_a());
                            mediaOperation.setAb_loop_end(songList.get(song_selected).getMark_b());
                            //}
                            if (songPlaying != song_selected) {
                                //NumberFormat f = new DecimalFormat("00");
                                //NumberFormat f2 = new DecimalFormat("000");

                                double per_unit = (double) current_song_duration / 1000.0;


                                double duration = seekBar.getProgress() * per_unit;

                                Log.e(TAG, "unit = " + String.valueOf(per_unit) + " duration = " + String.valueOf(duration));

                                //int minutes = ((int) duration) / 60000;

                                //int seconds = ((int) duration / 1000) % 60;

                                //int minisec = (int) duration % 1000;


                                //textA.setText(f.format(minutes) + ":" + f.format(seconds) + "." + f2.format(minisec));
                                setTextAudioA(duration);

                                progress_mark_a = seekBar.getProgress();

                                if (progress_mark_b == 1000) {
                                    Log.e(TAG, "progress_mark_b = 1000");
                                    setTextAudioB(current_song_duration);
                                }

                                seekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                                seekBar.setDotsDrawable(R.drawable.dot);
                                seekBar.setmLine(R.drawable.line);

                                if (current_mode == MODE_PLAY_AB_LOOP) {
                                    songList.get(song_selected).setMark_a((int) duration);
                                }
                            }
                        }
                    }
                }



                //Log.e(TAG, "textA "+textA.getText().toString());

                //seekBar.setDots(new int[] {25, 50, 75});
                //seekBar.setDotsDrawable(R.drawable.dot);
            }
        });

        textB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "text B beforeTextChanged");
                is_editMarkB_change = true;
                //is_seekBarTouch = false;
            }
        });

        markButtonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songList.size() > 0) {
                    if (current_song_duration != 0) {

                        if (is_editMarkB_change) {
                            String time[];
                            String secs[];

                            if (textB.getText().length() > 0) {

                                time = textB.getText().toString().split(":");

                                if (time.length == 1) {
                                    toast("can't find ( : )");
                                } else if (time.length > 2) {
                                    toast("Invalid input!");
                                } else {

                                    secs = time[1].split("\\.");

                                    if (secs.length == 0) {
                                        toast("can't find ( . )");
                                    } else {

                                        if (textB.getText().length() < 9) {
                                            toast("Mark B range is invalid");
                                        } else if (time[0].length() != 2 && time[1].length() != 6) {
                                            toast("Mark B range is invalid");
                                        } else if (secs[0].length() != 2 && secs[1].length() != 3) {
                                            toast("Mark B range is invalid");
                                        } else if (!isNumber(time[0]) ||
                                                !isNumber(secs[0]) ||
                                                !isNumber(secs[1])) {
                                            toast("Invalid input!");
                                        } else {


                                            int duration = Integer.valueOf(time[0]) * 60000;
                                            duration += Integer.valueOf(secs[0]) * 1000;
                                            duration += Integer.valueOf(secs[1]);

                                            progress_mark_b = (duration * 1000) / current_song_duration;

                                            if (progress_mark_b <= 1000) {

                                                seekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                                                seekBar.setDotsDrawable(R.drawable.dot);

                                                seekBar.setmLine(R.drawable.line);
                                            } else {
                                                toast("Mark B value must less than song duration or equal to it");
                                            }
                                        }
                                    }
                                }
                            } else {
                                toast("Mark A range should not be empty");
                            }
                            is_editMarkB_change = false;
                        } else {
                            //NumberFormat f = new DecimalFormat("00");
                            //NumberFormat f2 = new DecimalFormat("000");

                            double per_unit = (double) current_song_duration / 1000.0;


                            double duration = seekBar.getProgress() * per_unit;

                            Log.e(TAG, "unit = " + String.valueOf(per_unit) + " duration = " + String.valueOf(duration));

                            //int minutes = ((int) duration) / 60000;

                            //int seconds = ((int) duration / 1000) % 60;

                            //int minisec = (int) duration % 1000;


                            //textB.setText(f.format(minutes) + ":" + f.format(seconds) + "." + f2.format(minisec));
                            setTextAudioB(duration);

                            progress_mark_b = seekBar.getProgress();
                            seekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                            seekBar.setDotsDrawable(R.drawable.dot);
                            seekBar.setmLine(R.drawable.line);

                            if (current_mode == MODE_PLAY_AB_LOOP) {
                                songList.get(song_selected).setMark_b((int) duration);
                            }
                        }

                        if (progress_mark_b <= progress_mark_a) {
                            toast("Mark B must greater than Mark A");

                            progress_mark_a = 0;
                            progress_mark_b = 1000;

                            songList.get(song_selected).setMark_a(0);
                            songList.get(song_selected).setMark_b(current_song_duration);

                            seekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                            seekBar.setDotsDrawable(R.drawable.dot);
                            seekBar.setmLine(R.drawable.line);

                            mediaOperation.setAb_loop_start(0);
                            mediaOperation.setAb_loop_end(current_song_duration);
                        } else {
                            Log.e(TAG, "Mark B reset");
                            //if (current_mode == MODE_PLAY_AB_LOOP) {
                            mediaOperation.setAb_loop_start(songList.get(song_selected).getMark_a());
                            mediaOperation.setAb_loop_end(songList.get(song_selected).getMark_b());
                            //}
                            if (songPlaying != song_selected) {
                                NumberFormat f = new DecimalFormat("00");
                                NumberFormat f2 = new DecimalFormat("000");

                                double per_unit = (double) current_song_duration / 1000.0;


                                double duration = seekBar.getProgress() * per_unit;

                                Log.e(TAG, "unit = " + String.valueOf(per_unit) + " duration = " + String.valueOf(duration));

                                int minutes = ((int) duration) / 60000;

                                int seconds = ((int) duration / 1000) % 60;

                                int minisec = (int) duration % 1000;


                                textA.setText(f.format(minutes) + ":" + f.format(seconds) + "." + f2.format(minisec));

                                progress_mark_b = seekBar.getProgress();
                                seekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                                seekBar.setDotsDrawable(R.drawable.dot);
                                seekBar.setmLine(R.drawable.line);

                                if (current_mode == MODE_PLAY_AB_LOOP) {
                                    songList.get(song_selected).setMark_b((int) duration);
                                }
                            }
                        }
                    }
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (songList.size() > 0) {

                    seekBar.setDots(new int[]{});
                    seekBar.setDotsDrawable(R.drawable.dot);

                    progress_mark_a = 0;
                    String ta = "00:00.000";
                    textA.setText(ta);

                    progress_mark_b = 1000;
                    String tb = "00:00.000";
                    textB.setText(tb);

                    songList.get(song_selected).setMark_a(0);
                    songList.get(song_selected).setMark_b(current_song_duration);

                    mediaOperation.setAb_loop_start(0);
                    mediaOperation.setAb_loop_end(current_song_duration);
                }
            }
        });

        imgPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songList.size() > 0) { //check if songs exist in list
                    //set click disable
                    //imgPlayOrPause.setClickable(false);

                    if (mediaOperation.getCurrent_state() == Constants.STATE.Started) { //if playing, pause

                        Log.d(TAG, "[imgPlayOrPause] isPlaying, songPlaying = "+songPlaying);

                        mediaOperation.setTaskStop();

                        isPlayPress = false;

                        mediaOperation.doPause();
                        current_position = mediaOperation.getCurrentPosition();
                        Log.e(TAG, "===> current_position = "+current_position);

                    } else {
                        Log.d(TAG, "[imgPlayOrPause] state is paused, stopped...");

                        mediaOperation.setTaskStart();

                        String songPath, songName;
                        isPlayPress = true;



                        if (song_selected > 0) { //if selected, get selected
                            songPath = songArrayAdapter.getItem(song_selected).getPath();
                            songName = songArrayAdapter.getItem(song_selected).getName();

                            current_song_duration = (int)(songList.get(song_selected).getDuration_u()/1000);
                        } else { //else use first

                            if (current_mode == MODE_PLAY_SHUFFLE) {
                                songPath = songList.get(mediaOperation.getShufflePosition()).getPath();
                                songName = songList.get(mediaOperation.getShufflePosition()).getName();
                                song_selected = mediaOperation.getShufflePosition();

                                //deselect other
                                for (int i=0; i<songList.size(); i++) {

                                    if (i == song_selected) {
                                        songList.get(i).setSelected(true);

                                    } else {
                                        songList.get(i).setSelected(false);

                                    }
                                }

                                current_song_duration = (int)(songList.get(song_selected).getDuration_u()/1000);
                            } else {

                                songPath = songList.get(0).getPath();
                                songName = songList.get(0).getName();
                                current_song_duration = (int)(songList.get(0).getDuration_u()/1000);
                            }
                        }





                        myListview.invalidateViews();

                        if (mediaOperation.getCurrent_state() == Constants.STATE.Paused) {
                            if (songPlaying == song_selected) {
                                Log.d(TAG, "The same song from pause to play");

                                if (current_mode == MODE_PLAY_AB_LOOP) {
                                    if (current_position >= songList.get(song_selected).getMark_b()) {
                                        current_position = songList.get(song_selected).getMark_a();
                                    }
                                    mediaOperation.setLooping(false);
                                } else if (current_mode == MODE_PLAY_REPEAT) {
                                    mediaOperation.setLooping(true);
                                } else {
                                    mediaOperation.setLooping(false);
                                }
                            } else {
                                Log.d(TAG, "The song was different from pause to play, stop!");
                                mediaOperation.doStop();

                                songPlaying = song_selected;


                                if (current_mode == MODE_PLAY_AB_LOOP) {
                                    current_position = songList.get(song_selected).getMark_a();
                                    mediaOperation.setAb_loop_start(songList.get(song_selected).getMark_a());
                                    mediaOperation.setAb_loop_end(songList.get(song_selected).getMark_b());
                                } else {
                                    current_position = 0;
                                }
                            }
                        } else { //not pause, maybe stop
                            Log.d(TAG, "not pause, maybe stop");

                            if (current_mode == MODE_PLAY_AB_LOOP) {
                                NumberFormat f = new DecimalFormat("00");
                                NumberFormat f2 = new DecimalFormat("000");

                                progress_mark_a = (int) ((float) songList.get(song_selected).getMark_a() / (float) current_song_duration * 1000.0);
                                progress_mark_b = (int) ((float) songList.get(song_selected).getMark_b() / (float) current_song_duration * 1000.0);

                                int minutes_a = songList.get(song_selected).getMark_a() / 60000;
                                int seconds_a = (songList.get(song_selected).getMark_a() / 1000) % 60;
                                int minisec_a = songList.get(song_selected).getMark_a() % 1000;

                                int minutes_b = songList.get(song_selected).getMark_b() / 60000;
                                int seconds_b = (songList.get(song_selected).getMark_b() / 1000) % 60;
                                int minisec_b = songList.get(song_selected).getMark_b() % 1000;

                                seekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                                seekBar.setDotsDrawable(R.drawable.dot);
                                seekBar.setmLine(R.drawable.line);

                                textA.setText(f.format(minutes_a) + ":" + f.format(seconds_a) + "." + f2.format(minisec_a));
                                textB.setText(f.format(minutes_b) + ":" + f.format(seconds_b) + "." + f2.format(minisec_b));

                                current_position = songList.get(song_selected).getMark_a();
                                mediaOperation.setAb_loop_start(songList.get(song_selected).getMark_a());
                                mediaOperation.setAb_loop_end(songList.get(song_selected).getMark_b());
                            }
                        }





                        Log.d(TAG, "play "+songName+" position = "+current_position);
                        //audioOperation.setCurrentPosition(current_position_d);
                        //audioOperation.doPlay(songPath);
                        mediaOperation.setCurrentPosition(current_position);
                        mediaOperation.doPlay(songPath);
                    }

                } else {
                    toast(getResources().getString(R.string.song_list_empty));
                }
            }
        });

        imgSkipPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "imgSkipPrev");

                if (songList.size() > 0) {

                    if (mediaOperation.getCurrent_state() == Constants.STATE.Started) { //playing
                        mediaOperation.doStop();
                        mediaOperation.doPrev();
                    } else {
                        if (song_selected > 0 && song_selected < songList.size()) { //song_selected must >= 1
                            song_selected--;
                        } else {
                            song_selected = 0;
                        }

                        //deselect other
                        for (int i = 0; i < songList.size(); i++) {

                            if (i == song_selected) {
                                songList.get(i).setSelected(true);

                            } else {
                                songList.get(i).setSelected(false);

                            }
                        }

                        myListview.invalidateViews();

                        current_song_duration = (int) (songList.get(song_selected).getDuration_u() / 1000);
                    }

                    if (current_song_duration != 0) {

                        NumberFormat f = new DecimalFormat("00");
                        NumberFormat f2 = new DecimalFormat("000");

                        switch (current_mode) {
                            case MODE_PLAY_ALL:
                                break;
                            case MODE_PLAY_SHUFFLE:
                                break;
                            case MODE_PLAY_REPEAT:
                                break;
                            case MODE_PLAY_AB_LOOP:
                                progress_mark_a = (int) ((float) songList.get(song_selected).getMark_a() / (float) current_song_duration * 1000.0);
                                progress_mark_b = (int) ((float) songList.get(song_selected).getMark_b() / (float) current_song_duration * 1000.0);

                                int minutes_a = songList.get(song_selected).getMark_a() / 60000;
                                int seconds_a = (songList.get(song_selected).getMark_a() / 1000) % 60;
                                int minisec_a = songList.get(song_selected).getMark_a() % 1000;

                                int minutes_b = songList.get(song_selected).getMark_b() / 60000;
                                int seconds_b = (songList.get(song_selected).getMark_b() / 1000) % 60;
                                int minisec_b = songList.get(song_selected).getMark_b() % 1000;

                                seekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                                seekBar.setDotsDrawable(R.drawable.dot);
                                seekBar.setmLine(R.drawable.line);

                                textA.setText(f.format(minutes_a) + ":" + f.format(seconds_a) + "." + f2.format(minisec_a));
                                textB.setText(f.format(minutes_b) + ":" + f.format(seconds_b) + "." + f2.format(minisec_b));


                                break;
                        }

                    }
                }
            }
        });

        imgSkipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "imgSkipNext");

                if (songList.size() > 0) {

                    if (mediaOperation.getCurrent_state() == Constants.STATE.Started) { //playing
                        mediaOperation.doStop();
                        mediaOperation.doNext();
                    } else {
                        if (song_selected < songList.size() - 1) { //song_selected must >= 1
                            song_selected++;
                        } else {
                            song_selected = songList.size() - 1;
                        }

                        //deselect other
                        for (int i = 0; i < songList.size(); i++) {

                            if (i == song_selected) {
                                songList.get(i).setSelected(true);

                            } else {
                                songList.get(i).setSelected(false);

                            }
                        }

                        myListview.invalidateViews();

                        current_song_duration = (int) (songList.get(song_selected).getDuration_u() / 1000);
                    }

                    if (current_song_duration != 0) {

                        NumberFormat f = new DecimalFormat("00");
                        NumberFormat f2 = new DecimalFormat("000");

                        switch (current_mode) {
                            case MODE_PLAY_ALL:
                                break;
                            case MODE_PLAY_SHUFFLE:
                                break;
                            case MODE_PLAY_REPEAT:
                                break;
                            case MODE_PLAY_AB_LOOP:
                                progress_mark_a = (int) ((float) songList.get(song_selected).getMark_a() / (float) current_song_duration * 1000.0);
                                progress_mark_b = (int) ((float) songList.get(song_selected).getMark_b() / (float) current_song_duration * 1000.0);

                                int minutes_a = songList.get(song_selected).getMark_a() / 60000;
                                int seconds_a = (songList.get(song_selected).getMark_a() / 1000) % 60;
                                int minisec_a = songList.get(song_selected).getMark_a() % 1000;

                                int minutes_b = songList.get(song_selected).getMark_b() / 60000;
                                int seconds_b = (songList.get(song_selected).getMark_b() / 1000) % 60;
                                int minisec_b = songList.get(song_selected).getMark_b() % 1000;

                                seekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                                seekBar.setDotsDrawable(R.drawable.dot);
                                seekBar.setmLine(R.drawable.line);

                                textA.setText(f.format(minutes_a) + ":" + f.format(seconds_a) + "." + f2.format(minisec_a));
                                textB.setText(f.format(minutes_b) + ":" + f.format(seconds_b) + "." + f2.format(minisec_b));


                                break;
                        }

                    }
                }
            }
        });

        imgFastRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "imgFastRewind");

                if (songList.size() > 0) {

                    Log.d(TAG, "current_position = " + mediaOperation.getCurrentPosition());

                    if (mediaOperation.getCurrent_state() == Constants.STATE.Started) { //if playing, pause, seek to new position then play
                        //if (!audioOperation.isPause()) {

                        mediaOperation.doPause();

                        current_position = mediaOperation.getCurrentPosition();
                        if (current_position > 10000) { //10 seconds
                            current_position = current_position - 10000;
                        } else {
                            current_position = 0;
                        }
                        mediaOperation.setCurrentPosition(current_position);
                        mediaOperation.setSeekTo(current_position);
                        mediaOperation.doPlay(songList.get(song_selected).getPath());
                    } else { //

                        current_position = mediaOperation.getCurrentPosition();

                        if (current_position > 10000) {
                            current_position = current_position - 10000;
                        } else {
                            current_position = 0;
                        }
                        mediaOperation.setCurrentPosition(current_position);

                        setSeekBarLocation(current_position);
                        setSongDuration(current_position);
                    }
                }
            }
        });

        imgFastForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "imgFastForward");

                if (songList.size() > 0) {

                    Log.d(TAG, "current_position = " + mediaOperation.getCurrentPosition());

                    if (!mediaOperation.isPause()) { //if playing, pause, seek to new position then play
                        mediaOperation.doPause();
                        //current_position = mediaOperation.getCurrentPosition()+10000;
                        //current_position = (int)(audioOperation.getCurrentPosition()*1000.0)+10000;

                        current_position = mediaOperation.getCurrentPosition() + 10000;

                        if (current_position >= current_song_duration) {
                            mediaOperation.setSeekTo(current_song_duration);
                            current_position = current_song_duration;
                        }
                        mediaOperation.setCurrentPosition(current_position);
                        mediaOperation.setSeekTo(current_position);
                        mediaOperation.doPlay(songList.get(song_selected).getPath());
                    } else {

                        current_position = mediaOperation.getCurrentPosition() + 10000;

                        if (current_position >= current_song_duration) {
                            mediaOperation.setSeekTo(current_song_duration);
                            current_position = current_song_duration;
                        }
                        mediaOperation.setCurrentPosition(current_position);

                        setSeekBarLocation(current_position);
                        setSongDuration(current_position);
                    }
                }
            }
        });

        myListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "select "+position);

                song_selected = position;

                //deselect other
                for (int i=0; i<songList.size(); i++) {

                    if (i == position) {
                        songList.get(i).setSelected(true);

                    } else {
                        songList.get(i).setSelected(false);

                    }
                }

                myListview.invalidateViews();

                current_song_duration = (int)(songList.get(song_selected).getDuration_u()/1000);

                if (current_song_duration != 0) {

                    NumberFormat f = new DecimalFormat("00");
                    NumberFormat f2 = new DecimalFormat("000");

                    switch (current_mode) {
                        case MODE_PLAY_ALL:
                            break;
                        case MODE_PLAY_SHUFFLE:
                            break;
                        case MODE_PLAY_REPEAT:
                            break;
                        case MODE_PLAY_AB_LOOP:
                            progress_mark_a = (int) ((float) songList.get(position).getMark_a() / (float) current_song_duration * 1000.0);
                            progress_mark_b = (int) ((float) songList.get(position).getMark_b() / (float) current_song_duration * 1000.0);

                            int minutes_a = songList.get(position).getMark_a()/60000;
                            int seconds_a = (songList.get(position).getMark_a()/1000) % 60;
                            int minisec_a = songList.get(position).getMark_a()%1000;

                            int minutes_b = songList.get(position).getMark_b()/60000;
                            int seconds_b = (songList.get(position).getMark_b()/1000) % 60;
                            int minisec_b = songList.get(position).getMark_b()%1000;

                            seekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                            seekBar.setDotsDrawable(R.drawable.dot);
                            seekBar.setmLine(R.drawable.line);

                            textA.setText(f.format(minutes_a)+":"+f.format(seconds_a)+"."+f2.format(minisec_a));
                            textB.setText(f.format(minutes_b)+":"+f.format(seconds_b)+"."+f2.format(minisec_b));
                            break;
                    }

                }

            }
        });

        myListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {

                SongChooseLongClick = true;
                for(int i=0;i<myListview.getCount(); i++) {
                    Song song = songArrayAdapter.getItem(i);

                    if (song != null) {

                        if (song.getCheckBox() != null) {
                            //Log.e(TAG, "set item[" + i + "] visible");
                            //if (!fileChooseItem.getName().equals(".."))
                            //    fileChooseItem.getCheckBox().setVisibility(View.VISIBLE);
                            //else
                            //    fileChooseItem.getCheckBox().setVisibility(View.INVISIBLE);
                            song.getCheckBox().setVisibility(View.VISIBLE);
                        }
                    }
                }

                return false;
            }
        });

        if (songList.size() == 0 ) {
            loadSongs();
        } else {
            songArrayAdapter = new SongArrayAdapter(context, R.layout.music_list_item, songList);
            myListview.setAdapter(songArrayAdapter);
        }


    }

    public void loadSongs() {

        if (check_record_exist("favorite")) {
            Log.d(TAG, "load file success!");
            loadDialog = new ProgressDialog(MainActivity.this);
            loadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadDialog.setTitle("Loading...");
            loadDialog.setIndeterminate(false);
            loadDialog.setCancelable(false);

            loadDialog.show();

            Intent intent = new Intent(context, GetSongListFromRecordService.class);
            intent.setAction(Constants.ACTION.GET_SONGLIST_ACTION);
            intent.putExtra("FILENAME", "favorite");
            context.startService(intent);


        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");

        if (mGoogleApiClient != null) {

            if (mGoogleApiClient.isConnected())
                mGoogleApiClient.disconnect();
        }

        if (isRegister && mReceiver != null) {

            try {
                unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            isRegister = false;
            mReceiver = null;
            Log.d(TAG, "unregisterReceiver mReceiver");

        }

        super.onDestroy();

    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }
    @Override
    public void onResume() {
        Log.i(TAG, "onResume");

        super.onResume();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    @Override
    public void onBackPressed() {

        if (SongChooseLongClick) {
            //MenuItem menuItem = actionmenu.findItem(R.id.action_selectall);

            SongChooseLongClick = false;
            //FileChooseSelectAll = false;
            //menuItem.setTitle(getResources().getString(R.string.select_all));


            for(int i=0;i<myListview.getCount(); i++) {
                Song song = songArrayAdapter.getItem(i);

                if (song != null) {

                    if (song.getCheckBox() != null) {
                        song.getCheckBox().setVisibility(View.INVISIBLE);
                        song.getCheckBox().setChecked(false);
                    }

                    if (song.getFrameLayout() != null) {
                        song.getFrameLayout().setVisibility(View.GONE);
                    }
                    songArrayAdapter.mSparseBooleanArray.put(i, false);
                }
            }

            //confirm.setVisibility(View.GONE);
        } else {
            finish();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        item_search = menu.findItem(R.id.action_search);
        item_remove = menu.findItem(R.id.action_remove);
        item_clear = menu.findItem(R.id.action_clear);

        item_play_all = menu.findItem(R.id.action_play_all);
        item_shuffle = menu.findItem(R.id.action_shuffle);
        item_single_repeat = menu.findItem(R.id.action_repeat);
        item_ab_loop = menu.findItem(R.id.action_loop);
        item_upload_watch = menu.findItem(R.id.action_upload_to_watch);

        //item_clear = menu.findItem(R.id.action_clear);

        item_search.setVisible(false);
        item_upload_watch.setVisible(false);

        try {
            //SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search_keeper));
            searchView.setOnQueryTextListener(queryListener);
        }catch(Exception e){
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;
        NumberFormat f = new DecimalFormat("00");
        NumberFormat f2 = new DecimalFormat("000");
        String ta, tb;

        switch (item.getItemId()) {
            case R.id.action_add:

                intent = new Intent(MainActivity.this, FileChooseActivity.class);
                startActivity(intent);
                break;
            case R.id.action_play_all:
                actionBar.setHomeAsUpIndicator(R.drawable.ic_all_inclusive_white_48dp);
                currentAcitonBarTitle = getResources().getString(R.string.play_mode_all);
                actionBar.setTitle(currentAcitonBarTitle);
                linearLayoutAB.setVisibility(View.GONE);
                current_mode = MODE_PLAY_ALL;
                mediaOperation.setCurrent_play_mode(current_mode);

                //set loop = false
                mediaOperation.setLooping(false);

                //clear loop
                seekBar.setDots(new int[] {});
                seekBar.setDotsDrawable(R.drawable.dot);

                progress_mark_a = 0;
                ta = "00:00.000";
                textA.setText(ta);

                progress_mark_b = 1000;
                tb = "00:00.000";
                textB.setText(tb);
                break;
            case R.id.action_shuffle:
                actionBar.setHomeAsUpIndicator(R.drawable.ic_shuffle_white_48dp);
                currentAcitonBarTitle = getResources().getString(R.string.play_mode_shuffle);
                actionBar.setTitle(currentAcitonBarTitle);
                linearLayoutAB.setVisibility(View.GONE);
                current_mode = MODE_PLAY_SHUFFLE;
                mediaOperation.setCurrent_play_mode(current_mode);

                //set loop = false
                mediaOperation.setLooping(false);

                //clear loop
                seekBar.setDots(new int[] {});
                seekBar.setDotsDrawable(R.drawable.dot);

                progress_mark_a = 0;
                ta = "00:00.000";
                textA.setText(ta);

                progress_mark_b = 1000;
                tb = "00:00.000";
                textB.setText(tb);
                break;

            case R.id.action_repeat:
                actionBar.setHomeAsUpIndicator(R.drawable.ic_repeat_white_48dp);
                currentAcitonBarTitle = getResources().getString(R.string.play_mode_repeat);
                actionBar.setTitle(currentAcitonBarTitle);
                linearLayoutAB.setVisibility(View.GONE);
                current_mode = MODE_PLAY_REPEAT;
                mediaOperation.setCurrent_play_mode(current_mode);

                //set loop = true
                mediaOperation.setLooping(true);

                //clear loop
                seekBar.setDots(new int[] {});
                seekBar.setDotsDrawable(R.drawable.dot);

                progress_mark_a = 0;
                ta = "00:00.000";
                textA.setText(ta);

                progress_mark_b = 1000;
                tb = "00:00.000";
                textB.setText(tb);
                break;

            case R.id.action_loop:


                Log.e(TAG, "song_selected = "+song_selected);

                actionBar.setHomeAsUpIndicator(R.drawable.ic_loop_white_48dp);
                currentAcitonBarTitle = getResources().getString(R.string.play_mode_ab_loop);
                actionBar.setTitle(currentAcitonBarTitle);
                linearLayoutAB.setVisibility(View.VISIBLE);
                current_mode = MODE_PLAY_AB_LOOP;
                mediaOperation.setCurrent_play_mode(current_mode);

                if (songList.size() > 0) {

                    //set loop = false
                    mediaOperation.setLooping(false);

                    int minutes_a = 0;
                    int seconds_a = 0;
                    int minisec_a = 0;
                    int minutes_b = 0;
                    int seconds_b = 0;
                    int minisec_b = 0;

                    if (current_song_duration > 0) {
                        progress_mark_a = (int) ((float) songList.get(song_selected).getMark_a() / (float) current_song_duration * 1000.0);
                        progress_mark_b = (int) ((float) songList.get(song_selected).getMark_b() / (float) current_song_duration * 1000.0);

                        minutes_a = songList.get(song_selected).getMark_a() / 60000;
                        seconds_a = (songList.get(song_selected).getMark_a() / 1000) % 60;
                        minisec_a = songList.get(song_selected).getMark_a() % 1000;

                        minutes_b = songList.get(song_selected).getMark_b() / 60000;
                        seconds_b = (songList.get(song_selected).getMark_b() / 1000) % 60;
                        minisec_b = songList.get(song_selected).getMark_b() % 1000;

                        seekBar.setDots(new int[]{progress_mark_a, progress_mark_b});
                        seekBar.setDotsDrawable(R.drawable.dot);
                        seekBar.setmLine(R.drawable.line);
                    } else {
                        seekBar.setDots(new int[]{0, 1000});
                        seekBar.setDotsDrawable(R.drawable.dot);
                        seekBar.setmLine(R.drawable.line);
                    }

                    textA.setText(f.format(minutes_a) + ":" + f.format(seconds_a) + "." + f2.format(minisec_a));
                    textB.setText(f.format(minutes_b) + ":" + f.format(seconds_b) + "." + f2.format(minisec_b));
                }
                break;

            case R.id.action_volume:
                showVolumeDialog();
                break;
            case R.id.action_remove:
                if (songList.size() > 0) {

                    Log.e(TAG, "remove song_selected = " + song_selected);

                    AlertDialog.Builder confirmdialog = new AlertDialog.Builder(this);
                    confirmdialog.setIcon(R.drawable.ic_warning_black_48dp);
                    confirmdialog.setTitle(getResources().getString(R.string.remove_select));
                    confirmdialog.setMessage(getResources().getString(R.string.remove_file_from_list, songList.get(song_selected).getName()));
                    confirmdialog.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            if (mediaOperation.getCurrent_state() == Constants.STATE.Started) {
                                if (songPlaying == song_selected) {
                                    mediaOperation.doStop();
                                }
                            }


                            songList.remove(song_selected);

                            songArrayAdapter.notifyDataSetChanged();

                            //reset shuffle
                            mediaOperation.shuffleReset();

                            Intent saveintent = new Intent(MainActivity.this, SaveListToFileService.class);
                            saveintent.setAction(Constants.ACTION.SAVE_SONGLIST_ACTION);
                            saveintent.putExtra("FILENAME", "favorite");
                            context.startService(saveintent);

                            loadDialog = new ProgressDialog(MainActivity.this);
                            loadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            loadDialog.setTitle("Saving...");
                            loadDialog.setIndeterminate(false);
                            loadDialog.setCancelable(false);

                            loadDialog.show();

                            if (songList.size() == 0) {
                                //show item
                                if (item_remove != null) {
                                    item_remove.setVisible(false);
                                }
                                if (item_clear != null) {
                                    item_clear.setVisible(false);
                                }

                                //clear loop
                                seekBar.setDots(new int[] {});
                                seekBar.setDotsDrawable(R.drawable.dot);

                                String zero = "00:00.000";
                                progress_mark_a = 0;

                                textA.setText(zero);

                                progress_mark_b = 1000;

                                textB.setText(zero);

                                songDuration.setText(zero);


                            }
                            //reset song_selected
                            song_selected = 0;
                            mediaOperation.setShufflePosition(0);
                        }
                    });
                    confirmdialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    });
                    confirmdialog.show();
                }



                break;
            case R.id.action_clear:
                Log.e(TAG, "clear all");

                if (songList.size() > 0) {

                    AlertDialog.Builder cleardialog = new AlertDialog.Builder(this);
                    cleardialog.setIcon(R.drawable.ic_warning_black_48dp);
                    cleardialog.setTitle(getResources().getString(R.string.clear_all));
                    cleardialog.setMessage(getResources().getString(R.string.clear_list_all));
                    cleardialog.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            if (mediaOperation.getCurrent_state() == Constants.STATE.Started) {
                                mediaOperation.doStop();
                            }

                            songList.clear();

                            songArrayAdapter.notifyDataSetChanged();

                            Intent saveintent = new Intent(MainActivity.this, SaveListToFileService.class);
                            saveintent.setAction(Constants.ACTION.SAVE_SONGLIST_ACTION);
                            saveintent.putExtra("FILENAME", "favorite");
                            context.startService(saveintent);

                            loadDialog = new ProgressDialog(MainActivity.this);
                            loadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            loadDialog.setTitle("Saving...");
                            loadDialog.setIndeterminate(false);
                            loadDialog.setCancelable(false);

                            loadDialog.show();

                            if (songList.size() == 0) {
                                //show item
                                if (item_remove != null) {
                                    item_remove.setVisible(false);
                                }
                                if (item_clear != null) {
                                    item_clear.setVisible(false);
                                }

                                //clear loop
                                seekBar.setDots(new int[]{});
                                seekBar.setDotsDrawable(R.drawable.dot);

                                String zero = "00:00.000";
                                progress_mark_a = 0;

                                textA.setText(zero);

                                progress_mark_b = 1000;

                                textB.setText(zero);

                                songDuration.setText(zero);

                                current_song_duration = 0;

                                song_selected = 0;
                            }

                            //reset song_selected
                            song_selected = 0;
                            mediaOperation.setShufflePosition(0);
                        }
                    });
                    cleardialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    });
                    cleardialog.show();
                }
                break;
            case R.id.action_upload_to_watch:
                Log.e(TAG, "action_upload_to_watch");

                if (!isSync) {

                    uploadList.clear();
                    totalUploadFileNum = 0;
                    totalUploadSize = 0L;
                    for (int i = 0; i < myListview.getCount(); i++) {
                        if (songArrayAdapter.mSparseBooleanArray.get(i)) {
                            Song song = songArrayAdapter.getItem(i);
                            File file = new File(song.getPath());
                            if (song != null && check_file_exist(song.getPath())) {

                                Log.e(TAG, "select : " + song.getPath());
                                uploadList.add(song.getPath());
                                totalUploadSize += file.length();
                                totalUploadFileNum++;
                            }
                        }

                    }

                    //send get watch available space
                    if (mGoogleApiClient.isConnected()) {
                        PutDataMapRequest putRequest = PutDataMapRequest.create("/MOBILE_COMMAND");
                        DataMap map = putRequest.getDataMap();
                        //map.putInt("color", Color.RED);
                        count_for_upload++;
                        map.putString("cmd", "GetAvailableSpace");
                        map.putLong("count", count_for_upload);
                        Wearable.DataApi.putDataItem(mGoogleApiClient, putRequest.asPutDataRequest());
                    } else {
                        Log.e(TAG, "mGoogleApiClient is disconnected");
                    }


                    //Intent uploadIntent = new Intent(MainActivity.this, UploadToWatchService.class);
                    //uploadIntent.setAction(Constants.ACTION.UPLOAD_SONGS_TO_WATCH_ACTION);
                    //startService(uploadIntent);

                    break;
                } else {
                    toast("Sync now. Please wait.");
                }

        }

        editor = pref.edit();
        editor.putInt("PLAY_MODE", current_mode);
        editor.apply();

        return true;
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            //super.handleMessage(msg);

            Log.e(TAG, "receive close");
            dialog.dismiss();

            return true;
        }
    });

    protected void showVolumeDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        View promptView = View.inflate(MainActivity.this, R.layout.volume_dialog, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        //AlertDialog dialog = null;

        alertDialogBuilder.setView(promptView);

        final TextView textVolume = (TextView) promptView.findViewById(R.id.textVolume);
        final SeekBar seekbarVolume = (SeekBar) promptView.findViewById(R.id.seekBarVolume);

        current_volume = mediaOperation.getCurrent_volume();

        seekbarVolume.setProgress(current_volume);
        String vol = mediaOperation.getCurrent_volume()+"%";
        textVolume.setText(vol);

        seekbarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String volume = String.valueOf(progress) + "%";
                textVolume.setText(volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaOperation.getCurrent_state() == Constants.STATE.Started) { //playing, doPause
                    mediaOperation.doPause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                Log.d(TAG, "new volume = "+seekBar.getProgress());

                current_volume = seekBar.getProgress();

                mediaOperation.setCurrent_volume(current_volume);

                if (isPlayPress) {

                    if (mediaOperation.getCurrent_state() == Constants.STATE.Paused) {
                        //if (audioOperation.isPause()) {
                        //mediaOperation.setSeekTo((int) duration);

                        mediaOperation.doPlay(songList.get(song_selected).getPath());

                        //audioOperation.setCurrentPosition(duration/1000.0);
                        //audioOperation.doPlay(songList.get(song_selected).getPath());
                    } else {
                        Log.e(TAG, "Not Pause state");
                    }
                }

                editor = pref.edit();
                editor.putInt("PLAY_VOLUME", current_volume);
                editor.apply();

                Message msg = new Message();
                mHandler.sendMessage(msg);
            }
        });

        // setup a dialog window
        alertDialogBuilder.setCancelable(false);

        dialog = alertDialogBuilder.show();
    }

    final private android.support.v7.widget.SearchView.OnQueryTextListener queryListener = new android.support.v7.widget.SearchView.OnQueryTextListener() {
        //searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            /*Intent intent;


            //ArrayList<MeetingListItem> list = new ArrayList<>();
            sortedNotifyList.clear();
            if (!newText.equals("")) {



                //ArrayList<PasswordKeeperItem> list = new ArrayList<PasswordKeeperItem>();
                for (int i = 0; i < historyItemArrayList.size(); i++) {
                    if (historyItemArrayList.get(i).getTitle() != null && historyItemArrayList.get(i).getTitle().contains(newText)) {
                        sortedNotifyList.add(historyItemArrayList.get(i));
                    } else if (historyItemArrayList.get(i).getMsg() != null && historyItemArrayList.get(i).getMsg().contains(newText)) {
                        sortedNotifyList.add(historyItemArrayList.get(i));
                    } else if (historyItemArrayList.get(i).getDate() != null && historyItemArrayList.get(i).getDate().contains(newText)) {
                        sortedNotifyList.add(historyItemArrayList.get(i));
                    }
                }

                //passwordKeeperArrayAdapter = new PasswordKeeperArrayAdapter(Password_Keeper.this, R.layout.passwd_keeper_browsw_item, list);
                //listView.setAdapter(passwordKeeperArrayAdapter);

            } else {
                //ArrayList<PasswordKeeperItem> list = new ArrayList<PasswordKeeperItem>();

                for (int i = 0; i < historyItemArrayList.size(); i++) {
                    sortedNotifyList.add(historyItemArrayList.get(i));
                }


                //passwordKeeperArrayAdapter = new PasswordKeeperArrayAdapter(Password_Keeper.this, R.layout.passwd_keeper_browsw_item, list);
                //listView.setAdapter(passwordKeeperArrayAdapter);
            }

            //meetingArrayAdapter = new MeetingArrayAdapter(context, R.layout.meeting_list_item, list);
            //AllFragment.resetAdapter(list);
            //AllFragment.listView.setAdapter(AllFragment.meetingArrayAdapter);
            intent = new Intent(Constants.ACTION.GET_HISTORY_LIST_SORT_COMPLETE);
            sendBroadcast(intent);*/


            return false;
        }
    };

    public void toast(String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }


    public boolean isNumber(String string) {
        boolean ret = false;

        if(string.matches("\\d+(?:\\.\\d+)?"))
        {
            Log.d(TAG, "string: "+string+" is number");
            ret = true;
        }
        else
        {
            Log.d(TAG, "string: "+string+" is not number");

        }

        return ret;
    }

    public static void setSongDuration(int timeStamp) {

        NumberFormat f = new DecimalFormat("00");
        NumberFormat f2 = new DecimalFormat("000");


        int minutes = (timeStamp/60000);

        int seconds = (timeStamp/1000) % 60;

        int minisec = (timeStamp%1000);

        songDuration.setText(f.format(minutes)+":"+f.format(seconds)+"."+f2.format(minisec));
    }

    public static void setSeekBarLocation(int timeStamp) {

        if (timeStamp == 0) {
            seekBar.setProgress(0);
        } else {

            double per_unit = (double) current_song_duration / 1000.0;
            double progress = (double) timeStamp / per_unit;

            Log.d(TAG, "unit = " + per_unit + ", progress = " + progress);

            seekBar.setProgress((int)progress);
        }


    }

    private void setTextAudioA(double duration) {
        NumberFormat f = new DecimalFormat("00");
        NumberFormat f2 = new DecimalFormat("000");

        int minutes = ((int) duration) / 60000;

        int seconds = ((int) duration / 1000) % 60;

        int minisec = (int) duration % 1000;


        textA.setText(f.format(minutes) + ":" + f.format(seconds) + "." + f2.format(minisec));
    }

    private void setTextAudioB(double duration) {
        NumberFormat f = new DecimalFormat("00");
        NumberFormat f2 = new DecimalFormat("000");

        int minutes = ((int) duration) / 60000;

        int seconds = ((int) duration / 1000) % 60;

        int minisec = (int) duration % 1000;


        textB.setText(f.format(minutes) + ":" + f.format(seconds) + "." + f2.format(minisec));
    }


    private  boolean checkAndRequestPermissions() {
        //int permissionSendMessage = ContextCompat.checkSelfPermission(this,
        //        android.Manifest.permission.WRITE_CALENDAR);
        int locationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //int cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        //if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
        //    listPermissionsNeeded.add(android.Manifest.permission.WRITE_CALENDAR);
        //}
        //if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
        //    listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        //}

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //Log.e(TAG, "result size = "+grantResults.length+ "result[0] = "+grantResults[0]+", result[1] = "+grantResults[1]);


        /*switch (requestCode) {
            case 200: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                    Log.i(TAG, "WRITE_CALENDAR permissions granted");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i(TAG, "READ_CONTACTS permissions denied");

                    RetryDialog();
                }
            }
            break;

            // other 'case' lines to check for other
            // permissions this app might request
        }*/
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                //perms.put(android.Manifest.permission.WRITE_CALENDAR, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                //perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (//perms.get(android.Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                            perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED )
                    //&& perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    {
                        Log.d(TAG, "write permission granted");

                        // process the normal flow
                        initGoogleApi();
                        init_folder_and_files();
                        loadFunc();
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (//ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_CALENDAR) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            //|| ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)
                                ) {
                            showDialogOK("Warning",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }
}
