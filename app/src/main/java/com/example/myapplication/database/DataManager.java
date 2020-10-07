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
        ContentValues cvValues = new ContentValues();
        cvValues.put(SongFavouriteDatabaseHelper.ID, song.getId());
        cvValues.put(SongFavouriteDatabaseHelper.PATH, song.getPath());
        cvValues.put(SongFavouriteDatabaseHelper.AUTHOR, song.getAuthor());
        cvValues.put(SongFavouriteDatabaseHelper.TITLE, song.getTitle());
        cvValues.put(SongFavouriteDatabaseHelper.DISPLAY_NAME, song.getDisplay_Name());
        cvValues.put(SongFavouriteDatabaseHelper.DURATION, song.getDuration());
        long insert = mDatabase.insert(SongFavouriteDatabaseHelper.TABLE_NAME, null, cvValues);
        if (insert > 0) {
            if (LogSetting.IS_DEBUG) {
                Log.d(MainActivity.TAG_MAIN, "Thêm Thành Công");
            }
        } else {
            if (LogSetting.IS_DEBUG) {
                Log.d(MainActivity.TAG_MAIN, "Thêm Thất Bại");
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
                Log.d(MainActivity.TAG_MAIN, "Xóa Thành Công");
            }
        } else {
            if (LogSetting.IS_DEBUG) {
                Log.d(MainActivity.TAG_MAIN, "Xóa Thất Bại");
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
        //BachNN :  lấy vị trị của các cột theo các thuộc tính trong database
        int id = cursor.getColumnIndex(SongFavouriteDatabaseHelper.ID);
        int path = cursor.getColumnIndex(SongFavouriteDatabaseHelper.PATH);
        int author = cursor.getColumnIndex(SongFavouriteDatabaseHelper.AUTHOR);
        int title = cursor.getColumnIndex(SongFavouriteDatabaseHelper.TITLE);
        int displayName = cursor.getColumnIndex(SongFavouriteDatabaseHelper.DISPLAY_NAME);
        int duration = cursor.getColumnIndex(SongFavouriteDatabaseHelper.DURATION);
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
