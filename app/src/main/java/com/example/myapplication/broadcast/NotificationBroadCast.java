package com.example.myapplication.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.listenner.INotificationBroadCastListener;
import com.example.myapplication.unit.Coast;
import com.example.myapplication.unit.LogSetting;

/** BachNN
 * Nhận các Message từ Notification bắn ra
 */
public class NotificationBroadCast extends BroadcastReceiver {
    private static final String TAG_BROADCAST = "Log_BroadCast";
    private INotificationBroadCastListener mMusicBroadCastListener;

    public NotificationBroadCast(INotificationBroadCastListener mBroadCastListener) {
        this.mMusicBroadCastListener = mBroadCastListener;
    }

    /** BachNN
     * @param context
     * @param intent  ..
     *                nhân các thông báo từ các Action của Notification.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Coast.ACTION_PREVIOUS.equals(intent.getAction())) {
            if (LogSetting.sLife) {
                Log.d(TAG_BROADCAST, Coast.ACTION_PREVIOUS);
            }
            mMusicBroadCastListener.onPreviousMusicBroadCast();
        }
        if (Coast.ACTION_NEXT.equals(intent.getAction())) {
            if (LogSetting.sLife) {
                Log.d(TAG_BROADCAST, Coast.ACTION_NEXT);
            }
            mMusicBroadCastListener.onNextMusicBroadCast();
        }
        if (Coast.ACTION_PLAY.equals(intent.getAction())) {
            if (LogSetting.sLife) {
                Log.d(TAG_BROADCAST, Coast.ACTION_PLAY);
            }
            mMusicBroadCastListener.onOnPlayMusicBroadCast();
        }
        if (Coast.ACTION_AUTONEXT.equals(intent.getAction())) {
            if (LogSetting.sLife) {
                Log.d(TAG_BROADCAST, Coast.ACTION_AUTONEXT);
            }
            mMusicBroadCastListener.onPlayMusicAutoNextBroadCast();
        }
    }
}
