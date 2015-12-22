package com.macernow.djstava.ljnavigation.navigation.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.macernow.djstava.ljnavigation.R;
import com.macernow.djstava.ljnavigation.adapter.AdapterTextview;
import com.macernow.djstava.ljnavigation.navigation.db.SqliteDataBaseHelper;
import com.macernow.djstava.ljnavigation.utils.DJLog;

import java.util.ArrayList;

public class StartPointInputActivity extends AppCompatActivity {
    private static final int RESULT_CODE_OK = 1;
    private ImageView imageView_back;
    private EditText editText;
    private ListView listView;
    private Button button;
    private View.OnClickListener onClickListener;
    private LatLonPoint latLng;

    private ArrayList<String> startPoiAddressList = new ArrayList<String>();
    private ArrayList<LatLonPoint> startPoiLatLonList = new ArrayList<LatLonPoint>();
    private String[] startPoiAddressArray;
    private LatLonPoint[] startPoiLatLngArray;
    private AdapterTextview adapterTextview_start_poi_result;

    private final static String DATABASE_NAME = "history.db";
    private final static String DATABASE_TABLE_NAME = "history_address";
    private SqliteDataBaseHelper sqliteDataBaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    private boolean isNeedInsert = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_point_input);

        initUIComponent();
        initUIComponentListener();
    }

    private void initUIComponent() {
        imageView_back = (ImageView) findViewById(R.id.start_point_back);
        editText = (EditText) findViewById(R.id.start_point_edit);
        listView = (ListView) findViewById(R.id.start_point_listview);
        button = (Button) findViewById(R.id.start_point_button);

        sqliteDataBaseHelper = new SqliteDataBaseHelper(StartPointInputActivity.this, DATABASE_NAME);
        sqLiteDatabase = sqliteDataBaseHelper.getWritableDatabase();
    }

    private void initUIComponentListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.start_point_back:
                        /*
                        * 将输入框的值进行回传
                        * */
                        if ((!editText.getText().toString().isEmpty()) && (latLng != null)) {
                            Intent intent = new Intent();
                            intent.putExtra("start_point_edittext_string", editText.getText().toString());
                            intent.putExtra("start_point_edittext_lat", latLng.getLatitude());
                            intent.putExtra("start_point_edittext_lng", latLng.getLongitude());

                            setResult(RESULT_CODE_OK, intent);
                        }

                        finish();
                        break;

                    case R.id.start_point_edit:
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String newText = s.toString().trim();

                                PoiSearch.Query query = new PoiSearch.Query(newText, "", "021");
                                query.setPageSize(10);
                                query.setPageNum(1);
                                PoiSearch poiSearch = new PoiSearch(StartPointInputActivity.this, query);
                                poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
                                    @Override
                                    public void onPoiSearched(PoiResult poiResult, int i) {
                                        if (i == 0) {
                                            for (int j = 0; j < poiResult.getPois().size(); j++) {
                                                if (!poiResult.getPois().get(j).getSnippet().isEmpty()) {
                                                    startPoiAddressList.add(poiResult.getPois().get(j).getSnippet());
                                                    startPoiLatLonList.add(poiResult.getPois().get(j).getLatLonPoint());
                                                }
                                            }

                                            startPoiAddressArray = new String[startPoiAddressList.size()];
                                            startPoiAddressArray = startPoiAddressList.toArray(startPoiAddressArray);

                                            startPoiLatLngArray = new LatLonPoint[startPoiLatLonList.size()];
                                            startPoiLatLngArray = startPoiLatLonList.toArray(startPoiLatLngArray);

                                            adapterTextview_start_poi_result = new AdapterTextview(StartPointInputActivity.this, startPoiAddressArray);
                                            listView.setAdapter(adapterTextview_start_poi_result);
                                            adapterTextview_start_poi_result.notifyDataSetChanged();

                                            startPoiAddressList.clear();
                                            startPoiLatLonList.clear();

                                        }
                                    }

                                    @Override
                                    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

                                    }
                                });

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        editText.setText(startPoiAddressArray[position]);
                                        latLng = startPoiLatLngArray[position];

                                        /*
                                        * 保存到本地数据库
                                        * 1 打开或者创建
                                        * 2 遍历查找是否已经有纪录
                                        * 3 如果没有，则进行插入操作
                                        * */

                                        Cursor cursor = sqLiteDatabase.query(DATABASE_TABLE_NAME, new String[]{"address"}, null, null, null, null, null);

                                        while (cursor.moveToNext()) {
                                            String tmp = cursor.getString(cursor.getColumnIndex("address"));
                                            if (tmp.equals(startPoiAddressArray[position])) {
                                                break;
                                            }
                                        }

                                        if (cursor.isAfterLast()) {
                                            isNeedInsert = true;
                                        }

                                        if (isNeedInsert == true) {
                                            //insert element
                                            DJLog.d("insert address:" + startPoiAddressArray[position]
                                                    + "lat:" + startPoiLatLngArray[position].getLatitude()
                                                    + "lng:" + startPoiLatLngArray[position].getLongitude());
                                            ContentValues contentValues = new ContentValues();
                                            contentValues.put("address", startPoiAddressArray[position]);
                                            contentValues.put("lat", startPoiLatLngArray[position].getLatitude());
                                            contentValues.put("lng", startPoiLatLngArray[position].getLongitude());
                                            sqLiteDatabase.insert(DATABASE_TABLE_NAME, null, contentValues);
                                            cursor.close();
                                        }

                                    }
                                });

                                poiSearch.searchPOIAsyn();
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                                if (editText.getText().toString() != null && !editText.getText().toString().equals("")) {
                                    button.setVisibility(View.VISIBLE);
                                } else {
                                    button.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                        break;

                    case R.id.start_point_button:
                        if (!editText.getText().toString().isEmpty() && !editText.getText().toString().equals("")) {
                            editText.setText("");
                            button.setVisibility(View.INVISIBLE);
                        }
                        break;

                    default:
                        break;
                }
            }
        };

        imageView_back.setOnClickListener(onClickListener);
        editText.setOnClickListener(onClickListener);
        button.setOnClickListener(onClickListener);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!editText.getText().toString().isEmpty() && (latLng != null)) {
                DJLog.d("====start_point back onKeyDown....");
                Intent intent = new Intent();
                intent.putExtra("start_point_edittext_string", editText.getText().toString());
                intent.putExtra("start_point_edittext_lat", latLng.getLatitude());
                intent.putExtra("start_point_edittext_lng", latLng.getLongitude());

                setResult(RESULT_CODE_OK, intent);
            }

            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_point_input, menu);
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
