package com.example.levin.mockia2;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.ArrayList;
/*
This object deals with storing and retrieving information from internal storage. This includes
user progress, such as their level of mastery for skills, what tasks they have already
completed, etc.

The general working principle of this object is to, on initialization, read the files to
string arrays. Then it will work only with those arrays, and write those arrays back to the
files. This is because arrays are generally easier to work with (you can print them out etc).
*/
public class StorageUtility {
    private Context appContext;

    /*
    skillsProgress.txt stores the user's level of mastery of each skill, allowing the app to decide
    which skills the user needs to practice and which skills the user has already grasped.
     */
    private static final String SKILLS_PROGRESS_FILE_NAME = "skillsProgress.txt";
    public ArrayList<String> skillsProgressStringArray = new ArrayList<>();

    /*
    doneTasks.txt stores which tasks the user has completed in the past, to prevent reassigning
    them too frequently.
     */
    private static final String DONE_TASKS_FILE_NAME = "doneTasks.txt";
    public ArrayList<String> doneTasksStringArray = new ArrayList<>();

    /*
    songSkills.txt stores the progression of tasks and intermediate songs the app has generated
    for the user, from the most basic to the most advanced.
     */
    private static final String SONG_SKILLS_FILE_NAME = "songSkills.txt";
    public ArrayList<String> songSkillsStringArray = new ArrayList<>();

    public StorageUtility(Context c) throws IOException {
        appContext = c;

        File skillsProgressFile = new File(appContext.getFilesDir(), SKILLS_PROGRESS_FILE_NAME);
        if (!skillsProgressFile.exists()) {
            //Log.d("myInfoTag", "creating skills progress file anew");
            skillsProgressFile.createNewFile();
        }
        else
            //Log.d("myInfoTag", "skills progress file already exists; not created anew.");
        loadSkillsProgressStringArray();


        File doneTasksFile = new File(appContext.getFilesDir(), DONE_TASKS_FILE_NAME);
        if (!doneTasksFile.exists()) {
            doneTasksFile.createNewFile();
        }
        loadDoneTasksStringArray();

        File songSkillsFile = new File(appContext.getFilesDir(), SONG_SKILLS_FILE_NAME);
        if (!songSkillsFile.exists()) {
            songSkillsFile.createNewFile();
        }
        loadSongSkillsStringArray();
    }

    //This is a method for bugfixing that wipes all storage of user data (except their target song).
    public void resetSkillsAndTasksStorage() throws IOException {
        skillsProgressStringArray = new ArrayList<>();
        skillsProgressStringArray.add("");
        doneTasksStringArray = new ArrayList<>();
        doneTasksStringArray.add("");

        saveSkillsProgressStringArray();
        saveDoneTasksStringArray();
    }

    //Load skillsProgress.txt to a string array.
    public void loadSkillsProgressStringArray() throws IOException {
        FileInputStream fileInputStream = appContext.openFileInput(SKILLS_PROGRESS_FILE_NAME);

        BufferedReader bReader = new BufferedReader(new InputStreamReader(fileInputStream));

        String line;
        while ((line = bReader.readLine()) != null) {
            //Log.d("myInfoTag", "line: " + line);
            skillsProgressStringArray.add(line);
        }
        //Log.d("myInfoTag", "load completed.");

        fileInputStream.close();
    }

    //Load doneTasks.txt to a string array.
    public void loadDoneTasksStringArray() throws IOException {
        FileInputStream fileInputStream = appContext.openFileInput(DONE_TASKS_FILE_NAME);

        BufferedReader bReader = new BufferedReader(new InputStreamReader(fileInputStream));

        String line;
        while ((line = bReader.readLine()) != null) {
            doneTasksStringArray.add(line);
        }

        fileInputStream.close();
    }

    //load songSkills.txt to a string array.
    public void loadSongSkillsStringArray() throws IOException {
        FileInputStream fileInputStream = appContext.openFileInput(SONG_SKILLS_FILE_NAME);

        BufferedReader bReader = new BufferedReader(new InputStreamReader(fileInputStream));

        String line;
        while ((line = bReader.readLine()) != null) {
            songSkillsStringArray.add(line);
        }

        fileInputStream.close();
    }

    //Write the skills progress string array to skillsProgress.txt.
    public void saveSkillsProgressStringArray() throws IOException {
        String skillsProgressString = null;

        FileOutputStream fileOutputStream = appContext.openFileOutput(SKILLS_PROGRESS_FILE_NAME, appContext.MODE_PRIVATE);

        for (String line : skillsProgressStringArray) {
            if (skillsProgressString == null)
                skillsProgressString = line;
            else
                skillsProgressString = skillsProgressString + "\n" + line;
        }

        //Log.d("myInfoTag", "stringSaving: " + skillsProgressString);

        fileOutputStream.write(skillsProgressString.getBytes());

        fileOutputStream.close();
    }

