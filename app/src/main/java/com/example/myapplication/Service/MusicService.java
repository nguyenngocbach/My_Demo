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
import com.example.myapplication.fragment.BaseSongListFragment;
import com.example.myapplication.model.Song;
import com.example.myapplication.R;
import com.example.myapplication.util.Util;
import com.example.myapplication.util.LogSetting;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicService extends Service {

    public static final int RANDOM = 5;
    //Bkav Thanhnch: khong hieu bien nay la trang thai nao -ok
    public static final int REPEAT = 6;
    public static final int NORMAL = 7;
    // BachNN :next,previous bài hát hoặc trọn 1 bài hát bất kỳ.
    public static final int INITIALLY = 0;
    // BachNN :khí bài hát đang chạy nó bị dừng.
    public static final int STOP = 3;
    private static final String ID_CHANNEL = "1999";
    private static final int ID_NOTIFICATION = 111;
    private static final String LOG_REALTIME = "log_realtime";
    private int TIME_REPEAT = 300;
    //    private MusicManager mMusicManager;
    private RemoteViews mNotificationRemoteSmall;
    private RemoteViews mNotificationRemoteBig;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    //Bkav Thanhnch: sai convention -ok
    private IBinder mIBinder = new LocalMusic();
    private SeekBar mSeekBar;
    private List<Song> mSongs;
    private MediaPlayer mPlayer;
    private int mCurrentSong = -1;
    private Context mContext;
    private int mStatueRepeat = NORMAL;
    // BachNN :về trang thái lúc ban đâu là next , previous bài hát.
    private int mStatus = INITIALLY;


    private Handler mHandler = new Handler();
    //Bkav Thanhnch: sai convention -ok
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (getCurrentSong() != -1) {
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
        return mIBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSongs = getAllSong();
        mSeekBar = new SeekBar(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(ID_CHANNEL, "App_Music_OF_Bach", NotificationManager.IMPORTANCE_LOW);
            channel.setLightColor(Color.RED);
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(channel);
            mNotificationRemoteSmall = new RemoteViews(getPackageName(), R.layout.notifiation_small);
            mNotificationRemoteBig = new RemoteViews(getPackageName(), R.layout.notifiation_big);
            //Bkav Thanhnch: sao can check null? -ok
            mNotificationRemoteBig.setOnClickPendingIntent(R.id.icon_previous, onButtonNotificationClick(R.id.icon_previous, Util.ACTION_PREVIOUS));
            mNotificationRemoteBig.setOnClickPendingIntent(R.id.icon_play, onButtonNotificationClick(R.id.icon_play, Util.ACTION_PLAY));
            mNotificationRemoteBig.setOnClickPendingIntent(R.id.icon_next, onButtonNotificationClick(R.id.icon_next, Util.ACTION_NEXT));

            mNotificationRemoteSmall.setOnClickPendingIntent(R.id.icon_previous, onButtonNotificationClick(R.id.icon_previous, Util.ACTION_PREVIOUS));
            mNotificationRemoteSmall.setOnClickPendingIntent(R.id.icon_play, onButtonNotificationClick(R.id.icon_play, Util.ACTION_PLAY));
            mNotificationRemoteSmall.setOnClickPendingIntent(R.id.icon_next, onButtonNotificationClick(R.id.icon_next, Util.ACTION_NEXT));


            //Bkav Thanhnch: xem lai bien -1
            if (getCurrentSong() != BaseSongListFragment.POSITION_MUSIC) {
                loadImageNotification();
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

    /**
     * BachNN hàm này dung để chay bài nhạc.
     */
    //Bkav Thanhnch:  thieu comment logic kho hieu, can
    //comment them
    public void onPlayMusic() {
        mHandler.postDelayed(mRunnable, TIME_REPEAT);
        //BachNN : if bài nhạc đang chay mà dừng bài hát lại chuyền bài hát khác thì.
        // đọng sự lý logic dưới.
        if (mStatus == STOP) {
            mPlayer.reset();
            mStatus = INITIALLY;
        }
        try {
            mPlayer.setDataSource(mSongs.get(mCurrentSong).getPath());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) { // BachNN : nó tự động try /catch
            e.printStackTrace();
        }
    }

    //Bkav Thanhnch: thieu ten ai code ham nay -ok
    //BachNN :ten ham khong dung, ne la load anh cho view Notification
    // ham set Anh.
    private void loadImageNotification() {
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
        } else if (mStatueRepeat == REPEAT) {

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
    public int getCurrentSong() {
        return mCurrentSong;

    }

    /**
     * BachNN
     *
     * @param CurrentSong lấy bài hát của vi trị trên
     */
    public void setCurrentSong(int CurrentSong) {
        mCurrentSong = CurrentSong;
    }

    public void setRandom() {
        if (mStatueRepeat == RANDOM) {
            mStatueRepeat = NORMAL;
        } else mStatueRepeat = RANDOM;
    }

    public void setShuff() {
        if (mStatueRepeat == REPEAT) {
            mStatueRepeat = NORMAL;
        } else mStatueRepeat = REPEAT;
    }

    public int getStatueRepeat() {
        return mStatueRepeat;
    }


    /**
     * BachNN
     *
     * @return true thi bài nhạc đang chay con false thi ngược lại
     */
    public boolean isMusicPlaying() {
        return (mPlayer.isPlaying()) ? true : false;
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
        //Bkav Thanhnch: sao khong chuyen vao trong doan check null?
        if (cursor==null){
            if (LogSetting.IS_DEBUG){
                Log.d(MainActivity.TAG_MAIN,"Cursor bang null");
            }
            return null ;
        }
        cursor.moveToFirst();
        if (cursor != null) {
            while (!cursor.isAfterLast()) {
                songs.add(new Song(cursor));
                if (LogSetting.IS_DEBUG){
                    Log.d(MainActivity.TAG_MAIN,""+songs.size());
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return songs;

    }

    public List<Song> getSongs() {
        return mSongs;
    }


    public Song getSongIsPlay() {
        return mSongs.get(mCurrentSong);
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int Status) {
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
        loadImageNotification();
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
    /**
     * BachNN
     * set lại các giai trị của Notification
     */
    public void setNextMusicNotification() {
        mNotificationRemoteBig.setTextViewText(R.id.noti_title, getSongIsPlay().getTitle());
        mNotificationRemoteBig.setTextViewText(R.id.noti_author, getSongIsPlay().getAuthor());
        mNotificationRemoteBig.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
        mNotificationRemoteSmall.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
        loadImageNotification();
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
        loadImageNotification();
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



    public class LocalMusic extends Binder implements Serializable {
        public MusicService getInstanceService() {
            return MusicService.this;
        }

    }
}
