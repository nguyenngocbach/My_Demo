package com.example.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.Model.Song;
import com.example.myapplication.R;
import com.example.myapplication.Service.MusicManager;

public class MediaPlaybackFragment extends Fragment implements View.OnClickListener{

    private static final String KEY_MEDIA_FRAGMENT = "com.example.myapplication.fragment.musicManager" ;
    private static MediaPlaybackFragment mediaPlaybackFragment;

    private ImageView imgMusic, imgAvatar;
    private ImageView iconLike, iconPrevious, iconPlay, iconNext, iconDislike, iconMore, iconListMusic, iconRePeat, iconShuffle;
    private TextView txtTime, txtTotalTime, txtTitel, txtAuthor;
    private SeekBar seekBar;

    private MusicManager musicManager;
    private MediaPlayFragmentListenner listenner;
    private MainActivity activity;

    private Handler handler = new Handler();
    private Runnable runnable= new Runnable() {
        @Override
        public void run() {
            setUI();
            int index=(Integer.parseInt(musicManager.getSongIsPlay().getDuration()));
            if (seekBar.getProgress()== index){
                musicManager.onNext();
                setSeekBar();
                setTile(musicManager.getSongIsPlay());
                listenner.onSeekBar();
            }
            handler.postDelayed(this,300);
        }
    };

    public static MediaPlaybackFragment getInstance(MusicManager m){
        if (mediaPlaybackFragment==null){
            mediaPlaybackFragment= new MediaPlaybackFragment();
        }
        Bundle bundle= new Bundle();
        bundle.putSerializable(KEY_MEDIA_FRAGMENT,m);
        mediaPlaybackFragment.setArguments(bundle);
        return mediaPlaybackFragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity){
            listenner= (MediaPlayFragmentListenner) context;
            activity= (MainActivity) context;
        }else throw  new ClassCastException("onAttach Methods have problem !");
    }

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

        iconMore.setOnClickListener(this);
        iconListMusic.setOnClickListener(this);
        iconLike.setOnClickListener(this);
        iconPrevious.setOnClickListener(this);
        iconPlay.setOnClickListener(this);
        iconNext.setOnClickListener(this);
        iconDislike.setOnClickListener(this);
        iconRePeat.setOnClickListener(this);
        iconShuffle.setOnClickListener(this);
        if (!activity.isVertical()) imgMusic.setScaleType(ImageView.ScaleType.FIT_CENTER);

        imgMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b){
                    if (musicManager!=null){
                        seekBar.setProgress(musicManager.getTimeCurrents());
                        musicManager.setSeek(i);
                        txtTime.setText(getDuration(i + ""));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState!=null){
            String str= savedInstanceState.getString("bachdz");
            Log.d("bachdz","Fragment "+ str);
        }
        if (getArguments()!=null){
            musicManager= (MusicManager) getArguments().getSerializable(KEY_MEDIA_FRAGMENT);
            setTile(musicManager.getSongIsPlay());
            setSeekBar();
            setStatusIcon(musicManager.isMusicPlaying());
        }
    }

    public void setMusicManager(MusicManager manager){
        musicManager=manager;
        setSeekBar();
        setTile(musicManager.getSongIsPlay());
        setStatusIcon(musicManager.isMusicPlaying());
    }

    public void setStatusIcon(boolean musicPlaying) {
        if (musicPlaying) {
            iconPlay.setImageResource(R.drawable.custom_play_pause);
        }
        else {
            iconPlay.setImageResource(R.drawable.costom_play);
        }
    }

    private void setSeekBar(){
        seekBar.setMax(Integer.parseInt(musicManager.getSongIsPlay().getDuration()));
        handler.postDelayed(runnable,300);
    }
    //
    public void setTile(Song song){
        txtAuthor.setText(song.getAuthor());
        txtTitel.setText(song.getTitle());
    }

    // Set Seekbar
    private String getDuration(String time) {
        Long total= Long.parseLong(time);
        int minutes= (int) ((total/1000)/60);
        int second= (int) ((total/1000)%60);
        return (minutes<10 ? "0"+minutes:minutes+"") + ":"+ (second<10 ? "0"+second: second+"");
    }

    private void setUI(){
        if (musicManager!=null){
            String totalT = getDuration(musicManager.getSongIsPlay().getDuration());
            txtTotalTime.setText(totalT);
            String realTime= getDuration(musicManager.getTimeCurrents()+"");
            txtTime.setText(realTime);
            seekBar.setProgress(musicManager.getTimeCurrents());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_more:
                break;
            case R.id.icon_queue:
                break;
            case R.id.iconLike:
                listenner.onLike();
                break;
            case R.id.iconPrevious:
                musicManager.onPrevious();
                setTile(musicManager.getSongIsPlay());
                setSeekBar();
                listenner.onPrevious();
                break;
            case R.id.iconPlay:
                if (musicManager.isMusicPlaying()){
                    musicManager.onStop();
                    iconPlay.setImageResource(R.drawable.costom_play);
                    if (musicManager.getStatus()==0){
                        musicManager.onPlay();
                        musicManager.setStatus(1);
                    }
                }
                else {
                    musicManager.onResumeMusic();
                    if (musicManager.getStatus()==0){
                        musicManager.onPlay();
                        musicManager.setStatus(1);
                    }
                    iconPlay.setImageResource(R.drawable.custom_play_pause);
                }
                break;
            case R.id.iconNext:
                musicManager.onNext();
                setTile(musicManager.getSongIsPlay());
                setSeekBar();
                listenner.onNext();
                break;
            case R.id.iconDislike:
                listenner.onDisLike();
                break;
            case R.id.icon_shuffle:
                getActivity().onBackPressed();
                break;
        }
    }

    public interface MediaPlayFragmentListenner {

        void onLike();

        void onPrevious();

        void onPlay();

        void onNext();

        void onDisLike();

        void onSeekBar();
    }

}
