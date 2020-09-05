package com.example.myapplication.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.myapplication.R;

public class MusicService extends Service {

    public static final String ID_CHANNEL = "1999";
    private static final CharSequence NANME_CHANNEL ="App_Music" ;
    private MusicManager musicManager;

    private IBinder iBinder = new LocalMusic();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("bachdz","onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // đã chạy MusicManager trong service nhé.
        musicManager= new MusicManager(getApplication());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ID_CHANNEL,NANME_CHANNEL,NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);


            RemoteViews notification_small = new RemoteViews(getPackageName(), R.layout.notifiation_small);
            RemoteViews notification_big = new RemoteViews(getPackageName(), R.layout.notifiation_big);

            //notification_small.setO

            if (notification_small!=null){
                //ImageView imageView= notification_small.setOnClickFillInIntent(R.id.iconPrevious,new Intent());
            }
            NotificationCompat.Builder builder= new NotificationCompat.Builder(this, ID_CHANNEL)
                    .setSmallIcon(R.drawable.ic_baseline_library_music_24)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notification_small)
                    .setCustomBigContentView(notification_big);
            manager.notify(10,builder.build());
            //startForeground(11,builder.build());
        }


        return START_STICKY;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class LocalMusic extends Binder {
        public MusicService getInstanceService(){
            return MusicService.this;
        }
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("bachdz","onDestroy");
    }
}
