package com.example.caterpillar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class pillbox extends AppCompatActivity {
    private final String [] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private final String [] times = {"Mor", "Aft", "Ngt"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pillbox);
        //onClickRefresh(this);
    }

    public void onClickRefresh(View view) {
        for( String day : days ) {
            for( String time : times ){
                String textID = "text" + day + time;
                int resID = getResources().getIdentifier(textID, "id", getPackageName());
                TextView text = (TextView) findViewById(resID);
                text.setText(day + "\n" + time);
            }
        }
    }
}
