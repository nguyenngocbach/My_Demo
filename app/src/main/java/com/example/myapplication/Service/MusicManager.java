package com.example.myapplication.Service;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import com.example.myapplication.MainActivity;
import com.example.myapplication.Model.Song;
import com.example.myapplication.unit.LogSetting;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** BachNN
 * Class này để điều khiển các tương tác giữa người dung đế các
 * bài hát nhữ Next, Previous, Play Music.
 */
public class MusicManager {
    private static MusicManager INSTANCE;
    private List<Song> mSongs = new ArrayList<>();
    private MediaPlayer mPlayer;
    private int mCurrentSong = -1;
    private Context mContext;
    private Handler mHandler = new Handler();

    // next,previous bài hát hoặc trọn 1 bài hát bất kỳ.
    private int mInitially = 0;
    // khí bài hát đang chạy nó bị dừng.
    private int mStop = 3;
    private int mRandom=5;
    // về trang thái lúc ban đâu là next , previous bài hát.
    private int mStatus = mInitially;

    public MusicManager(Context context) {
        mContext = context;
        getAllSong();
        mPlayer = new MediaPlayer();
    }

    /** BachNN
     * @param mContext tham số để khởi tạo MusicManager.
     * @return một biến MusciManager duy nhất.
     */
    public static MusicManager getInstance(Context mContext) {
        if (INSTANCE == null) {
            INSTANCE = new MusicManager(mContext);
        }
        return INSTANCE;
    }

    public int getmStatus() {
        return mStatus;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    /** BachNN
     * hàm này để phát nhạc
     */
    public void onPlayMusic() {
        if (mStatus == mStop) {
            mPlayer.reset();
            mStatus = 0;
        }
        try {
            mPlayer.setDataSource(mSongs.get(mCurrentSong).getPath());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /** BachNN
     * quay lại bàn nhạc
     */
    public void onPreviousMusic() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        if (mCurrentSong == 0) {
            mCurrentSong = mSongs.size() - 1;
        } else mCurrentSong--;
        mPlayer.reset();
        onPlayMusic();
    }

    /** BachNN
     * next bài hát
     */
    public void onNextMusic() {
        mStatus= mInitially;
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        if (mCurrentSong == (mSongs.size() - 1)) {
            mCurrentSong = 0;
        } else mCurrentSong++;
        mPlayer.reset();
        onPlayMusic();
    }


    /** BachNN
     * @return lấy vị trị mà bài hát đang chạy
     */
    public int getmCurrentSong() {
        return mCurrentSong;

    }

    /** BachNN
     * @param mCurrentSong lấy bài hát của vi trị trên
     */
    public void setmCurrentSong(int mCurrentSong) {
        this.mCurrentSong = mCurrentSong;
    }

    /** BachNN
     * @param position vị tri mà người dụng chon để chay bài hát.
     */
    public void selectMusic(int position) {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        mPlayer.reset();
        mCurrentSong = position;
        onPlayMusic();
        mStatus= mInitially;
    }

    public void setRunning() {
        if (mPlayer.getCurrentPosition() == Integer.parseInt(getSongIsPlay().getDuration())) {
            onNextMusic();
        }
    }


    /** BachNN
     * @return true thi bài nhạc đang chay con false thi ngược lại
     */
    public boolean isMusicPlaying() {
        return (mPlayer.isPlaying()) ? true : false;
    }

    public void onPauseMusic() {
        mPlayer.pause();
    }

    public void onResumeMusic() {
        mPlayer.start();
    }

    /** BachNN
     * re lại bài hát
     */
    public void onResetMusic() {
        mPlayer.pause();
        mPlayer.reset();
    }

    /** BachNN
     * dưng lại bài hát
     */
    public void onStopMusic() {
        mPlayer.pause();
        mStatus = mStop;
    }

    /** BachNN
     * @param position vị tri người dung muôn tua đến
     *                 tua bài hát.
     */
    public void setSeekMusic(int position) {
        mPlayer.seekTo(position);
    }

    /** BachNN
     * get tất cả các bài hát từ Database trên thiết bị di động.
     */
    private void getAllSong() {
        String[] allColoumSong = new String[]{
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DURATION
        };
        // query các trường trên để lấy thông tin các bàn hát
        Cursor cursor = mContext.getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, allColoumSong, null, null, null, null);
        // chuyển con trỏ đến đâu bảng
        if (LogSetting.sLife){
            Log.d(MainActivity.TAG_MAIN, "cursor : "+ cursor);
        }
        cursor.moveToFirst();
        if (cursor != null) {
            while (!cursor.isAfterLast()) {
                // lấy các gia trị theo các trương của bảng
                int _ID = cursor.getColumnIndex(allColoumSong[0]);
                int DATA = cursor.getColumnIndex(allColoumSong[1]);
                int ARTIST = cursor.getColumnIndex(allColoumSong[2]);
                int TITLE = cursor.getColumnIndex(allColoumSong[3]);
                int DISPLAY_NAME = cursor.getColumnIndex(allColoumSong[4]);
                int DURATION = cursor.getColumnIndex(allColoumSong[5]);

                String id = cursor.getString(_ID);
                String data = cursor.getString(DATA);
                String author = cursor.getString(ARTIST);
                String title = cursor.getString(TITLE);
                String displayName = cursor.getString(DISPLAY_NAME);
                String duration = cursor.getString(DURATION);
                mSongs.add(new Song(id, data, author, title, displayName, duration));
                if (LogSetting.sLife){
                    Log.d(MainActivity.TAG_MAIN, mSongs.size()+ "");
                }
                cursor.moveToNext();
            }
            cursor.close();
        }

    }

    public List<Song> getmSongs() {
        return mSongs;
    }

    public Song getSinpleSong(int position) {
        return mSongs.get(position);
    }

    public Song getSongIsPlay() {
        try{
            return mSongs.get(mCurrentSong);
        }catch (Exception e){

        }
        return null;
    }

    /**BachNN
     * @return get vị trị mà bài hát đang chay trong bài hát.
     */
    public int getTimeCurrents() {
        return mPlayer.getCurrentPosition();
    }

    public boolean getCurrentBegin(){
//        if(mCurrentSong==-1) return true;
//        else return false;
        return (mCurrentSong == -1) ? true :false;
    }

}
