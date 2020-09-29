package com.example.myapplication.unit;

import android.media.MediaMetadataRetriever;

/** BachNN
 * khái báo các biện mà nhiêu lớp dung
 */
public class Coast {
    public static final String ACTION_NEXT = "com.example.myapplication.unit.nextMusic";
    public static final String ACTION_PREVIOUS = "com.example.myapplication.unit.previousMusic";
    public static final String ACTION_PLAY = "com.example.myapplication.unit.playMusic";
    public static final String ACTION_AUTONEXT = "com.example.myapplication.unit.autoNextMusic";

    // Database
    public static final String NAME_DATABASE = "SongFvourite.db";
    public static final int VERSION = 1;
    public static final String TABLE_NAME = "MY_SONG_FVOURITE";
    public static final String _ID = "_id";
    public static final String _PATH = "_path";
    public static final String _AUTHOR = "_author";
    public static final String _TITLE = "_title";
    public static final String _DISPLAY_NAME = "_display_name";
    public static final String _DURATION = "_duration";

    public static final String CREATE_SONG_TABLE = "CREATE TABLE  " + TABLE_NAME+" ( "+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT ,"
            +_PATH+" TEXT,"
            +_AUTHOR+" TEXT,"
            +_TITLE+" TEXT,"
            +_DISPLAY_NAME+" TEXT,"
            +_DURATION+" TEXT )";

    public static byte[] getByteImageSong(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(path);
        } catch (IllegalArgumentException e) {
            retriever.setDataSource("");
        }
        return retriever.getEmbeddedPicture();
    }
}
