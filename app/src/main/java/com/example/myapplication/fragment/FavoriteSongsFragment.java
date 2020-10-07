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

    //BachNN : mID là chưa ID cua 1 bài hát để tìm lại vị trị dúng của bài hát
    // khi chay bài hát ở FavouriteSongFragment khi quay lại AllSongFragment.
    private String mID;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void loadData() {
        //Bkav Thanhnch: -1 can co y nghia, la gi? tao bien constant.-ok
        //BachNN : khi chuyền từ AllSongFragment sang FavouriteSongFraggment mà chưa chay bài hát nào.
        // dong dười để kiềm tra xem AllSongFragment đã chay bài nhạc nào chưa nêu rồi thì lưu mID của nó.
        if (mMusicService.getCurrentSong() != POSITION_MUSIC) {
            mID = mMusicService.getSongIsPlay().getId();
        }
        new AllFavouriteMusic().execute();
        mMusicService.setCurrentSong(POSITION_MUSIC);
    }

    /**
     * BachNN
     * Hàm này dùng đế lấy lại List Nhạc mà đã bị thay thế băng các bài hát yêu thích.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Bkav Thanhnch:
        if (mMusicService.getCurrentSong() != POSITION_MUSIC) {
            mID = mMusicService.getSongIsPlay().getId();
        }
        mMusicService.setAllSongService(getAllSong());
        resetCurrentSong();
        if (LogSetting.IS_DEBUG) {
            Log.d(MainActivity.TAG_MAIN, "onDestroy To ");
        }
    }

    /**
     * BachNN
     * hàm này dụng để set lại vị trị của bài hát cho AllSongFragment.
     */
    //Bkav Thanhnch:
    //chua fomat code
    public void resetCurrentSong() {
        for (int i = 0; i < mMusicService.getSongs().size(); i++) {
            if (mMusicService.getSongs().get(i).getId().equals(mID)) {
                mMusicService.setCurrentSong(i);
            }
        }
    }
    /**Bkav Thanhnch: todo chuyen sang lop favourite
     *
     * BachNN
     * Tạo ra một Thread khác để đọc các bài hát yêu thích từ CSDL về.
     */
    class AllFavouriteMusic extends AsyncTask<Void, Void, List<Song>> {
        @Override
        protected List<Song> doInBackground(Void... voids) {
            return mDatabaseManager.getAllMusicFavourite();
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            setData(songs);
            mMusicService.setAllSongService(songs);
        }
    }
}
