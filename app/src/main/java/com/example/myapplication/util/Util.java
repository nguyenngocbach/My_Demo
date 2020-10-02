package com.example.myapplication.util;

import android.media.MediaMetadataRetriever;

/** BachNN
 * khái báo các biện mà nhiêu lớp dung
 */
public class Util {
    public static final String ACTION_NEXT = "com.example.myapplication.unit.nextMusic";
    public static final String ACTION_PREVIOUS = "com.example.myapplication.unit.previousMusic";
    public static final String ACTION_PLAY = "com.example.myapplication.unit.playMusic";
    public static final String ACTION_AUTONEXT = "com.example.myapplication.unit.autoNextMusic";

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
