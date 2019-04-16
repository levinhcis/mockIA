package com.example.levin.mockia2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;

/*
The Song Selection Activity is where the user can reselect their target song.
 */
public class SongSelectionActivity extends AppCompatActivity {
    BackendRunner backendRunner;

    private RecyclerView songsRecyclerView;
    private SongsAdapter songsAdapter;
    private RecyclerView.LayoutManager songsLayoutManager;

    private Button backButton;

    private ArrayList<SongItem> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songselection);
        try {
            backendRunner = new BackendRunner(getApplicationContext());
            songList = backendRunner.getAllSongItems();

            songsRecyclerView = (RecyclerView) findViewById(R.id.songsRecyclerView);
            songsRecyclerView.setHasFixedSize(true);
            songsLayoutManager = new LinearLayoutManager(this);
            songsAdapter = new SongsAdapter(songList);

            /*
            This is what allows the user to tap the songs in the recyclerList directly
            to select them.
             */
            songsAdapter.setOnItemClickListener(new SongsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    SongItem clickedItem = songList.get(position);

                    Log.d("myInfoTag", "item clicked! item: " + clickedItem.getName());
                    try {
                        backendRunner.resetSongSkills(clickedItem.getId());

                        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);

                        startActivity(mainIntent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            songsRecyclerView.setLayoutManager(songsLayoutManager);
            songsRecyclerView.setAdapter(songsAdapter);

            //The button the user presses to return to the main activity without reselecting a
            //target song.
            backButton = (Button) findViewById(R.id.backButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainIntent);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
