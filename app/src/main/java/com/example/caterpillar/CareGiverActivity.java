package com.example.caterpillar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.server.interaction.SocketManager;

import org.json.JSONArray;
import org.json.JSONObject;

public class CareGiverActivity extends AppCompatActivity {

    private Socket mSocket;
    private final String [] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private final String [] times = {"Morning", "Afternoon", "Night"};

    private String name;
    private String caregiver;
    private TextView userName;
    private TextView careGiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_giver);

        SocketManager app = (SocketManager) getApplication();
        mSocket = app.getmSocket();
        mSocket.on("responseMed", medRequest);
        mSocket.emit("queryMed", app.getUser());
        mSocket.on("responseHistory", historyRequest);
        mSocket.emit("queryHistory", app.getUser());

        userName = findViewById(R.id.textPatientName);
        careGiver = findViewById(R.id.textView4);

        name = app.getUser();
        caregiver = app.getCaregiver();
        userName.setText(name);
        careGiver.setText(caregiver);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("responseMed", medRequest);
    }

    public void onClickAddMedication(View view) {
        Intent intent = new Intent(this, AddMedicationActivity.class);
        startActivity(intent);
    }
    public void onClickUser(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private Emitter.Listener medRequest = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray data = (JSONArray) args[0];
                    Log.i ("Received JSON Data" , data.toString());

                    TextView textMedicationDetail = (TextView) findViewById(R.id.textMedicationDetail);
                    textMedicationDetail.setMovementMethod(new ScrollingMovementMethod());

                    try {
                        String textOut = "";
                        if(data.length() == 0) {
                            textOut = "No medication entry.";
                        }
                        else {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);

                                textOut = textOut + (i + 1) + ". " + obj.getString("Name") + ";\t\t\ton ";
                                for( String day : days ) {
                                    if(obj.getBoolean(day)){
                                        textOut = textOut + day + " ";
                                    }
                                }
                                textOut = textOut + ";\t\tat ";
                                for( String time : times ) {
                                    if(obj.getBoolean(time)){
                                        textOut = textOut + time + " ";
                                    }
                                }
                                textOut =  textOut + "\n";
                            }
                        }
                        textMedicationDetail.setText(textOut);
                    }
                    catch (Exception e) {
                        Log.e("medRequest", e.getMessage());
                        return;
                   }

                }
            });
        }
    };
    private Emitter.Listener historyRequest = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray data = (JSONArray) args[0];
                    Log.i ("Received JSON Data" , data.toString());

                    TextView textMedicationIntake = (TextView) findViewById(R.id.textMedicationIntake);
                    textMedicationIntake.setMovementMethod(new ScrollingMovementMethod());

                    try {
                        String textOut = "";
                        if(data.length() == 0) {
                            textOut = "No medication intake history.";
                        }
                        else {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                textOut = textOut + (i + 1) + ". " + obj.getString("MedName") + ";\t\t\tat " + obj.getString("Timestamp") + "\n";
                            }
                        }
                        textMedicationIntake.setText(textOut);
                    }
                    catch (Exception e) {
                        Log.e("historyRequest", e.getMessage());
                        return;
                    }

                }
            });
        }
    };

}
