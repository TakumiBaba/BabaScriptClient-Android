package org.babascript.android.client;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by takumi on 2013/12/07.
 */
public class WebSocket extends WebSocketClient {

    private Linda linda;
    private String session;

    public WebSocket(String url){
        super(URI.create(url), new Draft_17());
    }

    public void push(String type, JSONArray data){
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("data", data);
            json.put("session", session);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.send(json.toString());
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        Log.d("websocket", "onopen!");
    }

    @Override
    public void onMessage(String s) {
        JSONObject json = null;
        String type = "";
        try{
            json = new JSONObject(s);
            type = json.getString("type");
        }catch (JSONException e){
            e.printStackTrace();
        }
        if(type.equals("__session_id")){
            try{
                session = json.getString("data");
            }catch (JSONException e){
                e.printStackTrace();
            }
            linda.callback.connect(json);
        }else if(type.matches("__linda_write_callback.*")){
            linda.callback.write(json);
        }else if(type.matches("__linda_watch_callback.*")){
            linda.callback.watch(json);
        }else if(type.matches("__linda_read_callback.*")){
            linda.callback.read(json);
        }else if(type.matches("__linda_take_callback.*")){
            linda.callback.take(json);
        }
        Log.d("websocket", "onmessge: "+s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        Log.d("websocket", "onclose"+s);
//        reconnect();
    }

    @Override
    public void onError(Exception e) {
        Log.d("websocket", "onerror");
        e.printStackTrace();
//        reconnect();
    }

    private void reconnect(){

        TimerTask task = new TimerTask(){
            @Override
            public void run(){
                connect();
            }
        };
        Timer timer = new Timer("reconnect");
        timer.schedule(task, 10000);

    }
}
