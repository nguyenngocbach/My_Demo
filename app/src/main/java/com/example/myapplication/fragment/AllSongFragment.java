package com.example.myapplication.fragment;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import com.example.myapplication.model.Song;
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
        new LoadAllMusic().execute();
    }
    /**
     * BachNN
     * tạo một luồng để lấy một list bài hát về.
     */
    class LoadAllMusic extends AsyncTask<Void, Void, List<Song>> {
        @Override
        protected List<Song> doInBackground(Void... voids) {
            return getAllSong();
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            setData(songs);
            super.onPostExecute(songs);
        }
    }
}
