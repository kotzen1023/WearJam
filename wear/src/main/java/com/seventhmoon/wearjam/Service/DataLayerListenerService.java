package com.seventhmoon.wearjam.Service;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
            }
            else if("/MUSIC".equals(path)) {
                Log.d(TAG, "/MUSIC");

                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset profileAsset = dataMapItem.getDataMap().getAsset("profileVoice");
                InputStream inputStream = loadSoundFromAsset(profileAsset);
                String filename = dataMapItem.getDataMap().getString("filename");
                Long size = dataMapItem.getDataMap().getLong("datasize");
                Long count = dataMapItem.getDataMap().getLong("count");


                Log.d(TAG, "receive asset ! file name = "+filename+" size = "+size+" count = "+count);




                /*Intent sendIntent = new Intent();
                sendIntent.setAction(GET_IMG_COMPLETE);
                sendBroadcast(sendIntent);*/
            }
        }
    }

    public InputStream loadSoundFromAsset(Asset asset) {


        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =
                mGoogleApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }

        // decode the stream into a bitmap
        return assetInputStream;
    }
}
