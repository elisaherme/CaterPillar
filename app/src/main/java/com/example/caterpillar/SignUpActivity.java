package com.example.caterpillar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.socketio.client.Socket;
import com.server.interaction.SocketManager;
import com.utils.encryption.AESUtils;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.SecretKey;

public class SignUpActivity extends AppCompatActivity {
    private EditText inputName ;
    private EditText userName;
    private EditText password;
    private EditText caregiverName;
    private Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        inputName = findViewById(R.id.input_name);
        userName = findViewById(R.id.input_user);
        password = findViewById(R.id.input_password);
        caregiverName = findViewById(R.id.input_caregiver);
        final SocketManager app = (SocketManager) getApplication();
        mSocket = app.getmSocket();
        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // For now allow
                JSONObject regDetails = new JSONObject();
                JSONObject topLevel = new JSONObject();
                SecretKey secretKey = AESUtils.createKey(  getResources().getString(R.string.encrypt_key));
                String userEncrypt = new String (AESUtils.encrypt(secretKey, userName.getText().toString().getBytes()));
                String passEncrypt =new String (AESUtils.encrypt(secretKey, password.getText().toString().getBytes()));
                try {
                    regDetails.put("Username", userEncrypt);
                    regDetails.put("Password", passEncrypt);
                    regDetails.put("Name",inputName.getText().toString());
                    regDetails.put("Caregiver", caregiverName.getText().toString());
                    topLevel.put("type", "userDetails");
                    topLevel.put("data", regDetails);
                } catch (JSONException e) {
                    Log.d("Registration", e.getMessage());
                }
                // Set user
                app.setUser(inputName.getText().toString());
                app.setCaregiver(caregiverName.getText().toString());
                // Registration event to write to files
                mSocket.emit("data", topLevel);
                Log.i("socket", "sent registration details");
                Intent intent = new Intent(SignUpActivity.this, QuestionsActivity.class);
                startActivity(intent);
            }
        });
    }
}
