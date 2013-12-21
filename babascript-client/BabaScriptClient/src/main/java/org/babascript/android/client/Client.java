package org.babascript.android.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.babascript.android.client.Callback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by takumi on 2013/12/07.
 */
public class Client{

    Linda linda;
    TupleSpace group;
    TupleSpace uni;
    String id;
    ArrayList<JSONArray> tasks;
    Context mContext;
    Routing routing;

    public Client(Context c, Routing r){
        this.mContext = c;
        this.routing = r;
        id = getUniqueId();
        linda = new Linda("ws://linda.masuilab.org:10010");
        group = new TupleSpace("baba", linda);
        uni   = new TupleSpace(id, linda);
        tasks = new ArrayList<JSONArray>();
        linda.io.once("connect", new TupleSpaceCallback() {
            @Override
            public Object callback(JSONObject options) {
                waitNext();
                waitAliveCheck();
                waitUnicast();
                waitBroadcast();
                waitCancel();
                return null;
            }
        });
    }

    public void waitNext(){
        if(tasks.size() > 0){
            JSONArray task = null;
            String format = null;
            try {
                task = tasks.get(0);
                format = task.getJSONObject(3).getString("format");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            JSONArray tuple = new JSONArray();
            tuple.put("babascript");
            tuple.put("eval");
            group.take(tuple, new TupleSpaceCallback() {
                @Override
                public Object callback(JSONObject options) {
                    JSONArray tuple = null;
                    try {
                        tuple = options.getJSONObject("data").getJSONArray("tuple");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    tasks.add(tuple);
                    if(tasks.size() <= 1){
                        JSONArray task = null;
                        String format = null;
                        try {
                            task = tasks.get(0);
                            format = task.getJSONObject(3).getString("format");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    routing.callback(tuple);
                    return null;
                }
            });
        }
    }

    public void waitUnicast(){
        JSONArray tuple = new JSONArray();
        tuple.put("babascript"); tuple.put("eval");
        uni.watch(tuple, new TupleSpaceCallback() {
            @Override
            public Object callback(JSONObject options) {
                JSONArray tuple = null;
                try {
                    tuple = options.getJSONObject("data").getJSONArray("tuple");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                tasks.add(tuple);
                if(tasks.size() <= 1){
                    JSONArray task = null;
                    String format = null;
                    task = tasks.get(0);
                    try {
                        format = task.getJSONObject(3).getString("format");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                routing.callback(tuple);
                return null;
            }
        });
    }

    public  void waitBroadcast(){
        JSONArray tuple = new JSONArray();
        tuple.put("babascript"); tuple.put("broadcast");
        group.watch(tuple, new TupleSpaceCallback() {
            @Override
            public Object callback(JSONObject options) {
                JSONArray tuple = null;
                try {
                    tuple = options.getJSONObject("data").getJSONArray("tuple");
                    if(tuple.getString(2).equals("ping")){
                        JSONArray t = new JSONArray();
                        t.put("babascript"); t.put("pong"); t.put(getUniqueId());
                        group.write(t, new JSONObject());
                        return null;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                tasks.add(tuple);
                if(tasks.size() <= 1){
                    JSONArray task = tasks.get(0);
                    String format = null;
                    try {
                        format = task.getJSONObject(3).getString("format");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                routing.callback(tuple);
                return null;
            }
        });
    }

    public void waitCancel(){
        JSONArray tuple = new JSONArray();
        tuple.put("babascript"); tuple.put("cancel");
        group.watch(tuple, new TupleSpaceCallback() {
            @Override
            public Object callback(JSONObject options) {
                JSONArray tuple = null;
                String cid = null;
                try {
                    tuple = options.getJSONObject("data").getJSONArray("tuple");
                    cid = tuple.getString(2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray cancelTask = null;
                try {
                    for(int i=0;i<tasks.size();i++){
                        if(tasks.get(i).getJSONObject(3).getString("cid").equals(cid)){
                            tasks.remove(i);
                            if(i == 0)routing.callback(null);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    public void waitAliveCheck(){
        JSONArray tuple = new JSONArray();
        tuple.put("babascript"); tuple.put("alivecheck");
        group.watch(tuple, new TupleSpaceCallback() {
            @Override
            public Object callback(JSONObject options) {
                Log.d("alivecheck", options.toString());
                JSONArray tuple = new JSONArray();
                tuple.put("babascript");
                tuple.put("alive");
                tuple.put(getUniqueId());
                group.write(tuple, new JSONObject());
                return null;
            }
        });
    }

    public void returnValue(Object value, JSONObject option){
        JSONArray task = null;
        String cid = null;
        try {
            task = tasks.get(0);
            Log.d("retrunvalue", "task is " + task.toString());
            cid = task.getJSONObject(3).getString("cid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray tuple = new JSONArray();
        try {
            option.put("worker", getUniqueId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tuple.put("babascript");
        tuple.put("return");
        tuple.put(cid);
        tuple.put(value);
        tuple.put(option);
        group.write(tuple, new JSONObject());
        tasks.remove(0);
        Log.d("task", "tasks is" + tasks.toString());
        waitNext();
    }

    public String getUniqueId(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        String id = settings.getString("uuid", "");
        if(id.equals("")){
            id = UUID.randomUUID().toString();
            settings.edit().putString("uuid", id).commit();
        }
        return id;
    }
}
