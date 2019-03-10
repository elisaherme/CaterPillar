package com.example.caterpillar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.server.interaction.SocketManager;

public class MainActivity extends AppCompatActivity {

    private String name;
    private String caregiver;
    private TextView userName;
    private TextView careGiver;
    private Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName = findViewById(R.id.textView5);
        careGiver = findViewById(R.id.textView6);
        b1 = (Button)findViewById(R.id.register_button);

        final SocketManager app = (SocketManager) getApplication();

        name = app.getUser();
        caregiver = app.getCaregiver();
        userName.setText(name);
        careGiver.setText(caregiver);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.sendNotification();
            }
        });
    }

    public void onClickUser(View view) {
        Intent intent = new Intent(this, pillbox.class);
        startActivity(intent);
    }

    public void onClickCareGiver(View view) {
        Intent intent = new Intent(this, CareGiverActivity.class);
        startActivity(intent);
    }

//    public void onClickManage(View view) {
//        Intent intent = new Intent(this, AlarmActivity.class);
//        startActivity(intent);
//    }

    public void onClickSignOut(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
