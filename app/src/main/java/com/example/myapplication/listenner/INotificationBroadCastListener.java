package com.example.myapplication.listenner;

/** BachNN
 * các hàm để callback về MainActivity để thay đôi các Fragment
 * theo các Action của Notification.
 */
public interface INotificationBroadCastListener {
    //BachNN : chuyểm bài hát theo Action của Notification
    void onNextMusicBroadCast();
    //BachNN : quay lại bài hát theo Action của Notification
    void onPreviousMusicBroadCast();
    //BachNN : dung hoặc chay  bài hát theo Action của Notification
    void onOnPlayMusicBroadCast();
    //BachNN : tự động next bài khi hết bài nhạc
    void onPlayMusicAutoNextBroadCast();
}
