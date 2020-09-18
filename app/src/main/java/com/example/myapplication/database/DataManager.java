package com.example.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DataManager {
    private SongaFvouriteDatabaseHelper helper;
    private Context context;
    private SQLiteDatabase database;

    public DataManager(Context context) {
        this.context = context;
        helper= new SongaFvouriteDatabaseHelper(context);
    }
}
