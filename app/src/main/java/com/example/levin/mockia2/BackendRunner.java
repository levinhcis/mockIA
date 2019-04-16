package com.example.levin.mockia2;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Queue;

/*
This object is the interface between the activities and the utility functions. It coordinates all
utility activities, including storing and retrieving data, and processing information and
generating information, with user actions in the activities.
Most of the methods here redirect to methods in the different utility classes; no actual processing
is performed here.
 */
public class BackendRunner {
    private Context appContext;

    private FileUtility fileUtility;
    private SkillTree skillTree;
    private ProgressionGenerator progressionGenerator;
    private StorageUtility storageUtility;

    private ArrayList<String> sessionQueue;

    public BackendRunner(Context c) throws IOException {
        appContext = c;

        fileUtility = new FileUtility(appContext);

        skillTree = new SkillTree(fileUtility);
        storageUtility = new StorageUtility(appContext);
        progressionGenerator = new ProgressionGenerator(skillTree, fileUtility, storageUtility);
        sessionQueue = new ArrayList<>();
    }


    public ArrayList<String> getSessionQueue() {
        return sessionQueue;
    }

    public void resetSongSkills(String songId) throws IOException {
        progressionGenerator.generateSongSkills(songId);
        generateSessionQueue();
    }

    public void emptySongSkills() throws IOException {
        storageUtility.setSongSkillsMatrix(new ArrayList<ArrayList<String>>());
    }

    public void resetSkillsAndTasksStorage() throws IOException {
        storageUtility.resetSkillsAndTasksStorage();
    }

    public void generateSessionQueue() throws IOException {
        sessionQueue = progressionGenerator.generateSessionQueue();
    }

    public String getTargetSongName() throws IOException {
        return fileUtility.getObjectContentsArray(storageUtility.getFormattedSongSkillsMatrix().get(0).get(0)).get(1);
    }

    public int getSkillProgressPercentage(String id) {
        int skillProgress = storageUtility.getSkillProgress(id);
        int skillMaxProgress = skillTree.getNodeMaxProgress(id);
        return (int) ((float)skillProgress/skillMaxProgress) * 100;
    }

    public ArrayList<String> getObjectContentsArray(String id) throws IOException {
        return fileUtility.getObjectContentsArray(id);
    }

    public void registerTaskAsDone(String id) throws IOException {
        storageUtility.addTaskAsDone(id);
        storageUtility.updateSkillProgress(fileUtility.getSkillIdForTaskId(id));
    }

    public void setSessionQueue(ArrayList<String> queue) {
        sessionQueue = queue;
    }

    public String getSkillNameForTask(String taskId) throws IOException {
        String skillId = fileUtility.getSkillIdForTaskId(taskId);
        return skillTree.getNodeName(skillId);
    }

    public ArrayList<SongItem> getAllSongItems() throws IOException {
        return fileUtility.getAllSongItems();
    }
}
