package com.example.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.myapplication.unit.Coast;

public class SongaFvouriteDatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;

    public SongaFvouriteDatabaseHelper(@Nullable Context context) {
        super(context, Coast.NAME_DATABASE, null, Coast.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Coast.CREATE_SONG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
