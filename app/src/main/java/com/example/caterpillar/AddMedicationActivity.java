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

    private CheckBox checkBoxMon;
    private CheckBox checkBoxTue;
    private CheckBox checkBoxWed;
    private CheckBox checkBoxThu;
    private CheckBox checkBoxFri;
    private CheckBox checkBoxSat;
    private CheckBox checkBoxSun;

    private CheckBox checkBoxMorning;
    private CheckBox checkBoxAfternoon;
    private CheckBox checkBoxNight;

    private RadioGroup radioGroupMeal;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        editName = (EditText) findViewById(R.id.editName);
        editInstruction = (EditText) findViewById(R.id.editInstruction);

        checkBoxMon = ((CheckBox) findViewById(R.id.checkBoxMon));
        checkBoxTue = ((CheckBox) findViewById(R.id.checkBoxTue));
        checkBoxWed = ((CheckBox) findViewById(R.id.checkBoxWed));
        checkBoxThu = ((CheckBox) findViewById(R.id.checkBoxThu));
        checkBoxFri = ((CheckBox) findViewById(R.id.checkBoxFri));
        checkBoxSat = ((CheckBox) findViewById(R.id.checkBoxSat));
        checkBoxSun = ((CheckBox) findViewById(R.id.checkBoxSun));

        checkBoxMorning = ((CheckBox) findViewById(R.id.checkBoxMorning));
        checkBoxAfternoon = ((CheckBox) findViewById(R.id.checkBoxAfternoon));
        checkBoxNight = ((CheckBox) findViewById(R.id.checkBoxNight));

    }

    public void onClickConfirm(View view) {
        SocketManager app = (SocketManager) getApplication();
        mSocket = app.getmSocket();

        setContentView(R.layout.activity_add_medication);

        radioGroupMeal = (RadioGroup) findViewById(R.id.radioGroupMeal);
        radioButton = (RadioButton) findViewById(radioGroupMeal.getCheckedRadioButtonId());

        Log.i("days", String.valueOf(checkBoxMon.isChecked()));

        JSONObject topLevel = new JSONObject();
        JSONObject regDetails = new JSONObject();
        try {
            regDetails.put("User", app.getUser());
            regDetails.put("Name", editName.getText().toString());
            regDetails.put("Instruction", editInstruction.getText().toString());
            regDetails.put("Mon", checkBoxMon.isChecked());
            regDetails.put("Tue", checkBoxTue.isChecked());
            regDetails.put("Wed", checkBoxWed.isChecked());
            regDetails.put("Thu", checkBoxThu.isChecked());
            regDetails.put("Fri", checkBoxFri.isChecked());
            regDetails.put("Sat", checkBoxSat.isChecked());
            regDetails.put("Sun", checkBoxSun.isChecked());
            regDetails.put("Morning", checkBoxMorning.isChecked());
            regDetails.put("Afternoon", checkBoxAfternoon.isChecked());
            regDetails.put("Night", checkBoxNight.isChecked());
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
