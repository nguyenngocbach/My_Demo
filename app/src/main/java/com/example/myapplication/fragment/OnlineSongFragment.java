package com.example.myapplication.fragment;

import android.os.AsyncTask;

import com.example.myapplication.model.Song;

import java.lang.ref.WeakReference;
import java.util.List;

class OnlineSongFragment extends AllSongFragment {
    private String mIDMusic;
    @Override
    public void loadData() {
        if (mMainActivity.getMusicService().getCurrentSong() != POSITION_MUSIC_DEFAULT) {
            mIDMusic = mMainActivity.getMusicService().getSongPlaying().getId();
        }
        new AllServerMusic(OnlineSongFragment.this).execute();
        mMainActivity.getMusicService().setCurrentSong(POSITION_MUSIC_DEFAULT);
    }

    static class AllServerMusic extends AsyncTask<Void, Void, List<Song>> {
        private final WeakReference<OnlineSongFragment> mFavoriteSongsFragmentReference;

        public AllServerMusic(OnlineSongFragment favoriteSongsFragment) {
            mFavoriteSongsFragmentReference = new WeakReference<>(favoriteSongsFragment);
        }

        @Override
        protected List<Song> doInBackground(Void... voids) {
            if (mFavoriteSongsFragmentReference.get() == null) {
                return null;
            }
            return mFavoriteSongsFragmentReference.get().
                    mDatabaseManager.getAllMusicOnline();
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
