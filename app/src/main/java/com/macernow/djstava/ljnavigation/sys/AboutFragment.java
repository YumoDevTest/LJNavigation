package com.macernow.djstava.ljnavigation.sys;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.macernow.djstava.ljnavigation.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {
    private TextView android_version, kernel_version, app_version, wlan_mac, serial_num;
    private Context context;

    public AboutFragment(Context context) {
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
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        android_version = (TextView) view.findViewById(R.id.android_version);
        kernel_version = (TextView) view.findViewById(R.id.kernel_version);
        app_version = (TextView) view.findViewById(R.id.app_version);
        wlan_mac = (TextView) view.findViewById(R.id.wlan_mac);
        serial_num = (TextView) view.findViewById(R.id.serial_num);

        getAndroidVersion();
        getKernelVersion();
        getAppVersion();
        getWlanMac();
        getSerialNum();

        return view;
    }

    /*
    * 获取Android的版本号
    * */
    private void getAndroidVersion() {
        try {
            android_version.setText(Build.VERSION.RELEASE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * 获取本APP的版本号，在清单文件中有描述
    * */
    private void getAppVersion() {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            app_version.setText(packageInfo.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * 获取WLAN的MAC地址
    * */
    private void getWlanMac() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            String mac = wifiManager.getConnectionInfo().getMacAddress();
            wlan_mac.setText(mac);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * TODO
    * */
    private void getSerialNum() {

        try {
            serial_num.setText("1234567890");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * 通过解析/proc/version文件来得到linux kernel版本号
    * */
    private void getKernelVersion() {
        String kernelVersion = "";
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream("/proc/version");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
        String info = "";
        String line = "";
        try {
            while ((line = bufferedReader.readLine()) != null) {
                info += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if (info != "") {
                final String keyword = "version ";
                int index = info.indexOf(keyword);
                line = info.substring(index + keyword.length());
                index = line.indexOf(" ");
                kernelVersion = line.substring(0, index);
                kernel_version.setText(kernelVersion);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

}
