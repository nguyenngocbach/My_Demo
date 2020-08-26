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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 123;
    private static final String KEY_MUSIC_MANAGER ="com.example.myapplication.musicManager" ;
    private Intent intent;
    private MusicService musicService;
    private MusicManager musicManager;
    private FragmentManager fragmentManager;



    private boolean isVertical=false;
    private boolean check=false;

    private ServiceConnection mConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.LocalMusic localMusic = (MusicService.LocalMusic) iBinder;
            musicService= localMusic.getInstanceService();
            musicManager= musicService.getMusicManager();
            if (isVertical){
                AllSongFragment allSongFragment= (AllSongFragment) getSupportFragmentManager().findFragmentById(R.id.allSongFragment);
                Log.d("bachdz","onServiceConnected"+ musicManager.getmSongs().size());
                allSongFragment.setSongManager(musicManager);
                allSongFragment.setData(musicManager.getmSongs());
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
        outState.putSerializable(KEY_MUSIC_MANAGER,musicManager);
        super.onSaveInstanceState(outState, outPersistentState);
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
}