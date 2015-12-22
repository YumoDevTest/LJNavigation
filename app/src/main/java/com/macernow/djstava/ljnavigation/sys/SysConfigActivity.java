package com.macernow.djstava.ljnavigation.sys;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.macernow.djstava.ljnavigation.R;
import com.macernow.djstava.ljnavigation.utils.CornerListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SysConfigActivity extends AppCompatActivity {
    private static final int SYS_WLAN = 0;
    private static final int SYS_BLUETOOTH = 1;
    private static final int SYS_STORAGE = 2;
    private static final int SYS_APP = 3;
    private static final int SYS_DATE_TIME = 4;
    private static final int SYS_UPGRADE = 5;
    private static final int SYS_LANGUAGE = 6;
    private static final int SYS_ABOUT = 7;

    private CornerListView listView;
    private ArrayList<Map<String, String>> listData;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_config);

        initUIComponent();

        initUIComponentListener();
    }

    private void initUIComponent() {
        listView = (CornerListView) findViewById(R.id.sys_listview);
        listView.setAdapter(getSimpleAdapter());

        fragmentManager = getFragmentManager();
        //setListViewHeightBasedOnChildren(listView);
    }

    private void initUIComponentListener() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case SYS_WLAN:
                        WLANFragment wlanFragment = new WLANFragment(SysConfigActivity.this);

                        FragmentTransaction fragmentTransaction_wlan = fragmentManager.beginTransaction();
                        fragmentTransaction_wlan.replace(R.id.sys_fragment, wlanFragment, "wlanFragment");
                        fragmentTransaction_wlan.addToBackStack("wlanFragment");
                        fragmentTransaction_wlan.commit();
                        break;

                    case SYS_BLUETOOTH:
                        BluetoothFragment bluetoothFragment = new BluetoothFragment(SysConfigActivity.this);

                        FragmentTransaction fragmentTransaction_bluetooth = fragmentManager.beginTransaction();
                        fragmentTransaction_bluetooth.replace(R.id.sys_fragment, bluetoothFragment, "bluetoothFragment");
                        fragmentTransaction_bluetooth.addToBackStack("bluetoothFragment");
                        fragmentTransaction_bluetooth.commit();
                        break;

                    case SYS_STORAGE:
                        StorageFragment storageFragment = new StorageFragment(SysConfigActivity.this);

                        FragmentTransaction fragmentTransaction_storage = fragmentManager.beginTransaction();
                        fragmentTransaction_storage.replace(R.id.sys_fragment, storageFragment, "storageFragment");
                        fragmentTransaction_storage.addToBackStack("storageFragment");
                        fragmentTransaction_storage.commit();
                        break;

                    case SYS_APP:
                        AppManagerFragment appManagerFragment = new AppManagerFragment(SysConfigActivity.this);

                        FragmentTransaction fragmentTransaction_app = fragmentManager.beginTransaction();
                        fragmentTransaction_app.replace(R.id.sys_fragment, appManagerFragment, "appManagerFragment");
                        fragmentTransaction_app.addToBackStack("appManagerFragment");
                        fragmentTransaction_app.commit();
                        break;

                    case SYS_DATE_TIME:
                        DateTimeFragment dateTimeFragment = new DateTimeFragment(SysConfigActivity.this);

                        FragmentTransaction fragmentTransaction_date_time = fragmentManager.beginTransaction();
                        fragmentTransaction_date_time.replace(R.id.sys_fragment, dateTimeFragment, "dateTimeFragment");
                        fragmentTransaction_date_time.addToBackStack("dateTimeFragment");
                        fragmentTransaction_date_time.commit();
                        break;

                    case SYS_UPGRADE:
                        UpgradeFragment upgradeFragment = new UpgradeFragment(SysConfigActivity.this);

                        FragmentTransaction fragmentTransaction_upgrade = fragmentManager.beginTransaction();
                        fragmentTransaction_upgrade.replace(R.id.sys_fragment, upgradeFragment, "upgradeFragment");
                        fragmentTransaction_upgrade.addToBackStack("upgradeFragment");
                        fragmentTransaction_upgrade.commit();
                        break;

                    case SYS_LANGUAGE:
                        LanguageFragment languageFragment = new LanguageFragment(SysConfigActivity.this);

                        FragmentTransaction fragmentTransaction_language = fragmentManager.beginTransaction();
                        fragmentTransaction_language.replace(R.id.sys_fragment, languageFragment, "languageFragment");
                        fragmentTransaction_language.addToBackStack("languageFragment");
                        fragmentTransaction_language.commit();
                        break;

                    case SYS_ABOUT:
                        AboutFragment aboutFragment = new AboutFragment(SysConfigActivity.this);

                        FragmentTransaction fragmentTransaction_about = fragmentManager.beginTransaction();
                        fragmentTransaction_about.replace(R.id.sys_fragment, aboutFragment, "aboutFragment");
                        fragmentTransaction_about.addToBackStack("aboutFragment");
                        fragmentTransaction_about.commit();
                        break;

                    default:
                        break;
                }

            }
        });

    }

    /*
    * lisview的适配器
    * */
    private SimpleAdapter getSimpleAdapter() {
        listData = new ArrayList<Map<String, String>>();

        Map<String, String> map = new HashMap<String, String>();
        map.put("text", getResources().getString(R.string.sys_item_wlan));
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("text", getResources().getString(R.string.sys_item_bluetooth));
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("text", getResources().getString(R.string.sys_item_storage));
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("text", getResources().getString(R.string.sys_item_app));
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("text", getResources().getString(R.string.sys_item_date_time));
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("text", getResources().getString(R.string.sys_item_upgrade));
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("text", getResources().getString(R.string.sys_item_language));
        listData.add(map);

        map = new HashMap<String, String>();
        map.put("text", getResources().getString(R.string.sys_item_about));
        listData.add(map);

        return new SimpleAdapter(SysConfigActivity.this, listData,
                R.layout.list_item, new String[]{"text"},
                new int[]{R.id.tv_list_item});

    }

    /***
     * 动态设置listview的高度
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // params.height += 5;// if without this statement,the listview will be
        // a
        // little short
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sys_config, menu);
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
