package com.example.myapplication.listenner;

import android.view.View;

/** BachNN
 * đùng để chọn bài hát trong AllSongFragment.
 */
public interface IMusicListenner {
    //BachNN : để chọn một bài trong list Music.
    void selectMusic(int i);
    //BachNN : xuất biết Popun khi click vào iconMore trên tường dòng.
    void selectMoreMusic(int i, View view);
}
