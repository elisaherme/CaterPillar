package com.server.interaction;
import android.app.Application;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import java.net.URISyntaxException;

//  keep the global state of my application
public class SocketManager extends Application {
    private Socket mSocket;
    private static final String URL = "http://192.168.0.24:65080/";
    public SocketManager(){
        //initialize maybe with default values if any based on use case
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
    }
    public Socket getmSocket(){
        return mSocket;
    }
}
