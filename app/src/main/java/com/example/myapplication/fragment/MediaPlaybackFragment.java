package com.example.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.example.myapplication.Model.Song;
import com.example.myapplication.R;
import com.example.myapplication.Service.MusicManager;
import com.example.myapplication.unit.Coast;
import com.example.myapplication.unit.LogSetting;

public class MediaPlaybackFragment extends Fragment implements View.OnClickListener {

    public static final String KEY_MEDIA_FRAGMENT = "com.example.myapplication.fragment.musicManager";
    // static de lam j
    // phai bo di
    // loi rat lon co

    private int TIME_REPEAT= 300;
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
    /**
     * để lặp lại set giời gian chạy thực cho bài hát.
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setUI();
            int index = (Integer.parseInt(mMusicManager.getSongIsPlay().getDuration()));
            mSeekBar.setMax(index);
            if (mSeekBar.getProgress() == index) {
                setSeekBar();
                setTile(mMusicManager.getSongIsPlay());
                mMediaListenner.onSeekBar();
            }
            mHandler.postDelayed(this, TIME_REPEAT);
        }
    };

    public void setMusicManager(MusicManager musicManager) {
        this.mMusicManager = musicManager;
    }

    /**
     * @param context của  Activity chưa các Fragment này.
     *                gián giá trị cho Ifnterface để callback
     */
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
                        //set thời gian thưc của ban hát ban đang chạy
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

    /**
     * @param savedInstanceState lấy các dữ liện mà ta lựu trong Bundle.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            mMusicManager = (MusicManager) getArguments().getSerializable(KEY_MEDIA_FRAGMENT);
            setTile(mMusicManager.getSongIsPlay());
            setSeekBar();
            setStatusIcon(mMusicManager.isMusicPlaying());
        }
        setImagePlayer();
    }

    /**
     * @param manager gán MusicManager cho bến ở trên.
     */
    public void setmMusicManager(MusicManager manager) {
        mMusicManager = manager;
        // set lai seekbar cua MediaPlayerFragment
        setSeekBar();
        setTile(mMusicManager.getSongIsPlay());
        setStatusIcon(mMusicManager.isMusicPlaying());
    }

    /**
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
     * set Max của SeekBar và chay đung giơi gian thực của bài hát.
     */
    private void setSeekBar() {
        mSeekBar.setMax(Integer.parseInt(mMusicManager.getSongIsPlay().getDuration()));
        mHandler.postDelayed(runnable, TIME_REPEAT);
    }

    // set title va author cho bai hay
    public void setTile(Song song) {
        mAuthorTextView.setText(song.getAuthor());
        mTitelTextView.setText(song.getTitle());
    }

    /**
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
     * set Thời gian thực vào các textView và SeekBar sẽ chay theo thời gian thực
     */
    private void setUI() {
        if (mMusicManager != null) {
            String totalT = getDuration(mMusicManager.getSongIsPlay().getDuration());
            mTotalTimeTextView.setText(totalT);
            String realTime = getDuration(mMusicManager.getTimeCurrents() + "");
            mTimeTextView.setText(realTime);
            mSeekBar.setProgress(mMusicManager.getTimeCurrents());
        }
    }

    public void setImagePlayer() {
        byte[] sourceImage = Coast.getByteImageSong(mMusicManager.getSongIsPlay().getPath());
        Glide.with(getContext())
                .load(sourceImage)
                .into(mMusicImageView);
        Glide.with(getContext())
                .load(sourceImage)
                .into(mAvatarImageView);
    }

    /**
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
            case R.id.iconLike:
                mMediaListenner.onLike();
                break;
            case R.id.iconPrevious:
                mMusicManager.onPreviousMusic();
                setTile(mMusicManager.getSongIsPlay());
                setSeekBar();
                mMediaListenner.onPrevious();
                setImagePlayer();
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
                    // 0 ,1 ko ro ràng
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
                setImagePlayer();
                break;
            case R.id.iconDislike:
                mMediaListenner.onDisLike();
                break;
            case R.id.icon_shuffle:
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
     * Interface để callback lại cho MainActivity.
     */
    public interface IMediaPlayFragmentListenner {
        // bài hát yêu thich
        void onLike();
        // quay lại bài hát trước
        void onPrevious();
        // chay nhạc hay dừng
        void onPlay();
        // chuyền bài khác
        void onNext();
        // bài hát ko thicks
        void onDisLike();

        void onSeekBar();
    }

}
