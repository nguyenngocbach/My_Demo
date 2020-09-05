package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.Model.Song;
import com.example.myapplication.Service.MusicManager;
import com.example.myapplication.Service.MusicService;
import com.example.myapplication.fragment.AllSongFragment;
import com.example.myapplication.fragment.MediaPlaybackFragment;
import com.example.myapplication.listenner.IMusicListenner;

public class MainActivity extends AppCompatActivity implements IMusicListenner, AllSongFragment.AllSongFragmentListenner
        , MediaPlaybackFragment.IMediaPlayFragmentListenner {

    private static final int MY_PERMISSION_REQUEST = 123;
    private static final String KEY_MUSIC_MANAGER = "com.example.myapplication.musicManager";
    private Intent mIntent;
    private MusicService mMusicService;
    private MusicManager mMusicManager;
    private FragmentManager mFragmentManager;

    private boolean isVertical = false;
    private boolean mCheck = false;
    private Song song;

    private MediaPlaybackFragment mMediaFragment;
    private FragmentTransaction mFragmentTransaction;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.LocalMusic localMusic = (MusicService.LocalMusic) iBinder;
            mMusicService = localMusic.getInstanceService();
            if (mMusicManager == null) {
                mMusicManager = mMusicService.getmMusicManager();
            }
            if (!mCheck) {
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
                    playbackFragment.setmMusicManager(mMusicManager);
                }
            }
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
            mCheck = true;
            mMusicManager = (MusicManager) savedInstanceState.getSerializable(KEY_MUSIC_MANAGER);
            Log.d("bachdz", mCheck + " savedInstanceState" + mMusicManager);
        }


        mFragmentManager = getSupportFragmentManager();
        mIntent = new Intent(MainActivity.this, MusicService.class);
        startService(mIntent);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);

        if (findViewById(R.id.vertical_Screen) != null) isVertical = true;
        if (isVertical) {
            AllSongFragment mAllSongFragment = new AllSongFragment();
            FragmentTransaction allSongTranasction = mFragmentManager.beginTransaction();
            allSongTranasction.replace(R.id.allSongFragment, mAllSongFragment);
            allSongTranasction.commit();
        } else {
            MediaPlaybackFragment mediaPlaybackFragment = new MediaPlaybackFragment();
            FragmentTransaction mMediaPlayTransaction = mFragmentManager.beginTransaction();
            mMediaPlayTransaction.replace(R.id.musicPlayer, mediaPlaybackFragment);
            mMediaPlayTransaction.addToBackStack(null);
            mMediaPlayTransaction.commit();

            AllSongFragment allSongFragment = new AllSongFragment();
            FragmentTransaction mAllSongTransaction = mFragmentManager.beginTransaction();
            mAllSongTransaction.replace(R.id.allSongFragment, allSongFragment);
            mAllSongTransaction.commit();

        }

        Log.d("bachdz", "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("bachdz", "onStart");
        if (mCheck) {
            AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
            allSongFragment.setData(mMusicManager.getmSongs());
            allSongFragment.setSongManager(mMusicManager);
            allSongFragment.setTitle(mMusicManager.getSongIsPlay());
            if (!isVertical) {
                Log.d("bachNgoc", mMusicManager + "");
                MediaPlaybackFragment mediaPlaybackFragment = (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
                allSongFragment.setVisible();
                mediaPlaybackFragment.setmMusicManager(mMusicManager);
            } else allSongFragment.isPlayMusic(mMusicManager.isMusicPlaying());
        }
        if (mFragmentTransaction != null && isVertical) {
            Log.d("YeuEm", "ok ++" + mFragmentTransaction);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("bachdz", "onResume");
    }

    public MusicManager getmMusicManager() {
        return mMusicManager;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(KEY_MUSIC_MANAGER, mMusicManager);
        outState.putString("bachdz", "Nguyen Ngoc Bach");
        Log.d("bachdz", "outState " + mMusicManager);
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


    @Override
    public void selectMusic(int i) {
        AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        allSongFragment.setSelection(i);
        allSongFragment.isPlayMusic(true);

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
            player.setmMusicManager(mMusicManager);



        }

    }

    @Override
    public void show() {
        if (isVertical) {
            mMediaFragment = MediaPlaybackFragment.getInstance(mMusicManager);
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.musicPlayer_ok, mMediaFragment);
            mFragmentTransaction.addToBackStack(null);
            mFragmentTransaction.commit();
        } else {
            mMediaFragment = new MediaPlaybackFragment();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.musicPlayer_ok, mMediaFragment);
            mFragmentTransaction.addToBackStack(null);
            mFragmentTransaction.commit();
        }
    }


    private void setInitially() {
        AllSongFragment allSongFragment = (AllSongFragment)
                getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        MediaPlaybackFragment playbackFragment = (MediaPlaybackFragment)
                getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
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
            playbackFragment.setmMusicManager(mMusicManager);
        }

    }


    @Override
    public void onLike() {
    }

    @Override
    public void onPrevious() {
        AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        allSongFragment.setData(mMusicManager.getmSongs());
    }

    @Override
    public void onPlay() {
    }

    @Override
    public void onNext() {
//        if (musicManager!=null){
//            musicManager.onNext();
//        }
        AllSongFragment allSongFragment = (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        allSongFragment.setData(mMusicManager.getmSongs());

//        MediaPlaybackFragment fragment= (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
//        fragment.setTile(song);
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
        return mCheck;
    }

//    @Override
//    public void musicRun() {
//
//    }

}