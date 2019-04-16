package com.example.levin.mockia2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/*
The Task Activity is where a task (a lesson or a song) are displayed for the user to complete.
 */
public class TaskActivity extends AppCompatActivity {

    private Button backButton;
    private Button completeButton;

    private TextView topTextView;
    private TextView contentTextView;
    private TextView bottomTextView;

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        backButton = (Button) findViewById(R.id.backButton);
        completeButton = (Button) findViewById(R.id.completeButton);

        topTextView = (TextView) findViewById(R.id.topTextView);
        contentTextView = (TextView) findViewById(R.id.contentTextView);
        bottomTextView = (TextView) findViewById(R.id.bottomTextView);

        extras = getIntent().getExtras();

        topTextView.setText(extras.getString("name"));
        contentTextView.setText(extras.getString("contents"));

        if (extras.getString("id").substring(0, 1).equals("T")) {
            bottomTextView.setText(extras.getString("successCondition"));
        }
        else if (extras.getString("id").substring(0, 1).equals("P")) {

        }

        //The button the user presses to return to the main activity without completing the task.
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                mainIntent.putExtra("cameFromTaskActivity", 1);
                mainIntent.putExtra("sessionQueue", extras.getStringArrayList("sessionQueue"));
                startActivity(mainIntent);
            }
        });

        //The button the user presses when they successfully complete the task/song.
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                mainIntent.putExtra("cameFromTaskActivity", 1);
                mainIntent.putExtra("itemCompleted", extras.getString("id"));
                mainIntent.putExtra("sessionQueue", extras.getStringArrayList("sessionQueue"));
                startActivity(mainIntent);
            }
        });
    }
}
