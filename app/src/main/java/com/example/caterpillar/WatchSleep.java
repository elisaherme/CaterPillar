package com.example.caterpillar;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.server.interaction.SocketManager;

import java.util.ArrayList;

public class WatchSleep extends Service implements DataClient.OnDataChangedListener {
    private static final String TAG = WatchSleep.class.getSimpleName();

    private static final String COUNT_KEY = "com.example.caterpillar.count";

    private Socket mSocket;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents){
        Log.d(TAG, "onDataChanged(): " + dataEvents);
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                Log.d(TAG, "DataItem TYPE_CHANGED");
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/acc_data") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    updateCount(dataMap.getStringArrayList(COUNT_KEY));
                }
                else if (item.getUri().getPath().compareTo("/start_time") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    updateSleepTime(dataMap.getLong(COUNT_KEY));
                }
                else if (item.getUri().getPath().compareTo("/stop_time") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    updateSleepTime(dataMap.getLong(COUNT_KEY));
                }
            }
            else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
                Log.d(TAG, "DataItem TYPE_DELETED");

            }
        }
    }

    // Update the count and print on phone
    private void updateCount(ArrayList<String> c ) {
        Log.i(TAG, "UPDATE COUNT " + c);

//        if (c != null) {
//            String s = c.get(0);
//
//            String time = s.split(",")[0];
//            String x = s.split(",")[1];
//            String y = s.split(",")[2];
//            String z = s.split(",")[3];
//        }

        mSocket.emit("accData", c);

    }



    private void updateSleepTime(Long t) {
        // do something
        Log.i(TAG, "Sending time the button was pressed");
        mSocket.emit("sleepTime", t);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(TAG, "in WatchSleep, onStartCommand");

        SocketManager app = (SocketManager) getApplication();
        mSocket = app.getmSocket();

        Wearable.getDataClient(this).addListener(this);

        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
