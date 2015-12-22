package com.macernow.djstava.ljnavigation.sys;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Switch;

import com.macernow.djstava.ljnavigation.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WLANFragment extends Fragment {
    private static final String TAG = WLANFragment.class.getSimpleName();

    private Context context;
    private Switch aSwitch;
    private ListView listView;

    public WLANFragment(Context context) {
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
        View view = inflater.inflate(R.layout.fragment_wlan, container, false);
        aSwitch = (Switch)view.findViewById(R.id.switch_wlan);
        listView = (ListView)view.findViewById(R.id.listview_wlan);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
