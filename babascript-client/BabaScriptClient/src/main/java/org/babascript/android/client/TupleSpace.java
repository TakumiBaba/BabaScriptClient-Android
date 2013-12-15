package org.babascript.android.client;

import org.json.JSONArray;
import org.json.JSONObject;

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
        JSONArray data = new JSONArray();
        String cid = createCallbackId();
        data.put(tuple);
        data.put(options);
        data.put(cid);
        this.linda.push("__linda_write", data);
    }

    public void watch(JSONArray tuple){

    }

    public void take(){

    }

    public void read(){

    }

    private String createCallbackId(){
        return "";
    }

}
