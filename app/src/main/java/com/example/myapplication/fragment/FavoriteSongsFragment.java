package com.example.myapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.AllSongAdapter;
import com.example.myapplication.database.DataManager;
import com.example.myapplication.listenner.IMusicListenner;

public class FavoriteSongsFragment extends AllSongFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

//        View view= inflater.inflate(R.layout.favourite_song,container,false);
//        return view;
        View view = inflater.inflate(R.layout.favourite_song, container, false);
        mMusicRecyclerView = view.findViewById(R.id.recycler_song);
        final LinearLayoutManager managerLayout = new LinearLayoutManager(getContext());
        managerLayout.setOrientation(RecyclerView.VERTICAL);
        mMusicRecyclerView.setLayoutManager(managerLayout);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
        mAllSongListener = (AllSongFragmentListenner)  getActivity();
        mMusicManager = mMainActivity.getMusicManager();
        setData(new DataManager(mMainActivity).getAllMusicFvourite());
    }
}
