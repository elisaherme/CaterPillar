package com.example.caterpillar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.github.nkzawa.socketio.client.Socket;
import com.server.interaction.SocketManager;

import org.json.JSONException;
import org.json.JSONObject;

public class AddMedicationActivity extends AppCompatActivity {
    private Socket mSocket;
    private EditText editName;
    private EditText editInstruction;

    private Boolean checkBoxMon;
    private Boolean checkBoxTue;
    private Boolean checkBoxWed;
    private Boolean checkBoxThu;
    private Boolean checkBoxFri;
    private Boolean checkBoxSat;
    private Boolean checkBoxSun;

    private Boolean checkBoxMorning;
    private Boolean checkBoxAfternoon;
    private Boolean checkBoxNight;

    private RadioGroup radioGroupMeal;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        editName = (EditText) findViewById(R.id.editName);
        editInstruction = (EditText) findViewById(R.id.editInstruction);
    }

    public void onClickConfirm(View view) {
        SocketManager app = (SocketManager) getApplication();
        mSocket = app.getmSocket();

        setContentView(R.layout.activity_add_medication);

        checkBoxMon = ((CheckBox) findViewById(R.id.checkBoxMon)).isChecked();
        checkBoxTue = ((CheckBox) findViewById(R.id.checkBoxTue)).isChecked();
        checkBoxWed = ((CheckBox) findViewById(R.id.checkBoxWed)).isChecked();
        checkBoxThu = ((CheckBox) findViewById(R.id.checkBoxThu)).isChecked();
        checkBoxFri = ((CheckBox) findViewById(R.id.checkBoxFri)).isChecked();
        checkBoxSat = ((CheckBox) findViewById(R.id.checkBoxSat)).isChecked();
        checkBoxSun = ((CheckBox) findViewById(R.id.checkBoxSun)).isChecked();

        checkBoxMorning = ((CheckBox) findViewById(R.id.checkBoxMorning)).isChecked();
        checkBoxAfternoon = ((CheckBox) findViewById(R.id.checkBoxAfternoon)).isChecked();
        checkBoxNight = ((CheckBox) findViewById(R.id.checkBoxNight)).isChecked();

        radioGroupMeal = (RadioGroup) findViewById(R.id.radioGroupMeal);
        radioButton = (RadioButton) findViewById(radioGroupMeal.getCheckedRadioButtonId());

        JSONObject topLevel = new JSONObject();
        JSONObject regDetails = new JSONObject();
        try {
            regDetails.put("Name", editName.getText().toString());
            regDetails.put("Instruction", editInstruction.getText().toString());
            regDetails.put("Mon", checkBoxMon);
            regDetails.put("Tue", checkBoxTue);
            regDetails.put("Wed", checkBoxWed);
            regDetails.put("Thu", checkBoxThu);
            regDetails.put("Fri", checkBoxFri);
            regDetails.put("Sat", checkBoxSat);
            regDetails.put("Sun", checkBoxSun);
            regDetails.put("Morning", checkBoxMorning);
            regDetails.put("Afternoon", checkBoxAfternoon);
            regDetails.put("Night", checkBoxNight);
            regDetails.put("Meal", radioButton.getText().toString());

            topLevel.put("type", "addMedication");
            topLevel.put("data", regDetails);
        } catch (JSONException e) {
            Log.d("Medication", e.getMessage());
        }
        mSocket.emit("data", topLevel);
        Log.i("socket", "sent medication details");
        Log.i("name", editName.getText().toString());
        Log.i("instr", editInstruction.getText().toString());
        Log.i("checkpoint", "mmm");

        Intent intent = new Intent(this, CareGiverActivity.class);
        startActivity(intent);
    }
}
