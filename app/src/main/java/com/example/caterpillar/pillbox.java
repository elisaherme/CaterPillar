package com.example.caterpillar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Arrays;
import java.util.List;

public class pillbox extends AppCompatActivity {
    private String [] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private String [] times = {"Mor", "Aft", "Ngt"};
    //private List<String> days = Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
    //private List<String> times = Arrays.asList("Mor", "Aft", "Ngt");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pillbox);
    }

    public void onClickRefresh(View view) {
        for( String day : days ) {
            for( String time : times ){

            }
        }
    }
}
