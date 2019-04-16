package com.example.levin.mockia2;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/*
Adapter for song selection recyclerList in the song selection activity.
Created with help from: https://youtu.be/17NbUcEts9c
 */
public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder> {
    private ArrayList<SongItem> songList;
    private OnItemClickListener songsListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        songsListener = listener;
    }

    public static class SongsViewHolder extends RecyclerView.ViewHolder {
        public TextView line1TextView;
        public TextView line2TextView;

        public SongsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            line1TextView = (TextView) itemView.findViewById(R.id.line1TextView);
            line2TextView = (TextView) itemView.findViewById(R.id.line2TextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public SongsAdapter(ArrayList<SongItem> s) {
        songList = s;
    }

    @NonNull
    @Override
    public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_songselect, viewGroup, false);
        SongsViewHolder songsViewHolder = new SongsViewHolder(view,  songsListener);
        return songsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SongsViewHolder songsViewHolder, int i) {
        SongItem currentItem = songList.get(i);

        String genresString = "";
        for (String genre : currentItem.getGenres()) {
            genresString = genresString + genre + ", ";
        }
        genresString = genresString.substring(0, genresString.length() - 2);

        songsViewHolder.line1TextView.setText(currentItem.getName());
        songsViewHolder.line2TextView.setText(genresString);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
