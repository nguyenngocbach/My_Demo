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
import com.example.myapplication.model.Song;
import com.example.myapplication.R;
import com.example.myapplication.service.MusicService;
import com.example.myapplication.util.LogSetting;
import com.example.myapplication.util.Util;

import java.util.List;

public class MediaPlaybackFragment extends Fragment implements View.OnClickListener {
    private int TIME_REPEAT = 300;
    private ImageView mMusicImageView;
    private ImageView mAvatarImageView;
    private ImageView mLikeIcon;
    private ImageView mPlayIcon;
    private ImageView mRePeatIcon;
    private ImageView mShuffleIcon;
    private TextView mTimeTextView;
    private TextView mTotalTimeTextView;
    private TextView mTitleTextView;
    private TextView mAuthorTextView;
    private SeekBar mSeekBar;
    private ImageView mPreviousIcon;
    private ImageView mNextIcon;
    private ImageView mDislikeIcon;
    private ImageView mMoreIcon;
    private ImageView mListMusicIcon;
    private IMediaPlayFragmentListenner mMediaListener;
    private MainActivity mActivity;

    private Handler mHandler = new Handler();
    /**
     * BachNN
     * để lặp lại set giời gian chạy thực cho bài hát.
     */
    private Runnable mSetTimeSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            if (mActivity.getMusicService() != null) {
                //BachNN : nêu mà chưa có bài hát nào đang chạy thì nó sẽ ko chay mRunnable.
                if (mActivity.getMusicService().getCurrentSong() == BaseSongListFragment.POSITION_MUSIC_DEFAULT)
                    return;
                setUIMediaPlaybackFragment();
                int indexMusicPlaying = (Integer.parseInt(mActivity.getMusicService().getSongPlaying().getDuration()));
                mSeekBar.setMax(indexMusicPlaying);
                //BachNN : next bài
                if (mSeekBar.getProgress() == indexMusicPlaying) {
                    setSeekBar();
                    setTile(mActivity.getMusicService().getSongPlaying());
                    mMediaListener.onSeekBar();
                }
                mHandler.postDelayed(this, TIME_REPEAT);
            }
        }
    };

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
        initView(view);
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
        if (!mActivity.getVertical()) mMusicImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        //BachNN : user click vào icon này sẽ thêm hoặc xóa bài hát.
        mLikeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSongFavourite(mActivity.getMusicService().getSongPlaying())) {
                    int iDMusic = Integer.parseInt(mActivity.getMusicService().getSongPlaying().getId());
                    mLikeIcon.setImageResource(R.drawable.ic_thumbs_up_default);
                    //BachNN : xóa bài hát vào CSDL.
                    mActivity.getDatabase().removeMusicFavourite(iDMusic);
                } else {
                    //BachNN : thêm bài hát vào CSDL.
                    mActivity.getDatabase().addMusicFavourite(mActivity.getMusicService().getSongPlaying());
                    mLikeIcon.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                }
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    if (mActivity.getMusicService() != null) {
                        //BachNN : set thời gian thưc của ban hát ban đang chạy
                        seekBar.setProgress(mActivity.getMusicService().getTimeCurrents());
                        mActivity.getMusicService().setSeekMusic(i);
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

    private void initView(View view) {
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
    }

    /**
     * BachNN
     *
     * @param savedInstanceState lấy các dữ liện mà ta lựu trong Bundle.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //BachNN : nếu nó là màn hinh doc thi ẩn toolbar di.
        if (mActivity.getVertical()) {
            mActivity.setHideToolbar();
        }
        if (mActivity.getMusicService() != null) {
            if (mActivity.getMusicService().getCurrentSong() == BaseSongListFragment.POSITION_MUSIC_DEFAULT)
                return;
            setUIMusic();
            if (checkSongFavourite(mActivity.getMusicService().getSongPlaying())) {
                if (LogSetting.IS_DEBUG) {
                    Log.d(MainActivity.TAG, "Like To ");
                }
                mLikeIcon.setImageResource(R.drawable.ic_baseline_thumb_up_24);
            } else {
                if (LogSetting.IS_DEBUG) {
                    Log.d(MainActivity.TAG, "Like Bé");
                }
                mLikeIcon.setImageResource(R.drawable.ic_thumbs_up_default);
            }
        }
    }

    /**
     * BachNN
     * Hàm này dùng để set cái trạng thai khi chuyển bài hát trong app.
     */
    private void setRepeat() {
        if (mActivity.getMusicService().getStatueRepeat() == MusicService.NORMAL) {
            mShuffleIcon.setImageResource(R.drawable.ic_baseline_shuffle_24);
            mRePeatIcon.setImageResource(R.drawable.ic_baseline_repeat_24);
        } else if (mActivity.getMusicService().getStatueRepeat() == MusicService.RANDOM) {
            mShuffleIcon.setImageResource(R.drawable.ic_baseline_shuffle);
        } else if (mActivity.getMusicService().getStatueRepeat() == MusicService.REPEAT) {
            mRePeatIcon.setImageResource(R.drawable.ic_baseline_repeat);
        }
    }

    /**
     * BachNN
     * set lại toàn bộ các view trong Fragment này.
     */
    public void setUIMusic() {
        setSeekBar();
        setTile(mActivity.getMusicService().getSongPlaying());
        setStatusIcon(mActivity.getMusicService().checkMusicPlaying());
        loadImageMusicAvatar();
        setRepeat();
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
        mSeekBar.setMax(Integer.parseInt(mActivity.getMusicService().getSongPlaying().getDuration()));
        mHandler.postDelayed(mSetTimeSeekBarRunnable, TIME_REPEAT);
    }

    /**
     * BachNN
     *
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
        long total = Long.parseLong(time);
        int minutes = (int) ((total / 1000) / 60);
        int second = (int) ((total / 1000) % 60);
        return (minutes < 10 ? "0" + minutes : minutes + "") + ":" + (second < 10 ? "0" + second : second + "");
    }

    /**
     * BachNN
     * set Thời gian thực vào các textView và SeekBar sẽ chay theo thời gian thực
     */
    private void setUIMediaPlaybackFragment() {
        if (mActivity.getMusicService() != null) {
            if (mActivity.getMusicService().getCurrentSong() == BaseSongListFragment.POSITION_MUSIC_DEFAULT) {
                return;
            }
            String totalTime = getDuration(mActivity.getMusicService().getSongPlaying().getDuration());
            mTotalTimeTextView.setText(totalTime);
            String realTime = getDuration(mActivity.getMusicService().getTimeCurrents() + "");
            mTimeTextView.setText(realTime);
            mSeekBar.setProgress(mActivity.getMusicService().getTimeCurrents());
        }
    }

    /**
     * BachNN
     * set Image cho 2 cái ảnh của MediaPlaybackFragment.
     */
    public void loadImageMusicAvatar() {
        if (mActivity.getMusicService() == null) return;
        byte[] sourceImage = Util.getByteImageSong(mActivity.getMusicService().getSongPlaying().getPath());
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
//                displayPopupMenu(view);
                break;
            case R.id.icon_queue:
                getActivity().onBackPressed();
                break;
            case R.id.icon_Like:
                mActivity.getMusicService().setChangeNotification();
                break;
            case R.id.icon_Previous:
                //BachNN : quay lại bài và set lại các giá trị.
                mActivity.getMusicService().onPreviousMusic();
                setUIMusic();
                mMediaListener.onPrevious();
                mActivity.getMusicService().setChangeNotification();
                break;
            case R.id.icon_Play:
                mMediaListener.onPlay();
                if (mActivity.getMusicService().checkMusicPlaying()) {
                    mActivity.getMusicService().onStopMusic();
                    mPlayIcon.setImageResource(R.drawable.costom_play);
                    if (mActivity.getMusicService().getStatus() == MusicService.INITIALLY) {
                        mActivity.getMusicService().onPlayMusic();
                        mActivity.getMusicService().setStatus(MusicService.STOP);
                    }
                } else {
                    mActivity.getMusicService().onResumeMusic();
                    if (mActivity.getMusicService().getStatus() == MusicService.INITIALLY) {
                        mActivity.getMusicService().onPlayMusic();
                        mActivity.getMusicService().setStatus(MusicService.STOP);
                    }
                    mPlayIcon.setImageResource(R.drawable.custom_play_pause);
                }
                mActivity.getMusicService().setChangeNotification();
                break;
            case R.id.icon_Next:
                //BachNN : next bài và set lại các giá trị.
                mActivity.getMusicService().onNextMusic();
                mMediaListener.onNext();
                mActivity.getMusicService().setChangeNotification();
                setUIMusic();
                break;
            case R.id.icon_Dislike:
                mActivity.getDatabase().
                        removeMusicFavourite(Integer.parseInt(mActivity.getMusicService().getSongPlaying().getId()));
                break;
            case R.id.icon_shuffle:
                mActivity.getMusicService().setShuffle();
                // BachNN : kỉểm tra xem trạng thái của Service hiện tại có bằng REPEAT không.
                if (mActivity.getMusicService().getStatueRepeat() == MusicService.REPEAT) {
                    mShuffleIcon.setImageResource(R.drawable.ic_baseline_shuffle_24);
                    mActivity.getMusicService().setStatus(MusicService.NORMAL);
                } else {
                    mShuffleIcon.setImageResource(R.drawable.ic_baseline_shuffle);
                    mActivity.getMusicService().setStatus(MusicService.REPEAT);
                }
                break;
            case R.id.icon_repeat:
                mActivity.getMusicService().setRandom();
                // BachNN : kỉểm tra xem trạng thái của Service hiện tại có bằng RANDOM không.
                if (mActivity.getMusicService().getStatueRepeat() == MusicService.RANDOM) {
                    mRePeatIcon.setImageResource(R.drawable.ic_baseline_repeat_24);
                    mActivity.getMusicService().setStatus(MusicService.NORMAL);
                } else {
                    mRePeatIcon.setImageResource(R.drawable.ic_baseline_repeat);
                    mActivity.getMusicService().setStatus(MusicService.RANDOM);
                }
                break;
        }
    }

    /**
     * BachNN
     *
     * @param view view khi user click vào
     *             hàm dung để hiện thi nên để like hay ko like.
     */
