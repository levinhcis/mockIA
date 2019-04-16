package com.example.levin.mockia2;

/*
Parent class for both tasks and songs. This allows easier processing, e.g in the recyclerList
in MainActivity, or for displaying content in TaskActivity.
 */
public class QueueItem {
    private String name;
    private String id;
    private String contents;

    public QueueItem(String i, String n, String c) {
        id = i;
        name = n;
        contents = c;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
