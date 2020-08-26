package com.example.myapplication.adapter;

import android.content.Context;
import android.util.Log;
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

import java.util.List;

public class AllSongAdapter extends RecyclerView.Adapter<AllSongAdapter.ViewHolder> {

    private Context mContext;
    private List<Song> mSongs;

    public int cerrentSong=0;



    public AllSongAdapter(Context mContext, List<Song> mSongs) {
        this.mContext = mContext;
        this.mSongs = mSongs;
    }

    @Override
    public int getItemViewType(int position) {
        if (position==cerrentSong) return 1;
        return 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if (viewType==1){
            view= LayoutInflater.from(mContext).inflate(R.layout.song_line_play,parent,false);
        }
        else {
            view= LayoutInflater.from(mContext).inflate(R.layout.song_line,parent,false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        boolean check= position==cerrentSong ? true: false;
        holder.txtStt.setText(""+(position+1));
        holder.onBind(mSongs.get(position),check);
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtStt,txtTitle, txtAuthor;
        ImageView iconPlayMusic, iconMore;
        LinearLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtStt= itemView.findViewById(R.id.txt_stt);
            txtTitle= itemView.findViewById(R.id.nameMusic);
            txtAuthor= itemView.findViewById(R.id.nameAirsts);
            iconPlayMusic= itemView.findViewById(R.id.icon_play_music);
            iconMore= itemView.findViewById(R.id.icon_more);
            layout= itemView.findViewById(R.id.click_item);
        }

        public void onBind(Song song,boolean check) {
            txtTitle.setText(song.getTitle());
            txtAuthor.setText(song.getAuthor());
            if (check){
                txtStt.setVisibility(View.INVISIBLE);
                iconPlayMusic.setVisibility(View.VISIBLE);
            }
            Log.d("bachdz", song.toString());
        }
    }

    public int getCerrentSong() {
        return cerrentSong;
    }

    public void setCerrentSong(int cerrentSong) {
        this.cerrentSong = cerrentSong;
    }
}