//    private void displayPopupMenu(View view) {
//        PopupMenu mPopupMen = new PopupMenu(getContext(), view);
//        mPopupMen.getMenuInflater().inflate(R.menu.more_menu, mPopupMen.getMenu());
//        mPopupMen.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                switch (menuItem.getItemId()) {
//                    case R.id.like_music:
//                        mActivity.getDatabase()
//                                .addMusicFavourite(mActivity.getMusicService().getAllSongs().get(mActivity.getMusicService().getCurrentSong()));
//                        break;
//                    case R.id.dislike_music:
//                        mActivity.getDatabase()
//                                .removeMusicFavourite(Integer.parseInt(mActivity.getMusicService().getSongPlaying().getId()));
//                        break;
//                }
//                return true;
//            }
//        });
//        mPopupMen.show();
//    }


    /**
     * BachNN
     *
     * @param song song la bai hat dc hien thi tren Fragment nay
     *             kiem tra xem bai hat co trong cac bai hat yeu thich ko
     * @return neu co tra ve true nguoc lai false.
     */
    public boolean checkSongFavourite(Song song) {
        List<Song> songFavourite = mActivity.getDatabase().getAllMusicFavourite();
        for (int i = 0; i < songFavourite.size(); i++) {
            if (songFavourite.get(i).getId().equals(song.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStop() {
        super.onStop();
        //BachNN : dừng runnable để nó ko cập nhật UI cho fragment này nữa.
        mHandler.removeCallbacks(mSetTimeSeekBarRunnable);
    }

    /**
     * BachNN
     * khi stop bài hát Handler sẽ dừng cập nhất seekbar.
     */
    @Override
    public void onStart() {
        super.onStart();
        mHandler.postDelayed(mSetTimeSeekBarRunnable, TIME_REPEAT);
    }

    /**
     * BachNN
     * Interface để callback lại cho MainActivity.
     */
    public interface IMediaPlayFragmentListenner {
        //BachNN : quay lại bài hát trước
        void onPrevious();

        //BachNN : chay nhạc hay dừng
        void onPlay();

        //BachNN : chuyền bài khác
        void onNext();

        //BachNN :
        void onSeekBar();
    }

}
