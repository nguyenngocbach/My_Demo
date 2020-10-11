package com.example.myapplication.fragment;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import com.example.myapplication.model.Song;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * BachNN
 * Hiện thị một Danh Sach Music cho người dung thấy
 */
public class AllSongFragment extends BaseSongListFragment {

    public AllSongFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            loadData();
    }

    @Override
    public void loadData() {
        new LoadAllMusic(AllSongFragment.this).execute();
    }

    /**
     * BachNN
     * tạo một luồng để lấy một list bài hát về.
     */
    static class LoadAllMusic extends AsyncTask<Void, Void, List<Song>> {
        private final WeakReference<AllSongFragment> mAllSongReference;

        public LoadAllMusic(AllSongFragment allSongFragment) {
            mAllSongReference = new WeakReference<>(allSongFragment);
        }

        @Override
        protected List<Song> doInBackground(Void... voids) {
            if (mAllSongReference.get() == null) {
                return null;
            }
            return mAllSongReference.get().getAllSongDatabase();
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            if (songs != null && mAllSongReference.get() != null) {
                mAllSongReference.get().setDataAllMusic(songs);
            }
            super.onPostExecute(songs);
        }
    }
}
