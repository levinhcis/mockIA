package com.example.levin.mockia2;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/*
This utility class is what generates the skill progression - the progression of skills and songs
for the user to master. It also generates the session queue - the queue of tasks and songs the user
can practice within a given session.
 */
public class ProgressionGenerator
{
    private FileUtility fileUtility;
    private SkillTree skillTree;
    private StorageUtility storageUtility;

    private ArrayList<ArrayList<String>> songAndSkillsMatrix;

    public ProgressionGenerator(SkillTree s, FileUtility f, StorageUtility t) {
        skillTree = s;
        fileUtility = f;
        storageUtility = t;

        songAndSkillsMatrix = storageUtility.getFormattedSongSkillsMatrix();
    }

    //This is the function that generates the progression of skills and intermediate songs for the
    //user.
    public ArrayList<ArrayList<String>> generateSongSkills(String songId) throws IOException {
        //Get the list of all skills needed for the song.
        ArrayList<String> baseSongSkills = skillTree.getAllSkillsForSong(songId);

        //Add the song id itself to the front of that list.
        baseSongSkills.add(0, songId);

        ArrayList<String> songsChosen = new ArrayList<>(); songsChosen.add(songId);

        //Get the 2D array of this song and intermediate songs, and every skill associated
        //with each song
        songAndSkillsMatrix = recursiveSongSkillsArranger(baseSongSkills, songsChosen, songId);

        //Iterate thru the 2D array, from the easiest song to the hardest
        for (int checkIndex = songAndSkillsMatrix.size() - 1; checkIndex > 0; checkIndex--) {
            ArrayList<String> skillArrayToCheck = songAndSkillsMatrix.get(checkIndex);

            //Iterate thru every song harder than the current song being checked
            for (int cutIndex = 0; cutIndex < checkIndex; cutIndex++) {
                ArrayList<String> skillArrayToCut = songAndSkillsMatrix.get(cutIndex);

                //Remove all duplicate skills - this ensures that only one copy of each skill
                //remains in the 2D array, and that it remains with the easiest song that involves
                //it.
                //(Since the user will be learning the songs from easiest to hardest, the skill will
                //be taught for the first song that requires it - i.e, the easiest).
                for (int skillIndex = 0; skillIndex < skillArrayToCut.size(); skillIndex++) {
                    String skillId = skillArrayToCut.get(skillIndex);
                    if (skillArrayToCheck.contains(skillId)) {
                        skillArrayToCut.remove(skillId);
                        skillIndex--;
                    }
                }
            }
        }
        storageUtility.setSongSkillsMatrix(songAndSkillsMatrix);
        return songAndSkillsMatrix;
    }

    //This is a recursive function used by the above function. It finds intermediate songs.
    private ArrayList<ArrayList<String>> recursiveSongSkillsArranger(ArrayList<String> songAndSkills,
                             ArrayList<String> previouslyChosenSongs, String originalSongId) throws IOException {
        //2D array where all the songs and associated skills will be stored
        ArrayList<ArrayList<String>> tempSongAndSkillsMatrix = new ArrayList<ArrayList<String>>();
        tempSongAndSkillsMatrix.add(songAndSkills);

        //SongItem (object with all song info) for the given song id
        SongItem currentSong = fileUtility.getSongItem(songAndSkills.get(0));

        //If the song has more than 4 skills associated with it, then find an intermediate song:
        if (songAndSkills.size() > 4 && !currentSong.getId().equals("P000001"))
        {
            //Get all songItems
            ArrayList<SongItem> allSongs = fileUtility.getAllSongItems();

            String mostIdealFollowingSongId = "";

            int mostIdealFollowingSongScore = 0;

            int songScore;

            //This loop iterates through all the songs to find the ideal intermediate song:
            for (SongItem possibleSong : allSongs) {
                //Every song is assigned a "songScore". The song with the highest "songScore"
                //is made an intermediate song.
                songScore = 0;

                //+GENRE MATCH TO TARGET SONG
                for (String genre : fileUtility.getSongItem(originalSongId).getGenres()) {
                    if (possibleSong.getGenres().contains(genre))
                        songScore += 20;
                }
                //+SKILLS MATCH TO INTERMEDIATE SKILLS OF ORIGINAL SONG
                for (int index = 5; index < songAndSkills.size(); index++) {
                    String currentSkill = songAndSkills.get(index);
                    for (String possibleSongTopSkillId : possibleSong.getTopSkillIds()) {
                        if (currentSkill.equals(possibleSongTopSkillId)) {
                            songScore += 15 - index;
                        }
                    }
                }
                //-OVERLY ADVANCED SKILLS
                //Prevent assigning an intermediate song that overlaps too much with, or is
                //just as or more advanced than, the original song.
                ArrayList<String> possibleSongSkills = skillTree.getAllSkillsForSong(possibleSong.getId());

                for (String possibleSongSkill : possibleSongSkills) {
                    if (!songAndSkills.contains(possibleSongSkill)) {
                        songScore -= 20;
                    }
                }
                //CHECK IF ALREADY ASSIGNED
                //Check if this song has already previously been assigned as an intermediate song.
                if (previouslyChosenSongs.contains(possibleSong.getId())) {
                    songScore = 0;
                }
                if (songScore > mostIdealFollowingSongScore && !possibleSong.getId().equals(songAndSkills.get(0))) {
                    mostIdealFollowingSongScore = songScore;
                    mostIdealFollowingSongId = possibleSong.getId();
                }
            }
            //If the song with the best songScore has a score better than 5
            if (mostIdealFollowingSongScore > 5) {
                previouslyChosenSongs.add(mostIdealFollowingSongId);

                ArrayList<String> followingSongAndSkills = skillTree.getAllSkillsForSong(mostIdealFollowingSongId);
                followingSongAndSkills.add(0, mostIdealFollowingSongId);

                //RECURSION - calls itself for the intermediate song.
                //Basically, if the intermediate song itself has too many skills associated with it,
                //the function will try to find an intermediate song for the intermediate song,
                //and so on and so forth, until there's an intermediate song for every 4 skills
                //(as far as possible).
                ArrayList<ArrayList<String>> followingSongAndSkillsMatrix
                        = recursiveSongSkillsArranger(followingSongAndSkills, previouslyChosenSongs, originalSongId);

                //Adds the result to the 2D string array.
                tempSongAndSkillsMatrix.addAll(followingSongAndSkillsMatrix);
            }
        }

        //Return the 2D string array.
        return tempSongAndSkillsMatrix;
    }

