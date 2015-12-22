package com.macernow.djstava.ljnavigation.media;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.macernow.djstava.ljnavigation.R;


/**
 * Base Activity，原生MediaPlayer视频播放器页面基类。
 * 如果要实现自定义UI的视频播放器页面，只需要继承该类，在此基础上实现一个{@link NativeMediaController.MediaControllerGenerator}接口即可。
 */
public class BaseNativeVideoPlayerActivity extends Activity
        implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,
        NativeMediaController.MediaPlayerControl, NativeMediaController.MediaControllerGenerator {

    private static final String TAG = BaseNativeVideoPlayerActivity.class.getSimpleName();
    private SurfaceView videoSurface;
    protected MediaPlayer mPlayer;
    protected NativeMediaController mController;
    protected Intent mIntent;
    private SurfaceHolder mVideoHolder;
    private int mCurrentPosition,mTotalTime,mMovieId;
    private String mMovieName,wifiMacAddress,ethMacAddress;
    private AudioManager audioManager;
    private long firstTime = 0;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video_native_activity);

        mIntent = getIntent();
        mMovieName = mIntent.getStringExtra("movieName");
        mMovieId = mIntent.getIntExtra("movieId",0);
        mCurrentPosition = mIntent.getIntExtra("currentPosition",0);

        /*
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt("currentPosition", 0);
        }
        */

        videoSurface = (SurfaceView) findViewById(R.id.video_surface);
        mVideoHolder = videoSurface.getHolder();
        mVideoHolder.addCallback(this);

        mController = new NativeMediaController(this);
        mController.setUIGenerator(this);

        //初始化音频焦点监听器
        initAudioFocusListener();
    }


    @Override
    public BaseMediaControllerHolder generateMediaController() {
        return new BaseMediaControllerHolder();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mController.show();
        return false;
    }

    /*
    * 请求音频焦点
    * */
    private void requestAudioFocus() {
        audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        if (audioManager == null) {
            Log.e(TAG,"audioManager create failed.");
        }

        int ret = audioManager.requestAudioFocus(onAudioFocusChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);

        if (ret != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.e(TAG,"Request AudioFocus failed.");
        }

    }

    private void initAudioFocusListener() {
        Log.e(TAG,"initAudioFocusListen.");
        onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                Log.e(TAG,"onAudioFocusChange");
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_LOSS:
                        Log.e(TAG,"AUDIOFOCUS_LOSS");
                        if (mPlayer.isPlaying()) {
                            mPlayer.pause();
                        }
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        Log.e(TAG,"AUDIOFOCUS_LOSS_TRANSIENT");
                        if (mPlayer.isPlaying()) {
                            mPlayer.pause();
                        }
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        if (mPlayer.isPlaying()) {
                            mPlayer.setVolume(0.1f,0.1f);
                        }
                        break;

                    case AudioManager.AUDIOFOCUS_GAIN:
                        Log.e(TAG,"AUDIOFOCUS_GAIN");
                        if (!mPlayer.isPlaying()) {
                            mPlayer.start();
                        }
                        break;

                    default:
                        break;
                }
            }
        };

    }

    /*
    * 放弃音频焦点
    * */
    private void abandonAudioFocus() {
        if (audioManager != null) {
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
            Log.e(TAG,"abandomAudioFocus.");
            audioManager = null;
        }
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // 创建一个MediaPlayer对象
            mPlayer = new MediaPlayer();

            requestAudioFocus();

            if (mIntent.getStringExtra("movieUrl").isEmpty()) {
                Toast.makeText(BaseNativeVideoPlayerActivity.this,"文件不存在!",Toast.LENGTH_SHORT).show();
                finish();
            }

            // 设置播放的视频数据源
            mPlayer.setDataSource(this, Uri.parse(mIntent.getStringExtra("movieUrl")));

            System.out.println(mIntent.getStringExtra("movieUrl"));

            // 设置AudioStreamType
            //mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 将视频输出到SurfaceView
            mPlayer.setDisplay(mVideoHolder);
            // 播放准备，使用异步方式，配合OnPreparedListener
            mPlayer.prepareAsync();
            // 设置相关的监听器
            mPlayer.setOnPreparedListener(this);

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Toast.makeText(BaseNativeVideoPlayerActivity.this, "影片播放完毕,谢谢欣赏!", Toast.LENGTH_SHORT).show();
                    mPlayer.stop();
                    finish();
                }
            });

            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    if (what == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
                        Toast.makeText(BaseNativeVideoPlayerActivity.this,"Some operation takes too long to complete, usually more than 3-5 seconds.",Toast.LENGTH_LONG).show();
                    } else if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                        Toast.makeText(BaseNativeVideoPlayerActivity.this,"服务器没有响应,请重新点播!",Toast.LENGTH_LONG).show();
                    } else if (what == MediaPlayer.MEDIA_ERROR_UNSUPPORTED) {
                        Toast.makeText(BaseNativeVideoPlayerActivity.this,"Bitstream is conforming to the related coding standard or file spec, but the media framework does not support the feature.",Toast.LENGTH_LONG).show();
                    } else if (what == MediaPlayer.MEDIA_ERROR_MALFORMED) {
                        Toast.makeText(BaseNativeVideoPlayerActivity.this,"Bitstream is not conforming to the related coding standard or file spec.",Toast.LENGTH_LONG).show();
                    } else if (what == MediaPlayer.MEDIA_ERROR_IO) {
                        Toast.makeText(BaseNativeVideoPlayerActivity.this,"File or network related operation errors.",Toast.LENGTH_LONG).show();
                    } else if (what == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
                        Toast.makeText(BaseNativeVideoPlayerActivity.this,"The video is streamed and its container is not valid for progressive playback",Toast.LENGTH_LONG).show();
                    } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                        Toast.makeText(BaseNativeVideoPlayerActivity.this,"未知错误,请重新播放!",Toast.LENGTH_LONG).show();
                    }

                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                    finish();
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    // Implement VideoMediaCController.MediaPlayerControl
    @Override
    public void start() {
        if (mPlayer != null)
            mPlayer.start();
    }

    @Override
    public void pause() {
        if(mPlayer != null)
        mPlayer.pause();
    }

    @Override
    public int getDuration() {
        if (mPlayer != null) {
            return mPlayer.getDuration();
        } else
            return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        } else
            return 0;
    }

    @Override
    public void seekTo(int pos) {
        if (mPlayer != null) {
            mPlayer.seekTo(pos);
        }
    }

    @Override
    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {
    }
    // End Implement VideoMediaCController.MediaPlayerControl

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        mController.setMediaPlayer(this);
        mController.setAnchorView((FrameLayout) findViewById(R.id.video_surface_container));
        mPlayer.start();
        mTotalTime = mPlayer.getDuration();
        this.seekTo(mCurrentPosition);
        if (mCurrentPosition != 0) {
            Toast.makeText(BaseNativeVideoPlayerActivity.this,"从头播放，请连续按2次方向左键!",Toast.LENGTH_LONG).show();
        }
        mController.show();
        mController.updatePausePlay();
    }
    // End MediaPlayer.OnPreparedListener

    @Override
    protected void onPause() {
        super.onPause();
        mCurrentPosition = this.getCurrentPosition();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentPosition", mCurrentPosition);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        abandonAudioFocus();
    }

    private void releaseMediaPlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent keyEvent) {

        switch (keyCode) {

            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (mPlayer.isPlaying() == true) {
                    mPlayer.pause();
                    mController.show();
                } else {
                    mPlayer.start();
                    mController.hide();
                }
                return true;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mPlayer.isPlaying() == true) {
                    mCurrentPosition = mPlayer.getCurrentPosition();
                    if (mCurrentPosition - 5000 <= 0) {
                        seekTo(0);
                    }
                    else {
                        seekTo(mCurrentPosition - 5000);
                    }
                    mController.show();
                }
                keyEvent.startTracking();
                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mPlayer.isPlaying() == true) {
                    mCurrentPosition = mPlayer.getCurrentPosition();
                    if (mCurrentPosition + 15000 >= mTotalTime){
                        seekTo(mTotalTime);
                    }
                    else {
                        seekTo(mCurrentPosition + 15000);
                    }
                    mController.show();
                }
                keyEvent.startTracking();
                return true;


            case KeyEvent.KEYCODE_BACK:
                mPlayer.pause();

                new AlertDialog.Builder(BaseNativeVideoPlayerActivity.this).setMessage("是否退出?")
                        .setPositiveButton("是",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mPlayer.stop();
                                mPlayer = null;
                                finish();
                            }
                        })
                        .setNegativeButton("否",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mPlayer.start();
                            }
                        }).show();


                return true;

            case KeyEvent.KEYCODE_VOLUME_UP:

                //audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;

            case KeyEvent.KEYCODE_VOLUME_DOWN:

                //audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
        }

        return super.onKeyDown(keyCode, keyEvent);
    }

}
