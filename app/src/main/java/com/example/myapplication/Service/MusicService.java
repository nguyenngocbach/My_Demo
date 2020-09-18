package com.example.myapplication.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.unit.Coast;

public class MusicService extends Service {

    private static final String ID_CHANNEL = "1999";
    private static final int ID_NOTIFICATION = 111;
    private MusicManager mMusicManager;
    private RemoteViews mNotificationRemoteSmall;
    private RemoteViews mNotificationRemoteBig;
    private NotificationManager mNotifiacationManager;
    private NotificationCompat.Builder mBuilder;
    private Handler mHandler = new Handler();
    private IBinder iBinder = new LocalMusic();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int index = (Integer.parseInt(mMusicManager.getSongIsPlay().getDuration()));
            if (mMusicManager.getTimeCurrents() == index) {
                mMusicManager.onNextMusic();
                Log.d("bcahdz",mMusicManager.getTimeCurrents()+"");
                Intent intent= new Intent(Coast.ACTION_AUTONEXT);
                sendBroadcast(intent);
            }
            mHandler.postDelayed(this, 300);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("bachdz", "onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // đã chạy MusicManager trong service nhé.
        mMusicManager = new MusicManager(this);
        mMusicManager = MusicManager.getInstance(this);
        mHandler.postDelayed(runnable, 300);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //
            NotificationChannel channel =
                    new NotificationChannel(ID_CHANNEL, "App_Music_OF_Bach", NotificationManager.IMPORTANCE_LOW);
            channel.setLightColor(Color.RED);
            //
            mNotifiacationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifiacationManager.createNotificationChannel(channel);
            mNotificationRemoteSmall = new RemoteViews(getPackageName(), R.layout.notifiation_small);
            mNotificationRemoteBig = new RemoteViews(getPackageName(), R.layout.notifiation_big);
            if (mNotificationRemoteBig != null) {
                mNotificationRemoteBig.setTextViewText(R.id.noti_title, mMusicManager.getSongIsPlay().getTitle());
                mNotificationRemoteBig.setTextViewText(R.id.noti_author, mMusicManager.getSongIsPlay().getAuthor());
                mNotificationRemoteBig.setOnClickPendingIntent(R.id.icon_previous, onButtonNotificationClick(R.id.icon_previous, Coast.ACTION_PREVIOUS));
                mNotificationRemoteBig.setOnClickPendingIntent(R.id.icon_play, onButtonNotificationClick(R.id.icon_play, Coast.ACTION_PLAY));
                mNotificationRemoteBig.setOnClickPendingIntent(R.id.icon_next, onButtonNotificationClick(R.id.icon_next, Coast.ACTION_NEXT));
            }
            if (mNotificationRemoteSmall != null) {
                mNotificationRemoteSmall.setOnClickPendingIntent(R.id.icon_previous, onButtonNotificationClick(R.id.icon_previous, Coast.ACTION_PREVIOUS));
                mNotificationRemoteSmall.setOnClickPendingIntent(R.id.icon_play, onButtonNotificationClick(R.id.icon_play, Coast.ACTION_PLAY));
                mNotificationRemoteSmall.setOnClickPendingIntent(R.id.icon_next, onButtonNotificationClick(R.id.icon_next, Coast.ACTION_NEXT));
            }
            loadImage();
            mBuilder = new NotificationCompat.Builder(this, ID_CHANNEL)
                    .setSmallIcon(R.drawable.ic_baseline_library_music_24)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(mNotificationRemoteSmall)
                    .setCustomBigContentView(mNotificationRemoteBig);
            mNotifiacationManager.notify(ID_NOTIFICATION, mBuilder.build());
            startForeground(ID_NOTIFICATION, mBuilder.build());
        }
        return START_STICKY;
    }

    private void loadImage(){
        byte[] sourceImage = Coast.getByteImageSong(mMusicManager.getSongIsPlay().getPath());
        Bitmap imageBitmap= BitmapFactory.decodeByteArray(sourceImage,0,sourceImage.length);
        mNotificationRemoteSmall.setImageViewBitmap(R.id.icon_music,imageBitmap);
        mNotificationRemoteBig.setImageViewBitmap(R.id.icon_music,imageBitmap);
    }

    /**
     * @param icon_previous  ID của cái View
     * @param actionPrevious Action Name của BroadCast
     * @return chả về một PendingIntent để chuyền message cho BroadCase
     */
    private PendingIntent onButtonNotificationClick(int icon_previous, String actionPrevious) {
        Intent intent = new Intent(actionPrevious);
        Log.d("broadcast", "44444444444");
        return PendingIntent.getBroadcast(this, icon_previous, intent, 0);
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public MusicManager getMusicManager() {
        return mMusicManager;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("bachdz", "onDestroy");
    }

    public class LocalMusic extends Binder {
        public MusicService getInstanceService() {
            return MusicService.this;
        }

        /**
         * set lại các giai trị của Notification
         */
        public void setNextMusicNotification() {
            mNotificationRemoteBig.setTextViewText(R.id.noti_title, mMusicManager.getSongIsPlay().getTitle());
            mNotificationRemoteBig.setTextViewText(R.id.noti_author, mMusicManager.getSongIsPlay().getAuthor());
            mNotificationRemoteBig.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
            mNotificationRemoteSmall.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
            loadImage();
            mNotifiacationManager.notify(ID_NOTIFICATION, mBuilder.build());
        }

        /**
         * set lại các giai trị của Notification
         */
        public void setPreviousMusicNotification() {
            mNotificationRemoteBig.setTextViewText(R.id.noti_title, mMusicManager.getSongIsPlay().getTitle());
            mNotificationRemoteBig.setTextViewText(R.id.noti_author, mMusicManager.getSongIsPlay().getAuthor());
            mNotificationRemoteBig.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
            mNotificationRemoteSmall.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
            loadImage();
            mNotifiacationManager.notify(ID_NOTIFICATION, mBuilder.build());
        }

        /**
         * set lại các giai trị của Notification
         */
        public void setPlayMusic() {
            if (mMusicManager.isMusicPlaying()) {
                mNotificationRemoteBig.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
                mNotificationRemoteSmall.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
            }else {
                mNotificationRemoteBig.setImageViewResource(R.id.icon_play, R.drawable.costom_play);
                mNotificationRemoteSmall.setImageViewResource(R.id.icon_play, R.drawable.costom_play);
            }
            mNotifiacationManager.notify(ID_NOTIFICATION, mBuilder.build());
        }

        public void setPlayMusicNoti() {
            if (!mMusicManager.isMusicPlaying()) {
                mNotificationRemoteBig.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
                mNotificationRemoteSmall.setImageViewResource(R.id.icon_play, R.drawable.custom_play_pause);
            }else {
                mNotificationRemoteBig.setImageViewResource(R.id.icon_play, R.drawable.costom_play);
                mNotificationRemoteSmall.setImageViewResource(R.id.icon_play, R.drawable.costom_play);
            }
            mNotifiacationManager.notify(ID_NOTIFICATION, mBuilder.build());
        }
    }
}
