package org.babascript.android;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.babascript.android.client.Client;
import org.babascript.android.client.Routing;
import org.babascript.android.client.TupleSpace;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity implements OnFragmentInteractionListener{

    Client client = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        client = new Client(getApplicationContext(), new Routing(){
            @Override
            public void callback(JSONArray tuple){
                String format = null;
                Fragment fragment = null;
                try {
                    format = tuple.getJSONObject(3).getString("format").toLowerCase();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(format.equals("bool") || format.equals("boolean")){
                    fragment = (BooleanViewFragment) BooleanViewFragment.newInstance(tuple);
                }else if(format.equals("string")){
                    fragment = (StringViewFragment) StringViewFragment.newInstance(tuple);
                }else if(format.equals("int") || format.equals("number")){
                    fragment = (IntViewFragment) IntViewFragment.newInstance(tuple);
                }else if(format.equals("list")){
                    fragment = (ListViewFragment) ListViewFragment.newInstance(tuple);
                }else{
                    fragment = (BooleanViewFragment) BooleanViewFragment.newInstance(tuple);
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            };
        });
    }

    public void onReturnValueClicked(Object v){
        Log.d("return valie", v.toString());
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Object value) {
        client.returnValue(value, new JSONObject());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new PlaceholderFragment())
                .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
