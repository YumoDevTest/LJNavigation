package com.macernow.djstava.ljnavigation.music;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.macernow.djstava.ljnavigation.R;
import com.macernow.djstava.ljnavigation.utils.DJLog;

public class MusicService extends Service {
    public static final String ACTION_UPDATE_PROGRESS = "com.macernow.djstava.ljnavigation.music.UPDATE_PROGRESS";
    public static final String ACTION_UPDATE_DURATION = "com.macernow.djstava.ljnavigation.music.UPDATE_DURATION";
    public static final String ACTION_UPDATE_CURRENT_MUSIC = "com.macernow.djstava.ljnavigation.music.UPDATE_CURRENT_MUSIC";


    private int currentMode = 1; //default all loop

    public static final int MODE_ONE_LOOP = 0;
    public static final int MODE_ALL_LOOP = 1;
    public static final int MODE_RANDOM = 2;
    public static final int MODE_SEQUENCE = 3;

    private static final int updateProgress = 1;
    private static final int updateCurrentMusic = 2;
    private static final int updateDuration = 3;

    private Notification notification;

    private MediaPlayer mediaPlayer;
    private int currentIndex = 0;
    private int currentPosition = 0;
    private boolean isPlaying = false;

    private ArrayList<File> mMusicFilesList = new ArrayList<File>();

    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = null;

    private final IBinder musicBinder = new MusicBinder();

    public MusicService() {
    }

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case updateProgress:
                    toUpdateProgress();
                    break;
                case updateDuration:
                    toUpdateDuration();
                    break;
                case updateCurrentMusic:
                    toUpdateCurrentMusic();
                    break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");

        DJLog.d("service onBind.");

        return musicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        DJLog.d("service onUnbind.");
        super.onUnbind(intent);

        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        DJLog.d("service onRebind.");
        super.onRebind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DJLog.d("onCreate.");

        //初始化音频焦点
        initAudioFocusListener();

        //初始化播放器
        initMediaPlayer();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*
        * 获取activity里传递过来的ArrayList<File>
        * */
        Bundle bundle = intent.getExtras();
        mMusicFilesList.clear();

        mMusicFilesList = (ArrayList<File>) bundle.getSerializable("musicFileList");
        currentIndex = 0;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        abandonAudioFocus();

