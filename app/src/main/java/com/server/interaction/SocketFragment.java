//package com.server.interaction;
//import com.example.caterpillar.R;
//import com.github.nkzawa.emitter.Emitter;
//import com.github.nkzawa.engineio.client.Transport;
//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Manager;
//import com.github.nkzawa.socketio.client.Socket;
//
//import android.os.Message;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.DividerItemDecoration;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SocketFragment extends Fragment {
//    private List<String> messages = new ArrayList<String>();
//    private Socket mSocket;
//    private TextView textView;
//    private EditText editText;
//    RecyclerView recyclerView;
//    private static final String TAG = "MainActivity";
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // Socket io
//        SocketManager app = (SocketManager)  getActivity().getApplication();
//        mSocket = app.getmSocket();
//        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
//        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
//        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
//        mSocket.on("new message", onNewMessage);
//
//        mSocket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                Transport transport = (Transport) args[0];
//                transport.on(Transport.EVENT_ERROR, new Emitter.Listener() {
//                    @Override
//                    public void call(Object... args) {
//                        Exception e = (Exception) args[0];
//                        Log.e(TAG, "Transport error " + e);
//                        e.printStackTrace();
//                        e.getCause().printStackTrace();
//                    }
//                });
//            }
//        });
//        if (mSocket.connected()) {
//            Toast.makeText(getActivity(), "Connected!!", Toast.LENGTH_SHORT).show();
//            textView.setText("Connected");
//            Log.i("socket","Socket connected!");
//        }
//
//
//        // Setup socket calls
//
////
////
////        // Recycler view test
////        // data to populate the RecyclerView with
////        messages.add("Watch for messages here");
////
////        // set up the RecyclerView
////        recyclerView = findViewById(R.id.recycleView);
////        recyclerView.setLayoutManager(new LinearLayoutManager(this));
////        adapter  = new MessageAdapter(this, messages);
////        adapter.setClickListener(this);
////        recyclerView.setAdapter(adapter);
//////        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
//////                layoutManager.getOrientation());
//////        recyclerView.addItemDecoration(dividerItemDecoration);
//
//        // button to send
////        Button clickButton =  findViewById(R.id.button);
////        clickButton.setOnClickListener( new View.OnClickListener() {
////
////            @Override
////            public void onClick(View v) {
////
////                String res = editText.getText().toString();
////                if (res.length() != 0){
////                    mSocket.emit("new message", res);
////                    editText.setText("");
////
////                    Toast.makeText(MainActivity.this, "Sent Msg", Toast.LENGTH_SHORT).show();
////                }
////
////
////            }
////        });
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
////        textView = (EditText) textView.findViewById(R.id.textview);
////        editText = view.findViewById(R.id.editText2);
////        mInputMessageView = (EditText) view.findViewById(R.id.message_input);
////        mInputMessageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
////            @Override
////            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
////                if (id == R.id.send || id == EditorInfo.IME_NULL) {
////                    attemptSend();
////                    return true;
////                }
////                return false;
////            }
////        });
//    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        mSocket.disconnect();
//
//
//        mSocket.off("new message", onNewMessage);
//        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
//        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
//        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
//
//
//    }
//
//    private Emitter.Listener onDisconnect = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.i(TAG, "diconnected");
//                    if (!mSocket.connected()) {
////                        Toast.makeText(MainActivity.this, "Not Connected!!", Toast.LENGTH_SHORT).show();
//////                        textView.setText("Not connected");
//                    }
//                }
//            });
//        }
//    };
//
//    private Emitter.Listener onConnectError = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e(TAG, "Error connecting");
////                    Toast.makeText(getApplicationContext(),
////                            "error connecting", Toast.LENGTH_LONG).show();
////                    textView.setText("Error connecting. Trying again");
//                }
//            });
//        }
//    };
//
//    private Emitter.Listener onNewMessage = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    JSONObject data = (JSONObject) args[0];
//                    String message;
//                    try {
//                        message = data.getString("message");
//                    } catch (Exception e) {
//                        Log.e(TAG, e.getMessage());
//                        return;
//                    }
//                }
//            });
//        }
//    };
//
//}
