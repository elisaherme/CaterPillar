package com.example.caterpillar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

public class SensorDataSender extends Service {
    private static final String TAG = SensorDataSender.class.getSimpleName();
    private static final String COUNT_KEY = "com.example.caterpillar.count";

    private SensorReader mSensorReader;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    //private DataClient mDataClient;

    // SensorReader.OnSensorChanged() is calling this once for every 200 readings
    public void sendData(ArrayList<String> accelerometerData) {
        Log.d(TAG, "sending data: " + accelerometerData);

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/acc_data"); // create data map
        putDataMapReq.getDataMap().putStringArrayList(COUNT_KEY, accelerometerData); // put data in map

        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();

        // Send data
        Task<DataItem> putDataTask = Wearable.getDataClient(this).putDataItem(putDataReq);
        putDataTask.addOnSuccessListener(
                new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.i(TAG, "Sending sensor readings was successful: " + dataItem);
                    }
                });
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "in SensorDataSender onStartCommand");

        mSensorReader = new SensorReader(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Start listening to accelerometer data
        mSensorManager.registerListener(mSensorReader, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);

        Intent sensorIntent = new Intent(this, SensorReader.class);
        startService(sensorIntent);

        // Instantiate API client for sending data to phone
        //mDataClient = Wearable.getDataClient(SensorDataSender.this);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "in SensorDataSender onDestroy");

        // Stop listening to accelerometer data
        mSensorManager.unregisterListener(mSensorReader);
        Intent intent = new Intent(this, SensorReader.class);
        stopService(intent);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
