package com.macernow.djstava.ljnavigation.sys;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.macernow.djstava.ljnavigation.R;
import com.macernow.djstava.ljnavigation.upgrade.UpgradeInfo;
import com.macernow.djstava.ljnavigation.upgrade.UpgradeInfoParser;
import com.macernow.djstava.ljnavigation.utils.DJLog;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpgradeFragment extends Fragment {
    private final int UPGRADE_NONEED = 0;
    private final int UPGRADE_CLIENT = 1;
    private final int GET_UPGRADEINFO_ERROR = 2;
    private final int SDCARD_NOMOUNTED = 3;
    private final int DOWNLOAD_ERROR = 4;

    private Button button;
    private Context context;
    private String localVersion;
    private UpgradeInfo upgradeInfo;

    public UpgradeFragment(Context context) {
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
        View view = inflater.inflate(R.layout.fragment_upgrade, container, false);

        button = (Button) view.findViewById(R.id.button_check_upgrade);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    localVersion = getAppVersion();
                    CheckVersionTask checkVersionTask = new CheckVersionTask();
                    new Thread(checkVersionTask).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    private String getAppVersion() throws Exception {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        return packageInfo.versionName;
    }

    public class CheckVersionTask implements Runnable {
        InputStream inputStream;

        public void run() {
            try {
                String path = getResources().getString(R.string.upgrade_url_server);
                URL url = new URL(path);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("GET");

                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    inputStream = httpURLConnection.getInputStream();
                }

                upgradeInfo = UpgradeInfoParser.getUpgradeInfo(inputStream);
                if (upgradeInfo.getVersion().equals(localVersion)) {
                    DJLog.d("Same version.");
                    Message message = new Message();
                    message.what = UPGRADE_NONEED;
                    handler.sendMessage(message);
                } else {
                    DJLog.d("A new version.");
                    Message message = new Message();
                    message.what = UPGRADE_CLIENT;
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                Message message = new Message();
                message.what = GET_UPGRADEINFO_ERROR;
                handler.sendMessage(message);
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            switch (message.what) {
                case UPGRADE_NONEED:
                    Toast.makeText(context, R.string.sys_upgrade_no_need, Toast.LENGTH_SHORT).show();
                    break;

                case UPGRADE_CLIENT:
                    showUpgradeDialog();
                    break;

                case GET_UPGRADEINFO_ERROR:
                    Toast.makeText(context, R.string.sys_upgrade_fetch_fail, Toast.LENGTH_LONG).show();
                    break;

                case DOWNLOAD_ERROR:
                    Toast.makeText(context, R.string.sys_upgrade_download_fail, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    protected void showUpgradeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.sys_upgrade_dialog_title);
        builder.setMessage(upgradeInfo.getDescription());
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.alertdialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DJLog.d("start to download apk file.");
                downloadApk();
            }
        });
        builder.setNegativeButton(R.string.alertdialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    protected void downloadApk() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage(getResources().getString(R.string.sys_upgrade_downloading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread() {
            @Override
            public void run() {
                try {
                    File file = com.macernow.djstava.ljnavigation.upgrade.DownloadManager.getFileFromServer(upgradeInfo.getUrl(), progressDialog);
                    sleep(3000);
                    installApk(file);
                    progressDialog.dismiss();
                } catch (Exception e) {
                    Message message = new Message();
                    message.what = DOWNLOAD_ERROR;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    protected void installApk(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

}
