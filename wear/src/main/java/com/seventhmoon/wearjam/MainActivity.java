package com.seventhmoon.wearjam;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;

import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.CurvedChildLayoutManager;
import android.support.wearable.view.WearableRecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.seventhmoon.wearjam.Data.Constants;
import com.seventhmoon.wearjam.Data.MediaOperation;
import com.seventhmoon.wearjam.Data.MyAdapter;
import com.seventhmoon.wearjam.Data.Song;
import com.seventhmoon.wearjam.Data.SongArrayAdapter;
import com.seventhmoon.wearjam.Service.GetSongListFromRecordService;
import com.seventhmoon.wearjam.Service.SaveListToFileService;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.seventhmoon.wearjam.Data.FileOperation.check_record_exist;
import static com.seventhmoon.wearjam.Data.FileOperation.init_wearjam_folder;

public class MainActivity extends WearableActivity {
    private static final String TAG = MainActivity.class.getName();

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    //private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
    //        new SimpleDateFormat("HH:mm", Locale.US);

    //private BoxInsetLayout mContainerView;
    //private TextView mTextView;
    //private TextView mClockView;
    private Context context;

    static SharedPreferences pref ;
    static SharedPreferences.Editor editor;
    private static final String FILE_NAME = "Preference";

    //ArrayList<Song> myList = new ArrayList<>();

    public static WearableRecyclerView wearableRecyclerView;
    MyAdapter songAdapter;


    public static GoogleApiClient mGoogleApiClient;

    //private MediaPlayer mediaPlayer;
    public static ArrayList<Song> songList = new ArrayList<>();
    //for add songs to list
    public static ArrayList<String> searchList = new ArrayList<>();
    public static ArrayList<Song> addSongList = new ArrayList<>();

    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;
    //public static ProgressDialog loadDialog = null;
    private static long receive_count = 0;
    public static MediaOperation mediaOperation;
    public static boolean isPlayPress = false;
    public static int current_position = 0;
    public static int current_volume = 50;
    public static int current_song_duration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        context = getBaseContext();

        pref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        current_volume = pref.getInt("PLAY_VOLUME", 50);

        wearableRecyclerView = findViewById(R.id.listView);
        CircledImageView btnImage = findViewById(R.id.btn_add);

        boolean permission_result = checkAndRequestPermissions();

