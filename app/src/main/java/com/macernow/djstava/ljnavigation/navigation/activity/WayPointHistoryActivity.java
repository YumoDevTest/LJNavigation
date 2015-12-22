package com.macernow.djstava.ljnavigation.navigation.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.macernow.djstava.ljnavigation.R;
import com.macernow.djstava.ljnavigation.adapter.AdapterTextview;
import com.macernow.djstava.ljnavigation.navigation.db.SqliteDataBaseHelper;

import java.util.ArrayList;

public class WayPointHistoryActivity extends AppCompatActivity {
    private static final int ACTIVITY_NAVI_WAY_CODE = 2;
    private static final int ACTIVITY_WAY_POINT_INPUT_CODE = 2;

    private ImageView imageView;
    private EditText editText;
    private ListView listView;
    private Button button_clear;
    private View.OnClickListener onClickListener;
    private AdapterTextview adapterTextview;
    private Double lat,lng;

    private final static String DATABASE_NAME = "history.db";
    private final static String DATABASE_TABLE_NAME = "history_address";
    private SqliteDataBaseHelper sqliteDataBaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    private ArrayList<String> addressList = new ArrayList<String>();
    private String[] addressArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_way_point_history);

        initUIComponent();
        initUIComponentListener();

        loadHistory();
    }

    private void initUIComponent() {
        imageView = (ImageView) findViewById(R.id.way_point_history_back);
        editText = (EditText) findViewById(R.id.way_point_history_edit);
        editText.setInputType(InputType.TYPE_NULL);
        listView = (ListView) findViewById(R.id.way_point_history_listview);
        button_clear = (Button) findViewById(R.id.button_clear);
    }

    private void initUIComponentListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.way_point_history_back:
                        Intent intent = new Intent();
                        intent.putExtra("way_point_history_string", editText.getText().toString());
                        intent.putExtra("way_point_history_lat", lat);
                        intent.putExtra("way_point_history_lng", lng);
                        setResult(ACTIVITY_NAVI_WAY_CODE, intent);

                        finish();
                        break;

                    case R.id.way_point_history_edit:
                        startActivityForResult(new Intent(WayPointHistoryActivity.this,WayPointInputActivity.class),ACTIVITY_WAY_POINT_INPUT_CODE);
                        break;

                    case R.id.button_clear:
                        new AlertDialog.Builder(WayPointHistoryActivity.this).setMessage(R.string.alertdialog_message_clear)
                                .setTitle(R.string.alertdialog_title)
                                .setCancelable(false)
                                .setPositiveButton(R.string.alertdialog_yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        addressList.clear();
                                        addressArray = addressList.toArray(addressArray);
                                        listView.setAdapter(null);
                                        adapterTextview.notifyDataSetChanged();

                                        sqLiteDatabase.delete(DATABASE_TABLE_NAME, null, null);

                                        button_clear.setVisibility(View.INVISIBLE);
                                        editText.setText("");
                                    }
                                })
                                .setNegativeButton(R.string.alertdialog_no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();

                        break;

                }
            }
        };

        imageView.setOnClickListener(onClickListener);
        editText.setOnClickListener(onClickListener);
        button_clear.setOnClickListener(onClickListener);
    }

    private void loadHistory() {
        addressList.clear();

        sqliteDataBaseHelper = new SqliteDataBaseHelper(WayPointHistoryActivity.this,DATABASE_NAME);
        sqLiteDatabase = sqliteDataBaseHelper.getWritableDatabase();

        try {
            Cursor cursor = sqLiteDatabase.query(DATABASE_TABLE_NAME,new String[]{"address"},null,null,null,null,null);
            while (cursor.moveToNext()) {
                String tmp = cursor.getString(cursor.getColumnIndex("address"));
                addressList.add(tmp);
            }

            addressArray = new String[addressList.size()];
            addressArray = addressList.toArray(addressArray);

            if (!addressList.isEmpty()) {
                button_clear.setVisibility(View.VISIBLE);
            }

            adapterTextview = new AdapterTextview(WayPointHistoryActivity.this,addressArray);
            listView.setAdapter(adapterTextview);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    editText.setText(addressArray[position]);
                    String sql = "select lat,lng from " + DATABASE_TABLE_NAME + " where address like '%" + addressArray[position] + "%'";
                    final Cursor mCursor = sqLiteDatabase.rawQuery(sql,null);
                    if (mCursor.moveToFirst()) {
                        lat = mCursor.getDouble(mCursor.getColumnIndex("lat"));
                        lng = mCursor.getDouble(mCursor.getColumnIndex("lng"));
                    }

                    mCursor.close();

                    Intent intent = new Intent();
                    intent.putExtra("way_point_history_string", editText.getText().toString());
                    intent.putExtra("way_point_history_lat", lat);
                    intent.putExtra("way_point_history_lng", lng);
                    setResult(ACTIVITY_NAVI_WAY_CODE, intent);

                    finish();
                }
            });

            cursor.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        loadHistory();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case ACTIVITY_WAY_POINT_INPUT_CODE:
                String way_point_string = data.getStringExtra("way_point_edittext_string");
                lat = data.getDoubleExtra("way_point_edittext_lat", 0);
                lng = data.getDoubleExtra("way_point_edittext_lng", 0);

                if (!way_point_string.isEmpty()) {
                    editText.setText(way_point_string);
                    button_clear.setVisibility(View.VISIBLE);
                }
                break;

            default:
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra("way_point_history_string", editText.getText().toString());
            intent.putExtra("way_point_history_lat", lat);
            intent.putExtra("way_point_history_lng", lng);
            setResult(ACTIVITY_NAVI_WAY_CODE, intent);

            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_address_history, menu);
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
