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
        String id = cursor.getString(0);
        String data = cursor.getString(1);
        String author = cursor.getString(2);
        String title = cursor.getString(3);
        String displayName = cursor.getString(4);
        String duration = cursor.getString(5);
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
