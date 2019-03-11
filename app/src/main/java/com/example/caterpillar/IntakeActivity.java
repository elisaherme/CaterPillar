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
import org.json.JSONObject;

public class IntakeActivity extends AppCompatActivity {

    private Socket mSocket;
    private final String [] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private final String [] times = {"Mor", "Aft", "Ngt"};
    private final String [] fulltimes = {"Morning", "Afternoon", "Night"};
    private Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intake);
    }

    public void onClickTaken(View view) {
        Intent intent = new Intent(this, pillbox.class);
        startActivity(intent);
    }

//    private Emitter.Listener fillPillbox = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    JSONArray data = (JSONArray) args[0];
//                    Log.i ("Received JSON Data" , data.toString());
//
//
//                    TextView textSlot;
//                    TextView textMedicationDetail = (TextView) findViewById(R.id.textMonMor);
//                    ImageView imageSlot;
//                    try {
//                        for(String day : days) {
//                            for(String time : times){
//                                String slotID = "text" + day + time;
//                                int resID = getResources().getIdentifier(slotID, "id", getPackageName());
//                                textSlot = findViewById(resID);
//                                textSlot.setText("");
//
//                                slotID = "empty" + day + time;
//                                resID = getResources().getIdentifier(slotID, "id", getPackageName());
//                                imageSlot = findViewById(resID);
//                                imageSlot.setVisibility(View.VISIBLE);
//                            }
//                        }
//
//                        for (int i = 0; i < data.length(); i++) {
//                            JSONObject obj = data.getJSONObject(i);
//                            String medName = obj.getString("Name");
//
//                            for(int j=0; j<times.length; j++) {
//                                if(obj.getBoolean(fulltimes[j])){
//                                    for( String day : days ) {
//                                        if(obj.getBoolean(day)){
//                                            String slotID = "text" + day + times[j];
//                                            int resID = getResources().getIdentifier(slotID, "id", getPackageName());
//
//                                            textSlot = findViewById(resID);
//                                            textSlot.setText(textSlot.getText().toString() + medName + "\n");
//                                        }
//                                    }
//
//                                }
//                            }
//                        }
//
//                    }
//                    catch (Exception e) {
//                        Log.e("fillPillbox", e.getMessage());
//                        return;
//                    }
//
//                }
//            });
//        }
//    };

}
