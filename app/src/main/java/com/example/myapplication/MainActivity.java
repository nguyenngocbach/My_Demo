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
import com.example.myapplication.listenner.MusicListenner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MusicListenner , AllSongFragment.AllSongFragmentListenner , MediaPlaybackFragment.MediaPlayFragmentListenner {

    private static final int MY_PERMISSION_REQUEST = 123;
    private static final String KEY_MUSIC_MANAGER ="com.example.myapplication.musicManager" ;
    private Intent intent;
    private MusicService musicService;
    private MusicManager musicManager;
    private FragmentManager fragmentManager;

    private boolean isVertical=false;
    private boolean check=false;
    private Song song;

    private ServiceConnection mConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.LocalMusic localMusic = (MusicService.LocalMusic) iBinder;
            musicService= localMusic.getInstanceService();
            if (musicManager==null){
                musicManager= musicService.getMusicManager();
            }
            if (isVertical){
                AllSongFragment allSongFragment= (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
                Log.d("bachdz",musicManager+"  onServiceConnected"+ musicManager.getmSongs().size());
                allSongFragment.setSongManager(musicManager);
                allSongFragment.setData(musicManager.getmSongs());
                allSongFragment.isPlayMusic(musicManager.isMusicPlaying());
            }
            //Log.d("bachdz","onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService=null;
            musicManager=null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState!=null){
            check=true;
            musicManager= (MusicManager) savedInstanceState.getSerializable(KEY_MUSIC_MANAGER);
            Log.d("bachdz",check+ " savedInstanceState" + musicManager);
        }

        // if check == true thì ta set các giá trị cho 2 fragment luôn
        // còn là false thì ta phải set giá tri cho 2 fragment in mConnection.

        fragmentManager= getSupportFragmentManager();
        intent= new Intent(MainActivity.this, MusicService.class);
        startService(intent);
        bindService(intent,mConnection,BIND_AUTO_CREATE);

        if (findViewById(R.id.vertical_Screen)!=null) isVertical=true;
        if (isVertical){
            AllSongFragment allSongFragment= new AllSongFragment();
            FragmentTransaction ft= fragmentManager.beginTransaction();
            ft.replace(R.id.allSongFragment,allSongFragment);
            ft.commit();
        }
        else {
            AllSongFragment allSongFragment= new AllSongFragment();
            FragmentTransaction ft= fragmentManager.beginTransaction();
            ft.replace(R.id.allSongFragment,allSongFragment);
            ft.commit();

            MediaPlaybackFragment mediaPlaybackFragment= new MediaPlaybackFragment();
            FragmentTransaction layer= fragmentManager.beginTransaction();
            layer.replace(R.id.musicPlayer,mediaPlaybackFragment);
            layer.addToBackStack(null);
            layer.commit();
        }

        Log.d("bachdz","onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("bachdz","onStart");
        if (check){
            AllSongFragment allSongFragment= (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
            allSongFragment.setData(musicManager.getmSongs());
            if (!isVertical){
                Log.d("bachNgoc", musicManager+ "");
                MediaPlaybackFragment mediaPlaybackFragment= (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
                allSongFragment.setVisible();
                mediaPlaybackFragment.setMusicManager(musicManager);
            }
            else allSongFragment.isPlayMusic(musicManager.isMusicPlaying());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("bachdz","onResume");
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(KEY_MUSIC_MANAGER,musicManager);
        outState.putString("bachdz","Nguyen Ngoc Bach");
        Log.d("bachdz", "outState " + musicManager);
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
        AllSongFragment allSongFragment= (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
        allSongFragment.setSelection(i);
        allSongFragment.isPlayMusic(true);
        //???????????
        //????????????

        Log.d("bachNgoc", musicManager+ "");
        if (musicManager!=null) {
            if (musicManager.isMusicPlaying()) {
                Log.d("bachNgoc", musicManager.isMusicPlaying() + "");
                musicManager.onResetMusic();
            }
            musicManager.setCurrentSong(i);
            musicManager.onPlay();
        }
        if (!isVertical){
            MediaPlaybackFragment player= (MediaPlaybackFragment) getSupportFragmentManager().findFragmentById(R.id.musicPlayer);
            //player.setTile(musicManager.getSinpleSong(i));
            player.setMusicManager(musicManager);
        }

    }

    @Override
    public void show() {
        MediaPlaybackFragment mediaPlaybackFragment;
        if (isVertical){
            mediaPlaybackFragment=  MediaPlaybackFragment.getInstance(musicManager);
            FragmentTransaction ft= fragmentManager.beginTransaction();
            ft.replace(R.id.musicPlayer, mediaPlaybackFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
        else {
            mediaPlaybackFragment= new MediaPlaybackFragment();
            FragmentTransaction ft= fragmentManager.beginTransaction();
            ft.replace(R.id.musicPlayer, mediaPlaybackFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }



    @Override
    public void onLike() {

    }

    @Override
    public void onPrevious() {

    }

    @Override
    public void onPlay() {

    }

    @Override
    public void onNext() {

    }

    @Override
    public void onDisLike() {

    }

    public boolean isVertical() {
        return isVertical;
    }
}