package com.example.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myapplication.MainActivity;
import com.example.myapplication.model.Song;
import com.example.myapplication.R;
import com.example.myapplication.Service.MusicService;
import com.example.myapplication.util.Util;

public class MediaPlaybackFragment extends Fragment implements View.OnClickListener {

    private int TIME_REPEAT = 300;
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
    private TextView mTitleTextView;
    private TextView mAuthorTextView;
    private SeekBar mSeekBar;
    private MusicService mMusicService;
    private IMediaPlayFragmentListenner mMediaListener;
    private MainActivity mActivity;

    private Handler mHandler = new Handler();
    /**
     * BachNN
     * để lặp lại set giời gian chạy thực cho bài hát.
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setUI();
            int index = (Integer.parseInt(mMusicService.getSongIsPlay().getDuration()));
            mSeekBar.setMax(index);
            if (mSeekBar.getProgress() == index) {
                setSeekBar();
                setTile(mMusicService.getSongIsPlay());
                mMediaListener.onSeekBar();
            }
            mHandler.postDelayed(this, TIME_REPEAT);
        }
    };

    public void setMusicService(MusicService musicService) {
        this.mMusicService = musicService;
    }

    /**
     * BachNN
     *
     * @param context của  Activity chưa các Fragment này.
     *                gián giá trị cho Ifnterface để callback
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mMediaListener = (IMediaPlayFragmentListenner) context;
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
        mLikeIcon = view.findViewById(R.id.icon_Like);
        mPreviousIcon = view.findViewById(R.id.icon_Previous);
        mPlayIcon = view.findViewById(R.id.icon_Play);
        mNextIcon = view.findViewById(R.id.icon_Next);
        mDislikeIcon = view.findViewById(R.id.icon_Dislike);
        mRePeatIcon = view.findViewById(R.id.icon_repeat);
        mShuffleIcon = view.findViewById(R.id.icon_shuffle);
        mAuthorTextView = view.findViewById(R.id.txt_Author);
        mTitleTextView = view.findViewById(R.id.txt_Title);
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

        //BachNN : set kiểu anh cho ảnh chính của MediaPlayFragment.
        if (!mActivity.isVertical()) mMusicImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    if (mMusicService != null) {
                        //BachNN : set thời gian thưc của ban hát ban đang chạy
                        seekBar.setProgress(mMusicService.getTimeCurrents());
                        mMusicService.setSeekMusic(i);
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

    /**
     * BachNN
     *
     * @param savedInstanceState lấy các dữ liện mà ta lựu trong Bundle.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mMusicService != null) {
            setTile(mMusicService.getSongIsPlay());
            setSeekBar();
            setStatusIcon(mMusicService.isMusicPlaying());
            setRepeat();
        }
        setImagePlayer();
    }

    private void setRepeat() {
        if (mMusicService.getStatueRepeat() == MusicService.NORMAL) {
            mShuffleIcon.setImageResource(R.drawable.ic_baseline_shuffle_24);
            mRePeatIcon.setImageResource(R.drawable.ic_baseline_repeat_24);
        } else if (mMusicService.getStatueRepeat() == MusicService.RANDOM) {
            mShuffleIcon.setImageResource(R.drawable.ic_baseline_shuffle);
        } else {
            mRePeatIcon.setImageResource(R.drawable.ic_baseline_repeat);
        }
    }

    /**
     * BachNN
     *
     * @param manager gán MusicManager cho bến ở trên.
     */
    public void setmMusicService(MusicService manager) {
        mMusicService = manager;
        //BachNN : set lai seekbar cua MediaPlayerFragment
        setSeekBar();
        setTile(mMusicService.getSongIsPlay());
        setStatusIcon(mMusicService.isMusicPlaying());
    }


    /**
     * BachNN
     * set lại toàn bộ các view trong Fragment này.
     */
    public void setUIMusic() {
        setSeekBar();
        setTile(mMusicService.getSongIsPlay());
        setStatusIcon(mMusicService.isMusicPlaying());
        setImagePlayer();
    }

    /**
     * BachNN
     *
     * @param musicPlaying if true thi set icon Play false thì ngược lại.
     */
    public void setStatusIcon(boolean musicPlaying) {
        if (musicPlaying) {
            mPlayIcon.setImageResource(R.drawable.custom_play_pause);
        } else {
            mPlayIcon.setImageResource(R.drawable.costom_play);
        }
    }

    /**
     * BachNN
     * set Max của SeekBar và chay đung giơi gian thực của bài hát.
     */
    private void setSeekBar() {
        mSeekBar.setMax(Integer.parseInt(mMusicService.getSongIsPlay().getDuration()));
        mHandler.postDelayed(runnable, TIME_REPEAT);
    }

    /**
     * @param song bài hát mà muosn set tiêu đề.
     *             set title va author cho bai hay
     */
    public void setTile(Song song) {
        mAuthorTextView.setText(song.getAuthor());
        mTitleTextView.setText(song.getTitle());
    }

