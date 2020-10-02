package com.example.myapplication.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.MainActivity;
import com.example.myapplication.database.DataManager;
import com.example.myapplication.model.Song;
import com.example.myapplication.R;
import com.example.myapplication.Service.MusicService;
import com.example.myapplication.adapter.AllSongAdapter;
import com.example.myapplication.listenner.IMusicListenner;
import com.example.myapplication.util.Util;
import com.example.myapplication.util.LogSetting;

import java.util.ArrayList;
import java.util.List;

public class BaseSongListFragment extends Fragment implements IMusicListenner {
    protected MusicService.LocalMusic mLocalMusic;
    protected AllSongFragment.IAllSongFragmentListener mAllSongListener;
    protected MainActivity mMainActivity;
    protected DataManager mDatabaseManager;
    protected MusicService mMusicService;
    protected RecyclerView mMusicRecyclerView;
    protected List<Song> mSongs = new ArrayList<>();
    protected AllSongAdapter mAdapter;
    protected ImageView mPlayImageView;
    protected ImageView mMusicImageView;
    protected TextView mTitleTextView;
    protected TextView mAuthorTextView;
    protected LinearLayout mItemMusic;
    protected int mCurrentMusic;
    private boolean mIsVertical;

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mLocalMusic = (MusicService.LocalMusic) iBinder;
            mMusicService = mLocalMusic.getInstanceService();
            //setImageMusic();
            if (mIsVertical && mMusicService.getmCurrentSong() != -1) {
                setVisibleDisPlay();
            } else {
                //setVisible();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mLocalMusic = null;
        }
    };

    public void setmMusicService(MusicService mMusicService) {
        this.mMusicService = mMusicService;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        if (context instanceof MainActivity) {
            mAllSongListener = (AllSongFragment.IAllSongFragmentListener) context;
        }
        MainActivity mainActivity = (MainActivity) context;
        if (mainActivity.isMyServiceRunning(MusicService.class)) {
            Intent intent = new Intent(getContext(), MusicService.class);
            context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
        mMusicService = mainActivity.getmMusicService();
        mDatabaseManager = mainActivity.getDatabase();
        mIsVertical = mainActivity.isVertical;
        super.onAttach(context);
    }

    public void setCurrentMusic(int currentMusic) {
        mCurrentMusic = currentMusic;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMusicService == null) return;
        if (mMusicService.getmCurrentSong() != -1) {
            setSelection(mMusicService.getmCurrentSong());
            setImageMusic();
        }
        setData(mMusicService.getmSongs());
        setTitle(mMusicService.getSongIsPlay());
        isPlayMusic(mMusicService.isMusicPlaying());
        if (mIsVertical && mMusicService.getmCurrentSong() != -1) {
            setVisibleDisPlay();
        } else {
            setVisible();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (LogSetting.IS_DEBUG) {
            Log.d(MainActivity.TAG_MAIN, "Fragment Stop");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_song_fragment, container, false);
        mMusicRecyclerView = view.findViewById(R.id.recycler_song);
        final LinearLayoutManager managerLayout = new LinearLayoutManager(getContext());
        managerLayout.setOrientation(RecyclerView.VERTICAL);
        mMusicRecyclerView.setLayoutManager(managerLayout);
        mAdapter = new AllSongAdapter(getContext(), mSongs, this);
        mMusicRecyclerView.setAdapter(mAdapter);
        mMainActivity = (MainActivity) getContext();
        mMusicImageView = view.findViewById(R.id.avatar);
        mPlayImageView = view.findViewById(R.id.icon_play_music);
        mTitleTextView = view.findViewById(R.id.name_Music);
        mAuthorTextView = view.findViewById(R.id.name_Airsts);
        mItemMusic = view.findViewById(R.id.linear_Layout);
        mItemMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAllSongListener.show();
            }
        });

