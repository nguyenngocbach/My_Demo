package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Model.Song;
import com.example.myapplication.Service.MusicManager;
import com.example.myapplication.Service.MusicService;
import com.example.myapplication.broadcast.NotificationBroadCast;
import com.example.myapplication.database.DataManager;
import com.example.myapplication.fragment.AllSongFragment;
import com.example.myapplication.fragment.FavoriteSongsFragment;
import com.example.myapplication.fragment.MediaPlaybackFragment;
import com.example.myapplication.listenner.IDatabaseListenner;
import com.example.myapplication.listenner.IMusicListenner;
import com.example.myapplication.listenner.INotificationBroadCastListener;
import com.example.myapplication.unit.Coast;
import com.example.myapplication.unit.LogSetting;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  AllSongFragment.AllSongFragmentListenner
        , MediaPlaybackFragment.IMediaPlayFragmentListenner, INotificationBroadCastListener, IDatabaseListenner {

    public static final String KEY_MUSIC_IBINDER = "com.example.myapplication.mIBinder";
    public static final String TAG_MAIN = "BachNN_MAIN";
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 123;
    private MediaPlaybackFragment mMediaPlayer;
    private FragmentTransaction mTransaction;
    private Intent mIntent;
    private MusicService mMusicService;
    private MusicManager mMusicManager;
    private FragmentManager mFragmentManager;
    private boolean mIsVertical = false;
    private boolean mCheck = false;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    // ahead of Navigation
    private TextView mListenTextView;
    private TextView mRecentTextView;
    private TextView mMusicLibraryTextView;
    private ImageView mListenImage;
    private ImageView mRecentImage;
    private ImageView mMusicLibraryImage;
    private LinearLayout mListenLayout;
    private LinearLayout mRecentLayout;
    private LinearLayout mMusicLibraryLayout;
    private int mPositionAHeader = 0;
    private NotificationBroadCast mBroadCast;
    private MusicService.LocalMusic mLocalMusic;
    private DataManager mDatabase;
    private boolean mFavourite = false;
    /*
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            mLocalMusic = (MusicService.LocalMusic) iBinder;
//            mMusicService = mLocalMusic.getInstanceService();
//            if (mMusicManager == null) {
//                mMusicManager = mMusicService.getMusicManager();
//            }
//            if (!mCheck) {
//                if (mIsVertical) {
//                    AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
//                    if (LogSetting.sLife) {
//                        Log.d(TAG_MAIN, mMusicManager + "  onServiceConnected " + mMusicManager.getmSongs().size());
//                    }
//                    //allSongFragment.setData(mMusicManager.getmSongs());
////                    allSongFragment.isPlayMusic(mMusicManager.isMusicPlaying());
////                    allSongFragment.setTitle(mMusicManager.getSongIsPlay());
////                    allSongFragment.setImageMusic();
//
//                } else {
//                    AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
//                    //MediaPlaybackFragment playbackFragment = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
//                    allSongFragment.setData(mMusicManager.getmSongs());
//                    allSongFragment.setData(mMusicManager.getmSongs());
//                    allSongFragment.isPlayMusic(mMusicManager.isMusicPlaying());
//                    allSongFragment.setTitle(mMusicManager.getSongIsPlay());
//                    allSongFragment.setVisible();
//                }
//            }
        }


        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMusicService = null;
            mMusicManager = null;
        }
    };
    private View mFavouriteLayout;

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMusicManager = MusicManager.getInstance(this);

        if (savedInstanceState != null) {
            mCheck = true;
            mLocalMusic = (MusicService.LocalMusic) savedInstanceState.getSerializable(KEY_MUSIC_IBINDER);
            mMusicManager = mLocalMusic.getInstanceService().getMusicManager();
            Log.d(TAG_MAIN, "" + mLocalMusic);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            createFragment();
        }

    }

    /**
     * BachNN
     * khơi tạo tất cả các thuộc tính cần thiến khi chay Activity
     * và kính hoạt Fragment , Service , Database
     */
    private void createFragment() {
        mFragmentManager = getSupportFragmentManager();
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // start service.
        mIntent = new Intent(MainActivity.this, MusicService.class);
        startService(mIntent);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);

        mDatabase = new DataManager(this);
        // register BroadCast.
        mBroadCast = new NotificationBroadCast(this);
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Coast.ACTION_NEXT);
        mFilter.addAction(Coast.ACTION_PLAY);
        mFilter.addAction(Coast.ACTION_PREVIOUS);
        mFilter.addAction(Coast.ACTION_AUTONEXT);
        this.registerReceiver(mBroadCast, mFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCast, mFilter);

        if (findViewById(R.id.vertical_Screen) != null) mIsVertical = true;
        if (findViewById(R.id.vertical_Screen) != null) mIsVertical = true;
        if (mIsVertical) {
            if ((MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer) != null) {
                MediaPlaybackFragment fragment = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
                fragment.setMusicManager(mMusicManager);
                if (LogSetting.sLife) {
                    Log.d(TAG_MAIN, "cos nhe");
                    //onBackPressed();
                }
            }
            AllSongFragment allSongFragment = new AllSongFragment();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.allSongFragment, allSongFragment);
            ft.commit();
            //DrawerLayout and Navigation
            mDrawerLayout = findViewById(R.id.vertical_Screen);
            mToggle = new ActionBarDrawerToggle(MainActivity.this
                    , mDrawerLayout, mToolbar, R.string.open_navigation, R.string.close_navigetion);
            mDrawerLayout.addDrawerListener(mToggle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            mToggle.syncState();
            mNavigationView = findViewById(R.id.navigationVew);
            mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.setting:
                            Toast.makeText(MainActivity.this, "Setting", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.item_help:
                            Toast.makeText(MainActivity.this, "Help & FeelBack", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    mDrawerLayout.closeDrawers();
                    return false;
                }
            });
            mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(@NonNull View drawerView) {
                    setAHeaderNavigation();
                }

                @Override
                public void onDrawerClosed(@NonNull View drawerView) {

                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
        } else {
            MediaPlaybackFragment mediaPlaybackFragment = mLocalMusic.getMediaPlaybachFragment();
            if (LogSetting.sLife) {
                Log.d("BachNN_MAIN", mLocalMusic.getInstanceService().getMusicManager() + "  kkkkkk");
            }
            mediaPlaybackFragment.setMusicManager(mMusicManager);
            Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_MUSIC_IBINDER, mLocalMusic);
            mediaPlaybackFragment.setArguments(bundle);
            FragmentTransaction layer = mFragmentManager.beginTransaction();
            layer.replace(R.id.musicPlayer, mediaPlaybackFragment);
            layer.addToBackStack(null);
            layer.commit();

            AllSongFragment allSongFragment = new AllSongFragment();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.allSongFragment, allSongFragment);
            ft.commit();
        }
    }


    /**
     * BachNN
     * anh xạ các View có trong aheader Layout ,
     * set sự kiểm có các mục trên aheader Layout.
     */
    private void setAHeaderNavigation() {
        View mView = mNavigationView.getHeaderView(0);
        mListenTextView = mView.findViewById(R.id.txt_listen_now);
        mRecentTextView = mView.findViewById(R.id.txt_recent);
        mMusicLibraryTextView = mView.findViewById(R.id.txt_music_library);
        mListenImage = mView.findViewById(R.id.icon_listen_now);
        mRecentImage = mView.findViewById(R.id.icon_recent);
        mMusicLibraryImage = mView.findViewById(R.id.icon_muic_library);
        mListenLayout = mView.findViewById(R.id.listen_now);
        mRecentLayout = mView.findViewById(R.id.recent);
        mFavouriteLayout = mView.findViewById(R.id.favourite_layout);
        mMusicLibraryLayout = mView.findViewById(R.id.music_library);

        mListenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Listen_Now", Toast.LENGTH_SHORT).show();
                setChangeAheader(0);
                new AllFavouriteMusic().execute();
                mDrawerLayout.closeDrawers();
                //mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
                mFavourite = true;

            }
        });

        mRecentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Recent", Toast.LENGTH_SHORT).show();
                setChangeAheader(1);
                mDrawerLayout.closeDrawers();
            }
        });

        mFavouriteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (LogSetting.sLife) {
                    Log.d("BachNN_MAIN", mLocalMusic.getInstanceService().getMusicManager() + "  kkkkkk");
                }

                FavoriteSongsFragment favouriteFragment= new FavoriteSongsFragment();
                FragmentTransaction layer = mFragmentManager.beginTransaction();
                layer.replace(R.id.allSongFragment, favouriteFragment);
                layer.addToBackStack(null);
                layer.commit();
                mDrawerLayout.closeDrawers();
            }
        });

        mMusicLibraryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Music Library", Toast.LENGTH_SHORT).show();
                setChangeAheader(2);

                mDrawerLayout.closeDrawers();
            }
        });

    }


    /**
     * BachNN
     *
     * @param i là vị trí mà người dụng chọn trên aheader Layout.
     */
    private void setChangeAheader(int i) {
        if (i == mPositionAHeader) return;
        switch (i) {
            case 0:
                mListenImage.setImageResource(R.drawable.custom_music_player);
                mListenTextView.setTextColor(Color.argb(100, 250, 98, 50));
                mListenLayout.setBackgroundColor(Color.argb(100, 234, 234, 232));
                break;
            case 1:
                mRecentImage.setImageResource(R.drawable.ic_replay_10);
                mRecentTextView.setTextColor(Color.argb(100, 250, 98, 50));
                mRecentLayout.setBackgroundColor(Color.argb(100, 234, 234, 232));
                break;
            case 2:
                mMusicLibraryImage.setImageResource(R.drawable.ic_library_music_24);
                mMusicLibraryTextView.setTextColor(Color.argb(100, 250, 98, 50));
                mMusicLibraryLayout.setBackgroundColor(Color.argb(100, 234, 234, 232));
                break;
        }

        switch (mPositionAHeader) {
            case 0:
                mListenImage.setImageResource(R.drawable.ic_music_note_24);
                mListenTextView.setTextColor(Color.argb(100, 122, 122, 121));
                mListenLayout.setBackgroundColor(Color.argb(100, 255, 255, 255));
                break;
            case 1:
                mRecentImage.setImageResource(R.drawable.ic_10_previous);
                mRecentTextView.setTextColor(Color.argb(100, 122, 122, 121));
                mRecentLayout.setBackgroundColor(Color.argb(100, 255, 255, 255));
                break;
            case 2:
                mMusicLibraryImage.setImageResource(R.drawable.ic_baseline_library_music_24);
                mMusicLibraryTextView.setTextColor(Color.argb(100, 122, 122, 121));
                mMusicLibraryLayout.setBackgroundColor(Color.argb(100, 255, 255, 255));
                break;
        }
        mPositionAHeader = i;

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCheck) {
            AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
            allSongFragment.setData(mMusicManager.getmSongs());
//            allSongFragment.setSongManager(mMusicManager);
            allSongFragment.setTitle(mMusicManager.getSongIsPlay());
            allSongFragment.setImageMusic();
            //allSongFragment.setVisible(!mMusicManager.getCurrentBegin());
            if (!mIsVertical) {
                //MediaPlaybackFragment mediaPlaybackFragment = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
                allSongFragment.setVisible();
            } else allSongFragment.isPlayMusic(mMusicManager.isMusicPlaying());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public MusicManager getMusicManager() {
        return mMusicManager;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(KEY_MUSIC_IBINDER, mLocalMusic);
        //outState.putSerializable(KEY_MUSIC_MANAGER, mMusicManager);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    createFragment();
                }
            } else {
                finish();
            }
        }
    }

    /**
     * BachNN
     *
     * @param i là vị trí bạn hát được chọn trongn AllFragment ,
     *          và sẽ set lại ban hát cho đung  vị trí của i và cập nhập lại giao diện
     */
