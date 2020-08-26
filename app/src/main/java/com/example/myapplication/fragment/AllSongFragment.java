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
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class AllSongFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Song> mSongs = new ArrayList<>();
    private AllSongAdapter adapter;

    private ImageView imgPlay, imgImage;
    private TextView txtTitle, txtAuthor;
    private LinearLayout layout;

    private MainActivity mainActivity;
    private MusicManager musicManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        musicManager = mainActivity.getMusicManager();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_song_fragment, container, false);
        recyclerView= view.findViewById(R.id.recycler_song);
        //mSongs.add(new Song("1","1","1","1","1","1"));
        final LinearLayoutManager manager= new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter= new AllSongAdapter(getContext(),mSongs);
        recyclerView.setAdapter(adapter);
        mainActivity = (MainActivity) getContext();
        imgImage = view.findViewById(R.id.avatar);
        imgPlay = view.findViewById(R.id.icon_play_music);
        txtTitle = view.findViewById(R.id.nameMusic);
        txtAuthor = view.findViewById(R.id.nameAirsts);
        layout = view.findViewById(R.id.linearLayout);
//        recyclerView = view.findViewById(R.id.recycler_song);
//        final LinearLayoutManager manager= new LinearLayoutManager(getContext());
//        manager.setOrientation(RecyclerView.VERTICAL);
//        recyclerView.setLayoutManager(manager);
//        adapter = new AllSongAdapter(getContext(), mSongs);
//        recyclerView.setAdapter(adapter);
        return view;
    }

    public void setData(List<Song> s) {
        if (mSongs!=null) mSongs.clear();
        //mSongs=null;
        //mSongs=s;
        mSongs.addAll(s);
        Log.d("bachdz", "All Song " + s.size()+ mSongs.size());
        adapter.setCerrentSong(musicManager.getCurrentSong());
        adapter.notifyDataSetChanged();
    }

    public void setSongManager(MusicManager manager) {
        this.musicManager = manager;
//        mSongs.addAll(manager.getmSongs());
//        adapter.notifyDataSetChanged();
    }
}