//        if (Configuration.ORIENTATION_PORTRAIT==1 && mMusicService.getmCurrentSong()!=-1){
//                mItemMusic.setVisibility(View.VISIBLE);
//        }else {
//            mItemMusic.setVisibility(View.GONE);
//        }

        mPlayImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMusicService.isMusicPlaying()) {
                    mMusicService.onStopMusic();
                    mPlayImageView.setImageResource(R.drawable.ic_play_black);
                    mMusicService.setChangeNotification();
                } else {
                    mMusicService.onResumeMusic();
                    mMusicService.setChangeNotification();
                    mPlayImageView.setImageResource(R.drawable.ic_pause_black_large);
                    if (mMusicService.getmStatus() == 0) {
                        mMusicService.onPlayMusic();
                        mMusicService.setmStatus(1);
                    }
                }
                mAllSongListener.setIconNotification();
            }
        });

        return view;
    }

    public void setData(List<Song> s) {
        if (mSongs != null) mSongs.clear();
        mSongs.addAll(s);
        // BachNN : set lai vị trị bài hát đang chay cho List Song
        if (mLocalMusic != null) {
            mAdapter.setCurrentSong(mMusicService.getmCurrentSong());
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * BachNN
     * hàm để set anh cho từng bài hát
     */
    public void setImageMusic() {
        if (mMusicService.getmCurrentSong() != -1) {
            byte[] sourceImage = Util.getByteImageSong(mMusicService.getSongIsPlay().getPath());
            Glide.with(getContext())
                    .load(sourceImage)
                    .placeholder(R.drawable.anh_ngoc_trinh)
                    .into(mMusicImageView);
        }
    }

    /**
     * BachNN
     *
     * @param musicRunning kiểm tra bài hát đang chay hay đang dung để
     *                     sét Icon của bài nhạc.
     */
    public void isPlayMusic(boolean musicRunning) {
        if (musicRunning) {
            mPlayImageView.setImageResource(R.drawable.ic_pause_black_large);
        } else {
            mPlayImageView.setImageResource(R.drawable.ic_play_black);
        }
    }

    /**
     * BachNN
     * ẩn một ViewGround trong AllSongFragment khi nó quay ngang.
     */
    public void setVisible() {
        if (LogSetting.IS_DEBUG) {
            Log.d(MainActivity.TAG_MAIN, mItemMusic + "  hllll");
        }
        mItemMusic.setVisibility(View.GONE);
    }

    public void setVisibleDisPlay() {
        mItemMusic.setVisibility(View.VISIBLE);
    }

    /**
     * BachNN
     *
     * @param cerrentMusic vị trị mà người dụng click vào ban hát
     *                     nó sẽ sưa lại giao diên củâ AllSongFragment
     */
    public void setSelection(int cerrentMusic) {
        mMusicService.setmCurrentSong(cerrentMusic);
        if (mSongs != null) {
            mSongs.clear();
        }
        mSongs.addAll(mMusicService.getmSongs());
        mAdapter.setCurrentSong(cerrentMusic);
        setTitle(mMusicService.getSongIsPlay());
        mAdapter.notifyDataSetChanged();
        mMusicService.setChangeNotification();
    }

    public void setUIAllView() {
        setData(mMusicService.getmSongs());
        setTitle(mMusicService.getSongIsPlay());
        isPlayMusic(mMusicService.isMusicPlaying());
        setImageMusic();
    }

    /**
     * BachNN
     *
     * @param song
     */
    public void setTitle(Song song) {
        if (mMusicService.getmCurrentSong() != -1) {
            mAuthorTextView.setText(song.getAuthor());
            mTitleTextView.setText(song.getTitle());
        }
    }

    @Override
    public void selectMusic(int i) {
        setSelection(i);
        isPlayMusic(true);
        setImageMusic();
        if (mIsVertical) {
            setVisibleDisPlay();
        }
        if (!mIsVertical) {
            mMusicService.setChangeUIMediaFragment();
        }
        if (mLocalMusic != null) {
            if (mMusicService.isMusicPlaying()) {
                mMusicService.onResetMusic();
            }
            mMusicService.setmCurrentSong(i);
            mMusicService.onPlayMusic();
        }
        mMusicService.setChangeNotification();
    }

    @Override
    public void selectMoreMusic(final int i, View view) {
        PopupMenu mPopupMen = new PopupMenu(getActivity(), view);
        mPopupMen.getMenuInflater().inflate(R.menu.more_menu, mPopupMen.getMenu());

        mPopupMen.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.like_music:
                        mDatabaseManager.addMusicFavourite(mSongs.get(i));
                        break;
                    case R.id.dislike_music:
                        mDatabaseManager.removeMusicFavourite(Integer.parseInt(mSongs.get(i).getId()));
                        break;
                }
                return true;
            }
        });
        mPopupMen.show();
    }

    private List<Song> getAllSong() {
        mSongs = new ArrayList<>();
        String[] allColoumSong = new String[]{
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DURATION
        };
        //BachNN : query các trường trên để lấy thông tin các bàn hát
        Cursor cursor = getContext().getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, allColoumSong, null, null, null, null);
        //BachNN : chuyển con trỏ đến đâu bảng
        if (LogSetting.IS_DEBUG) {
            Log.d(MainActivity.TAG_MAIN, "cursor : " + cursor);
        }
        cursor.moveToFirst();
        if (cursor != null) {
            while (!cursor.isAfterLast()) {
                //BachNN : lấy các gia trị theo các trương của bảng
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
                if (LogSetting.IS_DEBUG) {
                    Log.d(MainActivity.TAG_MAIN, mSongs.size() + "");
                }
                cursor.moveToNext();
            }
            cursor.close();


        }
        return mSongs;

    }

    public interface IAllSongFragmentListener {
        //BachNN : để hiện MediaPlayerFragemnt nên.
        void show();

        //BachNN : set lại icon cho notification chay nhạc hay dưng.
        void setIconNotification();
    }

    class LoadAllMusic extends AsyncTask<Void, Void, List<Song>> {
        @Override
        protected List<Song> doInBackground(Void... voids) {

            return getAllSong();
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            setData(songs);
            super.onPostExecute(songs);
        }
    }

}
