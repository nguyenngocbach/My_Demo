package com.example.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SongFavouriteDatabaseHelper extends SQLiteOpenHelper {

    public static final String NAME_DATABASE = "SongFavourite.db";
    public static final int VERSION = 1;
    public static final String TABLE_NAME = "MY_SONG_FAVOURITE";
    public static final String ID = "_id";
    public static final String PATH = "_path";
    public static final String AUTHOR = "_author";
    public static final String TITLE = "_title";
    public static final String DISPLAY_NAME = "_display_name";
    public static final String DURATION = "_duration";

    public static final String CREATE_SONG_TABLE = "CREATE TABLE  " + TABLE_NAME+" ( "+ ID +" INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + PATH +" TEXT,"
            + AUTHOR +" TEXT,"
            + TITLE +" TEXT,"
            + DISPLAY_NAME +" TEXT,"
            + DURATION +" TEXT )";
    public SongFavouriteDatabaseHelper(@Nullable Context context) {
        //BachNN : Coast.NameDatabase là tên Database
        super(context, NAME_DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //BachNN : Coast.CREATE_SONG_TABLE là chuỗi String để tảo bảng.
        sqLiteDatabase.execSQL(CREATE_SONG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
