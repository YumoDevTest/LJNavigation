package com.macernow.djstava.ljnavigation;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.macernow.djstava.ljnavigation.adapter.AdapterTextview;
import com.macernow.djstava.ljnavigation.utils.DJLog;
import com.macernow.djstava.ljnavigation.vitamio.VitamioVideoViewActivity;
import com.macernow.djstava.ljnavigation.music.MusicViewActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MovieViewActivity extends ActionBarActivity {
    private ListView listView;
    private Button button_back;

    private View.OnClickListener clickListener;

    private List<String> fileList, fileListPath;
    private String[] files, filesPath;

    private AdapterTextview adapterTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        * 去屏保
        * */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_movie_view);

        //初始化UI
        initUIComponent();

        //设置监听器
        initUIComponentListener();

        //扫描歌曲
        scanMovieFiles();

        adapterTextview = new AdapterTextview(this, files);
        listView.setAdapter(adapterTextview);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MovieViewActivity.this, VitamioVideoViewActivity.class);

                if (!filesPath[position].isEmpty()) {
                    intent.putExtra("movieUrl", filesPath[position]);
                    DJLog.d("movieUrl :" + filesPath[position]);
                    startActivity(intent);
                } else {
                    Toast.makeText(MovieViewActivity.this,R.string.no_such_movie,Toast.LENGTH_SHORT).show();
                }

                //intent.putExtra("movieName", files[position]);
                //intent.putExtra("movieId", position + 1);
                //intent.putExtra("currentPosition",currentPosition);

                //Log.e(TAG, "movieName : " + files[position]);
                //Log.e(TAG, "movieId : " + String.valueOf(position + 1));
                //Log.e(TAG,"string currentPosition : " + String.valueOf(currentPosition));
            }
        });
    }

    private void initUIComponent() {
        button_back = (Button) findViewById(R.id.button_back);
        listView = (ListView) findViewById(R.id.listview);
    }

    private void initUIComponentListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_back:
                        finish();
                        break;

                    default:
                        break;
                }
            }
        };

        button_back.setOnClickListener(clickListener);

    }

    private void scanMovieFiles() {
        fileList = new ArrayList<String>();
        fileListPath = new ArrayList<String>();

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            DJLog.d("SD Card not mounted.");
        }

        final File[] file = Environment.getExternalStorageDirectory().listFiles();
        readFile(file);
        files = fileList.toArray(new String[1]);
        filesPath = fileListPath.toArray(new String[1]);
    }

    private void readFile(final File[] file) {
        for (int i = 0; (file != null) && (i < file.length); i++) {
            if (file[i].isFile() && (file[i].getName().endsWith("mp4") || file[i].getName().endsWith("mkv")
                    || file[i].getName().endsWith("MP4") || file[i].getName().endsWith("MKV")
                    || file[i].getName().endsWith("avi") || file[i].getName().endsWith("AVI")
                    || file[i].getName().endsWith("rmvb") || file[i].getName().endsWith("RMVB"))) {
                fileList.add(file[i].getName());
                fileListPath.add(file[i].getPath());
            } else if (file[i].isDirectory()) {
                final File[] tempFileList = new File(file[i].getAbsolutePath()).listFiles();
                readFile(tempFileList);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_view, menu);
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
