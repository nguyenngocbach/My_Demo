package com.example.myapplication.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.myapplication.MainActivity;
import com.example.myapplication.listenner.INotificationBroadCastListener;
import com.example.myapplication.util.Util;
import com.example.myapplication.util.LogSetting;

/**
 * BachNN
 * Nhận các Message từ Notification bắn ra
 */
public class NotificationBroadCast extends BroadcastReceiver {
    private INotificationBroadCastListener mMusicBroadCastListener;

    public NotificationBroadCast(INotificationBroadCastListener mBroadCastListener) {
        this.mMusicBroadCastListener = mBroadCastListener;
    }

    /**
     * BachNN
     *
     * @param context
     * @param intent  ..
     *                nhân các thông báo từ các Action của Notification.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Util.ACTION_PREVIOUS.equals(intent.getAction())) {
            if (LogSetting.IS_DEBUG) {
                Log.d(MainActivity.TAG, Util.ACTION_PREVIOUS);
            }
            mMusicBroadCastListener.onPreviousMusicBroadCast();
        }
        if (Util.ACTION_NEXT.equals(intent.getAction())) {
            if (LogSetting.IS_DEBUG) {
                Log.d(MainActivity.TAG, Util.ACTION_NEXT);
            }
            mMusicBroadCastListener.onNextMusicBroadCast();
        }
        if (Util.ACTION_PLAY.equals(intent.getAction())) {
            if (LogSetting.IS_DEBUG) {
                Log.d(MainActivity.TAG, Util.ACTION_PLAY);
            }
            mMusicBroadCastListener.onOnPlayMusicBroadCast();
        }
        if (Util.ACTION_AUTONEXT.equals(intent.getAction())) {
            if (LogSetting.IS_DEBUG) {
                Log.d(MainActivity.TAG, Util.ACTION_AUTONEXT);
            }
            mMusicBroadCastListener.onPlayMusicAutoNextBroadCast();
        }
    }
}
