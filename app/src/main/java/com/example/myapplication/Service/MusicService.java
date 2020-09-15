package com.example.myapplication.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.myapplication.R;

public class MusicService extends Service {

    public static final String ID_CHANNEL = "1999";
    private static final CharSequence NANME_CHANNEL = "App_Music";
    private MusicManager mMusicManager;

    private IBinder mIBinder = new LocalMusic();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("bachdz", "onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // đã chạy MusicManager trong service nhé.
        mMusicManager = new MusicManager(getApplication());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mNotificationChannel = new
                    NotificationChannel(ID_CHANNEL, NANME_CHANNEL, NotificationManager.IMPORTANCE_LOW);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mNotificationChannel);
            RemoteViews notification_small = new RemoteViews(getPackageName(), R.layout.notifiation_small);
            RemoteViews notification_big = new RemoteViews(getPackageName(), R.layout.notifiation_big);
            if (notification_small != null) {
                //ImageView imageView= notification_small.setOnClickFillInIntent(R.id.iconPrevious,new Intent());
                //PendingIntent pending= new PendingIntent(this,0, intent,);
                //notification_small.setOnClickPendingIntent(R.id.icon_next, pending);
            }

            NotificationCompat.Builder bulderNotification = new NotificationCompat.Builder(this, ID_CHANNEL)
                    .setSmallIcon(R.drawable.ic_baseline_library_music_24)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notification_small)
                    .setCustomBigContentView(notification_big);
            mNotificationManager.notify(10, bulderNotification.build());
        }

        Log.d("bachdz","Service "+getApplicationContext()+"");
        return START_STICKY;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public MusicManager getmMusicManager() {
        return mMusicManager;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("bachdz", "onDestroy Service");
    }

    public class LocalMusic extends Binder {
        public MusicService getInstanceService() {
            return MusicService.this;
        }
    }
}
