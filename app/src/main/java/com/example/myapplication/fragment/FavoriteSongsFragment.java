package com.example.myapplication.fragment;

import android.os.AsyncTask;
import android.util.Log;

import com.example.myapplication.MainActivity;
import com.example.myapplication.model.Song;
import com.example.myapplication.util.LogSetting;

import java.util.List;

public class FavoriteSongsFragment extends AllSongFragment {

    //BachNN : mID là chưa ID cua 1 bài hát để tìm lại vị trị dúng của bài hát
    // khi chay bài hát ở FavouriteSongFragment khi quay lại AllSongFragment.
    private String mIDMusic;

    @Override
    public void loadData() {
        //Bkav Thanhnch: -1 can co y nghia, la gi? tao bien constant.-ok
        //BachNN : khi chuyền từ AllSongFragment sang FavouriteSongFraggment mà chưa chay bài hát nào.
        // dong dười để kiềm tra xem AllSongFragment đã chay bài nhạc nào chưa nêu rồi thì lưu mID của nó.
        if (mMainActivity.getMusicService().getCurrentSong() != POSITION_MUSIC) {
            mIDMusic = mMainActivity.getMusicService().getSongIsPlay().getId();
        }
        new AllFavouriteMusic().execute();
        mMainActivity.getMusicService().setCurrentSong(POSITION_MUSIC);
    }

    /**
     * BachNN
     * Hàm này dùng đế lấy lại List Nhạc mà đã bị thay thế băng các bài hát yêu thích.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Bkav Thanhnch:
        if (mMainActivity.getMusicService().getCurrentSong() != POSITION_MUSIC) {
            mIDMusic = mMainActivity.getMusicService().getSongIsPlay().getId();
        }
        mMainActivity.getMusicService().setAllSongService(getAllSong());
        resetCurrentSong();
        if (LogSetting.IS_DEBUG) {
            Log.d(MainActivity.TAG, "onDestroy To ");
        }
    }

    /**
     * BachNN
     * hàm này dụng để set lại vị trị của bài hát cho AllSongFragment.
     */
    //Bkav Thanhnch:
    //chua fomat code
    public void resetCurrentSong() {
        for (int i = 0; i < mMainActivity.getMusicService().getSongs().size(); i++) {
            if (mMainActivity.getMusicService().getSongs().get(i).getId().equals(mIDMusic)) {
                mMainActivity.getMusicService().setCurrentSong(i);
            }
        }
    }

    /**
     * Bkav Thanhnch: todo chuyen sang lop favourite
     * <p>
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
            mMainActivity.getMusicService().setAllSongService(songs);
        }
    }
}
