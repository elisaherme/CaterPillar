package com.example.caterpillar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

public class MainActivity extends WearableActivity implements DataClient.OnDataChangedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String COUNT_KEY = "com.example.caterpillar.count";
    private DataClient mDataClient; // = Wearable.getDataClient(this);

    private Button sleepButton;
    private Button wakeButton;
    private boolean isMeasuring;

    private String nextDosageTime;

    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        // Enables Always-on
        setAmbientEnabled();

        // Instantiate API client for sending data to phone
        mDataClient = Wearable.getDataClient(this);

        // layout shenanigans depend on state of user: sleep or awake
        setContentView(R.layout.activity_main); // seems redundant, but nec for onResume to set textview
        mTextView = findViewById(R.id.text);
        isMeasuring = false;

        // get state: isMeasuring, rewrite current isMeasuring to saved version
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        isMeasuring = sharedPref.getBoolean("isMeasuring", isMeasuring);

        // get next dosage time
        nextDosageTime = sharedPref.getString("nextDosageTime", " ");
        if(nextDosageTime==" "){
            nextDosageTime = "Hello! It's not time for your next dosage yet!";
        }

        if(isMeasuring){
            startSleeping(null);
        }
        else{
            stopSleeping(null);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        Wearable.getDataClient(this).addListener(this);

        // phone might have sent an update when app was paused
        mTextView.setText(nextDosageTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        Wearable.getDataClient(this).removeListener(this);
    }


    public void startSleeping(View view){
        isMeasuring = true;

        // make sure it was from button click
        if (view!=null){
            sendCurTime("/start_time");
        }

        setContentView(R.layout.sensor);

        // Will start listening to accelerometer data in SensorDataSender
        // http://coderzpassion.com/implement-service-android/
        Intent sendIntent = new Intent(this, SensorDataSender.class);
        startService(sendIntent);

        // save state: isMeasuring aka isSleeping
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isMeasuring", true);
        editor.apply();
    }

    public void stopSleeping(View view){
        Intent sendIntent = new Intent(this, SensorDataSender.class);
        stopService(sendIntent);

        // make sure it was from button click
        if(view!=null){
            sendCurTime("/stop_time");
        }


        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.text);
        mTextView.setText(nextDosageTime);
        sleepButton = findViewById(R.id.sleepButton);

        isMeasuring = false;

        // save state: !isMeasuring aka !isSleeping
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isMeasuring", false);
        editor.apply();
    }


    // Receive and save next dosage time from the phone/tablet app
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        //Log.d(TAG, "onDataChanged");
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.i(TAG, "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.i(TAG, "DataItem changed: " + event.getDataItem().getUri());
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/dosage_time") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    updateSchedule(dataMap.getString(COUNT_KEY));
                }
            }
        }
    }

    // state HAS TO BE either "/start_time" or "/stop_time"
    private void sendCurTime(String state) {
        Log.d(TAG, "sending current time");
        final long t = System.currentTimeMillis();

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(state);
        putDataMapReq.getDataMap().putLong(COUNT_KEY, t);

        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();

        // Send data
        Task<DataItem> putDataTask = mDataClient.putDataItem(putDataReq);
        putDataTask.addOnSuccessListener(
                new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.i(TAG, "Sending current time was successful: " + t);
                    }
                });

    }

    // save next dosage time in a variable
    private void updateSchedule(String time) {
        nextDosageTime = "The time for your next dose is: " + time;
        Log.d(TAG, "received next dosage time: " + time);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("nextDosageTime", nextDosageTime);
        editor.apply();

//        mTextView.setText("The time for your next dose is: " + nextDosageTime);
    }
}