        super.onDestroy();
        DJLog.d("onDestroy.");
    }

    private void initAudioFocusListener() {
        onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                DJLog.d("onAudioFocusChange");
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_LOSS:
                        DJLog.d("AUDIOFOCUS_LOSS");
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        }

                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        DJLog.d("AUDIOFOCUS_LOSS_TRANSIENT");
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        }
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        DJLog.d("AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.setVolume(0.1f, 0.1f);
                        }
                        break;

                    case AudioManager.AUDIOFOCUS_GAIN:
                        DJLog.d("AUDIOFOCUS_GAIN");
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                        }
                        break;

                    default:
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }

                        mediaPlayer.release();
                        mediaPlayer = null;
                        break;
                }
            }
        };

    }

    /**
     * initialize the MediaPlayer
     */
    private void initMediaPlayer() {

        requestAudioFocus();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.seekTo(currentPosition);
                mediaPlayer.start();
                //Log.e(TAG, "[OnPreparedListener] Start at " + currentIndex + " in mode " + currentMode + ", currentPosition : " + currentPosition);
                handler.sendEmptyMessage(updateDuration);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (isPlaying) {
                    switch (currentMode) {
                        case MODE_ONE_LOOP:
                            DJLog.d("[Mode] currentMode = MODE_ONE_LOOP.");
                            play(currentIndex, 0);
                            break;

                        case MODE_ALL_LOOP:
                            DJLog.d("[Mode] currentMode = MODE_ALL_LOOP.");
                            play((currentIndex + 1) % mMusicFilesList.size(), 0);
                            break;

                        case MODE_RANDOM:
                            DJLog.d("[Mode] currentMode = MODE_RANDOM.");
                            play(getRandomPosition(), 0);
                            break;

                        case MODE_SEQUENCE:
                            DJLog.d("[Mode] currentMode = MODE_SEQUENCE.");
                            if (currentIndex < mMusicFilesList.size() - 1) {
                                playNext();
                            } else {
                                stop();
                            }

                            break;
                        default:
                            DJLog.d("No Mode selected! How could that be ?");
                            break;
                    }
                    //Log.e(TAG, "[OnCompletionListener] Going to play at " + currentIndex);
                }
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                DJLog.d("media player error.");
                return false;
            }
        });

    }

    /*
    * 请求音频焦点
    * */
    private void requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager == null) {
            DJLog.d("audioManager create failed.");
        }

        int ret = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (ret != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            DJLog.d("Request AudioFocus failed.");
        }
    }

    /*
    * 放弃音频焦点
    * */
    private void abandonAudioFocus() {
        if (audioManager != null) {
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
            DJLog.d("abandonAudioFocus");
            audioManager = null;
        }
    }

    private void play(int curIndex, int pCurrentPosition) {
        currentPosition = pCurrentPosition;
        setCurrentMusic(curIndex);
        mediaPlayer.reset();

        if ((0 <= currentIndex) && (currentIndex < mMusicFilesList.size())) {
            try {
                mediaPlayer.setDataSource(mMusicFilesList.get(currentIndex).getAbsolutePath());
                mediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.sendEmptyMessage(updateProgress);

            isPlaying = true;

            sendPendingIntend();
        } else {
            DJLog.d("music index out of bounds.");
        }
    }

    private void sendPendingIntend() {
        //消息中心
        Intent intent = new Intent(this, MusicViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new Notification.Builder(this)
                .setTicker("LJNavigation")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("Playing")
                .setContentText(mMusicFilesList.get(currentIndex).getName())
                .setContentIntent(pendingIntent)
                .getNotification();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        startForeground(1, notification);
    }

    private void setCurrentMusic(int pCurrentMusicIndex) {
        currentIndex = pCurrentMusicIndex;
        handler.sendEmptyMessage(updateCurrentMusic);
    }

    private void stop() {
        mediaPlayer.stop();
        isPlaying = false;
    }

    private void playNext() {
        switch (currentMode) {
            case MODE_ONE_LOOP:
                play(currentIndex, 0);
                break;
            case MODE_ALL_LOOP:
                if (currentIndex + 1 == mMusicFilesList.size()) {
                    currentIndex = 0;
                    play(currentIndex, 0);
                } else {
                    currentIndex += 1;
                    play(currentIndex, 0);
                }
                break;
            case MODE_SEQUENCE:
                if (currentIndex + 1 == mMusicFilesList.size()) {
                    Toast.makeText(this, R.string.music_no_songs, Toast.LENGTH_SHORT).show();
                } else {
                    currentIndex += 1;
                    play(currentIndex, 0);
                }
                break;
            case MODE_RANDOM:
                play(getRandomPosition(), 0);
                break;
        }
    }

    private void playPrevious() {
        switch (currentMode) {
            case MODE_ONE_LOOP:
                play(currentIndex, 0);
                break;
            case MODE_ALL_LOOP:
                if (currentIndex - 1 < 0) {
                    currentIndex = mMusicFilesList.size() - 1;
                    play(currentIndex, 0);
                } else {
                    currentIndex -= 1;
                    play(currentIndex, 0);
                }
                break;
            case MODE_SEQUENCE:
                if (currentIndex - 1 < 0) {
                    Toast.makeText(this, R.string.music_no_previous, Toast.LENGTH_SHORT).show();
                } else {
                    currentIndex -= 1;
                    play(currentIndex, 0);
                }
                break;
            case MODE_RANDOM:
                play(getRandomPosition(), 0);
                break;
        }
    }

    private int getRandomPosition() {
        int random = (int) (Math.random() * mMusicFilesList.size());
        return random;
    }

    private void toUpdateProgress() {
        if (mediaPlayer != null && isPlaying) {
            int progress = mediaPlayer.getCurrentPosition();
            //Log.e(TAG,"current: " + progress);
            Intent intent = new Intent();
            intent.setAction(ACTION_UPDATE_PROGRESS);
            intent.putExtra(ACTION_UPDATE_PROGRESS, progress);
            sendBroadcast(intent);
            handler.sendEmptyMessageDelayed(updateProgress, 1000);
        }
    }

    private void toUpdateDuration() {
        if (mediaPlayer != null) {
            int duration = mediaPlayer.getDuration();
            //Log.e(TAG,"duration=" + duration);
            Intent intent = new Intent();
            intent.setAction(ACTION_UPDATE_DURATION);
            intent.putExtra(ACTION_UPDATE_DURATION, duration);
            sendBroadcast(intent);
        }
    }

    private void toUpdateCurrentMusic() {
        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_CURRENT_MUSIC);
        intent.putExtra(ACTION_UPDATE_CURRENT_MUSIC, currentIndex);
        sendBroadcast(intent);
    }

    class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }

        //index是目标歌曲在musicFileList中的索引
        public void startPlay(int index, int currentPosition) {
            play(index, currentPosition);
        }

        public void stopPlay() {
            stop();
        }

        public void toNext() {
            playNext();
        }

        public void toPrevious() {
            playPrevious();
        }

        /**
         * MODE_ONE_LOOP = 1;
         * MODE_ALL_LOOP = 2;
         * MODE_RANDOM = 3;
         * MODE_SEQUENCE = 4;
         */
        public void setMode(int mode) {
            currentMode = mode;
        }

        /**
         * return the current mode
         * MODE_ONE_LOOP = 1;
         * MODE_ALL_LOOP = 2;
         * MODE_RANDOM = 3;
         * MODE_SEQUENCE = 4;
         *
         * @return
         */
        public int getCurrentMode() {
            return currentMode;
        }

        /**
         * The service is playing the music
         *
         * @return
         */
        public boolean isPlaying() {
            return isPlaying;
        }

        /**
         * Notify Activities to update the current music and duration when current activity changes.
         */
        public void notifyActivity() {
            toUpdateCurrentMusic();
            toUpdateDuration();
            toUpdateProgress();
        }

        /**
         * Seekbar changes
         *
         * @param progress
         */
        public void changeProgress(int progress) {
            if (mediaPlayer != null) {
                //Log.e(TAG, "changeProgress.");
                currentPosition = progress * 1000;
                mediaPlayer.seekTo(currentPosition);

            }
        }
    }
}
