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

public class MediaPlaybackFragment extends Fragment implements View.OnClickListener {

    private static final String KEY_MEDIA_FRAGMENT = "com.example.myapplication.fragment.musicManager";
    private static MediaPlaybackFragment sMediaPlaybackFragment;

    private ImageView mMusicImageView;
    private ImageView mAvatarImageView;
    private ImageView mLikeIcon;
    private ImageView mPreviousIcon;
    private ImageView mPlayIcon;
    private ImageView mNextIcon;
    private ImageView mDislikeIcon;
    private ImageView mMoreIcon;
    private ImageView mListMusicIcon;
    private ImageView mRePeatIcon;
    private ImageView mShuffleIcon;
    private TextView mTimeTextView;
    private TextView mTotalTimeTextView;
    private TextView mTitelTextView;
    private TextView mAuthorTextView;
    private SeekBar mSeekBar;
    private MusicManager mMusicManager;
    private IMediaPlayFragmentListenner mMediaListenner;
    private MainActivity mActivity;

    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setUI();
            int index = (Integer.parseInt(mMusicManager.getSongIsPlay().getDuration()));
            if (mSeekBar.getProgress() == index) {
                mMusicManager.onNextMusic();
                setSeekBar();
                setTile(mMusicManager.getSongIsPlay());
                mMediaListenner.onSeekBar();
            }
            mHandler.postDelayed(this, 300);
        }
    };

    public static MediaPlaybackFragment getInstance(MusicManager m) {
        if (sMediaPlaybackFragment == null) {
            sMediaPlaybackFragment = new MediaPlaybackFragment();
        }
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(KEY_MEDIA_FRAGMENT, m);
        sMediaPlaybackFragment.setArguments(mBundle);
        return sMediaPlaybackFragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mMediaListenner = (IMediaPlayFragmentListenner) context;
            mActivity = (MainActivity) context;
        } else throw new ClassCastException("onAttach Methods have problem !");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.media_play_back_fragment, container, false);
        mMusicImageView = view.findViewById(R.id.img_music);
        mAvatarImageView = view.findViewById(R.id.icon_avata);
        mMoreIcon = view.findViewById(R.id.icon_more);
        mListMusicIcon = view.findViewById(R.id.icon_queue);
        mLikeIcon = view.findViewById(R.id.iconLike);
        mPreviousIcon = view.findViewById(R.id.iconPrevious);
        mPlayIcon = view.findViewById(R.id.iconPlay);
        mNextIcon = view.findViewById(R.id.iconNext);
        mDislikeIcon = view.findViewById(R.id.iconDislike);
        mRePeatIcon = view.findViewById(R.id.icon_repeat);
        mShuffleIcon = view.findViewById(R.id.icon_shuffle);
        mAuthorTextView = view.findViewById(R.id.txtAuthor);
        mTitelTextView = view.findViewById(R.id.txtTitle);
        mTimeTextView = view.findViewById(R.id.txt_startTime);
        mTotalTimeTextView = view.findViewById(R.id.txt_totalTime);
        mSeekBar = view.findViewById(R.id.seebar_ok);

        mMoreIcon.setOnClickListener(this);
        mListMusicIcon.setOnClickListener(this);
        mLikeIcon.setOnClickListener(this);
        mPreviousIcon.setOnClickListener(this);
        mPlayIcon.setOnClickListener(this);
        mNextIcon.setOnClickListener(this);
        mDislikeIcon.setOnClickListener(this);
        mRePeatIcon.setOnClickListener(this);
        mShuffleIcon.setOnClickListener(this);
        if (!mActivity.isVertical()) mMusicImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        mMusicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    if (mMusicManager != null) {
                        seekBar.setProgress(mMusicManager.getTimeCurrents());
                        mMusicManager.setSeekMusic(i);
                        mTimeTextView.setText(getDuration(i + ""));
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
        if (savedInstanceState != null) {
            String str = savedInstanceState.getString("bachdz");
            Log.d("bachdz", "Fragment " + str);
        }
        if (getArguments() != null) {
            mMusicManager = (MusicManager) getArguments().getSerializable(KEY_MEDIA_FRAGMENT);
            setTile(mMusicManager.getSongIsPlay());
            setSeekBar();
            setStatusIcon(mMusicManager.isMusicPlaying());
        }
    }

    public void setmMusicManager(MusicManager manager) {
        mMusicManager = manager;
        setSeekBar();
        setTile(mMusicManager.getSongIsPlay());
        setStatusIcon(mMusicManager.isMusicPlaying());
    }

    public void setStatusIcon(boolean musicPlaying) {
        if (musicPlaying) {
            mPlayIcon.setImageResource(R.drawable.custom_play_pause);
        } else {
            mPlayIcon.setImageResource(R.drawable.costom_play);
        }
    }

    private void setSeekBar() {
        mSeekBar.setMax(Integer.parseInt(mMusicManager.getSongIsPlay().getDuration()));
        mHandler.postDelayed(runnable, 300);
    }

    //
    public void setTile(Song song) {
        mAuthorTextView.setText(song.getAuthor());
        mTitelTextView.setText(song.getTitle());
    }

    // Set Seekbar
    private String getDuration(String time) {
        Long total = Long.parseLong(time);
        int minutes = (int) ((total / 1000) / 60);
        int second = (int) ((total / 1000) % 60);
        return (minutes < 10 ? "0" + minutes : minutes + "") + ":" + (second < 10 ? "0" + second : second + "");
    }

    private void setUI() {
        if (mMusicManager != null) {
            String totalT = getDuration(mMusicManager.getSongIsPlay().getDuration());
            mTotalTimeTextView.setText(totalT);
            String realTime = getDuration(mMusicManager.getTimeCurrents() + "");
            mTimeTextView.setText(realTime);
            mSeekBar.setProgress(mMusicManager.getTimeCurrents());
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
                mMediaListenner.onLike();
                break;
            case R.id.iconPrevious:
                mMusicManager.onPreviousMusic();
                setTile(mMusicManager.getSongIsPlay());
                setSeekBar();
                mMediaListenner.onPrevious();
                break;
            case R.id.iconPlay:
                mMediaListenner.onPlay();
                if (mMusicManager.isMusicPlaying()) {
                    mMusicManager.onStopMusic();
                    mPlayIcon.setImageResource(R.drawable.costom_play);
                    if (mMusicManager.getmStatus() == 0) {
                        mMusicManager.onPlayMusic();
                        mMusicManager.setmStatus(1);
                    }
                } else {
                    mMusicManager.onResumeMusic();
                    if (mMusicManager.getmStatus() == 0) {
                        mMusicManager.onPlayMusic();
                        mMusicManager.setmStatus(1);
                    }
                    mPlayIcon.setImageResource(R.drawable.custom_play_pause);
                }
                break;
            case R.id.iconNext:
                mMusicManager.onNextMusic();
                setTile(mMusicManager.getSongIsPlay());
                setSeekBar();
                mMediaListenner.onNext();
                break;
            case R.id.iconDislike:
                mMediaListenner.onDisLike();
                break;
            case R.id.icon_shuffle:
                getActivity().onBackPressed();
                break;
        }
    }

    public interface IMediaPlayFragmentListenner {

        void onLike();

        void onPrevious();

        void onPlay();

        void onNext();

        void onDisLike();

        void onSeekBar();
    }

}
