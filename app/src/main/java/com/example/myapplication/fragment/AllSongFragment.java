package com.example.myapplication.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * BachNN
 * Hiện thị một Danh Sach Music cho người dung thấy
 */
public class AllSongFragment extends BaseSongListFragment {

    public AllSongFragment() {
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            LoadData();
    }

    @Override
    public void LoadData() {
        new LoadAllMusic().execute();
    }
}
