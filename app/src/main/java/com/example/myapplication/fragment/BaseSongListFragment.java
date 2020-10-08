package com.example.myapplication.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
    public static final int POSITION_MUSIC = -1;
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
    private boolean mIsVertical;

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mLocalMusic = (MusicService.LocalMusic) iBinder;
            mMusicService = mLocalMusic.getInstanceService();
            if (mIsVertical && mMusicService.getCurrentSong() != -1) {
                setVisibleDisPlay();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mLocalMusic = null;
        }
    };

    public void setMusicService(MusicService mMusicService) {
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
        mMusicService = mainActivity.getMusicService();
        mDatabaseManager = mainActivity.getDatabase();
        mIsVertical = mainActivity.mIsVertical;
        super.onAttach(context);
    }

    /**
     * BachNN
     * hàm này mục đích là restore dữ liệu khi back từ MediaPlaybackFragment.
     */
    @Override
    public void onStart() {
        super.onStart();
        if (mMusicService == null) return;
        if (mMusicService.getCurrentSong() != -1) {
            setSelection(mMusicService.getCurrentSong());
            setImageMusic();
        }
        //BachNN : lần đâu vài chưa set title vị trị.
        if (mMusicService.getCurrentSong() == -1) return;
        setData(mMusicService.getSongs());
        setTitle(mMusicService.getSongIsPlay());
        setButtonIconPlayMusic(mMusicService.isMusicPlaying());
        if (mIsVertical && mMusicService.getCurrentSong() != -1) {
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
        mSongs = new ArrayList<>();
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
                mAllSongListener.showMediaPlaybackFragment();
            }
        });

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
                    if (mMusicService.getStatus() == 0) {
                        mMusicService.onPlayMusic();
                        mMusicService.setStatus(1);
                    }
                }
                mAllSongListener.setIconNotification();
            }
        });

        return view;
    }

    public void setData(List<Song> songList) {
        mSongs.clear();
        mSongs.addAll(songList);
        // BachNN : set lai vị trị bài hát đang chay cho List Song
        if (mLocalMusic != null) {
            mAdapter.setCurrentSong(mMusicService.getCurrentSong());
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * BachNN
     * hàm để set anh cho từng bài hát
     */
    public void setImageMusic() {
        if (mMusicService.getCurrentSong() != -1) {
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
    //Thanhnch todo ten khong dung nghia oK
    public void setButtonIconPlayMusic(boolean musicRunning) {
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
     * @param currentMusic vị trị mà người dụng click vào ban hát
     *                     nó sẽ sưa lại giao diên củâ AllSongFragment
     */
    public void setSelection(int currentMusic) {
        mMusicService.setCurrentSong(currentMusic);
        if (mSongs != null) {
            mSongs.clear();
        }
        mSongs.addAll(mMusicService.getSongs());
        mAdapter.setCurrentSong(currentMusic);
        setTitle(mMusicService.getSongIsPlay());
        mAdapter.notifyDataSetChanged();
        mMusicService.setChangeNotification();
    }

    /**
     * BachNN
     * set lại toàn bị các view trên Fragment này
     */
    public void setUIAllView() {
        setData(mMusicService.getSongs());
        setTitle(mMusicService.getSongIsPlay());
        setButtonIconPlayMusic(mMusicService.isMusicPlaying());
        setImageMusic();
    }

    /**
     * BachNN
     *
     * @param song
     */
    public void setTitle(Song song) {
        if (mMusicService.getCurrentSong() != -1) {
            mAuthorTextView.setText(song.getAuthor());
            mTitleTextView.setText(song.getTitle());
        }
    }

    /**
     * BachNN
     *
     * @param i vị trị mà người dụng chọn.
     *          hàm này có sẽ chạy bài hát vưa đươc chọn vào set lại vị trị và tiêu đề bài hát.
     */
    @Override
    public void selectMusic(int i) {
        setSelection(i);
        setButtonIconPlayMusic(true);
        setImageMusic();
        if (mIsVertical) {
            //BachNN nếu chưa xuất hiện thanh Tiêu đề thì hiện nó nên.
            setVisibleDisPlay();
        }
        //BachNN : nếu là màn hinh ngang thì set lại giai trị đúng cho MediaPlaybackFragment.
        if (!mIsVertical) {
            MediaPlaybackFragment mediaPlaybackFragment= (MediaPlaybackFragment)
                    getActivity().getSupportFragmentManager().findFragmentById(R.id.music_Player);
            mediaPlaybackFragment.setUIMusic();
        }
        // BachNN : kiểm tra xem bài hát nó đang chay hay ko chay.
        if (mLocalMusic != null) {
            if (mMusicService.isMusicPlaying()) {
                mMusicService.onResetMusic();
            }
            mMusicService.setCurrentSong(i);
            mMusicService.onPlayMusic();
        }
        mMusicService.setChangeNotification();
    }

    /**
     * BachNN
     *
     * @param i    vị trị bài hát người dùng chọn.
     * @param view mà nguời dùng click.
     *             hàm này để set các bài hát yêu thich của người dùng.
     *             nêu chọn thích thi thêm vào CSDL.
     *             ngước lại là ko thịch.
     */
    @Override
    public void selectMoreMusic(final int i, View view) {
        PopupMenu mPopupMen = new PopupMenu(getActivity(), view);
        mPopupMen.getMenuInflater().inflate(R.menu.more_menu, mPopupMen.getMenu());
        //BachNN : set mau text cho một Item.
        MenuItem menuItem = mPopupMen.getMenu().getItem(0);
        SpannableString text = new SpannableString("Like");
        text.setSpan(new ForegroundColorSpan(Color.RED), 0, text.length(), 0);
        menuItem.setTitle(text);

        mPopupMen.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.like_music:
                        mDatabaseManager.addMusicFavourite(mMusicService.getSongs().get(i));
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

    /**
     * BachNN
     *
     * @return một List<song> Music.
     * hàm này nó lấy một List nhạc
     */
    public List<Song> getAllSong() {
        List<Song> song = new ArrayList<>();
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
        //Bkav Thanhnch: todo can check null, neu null thi bao loi hoac log r --ok
        if (cursor==null){
            if (LogSetting.IS_DEBUG){
                Log.d(MainActivity.TAG_MAIN,"loi vi cursor = null");
            }
            return null;
        }

        cursor.moveToFirst();
        if (cursor != null) {
            while (!cursor.isAfterLast()) {
                song.add(new Song(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return song;

    }

    /**
     * BachNN
     * hàm này dùng đẻ ghi đề ở lớp con của nó đó là AllSongFragment.
     */
    //Bkav Thanhnch: thieu comment -ok
    public void loadData() {
    }

    /**
     * BachNN
     * interface này dung để callback về MainActivity.
     */
    public interface IAllSongFragmentListener {
        //BachNN : để hiện MediaPlayerFragemnt nên.
        void showMediaPlaybackFragment();

        //BachNN : set lại icon cho notification chay nhạc hay dưng.
        void setIconNotification();
    }

    /**
     * BachNN
     * tạo một luồng để lấy một list bài hát về.
     */
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
