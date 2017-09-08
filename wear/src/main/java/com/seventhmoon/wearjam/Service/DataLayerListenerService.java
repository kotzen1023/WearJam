package com.seventhmoon.wearjam.Service;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.seventhmoon.wearjam.Data.Constants;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.seventhmoon.wearjam.Data.FileOperation.copyInputStreamToFile;
import static com.seventhmoon.wearjam.MainActivity.mGoogleApiClient;


public class DataLayerListenerService extends WearableListenerService {
    private static final String TAG = DataLayerListenerService.class.getName();

    int TIMEOUT_MS = 10000;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for(DataEvent event : events) {
            final Uri uri = event.getDataItem().getUri();
            final String path = uri!=null ? uri.getPath() : null;
            Log.d(TAG, "path = "+path);
            if("/MOBILE_COMMAND".equals(path)) {
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                // read your values from map:
                //int color = map.getInt("color");
                String command = map.getString("cmd");
                Long count = map.getLong("count");
                Log.e(TAG, "command = "+command+" count = "+count);

                if (command.equals("TransferComplete")) {
                    Log.e(TAG, "receive TransferComplete!");
                }
            }
            else if("/MOBILE_MUSIC".equals(path)) {
                Log.d(TAG, "/MOBILE_MUSIC");

                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset profileAsset = dataMapItem.getDataMap().getAsset("profileMusic");
                String filename = dataMapItem.getDataMap().getString("filename");
                Long size = dataMapItem.getDataMap().getLong("filesize");
                Long count = dataMapItem.getDataMap().getLong("count");
                //InputStream inputStream = loadSoundFromAsset(profileAsset);
                saveMusicFromAsset(profileAsset, filename);


                Log.d(TAG, "receive asset ! file name = "+filename+" size = "+size);

                //sendReceiveComplete();

                Intent intent = new Intent(Constants.ACTION.RECEIVE_UPLOAD_COMPLETE);
                sendBroadcast(intent);


                /*Intent sendIntent = new Intent();
                sendIntent.setAction(GET_IMG_COMPLETE);
                sendBroadcast(sendIntent);*/
            }
        }
    }



    public void saveMusicFromAsset(Asset asset, String filename) {
        int ret;
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result = mGoogleApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            //return null;
        } else {
            // convert asset into a file descriptor and block until it's ready
            InputStream assetInputStream = Wearable.DataApi.getFdForAsset(mGoogleApiClient, asset).await().getInputStream();

            if (assetInputStream == null) {
                Log.e(TAG, "Requested an unknown Asset.");

            } else {
                ret = copyInputStreamToFile(assetInputStream, filename);
                Log.e(TAG, "ret = "+ret);
            }




        }

    }


}
