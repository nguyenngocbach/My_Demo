package com.example.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.myapplication.unit.Coast;

public class SongaFvouriteDatabaseHelper extends SQLiteOpenHelper {

    public SongaFvouriteDatabaseHelper(@Nullable Context context) {
        // Coast.NameDatabase là tên Database
        super(context, Coast.NAME_DATABASE, null, Coast.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Coast.CREATE_SONG_TABLE là chuỗi String để tảo bảng.
        sqLiteDatabase.execSQL(Coast.CREATE_SONG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
