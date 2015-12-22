package com.macernow.djstava.ljnavigation.navigation.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.macernow.djstava.ljnavigation.adapter.AdapterTextview;
import com.macernow.djstava.ljnavigation.R;
import com.macernow.djstava.ljnavigation.utils.DJLog;

import java.util.ArrayList;
import java.util.List;


public class NaviConfigActivity extends ActionBarActivity {
    private EditText editText_home, editText_company;
    private ListView listView_home, listView_company;
    private AdapterTextview adapterTextview_home, adapterTextview_company;
    private Button button_home, button_company;
    private View.OnClickListener clickListener;

    private ArrayList<String> poiAddressList_home = new ArrayList<String>();
    private ArrayList<String> poiAddressList_company = new ArrayList<String>();
    private ArrayList<LatLonPoint> poiLatLonList_home = new ArrayList<LatLonPoint>();
    private ArrayList<LatLonPoint> poiLatLonList_company = new ArrayList<LatLonPoint>();
    private String[] poiAddressArray_home;
    private String[] poiAddressArray_company;
    private LatLonPoint[] poiLatLngArray_home;
    private LatLonPoint[] poiLatLngArray_company;

    private Double home_lat, home_lon;
    private Double company_lat, company_lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi_config);

        initUIComponent();
        initUIComponentListener();
    }

    private void initUIComponent() {
        editText_home = (EditText) findViewById(R.id.edit_text_home);
        editText_company = (EditText) findViewById(R.id.edit_text_company);

        listView_home = (ListView) findViewById(R.id.listview_home);
        listView_company = (ListView) findViewById(R.id.listview_company);

        button_home = (Button) findViewById(R.id.button_set_home);
        button_company = (Button) findViewById(R.id.button_set_company);

    }

    private void initUIComponentListener() {

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.edit_text_home:
                        editText_home.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String newText = s.toString().trim();
                                PoiSearch.Query query = new PoiSearch.Query(newText, "", "021");
                                query.setPageSize(10);
                                query.setPageNum(1);
                                PoiSearch poiSearch = new PoiSearch(NaviConfigActivity.this, query);
                                poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
                                    @Override
                                    public void onPoiSearched(PoiResult poiResult, int i) {
                                        if (i == 0) {
                                            for (int j = 0; j < poiResult.getPois().size(); j++) {
                                                if ((!poiResult.getPois().get(j).getSnippet().isEmpty()) &&
                                                        (!poiResult.getPois().get(j).getLatLonPoint().toString().isEmpty())) {

                                                    poiAddressList_home.add(poiResult.getPois().get(j).getSnippet());
                                                    poiLatLonList_home.add(poiResult.getPois().get(j).getLatLonPoint());
                                                }
                                            }

                                            poiAddressArray_home = new String[poiAddressList_home.size()];
                                            poiAddressArray_home = poiAddressList_home.toArray(poiAddressArray_home);

                                            poiLatLngArray_home = new LatLonPoint[poiLatLonList_home.size()];
                                            poiLatLngArray_home = poiLatLonList_home.toArray(poiLatLngArray_home);

                                            adapterTextview_home = new AdapterTextview(NaviConfigActivity.this, poiAddressArray_home);
                                            listView_home.setAdapter(adapterTextview_home);
                                            adapterTextview_home.notifyDataSetChanged();

                                            poiAddressList_home.clear();
                                            poiLatLonList_home.clear();

                                        }
                                    }

                                    @Override
                                    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

                                    }
                                });

                                poiSearch.searchPOIAsyn();
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        listView_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                editText_home.setText(poiAddressArray_home[position]);
                                home_lat = poiLatLngArray_home[position].getLatitude();
                                home_lon = poiLatLngArray_home[position].getLongitude();
                            }
                        });
                        break;

                    case R.id.edit_text_company:
                        editText_company.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                                String newText = s.toString().trim();
                                PoiSearch.Query query = new PoiSearch.Query(newText, "", "021");
                                query.setPageSize(10);
                                query.setPageNum(1);
                                PoiSearch poiSearch = new PoiSearch(NaviConfigActivity.this, query);
                                poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
                                    @Override
                                    public void onPoiSearched(PoiResult poiResult, int i) {
                                        if (i == 0) {
                                            for (int j = 0; j < poiResult.getPois().size(); j++) {
                                                if (!poiResult.getPois().get(j).getSnippet().isEmpty()) {
                                                    poiAddressList_company.add(poiResult.getPois().get(j).getSnippet());
                                                    poiLatLonList_company.add(poiResult.getPois().get(j).getLatLonPoint());
                                                }
                                            }

                                            poiAddressArray_company = new String[poiAddressList_company.size()];
                                            poiAddressArray_company = poiAddressList_company.toArray(poiAddressArray_company);

                                            poiLatLngArray_company = new LatLonPoint[poiLatLonList_company.size()];
                                            poiLatLngArray_company = poiLatLonList_company.toArray(poiLatLngArray_company);

                                            adapterTextview_company = new AdapterTextview(NaviConfigActivity.this, poiAddressArray_company);
                                            listView_company.setAdapter(adapterTextview_company);
                                            adapterTextview_company.notifyDataSetChanged();

                                            poiAddressList_company.clear();
                                            poiLatLonList_company.clear();

                                        }
                                    }

                                    @Override
                                    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

                                    }
                                });

                                poiSearch.searchPOIAsyn();

                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        listView_company.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                editText_company.setText(poiAddressArray_company[position]);
                                company_lat = poiLatLngArray_company[position].getLatitude();
                                company_lon = poiLatLngArray_company[position].getLongitude();
                            }
                        });
                        break;

                    case R.id.button_set_home:
                        final String destination_home = editText_home.getText().toString();
                        DJLog.d("home: " + destination_home);

                        if (destination_home.isEmpty()) {
                            Toast.makeText(NaviConfigActivity.this, "家庭地址不能为空，请重新输入", Toast.LENGTH_SHORT).show();
                        } else {

                            new AlertDialog.Builder(NaviConfigActivity.this).setMessage(destination_home)
                                    .setTitle("是否将家庭地址设置为")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //editText_home.setText("");
                                            SharedPreferences prefs = getSharedPreferences("navi_config", Activity.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.putString("home", destination_home);
                                            editor.putString("home_lat", String.valueOf(home_lat));
                                            editor.putString("home_lon", String.valueOf(home_lon));
                                            editor.commit();
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();


                        }
                        break;

                    case R.id.button_set_company:
                        final String destination_company = editText_company.getText().toString();
                        DJLog.d("company: " + destination_company);

                        if (destination_company.isEmpty()) {
                            Toast.makeText(NaviConfigActivity.this, "公司地址不能为空，请重新输入", Toast.LENGTH_SHORT).show();
                        } else {

                            new AlertDialog.Builder(NaviConfigActivity.this).setMessage(destination_company)
                                    .setTitle("是否将公司地址设置为")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //editText_company.setText("");
                                            SharedPreferences prefs = getSharedPreferences("navi_config", Activity.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.putString("company", destination_company);
                                            editor.putString("company_lat", String.valueOf(company_lat));
                                            editor.putString("company_lon", String.valueOf(company_lon));
                                            editor.commit();
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }
                        break;
                }
            }
        };

        editText_home.setOnClickListener(clickListener);
        editText_company.setOnClickListener(clickListener);

        button_home.setOnClickListener(clickListener);
        button_company.setOnClickListener(clickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navi_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
