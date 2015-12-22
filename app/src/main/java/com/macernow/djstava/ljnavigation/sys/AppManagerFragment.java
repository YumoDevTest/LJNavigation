package com.macernow.djstava.ljnavigation.sys;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.macernow.djstava.ljnavigation.R;
import com.macernow.djstava.ljnavigation.app.AppInfo;
import com.macernow.djstava.ljnavigation.app.PackageInfoProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppManagerFragment extends Fragment {
    protected static final int LOAD_FINISH = 1;
    private static final int INTENT_REQUEST_CODE = 0;

    private Context context;
    private ListView listView;
    private List<AppInfo> appInfos;
    private List<AppInfo> userApps;
    private List<AppInfo> sysApps;
    private AppListViewAdapter appListViewAdapter;
    private PackageInfoProvider packageInfoProvider;
    private ProgressDialog progressDialog;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_FINISH:
                    progressDialog.dismiss();
                    initAdapters();
                    listView.setAdapter(appListViewAdapter);

                    break;
            }
        }
    };


    public AppManagerFragment() {
        // Required empty public constructor
    }

    public AppManagerFragment(Context context) {
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
        View view = inflater.inflate(R.layout.fragment_app_manager, null);
        progressDialog = ProgressDialog.show(context, "", getResources().getString(R.string.sys_app_manager_loading));
        listView = (ListView) view.findViewById(R.id.app_listView);
        packageInfoProvider = new PackageInfoProvider(context);
        appListViewAdapter = new AppListViewAdapter();
        loadApp();

        return view;
    }

    /*
     * 把应用程序分为两类去处理,即用户程序和系统程序
	 */
    private void initAdapters() {
        userApps = new ArrayList<AppInfo>();
        sysApps = new ArrayList<AppInfo>();
        for (AppInfo appInfo : appInfos) {
            if (appInfo.getIsUserApp()) {
                userApps.add(appInfo);
            } else {
                sysApps.add(appInfo);
            }
        }
    }

    private void loadApp() {
        new Thread() {
            public void run() {
                appInfos = packageInfoProvider.getAppInfo();
                Message msg = Message.obtain();
                msg.what = LOAD_FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private class AppListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userApps.size();// 多加了两个标签 一个是用户程序的标签一个是系统程序的标签
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (position < userApps.size()) {

                ViewHolder holder;
                if (convertView == null) {
                    view = View.inflate(context, R.layout.app_info_item, null);
                    holder = new ViewHolder();
                    holder.tv_name = (TextView) view.findViewById(R.id.tv_app_info_item_name);
                    holder.tv_version = (TextView) view.findViewById(R.id.tv_app_info_item_version);
                    holder.iv_icon = (ImageView) view.findViewById(R.id.iv_app_info_item);
                    holder.bt = (Button) view.findViewById(R.id.btn_app_info_item);
                    view.setTag(holder);
                } else {
                    view = convertView;
                    holder = (ViewHolder) view.getTag();
                }

                AppInfo info = userApps.get(position);
                if (info != null) {
                    holder.iv_icon.setImageDrawable(info.getDrawable());
                    holder.tv_name.setText(info.getAppName());
                    holder.tv_name.setTextColor(R.color.black);
                    holder.tv_version.setText(getResources().getString(R.string.sys_app_manager_app_version_text) + info.getAppVersion());
                    holder.tv_version.setTextColor(R.color.black);
                    holder.bt.setVisibility(View.VISIBLE);
                    holder.bt.setTag(info.getPackageName());
                    holder.bt.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            String packname = (String) v.getTag();
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_UNINSTALL_PACKAGE);
                            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                            intent.setData(Uri.parse("package:" + packname));
                            startActivityForResult(intent, INTENT_REQUEST_CODE);
                        }
                    });
                }
            }

            return view;
        }
    }

    static class ViewHolder {
        TextView tv_name;
        TextView tv_version;
        ImageView iv_icon;
        Button bt;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                loadApp();
            } else if (resultCode == getActivity().RESULT_FIRST_USER) {
                Toast.makeText(context, R.string.sys_app_manager_delete_fail, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
