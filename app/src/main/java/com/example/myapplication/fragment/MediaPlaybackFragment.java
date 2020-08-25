package com.example.myapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

public class MediaPlaybackFragment extends Fragment {

    private ImageView imgMusic, imgAvatar;
    private ImageView iconLike, iconPrevious, iconPlay, iconNext, iconDislike, iconMore, iconListMusic, iconRePeat, iconShuffle;
    private TextView txtTime, txtTotalTime, txtTitel, txtAuthor;
    private SeekBar seekBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.media_play_back_fragment,container,false);
        imgMusic = view.findViewById(R.id.img_music);
        imgAvatar = view.findViewById(R.id.icon_avata);
        iconMore = view.findViewById(R.id.icon_more);
        iconListMusic = view.findViewById(R.id.icon_queue);
        iconLike = view.findViewById(R.id.iconLike);
        iconPrevious = view.findViewById(R.id.iconPrevious);
        iconPlay = view.findViewById(R.id.iconPlay);
        iconNext = view.findViewById(R.id.iconNext);
        iconDislike = view.findViewById(R.id.iconDislike);
        iconRePeat = view.findViewById(R.id.icon_repeat);
        iconShuffle = view.findViewById(R.id.icon_shuffle);
        txtAuthor = view.findViewById(R.id.txtAuthor);
        txtTitel = view.findViewById(R.id.txtTitle);
        txtTime = view.findViewById(R.id.txt_startTime);
        txtTotalTime = view.findViewById(R.id.txt_totalTime);
        seekBar = view.findViewById(R.id.seebar_ok);
        return view;
    }
}
