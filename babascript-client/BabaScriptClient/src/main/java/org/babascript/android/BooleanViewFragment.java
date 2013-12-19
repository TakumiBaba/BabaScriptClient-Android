package org.babascript.android;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link BooleanViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class BooleanViewFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "tuple";

    // TODO: Rename and change types of parameters
//    private JSONArray mParam1;
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tuple Parameter 1.
     * @return A new instance of fragment BooleanViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BooleanViewFragment newInstance(JSONArray tuple) {
        BooleanViewFragment fragment = new BooleanViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, tuple.toString());
        fragment.setArguments(args);
        return fragment;
    }
    public BooleanViewFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        JSONArray tuple = null;
        String methodname = "";
        try {
            tuple = new JSONArray(mParam1);
            methodname = tuple.getString(2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        View v = inflater.inflate(R.layout.fragment_boolean_view, container, false);
        Button trueButton = (Button) v.findViewById(R.id.boolean_true);
        trueButton.setOnClickListener(this);
        Button falseButton = (Button) v.findViewById(R.id.boolean_false);
        falseButton.setOnClickListener(this);
        TextView methodNameView = (TextView) v.findViewById(R.id.method_name);
        methodNameView.setText(methodname);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        Log.d("boolean-view", ""+v.getId());
        if(v.getId() == R.id.boolean_true){
            mListener.onFragmentInteraction(true);
        }else if(v.getId() == R.id.boolean_false){
            mListener.onFragmentInteraction(false);
        }

    }


}
