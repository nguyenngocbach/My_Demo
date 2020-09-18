package com.example.myapplication.listenner;

import android.view.View;

/**
 * đùng để chọn bài hát trong AllSongFragment.
 */
public interface IMusicListenner {
    void selectMusic(int i);
    void selectMoreMusic(int i, View view);
}
