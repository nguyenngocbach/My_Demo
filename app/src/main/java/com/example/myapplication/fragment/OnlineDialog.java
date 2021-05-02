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

public class OnlineDialog extends DialogFragment {
    private TextView txtTitle;
    private TextView txtLike;
    private TextView txtDislike;
    private TextView txtDownload;
    private BaseSongListFragment mBaseSongListFragment;
    private Song mSong;

    public OnlineDialog(BaseSongListFragment baseSongListFragment, Song song){
        mBaseSongListFragment= baseSongListFragment;
        mSong = song;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_dialog,container,false);
        txtTitle = view.findViewById(R.id.txt_dialog);
        txtLike = view.findViewById(R.id.txt_like);
        txtDislike = view.findViewById(R.id.txt_dislike);
        txtDownload = view.findViewById(R.id.txt_download_music);
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

        txtDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaseSongListFragment.onDownloadMusic(mSong);
            }
        });
        return view;
    }
}
