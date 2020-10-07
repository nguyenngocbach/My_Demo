package com.example.myapplication.model;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

public class Song implements Serializable {
    private String mID;
    private String mPath;
    private String mAuthor;
    private String mTitle;
    private String mDisplayName;
    private String mDuration;

    public Song(String id, String path, String author, String title, String display_Name, String duration) {
        this.mID = id;
        this.mPath = path;
        this.mAuthor = author;
        this.mTitle = title;
        this.mDisplayName = display_Name;
        this.mDuration = duration;
    }

    /**
     * @param cursor bien nay dung de doc cac du lieu ra khoi dong bang.
     *               constructor nay chieu tham so la mot bien Cursor
     */
    public Song(Cursor cursor) {
        String[] allColoumSong = new String[]{
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DURATION
        };
        int idColumn = cursor.getColumnIndex(allColoumSong[0]);
        int dataColumn = cursor.getColumnIndex(allColoumSong[1]);
        int artistColumn = cursor.getColumnIndex(allColoumSong[2]);
        int titleColumn = cursor.getColumnIndex(allColoumSong[3]);
        int displayNameColumn = cursor.getColumnIndex(allColoumSong[4]);
        int durationColumn = cursor.getColumnIndex(allColoumSong[5]);
        String id = cursor.getString(idColumn);
        String data = cursor.getString(dataColumn);
        String author = cursor.getString(artistColumn);
        String title = cursor.getString(titleColumn);
        String displayName = cursor.getString(displayNameColumn);
        String duration = cursor.getString(durationColumn);
        this.mID = id;
        this.mPath = data;
        this.mAuthor = author;
        this.mTitle = title;
        this.mDisplayName = displayName;
        this.mDuration = duration;
    }


    public String getId() {
        return mID;
    }

    public void setId(String id) {
        this.mID = id;
    }

    public String getDisplay_Name() {
        return mDisplayName;
    }

    public void setDisplay_Name(String display_Name) {
        this.mDisplayName = display_Name;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        this.mDuration = duration;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        this.mAuthor = author;
    }

    @Override
    public String toString() {
        return "Song{" +
                "mID='" + mID + '\'' +
                ", mPath='" + mPath + '\'' +
                ", mAuthor='" + mAuthor + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mDsplayName='" + mDisplayName + '\'' +
                ", mDuration='" + mDuration + '\'' +
                '}';
    }

}
