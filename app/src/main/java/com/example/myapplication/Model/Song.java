package com.example.myapplication.Model;

public class Song {
    private String mID;
    private String mPath;
    private String mAuthor;
    private String mTitle;
    private String mDsplayName;
    private String mDuration;

    public Song(String id, String path, String author, String title, String display_Name, String duration) {
        this.mID = id;
        this.mPath = path;
        this.mAuthor = author;
        this.mTitle = title;
        this.mDsplayName = display_Name;
        this.mDuration = duration;
    }

    public String getId() {
        return mID;
    }

    public void setId(String id) {
        this.mID = id;
    }

    public String getDisplay_Name() {
        return mDsplayName;
    }

    public void setDisplay_Name(String display_Name) {
        this.mDsplayName = display_Name;
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
                ", mDsplayName='" + mDsplayName + '\'' +
                ", mDuration='" + mDuration + '\'' +
                '}';
    }
}
