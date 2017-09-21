package com.seventhmoon.wearjam.Data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.seventhmoon.wearjam.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import static com.seventhmoon.wearjam.FileChooseActivity.FileChooseLongClick;
import static com.seventhmoon.wearjam.FileChooseActivity.confirm;
import static com.seventhmoon.wearjam.MainActivity.SongChooseLongClick;
import static com.seventhmoon.wearjam.MainActivity.item_upload_watch;


public class SongArrayAdapter extends ArrayAdapter<Song> {
    private static final String TAG = SongArrayAdapter.class.getName();

    private LayoutInflater inflater = null;

    private int layoutResourceId;
    private ArrayList<Song> items = new ArrayList<>();
    public SparseBooleanArray mSparseBooleanArray;
    private int count = 0;

    public SongArrayAdapter(Context context, int textViewResourceId,
                            ArrayList<Song> objects) {
        super(context, textViewResourceId, objects);
        this.layoutResourceId = textViewResourceId;
        this.items = objects;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSparseBooleanArray = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return items.size();

    }

    public Song getItem(int position)
    {
        return items.get(position);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Log.e(TAG, "getView = "+ position);
        View view;
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            //Log.e(TAG, "convertView = null");
            /*view = inflater.inflate(layoutResourceId, null);
            holder = new ViewHolder(view);
            view.setTag(holder);*/

            //LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutResourceId, null);
            holder = new ViewHolder(view);
            //holder.checkbox.setVisibility(View.INVISIBLE);
            view.setTag(holder);
        }
        else {
            view = convertView ;
            holder = (ViewHolder) view.getTag();
        }

        //holder.fileicon = (ImageView) view.findViewById(R.id.fd_Icon1);
        //holder.filename = (TextView) view.findViewById(R.id.fileChooseFileName);
        //holder.checkbox = (CheckBox) view.findViewById(R.id.checkBoxInRow);

        Song songItem = items.get(position);
        if (songItem != null) {

            NumberFormat f = new DecimalFormat("00");
            NumberFormat f2 = new DecimalFormat("000");


            int minutes = (int)(songItem.getDuration_u()/60000000);

            int seconds = (int)(songItem.getDuration_u()/1000000) % 60;

            int minisec = (int)((songItem.getDuration_u()/1000)%1000);

            //if (minutes == 0 && seconds == 0) {
            //    seconds = 1;
            //}




            Bitmap bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.ic_audiotrack_black_48dp);
            holder.songicon.setImageBitmap(bitmap);
            holder.songname.setText(songItem.getName());
            holder.checkBox.setTag(position);

            CheckBox ck = view.findViewById(R.id.checkBoxSong);
            FrameLayout fl = view.findViewById(R.id.frameLayoutCheckBox);

            songItem.setCheckBox(ck);
            songItem.setFrameLayout(fl);
            ck.setVisibility(View.INVISIBLE);
            fl.setVisibility(View.GONE);

            if (SongChooseLongClick) {
                fl.setVisibility(View.VISIBLE);
                ck.setVisibility(View.VISIBLE);
            }

            if (mSparseBooleanArray.get(position))
            {
                ck.setChecked(true);
            } else {
                ck.setChecked(false);
            }
            //holder.songtime.setText(f.format(minutes)+":"+f.format(seconds)+"."+f2.format(minisec));
            if (seconds > 1) {
                holder.songtime.setText(minutes+":"+f.format(seconds));
            } else {
                holder.songtime.setText(seconds+"."+f2.format(minisec));
            }

            if (songItem.isSelected()) {
                //Log.e(TAG, ""+position+" is selected.");
                //view.setSelected(true);
                view.setBackgroundColor(Color.rgb(0x4d, 0x90, 0xfe));
            } else {
                //Log.e(TAG, ""+position+" clear.");
                //view.setSelected(false);
                view.setBackgroundColor(Color.TRANSPARENT);
            }

            holder.checkBox.setOnCheckedChangeListener(mCheckedChangeListener);






        }
        return view;
    }

    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.i(TAG, "switch " + buttonView.getTag() + " checked = " + isChecked);
            //int idx = (Integer) buttonView.getTag();

            //if(isChecked == true) {
            Song song = items.get((Integer) buttonView.getTag());

            if (song.getCheckBox() != null) {

                mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
                if (isChecked)
                    count++;
                else
                    count--;
            }
            //}
            /*int count = 0;

            for (int i=0; i<fileChooseArrayAdapter.getCount(); i++) {
                if (mSparseBooleanArray.get(i)) {
                    count++;
                }
            }*/

            //Log.e(TAG, "Count = "+count);

            /*if (count > 0) {
                item_upload_watch.setVisible(true);
            } else  {
                item_upload_watch.setVisible(false);
            }*/
        }
    };

    private class ViewHolder {
        ImageView songicon;
        TextView songname;
        TextView songtime;
        FrameLayout frameLayoutCheckBox;
        CheckBox checkBox;


        private ViewHolder(View view) {
            this.songicon =  view.findViewById(R.id.songIcon);
            this.songname =  view.findViewById(R.id.songName);
            this.songtime = view.findViewById(R.id.songTime);
            this.frameLayoutCheckBox = view.findViewById(R.id.frameLayoutCheckBox);
            this.checkBox = view.findViewById(R.id.checkBoxSong);
        }
    }
}
