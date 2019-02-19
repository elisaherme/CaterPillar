package com.example.caterpillar;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

public class SensorReader implements SensorEventListener {

    // https://stackoverflow.com/questions/7836415/call-a-public-method-in-the-activity-class-from-another-class#comment9554272_7836465
    private MainActivity mActivity;
    // Constructor for class SensorReader to accept activity as arg
    public SensorReader(MainActivity activity) {
        mActivity = activity;
    }

    private static final String TAG = SensorReader.class.getSimpleName();
    private ArrayList<String> accelerometerData = new ArrayList<String>();


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
            mActivity.sendData(accelerometerData);

            accelerometerData.clear();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }
}
