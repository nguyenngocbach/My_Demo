package com.example.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Model.Song;
import com.example.myapplication.R;
import com.example.myapplication.Service.MusicManager;
import com.example.myapplication.adapter.AllSongAdapter;
import com.example.myapplication.listenner.IMusicListenner;
import com.example.myapplication.unit.Coast;

import java.util.ArrayList;
import java.util.List;

/**
 * BachNN
 * Hiện thị một Danh Sach Music cho người dung thấy
 */
public class AllSongFragment extends BaseSongListFragment implements IMusicListenner{

    protected RecyclerView mMusicRecyclerView;
    protected List<Song> mSongs = new ArrayList<>();
    protected AllSongAdapter mAdapter;
    protected ImageView mPlayImageView;
    protected ImageView mMusicImageView;
    protected TextView mTitleTextView;
    protected TextView mAuthorTextView;
    protected LinearLayout mItemMusic;
//    protected AllSongFragmentListenner mAllSongListener;
//    protected MainActivity mMainActivity;
//    protected MusicManager mMusicManager;

    public AllSongFragment() {
    }

//    /**
//     * BachNN
//     * @param context là còn context của Activity chứa các Fragment này.
//     *                khới tạo một số biến như Interface ...
//     *                qua Context này.
//     */
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
//            mMainActivity = (MainActivity) getActivity();
//            mAllSongListener = (AllSongFragmentListenner)  getActivity();
//            mMusicManager = mMainActivity.getMusicManager();
            setData(mAllMusicProvider.getAllListSong());
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
        mTitleTextView = view.findViewById(R.id.nameMusic);
        mAuthorTextView = view.findViewById(R.id.nameAirsts);
        mItemMusic = view.findViewById(R.id.linearLayout);
        mItemMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAllSongListener.show();
            }
        });

        mPlayImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMusicManager.isMusicPlaying()) {
                    mMusicManager.onStopMusic();
                    mPlayImageView.setImageResource(R.drawable.ic_play_black);
                } else {
                    mMusicManager.onResumeMusic();
                    mPlayImageView.setImageResource(R.drawable.ic_pause_black_large);
                    if (mMusicManager.getmStatus() == 0) {
                        mMusicManager.onPlayMusic();
                        mMusicManager.setmStatus(1);
                    }
                }
                mAllSongListener.setIconNotification();
            }
        });

        return view;
    }

    /**
     * BachNN
     * @param s là một List<Song> để gián cho List<Song> của adapter.
     *          set lại vị trị của ban hát đang chạy
     *          reset lại adapter.
     */
    public void setData(List<Song> s) {
        if (mSongs != null) mSongs.clear();
        mSongs.addAll(s);
        // set lai vị trị bài hát đang chay cho List Song
        if (mMusicManager!=null) {
            mAdapter.setCurrentSong(mMusicManager.getmCurrentSong());
            //mCurrentMusic=
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * BachNN
     * hàm để set anh cho từng bài hát
     */
    public void setImageMusic() {
        if (mMusicManager.getmCurrentSong() != -1) {
            byte[] sourceImage = Coast.getByteImageSong(mMusicManager.getSongIsPlay().getPath());
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

//    public void setSongManager(MusicManager musicManager) {
//        this.mMusicManager = musicManager;
//    }

    /**
     * BachNN
     * ẩn một ViewGround trong AllSongFragment khi nó quay ngang.
     */
    public void setVisible() {
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
        mMusicManager.setmCurrentSong(cerrentMusic);
        if (mSongs != null) {
            mSongs.clear();
        }
        mSongs.addAll(mMusicManager.getmSongs());
        mAdapter.setCurrentSong(cerrentMusic);
        setTitle(mMusicManager.getSinpleSong(cerrentMusic));
        mAdapter.notifyDataSetChanged();
    }

    /**
     * BachNN
     *
     * @param song
     */
    public void setTitle(Song song) {
        if (mMusicManager.getmCurrentSong() != -1) {
            mAuthorTextView.setText(song.getAuthor());
            mTitleTextView.setText(song.getTitle());
        }
    }

    @Override
    public void selectMusic(int i) {
        setSelection(i);
        isPlayMusic(true);
        setImageMusic();
        setVisibleDisPlay();
        if (mMusicManager != null) {
            if (mMusicManager.isMusicPlaying()) {
                mMusicManager.onResetMusic();
            }
            mMusicManager.setmCurrentSong(i);
            mMusicManager.onPlayMusic();
        }
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
                        //new MainActivity.AddFavouriteMusic().execute(mMusicManager.getSinpleSong(i));
                        Toast.makeText(getContext(), "You like " + mMusicManager.getmSongs().get(i).getTitle(), Toast.LENGTH_SHORT).show();

                        break;
                    case R.id.dislike_music:
                        //new MainActivity.DeleteFavouriteMusic().execute(Integer.parseInt(mMusicManager.getSinpleSong(i).getId()));
                        Toast.makeText(getContext(), "You dislike " + mMusicManager.getmSongs().get(i).getTitle(), Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        mPopupMen.show();
    }

    public interface AllSongFragmentListenner {
        // để hiện MediaPlayerFragemnt nên.
        void show();

        // set lại icon cho notification chay nhạc hay dưng.
        void setIconNotification();
    }

}
