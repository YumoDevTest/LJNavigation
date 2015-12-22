package com.macernow.djstava.ljnavigation.sys;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.os.StatFs;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.macernow.djstava.ljnavigation.R;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class StorageFragment extends Fragment {
    private Context context;
    private TextView textView_sd_total,textView_sd_valid;
    private TextView textView_usb_total,textView_usb_valid;

    public StorageFragment() {
        // Required empty public constructor
    }

    public StorageFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_storage, container, false);

        textView_sd_total = (TextView)view.findViewById(R.id.sd_total);
        textView_sd_valid = (TextView)view.findViewById(R.id.sd_valid);

        textView_usb_total = (TextView)view.findViewById(R.id.usb_total);
        textView_usb_valid = (TextView)view.findViewById(R.id.usb_valid);

        getSDCardStorage();

        return view;
    }

    /*
    * 获取SD卡的容量信息
    * */
    private void getSDCardStorage() {
        /*
        * 判断SD卡是否存在
        * */
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs statFs = new StatFs(path.getPath());

            long blockSize = statFs.getBlockSizeLong();
            long freeBlocks = statFs.getAvailableBlocksLong();
            long totalBlocks = statFs.getBlockCountLong();

            long freeSize = freeBlocks * blockSize / 1024 /1024;
            long totalSize = totalBlocks * blockSize /1024 /1024;

            textView_sd_total.setText(totalSize + "MB");
            textView_sd_valid.setText(freeSize + "MB");
        }
    }

    /*
    * 获取外接USB设备的容量信息
    * */
    private void getUSBStorage() {

    }
}
