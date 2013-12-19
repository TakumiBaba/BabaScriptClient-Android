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
    private JSONObject callbackOnList;
    private JSONObject callbackOnceList;

    public WebSocket(String url){
        super(URI.create(url), new Draft_17());
        callbackOnList = new JSONObject();
        callbackOnceList = new JSONObject();
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
        Log.d("push", json.toString());
        this.send(json.toString());
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        Log.d("websocket", "onopen!");
    }

    @Override
    public void onMessage(String s) {
        Log.d("websocket", "onmessage: "+s);
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
                session = json.getString("data").toString();
            }catch (JSONException e){
                e.printStackTrace();
            }
            this.emit("connect", new JSONObject());
        }else{
            this.emit(type, json);
        }

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

    public void on(String type, TupleSpaceCallback callback){
        Log.d("websocket", "on:"+type);
        try {
            callbackOnList.put(type, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void once(String type, TupleSpaceCallback callback){
        Log.d("websocket", "once:"+type);
        try {
            callbackOnceList.put(type, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void emit(String type, JSONObject options){
        Log.d("websocket", "emit:" + options.toString());
        TupleSpaceCallback tsc = null;
        try {
            tsc = (TupleSpaceCallback) callbackOnceList.get(type);
            callbackOnceList.remove(type);
        } catch (JSONException e) {
            try{
                tsc = (TupleSpaceCallback) callbackOnList.get(type);
            }catch (JSONException ee){
                return;
            }
        }
        tsc.callback(options);
    }
}
