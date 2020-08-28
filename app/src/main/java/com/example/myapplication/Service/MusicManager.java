package com.example.myapplication.Service;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;

import com.example.myapplication.MainActivity;
import com.example.myapplication.Model.Song;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MusicManager implements Serializable {
    private static MusicManager INSTANCE;
    private List<Song> mSongs = new ArrayList<>();
    private MediaPlayer mPlayer;
    private int currentSong = 0;
    private Context mContext;
    //private RunningListenner listenner;

    private int INITIALLY = 0;
    private int STOP = 3;
    private int status = INITIALLY;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public MusicManager(Context context) {
        mContext = context;
        getAllSong();
        Log.d("Running", "Running 0");
//        if (context instanceof MainActivity) {
//            Log.d("Running", "Running");
//            listenner = (RunningListenner) context;
//        }
        Log.d("bachdz", mSongs.size() + "");
        mPlayer = new MediaPlayer();
    }

    public static MusicManager getInstance(Context mContext) {
        if (INSTANCE == null) {
            INSTANCE = new MusicManager(mContext);
        }
        return INSTANCE;
    }


    public void onPlay() {
        if (status == STOP) {
            mPlayer.reset();
            status = 0;
        }
        try {
            mPlayer.setDataSource(mSongs.get(currentSong).getPath());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void onPrevious() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        if (currentSong == 0) {
            currentSong = mSongs.size() - 1;
        } else currentSong--;
        mPlayer.reset();
        onPlay();
        ;
    }

    public void onNext() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        if (currentSong == (mSongs.size() - 1)) {
            currentSong = 0;
        } else currentSong++;
        mPlayer.reset();
        onPlay();
    }


    public int getCurrentSong() {
        return currentSong;

    }

    public void setCurrentSong(int currentSong) {
        this.currentSong = currentSong;
    }

    public void selectMusic(int position) {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        mPlayer.reset();
        currentSong = position;
        onPlay();
    }

    public void setRunning() {
        if (mPlayer.getCurrentPosition() == Integer.parseInt(getSongIsPlay().getDuration())) {
            onNext();
//            if (listenner!=null){
//                Log.d("Running", "Instance");
//                //listenner.musicRun();
//            }
        }
    }


    public boolean isMusicPlaying() {
        if (mPlayer.isPlaying()) return true;
        else return false;
    }

    public void onPauseMusic() {
        mPlayer.pause();
    }

    public void onResumeMusic() {
        mPlayer.start();
    }

    public void onResetMusic() {
        mPlayer.pause();
        mPlayer.reset();
    }

    public void onStop() {
        mPlayer.pause();
        status = STOP;
    }

    public void setSeek(int position) {
        mPlayer.seekTo(position);
    }

    private void getAllSong() {
        String[] allColoumSong = new String[]{
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DURATION
        };

        //Log.d("bachdz","getAllSong" + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        Cursor cursor = mContext.getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, allColoumSong, null, null, null, null);
        cursor.moveToFirst();
        if (cursor != null) {
            while (!cursor.isAfterLast()) {

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
                cursor.moveToNext();
            }
            cursor.close();
        }

    }

    public List<Song> getmSongs() {
        return mSongs;
    }

    public Song getSinpleSong(int position) {
        return mSongs.get(position);
    }

    public Song getSongIsPlay() {
        return mSongs.get(currentSong);
    }

    public int getTimeCurrents() {
        return mPlayer.getCurrentPosition();
    }
//
//    public interface RunningListenner {
//        void musicRun();
//    }
}
