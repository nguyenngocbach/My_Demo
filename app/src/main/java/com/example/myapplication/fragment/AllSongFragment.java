package com.example.myapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Song;
import com.example.myapplication.R;
import com.example.myapplication.adapter.AllSongAdapter;

import java.util.ArrayList;
import java.util.List;

public class AllSongFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Song> mSongs= new ArrayList<>();
    private AllSongAdapter adapter;

    private ImageView imgPlay, imgImage;
    private TextView txtTitle, txtAuthor;
    private LinearLayout layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.all_song_fragment,container,false);

        imgImage=view.findViewById(R.id.avatar);
        imgPlay=view.findViewById(R.id.icon_play_music);
        txtTitle= view.findViewById(R.id.nameMusic);
        txtAuthor= view.findViewById(R.id.nameAirsts);
        layout= view.findViewById(R.id.linearLayout);

        return view;
    }
}
