package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myapplication.MainActivity;
import com.example.myapplication.model.Song;
import com.example.myapplication.util.LogSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * BachNN
 * Class nay dùng để quản lý các hành vị query với DATABSE
 */
public class DataManager {
    private SQLiteDatabase mDatabase;

    /**
     * BachNN
     *
     * @param context khơi tao
     */
    public DataManager(Context context) {
        SongFavouriteDatabaseHelper mMusicHelper;
        //BachNN : khởi tạo SongaFavouriteDatabaseHelper là lớp còn của SQLiteOpenHelper class
        mMusicHelper = new SongFavouriteDatabaseHelper(context);
        mDatabase = mMusicHelper.getWritableDatabase();
    }

    /**
     * BachNN
     *
     * @param song bài hát chuyền vào để lưu vào bảng CSDL
     *             hàm này dùng để thêm một bài hát vào CSDL
     */
    public void addMusicFavourite(Song song) {
        //BachNN : chuyền các giá trị của bài hát vào cvValues để chuyền vào CDSL
        ContentValues contentValues = new ContentValues();
        contentValues.put(SongFavouriteDatabaseHelper.ID, song.getId());
        contentValues.put(SongFavouriteDatabaseHelper.PATH, song.getPath());
        contentValues.put(SongFavouriteDatabaseHelper.AUTHOR, song.getAuthor());
        contentValues.put(SongFavouriteDatabaseHelper.TITLE, song.getTitle());
        contentValues.put(SongFavouriteDatabaseHelper.DISPLAY_NAME, song.getDisplay_Name());
        contentValues.put(SongFavouriteDatabaseHelper.DURATION, song.getDuration());
        long insert = mDatabase.insert(SongFavouriteDatabaseHelper.TABLE_NAME, null, contentValues);
        if (insert > 0) {
            if (LogSetting.IS_DEBUG) {
                Log.d(MainActivity.TAG, "Thêm Thành Công");
            }
        } else {
            if (LogSetting.IS_DEBUG) {
                Log.d(MainActivity.TAG, "Thêm Thất Bại");
            }
        }

    }

    /**
     * BachNN
     *
     * @param id ID của từng bài hát.
     *           để xóa bài hát khỏi CSDL
     */
    public void removeMusicFavourite(int id) {
        int delete = mDatabase.delete(SongFavouriteDatabaseHelper.TABLE_NAME, SongFavouriteDatabaseHelper.ID + "=" + id, null);
        if (delete > 0) {
            if (LogSetting.IS_DEBUG) {
                Log.d(MainActivity.TAG, "Xóa Thành Công");
            }
        } else {
            if (LogSetting.IS_DEBUG) {
                Log.d(MainActivity.TAG, "Xóa Thất Bại");
            }
        }
    }

    /**
     * BachNN
     *
     * @return chả về một list bài nhạc yêu thich trong Database
     */
    public List<Song> getAllMusicFavourite() {
        List<Song> mAllSong = new ArrayList<>();
        Cursor cursor = mDatabase.query(SongFavouriteDatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        //BachNN : chuyển con trỏ đến đâu bảng
        cursor.moveToFirst();
        // BachNN :nếu con trỏ ko phải vị trị cuối cùng thì chạy tiếp vong While
        while (!cursor.isAfterLast()) {
            mAllSong.add(new Song(cursor));
            // chuyển con tro xuông dong dươi của bảng
            cursor.moveToNext();
        }
        //BachNN : đóng con trỏ lại
        cursor.close();
        return mAllSong;
    }
}
