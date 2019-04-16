package com.example.levin.mockia2;

import java.util.ArrayList;

/*
Child class of queueItem, specifically for songs - so it contains song-specific data.
 */
public class SongItem extends QueueItem {
    public ArrayList<String> genres;

    public ArrayList<String> topSkillIds;

    public SongItem(String i, String n, String c) {
        super(i, n, c);
    }

    public ArrayList<String> getTopSkillIds() {
        return topSkillIds;
    }

    public void setTopSkillIds(ArrayList<String> topSkillIds) {
        this.topSkillIds = topSkillIds;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }
}
