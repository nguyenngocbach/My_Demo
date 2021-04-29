package com.example.myapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.database.DataManager;
import com.example.myapplication.model.Song;
import com.example.myapplication.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainMusicFragment extends Fragment {
    private LinearLayout mMusicOnline;
    private LinearLayout mMusicOffline;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_music_fragment, container,false);
        mMusicOnline  = view.findViewById(R.id.view_music_online);
        mMusicOffline  = view.findViewById(R.id.view_music_offline);
        mMusicOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fragment Music online
            }
        });

        mMusicOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fragment Music offline
            }
        });
        downloadMusicServer();
        return view;
    }

    private void downloadMusicServer() {
        final DataManager manager = new DataManager(getContext());
        FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
        mFireStore.collection("MusicOnline")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String id = document.getId();
                        String data = (String) document.get(Util.DATA);
                        String artist = (String) document.get(Util.ARTIST);
                        String title = (String) document.get(Util.TITLE);
                        String displayName = (String) document.get(Util.DISPLAY_NAME);
                        manager.addMusicOnline(new Song(id,data,artist,title,displayName,"0"));
                    }
                }
            }
        });
    }
}
