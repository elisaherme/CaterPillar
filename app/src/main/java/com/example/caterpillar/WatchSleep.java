package com.example.caterpillar;

import android.os.Bundle;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class WatchSleep extends Activity implements DataClient.OnDataChangedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String COUNT_KEY = "com.example.caterpillar.count";

    private DataClient mDataClient;

    TextView mTextView;
    EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.text_values);
        mEditText = findViewById(R.id.edit_text);

        // Instantiate data client
        mDataClient = Wearable.getDataClient(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
    }

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
                    updateSleepTime(dataMap.getString(COUNT_KEY));
                }
                else if (item.getUri().getPath().compareTo("/stop_time") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    updateSleepTime(dataMap.getString(COUNT_KEY));
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

        if (c != null) {
            String s = c.get(0);

            String time = s.split(",")[0];
            String x = s.split(",")[1];
            String y = s.split(",")[2];
            String z = s.split(",")[3];

            // Show this on the phone
            mTextView.setText(
                    "x = " + x + "\n" +
                            "y = " + y + "\n" +
                            "z = " + z + "\n"
            );
        }

    }

    public void sendTime(View view){
        String toSend = mEditText.getText().toString();
        if(toSend.isEmpty()) {
            toSend = "You forgot to input time";
        }
        Log.d(TAG, "sending data: " + toSend);

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/dosage_time");
        putDataMapReq.getDataMap().putString(COUNT_KEY, toSend);

        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();

        // Send data
        Task<DataItem> putDataTask = mDataClient.putDataItem(putDataReq);
        putDataTask.addOnSuccessListener(
                new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.i(TAG, "Sending time was successful: " + dataItem);
                    }
                });
    }

    private void updateSleepTime(String t) {
        // do something
    }

}
