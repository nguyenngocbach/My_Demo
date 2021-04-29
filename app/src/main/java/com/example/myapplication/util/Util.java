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
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String IS_LOGIN = "password";


    public static final String DATA ="DATA";
    public static final String ARTIST ="ARTIST";
    public static final String DISPLAY_NAME ="DISPLAY_NAME";
    public static final String TITLE ="TITLE";

    /**
     * BachNN
     * @param path path bai hat
     *             ham nay dung de chuyen path bai hat thanh 1 chuoi byte[].
     * @return 1 chuoi Byte[]
     */
    //Bkav Thanhnch: ham nay de lam gi?
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
