package com.example.levin.mockia2;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/*
Adapter for session queue recyclerList in the main activity.
Created with help from: https://youtu.be/17NbUcEts9c
 */

public class SessionQueueAdapter extends RecyclerView.Adapter<SessionQueueAdapter.SessionQueueViewHolder> {
    private ArrayList<QueueItem> taskList;

    public static class SessionQueueViewHolder extends RecyclerView.ViewHolder {
        public TextView line1TextView;
        public TextView line2TextView;

        public SessionQueueViewHolder(View itemView) {
            super(itemView);
            line1TextView = itemView.findViewById(R.id.line1TextView);
            line2TextView = itemView.findViewById(R.id.line2TextView);
        }
    }

    public SessionQueueAdapter(ArrayList<QueueItem> t) {
        taskList = t;
    }

    @NonNull
    @Override
    public SessionQueueViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_queue, viewGroup, false);
        SessionQueueViewHolder sessionQueueViewHolder = new SessionQueueViewHolder(view);
        return sessionQueueViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SessionQueueViewHolder sessionQueueViewHolder, int i) {
        QueueItem currentItem = taskList.get(i);
        //not getting here...
        //Log.d("myInfoTag", "adding to recyclerview: " + currentItem.getName());
        if (currentItem instanceof TaskItem) {
            sessionQueueViewHolder.line1TextView.setText(currentItem.getName());

            String skillName = ((TaskItem) currentItem).getSkillName();

            if (((TaskItem) currentItem).getContents().length() > 15) {
                sessionQueueViewHolder.line2TextView.setText(skillName + " - "
                        + ((TaskItem) currentItem).getContents().substring(0, 40 - skillName.length())
                        + "...");
            }
            else {
                sessionQueueViewHolder.line2TextView.setText(skillName);
            }
        }
        else if (currentItem instanceof SongItem) {
            sessionQueueViewHolder.line1TextView.setText("\"" + currentItem.getName() + "\"");
            sessionQueueViewHolder.line2TextView.setText("Song");
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
