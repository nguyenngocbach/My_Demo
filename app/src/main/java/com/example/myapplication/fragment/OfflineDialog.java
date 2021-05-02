package com.example.myapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.model.Song;

public class OfflineDialog extends DialogFragment {
    private TextView txtTitle;
    private TextView txtLike;
    private TextView txtDislike;
    private BaseSongListFragment mBaseSongListFragment;
    private Song mSong;

    public OfflineDialog(BaseSongListFragment baseSongListFragment, Song song){
        mBaseSongListFragment= baseSongListFragment;
        mSong = song;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.offline_diglog,container,false);
        txtTitle = view.findViewById(R.id.txt_dialog);
        txtLike = view.findViewById(R.id.txt_like);
        txtDislike = view.findViewById(R.id.txt_dislike);
        txtTitle.setText(mSong.getTitle());
        txtLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaseSongListFragment.onLike(mSong);
            }
        });

        txtDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaseSongListFragment.onDisLike(mSong);
            }
        });

        return view;
    }
}
