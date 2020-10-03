package com.example.myapplication.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.example.myapplication.model.Song;

import java.util.List;

public class FavoriteSongsFragment extends AllSongFragment {



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void LoadData() {
        new AllFavouriteMusic().execute();
    }

    class AllFavouriteMusic extends AsyncTask<Void, Void, List<Song>> {
        @Override
        protected List<Song> doInBackground(Void... voids) {
            return mDatabaseManager.getAllMusicFavourite();
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            setData(songs);
        }
    }
}
