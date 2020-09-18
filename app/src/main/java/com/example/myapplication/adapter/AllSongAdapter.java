package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Song;
import com.example.myapplication.R;
import com.example.myapplication.listenner.IMusicListenner;

import java.util.List;

public class AllSongAdapter extends RecyclerView.Adapter<AllSongAdapter.ViewHolder> {

    public int mCerrentSong = 0;
    private Context mContext;
    private List<Song> mSongs;
    private IMusicListenner mListenner;

    public AllSongAdapter(Context mContext, List<Song> mSongs, IMusicListenner listenner) {
        this.mContext = mContext;
        this.mSongs = mSongs;
        this.mListenner = listenner;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mCerrentSong) return 1;
        return 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(mContext).inflate(R.layout.song_line_play, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.song_line, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        boolean check = (position == mCerrentSong) ? true : false;
        holder.mIndexMusic.setText("" + (position + 1));
        holder.onBind(mSongs.get(position), check);

        holder.mItemMuiscLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListenner.selectMusic(position);
            }
        });
        holder.mMoreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListenner.selectMoreMusic(position, holder.mMoreIcon);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    /**
     * @param time tổng thời gian của bàn hát
     * @return format về đinh dạnh phút / giây
     */
    private String getDuration(String time) {
        Long total = Long.parseLong(time);
        int minutes = (int) ((total / 1000) / 60);
        int second = (int) ((total / 1000) % 60);
        return (minutes < 10 ? "0" + minutes : minutes + "") + ":" + (second < 10 ? "0" + second : second + "");
    }

    public int getmCerrentSong() {
        return mCerrentSong;
    }

    public void setmCerrentSong(int mCerrentSong) {
        this.mCerrentSong = mCerrentSong;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mIndexMusic;
        TextView mTitleTextView;
        TextView mAuthorTextView;
        ImageView mPlayMusicIcon;
        ImageView mMoreIcon;
        LinearLayout mItemMuiscLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIndexMusic = itemView.findViewById(R.id.txt_stt);
            mTitleTextView = itemView.findViewById(R.id.nameMusic);
            mAuthorTextView = itemView.findViewById(R.id.nameAirsts);
            mPlayMusicIcon = itemView.findViewById(R.id.icon_play_music);
            mMoreIcon = itemView.findViewById(R.id.icon_more);
            mItemMuiscLayout = itemView.findViewById(R.id.click_item);
        }

        public void onBind(Song song, boolean check) {
            mTitleTextView.setText(song.getTitle());
            mAuthorTextView.setText(getDuration(song.getDuration()));
            if (check) {
                mIndexMusic.setVisibility(View.INVISIBLE);
                mPlayMusicIcon.setVisibility(View.VISIBLE);
            }
        }
    }
}
