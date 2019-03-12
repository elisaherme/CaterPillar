package com.example.caterpillar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.server.interaction.SocketManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

public class IntakeActivity extends AppCompatActivity {

    private Socket mSocket;
    private SocketManager app;
    private final String [] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private final String [] times = {"Mor", "Aft", "Ngt"};
    private final String [] fulltimes = {"Morning", "Afternoon", "Night"};
    private TextView textMedication;
    private ImageView imageWarning;
    private TextView textWarning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intake);

        Intent myIntent = getIntent(); // gets the previously created intent
        String medName = myIntent.getStringExtra("medName");
        textMedication = findViewById(R.id.textPill);
        textMedication.setText(medName);

        imageWarning = findViewById(R.id.imageWarning);
        textWarning = findViewById(R.id.textWarning);

        app = (SocketManager) getApplication();
        mSocket = app.getmSocket();
        mSocket.on("wrong_lid", wrongLid);
        mSocket.on("correct_lid", correctLid);
        mSocket.on("intakeDone", intakeDone);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("wrong_lid", wrongLid);
        mSocket.off("correct_lid", correctLid);
        mSocket.off("intakeDone", intakeDone);
    }

    public void onClickTaken(View view) {
        Intent intent = new Intent(this, pillbox.class);
        startActivity(intent);
    }

    private Emitter.Listener correctLid = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("CORRECT", "correct lid");
                    imageWarning.setVisibility(View.GONE);
                    textWarning.setVisibility(View.GONE);
                }
            });
        }
    };

    private Emitter.Listener wrongLid = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("WRONG", "wrong lid");
                    imageWarning.setVisibility(View.VISIBLE);
                    textWarning.setVisibility(View.VISIBLE);
                }
            });
        }
    };

    private Emitter.Listener intakeDone = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("INTAKE", "done");

                    JSONObject topLevel = new JSONObject();
                    JSONObject regDetails = new JSONObject();
                    try {
                        regDetails.put("User", app.getUser());
                        regDetails.put("MedName", textMedication.getText().toString().replace('\n',','));
                        regDetails.put("Timestamp", DateFormat.getDateTimeInstance().format(new Date()));

                        topLevel.put("type", "timeTaken");
                        topLevel.put("data", regDetails);
                    }
                    catch (JSONException e) {
                        Log.d("intakeDone", e.getMessage());
                    }
                    mSocket.emit("data", topLevel);


                    Intent intent = new Intent(IntakeActivity.this, pillbox.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    };

}
