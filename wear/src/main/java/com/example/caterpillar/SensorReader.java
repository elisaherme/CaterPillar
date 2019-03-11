package com.example.caterpillar;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class SensorReader extends Service implements SensorEventListener {
    // Default constructor for service
    // https://stackoverflow.com/questions/25924465/android-manifest-has-no-default-constructor-with-activity-runnable-class/25924524
    public SensorReader() {

    }


    // https://stackoverflow.com/questions/7836415/call-a-public-method-in-the-activity-class-from-another-class#comment9554272_7836465
    private SensorDataSender mSensorDataSender;
    public SensorReader(SensorDataSender sender) {
        mSensorDataSender = sender;
    }

    private static final String TAG = SensorReader.class.getSimpleName();
    private ArrayList<String> accelerometerData = new ArrayList<String>();
    private boolean isMeasuring = false;


    @Override
    public void onSensorChanged(SensorEvent event) {
        // If sensor is unreliable, then just return
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return;
        }
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        // Log.d(TAG, "x is " + Float.toString(x));

        accelerometerData.add(System.currentTimeMillis() + "," + gX + "," + gY + "," + gZ);

        // Don't send data too frequently -> TODO: experiment with rates
        if(accelerometerData.size() == 200) {
            mSensorDataSender.sendData(accelerometerData);
            accelerometerData.clear();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // don't want to allow binding
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(TAG, "in onStartCommand");
        //isMeasuring = true;

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        Log.i(TAG, "in onDestroy");
        isMeasuring = false;
    }
}
