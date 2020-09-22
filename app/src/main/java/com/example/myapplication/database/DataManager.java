package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myapplication.Model.Song;
import com.example.myapplication.unit.Coast;
import com.example.myapplication.unit.LogSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * BachNN
 * Class nay dùng để quản lý các hành vị query với DATABSE
 */
public class DataManager {
    private static final String TAG_DB = "BachNN";
    private SongaFvouriteDatabaseHelper mHelper;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    /**
     * @param context khơi tao
     */
    public DataManager(Context context) {
        this.mContext = context;
        // khởi tạo SongaFvouriteDatabaseHelper là lớp còn của SQLiteOpenHelper class
        mHelper = new SongaFvouriteDatabaseHelper(context);
        mDatabase = mHelper.getWritableDatabase();
    }

    public void addMusicFvourite(Song song) {
        ContentValues cv = new ContentValues();
        cv.put(Coast._ID, song.getId());
        cv.put(Coast._PATH, song.getPath());
        cv.put(Coast._AUTHOR, song.getAuthor());
        cv.put(Coast._TITLE, song.getTitle());
        cv.put(Coast._DISPLAY_NAME, song.getDisplay_Name());
        cv.put(Coast._DURATION, song.getDuration());
        Long insert = mDatabase.insert(Coast.TABLE_NAME, null, cv);
        if (insert > 0) {
            if (LogSetting.sLogDB) {
                Log.d(TAG_DB, "Thêm Thành Công");
            }
        } else {
            if (LogSetting.sLogDB) {
                Log.d(TAG_DB, "Thêm Thất Bại");
            }
        }

    }

    public void removeMusicFvourite(int id) {
        int delete = mDatabase.delete(Coast.TABLE_NAME, Coast._ID + "=" + id, null);
        if (delete > 0) {
            if (LogSetting.sLogDB) {
                Log.d(TAG_DB, "Xóa Thành Công");
            }
        } else {
            if (LogSetting.sLogDB) {
                Log.d(TAG_DB, "Xóa Thất Bại");
            }
        }
    }

    /**
     * BachNN
     *
     * @return chả về một list bài nhạc yêu thich trong Database
     */
    public List<Song> getAllMusicFvourite() {
        List<Song> mAllSong = new ArrayList<>();
        Cursor cursor = mDatabase.query(Coast.TABLE_NAME, null, null, null, null, null, null);
        // lấy vị trị của các cột theo các thuộc tính trong database
        int id = cursor.getColumnIndex(Coast._ID);
        int path = cursor.getColumnIndex(Coast._PATH);
        int author = cursor.getColumnIndex(Coast._AUTHOR);
        int title = cursor.getColumnIndex(Coast._TITLE);
        int displayName = cursor.getColumnIndex(Coast._DISPLAY_NAME);
        int duration = cursor.getColumnIndex(Coast._DURATION);
        // chuyển con trỏ đến đâu bảng
        cursor.moveToFirst();
        // nếu con trỏ ko phải vị trị cuối cùng thì chạy tiếp vong While
        while (!cursor.isAfterLast()) {
            // lấy các gia trị theo các trương của bảng
            String idSong = cursor.getString(id);
            String pathSong = cursor.getString(path);
            String authorSong = cursor.getString(author);
            String titleSong = cursor.getString(title);
            String displaySing = cursor.getString(displayName);
            String durationSong = cursor.getString(duration);
            Song song = new Song(idSong, pathSong, authorSong, titleSong, displaySing, durationSong);
            // thêm Song vào List Song
            mAllSong.add(song);
            // chuyển con tro xuông dong dươi của bảng
            cursor.moveToNext();
        }
        // đóng con trỏ lại
        cursor.close();
        return mAllSong;
    }
}
