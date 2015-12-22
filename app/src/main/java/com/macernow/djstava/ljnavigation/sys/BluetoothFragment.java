package com.macernow.djstava.ljnavigation.sys;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.macernow.djstava.ljnavigation.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BluetoothFragment extends Fragment {
    private Context context;
    private Switch aSwitch;

    public BluetoothFragment(Context context) {
        // Required empty public constructor

        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        aSwitch = (Switch)view.findViewById(R.id.switch_bluetooth);

        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
    }
}
