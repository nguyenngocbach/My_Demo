package com.example.myapplication.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.MainActivity;
import com.example.myapplication.service.MusicService;
import com.example.myapplication.database.DataManager;
import com.example.myapplication.model.Song;
import com.example.myapplication.R;
import com.example.myapplication.adapter.AllSongAdapter;
import com.example.myapplication.listenner.IMusicListenner;
import com.example.myapplication.util.Util;
import com.example.myapplication.util.LogSetting;

import java.util.ArrayList;
import java.util.List;

public class BaseSongListFragment extends Fragment implements IMusicListenner {
    public static final int POSITION_MUSIC_DEFAULT = -1;
    protected AllSongFragment.IAllSongFragmentListener mAllSongListener;
    protected MainActivity mMainActivity;
    protected DataManager mDatabaseManager;
    protected RecyclerView mMusicRecyclerView;
    protected List<Song> mSongs = new ArrayList<>();
    protected AllSongAdapter mAdapter;
    protected ImageView mPlayImageView;
    protected ImageView mMusicImageView;
    protected TextView mTitleTextView;
    protected TextView mAuthorTextView;
    protected LinearLayout mItemMusic;
    protected boolean mIsVertical;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mAllSongListener = (AllSongFragment.IAllSongFragmentListener) context;
        } else throw new ClassCastException("onAttach Methods have problem !");
        mMainActivity = (MainActivity) context;
        mDatabaseManager = mMainActivity.getDatabase();
        mIsVertical = mMainActivity.getVertical();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_song_fragment, container, false);
        mMusicRecyclerView = view.findViewById(R.id.recycler_song);
        final LinearLayoutManager managerOrientationLayout = new LinearLayoutManager(getContext());
        managerOrientationLayout.setOrientation(RecyclerView.VERTICAL);
        mMusicRecyclerView.setLayoutManager(managerOrientationLayout);
        mSongs = new ArrayList<>();
        mAdapter = new AllSongAdapter(getContext(), mSongs, this);
        mMusicRecyclerView.setAdapter(mAdapter);
        mMusicImageView = view.findViewById(R.id.avatar);
        mPlayImageView = view.findViewById(R.id.icon_play_music);
        mTitleTextView = view.findViewById(R.id.name_Music);
        mAuthorTextView = view.findViewById(R.id.name_Airsts);
        mItemMusic = view.findViewById(R.id.linear_Layout);
        mItemMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAllSongListener.showMediaPlaybackFragment();
            }
        });

        mPlayImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMainActivity.getMusicService().checkMusicPlaying()) {
                    mMainActivity.getMusicService().onStopMusic();
                    mPlayImageView.setImageResource(R.drawable.ic_play_black);
                    mMainActivity.getMusicService().setChangeNotification();
                } else {
                    mMainActivity.getMusicService().onResumeMusic();
                    mMainActivity.getMusicService().setChangeNotification();
                    mPlayImageView.setImageResource(R.drawable.ic_pause_black_large);
                    if (mMainActivity.getMusicService().getStatus() == MusicService.INITIALLY) {
                        mMainActivity.getMusicService().onPlayMusic();
                        mMainActivity.getMusicService().setStatus(MusicService.STOP);
                    }
                }
                mAllSongListener.setIconNotification();
            }
        });

        return view;
    }

    /**
     * BachNN
     * hàm này mục đích là restore dữ liệu khi back từ MediaPlaybackFragment.
     */
    @Override
    public void onStart() {
        super.onStart();
        //BachNN : mMainActivity.getMusicService() == null --> return, bời vì nếu mà ko thì sẽ ko có Data để set
        // cho các View ở các dòng lệnh dưới.
        if (mMainActivity.getMusicService() == null) return;
        //BachNN : nếu màn hình dọc mới hiên set hiện thị lại ToolBar ngược lại thì ko.
        if (mMainActivity.getVertical()) {
            mMainActivity.setShowToolbar();
        }
        // BachNN : mMainActivity.getMusicService().getCurrentSong() == POSITION_MUSIC_DEFAULT -->return
        // thì sẽ ko có Data để set cho các View Thanh hiển thị bài hát đang chay và ảnh ở dưới.
        // POSITION_MUSIC_DEFAULT= -1 vị trí bài hát default.
        if (mMainActivity.getMusicService().getCurrentSong() == POSITION_MUSIC_DEFAULT) return;
        setUIAllView();
        setSelectMusic(mMainActivity.getMusicService().getCurrentSong());
        if (mIsVertical && mMainActivity.getMusicService().getCurrentSong() != POSITION_MUSIC_DEFAULT) {
            setVisibleDisPlay();
        } else {
            setVisible();
        }
    }

    /**
     * BachNN
     * set lại toàn bị các view trên Fragment này
     */
    public void setUIAllView() {
        setDataAllMusic(mMainActivity.getMusicService().getAllSongs());
        setTitleMusic(mMainActivity.getMusicService().getSongPlaying());
        setButtonIconPlayMusic(mMainActivity.getMusicService().checkMusicPlaying());
        setImageMusic();
    }

    public void setDataAllMusic(List<Song> songList) {
        mSongs.clear();
        mSongs.addAll(songList);
        // BachNN : set lai vị trị bài hát đang chay cho List Song
        if (mMainActivity.getMusicService() != null) {
            mAdapter.setCurrentSong(mMainActivity.getMusicService().getCurrentSong());
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * BachNN
     * hàm để set anh cho từng bài hát
     */
    public void setImageMusic() {
        if (mMainActivity.getMusicService().getCurrentSong() != POSITION_MUSIC_DEFAULT) {
            byte[] sourceImage = Util.getByteImageSong(mMainActivity.getMusicService().getSongPlaying().getPath());
            Glide.with(getContext())
                    .load(sourceImage)
                    .placeholder(R.drawable.anh_ngoc_trinh)
                    .into(mMusicImageView);
        }
    }

    /**
     * BachNN
     *
     * @param musicRunning kiểm tra bài hát đang chay hay đang dung để
     *                     sét Icon của bài nhạc.
     */
    public void setButtonIconPlayMusic(boolean musicRunning) {
        if (musicRunning) {
            mPlayImageView.setImageResource(R.drawable.ic_pause_black_large);
        } else {
            mPlayImageView.setImageResource(R.drawable.ic_play_black);
        }
    }

    /**
     * BachNN
     * ẩn một ViewGround trong AllSongFragment khi nó quay ngang.
     */
    public void setVisible() {
        mItemMusic.setVisibility(View.GONE);
    }

    /**
     * BachNN
     * ẩn một ViewGround trong AllSongFragment khi nó quay ngang.
     */
    public void setVisibleDisPlay() {
        mItemMusic.setVisibility(View.VISIBLE);
    }

    /**
     * BachNN
     *
     * @param currentMusic vị trị mà người dụng click vào ban hát
     *                     nó sẽ sưa lại giao diên củâ AllSongFragment
     */
    public void setSelectMusic(int currentMusic) {
        mMainActivity.getMusicService().setCurrentSong(currentMusic);
        if (mSongs != null) {
            mSongs.clear();
        }
        mSongs.addAll(mMainActivity.getMusicService().getAllSongs());
        mAdapter.setCurrentSong(currentMusic);
        setTitleMusic(mMainActivity.getMusicService().getSongPlaying());
        mAdapter.notifyDataSetChanged();
        mMainActivity.getMusicService().setChangeNotification();
    }


    /**
     * BachNN
     *
     * @param song bài hát sẽ được đang chạy
     *             set title và author cho bài hát đang chạy.
     */
    public void setTitleMusic(Song song) {
        // BachNN nêu chưa có bài hát nào được chọn thì ko set title.
        if (mMainActivity.getMusicService().getCurrentSong() != POSITION_MUSIC_DEFAULT) {
            mAuthorTextView.setText(song.getAuthor());
            mTitleTextView.setText(song.getTitle());
        }
    }

    /**
     * BachNN
     *
     * @param i vị trị mà người dụng chọn.
     *          hàm này có sẽ chạy bài hát vưa đươc chọn vào set lại vị trị và tiêu đề bài hát.
     */
    @Override
    public void selectMusic(int i) {
        setSelectMusic(i);
        setButtonIconPlayMusic(true);
        setImageMusic();
        if (mIsVertical) {
            //BachNN nếu chưa xuất hiện thanh Tiêu đề thì hiện nó nên.
            setVisibleDisPlay();
        }
        //BachNN : nếu là màn hinh ngang thì set lại giai trị đúng cho MediaPlaybackFragment.
        if (!mIsVertical) {
            MediaPlaybackFragment mediaPlaybackFragment = (MediaPlaybackFragment)
                    getActivity().getSupportFragmentManager().findFragmentById(R.id.music_Player);
            mediaPlaybackFragment.setUIMusic();
        }
        // BachNN : kiểm tra xem bài hát nó đang chay hay ko chay.
        if (mMainActivity.getMusicService().checkMusicPlaying()) {
            mMainActivity.getMusicService().onResetMusic();
        }
        mMainActivity.getMusicService().setCurrentSong(i);
        mMainActivity.getMusicService().onPlayMusic();
        mMainActivity.getMusicService().setChangeNotification();
    }

    /**
     * BachNN
     *
     * @param i    vị trị bài hát người dùng chọn.
     * @param view mà nguời dùng click.
     *             hàm này để set các bài hát yêu thich của người dùng.
     *             nêu chọn thích thi thêm vào CSDL.
     *             ngước lại là ko thịch.
     */
    @Override
    public void selectMoreMusic(final int i, View view) {
        OfflineDialog dialog = new OfflineDialog(this,mMainActivity.getMusicService().getAllSongs().get(i));
        dialog.show(getFragmentManager(),"Offline");
        //dung dialog.
//        PopupMenu popupMenuFavouriteSong = new PopupMenu(getActivity(), view);
//        popupMenuFavouriteSong.getMenuInflater().inflate(R.menu.more_menu, popupMenuFavouriteSong.getMenu());
//        //BachNN : set mau text cho một Item.
//        MenuItem menuItem = popupMenuFavouriteSong.getMenu().getItem(0);
//        SpannableString textColor = new SpannableString("Like");
//        textColor.setSpan(new ForegroundColorSpan(Color.RED), 0, textColor.length(), 0);
//        menuItem.setTitle(textColor);
//
//        popupMenuFavouriteSong.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                switch (menuItem.getItemId()) {
//                    case R.id.like_music:
//                        mDatabaseManager.addMusicFavourite(mMainActivity.getMusicService().getAllSongs().get(i));
//                        break;
//                    case R.id.dislike_music:
//                        mDatabaseManager.removeMusicFavourite(Integer.parseInt(mSongs.get(i).getId()));
//                        break;
//                }
//                return true;
//            }
//        });
//        popupMenuFavouriteSong.show();
    }

    public void onLike(Song song){
        mDatabaseManager.addMusicFavourite(song);
    }

    public void onDisLike(Song song){
        mDatabaseManager.removeMusicFavourite(Integer.parseInt(song.getId()));
    }

    public void onDownloadMusic(Song song){

    }


    /**
     * BachNN
     * get tất cả các bài hát từ Database trên thiết bị di động.
     */
    public List<Song> getAllSongDatabase() {
        List<Song> songs = new ArrayList<>();
        String[] allColumnSong = new String[]{
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DURATION
        };
        // BachNN :query các trường trên để lấy thông tin các bàn hát
        Cursor cursor = getActivity().getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, allColumnSong, null, null, null, null);
        // BachNN :chuyển con trỏ đến đâu bảng
        if (LogSetting.IS_DEBUG) {
            Log.d(MainActivity.TAG, "cursor : " + cursor);
        }
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                songs.add(new Song(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return songs;

    }
    /**
     * BachNN
     * hàm này dùng đẻ ghi đề ở lớp con của nó đó là AllSongFragment.
     */
    public void loadData() {
    }

    /**
     * BachNN
     * interface này dung để callback về MainActivity.
     */
    public interface IAllSongFragmentListener {
        //BachNN : để hiện MediaPlayerFragemnt nên.
        void showMediaPlaybackFragment();

        //BachNN : set lại icon cho notification chay nhạc hay dưng.
        void setIconNotification();
    }

}