//    @Override
//    public void selectMusic(int i) {
////        AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
////        allSongFragment.setSelection(i);
////        allSongFragment.isPlayMusic(true);
////        allSongFragment.setImageMusic();
////        allSongFragment.setVisibleDisPlay();
////        if (mMusicManager != null) {
////            if (mMusicManager.isMusicPlaying()) {
////                mMusicManager.onResetMusic();
////            }
////            mMusicManager.setmCurrentSong(i);
////            mMusicManager.onPlayMusic();
////        }
//        if (!mIsVertical) {
//            MediaPlaybackFragment player = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
//            player.setMusicManager(mMusicManager);
//        }
//        mLocalMusic.setNextMusicNotification();
//    }

    /**
     * BachNN
     * @param i vị trí của item trong list Music.
     * @param view chuyền vào các view đã click
     *             hiện thi một PopupMenu đệ người dung chọn bài hát thích hay không thích.
//     */
//    @Override
//    public void selectMoreMusic(final int i, View view) {
////        PopupMenu mPopupMen = new PopupMenu(MainActivity.this, view);
////        mPopupMen.getMenuInflater().inflate(R.menu.more_menu, mPopupMen.getMenu());
////
////        mPopupMen.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
////            @Override
////            public boolean onMenuItemClick(MenuItem menuItem) {
////                switch (menuItem.getItemId()) {
////                    case R.id.like_music:
////                        new AddFavouriteMusic().execute(mMusicManager.getSinpleSong(i));
////                        Toast.makeText(mMusicService, "You like " + mMusicManager.getmSongs().get(i).getTitle(), Toast.LENGTH_SHORT).show();
////
////                        break;
////                    case R.id.dislike_music:
////                        new DeleteFavouriteMusic().execute(Integer.parseInt(mMusicManager.getSinpleSong(i).getId()));
////                        Toast.makeText(mMusicService, "You dislike " + mMusicManager.getmSongs().get(i).getTitle(), Toast.LENGTH_SHORT).show();
////                        break;
////                }
////                return true;
////            }
////        });
////        mPopupMen.show();
//    }

    /**
     * BachNN
     * hiện thi ra MediaPlaybackFragment.
     */
    @Override
    public void show() {
        if(mIsVertical) {
            mMediaPlayer = mLocalMusic.getMediaPlaybachFragment();
            mTransaction = mFragmentManager.beginTransaction();
            mTransaction.replace(R.id.musicPlayer, mMediaPlayer);
            mTransaction.addToBackStack(null);
            mTransaction.commit();

        } else {
            mMediaPlayer = mLocalMusic.getMediaPlaybachFragment();
            mTransaction = mFragmentManager.beginTransaction();
            mTransaction.replace(R.id.musicPlayer, mMediaPlayer);
            mTransaction.addToBackStack(null);
            mTransaction.commit();

        }
    }

    @Override
    public void setIconNotification() {
        mLocalMusic.setPlayMusic();
    }

    /**
     *
     */
    @Override
    public void onLike() {

    }

    /**
     * BachNN
     * quay lại bàn hái
     * set lại giao dien AllSongFragmen và Notification.
     */
    @Override
    public void onPrevious() {
        AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        allSongFragment.setData(mMusicManager.getmSongs());
        allSongFragment.setImageMusic();
        mLocalMusic.setPreviousMusicNotification();
    }

    /**
     * BachNN
     * play bài hày hoặc puase
     * set lại giao dien AllSongFragmen và Notification.
     */
    @Override
    public void onPlay() {
        AllSongFragment mAllSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        mAllSongFragment.isPlayMusic(!mMusicManager.isMusicPlaying());
        mLocalMusic.setPlayMusicNoti();
    }

    /**
     * BachNN
     * next sang bài hat tiếp theo
     * set lại giao dien AllSongFragmen và Notification.
     */
    @Override
    public void onNext() {

        AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        allSongFragment.setData(mMusicManager.getmSongs());
        allSongFragment.setImageMusic();
        mLocalMusic.setNextMusicNotification();
    }

    @Override
    public void onDisLike() {

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onSeekBar() {
        AllSongFragment songFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        songFragment.setTitle(mMusicManager.getSongIsPlay());
    }

    public boolean isVertical() {
        return mIsVertical;
    }

    public boolean isCheck() {
        return mCheck;
    }


    /**
     * BachNN
     * next bài hát vào tiếp thep ,
     * và set lai giao diện của AllFragment và Notification.
     */
    //
    @Override
    public void onNextMusicBroadCast() {
        mMusicManager.onNextMusic();
        AllSongFragment mAllSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        mAllSongFragment.setData(mMusicManager.getmSongs());
        mAllSongFragment.setTitle(mMusicManager.getSongIsPlay());
        mAllSongFragment.isPlayMusic(mMusicManager.isMusicPlaying());
        mAllSongFragment.setImageMusic();
        MediaPlaybackFragment mMediaPlaybackFragment = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
        if (mMediaPlaybackFragment != null) {
            mMediaPlaybackFragment.setStatusIcon(mMusicManager.isMusicPlaying());
            mMediaPlaybackFragment.setTile(mMusicManager.getSongIsPlay());
            mMediaPlaybackFragment.setImagePlayer();
        }
        mLocalMusic.setNextMusicNotification();
    }

    /**
     * BachNN
     * quay lại bài hát gần nhất ,
     * set lai giao diện của AllFragment và Notification.
     */
    @Override
    public void onPreviousMusicBroadCast() {
        mMusicManager.onPreviousMusic();
        AllSongFragment mAllSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        mAllSongFragment.setData(mMusicManager.getmSongs());
        mAllSongFragment.setTitle(mMusicManager.getSongIsPlay());
        mAllSongFragment.isPlayMusic(mMusicManager.isMusicPlaying());
        mAllSongFragment.setImageMusic();
        MediaPlaybackFragment mMediaPlaybackFragment = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
        if (mMediaPlaybackFragment != null) {
            mMediaPlaybackFragment.setStatusIcon(mMusicManager.isMusicPlaying());
            mMediaPlaybackFragment.setTile(mMusicManager.getSongIsPlay());
            mMediaPlaybackFragment.setImagePlayer();
        }
        mLocalMusic.setPreviousMusicNotification();
    }

    /**
     * BachNN
     * play bài hát hoặc pause
     * set lai giao diện của AllFragment và Notification.
     */
    @Override
    public void onOnPlayMusicBroadCast() {
        if (mMusicManager.isMusicPlaying()) {
            mMusicManager.onStopMusic();
        } else {
            mMusicManager.onResumeMusic();
        }
        AllSongFragment mAllSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        mAllSongFragment.isPlayMusic(mMusicManager.isMusicPlaying());

        MediaPlaybackFragment mMediaPlaybackFragment = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
        if (mMediaPlaybackFragment != null) {
            mMediaPlaybackFragment.setStatusIcon(mMusicManager.isMusicPlaying());
        }
        mLocalMusic.setPlayMusic();
    }

    @Override
    public void onPlayMusicAutoNextBroadCast() {
        AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        allSongFragment.setSelection(mMusicManager.getmCurrentSong());
        allSongFragment.isPlayMusic(true);
        if (!mIsVertical) {
            MediaPlaybackFragment player = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
            player.setMusicManager(mMusicManager);
        }
        mLocalMusic.setNextMusicNotification();
    }

    @Override
    public void addFavouriteMusic(Song song) {
        new AddFavouriteMusic().execute(song);
    }

    @Override
    public void deleteFavouriteMusic(int id) {
        new DeleteFavouriteMusic().execute(id);
    }

    @Override
    public void getAllFavouriteMusic() {
    }

    class AddFavouriteMusic extends AsyncTask<Song, Void, Void> {
        @Override
        protected Void doInBackground(Song... songs) {
            mDatabase.addMusicFvourite(songs[0]);
            return null;
        }
    }

    class DeleteFavouriteMusic extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            mDatabase.removeMusicFvourite(integers[0]);
            return null;
        }
    }

    class AllFavouriteMusic extends AsyncTask<Void, Void, List<Song>> {
        @Override
        protected List<Song> doInBackground(Void... voids) {
            return mDatabase.getAllMusicFvourite();
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            super.onPostExecute(songs);
            // todo something
            if (songs.size() > 0) {
                for (int i = 0; i < songs.size(); i++) {
                }
            }
        }
    }
}