package com.example.myapplication.listenner;

/**
 * các hàm để callback về MainActivity để thay đôi các Fragment
 * theo các Action của Notification.
 */
public interface INotificationBroadCastListener {
    void onNextMusicBroadCast();
    void onPreviousMusicBroadCast();
    void onOnPlayMusicBroadCast();
}
