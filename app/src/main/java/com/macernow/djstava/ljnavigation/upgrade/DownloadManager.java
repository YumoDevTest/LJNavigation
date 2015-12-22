package com.macernow.djstava.ljnavigation.upgrade;

import android.app.ProgressDialog;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by djstava on 15/4/30.
 */
public class DownloadManager {
    public static File getFileFromServer(String path,ProgressDialog progressDialog) throws Exception {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(5000);

            progressDialog.setMax(httpURLConnection.getContentLength());
            InputStream inputStream = httpURLConnection.getInputStream();

            File file = new File(Environment.getExternalStorageDirectory(),"upgrade.apk");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bufferedInputStream.read(buffer)) != -1 ) {
                fileOutputStream.write(buffer,0,len);
                total += len;
                progressDialog.setProgress(total);
            }

            fileOutputStream.close();
            bufferedInputStream.close();
            inputStream.close();
            return file;
        } else {
            return null;
        }
    }
}
