package com.example.caterpillar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.server.interaction.SocketManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class pillbox extends AppCompatActivity {
    private Socket mSocket;
    private final String [] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private final String [] times = {"Mor", "Aft", "Ngt"};
    private final String [] fulltimes = {"Morning", "Afternoon", "Night"};
    private ImageView [] empty;
//    private final Map<String, String> DayTime_to_Index = new HashMap<String, String>() {{
//        put("foo", "bar");
//        put("key", "value");
//        //etc
//    }};

    private SocketManager app;
    private String name;
    private TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pillbox);

        app = (SocketManager) getApplication();
        mSocket = app.getmSocket();
        mSocket.on("responseMed", fillPillbox);
        mSocket.emit("queryMed", app.getUser());
        mSocket.on("slot_opened",slotOpen);
        mSocket.on("pill",pillDisplay);
        mSocket.on("pill_presence", updateEmptyStatus);
        mSocket.on("wrong_lid", warnWrongLid);
        mSocket.on("startNotification", notification);
        userName = findViewById(R.id.textUsername);
        name = app.getUser();
        userName.setText(name);

        empty = new ImageView[] {   findViewById(R.id.emptySunMor), findViewById(R.id.emptySunAft), findViewById(R.id.emptySunNgt),
                                    findViewById(R.id.emptyMonMor), findViewById(R.id.emptyMonAft), findViewById(R.id.emptyMonNgt),
                                    findViewById(R.id.emptyTueMor), findViewById(R.id.emptyTueAft), findViewById(R.id.emptyTueNgt),
                                    findViewById(R.id.emptyWedMor), findViewById(R.id.emptyWedAft), findViewById(R.id.emptyWedNgt),
                                    findViewById(R.id.emptyThuMor), findViewById(R.id.emptyThuAft), findViewById(R.id.emptyThuNgt),
                                    findViewById(R.id.emptyFriMor), findViewById(R.id.emptyFriAft), findViewById(R.id.emptyFriNgt),
                                    findViewById(R.id.emptySatMor), findViewById(R.id.emptySatAft), findViewById(R.id.emptySatNgt)
                                };
    }
    private Emitter.Listener slotOpen = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.i ("Received JSON Data" , data.toString());
                    Toast.makeText(pillbox.this, "Raspberry Pi Slot Opened, MSG: " +  data.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private Emitter.Listener pillDisplay = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.i ("Received JSON Data" , data.toString());
                    Toast.makeText(pillbox.this, "Raspberry Pi Pill taken, MSG: " +  data.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };



    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("responseMed", fillPillbox);
    }

    public void onClickUser(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private Emitter.Listener warnWrongLid = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    };


    private Emitter.Listener updateEmptyStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String tmpState = data.getString("emptyState");

                        int[] state = {
                                1, 1, 1,
                                1, 1, 1,
                                1, 1, 1,
                                tmpState.charAt(0) - '0', tmpState.charAt(1) - '0', 1,
                                tmpState.charAt(2) - '0', tmpState.charAt(3) - '0', 1,
                                1, 1, 1,
                                1, 1, 1,
                        };
                        TextView textSlot;
                        int i = 0;
                        for (String day : days) {
                            for (String time : times) {
                                String slotID = "text" + day + time;
                                int resID = getResources().getIdentifier(slotID, "id", getPackageName());
                                textSlot = findViewById(resID);

                                if(textSlot.getText().toString() == ""){
                                    empty[i].setVisibility(View.INVISIBLE);
                                }
                                else if(state[i] == 0){
                                    empty[i].setVisibility(View.VISIBLE);
                                }
                                else{
                                    empty[i].setVisibility(View.INVISIBLE);
                                }

//                                Log.i("i", Integer.toString(i));
//                                Log.i("state", Integer.toString(state[i]));

//                                if (state[i] == 1) {
//                                    empty[i].setVisibility(View.INVISIBLE);
//                                } else {
//                                    empty[i].setVisibility(View.VISIBLE);
//                                }
                                i = i + 1;
                            }
                        }
                    }
                    catch (Exception e) {
                        Log.e("updateEmptyStatus", e.getMessage());
                        return;
                    }
                }
            });
        }
    };


    private Emitter.Listener fillPillbox = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray data = (JSONArray) args[0];
                    Log.i ("Received JSON Data" , data.toString());


                    TextView textSlot;
                    TextView textMedicationDetail = (TextView) findViewById(R.id.textMonMor);
                    ImageView imageSlot;
                    try {
                        for(String day : days) {
                            for(String time : times){
                                String slotID = "text" + day + time;
                                int resID = getResources().getIdentifier(slotID, "id", getPackageName());
                                textSlot = findViewById(resID);
                                textSlot.setText("");

                                slotID = "empty" + day + time;
                                resID = getResources().getIdentifier(slotID, "id", getPackageName());
                                imageSlot = findViewById(resID);
                                imageSlot.setVisibility(View.VISIBLE);
                            }
                        }

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            String medName = obj.getString("Name");

                            for(int j=0; j<times.length; j++) {
                                if(obj.getBoolean(fulltimes[j])){
                                    for( String day : days ) {
                                        if(obj.getBoolean(day)){
                                            String slotID = "text" + day + times[j];
                                            int resID = getResources().getIdentifier(slotID, "id", getPackageName());

                                            textSlot = findViewById(resID);
                                            textSlot.setText(textSlot.getText().toString() + medName + "\n");
                                        }
                                    }

                                }
                            }
                        }

                    }
                    catch (Exception e) {
                        Log.e("fillPillbox", e.getMessage());
                        return;
                    }

                }
            });
        }
    };

    public void onClickRefresh(View view) {
//        for( String day : days ) {
//            for( String time : times ){
//                String textID = "text" + day + time;
//                int resID = getResources().getIdentifier(textID, "id", getPackageName());
//                TextView text = (TextView) findViewById(resID);
//                text.setText(day + "\n" + time);
//            }
//        }
        int [] tmpState = {1,1,0,1};
        int [] state = {tmpState[0],    tmpState[1],    1,
                        tmpState[2],    tmpState[3],    1,
                        1,              1,              1,
                        1,              1,              1,
                        1,              1,              1,
                        1,              1,              1,
                        1,              1,              1,
        };
        TextView textSlot;

        int i = 0;
        for(String day : days) {
            for(String time : times){
                String slotID = "text" + day + time;
                int resID = getResources().getIdentifier(slotID, "id", getPackageName());
                textSlot = findViewById(resID);
                textSlot.setText("test");

//                if(textSlot.getText().toString() == ""){
//                    empty[i].setVisibility(View.INVISIBLE);
//                }
//                else if(state[i] == 0){
//                    empty[i].setVisibility(View.VISIBLE);
//                }

                if (state[i] == 1) {
                        empty[i].setVisibility(View.INVISIBLE);
                    } else {
                        empty[i].setVisibility(View.VISIBLE);
                    }
                i = i+1;
            }
        }


    }

    private Emitter.Listener notification = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{
                        JSONObject data = (JSONObject) args[0];
                        Log.i ("Received JSON Data" , data.toString());

                        int alertLevel = data.getInt("alertLevel");
                        int boxSent = data.getInt("boxToSend");

                        TextView textPillbox;
                        String medName;
                        switch(boxSent) {
                            case 1:
                                textPillbox = findViewById(R.id.textWedMor);
                                medName = textPillbox.getText().toString();
                                break;
                            case 2:
                                textPillbox = findViewById(R.id.textWedAft);
                                medName = textPillbox.getText().toString();
                                break;
                            case 3:
                                textPillbox = findViewById(R.id.textThuMor);
                                medName = textPillbox.getText().toString();
                                break;
                            case 4:
                                textPillbox = findViewById(R.id.textThuAft);
                                medName = textPillbox.getText().toString();
                                break;
                            default:
                                medName = "";
                        }

                        app.sendNotification(alertLevel);

                        Intent intent = new Intent(pillbox.this, IntakeActivity.class);
                        intent.putExtra("medName", medName);
                        Log.e("PILLBOX", medName);
                        startActivity(intent);
                    }

                    catch (Exception e) {
                        Log.e("startNotification", e.getMessage());
                        return;
                    }
                }
            });
        }
    };
}
