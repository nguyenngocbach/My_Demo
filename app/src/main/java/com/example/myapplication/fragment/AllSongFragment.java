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

import com.example.myapplication.MainActivity;
import com.example.myapplication.Model.Song;
import com.example.myapplication.R;
import com.example.myapplication.Service.MusicManager;
import com.example.myapplication.adapter.AllSongAdapter;
import com.example.myapplication.listenner.IMusicListenner;

import java.util.ArrayList;
import java.util.List;

public class AllSongFragment extends Fragment {

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mMainActivity = (MainActivity) context;
            mAllSongListenner = (AllSongFragmentListenner) context;
            mMusicManager = mMainActivity.getmMusicManager();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_song_fragment, container, false);
        mMusicRecyclerView = view.findViewById(R.id.recycler_song);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        mMusicRecyclerView.setLayoutManager (manager) ;
        mAdapter = new AllSongAdapter (getContext(), mSongs, (IMusicListenner) getActivity()) ;
        mMusicRecyclerView.setAdapter (mAdapter) ;
        mMainActivity = (MainActivity) getContext();
        mMusicImageView = view.findViewById (R.id.avatar) ;
        mPlayImageView = view.findViewById (R.id.icon_play_music) ;
        mTitleTextView = view.findViewById (R.id.nameMusic) ;
        mAuthorTextView = view.findViewById (R.id.nameAirsts) ;
        mItemMusic = view.findViewById (R.id.linearLayout) ;

        mItemMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAllSongListenner.show();
            }
        });

        mPlayImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("Ngoc", musicManager.isMusicPlaying()+"");
                if (mMusicManager.isMusicPlaying()) {
                    mMusicManager.onStopMusic();
                    mPlayImageView.setImageResource(R.drawable.ic_baseline_play_arrow);
                } else {
                    mMusicManager.onResumeMusic();
                    mPlayImageView.setImageResource(R.drawable.ic_pause_24);
                    if (mMusicManager.getmStatus() == 0) {
                        mMusicManager.onPlayMusic();
                        mMusicManager.setmStatus(1);
                    }
                }
            }
        });
        return view;
    }

    public void setData(List<Song> s) {
        if (mSongs != null) mSongs.clear();
        mSongs.addAll(s);
        Log.d("bachdz", "All Song " + s.size() + mSongs.size());
        mAdapter.setmCerrentSong(mMusicManager.getmCurrentSong());
        mAdapter.notifyDataSetChanged();
    }

    public void isPlayMusic(boolean musicRunning) {
        if (musicRunning) mPlayImageView.setImageResource(R.drawable.ic_pause_24);
        else mPlayImageView.setImageResource(R.drawable.ic_baseline_play_arrow);
    }

    public void setSongManager(MusicManager musicManager) {
        this.mMusicManager = musicManager;
    }

    public void setVisible() {
        mItemMusic.setVisibility(View.GONE);
    }

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

    public void setTitle(Song song) {
        mAuthorTextView.setText(song.getAuthor());
        mTitleTextView.setText(song.getTitle());
    }

    public interface AllSongFragmentListenner {
        void show();
    }
}
