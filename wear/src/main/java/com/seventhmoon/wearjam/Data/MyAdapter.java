package com.seventhmoon.wearjam.Data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
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
        public ViewHolder(View v) {
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
            Log.e(TAG, "onClick "+position);
            Intent intent = new Intent(context, PlayActivity.class);
            intent.putExtra("TITLE", items.get(position).getName());
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

            if (songItem.isSelected()) {
                //Log.e(TAG, ""+position+" is selected.");
                //view.setSelected(true);
                holder.layoutItem.setBackgroundColor(Color.rgb(0x4d, 0x90, 0xfe));
            } else {
                //Log.e(TAG, ""+position+" clear.");
                //view.setSelected(false);
                holder.layoutItem.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }
}
