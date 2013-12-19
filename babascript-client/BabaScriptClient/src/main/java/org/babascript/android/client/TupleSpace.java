package org.babascript.android.client;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.Random;

/**
 * Created by takumi on 2013/12/16.
 */
public class TupleSpace {

    public String name;
    public Linda linda;

    public TupleSpace(String name, Linda linda){
        this.name  = name;
        this.linda = linda;
    }

    public void write(JSONArray tuple, JSONObject options){
        Log.d("write", tuple.toString());
        JSONArray data = new JSONArray();
        data.put(name);
        data.put(tuple);
        data.put(options);
        this.linda.push("__linda_write", data);
    }

    public void watch(JSONArray tuple, TupleSpaceCallback callback){
        Log.d("watch", "tuple:"+tuple.toString());
        JSONArray data = new JSONArray();
        String cid = createCallbackId();
        data.put(name);
        data.put(tuple);
        data.put(cid);
        this.linda.push("__linda_watch", data);
        this.linda.io.on("__linda_watch_callback_"+cid, callback);
    }

    public void take(JSONArray tuple, TupleSpaceCallback callback){
        Log.d("take", "tuple:"+tuple.toString());
        JSONArray data = new JSONArray();
        String cid = createCallbackId();
        data.put(name);
        data.put(tuple);
        data.put(cid);
        this.linda.push("__linda_take", data);
        this.linda.io.once("__linda_take_callback_"+cid, callback);
    }

    public void read(JSONArray tuple, TupleSpaceCallback callback){
        Log.d("read", "tuple:"+tuple.toString());
        JSONArray data = new JSONArray();
        String cid = createCallbackId();
        data.put(name);
        data.put(tuple);
        data.put(cid);
        this.linda.push("__linda_read", data);
        this.linda.io.once("__linda_read_callback_"+cid, callback);
    }

    private String createCallbackId(){
        return String.format("%d_%d", (new Date()).getTime(), (new Random()).nextInt(1000000));
    }


}

