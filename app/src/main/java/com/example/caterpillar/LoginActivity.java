package com.example.caterpillar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.server.interaction.SocketManager;


public class LoginActivity extends AppCompatActivity {
    private EditText mUsernameView;
    private EditText mPasswordView;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SocketManager app = (SocketManager) getApplication();
        mSocket = app.getmSocket();

        // Set up the login details
        mUsernameView = findViewById(R.id.input_user);
        mPasswordView = findViewById(R.id.input_password);
        if (mSocket.connected()) {
            Toast.makeText(LoginActivity.this, "Connected!!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LoginActivity.this, "Not Connected!!", Toast.LENGTH_SHORT).show();
        }
        mSocket.on("LoginAuth", LoginAuth);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("LoginAuth", LoginAuth);
    }
    private Emitter.Listener LoginAuth = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    try {
                        message = data.getString("Success");
                        Log.i ("loginAuth",message);

                        
                        if (message.equals("Success")) {
                            Log.i ("loginAuth","Starting new intent");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }else {

                            Log.i ("loginAuth","Failed to pass auth " + message);
                            Toast.makeText(LoginActivity.this, "Wrong Password or Username!", Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        Log.e("loginAuth", e.getMessage());
                        return;
                    }
                }
            });
        }
    };


    public void onClickCreateAccount(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void onClickLogin(View view) {
        JSONObject loginDetails = new JSONObject();
        JSONObject topLevel = new JSONObject();
        try {
            loginDetails.put("Username", mUsernameView.getText().toString());
            loginDetails.put("Password", mPasswordView.getText().toString());
            topLevel.put("type", "userDetails");
            topLevel.put("data", loginDetails);
        } catch (JSONException e) {
            Log.d("Exec", e.getMessage());
        }
        mSocket.emit("login", topLevel);
        Log.i("socket", "sent login details");
    }
}
