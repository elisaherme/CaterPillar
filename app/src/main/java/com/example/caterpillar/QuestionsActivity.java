package com.example.caterpillar;

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

//        wakePicker = findViewById(R.id.wake_timePicker);
//        calendar = Calendar.getInstance();
//
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int min = calendar.get(Calendar.MINUTE);

//        this.showTimePickerDialog();
    }

//    // Create and show a TimePickerDialog when click button.
//    private void showTimePickerDialog()
//    {
//        // Get open TimePickerDialog button.
//        Button timePickerDialogButton = (Button)findViewById(R.id.button);
//        timePickerDialogButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Create a new OnTimeSetListener instance. This listener will be invoked when user click ok button in TimePickerDialog.
//                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
//                        StringBuffer strBuf = new StringBuffer();
//                        strBuf.append(hour);
//                        strBuf.append(":");
//                        strBuf.append(minute);
//
//                        TextView timePickerValueTextView = (TextView)findViewById(R.id.timePickerValue);
//                        timePickerValueTextView.setText(strBuf.toString());
//                    }
//                };
//
//                Calendar now = Calendar.getInstance();
//                int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
//                int minute = now.get(java.util.Calendar.MINUTE);
//
//                // Whether show time in 24 hour format or not.
//                boolean is24Hour = true;
//
//                TimePickerDialog timePickerDialog = new TimePickerDialog(QuestionsActivity.this, onTimeSetListener, hour, minute, is24Hour);
//
//                timePickerDialog.setTitle("Please select time.");
//
//                timePickerDialog.show();
//            }
//        });
//    }

}
