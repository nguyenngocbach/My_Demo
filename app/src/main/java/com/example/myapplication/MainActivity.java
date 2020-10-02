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

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.fragment.FavoriteSongsFragment;
import com.example.myapplication.model.Song;
import com.example.myapplication.Service.MusicService;
import com.example.myapplication.broadcast.NotificationBroadCast;
import com.example.myapplication.database.DataManager;
import com.example.myapplication.fragment.AllSongFragment;
import com.example.myapplication.fragment.MediaPlaybackFragment;
import com.example.myapplication.listenner.IDatabaseListenner;
import com.example.myapplication.listenner.INotificationBroadCastListener;
import com.example.myapplication.util.Util;
import com.example.myapplication.util.LogSetting;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AllSongFragment.IAllSongFragmentListener
        , MediaPlaybackFragment.IMediaPlayFragmentListenner, INotificationBroadCastListener, IDatabaseListenner {

    public static final String TAG_MAIN = "BachNN_MAIN";
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 123;
    private static final int ALL_MUSIC = 0;
    private static final int ALL_FAVOURITE_MUSIC = 1;
    private static final int MUSIC_LIBRARY = 2;
    private MediaPlaybackFragment mMediaPlayer;
    private FragmentTransaction mTransaction;
    private Intent mIntent;
    private MusicService mMusicService;
    private FragmentManager mFragmentManager;
    public boolean isVertical = false;
    private boolean mCheck = false;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    //BachNN : ahead of Navigation
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
    private View mFavouriteLayout;
    /*
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mLocalMusic = (MusicService.LocalMusic) iBinder;
            mMusicService = mLocalMusic.getInstanceService();
            createFragment();
            if (LogSetting.IS_DEBUG) {
                Log.d(TAG_MAIN, mLocalMusic + "  Connnect");
            }
        }


        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMusicService = null;
        }
    };

    public MusicService getmMusicService() {
        return mMusicService;
    }

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
        mFragmentManager = getSupportFragmentManager();

        mIntent = new Intent(MainActivity.this, MusicService.class);
        startService(mIntent);
        mDatabase = new DataManager(this);
        // register BroadCast.
        mBroadCast = new NotificationBroadCast(this);
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Util.ACTION_NEXT);
        mFilter.addAction(Util.ACTION_PLAY);
        mFilter.addAction(Util.ACTION_PREVIOUS);
        mFilter.addAction(Util.ACTION_AUTONEXT);
        this.registerReceiver(mBroadCast, mFilter);
        //createFragment();

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        }

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
     * khơi tạo tất cả các thuộc tính cần thiến khi chay Activity
     * và kính hoạt Fragment , Service , Database
     */
    private void createFragment() {
        if (findViewById(R.id.vertical_Screen) != null) isVertical = true;


        // start service.
//        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        // todo xoa no di
        if (isVertical) {
            AllSongFragment allSongFragment = new AllSongFragment();
            allSongFragment.setmMusicService(mMusicService);
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.all_Song_Fragment, allSongFragment);
            ft.commit();
            //BachNN :DrawerLayout and Navigation
            mToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            //todo hoi mr Thanh.
            mDrawerLayout = findViewById(R.id.vertical_Screen);
            mToggle = new ActionBarDrawerToggle(MainActivity.this
                    , mDrawerLayout, mToolbar, R.string.open_navigation, R.string.close_navigetion);
            mDrawerLayout.addDrawerListener(mToggle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            mToggle.syncState();
            mNavigationView = findViewById(R.id.navigation_Vew);
            mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.setting:
                            break;
                        case R.id.item_help:
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
            if (LogSetting.IS_DEBUG) {
                Log.d(TAG_MAIN, mLocalMusic + "  DisConnnect");
            }
            MediaPlaybackFragment mediaPlaybackFragment = mLocalMusic.getMediaPlaybachFragment();
            //todo xoa pham tren di
            mediaPlaybackFragment.setMusicService(mMusicService);
            FragmentTransaction layer = mFragmentManager.beginTransaction();
            layer.replace(R.id.music_Player, mediaPlaybackFragment);
            layer.addToBackStack(null);
            layer.commit();

            AllSongFragment allSongFragment = new AllSongFragment();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.all_Song_Fragment, allSongFragment);
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
                setChangeAheader(0);
                //new AllFavouriteMusic().execute();
                mDrawerLayout.closeDrawers();
                mFavourite = true;

            }
        });

        mRecentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setChangeAheader(1);
                mDrawerLayout.closeDrawers();
            }
        });

        mFavouriteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FavoriteSongsFragment favoriteSongsFragment = new FavoriteSongsFragment();
                mTransaction = mFragmentManager.beginTransaction();
                mTransaction.replace(R.id.all_Song_Fragment, favoriteSongsFragment);
                mTransaction.addToBackStack(null);
                mTransaction.commit();
                mDrawerLayout.closeDrawers();
            }
        });

        mMusicLibraryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setChangeAheader(2);

                mDrawerLayout.closeDrawers();
            }
        });

    }

    public DataManager getDatabase() {
        return mDatabase;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * BachNN
     *
     * @param i là vị trí mà người dụng chọn trên aheader Layout.
     */
    private void setChangeAheader(int i) {
        if (i == mPositionAHeader) return;
        switch (i) {
            case ALL_MUSIC:
                mListenImage.setImageResource(R.drawable.custom_music_player);
                mListenTextView.setTextColor(getResources().getColor(R.color.ogin));
                mListenLayout.setBackgroundColor(getResources().getColor(R.color.white_color));
                break;
            case ALL_FAVOURITE_MUSIC:
                mRecentImage.setImageResource(R.drawable.ic_replay_10);
                mRecentTextView.setTextColor(getResources().getColor(R.color.ogin));
                mRecentLayout.setBackgroundColor(getResources().getColor(R.color.white_color));
                break;
            case MUSIC_LIBRARY:
                mMusicLibraryImage.setImageResource(R.drawable.ic_library_music_24);
                mMusicLibraryTextView.setTextColor(getResources().getColor(R.color.ogin));
                mMusicLibraryLayout.setBackgroundColor(getResources().getColor(R.color.white_color));
                break;
        }

        switch (mPositionAHeader) {
            case ALL_MUSIC:
                mListenImage.setImageResource(R.drawable.ic_music_note_24);
                mListenTextView.setTextColor(getResources().getColor(R.color.grey_color));
                mListenLayout.setBackgroundColor(getResources().getColor(R.color.ogin));
                break;
            case ALL_FAVOURITE_MUSIC:
                mRecentImage.setImageResource(R.drawable.ic_10_previous);
                mRecentTextView.setTextColor(getResources().getColor(R.color.grey_color));
                mRecentLayout.setBackgroundColor(getResources().getColor(R.color.ogin));
                break;
            case MUSIC_LIBRARY:
                mMusicLibraryImage.setImageResource(R.drawable.ic_baseline_library_music_24);
                mMusicLibraryTextView.setTextColor(getResources().getColor(R.color.grey_color));
                mMusicLibraryLayout.setBackgroundColor(getResources().getColor(R.color.ogin));
                break;
        }
        mPositionAHeader = i;

    }


    /**
     * BachNN
     * hiện thi ra MediaPlaybackFragment.
     */
    @Override
    public void show() {
        MediaPlaybackFragment mediaPlaybackFragment = mLocalMusic.getMediaPlaybachFragment();
        mTransaction = mFragmentManager.beginTransaction();
        mTransaction.replace(R.id.all_Song_Fragment, mediaPlaybackFragment);
        mTransaction.addToBackStack(null);
        mTransaction.commit();
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
        if (!isVertical) {
            AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            // todo xoa di nhe
            allSongFragment.setData(mMusicService.getmSongs());
            allSongFragment.setImageMusic();
            mLocalMusic.setPreviousMusicNotification();
        }
    }

    /**
     * BachNN
     * play bài hày hoặc puase
     * set lại giao dien AllSongFragmen và Notification.
     */
    @Override
    public void onPlay() {
        if (!isVertical) {
            AllSongFragment mAllSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            mAllSongFragment.isPlayMusic(!mMusicService.isMusicPlaying());
            mLocalMusic.setPlayMusicNoti();
        }
    }

    /**
     * BachNN
     * next sang bài hat tiếp theo
     * set lại giao dien AllSongFragmen và Notification.
     */
    @Override
    public void onNext() {
        if (!isVertical) {
            AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            // todo xoa di nhe
            allSongFragment.setData(mMusicService.getmSongs());
            allSongFragment.setImageMusic();
            mLocalMusic.setNextMusicNotification();
        }

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
        if (!isVertical) {
            AllSongFragment songFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            // todo ko can cai nay
            songFragment.setTitle(mMusicService.getSongIsPlay());
        }
    }

    public boolean isVertical() {
        return isVertical;
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
        mMusicService.onNextMusic();
        if (!isVertical){
            AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            allSongFragment.setSelection(mMusicService.getmCurrentSong());
            allSongFragment.isPlayMusic(true);

            MediaPlaybackFragment player = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.music_Player);
            player.setMusicService(mMusicService);
            return;
        }
        if (getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment) instanceof AllSongFragment) {
            AllSongFragment mAllSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            mAllSongFragment.setUIAllView();
        }
        if (getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment) instanceof MediaPlaybackFragment) {
            MediaPlaybackFragment mMediaPlaybackFragment =
                    (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            mMediaPlaybackFragment.setStatusIcon(mMusicService.isMusicPlaying());
            mMediaPlaybackFragment.setTile(mMusicService.getSongIsPlay());
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
        mMusicService.onPreviousMusic();
        if (!isVertical){
            AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            allSongFragment.setSelection(mMusicService.getmCurrentSong());
            allSongFragment.isPlayMusic(true);

            MediaPlaybackFragment player = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.music_Player);
            player.setMusicService(mMusicService);
            return;
        }
        if (getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment) instanceof AllSongFragment) {
            AllSongFragment mAllSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            mAllSongFragment.setData(mMusicService.getmSongs());
            mAllSongFragment.setTitle(mMusicService.getSongIsPlay());
            mAllSongFragment.isPlayMusic(mMusicService.isMusicPlaying());
            mAllSongFragment.setImageMusic();
        }
        if (getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment) instanceof AllSongFragment) {
            MediaPlaybackFragment mMediaPlaybackFragment = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            if (mMediaPlaybackFragment != null) {
                mMediaPlaybackFragment.setStatusIcon(mMusicService.isMusicPlaying());
                mMediaPlaybackFragment.setTile(mMusicService.getSongIsPlay());
                mMediaPlaybackFragment.setImagePlayer();
            }
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
        if (mMusicService.isMusicPlaying()) {
            mMusicService.onStopMusic();
        } else {
            mMusicService.onResumeMusic();
        }
        if (!isVertical){
            AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            allSongFragment.setSelection(mMusicService.getmCurrentSong());
            allSongFragment.isPlayMusic(true);

            MediaPlaybackFragment player = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.music_Player);
            player.setMusicService(mMusicService);
            return;
        }
        if (getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment) instanceof AllSongFragment) {
            AllSongFragment mAllSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            mAllSongFragment.isPlayMusic(mMusicService.isMusicPlaying());
        }

        if (getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment) instanceof MediaPlaybackFragment) {
            MediaPlaybackFragment mMediaPlaybackFragment = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            if (mMediaPlaybackFragment != null) {
                mMediaPlaybackFragment.setStatusIcon(mMusicService.isMusicPlaying());
            }
        }
        mLocalMusic.setPlayMusic();
    }

    @Override
    public void onPlayMusicAutoNextBroadCast() {
        if (!isVertical){
            AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            allSongFragment.setSelection(mMusicService.getmCurrentSong());
            allSongFragment.isPlayMusic(true);

            MediaPlaybackFragment player = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.music_Player);
            player.setMusicService(mMusicService);
            return;
        }
        if (getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment) instanceof AllSongFragment) {
            AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            allSongFragment.setSelection(mMusicService.getmCurrentSong());
            allSongFragment.isPlayMusic(true);
        }
        if (!isVertical) {
            if (getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment) instanceof AllSongFragment) {
                MediaPlaybackFragment player = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
                player.setMusicService(mMusicService);
            }
        }
        mLocalMusic.setNextMusicNotification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void addFavouriteMusic(Song song) {
//        new AddFavouriteMusic().execute(song);
    }

    @Override
    public void deleteFavouriteMusic(int id) {
//        new DeleteFavouriteMusic().execute(id);
    }

    @Override
    public void getAllFavouriteMusic() {
    }

}