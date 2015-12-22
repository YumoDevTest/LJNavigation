package com.macernow.djstava.ljnavigation.sys;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.macernow.djstava.ljnavigation.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class LanguageFragment extends Fragment {
    private Context context;
    private Spinner spinner_language;
    private Button button_language_set;
    private String language;

    private List<String> list = new ArrayList<String>();
    private ArrayAdapter adapter;

    public LanguageFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list.add(getResources().getString(R.string.language_chinese));
        list.add(getResources().getString(R.string.language_english));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_language, container, false);
        spinner_language = (Spinner) view.findViewById(R.id.sys_language_spinner);
        button_language_set = (Button) view.findViewById(R.id.button_language_set);

        adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, list);
        spinner_language.setAdapter(adapter);

        spinner_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        language = "zh";
                        break;

                    case 1:
                        language = "en";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button_language_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                * 设置App使用的语言
                * */
                new AlertDialog.Builder(context).setMessage(R.string.alertdialog_language_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.alertdialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!language.isEmpty()) {
                                    changeLanguage(language);
                                }
                            }
                        })
                        .setNegativeButton(R.string.alertdialog_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

            }
        });

        return view;
    }

    public void changeLanguage(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(configuration, getActivity().getBaseContext().getResources().getDisplayMetrics());
    }


}