    //Write the done tasks string array to doneTasks.txt
    public void saveDoneTasksStringArray() throws IOException {
        String skillsProgressString = null;

        FileOutputStream fileOutputStream = appContext.openFileOutput(DONE_TASKS_FILE_NAME, appContext.MODE_PRIVATE);

        for (String line : doneTasksStringArray) {
            if (skillsProgressString == null)
                skillsProgressString = line;
            else
                skillsProgressString = skillsProgressString + "\n" + line;
        }

        fileOutputStream.write(skillsProgressString.getBytes());

        fileOutputStream.close();
    }

    //Write the song skills string array to songSkills.txt
    public void saveSongSkillsArray() throws IOException {
        String songSkillsString = null;

        FileOutputStream fileOutputStream = appContext.openFileOutput(SONG_SKILLS_FILE_NAME, appContext.MODE_PRIVATE);

        for (String line : songSkillsStringArray) {
            if (songSkillsString == null)
                songSkillsString = line;
            else
                songSkillsString = songSkillsString + "\n" + line;
        }

        if (songSkillsString != null && !songSkillsString.equals(""))
            fileOutputStream.write(songSkillsString.getBytes());
        else
            fileOutputStream.write("".getBytes());

        fileOutputStream.close();
    }

    //Reformats the 2D song skills array, as generated and processed elsewhere, into the 1D string
    //array that will be saved by storageUtility.
    public void setSongSkillsMatrix(ArrayList<ArrayList<String>> songAndSkillsMatrix) throws IOException {
        songSkillsStringArray.clear();

        for (ArrayList<String> skillLine : songAndSkillsMatrix) {
            songSkillsStringArray.add(skillLine.toString());
        }
        saveSongSkillsArray();
    }

    //Returns the 1D song skills array reformatted back into 2D form.
    public ArrayList<ArrayList<String>> getFormattedSongSkillsMatrix() {
        ArrayList<ArrayList<String>> formattedSongSkillsMatrix = new ArrayList<>();

        for (String skillLine : songSkillsStringArray) {
            ArrayList<String> skillLineArray = new ArrayList<>();
            String currentItem = "";
            for (int index = 0; index < skillLine.length(); index++) {
                char charAtIndex = skillLine.charAt(index);
                if (Character.isAlphabetic(charAtIndex) || Character.isDigit(charAtIndex)) {
                    currentItem = currentItem + Character.toString(charAtIndex);
                }
                else
                {
                    if (!currentItem.equals("")) {
                        skillLineArray.add(currentItem);
                        currentItem = "";
                    }
                }
            }
            formattedSongSkillsMatrix.add(skillLineArray);
        }

        return formattedSongSkillsMatrix;
    }

    //Stores an increase to a skill's level of mastery.
    public void updateSkillProgress(String skillId, int value) throws IOException {
        int skillIndex = -1;
        for (int index = 0; index < skillsProgressStringArray.size(); index++) {
            if (skillsProgressStringArray.get(index).equals("{" + skillId)) {
                skillIndex = index;
                break;
            }
        }

        if (skillIndex != -1) {
            int updatedProgress = Integer.parseInt(skillsProgressStringArray.get(skillIndex + 1));
            updatedProgress += value;
            skillsProgressStringArray.set(skillIndex + 1, Integer.toString(updatedProgress));
        }
        else {
            skillsProgressStringArray.add("{" + skillId);
            skillsProgressStringArray.add(Integer.toString(value));
        }

        saveSkillsProgressStringArray();
    }

    //Stores an increase (with default value 10) to a skill's level of mastery.
    public void updateSkillProgress(String skillId) throws IOException {
        updateSkillProgress(skillId, 10);
    }

    //Returns a skill's current level of mastery.
    public int getSkillProgress(String skillId) {
        int skillIndex = -1;
        for (int index = 0; index < skillsProgressStringArray.size(); index++) {
            if (skillsProgressStringArray.get(index).equals("{" + skillId)) {
                skillIndex = index;
                break;
            }
        }

        if (skillIndex != -1) {
            return Integer.parseInt(skillsProgressStringArray.get(skillIndex + 1));
        }
        else {
            return 0;
        }
    }

    //Stores a given task as completed.
    public void addTaskAsDone(String taskId) throws IOException {
        if (!isTaskDone(taskId))
            doneTasksStringArray.add("{" + taskId);
        saveDoneTasksStringArray();
    }

    //Returns whether a task has been stored as being previously completed.
    public boolean isTaskDone(String taskId) {
        boolean taskFound = false;
        for (int index = 0; index < doneTasksStringArray.size(); index++) {
            if (doneTasksStringArray.get(index).equals("{" + taskId)) {
                taskFound = true;
                break;
            }
        }
        return taskFound;
    }
}
