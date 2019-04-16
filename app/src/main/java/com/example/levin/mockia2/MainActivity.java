package com.example.levin.mockia2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

/*
The Main Activity is what the app looks like when it is first opened. It will display the
queue of tasks and intermediate songs it wants the user to learn.

note to self - NEVER RE-INITIALIZE RECYCLERLIST! ALWAYS CLEAR AND RE-ADD!
This is so the Adapter doesn't lose connection to it.
*/
public class MainActivity extends AppCompatActivity {
    public BackendRunner backendRunner;

    private TextView targetSongTextView;
    private Button pickSongButton;
    private Button startTaskButton;
    private Button resetButton;
    private Button regenButton;

    private RecyclerView sessionQueueRecyclerView;
    private RecyclerView.Adapter sessionQueueAdapter;
    private RecyclerView.LayoutManager sessionQueueLayoutManager;

    private ArrayList<String> sessionQueue;
    private ArrayList<QueueItem> recyclerList;

    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            backendRunner = new BackendRunner(getApplicationContext());
            //Log.d("myInfoTag", "backendRunner init ended.");

            targetSongTextView = (TextView) findViewById(R.id.targetSongTextView);

            targetSongTextView.setText("Your current target song: " + backendRunner.getTargetSongName());

            pickSongButton = (Button) findViewById(R.id.pickSongButton);

            startTaskButton = (Button) findViewById(R.id.startTaskButton);

            resetButton = (Button) findViewById(R.id.resetButton);

            regenButton = (Button) findViewById(R.id.regenButton);

            extras = getIntent().getExtras();

            recyclerList = new ArrayList<>();

            buildRecyclerView();

            /*
            If we're coming here from the task activity, and not from launching the app:
             */
            if (extras != null && extras.getString("itemCompleted") != null) {
                //Register the task we've just completed;
                String completedItemId = extras.getString("itemCompleted");
                extras.getStringArrayList("sessionQueue").remove(0);
                backendRunner.registerTaskAsDone(extras.getString("itemCompleted"));
                backendRunner.setSessionQueue(extras.getStringArrayList("sessionQueue"));
            }
            else {
                //Otherwise, generate the session queue anew:
                backendRunner.generateSessionQueue();
            }

            sessionQueue = backendRunner.getSessionQueue();

            reloadRecyclerView();

            //This is the button to begin the first task/song in the session queue.
            startTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent taskIntent = new Intent(getApplicationContext(), TaskActivity.class);
                    QueueItem firstItem = recyclerList.get(0);
                    taskIntent.putExtra("name", firstItem.getName());
                    taskIntent.putExtra("id", firstItem.getId());
                    taskIntent.putExtra("sessionQueue", sessionQueue);

                    if (firstItem instanceof TaskItem) {
                        taskIntent.putExtra("contents", ((TaskItem) firstItem).getContents());
                        //Log.d("myInfoTag", "contents: " + ((TaskItem)firstItem).getContents());
                        taskIntent.putExtra("successCondition", ((TaskItem) firstItem).getSuccessCondition());
                        //Log.d("myInfoTag", "successCondition: " + ((TaskItem)firstItem).getSuccessCondition());
                    }
                    else if (firstItem instanceof SongItem) {
                        taskIntent.putExtra("contents", ((SongItem) firstItem).getContents());
                    }

                    startActivity(taskIntent);
                }
            });

            pickSongButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent songSelectIntent = new Intent(getApplicationContext(), SongSelectionActivity.class);

                    startActivity(songSelectIntent);
                }
            });

            //This button is for bugfixing purposes. It resets all stored progress.
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //backendRunner.emptySongSkills();
                        backendRunner.resetSkillsAndTasksStorage();
                        backendRunner.generateSessionQueue();
                        sessionQueue = backendRunner.getSessionQueue();
                        reloadRecyclerView();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            //This button is for bugfixing purposes. It regenerates the session queue (which
            //normally only generates itself every time the app is opened).
            regenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        backendRunner.generateSessionQueue();
                        sessionQueue = backendRunner.getSessionQueue();
                        reloadRecyclerView();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buildRecyclerView() {
        sessionQueueRecyclerView = (RecyclerView) findViewById(R.id.sessionQueueRecyclerView);
        sessionQueueRecyclerView.setHasFixedSize(false);
        sessionQueueAdapter = new SessionQueueAdapter(recyclerList);
        sessionQueueLayoutManager = new LinearLayoutManager(this);

        sessionQueueRecyclerView.setLayoutManager(sessionQueueLayoutManager);
        sessionQueueRecyclerView.setAdapter(sessionQueueAdapter);
    }

    public void reloadRecyclerView() throws IOException {
        recyclerList.clear();
        generateRecyclerList();
        sessionQueueAdapter.notifyDataSetChanged();
        //("myInfoTag", "got here.");
    }

    /*
    The BackendRunner feeds this activity the sessionQueue as a String array of the IDs associated
    with the tasks and songs. This function reconstructs the sessionQueue out of queueItem objects
    based on those IDs.
     */
    public void generateRecyclerList() throws IOException {
        for (String id : sessionQueue) {
            QueueItem item;
            ArrayList<String> fileContents = backendRunner.getObjectContentsArray(id);
            if (fileContents != null) {
                if (id.substring(0, 1).equals("T")) {
                    item = new TaskItem(id, fileContents.get(1), fileContents.get(2), fileContents.get(3));
                    ((TaskItem) item).setSkillName(backendRunner.getSkillNameForTask(id));
                } else {
                    item = new SongItem(id, fileContents.get(1), fileContents.get(4));
                }
            }
            else
            {
                if (id.substring(0, 1).equals("T")) {
                    item = new TaskItem(id, id, "placeholder", "placeholder");
                    ((TaskItem) item).setSkillName(backendRunner.getSkillNameForTask(id));
                }
                else
                    item = new SongItem(id, id, "placeholder");
            }
            recyclerList.add(item);
        }
    }
}