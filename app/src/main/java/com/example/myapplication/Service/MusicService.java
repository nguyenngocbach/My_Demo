package com.example.myapplication.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.myapplication.MainActivity;
import com.example.myapplication.model.Song;
import com.example.myapplication.R;
import com.example.myapplication.fragment.MediaPlaybackFragment;
import com.example.myapplication.util.Util;
import com.example.myapplication.util.LogSetting;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicService extends Service {

    public static final int RANDOM = 5;
    public static final int SHUFF = 6;
    public static final int NORMAL = 7;
    private static final String ID_CHANNEL = "1999";
    private static final int ID_NOTIFICATION = 111;
    private static final String LOG_REALTIME = "log_realtime";
    private int TIME_REPEAT = 300;
    //    private MusicManager mMusicManager;
    private MediaPlaybackFragment mMediaPlaybackFragment = new MediaPlaybackFragment();
    private RemoteViews mNotificationRemoteSmall;
    private RemoteViews mNotificationRemoteBig;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private IBinder iBinder = new LocalMusic();
    private SeekBar mSeekBar;

    private List<Song> mSongs;
    private MediaPlayer mPlayer;
    private int mCurrentSong = -1;
    private Context mContext;

    // BachNN :next,previous bài hát hoặc trọn 1 bài hát bất kỳ.
    public static final int INITIALLY = 0;
    // BachNN :khí bài hát đang chạy nó bị dừng.
    public static final int STOP = 3;
    private int mStatueRepeat = NORMAL;
    // BachNN :về trang thái lúc ban đâu là next , previous bài hát.
    private int mStatus = INITIALLY;


    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (getmCurrentSong() != -1) {
                int index = (Integer.parseInt(getSongIsPlay().getDuration()));
                mSeekBar.setMax(index);
                if (LogSetting.IS_DEBUG) {
                    Log.d(LOG_REALTIME, getTimeCurrents() + "---" + (index) + "--" + mSeekBar.getProgress());
                }
                if (mSeekBar.getProgress() / 100 == index / 100) {
                    onNextMusic();
                    if (LogSetting.IS_DEBUG) {
                        Log.d(LOG_REALTIME, "Next Bai");
                    }
                    Intent intent = new Intent(Util.ACTION_AUTONEXT);
                    sendBroadcast(intent);
                }
                mSeekBar.setProgress(getTimeCurrents());
                mHandler.postDelayed(this, TIME_REPEAT);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mPlayer = new MediaPlayer();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSongs = getAllSong();
        mSeekBar = new SeekBar(this);
//        mHandler.postDelayed(runnable, TIME_REPEAT);
        mMediaPlaybackFragment.setMusicService(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //
            NotificationChannel channel =
                    new NotificationChannel(ID_CHANNEL, "App_Music_OF_Bach", NotificationManager.IMPORTANCE_LOW);
            channel.setLightColor(Color.RED);
            //

            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(channel);
            mNotificationRemoteSmall = new RemoteViews(getPackageName(), R.layout.notifiation_small);
            mNotificationRemoteBig = new RemoteViews(getPackageName(), R.layout.notifiation_big);
            if (mNotificationRemoteBig != null) {
                mNotificationRemoteBig.setOnClickPendingIntent(R.id.icon_previous, onButtonNotificationClick(R.id.icon_previous, Util.ACTION_PREVIOUS));
                mNotificationRemoteBig.setOnClickPendingIntent(R.id.icon_play, onButtonNotificationClick(R.id.icon_play, Util.ACTION_PLAY));
                mNotificationRemoteBig.setOnClickPendingIntent(R.id.icon_next, onButtonNotificationClick(R.id.icon_next, Util.ACTION_NEXT));
            }
            if (mNotificationRemoteSmall != null) {
                mNotificationRemoteSmall.setOnClickPendingIntent(R.id.icon_previous, onButtonNotificationClick(R.id.icon_previous, Util.ACTION_PREVIOUS));
                mNotificationRemoteSmall.setOnClickPendingIntent(R.id.icon_play, onButtonNotificationClick(R.id.icon_play, Util.ACTION_PLAY));
                mNotificationRemoteSmall.setOnClickPendingIntent(R.id.icon_next, onButtonNotificationClick(R.id.icon_next, Util.ACTION_NEXT));

            }
            if (getmCurrentSong() != -1) {
                loadImage();
            }
            mBuilder = new NotificationCompat.Builder(this, ID_CHANNEL)
                    .setSmallIcon(R.drawable.ic_baseline_library_music_24)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(mNotificationRemoteSmall)
                    .setCustomBigContentView(mNotificationRemoteBig);
            mNotificationManager.notify(ID_NOTIFICATION, mBuilder.build());
            startForeground(ID_NOTIFICATION, mBuilder.build());
        }
        return START_NOT_STICKY;
    }

    public void onPlayMusic() {
        mHandler.postDelayed(runnable, TIME_REPEAT);
        if (mStatus == STOP) {
            mPlayer.reset();
            mStatus = 0;
        }
        try {
            mPlayer.setDataSource(mSongs.get(mCurrentSong).getPath());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ham set Anh.
    private void loadImage() {
        byte[] sourceImage = Util.getByteImageSong(getSongIsPlay().getPath());
        if ((sourceImage == null)) return;
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(sourceImage, 0, sourceImage.length);
        mNotificationRemoteSmall.setImageViewBitmap(R.id.icon_music, imageBitmap);
        mNotificationRemoteBig.setImageViewBitmap(R.id.icon_music, imageBitmap);
    }

    /**
     * BachNN
     *
     * @param icon_previous  ID của cái View
     * @param actionPrevious Action Name của BroadCast
     * @return chả về một PendingIntent để chuyền message cho BroadCase
     */
    private PendingIntent onButtonNotificationClick(int icon_previous, String actionPrevious) {
        Intent intent = new Intent(actionPrevious);
        return PendingIntent.getBroadcast(this, icon_previous, intent, 0);
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * BachNN
     * quay lại bàn nhạc
     */
    public void onPreviousMusic() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        if (mCurrentSong == 0) {
            mCurrentSong = mSongs.size() - 1;
        } else mCurrentSong--;
        mPlayer.reset();
        onPlayMusic();
    }


    /**
     * BachNN
     * next bài hát
     */
    public void onNextMusic() {
        mStatus = INITIALLY;
        if (mStatueRepeat == NORMAL) {
            if (mCurrentSong == (mSongs.size() - 1)) {
                mCurrentSong = 0;
            } else mCurrentSong++;
        } else if (mStatueRepeat == SHUFF) {

        } else {
            Random random = new Random();
            mCurrentSong = random.nextInt(mSongs.size());
        }
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        mPlayer.reset();
        onPlayMusic();
    }

    /**
     * BachNN
     *
     * @return lấy vị trị mà bài hát đang chạy
     */
    public int getmCurrentSong() {
        return mCurrentSong;

    }

    /**
     * BachNN
     *
     * @param CurrentSong lấy bài hát của vi trị trên
     */
    public void setmCurrentSong(int CurrentSong) {
        mCurrentSong = CurrentSong;
    }

    public void setRandom() {
        if (mStatueRepeat == RANDOM) {
            mStatueRepeat = NORMAL;
        } else mStatueRepeat = RANDOM;
    }

    public void setShuff() {
        if (mStatueRepeat == SHUFF) {
            mStatueRepeat = NORMAL;
        } else mStatueRepeat = SHUFF;
    }

    public int getStatueRepeat() {
        return mStatueRepeat;
    }

    /**
     * BachNN
     *
     * @param position vị tri mà người dụng chon để chay bài hát.
     */
    public void selectMusic(int position) {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        mPlayer.reset();
        mCurrentSong = position;
        onPlayMusic();
        mStatus = INITIALLY;
    }

    public void setRunning() {
        if (mPlayer.getCurrentPosition() == Integer.parseInt(getSongIsPlay().getDuration())) {
            onNextMusic();
        }
    }

    /**
     * BachNN
     *
     * @return true thi bài nhạc đang chay con false thi ngược lại
     */
    public boolean isMusicPlaying() {
        return (mPlayer.isPlaying()) ? true : false;
    }

    public void onPauseMusic() {
        mPlayer.pause();
    }

    public void onResumeMusic() {
        mPlayer.start();
    }

    /**
     * BachNN
     * re lại bài hát
     */
    public void onResetMusic() {
        mPlayer.pause();
        mPlayer.reset();
    }

    /**
     * BachNN
     * dưng lại bài hát
     */
    public void onStopMusic() {
        mPlayer.pause();
        mStatus = STOP;
    }

    /**
     * BachNN
     *
     * @param position vị tri người dung muôn tua đến
     *                 tua bài hát.
     */
    public void setSeekMusic(int position) {
        mPlayer.seekTo(position);
    }

    /**
     * BachNN
     * get tất cả các bài hát từ Database trên thiết bị di động.
     */
    private List<Song> getAllSong() {
        List<Song> songs = new ArrayList<>();
        String[] allColoumSong = new String[]{
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DURATION
        };
        // BachNN :query các trường trên để lấy thông tin các bàn hát
        Cursor cursor = mContext.getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, allColoumSong, null, null, null, null);
        // BachNN :chuyển con trỏ đến đâu bảng
        if (LogSetting.IS_DEBUG) {
            Log.d(MainActivity.TAG_MAIN, "cursor : " + cursor);
        }
        cursor.moveToFirst();
        if (cursor != null) {
            while (!cursor.isAfterLast()) {
                // BachNN :lấy các gia trị theo các trương của bảng
                int _ID = cursor.getColumnIndex(allColoumSong[0]);
                int DATA = cursor.getColumnIndex(allColoumSong[1]);
                int ARTIST = cursor.getColumnIndex(allColoumSong[2]);
                int TITLE = cursor.getColumnIndex(allColoumSong[3]);
                int DISPLAY_NAME = cursor.getColumnIndex(allColoumSong[4]);
                int DURATION = cursor.getColumnIndex(allColoumSong[5]);

                String id = cursor.getString(_ID);
                String data = cursor.getString(DATA);
                String author = cursor.getString(ARTIST);
                String title = cursor.getString(TITLE);
                String displayName = cursor.getString(DISPLAY_NAME);
                String duration = cursor.getString(DURATION);
                songs.add(new Song(id, data, author, title, displayName, duration));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return songs;

    }

    public List<Song> getmSongs() {
        return mSongs;
    }


    public Song getSongIsPlay() {
        return mSongs.get(mCurrentSong);
    }

    public int getmStatus() {
        return mStatus;
    }

    public void setmStatus(int Status) {
        mStatus = Status;
    }

    /**
     * BachNN
     *
     * @return get vị trị mà bài hát đang chay trong bài hát.
     */
    public int getTimeCurrents() {
        return mPlayer.getCurrentPosition();
    }

    public void setChangeUIMediaFragment() {
        mMediaPlaybackFragment.setUIMusic();
    }

    public void setChangeNotification() {
        mNotificationRemoteBig.setTextViewText(R.id.noti_title, getSongIsPlay().getTitle());
        mNotificationRemoteBig.setTextViewText(R.id.noti_author, getSongIsPlay().getAuthor());
        if (isMusicPlaying()) {
            mNotificationRemoteBig.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
            mNotificationRemoteSmall.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
        } else {
            mNotificationRemoteBig.setImageViewResource(R.id.icon_play, R.drawable.costom_play);
            mNotificationRemoteSmall.setImageViewResource(R.id.icon_play, R.drawable.costom_play);
        }
        loadImage();
        mNotificationManager.notify(ID_NOTIFICATION, mBuilder.build());
    }

    /**
     * BachNN
     *
     * @param s là list bài hát
     *          hàm này dùng để set bài hát .
     */
    public void setAllSongService(List<Song> s) {
        mSongs.clear();
        mSongs.addAll(s);
    }


    public class LocalMusic extends Binder implements Serializable {
        public MusicService getInstanceService() {
            return MusicService.this;
        }

        /**
         * BachNN
         * set lại các giai trị của Notification
         */
        public void setNextMusicNotification() {
            mNotificationRemoteBig.setTextViewText(R.id.noti_title, getSongIsPlay().getTitle());
            mNotificationRemoteBig.setTextViewText(R.id.noti_author, getSongIsPlay().getAuthor());
            mNotificationRemoteBig.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
            mNotificationRemoteSmall.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
            loadImage();
            mNotificationManager.notify(ID_NOTIFICATION, mBuilder.build());
        }

        /**
         * BachNN
         * set lại các giai trị của Notification
         */
        public void setPreviousMusicNotification() {
            mNotificationRemoteBig.setTextViewText(R.id.noti_title, getSongIsPlay().getTitle());
            mNotificationRemoteBig.setTextViewText(R.id.noti_author, getSongIsPlay().getAuthor());
            mNotificationRemoteBig.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
            mNotificationRemoteSmall.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
            loadImage();
            mNotificationManager.notify(ID_NOTIFICATION, mBuilder.build());
        }

        /**
         * BachNN
         * set lại các giai trị của Notification
         */
        public void setPlayMusic() {
            if (isMusicPlaying()) {
                mNotificationRemoteBig.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
                mNotificationRemoteSmall.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
            } else {
                mNotificationRemoteBig.setImageViewResource(R.id.icon_play, R.drawable.costom_play);
                mNotificationRemoteSmall.setImageViewResource(R.id.icon_play, R.drawable.costom_play);
            }
            mNotificationManager.notify(ID_NOTIFICATION, mBuilder.build());
        }

        public void setPlayMusicNoti() {
            if (!isMusicPlaying()) {
                mNotificationRemoteBig.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
                mNotificationRemoteSmall.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
            } else {
                mNotificationRemoteBig.setImageViewResource(R.id.icon_play, R.drawable.costom_play);
                mNotificationRemoteSmall.setImageViewResource(R.id.icon_play, R.drawable.costom_play);
            }
            mNotificationManager.notify(ID_NOTIFICATION, mBuilder.build());
        }

        public MediaPlaybackFragment getMediaPlaybachFragment() {
            return mMediaPlaybackFragment;
        }

        public List<Song> getmSongs() {
            return mSongs;
        }

        public Song getSongIsPlay() {
            return mSongs.get(mCurrentSong);
        }

    }
}