    /**
     * BachNN
     *
     * @param time tổng thời gian của bàn hát
     * @return format về đinh dạnh phút / giây và tra về kiểu String
     */
    private String getDuration(String time) {
        Long total = Long.parseLong(time);
        int minutes = (int) ((total / 1000) / 60);
        int second = (int) ((total / 1000) % 60);
        return (minutes < 10 ? "0" + minutes : minutes + "") + ":" + (second < 10 ? "0" + second : second + "");
    }

    /**
     * BachNN
     * set Thời gian thực vào các textView và SeekBar sẽ chay theo thời gian thực
     */
    private void setUI() {
        if (mMusicService != null) {
            String totalT = getDuration(mMusicService.getSongIsPlay().getDuration());
            mTotalTimeTextView.setText(totalT);
            String realTime = getDuration(mMusicService.getTimeCurrents() + "");
            mTimeTextView.setText(realTime);
            mSeekBar.setProgress(mMusicService.getTimeCurrents());
        }
    }

    /**
     * BachNN
     * set Image cho 2 cái ảnh của MediaPlaybackFragment.
     */
    public void setImagePlayer() {
        if (mMusicService == null) return;
        byte[] sourceImage = Util.getByteImageSong(mMusicService.getSongIsPlay().getPath());
        Glide.with(getContext())
                .load(sourceImage)
                .into(mMusicImageView);
        Glide.with(getContext())
                .load(sourceImage)
                .into(mAvatarImageView);
    }

    /**
     * BachNN
     *
     * @param view overide các veiw và set OnclickListenner
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_more:
                displayPopupMenu(view);
                break;
            case R.id.icon_queue:
                getActivity().onBackPressed();
                break;
            case R.id.icon_Like:
                mMediaListener.onLike();
                mMusicService.setChangeNotification();
                break;
            case R.id.icon_Previous:
                mMusicService.onPreviousMusic();
                setTile(mMusicService.getSongIsPlay());
                setSeekBar();
                mMediaListener.onPrevious();
                setImagePlayer();
                mMusicService.setChangeNotification();
                break;
            case R.id.icon_Play:
                mMediaListener.onPlay();
                if (mMusicService.isMusicPlaying()) {
                    mMusicService.onStopMusic();
                    mPlayIcon.setImageResource(R.drawable.costom_play);
                    if (mMusicService.getmStatus() == MusicService.INITIALLY) {
                        mMusicService.onPlayMusic();
                        mMusicService.setmStatus(MusicService.STOP);
                    }
                } else {
                    mMusicService.onResumeMusic();
                    // 0 ,1 ko ro ràng
                    if (mMusicService.getmStatus() ==  MusicService.INITIALLY) {
                        mMusicService.onPlayMusic();
                        mMusicService.setmStatus(MusicService.STOP);
                    }
                    mPlayIcon.setImageResource(R.drawable.custom_play_pause);
                }
                mMusicService.setChangeNotification();
                break;
            case R.id.icon_Next:
                mMusicService.onNextMusic();
                setTile(mMusicService.getSongIsPlay());
                setSeekBar();
                mMediaListener.onNext();
                setImagePlayer();
                mMusicService.setChangeNotification();
                break;
            case R.id.icon_Dislike:
                mMediaListener.onDisLike();
                break;
            case R.id.icon_shuffle:
                mMusicService.setShuff();
                if (mMusicService.getStatueRepeat() == MusicService.SHUFF) {
                    mShuffleIcon.setImageResource(R.drawable.ic_baseline_shuffle_24);
                }
                mShuffleIcon.setImageResource(R.drawable.ic_baseline_shuffle);
                break;
            case R.id.icon_repeat:
                mMusicService.setRandom();
                if (mMusicService.getStatueRepeat() == MusicService.RANDOM) {
                    mRePeatIcon.setImageResource(R.drawable.ic_baseline_repeat_24);
                }
                mRePeatIcon.setImageResource(R.drawable.ic_baseline_repeat);
                break;
        }
    }

    private void displayPopupMenu(View view) {
        PopupMenu mPopupMen = new PopupMenu(getContext(), view);
        mPopupMen.getMenuInflater().inflate(R.menu.more_menu, mPopupMen.getMenu());
        mPopupMen.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.like_music:
                        break;
                    case R.id.dislike_music:
                        break;
                }
                return true;
            }
        });
        mPopupMen.show();
    }


    /**
     * BachNN
     * Interface để callback lại cho MainActivity.
     */
    public interface IMediaPlayFragmentListenner {
        //BachNN : bài hát yêu thich
        void onLike();

        //BachNN : quay lại bài hát trước
        void onPrevious();

        //BachNN : chay nhạc hay dừng
        void onPlay();

        //BachNN : chuyền bài khác
        void onNext();

        //BachNN : bài hát ko thicks
        void onDisLike();

        //BachNN :
        void onSeekBar();
    }

}
