package com.example.caterpillar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
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
    private String[] Names;
    private final String [] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private final String [] times = {"Morning", "Afternoon", "Night"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_giver);

        SocketManager app = (SocketManager) getApplication();
        mSocket = app.getmSocket();
        mSocket.on("responseMed", medRequest);
        Log.i ("user", app.getUser());
        mSocket.emit("queryMed", app.getUser());
    }

    public void onClickAddMedication(View view) {
        Intent intent = new Intent(this, AddMedicationActivity.class);
        startActivity(intent);
    }
    public void onClickUser(View view) {
        Intent intent = new Intent(this, pillbox.class);
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

                    try {
                        String textOut = "";
                        if(data.length() == 0) {
                            textOut = "No medication entry.";
                        }
                        else {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);

                                textOut = textOut + (i + 1) + ". " + obj.getString("Name") + "\t\t\ton ";
                                for( String day : days ) {
                                    if(obj.getBoolean(day)){
                                        textOut = textOut + day + " ";
                                    }
                                }
                                textOut = textOut + "\t\tat ";
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

}
