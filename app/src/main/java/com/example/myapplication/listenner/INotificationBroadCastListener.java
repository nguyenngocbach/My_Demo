package com.example.myapplication.listenner;

/** BachNN
 * các hàm để callback về MainActivity để thay đôi các Fragment
 * theo các Action của Notification.
 */
public interface INotificationBroadCastListener {
    // chuyểm bài hát theo Action của Notification
    void onNextMusicBroadCast();
    // quay lại bài hát theo Action của Notification
    void onPreviousMusicBroadCast();
    // dung hoặc chay  bài hát theo Action của Notification
    void onOnPlayMusicBroadCast();
    // tự động next bài khi hết bài nhạc
    void onPlayMusicAutoNextBroadCast();
}
