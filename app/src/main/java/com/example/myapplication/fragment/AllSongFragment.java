package com.example.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
 * Hiện thị một Danh Sach Music cho người dung thấy
 */
public class AllSongFragment extends Fragment {

    //private static AllSongFragment sInstance;
    private RecyclerView mMusicRecyclerView;
    private List<Song> mSongs = new ArrayList<>();
    private AllSongAdapter mAdapter;
    private ImageView mPlayImageView;
    private ImageView mMusicImageView;
    private TextView mTitleTextView;
    private TextView mAuthorTextView;
    private LinearLayout mItemMusic;
    private AllSongFragmentListenner mAllSongListenner;
    private MainActivity mMainActivity;
    private MusicManager mMusicManager;

    public AllSongFragment() {
    }

    // todo bo ham nay
    // ko lam dc goi anh thanh
//    public static AllSongFragment getInstance() {
//        if (sInstance == null) {
//            sInstance = new AllSongFragment();
//        }
//        return sInstance;
//    }

    /**
     * @param context là còn context của Activity chứa các Fragment này.
     *                khới tạo một số biến như Interface ...
     *                qua Context này.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mMainActivity = (MainActivity) context;
            mAllSongListenner = (AllSongFragmentListenner) context;
            mMusicManager = mMainActivity.getMusicManager();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_song_fragment, container, false);
        mMusicRecyclerView = view.findViewById(R.id.recycler_song);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        mMusicRecyclerView.setLayoutManager(manager);
        mAdapter = new AllSongAdapter(getContext(), mSongs, (IMusicListenner) getActivity());
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
                mAllSongListenner.show();
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
                mAllSongListenner.setIconNotification();
            }
        });
        return view;
    }

    /**
     * @param s là một List<Song> để gián cho List<Song> của adapter.
     *          set lại vị trị của ban hát đang chạy
     *          reset lại adapter.
     */
    public void setData(List<Song> s) {
        if (mSongs != null) mSongs.clear();
        mSongs.addAll(s);
        // set lai vị trị bài hát đang chay cho List Song
        mAdapter.setmCerrentSong(mMusicManager.getmCurrentSong());
        mAdapter.notifyDataSetChanged();
    }

    /**
     * hàm để set anh cho từng bài hát
     */
    public void setImageMusic() {
      //  if (mMusicManager.getmCurrentSong() != -1) {
            byte[] sourceImage = Coast.getByteImageSong(mMusicManager.getSongIsPlay().getPath());
            Glide.with(getContext())
                    .load(sourceImage)
                    .placeholder(R.drawable.anh_ngoc_trinh)
                    .into(mMusicImageView);
    //    }
    }

    /**
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

    public void setSongManager(MusicManager musicManager) {
        this.mMusicManager = musicManager;
    }

    /**
     * ẩn một ViewGround trong AllSongFragment khi nó quay ngang.
     */
    public void setVisible() {
        mItemMusic.setVisibility(View.GONE);
    }

    /**
     * @param cerrentMusic vị trị mà người dụng click vào ban hát
     *                     nó sẽ sưa lại giao diên củâ AllSongFragment
     */
    public void setSelection(int cerrentMusic) {
        mMusicManager.setmCurrentSong(cerrentMusic);
        if (mSongs != null) {
            mSongs.clear();
        }
        mSongs.addAll(mMusicManager.getmSongs());
        mAdapter.setmCerrentSong(cerrentMusic);
        setTitle(mMusicManager.getSinpleSong(cerrentMusic));
        mAdapter.notifyDataSetChanged();
    }

    /**
     * @param song
     */
    public void setTitle(Song song) {
        if (mMusicManager.getmCurrentSong() != -1) {
            mAuthorTextView.setText(song.getAuthor());
            mTitleTextView.setText(song.getTitle());
        }
    }

    public interface AllSongFragmentListenner {
        // để hiện MediaPlayerFragemnt nên.
        void show();

        // set lại icon cho notification chay nhạc hay dưng.
        void setIconNotification();
    }
}
