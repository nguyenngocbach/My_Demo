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
import android.content.res.Configuration;
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

import com.example.myapplication.fragment.FavoriteSongsFragment;
import com.example.myapplication.Service.MusicService;
import com.example.myapplication.broadcast.NotificationBroadCast;
import com.example.myapplication.database.DataManager;
import com.example.myapplication.fragment.AllSongFragment;
import com.example.myapplication.fragment.MediaPlaybackFragment;
import com.example.myapplication.listenner.INotificationBroadCastListener;
import com.example.myapplication.util.Util;
import com.example.myapplication.util.LogSetting;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements AllSongFragment.IAllSongFragmentListener
        , MediaPlaybackFragment.IMediaPlayFragmentListenner, INotificationBroadCastListener {

    public static final String TAG_MAIN = "BachNN_MAIN";
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 123;
    private static final int ALL_MUSIC = 0;
    private static final int ALL_FAVOURITE_MUSIC = 1;
    private static final int MUSIC_LIBRARY = 2;
    //Bkav Thanhnch: sai convention -ok
    public boolean mIsVertical = false;
    private FragmentTransaction mTransaction;
    private Intent mIntent;
    private MusicService mMusicService;
    private FragmentManager mFragmentManager;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    //BachNN : ahead of Navigation
    private TextView mListenTextView;
    //Bkav Thanhnch: Khong dung?
    private TextView mRecentTextView;
    private TextView mMusicLibraryTextView;
    private ImageView mListenImage;
    //Bkav Thanhnch: Khong dung?
    private ImageView mRecentImage;
    private ImageView mMusicLibraryImage;
    private LinearLayout mListenLayout;
    //Bkav Thanhnch: Khong dung?
    private LinearLayout mFavouriteLayout;
    private LinearLayout mMusicLibraryLayout;
    private int mPositionAHeader = 0;
    private NotificationBroadCast mBroadCast;
    private MusicService.LocalMusic mLocalMusic;
    private DataManager mDatabase;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //Bkav Thanhnch: Bien nay la gi? CODE linh tinh?
            mLocalMusic = (MusicService.LocalMusic) iBinder;
            mMusicService = ((MusicService.LocalMusic) iBinder).getInstanceService();
            //Bkav Thanhnch: tao fragment gi day? dat ten khong chinh xac.
            createAllFragmentMusic();
        }


        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMusicService = null;
        }
    };

    public MusicService getMusicService() {
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
        //Bkav Thanhnch: Khong co comment ai code
        //BachNN : register BroadCast.
        mBroadCast = new NotificationBroadCast(this);
        //BachNN tao ra IntentFilter để lưu các action.
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Util.ACTION_NEXT);
        mFilter.addAction(Util.ACTION_PLAY);
        mFilter.addAction(Util.ACTION_PREVIOUS);
        mFilter.addAction(Util.ACTION_AUTONEXT);
        //BachNN đăng ký cac Action qua mFilter;
        this.registerReceiver(mBroadCast, mFilter);

        //Bkav Thanhnch: Khong co comment ai code
        //BachNN : kiêm tra xem đã có quyền truy cập hay chưa.
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            startService(mIntent);
            bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        }

    }

    /**Bkav Thanhnch: Khong co comment ai code

     * @param requestCode
     * @param permissions
     * @param grantResults ..
     *                     xin quyêm truy cập để lấy các bài hát.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startService(mIntent);
                    bindService(mIntent, mConnection, BIND_AUTO_CREATE);
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
    private void createAllFragmentMusic() {
        //Bkav Thanhnch: tai sao lai code the nay?
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mIsVertical = false;
        } else {
            mIsVertical = true;
        }
        if (mIsVertical) {
            AllSongFragment allSongFragment = new AllSongFragment();
            allSongFragment.setMusicService(mMusicService);
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.all_Song_Fragment, allSongFragment);
            ft.commit();
            //BachNN :DrawerLayout and Navigation
            mToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            mDrawerLayout = findViewById(R.id.vertical_Screen);
            mToggle = new ActionBarDrawerToggle(MainActivity.this
                    , mDrawerLayout, mToolbar, R.string.open_navigation, R.string.close_navigetion);
            mDrawerLayout.addDrawerListener(mToggle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            mToggle.syncState();
            mNavigationView = findViewById(R.id.navigation_Vew);
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
            MediaPlaybackFragment mediaPlaybackFragment = new MediaPlaybackFragment();
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
        //Bkav Thanhnch: Sao lai get ra gia tri 0.
        //BachNN : get = 0 để lấy cái view của Navigation thì mới ánh xa đc.
        View mView = mNavigationView.getHeaderView(0);
        mListenTextView = mView.findViewById(R.id.txt_listen_now);
        mMusicLibraryTextView = mView.findViewById(R.id.txt_music_library);
        mListenImage = mView.findViewById(R.id.icon_listen_now);
        mMusicLibraryImage = mView.findViewById(R.id.icon_muic_library);
        mListenLayout = mView.findViewById(R.id.listen_now);
        mFavouriteLayout = mView.findViewById(R.id.favourite_layout);
        mMusicLibraryLayout = mView.findViewById(R.id.music_library);
        mRecentTextView= mView.findViewById(R.id.favourite_textview);
        mRecentImage= mView.findViewById(R.id.favourite_icon);

        mListenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bkav Thanhnch: Sao lai truyen vao gia tri 0
                //BachNN : 0 là set backGround cho Music Now Navigation
                setChangeAHeader(ALL_MUSIC);
                mDrawerLayout.closeDrawers();

            }
        });

        mFavouriteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bkav Thanhnch: Sao lai truyen vao gia tri 1
                //BachNN : 1 là set backGround cho FavouriteSong Navigation
                setChangeAHeader(ALL_FAVOURITE_MUSIC);
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
                //Bkav Thanhnch: Sao lai truyen vao gia tri 2
                //BachNN : 2 là set backGround cho Music Library Navigation
                setChangeAHeader(MUSIC_LIBRARY);
                mDrawerLayout.closeDrawers();
            }
        });

    }

    public DataManager getDatabase() {
        return mDatabase;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    /**
     * BachNN
     *Bkav Thanhnch: setChangeAheader-> sai ten?
     * @param i là vị trí mà người dụng chọn trên aheader Layout.
     */
    private void setChangeAHeader(int i) {
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
                mFavouriteLayout.setBackgroundColor(getResources().getColor(R.color.white_color));
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
                mListenLayout.setBackgroundColor(getResources().getColor(R.color.white_color));
                break;
            case ALL_FAVOURITE_MUSIC:
                mRecentImage.setImageResource(R.drawable.ic_10_previous);
                mRecentTextView.setTextColor(getResources().getColor(R.color.grey_color));
                mFavouriteLayout.setBackgroundColor(getResources().getColor(R.color.white_color));
                break;
            case MUSIC_LIBRARY:
                mMusicLibraryImage.setImageResource(R.drawable.ic_baseline_library_music_24);
                mMusicLibraryTextView.setTextColor(getResources().getColor(R.color.grey_color));
                mMusicLibraryLayout.setBackgroundColor(getResources().getColor(R.color.white_color));
                break;
        }
        mPositionAHeader = i;

    }


    /**Bkav Thanhnch: Ten khong ro rang.
     * BachNN
     * hiện thi ra MediaPlaybackFragment.
     */
    @Override
    public void showMediaPlaybackFragment() {
        MediaPlaybackFragment mediaPlaybackFragment = new MediaPlaybackFragment();
        mTransaction = mFragmentManager.beginTransaction();
        mTransaction.replace(R.id.all_Song_Fragment, mediaPlaybackFragment);
        mTransaction.addToBackStack(null);
        mTransaction.commit();
    }

    @Override
    public void setIconNotification() {
        mMusicService.setPlayMusic();
    }


    /**
     * BachNN
     * quay lại bàn hái
     * set lại giao dien AllSongFragment và Notification.
     */
    @Override
    public void onPrevious() {
        if (!mIsVertical) {
            AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            // todo xoa di nhe
            allSongFragment.setData(mMusicService.getmSongs());
            allSongFragment.setImageMusic();
            mMusicService.setPreviousMusicNotification();
        }
    }
    /**
     * BachNN
     * play bài hày hoặc puase
     * set lại giao dien AllSongFragmen và Notification.
     */
    @Override
    public void onPlay() {
        if (!mIsVertical) {
            AllSongFragment mAllSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            mAllSongFragment.setButtonIconPlayMusic(!mMusicService.isMusicPlaying());
            mMusicService.setPlayMusicNoti();
        }
    }

    /**
     * BachNN
     * next sang bài hat tiếp theo
     * set lại giao dien AllSongFragmen và Notification.
     */
    @Override
    public void onNext() {
        if (!mIsVertical) {
            AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            allSongFragment.setData(mMusicService.getmSongs());
            allSongFragment.setImageMusic();
            mMusicService.setNextMusicNotification();
        }

    }

    /**
     * BachNN
     *  hàm nay dung de set lại title lại chi AllSongFragment.
     */
    //Bkav Thanhnch:  thieu comment
    @Override
    public void onSeekBar() {
        if (!mIsVertical) {
            AllSongFragment songFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            songFragment.setTitle(mMusicService.getSongIsPlay());
        }
    }

    public boolean isVertical() {
        return mIsVertical;
    }

    /**
     * BachNN
     * next bài hát vào tiếp thep ,
     * và set lai giao diện của AllFragment và Notification.
     */
    @Override
    public void onNextMusicBroadCast() {
        mMusicService.onNextMusic();
        if (getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment) instanceof AllSongFragment) {
            AllSongFragment mAllSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            mAllSongFragment.setUIAllView();
        }
        if (getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment) instanceof MediaPlaybackFragment) {
            MediaPlaybackFragment mediaPlaybackFragment= (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            mediaPlaybackFragment.setUIMusic();
        }
        mMusicService.setNextMusicNotification();
    }

    /**
     * BachNN
     * quay lại bài hát gần nhất ,
     * set lai giao diện của AllFragment và Notification.
     */
    @Override
    public void onPreviousMusicBroadCast() {
        mMusicService.onPreviousMusic();
        if (getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment) instanceof AllSongFragment) {
            AllSongFragment mAllSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            mAllSongFragment.setData(mMusicService.getmSongs());
            mAllSongFragment.setTitle(mMusicService.getSongIsPlay());
            mAllSongFragment.setButtonIconPlayMusic(mMusicService.isMusicPlaying());
            mAllSongFragment.setImageMusic();
        }
        if (getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment) instanceof MediaPlaybackFragment) {
            MediaPlaybackFragment mediaPlaybackFragment= (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            mediaPlaybackFragment.setUIMusic();
        }
        mMusicService.setPreviousMusicNotification();
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
        if (getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment) instanceof AllSongFragment) {
            AllSongFragment mAllSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            mAllSongFragment.setButtonIconPlayMusic(mMusicService.isMusicPlaying());
        }

        if (getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment) instanceof MediaPlaybackFragment) {
            MediaPlaybackFragment mediaPlaybackFragment= (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            mediaPlaybackFragment.setUIMusic();
        }
        mMusicService.setPlayMusic();
    }

    /**
     * BachNN
     * hàm này tự động next bài hát khi chay hết bài
     */
    @Override
    public void onPlayMusicAutoNextBroadCast() {
        if (getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment) instanceof AllSongFragment) {
            AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            allSongFragment.setSelection(mMusicService.getmCurrentSong());
            allSongFragment.setButtonIconPlayMusic(true);
        }
        if (getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment) instanceof MediaPlaybackFragment) {
            MediaPlaybackFragment mediaPlaybackFragment= (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.all_Song_Fragment);
            mediaPlaybackFragment.setUIMusic();
        }
        mMusicService.setNextMusicNotification();
    }

}