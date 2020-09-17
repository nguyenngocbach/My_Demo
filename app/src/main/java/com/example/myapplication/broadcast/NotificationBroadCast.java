package com.example.myapplication.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.listenner.INotificationBroadCastListener;
import com.example.myapplication.unit.Coast;

/**
 * Nhận các Message từ Notification bắn ra
 */
public class NotificationBroadCast extends BroadcastReceiver {
    private INotificationBroadCastListener mBroadCastListener;

    public NotificationBroadCast(INotificationBroadCastListener mBroadCastListener) {
        this.mBroadCastListener = mBroadCastListener;
    }

    /**
     * @param context
     * @param intent ..
     *               nhân các thông báo từ các Action của Notification.
     *
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Coast.ACTION_PREVIOUS)) {
            Log.d("broadcast", Coast.ACTION_PREVIOUS);
            mBroadCastListener.onPreviousMusicBroadCast();
        }
        if (intent.getAction().equals(Coast.ACTION_NEXT)) {
            Log.d("broadcast", Coast.ACTION_NEXT);
            mBroadCastListener.onNextMusicBroadCast();
        }
        if (intent.getAction().equals(Coast.ACTION_PLAY)) {
            Log.d("broadcast", Coast.ACTION_PLAY);
            mBroadCastListener.onOnPlayMusicBroadCast();
        }
    }
}
