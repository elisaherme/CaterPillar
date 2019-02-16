package com.example.caterpillar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class QuestionsActivity extends AppCompatActivity {

    private TimePicker wakePicker;
    private Calendar calendar;
    private String format = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Button registerButton = findViewById(R.id.button_next);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(QuestionsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        this.showTimePickerDialog();
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
