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
import com.example.myapplication.listenner.MusicListenner;

import java.util.ArrayList;
import java.util.List;

public class AllSongFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Song> mSongs = new ArrayList<>();
    private AllSongAdapter adapter;

    private ImageView imgPlay, imgImage;
    private TextView txtTitle, txtAuthor;
    private LinearLayout layout;
    private AllSongFragmentListenner listenner;

    private MainActivity mainActivity;
    private MusicManager musicManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
            listenner= (AllSongFragmentListenner) context;
            musicManager = mainActivity.getMusicManager();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_song_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_song);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new AllSongAdapter(getContext(), mSongs, (MusicListenner) getActivity());
        recyclerView.setAdapter(adapter);
        mainActivity = (MainActivity) getContext();
        imgImage = view.findViewById(R.id.avatar);
        imgPlay = view.findViewById(R.id.icon_play_music);
        txtTitle = view.findViewById(R.id.nameMusic);
        txtAuthor = view.findViewById(R.id.nameAirsts);
        layout = view.findViewById(R.id.linearLayout);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenner.show();
            }
        });

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("Ngoc", musicManager.isMusicPlaying()+"");
                if (musicManager.isMusicPlaying()){
                    musicManager.onStop();
                    imgPlay.setImageResource(R.drawable.ic_baseline_play_arrow);
                }
                else {
                    musicManager.onResumeMusic();
                    imgPlay.setImageResource(R.drawable.ic_pause_24);
                }
            }
        });
        return view;
    }

    public void setData(List<Song> s) {
        if (mSongs != null) mSongs.clear();
        mSongs.addAll(s);
        Log.d("bachdz", "All Song " + s.size() + mSongs.size());
        adapter.setCerrentSong(musicManager.getCurrentSong());
        adapter.notifyDataSetChanged();
    }

    public void isPlayMusic(boolean s){
        if (s) imgPlay.setImageResource(R.drawable.ic_pause_24);
        else imgPlay.setImageResource(R.drawable.ic_baseline_play_arrow);
    }

    public void setSongManager(MusicManager manager) {
        this.musicManager = manager;
    }

    public void setVisible(){
        layout.setVisibility(View.GONE);
    }

    public void setSelection(int cerrent) {
        musicManager.setCurrentSong(cerrent);
        if (mSongs != null) {
            mSongs.clear();
        }
        mSongs.addAll(musicManager.getmSongs());
        adapter.setCerrentSong(cerrent);
        setTitle(musicManager.getSinpleSong(cerrent));
        adapter.notifyDataSetChanged();
    }

    public void setTitle(Song song) {
        txtAuthor.setText(song.getAuthor());
        txtTitle.setText(song.getTitle());
    }

    public interface AllSongFragmentListenner {
        void show();
    }
}
