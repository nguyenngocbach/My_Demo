package com.example.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SongaFvouriteDatabaseHelper extends SQLiteOpenHelper {

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
    public SongaFvouriteDatabaseHelper(@Nullable Context context) {
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
