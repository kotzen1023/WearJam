package com.seventhmoon.wearjam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.CurvedChildLayoutManager;
import android.support.wearable.view.WearableRecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.seventhmoon.wearjam.Data.Constants;
import com.seventhmoon.wearjam.Data.FileChooseAdapter;
import com.seventhmoon.wearjam.Data.FileChooseItem;
import com.seventhmoon.wearjam.Data.MyAdapter;
import com.seventhmoon.wearjam.Service.SearchFileService;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static com.seventhmoon.wearjam.MainActivity.searchList;


public class FileChooseActivity extends WearableActivity {
    private static final String TAG = FileChooseActivity.class.getName();

    public static boolean FileChooseLongClick = false;
    public static boolean FileChooseSelectAll = false;

    private static Context context;

    //public static FileChooseArrayAdapter fileChooseArrayAdapter;
    //public ListView listView;
    public static WearableRecyclerView wearableRecyclerView;
    private static FileChooseAdapter fileChooseAdapter;
    public static CircledImageView confirm;
    public static File currentDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_choose_list);
        setAmbientEnabled();

        context = getBaseContext();

        wearableRecyclerView = findViewById(R.id.fileChooseListView);
        confirm = findViewById(R.id.btnFileChooseListConfirm);

        wearableRecyclerView.setLayoutManager(new CurvedChildLayoutManager(this));
        wearableRecyclerView.setCenterEdgeItems(true);


        currentDir = new File(Environment.getExternalStorageDirectory().getPath());
        Log.e(TAG, "currentDir = "+Environment.getExternalStorageDirectory().getPath());
        //fileChooseArrayAdapter = new FileChooseArrayAdapter(this, R.layout.file_choose_in_row, );
        fill(currentDir);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchList.clear();

                Log.e(TAG, "size = "+fileChooseAdapter.getItemCount());


                for (int i = 0; i < fileChooseAdapter.getItemCount(); i++) {
                    if (fileChooseAdapter.mSparseBooleanArray.get(i)) {
                        FileChooseItem fileChooseItem = fileChooseAdapter.getItem(i);

                        if (fileChooseItem != null) {

                            Log.e(TAG, "select : " + fileChooseItem.getPath());
                            searchList.add(fileChooseItem.getPath());
                        }
                    }

                }


                Intent saveintent = new Intent(FileChooseActivity.this, SearchFileService.class);
                saveintent.setAction(Constants.ACTION.GET_SEARCHLIST_ACTION);
                startService(saveintent);

                //searchFiles();
                finish();
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

        FileChooseLongClick = false;
        FileChooseSelectAll = false;

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

    public static void fill(File f)
    {
        final File[]dirs = f.listFiles();

        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(f.getAbsolutePath());
        }*/


        ArrayList<FileChooseItem> dir = new ArrayList<>();
        ArrayList<FileChooseItem> fls = new ArrayList<>();
        try{
            for(File ff: dirs)
            {
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);
                //CheckBox checkBox = new CheckBox(getApplicationContext());
                if(ff.isDirectory()){


                    File[] fbuf = ff.listFiles();
                    int buf;
                    if(fbuf != null){
                        buf = fbuf.length;
                    }
                    else buf = 0;
                    String num_item = String.valueOf(buf);
                    if(buf == 0) num_item = num_item + " "+"file";
                    else num_item = num_item + " "+"files";

                    //String formated = lastModDate.toString();
                    char first = ff.getName().charAt(0);
                    if (first != '.')
                        dir.add(new FileChooseItem(ff.getName(),num_item,date_modify,ff.getAbsolutePath(),"directory_icon"));
                }
                else
                {
                    char first = ff.getName().charAt(0);
                    if (first != '.')
                        fls.add(new FileChooseItem(ff.getName(),ff.length() + " Byte", date_modify, ff.getAbsolutePath(),"file_icon"));
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if(//!f.getName().equalsIgnoreCase("sdcard") ||
                !f.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getPath())) {
            //CheckBox checkBox = new CheckBox(this);
            dir.add(0, new FileChooseItem("..", "Parent Directory", "", f.getParent(), "directory_up"));
        }
        fileChooseAdapter = new FileChooseAdapter(context,R.layout.file_choose_in_row,dir);
        wearableRecyclerView.setAdapter(fileChooseAdapter);
        //wearableRecyclerView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);



        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileChooseItem o = fileChooseArrayAdapter.getItem(position);

                if (o != null) {

                    if (o.getPath() != null && (o.getImage().equalsIgnoreCase("directory_icon") || o.getImage().equalsIgnoreCase("directory_up"))) {
                        currentDir = new File(o.getPath());
                        fill(currentDir);

                        MenuItem menuItem = actionmenu.findItem(R.id.action_selectall);

                        FileChooseLongClick = false;
                        FileChooseSelectAll = false;
                        menuItem.setTitle("Select all");


                        for (int i = 0; i < listView.getCount(); i++) {
                            FileChooseItem fileChooseItem = fileChooseArrayAdapter.getItem(i);

                            if (fileChooseItem != null) {

                                if (fileChooseItem.getCheckBox() != null) {
                                    fileChooseItem.getCheckBox().setVisibility(View.INVISIBLE);
                                    fileChooseItem.getCheckBox().setChecked(false);
                                }
                                fileChooseArrayAdapter.mSparseBooleanArray.put(i, false);
                            }

                        }
                    } else {
                        //onFileClick(o);
                        Log.d(TAG, "click " + o.getName());
                    }
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                FileChooseLongClick = true;


                for(int i=0;i<listView.getCount(); i++) {
                    FileChooseItem fileChooseItem = fileChooseArrayAdapter.getItem(i);

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
            }
        });*/
    }
}
