package org.babascript.android;

import org.json.JSONObject;

/**
 * Created by takumi on 2013/12/07.
 */
public class Linda {
    public Callback callback;
}

interface Callback{
    void connect(JSONObject json);
    void write(JSONObject json);
    void read(JSONObject json);
    void take(JSONObject json);
    void watch(JSONObject json);
}
