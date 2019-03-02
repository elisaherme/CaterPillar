package com.example.caterpillar;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
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
    private SensorReader mSensorReader; // implements SensorEventListener
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private String nextDosageTime;

    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.text);
        nextDosageTime = "Hello! It's not time for your next dosage yet!";

        // Sensor-related instantiations
        mSensorReader = new SensorReader(this); // new listener
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Set up rest of the home screen
        homeScreen();

        // Enables Always-on
        setAmbientEnabled();

        // Instantiate API client for sending data to phone
        mDataClient = Wearable.getDataClient(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        Wearable.getDataClient(this).addListener(this);

        mTextView.setText(nextDosageTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        Wearable.getDataClient(this).removeListener(this);
    }

    private void homeScreen(){
        mTextView.setText(nextDosageTime);
        sleepButton = findViewById(R.id.sleepButton);
        isMeasuring = false;

        // Stop listening to accelerometer data
        mSensorManager.unregisterListener(mSensorReader);
    }


    public void startSleeping(View view){
        isMeasuring = true;
        setContentView(R.layout.sensor);

        // Start listening to accelerometer data
        mSensorManager.registerListener(mSensorReader, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);

        // http://coderzpassion.com/implement-service-android/
        Intent intent = new Intent(this, SensorReader.class);
        startService(intent);

    }

    public void stopSleeping(View view){
        Intent intent = new Intent(this, SensorReader.class);
        stopService(intent);
        setContentView(R.layout.activity_main);
        homeScreen();
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

    // SensorReader.OnSensorChanged() is calling this once for every 200 readings
    public void sendData(ArrayList<String> accelerometerData) {
        Log.d(TAG, "sending data: " + accelerometerData);

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/acc_data"); // create data map
        putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY, accelerometerData); // put data in map

        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();

        // Send data
        Task<DataItem> putDataTask = mDataClient.putDataItem(putDataReq);
        putDataTask.addOnSuccessListener(
                new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.i(TAG, "Sending sensor readings was successful: " + dataItem);
                    }
                });
    }

    // save next dosage time in a variable
    private void updateSchedule(String time) {
        nextDosageTime = "The time for your next dose is: " + time;
        Log.d(TAG, "received next dosage time: " + time);

//        mTextView.setText("The time for your next dose is: " + nextDosageTime);
    }
}
