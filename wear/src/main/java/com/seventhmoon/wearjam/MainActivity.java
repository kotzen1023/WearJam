package com.seventhmoon.wearjam;

import android.content.Context;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.WearableRecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.seventhmoon.wearjam.Data.MyAdapter;
import com.seventhmoon.wearjam.Data.Song;
import com.seventhmoon.wearjam.Data.SongArrayAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity {
    private static final String TAG = MainActivity.class.getName();

    //private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
    //        new SimpleDateFormat("HH:mm", Locale.US);

    //private BoxInsetLayout mContainerView;
    //private TextView mTextView;
    //private TextView mClockView;
    private Context context;

    ArrayList<Song> myList = new ArrayList<>();

    WearableRecyclerView wearableRecyclerView;
    MyAdapter songAdapter;

    public static GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        context = getBaseContext();

        wearableRecyclerView = findViewById(R.id.listView);
        //mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        //mTextView = (TextView) findViewById(R.id.text);
        //mClockView = (TextView) findViewById(R.id.clock);
        Song song0 = new Song();
        song0.setName("test0");
        myList.add(song0);
        Song song1 = new Song();
        song1.setName("test1");
        myList.add(song1);
        Song song2 = new Song();
        song2.setName("test2");
        myList.add(song2);
        Song song3 = new Song();
        song3.setName("test3");
        myList.add(song3);
        Song song4 = new Song();
        song4.setName("test4");
        myList.add(song4);

        songAdapter = new MyAdapter(context, R.layout.music_list_item, myList);
        wearableRecyclerView.setAdapter(songAdapter);


        wearableRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //deselect other
                /*for (int i=0; i<myList.size(); i++) {

                    if (i == position) {
                        myList.get(i).setSelected(true);

                    } else {
                        myList.get(i).setSelected(false);

                    }
                }

                listView.invalidateViews();*/
            }
        });

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //deselect other
                for (int i=0; i<myList.size(); i++) {

                    if (i == position) {
                        myList.get(i).setSelected(true);

                    } else {
                        myList.get(i).setSelected(false);

                    }
                }

                listView.invalidateViews();
            }
        });*/

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
