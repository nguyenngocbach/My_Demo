package com.example.myapplication.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MusicService extends Service {

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
