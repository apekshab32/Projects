package com.hari.autotasx;

import android.content.SharedPreferences;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Hari on 24-Feb-15.
 */
public class LocFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    TextView displname;
    SharedPreferences prefs;
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static LocFragment newInstance(int sectionNumber) {
        LocFragment fragment = new LocFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public LocFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        prefs = getActivity().getSharedPreferences(
                "com.hari.autotasx", getActivity().MODE_PRIVATE);


        FloatingActionButton fab1 = (FloatingActionButton) rootView.findViewById(R.id.fab_1);
        //fab1.setOnCheckedChangeListener(this);
        displname = (TextView) rootView.findViewById(R.id.displname);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
            }
        });
        displname.setText(prefs.getString("name","No data"));
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        displname.setText("Name: "+prefs.getString("name","No data")+"\n"+"Latitude: "+String.valueOf(prefs.getFloat("lat",0))+"\n"+"Longitude: "+String.valueOf(prefs.getFloat("lng",0)));


    }
}
