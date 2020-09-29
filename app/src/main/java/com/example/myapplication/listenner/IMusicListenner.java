package com.example.myapplication.listenner;

import android.view.View;

/** BachNN
 * đùng để chọn bài hát trong AllSongFragment.
 */
public interface IMusicListenner {
    // để chọn một bài trong list Music.
    void selectMusic(int i);
    // xuất biết Popun khi click vào iconMore trên tường dòng.
    void selectMoreMusic(int i, View view);
}
