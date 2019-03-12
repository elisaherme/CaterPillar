package com.example.caterpillar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.Calendar;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import android.app.TimePickerDialog;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.github.nkzawa.socketio.client.Socket;
import com.server.interaction.SocketManager;

import org.json.JSONException;
import org.json.JSONObject;

public class QuestionsActivity extends AppCompatActivity {

    private TimePicker wakePicker;
    private Calendar calendar;
    private String format = "";
    private Socket mSocket;
    JSONObject questionAnswers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        final SocketManager app = (SocketManager) getApplication();
        mSocket = app.getmSocket();
        questionAnswers = new JSONObject();
        addToJSON("Wake","08:30" );
        addToJSON("Breakfast","09:15" );
        addToJSON("Lunch","13:00" );
        addToJSON("Dinner","18:45" );
        addToJSON("Sleep","23:15" );
        Button registerButton = findViewById(R.id.button_next);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                JSONObject topLevel = new JSONObject();
                try {
                    questionAnswers.put("Name", app.getUser() );
                    topLevel.put("type", "questions");
                    topLevel.put("data", questionAnswers);
                } catch (JSONException e) {
                    Log.d("Registration", e.getMessage());
                }
                mSocket.emit("data",topLevel);
                Log.d("Sent QuestionAnswers", topLevel.toString());
                Intent intent = new Intent(QuestionsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        this.showTimePickerDialog();
    }
    private void addToJSON(String tag, String input ){
        try {
            questionAnswers.put(tag,input) ;
        } catch (JSONException e) {
            Log.d(tag, e.getMessage());
        }
    }

//    // Create and show a TimePickerDialog when click button.
    private void showTimePickerDialog()
    {
        // Get open TimePickerDialog button.
        final Button timePickerDialogWake = (Button)findViewById(R.id.button_wake);
        timePickerDialogWake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new OnTimeSetListener instance. This listener will be invoked when user click ok button in TimePickerDialog.
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        StringBuffer strBuf = new StringBuffer();
                        if(hour < 10){
                            strBuf.append("0");
                        }
                        strBuf.append(hour);
                        strBuf.append(":");
                        strBuf.append(minute);
                        addToJSON("Wake",strBuf.toString() );
                        timePickerDialogWake.setText(strBuf.toString());
                    }
                };

                Calendar now = Calendar.getInstance();
                int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = now.get(java.util.Calendar.MINUTE);

                // Whether show time in 24 hour format or not.
                boolean is24Hour = true;

                TimePickerDialog timePickerDialog = new TimePickerDialog(QuestionsActivity.this, onTimeSetListener, hour, minute, is24Hour);

                timePickerDialog.setTitle("Please select time.");

                timePickerDialog.show();
            }
        });

        final Button timePickerDialogBreakfast = (Button)findViewById(R.id.button_breakfast);
        timePickerDialogBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new OnTimeSetListener instance. This listener will be invoked when user click ok button in TimePickerDialog.
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        StringBuffer strBuf = new StringBuffer();
                        if(hour < 10){
                            strBuf.append("0");
                        }
                        strBuf.append(hour);
                        strBuf.append(":");
                        strBuf.append(minute);
                        addToJSON("Breakfast",strBuf.toString());
                        timePickerDialogBreakfast.setText(strBuf.toString());
                    }
                };

                Calendar now = Calendar.getInstance();
                int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = now.get(java.util.Calendar.MINUTE);

                // Whether show time in 24 hour format or not.
                boolean is24Hour = true;

                TimePickerDialog timePickerDialog = new TimePickerDialog(QuestionsActivity.this, onTimeSetListener, hour, minute, is24Hour);

                timePickerDialog.setTitle("Please select time.");

                timePickerDialog.show();
            }
        });

        final Button timePickerDialogLunch = (Button)findViewById(R.id.button_lunch);
        timePickerDialogLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new OnTimeSetListener instance. This listener will be invoked when user click ok button in TimePickerDialog.
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        StringBuffer strBuf = new StringBuffer();
                        if(hour < 10){
                            strBuf.append("0");
                        }
                        strBuf.append(hour);
                        strBuf.append(":");
                        strBuf.append(minute);
                        addToJSON("Lunch",strBuf.toString());
                        timePickerDialogLunch.setText(strBuf.toString());
                    }
                };

                Calendar now = Calendar.getInstance();
                int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = now.get(java.util.Calendar.MINUTE);

                // Whether show time in 24 hour format or not.
                boolean is24Hour = true;

                TimePickerDialog timePickerDialog = new TimePickerDialog(QuestionsActivity.this, onTimeSetListener, hour, minute, is24Hour);

                timePickerDialog.setTitle("Please select time.");

                timePickerDialog.show();
            }
        });

        final Button timePickerDialogDinner = (Button)findViewById(R.id.button_dinner);
        timePickerDialogDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new OnTimeSetListener instance. This listener will be invoked when user click ok button in TimePickerDialog.
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        StringBuffer strBuf = new StringBuffer();
                        if(hour < 10){
                            strBuf.append("0");
                        }
                        strBuf.append(hour);
                        strBuf.append(":");
                        strBuf.append(minute);
                        addToJSON("Dinner",strBuf.toString());
                        timePickerDialogDinner.setText(strBuf.toString());
                    }
                };

                Calendar now = Calendar.getInstance();
                int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = now.get(java.util.Calendar.MINUTE);

                // Whether show time in 24 hour format or not.
                boolean is24Hour = true;

                TimePickerDialog timePickerDialog = new TimePickerDialog(QuestionsActivity.this, onTimeSetListener, hour, minute, is24Hour);

                timePickerDialog.setTitle("Please select time.");

                timePickerDialog.show();
            }
        });

        final Button timePickerDialogSleep = (Button)findViewById(R.id.button_sleep);
        timePickerDialogSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new OnTimeSetListener instance. This listener will be invoked when user click ok button in TimePickerDialog.
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        StringBuffer strBuf = new StringBuffer();
                        if(hour < 10){
                            strBuf.append("0");
                        }
                        strBuf.append(hour);
                        strBuf.append(":");
                        strBuf.append(minute);
                        addToJSON("Sleep",strBuf.toString());
                        timePickerDialogSleep.setText(strBuf.toString());
                    }
                };

                Calendar now = Calendar.getInstance();
                int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = now.get(java.util.Calendar.MINUTE);

                // Whether show time in 24 hour format or not.
                boolean is24Hour = true;

                TimePickerDialog timePickerDialog = new TimePickerDialog(QuestionsActivity.this, onTimeSetListener, hour, minute, is24Hour);

                timePickerDialog.setTitle("Please select time.");

                timePickerDialog.show();
            }
        });
    }
}
