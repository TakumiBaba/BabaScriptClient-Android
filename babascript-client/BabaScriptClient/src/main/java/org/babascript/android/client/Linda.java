package org.babascript.android.client;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by takumi on 2013/12/07.
 */
public class Linda {
    public Callback callback;
    public WebSocket io;

    public Linda(String base){
        io = new WebSocket(base);
        callback = new Callback() {
            @Override
            public void connect(JSONObject json) {

            }

            @Override
            public void write(JSONObject json) {

            }

            @Override
            public void read(JSONObject json) {

            }

            @Override
            public void take(JSONObject json) {

            }

            @Override
            public void watch(JSONObject json) {

            }
        };
        io.connect();
    }

    public void push(String type, JSONArray tuple){
        io.push(type, tuple);
    }
}

interface Callback{
    void connect(JSONObject json);
    void write(JSONObject json);
    void read(JSONObject json);
    void take(JSONObject json);
    void watch(JSONObject json);
}
