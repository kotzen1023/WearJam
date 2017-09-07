package com.seventhmoon.wearjam.Service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.seventhmoon.wearjam.Data.Constants;
import com.seventhmoon.wearjam.Data.Song;

import java.io.File;

import static com.seventhmoon.wearjam.Data.FileOperation.check_file_exist;
import static com.seventhmoon.wearjam.Data.FileOperation.check_record_exist;
import static com.seventhmoon.wearjam.Data.FileOperation.read_record;
import static com.seventhmoon.wearjam.MainActivity.count_for_upload;
import static com.seventhmoon.wearjam.MainActivity.mGoogleApiClient;
import static com.seventhmoon.wearjam.MainActivity.songList;
import static com.seventhmoon.wearjam.MainActivity.uploadList;


public class UploadToWatchService extends IntentService {
    private static final String TAG = UploadToWatchService.class.getName();

    public UploadToWatchService() {
        super("UploadToWatchService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");



    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "Handle");

        //String filename = intent.getStringExtra("FILENAME");


        if (intent.getAction().equals(Constants.ACTION.UPLOAD_SONGS_TO_WATCH_ACTION)) {
            Log.i(TAG, "UPLOAD_SONGS_TO_WATCH_ACTION");
        }

        //for (int i=0; i <uploadList.size(); i++) {

        if (uploadList.size() > 0) {
            Log.d(TAG, "Upload "+uploadList.get(0));
            File file = new File(uploadList.get(0));
            //Log.d(TAG, "uri = "+Uri.fromFile(file));

            Asset asset = Asset.createFromUri(Uri.fromFile(file));

            PutDataMapRequest dataMap = PutDataMapRequest.create("/MOBILE_MUSIC");
            DataMap map = dataMap.getDataMap();
            map.putLong("count", count_for_upload);
            map.putString("filename", file.getName());
            map.putLong("filesize", file.length());

            dataMap.getDataMap().putAsset("profileMusic", asset);
            PutDataRequest request = dataMap.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                    .putDataItem(mGoogleApiClient, request);

            count_for_upload++;
            Log.e(TAG, "pendingResult = "+pendingResult);
            //remove first
            uploadList.remove(0);
        }


        //}

        //Asset asset = Asset.createFromUri(Uri.fromFile())
}

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        //Intent intent = new Intent(Constants.ACTION.GET_SONGLIST_FROM_RECORD_FILE_COMPLETE);
        //sendBroadcast(intent);
    }
}
