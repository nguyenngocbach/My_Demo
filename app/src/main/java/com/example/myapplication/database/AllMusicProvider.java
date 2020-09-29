package com.example.myapplication.database;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.example.myapplication.MainActivity;
import com.example.myapplication.Model.Song;
import com.example.myapplication.unit.LogSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * BachNN
 * Class cung cấp một ListSong cho BaseSongListFragment.
 */
public class AllMusicProvider {
    private Context mContext;
    private List<Song> mSongs;

    public AllMusicProvider(Context mContext) {
        this.mContext = mContext;
        new LoadAllMusic().doInBackground();
        getAllSong();
    }

    public List<Song> getAllListSong(){
        return mSongs;
    }

    private void getAllSong() {
        mSongs= new ArrayList<>();
        String[] allColoumSong = new String[]{
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DURATION
        };
        // query các trường trên để lấy thông tin các bàn hát
        Cursor cursor = mContext.getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, allColoumSong, null, null, null, null);
        // chuyển con trỏ đến đâu bảng
        if (LogSetting.sLife) {
            Log.d(MainActivity.TAG_MAIN, "cursor : " + cursor);
        }
        cursor.moveToFirst();
        if (cursor != null) {
            while (!cursor.isAfterLast()) {
                // lấy các gia trị theo các trương của bảng
                int _ID = cursor.getColumnIndex(allColoumSong[0]);
                int DATA = cursor.getColumnIndex(allColoumSong[1]);
                int ARTIST = cursor.getColumnIndex(allColoumSong[2]);
                int TITLE = cursor.getColumnIndex(allColoumSong[3]);
                int DISPLAY_NAME = cursor.getColumnIndex(allColoumSong[4]);
                int DURATION = cursor.getColumnIndex(allColoumSong[5]);

                String id = cursor.getString(_ID);
                String data = cursor.getString(DATA);
                String author = cursor.getString(ARTIST);
                String title = cursor.getString(TITLE);
                String displayName = cursor.getString(DISPLAY_NAME);
                String duration = cursor.getString(DURATION);
                mSongs.add(new Song(id, data, author, title, displayName, duration));
                if (LogSetting.sLife) {
                    Log.d(MainActivity.TAG_MAIN, mSongs.size() + "");
                }
                cursor.moveToNext();
            }
            cursor.close();
        }

    }

    class LoadAllMusic extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            getAllSong();
            return null;
        }

    }
}
