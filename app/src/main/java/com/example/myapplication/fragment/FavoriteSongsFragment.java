package com.example.myapplication.fragment;

import android.os.AsyncTask;
import android.util.Log;

import com.example.myapplication.MainActivity;
import com.example.myapplication.model.Song;
import com.example.myapplication.util.LogSetting;

import java.lang.ref.WeakReference;
import java.util.List;

public class FavoriteSongsFragment extends AllSongFragment {

    //BachNN : mID là chưa ID cua 1 bài hát để tìm lại vị trị dúng của bài hát
    // khi chay bài hát ở FavouriteSongFragment khi quay lại AllSongFragment.
    private String mIDMusic;

    @Override
    public void loadData() {
        //BachNN : khi chuyền từ AllSongFragment sang FavouriteSongFraggment mà chưa chay bài hát nào.
        // dong dười để kiềm tra xem AllSongFragment đã chay bài nhạc nào chưa nêu rồi thì lưu mID của nó.
        if (mMainActivity.getMusicService().getCurrentSong() != POSITION_MUSIC_DEFAULT) {
            mIDMusic = mMainActivity.getMusicService().getSongPlaying().getId();
        }
        new AllFavouriteMusic(FavoriteSongsFragment.this).execute();
        mMainActivity.getMusicService().setCurrentSong(POSITION_MUSIC_DEFAULT);
    }

    /**
     * BachNN
     * Hàm này dùng đế lấy lại List Nhạc mà đã bị thay thế băng các bài hát yêu thích.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //BachNN : nếu mà trong danh sach bài hát yêu tích mà chưa chay bài hát nào thì ko set mIDMusic
        if (mMainActivity.getMusicService().getCurrentSong() != POSITION_MUSIC_DEFAULT) {
            mIDMusic = mMainActivity.getMusicService().getSongPlaying().getId();
        }
        mMainActivity.getMusicService().setAllSongService(mMainActivity.getMusicService().getAllSongDatabase());
        resetCurrentSong();
        if (LogSetting.IS_DEBUG) {
            Log.d(MainActivity.TAG, "onDestroy To ");
        }
    }

    /**
     * BachNN
     * hàm này dụng để set lại vị trị của bài hát cho AllSongFragment.
     */
    public void resetCurrentSong() {
        for (int i = 0; i < mMainActivity.getMusicService().getAllSongs().size(); i++) {
            if (mMainActivity.getMusicService().getAllSongs().get(i).getId().equals(mIDMusic)) {
                mMainActivity.getMusicService().setCurrentSong(i);
            }
        }
    }

    /**
     * BachNN
     * Tạo ra một Thread khác để đọc các bài hát yêu thích từ CSDL về.
     */
    static class AllFavouriteMusic extends AsyncTask<Void, Void, List<Song>> {
        private final WeakReference<FavoriteSongsFragment> mFavoriteSongsFragmentReference;

        public AllFavouriteMusic(FavoriteSongsFragment favoriteSongsFragment) {
            mFavoriteSongsFragmentReference = new WeakReference<>(favoriteSongsFragment);
        }

        @Override
        protected List<Song> doInBackground(Void... voids) {
            if (mFavoriteSongsFragmentReference.get() == null) {
                return null;
            }
            return mFavoriteSongsFragmentReference.get().
                    mDatabaseManager.getAllMusicFavourite();
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            if (songs != null && mFavoriteSongsFragmentReference.get() != null) {
                mFavoriteSongsFragmentReference.get().setDataAllMusic(songs);
                mFavoriteSongsFragmentReference.get().
                        mMainActivity.getMusicService().setAllSongService(songs);
            }

        }
    }
}
