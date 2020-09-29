package com.example.myapplication.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.Service.MusicManager;
import com.example.myapplication.Service.MusicService;
import com.example.myapplication.database.AllMusicProvider;

public class BaseSongListFragment extends Fragment {
    protected AllMusicProvider mAllMusicProvider;
    protected MusicService.LocalMusic mLocalMusic;
    protected int mCurrentMusic;
    protected AllSongFragment.AllSongFragmentListenner mAllSongListener;
    protected MainActivity mMainActivity;
    protected MusicManager mMusicManager;

    ServiceConnection mConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mLocalMusic= (MusicService.LocalMusic) iBinder;
            mMusicManager= mLocalMusic.getInstanceService().getMusicManager();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mLocalMusic=null;
            mLocalMusic=null;
        }
    };
    @Override
    public void onAttach(@NonNull Context context) {
        if (context instanceof MainActivity){
            mAllMusicProvider= new AllMusicProvider(context);
            mAllSongListener= (AllSongFragment.AllSongFragmentListenner) context;
        }
        MainActivity mainActivity= (MainActivity) context;
        if (mainActivity.isMyServiceRunning(MusicService.class) ) {
            Intent intent= new Intent(getContext(),MusicService.class);
            context.bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
        }
        super.onAttach(context);
    }

    public void setCurrentMusic(int currentMusic){
        mCurrentMusic= currentMusic;
    }

    @Override
    public void onDestroy() {
        getActivity().unbindService(mConnection);
        super.onDestroy();
    }
}
