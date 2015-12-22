package com.macernow.djstava.ljnavigation.media;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.macernow.djstava.ljnavigation.R;
import com.macernow.djstava.ljnavigation.media.BaseMediaControllerHolder;
import com.macernow.djstava.ljnavigation.media.BaseNativeVideoPlayerActivity;
import com.macernow.djstava.ljnavigation.media.NativeMediaController;


public class MovieMediaPlayerActivity extends BaseNativeVideoPlayerActivity implements NativeMediaController.MediaControllerGenerator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public BaseMediaControllerHolder generateMediaController() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.activity_movie_media_player, null);

        BaseMediaControllerHolder holder = new BaseMediaControllerHolder();
        holder.parentLayout = view;
        holder.pauseButton = (ImageButton) view.findViewById(R.id.video_native_media_controller_custom_btn_start);
        holder.currentTimeView = (TextView) view.findViewById(R.id.video_native_media_controller_custom_currenttime);
        holder.totalTimeView = (TextView) view.findViewById(R.id.video_native_media_controller_custom_totaltime);
        holder.seekbar = (SeekBar) view.findViewById(R.id.video_native_media_controller_custom_seekbar);
        //holder.titleView = (TextView)findViewById(R.id.video_native_media_controller_custom_title);
        //holder.fullScreenButton = (ImageButton) view.findViewById(R.id.video_native_media_controller_custom_btn_unfullscreen);
        holder.pauseResId = R.drawable.selector_video_btn_pause;
        holder.startResId = R.drawable.selector_video_btn_start;
        //holder.fullscreenResId = R.drawable.selector_video_btn_fullscreen;
        //holder.unfullscreenResId = R.drawable.selector_video_btn_unfullscreen;

        return holder;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_media_player, menu);
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
