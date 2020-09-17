package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Model.Song;
import com.example.myapplication.Service.MusicManager;
import com.example.myapplication.Service.MusicService;
import com.example.myapplication.broadcast.NotificationBroadCast;
import com.example.myapplication.fragment.AllSongFragment;
import com.example.myapplication.fragment.MediaPlaybackFragment;
import com.example.myapplication.listenner.IMusicListenner;
import com.example.myapplication.listenner.INotificationBroadCastListener;
import com.example.myapplication.unit.Coast;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements IMusicListenner, AllSongFragment.AllSongFragmentListenner
        , MediaPlaybackFragment.IMediaPlayFragmentListenner, INotificationBroadCastListener {

    private static final int MY_PERMISSION_REQUEST = 123;
    private static final String KEY_MUSIC_MANAGER = "com.example.myapplication.musicManager";
    private static final String ID_CHANNEL = "1999";
    private MediaPlaybackFragment mMediaPlayer;
    private FragmentTransaction mTransaction;
    private Intent mIntent;
    private MusicService mMusicService;
    private MusicManager mMusicManager;
    private FragmentManager mFragmentManager;
    private boolean isVertical = false;
    private boolean check = false;
    private Song mSong;
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

    /*
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mLocalMusic = (MusicService.LocalMusic) iBinder;
            mMusicService = mLocalMusic.getInstanceService();
            if (mMusicManager == null) {
                mMusicManager = mMusicService.getMusicManager();
                //onCreateNotification();
            }
            if (!check) {
                if (isVertical) {
                    AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
                    Log.d("bachdz", mMusicManager + "  onServiceConnected" + mMusicManager.getmSongs().size());
                    allSongFragment.setSongManager(mMusicManager);
                    allSongFragment.setData(mMusicManager.getmSongs());
                    allSongFragment.isPlayMusic(mMusicManager.isMusicPlaying());
                    allSongFragment.setTitle(mMusicManager.getSongIsPlay());

                } else {
                    AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
                    MediaPlaybackFragment playbackFragment = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
                    allSongFragment.setData(mMusicManager.getmSongs());
                    allSongFragment.setSongManager(mMusicManager);
                    allSongFragment.setData(mMusicManager.getmSongs());
                    allSongFragment.isPlayMusic(mMusicManager.isMusicPlaying());
                    allSongFragment.setTitle(mMusicManager.getSongIsPlay());
                    allSongFragment.setVisible();
                    // playbackFragment.setMusicManager(musicManager);
                }
            }
            //Log.d("bachdz","onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMusicService = null;
            mMusicManager = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            check = true;
            mMusicManager = (MusicManager) savedInstanceState.getSerializable(KEY_MUSIC_MANAGER);
            Log.d("bachdz", check + " savedInstanceState" + mMusicManager);
        }

        // if check == true thì ta set các giá trị cho 2 fragment luôn
        // còn là false thì ta phải set giá tri cho 2 fragment in mConnection.
        mFragmentManager = getSupportFragmentManager();
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // start service.
        mIntent = new Intent(MainActivity.this, MusicService.class);
        startService(mIntent);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        // register BroadCast.
        mBroadCast = new NotificationBroadCast(this);
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Coast.ACTION_NEXT);
        mFilter.addAction(Coast.ACTION_PLAY);
        mFilter.addAction(Coast.ACTION_PREVIOUS);
        this.registerReceiver(mBroadCast, mFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCast, mFilter);

        if (findViewById(R.id.vertical_Screen) != null) isVertical = true;
        if (isVertical) {
            AllSongFragment allSongFragment =AllSongFragment.getInstance();
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
            MediaPlaybackFragment mediaPlaybackFragment = MediaPlaybackFragment.getInstance();
            FragmentTransaction layer = mFragmentManager.beginTransaction();
            layer.replace(R.id.musicPlayer, mediaPlaybackFragment);
            layer.addToBackStack(null);
            layer.commit();

            AllSongFragment allSongFragment = AllSongFragment.getInstance();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.allSongFragment, allSongFragment);
            ft.commit();
        }

        Log.d("bachdz", "onCreate");
    }


    /**
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
        mMusicLibraryLayout = mView.findViewById(R.id.music_library);

        mListenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Listen_Now", Toast.LENGTH_SHORT).show();
                setChangeAheader(0);
            }
        });

        mRecentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Recent", Toast.LENGTH_SHORT).show();
                setChangeAheader(1);
            }
        });

        mMusicLibraryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Music Library", Toast.LENGTH_SHORT).show();
                setChangeAheader(2);
            }
        });

    }


    /**
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
        Log.d("bachdz", "onStart");
        if (check) {
            AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
            allSongFragment.setData(mMusicManager.getmSongs());
            allSongFragment.setSongManager(mMusicManager);
            allSongFragment.setTitle(mMusicManager.getSongIsPlay());
            if (!isVertical) {
                Log.d("bachNgoc", mMusicManager + "");
                MediaPlaybackFragment mediaPlaybackFragment = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
                allSongFragment.setVisible();
                mediaPlaybackFragment.setMusicManager(mMusicManager);
            } else allSongFragment.isPlayMusic(mMusicManager.isMusicPlaying());
        }
        if (mTransaction != null && isVertical) {
            Log.d("YeuEm", "ok ++" + mTransaction);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("bachdz", "onResume");
    }

    public MusicManager getMusicManager() {
        return mMusicManager;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(KEY_MUSIC_MANAGER, mMusicManager);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No permission granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    /**
     * @param i là vị trí bạn hát được chọn trongn AllFragment ,
     *          và sẽ set lại ban hát cho đung  vị trí của i và cập nhập lại giao diện
     */
    @Override
    public void selectMusic(int i) {
        AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        allSongFragment.setSelection(i);
        allSongFragment.isPlayMusic(true);
        //???????????
        //????????????
        Log.d("bachNgoc", mMusicManager + "");
        if (mMusicManager != null) {
            if (mMusicManager.isMusicPlaying()) {
                Log.d("bachNgoc", mMusicManager.isMusicPlaying() + "");
                mMusicManager.onResetMusic();
            }
            mMusicManager.setmCurrentSong(i);
            mMusicManager.onPlayMusic();
        }
        if (!isVertical) {
            MediaPlaybackFragment player = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
            //player.setTile(musicManager.getSinpleSong(i));
            player.setMusicManager(mMusicManager);
        }
        mLocalMusic.setNextMusicNotification();
    }

    /**
     * hiện thi ra MediaPlaybackFragment.
     */
    @Override
    public void show() {
        if (isVertical) {
            mMediaPlayer = MediaPlaybackFragment.getInstance(mMusicManager);
            mTransaction = mFragmentManager.beginTransaction();
            mTransaction.replace(R.id.musicPlayer, mMediaPlayer);
            mTransaction.addToBackStack(null);
            mTransaction.commit();
        } else {
            mMediaPlayer = new MediaPlaybackFragment();
            mTransaction = mFragmentManager.beginTransaction();
            mTransaction.replace(R.id.musicPlayer, mMediaPlayer);
            mTransaction.addToBackStack(null);
            mTransaction.commit();
        }
    }

    private void setInitially() {
        AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        MediaPlaybackFragment playbackFragment = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
        Log.d("bachdz", "" + mMusicManager);
        if (isVertical) {
            allSongFragment.setData(mMusicManager.getmSongs());
            allSongFragment.setSongManager(mMusicManager);
            allSongFragment.setData(mMusicManager.getmSongs());
            allSongFragment.isPlayMusic(mMusicManager.isMusicPlaying());
            allSongFragment.setTitle(mMusicManager.getSongIsPlay());
        } else {
            allSongFragment.setData(mMusicManager.getmSongs());
            allSongFragment.setSongManager(mMusicManager);
            allSongFragment.setData(mMusicManager.getmSongs());
            allSongFragment.isPlayMusic(mMusicManager.isMusicPlaying());
            allSongFragment.setTitle(mMusicManager.getSongIsPlay());

            playbackFragment.setMusicManager(mMusicManager);
        }

    }

    /**
     *
     */
    @Override
    public void onLike() {

    }

    /**
     * quay lại bàn hái
     * set lại giao dien AllSongFragmen và Notification.
     */
    @Override
    public void onPrevious() {
        AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        allSongFragment.setData(mMusicManager.getmSongs());
        mLocalMusic.setPreviousMusicNotification();
    }

    /**
     * play bài hày hoặc puase
     * set lại giao dien AllSongFragmen và Notification.
     */
    @Override
    public void onPlay() {
        AllSongFragment mAllSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        mAllSongFragment.isPlayMusic(!mMusicManager.isMusicPlaying());
        mLocalMusic.setPlayMusic();
    }

    /**
     * next sang bài hat tiếp theo
     * set lại giao dien AllSongFragmen và Notification.
     */
    @Override
    public void onNext() {
//        if (musicManager!=null){
//            musicManager.onNext();
//        }
        AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        allSongFragment.setData(mMusicManager.getmSongs());
//        MediaPlaybackFragment fragment= (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
//        fragment.setTile(song);
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
        //songFragment.setSongManager(musicManager);
        songFragment.setTitle(mMusicManager.getSongIsPlay());
    }

    public boolean isVertical() {
        return isVertical;
    }

    public boolean isCheck() {
        return check;
    }


    /**
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
        MediaPlaybackFragment mMediaPlaybackFragment= (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
        if (mMediaPlaybackFragment!=null){
            Log.d("icon",""+mMediaPlaybackFragment);
            mMediaPlaybackFragment.setStatusIcon(mMusicManager.isMusicPlaying());
            mMediaPlaybackFragment.setTile(mMusicManager.getSongIsPlay());
        }
        mLocalMusic.setNextMusicNotification();
    }

    /**
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
        MediaPlaybackFragment mMediaPlaybackFragment= (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
        if (mMediaPlaybackFragment!=null){
            Log.d("icon",""+mMediaPlaybackFragment);
            mMediaPlaybackFragment.setStatusIcon(mMusicManager.isMusicPlaying());
            mMediaPlaybackFragment.setTile(mMusicManager.getSongIsPlay());
        }
        mLocalMusic.setPreviousMusicNotification();
    }

    /**
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

        MediaPlaybackFragment mMediaPlaybackFragment= (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
        if (mMediaPlaybackFragment!=null){
            mMediaPlaybackFragment.setStatusIcon(mMusicManager.isMusicPlaying());
        }
        mLocalMusic.setPlayMusic();
    }


}