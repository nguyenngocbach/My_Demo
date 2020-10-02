package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myapplication.model.Song;
import com.example.myapplication.util.Util;
import com.example.myapplication.util.LogSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * BachNN
 * Class nay dùng để quản lý các hành vị query với DATABSE
 */
public class DataManager {
    public static final String TAG = "BachNN";
    private SongaFvouriteDatabaseHelper mMusicHelper;
    private SQLiteDatabase mDatabase;

    /** BachNN
     * @param context khơi tao
     */
    public DataManager(Context context) {
        //BachNN : khởi tạo SongaFavouriteDatabaseHelper là lớp còn của SQLiteOpenHelper class
        mMusicHelper = new SongaFvouriteDatabaseHelper(context);
        mDatabase = mMusicHelper.getWritableDatabase();
    }

    public void addMusicFavourite(Song song) {
        ContentValues cvValues = new ContentValues();
        cvValues.put(SongaFvouriteDatabaseHelper._ID, song.getId());
        cvValues.put(SongaFvouriteDatabaseHelper._PATH, song.getPath());
        cvValues.put(SongaFvouriteDatabaseHelper._AUTHOR, song.getAuthor());
        cvValues.put(SongaFvouriteDatabaseHelper._TITLE, song.getTitle());
        cvValues.put(SongaFvouriteDatabaseHelper._DISPLAY_NAME, song.getDisplay_Name());
        cvValues.put(SongaFvouriteDatabaseHelper._DURATION, song.getDuration());
        Long insert = mDatabase.insert(SongaFvouriteDatabaseHelper.TABLE_NAME, null, cvValues);
        if (insert > 0) {
            if (LogSetting.IS_DEBUG) {
                Log.d(TAG, "Thêm Thành Công");
            }
        } else {
            if (LogSetting.IS_DEBUG) {
                Log.d(TAG, "Thêm Thất Bại");
            }
        }

    }

    public void removeMusicFavourite(int id) {
        int delete = mDatabase.delete(SongaFvouriteDatabaseHelper.TABLE_NAME, SongaFvouriteDatabaseHelper._ID + "=" + id, null);
        if (delete > 0) {
            if (LogSetting.IS_DEBUG) {
                Log.d(TAG, "Xóa Thành Công");
            }
        } else {
            if (LogSetting.IS_DEBUG) {
                Log.d(TAG, "Xóa Thất Bại");
            }
        }
    }

    /**
     * BachNN
     * @return chả về một list bài nhạc yêu thich trong Database
     */
    public List<Song> getAllMusicFavourite() {
        List<Song> mAllSong = new ArrayList<>();
        Cursor cursor = mDatabase.query(SongaFvouriteDatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        //BachNN :  lấy vị trị của các cột theo các thuộc tính trong database
        int id = cursor.getColumnIndex(SongaFvouriteDatabaseHelper._ID);
        int path = cursor.getColumnIndex(SongaFvouriteDatabaseHelper._PATH);
        int author = cursor.getColumnIndex(SongaFvouriteDatabaseHelper._AUTHOR);
        int title = cursor.getColumnIndex(SongaFvouriteDatabaseHelper._TITLE);
        int displayName = cursor.getColumnIndex(SongaFvouriteDatabaseHelper._DISPLAY_NAME);
        int duration = cursor.getColumnIndex(SongaFvouriteDatabaseHelper._DURATION);
        //BachNN : chuyển con trỏ đến đâu bảng
        cursor.moveToFirst();
        // BachNN :nếu con trỏ ko phải vị trị cuối cùng thì chạy tiếp vong While
        while (!cursor.isAfterLast()) {
            // BachNN: lấy các gia trị theo các trương của bảng
            String idSong = cursor.getString(id);
            String pathSong = cursor.getString(path);
            String authorSong = cursor.getString(author);
            String titleSong = cursor.getString(title);
            String displaySing = cursor.getString(displayName);
            String durationSong = cursor.getString(duration);
            Song song = new Song(idSong, pathSong, authorSong, titleSong, displaySing, durationSong);
            // BachNN :thêm Song vào List Song
            mAllSong.add(song);
            // chuyển con tro xuông dong dươi của bảng
            cursor.moveToNext();
        }
        //BachNN : đóng con trỏ lại
        cursor.close();
        return mAllSong;
    }
}
