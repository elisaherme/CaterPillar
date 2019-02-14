package com.example.caterpillar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.server.interaction.SocketManager;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText mUsernameView;
    private EditText mPasswordView;
    private String mUsername;

    private Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SocketManager app = (SocketManager) getApplication();
        mSocket = app.getmSocket();
        mSocket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Transport transport = (Transport) args[0];
                transport.on(Transport.EVENT_ERROR, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Exception e = (Exception) args[0];
                        Log.e("socketConnection", "Transport error " + e);
                        e.printStackTrace();
                        e.getCause().printStackTrace();
                    }
                });
            }
        });
        // Set up the login form.
        mUsernameView = findViewById(R.id.input_user);
        mPasswordView= findViewById(R.id.input_password);

//        Button signInButton = (Button) findViewById(R.id.loginButton);
//        signInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                attemptLogin();
//            }
//        });



    }

    public void onClickCreateAccount(View view){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void onClickLogin(View view){
//        Map<String, String> loginDetails = new HashMap<>();
//        loginDetails.put("Login", mUsernameView.getText().toString());
//        loginDetails.put("Password", mPasswordView.getText().toString());
        mSocket.emit("new message", mUsernameView.getText().toString());
        Log.i("socket", "sent login details");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