    /*
    This is the function that generates the session queue for the user. It assigns tasks based on
    what skills - from easiest to hardest - the user has yet to master, as well as assigning
    intermediate songs.
     */
    public ArrayList<String> generateSessionQueue() {
        ArrayList<String> sessionQueue = new ArrayList<>();

        //For every skill in the skills progression
        for (int skillLineIndex = songAndSkillsMatrix.size() - 1; skillLineIndex >= 0; skillLineIndex--) {
            ArrayList<String> skillLine = songAndSkillsMatrix.get(skillLineIndex);
            for (int skillIndex = skillLine.size() - 1; skillIndex > 0; skillIndex--) {
                String skillId = skillLine.get(skillIndex);
                String nextSkillId = "";
                if (skillIndex != 1) {
                    nextSkillId = skillLine.get(skillIndex - 1);
                }

                int skillProgress;
                int skillMaxProgress;
                float skillCompletionPercentage;
                int numTasksToAssign;

                //Find what level of mastery the skill is at:
                skillProgress = storageUtility.getSkillProgress(skillId);
                skillMaxProgress = skillTree.getNodeMaxProgress(skillId);
                skillCompletionPercentage = (float) skillProgress / skillMaxProgress;

                /*
                Formula to decide how many tasks to assign to practice the skill.
                The higher the skill's level of mastery, the less tasks are assigned to practice it,
                because the user is already somewhat familiar with it:
                */
                numTasksToAssign = Math.round(((float) skillMaxProgress / 20) * (1 - skillCompletionPercentage));

                //Get the list of potential tasks to assign for the skill
                ArrayList<String> relevantTasks = skillTree.getRelevantTasks(skillId);

                //A temporary list of tasks this method has chosen to assign for this skill,
                //which prevents assigning the same task twice.
                ArrayList<String> tasksAssigned = new ArrayList<>();

                //For every task number that needs to be assigned:
                for (int taskIndex = 0; taskIndex < numTasksToAssign; taskIndex++) {
                    boolean taskFound = false;

                    //Look at every potential task:
                    for (String taskId : relevantTasks) {
                        //If the task has not previously been completed, and it has not already
                        //been assigned,
                        if (!storageUtility.isTaskDone(taskId) && !tasksAssigned.contains(taskId)) {
                            //assign it.
                            tasksAssigned.add(taskId);
                            taskFound = true;
                            break;
                        }
                    }
                    //BACKUP: If a suitable task hasn't been found
                    if (!taskFound) {
                        //If we've already assigned enough tasks
                        if (taskIndex < numTasksToAssign - 1)
                            //Just omit this task
                            numTasksToAssign--;
                        Random rand = new Random();
                        //Pick a random potential task, even if it's already been completed
                        for (int i = 0; i < 5; i++) {
                            String backupTaskId = relevantTasks.get(rand.nextInt(relevantTasks.size()));
                            //If this random task hasn't already been assigned
                            if (!tasksAssigned.contains(backupTaskId)) {
                                //assign it.
                                tasksAssigned.add(backupTaskId);
                                break;
                            }
                        }
                    }
                }
                //add the assigned tasks to the session queue.
                sessionQueue.addAll(tasksAssigned);
            }
            //add the intermediate song that the past few skills were associated with.
            sessionQueue.add(skillLine.get(0));
        }
        return sessionQueue;
    }
}
