package com.server.interaction;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannelGroup;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
import android.util.Log;
import android.view.View;
import android.app.PendingIntent;
import android.graphics.BitmapFactory;

import com.example.caterpillar.IntakeActivity;
import com.example.caterpillar.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import java.net.URISyntaxException;

//  keep the global state of my application
public class SocketManager extends Application {
    private Socket mSocket;
    private static final String URL = "http://35.246.29.217:65080/";
    //private static final String URL = "http://146.169.166.31:65080/";
    private static  String user;
    private static  String caregiver;
    public static final int NOTIFICATION_ID = 1;
    public SocketManager(){
        //initialize maybe with default values if any based on use case
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCaregiver() {
        return caregiver;
    }

    public void setCaregiver(String caregiver) {
        this.caregiver = caregiver;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mSocket = IO.socket(URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        mSocket.connect();
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
        //sendNotification();
        //mSocket.on("Notification", sendNotification);
    }

    public void sendNotification() {

        // BEGIN_INCLUDE(build_action)
        Intent intent = new Intent(this, IntakeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        // END_INCLUDE(build_action)

        // BEGIN_INCLUDE (build_notification)

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.drawable.caterpillar);

        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.caterpillar));

        builder.setContentTitle("Time to take your pills!");
        builder.setContentText("Don't forget to take your pills :)");
        builder.setSubText("Tap to view details");

        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.extend(new NotificationCompat.WearableExtender());
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        // END_INCLUDE (build_notification)

        // BEGIN_INCLUDE(send_notification)

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        // END_INCLUDE(send_notification)
    }

    public Socket getmSocket(){
        return mSocket;
    }
}
