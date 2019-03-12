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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WatchSleep extends Service implements DataClient.OnDataChangedListener {
    private static final String TAG = WatchSleep.class.getSimpleName();

    private static final String COUNT_KEY = "com.example.caterpillar.count";

    private Socket mSocket;
    private Long sleepTime;

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
                    updateWakeTime(dataMap.getLong(COUNT_KEY));
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
        JSONObject accData = new JSONObject();

        try {
            String s = c.get(0);

            String time = s.split(",")[0];
            String x = s.split(",")[1];
            String y = s.split(",")[2];
            String z = s.split(",")[3];

            accData.put("time", time);
            accData.put("x", x);
            accData.put("y", y);
            accData.put("z", z);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

//        if (c != null) {
//            String s = c.get(0);
//
//            String time = s.split(",")[0];
//            String x = s.split(",")[1];
//            String y = s.split(",")[2];
//            String z = s.split(",")[3];
//        }

        mSocket.emit("accData", accData);
        Log.i(TAG, "emit to socket successful: " + accData);

    }



    private void updateSleepTime(Long t) {
        // do something
        Log.i(TAG, "Saving time SLEEP button was pressed");
        sleepTime = t;
        //mSocket.emit("sleepTime", t);
    }

    private void updateWakeTime(Long t) {
        Log.i(TAG, "Sending time WAKE button was pressed");

        JSONObject topLevel = new JSONObject();
        JSONObject info = new JSONObject();

        try {
            info.put("Username", "pat");
            info.put("wakeTime", t.toString());
            info.put("sleepTime", sleepTime.toString());

            topLevel.put("type", "watchInfo");
            topLevel.put("data", info);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

        mSocket.emit("wakeTime", t);
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
    public void onDestroy() {
        // TODO: close socket connection

        Wearable.getDataClient(this).removeListener(this);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
