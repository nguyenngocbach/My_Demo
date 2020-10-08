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

import com.example.myapplication.model.Song;
import com.example.myapplication.R;
import com.example.myapplication.listenner.IMusicListenner;

import java.util.List;

public class AllSongAdapter extends RecyclerView.Adapter<AllSongAdapter.ViewHolder> {
    //BachNN : LINE_NORMAL là  bài hát không đc chọn.
    private static final int LINE_NORMAL = 0;
    //BachNN : LINE_CHOOSE là bài hát được chọn và đang chạy nhạc
    private static final int LINE_CHOOSE = 1;
    public int mCurrentSong = 2;
    private Context mContext;
    private List<Song> mSongs;
    private IMusicListenner mListener;

    public AllSongAdapter(Context mContext, List<Song> mSongs, IMusicListenner listener) {
        this.mContext = mContext;
        this.mSongs = mSongs;
        this.mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mCurrentSong) return LINE_CHOOSE;
        return LINE_NORMAL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        //BachNN : nếu type = LINE_CHOOSE sẽ set cái dong đấy là bài được chọn còn ngược lại.
        if (viewType == LINE_CHOOSE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.song_line_play, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.song_line, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // BachNN : set cac gia tri cua cua cac view
        holder.onBind(mSongs.get(position));
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    /**
     * BacnhNN
     *
     * @param time tổng thời gian của bàn hát
     * @return format về đinh dạnh phút / giây
     */
    private String getDuration(String time) {
        Long total = Long.parseLong(time);
        int minutes = (int) ((total / 1000) / 60);
        int second = (int) ((total / 1000) % 60);
        return (minutes < 10 ? "0" + minutes : minutes + "") + ":" + (second < 10 ? "0" + second : second + "");
    }


    public void setCurrentSong(int mCurrentSong) {
        this.mCurrentSong = mCurrentSong;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mIndexMusic;
        TextView mTitleTextView;
        TextView mAuthorTextView;
        ImageView mPlayMusicIcon;
        ImageView mMoreIcon;
        LinearLayout mItemMusicLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIndexMusic = itemView.findViewById(R.id.txt_stt);
            mTitleTextView = itemView.findViewById(R.id.name_Music);
            mAuthorTextView = itemView.findViewById(R.id.name_Airsts);
            mPlayMusicIcon = itemView.findViewById(R.id.icon_play_music);
            mMoreIcon = itemView.findViewById(R.id.icon_more);
            mItemMusicLayout = itemView.findViewById(R.id.click_item);
            mItemMusicLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int mPosition = getLayoutPosition();
                    mListener.selectMusic(mPosition);
                }
            });

            mMoreIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int mPosition = getLayoutPosition();
                    mListener.selectMoreMusic(mPosition, mMoreIcon);
                }
            });
        }

        public void onBind(Song song) {
            boolean check = getAdapterPosition() == mCurrentSong;
            //BachNN : set giá trị TextView nó là mIndexMusic với các số theo bài hát .
            mIndexMusic.setText("" + (getAdapterPosition() + 1));
            //BachNN : chuyền một bài hát đang chay vào hàm dưới để title và author
            mTitleTextView.setText(song.getTitle());
            mAuthorTextView.setText(getDuration(song.getDuration()));
            if (check) {
                mIndexMusic.setVisibility(View.INVISIBLE);
                mPlayMusicIcon.setVisibility(View.VISIBLE);
            }
        }
    }
}
