package com.macernow.djstava.ljnavigation.sys;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.macernow.djstava.ljnavigation.R;
import com.macernow.djstava.ljnavigation.utils.DateTimePickDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class DateTimeFragment extends Fragment {
    private Context context;
    private Button button;
    private String initDateTime;

    public DateTimeFragment(Context context) {
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
        View view = inflater.inflate(R.layout.fragment_date_time, container, false);

        button = (Button) view.findViewById(R.id.button_date_time);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                initDateTime = simpleDateFormat.format(calendar.getTime());

                DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(
                        getActivity(), initDateTime);
                dateTimePickDialog.dateTimePicKDialog();
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
