package com.seventhmoon.wearjam.Data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seventhmoon.wearjam.PlayActivity;
import com.seventhmoon.wearjam.R;

import java.util.ArrayList;

import static com.seventhmoon.wearjam.MainActivity.current_song_duration;
import static com.seventhmoon.wearjam.MainActivity.wearableRecyclerView;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static final String TAG = MyAdapter.class.getName();
    //private String[] mDataset;

    private LayoutInflater inflater = null;

    private int layoutResourceId;

    private static ArrayList<Song> items = new ArrayList<>();

    private static Context context;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        LinearLayout layoutItem;
        ImageView songicon;
        TextView songname;
        //public TextView mTextView;
        private ViewHolder(View v) {
            super(v);
            //mTextView = v;
            this.layoutItem = v.findViewById(R.id.layoutItem);
            this.songicon = v.findViewById(R.id.songIcon);
            this.songname = v.findViewById(R.id.songName);
            layoutItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // here you use position
            int position = getAdapterPosition();


            /*
            android:ellipsize="marquee"
        android:marqueeRepeatLimit="marquee_forever"
        android:singleLine="true"
        android:focusableInTouchMode="true"
        android:focusable="true"
             */

            //songname.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            //songname.setFocusable(true);
            //songname.setFocusableInTouchMode(true);

            for (int i=0; i<items.size(); i++) {
                if (i == position) {
                    Log.e(TAG, "onClick "+position);
                    items.get(i).setSelected(true);

                } else {
                    items.get(i).setSelected(false);

                }
            }

            //wearableRecyclerView.postInvalidate();

            Intent bcIntent = new Intent(Constants.ACTION.GET_UPDATE_VIEW_ACTION);
            context.sendBroadcast(bcIntent);


            Intent intent = new Intent(context, PlayActivity.class);
            intent.putExtra("TITLE", items.get(position).getName());
            intent.putExtra("PATH", items.get(position).getPath());
            Log.e(TAG, "duration_u = "+items.get(position).getDuration_u());
            current_song_duration = (int)(items.get(position).getDuration_u()/1000);
            //current_song_duration = (int)(songList.get(song_selected).getDuration_u()/1000);
            context.startActivity(intent);
        }


    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, int textViewResourceId,
                     ArrayList<Song> objects) {
        //this.mDataset = myDataset;
        this.layoutResourceId = textViewResourceId;
        this.items = objects;
        this.context = context;

        //inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.setText(mDataset[position]);
        Song songItem = items.get(position);
        if (songItem != null) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_audiotrack_white_48dp);
            holder.songicon.setImageBitmap(bitmap);
            holder.songname.setText(songItem.getName());
            songItem.setTextView(holder.songname);


            if (songItem.isSelected()) {
                Log.e(TAG, ""+position+" is selected.");
                //view.setSelected(true);
                items.get(position).getTextView().setTextColor(Color.rgb(0x0e, 0xb6, 0x95));
                //items.get(position).getTextView().setEllipsize(TextUtils.TruncateAt.MARQUEE);
                //items.get(position).getTextView().setMarqueeRepeatLimit(-1);
                //items.get(position).getTextView().setFocusable(true);
                //items.get(position).getTextView().setFocusableInTouchMode(true);
                /*
                items.get(position).getTextView().setMaxLines(0);
                items.get(position).getTextView().setSingleLine(true);*/

                //holder.layoutItem.setBackgroundColor(Color.rgb(0x4d, 0x90, 0xfe));
            } else {
                Log.e(TAG, ""+position+" clear.");
                items.get(position).getTextView().setTextColor(Color.WHITE);
                //items.get(position).getTextView().setEllipsize(TextUtils.TruncateAt.END);
                //items.get(position).getTextView().setMarqueeRepeatLimit(0);
                //items.get(position).getTextView().setFocusable(false);
                //items.get(position).getTextView().setFocusableInTouchMode(false);
                /*items.get(position).getTextView().setMaxLines(1);
                items.get(position).getTextView().setSingleLine(false);*/
                //view.setSelected(false);
                //holder.layoutItem.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }
}
