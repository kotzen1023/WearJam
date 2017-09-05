package com.seventhmoon.wearjam.Data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seventhmoon.wearjam.PlayActivity;
import com.seventhmoon.wearjam.R;

import java.io.File;
import java.util.ArrayList;

import static com.seventhmoon.wearjam.FileChooseActivity.FileChooseLongClick;
import static com.seventhmoon.wearjam.FileChooseActivity.FileChooseSelectAll;
//import static com.seventhmoon.wearjam.FileChooseActivity.confirm;
import static com.seventhmoon.wearjam.FileChooseActivity.currentDir;
import static com.seventhmoon.wearjam.FileChooseActivity.fill;


public class FileChooseAdapter extends RecyclerView.Adapter<FileChooseAdapter.ViewHolder> {
    private static final String TAG = FileChooseAdapter.class.getName();
    //private String[] mDataset;

    public static SparseBooleanArray mSparseBooleanArray;

    private LayoutInflater inflater = null;

    private int layoutResourceId;

    private static ArrayList<FileChooseItem> items = new ArrayList<>();

    private static Context context;
    private int count = 0;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    //public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        ImageView fileicon;
        TextView filename;
        CheckBox checkbox;
        LinearLayout folderlayout;
        //public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            //mTextView = v;
            this.fileicon = v.findViewById(R.id.fd_Icon1);
            this.filename = v.findViewById(R.id.fileChooseFileName);
            this.checkbox = v.findViewById(R.id.checkBoxInRow);
            this.folderlayout = v.findViewById(R.id.folderlayout);
            folderlayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // here you use position
            /*int position = getAdapterPosition();
            Log.e(TAG, "onClick "+position);
            Intent intent = new Intent(context, PlayActivity.class);
            intent.putExtra("TITLE", items.get(position).getName());
            context.startActivity(intent);*/
            int position = getAdapterPosition();
            FileChooseItem o = items.get(position);

            if (o != null) {

                if (o.getPath() != null && (o.getImage().equalsIgnoreCase("directory_icon") || o.getImage().equalsIgnoreCase("directory_up"))) {
                    currentDir = new File(o.getPath());
                    fill(currentDir);

                    //MenuItem menuItem = actionmenu.findItem(R.id.action_selectall);

                    FileChooseLongClick = false;
                    FileChooseSelectAll = false;
                    //menuItem.setTitle("Select all");


                    for (int i = 0; i < items.size(); i++) {
                        FileChooseItem fileChooseItem = items.get(i);

                        if (fileChooseItem != null) {

                            if (fileChooseItem.getCheckBox() != null) {
                                //fileChooseItem.getCheckBox().setVisibility(View.INVISIBLE);
                                fileChooseItem.getCheckBox().setChecked(false);
                            }
                            mSparseBooleanArray.put(i, false);
                        }

                    }
                } else {
                    //onFileClick(o);
                    Log.d(TAG, "click " + o.getName());
                }
            }
        }

        /*@Override
        public boolean onLongClick(View view) {
            FileChooseLongClick = true;
            //FileChooseItem fileChooseItem = (FileChooseItem) fileChooseArrayAdapter.getItem(position);
            //Log.i(TAG, "name = "+fileChooseItem.getName());
            //Log.e(TAG, "ck = " + fileChooseItem.getCheckBox());
            //fileChooseItem.getCheckBox().setVisibility(View.VISIBLE);

            for(int i=0;i<items.size(); i++) {
                FileChooseItem fileChooseItem = items.get(i);

                if (fileChooseItem != null) {

                    if (fileChooseItem.getCheckBox() != null) {
                        //Log.e(TAG, "set item[" + i + "] visible");
                        if (!fileChooseItem.getName().equals(".."))
                            fileChooseItem.getCheckBox().setVisibility(View.VISIBLE);
                        else
                            fileChooseItem.getCheckBox().setVisibility(View.INVISIBLE);
                    }
                }
            }


            return false;
        }*/
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FileChooseAdapter(Context context, int textViewResourceId,
                     ArrayList<FileChooseItem> objects) {
        //this.mDataset = myDataset;
        this.layoutResourceId = textViewResourceId;
        this.items = objects;
        this.context = context;
        mSparseBooleanArray = new SparseBooleanArray();

        //inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FileChooseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);
        // set the view's size, margins, paddings and layout parameters

        FileChooseAdapter.ViewHolder vh = new FileChooseAdapter.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FileChooseAdapter.ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.setText(mDataset[position]);
        FileChooseItem fileItem = items.get(position);
        if (fileItem != null) {

            holder.filename.setText(fileItem.getName());
            holder.checkbox.setTag(position);


            //holder.checkbox.setVisibility(View.INVISIBLE);

            //if (FileChooseLongClick) {
            //    holder.checkbox.setVisibility(View.VISIBLE);
            //}

            if (mSparseBooleanArray.get(position))
            {
                holder.checkbox.setChecked(true);
            } else {
                holder.checkbox.setChecked(false);
            }

            Bitmap bitmap, bm;
            if (fileItem.getPath() != null) {

                File file = new File(fileItem.getPath());

                //Log.i(TAG, "file: abs path"+file.getAbsolutePath()+ "name = "+fileChooseItem.getName()+" data = "+fileChooseItem.getData());

                if (file.isDirectory()) {
                    if (fileItem.getName().equals("..")) {
                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.up);
                        holder.checkbox.setVisibility(View.INVISIBLE);
                    } else {
                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder);
                    }
                } else if (file.isFile()) {
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.file);
                } else {
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder);
                }
                bm = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
                //ImageView imageCity = (ImageView) view.findViewById(R.id.fd_Icon1);
                //String uri = "drawable/" + o.getImage();
                //int imageResource = c.getResources().getIdentifier(uri, null, c.getPackageName());
                //Drawable image = c.getResources().getDrawable(imageResource);
                holder.fileicon.setImageBitmap(bm);
                //imageCity.setImageDrawable(image);

                holder.checkbox.setOnCheckedChangeListener(mCheckedChangeListener);
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }


    public FileChooseItem getItem(int index) {
        return items.get(index);
    }

    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.i(TAG, "switch " + buttonView.getTag() + " checked = " + isChecked);
            //int idx = (Integer) buttonView.getTag();

            //if(isChecked == true) {
            FileChooseItem fileChooseItem = items.get((Integer) buttonView.getTag());

            if (fileChooseItem.getCheckBox() != null) {

                if (!fileChooseItem.getName().equals("..")) {
                    mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
                    if (isChecked)
                        count++;
                    else
                        count--;
                }
                else {
                    fileChooseItem.getCheckBox().setChecked(false);
                    fileChooseItem.getCheckBox().setVisibility(View.INVISIBLE);
                    mSparseBooleanArray.put((Integer) buttonView.getTag(), false);
                    count--;
                }
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
                confirm.setVisibility(View.VISIBLE);
            } else  {
                confirm.setVisibility(View.GONE);
            }*/
        }
    };
}