        if (permission_result) {
            // carry on the normal flow, as the case of  permissions  granted.
            initGoogleApi();
            init_wearjam_folder();
            init();







        }

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FileChooseActivity.class);
                startActivity(intent);
            }
        });



        IntentFilter filter;

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(Constants.ACTION.GET_SONGLIST_FROM_RECORD_FILE_COMPLETE)) {

                    if (songList.size() > 0) {

                        songAdapter = new MyAdapter(context, R.layout.music_list_item, songList);
                        wearableRecyclerView.setAdapter(songAdapter);

                        wearableRecyclerView.setLayoutManager(new CurvedChildLayoutManager(context));
                        wearableRecyclerView.setCenterEdgeItems(true);

                        //if (loadDialog != null)
                        //    loadDialog.dismiss();

                        //save list
                        Intent saveintent = new Intent(context, SaveListToFileService.class);
                        saveintent.setAction(Constants.ACTION.SAVE_SONGLIST_ACTION);
                        saveintent.putExtra("FILENAME", "favorite");
                        context.startService(saveintent);
                        //set shuffle list
                        /*mediaOperation.shuffleReset();

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
                        }*/

                    } else {
                        //if (loadDialog != null)
                        //    loadDialog.dismiss();

                        /*if (item_remove != null) {
                            item_remove.setVisible(false);
                        }
                        if (item_clear != null) {
                            item_clear.setVisible(false);
                        }

                        toast(getResources().getString(R.string.list_empty));*/
                    }



                } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.ADD_SONG_LIST_COMPLETE)) {
                    Log.d(TAG, "receive ADD_SONG_LIST_COMPLETE !");


                    for (int i=0; i<addSongList.size(); i++) {
                        boolean found = false;
                        for (int j=0; j<songList.size(); j++) {
                            if (songList.get(j).getPath().equals(addSongList.get(i).getPath())) {
                                found = true;
                            }
                        }

                        if (!found) {
                            songList.add(addSongList.get(i));
                            Log.d(TAG, "add "+addSongList.get(i).getName()+" to songList");
                        }
                    }

                    //mediaOperation.shuffleReset();
                    //mediaOperation.setShufflePosition(0);

                    if (songAdapter == null) {
                        songAdapter = new MyAdapter(context, R.layout.music_list_item, songList);
                        wearableRecyclerView.setAdapter(songAdapter);

                        wearableRecyclerView.setLayoutManager(new CurvedChildLayoutManager(context));
                        wearableRecyclerView.setCenterEdgeItems(true);
                    } else {
                        Log.e(TAG, "notifyDataSetChanged");
                        songAdapter.notifyDataSetChanged();
                    }

                    Intent saveintent = new Intent(context, SaveListToFileService.class);
                    saveintent.setAction(Constants.ACTION.SAVE_SONGLIST_ACTION);
                    saveintent.putExtra("FILENAME", "favorite");
                    context.startService(saveintent);

                    /*
                    Intent saveintent = new Intent(context, SaveListToFileService.class);
                    saveintent.setAction(Constants.ACTION.SAVE_SONGLIST_ACTION);
                    saveintent.putExtra("FILENAME", "favorite");
                    context.startService(saveintent);

                    loadDialog = new ProgressDialog(context);
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
                    }*/


                } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.RECEIVE_UPLOAD_COMPLETE)) {
                    Log.d(TAG, "receive RECEIVE_UPLOAD_COMPLETE !");

                    Log.d(TAG, "sendReceiveComplete");
                    if(mGoogleApiClient == null) {
                        Log.e(TAG, "mGoogleApiClient = null");
                    } else {
                        if (mGoogleApiClient.isConnected()) {
                            PutDataMapRequest putRequest = PutDataMapRequest.create("/WEAR_COMMAND");
                            DataMap map = putRequest.getDataMap();
                            //map.putInt("color", Color.RED);
                            map.putString("cmd", "UploadComplete");
                            map.putLong("count", receive_count);
                            receive_count++;
                            Wearable.DataApi.putDataItem(mGoogleApiClient, putRequest.asPutDataRequest());
                        } else {
                            Log.e(TAG, "mGoogleApiClient is disconnected");
                        }


                    }
                } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.GET_UPDATE_VIEW_ACTION)) {
                    Log.d(TAG, "receive GET_UPDATE_VIEW_ACTION !");

                    if (wearableRecyclerView != null) {
                        songAdapter.notifyDataSetChanged();
                        wearableRecyclerView.invalidate();
                    }
                }

                /*else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.ADD_SONG_LIST_CHANGE)) {
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
                }*/
            }
        };


        if (!isRegister) {
            filter = new IntentFilter();
            filter.addAction(Constants.ACTION.ADD_SONG_LIST_COMPLETE);
            filter.addAction(Constants.ACTION.RECEIVE_UPLOAD_COMPLETE);
            //filter.addAction(Constants.ACTION.GET_PLAY_COMPLETE);
            filter.addAction(Constants.ACTION.GET_SONGLIST_FROM_RECORD_FILE_COMPLETE);
            filter.addAction(Constants.ACTION.GET_UPDATE_VIEW_ACTION);
            //filter.addAction(Constants.ACTION.SAVE_SONGLIST_TO_FILE_COMPLETE);
            //filter.addAction(Constants.ACTION.MEDIAPLAYER_STATE_PLAYED);
            //filter.addAction(Constants.ACTION.MEDIAPLAYER_STATE_PAUSED);
            context.registerReceiver(mReceiver, filter);
            isRegister = true;
            Log.d(TAG, "registerReceiver mReceiver");
        }



    }

    private void init() {

        Log.e(TAG, "init: songList.size() = "+songList.size());

        if (songList.size() == 0 ) {
            loadSongs();
        } else {
            songAdapter = new MyAdapter(context, R.layout.music_list_item, songList);
            wearableRecyclerView.setAdapter(songAdapter);

            wearableRecyclerView.setLayoutManager(new CurvedChildLayoutManager(this));
            wearableRecyclerView.setCenterEdgeItems(true);
        }

        mediaOperation = new MediaOperation(context);
        mediaOperation.setCurrent_volume(current_volume);

        /*songAdapter = new MyAdapter(context, R.layout.music_list_item, myList);
        wearableRecyclerView.setAdapter(songAdapter);

        wearableRecyclerView.setLayoutManager(new CurvedChildLayoutManager(this));
        wearableRecyclerView.setCenterEdgeItems(true);*/
    }

    public void loadSongs() {

        if (check_record_exist("favorite")) {
            Log.d(TAG, "load file success!");
            //loadDialog = new ProgressDialog(context);
            //loadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //loadDialog.setTitle("Loading...");
            //loadDialog.setIndeterminate(false);
            //loadDialog.setCancelable(false);

            //loadDialog.show();

            Intent intent = new Intent(context, GetSongListFromRecordService.class);
            intent.setAction(Constants.ACTION.GET_SONGLIST_ACTION);
            intent.putExtra("FILENAME", "favorite");
            context.startService(intent);

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



        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        if (mGoogleApiClient != null) {

            if (mGoogleApiClient.isConnected())
                mGoogleApiClient.disconnect();
        }

        if (isRegister && mReceiver != null) {
            try {
                context.unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            isRegister = false;
            mReceiver = null;
        }

        songAdapter = null;

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
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
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
                        //else any one or both the permissions are not granted
                        initGoogleApi();
                        init_wearjam_folder();
                        init();

                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (//ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_CALENDAR) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    /*builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle(getResources().getString(R.string.main_attention));
                                    builder.setMessage(getResources().getString(R.string.permission_descript));
                                    builder.setPositiveButton(getResources().getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            checkAndRequestPermissions();
                                        }
                                    });
                                    builder.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                                    builder.show();*/


                            /*showDialogOK(getResources().getString(R.string.permission_descript),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    //finish();

                                                    break;
                                            }
                                        }
                                    });*/
                            //showResetlog();
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
}
