package com.example.myapplication.Model;

public class Song {
    private String id;
    private String path;
    private String author;
    private String title;
    private String display_Name;
    private String duration;

    public Song(String id, String path, String author, String title, String display_Name, String duration) {
        this.id = id;
        this.path = path;
        this.author = author;
        this.title = title;
        this.display_Name = display_Name;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay_Name() {
        return display_Name;
    }

    public void setDisplay_Name(String display_Name) {
        this.display_Name = display_Name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
