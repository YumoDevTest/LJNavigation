package com.macernow.djstava.ljnavigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.macernow.djstava.ljnavigation.navigation.activity.NaviStartActivity;
import com.macernow.djstava.ljnavigation.sys.SysConfigActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        initUIComponent();
    }

    private void initUIComponent() {
        listView = (ListView) findViewById(R.id.listview_main);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, getData(), R.layout.main_icon_list, new String[]{"img", "title"}, new int[]{R.id.img, R.id.title});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, NaviStartActivity.class));
                        break;

                    case 1:
                        startActivity(new Intent(MainActivity.this, MovieViewActivity.class));
                        break;

                    case 2:
                        startActivity(new Intent(MainActivity.this, MultimediaWizardActivity.class));
                        break;

                    case 3:
                        startActivity(new Intent(MainActivity.this, LoadAppActivity.class));
                        break;

                    case 4:
                        startActivity(new Intent(MainActivity.this, SysConfigActivity.class));
                        break;
                }
            }
        });
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("img", R.drawable.main_new_style_navigation);
        map.put("title", getResources().getString(R.string.main_icon_text_navi));
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.main_new_style_phone);
        map.put("title", getResources().getString(R.string.main_icon_text_phone));
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.main_new_style_media);
        map.put("title", getResources().getString(R.string.main_icon_text_media));
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.main_new_style_app);
        map.put("title", getResources().getString(R.string.main_icon_text_app));
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.main_new_style_setting);
        map.put("title", getResources().getString(R.string.main_icon_text_settings));
        list.add(map);

        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
