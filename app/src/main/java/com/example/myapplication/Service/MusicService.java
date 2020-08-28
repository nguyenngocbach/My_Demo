package com.example.myapplication.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.myapplication.R;

public class MusicService extends Service {

    private static final String ID_CHANNEL = "1999";
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
        musicManager= new MusicManager(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //
            NotificationChannel channel=
                    new NotificationChannel(ID_CHANNEL, "App_Music_OF_Bach", NotificationManager.IMPORTANCE_LOW);
            channel.setLightColor(Color.RED);
            //
            NotificationManager manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            NotificationCompat.Builder builder= new NotificationCompat.Builder(this,ID_CHANNEL)
                    .setSmallIcon(R.drawable.ic_baseline_library_music_24)
                    .setContentTitle("Music OK")
                    .setContentText("Nguyeexn Ngoc Bach");

            manager.notify(111,builder.build());
        }
        //NotificationCompat.Builder buil
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
