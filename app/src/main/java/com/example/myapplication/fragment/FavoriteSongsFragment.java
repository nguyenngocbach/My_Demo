package com.example.myapplication.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.myapplication.MainActivity;
import com.example.myapplication.model.Song;
import com.example.myapplication.util.LogSetting;

import java.util.List;

public class FavoriteSongsFragment extends AllSongFragment {

    private String mID;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void LoadData() {
        mID= mMusicService.getSongIsPlay().getId();
        new AllFavouriteMusic().execute();
        mMusicService.setmCurrentSong(-1);
    }

    /**
     * BachNN
     * Hàm này dùng đế lấy lại List Nhạc mà đã bị thay thế băng các bài hát yêu thích.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMusicService.getmCurrentSong()!=-1) {
            mID = mMusicService.getSongIsPlay().getId();
        }
        mMusicService.setAllSongService(getAllSong());
        reSetCurrentSong();
        if (LogSetting.IS_DEBUG) {
            Log.d(MainActivity.TAG_MAIN, "onDestroy To ");
        }
    }

    /**
     * BachNN
     * hàm này dụng để set lại vị trị của bài hát cho AllSongFragment.
     */
    public void reSetCurrentSong(){
        for (int i=0;i<mMusicService.getmSongs().size();i++){
            if (mMusicService.getmSongs().get(i).getId().equals(mID)){
                mMusicService.setmCurrentSong(i);
            }
        }
    }
}
